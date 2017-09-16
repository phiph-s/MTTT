package de.melays.ttt;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.itembuilder.ItemBuilder;
import de.melays.ttt.api.TTTPlayer;

public class RoleInventory implements Listener {
	
	Player p;
	main plugin;
	
	Inventory inv;
	
	public RoleInventory(main m , Player p){
		this.p = p;
		this.plugin = m;
	}
	
	public void open(){
		TTTPlayer tp = plugin.getAPI().getPlayer(p);
		if (tp.isPlaying()){
			Arena a = plugin.m.searchPlayer(tp.getPlayer()); 
			String msg = plugin.mf.getMessage("roleinventory", true);
			if (a.usedpass.contains(tp.getPlayer())){
				msg = plugin.mf.getMessage("roleinventoryselected", true).replace("%role%", plugin.getTraitorDisplay(false));
			}
			else if (a.usedpass_detective.contains(tp.getPlayer())){
				msg = plugin.mf.getMessage("roleinventoryselected", true).replace("%role%", plugin.getDetectiveDisplay(false));
			}
			inv = Bukkit.createInventory(null, 9 , msg);
			inv.setItem(2, new ItemBuilder(Material.WOOL).setDyeColor(DyeColor.RED).setName(plugin.getTraitorDisplay(false)).addLoreLine(ChatColor.RESET + "Passes: " + plugin.karma.getPasses(tp.getPlayer().getUniqueId())).toItemStack());
			inv.setItem(6, new ItemBuilder(Material.WOOL).setDyeColor(DyeColor.BLUE).setName(plugin.getDetectiveDisplay(false)).addLoreLine(ChatColor.RESET + "Passes: " + plugin.karma.getPasses(tp.getPlayer().getUniqueId())).toItemStack());
			fillPanes(inv);
			p.openInventory(inv);
		}
	}
	
	private void fillPanes (Inventory inv){
		int counter = 0;
		for (ItemStack s : inv.getContents()){
			if (s == null){
				s = new ItemStack (Material.STAINED_GLASS_PANE , 1 ,  (byte)7);
				ItemMeta m = s.getItemMeta();
				m.setDisplayName(" ");
				s.setItemMeta(m);
				inv.setItem(counter , s);
			}
			counter ++;
		}
	}
	
	@EventHandler
	public void onClick (InventoryClickEvent e){
		try{
			Player p = ((Player)e.getWhoClicked());
			if (e.getClickedInventory().equals(inv) && p == this.p){
				e.setCancelled(true);
				if (e.getCurrentItem() == null){
					return;
				}
				TTTPlayer tp = plugin.getAPI().getPlayer(p);
				Arena a = plugin.m.searchPlayer(tp.getPlayer());
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getTraitorDisplay(false))){
					if (a.usedpass_detective.contains(p)) {
						toggleDetective();
					}
					toggleTraitor();
					open();
				}
				else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getDetectiveDisplay(false))){
					if (a.usedpass_detective.contains(p)) {
						toggleTraitor();
					}
					toggleDetective();
					open();
				}
			}
		}
		catch (Exception ex){
			
		}
	}
	
	public void toggleTraitor() {
		TTTPlayer tp = plugin.getAPI().getPlayer(p);
		Arena ar = plugin.m.searchPlayer(tp.getPlayer());
		if (ar == null){
			p.sendMessage(plugin.mf.getMessage("notingame", true));
			return;
		}
		if (ar.gamestate != "waiting"){
			p.sendMessage(plugin.mf.getMessage("alreadyingame", true));
			return;
		}
		else{
			if (ar.usedpass_detective.contains(p)){
				p.sendMessage(plugin.mf.getMessage("alreadydetectivepass", true));
				return;
			}
			if (ar.usedpass.contains(p)){
				ar.usedpass.remove(p);
				p.sendMessage(plugin.mf.getMessage("stoppass", true));
				return;
			}
			if (plugin.karma.getPasses(p.getUniqueId()) >= 1 || p.hasPermission("ttt.unlimited.traitor")){
				ar.usedpass.add(p);
				p.sendMessage(plugin.mf.getMessage("requesttraitor", true));
				return;
			}
			else{
				p.sendMessage(plugin.mf.getMessage("nopass", true));
				return;
			}
		}
	}
	
	public void toggleDetective() {
		TTTPlayer tp = plugin.getAPI().getPlayer(p);
		Arena ar = plugin.m.searchPlayer(tp.getPlayer());
		if (ar == null){
			p.sendMessage(plugin.mf.getMessage("notingame", true));
			return;
		}
		if (ar.gamestate != "waiting"){
			p.sendMessage(plugin.mf.getMessage("alreadyingame", true));
			return;
		}
		else{
			if (ar.usedpass.contains(p)){
				p.sendMessage(plugin.mf.getMessage("alreadytraitorpass", true));
				return;
			}
			if (ar.usedpass_detective.contains(p)){
				ar.usedpass_detective.remove(p);
				p.sendMessage(plugin.mf.getMessage("stoppass", true));
				return;
			}
			if (plugin.karma.getPasses(p.getUniqueId()) >= 1 || p.hasPermission("ttt.unlimited.detective")){
				ar.usedpass_detective.add(p);
				p.sendMessage(plugin.mf.getMessage("requestdetective", true));
				return;
			}
			else{
				p.sendMessage(plugin.mf.getMessage("nopass", true));
				return;
			}
		}
	}
	
}
