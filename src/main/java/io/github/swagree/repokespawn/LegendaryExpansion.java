package io.github.swagree.repokespawn;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class LegendaryExpansion extends PlaceholderExpansion {

    private final Main plugin;

    public LegendaryExpansion(Main plugin) {
        this.plugin = plugin;
    }

    // 插件标识符，例如 %legendary_countdown%
    @Override
    public String getIdentifier() {
        return "repokespawn";
    }

    @Override
    public String getAuthor() {
        return "SwagRee";  // 替换为你的名字
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return plugin != null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // 当占位符为 %legendary_countdown% 时返回倒计时秒数
        if (identifier.equalsIgnoreCase("countdown")) {
            if(PokemonEventListener.secondsRemaining<=0){
                PokemonEventListener.secondsRemaining = 0;
            }
            return String.valueOf(PokemonEventListener.secondsRemaining);
        }
        return null;
    }
}
