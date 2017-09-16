package de.melays.ttt.multispawn;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.melays.ttt.Arena;
import de.melays.ttt.main;
import net.md_5.bungee.api.ChatColor;

public class MultiSpawnCommand {
	
	main plugin;
	
	public MultiSpawnCommand(main m){
		plugin = m;
	}
	

	public boolean multispawnCommand(Player p , String[] args){
		
		if (args.length >= 3){
			if (args[2].equals("set")){
				if (args.length == 4){
					Arena a = plugin.m.get(args[1]);
					if (a != null){
						
						boolean setting = false;
						if (args[3].equals("on")){setting = true;}
						
						plugin.ms.setMultispawn(a, setting);
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] Saved.");
						return true;
					}
					else{
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] This Arena does not exist!");
						return true;
					}
				}
				else{
					p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> set <on/off>");
					return true;
				}
			}
			if (args[2].equals("add")){
				if (args.length == 4){
					Arena a = plugin.m.get(args[1]);
					if (a != null){
						plugin.ms.addSpawn(a, p.getLocation(), args[3]);
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] Saved.");
						return true;
					}
					else{
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] This Arena does not exist!");
						return true;
					}
				}
				else{
					p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> add <name>");
					return true;
				}
			}
			if (args[2].equals("remove")){
				if (args.length == 4){
					Arena a = plugin.m.get(args[1]);
					if (a != null){
						plugin.ms.removeSpawn(a, args[3]);
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] Tried to remove the spawn.");
						return true;
					}
					else{
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] This Arena does not exist!");
						return true;
					}
				}
				else{
					p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> remove <name>");
					return true;
				}
			}
			if (args[2].equals("list")){
				if (args.length == 3){
					Arena a = plugin.m.get(args[1]);
					if (a != null){
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] Spawns for Arena "+a.name+":");
						ArrayList<String> list = (ArrayList<String>) plugin.ms.getMultiSpawn().getStringList(a.name+".spawns");
						if (list != null){
							for (String s : list){
								p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] "+list.indexOf(s)+": "+s);
							}
						}
						else{
							p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] null");
						}
						return true;
					}
					else{
						p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] This Arena does not exist!");
						return true;
					}
				}
				else{
					p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> list");
					return true;
				}
			}

		}
			p.sendMessage(plugin.prefix + ChatColor.RED + " TTT Multispawn by MeLays / Schwalboss");
			p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> remove <name>");
			p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> list");
			p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> add <name>");
			p.sendMessage(plugin.prefix + ChatColor.RED + " [Multispawn] USAGE: /ttt multispawn <arena> set <on/off>");
			return true;
	}
	
}
