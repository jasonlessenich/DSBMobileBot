package com.dynxsty.dsbmobilebot.config.guild;

import com.dynxsty.dsbmobilebot.config.GuildConfigItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.entities.TextChannel;

@Data
@EqualsAndHashCode(callSuper = true)
public class ModerationConfig extends GuildConfigItem {
	private long logChannelId;
	public TextChannel getLogChannel() {
		return this.getGuild().getTextChannelById(this.logChannelId);
	}
}
