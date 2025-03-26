package com.spectplus.ak.listeners;

import com.spectplus.ak.Main;
import com.spectplus.ak.manager.PlayerManager;
import com.spectplus.ak.utils.itemstack.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpectatorItemsInteract implements Listener {

    int task = 0;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!PlayerManager.isInSpectatorMod(player)) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        e.setCancelled(true);

        /**
         * Random teleportation
         */
        Material randomtp = new ItemBuilder(Material.valueOf(Main.getInstance().getConfig().getString("hotbar.random_teleport.material"))).getType();
        if (player.getInventory().getItemInHand().getType() == randomtp) {
            List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
            list.remove(player);

            if (list.size() == 0) {
                player.sendMessage(Main.getInstance().getMessage("hotbar.random_teleport.none"));
                return;
            }

            Player target = list.get(new Random().nextInt(list.size()));
            player.teleport(target.getLocation());
        }

        /**
         * Phantom
         */
        Material phantom = new ItemBuilder(Material.valueOf(Main.getInstance().getConfig().getString("hotbar.phantom.material"))).getType();
        if (player.getInventory().getItemInHand().getType() == phantom) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    task++;
                    player.setGameMode(GameMode.SPECTATOR);

                    if (task == Main.getInstance().getConfig().getInt("hotbar.phantom.timer")) {
                        player.setGameMode(GameMode.getByValue(Main.getInstance().getConfig().getInt("Management.gamemode")));
                        cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 1, 1);
        }

        /**
         * Replay
         */
        /**Material replay = new ItemBuilder(Material.valueOf(Main.getInstance().getConfig().getString("hotbar.phantom.material"))).getType();
         if (player.getInventory().getItemInHand().getType() == replay) {
         ServerCommon().launchQueue("lobby");
         }**/

        /**
         * Player List
         */
        Material pl = new ItemBuilder(Material.valueOf(Main.getInstance().getConfig().getString("hotbar.players_list.material"))).getType();
        if (player.getInventory().getItemInHand().getType() == pl) {
            if (PlayerManager.isInSpectatorMod((Player) Bukkit.getOnlinePlayers())) return;
            if (Bukkit.getOnlinePlayers().size() <= 9) {
                Inventory i = Bukkit.createInventory(null, 9, Main.getInstance().getMessage("hotbar.player_list.invname"));

                for (Player players : Bukkit.getOnlinePlayers()) {
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    if (meta != null) {
                        meta.setOwningPlayer(players);
                        meta.setDisplayName(players.getName());
                        skull.setItemMeta(meta);
                    }
                    i.addItem(skull);
                }

                player.openInventory(i);
            }
        } else if(Bukkit.getOnlinePlayers().size() <= 2*9){
            Inventory i = Bukkit.createInventory(null, 2*9, Main.getInstance().getMessage("hotbar.player_list.invname"));

            for(Player players : Bukkit.getOnlinePlayers()) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(players);
                    meta.setDisplayName(players.getName());
                    skull.setItemMeta(meta);
                }
                i.addItem(skull);
            }

            player.openInventory(i);
        } else if(Bukkit.getOnlinePlayers().size() <= 3*9){
            Inventory i = Bukkit.createInventory(null, 3*9, Main.getInstance().getMessage("hotbar.player_list.invname"));

            for(Player players : Bukkit.getOnlinePlayers()) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(players);
                    meta.setDisplayName(players.getName());
                    skull.setItemMeta(meta);
                }
                i.addItem(skull);
            }

            player.openInventory(i);
        } else if(Bukkit.getOnlinePlayers().size() <= 4*9){
            Inventory i = Bukkit.createInventory(null, 4*9, Main.getInstance().getMessage("hotbar.player_list.invname"));

            for(Player players : Bukkit.getOnlinePlayers()) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(players);
                    meta.setDisplayName(players.getName());
                    skull.setItemMeta(meta);
                }
                i.addItem(skull);
            }

            player.openInventory(i);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(Main.getInstance().getMessage("hotbar.player_list.invname"))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.isInSpectatorMod(players)) {
                PlayerManager pm = PlayerManager.getFromPlayer(players);
                if (pm.isVanished()) {
                    players.showPlayer(player);
                }
            } else {
                players.hidePlayer(player);
            }
        }
    }
}
