package de.melays.ttt;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reward {
	
	int chance = 0;
	String sender;
	String command;
	
	public Reward(int chance , String sender , String command){
		
		this.chance = chance;
		this.sender = sender;
		this.command = command;
		
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public void reward (Player p){
		
		if (randInt(1, chance) == 1){
			String command = this.command.replace("%player%", p.getName());
			if (sender.equalsIgnoreCase("CONSOLE")){
				
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				
			}
			
			else if (sender.equalsIgnoreCase("PLAYER")){
				
				p.performCommand(command);
				
			}
			
		}
		
	}
}
