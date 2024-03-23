package com.hunterrumours;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("HunterRumours")
public interface HunterRumoursConfig extends Config
{
	String CONFIG_GROUP = "HunterRumours";
	@ConfigItem(
		keyName = "rumourInfoBox",
		name = "Rumours Info Box",
		description = "Show an info box for hunter rumours"
	)
	default boolean showRumourInfoBox()
	{
		return true;
	}
}
