package dev.silverandro.lootbeams;

import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.joml.Vector3f;

public class ParticleGenerator {
    public static void generateParticles(ItemEntity entity, int color, LootbeamsConfig config) {
        for (int i = 0; i < config.particleCount; i++) {
            entity.getEntityWorld().addParticle(
                    new DustParticleEffect(
                            new Vector3f(
                                    ((color >> 16) & 0xFF) / 255f,
                                    ((color >> 8) & 0xFF) / 255f,
                                    (color & 0xFF) / 255f
                            ),
                            1.0f),
                    true,
                    entity.getX(),
                    entity.getY() + config.beamOffset + (entity.getEntityWorld().random.nextDouble() * config.beamHeight),
                    entity.getZ(),
                    0.0,
                    0.0,
                    0.0
            );
        }

        if (config.enchantedParticles && entity.getStack().hasGlint()) {
            for (int i = 0; i < Math.max(config.particleCount / 2, 1); i++) {
                entity.getEntityWorld().addParticle(
                        ParticleTypes.PORTAL,
                        true,
                        entity.getX(),
                        entity.getY() + config.beamOffset + (entity.getEntityWorld().random.nextDouble() * config.beamHeight),
                        entity.getZ(),
                        0.0,
                        -0.3,
                        0.0
                );
            }
        }
    }
}
