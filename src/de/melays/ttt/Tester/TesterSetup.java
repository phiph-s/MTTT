package de.melays.ttt.Tester;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ttt.main;

public class TesterSetup implements Listener{
	
	main plugin;
	HashMap<Player , String> players = new HashMap<Player,String>();
	
	public TesterSetup (main m){
		plugin = m;
	}
	
	public void giveTools(Player p , String arena){
		ItemStack lamp = new ItemStack (Material.BLAZE_POWDER);
		ItemMeta metalamp = lamp.getItemMeta();
		metalamp.setDisplayName(ChatColor.YELLOW+"TESTER LAMP AND BUTTON TOOL");
		lamp.setItemMeta(metalamp);
		p.getInventory().addItem(lamp);
		if (!players.containsKey(p)){
			players.put(p,arena);
		}
		else{
			players.remove(p);
			players.put(p,arena);
		}
		p.sendMessage(ChatColor.GREEN+"Welcome to the tester creation setup!");
		p.sendMessage(ChatColor.GREEN+"I gave you Blazepowder, you will need it now.");
		p.sendMessage(ChatColor.GREEN+"Lets start at the beginning: To use a tester you need a room with a stone-button on the wall.");
		p.sendMessage(ChatColor.GREEN+"Place 2 redstone-lamps near the Tester which will start glowing when the player being tested is a traitor.");
		p.sendMessage(ChatColor.GREEN+"Lets start with the lamps. Take your lamp and button tool (Blaze Powder) and rightclick on the 2 lamps. Then press the Stone-Button with the Tool.");
		p.sendMessage(ChatColor.GREEN+"If you are finished set the location where the Players beeing tested should be teleported at. Thsi should be in the middle of the room you've build.");
		p.sendMessage(ChatColor.GREEN+"Do this by entering /ttt setlocation <arena> tester");
	}
	
	public void locationToConfig(Location l ,String path){
		plugin.getConfig().set(path+".x", l.getX());
		plugin.getConfig().set(path+".y", l.getY());
		plugin.getConfig().set(path+".z", l.getZ());
		plugin.getConfig().set(path+".world", l.getWorld().getName());
	}
	
	public Location locationFromConfig(String path){
		return new Location (Bukkit.getWorld(plugin.getConfig().getString(path+".world")), plugin.getConfig().getDouble(path+".x"), plugin.getConfig().getDouble(path+".y"),plugin.getConfig().getDouble(path+".z"));
	}
	
	
	@EventHandler
	public void onInteract (PlayerInteractEvent e){
		if (players.containsKey(e.getPlayer())){
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				if (e.getPlayer().getItemInHand().getType() == Material.BLAZE_POWDER){
					if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.YELLOW+"TESTER LAMP AND BUTTON TOOL")){
						if (e.getClickedBlock().getType() == Material.REDSTONE_LAMP_OFF){
							if (plugin.getConfig().get(players.get(e.getPlayer())+".tester.lamp.1.x")==null){
								locationToConfig(e.getClickedBlock().getLocation() , players.get(e.getPlayer())+".tester.lamp.1");
								plugin.saveConfig();
								e.getPlayer().sendMessage(ChatColor.GREEN+"Saved Lamp 1");
							}
							else if (plugin.getConfig().get(players.get(e.getPlayer())+".tester.lamp.2.x")==null){
								locationToConfig(e.getClickedBlock().getLocation() , players.get(e.getPlayer())+".tester.lamp.2");
								plugin.saveConfig();
								e.getPlayer().sendMessage(ChatColor.GREEN+"Saved Lamp 2");
							}
							else{
								e.getPlayer().sendMessage(ChatColor.RED+"You already set both lamps. You can delete the whole tester with /ttt deletetester <arena> or remove the lamp entrys in the config.yml and restart");
							}
						}
						else if (e.getClickedBlock().getType() == Material.STONE_BUTTON){
								locationToConfig(e.getClickedBlock().getLocation() , players.get(e.getPlayer())+".tester.button");
								plugin.saveConfig();
								e.getPlayer().sendMessage(ChatColor.GREEN+"Saved Button");
						}
					}
				}
			}
		}
	}
	
	public boolean checkTesterSetup(String arena){
		if (plugin.getConfig().get(arena+".tester.x") == null){
			return false;
		}
		if (plugin.getConfig().get(arena+".tester.lamp.1.x") == null){
			return false;
		}
		if (plugin.getConfig().get(arena+".tester.lamp.2.x") == null){
			return false;
		}
		if (plugin.getConfig().get(arena+".tester.button") == null){
			return false;
		}
		return true;
	}
	
}
