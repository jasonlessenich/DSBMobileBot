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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanUtils {

	private PlanUtils() {
	}

	public static Collection<MessageAction> buildPlanAction(MessageChannel channel, List<DSBMobile.TimeTable> tables) throws IOException {
		Map<String, MessageAction> actions = new HashMap<>();
		for (DSBMobile.TimeTable table : tables) {
			// group new plans by date
			if (actions.containsKey(table.getTitle())) {
				MessageAction action = actions.get(table.getTitle());
				actions.put(table.getTitle(),
						action.addFile(new URL(table.getDetail()).openStream(),
								String.format("%s-%s.png", table.getUUID(), tables.indexOf(table))));
			} else {
				actions.put(table.getTitle(),
						channel.sendMessageEmbeds(buildPlanEmbed(table))
								.addFile(new URL(table.getDetail()).openStream(),
										String.format("%s-%s.png", table.getUUID(), tables.indexOf(table)))
				);
			}
		}
		return actions.values();
	}

	public static MessageEmbed buildPlanEmbed(DSBMobile.TimeTable table) {
		return new EmbedBuilder()
				.setTitle(String.format("%s (%s)", table.getGroupName(), table.getTitle()), table.getDetail())
				.setFooter(table.getDate())
				.setTimestamp(LocalDateTime.parse(table.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
				.build();
	}
}
