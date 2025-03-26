package com.spectplus.ak.events;

import com.spectplus.ak.Main;
import com.spectplus.ak.commands.SpectatorCommand;
import com.spectplus.ak.listeners.SpectatorCancels;
import com.spectplus.ak.listeners.SpectatorItemsInteract;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class EventsRegister {

    private Plugin plugin;

    public EventsRegister(Plugin plugin){
        this.plugin = plugin;
    }

    public void registerListeners(){
        registerListener(new SpectatorItemsInteract());
        registerListener(new SpectatorCancels());
    }

    public void registerCommands(){
        registerCommand("spectator", new SpectatorCommand());
    }


    private void registerListener(Listener listener){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(listener, plugin);
    }

    private void registerCommand(String cmd, CommandExecutor command){
        Main.getInstance().getCommand(cmd).setExecutor(command);
    }
}