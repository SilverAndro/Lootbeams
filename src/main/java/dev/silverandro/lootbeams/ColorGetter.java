package dev.silverandro.lootbeams;

import dev.silverandro.lootbeams.compat.TierifyColorGetter;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;

public class ColorGetter {
    public static TextColor getColor(ItemEntity entity, LootbeamsConfig config) {
        if (FabricLoader.getInstance().isModLoaded("tiered")) {
            return TierifyColorGetter.getColor(entity, config);
        }

        TextColor color;
        if (!config.useBaseColor) {
            color = getFromStack(entity.getStack(), config);
        } else {
            color = getFromStack(new ItemStack(entity.getStack().getItem()), config);
        }

        return color;
    }

    public static TextColor getFromStack(ItemStack stack, LootbeamsConfig config) {
        List<Text> text = stack.getTooltip(Item.TooltipContext.create(MinecraftClient.getInstance().world), MinecraftClient.getInstance().player, TooltipType.BASIC);

        TextColor color = getColorRepeatedly(text.get(0), config);

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

    private static TextColor getColorRepeatedly(Text baseText, LootbeamsConfig config) {
        ObjectArrayFIFOQueue<Text> queue = new ObjectArrayFIFOQueue<>();
        queue.enqueue(baseText);

        while (!queue.isEmpty()) {
            Text current = queue.dequeue();
            if (current.getString().isBlank() && config.workarounds.skipEmptyTextUnits) {
                for (Text text : current.getSiblings()) {
                    queue.enqueue(text);
                }
            } else if (wouldBeWhite(current.getStyle().getColor()) && config.workarounds.skipWhiteAndUncoloredTextUnits) {
                for (Text text : current.getSiblings()) {
                    queue.enqueue(text);
                }
            } else {
                return current.getStyle().getColor();
            }
        }

        return null;
    }

    private static boolean wouldBeWhite(TextColor color) {
        return color == null || color.getName().equals("white");
    }
}
