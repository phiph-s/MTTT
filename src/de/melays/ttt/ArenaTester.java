package de.melays.ttt;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.melays.Shop.ShopItem;

public class ArenaTester {
	
	main plugin;
	Arena a;
	
	public ArenaTester (main m , Arena a){
		plugin = m;
		this.a = a;
	}
	
	public boolean enabled = false;
	
	public ArrayList<Location> buttons = new ArrayList<Location>();
	ArrayList<Location> diamondblocks = new ArrayList<Location>();
	ArrayList<Location> lamps = new ArrayList<Location>();
	
	public boolean isButton(Location l){
		for (Location loc : buttons){
			if (loc.getBlock().getLocation().equals(l.getBlock().getLocation())){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void enableLamps(String role){
		Material m = Material.getMaterial(plugin.getConfig().getString("tester."+role+"_lamp.material"));
		byte data = (byte) plugin.getConfig().getInt("tester."+role+"_lamp.data");
		for (Location loc : lamps){
			loc.getBlock().setType(m);
			loc.getBlock().setData(data);
		}
	}
	
	public void disableLamps(){
		for (Location loc : lamps){
			loc.getBlock().setType(Material.REDSTONE_LAMP_OFF);
		}
	}
	
	public void enableRedstone(){
		for (Location loc : diamondblocks){
			loc.getBlock().setType(Material.REDSTONE_BLOCK);
		}
	}
	
	public void disableRedstone(){
		for (Location loc : diamondblocks){
			loc.getBlock().setType(Material.DIAMOND_BLOCK);
		}
	}
	
	public boolean inTester(Location loc){
		if (smaller.getX() <= loc.getX() && smaller.getY() <= loc.getY() && smaller.getZ() <= loc.getZ()){
			if (bigger.getX() >= loc.getX() && bigger.getY() >= loc.getY() && bigger.getZ() >= loc.getZ()){
				return true;
			}
		}
		return false;
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
	
	Location bigger;
	Location smaller;
	
	Location outer;
	Location inner;
	
	public void load(){
		Set<String> blocks;
		try{
			blocks = plugin.getConfig().getConfigurationSection(a.name + ".tester_data.blocks").getKeys(false);
			
			for (String s : blocks){
				Location loc = locationFromConfig(a.name + ".tester_data.blocks."+s);
				if (loc.getBlock().getType().equals(Material.STONE_BUTTON)){
					buttons.add(loc.getBlock().getLocation());
				}
				else if (loc.getBlock().getType().equals(Material.REDSTONE_LAMP_OFF) || loc.getBlock().getType().equals(Material.GLOWSTONE)){
					lamps.add(loc.getBlock().getLocation());
				}
				else if (loc.getBlock().getType().equals(Material.DIAMOND_BLOCK) || loc.getBlock().getType().equals(Material.REDSTONE_BLOCK)){
					diamondblocks.add(loc.getBlock().getLocation());
				}
			}
			
			smaller = locationFromConfig(a.name + ".tester_data.corner_small");
			bigger = locationFromConfig(a.name + ".tester_data.corner_big");
			
			outer = getLocation(a.name + ".tester.outer");
			inner = getLocation(a.name + ".tester.inner");
			
			if (bigger == null || smaller == null || outer == null || inner == null){
				enabled = false;
				return;
			}
			
			enabled = true;
		}catch(Exception ex){
			enabled = false;
		}
	}
	
	Player testing = null;
	
	public boolean destroyed = false;
	
	public Location getLocation (String path){
		double x = plugin.getConfig().getDouble(path+".x");
		double y =  plugin.getConfig().getDouble(path+".y");
		double z =  plugin.getConfig().getDouble(path+".z");
		float yaw = (float) plugin.getConfig().getDouble(path+".yaw");
		float pitch = (float) plugin.getConfig().getDouble(path+".pitch");
		String world =  plugin.getConfig().getString(path+".world");
		return new Location (Bukkit.getWorld(world) , x ,y  ,z , yaw , pitch);
	}
	
	public void testPlayer (Player p){
		if (!a.gamestate.equals("ingame")){
			plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
			return;
		}
		if (destroyed){
			p.sendMessage(plugin.mf.getMessage("testerdisabled", true));
			plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
			return;
		}
		if (testing != null){
			plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
			return;
		}
		if (a.startplayers <= plugin.getConfig().getInt("minplayerstotest")){
			plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
			return;
		}
		if (a.tester){
			plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
			return;
		}
		p.teleport(inner);
		for (Player pt : a.getPlayerList()){
			if (pt != p){
				if (this.inTester(pt.getLocation())){
					pt.teleport(this.outer);
				}
			}
		}
		a.sendRadiusMessage(p, plugin.mf.getMessage("enteredtester", true).replace("%player%", p.getName()));
		this.enableRedstone();
		testing = p;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			public void run() {
				testing = null;
				disableRedstone();
				if (a.getPlayerList().contains(p)){
					if (a.traitors.contains(p)){
						 if (!p.getInventory().contains(new ShopItem().getSpoofer(false))){
								enableLamps("traitor");
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										disableLamps();
									}
								}, 50L);
						 }
						 else{
							 p.getInventory().remove(new ShopItem().getSpoofer(false));
							 p.sendMessage(plugin.mf.getMessage("spoofer", true));
						 }
					}
					else {
						enableLamps("innocent");
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								disableLamps();
							}
						}, 50L);
					}
				}
			}
			
		}, 100L);
	}
	
}
