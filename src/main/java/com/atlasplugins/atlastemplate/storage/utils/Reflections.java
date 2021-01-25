package com.atlasplugins.atlastemplate.storage.utils;

import org.bukkit.Bukkit;

import java.util.HashMap;

/**
 * créditos a: https://raw.githubusercontent.com/TheMFjulio/MFLib/master/src/main/java/com/mateus/mflib/nms/NMSReflect.java
 * adaptado por: dont
 */
public class Reflections {
    private static final String VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
    private static final HashMap<String, Class<?>> CLASSES = new HashMap<>();

    public static Class<?> getNMSClass(String name) {
        if (!CLASSES.containsKey(name)) {
            try {
                CLASSES.put(name, Class.forName("net.minecraft.server." + VERSION + "." + name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return CLASSES.get(name);
    }

    public static Class<?> getCBClass(String prefix, String name) {
        if (!CLASSES.containsKey(prefix + "." + name)) {
            try {
                CLASSES.put(prefix + "." + name, Class.forName("org.bukkit.craftbukkit." + VERSION + "." + prefix + "." + name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return CLASSES.get(prefix + "." + name);
    }

}