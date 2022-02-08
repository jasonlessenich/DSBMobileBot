package com.dynxsty.dsbmobilebot.systems.plans;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.command.ResponseException;
import com.dynxsty.dsbmobilebot.command.interfaces.ISlashCommand;
import de.sematre.dsbmobile.DSBMobile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.io.IOException;
import java.net.URL;

public class GetPlansCommand implements ISlashCommand {
	@Override
	public ReplyCallbackAction handleSlashCommandInteraction(SlashCommandInteractionEvent event) throws ResponseException {
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
		return event.deferReply();
	}
}
