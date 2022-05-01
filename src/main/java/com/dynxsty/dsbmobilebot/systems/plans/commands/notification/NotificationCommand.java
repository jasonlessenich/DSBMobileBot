package com.dynxsty.dsbmobilebot.systems.plans.commands.notification;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands.NotificationAddSubcommand;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands.NotificationClassSubcommand;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands.NotificationListSubcommand;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.subcommands.NotificationRemoveSubcommand;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class NotificationCommand extends GuildSlashCommand {

	public NotificationCommand() {
		setCommandData(Commands.slash("notification", "A set of Commands for managing Timetable Notifications."));
		setSubcommands(NotificationAddSubcommand.class, NotificationRemoveSubcommand.class,
				NotificationClassSubcommand.class, NotificationListSubcommand.class);
	}
}
