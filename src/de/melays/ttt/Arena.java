package de.melays.ttt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import de.melays.Shop.Shop;
import de.melays.Shop.ShopGUI;
import de.melays.Shop.ShopItem;
import de.melays.itembuilder.ItemBuilder;
import me.blvckbytes.autonicker.NickSession;
import me.blvckbytes.autonicker.api.NickAPI;

public class Arena
{
  Location spectator;
  Location game;
  Location back;
  public Location testerbutton;
  public Location testerlocation;
  Location lamp1;
  Location lamp2;
  int startplayers= 0;
  boolean ended = false;
  public boolean tester = false;
  boolean testeruse = false;
  ArrayList<Player> spoofs = new ArrayList<Player>();
  ArrayList<Player> testerdontmove = new ArrayList<Player>();
  ArrayList<Block> blocks = new ArrayList<Block>(); 
  ArrayList<Block> eblocks = new ArrayList<Block>(); 
  ArrayList<Entity> creepers = new ArrayList<Entity>();
  String prefix ;
  
  public ArrayList<Player> usedpass = new ArrayList<Player>();
  public ArrayList<Player> usedpass_detective = new ArrayList<Player>();
  
  public ArrayList<Player> specs = new ArrayList<Player>();
  public ArrayList<Player> players = new ArrayList<Player>();
  public ArrayList<Player> detectives = new ArrayList<Player>();
  public ArrayList<Player> traitors = new ArrayList<Player>();
  ArrayList<ArmorStand> stands = new ArrayList<ArmorStand>();
  String traitors_save = "";
  public ArrayList<Player> innocents = new ArrayList<Player>();
  public String gamestate = "waiting";
  int min;
  
  HashMap<Player , ItemStack[]> inventorys = new HashMap<Player , ItemStack[]>();
  HashMap<Player , ItemStack[]> armorinventorys = new HashMap<Player , ItemStack[]>();
  
  HashMap<Player , Integer> startkarma = new HashMap<Player , Integer>();
  
  Team team;
  
  int max_players = 0;
  
  //Countdown
  int counter;
  int counterb = -1;
  
  public RoleManager rm;
  public Shop shop;
  public ShopGUI shopgui;
  
  public main plugin;
  
  public void leaveAll (){
	  for (Player p : new ArrayList<Player>(getPlayerList())){
		  p.teleport(back);
		  leave(p , true);
	  }
	  ArrayList<Player> specss = new ArrayList();
	  specss.addAll(specs);
	  for (Player p : new ArrayList<Player>(specss)){
		  p.setGameMode(GameMode.SURVIVAL);
		  p.teleport(back);
		  leave(p , true);
	  }
	  for (Player p : new ArrayList<Player>(players)){
		  p.setGameMode(GameMode.SURVIVAL);
		  p.teleport(back);
		  leave(p , true);
	  }
  }
  
  boolean forcestart = false;
  
  public String userStart(){
	  
	  if (this.gamestate.equals("waiting")){
		  
		  if (getCompleteList().size() >= 2){
			  if (!lobbymode){
				  forcestart = true;
				  this.counter = 10;  
				  return "complete";
			  }	
			  else{
				  if (lobby){
					  forcestart = true;
					  this.counter = plugin.getConfig().getInt("waitingtime") / 2;
					  lobby = false;
					  this.moveAllFromLobby();
				  }
				  else{
					  return "started";
				  }
			  }
			  return "complete";
		  }
		  
		  else{
			  return "missingplayers";
		  }
		  
	  }
	  else{
		  return "started";
	  }
	
  }
  
  public void callMoveEvent (PlayerMoveEvent e){
	  if (this.testerdontmove.contains(e.getPlayer())){
		  
		  if (e.getFrom().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)){
			  
			  testeruse = false;
			  testerdontmove.remove(e.getPlayer());
			  Bukkit.getScheduler().cancelTask(this.testerscheduler);
			  
		  }
		  else{
			  e.getPlayer().teleport(e.getFrom());
		  }
		  
	  }
	  
