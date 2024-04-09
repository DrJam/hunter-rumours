package com.drjam.rumourreminder;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.events.ChatMessage;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
public class RumoursManager {
    private static final String WOLF = "Wolf";
    private static final String ACO = "Aco";
    private static final String TECO = "Teco";
    private static final String CERVUS = "Cervus";
    private static final String ORNUS = "Ornus";
    private static final String GILMAN = "Gilman";
    private static final List<String> HUNTER_NAMES = Arrays.asList(WOLF, ACO, TECO, ORNUS, CERVUS, GILMAN);

    private static final Pattern WHISTLE_ACTIVE_PATTERN = Pattern
            .compile("(?:Your current rumour target is (?:a|an)) ([a-zA-Z -]+)(?:\\. You'll)([a-zA-Z ]+)");
    private static final Pattern WHISTLE_NO_ACTIVE_PATTERN = Pattern
            .compile("You do not have an active rumour right now.");

    private static final Pattern RUMOUR_COMPLETION_PATTERN = Pattern.compile(
            "(?:Another one done|Thanks for that)");

    private static final String CONFIG_KEY_ACTIVE_RUMOUR = "activeRumour";
    private static final String CONFIG_KEY_GILMAN_RUMOUR = "rumourGilman";
    private static final String CONFIG_KEY_ACO_RUMOUR = "rumourAco";
    private static final String CONFIG_KEY_CERVUS_RUMOUR = "rumourCervus";
    private static final String CONFIG_KEY_ORNUS_RUMOUR = "rumourOrnus";
    private static final String CONFIG_KEY_TECO_RUMOUR = "rumourTeco";
    private static final String CONFIG_KEY_WOLF_RUMOUR = "rumourWolf";
    private static final String CONFIG_KEY_TEMP_BG_RUMOUR = "tempBgRumour";
    private static final String CONFIG_KEY_TEMP_HUNTER = "tempHunter";
    private static final String CONFIG_KEY_ACTIVE_IS_TEMP = "activeIsTemp";

    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private RumourReminderPlugin plugin;

    @Inject
    RumourReminderConfig config;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    ConfigManager configManager;

    private String rumourGilman = null;
    private String rumourAco = null;
    private String rumourCervus = null;
    private String rumourOrnus = null;
    private String rumourTeco = null;
    private String rumourWolf = null;
    private String activeRumour = null;
    private Boolean activeIsTemp = null;
    private String tempBgRumour = null;
    private String tempHunter = null;

    public boolean isRumourCompleted = false;

    @Getter
    @Setter
    private RumourInfoBox infoBox;

    public boolean isInfoBoxVisible() {
        return infoBox != null;
    }

    public Integer getHighlightNpcId() {
        if (!isRumourCompleted) {
            return null;
        }
        if (activeRumour == null) {
            return null;
        }
        if (!isInfoBoxVisible()) {
            return null;
        }

        if (rumourGilman != null && activeRumour.toLowerCase().equals(rumourGilman.toLowerCase())) {
            return NpcID.HUNTMASTER_GILMAN_NOVICE;
        } else if (rumourAco != null && activeRumour.toLowerCase().equals(rumourAco.toLowerCase())) {
            return NpcID.GUILD_HUNTER_ACO_EXPERT;
        } else if (rumourCervus != null && activeRumour.toLowerCase().equals(rumourCervus.toLowerCase())) {
            return NpcID.GUILD_HUNTER_CERVUS_ADEPT;
        } else if (rumourOrnus != null && activeRumour.toLowerCase().equals(rumourOrnus.toLowerCase())) {
            return NpcID.GUILD_HUNTER_ORNUS_ADEPT;
        } else if (rumourTeco != null && activeRumour.toLowerCase().equals(rumourTeco.toLowerCase())) {
            return NpcID.GUILD_HUNTER_TECO_EXPERT;
        } else if (rumourWolf != null && activeRumour.toLowerCase().equals(rumourWolf.toLowerCase())) {
            return NpcID.GUILD_HUNTER_WOLF_MASTER;
        }

        return null;
    }

