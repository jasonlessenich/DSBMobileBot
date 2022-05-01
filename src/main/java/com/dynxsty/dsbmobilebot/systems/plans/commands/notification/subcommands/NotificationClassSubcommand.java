package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.Subcommand;
import com.dynxsty.dsbmobilebot.command.Responses;
import com.dynxsty.dsbmobilebot.data.h2db.DbHelper;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao.NotificationAccountRepository;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.NotificationAccount;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Optional;

public class NotificationClassSubcommand extends Subcommand {

	public NotificationClassSubcommand() {
		setSubcommandData(new SubcommandData("set-class", "Sets your current class.")
				.addOptions(new OptionData(OptionType.INTEGER, "class", "Your current class", true).setRequiredRange(7, 12)));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		OptionMapping classMapping = event.getOption("class");
		if (classMapping == null) {
			Responses.error(event, "Missing required arguments").queue();
			return;
		}
		int clazz = classMapping.getAsInt();
		event.deferReply(false).queue();
		DbHelper.doDaoAction(NotificationAccountRepository::new, dao -> {
			Optional<NotificationAccount> accountOptional = dao.getByUserId(event.getUser().getIdLong());
			if (accountOptional.isPresent()) {
				// simply update the class
				NotificationAccount account = accountOptional.get();
				account.setClassLevel(clazz);
				dao.update(account);
			} else {
				// Insert new account if none exists yet.
				NotificationAccount account = new NotificationAccount();
				account.setUserId(event.getUser().getIdLong());
				account.setClassLevel(clazz);
				account.setSubjects(new String[]{});
				dao.insert(account);
			}
			Responses.info(event.getHook(), "Class Set", String.format("Successfully set `%s` as your current Class!", clazz)).queue();
		});
	}
}
