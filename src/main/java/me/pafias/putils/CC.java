package me.pafias.putils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for handling chat components and legacy color codes.
 * Provides methods to serialize components, convert strings to components,
 * and format text with color codes.
 */
public class CC {

    public static final @NotNull Component EMPTY = Component.empty();
    public static final @NotNull Component NEW_LINE = Component.newline();

    public static String serialize(Component component) {
        if (component == null) return null;
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static Component multiLine(Object... s) {
        Component component = Component.empty();
        for (int i = 0; i < s.length; i++) {
            Object ss = s[i];
            if (ss == null) continue;
            if (ss instanceof String)
                component = component.append(a((String) ss));
            else if (ss instanceof Component)
                component = component.append((Component) ss);
            if (i < s.length - 1)
                component = component.append(NEW_LINE);
        }
        return component;
    }

    public static Component a(String s) {
        if (s == null) return null;
        TextComponent component = Component.empty();
        component = component.decoration(TextDecoration.ITALIC, false);
        component = component.append(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
        return component;
    }

    public static List<Component> a(List<String> collection) {
        return a(ArrayList::new, collection);
    }

    public static <C extends List<Component>> C a(@NotNull Supplier<C> collectionFactory, List<String> collection) {
        if (collection == null) return null;
        return collection.stream().map(CC::a).collect(Collectors.toCollection(collectionFactory));
    }

    public static Set<Component> a(Set<String> collection) {
        return a(HashSet::new, collection);
    }

    public static <C extends Set<Component>> C a(@NotNull Supplier<C> collectionFactory, Set<String> collection) {
        if (collection == null) return null;
        return collection.stream().map(CC::a).collect(Collectors.toCollection(collectionFactory));
    }

    public static Component a(Object o) {
        if (o instanceof String)
            return a((String) o);
        else if (o instanceof ArrayList) {
            ArrayList ol = (ArrayList) o;
            Component component = Component.empty();
            for (int i = 0; i < ol.size(); i++) {
                String s = (String) ol.get(i);
                if (s == null) continue;
                component = component.append(a(s));
                if (i < ol.size() - 1)
                    component = component.append(Component.newline());
            }
            return component;
        } else
            return null;
    }

    public static Component af(String s, Object... o) {
        if (s == null) return null;
        // Silly fix to be able to process Component in String.format.
        // Convert each passed Component to a regular string with '&' color codes and then at the end the a() method converts the whole thing back to one Component.
        for (int i = 0; i < o.length; i++) {
            Object value = o[i];
            if (value instanceof Component)
                o[i] = LegacyComponentSerializer.legacyAmpersand().serialize((Component) value);
        }
        return a(String.format(s, o));
    }

    public static List<Component> af(List<String> s, Object... o) {
        return af(ArrayList::new, s, o);
    }

    public static <C extends List<Component>> C af(@NotNull Supplier<C> collectionFactory, List<String> s, Object... o) {
        if (s == null) return null;
        return s.stream().map(ss -> af(ss, o)).collect(Collectors.toCollection(collectionFactory));
    }

    public static Set<Component> af(Set<String> s, Object... o) {
        return af(HashSet::new, s, o);
    }

    public static <C extends Set<Component>> C af(@NotNull Supplier<C> collectionFactory, Set<String> s, Object... o) {
        if (s == null) return null;
        return s.stream().map(ss -> af(ss, o)).collect(Collectors.toCollection(collectionFactory));
    }

    public static Component af(Object s, Object... o) {
        if (s instanceof String)
            return af((String) s, o);
        else if (s instanceof ArrayList) {
            ArrayList sl = (ArrayList) s;
            // These damn paper/adventure devs did not make a method to pass an array of Components to the sendMessage method
            // so we gotta do it this way...
            Component component = Component.empty();
            for (int i = 0; i < sl.size(); i++) {
                String ss = (String) sl.get(i);
                if (ss == null) continue;
                component = component.append(af(ss, o));
                if (i < sl.size() - 1)
                    component = component.append(Component.newline());
            }
            return component;
        } else
            return null;
    }

    public static String t(String s) {
        if (s == null) return null;
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> t(List<String> s) {
        return t(ArrayList::new, s);
    }

    public static <S extends List<String>> S t(Supplier<S> collectionFactory, List<String> s) {
        if (s == null) return null;
        return s.stream().map(CC::t).collect(Collectors.toCollection(collectionFactory));
    }

    public static Set<String> t(Set<String> s) {
        return t(HashSet::new, s);
    }

    public static <S extends Set<String>> S t(Supplier<S> collectionFactory, Set<String> s) {
        if (s == null) return null;
        return s.stream().map(CC::t).collect(Collectors.toCollection(collectionFactory));
    }

    public static Collection<String> t(Object o) {
        if (o instanceof String)
            return Collections.singletonList(t((String) o));
        else if (o instanceof List)
            return t((List<String>) o);
        else if (o instanceof Set)
            return t((Set<String>) o);
        else
            return null;
    }

    public static String tf(String s, Object... o) {
        if (s == null) return null;
        return t(String.format(s, o));
    }

    public static List<String> tf(List<String> s, Object... o) {
        return tf(ArrayList::new, s, o);
    }

    public static <S extends List<String>> S tf(Supplier<S> collectionFactory, List<String> s, Object... o) {
        if (s == null) return null;
        return s.stream().map(ss -> tf(ss, o)).collect(Collectors.toCollection(collectionFactory));
    }

    public static Set<String> tf(Set<String> s, Object... o) {
        return tf(HashSet::new, s, o);
    }

    public static <S extends Set<String>> S tf(Supplier<S> collectionFactory, Set<String> s, Object... o) {
        if (s == null) return null;
        return s.stream().map(ss -> tf(ss, o)).collect(Collectors.toCollection(collectionFactory));
    }

    public static Collection<String> tf(Object s, Object... o) {
        if (s instanceof String)
            return Collections.singletonList(tf((String) s, o));
        else if (s instanceof List)
            return tf((List<String>) s, o);
        else if (s instanceof Set)
            return tf((Set<String>) s, o);
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
