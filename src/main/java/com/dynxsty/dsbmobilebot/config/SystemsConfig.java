package com.dynxsty.dsbmobilebot.config;

import lombok.Data;

@Data
public class SystemsConfig {

	/**
	 * The token used to create the JDA Discord bot instance.
	 */
	private String jdaBotToken = "";

	/**
	 * Configuration for the DSBMobile client.
	 */
	private DSBMobileConfig dsbMobile = new DSBMobileConfig();

	/**
	 * The number of threads to allocate to the bot's general purpose async
	 * thread pool.
	 */
	private int asyncPoolSize = 8;

	/**
	 * Configuration for the Hikari connection pool that's used for the bot's
	 * SQL data source.
	 */
	private HikariConfig hikariConfig = new HikariConfig();

	/**
	 * Configuration for the DSBMobile client.
	 */
	@Data
	public static class DSBMobileConfig {
		private String username = "";
		private String password = "";
	}

	/**
	 * Configuration settings for the Hikari connection pool.
	 */
	@Data
	public static class HikariConfig {
		private String jdbcUrl = "jdbc:h2:tcp://localhost:9130/./dsb_bot";
		private int maximumPoolSize = 5;
	}
}
