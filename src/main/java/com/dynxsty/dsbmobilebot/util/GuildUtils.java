package com.dynxsty.dsbmobilebot.util;

import com.dynxsty.dsbmobilebot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public class GuildUtils {

	private GuildUtils() {
	}

	public static MessageChannel getPlanChannel(Guild guild) {
		return Bot.config.get(guild).getPlan().getPlanChannel();
	}

}
