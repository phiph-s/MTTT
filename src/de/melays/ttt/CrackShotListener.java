package de.melays.ttt;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent;

public class CrackShotListener implements Listener{
	
	main plugin;
	CrackShotListener (main m){
		plugin = m;
	}
	
	@EventHandler
	public void onCSWeapon (WeaponReloadCompleteEvent e){
		if (plugin.wf.csuses.containsKey(e.getWeaponTitle())){
			plugin.searchCSWeapon(e.getPlayer().getInventory() , e.getWeaponTitle()).setType(Material.AIR);
		}
	}

}
