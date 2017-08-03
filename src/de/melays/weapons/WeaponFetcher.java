package de.melays.weapons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.util.Seekable.InputStream;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import de.melays.ttt.main;

public class WeaponFetcher {
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	
	public HashMap<String , Boolean> csuses = new HashMap<String , Boolean>();
	
	public WeaponFetcher(main m){
		plugin = m;
	}
	
	public void reloadWeaponFile() {
	    if (customConfigurationFile == null) {
	    customConfigurationFile = new File(plugin.getDataFolder(), "weapons.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    // Schaut nach den Standardwerten in der jar
	    java.io.InputStream defConfigStream = plugin.getResource("weapons.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getWeaponFetcher() {
	    if (customConfig == null) {
	    	reloadWeaponFile();
	    }
	    return customConfig;
	}
	
	public void saveWeaponFile() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konfiguration konnte nicht nach " + customConfigurationFile + " geschrieben werden.", ex);
	    }
	}
	
	ArrayList<Weapon> weaponschest = new ArrayList<Weapon>();
	ArrayList<Weapon> weaponsenderchest = new ArrayList<Weapon>();
	ArrayList<Weapon> weaponslegend = new ArrayList<Weapon>();
	public void loadWeapons (){
		weaponschest = new ArrayList<Weapon>();
		weaponsenderchest = new ArrayList<Weapon>();
		weaponslegend = new ArrayList<Weapon>();
		ArrayList<String> weaponschestl = (ArrayList<String>) getWeaponFetcher().getStringList("chest");
		ArrayList<String> weaponsenderchestl = (ArrayList<String>) getWeaponFetcher().getStringList("enderchest");
		ArrayList<String> weaponslegendl = (ArrayList<String>) getWeaponFetcher().getStringList("legendary");
		for (String s : weaponschestl){
			Weapon w = new Weapon(this , s);
			w.loadWeapon();
			weaponschest.add(w);
			System.out.println("[MTTT] Loaded weapon "+w.name);
		}
		for (String s : weaponsenderchestl){
			Weapon w = new Weapon(this , s);
			w.loadWeapon();
			weaponsenderchest.add(w);
			System.out.println("[MTTT] Loaded weapon (Enderchest) "+w.name);
		}
		for (String s : weaponslegendl){
			Weapon w = new Weapon(this , s);
			w.loadWeapon();
			weaponslegend.add(w);
			System.out.println("[MTTT] Loaded weapon (Legendary) "+w.name);
		}
	}
	
	public boolean giveRandomChestItem (Player p , String chest){
		
		ArrayList<Weapon> chances = new ArrayList<Weapon>();
		
		boolean enderchest = false;
		if (chest.equals("ec")){
			enderchest = true;
		}
		boolean legend = false;
		if (chest.equals("legend")){
			legend = true;
		}
		
		for (Weapon w : weaponschest){
			for (int i = 0 ; i < w.chance ; i++){
				chances.add(w);
			}
		}
		
		if (enderchest){
			chances = new ArrayList<Weapon>();
			for (Weapon w : weaponsenderchest){
				for (int i = 0 ; i < w.chance ; i++){
					chances.add(w);
				}
			}
		}
		
		if (legend){
			chances = new ArrayList<Weapon>();
			for (Weapon w : weaponslegend){
				for (int i = 0 ; i < w.chance ; i++){
					chances.add(w);
				}
			}
		}
		
		ItemStack giveback = null;
		
		try{
			for (ItemStack i : p.getInventory().getContents()){
				if (i != null){
					
					if (i.getItemMeta() != null){
						
						if (i.getItemMeta().getDisplayName().equals(plugin.getConfig().getString("shopopener"))){
							
							giveback = i.clone();
							i.setType(Material.AIR);
							
						}
						
					}
					
				}
			}
		}
		catch(Exception ex){
			
		}
		
		
		Collections.shuffle(chances);
		Collections.shuffle(chances);
		
		Weapon random = chances.get(0);
		
		if (random.crackshot){
			
			if (plugin.searchCSWeapon(p.getInventory(), random.crackshotname) != null){
				plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
				return false;
			}
			random.giveCrackShot(p);
			
			if (giveback != null){
				p.getInventory().addItem(giveback);
			}
			
			return true;
		}
		
		ArrayList<ItemStack> stacks = random.getItemStacks();
		
		int classid = random.classid;
		int priority = random.priority;
		
		boolean give = true;
		boolean remchest = true;
		
//		for (ItemStack s : p.getInventory().getContents()){
//			Bukkit.broadcastMessage(p.getInventory().getContents()+"");
//		}
		
		ArrayList<ItemStack> ItemList = new ArrayList<ItemStack>();
		for (ItemStack i : p.getInventory().getContents()){
			ItemList.add(i);
		}
		for (ItemStack i : p.getInventory().getArmorContents()){
			ItemList.add(i);
		}
		
	    for (int i = 0; i < p.getInventory().getSize(); i++) {
	        if (ItemList.get(i) != null) {
	        	ItemStack s = ItemList.get(i);
				if (getClass(s) == classid){
					if (getPriority(s) > priority){
						give = false;
						remchest = false;
					}
					else if (getPriority(s) <= priority && !(priority == 0)){
						if (getPriority(s) == priority){
							plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
							remchest = false;
						}
						if (!s.getItemMeta().getDisplayName().equals(plugin.getConfig().getString("shopopener"))){
							p.getInventory().remove(s);
						}
					}
				}
	        }
	    }
		
		if (give){
			for (ItemStack is : stacks){
				if (removeBows() && is.getType() == Material.BOW && p.getInventory().contains(Material.BOW)){
					
				}
				else{
					p.getInventory().addItem(is);
				}
			}
		}
		else{
			plugin.sd.playSound(p , "CLICK", "BLOCK_LEVER_CLICK" );
			remchest = false;
		}
		
		if (giveback != null){
			p.getInventory().addItem(giveback);
		}
		
		return remchest;
	}
	
	public boolean removeBows(){
		return getWeaponFetcher().getBoolean("removemultiplebows");
	}
	
	public int getPriority (ItemStack s){
		if (s != null){
			ArrayList<String> lore = new ArrayList<String>();
			lore = (ArrayList<String>) s.getItemMeta().getLore();
			if (lore != null){
				if (lore.size() == 1){
					String[] data = lore.get(0).split(":");
					if (data.length == 2){
						return Integer.parseInt(data[0]);
					}
				}
			}
		}
		return -1;
	}
	
	public int getClass (ItemStack s){
		if (s != null){
			ArrayList<String> lore = new ArrayList<String>();
			lore = (ArrayList<String>) s.getItemMeta().getLore();
			if (lore != null){
				if (lore.size() == 1){
					String[] data = lore.get(0).split(":");
					if (data.length == 2){
						return Integer.parseInt(data[1]);
					}
				}
			}
		}
		return -1;
	}
}