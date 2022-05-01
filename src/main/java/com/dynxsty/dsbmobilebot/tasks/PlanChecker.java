package com.dynxsty.dsbmobilebot.tasks;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.systems.plans.PlanProcessor;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

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
			if (tables.isEmpty()) return;
			this.timetables = tables;
			for (Guild guild : jda.getGuilds()) {
				MessageChannel logChannel = Bot.config.get(guild).getPlan().getChannel();
				if (logChannel == null) {
					log.warn("Could not find Log Channel for " + guild.getName());
					return;
				}
				logChannel.sendMessageFormat("Found **%s** new plans!", tables.size()).queue();
				try {
					PlanProcessor.buildPlanAction(guild, logChannel, tables, true).forEach(MessageAction::queue);
				} catch (Exception e) {
					log.error("Couldn't send new Plans to Log Channel: ", e);
				}
				log.info("Sent new Plans to #{} ({})", logChannel.getName(), guild.getName());
			}
		}
	}

	private boolean compareList(List<DSBMobile.TimeTable> ls1, List<DSBMobile.TimeTable> ls2) {
		return ls1.containsAll(ls2) && ls1.size() == ls2.size();
	}
}
