package de.melays.ttt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ColorTabAPI {
	
	static Map< UUID, String > tabTeam = new HashMap<>();

	public static void setHeaderAndFooter( List< String > headerLines, List< String > footerLines, Collection< ? extends Player > receivers ) {
		try{
			if( headerLines == null ) headerLines = new ArrayList<>();
			if( footerLines == null ) footerLines = new ArrayList<>();
			
			try {
				Constructor<?> constructor = getNMSClass( "PacketPlayOutPlayerListHeaderFooter" ).getConstructor();
				Object packet = constructor.newInstance();
							
				Object headerComponent = getNMSClass( "IChatBaseComponent" ).getDeclaredClasses()[ 0 ].getMethod( "a", String.class ).invoke( null, "{\"text\":\"" + listToString( headerLines ) + "\"}" );
				Object footerComponent = getNMSClass( "IChatBaseComponent" ).getDeclaredClasses()[ 0 ].getMethod( "a", String.class ).invoke( null, "{\"text\":\"" + listToString( footerLines )+ "\"}" );
				
				setField( packet, "a", headerComponent );
				setField( packet, "b", footerComponent );
				
				for( Player t : receivers ) sendPacket( t, packet );
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
	public static void setTabStyle( Player p, String prefix, String suffix, int priority, Collection< ? extends Player > receivers ) {
		try{
			if( prefix == null ) prefix = "";
			if( suffix == null ) suffix = "";
			
			try {
				String teamName = priority + UUID.randomUUID().toString();
				
				if( teamName.length() > 16 ) teamName = teamName.substring( 0, 16 );
				
				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();
				
				List< String > contents = new ArrayList<>();
				contents.add( p.getName() );
				
				setField( packet, "a", teamName );
				setField( packet, "b", teamName );
				setField( packet, "c", prefix );
				setField( packet, "d", suffix );
				setField( packet, "e", "ALWAYS" );
				setField( packet, "h", 0 );
				setField( packet, "g", contents );
				
				for( Player t : receivers ) sendPacket( t, packet );
				tabTeam.put( p.getUniqueId(), teamName );
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
	public static void clearTabStyle( Player p, Collection< ? extends Player > receivers ) {
		try{
		
			if( !tabTeam.containsKey( p.getUniqueId() ) )
				tabTeam.put( p.getUniqueId(), "nothing" );
			
			String teamName = tabTeam.get( p.getUniqueId() );
			
			try {
				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();
	
				List< String > contents = new ArrayList<>();
				contents.add( p.getName() );
				
				setField( packet, "a", teamName );
				setField( packet, "b", teamName );
				setField( packet, "e", "ALWAYS" );
				setField( packet, "h", 1 );
				setField( packet, "g", contents );
				
				for( Player t : receivers ) sendPacket( t, packet );
				tabTeam.put( p.getUniqueId(), teamName );
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
	/* This Methods are unreachable from outside of the API because they are
	 * only relevant for processing the given Data 
	*/
	
	private static String listToString( List< String > list ) {
		String output = "";
		for( String s : list ) {
			output += s.replace( "&", "§" ) + "\n";
		}
		return output.length() > 0 ? output.substring( 0, output.length() -1 ) : output;
	}
	
	private static Class< ? > getNMSClass( String name ) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
		try {
			return Class.forName( "net.minecraft.server." + version + "." + name );
		} catch ( Exception e ) {
			
		}
		return null;
	}
	
	private static void sendPacket( Player to, Object packet ) {
		try {
			Object playerHandle = to.getClass().getMethod( "getHandle" ).invoke( to );
			Object playerConnection = playerHandle.getClass().getField( "playerConnection" ).get( playerHandle );
			playerConnection.getClass().getMethod( "sendPacket", getNMSClass( "Packet" ) ).invoke( playerConnection, packet );
		} catch ( Exception e ) {
			
		}
	}
	
	private static void setField( Object change, String name, Object to ) {
		try {
			Field field = change.getClass().getDeclaredField( name );
			field.setAccessible( true );
			field.set( change, to );
			field.setAccessible( false );
		} catch ( Exception e ) {
			
		}
	}
}
