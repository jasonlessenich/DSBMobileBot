package com.dynxsty.dsbmobilebot.systems.plans;

import com.dynxsty.dih4jda.commands.interactions.slash_command.ISlashCommand;
import com.dynxsty.dih4jda.commands.interactions.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dsbmobilebot.Bot;
import de.sematre.dsbmobile.DSBMobile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.io.IOException;
import java.net.URL;

public class GetPlansCommand extends GuildSlashCommand implements ISlashCommand {

	public GetPlansCommand() {
		this.setCommandData(Commands.slash("get-plans", "Retrieves all available plans from DSBMobile"));
	}

	@Override
	public void handleSlashCommandInteraction(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		var tables = Bot.dsbMobile.getTimeTables();
		event.getHook().sendMessageFormat("There are **%s** available plans!", tables.size()).queue();
		for (DSBMobile.TimeTable table : tables) {
			try {
				event.getChannel().sendMessageFormat("\"%s\" | `%s`", table.getGroupName(), table.getDate())
						.addFile(new URL(table.getDetail()).openStream(), String.format("%s-%s.png", table.getUUID(), tables.indexOf(table)))
						.queue();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
