package com.kunta.dev;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PartyHandler{
	// <string, string> is <partyName, playername>
	static HashMap<String, List<String>> parties = new HashMap<String, List<String>>();
	static HashMap<String, List<String>> invites = new HashMap<String, List<String>>();
	static List<String> commands = new ArrayList<String>();
	
	static int partyCap = 6;
	
	public static void createParty(Player host, String PlayerName)
	{
		if(!parties.containsKey(PlayerName)){
			List<String> players = new ArrayList<String>();
			players.add(PlayerName);
			if(parties.containsValue(PlayerName)){
				leaveParty(host);
			}
			parties.put(PlayerName, players);
		}
	}
	public static void confirmDeleteParty(Player host)
	{
		if(playerInParty(host)){
			host.sendMessage(ChatColor.BLUE + "Are you sure you want to disband the party?");
		}
	}
	public static void deleteParty(Player host)
	{
		String hostName = host.getName();
		if(playerInParty(host)){
			partyBroadcast(host, ChatColor.BLUE + "The party has been disbanded!");
			parties.remove(hostName);
		}
	}
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
		List<String> guess = new ArrayList<String>();
		try {
			for(Player player : sender.getServer().getOnlinePlayers()){
				guess.add(player.getName());
			}
			if(guess.contains(target)){
				Player p = sender.getServer().getPlayer(target);
				return p;
			} else
				sender.sendMessage(ChatColor.RED + "Player does not exist.");
		} catch(IllegalArgumentException ignored) {}
		return null;
	}
	public static void inviteToParty(Player host, Player invited)
	{
		//if(host.getServer().getOnlinePlayers().contains(invited.getName())){
		String hostName = host.getName();
		String invitedName = invited.getName();
		List<String> invs = new ArrayList<String>();
		List<String> party = parties.get(hostName);
		if(!parties.containsKey(hostName)){
    		createParty(host, hostName);
		}
		if(hostName != invitedName && party.size() < partyCap){
			invited.sendMessage(ChatColor.GREEN + hostName + ChatColor.BLUE + " invited you to join their party!");
			invs.add(hostName);
			invites.put(invitedName, invs);
			host.sendMessage(ChatColor.BLUE + "Successfully invited " + ChatColor.GREEN + invitedName + ChatColor.BLUE + " to your party!");
		} else
			if(party.size() == partyCap){
				host.sendMessage(ChatColor.RED + "Cannot invite player! \nParty is full!");
			} else
			host.sendMessage(ChatColor.RED + "Cannot join your own party!");
		//}
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
					if(party.size() < partyCap){
						partyBroadcast(host, ChatColor.GREEN + joining.getName() + ChatColor.BLUE + " joined your party!");
						party.add(joinName);
						parties.put(hostName, party);
						for(int i = 0; i < invs.size(); i++){
							if(invs.get(i) == hostName){
								invs.remove(i);
							}
						}
			    		joining.sendMessage(ChatColor.BLUE + "Successfully joined " + ChatColor.GREEN + host.getName() + "'s " + ChatColor.BLUE + "party!");
					} else
						joining.sendMessage(ChatColor.RED + "Party is full!");
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
					partyBroadcast(host, ChatColor.GREEN + kickedName + ChatColor.BLUE + " was kicked from your party!");
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
				partyBroadcast(leave, ChatColor.GREEN + leaveName + ChatColor.BLUE + " has left the party!");
				party.remove(i);
				leave.sendMessage(ChatColor.BLUE + "You have left the party");
			}
		}
		
	}
	public static void loadCommands()
	{
		commands.add("invite");
		commands.add("join");
		commands.add("list");
		commands.add("kick");
		commands.add("leave");
		commands.add("invites");
		commands.add("create");
		commands.add("disband");
	}
	public static void partyBroadcast(Player p, String message)
	{
		String partyHost = getPartyHost(p);
		List<String> party = parties.get(partyHost);
		for(String name : party){
			Player player = getPlayer(p, name);
			player.sendMessage(message);
		}
	}
	public static void partyHelp(Player player)
	{
		player.sendMessage("");
	}
	public static Boolean playerInParty(Player player)
	{
		String hostName = player.getName();
		if(parties.containsKey(hostName)){
			return true;
		}
		return false;
	}
	
	/**Test Area**/
	public static UUID fetchUUID(String s) throws Exception{
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + s);
        String uuid = (String)((JSONObject)new JSONParser().parse(new InputStreamReader(url.openStream()))).get("id");
        String realUUID = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
        return UUID.fromString(realUUID);
	}
	/**Test Area**/
}