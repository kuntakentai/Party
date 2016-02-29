package com.kunta.dev.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.kunta.dev.PartyHandler;

public class EventPlayerJoin implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		PartyHandler.partyChat.put(event.getPlayer().getName(), false);
	}
}
