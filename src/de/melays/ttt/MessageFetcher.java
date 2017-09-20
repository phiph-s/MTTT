package de.melays.ttt;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	    if (customConfigurationFile == null) {
	    	customConfigurationFile = new File(plugin.getDataFolder(), "messages.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    java.io.InputStream defConfigStream = plugin.getResource("messages.yml");
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        customConfig.setDefaults(defConfig);
	    }
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