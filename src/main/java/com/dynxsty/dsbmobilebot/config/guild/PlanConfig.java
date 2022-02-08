package com.dynxsty.dsbmobilebot.config.guild;

import com.dynxsty.dsbmobilebot.config.GuildConfigItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlanConfig extends GuildConfigItem {
	private long planChannelId;
	private long planPingRoleId;

	public TextChannel getPlanChannel() {
		return this.getGuild().getTextChannelById(this.planChannelId);
	}

	public Role getPlanPingRole() { return this.getGuild().getRoleById(this.planPingRoleId); }
}
