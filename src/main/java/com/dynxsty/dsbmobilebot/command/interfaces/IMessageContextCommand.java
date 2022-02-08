package com.dynxsty.dsbmobilebot.command.interfaces;

import com.dynxsty.dsbmobilebot.command.ResponseException;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

/**
 * Interface that handles Discord's Message Context Commands.
 */
public interface IMessageContextCommand {
	ReplyCallbackAction handleMessageContextCommandInteraction(MessageContextInteractionEvent event) throws ResponseException;
}
