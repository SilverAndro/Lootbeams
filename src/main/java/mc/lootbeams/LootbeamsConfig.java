package mc.lootbeams;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;

public class LootbeamsConfig implements ConfigData {
    @Comment("If items with a white (common) item name should get a beam")
    public boolean showWhiteItems = false;
    @Comment("How many particles to create per tick")
    public int particleCount = 1;
    @Comment("How tall the loot beam is (in blocks)")
    public float beamHeight = 0.8f;
    @Comment("How much to offset the beam vertically (in blocks)")
    public float beamOffset = 0.2f;
    @Comment("How old the item needs to be before it gets a beam (in ticks)")
    public int minimumAge = 10;
    
    @Comment("If the mod should print caught errors instead of swallowing them")
    public boolean printErrors = false;
}