    public List<Integer> getEquivalentHighlightNpcIds() {
        var result = new ArrayList<Integer>();
        if (activeRumour == null) {
            return result;
        }
        if (!isInfoBoxVisible()) {
            return result;
        }

        if (rumourGilman != null && !activeRumour.toLowerCase().equals(rumourGilman.toLowerCase())
                && hasEquivalentRumour(rumourGilman)) {
            result.add(NpcID.HUNTMASTER_GILMAN_NOVICE);
        }
        if (rumourOrnus != null && !activeRumour.toLowerCase().equals(rumourOrnus.toLowerCase())
                && hasEquivalentRumour(rumourOrnus)) {
            result.add(NpcID.GUILD_HUNTER_ORNUS_ADEPT);
        }
        if (rumourCervus != null && !activeRumour.toLowerCase().equals(rumourCervus.toLowerCase())
                && hasEquivalentRumour(rumourCervus)) {
            result.add(NpcID.GUILD_HUNTER_CERVUS_ADEPT);
        }
        if (rumourAco != null && !activeRumour.toLowerCase().equals(rumourAco.toLowerCase())
                && hasEquivalentRumour(rumourAco)) {
            result.add(NpcID.GUILD_HUNTER_ACO_EXPERT);
        }
        if (rumourTeco != null && !activeRumour.toLowerCase().equals(rumourTeco.toLowerCase())
                && hasEquivalentRumour(rumourTeco)) {
            result.add(NpcID.GUILD_HUNTER_TECO_EXPERT);
        }
        if (rumourWolf != null && !activeRumour.toLowerCase().equals(rumourWolf.toLowerCase())
                && hasEquivalentRumour(rumourWolf)) {
            result.add(NpcID.GUILD_HUNTER_WOLF_MASTER);
        }

        return result;
    }

    private boolean hasEquivalentRumour(String rumour) {
        if (rumour == null) {
            return false;
        }
        var check = HunterCreature.getHunterCreatureFromCreatureName(rumour.toLowerCase());
        var active = HunterCreature.getHunterCreatureFromCreatureName(activeRumour.toLowerCase());

        return check.targetItemID.equals(active.targetItemID);
    }

    private void rumourCompleted(String hunter) {
        this.activeRumour = null;
        if (hunter.contains(GILMAN)) {
            this.rumourGilman = null;
        }
        if (hunter.contains(CERVUS)) {
            this.rumourCervus = null;
        }
        if (hunter.contains(ORNUS)) {
            this.rumourOrnus = null;
        }
        if (hunter.contains(ACO)) {
            this.rumourAco = null;
        }
        if (hunter.contains(TECO)) {
            this.rumourTeco = null;
        }
        if (hunter.contains(WOLF)) {
            this.rumourWolf = null;
        }
    }

    private void rumourAssigned(String target, String hunter) {

        this.activeRumour = target;
        rumourConfirmed(target, hunter, true);
    }

    private String getRumour(String hunter) {
        if (hunter.contains(GILMAN)) {
            return rumourGilman;
        }
        if (hunter.contains(CERVUS)) {
            return rumourCervus;
        }
        if (hunter.contains(ORNUS)) {
            return rumourOrnus;
        }
        if (hunter.contains(ACO)) {
            return rumourAco;
        }
        if (hunter.contains(TECO)) {
            return rumourTeco;
        }
        if (hunter.contains(WOLF)) {
            return rumourWolf;
        }
        return null;
    }

    private void resetRumours() {
        this.rumourGilman = null;
        this.rumourAco = null;
        this.rumourCervus = null;
        this.rumourOrnus = null;
        this.rumourTeco = null;
        this.rumourWolf = null;
        this.clearTemp();
    }

    private void rumourConfirmed(String target, String hunter, boolean isActive) {
        if (isActive) {
            this.activeRumour = target;
        }
        if (hunter.contains(GILMAN)) {
            this.rumourGilman = target;
        }
        if (hunter.contains(CERVUS)) {
            this.rumourCervus = target;
        }
        if (hunter.contains(ORNUS)) {
            this.rumourOrnus = target;
        }
        if (hunter.contains(ACO)) {
            this.rumourAco = target;
        }
        if (hunter.contains(TECO)) {
            this.rumourTeco = target;
        }
        if (hunter.contains(WOLF)) {
            this.rumourWolf = target;
        }
    }

    public void removeInfoBox() {
        if (infoBox != null) {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }
    }

