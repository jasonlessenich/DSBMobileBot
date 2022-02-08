package com.dynxsty.dsbmobilebot.config;

import lombok.Data;

@Data
public class SystemsConfig {

	/**
	 * The token used to create the JDA Discord bot instance.
	 */
	private String jdaBotToken = "";


	private DSBMobileConfig dsbMobile = new DSBMobileConfig();

	/**
	 * The number of threads to allocate to the bot's general purpose async
	 * thread pool.
	 */
	private int asyncPoolSize = 4;

	/**
	 * Configuration settings for the DSBMobile client.
	 */
	@Data
	public static class DSBMobileConfig {
		private String username = "";
		private String password = "";
	}
}
