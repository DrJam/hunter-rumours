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
		// rumoursManager.setStoredRumours();
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
		// String rumourGilman = rumoursManager.getStoredRumour("rumourGilman");
		// String rumourAco = rumoursManager.getStoredRumour("rumourAco");
		// String rumourTeco = rumoursManager.getStoredRumour("rumourTeco");
		// String rumourOrnus = rumoursManager.getStoredRumour("rumourOrnus");
		// String rumourCervus = rumoursManager.getStoredRumour("rumourCervus");
		// String rumourWolf = rumoursManager.getStoredRumour("rumourWolf");
		// rumoursManager.setAllRumours(rumourGilman, rumourAco, rumourCervus,
		// rumourOrnus, rumourTeco, rumourWolf);

		// String activeRumour = rumoursManager.getStoredRumour("activeRumour");
		// rumoursManager.setActiveRumour(activeRumour);

		// rumoursManager.setStoredRumours();
		// rumoursManager.updateData();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
			rumoursManager.loadAllRumours();
		}
	}

	@Subscribe
	public void onGameTick(GameTick t) {
		// if (client.getGameState() != GameState.LOGGED_IN)
		// {
		// lastTickLocation = null;
		// return;
		// }

		// WorldPoint loc = lastTickLocation;
		// lastTickLocation = client.getLocalPlayer().getWorldLocation();

		// if (loc == null || loc.getRegionID() != lastTickLocation.getRegionID())
		// {
		// return;
		// }

		// //if (loc.getRegionID() == HUNTER_GUILD_REGION_ID)
		// //{

		// //}

		// rumoursManager.updateData();
	}

	@Subscribe
	void onChatMessage(ChatMessage event) {
		handleWhistleMessage(event);
		handleHunterDialog(event);
		// if (event.getType() != ChatMessageType.GAMEMESSAGE) {
		// return;
		// }

		// if (rumoursManager.isCheckHunterTask(event.getMessage())) {
		// rumoursManager.updateData();
		// }

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
		// get location and check region id

		rumoursManager.updateFromDialog(message);
	}

	@Subscribe
	void onStatChanged(StatChanged event) {
		// if (event.getSkill() == Skill.HUNTER)
		// {
		// rumoursManager.updateData();
		// }
	}
}
