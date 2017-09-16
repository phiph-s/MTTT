package de.melays.ttt;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

public class MessageFetcher {
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	
	public MessageFetcher(main m){
		plugin = m;
	}
	
	public void reloadMessageFile() {
	    customConfigurationFile = new File(plugin.getDataFolder(), "messages.yml");

		// Schaut nach den Standardwerten in der jar
		if(!customConfigurationFile.exists()) {
			plugin.saveResource("messages.yml", false);
		}
		
		customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);
	}
	
	public FileConfiguration getMessageFetcher() {
	    if (customConfig == null) {
	    	reloadMessageFile();
	    }
	    return customConfig;
	}
	
	public void saveMessageFile() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konfiguration konnte nicht nach " + customConfigurationFile + " geschrieben werden.", ex);
	    }
	}
	
	public String getMessage (String id , boolean prefixreplace){
		String msg = getMessageFetcher().getString(id);
		if (msg != null){
			if (prefixreplace){
				msg =  msg.replace("%prefix%", getMessage("prefix" , false));
			}
			msg = msg.replace("[ae]", "ä");
			msg = msg.replace("[ue]", "ü");
			msg = msg.replace("[oe]", "ö");
			msg = msg.replace("[AE]", "Ä");
			msg = msg.replace("[UE]", "Ü");
			msg = msg.replace("[OE]", "Ö");
			return ChatColor.translateAlternateColorCodes('&',msg);
		}
		else{
			return "Your custom messages.yml doesn't contain this key ("+id+")";
		}
	}
}