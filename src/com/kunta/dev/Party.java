package com.kunta.dev;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.kunta.dev.Events.EventPlayerChat;
import com.kunta.dev.Events.EventPlayerJoin;
import com.kunta.dev.Events.EventPlayerLeave;

public class Party extends JavaPlugin{
	
	@Override 
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		this.getCommand("party").setExecutor(new PartyCommand());
		PartyHandler.loadCommands();
		pm.registerEvents(new EventPlayerChat(), this);
		pm.registerEvents(new EventPlayerJoin(), this);
		pm.registerEvents(new EventPlayerLeave(), this);
	}
	
}
