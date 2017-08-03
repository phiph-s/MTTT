package de.melays.Shop;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.melays.ttt.Arena;
import net.minecraft.server.v1_8_R3.MobSpawnerAbstract.a;

import org.bukkit.ChatColor;

public class ShopGUI {
	
	Inventory inv;
	Inventory invd;
	Arena arena;
	
	public ShopGUI(Arena a){
		arena = a;
		inv = Bukkit.createInventory(null, 9 , "Shop");
		inv.addItem(new ShopItem().getCreeperArrows(true));
		inv.addItem(new ShopItem().getSpoofer(true));
		inv.addItem(new ShopItem().getTesterOff(true));
		inv.addItem(new ShopItem().getHeal(true));
		invd = Bukkit.createInventory(null, 9 , "Shop");
		invd.addItem(new ShopItem().getHeal(true));
		invd.addItem(new ShopItem().getStick(true));
	}
	
	
	public void open(Player p){
		p.openInventory(inv);
	}
	
	public void opend(Player p){
		p.openInventory(invd);
	}
	
	public void callClickEvent (InventoryClickEvent e){
		if (e.getCurrentItem().getItemMeta() != null){
			if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW+"TNT Arrows")){
				if (arena.shop.removePoints((Player)e.getWhoClicked(), 2)){
					e.getWhoClicked().getInventory().addItem(new ShopItem().getCreeperArrows(false));
				}
				else{
					e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("notp" , true));
				}
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW+"Spoofer")){
				if (arena.shop.removePoints((Player)e.getWhoClicked(), 3)){
					if (!e.getWhoClicked().getInventory().contains(new ShopItem().getSpoofer(false))){
						e.getWhoClicked().getInventory().addItem(new ShopItem().getSpoofer(false));
					}
					else{
						e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("alreadyspoofer" , true));
					}
				}
				else{
					e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("notp" , true));
				}
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW+"Shutdown Tester")){
				if (arena.shop.removePoints((Player)e.getWhoClicked(), 5)){
					if (arena.atester.enabled){
						for (Location l : arena.atester.buttons){
							l.getWorld().playEffect(l, Effect.EXPLOSION , 5);
							arena.plugin.sd.playSound(l.getWorld(), l , "EXPLODE", "ENTITY_GENERIC_EXPLODE" );
						}
						arena.atester.destroyed = false;
					}
					else if (arena.tester){
						arena.tester = false;
						arena.testerlocation.getWorld().playEffect(arena.testerlocation, Effect.EXPLOSION , 5);
						arena.plugin.sd.playSound(arena.testerlocation.getWorld(), arena.testerlocation , "EXPLODE", "ENTITY_GENERIC_EXPLODE" );
						e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("testershutdown" , true));
					}
					else{
						e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("alreadyshutdown" , true));
					}
				}
				else{
					e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("notp" , true));
				}
			}
			
			if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW+"Golden Apple")){
				if (arena.shop.removePoints((Player)e.getWhoClicked(), 1)){
					e.getWhoClicked().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
				}
				else{
					e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("notp" , true));
				}
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW+"Detective-Stick")){
				if (arena.shop.removePoints((Player)e.getWhoClicked(), 2)){
					e.getWhoClicked().getInventory().addItem(new ItemStack(Material.STICK));
				}
				else{
					e.getWhoClicked().sendMessage(arena.plugin.mf.getMessage("notp" , true));
				}
			}
		}
	}
	
}
