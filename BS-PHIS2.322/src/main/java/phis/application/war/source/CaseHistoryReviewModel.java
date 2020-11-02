/**
 * @(#)CaseHistoryReviewModel.java Created on 2013-4-26 上午9:56:54
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.CNDHelper;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.util.AppContextHolder;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class CaseHistoryReviewModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(CaseHistoryReviewModel.class);

	public CaseHistoryReviewModel(BaseDAO dao) {
		this.dao = dao;
	}

	public List<Map<String, Object>> listAllCaseRecord(String YWID1)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "JZXH", "i",
				Integer.parseInt(YWID1));
		List<?> cnd2=CNDHelper.createSimpleCnd("ne", "DLLB", "s", "-1");
		List<?> cnd=CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> re = null;
		try {
			re = dao.doList(cnd, "JLSJ desc", BSPHISEntryNames.EMR_BL01_SJRZ);
		} catch (PersistentDataOperationException e) {
			logger.error("list all BL01 fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病人所有病历失败！");
		}
		return re;
	}

	public Map<String, Object> listRecordByTabId(String tabId, String YWID1,
			String[] r, String YWID2, int pageNo,
			int pageSize, String queryCndsType)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "YWID1", "i",
				Integer.parseInt(YWID1));
		List<?> cnd1 = null;
		List<?> cnd2 = null;
		Map<String, Object> rsMap = new HashMap<String, Object>();
		if (YWID2 != null) {
			cnd2 = CNDHelper.createSimpleCnd("eq", "YWID2", "i",
					Integer.parseInt(YWID2));
			cnd = CNDHelper.createArrayCnd("and", cnd2, cnd);
		} else if(r!=null){
			if (r.length > 0) {
				cnd2 = CNDHelper.createInCnd("YWID2", r);
				cnd = CNDHelper.createArrayCnd("and", cnd2, cnd);
			}
		}else{
			return rsMap;
		}
		if (tabId.equals("visitRecord")) {
			cnd1 = CNDHelper.createSimpleCnd("eq", "SJXM", "s", "1006");
		}
		if (tabId.equals("updateRecord")) {
			String[] strs = { "1001", "1002", "1003" };
			cnd1 = CNDHelper.createInCnd("SJXM", strs);
		}
		if (tabId.equals("autographRecord")) {
			String[] strs = { "1004", "1008"};
			cnd1 = CNDHelper.createInCnd("SJXM", strs);
		}
		if (tabId.equals("printRecord")) {
			cnd1 = CNDHelper.createSimpleCnd("eq", "SJXM", "s", "1005");
		}
		if (tabId.equals("qualityInfo")) {
			cnd1 = CNDHelper.createSimpleCnd("eq", "SJXM", "s", "1007");
		}
		if (tabId.equals("allRecord")) {
			cnd1 = null;
		}
		if (cnd1 != null) {
			cnd = CNDHelper.createArrayCnd("and", cnd1, cnd);
		}
		
		try {
			rsMap = dao.doList(cnd, "XTSJ desc", BSPHISEntryNames.EMR_BLSJRZ,
					pageNo, pageSize, queryCndsType);
		} catch (PersistentDataOperationException e) {
			logger.error("listRecordByTabId fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据标签查询日志失败！");
		}
		return rsMap;
	}


	private Map<String, Integer> getRecordCountByTabId(String YWID1,
			String YWID2, String[] r) throws ModelDataOperationException {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		int sum = 0;
		int upc = 0;
		int auc = 0;
		int prc = 0;
		int vic = 0;
		int quc = 0;
		StringBuffer where = new StringBuffer();
		if (YWID1 != null) {
			where.append(" where ").append("YWID1=").append(YWID1);
		}
		if (YWID2 != null) {
			if (YWID1 != null) {
				where.append(" and ");
			} else {
				where.append(" where ");
			}
			where.append("YWID2=").append(YWID2);
		}else if(r!=null){
			if (r.length> 0) {
				if (YWID1 != null) {
					where.append(" and ");
				} else {
					where.append(" where ");
				}
				where.append("YWID2 in (");
				for (int i = 0; i < r.length; i++) {
					if(i==r.length-1){
						where.append(r[i]).append(")");
					}else{
						where.append(r[i]).append(",");
					}
				}
			}
		}else{
			counts.put("autographRecord", auc);
			counts.put("printRecord", prc);
			counts.put("visitRecord", vic);
			counts.put("qualityInfo", quc);
			counts.put("updateRecord", upc);
			counts.put("allRecord", sum);
			return counts;
		}
		String hql = "select SJXM as SJXM,count(*) as count from  EMR_BLSJRZ" + where + " group by SJXM";
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, null);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				String SJXM = (String) m.get("SJXM");
				if ("1001".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					upc += c;
					sum += c;
				}
				if ("1002".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					upc += c;
					sum += c;
				}
				if ("1003".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					upc += c;
					sum += c;
				}
				if ("1004".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					auc += c;
					sum += c;
				}
				if ("1005".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					prc += c;
					sum += c;
				}
				if ("1006".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					vic += c;
					sum += c;
				}
				if ("1007".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					quc += c;
					sum += c;
				}
				if ("1008".equals(SJXM)) {
					int c = Integer.parseInt(m.get("count").toString());
					auc += c;
					sum += c;
				}
			}
			counts.put("autographRecord", auc);
			counts.put("printRecord", prc);
			counts.put("visitRecord", vic);
			counts.put("qualityInfo", quc);
			counts.put("updateRecord", upc);
			counts.put("allRecord", sum);
		} catch (PersistentDataOperationException e) {
			logger.error("getRecordCountByTabId fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据标签查询日志失败！");
		}
		return counts;
	}

	public Map<String, Integer> loadCountByTabID(String yWID1, String yWID2, String[] r)
			throws ModelDataOperationException {
		Map<String, Integer> counts = getRecordCountByTabId(yWID1, yWID2,r);
		return counts;
	}

	public Map<String, Object> getFileContent(String type, String BLBH) throws ModelDataOperationException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		Session session = sf.openSession();
		StringBuffer where = new StringBuffer().append(" where BLBH=").append(
				BLBH);
		if (type.equals("XML")||type.equals("STR")) {
			where.append(" and WDLX='3' ");
		}
		if (type.equals("HTML")) {
			where.append(" and WDLX='1' ");
		}
		String hql = "select BLBH as BLBH,WDNR as WDNR from EMR_BL03"
				+ where;
		//System.out.println(hql);
		Query q = session.createQuery(hql);
		List<Object[]> records = q.list();
		Map<String, Object> o = new HashMap<String, Object>();
		try {
			if (records != null && records.size() > 0) {
				Object[] r = records.get(0);
				o.put("BLBH", r[0]);
				Blob blob = (Blob) r[1];
				if(blob==null){
					o.put("WDNR", null);
					return o;
				}
				InputStream is = blob.getBinaryStream();
				Reader utf8_reader = new InputStreamReader(is,"GBK");
				StringBuffer utf8Text = new StringBuffer();
				int charValue = 0;
				while ((charValue = utf8_reader.read()) != -1) {
					utf8Text.append((char) charValue);
				}
				String resultString = utf8Text.toString();
				if(type.equals("XML")){
					resultString=resultString.trim();
					resultString=resultString.replaceAll("&lt;", "<");
					resultString=resultString.replaceAll("&gt;", ">");
				}
				if(type.equals("STR")){
					resultString=resultString.trim();
					StringBuffer list=new StringBuffer();
					String[] reList=resultString.split("<text>");
					for (int i = 0; i < reList.length; i++) {
						String str=reList[i];
						String[] re=str.split("</text>");
						if(re.length>1){
							list.append(re[1]);
						}else{
							list.append(re[0]);
						}
					}
					resultString=list.toString();
					resultString=resultString.trim();
					resultString=resultString.replaceAll("&lt;", "<");
					resultString=resultString.replaceAll("&gt;", ">");
				}
				o.put("WDNR", resultString);
			}
		} catch (SQLException e) {
			logger.error("getFileContent fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询文档内容失败！");
		} catch (IOException e) {
			logger.error("getFileContent fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询文档内容失败！");
		}finally{
			session.close();
		}
		return o;
	}

	public Map<String, Object> getFormDataFromDB(String bLBH) throws ModelDataOperationException {
		Map<String, Object> map = null;
		try {
			map=dao.doLoad(BSPHISEntryNames.EMR_BL01_SJRZ, bLBH);
		} catch (PersistentDataOperationException e) {
			logger.error("getFormDataFromDB fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据主键查询病历失败！");
		}
		return map;
	}
	
	public Map<String, Object> getMZFormDataFromDB(String bLBH) throws ModelDataOperationException {
		Map<String, Object> map = null;
		try {
			map=dao.doLoad(BSPHISEntryNames.OMR_BL01_SJRZ, bLBH);
		} catch (PersistentDataOperationException e) {
			logger.error("getFormDataFromDB fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据主键查询病历失败！");
		}
		return map;
	}
}
