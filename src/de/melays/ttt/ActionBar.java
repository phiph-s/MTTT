package de.melays.ttt;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBar {
	public ActionBar(){
		
	}
	public void sendActionbar(Player player, String message) {
		  try {
		    Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
		       
		    Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
		    Object packet = constructor.newInstance(icbc, (byte) 2);
		    Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
		    Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

		    playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		  } catch (Exception ex) {
		    ex.printStackTrace();
		  }
	}

	private Class<?> getNMSClass(String name) {
		  try {
		    return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		  } catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    return null;
		  }
	}
	 
	private String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}
