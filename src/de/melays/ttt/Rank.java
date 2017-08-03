package de.melays.ttt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Rank {
	
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	
	public Rank (main m){
		plugin = m;
		load();
	}
	
	public void reloadRanks() {
	    if (customConfigurationFile == null) {
	    customConfigurationFile = new File(plugin.getDataFolder(), "ranks.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    // Schaut nach den Standardwerten in der jar
	    java.io.InputStream defConfigStream = plugin.getResource("ranks.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getRanks() {
	    if (customConfig == null) {
	    	reloadRanks();
	    }
	    return customConfig;
	}
	
	public void saveRanks() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konfiguration konnte nicht nach " + customConfigurationFile + " geschrieben werden.", ex);
	    }
	}
	
	HashMap<Integer , String> keylist = new HashMap<Integer , String>();
	
	public void load (){
		keylist = new HashMap<Integer , String>();
		ArrayList<String> keys = new ArrayList<String> (getRanks().getConfigurationSection("ranks").getKeys(false));
		Collections.sort(keys);
		for (String s : keys){
			
			try{
				keylist.put(Integer.parseInt(s), getRanks().getString("ranks."+s+".prefix"));
			}
			catch(Exception ex){
				
			}
		}
	}
	
	public String getRank (int karma){
		int last = 0;
		int lastindex = 0;
		ArrayList<Integer> templ = new ArrayList<Integer> (keylist.keySet());
		Collections.sort(templ);
		for (int i : templ){
			if (i > karma){
				return ChatColor.translateAlternateColorCodes('&',keylist.get(last)+"");
			}
			last = i;
		}
		return "";
		
	}
	
	public String nextRank (int karma){
		int last = 0;
		int lastindex = 0;
		ArrayList<Integer> templ = new ArrayList<Integer> (keylist.keySet());
		Collections.sort(templ);
		for (int i : templ){
			if (i > karma){
				return ChatColor.translateAlternateColorCodes('&',keylist.get(i)+"");
			}
			last = i;
		}
		return "";
		
	}
}
