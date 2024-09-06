package dev.silverandro.lootbeams.compat;

import dev.silverandro.lootbeams.ColorGetter;
import dev.silverandro.lootbeams.LootbeamsConfig;
import elocindev.tierify.compat.ItemBordersCompat;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

public class TierifyColorGetter {
    public static TextColor getColor(ItemEntity entity, LootbeamsConfig config) {
        NbtCompound compound = entity.getStack().getOrCreateNbt().getCompound("Tiered");
        if (compound != null && compound.getSize() > 0) {
            String tier = compound.getString("Tier");
            if (tier != null && !tier.isBlank() && !(tier.contains(":common") && !config.showWhiteItems)) {
                return TextColor.fromRgb(Integer.parseInt(ItemBordersCompat.getColorForIdentifier(new Identifier(tier)).substring(2), 16));
            } else {
                return ColorGetter.getFromStack(entity.getStack());
            }
        } else {
            return ColorGetter.getFromStack(entity.getStack());
        }
    }
}
