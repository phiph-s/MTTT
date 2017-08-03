package de.melays.Sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundDebugger {
	public SoundDebugger(){
		
	}
	
	public void playSound(Player p , String s1 , String s2){
		try{
			if (Sound.valueOf(s1) != null){
				p.playSound(p.getLocation(), Sound.valueOf(s1) , 1, 1);
			}
			else if (Sound.valueOf(s2) != null){
				p.playSound(p.getLocation(), Sound.valueOf(s2) , 1, 1);
			}
		}
		catch(Exception e){
			
		}
		
	}
	
	public void playSound(World w , Location loc , String s1 , String s2){
		try{
			if (Sound.valueOf(s1) != null){
				w.playSound(loc, Sound.valueOf(s1) , 1, 1);
			}
			else if (Sound.valueOf(s2) != null){
				w.playSound(loc, Sound.valueOf(s2) , 1, 1);
			}
		}
		catch(Exception e){
			
		}
		
	}
	
}
