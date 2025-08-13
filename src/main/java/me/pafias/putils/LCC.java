package me.pafias.putils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The legacy version of CC.class
 * @link CC
 */
public class LCC {

    public static String t(String s) {
        if (s == null) return null;
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String[] t(ArrayList<String> s) {
        if (s == null) return null;
        return s.stream().map(LCC::t).collect(Collectors.toList()).toArray(new String[]{});
    }

    public static String[] t(Object o) {
        if (o instanceof String)
            return new String[]{t((String) o)};
        else if (o instanceof ArrayList)
            return t((ArrayList<String>) o);
        else
            return null;
    }

    public static String tf(String s, Object... o) {
        if (s == null) return null;
        return t(String.format(s, o));
    }

    public static String[] tf(ArrayList<String> s, Object... o) {
        if (s == null) return null;
        return s.stream().map(ss -> tf(ss, o)).collect(Collectors.toList()).toArray(new String[]{});
    }

    public static String[] tf(Object s, Object... o) {
        if (s instanceof String)
            return new String[]{tf((String) s, o)};
        else if (s instanceof ArrayList)
            return tf((ArrayList<String>) s, o);
        else return null;
    }

    public static String strip(String s) {
        if (s == null) return null;
        return ChatColor.stripColor(s);
    }

    public static ChatColor fromShulkerType(String shulkerTypeColor) {
        switch (shulkerTypeColor.toUpperCase()) {
            case "WHITE":
                return ChatColor.WHITE;
            case "ORANGE":
                return ChatColor.GOLD;
            case "MAGENTA":
                return ChatColor.LIGHT_PURPLE;
            case "LIGHT_BLUE":
                return ChatColor.AQUA;
            case "YELLOW":
                return ChatColor.YELLOW;
            case "LIME":
                return ChatColor.GREEN;
            case "PINK":
                return ChatColor.LIGHT_PURPLE;
            case "GRAY":
                return ChatColor.DARK_GRAY;
            case "LIGHT_GRAY":
                return ChatColor.GRAY;
            case "CYAN":
                return ChatColor.DARK_AQUA;
            case "PURPLE":
                return ChatColor.DARK_PURPLE;
            case "BLUE":
                return ChatColor.DARK_BLUE;
            case "BROWN":
                return ChatColor.GOLD;
            case "GREEN":
                return ChatColor.DARK_GREEN;
            case "RED":
                return ChatColor.DARK_RED;
            case "BLACK":
                return ChatColor.BLACK;
            default:
                return ChatColor.RESET;
        }
    }

}
