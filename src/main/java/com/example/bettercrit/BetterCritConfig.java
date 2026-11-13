package com.example.bettercrit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Simple JSON config at config/bettercrit.json (restart the game to apply changes). */
public final class BetterCritConfig {
	/** Master switch for the whole crit-stun system. */
	public static boolean enableCrits = true;
	/** Max knockback multiplier of the released hit. 1.0 - 32767.0 */
	public static double critMultiplier = 2.0;
	/** Number of crits needed before the stored knockback is released. 1 - 32767 */
	public static int maxHits = 3;
	/** Fallback stun ticks for weapons that are not a tiered sword/axe. 1 - 32767 */
	public static int stunTicks = 20;
	/** Stun ticks per sword/axe material (item id prefix, e.g. "diamond" for diamond_sword). */
	public static Map<String, Integer> tierStunTicks = defaultTiers();

	private static Map<String, Integer> defaultTiers() {
		Map<String, Integer> m = new LinkedHashMap<>();
		m.put("wooden", 10);
		m.put("stone", 15);
		m.put("golden", 12);
		m.put("copper", 18);
		m.put("iron", 22);
		m.put("diamond", 30);
		m.put("netherite", 40);
		return m;
	}

	/** Stun duration in ticks for a crit made with the given weapon. */
	public static int stunTicksFor(ItemStack weapon) {
		String path = BuiltInRegistries.ITEM.getKey(weapon.getItem()).getPath();
		if (path.endsWith("_sword") || path.endsWith("_axe")) {
			for (Map.Entry<String, Integer> e : tierStunTicks.entrySet()) {
				if (path.startsWith(e.getKey() + "_")) {
					return e.getValue();
				}
			}
		}
		return stunTicks;
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static class Data {
		boolean enableCrits = true;
		double critMultiplier = 2.0;
		int maxHits = 3;
		int stunTicks = 20;
		Map<String, Integer> tierStunTicks = defaultTiers();
	}

	private BetterCritConfig() {}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("bettercrit.json");
		Data data = new Data();
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				Data read = GSON.fromJson(reader, Data.class);
				if (read != null) data = read;
			} catch (Exception e) {
				BetterCrit.LOGGER.warn("Could not read bettercrit.json, using defaults", e);
			}
		}
		data.critMultiplier = Math.max(1.0, Math.min(32767.0, data.critMultiplier));
		data.maxHits = Math.max(1, Math.min(32767, data.maxHits));
		data.stunTicks = Math.max(1, Math.min(32767, data.stunTicks));

		if (data.tierStunTicks == null) data.tierStunTicks = defaultTiers();
		for (Map.Entry<String, Integer> d : defaultTiers().entrySet()) {
			data.tierStunTicks.putIfAbsent(d.getKey(), d.getValue());
		}
		data.tierStunTicks.replaceAll((k, v) -> Math.max(1, Math.min(32767, v == null ? 20 : v)));

		enableCrits = data.enableCrits;
		tierStunTicks = data.tierStunTicks;
		critMultiplier = data.critMultiplier;
		maxHits = data.maxHits;
		stunTicks = data.stunTicks;

		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(data, writer);
		} catch (Exception e) {
			BetterCrit.LOGGER.warn("Could not write bettercrit.json", e);
		}
	}
}
