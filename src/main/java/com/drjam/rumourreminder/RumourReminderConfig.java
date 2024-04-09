package com.drjam.rumourreminder;

import java.awt.Color;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("RumourReminder")
public interface RumourReminderConfig extends Config {
	String CONFIG_GROUP = "RumourReminder";

	@ConfigItem(
			keyName = "highlightTurnIn",
			name = "Highlight Turn In",
			description = "Highlight the hunter you were given the rumour by",
			position = 0
	)
	default boolean highlightTurnInhunter() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightEquivalent",
			name = "Highlight Equivalent",
			description = "Highlight hunters who could also receive the rumour item (e.g. kebbit tuft)",
			position = 1
	)
	default boolean highlightEquivalent() {
		return true;
	}

	@ConfigSection(
			name = "Turn In Style",
			description = "Style for the turn in hunter",
			position = 3,
			closedByDefault = true
	)
	String turnInSection = "turnInSection";

	@ConfigItem(
			position = 0,
			keyName = "turnInHull",
			name = "Highlight hull",
			description = "Configures whether or not hunter should be highlighted by hull",
			section = turnInSection
	)
	default boolean turnInHull()
	{
		return false;
	}

	@ConfigItem(
			position = 1,
			keyName = "turnInTile",
			name = "Highlight tile",
			description = "Configures whether or not hunter should be highlighted by tile",
			section = turnInSection
	)
	default boolean turnInTile()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "turnInOutline",
			name = "Highlight outline",
			description = "Configures whether or not the model of the hunter should be highlighted by outline",
			section = turnInSection
	)
	default boolean turnInOutline()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 3,
			keyName = "turnInColor",
			name = "Highlight Color",
			description = "Color of the hunter highlight border, menu, and text",
			section = turnInSection
	)
	default Color turnInColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "turnInFillColor",
			name = "Fill Color",
			description = "Color of the hunter highlight fill",
			section = turnInSection
	)
	default Color turnInFillColor()
	{
		return new Color(0, 255, 0, 20);
	}

	@ConfigItem(
			position = 5,
			keyName = "turnInBorderWidth",
			name = "Border Width",
			description = "Width of the highlighted hunter border",
			section = turnInSection
	)
	default double turnInBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			position = 6,
			keyName = "turnInOutlineFeather",
			name = "Outline feather",
			description = "Specify between 0-4 how much of the model outline should be faded",
			section = turnInSection
	)
	@Range(
			min = 0,
			max = 4
	)
	default int turnInOutlineFeather()
	{
		return 0;
	}

	@ConfigSection(
			name = "Equivalent Style",
			description = "Style for the hunters with equivalent rumours",
			position = 4,
			closedByDefault = true
	)
	String equivalentSection = "equivalentSection";

	@ConfigItem(
			position = 0,
			keyName = "equivalentHull",
			name = "Highlight hull",
			description = "Configures whether or not hunter should be highlighted by hull",
			section = equivalentSection
	)
	default boolean equivalentHull()
	{
		return false;
	}

	@ConfigItem(
			position = 1,
			keyName = "equivalentTile",
			name = "Highlight tile",
			description = "Configures whether or not hunter should be highlighted by tile",
			section = equivalentSection
	)
	default boolean equivalentTile()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "equivalentOutline",
			name = "Highlight outline",
			description = "Configures whether or not the model of the hunter should be highlighted by outline",
			section = equivalentSection
	)
	default boolean equivalentOutline()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 3,
			keyName = "equivalentColor",
			name = "Highlight Color",
			description = "Color of the hunter highlight border, menu, and text",
			section = equivalentSection
	)
	default Color equivalentColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "equivalentFillColor",
			name = "Fill Color",
			description = "Color of the hunter highlight fill",
			section = equivalentSection
	)
	default Color equivalentFillColor()
	{
		return new Color(255, 255, 0, 20);
	}

	@ConfigItem(
			position = 5,
			keyName = "equivalentBorderWidth",
			name = "Border Width",
			description = "Width of the highlighted hunter border",
			section = equivalentSection
	)
	default double equivalentBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			position = 6,
			keyName = "equivalentOutlineFeather",
			name = "Outline feather",
			description = "Specify between 0-4 how much of the model outline should be faded",
			section = equivalentSection
	)
	@Range(
			min = 0,
			max = 4
	)
	default int equivalentOutlineFeather()
	{
		return 0;
	}


}
