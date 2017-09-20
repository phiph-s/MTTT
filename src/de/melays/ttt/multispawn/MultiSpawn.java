package de.melays.ttt.multispawn;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.ttt.Arena;
import de.melays.ttt.main;

public class MultiSpawn {
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	
	public MultiSpawn(main m){
		plugin = m;
	}
	
	public void reloadMultiSpawnFile() {
	    if (customConfigurationFile == null) {
	    	customConfigurationFile = new File(plugin.getDataFolder(), "multispawn.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    java.io.InputStream defConfigStream = plugin.getResource("multispawn.yml");
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getMultiSpawn() {
	    if (customConfig == null) {
	    	reloadMultiSpawnFile();
	    }
	    return customConfig;
	}
	
	public void saveMultiSpawn() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konfiguration konnte nicht nach " + customConfigurationFile + " geschrieben werden.", ex);
	    }
	}
	
	public boolean checkReady(Arena a){
		if (getMultiSpawn().getBoolean(a.name+".enabled")){
			if (getMultiSpawn().getStringList(a.name+".spawns") != null){
				if (getMultiSpawn().getStringList(a.name+".spawns").size() >= 1){
					return true;
				}
			}
			System.out.println("[MTTT] [Multispawn] Multispawn for Arena "+a.name +" disabled. No spawns set");
			return false;
		}
		//System.out.println("[MTTT] [Multispawn] Multispawn for Arena "+a.name +" disabled.");
		return false;
	}
	
	public void setMultispawn (Arena a , boolean setting){
		getMultiSpawn().set(a.name+".enabled", setting);
		saveMultiSpawn();
	}
	
	public void addSpawn(Arena a , Location loc , String name){
		ArrayList<String> list = (ArrayList<String>) getMultiSpawn().getStringList(a.name+".spawns");
		if (list == null){
			list = new ArrayList<String>();
		}
		if (!list.contains(name)){
			list.add(name);
		}
		getMultiSpawn().set(a.name+".spawns" , list);
		getMultiSpawn().set(a.name+"."+name+".x", loc.getX());
		getMultiSpawn().set(a.name+"."+name+".y", loc.getY());
		getMultiSpawn().set(a.name+"."+name+".z", loc.getZ());
		getMultiSpawn().set(a.name+"."+name+".pitch", loc.getPitch());
		getMultiSpawn().set(a.name+"."+name+".yaw", loc.getYaw());
		getMultiSpawn().set(a.name+"."+name+".world", loc.getWorld().getName());
		saveMultiSpawn();
	}
	
	public void removeSpawn (Arena a , String name){
		getMultiSpawn().set(a.name+"."+name , null);
		ArrayList<String> list = (ArrayList<String>) getMultiSpawn().getStringList(a.name+".spawns");
		if (list == null){
			list = new ArrayList<String>();
		}
		if (list.contains(name)){
			list.remove(name);
		}
		getMultiSpawn().set(a.name+".spawns" , list);
		saveMultiSpawn();
	}
	
	public Location getSpawn (Arena a , String name){
		if (getMultiSpawn().get(a.name+"."+name+".x") != null){
			double x = getMultiSpawn().getDouble(a.name+"."+name+".x");
			double y = getMultiSpawn().getDouble(a.name+"."+name+".y");
			double z = getMultiSpawn().getDouble(a.name+"."+name+".z");
			double pitch = getMultiSpawn().getDouble(a.name+"."+name+".pitch");
			double yaw = getMultiSpawn().getDouble(a.name+"."+name+".yaw");
			String world = getMultiSpawn().getString(a.name+"."+name+".world");
			
			return new Location (Bukkit.getWorld(world) , x , y+1.5 , z , (short)yaw , (short)pitch);
		}
		else{
			return null;
		}
	}
	
	public Location randomSpawn(Arena a){
		ArrayList<String> list = (ArrayList<String>) getMultiSpawn().getStringList(a.name+".spawns");
		if (list != null){
			Collections.shuffle(list);
			return getSpawn(a , list.get(0));
		}
		return null;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}