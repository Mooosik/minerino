package net.mooosik.minerino;


import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.mooosik.minerino.chat.ColorCalculator;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import net.mooosik.minerino.twitch.TwitchEventHandler;
import net.mooosik.minerino.util.SizedStack;

import java.awt.*;

public class Minerino implements ModInitializer {

	/**
	 *
	 */
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		System.out.println("Starting Minerino");


		ModConfig.getConfig().load();
		ColorCalculator.setup();

		Twitch.getChatMessages().put("Minecraft", new SizedStack<>(50));
	}


}
