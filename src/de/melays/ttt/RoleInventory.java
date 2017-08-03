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
			inv.setItem(2, new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.RED).setName(plugin.getTraitorDisplay(false)).addLoreLine(ChatColor.RESET + "Passes: " + plugin.karma.getPasses(tp.getPlayer().getUniqueId())).toItemStack());
			inv.setItem(6, new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.BLUE).setName(plugin.getDetectiveDisplay(false)).addLoreLine(ChatColor.RESET + "Passes: " + plugin.karma.getPasses(tp.getPlayer().getUniqueId())).toItemStack());
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
			if (e.getClickedInventory().equals(inv)){
				e.setCancelled(true);
				if (e.getCurrentItem() == null){
					return;
				}
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getTraitorDisplay(false))){
					((Player)e.getWhoClicked()).performCommand("traitor");
					e.getWhoClicked().closeInventory();
					open();
				}
				else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getDetectiveDisplay(false))){
					((Player)e.getWhoClicked()).performCommand("detective");
					e.getWhoClicked().closeInventory();
					open();
				}
			}
		}
		catch (Exception ex){
			
		}
	}
	
}
