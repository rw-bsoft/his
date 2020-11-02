/**
 * @(#)CustomQuery.java Created on 2012-8-6 下午03:10:27
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class CustomQuery extends AbstractActionService implements
		DAOSupportable {
	private static final Log logger = LogFactory
			.getLog(CustomQuery.class);
	
	protected void doFindPhoto(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String regionCode=(String) req.get("regionCode");
		String sql=new StringBuffer().append("select a.empiId as empiId,a.personName as personName,b.masterFlag as masterFlag,a.photo as photo from ")
		.append(" MPI_DemographicInfo a,EHR_HealthRecord b where a.empiId = b.empiId and b.status='0' and ")
		.append(" b.regionCode = '").append(regionCode).append("'").toString();
		Map<String, Object>  para = new HashMap<String, Object>();
		para.put("regionCode", regionCode);
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		try {
			list=dao.doQuery(sql, null);
		} catch (PersistentDataOperationException e) {
		logger.error("Failed to find photo .", e);
		res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
		res.put(RES_MESSAGE, "查询信息失败。");
		throw new ServiceException(e);
		}
		List<Map<String, Object>> resList=new ArrayList<Map<String,Object>>();
		resList=changeList(list);
		res.put(RES_CODE, Constants.CODE_OK);
		res.put(RES_MESSAGE, "Success");
		res.put("body", resList);
	}

	private List<Map<String, Object>> changeList(List<Map<String, Object>> list) {
		List<Map<String, Object>> resList=new ArrayList<Map<String,Object>>();
		if(list.size()>0){
			for (Map<String, Object> m : list) {
				if(m!=null&&(m.get("photo")==null||m.get("photo").equals(""))){
					m.put("photo", "0000000000000000.jpg");
				}
				Map<String, Object> map=new HashMap<String, Object>();
				Iterator<String> it=m.keySet().iterator();
				while(it.hasNext()){
					String key=it.next();
					map.put(key.toUpperCase(),m.get(key));
				}
				resList.add(map);
			}
		}
		return resList;
	}
}
