package de.melays.ttt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class RoleManager {
	
	main plugin;
	Arena arena;

	
	public RoleManager (main m , Arena a){
		plugin = m;
		arena = a;
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public ItemStack shopOpener(){
		ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("shopopener")), 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW+plugin.mf.getMessage("shopitemname", false));
		item.setItemMeta(meta);
		return item;
	}
	
	public void setRoles (ArrayList<Player> passes , ArrayList<Player> dpasses){
		long seed = System.nanoTime();
		Collections.shuffle(arena.players);
		seed = System.nanoTime();
		Collections.shuffle(arena.players);
		arena.startplayers = arena.players.size();
		for (Player p : arena.players){
		  	  try{
		  		  ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
		  	  }
		  	  catch(Exception ex){
		  		  
		  	  }
			p.setHealth(20);
		}
		
		int traitors = (int) Math.round((arena.players.size() / plugin.getConfig().getInt("traitorratio"))+0.5);
		
		if (arena.players.size() <= 4){
			traitors = 1;
		}
		int detecs = (int)(arena.players.size() / plugin.getConfig().getInt("detectiveratio"));
		if (traitors == 0){
			traitors = 1;
		}
		if (detecs == 1 && arena.players.size() < plugin.getConfig().getInt("min_players_for_detective")){
			detecs = 0;
		}
		arena.sendArenaMessage(ChatColor.GREEN+" ");
		for (Player p : new ArrayList<Player>(passes)){
			if (!arena.players.contains(p) || !p.isOnline()){
				passes.remove(p);
			}
		}
		for (Player p : new ArrayList<Player>(dpasses)){
			if (!arena.players.contains(p) || !p.isOnline()){
				dpasses.remove(p);
			}
		}
		for (int i = 0; i < traitors; i++){
			Player t;
			if (passes.size() == 0){
				int counter = 10;
				t = arena.players.get(randInt(0 , arena.players.size()-1));
				while (counter >= 0 && dpasses.contains(t)){
					counter --;
					t = arena.players.get(randInt(0 , arena.players.size()-1));
				}
				if (dpasses.contains(t)){
					t.sendMessage(plugin.mf.getMessage("toomanypasses", true));
					dpasses.remove(t);
				}
				arena.players.remove(t);
			}
			else{
				t = passes.get(0);
				passes.remove(t);
				arena.players.remove(t);
				if (!t.hasPermission("ttt.unlimited.traitor")){
					plugin.karma.removePasses(t, 1);
				}
				t.sendMessage(plugin.mf.getMessage("passactivated", true));
			}
			arena.traitors.add(t);
			t.sendMessage(plugin.mf.getMessage("role", true).replace("%role%" , plugin.getTraitorDisplay(true)));
			t.sendMessage(plugin.mf.getMessage("traitormessage", true));
			plugin.karma.addAdvancedStat(t.getUniqueId(), "traitor", 1);
			
			try{
				ItemStack opener = new ItemStack (Material.getMaterial(plugin.getConfig().getString("shopopener")));
				ItemMeta m = opener.getItemMeta();
				m.setDisplayName(plugin.mf.getMessage("shopitemname", false));
				opener.setItemMeta(m);
				t.getInventory().addItem(opener);
			}
			catch(Exception e){
				
			}
			Location lc = t.getLocation();
			lc.setY(t.getLocation().getY()+3);
			t.sendTitle(plugin.getTraitorDisplay(false), "");
			arena.traitors_save += t.getDisplayName()+" ";
			arena.karmaToLevel(t);
			arena.shop.addPoints(t, plugin.getConfig().getInt("startpoints"));
		}
		
		for (Player p : passes){
			p.sendMessage(plugin.mf.getMessage("toomanypasses", true));
		}
		for (int i = 0; i < detecs; i++){
			Player d;
			if (dpasses.size() == 0){
				d = arena.players.get(randInt(0 , arena.players.size()-1));
				arena.players.remove(d);
				arena.detectives.add(d);
			}
			else{
				d = dpasses.get(0);
				dpasses.remove(0);
				arena.players.remove(d);
				if (!d.hasPermission("ttt.unlimited.detective")){
					plugin.karma.removePasses(d, 1);
				}
				arena.detectives.add(d);
				d.sendMessage(plugin.mf.getMessage("passactivated", true));
			}
			d.sendMessage(plugin.mf.getMessage("role", true).replace("%role%" , plugin.getDetectiveDisplay(true)));
			d.sendMessage(plugin.mf.getMessage("detectivemessage", true));
			d.sendTitle(plugin.getDetectiveDisplay(false), "");
			d.getInventory().addItem(new ItemStack(Material.STICK));
			
			plugin.karma.addAdvancedStat(d.getUniqueId(), "detective", 1);
			
			ItemStack lhelmet = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			LeatherArmorMeta lam = (LeatherArmorMeta)lhelmet.getItemMeta();
			lam.setColor(Color.fromRGB(0, 47, 255));
			lhelmet.setItemMeta(lam);
			
			d.getInventory().setChestplate(lhelmet);
			
			try{
				ItemStack opener = new ItemStack (Material.getMaterial(plugin.getConfig().getString("shopopener")));
				ItemMeta m = opener.getItemMeta();
				m.setDisplayName(plugin.mf.getMessage("shopitemname", false));
				opener.setItemMeta(m);
				d.getInventory().addItem(opener);
			}
			catch(Exception e){
				
			}
			
			arena.karmaToLevel(d);
			arena.shop.addPoints(d, plugin.getConfig().getInt("startpoints"));
		}
		for (Player p : dpasses){
			p.sendMessage(plugin.mf.getMessage("toomanypasses", true));
		}
		for (Player in : arena.players){
			arena.innocents.add(in);
			in.sendMessage(plugin.mf.getMessage("role", true).replace("%role%" , plugin.getInnocentDisplay(true)));
			in.sendMessage(plugin.mf.getMessage("innocentmessage", true));
			in.sendTitle(plugin.getInnocentDisplay(false), "");
			arena.karmaToLevel(in);
			plugin.karma.addAdvancedStat(in.getUniqueId(), "innocent", 1);
		}

		arena.players = new ArrayList<Player>();
		if (arena.detectives.size() != 0){
			String dd = "";
			for (Player p : arena.detectives){
				dd += p.getName()+" ";
			}
			dd = arena.commaString(dd, ChatColor.translateAlternateColorCodes('&', plugin.mf.getMessage("dlistcolor", false)));
			arena.sendArenaMessage(plugin.mf.getMessage("detectivelist", true).replace("%detectives%", dd));
		}
		arena.traitors_save = arena.commaString(arena.traitors_save, ChatColor.translateAlternateColorCodes('&', plugin.mf.getMessage("tlistcolor", false)));
		for (Player p : arena.traitors){
			p.sendMessage(plugin.mf.getMessage("traitorslist", true).replace("%traitorlist%", arena.traitors_save));
			Collection<Player> ts = new ArrayList(arena.traitors);
		  	  try{
		  		  ColorTabAPI.setTabStyle(p , ChatColor.RED+"" , "" , 10 , ts);
		  	  }
		  	  catch(Exception ex){
		  		  
		  	  }
			
		}
		for (Player p : arena.detectives){
			Collection<Player> de = new ArrayList(arena.getPlayerList());
		  	  try{
		  		ColorTabAPI.setTabStyle(p , ChatColor.BLUE+"" , "" , 5 , de);
		  	  }
		  	  catch(Exception ex){
		  		  
		  	  }
		}
		if (plugin.getConfig().getBoolean("advancedtabcolors")){
			ArrayList<Player> allunknown = new ArrayList<Player>();
			allunknown.addAll(arena.innocents);
			allunknown.addAll(arena.traitors);
			for (Player p : allunknown){
				ArrayList<Player> allinnos = new ArrayList<Player>();
				allinnos.addAll(arena.innocents);
				allinnos.addAll(arena.detectives);
			  	  try{
			  		ColorTabAPI.setTabStyle(p , ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("advancedtabcolor_unknown"))+"" , "" , 5 , allinnos);
			  	  }
			  	  catch(Exception ex){
			  		  
			  	  }
			}
			for (Player p : arena.innocents){
			  	  try{
			  		ColorTabAPI.setTabStyle(p ,ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("advancedtabcolor_innocent"))+"" , "" , 5 , arena.traitors);
			  	  }
			  	  catch(Exception ex){
			  		  
			  	  }
			}
		}
		arena.sendArenaMessage(" ");
		
	}
	
	public void giveGlobalDetectives(int points){
		
		for (Player p : arena.detectives){
			
			arena.shop.addPoints(p, points);
			
		}
		
	}
	
	
