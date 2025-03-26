package com.spectplus.ak;

import com.spectplus.ak.events.EventsRegister;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if(getConfig().getBoolean("enable")){
            getServer().getConsoleSender().sendMessage(getMessage("start-message"));
            new EventsRegister(this).registerListeners();
            new EventsRegister(this).registerCommands();
        }
    }

    @Override
    public void onDisable() {
        if(getConfig().getBoolean("enable")){
            getServer().getConsoleSender().sendMessage(getMessage("close-message"));
        }
    }

    public String getMessage(String path){
        return getConfig().getString(path).replace("&", "§");
    }

    public static Main getInstance() {
        return instance;
    }
}
