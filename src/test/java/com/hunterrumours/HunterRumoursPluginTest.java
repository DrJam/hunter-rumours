package com.hunterrumours;

import com.drjam.rumourreminder.RumourReminderPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HunterRumoursPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RumourReminderPlugin.class);
		RuneLite.main(args);
	}
}