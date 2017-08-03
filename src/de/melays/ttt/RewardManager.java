package de.melays.ttt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardManager {
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	
	public RewardManager(main m){
		plugin = m;
	}
	
	public void reloadRewardFile() {
	    if (customConfigurationFile == null) {
	    customConfigurationFile = new File(plugin.getDataFolder(), "rewards.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    // Schaut nach den Standardwerten in der jar
	    java.io.InputStream defConfigStream = plugin.getResource("rewards.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getRewardFile() {
	    if (customConfig == null) {
	    	reloadRewardFile();
	    }
	    return customConfig;
	}
	
	public void saveRewardFile() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konfiguration konnte nicht nach " + customConfigurationFile + " geschrieben werden.", ex);
	    }
	}
	ArrayList<Reward> start = new ArrayList<Reward>();
	ArrayList<Reward> end = new ArrayList<Reward>();
	ArrayList<Reward> win = new ArrayList<Reward>();
	public void mountRewards(){
		
		try{
			Set<String> runstart = getRewardFile().getConfigurationSection("start").getKeys(false);
			if (runstart != null){
				for (String s : runstart){
					
					int chance = getRewardFile().getInt("start."+s+".chance");
					String sender = getRewardFile().getString("start."+s+".sender");
					String command = getRewardFile().getString("start."+s+".command");
					
					if (sender != null && command != null && chance != 0){
						if (sender.equalsIgnoreCase("CONSOLE") || sender.equalsIgnoreCase("PLAYER")){
							
							start.add(new Reward(chance , sender , command));
							System.out.println("[MTTT] Loaded reward(start) with command = " + command);
							
						}
					}
					
				}
			}
		}
		catch(Exception ex){
			System.out.println("[MTTT] No start Rewards were loaded.");
		}
		
		try{
			Set<String> runend = getRewardFile().getConfigurationSection("end").getKeys(false);
			if (runend != null){
				for (String s : runend){
					
					int chance = getRewardFile().getInt("end."+s+".chance");
					String sender = getRewardFile().getString("end."+s+".sender");
					String command = getRewardFile().getString("end."+s+".command");
					
					if (sender != null && command != null && chance != 0){
						if (sender.equalsIgnoreCase("CONSOLE") || sender.equalsIgnoreCase("PLAYER")){
							
							end.add(new Reward(chance , sender , command));
							System.out.println("[MTTT] Loaded reward(end) with command = " + command);
							
						}
					}
					
				}
			}
		}
		catch(Exception ex){
			System.out.println("[MTTT] No end Rewards were loaded.");
		}
		
		try{
			
			Set<String> runwin = getRewardFile().getConfigurationSection("win").getKeys(false);
			if (runwin != null){
				for (String s : runwin){
					
					int chance = getRewardFile().getInt("win."+s+".chance");
					String sender = getRewardFile().getString("win."+s+".sender");
					String command = getRewardFile().getString("win."+s+".command");
					
					if (sender != null && command != null && chance != 0){
						if (sender.equalsIgnoreCase("CONSOLE") || sender.equalsIgnoreCase("PLAYER")){
							
							win.add(new Reward(chance , sender , command));
							System.out.println("[MTTT] Loaded reward(win) with command = " + command);
							
						}
					}
					
				}
			}
		}
		catch(Exception ex){
			System.out.println("[MTTT] No win Rewards were loaded.");
		}
	}
	
	public void reward (Player p , String type){
		
		if (type.equalsIgnoreCase("start")){
			for (Reward r : start){
				r.reward(p);
			}
		}
		
		else if (type.equalsIgnoreCase("end")){
			for (Reward r : end){
				r.reward(p);
			}
		}
		
		else if (type.equalsIgnoreCase("win")){
			for (Reward r : win){
				r.reward(p);
			}
		}
		
	}
}
