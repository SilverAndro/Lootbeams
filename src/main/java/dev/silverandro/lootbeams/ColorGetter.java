package dev.silverandro.lootbeams;

import dev.silverandro.lootbeams.compat.TierifyColorGetter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.List;

public class ColorGetter {
    public static TextColor getColor(ItemEntity entity, LootbeamsConfig config) {
        if (FabricLoader.getInstance().isModLoaded("tiered")) {
            return TierifyColorGetter.getColor(entity, config);
        }

        TextColor color;
        if (!config.useBaseColor) {
            color = getFromStack(entity.getStack());
        } else {
            color = getFromStack(new ItemStack(entity.getStack().getItem()));
        }

        return color;
    }

    public static TextColor getFromStack(ItemStack stack) {
        List<Text> text = stack.getTooltip(null, TooltipContext.Default.BASIC);

        TextColor color = text.get(0).getStyle().getColor();

        if (color == null || color.getName().equals("white")) {
            if (!text.get(0).getSiblings().isEmpty()) {
                TextColor tmp = text.get(0).getSiblings().get(0).getStyle().getColor();
                if (tmp != null && !tmp.getName().equals("white")) {
                    color = tmp;
                }
            }
        }

        return color;
    }
}
