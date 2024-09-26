package dev.silverandro.lootbeams;

import dev.silverandro.lootbeams.mixin.ItemEntityAgeAccessor;
import dev.silverandro.microconfig.MicroConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Box;
import org.apache.commons.logging.impl.WeakHashtable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class LootbeamsClient implements ClientModInitializer {
    private final LootbeamsConfig config = MicroConfig.getOrCreate("lootbeams", new LootbeamsConfig());
    private final WeakHashMap<ItemEntity, Integer> itemsToColors = new WeakHashMap<>();

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("tiered")) {
            System.err.println("Lootbeams tierify/tiered support not implemented for this version! Please update this mod or open an issue!");
        }

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            assert MinecraftClient.getInstance().player != null;
            List<ItemEntity> items = world.getEntitiesByType(
                EntityType.ITEM,
                new Box(MinecraftClient.getInstance().player.getBlockPos()).expand(config.beamDistance),
                entity -> !itemsToColors.containsKey(entity)
            );
            
            items.forEach(entity -> {
                try {
                    if (entity != null) {
                        TextColor color = ColorGetter.getColor(entity, config);
                        if (color != null) {
                            itemsToColors.put(entity, color.getRgb());
                        }
                    }
                } catch (Throwable possible) {
                    if (config.debug.throwErrors) {
                        throw possible;
                    }
                    if (config.debug.printErrors) {
                        possible.printStackTrace();
                    }
                }
            });

            itemsToColors.forEach((entity, color) -> {
                if (entity.isRemoved() || !entity.isAlive()) {
                    return;
                }

                if (((ItemEntityAgeAccessor)entity).getItemAge() < config.minimumAge) {
                    return;
                }
                
                if (!config.showWhiteItems && color == 16777215) {
                    return;
                }

                if (MinecraftClient.getInstance().player == null) {
                    return;
                }

                ParticleGenerator.generateParticles(entity, color, config);
            });
        });
    }
}
