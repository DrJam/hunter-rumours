package com.hunterrumours;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;

import java.util.regex.Pattern;


import com.google.inject.Singleton;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.NullNpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

public class RumoursManager {
    private static final String GILMAN = "Gilman";
    private static final String ACO = "Aco";
    private static final String CERVUS = "Cervus";
    private static final String ORNUS = "Ornus";
    private static final String TECO = "Teco";
    private static final String WOLF = "Wolf";


    private static final Pattern RUMOUR_CHECK_PATTERN = Pattern.compile("(?:Your current rumour target is a) ([a-zA-Z -]+)(?:\\. You'll)([a-zA-Z ]+)");
    private static final Pattern RUMOUR_ASSIGN_PATTERN = Pattern.compile("(tropicalwagtail|wildkebbit|sapphireglacialis|swamplizard|spinedlarupia|barb-tailedkebbit|snowyknight|pricklykebbit|embertailedjerboa|hornedgraahk|spottedkebbit|blackwarlock|orangesalamander|razor-backedkebbit|sabre-toothedkebbit|greychinchompa|sabre-toothedkyatt|darkkebbit|pyrefox|redsalamander|redchinchompa|sunlightmoth|dashingkebbit|sunlightantelope|moonlightmoth|tecusalamander|herbiboar|moonlightantelope)",Pattern.CASE_INSENSITIVE);

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

    @Inject HunterRumoursConfig config;

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

    public void setActiveRumour(@Nullable String rumour)
    {
        this.activeRumour=rumour;
    }

    public void setRumour(@Nullable String rumour, String npcName)
    {
        boolean clearOthers = false;

        if (npcName.contains(GILMAN))
        {
            String oldRumour = rumourGilman;
            this.rumourGilman=rumour;

            if (rumour == null || (!rumour.equals(oldRumour) && oldRumour != null))
            {
                clearOthers = true;
            }
        }
        if (npcName.contains(CERVUS) || clearOthers)
        {
            this.rumourCervus=rumour;

            if (rumour == null)
            {
                clearOthers = true;
            }
        }
        if (npcName.contains(ORNUS) || clearOthers)
        {
            this.rumourOrnus=rumour;

            if (rumour == null)
            {
                this.rumourCervus=null;
                clearOthers = true;
            }
        }
        if (npcName.contains(ACO) || clearOthers)
        {
            this.rumourAco=rumour;

            if (rumour == null)
            {
                clearOthers = true;
            }
        }
        if (npcName.contains(TECO) || clearOthers)
        {
            this.rumourTeco=rumour;

            if (rumour == null)
            {
                this.rumourAco=null;
                clearOthers = true;
            }
        }
        if (npcName.contains(WOLF) || clearOthers)
        {
            this.rumourWolf=rumour;
        }
    }

    public void updateData(boolean displayInfoBox)
    {
        handleHunterMasterWidgetDialog();
        if (displayInfoBox)
        {
            handleInfoBox();
        }
    }

    public void removeInfoBox()
    {
        if (infoBox != null)
        {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }
    }

    private void handleInfoBox()
    {
        //if (activeRumour != (infoBox == null ? null : infoBox.getActiveRumour()))
        //{
        removeInfoBox();

        if (activeRumour != null)
        {
            HunterCreature rumourCreature = HunterCreature.getHunterCreatureFromCreatureName(activeRumour.toLowerCase());
            if (rumourCreature != null)
            {
                infoBox = new RumourInfoBox(itemManager.getImage(rumourCreature.creatureID), plugin, activeRumour, rumourGilman, rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, config);
                infoBoxManager.addInfoBox(infoBox);
            }
        }
        else
        {
            infoBox = new RumourInfoBox(itemManager.getImage(ItemID.BANK_FILLER), plugin, activeRumour, rumourGilman, rumourAco, rumourCervus, rumourOrnus, rumourTeco, rumourWolf, config);
            infoBoxManager.addInfoBox(infoBox);
        }
       // }
    }

    private void handleHunterMasterWidgetDialog()
    {
        Widget npcDialog = client.getWidget(ComponentID.DIALOG_NPC_HEAD_MODEL);

        if (npcDialog == null)
        {
            return;
        }

        String dialogNPCName = Text.removeTags((client.getWidget(ComponentID.DIALOG_NPC_NAME).getText()));

        if (npcDialog == null || !(dialogNPCName.contains(GILMAN) || dialogNPCName.contains(ACO) ||
                dialogNPCName.contains(CERVUS) || dialogNPCName.contains(ORNUS) || dialogNPCName.contains(TECO)
                || dialogNPCName.contains(WOLF)))
        {
            return;
        }

        String dialogText = Text.removeTags(client.getWidget(ComponentID.DIALOG_NPC_TEXT).getText());
        dialogText = dialogText.replaceAll("\\s","");
        if (dialogText.equals(RUMOUR_REWARDED_1) || dialogText.equals(RUMOUR_REWARDED_2))
        {
            setRumour(null,dialogNPCName);
            setActiveRumour(null);
        }

        if (dialogText.contains(GILMAN) || dialogText.contains(ACO) ||
                dialogText.contains(CERVUS) || dialogText.contains(ORNUS) || dialogText.contains(TECO)
                || dialogText.contains(WOLF))
        {
            return;
        }

        Matcher matcher = RUMOUR_ASSIGN_PATTERN.matcher(dialogText);

        if (!matcher.find())
        {
            return;
        }

        String creature = matcher.group(1);
        //String rareParts = matcher.group(2);

        setRumour(creature, dialogNPCName);
        setActiveRumour(creature);
    }

    public boolean isCheckHunterTask(String message)
    {
        Matcher matcher = RUMOUR_CHECK_PATTERN.matcher(message);

        if (!matcher.find())
        {
            return false;
        }

        setActiveRumour(matcher.group(1));
        return true;
    }

    @Nullable
    public String getStoredRumour(String configKey)
    {
        try
        {
            return configManager.getRSProfileConfiguration(config.CONFIG_GROUP, configKey);
        }
        catch (NumberFormatException ignored)
        {
            return null;
        }
    }

    public void setStoredRumours()
    {
        setStoredRumour(activeRumour, CONFIG_KEY_ACTIVE_RUMOUR);
        setStoredRumour(rumourGilman, CONFIG_KEY_GILMAN_RUMOUR);
        setStoredRumour(rumourAco, CONFIG_KEY_ACO_RUMOUR);
        setStoredRumour(rumourTeco, CONFIG_KEY_TECO_RUMOUR);
        setStoredRumour(rumourCervus, CONFIG_KEY_CERVUS_RUMOUR);
        setStoredRumour(rumourOrnus, CONFIG_KEY_ORNUS_RUMOUR);
        setStoredRumour(rumourWolf,CONFIG_KEY_WOLF_RUMOUR);
    }

    private void setStoredRumour(@Nullable String rumour, String configKey)
    {
        if (rumour != null)
        {
            configManager.setRSProfileConfiguration(config.CONFIG_GROUP, configKey, rumour);
        }
        else
        {
            configManager.unsetRSProfileConfiguration(config.CONFIG_GROUP, configKey);
        }
    }

}
