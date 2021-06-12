package mc.lootbeams;

import mc.lootbeams.mixin.ItemEntityAgeAccessor;
import mc.microconfig.MicroConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LootbeamsClient implements ClientModInitializer {
    private final LootbeamsConfig config = MicroConfig.getOrCreate("lootbeams", new LootbeamsConfig());
    
    private final HashMap<ItemEntity, Integer> itemsToColors = new HashMap<>();
    
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            assert MinecraftClient.getInstance().player != null;
            List<ItemEntity> items = world.getEntitiesByType(
                EntityType.ITEM,
                new Box(MinecraftClient.getInstance().player.getBlockPos()).expand(64.0),
                entity -> !itemsToColors.containsKey(entity)
            );
            
            items.forEach(entity -> {
                try {
                    if (entity != null) {
                        List<Text> text = entity.getStack().getTooltip(null, TooltipContext.Default.NORMAL);
                        
                        TextColor color = text.get(0).getStyle().getColor();
                        
                        if (color == null || color.getName().equals("white")) {
                            if (text.get(0).getSiblings().size() >= 1) {
                                TextColor tmp = text.get(0).getSiblings().get(0).getStyle().getColor();
                                if (tmp != null && !tmp.getName().equals("white")) {
                                    color = tmp;
                                }
                            }
                        }
                        
                        if (color != null) {
                            itemsToColors.put(entity, color.getRgb());
                        }
                    }
                } catch (Throwable possible) {
                    if (config.printErrors) {
                        possible.printStackTrace();
                    }
                }
            });
            
            ArrayList<ItemEntity> toRemove = new ArrayList<>();
            itemsToColors.forEach((entity, color) -> {
                if (entity.removed || !entity.isAlive()) {
                    toRemove.add(entity);
                    return;
                }
                
                if (!config.showWhiteItems && color == 16777215) {
                    return;
                }
                
                if (((ItemEntityAgeAccessor)entity).getAge() >= config.minimumAge) {
                    if (MinecraftClient.getInstance().player != null) {
                        for (int i = 0; i < config.particleCount; i++) {
                            world.addParticle(
                                new DustParticleEffect(
                                    ((color >> 16) & 0xFF) / 255f,
                                    ((color >> 8) & 0xFF) / 255f,
                                    (color & 0xFF) / 255f,
                                    1.0f),
                                true,
                                entity.getX(),
                                entity.getY() + config.beamOffset + (world.random.nextDouble() * config.beamHeight),
                                entity.getZ(),
                                0.0,
                                0.0,
                                0.0
                            );
                        }
                    }
                }
            });
            
            toRemove.forEach(itemsToColors::remove);
        });
    }
}
