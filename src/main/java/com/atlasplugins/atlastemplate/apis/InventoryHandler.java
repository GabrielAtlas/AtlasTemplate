package com.atlasplugins.atlastemplate.apis;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.atlasplugins.atlastemplate.AtlasTemplate;

/**
 * @author don't
 */
public class InventoryHandler {

    static {
    	AtlasTemplate main = AtlasTemplate.getPlugin(AtlasTemplate.class);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getSlotType() == SlotType.OUTSIDE || e.getCurrentItem() == null) return;
                if (e.getInventory().getHolder() instanceof HandlerHolder) {
                    e.setCancelled(true);
                    InventoryHandler handler = ((HandlerHolder) e.getInventory().getHolder()).getInventoryHandler();
                    if (handler.getCustomItems().containsKey(e.getSlot())) {
                        handler.getCustomItems().get(e.getSlot()).accept((Player) e.getWhoClicked());
                    } else if (handler.getHandler() != null) {
                        handler.getHandler().accept(e);
                    }
                }
            }
        }, main);
    }

    private Inventory inventory;
    private Consumer<InventoryClickEvent> handler;
    private Map<Integer, Consumer<Player>> customItems;

    public InventoryHandler(String name, int size) {
        this.inventory = Bukkit.createInventory(new HandlerHolder(this), size, name);
        this.customItems = new HashMap<>();
    }

    public InventoryHandler handler(Consumer<InventoryClickEvent> handler) {
        this.handler = handler;
        return this;
    }

    public InventoryHandler item(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    public InventoryHandler item(int slot, ItemStack item, Consumer<Player> consumer) {
        inventory.setItem(slot, item);
        customItems.put(slot, consumer);
        return this;
    }

    public InventoryHandler fill(ItemStack item, boolean replace) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || replace) inventory.setItem(i, item);
        }
        return this;
    }
    
    public InventoryHandler fill(ItemStack item, boolean replace, int startingIn) {
        for (int i = startingIn; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || replace) inventory.setItem(i, item);
        }
        return this;
    }

    public InventoryHandler fill(ItemStack item) {
        return fill(item, true);
    }

    public InventoryHandler item(ItemStack item, int... slot) {
        for (int i : slot) inventory.setItem(i, item);
        return this;
    }

    public InventoryHandler items(Consumer<Inventory> consumer) {
        consumer.accept(this.inventory);
        return this;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    private Consumer<InventoryClickEvent> getHandler() {
        return handler;
    }

    private Map<Integer, Consumer<Player>> getCustomItems() {
        return customItems;
    }

    private class HandlerHolder implements InventoryHolder {

        private InventoryHandler handler;

        public HandlerHolder(InventoryHandler handler) {
            super();
            this.handler = handler;
        }

        @Override
        public Inventory getInventory() {
            return handler.inventory;
        }

        public InventoryHandler getInventoryHandler() {
            return handler;
        }

    }

}