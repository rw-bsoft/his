package chis.source.gis;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class ReportSQLUtil {

	public static boolean isPercent(String id) {
		if (id == null || id.indexOf("_") == -1)
			return false;
		else if (id.split("_")[0].indexOf("PER") > -1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("rawtypes")
	public static String getBaseSQL(List subjects, Map<String,Object> jsonReq,
			String field){
		String manageUnit = StringUtils.trimToEmpty((String)jsonReq.get("manageUnit"));
		StringBuilder hql = new StringBuilder("select KPICode,");
		String queryField = field.replace(
				"manageUnit",
				"substring(manaUnitId,1,"
						+ LayerDic.getManaUnitNextLayerLength(manageUnit
								.length()) + ")").replace("year", "checkDate");
		hql.append(queryField).append(",sum(KPI) from PUB_Resource where (");
		for (int i = 0; i < subjects.size(); i++) {
			hql.append("KPICode='").append(subjects.get(i)).append("'");
			if (i + 1 < subjects.size())
				hql.append(" or ");
		}
		hql.append(") and substring(manaUnitId,1,").append(manageUnit.length());
		hql.append(")='").append(manageUnit).append("'");
		hql.append(" and length(manaUnitId)>=").append(
				getNextDataLevel(manageUnit));// getNextLevel(manageUnit) - 1);
		hql.append(" group by KPICode,").append(queryField);
		hql.append(" order by ").append(queryField);
		return hql.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String getDefaultSQL(List subjects, Map<String,Object> jsonReq,
			String field){
		String manageUnit = StringUtils.trimToEmpty((String)jsonReq.get("manageUnit"));
		String startDate = StringUtils.trimToEmpty((String)jsonReq.get("beginDate"));
		String endDate = StringUtils.trimToEmpty((String)jsonReq.get("endDate"));
		StringBuilder hql = new StringBuilder("select KPICode,");
		String queryField = field.replace(
				"manageUnit",
				"substring(manageUnit,1,"
						+ LayerDic.getManaUnitNextLayerLength(manageUnit
								.length()) + ")");
		hql.append(queryField).append(",sum(KPI) from STAT_ManageUnit where (");
		for (int i = 0; i < subjects.size(); i++) {
			hql.append("KPICode='").append(subjects.get(i)).append("'");
			if (i + 1 < subjects.size())
				hql.append(" or ");
		}
		hql.append(") and substring(manageUnit,1,").append(manageUnit.length());
		hql.append(")='").append(manageUnit).append("'");
		hql.append(" and length(manageUnit)>=").append(
				getNextDataLevel(manageUnit));// getNextLevel(manageUnit) - 1);
		hql.append(" and str(statDate,'yyyy-mm')>='").append(startDate)
				.append("'");
		hql.append(" and str(statDate,'yyyy-mm')<='").append(endDate)
				.append("'");
		hql.append(" group by KPICode,").append(queryField);
		hql.append(" order by ").append(queryField);
		return hql.toString();
	}

	private static int getNextDataLevel(String code) {
		switch (code.length()) {
		case 2:
		case 4:
			return 6;
		case 6:
			return 9;
		case 9:
		case 11:
			return code.length();
		default:
			return 6;
		}
	}

}
