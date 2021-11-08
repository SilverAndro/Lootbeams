package mc.lootbeams;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;

public class LootbeamsConfig implements ConfigData {
    @Comment("If items with a white (common) item name should get a beam")
    public boolean showWhiteItems = true;

    @Comment("How many particles to create per tick")
    public int particleCount = 1;

    @Comment("How tall the loot beam is (in blocks)")
    public double beamHeight = 0.8;

    @Comment("How much to offset the beam vertically (in blocks)")
    public double beamOffset = 0.2;

    @Comment("How old the item needs to be before it gets a beam (in ticks)")
    public int minimumAge = 10;

    @Comment("How close items need to be to get a beam")
    public double beamDistance = 64.0;
    
    @Comment("If the mod should print caught errors instead of swallowing them")
    public boolean printErrors = false;
}
