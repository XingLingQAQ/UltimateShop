package cn.superiormc.ultimateshop.paper.utils;

import cn.superiormc.ultimateshop.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PaperTextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component modernParse(String text) {
        try {
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            // 如果 MiniMessage 格式失败，退回兼容旧的 & 颜色代码
            return Component.text(TextUtil.parse(text));
        }
    }

    public static Component modernParse(String text, Player player) {
        text = cn.superiormc.ultimateshop.utils.TextUtil.withPAPI(text, player);
        try {
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            // 如果 MiniMessage 格式失败，退回兼容旧的 & 颜色代码
            return Component.text(TextUtil.parse(text));
        }
    }

}
