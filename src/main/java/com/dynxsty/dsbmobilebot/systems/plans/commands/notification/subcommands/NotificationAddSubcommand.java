package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.Subcommand;
import com.dynxsty.dsbmobilebot.command.Responses;
import com.dynxsty.dsbmobilebot.data.h2db.DbHelper;
import com.dynxsty.dsbmobilebot.systems.plans.Course;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao.NotificationAccountRepository;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.NotificationAccount;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationAddSubcommand extends Subcommand {

	public NotificationAddSubcommand() {
		setSubcommandData(new SubcommandData("add", "Add a single subject which you want to receive notifications for.")
				.addOptions(
						new OptionData(OptionType.STRING, "subject", "The subject", true).addChoices(getChoices()),
						new OptionData(OptionType.INTEGER, "course-id", "Your course's identifier.", true),
						new OptionData(OptionType.BOOLEAN, "advanced", "Whether you're in an Advanced Course", false)
				));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		OptionMapping subjectMapping = event.getOption("subject");
		OptionMapping courseMapping = event.getOption("course-id");
		boolean advanced = event.getOption("advanced", false, OptionMapping::getAsBoolean);
		if (subjectMapping == null || courseMapping == null) {
			Responses.error(event, "Missing required arguments").queue();
			return;
		}
		Course course = Course.valueOf(subjectMapping.getAsString());
		if (!course.advancedAllowed() && advanced) {
			Responses.error(event, course.name() + " does not allow Advanced Courses!").queue();
			return;
		}
		course.setAdvanced(advanced);
		course.setCourseId(courseMapping.getAsInt());
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
			// add course to subject-array
			NotificationAccount account = accountOptional.get();
			String[] courses = ArrayUtils.add(account.getSubjects(), course.toDatabaseString());
			account.setSubjects(courses);
			dao.update(account);
			Responses.info(event.getHook(), "Notification Added",
					String.format("Successfully added `%s` to your Notification List!", course)
			).queue();
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
