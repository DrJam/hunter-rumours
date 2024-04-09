package com.drjam.rumourreminder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ColorUtil;

class RumourInfoBox extends InfoBox {
    private String rumourGilman = null;
    private String rumourAco = null;
    private String rumourCervus = null;
    private String rumourOrnus = null;
    private String rumourTeco = null;
    private String rumourWolf = null;
    private String activeRumour = null;
    private boolean isRumourCompleted = false;

    RumourInfoBox(
            BufferedImage image,
            Plugin plugin,
            String activeRumour,
            String rumourGilman,
            String rumourAco,
            String rumourCervus,
            String rumourOrnus,
            String rumourTeco,
            String rumourWolf,
            boolean isRumourCompleted) {
        super(image, plugin);
        this.activeRumour = activeRumour != null ? activeRumour.toLowerCase() : null;
        this.rumourGilman = rumourGilman != null ? rumourGilman.toLowerCase() : null;
        this.rumourAco = rumourAco != null ? rumourAco.toLowerCase() : null;
        this.rumourCervus = rumourCervus != null ? rumourCervus.toLowerCase() : null;
        this.rumourOrnus = rumourOrnus != null ? rumourOrnus.toLowerCase() : null;
        this.rumourTeco = rumourTeco != null ? rumourTeco.toLowerCase() : null;
        this.rumourWolf = rumourWolf != null ? rumourWolf.toLowerCase() : null;
        this.isRumourCompleted = isRumourCompleted;
    }

    @Override
    public String getText() {
        if (this.isRumourCompleted) {
            return "Done";
        }
        return null;
    }

    @Override
    public Color getTextColor() {
        if (this.isRumourCompleted) {
            return Color.GREEN;
        }
        return null;
    }

    @Override
    public String getTooltip() {
        StringBuilder sb = new StringBuilder();

        HunterCreature activeCreature = null;
        if (activeRumour != null) {
            activeCreature = HunterCreature.getHunterCreatureFromCreatureName(activeRumour.toLowerCase());
        }

        sb.append(ColorUtil.wrapWithColorTag("Hunter Rumours:", Color.WHITE));
        if (activeCreature != null) {
            sb.append("</br>");
            sb.append(ColorUtil.wrapWithColorTag(activeCreature.name, Color.GREEN));

            String hunterString = "";
            if (activeRumour.equals(rumourGilman)) {
                hunterString = " - Gilman (Novice)";
            } else if (activeRumour.equals(rumourAco)) {
                hunterString = " - Aco (Expert)";
            } else if (activeRumour.equals(rumourCervus)) {
                hunterString = " - Cervus (Adept)";
            } else if (activeRumour.equals(rumourOrnus)) {
                hunterString = " - Ornus (Adept)";
            } else if (activeRumour.equals(rumourTeco)) {
                hunterString = " - Teco (Expert)";
            } else if (activeRumour.equals(rumourWolf)) {
                hunterString = " - Wolf (Master)";
            } else {
                hunterString = " - Unknown";
            }

            sb.append(ColorUtil.wrapWithColorTag(hunterString, Color.GREEN));

            sb.append("</br> Tools: " + activeCreature.hunterItems);
            sb.append("</br> Locations: " + activeCreature.locations);
            sb.append("</br>");
        }
        if (rumourWolf != null && !rumourWolf.equals(activeRumour)) {
            addOtherHunter(sb, rumourWolf, " - Wolf (Master)");
        }
        if (rumourTeco != null && !rumourTeco.equals(activeRumour)) {
            addOtherHunter(sb, rumourTeco, " - Teco (Expert)");
        }
        if (rumourAco != null && !rumourAco.equals(activeRumour)) {
            addOtherHunter(sb, rumourAco, " - Aco (Expert)");
        }
        if (rumourOrnus != null && !rumourOrnus.equals(activeRumour)) {
            addOtherHunter(sb, rumourOrnus, " - Ornus (Adept)");
        }
        if (rumourCervus != null && !rumourCervus.equals(activeRumour)) {
            addOtherHunter(sb, rumourCervus, " - Cervus (Adept)");
        }
        if (rumourGilman != null && !rumourGilman.equals(activeRumour)) {
            addOtherHunter(sb, rumourGilman, " - Gilman (Novice)");
        }
        return sb.toString();
    }

    private void addOtherHunter(StringBuilder sb, String rumour, String hunterDisplayText) {
        HunterCreature creature = HunterCreature.getHunterCreatureFromCreatureName(rumour.toLowerCase());
        if (creature == null) {
            return;
        }
        String creatureName = creature.name;
        String combinedString = "</br>" + creatureName + hunterDisplayText;
        sb.append(ColorUtil.wrapWithColorTag(combinedString, Color.ORANGE));
    }
}