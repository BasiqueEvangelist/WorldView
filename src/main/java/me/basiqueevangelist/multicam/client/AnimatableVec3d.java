package me.basiqueevangelist.multicam.client;

import me.basiqueevangelist.multicam.client.owocode.Animatable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record AnimatableVec3d(Vec3d inner) implements Animatable<AnimatableVec3d> {
    @Override
    public AnimatableVec3d interpolate(AnimatableVec3d next, float delta) {
        return new AnimatableVec3d(
            new Vec3d(
                MathHelper.lerp(delta, this.inner.x, next.inner.x),
                MathHelper.lerp(delta, this.inner.y, next.inner.y),
                MathHelper.lerp(delta, this.inner.z, next.inner.z)
            )
        );
    }
}
