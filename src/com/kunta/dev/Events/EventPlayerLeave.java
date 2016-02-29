package com.kunta.dev.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.kunta.dev.PartyHandler;

public class EventPlayerLeave implements Listener {
	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player p = (Player)event.getPlayer();
		if(PartyHandler.playerInParty(p)){
			PartyHandler.partyChat.remove(event.getPlayer().getName());
			PartyHandler.leaveParty(p);
		}
	}
}
