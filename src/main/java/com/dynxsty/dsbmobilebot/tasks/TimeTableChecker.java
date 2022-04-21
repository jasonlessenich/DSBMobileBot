package com.dynxsty.dsbmobilebot.tasks;

import com.dynxsty.dsbmobilebot.Bot;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimeTableChecker {

	private List<DSBMobile.TimeTable> timetables;

	public TimeTableChecker() {
		this.timetables = new ArrayList<>();
	}

	public void checkForNewTimeTables(JDA jda) {
		List<DSBMobile.TimeTable> tables = Bot.dsbMobile.getTimeTables();
		log.info("Checking for new Timetables...");
		if (!compareList(this.timetables, tables)) {
			if (tables.size() <= 0) return;
			this.timetables = tables;
			for (Guild guild : jda.getGuilds()) {
				TextChannel log = Bot.config.get(guild).getPlan().getTimeTableChannel();
				if (log == null) return;
				log.sendMessageFormat("There are **%s** new timetables!", tables.size()).queue();
				for (DSBMobile.TimeTable table : tables) {
					try {
						log.sendMessageFormat("\"%s\" | `%s`", table.getGroupName(), table.getDate())
								.addFile(new URL(table.getDetail()).openStream(), String.format("%s-%s.png", table.getUUID(), tables.indexOf(table)))
								.queue();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			log.info("Sent new Timetables to #{}", log.getName());
		}
	}

	private boolean compareList(List<DSBMobile.TimeTable> ls1, List<DSBMobile.TimeTable> ls2) {
		return ls1.containsAll(ls2) && ls1.size() == ls2.size();
	}
}