	  if (specs.contains(e.getPlayer())){
		  if (!e.getPlayer().getWorld().equals(spectator.getWorld())){
			  e.getPlayer().teleport(spectator);
			  return;
		  }
		  if (e.getPlayer().getLocation().distance(this.spectator) > 130){
			  
			  e.getPlayer().teleport(spectator);
			  
		  }
		  
	  }
  }
  
  Location sign;
  public String name = "";
  
  Location lobbyloc;
  
  public Arena(main m , Location spectator, Location game, Location back , int min , Location sign , String name , Location testerbutton , Location testerlocation , Location lamp1 , Location lamp2)
  {
	this.name = name;
	this.plugin = m;
	this.lamp1 = lamp1;
	this.lamp2 = lamp2;
	this.counter = plugin.getConfig().getInt("waitingtime");
	this.rm = new RoleManager(plugin , this);
	this.shop = new Shop();
	this.shopgui = new ShopGUI(this);
    this.spectator = spectator;
    this.testerbutton = testerbutton;
    this.testerlocation = testerlocation;
    if (plugin.ts.checkTesterSetup(name)){
    	tester = true;
    }
    this.min = min;
    this.sign = sign;
    this.game = game;
    this.back = back;
    this.gamestate = "waiting";
	this.prefix = this.plugin.prefix;
	
	if (plugin.getConfig().getString(name+".max") != null){
		this.max_players = plugin.getConfig().getInt(name+".max");
		System.out.println("[MTTT] Max-Players of " + name + " is " + this.max_players);
	}
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();
	team = board.registerNewTeam(UUID.randomUUID().toString().substring(0, 15));
	if (plugin.getConfig().getBoolean("hidenametag")){
		team.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);
	}
	lobbymode = plugin.getConfig().getBoolean("lobbymode");
	
	if (lobbymode){
		try{
		    double x = plugin.getConfig().getDouble(name + ".lobby.x");
		    double y = plugin.getConfig().getDouble(name + ".lobby.y");
		    double z = plugin.getConfig().getDouble(name + ".lobby.z");
		    double pitch = plugin.getConfig().getDouble(name + ".lobby.pitch");
		    double yaw = plugin.getConfig().getDouble(name + ".lobby.yaw");
		    lobbyloc = new Location(Bukkit.getWorld(plugin.getConfig().getString(name + ".lobby.world")), x, y, z, (float)yaw, (float)pitch);
		    
		    if (lobbyloc != null && plugin.getConfig().get(name + ".lobby.x") != null){
		    	lobby = true;
		    }
		    else{
		    	lobby = false;
		    	lobbymode = false;
		    	System.out.println("[MTTT] Lobbymode couldn't be activated for arena "+name+"! No lobby location set!");
		    }
		}
		catch (Exception ex){
	    	lobby = false;
	    	lobbymode = false;
	    	System.out.println("[MTTT] Lobbymode couldn't be activated for arena "+name+"! No lobby location set!");
		}
	}
	atester = new ArenaTester(plugin , this);
	atester.load();
	if (atester.enabled){
		System.out.println("[MTTT] New Tester activated for arena " + name + " !");
	}
    gameLoop();
  }
  
  public ArenaTester atester;
  
  public void sendRadiusMessage(Player p , String msg){
	  double maxDist = 10;
	  for (Player other : Bukkit.getOnlinePlayers()) {
		  if (other.getWorld().equals(game.getWorld()))
		    if (other.getLocation().distance(p.getLocation()) <= maxDist) {
		      if (getPlayerList().contains(other) || specs.contains(other)){
		    	  other.sendMessage(msg);
		      }
		    }
	  }
  }
  
  public void sendArenaMessage(String msg){
	  for (Player p : specs){
		  p.sendMessage(msg);
	  }
	  for (Player p : players){
		  p.sendMessage(msg);
	  }
	  for (Player p : detectives){
		  p.sendMessage(msg);
	  }
	  for (Player p : traitors){
		  p.sendMessage(msg);
	  }
	  for (Player p : innocents){
		  p.sendMessage(msg);
	  }
  }
  
  public void sendSpecMessage(String msg){
	  for (Player p : specs){
		  p.sendMessage(msg);
	  }
	  if (gamestate == "end"){
		  for (Player p : players){
			  p.sendMessage(msg);
		  }
		  for (Player p : detectives){
			  p.sendMessage(msg);
		  }
		  for (Player p : traitors){
			  p.sendMessage(msg);
		  }
		  for (Player p : innocents){
			  p.sendMessage(msg);
		  }
	  }
  }
  