    private void updateInfoBox() {
        removeInfoBox();

        if (activeRumour == null && rumourGilman == null && rumourAco == null && rumourCervus == null
                && rumourOrnus == null && rumourTeco == null && rumourWolf == null) {
            return;
        }

        if (activeRumour != null) {
            HunterCreature rumourCreature = HunterCreature
                    .getHunterCreatureFromCreatureName(activeRumour.toLowerCase());
            if (rumourCreature == null) {
                return;
            }
            infoBox = new RumourInfoBox(itemManager.getImage(rumourCreature.targetItemID), plugin, activeRumour,
                    rumourGilman, rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, isRumourCompleted);

        } else {
            infoBox = new RumourInfoBox(itemManager.getImage(ItemID.GUILD_HUNTER_HEADWEAR), plugin, activeRumour,
                    rumourGilman,
                    rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, isRumourCompleted);

        }
        infoBoxManager.addInfoBox(infoBox);
    }

    public void updateFromSavedInfo() {
        loadAllRumours();
        updateRumourCompleted();
        updateInfoBox();
    }

    public void updateRumourCompleted() {
        var inventory = this.client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) {
            isRumourCompleted = false;
            return;
        }
        var items = inventory.getItems();
        if (items == null || activeRumour == null) {
            isRumourCompleted = false;
            return;
        }

