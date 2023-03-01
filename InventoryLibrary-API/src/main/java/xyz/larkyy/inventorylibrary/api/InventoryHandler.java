package xyz.larkyy.inventorylibrary.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.larkyy.inventorylibrary.api.packet.PacketListener;
import xyz.larkyy.inventorylibrary.api.packet.PlayerPacketInjector;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedClientboundOpenScreenPacket;
import xyz.larkyy.inventorylibrary.api.packet.wrapped.WrappedServerboundContainerClickPacket;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryOpenEvent;
import xyz.larkyy.inventorylibrary.api.ui.history.HistoryHandler;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;

public class InventoryHandler {

    private static InventoryHandler instance;

    private final JavaPlugin plugin;
    private final IRenderHandler renderHandler;
    private final IItemHandler itemHandler;
    private final HistoryHandler historyHandler;

    private final PacketListener packetListener;

    public static InventoryHandler init(JavaPlugin plugin, IRenderHandler renderHandler, IItemHandler itemHandler,
                                        PlayerPacketInjector playerPacketInjector) {
        if (instance != null) {
            return instance;
        }
        instance = new InventoryHandler(plugin,renderHandler,itemHandler,playerPacketInjector);
        return instance;
    }

    private InventoryHandler(JavaPlugin plugin, IRenderHandler renderHandler, IItemHandler itemHandler,
                             PlayerPacketInjector playerPacketInjector) {
        this.plugin = plugin;
        this.renderHandler = renderHandler;
        this.itemHandler = itemHandler;
        this.packetListener = new PacketListener(playerPacketInjector);
        Bukkit.getPluginManager().registerEvents(packetListener,plugin);
        historyHandler = new HistoryHandler();

        registerListeners();
        onEnable();
    }

    public static InventoryHandler getInstance() {
        return instance;
    }

    public IRenderHandler getRenderHandler() {
        return renderHandler;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public HistoryHandler getHistoryHandler() {
        return historyHandler;
    }

    public PacketListener getPacketListener() {
        return packetListener;
    }

    /*
            Closes all opened custom inventories
         */
    public void closeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            var holder = renderHandler.getOpenedMenu(player).getHolder();
            if (holder instanceof RenderedMenu renderedMenu) {
                player.closeInventory();
            }
        }
    }

    public RenderedMenu getOpenedMenu(Player player) {
        var holder = renderHandler.getOpenedMenu(player).getHolder();
        if (holder instanceof RenderedMenu renderedMenu) {
            return renderedMenu;
        }
        return null;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private void onEnable() {
        packetListener.loadInjections();
    }

    public void onDisable() {
        packetListener.unloadInjections();
    }

    private void registerListeners() {
        packetListener.register(WrappedServerboundContainerClickPacket.class, event -> {
            var player = event.getPlayer();
            var openedMenu = getOpenedMenu(player);

            var bukkitEvent = new CustomInventoryClickEvent(openedMenu,player,event.getClickType(),event.getSlotNum());
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(bukkitEvent);
                    if (bukkitEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    openedMenu.interact(bukkitEvent);
                    if (bukkitEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }.runTask(plugin);

        });
    }
}
