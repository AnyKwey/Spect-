package com.spectplus.ak.commands;

import com.spectplus.ak.Main;
import com.spectplus.ak.manager.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by AnyKwey
 */
public class SpectatorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().getMessage("error.onlyPlayer"));
            return false;
        }

        if (!(Main.getInstance().getConfig().getBoolean("commands.spectator.enable")))
            return false;

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("spectator")) {
            if (!(player.hasPermission(Main.getInstance().getConfig().getString("commands.spectator.permission")))) {
                player.sendMessage(Main.getInstance().getMessage("error.notPermission"));
                return false;
            }

            if (args.length == 0) {
                if(PlayerManager.isInSpectatorMod(player)){
                    PlayerManager.getFromPlayer(player).destroy();
                } else {
                    new PlayerManager(player).init();
                }
                return false;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("off")) {
                    if (!(Main.getInstance().getConfig().getBoolean("commands.spectator.enable"))) {
                        player.sendMessage(Main.getInstance().getMessage("error.alreadyOff"));
                        return false;
                    }

                    try {
                        Main.getInstance().getConfig().set("commands.spectator.enable", false);
                        player.sendMessage(Main.getInstance().getMessage("configUpdate"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (args[0].equalsIgnoreCase("on")) {
                    if (Main.getInstance().getConfig().getBoolean("commands.spectator.enable")) {
                        player.sendMessage(Main.getInstance().getMessage("error.alreadyOn"));
                        return false;
                    }

                    try {
                        Main.getInstance().getConfig().set("commands.spectator.enable", true);
                        player.sendMessage(Main.getInstance().getMessage("configUpdate"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(Main.getInstance().getMessage("commands.spectator.syntax"));
                }
            }
        }
        return false;
    }
}