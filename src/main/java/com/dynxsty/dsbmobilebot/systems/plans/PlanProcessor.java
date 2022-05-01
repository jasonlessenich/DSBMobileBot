package com.dynxsty.dsbmobilebot.systems.plans;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao.NotificationAccountRepository;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.NotificationAccount;
import com.dynxsty.dsbmobilebot.util.Pair;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class PlanProcessor {

	public static final Path tesseractDir = Path.of("tesseract");

	// Hide the constructor
	private PlanProcessor() {
	}

	/**
	 * Sends and Groups all similar Plans and send them into the given {@link MessageChannel}.
	 *
	 * @param guild   The current {@link Guild}.
	 * @param channel The channel which the plans should be sent to.
	 * @param tables  A List with all {@link de.sematre.dsbmobile.DSBMobile.TimeTable}s-
	 * @param analyze Whether the plans should be analyzed. {@link PlanProcessor#analyzePlan}
	 * @return A Collection with all MessageActions, ready to be queued.
	 * @throws IOException If an error occurs.
	 */
	public static Collection<MessageAction> buildPlanAction(Guild guild, MessageChannel channel, List<DSBMobile.TimeTable> tables, boolean analyze) throws IOException {
		Map<String, MessageAction> actions = new HashMap<>();
		for (DSBMobile.TimeTable table : tables) {
			String planName = String.format("%s-%s.png", table.getUUID(), tables.indexOf(table));
			// group new plans by date
			if (actions.containsKey(table.getTitle())) {
				MessageAction action = actions.get(table.getTitle());
				actions.put(table.getTitle(),
						action.addFile(new URL(table.getDetail()).openStream(), planName));
			} else {
				actions.put(table.getTitle(),
						channel.sendMessageEmbeds(buildPlanEmbed(table))
								.addFile(new URL(table.getDetail()).openStream(), planName));
			}
			if (analyze) Bot.asyncPool.submit(() -> analyzePlan(guild, channel, table, planName));
		}
		return actions.values();
	}

	/**
	 * Does a simple OCR on the given URL's image.
	 *
	 * @param url The URL.
	 * @return The processed Result.
	 */
	private static String executeOCR(String url) {
		// simple OCR using the Tesseract API
		String result = "";
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.connect();
			BufferedImage image = ImageIO.read(con.getInputStream());
			// Set up Tesseract
			Tesseract tesseract = new Tesseract();
			tesseract.setLanguage("deu");
			tesseract.setDatapath(PlanProcessor.tesseractDir.toString());
			tesseract.setPageSegMode(1);
			tesseract.setOcrEngineMode(1);
			result = tesseract.doOCR(image);
		} catch (IOException | TesseractException e) {
			log.error("Could not analyze plan", e);
		}
		return result;
	}

	/**
	 * Reads through the OCR results and tries to find any notable timetable occurrences.
	 *
	 * @param ocrResult The OCR's result.
	 * @return A {@link List} containing all members that should be mentioned.
	 */
	private static List<Long> findNotifications(String ocrResult) {
		List<Long> users = new ArrayList<>();
		try (Scanner scanner = new Scanner(ocrResult); Connection con = Bot.dataSource.getConnection()) {
			String lastClass = null;
			// Read through the whole String, line by line.
			while (scanner.hasNextLine()) {
				String rawLine = scanner.nextLine();
				String line = "";
				String[] split = rawLine.split("\\s+");
				// we don't want empty lines
				if (split.length <= 0) continue;
				if (split[0].matches(".*\\d.*") && !split[0].contains(".")) {
					// As it's a number and does not contain a ".", we assume it must be a class.
					lastClass = split[0];
					line = rawLine;
				} else if (lastClass != null && split.length > 1 && rawLine.trim().length() > 0 && split[0].isBlank() || split[0].matches(".*\\d.*")) {
					line = String.format("%s %s", lastClass, rawLine);
				}
				Optional<Course> optional = Course.containsCourse(line);
				if (optional.isPresent()) {
					Course course = optional.get();
					NotificationAccountRepository repo = new NotificationAccountRepository(con);
					users.addAll(repo.getBySubject(course).stream().map(NotificationAccount::getUserId).toList());
				}
			}
		} catch (SQLException e) {
			log.error("An Exception was raised while a plan was analyzed: " + e.getMessage());
		}
		return users;
	}

	/**
	 * Retrieves the last 20 messages and attempts to find the plan's message by its filename.
	 *
	 * @param channel  The {@link MessageChannel} the message was sent in.
	 * @param planName The plan's filename.
	 * @return The {@link Message}, as an {@link Optional}.
	 */
	private static Optional<Message> getPlanMessage(MessageChannel channel, String planName) {
		MessageHistory history = channel.getHistory();
		Message message = null;
		List<Message> messages = history.retrievePast(20).complete();
		// reverse the list, to start at the bottom
		Collections.reverse(messages);
		for (Message m : messages) {
			for (Message.Attachment attachment : m.getAttachments()) {
				if (attachment.getFileName().equals(planName)) {
					message = m;
				}
			}
		}
		return Optional.ofNullable(message);
	}

	/**
	 * Updates the Plan's embed messages to show that they were analyzed and mentioned all members that are affected
	 * by the timetable.
	 *
	 * @param guild   The current {@link Guild}.
	 * @param channel The {@link MessageChannel} the plans were sent in.
	 * @param table   The plan itself.
	 * @param name    The plan's filename.
	 */
	public static void analyzePlan(Guild guild, MessageChannel channel, DSBMobile.TimeTable table, String name) {
		List<Long> users = findNotifications(executeOCR(table.getDetail()));
		Optional<Message> messageOptional = getPlanMessage(channel, name);
		messageOptional.ifPresent(message -> {
			// change the embed's color
			message.editMessageEmbeds(
					new EmbedBuilder(message.getEmbeds().get(0))
							.setColor(Bot.config.get(guild).getSlashCommand().getInfoColor())
							.build()
			).queue();
			if (!users.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				users.forEach(user -> sb.append(String.format("<@%s> ", user)));
				message.replyFormat("%s\nThis might be interesting for you!", sb.toString()).queue();
			}
		});
	}

	public static MessageEmbed buildPlanEmbed(DSBMobile.TimeTable table) {
		return new EmbedBuilder()
				.setTitle(String.format("%s (%s)", table.getGroupName(), table.getTitle()), table.getDetail())
				.setFooter(table.getDate())
				.setTimestamp(LocalDateTime.parse(table.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
				.build();
	}
}
