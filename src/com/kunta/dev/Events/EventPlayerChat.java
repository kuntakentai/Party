package com.kunta.dev.Events;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.kunta.dev.PartyHandler;

public class EventPlayerChat implements Listener {
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player p = (Player)event.getPlayer();
		String message = event.getMessage();
		Collection<String> recipients = PartyHandler.getPlayersInPartyChat(p);
		if(PartyHandler.playerInParty(p)){
			if(PartyHandler.partyChat.get(p.getName()) == true){
				//event.setFormat(ChatColor.GREEN + "[Party] " + p.getName() + ": " + ChatColor.LIGHT_PURPLE + message);
				PartyHandler.partyBroadcast(p, ChatColor.GREEN + "[Party] " + p.getName() + ": " + ChatColor.LIGHT_PURPLE + message);
				event.setCancelled(true);
			}
		}
	}
}
