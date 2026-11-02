package dev.kebab.dualraids;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualRaidsMod implements ModInitializer {
	public static final String MOD_ID = "dualraids";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Maximale Anzahl gleichzeitig laufender Raids im selben Dorf. */
	public static final int MAX_RAIDS_PER_VILLAGE = 2;

	@Override
	public void onInitialize() {
		LOGGER.info("Dual Raids geladen: bis zu {} Raids gleichzeitig pro Dorf.", MAX_RAIDS_PER_VILLAGE);
	}
}
