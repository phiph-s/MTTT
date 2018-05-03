package de.melays.ttt.Tester;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

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

public class NewTesterSetup implements Listener{
	
	main plugin;
	HashMap<Player , String> players = new HashMap<Player,String>();
	
	public NewTesterSetup (main m){
		plugin = m;
	}
	
	public void giveTools(Player p , String arena){
		
		ItemStack lamp = new ItemStack (Material.WOOD_HOE);
		ItemMeta metalamp = lamp.getItemMeta();
		metalamp.setDisplayName(plugin.prefix + ChatColor.YELLOW+"Tester Tool");
		lamp.setItemMeta(metalamp);
		
		ItemStack selec = new ItemStack (Material.WOOD_HOE);
		ItemMeta selecm = lamp.getItemMeta();
		selecm.setDisplayName(plugin.prefix + ChatColor.RED+"Area Selection Tool");
		selec.setItemMeta(selecm);
		p.getInventory().addItem(selec);
		
		p.getInventory().addItem(lamp);
		if (!players.containsKey(p)){
			players.put(p,arena);
		}
		else{
			players.remove(p);
			players.put(p,arena);
		}
		p.sendMessage(plugin.prefix + ChatColor.YELLOW + "Tester Tool" + ChatColor.GRAY + " Instructions:");
		p.sendMessage(plugin.prefix + "Rightclick every Block you want your tester to use.");
		p.sendMessage(plugin.prefix + ChatColor.RED + "Blocks: ----------------");
		p.sendMessage(plugin.prefix + "REDSTONE_LAMP --> Will light up if the testing Player is a Traitor");
		p.sendMessage(plugin.prefix + "STONE_BUTTON --> Used to use the tester");
		p.sendMessage(plugin.prefix + "DIAMOND_BLOCK --> Will change to REDSTONE_BLOCK if the Tester is in use.");
		p.sendMessage(plugin.prefix + ChatColor.RED + "Leftclick will remove the Block!");
		p.sendMessage(plugin.prefix + ChatColor.GREEN + "Area Selection Tool" + ChatColor.GRAY + " Instructions:");
		p.sendMessage(plugin.prefix + "Rightclick and Leftclick the borders of the tester.");
		p.sendMessage(plugin.prefix + "No Players will be able to enter this area while a player is testing.");
		p.sendMessage(plugin.prefix + "At the end use /ttt setlocation to set the location tester.inner and tester.outer!");
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
	
	HashMap<Player , LocationMarker> markers = new HashMap<Player , LocationMarker>();
	
	@EventHandler
	public void interactEvent (PlayerInteractEvent e){
		if (e.isCancelled()) return;
		try{
			if (e.getPlayer().hasPermission("ttt.setup")){
				if (!e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(plugin.prefix + ChatColor.RED+"Area Selection Tool")){
					return;
				}
				if (e.getPlayer().getItemInHand().getType() == Material.WOOD_HOE && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
					
					if (!markers.containsKey(e.getPlayer())){
						markers.put(e.getPlayer(), new LocationMarker(e.getPlayer()));
					}
					
					if (e.getAction() == Action.LEFT_CLICK_BLOCK){
						markers.get(e.getPlayer()).setLeft(e.getClickedBlock().getLocation());
						e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Left Location set succesfully!");
					}
					else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
						markers.get(e.getPlayer()).setRight(e.getClickedBlock().getLocation());
						e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Right Location set succesfully!");
					}
					e.setCancelled(true);
					if (markers.get(e.getPlayer()).complete()){
						generatePositions(markers.get(e.getPlayer()).locl , markers.get(e.getPlayer()).locr , e.getPlayer());
						markers.put(e.getPlayer(), new LocationMarker(e.getPlayer()));
					}
				}
			}
		}catch(Exception ex){return;}
	}
	
	public void generatePositions (Location l1 , Location l2 , Player p){
		//Pos1 needs smaller X and smaller Y
		double xpos1;
		double ypos1;
		double zpos1;
		double xpos2;
		double ypos2;
		double zpos2;
		if (l1.getX() <= l2.getX()){	
			xpos1 = l1.getX();
			xpos2 = l2.getX();	
		}
		else{	
			xpos1 = l2.getX();
			xpos2 = l1.getX();	
		}
		if (l1.getY() <= l2.getY()){	
			ypos1 = l1.getY();
			ypos2 = l2.getY();	
		}
		else{
			ypos1 = l2.getY();
			ypos2 = l1.getY();
		}
		if (l1.getZ() <= l2.getZ()){
			
			zpos1 = l1.getZ();
			zpos2 = l2.getZ();
		}
		else{	
			zpos1 = l2.getZ();
			zpos2 = l1.getZ();	
		}
		locationToConfig (new Location (l1.getWorld() , xpos1 , ypos1 , zpos1) , players.get(p) + ".tester_data.corner_small");
		locationToConfig (new Location (l2.getWorld() , xpos2 , ypos2 , zpos2) , players.get(p) + ".tester_data.corner_big");
		plugin.saveConfig();
		p.sendMessage(plugin.prefix + "The Corners of the Tester have been calculated and saved.");
	}
	
	public int clearBlocks(String arena , Location loc){
		Set<String> blocks;
		try{
			blocks = plugin.getConfig().getConfigurationSection(arena+".tester_data.blocks").getKeys(false);
		}catch(Exception ex){return 0;}
		if (blocks == null)return 0;
		int removed = 0;
		for (String s : blocks){
			Location loct = this.locationFromConfig(arena+".tester_data.blocks."+s);
			if (loct.getBlock().getLocation().equals(loc.getBlock().getLocation())){
				plugin.getConfig().set(arena+".tester_data.blocks."+s, null);
				removed ++;
			}
		}
		return removed;
	}
	
	@EventHandler
	public void onInteract (PlayerInteractEvent e){
		if (e.isCancelled()) return;
		try{
			Player p= e.getPlayer();
			if (players.containsKey(e.getPlayer())){
				if (e.getPlayer().getItemInHand().getType() == Material.WOOD_HOE){
						if (!e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(plugin.prefix + ChatColor.YELLOW+"Tester Tool"))return;
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
							p.sendMessage(plugin.prefix + "Removed " + clearBlocks(players.get(p) , e.getClickedBlock().getLocation()) + " Blocks from the config!");
							this.locationToConfig(e.getClickedBlock().getLocation(), players.get(p)+".tester_data.blocks."+UUID.randomUUID());
							p.sendMessage(plugin.prefix + "Saved the " + e.getClickedBlock().getType() + " to the config.yml");
						}
						else{
							p.sendMessage(plugin.prefix + "Removed " + clearBlocks(players.get(p) , e.getClickedBlock().getLocation()) + " Blocks from the config!");
						}
						plugin.saveConfig();
						e.setCancelled(true);
				}
			}
		}catch(Exception ex){ex.printStackTrace();}
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
