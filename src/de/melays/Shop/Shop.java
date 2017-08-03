package de.melays.Shop;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class Shop {
	
	HashMap<Player,Integer> points;
	
	public Shop(){
		points = new HashMap<Player,Integer>();
	}
	
	public int getPoints(Player p){
		if (points.containsKey(p)){
			return points.get(p);
		}
		else{
			points.put(p, 0);
			return 0;
		}
	}
	
	public void setPoints (Player p , int point){
		points.put(p, point);
	}
	
	public void addPoints (Player p , int point){
		setPoints(p , getPoints(p)+point);
	}
	
	public boolean removePoints (Player p , int i){
		if (getPoints(p) - i >= 0){
			setPoints(p , getPoints(p) - i);
			return true;
		}
		return false;
	}
	
}
