package com.kunta.dev;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand implements CommandExecutor {
	
	@Override 
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Player target;
            /**(args[0],args[1],args[2]) is /party arg0 arg1 arg2
             * (args.length = one, two, or three is /party one two three**/
            /**Invite Start**/
            if(args.length == 0){
            	
            	return true;
            }
            if(args[0].equalsIgnoreCase("invite")){
            	if(args.length > 1){
            		target = PartyHandler.getPlayer(player, args[1]);
            		if(target != null)
            			PartyHandler.inviteToParty(player, target);
            		return true;
            	}
            	if(args.length ==1){
            		player.sendMessage("Please provide players name to invite");
            		return true;
            	}
            }
            /**Invite End**/
            /**Join Start**/
            if(args[0].equalsIgnoreCase("join")){
            	if(args.length > 1){
            		target = PartyHandler.getPlayer(player, args[1]);
            		if(target != null)
            			PartyHandler.joinParty(target, player);
            		return true;
            	}
            	if(args.length ==1){
            		player.sendMessage("Please provide players name to join their party");
            	}
            }
            /**Join End**/
            /**List Start**/
            if(args[0].equalsIgnoreCase("list")){
            	if(args.length > 1){
            		target = PartyHandler.getPlayer(player, args[1]);
            		if(target != null)
            			PartyHandler.getParty(player, target);
            		return true;
            	}
            	if(args.length == 1){
            		if(PartyHandler.playerInParty(player)){
            			PartyHandler.getParty(player, player);
            			return true;
            		} else
            			player.sendMessage("Please provide a players name to view their party");
            		return true;
            	}
            }
            /**List End**/
            /**Kick Start**/
            if(args[0].equalsIgnoreCase("kick")){
            	if(args.length > 1){
            		target = PartyHandler.getPlayer(player, args[1]);
            		if(target != null)
            			PartyHandler.kickFromParty(target);
            		return true;
            	}
            	if(args.length ==1){
            		player.sendMessage("Please provide a players name to kick");
            		return true;
            	}
            }
            /**Kick End**/
            /**Leave Start**/
            if(args[0].equalsIgnoreCase("leave")){
            	if(args.length == 1){
            		PartyHandler.leaveParty(player);
            		return true;
            	}
            }
            /**Leave End**/
            /**Invites Start**/
            if(args[0].equalsIgnoreCase("invites")){
            	if(args.length == 1){
            		PartyHandler.getInvites(player);
            		return true;
            	}
            }
            /**Invites End**/
            if(args[0].equalsIgnoreCase("create")){
            	if(args.length == 1){
            		PartyHandler.createParty(player, player.getName());;
            		return true;
            	}
            }
            if(args[0].equalsIgnoreCase("disband")){
            	if(args.length == 1){
            		PartyHandler.createParty(player, player.getName());;
            		return true;
            	}
            }
            return true;
        }

        // If the player (or console) uses our command correct, we can return true
        return false;
    }
}
