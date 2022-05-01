package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.Subcommand;
import com.dynxsty.dsbmobilebot.command.Responses;
import com.dynxsty.dsbmobilebot.data.h2db.DbHelper;
import com.dynxsty.dsbmobilebot.systems.plans.Course;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao.GuildNotificationAccountRepository;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.GuildNotificationAccount;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationAddSubcommand extends Subcommand {

	public NotificationAddSubcommand() {
		setSubcommandData(new SubcommandData("add", "Add a single subject which ...")
				.addOptions(
						new OptionData(OptionType.STRING, "subject", "The subject", true).addChoices(getChoices()),
						new OptionData(OptionType.INTEGER, "course-id", "Your course's identifier.", true),
						new OptionData(OptionType.INTEGER, "class", "Your class", true)
				));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		OptionMapping subjectMapping = event.getOption("subject");
		OptionMapping courseMapping = event.getOption("course-id");
		OptionMapping classMapping = event.getOption("class");
		if (subjectMapping == null || courseMapping == null || classMapping == null) {
			Responses.error(event, "Missing required arguments").queue();
		}
		Course course = Course.valueOf(subjectMapping.getAsString());
		int courseId = courseMapping.getAsInt();
		int clazz = classMapping.getAsInt();
		event.deferReply(true).queue();
		DbHelper.doDaoAction(GuildNotificationAccountRepository::new, dao -> {


			GuildNotificationAccount account = new GuildNotificationAccount();
			account.setClassLevel(clazz);
			account.setUserId(event.getUser().getIdLong());
			account.setSubjects(new String[]{ course.toString(courseId) });
			dao.insert(account);
			Responses.info(event.getHook(), "Notification Added", "Successfully added ").queue();
		});
	}

	private List<Command.Choice> getChoices() {
		List<Command.Choice> choices = new ArrayList<>();
		for (Course value : Course.values()) {
			choices.add(new Command.Choice(value.name(), value.name()));
		}
		return choices;
	}
}