//  public void refreshTags (){
//	  for (Player p : getPlayerList()){
//		  TagAPI.refreshPlayer(p);
//	  }
//  }
//  
//  public void callNameApi (AsyncPlayerReceiveNameTagEvent e){
//	  System.out.println("called.2");
//	  Player p = e.getPlayer();
//	  Player aim = e.getNamedPlayer();
//	  if (rm.getRole(p).equals("INNOCENT") || rm.getRole(p).equals("DETECTIVE")){
//		  if (rm.getRole(aim).equals("DETECTIVE")){
//			  e.setTag(ChatColor.BLUE+"");
//		  }
//		  else{
//			  e.setTag(ChatColor.YELLOW+"");
//		  }
//	  }
//	  else if(rm.getRole(p).equals("TRAITOR")){
//		  if (rm.getRole(aim).equals("TRAITOR")){
//			  e.setTag(ChatColor.RED+"");
//		  }
//		  else if (rm.getRole(aim).equals("DETECTIVE")){
//			  e.setTag(ChatColor.BLUE+"");
//		  }
//		  else{
//			  e.setTag(ChatColor.GREEN+"");
//		  }
//	  }
//	  else{
//		  e.setTag(ChatColor.GRAY+"");
//	  }
//  }
  
  public void createDeathStand (Player p, Location loc, String role , Player killer , String rolekiller){
      ArmorStand am = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
      am.setVisible(true);
      am.setArms(false);
      am.setCustomName(ChatColor.YELLOW+"Dead Body");
      am.setCustomNameVisible(false);
      am.setGravity(true);
      ItemStack is = new ItemStack (Material.LEATHER_HELMET);
      ItemMeta meta = is.getItemMeta();
      meta.setDisplayName(role+"#"+p.getName());
      is.setItemMeta(meta);
      am.setHelmet(is);
      ItemStack isc = new ItemStack (Material.LEATHER_CHESTPLATE);
      ItemMeta metac = isc.getItemMeta();
      try{
    	  metac.setDisplayName(killer.getName());
      }
      catch (Exception e){
    	  metac.setDisplayName("Nobody");
      }
      isc.setItemMeta(metac);
      am.setChestplate(isc);
      am.setLeggings(new ItemStack (Material.LEATHER_LEGGINGS));
      am.setBoots(new ItemStack (Material.LEATHER_BOOTS));
      stands.add(am);
  }
  
  public ArrayList<Player> getPlayerList(){
	  ArrayList p = new ArrayList<Player>();
	  p.addAll(traitors);
	  p.addAll(innocents);
	  p.addAll(detectives);
	  return p;
  }
  public int getCompleteSize(){
	  return getPlayerList().size() + specs.size() + players.size();
  }
  
  public ArrayList<Player> getCompleteList(){
	  ArrayList p = new ArrayList<Player>();
	  p.addAll(traitors);
	  p.addAll(innocents);
	  p.addAll(detectives);
	  p.addAll(specs);
	  p.addAll(players);
	  return p;
  }
  
  boolean lobbymode = false;
  boolean lobby = false;
  
  public void movetoLobby(Player p){
	  p.teleport(lobbyloc);
  }
  
  public void moveAllFromLobby(){
	  for (Player p : getCompleteList()){
		  if (plugin.ms.checkReady(this)){
			  p.teleport(plugin.ms.randomSpawn(this));
		  }
		  else{
			  p.teleport(game);
		  }
	  }
  }
  
  public boolean checkForceStart(){
	  if (!forcestart){
		  return false;
	  }
	  if (getCompleteList().size() <= 1){
		  return false;
	  }
	  return true;
  }
  
  public static String replaceLast(String text, String regex, String replacement) {
      return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
  }
  
  public String commaString (String s , String strs){
	  String t = s;
	  t = t.replace(" ", ChatColor.translateAlternateColorCodes('&', plugin.mf.getMessage("commacolor", false))+", "+strs);
	  t = replaceLast(t , ", " , ".");
	  return t;
  }
  
  public void gameLoop(){
	  final Arena a = this;
	  plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		  public void run() {
			  for (Player p : getCompleteList()){
				  plugin.sb.setStats(p);
			  }
			  if (gamestate == "waiting"){
				  if(counter == plugin.getConfig().getInt("waitingtime") && players.size() < min){
					  if (lobbymode){
						  lobby = true;
					  }
				  }
				  else{
					  counter -= 1;
				  }
				  traitors_save = "";
				  if (counter == plugin.getConfig().getInt("waitingtime") / 2 && lobby && lobbymode){
					  if (players.size() < min){
						  sendArenaMessage(plugin.mf.getMessage("waiting", true));
						  counter = plugin.getConfig().getInt("waitingtime");
					  }
					  else{
						  moveAllFromLobby();
						  lobby = false;
					  }
				  }
				  if (counter == 0){
					  if (players.size() < min && !(checkForceStart())){
						  sendArenaMessage(plugin.mf.getMessage("waiting", true));
						  counter = plugin.getConfig().getInt("waitingtime");
						  if (lobbymode){
							  lobby = true;
							  for (Block b : blocks){
								  b.setType(Material.CHEST);
								  if (b instanceof Chest){
									  Chest chest = (Chest) b.getState();
									  chest.getInventory().clear();
								  }
							  }
							  for (Block b : eblocks){
								  b.setType(Material.ENDER_CHEST);
							  }
							  for (Player p : getCompleteList()){
								  movetoLobby(p);
								  for (PotionEffect effect : p.getActivePotionEffects()){
									  try {
										  p.removePotionEffect(effect.getType());
									  }
									  catch(Exception ex){
										  
									  }
								  }
								  startkarma.put(p, plugin.karma.getKarma(p));
								  p.setGameMode(GameMode.SURVIVAL);
								  p.getInventory().clear();
								  p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
							  }
						  }
					  }
					  else{
						  ArenaStateChangeEvent event = new ArenaStateChangeEvent(a , "waiting" , "ingame");
						  Bukkit.getServer().getPluginManager().callEvent(event);
						  gamestate = "ingame";
						  counter = plugin.getConfig().getInt("gametime");
						  for (Player p : players){
								plugin.rm.reward(p, "start");
								plugin.karma.addAdvancedStat(p.getUniqueId(), "games", 1);
						  }
						  int slotrole = plugin.getConfig().getInt("roleitem_slot");
						  int slotleave = plugin.getConfig().getInt("leaveitem_slot");
						  if (plugin.getConfig().getBoolean("leave_item")){
							  for (Player p : players){
									p.getInventory().setItem(slotleave-1, new ItemStack(Material.AIR));
							  }
						  }
						  if (plugin.getConfig().getBoolean("role_item")){
							  for (Player p : players){
									p.getInventory().setItem(slotrole-1, new ItemStack(Material.AIR));
							  }
						  }
						  rm.setRoles(usedpass , usedpass_detective);
					  }
				  }
				  else if ((counter % 10 == 0 || counter <= 5) && !(counter == plugin.getConfig().getInt("waitingtime") && players.size() < min)){
					  if (lobby){
						  sendArenaMessage(plugin.mf.getMessage("countdownstart", true).replace("%counter%", Integer.toString(counter- (plugin.getConfig().getInt("waitingtime") / 2))));
					  }
					  else{
						  sendArenaMessage(plugin.mf.getMessage("countdownstart", true).replace("%counter%", Integer.toString(counter)));
					  }
				  }
				  for (Player p : players){
					  p.setGameMode(GameMode.SURVIVAL);
					  if (lobby){
						  setPlayerLevel(p,counter - (plugin.getConfig().getInt("waitingtime") / 2));
					  }
					  else{
						  setPlayerLevel(p,counter);
					  }
					  p.setFoodLevel(20);
				  }
				  if (sign != null){
					  if (sign.getBlock().getType() == Material.SIGN || sign.getBlock().getType() == Material.SIGN_POST || sign.getBlock().getType() == Material.WALL_SIGN){
						  Sign s = (Sign) sign.getBlock().getState();
						  s.setLine(0, plugin.mf.getMessage("signtop", false));
						  s.setLine(1, plugin.mf.getMessage("signwaiting", false).replace("%counter%" , Integer.toString(counter)));
						  s.setLine(2, name);
						  s.setLine(3, plugin.mf.getMessage("signonline", false).replace("%amount%" , Integer.toString(getCompleteSize()).replace("%min%", min+"")));
						  s.update();
					  }
				  }
			  }
			  if (gamestate == "ingame"){
				  counter -= 1;
				  if (randInt(0,50)  == 1){
					  spawnLegendaryBlock();
				  }
				  if (counter == 0){
					  endGame(true);
					  counter = plugin.getConfig().getInt("restarttime");
					  for (Player p : getPlayerList()){
						  p.setHealth(20);
					  }
				  }
				  if (counter == 350 || counter == 60 || counter == 30 || counter == 10 || counter <= 5){
					  sendArenaMessage(plugin.mf.getMessage("countdownend", true).replace("%counter%", Integer.toString(counter)));
				  }
				  if (sign != null){
					  if (sign.getBlock().getType() == Material.SIGN || sign.getBlock().getType() == Material.SIGN_POST || sign.getBlock().getType() == Material.WALL_SIGN){
						  Sign s = (Sign) sign.getBlock().getState();
						  s.setLine(0, plugin.mf.getMessage("signtop", false));
						  s.setLine(1, plugin.mf.getMessage("signingame", false).replace("%counter%" , Integer.toString(counter)));
						  s.setLine(2, name);
						  s.setLine(3, plugin.mf.getMessage("signonline", false).replace("%amount%" , Integer.toString(getCompleteSize()).replace("%min%", min+"")));
						  s.update();
					  }
				  }
				  for (Player p : specs){
					  p.setGameMode(GameMode.SPECTATOR);
				  }
				  for (Player p : getPlayerList()){
					  p.setExp(0);
					  int level = plugin.karma.getKarma(p);
					  setPlayerLevel(p,level);
					  p.setFoodLevel(20);
				  }
			  }
			  if (gamestate == "end"){
				  if (!ended){
					  counter -= 1;
					  if (counter == 0){
						  restartArena();
					  }
					  if (counter == 10){
						  if (plugin.nicknamer){
							  for (String name : realnicks.keySet()){
								  sendArenaMessage(plugin.mf.getMessage("nickresolve", true).replaceAll("%nick%", realnicks.get(name)).replaceAll("%player%", plugin.names.get(UUID.fromString(name))));
							  }
						  }
						  else{
							  sendArenaMessage(plugin.mf.getMessage("countdownrestart", true).replace("%counter%", Integer.toString(counter)));
						  }
					  }
					  if (counter <= 5){
						  sendArenaMessage(plugin.mf.getMessage("countdownrestart", true).replace("%counter%", Integer.toString(counter)));
					  }
					  for (Player p : getPlayerList()){
						  setPlayerLevel(p,counter);
						  p.setFoodLevel(20);
					  }
					  if (sign != null){
						  if (sign.getBlock().getType() == Material.SIGN || sign.getBlock().getType() == Material.SIGN_POST || sign.getBlock().getType() == Material.WALL_SIGN){
							  Sign s = (Sign) sign.getBlock().getState();
							  s.setLine(0, plugin.mf.getMessage("signtop", false));
							  s.setLine(1, plugin.mf.getMessage("signending", false).replace("%counter%" , Integer.toString(counter)));
							  s.setLine(2, name);
							  s.setLine(3, plugin.mf.getMessage("signonline", false).replace("%amount%" , Integer.toString(getCompleteSize()).replace("%min%", min+"")));
							  s.update();
						  }
					  }
				  }
			  }
		  }
	  }, 0L, 20L);
  }
  
  public void karmaToLevel (Player p){
	  setPlayerLevel(p , plugin.karma.getKarma(p));
  }
  
  public void setPlayerLevel (Player p , int level){
	  p.setExp(0);
	  p.setLevel(0);
	  p.setLevel(level);
  }
  
  public int getAmountPlaying (){
	  return players.size() + traitors.size() + detectives.size() + innocents.size();
  }
  
  public void callHitEvent(EntityDamageByEntityEvent e){
	  Player hitter = (Player)e.getDamager();
	  Player aim = (Player)e.getEntity();
	  if (gamestate == "waiting" || gamestate == "end"){
		  e.setCancelled(true);
	  }
  }
  
  public void movetoBungeeServer(Player p){
	  	Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		System.out.println("Trying to send Player to Server "+plugin.getConfig().getString("bungeeserver"));
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		 
		try {
		    out.writeUTF("Connect");
		    out.writeUTF(plugin.getConfig().getString("bungeeserver"));
		} catch (IOException e) {

		}
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
  }
  
  public void restartArena (){
	  fixPlayers();
	  if (plugin.getConfig().getBoolean("lobbymode")){
		  if (!plugin.getConfig().getString("bungeeserver").equals("false")){
			  for (Player p : getCompleteList()){
				  movetoBungeeServer(p);
			  }
		  }
		  this.leaveAll();
	  }
	  for (Player p : getCompleteList()){
		  plugin.rm.reward(p, "end");
	  }
	  if (plugin.getConfig().getBoolean("stopserver")){
		  Bukkit.getServer().shutdown();
	  }
	  players.addAll(traitors);
	  players.addAll(innocents);
	  players.addAll(detectives);
	  for (Player p : specs){
		  p.setGameMode(GameMode.SURVIVAL);
	  }
	  players.addAll(specs);
	  innocents = new ArrayList<Player>();
	  detectives = new ArrayList<Player>();
	  specs = new ArrayList<Player>();
	  traitors = new ArrayList<Player>();
	  realnicks = new HashMap<String , String>();
	  for (Player p : players){
		  for (PotionEffect effect : p.getActivePotionEffects()){
			  try {
				  p.removePotionEffect(effect.getType());
			  }
			  catch(Exception ex){
				  
			  }
		  }
		  startkarma.put(p, plugin.karma.getKarma(p));
		  p.setGameMode(GameMode.SURVIVAL);
		  if (lobbymode){
			  movetoLobby(p);
		  }
		  else if (plugin.ms.checkReady(this)){
			  p.teleport(plugin.ms.randomSpawn(this));
		  }
		  else{
			  p.teleport(game);
		  }
		  p.getInventory().clear();
		  p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
		  if (plugin.nicknamer){
			  NickSession session = NickAPI.getSession(p);
			  String nick = session.getRandomNickname();
			  session.setName(nick, true);
			  realnicks.put(session.holder.getUniqueId().toString() , session.current_nick);
			  p.sendMessage(plugin.mf.getMessage("randomnick", true).replaceAll("%nick%", session.current_nick));
		  }
	  }
	  ArenaStateChangeEvent event = new ArenaStateChangeEvent(this , "end" , "waiting");
	  Bukkit.getServer().getPluginManager().callEvent(event);
	  gamestate = "waiting";
	  counter = plugin.getConfig().getInt("waitingtime");
	  for (Player p : this.getCompleteList()){
	  	  try{
	  		  ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
	  	  }
	  	  catch(Exception ex){
	  		  
	  	  }
	  }
  }
  
  public void callClickEvent (InventoryClickEvent e){
	  if (e.getInventory().getName().equals("Shop") || e.getWhoClicked().getOpenInventory().getTopInventory().getName().equals("Shop")){
		  e.setCancelled(true);
		  if (e.getInventory().getName().equals("Shop")){
			  shopgui.callClickEvent(e);
		  }
	  }
  }
  
  public void fixPlayers(){
	  ArrayList<Player> all = new ArrayList(getPlayerList());
	  all.addAll(specs);
	  for (Player p : all){
		  for (Player p2 : all){
			  if (p == p2){
				  for (Player pp : new ArrayList<Player>(traitors)){
					  if (pp == p2){
						  traitors.remove(pp);
					  }
				  }
				  for (Player pp : new ArrayList<Player>(innocents)){
					  if (pp == p2){
						  innocents.remove(pp);
					  }
				  }
				  for (Player pp : new ArrayList<Player>(detectives)){
					  if (pp == p2){
						  detectives.remove(pp);
					  }
				  }
				  for (Player pp : new ArrayList<Player>(specs)){
					  if (pp == p2){
						  specs.remove(pp);
					  }
				  }
				  for (Player pp : new ArrayList<Player>(players)){
					  if (pp == p2){
						  players.remove(pp);
					  }
				  }
				  players.add(p2);
			  }
		  }
	  }
  }
  
  public void callDeathByPlayerEvent(PlayerDeathEvent  e){
	  e.setDeathMessage(null);
	  Player aim = e.getEntity();
	  Player killer = e.getEntity().getKiller();
	  createDeathStand(aim , aim.getLocation() , rm.getRole(aim) , killer , rm.getRole(killer));
	  for (ItemStack i : e.getDrops()){
		  dropped.add(aim.getWorld().dropItem(aim.getLocation(), i));
	  }
	  e.getDrops().clear();
	  e.setDroppedExp(0);
	  rm.dropKillMessage(aim, killer);
	  aim.sendMessage(plugin.mf.getMessage("playerdiedspec", true));
	  try {
		new BypassRespawnAPI().sendRespawnPacket(aim);
	  } catch (Exception e1) {

	  }
	  rm.removePlayer(aim);
	  setSpec(aim);
	  String end = rm.checkEnd();
	  if (end != null){
		  endGame(false);
	  }
  }
  
  ArrayList<Item> dropped = new ArrayList<Item>();
  public void callDropEvent (PlayerDropItemEvent e){
	  if (plugin.getConfig().getBoolean("itemdrop")){
		  dropped.add(e.getItemDrop());
	  }
	  else{
		  e.setCancelled(true);
	  }
  }
  
  public void removeItems(){
	  for (Item i : dropped){
		  if (i != null){
			  i.remove();
		  }
	  }
	  dropped = new ArrayList<Item>();
  }
  
  public void callInteract (PlayerInteractEvent e){
	  
	  if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
		  
		  	Player p = e.getPlayer();
			if ( p.getItemInHand().getType().equals(Material.getMaterial(plugin.getConfig().getString("role_item_type"))) && this.gamestate.equals("waiting")){
				p.performCommand("role");
				e.setCancelled(true);
			}
			if ( p.getItemInHand().getType().equals(Material.getMaterial(plugin.getConfig().getString("leave_item_type"))) && this.gamestate.equals("waiting")){
				p.performCommand("ttt leave");
				e.setCancelled(true);
			}
		  
	  }
	  
  }
  
  int testerscheduler = 0;
  
  public ArrayList<Location> getCircle(Location center, double radius, int amount)
  {
      World world = center.getWorld();
      double increment = (2 * Math.PI) / amount;
      ArrayList<Location> locations = new ArrayList<Location>();
      for(int i = 0;i < amount; i++)
      {
          double angle = i * increment;
          double x = center.getX() + (radius * Math.cos(angle));
          double z = center.getZ() + (radius * Math.sin(angle));
          locations.add(new Location(world, x, center.getY(), z));
      }
      return locations;
  }
  
  HashMap<Location,Integer> legendscheduler = new HashMap<Location,Integer>();
  HashMap<Integer,Integer> position = new HashMap<Integer,Integer>();
  public void spawnLegendaryBlock(){
	  try{
		  Collections.shuffle(eblocks);
		  Location loc = eblocks.get(0).getLocation();
		  double x = loc.getX();
		  double z = loc.getZ();
		  Location particle = loc.clone();
		  particle.add(0, 0.5, 1);
		  particle.add(x > 0 ? 0.5 : -0.5, 0.0, z > 0 ? 0.5 : -0.5);
		  ArrayList<Location> particles = getCircle(particle, 1 , 25);
		  if (loc.getBlock().getType() == Material.SEA_LANTERN || loc.getBlock().getType() == Material.REDSTONE_BLOCK) return;
		  loc.getBlock().setType(Material.SEA_LANTERN);
		  plugin.sd.playSound(particle.getWorld() , particle , "EXPLODE", "ENTITY_ENDERDRAGON_FIREBALL_EXPLODE" );
	      BukkitScheduler scheduler = plugin.getServer().getScheduler();
	      int id = 0;
	      id = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
	          @Override
	          public void run() {
	        	  if (loc.getBlock().getType().equals(Material.SEA_LANTERN)){
	        		  loc.getBlock().setType(Material.REDSTONE_BLOCK);
	        	  }
	        	  else if (loc.getBlock().getType().equals(Material.REDSTONE_BLOCK)){
	        		  loc.getBlock().setType(Material.SEA_LANTERN);
	        	  }
	        	  else{
	        		  Bukkit.getScheduler().cancelTask(legendscheduler.get(loc));
	        	  }
	        	  int id = legendscheduler.get(loc);
	        	  particles.get(position.get(id)).getWorld().playEffect(particles.get(position.get(id)), Effect.HAPPY_VILLAGER, 2);
	        	  if (position.get(id) == 24){
	        		  position.put(id, 0);
	        	  }
	        	  else{
	        		  position.put(id, position.get(id) + 1 );
	        	  }
	          }
	      }, 3L, 3L);
	      position.put(id, 0);
	      legendscheduler.put(loc , id);
	  }catch(Exception ex){}
  }
  
  public boolean isLegendary (Location loc){
	  for (Block b : this.eblocks){
		  if (b.getLocation().getBlock().getLocation().equals(loc.getBlock().getLocation())) return true;
	  }
	  return false;
  }
  
  public void callClickChest (final PlayerInteractEvent e){
	  if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
		  if (!(counter == plugin.getConfig().getInt("waitingtime") && players.size() < min)){
				  if (e.getClickedBlock().getType() == Material.CHEST){
					  blocks.add(e.getClickedBlock());
					  if (plugin.wf.giveRandomChestItem(e.getPlayer() , "chest")){
						  e.getClickedBlock().setType(Material.AIR);
						  plugin.sd.playSound(e.getPlayer() , "CHEST_OPEN", "BLOCK_CHEST_OPEN" );
						  //e.getPlayer().playSound(e.getClickedBlock().getLocation(), Sound.CHEST_OPEN, 1, 1);
					  }
					  e.setCancelled(true);
				  }
				  else if (e.getClickedBlock().getType() == Material.ENDER_CHEST && gamestate != "waiting"){
					  eblocks.add(e.getClickedBlock());
					  if (plugin.wf.giveRandomChestItem(e.getPlayer() , "ec")){
						  e.getClickedBlock().setType(Material.AIR);
						  plugin.sd.playSound(e.getPlayer() , "CHEST_OPEN", "BLOCK_CHEST_OPEN" );
						  //e.getPlayer().playSound(e.getClickedBlock().getLocation(), Sound.CHEST_OPEN, 1, 1);
					  }
					  e.setCancelled(true);
				  }
				  else if ((e.getClickedBlock().getType() == Material.SEA_LANTERN || e.getClickedBlock().getType() == Material.REDSTONE_BLOCK)&& isLegendary(e.getClickedBlock().getLocation()) && gamestate != "waiting"){
					  eblocks.add(e.getClickedBlock());
					  if (plugin.wf.giveRandomChestItem(e.getPlayer() , "legend")){
						  e.getClickedBlock().setType(Material.AIR);
						  plugin.sd.playSound(e.getPlayer() , "LEVEL_UP", "ENTITY_PLAYER_LEVELUP" );
						  //e.getPlayer().playSound(e.getClickedBlock().getLocation(), Sound.CHEST_OPEN, 1, 1);
					  }
					  e.setCancelled(true);
				  }
				  else if (e.getClickedBlock().getType() == Material.STONE_BUTTON){
					  if (this.atester.isButton(e.getClickedBlock().getLocation()) && atester.enabled){
						  this.atester.testPlayer(e.getPlayer());
					  }
					  else if (tester){
						  if (!testeruse){
							  if (startplayers >= plugin.getConfig().getInt("minplayerstotest")){
								  if (e.getClickedBlock().getLocation().equals(testerbutton) && gamestate.equals("ingame")){
									  e.getPlayer().teleport(this.testerlocation);
									  testerdontmove = new ArrayList<Player>();
									  testerdontmove.add(e.getPlayer());
									  sendRadiusMessage(e.getPlayer(), plugin.mf.getMessage("enteredtester", true).replace("%player%", e.getPlayer().getName()));
									  testeruse = true;
									  
									  
									  this.testerscheduler = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										  public void run() {
											 testeruse = false;
											 testerdontmove.remove(e.getPlayer());
											 try{
												 if (traitors.contains(e.getPlayer())){
													 if (!e.getPlayer().getInventory().contains(new ShopItem().getSpoofer(false))){
														 testerLamp();
													 }
													 else{
														 e.getPlayer().getInventory().remove(new ShopItem().getSpoofer(false));
														 e.getPlayer().sendMessage(plugin.mf.getMessage("spoofer", true));
													 }
												 }
											 }
											 catch(Exception e){
												 
											 }
										  }
									  }, 100L);
								  }
								  else{
									  if (!traitors.contains(e.getPlayer())){
										  e.setCancelled(true);
									  }
								  }
							  }
							  else{
								  if (e.getClickedBlock().getLocation().equals(testerbutton) && gamestate.equals("ingame")){
									  e.getPlayer().sendMessage(plugin.mf.getMessage("testerdisabled", true));
								  }
								  else if (!traitors.contains(e.getPlayer())){
									  e.setCancelled(true);
								  }
								  
							  }
						  }
					  }
					  else{
						  if (!traitors.contains(e.getPlayer())){
							  e.setCancelled(true);
						  }
					  }
				  }
			  }
		  else if (e.getClickedBlock().getType() == Material.CHEST){
			  plugin.sd.playSound(e.getPlayer() , "CLICK", "BLOCK_LEVER_CLICK" );
			  //e.getPlayer().playSound(e.getClickedBlock().getLocation(), Sound.CLICK, 1, 1);
			  e.setCancelled(true);
		  }
		  else if (!traitors.contains(e.getPlayer())){
			  e.setCancelled(true);
		  }
	  }
  }
  
  public void tryDetectiveResearch(Player p , ArmorStand a){
	  p.sendMessage(plugin.mf.getMessage("detectiveresearch", true).replace("%killer%", a.getChestplate().getItemMeta().getDisplayName()));
  }
  
  
  public void removeInventoryItems(PlayerInventory inv, Material type, int amount) {
      for (ItemStack is : inv.getContents()) {
          if (is != null && is.getType() == type) {
              int newamount = is.getAmount() - amount;
              if (newamount > 0) {
                  is.setAmount(newamount);
                  break;
              } else {
                  inv.remove(is);
                  amount = -newamount;
                  if (amount == 0) break;
              }
          }
      }
  }
  
