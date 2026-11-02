package dev.kebab.dualraids.mixin;

import dev.kebab.dualraids.DualRaidsMod;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla: Ein Spieler mit Bad Omen, der in ein Dorf kommt, verlaengert immer den
 * bereits laufenden Raid (createOrExtendRaid findet ihn ueber getNearbyRaid).
 *
 * Hier: Waehrend createOrExtendRaid laeuft, tut getNearbyRaid so, als gaebe es keinen
 * laufenden Raid, solange im Dorf weniger als MAX_RAIDS_PER_VILLAGE Raids laufen.
 * Dadurch erzeugt Vanilla selbst einen neuen Raid mit der normalen Logik -
 * Mittelpunkt und Spawn-Orte der Raider werden also genauso berechnet wie sonst.
 */
@Mixin(Raids.class)
public abstract class RaidsMixin {

	/** Zweite (und weitere) Raids, die von dieser Mod erzeugt wurden. */
	@Unique
	private final Set<Raid> dualraids$extraRaids = Collections.newSetFromMap(new WeakHashMap<>());

	@Unique
	private boolean dualraids$inCreateOrExtend = false;

	@Unique
	private boolean dualraids$bypassed = false;

	@Inject(method = "createOrExtendRaid", at = @At("HEAD"))
	private void dualraids$begin(ServerPlayer player, BlockPos pos, CallbackInfoReturnable<Raid> cir) {
		this.dualraids$inCreateOrExtend = true;
		this.dualraids$bypassed = false;
	}

	@Inject(method = "createOrExtendRaid", at = @At("RETURN"))
	private void dualraids$end(ServerPlayer player, BlockPos pos, CallbackInfoReturnable<Raid> cir) {
		Raid result = cir.getReturnValue();
		if (this.dualraids$bypassed && result != null) {
			this.dualraids$extraRaids.add(result);
		}
		this.dualraids$inCreateOrExtend = false;
		this.dualraids$bypassed = false;
	}

	@Inject(method = "getNearbyRaid", at = @At("RETURN"), cancellable = true)
	private void dualraids$allowSecondRaid(BlockPos pos, int distance, CallbackInfoReturnable<Raid> cir) {
		if (!this.dualraids$inCreateOrExtend) {
			return;
		}
		Raid nearest = cir.getReturnValue();
		if (nearest == null || !dualraids$isRunning(nearest)) {
			return;
		}

		// Laufende Raids in der Naehe zaehlen (Primaer-Raid + eigene Extra-Raids).
		int running = 1;
		double maxDistSq = distance;
		for (Raid extra : this.dualraids$extraRaids) {
			if (extra != nearest && dualraids$isRunning(extra) && extra.getCenter().distSqr(pos) < maxDistSq) {
				running++;
			}
		}

		if (running < DualRaidsMod.MAX_RAIDS_PER_VILLAGE) {
			this.dualraids$bypassed = true;
			cir.setReturnValue(null);
		}
	}

	@Unique
	private static boolean dualraids$isRunning(Raid raid) {
		return raid.isStarted() && !raid.isOver() && !raid.isStopped();
	}
}
