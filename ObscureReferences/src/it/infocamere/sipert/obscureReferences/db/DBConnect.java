package it.infocamere.sipert.obscureReferences.db;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import it.infocamere.sipert.obscureReferences.db.dto.SchemaDTO;
import it.infocamere.sipert.obscureReferences.main.Main;
import it.infocamere.sipert.obscureReferences.util.Parameter;
import it.infocamere.sipert.obscureReferences.util.TnsNamesOra;
import it.infocamere.sipert.obscureReferences.util.TnsOraParser;

public class DBConnect {
	
	static private final String jdbcUrlFirstBlock = "jdbc:oracle:thin:@//";
	
	static private final String jdbcUrlFirstBlockForTNS = "jdbc:oracle:thin:@";
	
	static private final String HOST = "HOST";
	static private final String PORT = "PORT";
	static private final String SERVICE_NAME = "SERVICE_NAME";
	
	
	public static Connection getConnectionByHost(SchemaDTO schema) {
		
		Logger log = Logger.getLogger(Main.class);
		
		String url = jdbcUrlFirstBlock + schema.getHostServerURL() + ":" + schema.getPort() + "/" + schema.getDbName();
		
        String username = schema.getSchemaUserName();
        String password = schema.getPassword();

        try {
        	
        	return DriverManager.getConnection(url, username, password);
        	
        } catch (SQLException e) {
			//e.printStackTrace();
			throw new RuntimeException("SCHEMA " + schema.getSchemaUserName() + " " + e.toString());
        }
		
	}

	public static Connection getConnectionByTNS(SchemaDTO schema) {
		
		Logger log = Logger.getLogger(Main.class);

        String username = schema.getSchemaUserName();
        String password = schema.getPassword();
        
        String tns_admin =  System.getenv("TNS_ADMIN");
        
        if (tns_admin == null || (tns_admin != null && tns_admin.length() < 1)) {
        	log.error("Variabile d'ambiente TNS_ADMIN non trovata");
        	log.error("Impossibile accedere al data base: parametri non presenti/completi");
        	return null;
        }
        
		TnsNamesOra tnsOra = (new TnsOraParser(tns_admin + File.separator + "tnsnames.ora")).parse();  

		List<Parameter> parameters = tnsOra.getServiceNames(schema.getIdConnessione());
		
		String host = tnsOra.getValueFromNameParameter(HOST, parameters); 
		String port = tnsOra.getValueFromNameParameter(PORT, parameters);		
		String serviceNane = tnsOra.getValueFromNameParameter(SERVICE_NAME, parameters);
		
		if (host == null || (host != null && host.length() < 1) || (host != null && host.length() > 0 && hostIsKO(host))) {
    		log.warn("Impossibile accedere al data base tramite l'host name -> si procede tramite l'id connessione/tns");
    		return getConnectionByIdConnessione(username, password, tns_admin, schema.getIdConnessione(), serviceNane, schema.getSchemaUserName());
		}
		if (port == null || (port != null && port.length() < 1)) {
    		log.error("Impossibile accedere al data base: porta data base non trovato");
    		return null;			
		}		
		if (serviceNane == null || (serviceNane != null && serviceNane.length() < 1)) {
    		log.error("Impossibile accedere al data base: servece name data base non trovato");
    		return null;			
		}				
		String url = jdbcUrlFirstBlock + host + ":" + port + "/" + serviceNane;
        try {
    	    
        	return DriverManager.getConnection(url, username, password);
        	
        } catch (SQLException e) {
			//e.printStackTrace();
			throw new RuntimeException("SCHEMA " + schema.getSchemaUserName() + " " + e.toString());
        }			

		
	}	
	
	private static boolean hostIsKO(String host) {
		
		boolean isKO = true;
		
        InetAddress domainInetAddress = null;
        
        try {
            domainInetAddress = InetAddress.getByName(host);
            isKO = false;
        } catch (UnknownHostException uhe) {
        	return isKO;
        }
		
		return isKO;
	}
	
	private static Connection getConnectionByIdConnessione(String username, String password, String tns_admin, String idConnessione, String serviceNane, String schemaUserName) {
		
		String url = jdbcUrlFirstBlockForTNS + idConnessione;
		
		System.setProperty("oracle.net.tns_admin", tns_admin);
		
        try {
        	
        	Class.forName ("oracle.jdbc.OracleDriver");
    	    
        	return DriverManager.getConnection(url, username, password);
        	
        } catch (SQLException e) {
			//e.printStackTrace();
			throw new RuntimeException("SCHEMA " + schemaUserName + " " + e.toString());
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}
	
}
