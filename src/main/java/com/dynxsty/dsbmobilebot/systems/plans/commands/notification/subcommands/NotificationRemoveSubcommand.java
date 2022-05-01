package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands;

import com.dynxsty.dih4jda.interactions.commands.slash_command.autocomplete.AutoCompleteHandler;
import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.Subcommand;
import com.dynxsty.dih4jda.util.AutoCompleteUtils;
import com.dynxsty.dsbmobilebot.command.Responses;
import com.dynxsty.dsbmobilebot.data.h2db.DbHelper;
import com.dynxsty.dsbmobilebot.systems.plans.Course;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao.NotificationAccountRepository;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.NotificationAccount;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NotificationRemoveSubcommand extends Subcommand implements AutoCompleteHandler {

	public NotificationRemoveSubcommand() {
		setSubcommandData(new SubcommandData("remove", "Removes a single subject from your Notification List")
				.addOption(OptionType.STRING, "subject", "The subject you want to remove", true, true));
		enableAutoCompleteHandling();
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		OptionMapping subjectMapping = event.getOption("subject");
		if (subjectMapping == null) {
			Responses.error(event, "Missing required arguments").queue();
			return;
		}
		String subject = subjectMapping.getAsString();
		event.deferReply(false).queue();
		DbHelper.doDaoAction(NotificationAccountRepository::new, dao -> {
			Optional<NotificationAccount> accountOptional = dao.getByUserId(event.getUser().getIdLong());
			if (accountOptional.isEmpty()) {
				Responses.error(event.getHook(),
						String.format("Could not remove Notification from Account: `%s`." +
								"\nPlease make sure to set your class using `/notification set-class` first!", event.getUser().getIdLong())
				).queue();
				return;
			}
			NotificationAccount account = accountOptional.get();
			if (!Arrays.asList(account.getSubjects()).contains(subject)) {
				Responses.error(event.getHook(),
						String.format("Could not find Subject Notification `%s` on your Account. No need to remove it, then.", subject)).queue();
				return;
			}
			String[] subjects = ArrayUtils.removeElement(account.getSubjects(), subject);
			account.setSubjects(subjects);
			dao.update(account);
			Responses.info(event.getHook(), "Notification Removed", String.format("You will no longer get pinged for `%s`", Course.ofDatabaseString(subject).toString())).queue();
		});
	}

	@Override
	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target) {
		List<Command.Choice> choices = new ArrayList<>();
		for (Course course : NotificationListSubcommand.getSubjects(event.getUser().getIdLong())) {
			choices.add(new Command.Choice(course.toString(), course.toDatabaseString()));
		}
		event.replyChoices(AutoCompleteUtils.filterChoices(event, choices)).queue();
	}
}
