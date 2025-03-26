package com.spectplus.ak.listeners;

import com.spectplus.ak.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class SpectatorCancels implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(PlayerManager.isInSpectatorMod(e.getPlayer()));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(PlayerManager.isInSpectatorMod(e.getPlayer()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(PlayerManager.isInSpectatorMod(e.getPlayer()));
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        e.setCancelled(PlayerManager.isInSpectatorMod(e.getPlayer()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        e.setCancelled(PlayerManager.isInSpectatorMod((Player) e.getEntity()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.setCancelled(PlayerManager.isInSpectatorMod(e.getPlayer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(PlayerManager.isInSpectatorMod((Player) e.getWhoClicked()));
    }

    /**
     * Exemple
     */
    /**@EventHandler public void onEvent(PlayerEvent e) {
    Player player = e.getPlayer();
    if (player.getHealthScale() == 0) return;
    if (!(PlayerManager.isInSpectatorMod(player))) return;
    new PlayerManager(player).init();
    PlayerManager.getFromPlayer(player).teleportLocation();
    /*
     * Location + possibility to put a title
    /*
    }**/
}