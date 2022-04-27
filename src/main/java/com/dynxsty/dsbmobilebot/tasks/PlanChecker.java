package com.dynxsty.dsbmobilebot.tasks;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.util.PlanUtils;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PlanChecker {

	private List<DSBMobile.TimeTable> timetables;

	public PlanChecker() {
		this.timetables = new ArrayList<>();
	}

	public void checkForNewPlans(JDA jda) {
		List<DSBMobile.TimeTable> tables = Bot.dsbMobile.getTimeTables();
		log.info("Checking for new Plans...");
		if (!compareList(this.timetables, tables)) {
			if (tables.size() <= 0) return;
			this.timetables = tables;
			for (Guild guild : jda.getGuilds()) {
				MessageChannel logChannel = Bot.config.get(guild).getPlan().getTimeTableChannel();
				if (logChannel == null) return;
				logChannel.sendMessageFormat("Found **%s** new plans!", tables.size()).queue();
				for (DSBMobile.TimeTable table : tables) {
					try {
						PlanUtils.buildPlanAction(logChannel, tables, table).queue();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("Sent new Plans to #{}", logChannel.getName());
			}
		}
	}

	private boolean compareList(List<DSBMobile.TimeTable> ls1, List<DSBMobile.TimeTable> ls2) {
		return ls1.containsAll(ls2) && ls1.size() == ls2.size();
	}
}
