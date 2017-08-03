package de.melays.ttt;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class ArenaManager
{
  public HashMap<String,Arena> arenas = new HashMap<String,Arena>();
  
  public void addArena(Arena a , String name)
  {
     this.arenas.put(name,a);
  }
  
  public void removeComplete (Player p){
	  
	  for (String s : arenas.keySet()){
		  
		  Arena a = get(s);
		  
		  if (a.players.contains(p)){
			  
			  a.players.remove(p);
			  
		  }
		  
		  if (a.specs.contains(p)){
			  
			  a.specs.remove(p);
			  
		  }
		  
		  if (a.traitors.contains(p)){
			  
			  a.traitors.remove(p);
			  
		  }
		  
		  if (a.innocents.contains(p)){
			  
			  a.innocents.remove(p);
			  
		  }
		  
		  if (a.detectives.contains(p)){
			  
			  a.detectives.remove(p);
			  
		  }
		  
	  }
	  
  }
  
  public void closeAll(){
	  for (int i = 0; i < this.arenas.size(); i++)
	    {
	      Arena aa = (Arena)this.arenas.get(arenas.keySet().toArray()[i]);
	      aa.leaveAll();
	      System.out.println("[MTTT] Clearing Arena "+aa.name);
	    }
  }
  
  public void clearAll(){
	  for (int i = 0; i < this.arenas.size(); i++)
	    {
	      Arena aa = (Arena)this.arenas.get(arenas.keySet().toArray()[i]);
		  for (Block b : aa.blocks){
			  b.setType(Material.CHEST);
			  Chest chest = (Chest) b.getState();
			  chest.getInventory().clear();
		  }
		  for (Block b : aa.eblocks){
			  b.setType(Material.ENDER_CHEST);
		  }
		  for (ArmorStand s : aa.stands){
			  s.remove();
		  }
		  aa.removeItems();
		  System.out.println("[MTTT] Restoring Chests and Clearing Items for Arena "+aa.name);
	    }
  }
  
  public Arena searchSpec(Player p)
  {
    for (int i = 0; i < this.arenas.size(); i++)
    {
      Arena aa = (Arena)this.arenas.get(arenas.keySet().toArray()[i]);
      if (aa.specs != null){
	      for (Player pp : aa.specs){
	    	  if (pp == p){
	    		  return aa;
	    	  }
	      }
      }
    }
    return null;
  }
  
  public Arena searchPlayer(Player p)
  {
    for (int i = 0; i < this.arenas.size(); i++)
    {
      Arena aa = (Arena)this.arenas.get(arenas.keySet().toArray()[i]);
      if (aa.players != null){
	      for (Player pp : aa.players){
	    	  if (pp == p){
	    		  return aa;
	    	  }
	      }
      }
      if (!(aa.innocents.size()== 0)){
	      for (Player pp : aa.innocents){
	    	  if (pp == p){
	    		  return aa;
	    	  }
	      }
      }
      if (!(aa.traitors.size()== 0)){
	      for (Player pp : aa.traitors){
	    	  if (pp == p){
	    		  return aa;
	    	  }
	      }
      }
      if (!(aa.detectives.size()== 0)){
	      for (Player pp : aa.detectives){
	    	  if (pp == p){
	    		  return aa;
	    	  }
	      }
      }
    }
    return null;
  }
  
  public Arena get(String str)
  {
	  try{
	    return arenas.get(str);
	  }
	  catch (Exception e){
	    return null;
	  }
  }
}