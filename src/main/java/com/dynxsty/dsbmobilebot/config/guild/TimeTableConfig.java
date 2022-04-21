package com.dynxsty.dsbmobilebot.config.guild;

import com.dynxsty.dsbmobilebot.config.GuildConfigItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.entities.TextChannel;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimeTableConfig extends GuildConfigItem {
	private long timeTableChannelId;

	public TextChannel getTimeTableChannel() {
		return this.getGuild().getTextChannelById(this.timeTableChannelId);
	}
}
