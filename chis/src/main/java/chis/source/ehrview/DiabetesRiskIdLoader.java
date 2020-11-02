/**
 * @(#)DiabetesVisitPlanIdLoader.java Created on 2012-5-7 下午9:44:16
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ehrview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.util.CNDHelper;
import chis.source.visitplan.VisitPlanModel;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesRiskIdLoader extends AbstractIdLoader {

	public String getLoadBy() {
		return "empiId";
	}
	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#hasStatus()
	 */
	@Override
	public boolean hasStatus() {
		return false;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return "MDC_DiabetesRiskAssessment";
	}
	
	@Override
	public String getEntityName() {
		return MDC_DiabetesRiskAssessment;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "recordId";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "MDC_DiabetesRiskAssessment.recordId";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.AbstractIdLoader#load(java.lang.String, ctd.util.context.Context)
	 */
	public Map<String, Boolean> getControlData(Map<String, Object> data,
			Context ctx) throws ServiceException {
		BaseDAO dao = new BaseDAO();
		String empiId = (String) ctx.get(getLoadBy());
		StringBuffer cnd =new StringBuffer("['eq',['$','empiId'],['s','").append(empiId).append("']]");
		
		Map<String, Boolean> m = new HashMap<String, Boolean>();
		m.put("update", true);
		m.put("create", true);
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) dao.doQuery(CNDHelper.toListCnd(cnd.toString()), null, BSCHISEntryNames.EHR_HealthRecord);
			if(list != null ){
				Map<String, Object> record = list.get(0);
				if(record.get("status").equals("1")){
					m.put("update", false);
					m.put("create", false);
					return m;
				}
			}
			List<Map<String, Object>> list2 = (List<Map<String, Object>>) dao.doQuery(CNDHelper.toListCnd(cnd.toString()), null, BSCHISEntryNames.MDC_DiabetesRisk);
			if(list2 != null){
				for(Map<String, Object> map : list2){
					if(map.get("status").equals("0") || map.get("status").equals("1")){
						m.put("update", true);
						m.put("create", true);
						return m;
					}
				}
				m.put("update", false);
				m.put("create", false);
				return m;
			}
		} catch (ExpException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return m;
	}

	
}
