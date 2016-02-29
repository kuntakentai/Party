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
	public static HashMap<String, Boolean> partyChat = new HashMap<String, Boolean>();
	static List<String> commands = new ArrayList<String>();
	
	static int partyCap = 6;
	static int inviteCap = 10;
	
	public static void createParty(Player host, String PlayerName)
	{
		if(!parties.containsKey(PlayerName)){
			List<String> players = new ArrayList<String>();
			players.add(PlayerName);
			if(parties.containsValue(PlayerName)){
				leaveParty(host);
			}
			parties.put(PlayerName, players);
			partyChat.put(PlayerName, false);
		}
	}
	public static void disbandParty(Player host)
	{
		if(playerInParty(host) && getPartyHost(host).equalsIgnoreCase(host.getName())){
			partyBroadcast(host, ChatColor.BLUE + "The party has been disbanded!");
			parties.remove(host.getName());
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
	public static void getParty(Player asking)
	{
		List<String> party = parties.get(getPartyHost(asking));
		asking.sendMessage(ChatColor.BLUE + "Players in Party: " + party.size() + "\n Players: " + ChatColor.AQUA + party.toString());
	}
	public static void getParty(Player asking, Player host)
	{
		List<String> party = parties.get(getPartyHost(host));
		asking.sendMessage(ChatColor.BLUE + "Players in Party: " + party.size() + "\n Players: " + ChatColor.AQUA + party.toString());
	}
	public static List<String> getPartyMembers(Player player)
	{
		if(playerInParty(player)){
			String partyHost = getPartyHost(player);
			return parties.get(partyHost);
		} else {
			List<String> nullFix = new ArrayList<String>();
			nullFix.add("No Party Members!");
			return nullFix;
		}
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
	public static List<String> getPlayersInPartyChat(Player player)
	{
		Set<String> chat = partyChat.keySet();
		List<String> partyc = new ArrayList<String>();
		for(String name : chat){
			if(partyChat.get(name) == true){
				partyc.add(name);
			}
		}
		return partyc;
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
		if(parties.get(hostName) == null && !playerInParty(host)){
    		createParty(host, hostName);
		}
		String invitedName = invited.getName();
		List<String> invs = new ArrayList<String>();
		List<String> party = parties.get(hostName);
		Set<String> hosts = parties.keySet();
		if(hostName != invitedName){
			if(hosts.contains(hostName)){
				if(!getPartyHost(invited).equalsIgnoreCase(hostName)){
					invited.sendMessage(ChatColor.GREEN + hostName + ChatColor.BLUE + " invited you to join their party!");
					if(invites.containsKey(invitedName)){
						invs = invites.get(invitedName);
						if(invs.size() == inviteCap){
							invs.remove(0);
							invs.add(hostName);
							invites.put(invitedName, invs);
						} else {
							invs.add(hostName);
							invites.put(invitedName, invs);
						}
					} else {
						invs.add(hostName);
						invites.put(invitedName, invs);
					}
					host.sendMessage(ChatColor.BLUE + "Successfully invited " + ChatColor.GREEN + invitedName + ChatColor.BLUE + " to your party!");
				} else
					host.sendMessage(ChatColor.RED + "Player is already in your party");
			} else
				host.sendMessage(ChatColor.RED + "Invite Cancelled. \nYou must be the party leader to invite players!");
		} else
			host.sendMessage(ChatColor.RED + "Cannot join your own party!");
	}
	public static Boolean isPartyHost(Player p)
	{
		if(parties.containsKey(p.getName())){
			return true;
		} else
			return false;
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
						if(isPartyHost(joining)) disbandParty(joining);
						if(playerInParty(joining)) leaveParty(joining);
						partyBroadcast(host, ChatColor.GREEN + joining.getName() + ChatColor.BLUE + " joined your party!");
						party.add(joinName);
						partyChat.put(joinName, false);
						parties.put(hostName, party);
						for(int i = 0; i < invs.size(); i++){
							if(invs.get(i) == hostName){
								invs.remove(i);
							}
						}
			    		joining.sendMessage(ChatColor.BLUE + "Successfully joined " + ChatColor.GREEN + host.getName() + "'s " + ChatColor.BLUE + "party!");
					} else
						joining.sendMessage(ChatColor.RED + "Party is full!");
				} else
					joining.sendMessage(ChatColor.RED + "You have not been invited to this party!");
			} else
				joining.sendMessage(ChatColor.RED + "You have not been invited to this party!");

		} else
			joining.sendMessage(ChatColor.RED + "Player is not in a party");
	}
	public static void kickFromParty(Player kicked)
	{
		if(playerInParty(kicked)){
			String kickedName = kicked.getName();
			String hostName = getPartyHost(kicked);
			Player host = getPlayer(kicked, hostName);
			List<String> party = parties.get(hostName);
			for(int i = 0; i < party.size(); i++){
				String name = party.get(i);
				if(name == kickedName){
					if(name.equalsIgnoreCase(hostName)){
						host.sendMessage(ChatColor.RED + "Cannot kick yourself!");
					} else {
						party.remove(i);
						if(partyChat.get(kickedName).equals(true)) partyChat.put(kickedName, false);
						parties.put(hostName, party);
						host.sendMessage(ChatColor.BLUE + "Kicked " + ChatColor.GREEN + kickedName + ChatColor.BLUE + " from your party!");
						kicked.sendMessage(ChatColor.RED + "You were kicked from the party!");
						partyBroadcast(host, ChatColor.GREEN + kickedName + ChatColor.BLUE + " was kicked from your party!");
					}
				} else
					host.sendMessage(ChatColor.RED + "Player is not into your party!");
			}
		}
	}
	public static void leaveParty(Player leave)
	{
		String leaveName = leave.getName();
		String hostName = getPartyHost(leave);
		List<String> party = parties.get(hostName);
		if(playerInParty(leave) != false){
			for(int i = 0; i < party.size(); i++){
				if(hostName.equalsIgnoreCase(leaveName)){
					disbandParty(leave);
				} else
				if(party.get(i) == leaveName){
					messagePartyMembers(leave, ChatColor.GREEN + leaveName + ChatColor.BLUE + " has left the party!");
					party.remove(i);
					if(partyChat.get(leaveName).equals(true)) partyChat.put(leaveName, false);
					leave.sendMessage(ChatColor.BLUE + "You have left the party");
				}
			}
		} else
			leave.sendMessage(ChatColor.RED + "You are not in a party!");
	}
	public static void loadCommands()
	{
		commands.add("Chat");
		commands.add("Disband");
		commands.add("Help");
		commands.add("Invite");
		commands.add("Invites");
		commands.add("Kick");
		commands.add("Join");
		commands.add("Leave");
		commands.add("List");
	}
	public static void messagePartyMembers(Player p, String message)
	{
		String partyHost = getPartyHost(p);
		List<String> party = parties.get(partyHost);
		for(String name : party){
			Player player = getPlayer(p, name);
			if(name != p.getName())
				player.sendMessage(message);
		}
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
		String helpMessage = "";
		for(String cmd : commands){
			String message = ChatColor.GREEN + cmd + ": ";
			if(cmd.equalsIgnoreCase("Chat")) message = message + ChatColor.BLUE + "Toggle chat between Global or Party\n";
			if(cmd.equalsIgnoreCase("Disband")) message = message + ChatColor.BLUE + "Disbands current party\n";
			if(cmd.equalsIgnoreCase("Help")) message = message + ChatColor.BLUE + "Shows this help menu\n";
			if(cmd.equalsIgnoreCase("Invite")) message = message + ChatColor.BLUE + "Invite players to party\n";
			if(cmd.equalsIgnoreCase("Invites")) message = message + ChatColor.BLUE + "Checks your current invites\n";
			if(cmd.equalsIgnoreCase("Kick")) message = message + ChatColor.BLUE + "Kick player(s) from your party\n";
			if(cmd.equalsIgnoreCase("Join")) message = message + ChatColor.BLUE + "Join players party\n";
			if(cmd.equalsIgnoreCase("Leave")) message = message + ChatColor.BLUE + "Leave your current party\n";
			if(cmd.equalsIgnoreCase("List")) message = message + ChatColor.BLUE + "List your own or another players party";
			helpMessage = helpMessage + message;
		}
		player.sendMessage(helpMessage);
	}
	public static Boolean playerInParty(Player player)
	{
		Collection<List<String>> partyList = parties.values();
		for(List<String> party : partyList){
			if(party.contains(player.getName())){
				return true;
			}
		}
		return false;
	}
	public static void togglePartyChat(Player player)
	{
		if(partyChat.get(player.getName()).equals(null)){
			partyChat.put(player.getName(), false);
			player.sendMessage(ChatColor.BLUE + "Chat has been set to: " + ChatColor.GREEN + "Global");
		}
		if(partyChat.get(player.getName()) != true){
			partyChat.put(player.getName(), true);
			player.sendMessage(ChatColor.BLUE + "Chat has been set to: " + ChatColor.GREEN + "Party");
		} else {
			partyChat.put(player.getName(), false);
			player.sendMessage(ChatColor.BLUE + "Chat has been set to: " + ChatColor.GREEN + "Global");
		}
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