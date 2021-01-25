package com.atlasplugins.atlastemplate.storage.adapters;

import com.atlasplugins.atlastemplate.storage.utils.Serializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {
	@Override
	public void write(JsonWriter out, ItemStack item) throws IOException {
		out.beginObject();
		out.name("item").value(Serializer.serializeItem(item));
		out.endObject();
	}

	@Override
	public ItemStack read(JsonReader in) throws IOException {
		in.beginObject();

		ItemStack item = null;
		while (in.hasNext()) {
			if (in.nextName().equalsIgnoreCase("item")) {
				item = Serializer.deserializeItem(in.nextString());
			}
		}

		in.endObject();
		return item;
	}

}