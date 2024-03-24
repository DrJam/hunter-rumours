package com.hunterrumours;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Hunter Rumours")
public class HunterRumoursPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private HunterRumoursConfig config;

	@Inject
	private RumoursManager rumoursManager;

	private static final int HUNTER_GUILD_GROUND_FLOOR_REGION_ID = 6191;
	private static final int HUNTER_GUILD_BASEMENT_REGION_ID = 6291;

	private WorldPoint currentLocation = null;
	private boolean xpInitialised = false;

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
		xpInitialised = false;
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
		} else if (event.getGameState() == GameState.LOGIN_SCREEN) {
			rumoursManager.removeInfoBox();
			currentLocation = null;
			xpInitialised = false;
		}
	}

	@Subscribe
	public void onWorldChanged(WorldChanged event) {
		xpInitialised = false;
	}

	@Subscribe
	void onChatMessage(ChatMessage event) {
		handleWhistleMessage(event);
		handleHunterDialog(event);
	}

	@Subscribe
	void onGameTick(GameTick event) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			currentLocation = null;
			return;
		}

		var previousLocation = currentLocation;
		currentLocation = client.getLocalPlayer().getWorldLocation();
		var currentRegionId = currentLocation.getRegionID();

		var loggingInToGuild = currentLocation != null &&
				previousLocation == null &&
				(currentRegionId == HUNTER_GUILD_BASEMENT_REGION_ID
						|| currentRegionId == HUNTER_GUILD_GROUND_FLOOR_REGION_ID);
		var enteringGuild = currentLocation != null &&
				previousLocation != null &&
				currentRegionId != previousLocation.getRegionID() &&
				(currentRegionId == HUNTER_GUILD_BASEMENT_REGION_ID
						|| currentRegionId == HUNTER_GUILD_GROUND_FLOOR_REGION_ID);

		if (loggingInToGuild || enteringGuild) {
			rumoursManager.updateFromSavedInfo();
		}

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
		if (event.getSkill() == Skill.HUNTER) {
			if (!xpInitialised) {
				xpInitialised = true;
				return;
			}
			rumoursManager.updateFromSavedInfo();
		}
	}
}
