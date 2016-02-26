package com.kunta.dev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyHandler{
	// <string, string> is <partyName, playername>
	static HashMap<String, List<String>> parties = new HashMap<String, List<String>>();
	static HashMap<String, List<String>> invites = new HashMap<String, List<String>>();
	
	public static void createParty(String PlayerName)
	{
		if(!parties.containsKey(PlayerName)){
			List<String> players = new ArrayList<String>();
			players.add(PlayerName);
			if(parties.containsValue(PlayerName)){
				//leaveParty();
			}
			parties.put(PlayerName, players);
		}
	}
	public static void deleteParty(){}
	public static void getInvites(Player asking)
	{
		String name = asking.getName();
		if(invites.containsKey(name)){
			List<String> invs = invites.get(name);
			asking.sendMessage(ChatColor.BLUE + "Invites: " + ChatColor.AQUA + invs.toString());
		} else
			asking.sendMessage(ChatColor.RED + "You have no invites!");
	}	
	public static void getParty(Player asking, Player host)
	{
		String hostName = host.getName();
		List<String> party = parties.get(hostName);
		asking.sendMessage(ChatColor.BLUE + "Players in Party: " + party.size() + "\n Players: " + ChatColor.AQUA + party.toString());
	}
	public static String getPartyHost(Player asking)
	{
		Collection<List<String>> values = parties.values();
		Set<String> keys = parties.keySet();
		String askingName = asking.getName();
		List<String> lf;
		for(List<String> party : values){
			if(party.contains(askingName)){
				lf = party;
			}
		}
		for(String hostName : keys){
			List<String> party = parties.get(hostName);
			if(party.contains(askingName)){
				return hostName;
			}
		}
		return "Player not in party";
	}
	public static Player getPlayer(Player sender, String target) {
	    Player p = sender.getServer().getPlayer(target);
	    return p;
	}
	public static void inviteToParty(Player host, Player invited)
	{
		String hostName = host.getName();
		String invitedName = invited.getName();
		List<String> invs = new ArrayList<String>();
		if(!parties.containsKey(hostName)){
    		createParty(hostName);
		}
		invited.sendMessage(ChatColor.GREEN + hostName + ChatColor.BLUE + " invited you to join their party!");
		invs.add(hostName);
		invites.put(invitedName, invs);
		host.sendMessage(ChatColor.BLUE + "Successfully invited " + ChatColor.GREEN + invitedName + ChatColor.BLUE + " to your party!");
	}
	public static void joinParty(Player host, Player joining)
	{
		String hostName = host.getName();
		String joinName = joining.getName();
		if(parties.containsKey(hostName)){
			List<String> party = parties.get(hostName);
			if(invites.get(joinName) != null){
				List<String> invs = invites.get(joinName);
				if(invs.contains(hostName)){
					party.add(joinName);
					parties.put(hostName, party);
					for(int i = 0; i < invs.size(); i++){
						if(invs.get(i) == hostName){
							invs.remove(i);
						}
					}
		    		host.sendMessage(ChatColor.GREEN + joining.getName() + ChatColor.BLUE + " joined your party!");
		    		joining.sendMessage(ChatColor.BLUE + "Successfully joined " + ChatColor.GREEN + host.getName() + "'s " + ChatColor.BLUE + "party!");
				} 
			} else
				joining.sendMessage(ChatColor.RED + "You have not been invited to this party!");

		} else
			joining.sendMessage(ChatColor.RED + "Player is not in a party");
	}
	public static void kickFromParty(Player kicked)
	{
		String kickedName = kicked.getName();
		String hostName = getPartyHost(kicked);
		Player host = getPlayer(kicked, hostName);
		List<String> party = parties.get(hostName);
		if(party.contains(kickedName)){
			for(int i = 0; i < party.size(); i++){
				String name = party.get(i);
				if(name == kickedName){
					party.remove(i);
					parties.put(hostName, party);
					host.sendMessage(ChatColor.BLUE + "Kicked " + ChatColor.GREEN + kickedName + ChatColor.BLUE + " from your party!");
					kicked.sendMessage(ChatColor.RED + "You were kicked from the party!");
				}
			}
		}
	}
	public static void leaveParty(Player leave)
	{
		String leaveName = leave.getName();
		String hostName = getPartyHost(leave);
		List<String> party = parties.get(hostName);
		for(int i = 0; i < party.size(); i++){
			if(party.get(i) == leaveName){
				party.remove(i);
				leave.sendMessage(ChatColor.BLUE + "You have left the party");
			}
		}
		
	}

}
