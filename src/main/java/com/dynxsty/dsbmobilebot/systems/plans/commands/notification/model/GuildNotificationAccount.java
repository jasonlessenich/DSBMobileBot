package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model;

import lombok.Data;

@Data
public class GuildNotificationAccount {
	private long userId;
	private int classLevel;
	private String[] subjects;
}