//	public void createScoreboard (Player p){
//		ScoreboardManager manager = Bukkit.getScoreboardManager();
//		Scoreboard boardt = manager.getNewScoreboard();
//		Objective traitorsb;
//		traitorsb = boardt.registerNewObjective(ChatColor.GRAY+"["+ChatColor.GREEN+"TTT"+ChatColor.GRAY+"]", "dummy");
//		traitorsb.setDisplaySlot(DisplaySlot.SIDEBAR);
//		traitorsb.setDisplayName(ChatColor.GRAY+"["+ChatColor.GREEN+"TTT"+ChatColor.GRAY+"]");
//		if (getRole(p) == "TRAITOR"){
//			for (Player p2 : arena.players){
//				traitorsb.getScore(ChatColor.WHITE+p2.getName()).setScore(0);
//			}
//			for (Player p2 : arena.traitors){
//				traitorsb.getScore(ChatColor.RED+p2.getName()).setScore(0);
//			}
//			for (Player p2 : arena.detectives){
//				traitorsb.getScore(ChatColor.BLUE+p2.getName()).setScore(0);
//			}
//			for (Player p2 : arena.innocents){
//				traitorsb.getScore(ChatColor.GREEN+p2.getName()).setScore(0);
//			}
//		}
//		else{
//			for (Player p2 : arena.players){
//				traitorsb.getScore(ChatColor.WHITE+p2.getName()).setScore(0);
//			}
//			for (Player p2 : arena.traitors){
//				traitorsb.getScore(ChatColor.YELLOW+p2.getName()).setScore(0);
//			}
//			for (Player p2 : arena.detectives){
//				traitorsb.getScore(ChatColor.BLUE+p2.getName()).setScore(0);
//			}
//			for (Player p2 : arena.innocents){
//				traitorsb.getScore(ChatColor.YELLOW+p2.getName()).setScore(0);
//			}
//		}
//	}
	public String getRole (Player p){
		if (arena.traitors.contains(p)){
			return "TRAITOR";
		}
		if (arena.detectives.contains(p)){
			return "DETECTIVE";
		}
		if (arena.innocents.contains(p)){
			return "INNOCENT";
		}
		return null;
	}
	
	public String getColoredRole (Player p){
		if (arena.traitors.contains(p)){
			return ChatColor.RED+"TRAITOR";
		}
		if (arena.detectives.contains(p)){
			return ChatColor.BLUE+"DETECTIVE";
		}
		if (arena.innocents.contains(p)){
			return ChatColor.GREEN+"INNOCENT";
		}
		return null;
	}

	
	public void removePlayer (Player p){
		if (arena.players.contains(p)){
			arena.players.remove(p);
		}
		if (arena.traitors.contains(p)){
			arena.traitors.remove(p);
		}
		if (arena.detectives.contains(p)){
			arena.detectives.remove(p);
		}
		if (arena.innocents.contains(p)){
			arena.innocents.remove(p);
		}
		if (arena.specs.contains(p)){
			arena.specs.remove(p);
		}
		try{
			try{
				ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
		
			}
			catch(Exception ex){
				
			}
		}
		catch(Exception ex){
			
		}
	}
	
	public void manageKarma(Player killer , Player aim){
		RoleKillRoleEvent event = new RoleKillRoleEvent(arena.plugin , arena , aim , killer);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (getRole(killer).equals("INNOCENT") && getRole(aim).equals("TRAITOR")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaplus", false)).replaceAll("%karmaamount%", "20 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%", plugin.getTraitorDisplay(false)));
			plugin.karma.addKarma(killer, 20);
			if (plugin.getConfig().getBoolean("detective_global_receive")){giveGlobalDetectives(2);}
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "kills", 1);
		}
		else if (getRole(killer).equals("DETECTIVE") && getRole(aim).equals("TRAITOR")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaplus", false)).replaceAll("%karmaamount%", "25 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%", plugin.getTraitorDisplay(false)));
			if (plugin.getConfig().getBoolean("detective_global_receive")){giveGlobalDetectives(2);}
			else{
				arena.shop.addPoints(killer, 2);
			}
			killer.sendMessage(plugin.mf.getMessage("detectivepoints", true).replace("%amount%", "2").replace("%all%", Integer.toString(arena.shop.getPoints(killer))));
			plugin.karma.addKarma(killer, 25);
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "kills", 1);
		}
		else if (getRole(killer).equals("TRAITOR") && getRole(aim).equals("INNOCENT")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaplus", false)).replaceAll("%karmaamount%", "10 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%", plugin.getInnocentDisplay(false)));
			plugin.karma.addKarma(killer, 10);
			//detectivepoints
			arena.shop.addPoints(killer, 2);
			killer.sendMessage(plugin.mf.getMessage("traitorpoints", true).replace("%amount%", "2").replace("%all%", Integer.toString(arena.shop.getPoints(killer))));
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "kills", 1);
		}
		else if (getRole(killer).equals("TRAITOR") && getRole(aim).equals("DETECTIVE")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaplus", false)).replaceAll("%karmaamount%", "20 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%",  plugin.getDetectiveDisplay(false)));
			plugin.karma.addKarma(killer, 20);
			arena.shop.addPoints(killer, 3);
			killer.sendMessage(plugin.mf.getMessage("traitorpoints", true).replace("%amount%", "3").replace("%all%", Integer.toString(arena.shop.getPoints(killer))));
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "kills", 1);
		}
		else if (getRole(killer).equals("INNOCENT") && getRole(aim).equals("INNOCENT")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaminus", false)).replaceAll("%karmaamount%", "20 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%",  plugin.getInnocentDisplay(false)));
			plugin.karma.removeKarma(killer, 20);
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "randomkills", 1);
		}
		else if (getRole(killer).equals("DETECTIVE") && getRole(aim).equals("INNOCENT")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaminus", false)).replaceAll("%karmaamount%", "20 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%",  plugin.getInnocentDisplay(false)));
			plugin.karma.removeKarma(killer, 20);
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "randomkills", 1);
		}
		else if (getRole(killer).equals("DETECTIVE") && getRole(aim).equals("DETECTIVE")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaminus", false)).replaceAll("%karmaamount%", "40 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%", plugin.getDetectiveDisplay(false)));
			plugin.karma.removeKarma(killer, 40);
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "randomkills", 1);
		}
		else if (getRole(killer).equals("INNOCENT") && getRole(aim).equals("DETECTIVE")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaminus", false)).replaceAll("%karmaamount%", "40 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%",  plugin.getDetectiveDisplay(false)));
			plugin.karma.removeKarma(killer, 40);
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "randomkills", 1);
		}
		else if (getRole(killer).equals("TRAITOR") && getRole(aim).equals("TRAITOR")){
			killer.sendMessage(plugin.mf.getMessage("karmamsg", true).replace("%karmaindicator%", plugin.mf.getMessage("karmaminus", false)).replaceAll("%karmaamount%", "50 "+ plugin.mf.getMessage("karmaname", false)).replaceAll("%role%", plugin.getTraitorDisplay(false)));
			plugin.karma.removeKarma(killer, 50);
			plugin.karma.addAdvancedStat(killer.getUniqueId(), "randomkills", 1);
		}
		arena.karmaToLevel(killer);
	}
	
	public void dropKillMessage(Player aim , Player killer){
		if (killer != null){
			killer.sendMessage(arena.prefix + " You killed the "+getColoredRole(aim)+" "+aim.getName() );
			manageKarma (killer , aim);
			if (!arena.specs.contains(aim)){arena.sendArenaMessage(plugin.mf.getMessage("playerdied", true).replace("%remaining%", Integer.toString((arena.getAmountPlaying()-1))));}
		}
		else{
			if (!arena.specs.contains(aim)){arena.sendArenaMessage(plugin.mf.getMessage("playerdied", true).replace("%remaining%", Integer.toString((arena.getAmountPlaying()-1))));}
		}
	}
	
	public String checkEnd (){
		if (arena.gamestate == "ingame"){
			int innos = arena.detectives.size() + arena.innocents.size();
			int traitors = arena.traitors.size();
			ArrayList<Player> ts = (ArrayList<Player>) arena.players.clone();
			ArrayList<Player> is = (ArrayList<Player>) arena.players.clone();
			ts.addAll(arena.traitors);
			is.addAll(arena.innocents);
			is.addAll(arena.detectives);
			if (traitors == 0 && innos != 0){
				for (Player p : is){
					plugin.rm.reward(p, "win");
					plugin.karma.addAdvancedStat(p.getUniqueId(), "wins", 1);
				}
				arena.sendArenaMessage(ChatColor.GREEN+" ");
				arena.sendArenaMessage(plugin.mf.getMessage("innowin", true));
				arena.sendArenaMessage(plugin.mf.getMessage("traitors", true).replace("%traitors%", arena.traitors_save));
				arena.sendArenaMessage(ChatColor.GREEN+" ");
				arena.gamestate = "end";
				return "innocents";
			}
			if (innos == 0 && traitors != 0){
				for (Player p : ts){
					plugin.rm.reward(p, "win");
					plugin.karma.addAdvancedStat(p.getUniqueId(), "wins", 1);
				}
				arena.sendArenaMessage(ChatColor.GREEN+" ");
				arena.sendArenaMessage(plugin.mf.getMessage("traitorwin", true));
				arena.sendArenaMessage(plugin.mf.getMessage("traitors", true).replace("%traitors%", arena.traitors_save));
				arena.sendArenaMessage(ChatColor.GREEN+" ");
				arena.gamestate = "end";
				return "traitors";
			}
			return null;
		}
		return null;
	}
}
