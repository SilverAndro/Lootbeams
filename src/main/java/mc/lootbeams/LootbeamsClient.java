package mc.lootbeams;

import mc.lootbeams.mixin.ItemEntityAgeAccessor;
import mc.microconfig.MicroConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LootbeamsClient implements ClientModInitializer {
    private final LootbeamsConfig config = MicroConfig.getOrCreate("lootbeams", new LootbeamsConfig());
    
    private final HashMap<ItemEntity, Integer> itemsToColors = new HashMap<>();
    
    @Override
    public void onInitializeClient() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            //noinspection RedundantCast
            List<Entity> items = world.getEntitiesByType(EntityType.ITEM, entity -> !itemsToColors.containsKey((ItemEntity)entity));
            
            items.forEach(entity -> {
                try {
                    if (entity != null) {
                        List<Text> text = ((ItemEntity)entity).getStack().getTooltip(null, TooltipContext.Default.NORMAL);
        
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
                            itemsToColors.put((ItemEntity)entity, color.getRgb());
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
                    PlayerLookup.around(world, entity.getBlockPos(), 32f).forEach(playerEntity -> {
                        if (playerEntity != null) {
                            playerEntity.networkHandler.sendPacket(
                                new ParticleS2CPacket(
                                    new DustParticleEffect(
                                        ((color >> 16) & 0xFF) / 255f,
                                        ((color >> 8) & 0xFF) / 255f,
                                        (color & 0xFF) / 255f,
                                        1.0f),
                                    true,
                                    entity.getX(),
                                    entity.getY() + config.beamHeight,
                                    entity.getZ(),
                                    0f,
                                    config.beamOffset,
                                    0f,
                                    1.0f,
                                    config.particleCount
                                )
                            );
                        }
                    });
                }
            });
            
            toRemove.forEach(itemsToColors::remove);
        });
    }
}
