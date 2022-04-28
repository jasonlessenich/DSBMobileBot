package com.dynxsty.dsbmobilebot.systems.plans;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.util.PlanUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.IOException;

public class GetPlansCommand extends GuildSlashCommand {

	public GetPlansCommand() {
		this.setCommandData(Commands.slash("get-plans", "Retrieves all available plans from DSBMobile"));
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		var tables = Bot.dsbMobile.getTimeTables();
		event.getHook().sendMessageFormat("Found **%s** plans!", tables.size()).queue();
		try {
			PlanUtils.buildPlanAction(event.getChannel(), tables).forEach(MessageAction::queue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
