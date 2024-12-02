package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ObjectMenu {

    public MenuType type;

    public static Map<String, ObjectMenu> commonMenus = new HashMap<>();

    public static Collection<String> notCommonMenuNames = new HashSet<>();

    public String fileName;

    private ObjectShop shop = null;

    private ObjectCondition condition;

    private ObjectAction openAction;

    private ObjectAction closeAction;

    public Configuration menuConfigs;

    public Map<Integer, AbstractButton> menuItems = new TreeMap<>();

    public Map<String, AbstractButton> buttonItems = new HashMap<>();

    public ObjectMenu(String fileName, ObjectShop shop) {
        this.fileName = fileName;
        this.shop = shop;
        this.type = MenuType.Shop;
        initMenu();
        initShopItems();
        initButtonItems();
    }

    public ObjectMenu(String fileName, ObjectItem item) {
        this.fileName = fileName;
        this.shop = item.getShopObject();
        this.type = MenuType.More;
        initMenu();
        initButtonItems();
    }

    public ObjectMenu(String fileName) {
        this.fileName = fileName;
        this.type = MenuType.Common;
        initMenu();
        initButtonItems();
    }

    public MenuType getType() {
        return type;
    }

    public void initMenu() {
        if (type == MenuType.Common) {
            commonMenus.put(fileName, this);
        } else {
            notCommonMenuNames.add(fileName);
        }
        File file = new File(UltimateShop.instance.getDataFolder() + "/menus/" + fileName + ".yml");
        if (!file.exists()){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: We can not found your menu file: " +
                    fileName + ".yml!");
        } else {
            if (type == MenuType.Common) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded menu: " + fileName + ".yml!");
            }
            this.menuConfigs = YamlConfiguration.loadConfiguration(file);
        }
        if (menuConfigs == null) {
            this.condition = new ObjectCondition();
            this.openAction = new ObjectAction();
            this.closeAction = new ObjectAction();
        } else if (shop != null) {
            this.condition = new ObjectCondition(menuConfigs.getConfigurationSection("conditions"));
            this.openAction = new ObjectAction(menuConfigs.getConfigurationSection("open-actions"), shop);
            this.closeAction = new ObjectAction(menuConfigs.getConfigurationSection("close-actions"));
        } else {
            this.condition = new ObjectCondition(menuConfigs.getConfigurationSection("conditions"), shop);
            this.openAction = new ObjectAction(menuConfigs.getConfigurationSection("open-actions"));
            this.closeAction = new ObjectAction(menuConfigs.getConfigurationSection("close-actions"));
        }
    }

    public void initShopItems() {
        int i = 0;
        if (menuConfigs == null) {
            return;
        }
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (!UltimateShop.freeVersion && shop.getCopyItem(String.valueOf(itemChar))!= null) {
                    menuItems.put(slot, shop.getCopyItem(String.valueOf(itemChar)));
                    continue;
                }
                if (shop.getButton(String.valueOf(itemChar)) != null) {
                    menuItems.put(slot, shop.getButton(String.valueOf(itemChar)));
                    continue;
                }
                if (shop.getProduct(String.valueOf(itemChar)) != null) {
                    menuItems.put(slot, shop.getProduct(String.valueOf(itemChar)));
                    continue;
                }
            }
        }
    }

    public void initButtonItems() {
        if (menuConfigs == null) {
            return;
        }
        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("buttons");
        if (tempVal1 == null) {
            return;
        }
        for (String button : tempVal1.getKeys(false)) {
            if (shop == null) {
                buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button)));
            }
            else {
                buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button),
                        shop));
            }
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
        if (defaultValue == null) {
            return menuConfigs.getString(path);
        }
        return menuConfigs.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return menuConfigs.getInt(path, defaultValue);
    }

    public Map<Integer, AbstractButton> getMenu() {
        return menuItems;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

    public void doOpenAction(Player player) {
        if (openAction != null) {
            openAction.runAllActions(new ObjectThingRun(player));
        }
    }

    public void doCloseAction(Player player) {
        if (closeAction != null) {
            closeAction.runAllActions(new ObjectThingRun(player));
        }
    }

    public String getName() {
        return fileName;
    }

    public ConfigurationSection getConfig() {
        return menuConfigs;
    }

}