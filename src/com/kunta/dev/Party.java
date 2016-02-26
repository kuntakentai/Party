package com.kunta.dev;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Party extends JavaPlugin{
	
	@Override 
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		this.getCommand("party").setExecutor(new PartyCommand());
	}
	
}
