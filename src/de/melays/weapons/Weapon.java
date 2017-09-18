package de.melays.weapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import com.shampaggon.crackshot.CSUtility;

public class Weapon {
	WeaponFetcher fetcher;
	String name;
	int classid;
	int priority;
	ArrayList<String> types;
	int chance = 1;
	boolean crackshot = false;
	String crackshotname = null;
	
	int csamount = 1;
	boolean oneuse = false;
	
	
	ArrayList<EnchantmentPackage> enchs = new ArrayList<EnchantmentPackage>();
	
	public Weapon (WeaponFetcher fetcher , String name){
		this.name = name;
		this.fetcher = fetcher;
	}
	@SuppressWarnings("unchecked")
	public void loadWeapon (){
		if (name.startsWith("crackshot-")){
			
			if (fetcher.getWeaponFetcher().get(name+".chance") != null){
				chance = fetcher.getWeaponFetcher().getInt(name+".chance");
			}
			if (fetcher.getWeaponFetcher().get(name+".amount") != null){
				csamount = fetcher.getWeaponFetcher().getInt(name+".amount");
			}		
			if (fetcher.getWeaponFetcher().get(name+".oneuse") != null){
				oneuse = fetcher.getWeaponFetcher().getBoolean(name+".uses");
			}		
			crackshot = true;
			crackshotname = fetcher.getWeaponFetcher().getString(name+".weapon");
			System.out.println("[MTTT] Hooked Crackshot Weapon "+crackshotname + " as " + name);
			fetcher.csuses.put(crackshotname, oneuse);
			return;
			
		}
		types = (ArrayList<String>) fetcher.getWeaponFetcher().getList(name+".items");
		if (types == null){
			System.out.println("[MTTT] Weapon "+name+" item-List is null ! Please fix you config !");
		}
		if (fetcher.getWeaponFetcher().get(name+".class") != null){
			classid = fetcher.getWeaponFetcher().getInt(name+".class");
		}
		else{
			classid = 0;
			System.out.println("[MTTT] Weapon "+name+" classid is null ! Please fix you config !");
		}
		if (fetcher.getWeaponFetcher().get(name+".priority") != null){
			priority = fetcher.getWeaponFetcher().getInt(name+".priority");
		}
		List<String> enchantments = fetcher.getWeaponFetcher().getStringList(name+".enchantments");
		if (enchantments != null){
			if (enchantments.size() != 0){
				for (String s : enchantments){
					try{
						
						String[] names = s.split(":");
						String name = names[0];
						int level = 1;
						
						if (names.length == 2){
							try{
								level = Integer.parseInt(names[1]);
							}
							catch(Exception ex){
								System.out.println("[MTTT] Weapon "+name+" enchantment-level is invalid ! Please fix you config !");
							}
						}
						
						Enchantment e = Enchantment.getByName(name.toUpperCase());
						if (e != null){
							enchs.add(new EnchantmentPackage(e , level));
						}
						else{
							System.out.println("[MTTT] Enchantment "+s+" could not be loaded ! Please fix you config !");
						}
					}
					catch(Exception ex){
						ex.printStackTrace();
						System.out.println("[MTTT] Enchantment "+s+" could not be loaded ! Please fix you config !");
					}
				}
			}
			priority = fetcher.getWeaponFetcher().getInt(name+".priority");
		}
		else{
			priority = 0;
		}
		if (fetcher.getWeaponFetcher().get(name+".chance") != null){
			chance = fetcher.getWeaponFetcher().getInt(name+".chance");
		}
	}
	
	public void giveCrackShot(Player p){
		
		
		if(Bukkit.getServer().getPluginManager().getPlugin("CrackShot") == null){
			System.out.println("[MTTT] CrackShot is not installed!");
			return;
		}
		if (!Bukkit.getServer().getPluginManager().getPlugin("CrackShot").isEnabled()){
			System.out.println("[MTTT] For some reason CrackShot is disabled!");
			return;
		}
		
		CSUtility cs = new CSUtility ();
		cs.giveWeapon(p , crackshotname , csamount);
		
	}
	
	public ArrayList<ItemStack> getItemStacks(){
		
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		for (String s : types){
			String[] parts = s.split(":");
			Material m = Material.STONE;
			try{
				m = Material.getMaterial(parts[0]);
			}
			catch(Exception e){
				System.out.println("[MTTT] Material "+parts[0]+" not found ! Please fix you config !");
			}
			String name = null;
			if (parts.length > 1){
				if (!parts[1].equals("null")){
					name = ChatColor.translateAlternateColorCodes('&', parts[1]);
				}
			}
			int amount = 1;
			if (parts.length == 3){
				try{
					amount = Integer.parseInt(parts[2]);
				}
				catch(Exception e){
					System.out.println("[MTTT] Integer "+parts[2]+" could not be parsed ! Please fix you config !");
				}
			}
			
			ItemStack stack = new ItemStack(m);
			stack.setAmount(amount);
			ItemMeta meta = stack.getItemMeta();
			if (name != null){meta.setDisplayName(name);}
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(priority+":"+classid);
			if (meta != null){
				meta.setLore(lore);
				stack.setItemMeta(meta);
			}
			
			if (enchs != null){
				for (EnchantmentPackage e : enchs){
					
					try{
						stack.addUnsafeEnchantment(e.e, e.level);
					}
					catch(Exception ex){
						System.out.println("[MTTT] Error while adding Enchantment " + e.e.toString() + " to Item "+ this.name);
					}
					
				}
				
			}
			
			
			result.add(stack);
			
		}
		return result;
	}
	
}
