package com.drjam.rumourreminder;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.coords.WorldPoint;

import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Rumour Reminder", description = "Reminds you of the rumours you've heard in the Hunter's Guild", tags = {
		"hunter", "rumour", "hunter's", "guild", "reminder", "contract", "varlamore", "task" })
public class RumourReminderPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private RumoursManager rumoursManager;

	@Inject
	private NpcOverlayService npcOverlayService;

	@Inject
	private RumourReminderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RumourReminderConfig config;

	private static final int HUNTER_GUILD_GROUND_FLOOR_REGION_ID = 6191;
	private static final int HUNTER_GUILD_BASEMENT_REGION_ID = 6291;

	private WorldPoint currentLocation = null;
	private boolean xpInitialised = false;

	private String whistleChargesPrefix = "Your quetzal whistle has";
	private Pattern whistleChargesPattern = Pattern.compile("Your quetzal whistle has (\\d+) charges remaining.");
	private Pattern whistleRechargePattern = Pattern
			.compile("(?:There you go\\. Some whistle charges|Looks like the birds are all full for now\\.)");

	public boolean hasEnoughWhistleCharges = true;

	@Provides
	RumourReminderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(RumourReminderConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		npcOverlayService.registerHighlighter(this::highlighter);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		rumoursManager.removeInfoBox();
		npcOverlayService.unregisterHighlighter(this::highlighter);
		overlayManager.remove(overlay);
		xpInitialised = false;
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals(RumourReminderConfig.CONFIG_GROUP)) {
			return;
		}
		npcOverlayService.rebuild();
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
		handleWhistleCharges(event);
		handleWhistleRecharge(event);
	}

	private void handleWhistleMessage(ChatMessage message) {
		if (message.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}
		rumoursManager.updateFromWhistle(message);
		npcOverlayService.rebuild();
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
		npcOverlayService.rebuild();
	}

	private void handleWhistleCharges(ChatMessage message) {
		if (message.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}

		if (!message.getMessage().startsWith(whistleChargesPrefix)) {
			return;
		}

		var chargeMatcher = whistleChargesPattern.matcher(message.getMessage());

		if (!chargeMatcher.find()) {
			return;
		}
		var charges = Integer.parseInt(chargeMatcher.group(1));
		this.hasEnoughWhistleCharges = charges > config.whistleWarningCharges();
	}

	private void handleWhistleRecharge(ChatMessage message) {
		if (message.getType() != ChatMessageType.DIALOG) {
			return;
		}
		
		var rechargeMatcher = whistleRechargePattern.matcher(message.getMessage());
		if (!rechargeMatcher.find()) {
			return;
		}

		this.hasEnoughWhistleCharges = true;
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
			npcOverlayService.rebuild();
		}

	}

	@Subscribe
	void onStatChanged(StatChanged event) {
		if (event.getSkill() == Skill.HUNTER) {
			if (!xpInitialised) {
				xpInitialised = true;
				return;
			}
			rumoursManager.updateFromSavedInfo();
			npcOverlayService.rebuild();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (!rumoursManager.isInfoBoxVisible()) {
			return;
		}
		if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
			rumoursManager.updateFromSavedInfo();
			npcOverlayService.rebuild();
		}
	}

	private HighlightedNpc highlighter(NPC npc) {
		Integer npcId = npc.getId();
		Integer highlightId = rumoursManager.getHighlightNpcId();

		if (highlightId == null) {
			return null;
		}

		if (config.highlightTurnInhunter() && npcId.equals(highlightId)) {
			return HighlightedNpc
					.builder()
					.npc(npc)
					.highlightColor(config.turnInColor())
					.fillColor(config.turnInFillColor())
					.hull(config.turnInHull())
					.tile(config.turnInTile())
					.outline(config.turnInOutline())
					.borderWidth((float) config.turnInBorderWidth())
					.outlineFeather(config.turnInOutlineFeather())
					.build();
		}

		return null;
	}
}
