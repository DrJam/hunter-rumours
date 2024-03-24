package com.hunterrumours;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Instant;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ColorUtil;

class RumourInfoBox extends InfoBox
{
    private String rumourGilman = null;
    private String rumourAco = null;
    private String rumourCervus = null;
    private String rumourOrnus = null;
    private String rumourTeco = null;
    private String rumourWolf = null;
    private String activeRumour = null;

    private HunterRumoursConfig config;

    RumourInfoBox(BufferedImage image, Plugin plugin, String activeRumour, String rumourGilman, String rumourAco,
                  String rumourCervus, String rumourOrnus, String rumourTeco, String rumourWolf, HunterRumoursConfig config)
    {
        super(image, plugin);
        this.activeRumour = activeRumour;
        this.rumourGilman = rumourGilman;
        this.rumourAco = rumourAco;
        this.rumourCervus = rumourCervus;
        this.rumourOrnus = rumourOrnus;
        this.rumourTeco = rumourTeco;
        this.rumourWolf = rumourWolf;
        this.config=config;

    }

    public String getActiveRumour()
    {
        return activeRumour;
    }

    @Override
    public String getText()
    {
        return null;
    }

    @Override
    public Color getTextColor()
    {
        return null;
    }

    @Override
    public String getTooltip()
    {
        StringBuilder sb = new StringBuilder();

        HunterCreature hunterCreature = null;

        if (activeRumour != null)
        {
            hunterCreature = HunterCreature.getHunterCreatureFromCreatureName(activeRumour.toLowerCase());
        }

        if (hunterCreature != null)
        {
            sb.append(ColorUtil.wrapWithColorTag("Hunter Rumours:", Color.WHITE));
            sb.append("</br>");
            sb.append(ColorUtil.wrapWithColorTag(hunterCreature.name, Color.GREEN));

            String hunterString = "";

            if (activeRumour.equals(rumourGilman))
            {
                hunterString = " (Gilman - Novice)";
            }
            else if (activeRumour.equals(rumourAco))
            {
                hunterString = " (Aco - Expert)";
            }
            else if (activeRumour.equals(rumourCervus))
            {
                hunterString = " (Cervus - Adept)";
            }
            else if (activeRumour.equals(rumourOrnus))
            {
                hunterString = " (Ornus - Adept)";
            }
            else if (activeRumour.equals(rumourTeco))
            {
                hunterString = " (Teco - Expert)";
            }
            else if (activeRumour.equals(rumourWolf))
            {
                hunterString = " (Wolf - Master)";
            }
            else
            {
                hunterString = " (Unknown)";
            }

            sb.append(ColorUtil.wrapWithColorTag(hunterString, Color.GREEN));

            sb.append("</br> Tools: " + hunterCreature.hunterItems);
            sb.append("</br> Locations: " + hunterCreature.locations + "</br>");

            if (rumourWolf != null && !rumourWolf.equals(activeRumour))
            {
                addOtherHunter(sb, rumourWolf," (Wolf - Master)");
            }
            if (rumourTeco != null && !rumourTeco.equals(activeRumour))
            {
                addOtherHunter(sb, rumourTeco," (Teco - Expert)");
            }
            if (rumourAco != null && !rumourAco.equals(activeRumour))
            {
                addOtherHunter(sb, rumourAco," (Aco - Expert)");
            }
            if (rumourOrnus != null && !rumourOrnus.equals(activeRumour))
            {
                addOtherHunter(sb, rumourOrnus," (Ornus - Adept)");
            }
            if (rumourCervus != null && !rumourCervus.equals(activeRumour))
            {
                addOtherHunter(sb, rumourCervus," (Cervus - Adept)");
            }
            if (rumourGilman != null && !rumourGilman.equals(activeRumour))
            {
                addOtherHunter(sb, rumourGilman," (Gilman - Novice)");
            }
        }
        return sb.toString();
    }

    private void addOtherHunter(StringBuilder sb, String rumour, String hunterDisplayText)
    {
        String creatureName = HunterCreature.getHunterCreatureFromCreatureName(rumour).name;
        sb.append(ColorUtil.wrapWithColorTag("</br>" + creatureName + hunterDisplayText, Color.ORANGE));
    }
}