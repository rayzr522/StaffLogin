/**
 * 
 */
package com.rayzr522.stafflogin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * @author Rayzr
 *
 */
public class EventListener implements Listener {

    private static final List<String> ALLOWED_COMMANDS = Arrays.asList("login", "logout", "password");

    private Map<UUID, Boolean> warned = new HashMap<>();

    private StaffLogin plugin;

    public EventListener(StaffLogin plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            public void run() {
                warned.clear();
            }
        }.runTaskTimer(plugin, 0L, 60L);
    }

    private boolean handle(Cancellable e, Player player) {
        if (plugin.shouldPrevent(player)) {
            if (!warned.getOrDefault(player.getUniqueId(), false)) {
                warned.put(player.getUniqueId(), true);
                player.sendMessage(plugin.tr("must-login"));
            }
            e.setCancelled(true);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().toVector().equals(e.getFrom().toVector())) {
            return;
        }
        if (handle(e, e.getPlayer())) {
            e.setCancelled(false);
            e.setTo(e.getFrom());
            e.getPlayer().setVelocity(new Vector(0, 0, 0));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        handle(e, (Player) e.getWhoClicked());
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        if (e.getViewers() == null || e.getViewers().size() < 1) {
            return;
        }
        HumanEntity ent = e.getViewers().get(0);
        if (!(ent instanceof Player)) {
            return;
        }
        if (handle(new Cancellable() {
            @Override
            public void setCancelled(boolean cancel) {
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        }, (Player) ent)) {
            e.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().toLowerCase().split(" ")[0];
        if (cmd.startsWith("/"))
            cmd = cmd.substring(1);
        if (!ALLOWED_COMMANDS.contains(cmd)) {
            handle(e, e.getPlayer());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        handle(e, e.getPlayer());
    }

    @EventHandler
    public void onLogOut(PlayerQuitEvent e) {
        plugin.logOut(e.getPlayer());
    }

}
