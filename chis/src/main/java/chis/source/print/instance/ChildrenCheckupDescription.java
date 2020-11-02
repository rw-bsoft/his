/**
 * @(#)ChildrenCheckupDescription.java Created on 2014-10-7 上午10:40:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ChildrenCheckupDescription extends BSCHISPrint implements IHandler {
	private String empiId;
	private String phrId;
	private Context ctx;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		phrId = (String) request.get("phrId");
		this.ctx = ctx;
		String jrxml = (String) request.get("jrxml");
		Map<String, Object> personInfo = null;
		try {
			personInfo = this.getFirstRecord(this.getBaseInfo(
					MPI_DemographicInfo, request, ctx));
		} catch (ServiceException e2) {
			e2.printStackTrace();
		}
		response.put("personName", personInfo.get("personName"));
		response.put("phrId", phrId);
		Map<String, Object> jsonReq = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		jsonReq.put("schema", "chis.application.cdh.schemas.CDH_ChildrenCheckupDescription");
		String cnd = "['eq',['$','a.phrId'],['s','" + phrId + "']]";
		jsonReq.put("cnd", toListCnd(cnd));
		try {
			new SimpleQuery().execute(jsonReq, res, ctx);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list1 = (List<Map<String, Object>>) res
				.get("body");

		res = new HashMap<String, Object>();
		cnd = "";
		String schema = "";
		if (jrxml.startsWith("chis.prints.template.childrenCheckupDescription1")) {
			cnd = "['and',['eq',['$','a.phrId'],['s','"
					+ phrId
					+ "']],['or',['eq',['$','checkupStage'],['s','1']],['eq',['$','checkupStage'],['s','3']],['eq',['$','checkupStage'],['s','6']],['eq',['$','checkupStage'],['s','9']]]]";
			schema = "chis.application.cdh.schemas.CDH_CheckupInOne";
		} else if (jrxml.startsWith("chis.prints.template.childrenCheckupDescription2")) {
			cnd = "['and',['eq',['$','a.phrId'],['s','"
					+ phrId
					+ "']],['or',['eq',['$','checkupStage'],['s','12']],['eq',['$','checkupStage'],['s','18']],['eq',['$','checkupStage'],['s','24']],['eq',['$','checkupStage'],['s','30']]]]";
			schema = "chis.application.cdh.schemas.CDH_CheckupOneToTwo";
		} else if (jrxml.startsWith("chis.prints.template.childrenCheckupDescription3")) {
			cnd = "['and',['eq',['$','a.phrId'],['s','"
					+ phrId
					+ "']],['or',['eq',['$','checkupStage'],['s','36']],['eq',['$','checkupStage'],['s','48']],['eq',['$','checkupStage'],['s','60']],['eq',['$','checkupStage'],['s','72']]]]";
			schema = "chis.application.cdh.schemas.CDH_CheckupThreeToSix";
		}
		jsonReq.put("schema", schema);
		jsonReq.put("cnd", toListCnd(cnd));
		try {
			new SimpleQuery().execute(jsonReq, res, ctx);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> list2 = (List<Map<String, Object>>) res
				.get("body");
		list2 = SchemaUtil.setDictionaryMessageForList(list2, schema);
		for (Map<String, Object> map : list2) {
			String checkupStage = (String) map.get("checkupStage");
			String checkupId = (String) map.get("checkupId");
			response.put("checkupDate" + checkupStage, map.get("checkupDate"));
			response.put("nextCheckupDate" + checkupStage,
					map.get("nextCheckupDate"));
			response.put("checkDoctor" + checkupStage,
					map.get("checkDoctor_text"));
			for (Map<String, Object> m : list1) {
				if (checkupId.equals(m.get("checkupId"))) {
					response.put("other" + checkupStage, m.get("other"));
					if (m.get("description") != null) {
						String description = (String) m.get("description");
						if ("6".equals(checkupStage)
								|| "12".equals(checkupStage)
								|| "18".equals(checkupStage)
								|| "24".equals(checkupStage)
								|| "30".equals(checkupStage)
								|| "36".equals(checkupStage)) {
							String[] des=description.split(",");
							for (int i = 0; i < des.length; i++) {
								response.put("description" + checkupStage+des[i], "√");
							}
						}else{
							response.put("description" + checkupStage, description);
						}
					}
				}
			}
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
