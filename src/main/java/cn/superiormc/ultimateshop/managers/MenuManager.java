package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ui.AbstractButton;
import cn.superiormc.ultimateshop.objects.ui.ObjectButton;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    public static Map<String, MenuManager> menuManagers = new HashMap<>();

    private String fileName = null;

    private ObjectShop shop = null;

    private Configuration menuConfigs;

    private Map<Integer, AbstractButton> menuItems = new HashMap<>();

    private Map<String, AbstractButton> buttonItems = new HashMap<>();

    public MenuManager(String fileName, ObjectShop shop) {
        this.fileName = fileName;
        this.shop = shop;
        initMenu();
        initShopItems();
        initButtonItems();
    }

    public MenuManager(String fileName) {
        initMenu();
        this.fileName = fileName;
        initButtonItems();
    }

    private void initMenu() {
        menuManagers.put(fileName, this);
        File file = new File(UltimateShop.instance.getDataFolder() + "/menus/" + fileName + ".yml");
        if (!file.exists()){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cWe can not found your menu file: " +
                    fileName + ".yml!");
        }
        else {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded menu: " +
                    fileName + ".yml!");
            this.menuConfigs = YamlConfiguration.loadConfiguration(file);
        }
    }

    private void initShopItems() {
        int i = 0;
        if (menuConfigs == null) {
            return;
        }
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (shop.getProduct(String.valueOf(itemChar)) == null) {
                    continue;
                }
                menuItems.put(slot, shop.
                        getProduct(String.valueOf(itemChar)));
            }
        }
    }

    private void initButtonItems() {
        if (menuConfigs == null) {
            return;
        }
        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("buttons");
        if (tempVal1 == null) {
            return;
        }
        for (String button : tempVal1.getKeys(false)) {
            buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button)));
        }
        int i = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (buttonItems.get(String.valueOf(itemChar)) == null) {
                    continue;
                }
                menuItems.put(slot, buttonItems.get(String.valueOf(itemChar)));
            }
        }
    }

    public String getString(String path, String defaultValue) {
        return menuConfigs.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return menuConfigs.getInt(path, defaultValue);
    }

    public Map<Integer, AbstractButton> getMenu() {
        return menuItems;
    }


}