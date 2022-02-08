package com.dynxsty.dsbmobilebot.listener;

import com.dynxsty.dsbmobilebot.Bot;
import com.dynxsty.dsbmobilebot.tasks.PlanChecker;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class StartupListener extends ListenerAdapter {
	@Override
	public void onReady(ReadyEvent event) {
		Bot.config.loadGuilds(event.getJDA().getGuilds());
		Bot.config.flush();
		for (var guild : event.getJDA().getGuilds()) {
			Bot.interactionHandler.registerCommands(guild);
		}
		log.info("Logged in as {}", event.getJDA().getSelfUser().getAsTag());
		new PlanChecker().checkForNewPlans(event.getJDA());
	}
}
