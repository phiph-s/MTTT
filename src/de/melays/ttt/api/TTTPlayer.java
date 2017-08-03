package de.melays.ttt.api;

import org.bukkit.entity.Player;

import de.melays.ttt.Arena;
import de.melays.ttt.main;

public class TTTPlayer {
	
	Player p;
	main plugin;
	
	public TTTPlayer (main m , Player p){
		this.p = p;
		plugin = m;
	}
	
	public boolean isPlaying (){
		return (plugin.m.searchPlayer(p) != null);
	}
	
	public String getRole (){
		return plugin.m.searchPlayer(p).rm.getRole(p);
	}
	
	public String getColoredRole (){
		return plugin.m.searchPlayer(p).rm.getColoredRole(p);
	}
	
	public void leave (boolean silent){
		plugin.m.searchPlayer(p).leave(p, silent);
	}
	
	public void join (Arena a){
		if (!isPlaying()){
			a.join(p);
		}
	}
	
	public Player getPlayer (){
		return p;
	}

}
