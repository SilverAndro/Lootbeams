package dev.silverandro.lootbeams;

import dev.silverandro.microconfig.Comment;
import dev.silverandro.microconfig.ConfigData;

public class LootbeamsConfig implements ConfigData {
    @Comment("If items with a white (common) item name should get a beam")
    public boolean showWhiteItems = false;

    @Comment("How many particles to create per tick")
    public int particleCount = 1;

    @Comment("How tall the loot beam is (in blocks)")
    public double beamHeight = 0.8;

    @Comment("How much to offset the beam vertically (in blocks)")
    public double beamOffset = 0.2;

    @Comment("How old the item needs to be before it gets a beam (in ticks)")
    public int minimumAge = 12;

    @Comment("How close items need to be to get a beam")
    public double beamDistance = 64.0;

    @Comment("Adds extra particles for enchanted items")
    public boolean enchantedParticles = true;

    @Comment("Uses the color of the base item instead of the tooltip")
    public boolean useBaseColor = false;

    @Comment("If the mod should print caught errors instead of swallowing them")
    public boolean printErrors = false;
}
