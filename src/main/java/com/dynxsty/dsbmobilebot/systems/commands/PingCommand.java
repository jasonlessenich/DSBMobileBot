package com.dynxsty.dsbmobilebot.systems.commands;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.command.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class PingCommand implements ISlashCommand {
	@Override
	public ReplyCallbackAction handleSlashCommandInteraction(SlashCommandInteractionEvent event) {
		var e = new EmbedBuilder()
				.setColor(Bot.config.get(event.getGuild()).getSlashCommand().getDefaultColor())
				.setAuthor(event.getJDA().getGatewayPing() + "ms", null, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
				.build();
		return event.replyEmbeds(e);
	}
}
