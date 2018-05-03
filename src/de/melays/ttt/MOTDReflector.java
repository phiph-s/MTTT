package de.melays.ttt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class MOTDReflector {
	private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
	    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    String name = "net.minecraft.server." + version + nmsClassString;
	    Class<?> nmsClass = Class.forName(name);
	    return nmsClass;
	}
	public static void setMOTD(String motd) {
		try {
			Method m1 = getNMSClass("MinecraftServer").getMethod("getServer");
			Object mserver = m1.invoke(getNMSClass("MinecraftServer"));
			Method m2 = mserver.getClass().getMethod("setMotd" , String.class);
			m2.invoke(mserver, motd);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			System.out.println("[MTTT] Couldn' set MOTD at net.minecraft.server.XXX.setMotd(...) ...");
		}
		
	}
}
