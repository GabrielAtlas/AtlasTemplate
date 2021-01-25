package com.atlasplugins.atlastemplate.apis;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;

import com.mojang.authlib.properties.Property;

import de.tr7zw.nbtapi.NBTItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemComposer {

    private ItemStack item;

    public ItemComposer(ItemStack item) {
        this.item = item;
    }

    public ItemComposer(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemComposer(Material material, int amount, int durability) {
        this.item = new ItemStack(material, amount, (short) durability);
    }

    public ItemComposer(String url) {
        ItemStack skull = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (short) 3);
        if (url == null || url.isEmpty()) {
            this.item = skull;
            return;
        }
        if (!url.startsWith("http://textures.minecraft.net/texture/"))
            url = "http://textures.minecraft.net/texture/" + url;
        try {
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(url.getBytes()), null);
            profile.getProperties().put("textures", new Property("textures", 
            		new String(Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()))));
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
            skull.setItemMeta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.item = skull;
    }

    public static ItemComposer of(Material material) {
        return new ItemComposer(material);
    }

    public static ItemComposer of(Material material, int amount, int durability) {
        return new ItemComposer(material, amount, durability);
    }

    public static ItemComposer of(ItemStack item) {
        return new ItemComposer(item);
    }

    public static ItemComposer of(String url) {
        return new ItemComposer(url);
    }

    public ItemComposer compose(Consumer<ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    /**
     * créditos a: https://github.com/TheMFjulio/MFLib/blob/master/src/main/java/com/mateus/mflib/util/ItemBuilder.java
     */
    public ItemComposer setNBTTag(String key, String value) {
        try {
			Object nmsCopy = NMSReflect.getCraftBukkitClass("inventory", "CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object nbtTagCompound = NMSReflect.getNMSClass("NBTTagCompound").getConstructor().newInstance();
            boolean b = nmsCopy.getClass().getMethod("getTag").invoke(nmsCopy) != null;
            Object nbtTag = b ? nmsCopy.getClass().getMethod("getTag").invoke(nmsCopy) : nbtTagCompound;
            Constructor<?> nbsString = NMSReflect.getNMSClass("NBTTagString").getConstructor(String.class);
            nbtTag.getClass().getMethod("set", String.class, NMSReflect.getNMSClass("NBTBase"))
                    .invoke(nbtTag, key, nbsString.newInstance(value));
            nmsCopy.getClass().getMethod("setTag", NMSReflect.getNMSClass("NBTTagCompound")).invoke(nmsCopy, nbtTag);
            this.item = (ItemStack) NMSReflect.getCraftBukkitClass("inventory", "CraftItemStack").getMethod("asBukkitCopy", NMSReflect.getNMSClass("ItemStack"))
                    .invoke(null, nmsCopy);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
        	NBTItem nbti = new NBTItem(this.item);
        	nbti.setString(key, value);
        	this.item = nbti.getItem();
        }
        return this;
    }

    public ItemComposer composeMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();
        consumer.accept(meta);
        item.setItemMeta(meta);
        return this;
    }

    public ItemComposer setName(String name) {
        if (name == null || name.equalsIgnoreCase("nulo")) return this;
        composeMeta(meta -> meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name)));
        return this;
    }

    public ItemComposer setLore(List<String> lore) {
        if (lore == null || lore.isEmpty() || lore.get(0).equalsIgnoreCase("nulo")) return this;
        composeMeta(meta -> meta.setLore(lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList())));
        return this;
    }

    public ItemComposer setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemComposer addLore(String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemComposer addLore(List<String> lore) {
        composeMeta(meta -> {
            List<String> newLore = ((meta != null && meta.hasLore()) ? meta.getLore() : new ArrayList<String>());
            newLore.addAll((lore));
            meta.setLore(newLore);
        });
        return this;
    }

    public ItemComposer addGlow(boolean glow) {
        if (!glow) return this;
        try {
        	compose(it -> it.addUnsafeEnchantment(XEnchantment.DURABILITY.parseEnchantment(), 1));
            composeMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
		} catch (Exception e) {
		}
        return this;
    }

    public ItemComposer setAmount(int amount) {
        compose(it -> it.setAmount(amount));
        return this;
    }

    public ItemComposer setDurability(int durability) {
        compose(it -> it.setDurability((short) durability));
        return this;
    }

    public ItemComposer addEnchantment(Enchantment enchantment, int level) {
        compose(it -> it.addUnsafeEnchantment(enchantment, level));
        return this;
    }

    public ItemComposer addEnchantments(HashMap<Enchantment, Integer> enchantments) {
        compose(it -> it.addUnsafeEnchantments(enchantments));
        return this;
    }

    public ItemComposer addItemFlag(ItemFlag... itemflag) {
        composeMeta(meta -> meta.addItemFlags(itemflag));
        return this;
    }

    public ItemComposer setSkullOwner(String owner) {
        composeMeta(meta -> {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(owner);
        });
        return this;
    }

    public ItemComposer setClickListener(JavaPlugin plugin, Consumer<PlayerInteractEvent> consumer) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInteract(PlayerInteractEvent e) {
                if (e.hasItem() && e.getItem().isSimilar(item)) {
                    consumer.accept(e);
                }
            }
        }, plugin);
        return this;
    }

    /**
     * créditos a: https://github.com/TheMFjulio/MFLib/blob/master/src/main/java/com/mateus/mflib/util/NBTGetter.java
     */
    public static String getNBTTag(ItemStack item, String key) {
        if (item == null || item.getType().equals(Material.AIR)) return null;
        try {
            Object nmsCopy = NMSReflect.getCraftBukkitClass("inventory", "CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            if (nmsCopy.getClass().getMethod("getTag").invoke(nmsCopy) != null) {
                Object tagCompound = nmsCopy.getClass().getMethod("getTag").invoke(nmsCopy);
                return (String) tagCompound.getClass().getMethod("getString", String.class).invoke(tagCompound, key);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        	return new NBTItem(item).getString(key);
        }
		return null;
    }

    public ItemStack toItemStack() {
        return item;
    }

    public ItemStack build() {
        return item;
    }

}