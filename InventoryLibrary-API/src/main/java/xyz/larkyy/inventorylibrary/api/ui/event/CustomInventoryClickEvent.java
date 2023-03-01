package xyz.larkyy.inventorylibrary.api.ui.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import xyz.larkyy.inventorylibrary.api.packet.ClickType;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

public class CustomInventoryClickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private final RenderedMenu renderedMenu;
    private final Player player;
    private final ClickType clickType;
    private final int slot;

    public CustomInventoryClickEvent(RenderedMenu renderedMenu, Player player, ClickType clickType, int slot) {
        this.renderedMenu = renderedMenu;
        this.player = player;
        this.clickType = clickType;
        this.slot = slot;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public RenderedMenu getRenderedMenu() {
        return renderedMenu;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public int getSlot() {
        return slot;
    }
}
