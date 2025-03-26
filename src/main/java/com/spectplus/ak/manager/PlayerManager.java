package com.spectplus.ak.manager;

import com.spectplus.ak.Main;
import com.spectplus.ak.utils.itemstack.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by AnyKwey
 */
public class PlayerManager {

    private static List<UUID> spectator = new ArrayList<>();
    private static Map<UUID, PlayerManager> players = new HashMap<>();
    private Player player;
    private ItemStack[] items = new ItemStack[40];
    private boolean vanished;

    public PlayerManager(Player player){
        this.player = player;
        vanished = false;
    }

    public void init() {
        getPlayers().put(player.getUniqueId(), this);
        getSpectator().add(player.getUniqueId());
        player.setGameMode(GameMode.getByValue(Main.getInstance().getConfig().getInt("Management.gamemode")));
        saveInventory();
        setVanished(true);
        isVanished();

        if (Main.getInstance().getConfig().getBoolean("hotbar.replay.authorize")) {
            player.getInventory().setItem(Main.getInstance().getConfig().getInt("hotbar.replay.slot") - 1,
                    new ItemBuilder(Main.getInstance().getConfig().getItemStack("hotbar.replay.material"))
                            .setDisplayName(Main.getInstance().getConfig().getString("hotbar.replay.name").replace("&", "§"))
                            .setLore(Main.getInstance().getConfig().getString("hotbar.replay.lore").replace("&", "§")).toItemStack());
        }

        if (Main.getInstance().getConfig().getBoolean("hotbar.player_list.authorize")) {
            player.getInventory().setItem(Main.getInstance().getConfig().getInt("hotbar.player_list.slot") - 1,
                    new ItemBuilder(Main.getInstance().getConfig().getItemStack("hotbar.players_list.material"))
                            .setDisplayName(Main.getInstance().getConfig().getString("hotbar.player_list.name").replace("&", "§"))
                            .setLore(Main.getInstance().getConfig().getString("hotbar.player_list.lore").replace("&", "§")).toItemStack());
        }

        if (Main.getInstance().getConfig().getBoolean("hotbar.phantom.authorize")) {
            player.getInventory().setItem(Main.getInstance().getConfig().getInt("hotbar.phantom.slot") - 1,
                    new ItemBuilder(Main.getInstance().getConfig().getItemStack("hotbar.phantom.material"))
                            .setDisplayName(Main.getInstance().getConfig().getString("hotbar.phantom.name").replace("&", "§"))
                            .setLore(Main.getInstance().getConfig().getString("hotbar.phantom.lore").replace("&", "§")).toItemStack());
        }

        if (Main.getInstance().getConfig().getBoolean("hotbar.random_teleport.authorize")) {
            player.getInventory().setItem(Main.getInstance().getConfig().getInt("hotbar.random_teleport.slot") - 1,
                    new ItemBuilder(Main.getInstance().getConfig().getItemStack("hotbar.random_teleport.material"))
                            .setDisplayName(Main.getInstance().getConfig().getString("hotbar.random_teleport.name").replace("&", "§"))
                            .setLore(Main.getInstance().getConfig().getString("hotbar.random_teleport.lore").replace("&", "§")).toItemStack());
        }
    }

    public void destroy(){
        getPlayers().remove(player.getUniqueId());
        getSpectator().remove(player.getUniqueId());
        player.setGameMode(Bukkit.getServer().getDefaultGameMode());
        giveInventory();
    }

    public static PlayerManager getFromPlayer(Player player){
        return getPlayers().get(player.getUniqueId());
    }

    public static boolean isInSpectatorMod(Player player){
        return getSpectator().contains(player.getUniqueId());
    }

    public ItemStack[] getItems() {
        return items;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (vanished) {
                if(Main.getInstance().getConfig().getBoolean("spectators_view")) {
                    if (getFromPlayer(players).isVanished()) players.showPlayer(player);
                } else if(!Main.getInstance().getConfig().getBoolean("spectators_view")) {
                    if (!getFromPlayer(players).isVanished()) players.hidePlayer(player);
                }
            } else {
                players.showPlayer(player);
            }
        }
    }

    public void teleportLocation(){
        player.teleport(new Location((World) Main.getInstance().getConfig().get("Management.game.world"), Main.getInstance().getConfig().getDouble("Managment.game.X"), Main.getInstance().getConfig().getDouble("Managment.game.Y"), Main.getInstance().getConfig().getDouble("Managment.game.Z")));
    }

    public void saveInventory(){
        for(int slot = 0; slot < 36; slot++){
            ItemStack item = player.getInventory().getItem(slot);
            if(item != null){
                items[slot] = item;
            }
        }

        items[36] = player.getInventory().getHelmet();
        items[37] = player.getInventory().getChestplate();
        items[38] = player.getInventory().getLeggings();
        items[39] = player.getInventory().getBoots();

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void giveInventory(){
        player.getInventory().clear();

        for(int slot = 0; slot < 36; slot++){
            ItemStack item = items[slot];
            if(item != null){
                player.getInventory().setItem(slot, item);
            }
        }

        player.getInventory().setHelmet(items[36]);
        player.getInventory().setChestplate(items[37]);
        player.getInventory().setLeggings(items[38]);
        player.getInventory().setBoots(items[39]);
    }

    public static List<UUID> getSpectator() {
        return spectator;
    }

    public static Map<UUID, PlayerManager> getPlayers() {
        return players;
    }
}