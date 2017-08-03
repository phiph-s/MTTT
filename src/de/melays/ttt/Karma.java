package de.melays.ttt;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.util.Seekable.InputStream;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.mysql.mysql.MySQL;

import de.melays.statsAPI.StatsAPI;

public class Karma {
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	public String mode;
	public Connection c = null;
	public MySQL MySQL = null;
	
	Object statsapi;
	
	public Karma(main m , String mode){
		plugin = m;
		this.mode = mode;
		if (mode.equals("mysql")){
			System.out.println("[MTTT] [MySQL] Starting in MySQL mode ...");
		    String host = plugin.getConfig().getString("mysql.host");
		    String port = plugin.getConfig().getString("mysql.port");
		    String user = plugin.getConfig().getString("mysql.user");
		    String db = plugin.getConfig().getString("mysql.database");
		    String password = plugin.getConfig().getString("mysql.password");
		    
		    System.out.println("[MTTT] [MySQL] Trying to connect ...");
		    try{
			    MySQL = new MySQL(host, port, db, user, password);
			    c = MySQL.openConnection();
				Statement statement = c.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS karma(PlayerID TEXT,karma INT,passes INT)");
			    System.out.println("[MTTT] [MySQL] Connected");
		    }
		    catch (Exception e){
		    	e.printStackTrace();
		    	this.mode = "yml";
		    	System.out.println("[MTTT] [MySQL] Could not connect to the MySQL-Database. Continue using YML-Mode");
		    }
		    
		}
		else if (mode.equals("statsapi")){
			System.out.println("[MTTT] Starting in StatsAPI mode ...");
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				System.out.println("[MTTT] StatsAPI is currently running without a Database connection!");
			}
		}
		else{
			this.mode = "yml";
			System.out.println("[MTTT] Starting in YAML mode ...");
		}
	}
	
	public void reloadKarmaConfig() {
	    if (customConfigurationFile == null) {
	    customConfigurationFile = new File(plugin.getDataFolder(), "karma.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    // Schaut nach den Standardwerten in der jar
	    java.io.InputStream defConfigStream = plugin.getResource("karma.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getKarmaConfig() {
	    if (customConfig == null) {
	    	reloadKarmaConfig();
	    }
	    return customConfig;
	}
	
	public void saveKarma() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konfiguration konnte nicht nach " + customConfigurationFile + " geschrieben werden.", ex);
	    }
	}
	public int getKarma(OfflinePlayer p){
		if (mode.equals("yml")){
			if (getKarmaConfig().get(p.getUniqueId()+".karma") == null){
				getKarmaConfig().set(p.getUniqueId()+".karma", 100);
				saveKarma();
			}
			return (int) getKarmaConfig().getInt(p.getUniqueId()+".karma");
		}
		else if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return 100;
			}
			int karma = ((StatsAPI) statsapi).hookChannel(plugin, "mttt").getKey(p.getUniqueId(), "karma");
			if (karma == 0){
				return 100;
			}
			else{
				return karma;
			}
		}
		else{
			try {
				Statement statement = c.createStatement();
				ResultSet res = statement.executeQuery("SELECT * FROM karma WHERE PlayerID = '" + p.getUniqueId() + "';");
				if (res.next()){
					if(res.getString("PlayerID") == null) {
						return 100;
					} else {
						return res.getInt("karma");
					}
				}
				return 100;
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MTTT] [MySQL] Could not get Karma from Database. Returning 100.");
				return 100;
			}
		}
		
	}
	public int getKarma(Player p){
		return getKarma((OfflinePlayer)p);
	}
	public void setKarma(Player p , int Karma){
		setKarma ((OfflinePlayer)p , Karma);
	}
	public void setKarma(OfflinePlayer p , int Karma){
		if (mode.equals("yml")){
			getKarmaConfig().set(p.getUniqueId()+".karma", Karma);
			saveKarma();
		}
		else if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return;
			}
			((StatsAPI) statsapi).hookChannel(plugin, "mttt").setKey(p.getUniqueId(), "karma", Karma);
		}
		else{
			try {
				Statement statement = c.createStatement();
				int passes = getPasses(p.getUniqueId());
				statement.executeUpdate("DELETE FROM karma WHERE PlayerID = '"+p.getUniqueId().toString()+"';");
				statement.executeUpdate("INSERT INTO karma (`PlayerID`, `karma` , `passes`) VALUES ('" + p.getUniqueId().toString() + "', '"+ Karma +"' , '"+ passes +"');");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MTTT] [MySQL] Could not save Karma to Database. Please fix Config.");
			}
		}
	}
	public void addKarma(Player p , int Karma){
		setKarma(p , getKarma(p)+Karma);
	}
	public void removeKarma(Player p , int Karma){
		setKarma(p , getKarma(p)-Karma);
	}
	
	public int getPasses (UUID uuid){
		if (mode.equals("yml")){
			if (getKarmaConfig().get(uuid+".passes") == null){
				getKarmaConfig().set(uuid+".passes", 0);
				saveKarma();
			}
			return (int) getKarmaConfig().getInt(uuid+".passes");
		}
		else if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return 0;
			}
			return ((StatsAPI) statsapi).hookChannel(plugin, "mttt").getKey(uuid, "passes");
		}
		else{
			try {
				Statement statement = c.createStatement();
				ResultSet res = statement.executeQuery("SELECT * FROM karma WHERE PlayerID = '" + uuid + "';");
				if (res.next()){
					if(res.getString("PlayerID") == null) {
						return 100;
					} else {
						return res.getInt("passes");
					}
				}
				return 0;
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MTTT] [MySQL] Could not get Passes from Database. Returning 100.");
				return 0;
			}
		}
	}
	public void setPasses(OfflinePlayer p , int passes){
		if (mode.equals("yml")){
			getKarmaConfig().set(p.getUniqueId()+".passes", passes);
			saveKarma();
		}
		else if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return;
			}
			((StatsAPI) statsapi).hookChannel(plugin, "mttt").setKey(p.getUniqueId(), "passes", passes);
		}
		else{
			try {
				Statement statement = c.createStatement();
				int karma = getKarma(p);
				statement.executeUpdate("DELETE FROM karma WHERE PlayerID = '"+p.getUniqueId().toString()+"';");
				statement.executeUpdate("INSERT INTO karma (`PlayerID`, `karma` , `passes`) VALUES ('" + p.getUniqueId().toString() + "', '"+ karma +"' , '"+ passes +"');");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MTTT] [MySQL] Could not save Passes to Database. Please fix Config.");
			}
		}
	}
	public void setPasses(Player p , int passes){
		setPasses ((OfflinePlayer) p , passes);
	}
	public void addPasses(Player p , int passes){
		setPasses(p , getPasses(p.getUniqueId())+passes);
	}
	public void addPasses(OfflinePlayer p , int passes){
		setPasses(p , getPasses(p.getUniqueId())+passes);
	}
	public void removePasses(Player p , int passes){
		setPasses(p , getPasses(p.getUniqueId())-passes);
	}
	public void addAdvancedStat (UUID u , String key , int i){
		if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return;
			}
			((StatsAPI) statsapi).hookChannel(plugin, "mttt").addToKey(u, key, i);
		}
	}
	
	public String getStatsAPIValue (UUID u , String key){
		if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return "MySQL not connected!";
			}
			String r = ((StatsAPI) statsapi).hookChannel(plugin, "mttt").getStringKey(u, key);
			if (r == null){
				return "0";
			}
			return r;
		}
		return ChatColor.RED+ "StatsAPI missing!";
	}
	
	public HashMap<String,String> getValueSet (UUID u){
		if (mode.equals("statsapi")){
			statsapi = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
			if (((StatsAPI) statsapi).isDummy()){
				return null;
			}
			return ((StatsAPI) statsapi).hookChannel(plugin, "mttt").getAllKeys(u);
		}
		return null;
	}
}
