package com.drjam.rumourreminder;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RumourReminderPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(RumourReminderPlugin.class);
		RuneLite.main(args);
	}
}