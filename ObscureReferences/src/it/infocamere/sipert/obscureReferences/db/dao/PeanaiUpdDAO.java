package it.infocamere.sipert.obscureReferences.db.dao;

public class PeanaiUpdDAO {

    public String getSql() {
    	
    	String sql = "" + 
    	"SELECT TO_CHAR(cddipend,'FM000000') cddipend,TRIM(SUBSTR(ANDATI01||ANDATI02||ANDATI03||ANDATI04,839,8)) codice " + 
    	" FROM PEANAI_UPD WHERE CDAZIEND='5030' and '-'||TRIM(SUBSTR(ANDATI01||ANDATI02||ANDATI03||ANDATI04,839,8))||'-' <> '--' ";
    	
    	return sql;
    }
	
}
