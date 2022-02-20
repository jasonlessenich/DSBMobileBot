package com.dynxsty.dsbmobilebot;

import com.dynxsty.dih4jda.DIH4JDABuilder;
import com.dynxsty.dsbmobilebot.config.BotConfig;
import com.dynxsty.dsbmobilebot.listener.StartupListener;
import com.dynxsty.dsbmobilebot.tasks.PresenceUpdater;
import de.sematre.dsbmobile.DSBMobile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class Bot {
	/**
	 * The set of configuration properties that this bot uses.
	 */
	public static BotConfig config;

	public static DSBMobile dsbMobile;

	/**
	 * A general-purpose thread pool that can be used by the bot to execute
	 * tasks outside the main event processing thread.
	 */
	public static ScheduledExecutorService asyncPool;

	/**
	 * The main method that starts the bot.
	 * <ol>
	 *     <li>Setting default timezone to UTC to make development easier.</li>
	 *     <li>Setting async pool to allow us to execute tasks outside of the main processing thread.</li>
	 *     <li>Creating and configuring the {@link JDA} instance.</li>
	 *
	 * </ol>
	 *
	 * @param args Command-Line arguments.
	 * @throws Exception If any Exceptions are encountered during bot creation.
	 */
	public static void main(String[] args) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Berlin").getRules().getOffset(Instant.now())));
		config = new BotConfig(Path.of("config"));
		dsbMobile = new DSBMobile(config.getSystems().getDsbMobile().getUsername(), config.getSystems().getDsbMobile().getPassword());
		asyncPool = Executors.newScheduledThreadPool(config.getSystems().getAsyncPoolSize());
		var jda = JDABuilder.createDefault(config.getSystems().getJdaBotToken())
				.enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.build();
		DIH4JDABuilder.setJDA(jda)
				.setCommandsPackage("com.dynxsty.dsbmobilebot.systems")
				.build();
		addEventListener(jda);
	}

	private static void addEventListener(JDA jda) {
		jda.addEventListener(
				PresenceUpdater.standardActivities(),
				new StartupListener()
		);
	}
}