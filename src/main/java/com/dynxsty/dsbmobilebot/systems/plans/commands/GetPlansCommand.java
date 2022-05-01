package com.dynxsty.dsbmobilebot.systems.plans.commands;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.systems.plans.PlanProcessor;
import de.sematre.dsbmobile.DSBMobile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.IOException;
import java.util.List;

public class GetPlansCommand extends GuildSlashCommand {

	public GetPlansCommand() {
		setCommandData(Commands.slash("get-plans", "Retrieves all available plans from DSBMobile"));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		List<DSBMobile.TimeTable> tables = Bot.dsbMobile.getTimeTables();
		event.getHook().sendMessageFormat("Found **%s** plans!", tables.size()).queue();
		try {
			PlanProcessor.buildPlanAction(event.getGuild(), event.getChannel(), tables, false).forEach(MessageAction::queue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
