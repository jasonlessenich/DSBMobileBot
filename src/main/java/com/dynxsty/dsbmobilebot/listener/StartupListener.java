package com.dynxsty.dsbmobilebot.listener;

import com.dynxsty.dsbmobilebot.Bot;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

@Slf4j
public class StartupListener extends ListenerAdapter {
	@Override
	public void onReady(ReadyEvent event) {
		Bot.config.loadGuilds(event.getJDA().getGuilds());
		Bot.config.flush();
		log.info("Logged in as {}", event.getJDA().getSelfUser().getAsTag());
		Bot.asyncPool.scheduleAtFixedRate(() -> Bot.timeTableChecker.checkForNewTimeTables(event.getJDA()),
				0, 30, TimeUnit.SECONDS
		);
	}
}