        var activeContractHunterCreature = HunterCreature.getHunterCreatureFromCreatureName(activeRumour);
        for (var item : items) {
            if (item.getId() == activeContractHunterCreature.targetItemID) {
                isRumourCompleted = true;
                return;
            }
        }
        isRumourCompleted = false;
    }

    public void updateFromWhistle(ChatMessage message) {
        Matcher activeMacher = WHISTLE_ACTIVE_PATTERN.matcher(message.getMessage());
        Matcher noActiveMacher = WHISTLE_NO_ACTIVE_PATTERN.matcher(message.getMessage());

        if (!activeMacher.find() && !noActiveMacher.find()) {
            return;
        }

        loadAllRumours();
        if (noActiveMacher.find()) {
            this.activeRumour = null;
        } else {
            this.activeRumour = activeMacher.group(1);
        }
        saveAllStoredRumours();
        updateRumourCompleted();
        updateInfoBox();
    }

    public void updateFromDialog(ChatMessage message) {
        String hunterTalking = null;
        String hunterReferenced = null;
        String creature = null;

        var messageParts = message.getMessage().split("\\|");
        var prefix = messageParts[0];
        var contents = messageParts[1];

        for (String hunterName : HUNTER_NAMES) {
            if (prefix.contains(hunterName)) {
                hunterTalking = hunterName;
                break;
            }
        }
        for (String hunterName : HUNTER_NAMES) {
            if (contents.contains(hunterName)) {
                hunterReferenced = hunterName;
                break;
            }
        }
        for (HunterCreature creatureType : HunterCreature.values()) {
            if (contents.toLowerCase().contains(creatureType.name.toLowerCase())) {
                creature = creatureType.name;
                break;
            }
        }

        if (hunterTalking == null) {
            return;
        }

        loadAllRumours();

        var isRumourCompletion = hunterTalking != null && RUMOUR_COMPLETION_PATTERN.matcher(contents).find();
        var isAssignmantOrGilman = hunterTalking != null && hunterReferenced == null && creature != null;
        var isGilmanRemembering = hunterTalking == GILMAN && contents.startsWith("I seem to remember");
        var isGilmanResetAssignment = hunterTalking == GILMAN && creature != null && !creature.equals(rumourGilman)
                && rumourGilman != null;
        var isRumourConfirmation = hunterTalking != null && hunterReferenced != null && creature != null;
        var isTempAssignment = activeRumour == null && getRumour(hunterTalking) != null;

        if (isRumourCompletion) {
            rumourCompleted(hunterTalking);
        } else if (isAssignmantOrGilman) {
            if (isGilmanRemembering) {
                rumourConfirmed(creature, hunterTalking, false);
            } else {// is Assignment
                if (this.activeRumour != null && this.activeIsTemp) {
                    // if moving off a temp, put it back & clear
                    this.reapplyBgRumour();
                }
                if (isTempAssignment) {
                    tempBeingAssigned(hunterTalking);
                } else if (isGilmanResetAssignment) {
                    resetRumours();
                }
                rumourAssigned(creature, hunterTalking);
            }
        } else if (isRumourConfirmation) {
            rumourConfirmed(creature, hunterReferenced, true);
        }

        saveAllStoredRumours();
        updateRumourCompleted();
        updateInfoBox();
    }

    private void reapplyBgRumour() {
        if (this.tempHunter.contains(GILMAN)) {
            this.rumourGilman = this.tempBgRumour;
        }
        if (this.tempHunter.contains(CERVUS)) {
            this.rumourCervus = this.tempBgRumour;
        }
        if (this.tempHunter.contains(ORNUS)) {
            this.rumourOrnus = this.tempBgRumour;
        }
        if (this.tempHunter.contains(ACO)) {
            this.rumourAco = this.tempBgRumour;
        }
        if (this.tempHunter.contains(TECO)) {
            this.rumourTeco = this.tempBgRumour;
        }
        if (this.tempHunter.contains(WOLF)) {
            this.rumourWolf = this.tempBgRumour;
        }
        this.clearTemp();
    }

    private void clearTemp() {
        this.tempBgRumour = null;
        this.tempHunter = null;
        this.activeIsTemp = false;
    }

    private void tempBeingAssigned(String hunterTalking) {
        this.tempBgRumour = getRumour(hunterTalking);
        this.tempHunter = hunterTalking;
        this.activeIsTemp = true;
    }

    private void setStoredRumour(@Nullable String rumour, String configKey) {
        if (rumour != null) {
            configManager.setRSProfileConfiguration(RumourReminderConfig.CONFIG_GROUP, configKey, rumour);
        } else {
            configManager.unsetRSProfileConfiguration(RumourReminderConfig.CONFIG_GROUP, configKey);
        }
    }

    public void saveAllStoredRumours() {
        setStoredRumour(activeRumour, CONFIG_KEY_ACTIVE_RUMOUR);
        setStoredRumour(rumourGilman, CONFIG_KEY_GILMAN_RUMOUR);
        setStoredRumour(rumourAco, CONFIG_KEY_ACO_RUMOUR);
        setStoredRumour(rumourTeco, CONFIG_KEY_TECO_RUMOUR);
        setStoredRumour(rumourCervus, CONFIG_KEY_CERVUS_RUMOUR);
        setStoredRumour(rumourOrnus, CONFIG_KEY_ORNUS_RUMOUR);
        setStoredRumour(rumourWolf, CONFIG_KEY_WOLF_RUMOUR);
        setStoredRumour(tempBgRumour, CONFIG_KEY_TEMP_BG_RUMOUR);
        setStoredRumour(tempHunter, CONFIG_KEY_TEMP_HUNTER);
        setStoredRumour(activeIsTemp.toString(), CONFIG_KEY_ACTIVE_IS_TEMP);
    }

    @Nullable
    public String getStoredRumour(String configKey) {
        try {
            String result = configManager.getRSProfileConfiguration(RumourReminderConfig.CONFIG_GROUP, configKey);
            return result;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public void loadAllRumours() {
        activeRumour = getStoredRumour(CONFIG_KEY_ACTIVE_RUMOUR);
        rumourGilman = getStoredRumour(CONFIG_KEY_GILMAN_RUMOUR);
        rumourAco = getStoredRumour(CONFIG_KEY_ACO_RUMOUR);
        rumourCervus = getStoredRumour(CONFIG_KEY_CERVUS_RUMOUR);
        rumourOrnus = getStoredRumour(CONFIG_KEY_ORNUS_RUMOUR);
        rumourTeco = getStoredRumour(CONFIG_KEY_TECO_RUMOUR);
        rumourWolf = getStoredRumour(CONFIG_KEY_WOLF_RUMOUR);
        tempBgRumour = getStoredRumour(CONFIG_KEY_TEMP_BG_RUMOUR);
        tempHunter = getStoredRumour(CONFIG_KEY_TEMP_HUNTER);
        activeIsTemp = Boolean.parseBoolean(getStoredRumour(CONFIG_KEY_ACTIVE_IS_TEMP));
        updateInfoBox();
    }

}
