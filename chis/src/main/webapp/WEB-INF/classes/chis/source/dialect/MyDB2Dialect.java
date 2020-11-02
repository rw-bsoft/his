package chis.source.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MyDB2Dialect extends DB2Dialect {

	public MyDB2Dialect() {
		super();
		registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
		registerFunction( "sum_day", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 DAY") );
		registerFunction( "sum_day2", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + ?2 DAY") );
		registerFunction( "sum_month", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 MONTH") );
//		registerFunction( "str_date", new SQLFunctionTemplate(Hibernate.STRING, "char(?1)") );
	}
	
}
