package de.melays.CorpseRebornHook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;

import de.melays.ttt.Arena;

public class CRHook {
	
	public CRHook(){
		
	}
	
	
	
	public boolean isRunning(){
		if (Bukkit.getPluginManager().isPluginEnabled("CorpseReborn")){
			return true;
		}
		return false;
	}
	
	public void spawnCorpse (Arena a , Player p , Location loc){
		
		
	}
	
}
