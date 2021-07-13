package net.mooosik.minerino;


import net.fabricmc.api.ModInitializer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import net.mooosik.minerino.util.SizedStack;

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

		Twitch.getChatMessages().put("MC", new SizedStack<>(50));

	}


}
