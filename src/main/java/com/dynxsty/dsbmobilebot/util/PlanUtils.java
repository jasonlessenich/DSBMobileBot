package com.dynxsty.dsbmobilebot.util;

import com.dynxsty.dsbmobilebot.Bot;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class PlanUtils {

	public static Path TESSERACT_DIR = Path.of("tesseract");

	private PlanUtils() {
	}

	public static String analyzePlan(String url) {
		String result = "";
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.connect();
			BufferedImage image = ImageIO.read(con.getInputStream());
			// Set up Tesseract
			Tesseract tesseract = new Tesseract();
			tesseract.setLanguage("deu");
			tesseract.setDatapath(PlanUtils.TESSERACT_DIR.toString());
			tesseract.setPageSegMode(1);
			tesseract.setOcrEngineMode(1);
			result = tesseract.doOCR(image);
		} catch (IOException | TesseractException e) {
			log.error("Could not analyze plan", e);
		}
		return result;
	}

	public static Collection<MessageAction> buildPlanAction(MessageChannel channel, List<DSBMobile.TimeTable> tables, boolean analyze) throws IOException {
		Map<String, MessageAction> actions = new HashMap<>();
		for (DSBMobile.TimeTable table : tables) {
			// group new plans by date
			if (actions.containsKey(table.getTitle())) {
				MessageAction action = actions.get(table.getTitle());
				actions.put(table.getTitle(),
						action.addFile(new URL(table.getDetail()).openStream(),
								String.format("%s-%s.png", table.getUUID(), tables.indexOf(table))));
			} else {
				actions.put(table.getTitle(),
						channel.sendMessageEmbeds(buildPlanEmbed(table))
								.addFile(new URL(table.getDetail()).openStream(),
										String.format("%s-%s.png", table.getUUID(), tables.indexOf(table)))
				);
			}
			if (analyze) {
				Bot.asyncPool.submit(() -> {
						String result = analyzePlan(table.getDetail());
						System.out.println(result);
				});
			}
		}
		return actions.values();
	}

	public static MessageEmbed buildPlanEmbed(DSBMobile.TimeTable table) {
		return new EmbedBuilder()
				.setTitle(String.format("%s (%s)", table.getGroupName(), table.getTitle()), table.getDetail())
				.setFooter(table.getDate())
				.setTimestamp(LocalDateTime.parse(table.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
				.build();
	}
}
