package phis.source.dialect;

import java.sql.Types;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MyOracle10gDialect extends Oracle10gDialect {

	public MyOracle10gDialect() {
		super();
		registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
		registerFunction( "date", new SQLFunctionTemplate(StandardBasicTypes.DATE, "to_date(?1,'yyyy-MM-dd')") );
		registerFunction( "sum_day", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1") );
		registerFunction( "sum_day2", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1+?2"));
		registerFunction( "sum_month", new SQLFunctionTemplate(StandardBasicTypes.DATE, "add_months(?1,?2)") );
	}
}
