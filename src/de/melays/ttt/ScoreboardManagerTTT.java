package de.melays.ttt;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardManagerTTT {
	HashMap<Player, Objective> objectives = new HashMap<Player, Objective>();
	
	Scoreboard sb;
	
	main plugin;
	
	public ScoreboardManagerTTT (main m){
		
		plugin = m;
		
        ScoreboardManager manager = (ScoreboardManager) Bukkit.getScoreboardManager();
        sb = manager.getNewScoreboard();
		
	}
	
	public void createBoard (Player p){
//		if (objectives.containsKey(p)){
//			objectives.get(p).unregister();
//		}
//        Objective obj = sb.registerNewObjective(p.getName(), "b");
//        obj.setDisplayName("TTT");
//        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//        objectives.put(p, obj);
//        
//        setStats(p);        
	}
	
    public void setStats(Player p){
//        Objective obj = objectives.get(p);
//        for (Objective o : sb.getObjectives()){
//        	if (o.getName().equals(p.getName())){
//        		o.unregister();
//        	}
//        }
//        
//        Arena a = plugin.m.searchPlayer(p);
//        
//        if (a != null){
//        	objectives.put(p, obj);
//	        obj = sb.registerNewObjective(p.getName(), "b");
//	        String role = a.rm.getColoredRole(p);
//	        if (role == null){
//	        	role = ChatColor.GRAY + "Spectator";
//	        }
//	        obj.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"");
//	        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//	        objectives.put(p, obj);
//	        
//	        int count = 0;
//	        
//	        if (a.detectives.size() != 0){
//	        	
//	        	for (Player ps : a.detectives){
//	        		count ++;
//	        		
//		        	Score detec = obj.getScore(ChatColor.BLUE + ps.getName());
//		        	
//		        	detec.setScore(count);
//	        		
//	        	}
//	        	
//	        	Score detecl = obj.getScore(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Detectives:");
//	        	
//	        	detecl.setScore(count);
//	        	
//	        }
//	        if (a.rm.getRole(p) != null){
//		        if (a.rm.getRole(p).equals("TRAITOR")){
//		        	
//		        	for (Player ps : a.traitors){
//		        		count ++;
//		        		
//			        	Score tr = obj.getScore(ChatColor.RED + ps.getName());
//			        	
//			        	tr.setScore(count);
//		        		
//		        	}
//		        	
//		        	Score trl = obj.getScore(ChatColor.RED + "" + ChatColor.UNDERLINE + "Traitors:");
//		        	
//		        	trl.setScore(count);
//		        	
//		        }
//	        }
//	        
//	        count ++;
//	        
//        	Score detecl = obj.getScore(ChatColor.WHITE + ">>>" + ChatColor.BOLD + role);
//        	
//        	detecl.setScore(count);
//	        
//	
//	        updateBoard(sb, p);
//	        
//        }
    }

    public void updateBoard(Scoreboard board, Player p){
//        if(p.isOnline()){
//            try{
//                p.setScoreboard(board);
//            }catch(IllegalStateException e){
//            }
//        }
    }
    
    public void removeBoard (Player p){
//    	objectives.get(p).unregister();
//    	objectives.remove(p);
//    	p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
