package com.dynxsty.dsbmobilebot.util;

import de.sematre.dsbmobile.DSBMobile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlanUtils {

	private PlanUtils() {}

	public static MessageAction buildPlanAction(MessageChannel channel, List<DSBMobile.TimeTable> tables, DSBMobile.TimeTable table) throws IOException {
		return channel.sendMessageEmbeds(buildPlanEmbed(table))
				.addFile(new URL(table.getDetail()).openStream(), String.format("%s-%s.png", table.getUUID(), tables.indexOf(table)));
	}

	public static MessageEmbed buildPlanEmbed(DSBMobile.TimeTable table) {
		return new EmbedBuilder()
				.setTitle(String.format("%s (%s)", table.getGroupName(), table.getTitle()), table.getDetail())
				.setFooter(table.getDate())
				.setTimestamp(LocalDateTime.parse(table.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
				.build();
	}
}