public void callArrowHitEvent (ProjectileHitEvent e){
	  Player p = (Player)e.getEntity().getShooter();

	  if (p.getInventory().contains(Material.TNT) && traitors.contains(p)){
		  
		  removeInventoryItems(p.getInventory() , Material.TNT , 1);
		  plugin.sd.playSound(e.getEntity().getWorld(), e.getEntity().getLocation() , "EXPLODE", "ENTITY_GENERIC_EXPLODE" );
		  e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.EXPLOSION , 17);
		  //e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 2, 1);
		  List<Entity> list = e.getEntity().getNearbyEntities(1.5, 1.5, 1.5);
		  for (Entity en : list){
			  if (en instanceof Player){
				  ((Player) en).addPotionEffect(new PotionEffect (PotionEffectType.HARM , 1 , 1));
			  }
		  }
		  
	  }
  }
  
  public void testerLamp (){
	  lamp1.getBlock().setType(Material.GLOWSTONE);
	  lamp2.getBlock().setType(Material.GLOWSTONE);
	  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		  public void run() {
			  lamp1.getBlock().setType(Material.REDSTONE_LAMP_OFF);
			  lamp2.getBlock().setType(Material.REDSTONE_LAMP_OFF);
		  }
	  }, 40L);
  }
  
  public static int randInt(int min, int max) {
	  Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
  
  public String getRoleColor (String role){
	  if (role.equals("TRAITOR")){
		  return ChatColor.RED+"";
	  }
	  if (role.equals("INNOCENT")){
		  return ChatColor.GREEN+"";
	  }
	  if (role.equals("DETECTIVE")){
		  return ChatColor.BLUE+"";
	  }
	  else{
		  return "";
	  }
  }
  
  public void callRkEEvent(PlayerInteractAtEntityEvent  e){
	  if (e.getRightClicked().getType() == EntityType.ARMOR_STAND && e.getPlayer().isSneaking() && e.getPlayer().getPassenger() == null){
		  e.getPlayer().setPassenger(e.getRightClicked());
	  }
	  else if (e.getPlayer().isSneaking() || e.getPlayer().getPassenger() != null){
		  e.getPlayer().eject();
	  }
	  else if (e.getRightClicked().getType() == EntityType.ARMOR_STAND){
		  ArmorStand am = (ArmorStand)e.getRightClicked();
		  if (detectives.contains(e.getPlayer()) && e.getPlayer().getItemInHand().getType().equals(Material.STICK)){
			  try{
				  tryDetectiveResearch(e.getPlayer() , am);
				  this.removeInventoryItems(e.getPlayer().getInventory(), Material.STICK , 1);
			  }
			  catch(Exception ex){}
		  }
		  else{
			  e.setCancelled(true);
			  if (plugin.getConfig().getBoolean("only_detective_corpsescan")){
				  if (!detectives.contains(e.getPlayer())){
					  return;
				  }
			  }
			  ItemStack helmet = am.getHelmet();
			  String name = helmet.getItemMeta().getDisplayName();
			  String[] parts = name.split("#");
			  String role = parts[0];
			  String player = parts[1];
			  if (role.equals("TRAITOR") || role.equals("INNOCENT") || role.equals("DETECTIVE")){
				  if (am.getHelmet().getType().equals(Material.LEATHER_HELMET)){
					  sendArenaMessage(plugin.mf.getMessage("corpsefound", true).replace("%role%", plugin.getDisplay(role , false)).replace("%player%", player));
				  }
				  else{
					  e.getPlayer().sendMessage(plugin.mf.getMessage("corpsefoundtwice", true).replace("%role%", plugin.getDisplay(role , false)).replace("%player%", player));
				  }
				  ItemStack skull = new ItemStack(Material.SKULL_ITEM);
				  String headName = player;
				  skull.setDurability((short)3);
				  SkullMeta sm = (SkullMeta)skull.getItemMeta();
				  sm.setOwner(headName);
				  sm.setDisplayName(name);
				  skull.setItemMeta(sm);
				  am.setHelmet(skull);
				  am.setCustomName(getRoleColor(role)+player);
				  am.setCustomNameVisible(true);
				  e.setCancelled(true);
			  }
			  e.setCancelled(true);
		  }
	  }
  }
  
  public void callDeathEvent(PlayerDeathEvent  e){
	  Player p = e.getEntity();
	  if (!specs.contains(p)){
		  e.setDeathMessage(null);
		  rm.dropKillMessage(p, null);
		  for (ItemStack i : e.getDrops()){
			  dropped.add(p.getWorld().dropItem(p.getLocation(), i));
		  }
		  e.getDrops().clear();
		  e.setDroppedExp(0);
		  createDeathStand(p , p.getLocation() , rm.getRole(p) , null , null);
		  try {
				new BypassRespawnAPI().sendRespawnPacket(p);
		  } catch (Exception e1) {

		  }
		  rm.removePlayer(p);
		  setSpec(p);
		  String end = rm.checkEnd();
		  if (end != null){
			  if (end == "traitors"){
				  endGame(false);
			  }
			  else{
				  endGame(false);
			  }
		  }
	  }
  }
  
  public void leave(Player p , boolean silent){
	  if (!silent){
		  rm.dropKillMessage(p, null);
	  }
	  rm.removePlayer(p);
	  p.eject();
	  p.setGameMode(GameMode.SURVIVAL);
	  p.teleport(back);
	  p.getInventory().clear();
	  p.setHealth(20);
	  p.setLevel(0);
	  String end = rm.checkEnd();
	  if (end != null){
		  if (end == "traitors"){
			  endGame(false);
		  }
		  else{
			  endGame(false);
		  }
	  }
	  
	  if (plugin.getConfig().getBoolean("hide_players_outside_arena")){
		  for (Player p2 : this.getCompleteList()){
			  p2.hidePlayer(p);
		  }
		  
		  for (Player p2 :Bukkit.getOnlinePlayers()){
			  
			  if (plugin.m.searchPlayer(p2) == null){
				  p.showPlayer(p2);
			  }
			  
		  }
	  }
	  
	  p.getInventory().setContents(inventorys.get(p));
	  p.getInventory().setArmorContents(armorinventorys.get(p));
	  plugin.sb.removeBoard(p);
	  team.removePlayer(p);
  }
  
  public void setSign (Location sign){
	  this.sign = sign;
  }
  
  public void endGame(boolean preend){
	  ArenaStateChangeEvent event = new ArenaStateChangeEvent(this , "ingame" , "end");
	  Bukkit.getServer().getPluginManager().callEvent(event);
	  gamestate = "end";
	  counter = plugin.getConfig().getInt("restarttime");
	  shop = new Shop();
	  spoofs = new ArrayList<Player>();
	  for (Player p : getPlayerList()){
		  p.eject();
		  p.getInventory().clear();
		  p.setLevel(0);
	  }
	  for (Block b : blocks){
		  b.setType(Material.CHEST);
		  if (b instanceof Chest){
			  Chest chest = (Chest) b.getState();
			  chest.getInventory().clear();
		  }
	  }
	  for (Block b : eblocks){
		  b.setType(Material.ENDER_CHEST);
	  }
	  for (Entity e : creepers){
		  if (e != null){
			  e.remove();
		  }
	  }
	  for (ArmorStand am : stands){
		  am.remove();
	  }
	  removeItems();
//	  for (Player p : detectives){
//		  rm.boardi.resetScores(p);
//	  }
//	  for (Player p : innocents){
//		  rm.boardi.resetScores(p);
//	  }
  }
  
  public String state()
  {
    return this.gamestate;
  }
  
  public void addPlayer(Player p)
  {
		  join (p);
  }
  
  public void setSpec (final Player p){
	  if (plugin.getConfig().getBoolean("spectatormode")){
		  specs.add(p);
		  p.eject();
		  p.setGameMode(GameMode.SPECTATOR);
		  p.teleport(spectator);
	  	  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			  public void run() {
		          p.setGameMode(GameMode.SPECTATOR);
		          p.teleport(spectator);
			  }
		  },5L);
	  	  try{
	  		  ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
	  	  }
	  	  catch(Exception ex){
	  		  
	  	  }
	  }
	  else{
		  this.leave(p, true);
	  }

  }
  
  HashMap<String , String> realnicks = new HashMap<String , String>();
  
  public void join(Player p){
	  if (this.max_players != 0){
		  if (this.max_players <= this.getCompleteSize()){
			  if (!p.hasPermission("ttt.premiumjoin")){
				  if (!plugin.getConfig().getString("bungeeserver").equals("false")){
  					System.out.println("[MTTT] Trying to send Player to Server "+plugin.getConfig().getString("bungeeserver"));
  					ByteArrayOutputStream b = new ByteArrayOutputStream();
  					DataOutputStream out = new DataOutputStream(b);
  					 
  					try {
  					    out.writeUTF("Connect");
  					    out.writeUTF(plugin.getConfig().getString("bungeeserver"));
  					} catch (IOException e) {
  					    
  					}
  					p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
  					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
  						public void run() {
  							if (p.isOnline()){
  								p.kickPlayer(plugin.prefix + ChatColor.RED + " This Server is full!");
  							}
  						}
  					}, 20L);
  				}  
				  else{
					  p.sendMessage(plugin.mf.getMessage("arenafull", true));
				  }
				return;
			  }
		  }
	  }
	  inventorys.put(p, p.getInventory().getContents());
	  startkarma.put(p, plugin.karma.getKarma(p));
	  armorinventorys.put(p, p.getInventory().getArmorContents());
	  if (plugin.nicknamer){
		  NickSession session = NickAPI.getSession(p);
		  String nick = session.getRandomNickname();
		  session.setName(nick, true);
		  realnicks.put(session.holder.getUniqueId().toString() , session.current_nick);
		  p.sendMessage(plugin.mf.getMessage("randomnick", true).replaceAll("%nick%", session.current_nick));
	  }
	  team.addPlayer(p);
	  if (gamestate.equals("waiting")){
		  sendArenaMessage(plugin.mf.getMessage("join", true).replace("%player%", p.getName()));
		  players.add(p);
		  p.getInventory().clear();
		  p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
		  int slotrole = plugin.getConfig().getInt("roleitem_slot");
		  int slotleave = plugin.getConfig().getInt("leaveitem_slot");
		  if (plugin.getConfig().getBoolean("role_item")){
			  p.getInventory().setItem(slotrole-1, new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("role_item_type"))).setName(plugin.mf.getMessage("role_item", true)).toItemStack());
		  }
		  if (plugin.getConfig().getBoolean("leave_item")){
			  p.getInventory().setItem(slotleave-1, new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("leave_item_type"))).setName(plugin.mf.getMessage("leave_item", true)).toItemStack());
		  }
		  if (lobby){
			  this.movetoLobby(p);
		  }
		  else if (plugin.ms.checkReady(this)){
			  p.teleport(plugin.ms.randomSpawn(this));
		  }
		  else{
			  p.teleport(game);
		  }
	  }
	  else{
		  if (plugin.getConfig().getBoolean("spectatormode")){
			  setSpec(p);
		  }
		  else{
			  p.sendMessage(plugin.mf.getMessage("specdisabled", true));
		  }
	  }
	  plugin.sb.createBoard(p);
	  if (plugin.getConfig().getBoolean("hide_players_outside_arena")){
		  for (Player p2 : Bukkit.getOnlinePlayers()){
			  p.hidePlayer(p2);
		  }
		  for (Player p2 : this.getCompleteList()){
			  p.showPlayer(p2);
			  p2.showPlayer(p);
		  }
	  }
  }
  
  
}

