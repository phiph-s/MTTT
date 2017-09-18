package de.melays.Shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

public class ShopItem {
	
	public ShopItem(){
		
	}
	
	public ItemStack getCreeperArrows(boolean shop){
		ItemStack stack = new ItemStack(Material.TNT , 2);
		ItemMeta meta = stack.getItemMeta();
		
		String msg = "";
		if (shop){msg = " (2 Traitorpoints)";}
		
		meta.setDisplayName(ChatColor.YELLOW+"TNT Arrows"+msg);
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getHeal (boolean shop){
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE);
		
		String msg = "";
		if (shop){msg = " (1 Traitorpoint)";}
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW+"Golden Apple" + msg);
		
		stack.setItemMeta(meta);
		
		return stack;
	}
//	
//	public ItemStack getFakeCorpse (boolean shop){
//		ItemStack stack = new ItemStack(Material.ARMOR_STAND);
//		
//		String msg = "";
//		if (shop){msg = " (4 Traitorpoints)";}
//		
//		ItemMeta meta = stack.getItemMeta();
//		meta.setDisplayName(ChatColor.YELLOW+"Fakecorpse" + msg);
//		
//		stack.setItemMeta(meta);
//		
//		return stack;
//	}
//	
	public ItemStack getSpoofer(boolean shop){
		ItemStack stack = new ItemStack(Material.NETHER_STAR);
		
		ItemMeta meta = stack.getItemMeta();
		
		String msg = "";
		if (shop){msg = " (3 Traitorpoints)";}
		
		meta.setDisplayName(ChatColor.YELLOW+"Spoofer"+msg);
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getStick(boolean shop){
		ItemStack stack = new ItemStack(Material.STICK);
		
		ItemMeta meta = stack.getItemMeta();
		
		String msg = "";
		if (shop){msg = " (2 Traitorpoints)";}
		
		meta.setDisplayName(ChatColor.YELLOW+"Detective-Stick"+msg);
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getOneShotBow(boolean shop){
		ItemStack stack = new ItemStack(Material.BOW);
		
		String msg = "";
		if (shop){msg = " (5 Traitorpoints)";}
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW+"One-Shot Bow" + msg);
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getTesterOff(boolean shop){
		ItemStack stack = new ItemStack(Material.TRIPWIRE_HOOK);
		
		String msg = "";
		if (shop){msg = " (5 Traitorpoints)";}
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW+"Shutdown Tester" + msg);
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
}
