package de.melays.ttt;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.ChatColor;

public class PlaceHolderAPI extends EZPlaceholderHook {

    private main plugin;

    public PlaceHolderAPI(main ourPlugin) {
        super(ourPlugin, "mttt");
        this.plugin = ourPlugin;
    }
    

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) {
            return "";
        }
        if (identifier.equals("role_colored")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return null;
        	}
        	if (a.rm.getColoredRole(p) != null){
        		return a.rm.getColoredRole(p);
        	}
        	if (a.players.contains(p)){
        		return "Unknown";
        	}
        	return ChatColor.GRAY+"Spectator";
        }
        if (identifier.equals("role_display")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return null;
        	}
        	if (a.rm.getRole(p) != null){
        		return plugin.getDisplay(a.rm.getRole(p), false);
        	}
        	if (a.players.contains(p)){
        		return "Unknown";
        	}
        	return ChatColor.GRAY+"Spectator";
        }
        if (identifier.equals("role_display_big")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return null;
        	}
        	if (a.rm.getRole(p) != null){
        		return plugin.getDisplay(a.rm.getRole(p), true);
        	}
        	if (a.players.contains(p)){
        		return "UNKNOWN";
        	}
        	return ChatColor.GRAY+"SPECTATOR";
        }
        if (identifier.equals("role")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		return null;
        	}
        	if (a.rm.getRole(p) != null){
        		return a.rm.getRole(p);
        	}
        	if (a.players.contains(p)){
        		return "Unknown";
        	}
            return "Spectator";
        }
        if (identifier.equals("arena")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        		if (a == null){
        			return "None";
        		}
        		return a.name;
        	}
            return a.name;
        }
        if (identifier.equals("arena_ingame")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return null;
        	}
            return a.getCompleteSize()+"";
        }
        if (identifier.equals("arena_ingame")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return null;
        	}
            return a.getCompleteSize()+"";
        }
        if (identifier.equals("alive")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return "0";
        	}
            return a.getPlayerList().size()+"";
        }
        if (identifier.equals("arena_minplayers")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		a = plugin.m.searchSpec(p);
        	}
        	if (a == null){
        		return null;
        	}
            return a.min+"";
        }
        if (identifier.equals("points")) {
        	Arena a = plugin.m.searchPlayer(p);
        	if (a == null){
        		return "0";
        	}
            return a.shop.getPoints(p)+"";
        }
        if (identifier.equals("karma")) {
            return plugin.karma.getKarma(p)+"";
        }
        if (identifier.equals("passes")) {
            return plugin.karma.getPasses(p.getUniqueId())+"";
        }
        return null;
    }
}
