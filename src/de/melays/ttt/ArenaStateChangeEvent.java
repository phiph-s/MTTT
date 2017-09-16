package de.melays.ttt;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaStateChangeEvent extends Event{
	
    private static final HandlerList handlers = new HandlerList();
	
	Arena a;
	String from;
	String to;
	
    public ArenaStateChangeEvent(Arena a , String from , String to) {
    	this.a = a;
    	this.from = from;
    	this.to = to;
    }

    public Arena getArena() {
        return a;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
