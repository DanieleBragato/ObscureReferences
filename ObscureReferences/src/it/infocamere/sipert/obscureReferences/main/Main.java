package it.infocamere.sipert.obscureReferences.main;

import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import it.infocamere.sipert.obscureReferences.db.dao.PeanaiUpdDAO;
import it.infocamere.sipert.obscureReferences.db.dao.SigetatDAO;
import it.infocamere.sipert.obscureReferences.db.dto.GenericResultsDTO;
import it.infocamere.sipert.obscureReferences.db.dto.SchemaDTO;
import it.infocamere.sipert.obscureReferences.model.Model;
import it.infocamere.sipert.obscureReferences.util.io.DirectoryList;
import it.infocamere.sipert.obscureReferences.util.xml.ParseXmlForSetLogFileName;
import it.infocamere.sipert.obscureReferences.util.xml.ParserAndObscureXmlFile;

public class Main {

	private static String nomeUtenteOracle;
	private static String passwordUtenteOracle;
	private static String codiceAzienda;
	private static String AnnoMeseMensil;
	private static String percorsoInputFiles;
	private static String percorsoOutputFiles;
	private static String logFile;
	private static String IdConnessione;

	private static Logger log;

	public static void main(String[] args) {

		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (i == 0) nomeUtenteOracle = args[i].toString();
				if (i == 1) passwordUtenteOracle = args[i].toString();
				if (i == 2) codiceAzienda = args[i].toString();
				if (i == 3) AnnoMeseMensil = args[i].toString();
				if (i == 4) percorsoInputFiles = args[i].toString();
				if (i == 5) percorsoOutputFiles = args[i].toString();
				if (i == 6) logFile = args[i].toString();
				if (i == 7) IdConnessione = args[i].toString();
			}
		} else {
			System.out.println("ERRORE: Non ci sono PARAMETRI DI INPUT !!");
		}

		if ((nomeUtenteOracle == null || (nomeUtenteOracle != null && nomeUtenteOracle.length() < 1))
				|| (passwordUtenteOracle == null || (passwordUtenteOracle != null && passwordUtenteOracle.length() < 1))
				|| (codiceAzienda == null || (codiceAzienda != null && codiceAzienda.length() < 1))
				|| (AnnoMeseMensil == null || (AnnoMeseMensil != null && AnnoMeseMensil.length() < 1))
				|| (percorsoInputFiles == null || (percorsoInputFiles != null && percorsoInputFiles.length() < 1))
				|| (percorsoOutputFiles == null || (percorsoOutputFiles != null && percorsoOutputFiles.length() < 1))
				|| (logFile == null || (logFile != null && logFile.length() < 1))
				|| (IdConnessione == null || (IdConnessione != null && IdConnessione.length() < 1))) {
			System.out.println("ERRORE: Parametri in input Non completi/valorizzati !!");
			System.out.println("Elaborazione Conclusa con Errori: EXIT !! ");
			return;
		}

		// load configuration File in XML format for logging (log4j)
		URL url = Main.class.getResource("/Log4j.xml");
		if (logFile != null && logFile.length() > 0) {
			// setup nome file dei log da parametro di input
			ParseXmlForSetLogFileName parseXmlForSetLogFileName = new ParseXmlForSetLogFileName();
			InputStream is = parseXmlForSetLogFileName.setLogFileName(logFile, url);
			DOMConfigurator configurator = new DOMConfigurator();
			configurator.doConfigure(is, LogManager.getLoggerRepository());			
		}

		log = Logger.getLogger(Main.class);
		log.info("Inizio elaborazione");
		log.info("Elenco dei parametri di elaborazione:");
		log.info("   Parametro nr 1 - Nome utente Oracle = " + nomeUtenteOracle);
		log.info("   Parametro nr 2 - Password utente Oracle = " + passwordUtenteOracle);
		log.info("   Parametro nr 3 - Codice azienda = " + codiceAzienda);
		log.info("   Parametro nr 4 - Anno (SSAA) mese (MM) e mensilità (MS) in elaborazione (formato SSAA_MM_MS) = " + AnnoMeseMensil);
		log.info("   Parametro nr 5 - Percorso dei files xml da trattare = " + percorsoInputFiles);
		log.info("   Parametro nr 6 - Percorso di output = " + percorsoOutputFiles);
		log.info("   Parametro nr 7 - log file = " + logFile);
		log.info("   Parametro nr 8 - IdConnessione = " + IdConnessione);
		
		Model model = new Model();

