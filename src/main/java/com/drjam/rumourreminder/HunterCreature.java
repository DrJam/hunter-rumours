package com.drjam.rumourreminder;

import net.runelite.api.ItemID;
import java.util.*;

public enum HunterCreature {

    TROPICAL_WAGTAIL("Tropical wagtail",
            "Bird snare",
            "Feldip Hunter Area (AKS)",
            0, ItemID.TAILFEATHERS),
    WILD_KEBBIT("Wild kebbit",
            "Deadfall (knife, logs/axe)",
            "Piscatoris Hunter Area (AKQ)",
            0, ItemID.KEBBITY_TUFT),
    SAPPHIRE_GLACIALIS("Sapphire glacialis",
            "Butterfly net",
            "Rellekka Hunter Area (DKS)",
            0, ItemID.BLUE_BUTTERFLY_WING),
    SWAMP_LIZARD("Swamp lizard",
            "Net trap (small net, rope)",
            "Canifis Hunter Area (ALQ), Slepe",
            0, ItemID.SWAMP_LIZARD_CLAW),
    SPINED_LARUPIA("Spined larupia",
            "Spiked pit (teasing stick, knife, logs/axe)",
            "Feldip Hunter Area (AKS)",
            5, ItemID.LARUPIA_EAR),
    BARB_TAILED_KEBBIT("Barb-tailed kebbit",
            "Deadfall (knife, logs/axe)",
            "Feldip Hunter Area (AKS)",
            0, ItemID.KEBBITY_TUFT),
    SNOWY_KNIGHT("Snowy knight",
            "Butterfly net",
            "Weiss, Rellekka Hunter Area (DKS)",
            0, ItemID.WHITE_BUTTERFLY_WING),
    PRICKLY_KEBBIT("Prickly kebbit",
            "Deadfall (knife, logs/axe)",
            "Piscatoris Hunter Area (AKQ)",
            0, ItemID.KEBBITY_TUFT),
    EMBERTAILED_JERBOA("Embertailed jerboa",
            "Box Trap",
            "Hunter Guild, Locus Oasis (AJP)",
            0, ItemID.LARGE_JERBOA_TAIL),
    HORNED_GRAAHK("Horned graahk",
            "Spiked pit (teasing stick, knife, logs/axe)",
            "Karamja Hunter Area (CKR)",
            7, ItemID.GRAAHK_HORN_SPUR),
    SPOTTED_KEBBIT("Spotted kebbit",
            "Falconry",
            "Piscatoris Falconry Area (AKQ)",
            0, ItemID.KEBBITY_TUFT),
    BLACK_WARLOCK("Black warlock",
            "Butterfly net",
            "Feldip Hunter Area (AKS)",
            0, ItemID.BLACK_BUTTERFLY_WING),
    ORANGE_SALAMANDER("Orange salamander",
            "Net trap (small net, rope)",
            "Uzer Hunter Area, Necropolis Hunter Area (AKP)",
            0, ItemID.ORANGE_SALAMANDER_CLAW),
    RAZOR_BACKED_KEBBIT("Razor-backed kebbit",
            "Noose wand",
            "Piscatoris Hunter Area (AKQ)",
            0, ItemID.KEBBITY_TUFT),
    SABRE_TOOTHED_KEBBIT("Sabre-toothed kebbit",
            "Deadfall (knife, logs/axe)",
            "Rellekka Hunter Area (DKS)",
            0, ItemID.KEBBITY_TUFT),
    CARNIVOROUS_CHINCHOMPA("Red chinchompa",
            "Box trap",
            "Feldip Hunter Area (AKS), Gwenith Hunter Area",
            1, ItemID.RED_CHINCHOMPA_TUFT),
    CHINCHOMPA("Chinchompa",
            "Box trap",
            "Piscatoris Hunter Area (AKQ), Kourend Woodland, Isle of Souls (BJP)",
            1, ItemID.CHINCHOMPA_TUFT),
    SABRE_TOOTHED_KYATT("Sabre-toothed kyatt",
            "Spiked pit (teasing stick, knife, logs/axe)",
            "Rellekka Hunter Area (DKS)",
            7, ItemID.KYATT_TOOTH_CHIP),
    DARK_KEBBIT("Dark kebbit",
            "Falconry",
            "Piscatoris Falconry Area (AKQ)",
            0, ItemID.KEBBITY_TUFT),
    PYRE_FOX("Pyre fox",
            "Deadfall (knife, logs/axe)",
            "Avium Savannah (AJP)",
            0, ItemID.FOX_FLUFF),
    RED_SALAMANDER("Red salamander",
            "Net trap (small net, rope)",
            "Ourania Hunter Area",
            0, ItemID.RED_SALAMANDER_CLAW),
    SUNLIGHT_MOTH("Sunlight moth",
            "Butterfly net",
            "Avium Savannah (AJP), Neypotzli",
            0, ItemID.SUNLIGHT_MOTH_WING),
    DASHING_KEBBIT("Dashing kebbit",
            "Falconry",
            "Piscatoris Falconry Area (AKQ)",
            0, ItemID.KEBBITY_TUFT),
    SUNLIGHT_ANTELOPE("Sunlight antelope",
            "Spiked pit (teasing stick, knife, logs/axe)",
            "Avium Savannah (AJP)",
            5, ItemID.ANTELOPE_HOOF_SHARD),
    MOONLIGHT_MOTH("Moonlight moth",
            "Butterfly net",
            "Neypotzli, Hunter Guild",
            0, ItemID.MOONLIGHT_MOTH_WING),
    TECU_SALAMANDER("Tecu salamander",
            "Net trap (small net, rope)",
            "Cam Torum Entrance",
            0, ItemID.SALAMANDER_CLAW),
    HERBIBOAR("Herbiboar",
            "Tracking",
            "Fossil Island",
            0, ItemID.HERBY_TUFT),
    MOONLIGHT_ANTELOPE("Moonlight antelope",
            "Spiked pit (teasing stick, knife, logs/axe)",
            "Hunter Guild",
            0, ItemID.ANTELOPE_HOOF_SHARD_29241),;

    private static final Map<String, HunterCreature> creatureNameToHunterCreature = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(hunterCreature -> {
            creatureNameToHunterCreature.put(hunterCreature.name.toLowerCase(), hunterCreature);
        });
    }

    public static HunterCreature getHunterCreatureFromCreatureName(String creatureName) {
        return creatureNameToHunterCreature.get(creatureName.toLowerCase());
    }

    String name;
    String hunterItems;
    String locations;
    Integer maxHit;
    Integer targetItemID;

    HunterCreature(String name, String hunterItems, String locations, Integer maxHit, Integer targetItemID) {
        this.name = name;
        this.hunterItems = hunterItems;
        this.locations = locations;
        this.maxHit = maxHit;
        this.targetItemID = targetItemID;
    }
}
