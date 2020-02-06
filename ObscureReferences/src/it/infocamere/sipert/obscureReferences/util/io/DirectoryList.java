package it.infocamere.sipert.obscureReferences.util.io;

import java.io.File;

import org.apache.log4j.Logger;

import it.infocamere.sipert.obscureReferences.main.Main;

public class DirectoryList {
	
    String directoryName;  // Directory name entered by the user.
    File directory;        // File object referring to the directory.
    String[] files;        // Array of file names in the directory.
    
    public String[] searchListFiles(String directoryName) {

    	Logger log = Logger.getLogger(Main.class);
    	
    	directory = new File(directoryName);

    	if (directory.isDirectory() == false) {
    		if (directory.exists() == false)
    			log.error("Directory inesistente!");
    		else
    			log.error("Il percorso di input non è una Directory!");
    	} else {
    		files = directory.list();
    	} 
    	
    	return files;
    }
    
}
