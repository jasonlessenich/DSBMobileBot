package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.Subcommand;
import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.command.Responses;
import com.dynxsty.dsbmobilebot.data.h2db.DbHelper;
import com.dynxsty.dsbmobilebot.systems.plans.Course;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao.NotificationAccountRepository;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.NotificationAccount;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class NotificationListSubcommand extends Subcommand {

	public NotificationListSubcommand() {
		setSubcommandData(new SubcommandData("list", "Lists all your Notifications"));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		event.deferReply(false).queue();
		DbHelper.doDaoAction(NotificationAccountRepository::new, dao -> {
			Optional<NotificationAccount> accountOptional = dao.getByUserId(event.getUser().getIdLong());
			if (accountOptional.isEmpty()) {
				Responses.error(event.getHook(),
						String.format("Could not add Notification to Account: `%s`." +
								"\nPlease make sure to set your class using `/notification set-class` first!", event.getUser().getIdLong())
				).queue();
				return;
			}
			event.getHook().sendMessageEmbeds(buildNotificationListEmbed(event.getUser())).queue();
		});
	}

	private static MessageEmbed buildNotificationListEmbed(User user) {
		StringBuilder sb = new StringBuilder();
		for (Course course : getSubjects(user.getIdLong())) {
			sb.append(course.toString() + "\n");
		}
		return new EmbedBuilder()
				.setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
				.setTitle("Notification List")
				.setDescription(sb.toString())
				.setTimestamp(Instant.now())
				.build();
	}

	public static List<Course> getSubjects(long userId) {
		List<Course> subjects = new ArrayList<>();
		try (Connection con = Bot.dataSource.getConnection()) {
			NotificationAccountRepository repo = new NotificationAccountRepository(con);
			Optional<NotificationAccount> accountOptional = repo.getByUserId(userId);
			accountOptional.ifPresent(account -> {
				for (String subject : account.getSubjects()) {
					subjects.add(Course.ofDatabaseString(subject));
				}
			});
		} catch (SQLException e) {
			log.error("Could not retrieve Subjects from User: " + userId);
		}
		return subjects;
	}
}
