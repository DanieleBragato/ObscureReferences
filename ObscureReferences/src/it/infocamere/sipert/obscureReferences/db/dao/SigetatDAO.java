package it.infocamere.sipert.obscureReferences.db.dao;

public class SigetatDAO {
	
    public static String getSql() {

    	String sql = "" +
    	"SELECT " + 
    	" SUBSTR(CDKEYUTE,5,4) PROGR , " + 
    	" SUBSTR(ANDATIXX,1,1) TIPO , " + 
    	" SUBSTR(ANDATIXX,2,30) ETICHETTA, " + 
    	" SUBSTR(ANDATIXX,32,30) STRINGA " +      
    	" FROM SIGETAT WHERE " + 
    	" CDAPPLIC='PE' AND CDCODTAB='T55' AND " + 
    	" SUBSTR(CDKEYUTE,1,4)=TO_CHAR('5030','FM0000') " ;
    	
    	return sql;
    }

}
