package io.github.swagree.repokespawn.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.repokespawn.Main;
import io.github.swagree.repokespawn.PokemonEventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Gui {


    public static void openGui(Player player) {
        String title = "精灵刷新列表";
        Inventory inv = Bukkit.createInventory(null, 45, title);

        Set<String> keys = Main.pokemonProbability.keySet();
        List<Pokemon> pokemonList = new ArrayList<>();
        Map<Pokemon, String> pokemonGetKey = new HashMap<>();

        for (String key : keys) {
            String pokemonName = Main.plugin.getConfig().getString("pokemon." + key + ".species");
            Optional<EnumSpecies> speciesOpt = EnumSpecies.getFromName(pokemonName);

            if (!speciesOpt.isPresent()) {
                Bukkit.getLogger().warning("[RePokeSpawn] 找不到宝可梦种类: " + pokemonName);
                continue; // 跳过无效宝可梦
            }

            Pokemon pokemon = Pixelmon.pokemonFactory.create(speciesOpt.get());
            pokemonList.add(pokemon);
            pokemonGetKey.put(pokemon, key);
        }

        for (int i = 0; i < pokemonList.size() && i < inv.getSize(); i++) {
            Pokemon pokemon = pokemonList.get(i);
            String key = pokemonGetKey.get(pokemon);
            ItemStack photo = getPhoto(pokemon);
            ItemMeta itemMeta = photo.getItemMeta();

            if (itemMeta != null) {
                itemMeta.setDisplayName(ChatColor.YELLOW + key);
                List<String> lore = new ArrayList<>();

                List<String> biomeList = Main.plugin.getConfig().getStringList("pokemon." + key + ".biome");
                List<String> weatherList = Main.plugin.getConfig().getStringList("pokemon." + key + ".weather");
                List<String> timeList = Main.plugin.getConfig().getStringList("pokemon." + key + ".time");
                double probability = Double.parseDouble(Main.pokemonProbability.getOrDefault(key, String.valueOf(0.0)));

                lore.add(ChatColor.GRAY + "该宝可梦刷新条件:");
                // 如果列表为空则显示 "无要求"，否则显示列表中的内容
                String biomeDisplay = biomeList.isEmpty() ? "无要求" : String.join(", ", biomeList);
                String weatherDisplay = weatherList.isEmpty() ? "无要求" : String.join(", ", weatherList);
                String timeDisplay = timeList.isEmpty() ? "无要求" : String.join(", ", timeList);

                lore.add(ChatColor.GREEN + "地形: " + biomeDisplay);
                lore.add(ChatColor.BLUE + "天气: " + weatherDisplay);
                lore.add(ChatColor.AQUA + "时间: " + timeDisplay);
                lore.add(ChatColor.RED + "刷新概率: " + String.format("%.2f", probability));

                lore.add("");

                lore.add(ChatColor.GRAY + "你所在的地形条件:");
                lore.add(ChatColor.GREEN + "地形: " + String.join(", ", PokemonEventListener.getBiomeName(player.getLocation())));
                lore.add(ChatColor.BLUE + "天气: " + String.join(", ", PokemonEventListener.getCurrentWeather(player.getLocation())));
                lore.add(ChatColor.AQUA + "时间: " + String.join(", ", PokemonEventListener.getCurrentTime(player.getLocation())));

                itemMeta.setLore(lore);
                photo.setItemMeta(itemMeta);
            }

            inv.setItem(i, photo);
        }

        player.openInventory(inv);
        Main.pokemonProbability.clear();

    }

    public static ItemStack getPhoto(Pokemon pokemon) {
        net.minecraft.item.ItemStack nmsItem = ItemPixelmonSprite.getPhoto(pokemon);
        return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) nmsItem);
    }

}