package com.hunterrumours;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

@PluginDescriptor(name = "Hunter Rumours")
public class HunterRumoursPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private HunterRumoursConfig config;

	@Inject
	private RumoursManager rumoursManager;

	private static final int HUNTER_GUILD_BASEMENT_REGION_ID = 6291;

	@Provides
	HunterRumoursConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(HunterRumoursConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
	}

	@Override
	protected void shutDown() throws Exception {
		rumoursManager.removeInfoBox();
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
		}
	}

	@Subscribe
	void onChatMessage(ChatMessage event) {
		handleWhistleMessage(event);
		handleHunterDialog(event);
	}

	@Subscribe
	void onGameTick(GameTick event) {
		// var location = client.getLocalPlayer().getWorldLocation();
		// if (location.getRegionID() == HUNTER_GUILD_BASEMENT_REGION_ID) {
		// 	rumoursManager.loadAllRumours();
		
	}


	private void handleWhistleMessage(ChatMessage message) {
		if (message.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}
		rumoursManager.updateFromWhistle(message);
	}

	private void handleHunterDialog(ChatMessage message) {
		if (message.getType() != ChatMessageType.DIALOG) {
			return;
		}

		var location = client.getLocalPlayer().getWorldLocation();
		if (location.getRegionID() != HUNTER_GUILD_BASEMENT_REGION_ID) {
			return;
		}

		rumoursManager.updateFromDialog(message);
	}

	@Subscribe
	void onStatChanged(StatChanged event) {
	}
}
