package de.melays.ttt;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoleKillRoleEvent extends Event{
	
    private static final HandlerList handlers = new HandlerList();
	
	Arena a;
	Player p;
	Player killer;
	
	main plugin;
	
    public RoleKillRoleEvent(main m , Arena a , Player p , Player killer) {
    	this.a = a;
    	this.p = p;
    	this.killer = killer;
    	plugin = m;
    }

    public Arena getArena() {
        return a;
    }

    public Player getKiller() {
        return killer;
    }

    public Player getPlayer() {
        return p;
    }
    
    public String getPlayerRole() {
        return plugin.m.searchPlayer(p).rm.getRole(p);
    }
    
    public String getKillerRole() {
        return plugin.m.searchPlayer(killer).rm.getRole(killer);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
