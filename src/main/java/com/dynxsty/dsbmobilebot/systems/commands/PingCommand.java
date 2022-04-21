package com.dynxsty.dsbmobilebot.systems.commands;

import com.dynxsty.dih4jda.commands.interactions.slash_command.ISlashCommand;
import com.dynxsty.dih4jda.commands.interactions.slash_command.dao.GlobalSlashCommand;
import com.dynxsty.dsbmobilebot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class PingCommand extends GlobalSlashCommand implements ISlashCommand {

	public PingCommand() {
		this.setCommandData(Commands.slash("ping", "Pong!"));
	}

	@Override
	public void handleSlashCommandInteraction(SlashCommandInteractionEvent event) {
		var e = new EmbedBuilder()
				.setColor(Bot.config.get(event.getGuild()).getSlashCommand().getDefaultColor())
				.setAuthor(event.getJDA().getGatewayPing() + "ms", null, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
				.build();
		event.replyEmbeds(e).queue();
	}
}
