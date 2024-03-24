package com.hunterrumours;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;

import java.util.regex.Pattern;

import ch.qos.logback.classic.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

@Slf4j
public class RumoursManager {
    private static final String WOLF = "Wolf";
    private static final String ACO = "Aco";
    private static final String TECO = "Teco";
    private static final String CERVUS = "Cervus";
    private static final String ORNUS = "Ornus";
    private static final String GILMAN = "Gilman";
    private static final List<String> HUNTER_NAMES = Arrays.asList(WOLF, ACO, TECO, ORNUS, CERVUS, GILMAN);

    private static final Pattern WHISTLE_PATTERN = Pattern
            .compile("(?:Your current rumour target is (?:a|an)) ([a-zA-Z -]+)(?:\\. You'll)([a-zA-Z ]+)");
    private static final Pattern HUNTER_NAMES_PATTERN = Pattern.compile("(wolf|aco|teco|cervus|ornus|gilman)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern HUNTER_CREATURES_PATTERN = Pattern.compile(
            "(tropicalwagtail|wildkebbit|sapphireglacialis|swamplizard|spinedlarupia|barb-tailedkebbit|snowyknight|pricklykebbit|embertailedjerboa|hornedgraahk|spottedkebbit|blackwarlock|orangesalamander|razor-backedkebbit|sabre-toothedkebbit|greychinchompa|sabre-toothedkyatt|darkkebbit|pyrefox|redsalamander|redchinchompa|sunlightmoth|dashingkebbit|sunlightantelope|moonlightmoth|tecusalamander|herbiboar|moonlightantelope)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern RUMOUR_COMPLETION_PATTERN = Pattern.compile(
            "Another one done\\? You're really|Thanks for that\\. I'll mark off that report");
    private static final String RUMOUR_REWARDED_1 = "Anotheronedone\\?You'rereallydoingalotfortheguild\\.";
    private static final String RUMOUR_REWARDED_2 = "Thanksforthat\\.I'llmarkoffthatreportforyou\\.Wouldyoulikeanotherrumour\\?";

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
        rumourConfirmed(target, hunter);
    }

    private void resetRumours() {
        this.rumourGilman = null;
        this.rumourAco = null;
        this.rumourCervus = null;
        this.rumourOrnus = null;
        this.rumourTeco = null;
        this.rumourWolf = null;
    }

    private void rumourConfirmed(String target, String hunter) {
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

    // public void updateData() {
    // // handleHunterMasterWidgetDialog();
    // updateInfoBox();
    // }

    private void updateInfoBox() {
        if (infoBox != null) {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }

        if (!config.showRumourInfoBox()) {
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
            infoBox = new RumourInfoBox(itemManager.getImage(ItemID.BANK_FILLER), plugin, activeRumour, rumourGilman,
                    rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, config);

        }
        infoBoxManager.addInfoBox(infoBox);
    }

    public void updateFromWhistle(ChatMessage message) {

        Matcher matcher = WHISTLE_PATTERN.matcher(message.getMessage());

        if (!matcher.find()) {
            return;
        }
        this.activeRumour = matcher.group(1);
        updateInfoBox();
    }

    @SuppressWarnings("unused")
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
                creature = creatureType.getLookupName();
                break;
            }
        }

        if (hunterTalking == null && hunterReferenced == null && creature == null) {
            return;
        }

        if (hunterTalking != null && contents.matches(RUMOUR_COMPLETION_PATTERN.pattern())) {
            log.info("rumour completed for " + hunterTalking);
            rumourCompleted(hunterTalking);
        } else if (hunterTalking != null && hunterReferenced == null && creature != null) {
            if (hunterTalking == GILMAN && contents.startsWith("I seem to remember")) {
                log.info("gilman remembering " + creature);
                rumourConfirmed(creature, hunterTalking);
            } else {
                log.info("rumour assigned: " + creature + " " + hunterTalking);
                if (hunterTalking == GILMAN && creature != rumourGilman && rumourGilman != null) {
                    log.info("gilman rumour assigned, cleared others: " + creature);
                    resetRumours();
                }
                rumourAssigned(creature, hunterTalking);
            }
        } else if (hunterTalking != null && hunterReferenced != null && creature != null) {
            log.info("rumour confirmed: " + creature + " " + hunterReferenced);
            rumourConfirmed(creature, hunterReferenced);
        }
        saveAllStoredRumours();
        updateInfoBox();
    }

    // private void handleHunterMasterWidgetDialog() {
    // Widget npcDialog = client.getWidget(ComponentID.DIALOG_NPC_HEAD_MODEL);

    // if (npcDialog == null) {
    // return;
    // }

    // String dialogNPCName =
    // Text.removeTags((client.getWidget(ComponentID.DIALOG_NPC_NAME).getText()));

    // if (npcDialog == null || !(dialogNPCName.contains(GILMAN) ||
    // dialogNPCName.contains(ACO) ||
    // dialogNPCName.contains(CERVUS) || dialogNPCName.contains(ORNUS) ||
    // dialogNPCName.contains(TECO)
    // || dialogNPCName.contains(WOLF))) {
    // return;
    // }

    // String dialogText =
    // Text.removeTags(client.getWidget(ComponentID.DIALOG_NPC_TEXT).getText());
    // dialogText = dialogText.replaceAll("\\s", "");
    // if (dialogText.equals(RUMOUR_REWARDED_1) ||
    // dialogText.equals(RUMOUR_REWARDED_2)) {
    // setRumour(null, dialogNPCName);
    // setActiveRumour(null);
    // }

    // if (dialogText.contains(GILMAN) || dialogText.contains(ACO) ||
    // dialogText.contains(CERVUS) || dialogText.contains(ORNUS) ||
    // dialogText.contains(TECO)
    // || dialogText.contains(WOLF)) {
    // return;
    // }

    // Matcher matcher = HUNTER_CREATURES.matcher(dialogText);

    // if (!matcher.find()) {
    // return;
    // }

    // String creature = matcher.group(1);

    // setRumour(creature, dialogNPCName);
    // setActiveRumour(creature);
    // }

    // public boolean isCheckHunterTask(String message) {
    // Matcher matcher = WHISTLE_CHECK.matcher(message);

    // if (!matcher.find()) {
    // return false;
    // }

    // setActiveRumour(matcher.group(1));
    // return true;
    // }

    private void setStoredRumour(@Nullable String rumour, String configKey) {
        if (rumour != null) {
            log.info("Setting rumour in config: " + configKey + " " + rumour);
            configManager.setRSProfileConfiguration(HunterRumoursConfig.CONFIG_GROUP, configKey, rumour);
        } else {
            log.info("Unsetting rumour in config: " + configKey);
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
            log.info("Loaded rumour from config: " + configKey + " " + result);
            return result;
        } catch (NumberFormatException ignored) {
            log.warn("Failed to load rumour from config:" + configKey);
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
