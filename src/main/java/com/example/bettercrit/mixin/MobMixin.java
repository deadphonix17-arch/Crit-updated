package com.example.bettercrit.mixin;

import com.example.bettercrit.CritStunHolder;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** While crit-stunned, the mob's whole AI step (targeting, goals, attacks, movement) is skipped. */
@Mixin(Mob.class)
public abstract class MobMixin {
	@Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
	private void bettercrit$skipAi(CallbackInfo ci) {
		if (((Object) this) instanceof CritStunHolder holder && holder.getStunTicks() > 0) {
			ci.cancel();
		}
	}
}
