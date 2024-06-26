package cn.superiormc.ultimateshop.papi;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.StaticPlaceholder;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.shbobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.TextUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    public static PlaceholderAPIExpansion papi = null;

    private final UltimateShop plugin;
    @Override
    public boolean canRegister() {
        return true;
    }

    public PlaceholderAPIExpansion(UltimateShop plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "PQguanfang";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "ultimateshop";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return null;
        }
        String[] args = params.split("_");
        if (args.length < 1) {
            return null;
        } else if (args[0].startsWith("{")) {
            String result = params;
            Pattern pattern1 = Pattern.compile("\\{discount_(.*?)\\}");
            Matcher matcher1 = pattern1.matcher(result);
            while (matcher1.find()) {
                String discount = matcher1.group(1);
                result = result.replace("{discount_" + discount + "}",
                        String.valueOf(StaticPlaceholder.getDiscountValue(discount, player)));
            }
            Pattern pattern2 = Pattern.compile("\\{random_(.*?)\\}");
            Matcher matcher2 = pattern2.matcher(result);
            while (matcher2.find()) {
                String placeholder = matcher2.group(1);
                result = result.replace("{random_" + placeholder + "}",
                        ObjectRandomPlaceholder.getNowValue(placeholder));
            }
            Pattern pattern3 = Pattern.compile("\\{random-times_(.*?)\\}");
            Matcher matcher3 = pattern3.matcher(result);
            while (matcher3.find()) {
                String placeholder = matcher3.group(1);
                result = result.replace("{random-times_" + placeholder + "}",
                        ObjectRandomPlaceholder.getRefreshDoneTime(placeholder));
            }
            return result;
        } else {
            ObjectShop shop = ConfigManager.configManager.getShop(args[0]);
            if (shop == null) {
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-shop");
            }
            ObjectItem item = shop.getProduct(args[1]);
            if (item == null) {
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-product");
            }
            ObjectUseTimesCache playerTimesCache = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
            ObjectUseTimesCache serverTimesCache = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
            switch (args[2]) {
                case "{buy-price}":
                    return TextUtil.parse(ObjectPrices.getDisplayNameInLine(player,
                            item.getBuyPrice().takeSingleThing(player.getInventory(), player, playerTimesCache.getBuyUseTimes(), 1, true).getResultMap(),
                            item.getBuyPrice().getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")));
                case "{sell-price}":
                    return TextUtil.parse(ObjectPrices.getDisplayNameInLine(player,
                            item.getSellPrice().giveSingleThing(player, playerTimesCache.getBuyUseTimes(), 1).getResultMap(),
                            item.getSellPrice().getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")));
                case "{buy-limit-player}":
                    return String.valueOf(item.getPlayerBuyLimit(player));
                case "{sell-limit-player}":
                    return String.valueOf(item.getPlayerSellLimit(player));
                case "{buy-limit-server}":
                    return String.valueOf(item.getServerSellLimit(player));
                case "{sell-limit-server}":
                    return String.valueOf(item.getServerBuyLimit(player));
                case "{buy-times-player}":
                    return String.valueOf(playerTimesCache == null ? "0" :
                            playerTimesCache.getBuyUseTimes());
                case "{sell-times-player}":
                    return String.valueOf(playerTimesCache == null ? "0" :
                            playerTimesCache.getSellUseTimes());
                case "{buy-refresh-player}":
                    return String.valueOf(playerTimesCache == null ? "" :
                            playerTimesCache.getBuyRefreshTimeDisplayName());
                case "{sell-refresh-player}":
                    return String.valueOf(playerTimesCache == null ? "" :
                            playerTimesCache.getSellRefreshTimeDisplayName());
                case "{buy-cooldown-player}":
                    return String.valueOf(playerTimesCache == null ? ConfigManager.configManager.
                            getString("placeholder.cooldown.now") :
                            playerTimesCache.getBuyCooldownTimeDisplayName());
                case "{sell-cooldown-player}":
                    return String.valueOf(playerTimesCache == null ? ConfigManager.configManager.
                            getString("placeholder.cooldown.now") :
                            playerTimesCache.getSellCooldownTimeDisplayName());
                case "{buy-times-server}":
                    return String.valueOf(serverTimesCache == null ? "0" :
                            serverTimesCache.getBuyUseTimes());
                case "{sell-times-server}":
                    return String.valueOf(serverTimesCache == null ? "0" :
                            serverTimesCache.getSellUseTimes());
                case "{buy-refresh-server}":
                    return String.valueOf(serverTimesCache == null ? "" :
                            serverTimesCache.getBuyRefreshTimeDisplayName());
                case "{sell-refresh-server}":
                    return String.valueOf(serverTimesCache == null ? "" :
                            serverTimesCache.getSellRefreshTimeDisplayName());
                case "{buy-cooldown-server}":
                    return String.valueOf(serverTimesCache == null ? ConfigManager.configManager.
                            getString("placeholder.cooldown.now") :
                            serverTimesCache.getBuyCooldownTimeDisplayName());
                case "{sell-cooldown-server}":
                    return String.valueOf(serverTimesCache == null ? ConfigManager.configManager.
                            getString("placeholder.cooldown.now") :
                            serverTimesCache.getSellCooldownTimeDisplayName());
                case "{item-name}":
                    return item.getDisplayName(player);
            }
        }
        return null;
    }
}
