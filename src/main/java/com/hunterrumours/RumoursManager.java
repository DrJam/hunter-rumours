package com.hunterrumours;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;

import java.util.regex.Pattern;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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

    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private HunterRumoursPlugin plugin;

    @Inject
    HunterRumoursConfig config;

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

    @Getter
    @Setter
    private RumourInfoBox infoBox;

    public void setAllRumours(@Nullable String rumourGilman, @Nullable String rumourAco, @Nullable String rumourCervus,
            @Nullable String rumourOrnus, @Nullable String rumourTeco, @Nullable String rumourWolf) {
        this.rumourGilman = rumourGilman;
        this.rumourAco = rumourAco;
        this.rumourCervus = rumourCervus;
        this.rumourOrnus = rumourOrnus;
        this.rumourTeco = rumourTeco;
        this.rumourWolf = rumourWolf;
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

    private void resetRumours() {
        this.rumourGilman = null;
        this.rumourAco = null;
        this.rumourCervus = null;
        this.rumourOrnus = null;
        this.rumourTeco = null;
        this.rumourWolf = null;
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
            infoBox = new RumourInfoBox(itemManager.getImage(rumourCreature.creatureID), plugin, activeRumour,
                    rumourGilman, rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, config);

        } else {
            infoBox = new RumourInfoBox(itemManager.getImage(ItemID.GUILD_HUNTER_HEADWEAR), plugin, activeRumour,
                    rumourGilman,
                    rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, config);

        }
        infoBoxManager.addInfoBox(infoBox);
    }

    public void updateFromSavedInfo() {
        loadAllRumours();
        updateInfoBox();
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

        var isRumourCompletion = hunterTalking != null && contents.matches(RUMOUR_COMPLETION_PATTERN.pattern());
        var isAssignmantOrGilman = hunterTalking != null && hunterReferenced == null && creature != null;
        var isGilmanRemembering = hunterTalking == GILMAN && contents.startsWith("I seem to remember");
        var isGilmanAssignment = hunterTalking == GILMAN && creature != rumourGilman && rumourGilman != null;
        var isRumourConfirmation = hunterTalking != null && hunterReferenced != null && creature != null;

        log.info("isRumourCompletion: " + isRumourCompletion);
        log.info("isAssignmantOrGilman: " + isAssignmantOrGilman);
        log.info("isGilmanRemembering: " + isGilmanRemembering);
        log.info("isGilmanAssignment: " + isGilmanAssignment);
        log.info("isRumourConfirmation: " + isRumourConfirmation);

        if (isRumourCompletion) {
            rumourCompleted(hunterTalking);
        } else if (isAssignmantOrGilman) {
            if (isGilmanRemembering) {
                rumourConfirmed(creature, hunterTalking, false);
            } else {// is Assignment
                if (isGilmanAssignment) {
                    resetRumours();
                }
                rumourAssigned(creature, hunterTalking);
            }
        } else if (isRumourConfirmation) {
            rumourConfirmed(creature, hunterReferenced, true);
        }

        saveAllStoredRumours();
        updateInfoBox();
    }

    private void setStoredRumour(@Nullable String rumour, String configKey) {
        if (rumour != null) {
            configManager.setRSProfileConfiguration(HunterRumoursConfig.CONFIG_GROUP, configKey, rumour);
        } else {
            configManager.unsetRSProfileConfiguration(HunterRumoursConfig.CONFIG_GROUP, configKey);
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
    }

    @Nullable
    public String getStoredRumour(String configKey) {
        try {
            String result = configManager.getRSProfileConfiguration(HunterRumoursConfig.CONFIG_GROUP, configKey);
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
        updateInfoBox();
    }

}
