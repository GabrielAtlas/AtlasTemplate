package com.atlasplugins.atlastemplate.storage.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationAdapter extends TypeAdapter<Location> {
    private final static boolean FULL = true;

    @Override
    public void write(JsonWriter out, Location loc) throws IOException {
        out.beginObject();
        if (FULL) {
            out.name("location").value(loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName());
        } else {
            out.name("location").value(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld().getName());
        }
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        in.beginObject();

        Location loc = null;
        while (in.hasNext()) {
            if (in.nextName().equalsIgnoreCase("location")) {
                String[] parts = in.nextString().split(";");
                if (FULL) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    float yaw = Float.parseFloat(parts[3]);
                    float pitch = Float.parseFloat(parts[4]);
                    World w = Bukkit.getServer().getWorld(parts[5]);
                    loc = new Location(w, x, y, z, yaw, pitch);
                } else {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    World w = Bukkit.getServer().getWorld(parts[3]);
                    loc = new Location(w, x, y, z);
                }
            }
        }

        in.endObject();
        return loc;
    }
}