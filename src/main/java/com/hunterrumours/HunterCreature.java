package com.hunterrumours;

import net.runelite.api.ItemID;
import java.util.*;

public enum HunterCreature {

    TROPICAL_WAGTAIL("Tropical wagtail","tropicalwagtail", "Bird snare", "Feldip Hunter Area", 0, ItemID.TAILFEATHERS),
    WILD_KEBBIT("Wild kebbit", "wildkebbit","Deadfall", "Piscatoris Hunter Area", 0, ItemID.KEBBITY_TUFT),
    SAPPHIRE_GLACIALIS("Sapphire glacialis", "sapphireglacialis","Butterfly net", "Rellekka Hunter Area, Farming Guild", 0, ItemID.BLUE_BUTTERFLY_WING),
    SWAMP_LIZARD("Swamp lizard", "swamplizard","Net trap", "Canifis Hunter Area, Slepe", 0, ItemID.SWAMP_LIZARD_CLAW),
    SPINED_LARUPIA("Spined larupia", "spinedlarupia", "Spiked pit", "Feldip Hunter Area", 5, ItemID.LARUPIA_EAR),
    BARB_TAILED_KEBBIT("Barb-tailed kebbit", "barb-tailedkebbit","Deadfall", "Feldip Hunter Area", 0, ItemID.KEBBITY_TUFT),
    SNOWY_KNIGHT("Snowy knight", "snowyknight", "Butterfly net", "Weiss, Rellekka Hunter Area (Upper Level), Rellekka Hunter Area, Farming Guild", 0, ItemID.WHITE_BUTTERFLY_WING),
    PRICKLY_KEBBIT("Prickly kebbit","pricklykebbit", "Deadfall", "Piscatoris Hunter Area",0, ItemID.KEBBITY_TUFT),
    EMBERTAILED_JERBOA("Embertailed jerboa","embertailedjerboa", "Box Trap", "Hunter Guild, Locus Oasis", 0,ItemID.LARGE_JERBOA_TAIL),
    HORNED_GRAAHK("Horned graahk","hornedgraahk", "Spiked pit", "Karamja Hunter Area",7, ItemID.GRAAHK_HEADDRESS),
    SPOTTED_KEBBIT("Spotted kebbit", "spottedkebbit", "Falconry", "Pisctoris Falconry Area", 0, ItemID.KEBBITY_TUFT),
    BLACK_WARLOCK("Black warlock","blackwarlock", "Butterfly net", "Feldip Hunter Area, Farming Guild",0, ItemID.BLACK_BUTTERFLY_WING),
    ORANGE_SALAMANDER("Orange salamander","orangesalamander", "Net trap", "Uzer Hunter Area, Necropolis Hunter Area", 0, ItemID.ORANGE_SALAMANDER_CLAW),
    RAZOR_BACKED_KEBBIT("Razor-backed kebbit","razor-backedkebbit", "Noose wand","Piscatoris Hunter Area",0, ItemID.KEBBITY_TUFT),
    SABRE_TOOTHED_KEBBIT("Sabre-toothed kebbit","sabre-toothedkebbit", "Deadfall", "Rellekka Hunter Area", 0, ItemID.KEBBITY_TUFT),
    CHINCHOMPA("Chinchompa", "greychinchompa", "Box trap", "Piscatoris Hunter Area, Kourend Woodland, Isle of Souls",1, ItemID.CHINCHOMPA_TUFT),
    SABRE_TOOTHED_KYATT("Sabre-toothed kyatt","sabre-toothedkyatt", "Spiked pit", "Rellekka Hunter Area",7, ItemID.KYATT_HAT),
    DARK_KEBBIT("Dark kebbit","darkkebbit", "Falconry", "Piscatoris Falconry Area", 0, ItemID.KEBBITY_TUFT),
    PYRE_FOX("Pyre fox","pyrefox", "Deadfall", "Avium Savannah",0,ItemID.FOX_FLUFF),
    RED_SALAMANDER("Red salamander", "redsalamander", "Net trap", "Ourania Hunter Area", 0, ItemID.RED_SALAMANDER),
    CARNIVOROUS_CHINCHOMPA("Red chinchompa","redchinchompa", "Box trap", "Red Chinchompa Hunting Ground, Gwenith Hunter Area, Feldip Hunter Area (a-k-s)",1, ItemID.RED_CHINCHOMPA_TUFT),
    SUNLIGHT_MOTH("Sunlight moth","sunlightmoth", "Butterfly net", "Avium Savannah, Neypotzli",0,ItemID.SUNLIGHT_MOTH_WING),
    DASHING_KEBBIT("Dashing kebbit","dashingkebbit", "Falconry", "Piscatoris Falconry Area",0, ItemID.KEBBITY_TUFT),
    SUNLIGHT_ANTELOPE("Sunlight antelope","sunlightantelope", "Spiked pit","Avium Savannah",5,ItemID.ANTELOPE_HOOF_SHARD),
    MOONLIGHT_MOTH("Moonlight moth","moonlightmoth", "Butterfly net", "Neypotzli, Hunter Guild",0,ItemID.MOONLIGHT_MOTH_WING),
    TECU_SALAMANDER("Tecu salamander", "tecusalamander", "Net trap", "Cam Torum Entrance",0,ItemID.SALAMANDER_CLAW),
    HERBIBOAR("Herbiboar", "herbiboar","Tracking","Fossil Island", 0, ItemID.HERBY_TUFT),
    MOONLIGHT_ANTELOPE("Moonlight antelope", "moonlightantelope","Spiked pit","Hunters' Guild",0, ItemID.ANTELOPE_HOOF_SHARD),;

    private static final Map<String, HunterCreature> creatureNameToHunterCreature = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(hunterCreature -> {
            creatureNameToHunterCreature.put(hunterCreature.getLookupName(), hunterCreature);
        });
    }

    public static HunterCreature getHunterCreatureFromCreatureName(String creatureName) {
        return creatureNameToHunterCreature.get(creatureName);
    }

    String name;
    String lookupName;
    String hunterItems;
    String locations;
    Integer maxHit;
    Integer creatureID;

    HunterCreature(String name, String lookupName, String hunterItems, String locations, Integer maxHit, Integer creatureID) {
        this.name=name;
        this.lookupName=lookupName;
        this.hunterItems=hunterItems;
        this.locations=locations;
        this.maxHit=maxHit;
        this.creatureID=creatureID;
    }

    public String getLookupName() {
        return lookupName;
    }
}