//		if (!model.testConnessioneDB(getSchemaDataBase())) {
//			log.error("ERRORE: Test di connessione al data base NON COMPLETATO CORRETTAMENTE !! ");
//			log.error("Elaborazione Conclusa con Errori: EXIT !! ");
//			return;
//		}
		
		if (!model.testConnessioneDBbyTNS(getSchemaDataBase())) {
			log.error("ERRORE: Test di connessione al data base con TNS NON COMPLETATO CORRETTAMENTE !! ");
			log.error("Elaborazione Conclusa con Errori: EXIT !! ");
			return;
		}		

		GenericResultsDTO peanaiUpdRisultatiDTO;
		PeanaiUpdDAO peanaiUpdDAO = new PeanaiUpdDAO();

		peanaiUpdRisultatiDTO = model.runQuery(getSchemaDataBase(), peanaiUpdDAO.getSql());

		boolean okQueryPEANAIUPD = false;
		boolean okQuerySIGETAT = false;

		if (peanaiUpdRisultatiDTO != null && peanaiUpdRisultatiDTO.getListLinkedHashMap() != null) {
			okQueryPEANAIUPD = true;
			log.info("Query sulla tabella PEANAI_UPD >> Trovate " + peanaiUpdRisultatiDTO.getListLinkedHashMap().size() + " righe ");
		} else {
			log.error("ERRORE: QUERY sulla tabella PEANAI_UPD >> Nessuna riga trovata !! ");
		}

		GenericResultsDTO sigetatRisultatiDTO;
		SigetatDAO sigetatDAO = new SigetatDAO();

		sigetatRisultatiDTO = model.runQuery(getSchemaDataBase(), sigetatDAO.getSql());

		if (sigetatRisultatiDTO != null && sigetatRisultatiDTO.getListLinkedHashMap() != null) {
			okQuerySIGETAT = true;
			log.info("Query sulla tabella SIGETAT >> Trovate " + sigetatRisultatiDTO.getListLinkedHashMap().size() + " righe ");			
		} else {
			log.error("ERRORE: QUERY sulla tabella SIGETAT >> Nessuna riga trovata !! ");
		}

		String[] filesDiInput;
		filesDiInput = getFilesDiInput(percorsoInputFiles);

		int countTotFilesOscurati = 0;

		if (okQuerySIGETAT && okQueryPEANAIUPD) {
			if (filesDiInput != null && filesDiInput.length > 0) {
				for (String fileName : filesDiInput) {
					if (nomeFileCorretto(fileName)) {
						ParserAndObscureXmlFile parseXml = new ParserAndObscureXmlFile();
						if (parseXml.tryObscureReferencesOnFile(percorsoInputFiles + "\\" + fileName, fileName,
								percorsoOutputFiles, sigetatRisultatiDTO, peanaiUpdRisultatiDTO))
							countTotFilesOscurati++;
					}
				}
				log.info("Trovati " + filesDiInput.length + " files in input - Oscurati/Creati "
						+ countTotFilesOscurati + " files di output");
				log.info("Elaborazione conclusa correttamente !!");
			} else {
				log.warn("WARNING: Nessun File da trattare !!");
				log.warn("Elaborazione Conclusa con WARNING: EXIT !! ");
				return;
			}
		} else {
			log.error("ERRORE: Estrazioni dati dal data base non completate !!");
			log.error("Elaborazione Conclusa con Errori: EXIT !! ");
			return;
		}
		;

	}

	private static boolean nomeFileCorretto(String inputFileName) {

		if (inputFileName != null & inputFileName.length() > 22) {
			if (inputFileName.substring(0, 4).equals(codiceAzienda)
					&& inputFileName.substring(5, 15).equals(AnnoMeseMensil)) {
				return true;
			} else {
				log.warn("File " + inputFileName
						+ " azienda e/o mensilità non corrispondenti ai parametri di elaborazione >> non trattato/oscurato");
			}
		} else {
			log.warn("File " + inputFileName + " non corretto >> non trattato/oscurato");
		}

		return false;
	}

	private static SchemaDTO getSchemaDataBase() {

		SchemaDTO schemaDTO = new SchemaDTO();

		schemaDTO.setSchemaUserName(nomeUtenteOracle);
		schemaDTO.setPassword(passwordUtenteOracle);
//		schemaDTO.setDbName("ORAXAP");
//		schemaDTO.setPort("1523");
//		schemaDTO.setHostServerURL("lxidbpena01.intra.infocamere.it");
		schemaDTO.setIdConnessione(IdConnessione);
		return schemaDTO;

	}

	private static String[] getFilesDiInput(String percorso) {

		DirectoryList dirList = new DirectoryList();

		String[] files = dirList.searchListFiles(percorsoInputFiles);

		if (files != null && files.length > 0) {
			log.info("Trovati " + files.length + " files nella directory di input ");
		} else {
			log.warn("Nessun file trovato nella directory di input");
		}

		return files;
	}

}
