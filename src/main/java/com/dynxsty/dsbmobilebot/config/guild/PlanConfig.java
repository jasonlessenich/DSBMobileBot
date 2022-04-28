package com.dynxsty.dsbmobilebot.config.guild;

import com.dynxsty.dsbmobilebot.config.GuildConfigItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.entities.TextChannel;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlanConfig extends GuildConfigItem {
	private long channelId;

	public TextChannel getChannel() {
		return this.getGuild().getTextChannelById(this.channelId);
	}
}
