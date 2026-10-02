package com.soldierskull.eclipse.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;


public class LaserParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteSet;

    protected LaserParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;

        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;

        this.hasPhysics = false;
        this.gravity = 0.0F;

        this.lifetime = 4;

        this.quadSize = 0.22F;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.setSpriteFromAge(this.spriteSet);
        this.alpha = 1.0F - (float) this.age / (float) this.lifetime;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(
                BasicParticleType type,
                ClientWorld world,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed) {

            return new LaserParticle(world, x, y, z, spriteSet);
        }
    }
}