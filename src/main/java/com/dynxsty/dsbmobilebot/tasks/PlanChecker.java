package com.dynxsty.dsbmobilebot.tasks;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.util.GuildUtils;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PlanChecker {

	private List<DSBMobile.TimeTable> planDates;

	public PlanChecker() {
		this.planDates = Bot.dsbMobile.getTimeTables();
	}

	public void checkForNewPlans(JDA jda) {
		Bot.asyncPool.scheduleWithFixedDelay(() -> {
			var tables = Bot.dsbMobile.getTimeTables();
			log.info("Checking for new Plans...");
			if (!compareList(this.planDates, tables)) {
				if (tables.size() <= 0) return;
				this.planDates = tables;
				for (var guild : jda.getGuilds()) {
					var log = GuildUtils.getPlanChannel(guild);
					log.sendMessageFormat("There are **%s** new plans! %s", tables.size(), Bot.config.get(guild).getPlan().getPlanPingRole().getAsMention()).queue();
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
				log.info("Sent new Plans to Log Channel!");
			} else {
				log.info("Could not find any new Plans.");
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	private boolean compareList(List<DSBMobile.TimeTable> ls1, List<DSBMobile.TimeTable> ls2){
		return ls1.containsAll(ls2) && ls1.size() == ls2.size();
	}
}
