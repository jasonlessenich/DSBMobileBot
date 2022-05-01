package com.dynxsty.dsbmobilebot.systems.plans.commands.notification;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands.NotificationAddSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class NotificationCommand extends GuildSlashCommand {

	public NotificationCommand() {
		setCommandData(Commands.slash("notification", "Set of Commands for managing Timetable Notifications."));
		setSubcommands(NotificationAddSubcommand.class);
	}
}
