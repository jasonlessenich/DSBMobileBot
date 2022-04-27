package com.dynxsty.dsbmobilebot.systems.plans;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.util.PlanUtils;
import de.sematre.dsbmobile.DSBMobile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.io.IOException;
import java.net.URL;

public class GetPlansCommand extends GuildSlashCommand {

	public GetPlansCommand() {
		this.setCommandData(Commands.slash("get-plans", "Retrieves all available plans from DSBMobile"));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		var tables = Bot.dsbMobile.getTimeTables();
		event.getHook().sendMessageFormat("Found **%s** plans!", tables.size()).queue();
		for (DSBMobile.TimeTable table : tables) {
			try {
				PlanUtils.buildPlanAction(event.getChannel(), tables, table).queue();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
