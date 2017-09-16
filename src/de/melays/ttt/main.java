package de.melays.ttt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.stats.MetricsLite;

import com.shampaggon.crackshot.CSUtility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import de.melays.Sound.SoundDebugger;
import de.melays.statsAPI.StatsAPI;
import de.melays.ttt.ColorTabAPI;
import de.melays.ttt.Tester.NewTesterSetup;
import de.melays.ttt.Tester.TesterSetup;
import de.melays.ttt.api.TTTApi;
import de.melays.ttt.multispawn.MultiSpawn;
import de.melays.ttt.multispawn.MultiSpawnCommand;
import de.melays.weapons.WeaponFetcher;


public class main
extends JavaPlugin
implements Listener
{
	public static main instance;
	public ArenaManager m;
	public Karma karma;
	public MultiSpawn ms;
	public Rank rank;
	ScoreboardManagerTTT sb;
	TesterSetup ts;
	
	NewTesterSetup nts;
	
	public String prefix;
	public MessageFetcher mf = new MessageFetcher(this);
	WeaponFetcher wf = new WeaponFetcher(this);
	
	Ranker ranker = null;
	
	HashMap<Player , RoleInventory> roleinv = new HashMap<Player , RoleInventory>();
	
	
	TTTApi api;
	
	public SoundDebugger sd = new SoundDebugger();
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		for (Player p : Bukkit.getOnlinePlayers())
		{
			try{
				if (p.canSee(player)){
					p.showPlayer(player);
				}
			}
			catch(Exception e){
				
			}
		}
	}
	
	public String getDisplay (String s , boolean big){
		if (s.equalsIgnoreCase("traitor")){
			return getTraitorDisplay(big);
		}
		else if (s.equalsIgnoreCase("detective")){
			return getDetectiveDisplay(big);
		}
		else if (s.equalsIgnoreCase("innocent")){
			return getInnocentDisplay(big);
		}
		return "";
	}
	
	public String getTraitorDisplay (boolean big){
		if (big){
			return mf.getMessage("display_traitor_big", true);
		}
		else{
			return mf.getMessage("display_traitor", true);
		}
	}
	
	public String getDetectiveDisplay (boolean big){
		if (big){
			return mf.getMessage("display_detective_big", true);
		}
		else{
			return mf.getMessage("display_detective", true);
		}
	}
	
	public String getInnocentDisplay (boolean big){
		if (big){
			return mf.getMessage("display_innocent_big", true);
		}
		else{
			return mf.getMessage("display_innocent", true);
		}
	}
	
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		
		if (e.getEntity() instanceof Creeper){
			if (((Creeper)e.getEntity()).getTarget() instanceof Player){
				Player target = (Player) ((Creeper)e.getEntity()).getTarget();
				Arena ar2 = m.searchPlayer(target);
				if (ar2 != null){
					e.setCancelled(true);
				}
			}
			else if (((Creeper)e.getEntity()).getCustomName().equals(ChatColor.GREEN+"Creeper")){
				e.setCancelled(true);
			}
		}
	}
	
	
	public void mountArena(String stri)
	{
		int min = getConfig().getInt(stri + ".min");
		double x = getConfig().getDouble(stri + ".spec.x");
		double y = getConfig().getDouble(stri + ".spec.y");
		double z = getConfig().getDouble(stri + ".spec.z");
		double pitch = getConfig().getDouble(stri + ".spec.pitch");
		double yaw = getConfig().getDouble(stri + ".spec.yaw");
		Location spec = new Location(Bukkit.getWorld(getConfig().getString(stri + ".spec.world")), x, y, z, (float)yaw, (float)pitch);
		x = getConfig().getDouble(stri + ".game.x");
		y = getConfig().getDouble(stri + ".game.y");
		z = getConfig().getDouble(stri + ".game.z");
		pitch = getConfig().getDouble(stri + ".game.pitch");
		yaw = getConfig().getDouble(stri + ".game.yaw");
		Location game = new Location(Bukkit.getWorld(getConfig().getString(stri + ".game.world")), x, y, z, (float)yaw, (float)pitch);
		x = getConfig().getDouble(stri + ".back.x");
		y = getConfig().getDouble(stri + ".back.y");
		z = getConfig().getDouble(stri + ".back.z");
		pitch = getConfig().getDouble(stri + ".back.pitch");
		yaw = getConfig().getDouble(stri + ".back.yaw");
		Location back = new Location(Bukkit.getWorld(getConfig().getString(stri + ".back.world")), x, y, z, (float)yaw, (float)pitch);
		Location sign = null;
		if (getConfig().getBoolean(stri + ".sign.set")){
			x = getConfig().getDouble(stri + ".sign.x");
			y = getConfig().getDouble(stri + ".sign.y");
			z = getConfig().getDouble(stri + ".sign.z");
			sign = new Location(Bukkit.getWorld(getConfig().getString(stri + ".sign.world")), x, y, z);
		}
		Location lamp1 = null;
		Location lamp2 = null;
		Location testerloc = null;
		Location testerbutton = null;
		if (ts.checkTesterSetup(stri)){
			System.out.println("[MTTT] Tester activated for arena "+stri);
				System.out.println("[MTTT] " + stri + " is using an old Tester! It is recommenced to delete it using /ttt deleteoldtester and setup a new one!");
			lamp1 = ts.locationFromConfig(stri+".tester.lamp.1");
			lamp2 = ts.locationFromConfig(stri+".tester.lamp.2");
			testerbutton = ts.locationFromConfig(stri+".tester.button");
			x = getConfig().getDouble(stri + ".tester.x");
			y = getConfig().getDouble(stri + ".tester.y");
			z = getConfig().getDouble(stri + ".tester.z");
			pitch = getConfig().getDouble(stri + ".tester.pitch");
			yaw = getConfig().getDouble(stri + ".tester.yaw");
			testerloc = new Location(Bukkit.getWorld(getConfig().getString(stri + ".tester.world")), x, y, z, (float)yaw, (float)pitch);
		}
		Arena a = new Arena(this ,spec, game , back , min , sign , stri , testerbutton , testerloc , lamp1, lamp2);
		this.m.addArena(a , stri);
	}
	
	public boolean unmountArena(String str){
		if (!m.arenas.containsKey(str)){
			return false;
		}
		Arena a = m.get(str);
		if (a != null){
			System.out.println("[MTTT] Unmounting Arena "+str);
			a.leaveAll();
			a.ended = true;
			a.endGame(false);
			m.arenas.remove(str);
			return true;
		}
		return false;
	}
	
	public boolean reloadArena(String arena){
		if (unmountArena(arena)){
			try{
				System.out.println("[MTTT] Mounting Arena "+arena);
				mountArena(arena);
				return true;
			}
			catch (Exception e){
				return false;
			}
		}
		return false;
	}
	
	@EventHandler
	public void shopItemm (PlayerInteractEvent e){
		Player p = e.getPlayer();
		Arena ar2 = m.searchPlayer(p);
		if (ar2 != null){
			if (p.getItemInHand().getType() == Material.getMaterial(getConfig().getString("shopopener")) && p.getItemInHand().getItemMeta().getDisplayName().equals(mf.getMessage("shopitemname", false))){
				p.performCommand("tshop");
			}
		}
	}
	
	@EventHandler
	public void onCommand (PlayerCommandPreprocessEvent e){
		String[] cmds = e.getMessage().split(" ");
		if (cmds[0].equalsIgnoreCase("/start") && getConfig().getBoolean("startcommand") && e.getPlayer().hasPermission("ttt.start")){
			
			e.setCancelled(true);
			if (cmds.length == 2){
				e.getPlayer().performCommand("ttt start "+cmds[1]);
			}
			else{
				e.getPlayer().performCommand("ttt start");
			}
			
		}
		if (cmds[0].equalsIgnoreCase("/stats") && getConfig().getBoolean("command_stats")){
			e.getPlayer().performCommand(e.getMessage().replaceFirst("/", "ttt "));
			e.setCancelled(true);
		}
		if (cmds[0].equalsIgnoreCase("/hub") && getConfig().getBoolean("command_hub") && this.getAPI().getPlayer(e.getPlayer()).isPlaying()){
			e.getPlayer().performCommand("ttt leave");
		}
		if (cmds[0].equalsIgnoreCase("/l") && getConfig().getBoolean("command_l") && this.getAPI().getPlayer(e.getPlayer()).isPlaying()){
			e.getPlayer().performCommand("ttt leave");
		}
		if (cmds[0].equalsIgnoreCase("/lobby") && getConfig().getBoolean("command_lobby") && this.getAPI().getPlayer(e.getPlayer()).isPlaying()){
			e.getPlayer().performCommand("ttt leave");
		}
		if (cmds[0].equalsIgnoreCase("/leave") && getConfig().getBoolean("command_leave") && this.getAPI().getPlayer(e.getPlayer()).isPlaying()){
			e.getPlayer().performCommand("ttt leave");
		}
	}
	
	RewardManager rm;
	
	public void openRoleInv(Player p){
		if (!roleinv.containsKey(p)){
			RoleInventory ri = new RoleInventory (this , p);
			Bukkit.getPluginManager().registerEvents(ri, this);
			roleinv.put(p, ri);
		}
		roleinv.get(p).open();
	}
	
	boolean nicknamer = false;
	
	public void onEnable()
	{
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		sb = new ScoreboardManagerTTT(this);
		instance = this;
		rank = new Rank(this);
		rank.getRanks().options().copyDefaults(true);
		rank.saveRanks();
		rank.load();
		mf.getMessageFetcher().options().copyDefaults(true);
		mf.saveMessageFile();
		mf.reloadMessageFile();
		prefix = mf.getMessage("prefix", false);
		this.m = new ArenaManager();
		ts = new TesterSetup(this);
		nts = new NewTesterSetup(this);
		ms = new MultiSpawn(this);
		wf.getWeaponFetcher().options().copyDefaults(true);
		wf.saveWeaponFile();
		wf.reloadWeaponFile();
		wf.loadWeapons();
		
		rm = new RewardManager(this);
		rm.getRewardFile().options().copyDefaults(true);
		rm.saveRewardFile();
		rm.reloadRewardFile();
		rm.mountRewards();
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			
			System.out.println("[MTTT] Hooking into PlaceholderAPI ...");
			new PlaceHolderAPI(this).hook();
			System.out.println("[MTTT] Succesfully hooked into PlaceholderAPI !");
			
		}
		else{
			
			System.out.println("[MTTT] PlaceholderAPI was not found. Continuing ... ");
			
		}
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(ts , this);
		Bukkit.getServer().getPluginManager().registerEvents(nts , this);
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("mysql.enabled", false);
		getConfig().addDefault("mysql.host", "localhost");
		getConfig().addDefault("mysql.port", "3306");
		getConfig().addDefault("mysql.user", "user");
		getConfig().addDefault("mysql.database", "database");
		getConfig().addDefault("mysql.password", "password");
		getConfig().addDefault("shopopener", "GOLD_NUGGET");
		getConfig().addDefault("bungeemotd", "false");
		getConfig().addDefault("metrics", true);
		getConfig().addDefault("waitingtime", 20);
		getConfig().addDefault("gametime", 360);
		getConfig().addDefault("restarttime", 15);
		getConfig().addDefault("minplayerstotest", 0);
		getConfig().addDefault("startpoints", 0);
		getConfig().addDefault("detective_global_receive", false);
		getConfig().addDefault("autojoin", false);
		getConfig().addDefault("bungeeserver", false);
		getConfig().addDefault("lobbymode", false);
		getConfig().addDefault("stopserver", false);
		getConfig().addDefault("startcommand", false);
		getConfig().addDefault("itemdrop", true);
		getConfig().addDefault("traitorratio", 4);
		getConfig().addDefault("detectiveratio", 5);
		getConfig().addDefault("min_players_for_detective", 4);
		getConfig().addDefault("role_item", true);
		getConfig().addDefault("leave_item", true);
		getConfig().addDefault("roleitem_slot", 9);
		getConfig().addDefault("leaveitem_slot", 8);
		getConfig().addDefault("role_item_type", Material.BOOK.toString());
		getConfig().addDefault("leave_item_type", Material.SLIME_BALL.toString());
		getConfig().addDefault("ttt_help", true);
		getConfig().addDefault("advancedtabcolors", true);
		getConfig().addDefault("command_hub", true);
		getConfig().addDefault("command_l",true);
		getConfig().addDefault("command_lobby", true);
		getConfig().addDefault("command_leave", true);
		getConfig().addDefault("command_stats", true);
		getConfig().addDefault("hide_players_outside_arena", false);
		getConfig().addDefault("spectatormode", true);
		getConfig().addDefault("advancedtabcolor_unknown", "&e");
		getConfig().addDefault("advancedtabcolor_innocent", "&a");
		getConfig().addDefault("randomnicknames", false);
		getConfig().addDefault("hidenametag", false);
		getConfig().addDefault("serverpack", "none");
		getConfig().addDefault("only_detective_corpsescan", false);
		getConfig().addDefault("hidespecs", false);
		getConfig().addDefault("newspecmode", true);
		getConfig().addDefault("alphawarning", true);
		//    getConfig().addDefault("passreward.enabled", false);
		//    getConfig().addDefault("passreward.karmasteps", 500);
		//    getConfig().addDefault("passreward.passes", 1);
		
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("ttt");
		commands.add("tc");
		commands.add("shop");
		commands.add("tshop");
		commands.add("traitor");
		commands.add("vote");
		commands.add("detective");
		commands.add("role");
		getConfig().addDefault("enabledcommands", commands);
		saveConfig();
		
		String mode = "yml";
		if (getConfig().getBoolean("mysql.enabled")){
			mode = "mysql";
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("StatsAPI")){
			
			System.out.println("[MTTT] StatsAPI has been found! MTTT will neither use MySQL or YAML and will save all Data with StatsAPI.");
			mode = "statsapi";
			ranker = new Ranker(this  , ((StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI")).hookChannel(this, "mttt"));
			
		}
		else{
			
			System.out.println("[MTTT] StatsAPI was not found. Continuing ... ");
			
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("AutoNicker")){
			
			System.out.println("[MTTT] AutoNicker has been found! If randomnicknames is enabled, it will give random Nicknames!");
			nicknamer = true;
			nicknamer = getConfig().getBoolean("randomnicknames");
		}
		else{
			
			System.out.println("[MTTT] AutoNicker was not found.");
			
		}
		
		karma = new Karma(this , mode);
		karma.saveKarma();
		
		if (getConfig().getBoolean("metrics")){
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
				System.out.println("[MTTT] Sending Metrics to mcstats.org");
				new MetricsLite(this);
				System.out.println("[MTTT] Sending Metrics to bstats.org");
			} catch (Exception e) {
				// Failed to submit the stats :-(
			}
		}
		
		@SuppressWarnings("rawtypes")
		ArrayList list = (ArrayList)getConfig().getList("arenas");
		System.out.println(list);
		
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				mountArena((String)list.get(i));
			}
		}
		if (getConfig().getString("autojoin") != null){
			if (!getConfig().getString("autojoin").equals("false")){
				Arena a = m.get(getConfig().getString("autojoin"));
				if (a != null){
					for (Player p : Bukkit.getOnlinePlayers()){
						a.addPlayer(p.getPlayer());
					}
				}
			}
		}
		api = new TTTApi(this);
		if (Bukkit.getPluginManager().isPluginEnabled("CrackShot")){
			
			System.out.println("[MTTT] CrackShot found! Registering Events ...");
			Bukkit.getPluginManager().registerEvents(new CrackShotListener(this), this);
			System.out.println("[MTTT] CrackShot is ready!");
			
		}
		else{
			
			System.out.println("[MTTT] CrackShot was not found. Continuing ... ");
			
		}
	}
	
	@EventHandler
	public void onLogin (PlayerLoginEvent e){
		if (!getConfig().getString("autojoin").equals("false")){
			Arena a = m.get(getConfig().getString("autojoin"));
			if (a != null){
				if (a.max_players != 0){
					if (a.max_players <= a.getCompleteSize()){
						if (!e.getPlayer().hasPermission("ttt.premiumjoin")){
							e.setKickMessage(mf.getMessage("kickedwhilefull", true));
							e.disallow(Result.KICK_OTHER , mf.getMessage("kickedwhilefull", true));
						}
					}
				}
			}
		}
	}
	
	public TTTApi getAPI(){
		return api;
	}
	
	public static main getInstance() {
		return instance;
	}
	
	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		Arena ar2 = m.searchPlayer(p);
		if (ar2 != null){
			if(!p.hasPermission("ttt.bypass")) {
				List<String> cmds = getConfig().getStringList("enabledcommands");
				boolean sendCommand = false;
				for (String command : cmds) {
					if (event.getMessage().toLowerCase().startsWith("/"+command.toLowerCase())) {
						sendCommand = true;
					}
				}
				if (!sendCommand){
					p.sendMessage(mf.getMessage("notallowed", true));
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onGM (PlayerGameModeChangeEvent e){
		if (getConfig().getBoolean("hidespecs")){
			if (e.getNewGameMode().equals(GameMode.SPECTATOR)){
				for (Player p : Bukkit.getOnlinePlayers()){
					p.hidePlayer(e.getPlayer());
				}
			}
			if (!e.getNewGameMode().equals(GameMode.SPECTATOR)){
				for (Player p : Bukkit.getOnlinePlayers()){
					p.showPlayer(e.getPlayer());
				}
			}
		}
	}
	
	//CRACKSHOT
	
	public ItemStack searchCSWeapon(Inventory inv , String s){
		CSUtility cs = new CSUtility ();
		for (ItemStack i : inv.getContents()){
			if (i != null){
				if (cs.getWeaponTitle(i) != null){
					if (cs.getWeaponTitle(i).equals(s)){
						return i;
					}
				}
			}
		}
		return null;
	}
	
	//--------------
	
	HashMap<UUID , String> names = new HashMap<UUID , String>();
	
	@EventHandler
	public void autoJoin (PlayerJoinEvent e){
		if (e.getPlayer().hasPermission("ttt.setup") && this.getDescription().getVersion().endsWith("Alpha") && getConfig().getBoolean("alphawarning")){
			Player p = e.getPlayer();
			p.sendMessage(ChatColor.RED + "Hey Admin :) It seems like you are using a Alpha Verison of my plugin which is great!");
			p.sendMessage(ChatColor.RED + "But as this is a Alpha Version I need your help to find bugs, so please email them to me if you find any!");
			p.sendMessage(ChatColor.RED + "My E-Mail: " + ChatColor.YELLOW + "schwalboss@outlook.de");
			p.sendMessage(ChatColor.RED + "And please :C ... Do not write a bad review because of a bug in a alpha version :C");
			p.sendMessage(ChatColor.RED + "Thanks for your help! If you dont want to see this message again, disable it in the config.yml at 'alphawarning'");
			}
		names.put(e.getPlayer().getUniqueId() , e.getPlayer().getName());
		e.getPlayer().setResourcePack(getConfig().getString("serverpack"));
		if (!getConfig().getString("autojoin").equals("false")){
			Arena a = m.get(getConfig().getString("autojoin"));
			if (a != null){
				a.addPlayer(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void respawnOnPlayerDeath(final PlayerDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			if (m.searchPlayer(event.getEntity()) != null){
				if (!getConfig().getBoolean("itemdrop")){
					event.getDrops().clear();
				}
			}
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					try{
						event.getEntity().spigot().respawn();
					}
					catch (Exception ex){
						try {
							BypassRespawnAPI.sendRespawnPacket(event.getEntity());
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("[MTTT] Couldn't respawn player "+event.getEntity().getName() + " at all. Tried with Spigot (No Spigot used) + Packet (FAILED).");
						}
					}
				}
			},5L);
		}
	}
	
	@Override
	public void onDisable(){
		System.out.println("[MTTT] Disabling Arenas");
		m.clearAll();
		m.closeAll();
		for (Player p : Bukkit.getOnlinePlayers()){
			ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
		}
	}
	
	@EventHandler
	public void PlayerMoveEvent (PlayerMoveEvent e){
		Arena ar2 = m.searchPlayer(e.getPlayer());
		if (ar2 != null){
			ar2.callMoveEvent(e);
		}
		else{
			ar2 = m.searchSpec(e.getPlayer());
			if (ar2 != null){
				ar2.callMoveEvent(e);
			}
		}
	}
	
	@EventHandler
	public void sendClick (InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();
		Arena ar2 = m.searchPlayer(p);
		if (ar2 != null){
			ar2.callClickEvent(e);
			if (!ar2.gamestate.equals("waiting")){return;}
			int slotrole = getConfig().getInt("roleitem_slot");
			int slotleave = getConfig().getInt("leaveitem_slot");
			if (p.getInventory().getHeldItemSlot() == slotrole-1 && (getConfig().getBoolean("role_item"))){
				e.setCancelled(true);
			}
			if (p.getInventory().getHeldItemSlot() == slotleave-1 && getConfig().getBoolean("leave_item")){
				e.setCancelled(true);
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
		{
			if (commandLabel.equalsIgnoreCase("ttt")){
				if (args[0].equals("passes")){
					if (args.length < 3){
						return false;
					}
					@SuppressWarnings("deprecation")
					OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
					if (!op.hasPlayedBefore() && !op.isOnline()){
						return false;
					}
					if (args[1].equals("set")){
						int i;
						try{
							i = Integer.parseInt(args[3]);
						}
						catch(Exception ex){
							return false;
						}
						karma.setPasses(op , i);
						return true;
						
					}
					if (args[1].equals("add")){
						int i;
						try{
							i = Integer.parseInt(args[3]);
						}
						catch(Exception ex){
							return false;
						}
						karma.addPasses(op , i);
						return true;
						
					}
					return false;
				}
			}
			sender.sendMessage("You can't use this command, console!");
			return false;
		}
		Player p = (Player)sender;
		if ((commandLabel.equalsIgnoreCase("ttt")) && (args.length > 0))
		{
			if (args[0].equals("setlocation")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 3){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%", "/ttt setlocation <arena> <game,back,spec,tester.inner,tester.outer,lobby>"));
					return true;
				}
				if (!getConfig().getBoolean(args[1] + ".enabled")){
					p.sendMessage(prefix+ " This arena has not been created yet");
					return true;
				}
				
				getConfig().set(args[1] + "." + args[2] + ".x", Double.valueOf(p.getLocation().getX()));
				getConfig().set(args[1] + "." + args[2] + ".y", Double.valueOf(p.getLocation().getY()));
				getConfig().set(args[1] + "." + args[2] + ".z", Double.valueOf(p.getLocation().getZ()));
				getConfig().set(args[1] + "." + args[2] + ".pitch", Float.valueOf(p.getLocation().getPitch()));
				getConfig().set(args[1] + "." + args[2] + ".yaw", Float.valueOf(p.getLocation().getYaw()));
				getConfig().set(args[1] + "." + args[2] + ".world", p.getWorld().getName());
				p.sendMessage(prefix+ " Location "+args[2]+" saved for Arena "+args[1]);
					saveConfig();
				return true;
			}
			else if (args[0].equals("multispawn")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				new MultiSpawnCommand(this).multispawnCommand(p, args);
				return true;
			}
			else if (args[0].equals("start")){
				
				if(!(p.hasPermission("ttt.start"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				
				if (args.length >= 3){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt start [Arena]"));
					return true;
				}
				
				//    		if (getConfig().getBoolean("lobbymode")){
				//    			p.sendMessage(prefix + ChatColor.RED+ " This Feature has not been tested completely with lobbymode acitvated.");
				//    			p.sendMessage(prefix + ChatColor.RED+ " If any Issue occure please report it to the developer of this plugin!");
				//    		}
				
				Arena a;
				if (args.length == 1){
					a = m.searchPlayer(p);
					if (a == null){
						p.sendMessage(mf.getMessage("notingame", true));
						return true;
					}
				}
				else{
					a = this.m.get(args[1]);
					if (a == null){
						p.sendMessage(mf.getMessage("arenadoesntexist", true));
						return true;
					}
				}
				
				String back = a.userStart();
				if (back.equals("complete")){
					p.sendMessage(mf.getMessage("start_complete", true).replace("%arena%", a.name));
				}
				else if (back.equals("missingplayers")){
					p.sendMessage(mf.getMessage("start_missing_players", true).replace("%arena%", a.name));
				}
				else if (back.equals("started")){
					p.sendMessage(mf.getMessage("start_running", true).replace("%arena%", a.name));
				}
				return true;
				
			}
			else if (args[0].equals("autojoin")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 3 && args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt autojoin set <arena> OR /ttt autojoin off"));
					return true;
				}
				if (args[1].equals("set")){
					if (!getConfig().getBoolean(args[2] + ".enabled")){
						p.sendMessage(prefix+ " This arena has not been created yet");
						return true;
					}
					getConfig().set("autojoin", args[2]);
					saveConfig();
					p.sendMessage(prefix+ " Enabled autojoin for Arena "+args[2]+" successfully");
						return true;
				}
				else if (args[1].equals("off")){
					getConfig().set("autojoin", "false");
					saveConfig();
					p.sendMessage(prefix+ " Disabled autojoin successfully");
					return true;
				}
				else{
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt autojoin set <arena> OR /ttt autojoin off"));
					return true;
				}
			}
			else if (args[0].equals("create")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 3){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt create <arena> <minplayers>"));
					return true;
				}
				getConfig().set(args[1] + ".enabled" , true);
				getConfig().set(args[1] + ".min", Integer.parseInt(args[2]));
				@SuppressWarnings("unchecked")
				ArrayList<String> list = (ArrayList<String>)getConfig().getList("arenas");
				if (list == null) {
					list = new ArrayList<String>();
				}
				if (!list.contains(args[1])) {
					list.add(args[1]);
				}
				getConfig().set("arenas", list);
				saveConfig();
				p.sendMessage(prefix+ " Created "+args[1]+" successfully");
				return true;
			}
			else if (args[0].equals("list")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 1){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt list"));
					return true;
				}
				p.sendMessage(mf.getMessage("prefix", true) + " List of loaded arenas:");
				for (String s : m.arenas.keySet()){
					p.sendMessage(mf.getMessage("prefix", true) + " " + s);
				}
				return true;
			}
			else if (args[0].equals("info")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt info <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
					return true;
				}
				else{
					p.sendMessage("");
					p.sendMessage(mf.getMessage("prefix", false) + " Info "+ar.name+":");
					p.sendMessage(mf.getMessage("prefix", false) + " Min-Players: "+ar.min+" Start-Players: "+ar.startplayers);
					for (Player p2 : ar.players){
						p.sendMessage(mf.getMessage("prefix", false) + " Lobby: "+p2.getName());
					}
					for (Player p2 : ar.traitors){
						p.sendMessage(mf.getMessage("prefix", false) + " " + ar.rm.getColoredRole(p2)+" "+p2.getName());
					}
					for (Player p2 : ar.detectives){
						p.sendMessage(mf.getMessage("prefix", false) + " " +ar.rm.getColoredRole(p2)+" "+p2.getName());
					}
					for (Player p2 : ar.innocents){
						p.sendMessage(mf.getMessage("prefix", false) + " " +ar.rm.getColoredRole(p2)+" "+p2.getName());
					}
					for (Player p2 : ar.specs){
						p.sendMessage(mf.getMessage("prefix", false) + " Spec: "+p2.getName());
					}
					p.sendMessage("");
					return true;
				}
				
			}
			else if (args[0].equals("reload")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt reload <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
					return true;
				}
				else{
					if (reloadArena(args[1])){
						p.sendMessage(prefix+ " Reloaded "+args[1]+" successfully");
						return true;
					}
					p.sendMessage(prefix+ " Reloaded "+args[1]+" threw an Error. Please restart the Server");
					return true;
				}
				
			}
			else if (args[0].equals("load")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt load <arena>"));
					return true;
				}
				
				if (m.arenas.containsKey(args[1])){
					p.sendMessage(prefix+ " Arena "+args[1]+" already loaded. Use /ttt reload <arena>");
				}
				else{
					ArrayList list = (ArrayList)getConfig().getList("arenas");
					if (list.contains(args[1])){
						try{
							mountArena(args[1]);
							p.sendMessage(prefix+ " Loaded "+args[1]+" successfully");
							return true;
						}
						catch (Exception e){
							e.printStackTrace();
							p.sendMessage(prefix+ " Reloading "+args[1]+" threw an Error. Use /ttt check <arena>");
							return true;
						}
					}
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
					return true;
				}
				
			}
			else if (args[0].equals("unload")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt unload <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
					return true;
				}
				else{
					if (unmountArena(args[1])){
						p.sendMessage(prefix+ " Unmounted "+args[1]+" successfully! This is not permanent.");
						return true;
					}
					p.sendMessage(prefix+ " Unmounting "+args[1]+" threw an Error. Please restart the Server");
					return true;
				}
				
			}
			else if (args[0].equals("remove")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt remove <arena>"));
					return true;
				}
				else{
					ArrayList list = (ArrayList)getConfig().getList("arenas");
					if (list.contains(args[1])){
						try{
							unmountArena(args[1]);
							list.remove(args[1]);
							getConfig().set("arenas", list);
							getConfig().set(args[1], null);
							saveConfig();
							p.sendMessage(prefix+ " Removed Arena "+args[1]+" successfully");
							return true;
						}
						catch (Exception e){
							p.sendMessage(prefix+ " Failed removing Arena "+args[1]+"!");
							return true;
						}
					}
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
					return true;
				}
				
			}
			else if (args[0].equals("check")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt check <arena>"));
					return true;
				}
				else{
					ArrayList list = (ArrayList)getConfig().getList("arenas");
					if (list.contains(args[1])){
						p.sendMessage("");
						p.sendMessage(prefix + " Arena "+args[1] + " check:");
						p.sendMessage(prefix + ChatColor.BOLD + " Neccesarry:");
						p.sendMessage(prefix + " Location back: " + (getConfig().get(args[1] + ".back.x") != null));
						p.sendMessage(prefix + " Location game: " + (getConfig().get(args[1] + ".game.x") != null));
						p.sendMessage(prefix + " Location spec: " + (getConfig().get(args[1] + ".spec.x") != null));
						p.sendMessage(prefix + ChatColor.BOLD + " Optional:");
						p.sendMessage(prefix + " Location tester: " + (getConfig().get(args[1] + ".tester.x") != null));
						p.sendMessage(prefix + " Location sign: " + (getConfig().get(args[1] + ".sign.x") != null));
						p.sendMessage(prefix + " Location lobby: " + (getConfig().get(args[1] + ".lobby.x") != null));
						p.sendMessage("");
					}
					return true;
				}
				
			}
			else if (args[0].equals("setuptester")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt setuptester <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
				}
				else{
					nts.giveTools(p, ar.name);
				}
				return true;
			}
			else if (args[0].equals("setupoldtester")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt setupoldtester <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
				}
				else{
					ts.giveTools(p, ar.name);
				}
				return true;
			}
			else if (args[0].equals("deleteoldtester")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt deleteoldtester <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
				}
				else{
					getConfig().set(ar.name+".tester", null);
					saveConfig();
					p.sendMessage(mf.getMessage("prefix", false)+" Tester removed succesfully");
				}
				return true;
			}
			else if (args[0].equals("setmax")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 3){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt setmax <arena> <amount>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
				}
				else{
					getConfig().set(ar.name+".max", Integer.parseInt(args[2]));
					saveConfig();
					p.sendMessage(mf.getMessage("prefix", false)+" Set max players of arena " + args[1] + " to " + args[2]);
				}
				return true;
			}
			else if (args[0].equals("join")){
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt join <arena>"));
					return true;
				}
				Arena ar = m.get(args[1]);
				if (ar == null){
					p.sendMessage(mf.getMessage("arenadoesntexist", true));
				}
				else{
					if (m.searchPlayer(p) == null){
						ar.addPlayer(p);
					}
					else{
						p.sendMessage(mf.getMessage("alreadyingame", true));
					}
				}
				return true;
			}
			else if (args[0].equals("stats")){
				if (args.length > 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt stats [Player]"));
					return true;
				}
				OfflinePlayer stats = (OfflinePlayer) p;
				if (args.length == 2){
					stats = Bukkit.getOfflinePlayer(args[1]);
					if (stats.hasPlayedBefore() || stats.isOnline()){
						
					}
					else{
						p.sendMessage(mf.getMessage("unknownplayer", true));
						return true;
					}
				}
				ArrayList<String> statsinfo = new ArrayList<String>();
				statsinfo = (ArrayList<String>) mf.getMessageFetcher().getStringList("stats-command");
				String name = stats.getName();
				int karmat = karma.getKarma(stats);
				String r = rank.getRank(karmat);
				for (String s : statsinfo){
					if (s.contains("%ranking%") && ranker != null){
						s = s.replace("%ranking%" , ranker.getRank(stats.getUniqueId())+"");
					}
					else if (ranker == null && s.contains("%ranking%")){
						s = s.replace("%ranking%" , ChatColor.RED+"StatsAPI needed"+ChatColor.GRAY);
					}
					if (s.contains("%games%")){
						s = s.replace("%games%" , karma.getStatsAPIValue(stats.getUniqueId(), "games"));
					}
					if (s.contains("%traitor%")){
						s = s.replace("%traitor%" , karma.getStatsAPIValue(stats.getUniqueId(), "traitor"));
					}
					if (s.contains("%innocent%")){
						s = s.replace("%innocent%" , karma.getStatsAPIValue(stats.getUniqueId(), "innocent"));
					}
					if (s.contains("%detective%")){
						s = s.replace("%detective%" , karma.getStatsAPIValue(stats.getUniqueId(), "detective"));
					}
					if (s.contains("%kills%")){
						s = s.replace("%kills%" , karma.getStatsAPIValue(stats.getUniqueId(), "kills"));
					}
					if (s.contains("%randomkills%")){
						s = s.replace("%randomkills%" , karma.getStatsAPIValue(stats.getUniqueId(), "randomkills"));
					}
					if (s.contains("%wins%")){
						s = s.replace("%wins%" , karma.getStatsAPIValue(stats.getUniqueId(), "wins"));
					}
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%player%", name).replaceAll("%karma%", karmat+"").replaceAll("%rank%", r).replaceAll("%prefix%", prefix)));
				}
				return true;
			}
			else if (args[0].equals("aleave")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 1){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt leave"));
					return true;
				}
				Arena ar = m.searchPlayer(p);
				if (ar == null){
					ar = m.searchSpec(p);
				}
				if (ar == null){
					p.sendMessage(mf.getMessage("notingame", true));
					return true;
				}
				else{
					if (ar != null){
						ar.leave(p , false);
						m.removeComplete(p);
						return true;
					}
				}
			}
			else if (args[0].equals("configreload")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length != 2){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt configreload <messages/weapons/ranks>"));
					return true;
				}
				if (args[1].equalsIgnoreCase("messages")){
					mf.reloadMessageFile();
					prefix = mf.getMessage("prefix", false);
					p.sendMessage(prefix + " Sucessfully reloaded the messages.yml file!");
				}
				else if (args[1].equalsIgnoreCase("weapons")){
					this.wf.reloadWeaponFile();
					p.sendMessage(prefix + " Loading the Weapons ...");
					this.wf.loadWeapons();
					p.sendMessage(prefix + " Sucessfully reloaded the weapons.yml file!");
					p.sendMessage(prefix + ChatColor.RED + " It is recommenced to reload the running Arenas!");
				}
				else if (args[1].equalsIgnoreCase("ranks")){
					this.rank.reloadRanks();
					p.sendMessage(prefix + " Loading the Ranks ...");
					this.rank.load();
					p.sendMessage(prefix + " Sucessfully reloaded the ranks.yml file!");
					p.sendMessage(prefix + ChatColor.RED + " It is recommenced to reload the running Arenas!");
				}
				else{
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt configreload <messages/weapons/ranks>"));
				}
				return true;
			}
			else if (args[0].equals("passes")){
				if(!(p.hasPermission("ttt.setup"))){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				if (args.length < 3){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt passes <set/get/add> <Player> [amount]"));
					return true;
				}
				OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
				if (!op.hasPlayedBefore()  && !op.isOnline()){
					p.sendMessage(prefix+ " This Player has not played before!");
					return true;
				}
				if (args[1].equals("get")){
					
					p.sendMessage(prefix+ " This Player has " + karma.getPasses(op.getUniqueId()) + " passes !");
					return true;
					
				}
				if (args[1].equals("set")){
					int i;
					try{
						i = Integer.parseInt(args[3]);
					}
					catch(Exception ex){
						p.sendMessage(prefix+" "+ args[3] +" is not a valid number!");
						return true;
					}
					karma.setPasses(op , i);
					p.sendMessage(prefix+ " This Player now has " + karma.getPasses(op.getUniqueId()) + " passes !");
					return true;
					
				}
				if (args[1].equals("add")){
					int i;
					try{
						i = Integer.parseInt(args[3]);
					}
					catch(Exception ex){
						p.sendMessage(prefix+" "+ args[3] +" is not a valid number!");
						return true;
					}
					karma.addPasses(op , i);
					p.sendMessage(prefix+ " This Player now has " + karma.getPasses(op.getUniqueId()) + " passes !");
					return true;
					
				}
				return true;
			}
			else if (args[0].equals("leave")){
				if (args.length != 1){
					p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt leave"));
					return true;
				}
				Arena ar = m.searchPlayer(p);
				Arena ar2 = m.searchSpec(p);
				if (ar == null && ar2 == null){
					p.sendMessage(mf.getMessage("notingame", true));
				}
				else{
					if (!getConfig().getString("bungeeserver").equals("false")){
						System.out.println("Trying to send Player to Server "+getConfig().getString("bungeeserver"));
						ByteArrayOutputStream b = new ByteArrayOutputStream();
						DataOutputStream out = new DataOutputStream(b);
						
						try {
							out.writeUTF("Connect");
							out.writeUTF(getConfig().getString("bungeeserver")); // Target Server
						} catch (IOException e) {
							// Can never happen
						}
						p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
					}
					else{
						if (ar2 != null){
							ar2.leave(p , false);
						}
						else{
							ar.leave(p , false);
						}
					}
					
				}
				return true;
			}
			else if (args[0].equals("admin")){
				if (!p.hasPermission("ttt.setup")){
					p.sendMessage(mf.getMessage("nopermission", true));
					return true;
				}
				
				if (args.length == 1){
					sendAdminHelp(p , 1);
					return true;
				}
				
				try{
					int page = Integer.parseInt(args[1]);
					sendAdminHelp(p , page);
				}
				catch(Exception ex){
					sendAdminHelp(p , 1);
				}
				
				return true;
			}
			else{
				p.performCommand("ttt");
				return true;
			}
		}
		else if (commandLabel.equalsIgnoreCase("ttt")){
			if (getConfig().getBoolean("ttt_help")){
				p.sendMessage("");
				p.sendMessage(prefix + ChatColor.BOLD + " MTTT by MeLays/Schwalboss @ melays.de " + this.getDescription().getVersion());
				p.sendMessage(prefix + " /ttt Arguments:");
				p.sendMessage(prefix + "    join <arena> | Join an Arena");
				p.sendMessage(prefix + "    leave | Leave an Arena");
				if (p.hasPermission("ttt.setup")){
					p.sendMessage(prefix +ChatColor.RED+ "    admin <page> | Adminhelp");
				}
				if (p.hasPermission("ttt.start")){
					p.sendMessage(prefix +ChatColor.AQUA+ "    start [Arena] | Force-Start Arena");
				}
				p.sendMessage(prefix + " /tc <message> | Traitorchat");
				p.sendMessage(prefix + " /shop | Shop");
				p.sendMessage(prefix + " /traitor | Use a Traitorpass");
				p.sendMessage(prefix + " /detective | Use a Detectivepass");
				p.sendMessage(prefix + " /role | Opens the Role-Inventory");
				p.sendMessage("");
			}
			else{
				p.sendMessage(prefix + " MTTT by MeLays/Schwalboss @ melays.de " + this.getDescription().getVersion());
			}
			return true;
		}
		else if (commandLabel.equalsIgnoreCase("tc") && args.length >= 1){
			Arena ar = m.searchPlayer(p);
			if (ar != null){
				if (ar.traitors.contains(p)){
					String msg = "";
					for (String s : args){
						msg+=s+" ";
					}
					for (Player p2 : ar.traitors){
						p2.sendMessage(ChatColor.RED+"T | "+ChatColor.GRAY+p.getName()+" > "+msg);
					}
					return true;
				}
				else{
					p.sendMessage(mf.getMessage("notatraitor", true));
					return true;
				}
			}
			else{
				p.sendMessage(mf.getMessage("notingame", true));
				return true;
			}
		}
		else if ((commandLabel.equalsIgnoreCase("tshop") || commandLabel.equalsIgnoreCase("shop")) && args.length == 0){
			Arena ar = m.searchPlayer(p);
			if (ar != null){
				if (ar.traitors.contains(p)){
					ar.shopgui.open(p);
					return true;
				}
				else if (ar.detectives.contains(p)){
					ar.shopgui.opend(p);
					return true;
				}
				else{
					p.sendMessage(mf.getMessage("notatraitor", true));
					return true;
				}
			}
			else{
				p.sendMessage(mf.getMessage("notingame", true));
				return true;
			}
		}
		else if (commandLabel.equalsIgnoreCase("traitor")){
			if (args.length != 0){
				p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/traitor"));
				return true;
			}
			Arena ar = m.searchPlayer(p);
			if (ar == null){
				p.sendMessage(mf.getMessage("notingame", true));
				return true;
			}
			if (ar.gamestate != "waiting"){
				p.sendMessage(mf.getMessage("alreadyingame", true));
				return true;
			}
			else{
				if (ar.usedpass_detective.contains(p)){
					p.sendMessage(mf.getMessage("alreadydetectivepass", true));
					return true;
				}
				if (ar.usedpass.contains(p)){
					ar.usedpass.remove(p);
					p.sendMessage(mf.getMessage("stoppass", true));
					return true;
				}
				if (karma.getPasses(p.getUniqueId()) >= 1 || p.hasPermission("ttt.unlimited.traitor")){
					ar.usedpass.add(p);
					p.sendMessage(mf.getMessage("requesttraitor", true));
					return true;
				}
				else{
					p.sendMessage(mf.getMessage("nopass", true));
					return true;
				}
			}
		}
		else if (commandLabel.equalsIgnoreCase("detective")){
			if (args.length != 0){
				p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/detective"));
				return true;
			}
			Arena ar = m.searchPlayer(p);
			if (ar == null){
				p.sendMessage(mf.getMessage("notingame", true));
				return true;
			}
			if (ar.gamestate != "waiting"){
				p.sendMessage(mf.getMessage("alreadyingame", true));
				return true;
			}
			else{
				if (ar.usedpass.contains(p)){
					p.sendMessage(mf.getMessage("alreadytraitorpass", true));
					return true;
				}
				if (ar.usedpass_detective.contains(p)){
					ar.usedpass_detective.remove(p);
					p.sendMessage(mf.getMessage("stoppass", true));
					return true;
				}
				if (karma.getPasses(p.getUniqueId()) >= 1 || p.hasPermission("ttt.unlimited.detective")){
					ar.usedpass_detective.add(p);
					p.sendMessage(mf.getMessage("requestdetective", true));
					return true;
				}
				else{
					p.sendMessage(mf.getMessage("nopass", true));
					return true;
				}
			}
		}
		else if (commandLabel.equalsIgnoreCase("role")){
			if (args.length != 0){
				p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/role"));
				return true;
			}
			Arena ar = m.searchPlayer(p);
			if (ar == null){
				p.sendMessage(mf.getMessage("notingame", true));
				return true;
			}
			if (ar.gamestate != "waiting"){
				p.sendMessage(mf.getMessage("alreadyingame", true));
				return true;
			}
			else{
				this.openRoleInv(p);
				return true;
			}
		}
		else{
			p.sendMessage(mf.getMessage("wrongcommandusage", true).replace("%commandhelp%","/ttt"));
			return true;
		}
		
		return false;
	}
	
	public void sendAdminHelp(Player p , int page){
		if (page == 1){
			p.sendMessage("");
			p.sendMessage(prefix + "/ttt Admin-arguments (1/4):");
			p.sendMessage(prefix + "    create <arena> <minplayers> | Create a Arena");
			p.sendMessage(prefix + "    setlocation <arena> <game,back,spec,tester,lobby> | Set a Location");
			p.sendMessage(prefix + "    load/unload/reload/remove <arena>");
			p.sendMessage(prefix + "    autojoin set <arena> OR /ttt autojoin off | Join Arena on Server join");
			p.sendMessage(prefix + "    setuptester <arena> | Setup a tester");
			p.sendMessage("");
		}
		else if (page == 2){
			p.sendMessage("");
			p.sendMessage(prefix + "/ttt Admin-arguments (2/4):");
			p.sendMessage(prefix + "    deletetester <arena> | Setup a tester");
			p.sendMessage(prefix + "    info <arena> | Details about the arena");
			p.sendMessage(prefix + "    check <arena> | Check if the arena is setup correctly");
			p.sendMessage(prefix + "    multispawn | Shows the Multijoin help");
			p.sendMessage(prefix + "    list | List all Arenas");
			p.sendMessage("");
		}
		else if (page == 3){
			p.sendMessage("");
			p.sendMessage(prefix + "/ttt Admin-arguments (3/4):");
			p.sendMessage(prefix + "    aleave | If Bungeemode is true , as a admin you can leave by using this command");
			p.sendMessage(prefix + "    passes ... | Manage Passes");
			p.sendMessage(prefix + "    configreload <messages/weapons/ranks> | Reloads the configurtion");
			p.sendMessage(prefix + "    setmax <arena> <amount> | Sets the max players");
			p.sendMessage(prefix + "    setupoldtester <arena> | Sets the legacy tester");
			p.sendMessage("");
		}
		else if (page == 4){
			p.sendMessage("");
			p.sendMessage(prefix + "/ttt Admin-arguments (4/4):");
			p.sendMessage(prefix + "    deleteoldtester <arena> | Removes the legacy tester");
			p.sendMessage("");
		}
		else{
			sendAdminHelp(p , 1);
		}
	}
	
	@EventHandler
	public void onArrowHit(ProjectileHitEvent event){
		if(event.getEntity() instanceof Arrow){
			Arrow arrow = (Arrow) event.getEntity();
			if(arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				Arena ar = m.searchPlayer(p);
				if (ar != null){
					arrow.remove();
					ar.callArrowHitEvent(event);
				}
			}
		}
	}
	
	//Protection Events
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (ar != null){
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onBlockBreak(BlockPlaceEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (ar != null){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			Arena ar = m.searchPlayer(p);
			if (ar != null){
				if(!(ar.randInt(1,3) == 1)){
					e.setCancelled(true);
				}
				
			}
		}
	}
	
	@EventHandler
	public void PlayerRespawn (final PlayerRespawnEvent e){
		Player p = e.getPlayer();
		final Arena ar = m.searchPlayer(p);
		if (ar != null){
			e.setRespawnLocation(ar.spectator);
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					e.getPlayer().teleport(ar.spectator);
				}
			},5L);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (ar != null){
			ar.leave(p , false);
		}
		m.removeComplete(p);
	}
	
	@EventHandler
	public void onping (ServerListPingEvent e){
		if (getConfig().getString("autojoin") != null){
			Arena a = m.get(getConfig().getString("autojoin"));
			if (a != null && getConfig().getString("bungeemotd").equals("true")){
				if (a.gamestate.equals("waiting")){
					e.setMotd(mf.getMessage("motdwaiting", true));
				}
				else if (a.gamestate.equals("ingame")){
					e.setMotd(mf.getMessage("motdingame", true));
				}
				else if (a.gamestate.equals("end")){
					e.setMotd(mf.getMessage("motdending", true));
				}
				else{
					e.setMotd(a.gamestate);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (ar != null && !ar.specs.contains(p)){
			ar.sendArenaMessage(mf.getMessage("chatingame", true).replace("%player%", p.getName()).replace("%message%", e.getMessage()).replace("%playermarkup%", e.getPlayer().getDisplayName()).replace("%karma%", ar.startkarma.get(p)+"").replace("%rank%", rank.getRank(karma.getKarma(p))));
			e.setCancelled(true);
		}
		else{
			p = e.getPlayer();
			ar = m.searchSpec(p);
			if (ar != null){
				ar.sendSpecMessage(mf.getMessage("chatspec", true).replace("%player%", p.getName()).replace("%message%", e.getMessage()).replace("%playermarkup%", e.getPlayer().getDisplayName()).replace("%karma%", this.karma.getKarma(p)+"").replace("%rank%", rank.getRank(karma.getKarma(p))));
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			Arena ar = m.searchPlayer(p);
			if (ar != null){
				if (ar.gamestate == "waiting" || ar.gamestate == "end"){
					e.setCancelled(true);
				}
				else if (ar.testerdontmove.contains(p)){
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onAS(PlayerArmorStandManipulateEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (ar != null){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onArmorStandBreak(EntityDamageByEntityEvent event)
	{
		final Entity damaged = event.getEntity();
		if ( ! (damaged instanceof ArmorStand)) return;
		if(event.getCause() == DamageCause.PROJECTILE) {
			Arrow a = (Arrow) event.getDamager();
			if(a.getShooter() instanceof Player) {
				Player p = (Player) a.getShooter();
				Arena ar = m.searchPlayer(p);
				if (ar != null){
					event.setCancelled(true);
				}
			}
		}
		if (event.getDamager() instanceof Player){
			Player p = (Player) event.getDamager();
			Arena ar = m.searchPlayer(p);
			if (ar != null){
				event.setCancelled(true);
			}
		}
		
	}
	
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (ar != null){
			ar.callDropEvent(e);
			if (!ar.gamestate.equals("waiting")){return;}
			int slotrole = getConfig().getInt("roleitem_slot");
			int slotleave = getConfig().getInt("leaveitem_slot");
			if (p.getInventory().getHeldItemSlot() == slotrole-1 && (getConfig().getBoolean("role_item"))){
				e.setCancelled(true);
			}
			if (p.getInventory().getHeldItemSlot() == slotleave-1 && getConfig().getBoolean("leave_item")){
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onClick (PlayerInteractEvent e){
		Player p = e.getPlayer();
		Arena ar = m.searchPlayer(p);
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (e.getClickedBlock().getType() == Material.SIGN || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN){
				Sign s = (Sign) e.getClickedBlock().getState();
				if (s.getLine(0).equals(mf.getMessage("signtop", false))){
					p.performCommand("ttt join "+s.getLine(2));
				}
			}
			if (ar != null){
				if (!(ar.gamestate == "end")){
					if (e.getClickedBlock().getType() == Material.ENDER_CHEST && ar.gamestate == "waiting"){
						p.sendMessage(mf.getMessage("enderchestnotinagme", true));
						e.setCancelled(true);
					}
					ar.callClickChest(e);
				}
				else{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void Signit(SignChangeEvent sign) {
		Player p = sign.getPlayer();
		if (sign.getLine(0).equals("[TTT]") && p.hasPermission("ttt.setup")); {
			if (m.arenas.containsKey(sign.getLine(1))){
				Arena a = m.get(sign.getLine(1));
				Location loc = sign.getBlock().getLocation();
				a.setSign(loc);
				String stri = sign.getLine(1);
				getConfig().set(stri + "." + "sign" + ".set", true);
				getConfig().set(stri + "." + "sign" + ".x", Double.valueOf(sign.getBlock().getLocation().getX()));
				getConfig().set(stri + "." + "sign" + ".y", Double.valueOf(sign.getBlock().getLocation().getY()));
				getConfig().set(stri + "." + "sign" + ".z", Double.valueOf(sign.getBlock().getLocation().getZ()));
				getConfig().set(stri + "." + "sign" + ".world", sign.getBlock().getLocation().getWorld().getName());
				saveConfig();
				
			}
		}
	}
	
	@EventHandler
	public void rke (PlayerInteractAtEntityEvent e){
		Player p = e.getPlayer();
		Arena a1 = m.searchPlayer(p);
		if (a1 != null){
			try{
				a1.callRkEEvent(e);
			}
			catch(Exception ex){
				
			}
		}
	}
	
	@EventHandler
	public void inter (PlayerInteractEvent e){
		Player p = e.getPlayer();
		Arena a1 = m.searchPlayer(p);
		if (a1 != null){
			try{
				a1.callInteract(e);
			}
			catch(Exception ex){
				
			}
		}
		a1 = m.searchSpec(p);
		if (a1 != null){
			try{
				a1.callInteract(e);
			}
			catch(Exception ex){
				
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void tele (PlayerTeleportEvent e){
		Player p = e.getPlayer();
		Arena a1 = m.searchPlayer(p);
		if (a1 != null){
			e.getPlayer().eject();
		}
	}
	
	@EventHandler
	public void pvp (EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Arena a1 = m.searchPlayer((Player)e.getDamager());
			Arena a2 = m.searchPlayer((Player)e.getEntity());
			if (a1 != null){
				if (a1 == a2){
					a1.callHitEvent(e);
				}
			}
		}
	}
	
	@EventHandler
	public void farmlandProtect (PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL){
			Arena a2 = m.searchPlayer(p);
			if (a2 != null){
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void playerKill (PlayerDeathEvent e){
		if (e.getEntity().getKiller() != null){
			if (e.getEntity().getKiller() instanceof Player){
				Arena a1 = m.searchPlayer((Player)e.getEntity().getKiller());
				Arena a2 = m.searchPlayer((Player)e.getEntity());
				if (a1 != null){
					if (a1 == a2){
						a1.callDeathByPlayerEvent(e);
						return;
					}
				}
				if (a2 != null){
					a2.callDeathEvent(e);
				}
			}
			else{
				Arena a2 = m.searchPlayer((Player)e.getEntity());
				if (a2 != null){
					a2.callDeathEvent(e);
				}
			}
		}
		else{
			Arena a2 = m.searchPlayer((Player)e.getEntity());
			if (a2 != null){
				a2.callDeathEvent(e);
			}
		}
	}
}
