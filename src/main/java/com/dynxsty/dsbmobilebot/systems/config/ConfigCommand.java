package com.dynxsty.dsbmobilebot.systems.config;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.GuildSlashCommand;
import com.dynxsty.dih4jda.interactions.modal.ModalHandler;
import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.config.GuildConfig;
import com.dynxsty.dsbmobilebot.config.exception.UnknownPropertyException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;

public class ConfigCommand extends GuildSlashCommand implements ModalHandler {

	public ConfigCommand(Guild guild) {
		setCommandData(Commands.slash("config", "Allows to change this server's config").setDefaultEnabled(false));
		//setCommandPrivileges(CommandPrivilege.disableRole(guild.getIdLong()), CommandPrivilege.enableUser(guild.getOwnerIdLong()));
		handleModalIds("config");
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		event.replyModal(buildConfigModal(Bot.config.get(event.getGuild()))).queue();
	}

	@Override
	public void handleModal(ModalInteractionEvent event, List<ModalMapping> values) {
		event.deferReply(true).queue();
		StringBuilder changesBuilder = new StringBuilder();
		GuildConfig config = Bot.config.get(event.getGuild());
		for (ModalMapping mapping : values) {
			if (mapping == null || mapping.getAsString().length() == 0) continue;
			try {
				if (String.valueOf(config.resolve(mapping.getId())).equals(mapping.getAsString())) continue;
				changesBuilder.append(String.format("**%s**\n`%s`", mapping.getId(), config.resolve(mapping.getId())));
				config.set(mapping.getId(), mapping.getAsString());
				changesBuilder.append(String.format(" → `%s`\n\n", mapping.getAsString()));
			} catch (UnknownPropertyException e) {
				throw new RuntimeException(e);
			}
		}
		final String changes = changesBuilder.toString();
		if (changes.isEmpty()) {
			event.getHook().sendMessage("No changes were made.").queue();
		} else {
			event.getHook().sendMessage("**Updated values**\n\n" + changes).queue();
		}
	}

	private Modal buildConfigModal(GuildConfig config) {
		TextInput channelField = TextInput.create("plan.channelId", "Plan Channel Id", TextInputStyle.SHORT)
				.setValue(String.valueOf(config.getPlan().getChannelId()))
				.setRequired(true)
				.build();
		return Modal.create("config", "Guild Configuration")
				.addActionRows(ActionRow.of(channelField))
				.build();
	}
}
