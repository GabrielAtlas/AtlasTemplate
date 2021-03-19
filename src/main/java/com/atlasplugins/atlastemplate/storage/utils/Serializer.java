package com.atlasplugins.atlastemplate.storage.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

public class Serializer {

	private static boolean supportsBase64Encoder = true;

	static {
		verify();
	}

	private static void verify() {
		try {
			Class<?> nbtTagCompoundClass = Reflections.getNMSClass("NBTTagCompound");
			Class<?> nmsItemStackClass = Reflections.getNMSClass("ItemStack");
			nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass);
			supportsBase64Encoder = true;
		} catch (Exception e) {
			supportsBase64Encoder = false;
		}
	}

	public static String serializeLocation(Location loc) {
		if (loc == null) return null;
		return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch()
		+ ";" + loc.getWorld().getName();
	}

	public static Location deserializeLocation(String s) {
		if (s == null || !s.contains(";")) return null;
		String[] parts = s.split(";");
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);
		float yaw = Float.parseFloat(parts[3]);
		float pitch = Float.parseFloat(parts[4]);
		World w = Bukkit.getServer().getWorld(parts[5]);
		return new Location(w, x, y, z, yaw, pitch);
	}

	public static ItemStack deserializeItem(String data) {
		if(supportsBase64Encoder) {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
			DataInputStream dataInputStream = new DataInputStream(inputStream);

			ItemStack itemStack = null;
			try {
				Class<?> nbtTagCompoundClass = Reflections.getNMSClass("NBTTagCompound");
				Class<?> nmsItemStackClass = Reflections.getNMSClass("ItemStack");
				Object nbtTagCompound = Reflections.getNMSClass("NBTCompressedStreamTools").getMethod("a", DataInputStream.class).invoke(null, dataInputStream);
				Object craftItemStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass).invoke(null, nbtTagCompound);
				itemStack = (ItemStack) Reflections.getCBClass("inventory", "CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return itemStack;
		}
		final ItemStack rebuilded = NBTItem.convertNBTtoItem(new NBTContainer(data));
		return rebuilded;
	}

	public static String serializeItem(ItemStack item) {
		if(supportsBase64Encoder) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream dataOutput = new DataOutputStream(outputStream);

			try {
				Class<?> nbtTagCompoundClass = Reflections.getNMSClass("NBTTagCompound");
				Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
				Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
				Object nmsItemStack = Reflections.getCBClass("inventory", "CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
				Reflections.getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
				Reflections.getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, DataOutput.class).invoke(null, nbtTagCompound, (DataOutput) dataOutput);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return new BigInteger(1, outputStream.toByteArray()).toString(32);
		}
		final String nbtEncoded = NBTItem.convertItemtoNBT(item).toString();
		return nbtEncoded;
	}


}