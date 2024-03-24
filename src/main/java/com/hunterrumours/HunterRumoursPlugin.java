package com.hunterrumours;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Hunter Rumours"
)
public class HunterRumoursPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HunterRumoursConfig config;

	@Inject
	private RumoursManager rumoursManager;

	private WorldPoint lastTickLocation;

	private static final int HUNTER_GUILD_REGION_ID = 6291;

	@Override
	protected void startUp() throws Exception
	{

	}

	@Override
	protected void shutDown() throws Exception
	{
		rumoursManager.setStoredRumours();
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e)
	{
		String rumourGilman = rumoursManager.getStoredRumour("rumourGilman");
		rumoursManager.setRumour(rumourGilman,"Gilman");
		String rumourAco = rumoursManager.getStoredRumour("rumourAco");
		rumoursManager.setRumour(rumourAco,"Aco");
		String rumourTeco = rumoursManager.getStoredRumour("rumourTeco");
		rumoursManager.setRumour(rumourTeco,"Teco");
		String rumourOrnus = rumoursManager.getStoredRumour("rumourOrnus");
		rumoursManager.setRumour(rumourOrnus,"Ornus");
		String rumourCervus = rumoursManager.getStoredRumour("rumourCervus");
		rumoursManager.setRumour(rumourCervus,"Cervus");
		String rumourWolf = rumoursManager.getStoredRumour("rumourWolf");
		rumoursManager.setRumour(rumourWolf,"Wolf");

		String activeRumour = rumoursManager.getStoredRumour("activeRumour");
		rumoursManager.setActiveRumour(activeRumour);

		rumoursManager.setStoredRumours();
		rumoursManager.updateData();
	}

	@Provides
    HunterRumoursConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HunterRumoursConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick t)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			lastTickLocation = null;
			return;
		}

		WorldPoint loc = lastTickLocation;
		lastTickLocation = client.getLocalPlayer().getWorldLocation();

		if (loc == null || loc.getRegionID() != lastTickLocation.getRegionID())
		{
			return;
		}

		//if (loc.getRegionID() == HUNTER_GUILD_REGION_ID)
		//{

		//}

		rumoursManager.updateData();
	}


	@Subscribe void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (rumoursManager.isCheckHunterTask(event.getMessage()))
		{
			rumoursManager.updateData();
		}

	}

	@Subscribe void onStatChanged(StatChanged event)
	{
		if (event.getSkill() == Skill.HUNTER)
		{
			rumoursManager.updateData();
		}
	}
}
