package de.melays.ttt;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.melays.itembuilder.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class SpecInventory implements Listener {
	
	Arena a;
	
	public SpecInventory(Arena a){
		this.a = a;
		Bukkit.getPluginManager().registerEvents(this, a.plugin);
	}
	
	public void openInventory(Player p , ArrayList<Player> players){
		if (a.new_specmode) {
			Inventory inv = Bukkit.createInventory(null, ((a.getAmountPlaying() / 9)+1) * 9, a.plugin.mf.getMessage("specinv", true));
			for (Player tttp : players){
				inv.addItem(new ItemBuilder(Material.SKULL_ITEM , 1 , (byte) 3).setSkullOwner(tttp.getName()).setName(ChatColor.YELLOW + tttp.getName()).toItemStack());
			}
			p.openInventory(inv);
		}
	}
	
	@EventHandler
	public void onClick (InventoryClickEvent e){
		try {
			Player p = (Player)e.getWhoClicked();
			if (a.specs.contains(p)){
				e.setCancelled(true);
				if (e.getClickedInventory().getName().equals(a.plugin.mf.getMessage("specinv", true))){
					ItemStack stack = e.getCurrentItem();
					if (stack.getType() == Material.SKULL_ITEM){
						Player temp = Bukkit.getPlayer(stack.getItemMeta().getDisplayName().replaceAll(ChatColor.YELLOW+"", ""));
						if (a.plugin.m.searchPlayer(temp) != null) {
							if (a.plugin.m.searchPlayer(temp).name == a.name){
								p.teleport(temp);
								p.closeInventory();
							}
							else{
								p.closeInventory();
							}
						}
					}
				}
			}
		} catch (Exception e1) {

		}
		
	}
	
}
