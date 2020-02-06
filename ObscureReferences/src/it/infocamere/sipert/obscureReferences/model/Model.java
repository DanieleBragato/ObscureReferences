package it.infocamere.sipert.obscureReferences.model;

import java.util.List;

import it.infocamere.sipert.obscureReferences.db.dao.GenericDAO;
import it.infocamere.sipert.obscureReferences.db.dto.GenericResultsDTO;
import it.infocamere.sipert.obscureReferences.db.dto.SchemaDTO;

public class Model {
	
//	private List<SchemaDTO> schemi;
	
//	public List<SchemaDTO> getSchemi(File fileSchemiXLS, boolean reload) throws ErroreFileSchemiNonTrovato, ErroreColonneFileXlsSchemiKo {
//		
//		if (this.schemi == null || reload) {
//			SchemiManager schemiManager = new SchemiManager();
//			this.schemi = schemiManager.getListSchemi(fileSchemiXLS) ;
//
//			//System.out.println("Trovati " +  this.schemi.size() + " schemi");
//		}
//
//		return this.schemi ;
//
//	}
	
	public boolean testConnessioneDB(SchemaDTO schemaDB) {
		
		GenericDAO genericDAO = new GenericDAO();
		
		return genericDAO.testConnessioneOK(schemaDB);
		
	}
	
	public GenericResultsDTO runQuery(SchemaDTO schema, String queryDB) {
		
		GenericResultsDTO risultati = null;
		
		GenericDAO genericDAO = new GenericDAO();
		risultati = genericDAO.executeQuery(schema, queryDB);
		
		return risultati;
	}
}
