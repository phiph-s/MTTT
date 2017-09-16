package de.melays.ttt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.melays.itembuilder.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class SpecInventory {
	
	Arena a;
	
	public SpecInventory(Arena a){
		this.a = a;
	}
	
	public void openInventory(Player p){
		if (a.new_specmode) {
			Inventory inv = Bukkit.createInventory(null, ((a.getAmountPlaying() / 9)+1) * 9, a.plugin.mf.getMessage("specinv", true));
			for (Player tttp : a.getPlayerList()){
				inv.addItem(new ItemBuilder(Material.SKULL).setSkullOwner(tttp.getName()).setName(ChatColor.YELLOW + tttp.getName()).toItemStack());
			}
			p.openInventory(inv);
		}
	}
	
	public void onClick (InventoryClickEvent e){
		try {
			Player p = (Player)e.getWhoClicked();
			if (a.specs.contains(p)){
				if (e.getClickedInventory().getName().equals(a.plugin.mf.getMessage("specinv", true))){
					
					ItemStack stack = e.getCurrentItem();
					if (stack.getType() == Material.SKULL){
						Player temp = Bukkit.getPlayer(stack.getItemMeta().getDisplayName());
						if (a.getPlayerList().contains(temp)){
							p.teleport(temp);
							p.closeInventory();
						}
						else{
							p.closeInventory();
						}
						
					}
					
				}
			}
		} catch (Exception e1) {

		}
		
	}
	
}
