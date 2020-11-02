package phis.application.tcm.source;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import org.dom4j.DocumentException;

import phis.application.lis.source.ModelOperationException;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class TcmModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TcmModel.class);

	public TcmModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 查询需推送到省中医馆平台的草药信息条数
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */

	@SuppressWarnings("unchecked")
	public Map<String, Object> doHerbalMedicineCount(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();
			String hqlcount = "SELECT COUNT(*) totalcount " +
					"FROM (SELECT * FROM YK_YPXX WHERE JGID=:JGID) A,(SELECT * FROM YK_TYPK WHERE TYPE=3) B " +
					"WHERE A.YPXH=B.YPXH";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", jgid);
			long count = Long.parseLong(dao.doSqlLoad(hqlcount, parameters).get("TOTALCOUNT")+"");
			req.put("infocount", count);
		} catch (Exception e) {
			logger.error("查询需推送到省中医馆平台的草药信息条数.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询需推送到省中医馆平台的草药信息条数.");
		}
		return req;
	}

	/**
	 * 推送草药信息到省中医馆平台
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */

	@SuppressWarnings("unchecked")
	public Map<String, Object> doUploadHerbalMedicine(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			Tcmservices.Tcm02._Proxy7ServiceLocator service = new Tcmservices.Tcm02._Proxy7ServiceLocator();
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();
			// 获取系统参数中医馆平台接口地址（推送草药信息）
			service.setTCM_HIS_02EndpointAddress(ParameterUtil.getParameter(jgid.substring(0,4),"TCM_API_02", ctx));
			Tcmservices.Tcm02.TCM_HIS_02SoapBindingStub th02 = (Tcmservices.Tcm02.TCM_HIS_02SoapBindingStub)service.getTCM_HIS_02();
			th02.setTimeout(2000);
			String hql = "SELECT A.YPXH as ID,A.YPXH as CODE,B.YPMC as NAME,B.YPGG as SPECIFICATION,'' as MANUFACTURER," +
					"DECODE(A.YKZF,0,'Y',1,'N','Y') as ACTIVEFLG,E.ORGANIZCODE_TCM as ORGCODE " +
					"FROM (SELECT * FROM YK_YPXX WHERE JGID=:JGID) A,(SELECT * FROM YK_TYPK WHERE TYPE=3) B,SYS_ORGANIZATION E " +
					"WHERE A.YPXH=B.YPXH AND A.JGID = E.ORGANIZCODE";
			String hqlcount = "SELECT COUNT(*) totalcount " +
					"FROM (SELECT * FROM YK_YPXX WHERE JGID=:JGID) A,(SELECT * FROM YK_TYPK WHERE TYPE=3) B " +
					"WHERE A.YPXH=B.YPXH";
			String reqMessage = "";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", jgid);
			long count = Long.parseLong(dao.doSqlLoad(hqlcount, parameters).get("TOTALCOUNT")+"");
			long uploadcount = 0;
			String errorMsg = "";
			String strDate = "";
			List<Map<String, Object>> medicineList  = dao.doSqlQuery(hql, parameters);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式
			if(count>0){
				for (Map<String, Object> medicineMap : medicineList) {				
					Date now = new Date(); 
					strDate = dateFormat.format(now); 
					reqMessage = "<Request><Header><sender>HIS</sender><receiver>AE</receiver><sendTime>"+strDate+"</sendTime>" +
							"<msgType>TCM_HIS_02</msgType><msgID>HIS"+strDate+"</msgID></Header><Medicine><id>"+medicineMap.get("ID")+"</id>" +
							"<code>"+medicineMap.get("CODE")+"</code><name>"+medicineMap.get("NAME")+"</name>" +
							"<specification>"+medicineMap.get("SPECIFICATION")+"</specification><manufacturer>"+medicineMap.get("SPECIFICATION")+"</manufacturer>" +
							"<activeFlg>"+medicineMap.get("ACTIVEFLG")+"</activeFlg><orgCode>"+medicineMap.get("ORGCODE")+"</orgCode></Medicine></Request>";
					String res = th02.acceptMessage(reqMessage);
					if(res.contains("has been successfully received")){
						uploadcount += 1;
					}else
					{
						errorMsg += (medicineMap.get("ID")+"_")+(medicineMap.get("NAME")+"，");
					}
				}
			}
			if(errorMsg.length()>0){
				errorMsg="("+errorMsg+")";
			}
			req.put("msg", "总共"+count+"条草药信息，成功上传"+uploadcount+"条，失败"+(count-uploadcount)+"条"+errorMsg + " 【" + strDate+"】");
		} catch (Exception e) {
			logger.error("推送草药信息到省中医馆平台失败.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "推送草药信息到省中医馆平台失败.");
		}
		return req;
	}

	/**
	 * 查询未匹配的西医诊断（HIS）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doQueryDiagnosisContrastHIS(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		try {
 			Map<String, Object> PageMap = new HashMap<String, Object>();
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();	
			Map<String, Object> data = new HashMap<String, Object>();		
			String cndSQL = "";
			StringBuffer countHql = new StringBuffer();
			StringBuffer hql = new StringBuffer();
			long countsAll = 0;
			int pageSize = Constants.DEFAULT_PAGESIZE;
			int pageNo = Constants.DEFAULT_PAGENO;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo");
			}
			int first = (pageNo - 1) * pageSize;
			List cnd =(List)req.get("cnd");
			if (null != cnd) {
				try {
					cndSQL = ExpressionProcessor.instance().toString(cnd);

				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
			}
			countHql.append(" select count(*) as count ")
					.append(" FROM GY_JBBM WHERE ZFPB=0 AND JBXH NOT IN(SELECT JBXH FROM GY_JBBM_DZ)");
			if (cndSQL != null && cndSQL.length() > 0) {
				countHql.append(" AND " + cndSQL);
			}
			Map<String, Object> countValue = dao.doLoad(countHql.toString(),
					PageMap);
			if (countValue != null && !countValue.isEmpty()) {
				countsAll = Long.parseLong(countValue.get("count") + "");
			}			
			PageMap.put("first", first);
			PageMap.put("max", pageSize);
			hql.append("SELECT JBXH as JBXH,ICD10 as ICD10,JBMC as JBMC,PYDM as PYDM FROM GY_JBBM WHERE ZFPB=0 AND JBXH NOT IN(SELECT JBXH FROM GY_JBBM_DZ)");	
			if (cndSQL != null && cndSQL.length() > 0) {
				hql.append(" AND " + cndSQL);
			}
			listData = dao.doSqlQuery(hql.toString(), PageMap);
			data.put("totalCount", countsAll);
			data.put("pageNo", pageNo);
			data.put("pageSize", pageSize);
			data.put("body", listData);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询未匹配的西医诊断（HIS）失败", e);
		}
	}
	/**
	 * 查询未匹配的西医诊断（TCM）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doQueryDiagnosisContrastTCM(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		try {
 			Map<String, Object> PageMap = new HashMap<String, Object>();
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();	
			Map<String, Object> data = new HashMap<String, Object>();		
			String cndSQL = "";
			StringBuffer countHql = new StringBuffer();
			StringBuffer hql = new StringBuffer();
			long countsAll = 0;
			int pageSize = Constants.DEFAULT_PAGESIZE;
			int pageNo = Constants.DEFAULT_PAGENO;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo");
			}
			int first = (pageNo - 1) * pageSize;
			List cnd =(List)req.get("cnd");
			if (null != cnd) {
				try {
					cndSQL = ExpressionProcessor.instance().toString(cnd);
				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
			}
			countHql.append(" select count(*) as count ")
					.append(" FROM GY_JBBM_TCM WHERE JBXH NOT IN(SELECT JBXH_TCM FROM GY_JBBM_DZ)");
			if (cndSQL != null && cndSQL.length() > 0) {
				countHql.append(" AND " + cndSQL);
			}
			Map<String, Object> countValue = dao.doLoad(countHql.toString(),
					PageMap);
			if (countValue != null && !countValue.isEmpty()) {
				countsAll = Long.parseLong(countValue.get("count") + "");
			}			
			PageMap.put("first", first);
			PageMap.put("max", pageSize);
			hql.append("SELECT JBXH as JBXH,ICD10 as ICD10,JBMC as JBMC,PYDM as PYDM FROM GY_JBBM_TCM WHERE JBXH NOT IN(SELECT JBXH_TCM FROM GY_JBBM_DZ)");	
			if (cndSQL != null && cndSQL.length() > 0) {
				hql.append(" AND " + cndSQL);
			}
			listData = dao.doSqlQuery(hql.toString(), PageMap);
			data.put("totalCount", countsAll);
			data.put("pageNo", pageNo);
			data.put("pageSize", pageSize);
			data.put("body", listData);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询未匹配的西医诊断（TCM）失败", e);
		}
	}
	/**
	 * 删除已对照疾病诊断
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */

	@SuppressWarnings("unchecked")
	public void doRemoveDiagnosisContrastDz(Map<String, Object> body,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		Long jbxh = Long.parseLong(body.get("jbxh") + "");
		Long jbxh_tcm = Long.parseLong(body.get("jbxh_tcm") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JBXH", jbxh);
		parameters.put("JBXH_TCM", jbxh_tcm);
		StringBuffer removeHql = new StringBuffer();
		removeHql
				.append("delete from "
						+ "GY_JBBM_DZ"
						+ " where JBXH=:JBXH and JBXH_TCM=:JBXH_TCM");
		try {
			dao.doUpdate(removeHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除已对照疾病诊断失败", e);
		}
		ss.getTransaction().commit();
	}

	/**
	 * 保存对照的疾病诊断信息
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiagnosisContrastDz(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		Long jbxh = Long.parseLong(body.get("jbxh") + "");
		Long jbxh_tcm = Long.parseLong(body.get("jbxh_tcm") + "");
		String icd10 = body.get("icd10") + "";
		String jbmc = body.get("jbmc") + "";
		String pydm = body.get("pydm") + "";
		String icd10_tcm = body.get("icd10_tcm") + "";
		String jbmc_tcm = body.get("jbmc_tcm") + "";
		String pydm_tcm = body.get("pydm_tcm") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JBXH", jbxh);
		parameters.put("JBXH_TCM", jbxh_tcm);
		try {
			// 保存的时候，若记录在表里已存在，则不做操作
			long count = dao
					.doCount(
							"GY_JBBM_DZ",
							"JBXH=:JBXH and JBXH_TCM=:JBXH_TCM",
							parameters);
			if (count == 0) {
				parameters.put("ICD10", icd10);
				parameters.put("JBMC", jbmc);
				parameters.put("PYDM", pydm);
				parameters.put("ICD10_TCM", icd10_tcm);
				parameters.put("JBMC_TCM", jbmc_tcm);
				parameters.put("PYDM_TCM", pydm_tcm);
				// 若不存在，插入一条新的记录
				dao.doInsert(BSPHISEntryNames.GY_JBBM_DZ, parameters,
						false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存对照的疾病诊断信息失败", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存对照的疾病诊断信息失败", e);
		}
		ss.getTransaction().commit();
	}
	/**
	 * 自动匹配西医诊断信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doAuotMatchingDiagnosisContrast(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list_tcm  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_his  = new ArrayList<Map<String, Object>>();
		long successCount = 0;
		try{
			String hql_tcm = "SELECT JBXH as JBXH,ICD10 as ICD10,JBMC as JBMC,PYDM as PYDM FROM GY_JBBM_TCM WHERE JBXH NOT IN(SELECT JBXH_TCM FROM GY_JBBM_DZ)";	
			list_tcm  = dao.doSqlQuery(hql_tcm, parameters);
			String hql_his = "SELECT JBXH as JBXH,ICD10 as ICD10,JBMC as JBMC,PYDM as PYDM FROM GY_JBBM WHERE ZFPB=0 AND JBXH NOT IN(SELECT JBXH FROM GY_JBBM_DZ)";	
			list_his  = dao.doSqlQuery(hql_his, parameters);
		} catch (Exception e) {
			logger.error("查询未匹配的疾病诊断信息失败.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询未匹配的疾病诊断信息失败.");
		}
		if(list_tcm.size() > 0 && list_his.size() > 0){
			for (Map<String, Object> map_tcm : list_tcm) {	
				for (Map<String, Object> map_his : list_his) {
					//if((map_tcm.get("JBXH")+"").equals("20893") && (map_his.get("JBXH")+"").equals("20893")){
					if((map_tcm.get("ICD10")+"").equals(map_his.get("ICD10")+"") || (map_tcm.get("JBMC")+"").equals(map_his.get("JBMC")+"")){
						Session ss = (Session) ctx.get(Context.DB_SESSION);
						ss.beginTransaction();
						Long jbxh = Long.parseLong(map_his.get("JBXH")+"");
						Long jbxh_tcm = Long.parseLong(map_tcm.get("JBXH")+"");
						String icd10 = map_his.get("ICD10") + "";
						String jbmc = map_his.get("JBMC") + "";
						String pydm = map_his.get("PYDM") + "";
						String icd10_tcm = map_tcm.get("ICD10") + "";
						String jbmc_tcm = map_tcm.get("JBMC") + "";
						String pydm_tcm = map_tcm.get("PYDM") + "";
						parameters.clear();
						parameters.put("JBXH", jbxh);
						parameters.put("JBXH_TCM", jbxh_tcm);
						try {
							// 保存的时候，若记录在表里已存在，则不做操作
							long count = dao
									.doCount(
											"GY_JBBM_DZ",
											"JBXH=:JBXH and JBXH_TCM=:JBXH_TCM",
											parameters);
							if (count == 0) {
								parameters.put("ICD10", icd10);
								parameters.put("JBMC", jbmc);
								parameters.put("PYDM", pydm);
								parameters.put("ICD10_TCM", icd10_tcm);
								parameters.put("JBMC_TCM", jbmc_tcm);
								parameters.put("PYDM_TCM", pydm_tcm);
								// 若不存在，插入一条新的记录
								dao.doInsert(BSPHISEntryNames.GY_JBBM_DZ, parameters,
										false);
							}
						} catch (PersistentDataOperationException e) {
							ss.getTransaction().rollback();
							continue;
						} catch (ValidateException e) {
							ss.getTransaction().rollback();
							continue;
						}
						ss.getTransaction().commit();
						successCount += 1;
					}
					//}
				}
			}
		}
		req.put("msg", "成功匹配"+successCount+"条西医疾病诊断信息!");
		return req;
	}
	/**
	 * 查询未匹配的中医疾病诊断（HIS）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doQueryZyDiagnosisContrastHIS(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		try {
 			Map<String, Object> PageMap = new HashMap<String, Object>();
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();	
			Map<String, Object> data = new HashMap<String, Object>();		
			String cndSQL = "";
			StringBuffer countHql = new StringBuffer();
			StringBuffer hql = new StringBuffer();
			long countsAll = 0;
			int pageSize = Constants.DEFAULT_PAGESIZE;
			int pageNo = Constants.DEFAULT_PAGENO;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo");
			}
			int first = (pageNo - 1) * pageSize;
			List cnd =(List)req.get("cnd");
			if (null != cnd) {
				try {
					cndSQL = ExpressionProcessor.instance().toString(cnd);

				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
			}
			countHql.append(" select count(*) as count ")
					.append(" FROM EMR_ZYJB WHERE ZXBZ=0 AND JBBS NOT IN(SELECT JBBS FROM EMR_ZYJB_DZ)");
			if (cndSQL != null && cndSQL.length() > 0) {
				countHql.append(" AND " + cndSQL);
			}
			Map<String, Object> countValue = dao.doLoad(countHql.toString(),
					PageMap);
			if (countValue != null && !countValue.isEmpty()) {
				countsAll = Long.parseLong(countValue.get("count") + "");
			}
			PageMap.put("first", first);
			PageMap.put("max", pageSize);
			hql.append("SELECT JBBS as JBBS,JBDM as JBDM,JBMC as JBMC,PYDM as PYDM FROM EMR_ZYJB WHERE ZXBZ=0 AND JBBS NOT IN(SELECT JBBS FROM EMR_ZYJB_DZ)");	
			if (cndSQL != null && cndSQL.length() > 0) {
				hql.append(" AND " + cndSQL);
			}
			listData = dao.doSqlQuery(hql.toString(), PageMap);
			data.put("totalCount", countsAll);
			data.put("pageNo", pageNo);
			data.put("pageSize", pageSize);
			data.put("body", listData);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询未匹配的中医疾病诊断（HIS）失败", e);
		}
	}
	/**
	 * 查询未匹配的中医疾病诊断（TCM）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doQueryZyDiagnosisContrastTCM(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		try {
 			Map<String, Object> PageMap = new HashMap<String, Object>();
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();	
			Map<String, Object> data = new HashMap<String, Object>();		
			String cndSQL = "";
			StringBuffer countHql = new StringBuffer();
			StringBuffer hql = new StringBuffer();
			long countsAll = 0;
			int pageSize = Constants.DEFAULT_PAGESIZE;
			int pageNo = Constants.DEFAULT_PAGENO;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo");
			}
			int first = (pageNo - 1) * pageSize;
			List cnd =(List)req.get("cnd");
			if (null != cnd) {
				try {
					cndSQL = ExpressionProcessor.instance().toString(cnd);
				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
			}
			countHql.append(" select count(*) as count ")
					.append(" FROM EMR_ZYJB_TCM WHERE JBBS NOT IN(SELECT JBBS_TCM FROM EMR_ZYJB_DZ)");
			if (cndSQL != null && cndSQL.length() > 0) {
				countHql.append(" AND " + cndSQL);
			}
			Map<String, Object> countValue = dao.doLoad(countHql.toString(),
					PageMap);
			if (countValue != null && !countValue.isEmpty()) {
				countsAll = Long.parseLong(countValue.get("count") + "");
			}			
			PageMap.put("first", first);
			PageMap.put("max", pageSize);
			hql.append("SELECT JBBS as JBBS,JBDM as JBDM,JBMC as JBMC,PYDM as PYDM FROM EMR_ZYJB_TCM WHERE JBBS NOT IN(SELECT JBBS_TCM FROM EMR_ZYJB_DZ)");	
			if (cndSQL != null && cndSQL.length() > 0) {
				hql.append(" AND " + cndSQL);
			}
			listData = dao.doSqlQuery(hql.toString(), PageMap);
			data.put("totalCount", countsAll);
			data.put("pageNo", pageNo);
			data.put("pageSize", pageSize);
			data.put("body", listData);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询未匹配的中医疾病诊断（TCM）失败", e);
		}
	}
	/**
	 * 删除已对照的中医疾病诊断
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveZyDiagnosisContrastDz(Map<String, Object> body,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		Long jbbs = Long.parseLong(body.get("jbbs") + "");
		Long jbbs_tcm = Long.parseLong(body.get("jbbs_tcm") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JBBS", jbbs);
		parameters.put("JBBS_TCM", jbbs_tcm);
		StringBuffer removeHql = new StringBuffer();
		removeHql
				.append("delete from "
						+ "EMR_ZYJB_DZ"
						+ " where JBBS=:JBBS and JBBS_TCM=:JBBS_TCM");
		try {
			dao.doUpdate(removeHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除已对照的中医疾病诊断失败", e);
		}
		ss.getTransaction().commit();
	}

	/**
	 * 保存对照的中医疾病诊断信息
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZyDiagnosisContrastDz(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		Long jbbs = Long.parseLong(body.get("jbbs") + "");
		Long jbbs_tcm = Long.parseLong(body.get("jbbs_tcm") + "");
		String jbdm = body.get("jbdm") + "";
		String jbmc = body.get("jbmc") + "";
		String pydm = body.get("pydm") + "";
		String jbdm_tcm = body.get("jbdm_tcm") + "";
		String jbmc_tcm = body.get("jbmc_tcm") + "";
		String pydm_tcm = body.get("pydm_tcm") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JBBS", jbbs);
		parameters.put("JBBS_TCM", jbbs_tcm);
		try {
			// 保存的时候，若记录在表里已存在，则不做操作
			long count = dao
					.doCount(
							"EMR_ZYJB_DZ",
							"JBBS=:JBBS and JBBS_TCM=:JBBS_TCM",
							parameters);
			if (count == 0) {
				parameters.put("JBDM", jbdm);
				parameters.put("JBMC", jbmc);
				parameters.put("PYDM", pydm);
				parameters.put("JBDM_TCM", jbdm_tcm);
				parameters.put("JBMC_TCM", jbmc_tcm);
				parameters.put("PYDM_TCM", pydm_tcm);
				// 若不存在，插入一条新的记录
				dao.doInsert(BSPHISEntryNames.EMR_ZYJB_DZ, parameters,
						false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存对照的中医疾病诊断信息失败", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存对照的中医疾病诊断信息失败", e);
		}
		ss.getTransaction().commit();
	}
	/**
	 * 自动匹配中医疾病诊断信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doAuotMatchingZyDiagnosisContrast(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list_tcm  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_his  = new ArrayList<Map<String, Object>>();
		long successCount = 0;
		try{
			String hql_tcm = "SELECT JBBS as JBBS,JBDM as JBDM,JBMC as JBMC,PYDM as PYDM FROM EMR_ZYJB_TCM WHERE JBBS NOT IN(SELECT JBBS_TCM FROM EMR_ZYJB_DZ)";	
			list_tcm  = dao.doSqlQuery(hql_tcm, parameters);
			String hql_his = "SELECT JBBS as JBBS,JBDM as JBDM,JBMC as JBMC,PYDM as PYDM FROM EMR_ZYJB WHERE ZXBZ=0 AND JBBS NOT IN(SELECT JBBS FROM EMR_ZYJB_DZ)";	
			list_his  = dao.doSqlQuery(hql_his, parameters);
		} catch (Exception e) {
			logger.error("查询未匹配的中医疾病诊断信息失败.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询未匹配的中医疾病诊断信息失败.");
		}
		if(list_tcm.size() > 0 && list_his.size() > 0){
			for (Map<String, Object> map_tcm : list_tcm) {	
				for (Map<String, Object> map_his : list_his) {
					if((map_tcm.get("JBDM")+"").equals(map_his.get("JBDM")+"") || (map_tcm.get("JBMC")+"").equals(map_his.get("JBMC")+"")){
						Session ss = (Session) ctx.get(Context.DB_SESSION);
						ss.beginTransaction();
						Long jbbs = Long.parseLong(map_his.get("JBBS")+"");
						Long jbbs_tcm = Long.parseLong(map_tcm.get("JBBS")+"");
						String jbdm = map_his.get("JBDM") + "";
						String jbmc = map_his.get("JBMC") + "";
						String pydm = map_his.get("PYDM") + "";
						String jbdm_tcm = map_tcm.get("JBDM") + "";
						String jbmc_tcm = map_tcm.get("JBMC") + "";
						String pydm_tcm = map_tcm.get("PYDM") + "";
						parameters.clear();
						parameters.put("JBBS", jbbs);
						parameters.put("JBBS_TCM", jbbs_tcm);
						try {
							// 保存的时候，若记录在表里已存在，则不做操作
							long count = dao
									.doCount(
											"EMR_ZYJB_DZ",
											"JBBS=:JBBS and JBBS_TCM=:JBBS_TCM",
											parameters);
							if (count == 0) {
								parameters.put("JBDM", jbdm);
								parameters.put("JBMC", jbmc);
								parameters.put("PYDM", pydm);
								parameters.put("JBDM_TCM", jbdm_tcm);
								parameters.put("JBMC_TCM", jbmc_tcm);
								parameters.put("PYDM_TCM", pydm_tcm);
								// 若不存在，插入一条新的记录
								dao.doInsert(BSPHISEntryNames.EMR_ZYJB_DZ, parameters,
										false);
							}
						} catch (PersistentDataOperationException e) {
							ss.getTransaction().rollback();
							continue;
						} catch (ValidateException e) {
							ss.getTransaction().rollback();
							continue;
						}
						ss.getTransaction().commit();
						successCount += 1;
					}
				}
			}
		}
		req.put("msg", "成功匹配"+successCount+"条中医诊断信息!");
		return req;
	}
	/**
	 * 查询未匹配的中医证候（HIS）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doQueryZhDiagnosisContrastHIS(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		try {
 			Map<String, Object> PageMap = new HashMap<String, Object>();
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();	
			Map<String, Object> data = new HashMap<String, Object>();		
			String cndSQL = "";
			StringBuffer countHql = new StringBuffer();
			StringBuffer hql = new StringBuffer();
			long countsAll = 0;
			int pageSize = Constants.DEFAULT_PAGESIZE;
			int pageNo = Constants.DEFAULT_PAGENO;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo");
			}
			int first = (pageNo - 1) * pageSize;
			List cnd =(List)req.get("cnd");
			if (null != cnd) {
				try {
					cndSQL = ExpressionProcessor.instance().toString(cnd);
				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
			}
			countHql.append(" select count(*) as count ")
					.append("FROM EMR_ZYZH WHERE ZXBZ=0 AND ZHBS NOT IN(SELECT ZHBS FROM EMR_ZYZH_DZ)");
			if (cndSQL != null && cndSQL.length() > 0) {
				countHql.append(" AND " + cndSQL);
			}
			Map<String, Object> countValue = dao.doLoad(countHql.toString(),
					PageMap);
			if (countValue != null && !countValue.isEmpty()) {
				countsAll = Long.parseLong(countValue.get("count") + "");
			}			
			PageMap.put("first", first);
			PageMap.put("max", pageSize);
			hql.append("SELECT ZHBS as ZHBS,ZHDM as ZHDM,ZHMC as ZHMC,PYDM as PYDM FROM EMR_ZYZH WHERE ZXBZ=0 AND ZHBS NOT IN(SELECT ZHBS FROM EMR_ZYZH_DZ)");	
			if (cndSQL != null && cndSQL.length() > 0) {
				hql.append(" AND " + cndSQL);
			}
			listData = dao.doSqlQuery(hql.toString(), PageMap);
			data.put("totalCount", countsAll);
			data.put("pageNo", pageNo);
			data.put("pageSize", pageSize);
			data.put("body", listData);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询未匹配的中医证候（HIS）失败", e);
		}
	}
	/**
	 * 查询未匹配的中医证候（TCM）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doQueryZhDiagnosisContrastTCM(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		try {
 			Map<String, Object> PageMap = new HashMap<String, Object>();
			List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();	
			Map<String, Object> data = new HashMap<String, Object>();		
			String cndSQL = "";
			StringBuffer countHql = new StringBuffer();
			StringBuffer hql = new StringBuffer();
			long countsAll = 0;
			int pageSize = Constants.DEFAULT_PAGESIZE;
			int pageNo = Constants.DEFAULT_PAGENO;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo");
			}
			int first = (pageNo - 1) * pageSize;
			List cnd =(List)req.get("cnd");
			if (null != cnd) {
				try {
					cndSQL = ExpressionProcessor.instance().toString(cnd);
				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
			}
			countHql.append(" select count(*) as count ")
					.append("FROM EMR_ZYZH_TCM WHERE ZHBS NOT IN(SELECT ZHBS_TCM FROM EMR_ZYZH_DZ)");
			if (cndSQL != null && cndSQL.length() > 0) {
				countHql.append(" AND " + cndSQL);
			}
			Map<String, Object> countValue = dao.doLoad(countHql.toString(),
					PageMap);
			if (countValue != null && !countValue.isEmpty()) {
				countsAll = Long.parseLong(countValue.get("count") + "");
			}			
			PageMap.put("first", first);
			PageMap.put("max", pageSize);
			hql.append("SELECT ZHBS as ZHBS,ZHDM as ZHDM,ZHMC as ZHMC,PYDM as PYDM FROM EMR_ZYZH_TCM WHERE ZHBS NOT IN(SELECT ZHBS_TCM FROM EMR_ZYZH_DZ)");	
			if (cndSQL != null && cndSQL.length() > 0) {
				hql.append(" AND " + cndSQL);
			}
			listData = dao.doSqlQuery(hql.toString(), PageMap);
			data.put("totalCount", countsAll);
			data.put("pageNo", pageNo);
			data.put("pageSize", pageSize);
			data.put("body", listData);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询未匹配的中医证候（TCM）失败", e);
		}
	}
	/**
	 * 删除已对照的中医证候
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveZhDiagnosisContrastDz(Map<String, Object> body,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		Long zhbs = Long.parseLong(body.get("zhbs") + "");
		Long zhbs_tcm = Long.parseLong(body.get("zhbs_tcm") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZHBS", zhbs);
		parameters.put("ZHBS_TCM",zhbs_tcm);
		StringBuffer removeHql = new StringBuffer();
		removeHql
				.append("delete from "
						+ "EMR_ZYZH_DZ"
						+ " where ZHBS=:ZHBS and ZHBS_TCM=:ZHBS_TCM");
		try {
			dao.doUpdate(removeHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除已对照的中医证候失败", e);
		}
		ss.getTransaction().commit();
	}

	/**
	 * 保存对照的中医证候信息
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZhDiagnosisContrastDz(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		Long zhbs = Long.parseLong(body.get("zhbs") + "");
		Long zhbs_tcm = Long.parseLong(body.get("zhbs_tcm") + "");
		String zhdm = body.get("zhdm") + "";
		String zhmc = body.get("zhmc") + "";
		String pydm = body.get("pydm") + "";
		String zhdm_tcm = body.get("zhdm_tcm") + "";
		String zhmc_tcm = body.get("zhmc_tcm") + "";
		String pydm_tcm = body.get("pydm_tcm") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZHBS", zhbs);
		parameters.put("ZHBS_TCM", zhbs_tcm);
		try {
			// 保存的时候，若记录在表里已存在，则不做操作
			long count = dao
					.doCount(
							"EMR_ZYZH_DZ",
							"ZHBS=:ZHBS and ZHBS_TCM=:ZHBS_TCM",
							parameters);
			if (count == 0) {
				parameters.put("ZHDM", zhdm);
				parameters.put("ZHMC",zhmc);
				parameters.put("PYDM", pydm);
				parameters.put("ZHDM_TCM", zhdm_tcm);
				parameters.put("ZHMC_TCM", zhmc_tcm);
				parameters.put("PYDM_TCM", pydm_tcm);
				// 若不存在，插入一条新的记录
				dao.doInsert(BSPHISEntryNames.EMR_ZYZH_DZ, parameters,
						false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存对照的中医证候信息失败", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存对照的中医证候信息失败", e);
		}
		ss.getTransaction().commit();
	}
	/**
	 * 自动匹配中医证候信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doAuotMatchingZhDiagnosisContrast(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list_tcm  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_his  = new ArrayList<Map<String, Object>>();
		long successCount = 0;
		try{
			String hql_tcm = "SELECT ZHBS as ZHBS,ZHDM as ZHDM,ZHMC as ZHMC,PYDM as PYDM FROM EMR_ZYZH_TCM WHERE ZHBS NOT IN(SELECT ZHBS_TCM FROM EMR_ZYZH_DZ)";	
			list_tcm  = dao.doSqlQuery(hql_tcm, parameters);
			String hql_his = "SELECT ZHBS as ZHBS,ZHDM as ZHDM,ZHMC as ZHMC,PYDM as PYDM FROM EMR_ZYZH WHERE ZXBZ=0 AND ZHBS NOT IN(SELECT ZHBS FROM EMR_ZYZH_DZ)";	
			list_his  = dao.doSqlQuery(hql_his, parameters);
		} catch (Exception e) {
			logger.error("查询未匹配的中医证候信息失败.",e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询未匹配的中医证候信息失败.");
		}
		if(list_tcm.size() > 0 && list_his.size() > 0){
			for (Map<String, Object> map_tcm : list_tcm) {	
				for (Map<String, Object> map_his : list_his) {
					if((map_tcm.get("ZHDM")+"").equals(map_his.get("ZHDM")+"") || (map_tcm.get("ZHMC")+"").equals(map_his.get("ZHMC")+"")){
						Session ss = (Session) ctx.get(Context.DB_SESSION);
						ss.beginTransaction();
						Long zhbs = Long.parseLong(map_his.get("ZHBS")+"");
						Long zhbs_tcm = Long.parseLong(map_tcm.get("ZHBS")+"");
						String zhdm = map_his.get("ZHDM") + "";
						String zhmc = map_his.get("ZHMC") + "";
						String pydm = map_his.get("PYDM") + "";
						String zhdm_tcm = map_tcm.get("ZHDM") + "";
						String zhmc_tcm = map_tcm.get("ZHMC") + "";
						String pydm_tcm = map_tcm.get("PYDM") + "";
						parameters.clear();
						parameters.put("ZHBS", zhbs);
						parameters.put("ZHBS_TCM", zhbs_tcm);
						try {
							// 保存的时候，若记录在表里已存在，则不做操作
							long count = dao
									.doCount(
											"EMR_ZYZH_DZ",
											"ZHBS=:ZHBS and ZHBS_TCM=:ZHBS_TCM",
											parameters);
							if (count == 0) {
								parameters.put("ZHDM", zhdm);
								parameters.put("ZHMC", zhmc);
								parameters.put("PYDM", pydm);
								parameters.put("ZHDM_TCM", zhdm_tcm);
								parameters.put("ZHMC_TCM", zhmc_tcm);
								parameters.put("PYDM_TCM", pydm_tcm);
								// 若不存在，插入一条新的记录
								dao.doInsert(BSPHISEntryNames.EMR_ZYZH_DZ, parameters,
										false);
							}
						} catch (PersistentDataOperationException e) {
							ss.getTransaction().rollback();
							continue;
						} catch (ValidateException e) {
							ss.getTransaction().rollback();
							continue;
						}
						ss.getTransaction().commit();
						successCount += 1;
					}
				}
			}
		}
		req.put("msg", "成功匹配"+successCount+"条中医证候信息!");
		return req;
	}

	/**
	 * 获取跨域url页面内容
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doGetUrlPageContent(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			String url = body.get("url") + "";
			String pdata = body.get("pdata") + "";
			String tagUrl = callWebPagePost(url,pdata,Integer.parseInt(body.get("timeout") + ""));
			req.put("tagUrl", tagUrl);
		} catch (Exception e) {
			logger.error("获取跨域url页面内容.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "获取跨域url页面内容.");
		}
		return req;
	}
	/**
	 * 用get方式连接url
	 * @param urlString url路径
	 * @param pdata    url参数
	 * @return 从url获得的数据
	 */
	private String callWebPageGet(String urlString, String pdata) {
		String result = "";
		PrintWriter out = null;
		BufferedReader in = null;
		URL url = null;
		try {
			url = new URL(urlString + "?" + pdata);// 用url路径以及所用参数创建URL实例类
			URLConnection connect = url.openConnection();// 创建连接
			connect.setRequestProperty("content-type",
					"application/x-www-form-urlencoded;charset=utf-8");// 设置请求header的属性--请求内容类型
			connect.setRequestProperty("method", "GET");// 设置请求header的属性值--请求方式
			// 建立实际的连接
			connect.connect(); // 建立与url所在服务器的连接
			// 获取所有响应头字段
			Map<String, List<String>> map = connect.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connect.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 用gpost方式连接url
	 * @param urlString url路径
	 * @param pdata    url参数
	 * @return 从url获得的数据
	 */
	private String callWebPagePost(String urlString, String pdata, int timeout) {
		String result = "";
		PrintWriter out = null;
		BufferedReader in = null;
		URL url = null;
		try {
			url = new URL(urlString);
			URLConnection connect = url.openConnection();
			connect.setRequestProperty("content-type",
					"application/x-www-form-urlencoded;charset=utf-8");
			connect.setRequestProperty("method", "POST");
			byte[] bytes = pdata.getBytes("utf-8");
			connect.setDoOutput(true);
			connect.setDoInput(true);
			connect.setConnectTimeout(timeout);
			connect.setReadTimeout(timeout);

			//out = new PrintWriter(connect.getOutputStream());
			out = new PrintWriter(new OutputStreamWriter(connect.getOutputStream(), "utf-8"));
			// 发送请求参数
			out.print(pdata);
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			//in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			in = new BufferedReader(new InputStreamReader(connect.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 推送患者信息到省中医馆平台
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doUploadBrxxToTcm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String jzxh = body.get("jzxh") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		String apiAddress = ParameterUtil.getParameter(jgid.substring(0,4),"TCM_API_01", ctx);
		try {
			//根据就诊序号获取病人信息
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT B.BRID AS BRID,B.BRXM AS BRXM,B.BRXB AS BRXBDM,DECODE(B.BRXB,0,'未知的性别',1,'男',2,'女','未说明的性别') AS BRXB," +
					"TO_CHAR(B.CSNY,'YYYYMMDD') AS CSNY,B.SFZH AS SFZH,A.JZXH AS JZXH,C.ORGANIZCODE_TCM AS ORGANIZCODE_TCM," +
					"A.TCM_BRXX  AS TCM_BRXX,B.BRXZ AS BRXZDM,DECODE(B.BRXZ,2000,'医保',3000,'医保',6000,'新农合','自费') AS BRXZ,B.LXDH AS LXDH  " +
					"FROM YS_MZ_JZLS A,MS_BRDA B,SYS_ORGANIZATION C WHERE A.BRBH=B.BRID AND substr(A.JGID,0,9)=C.ORGANIZCODE " +
					" AND A.JZXH="+jzxh);	
			List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(), parameters);
			if(listData == null){
				req.put("msg","failed");
				return req;
			}
			String reqMessage = "";
			for (Map<String, Object> brxx : listData) {
				if (brxx.get("TCM_BRXX")!=null && brxx.get("TCM_BRXX").toString().equals("1")) {
					req.put("msg","success");
					return req;
				}
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式		
				Date now = new Date(); 
				String strDate = dateFormat.format(now); 
				reqMessage = "<Request><Header><sender>HIS</sender><receiver>EMR,HEAL</receiver><sendTime>"+strDate+"</sendTime>" +
						"<msgType>TCM_HIS_01</msgType><msgID>HIS"+strDate+"</msgID></Header><Patient><id>"+brxx.get("BRID")+"</id>" +
						"<name>"+brxx.get("BRXM")+"</name><genderCode>"+brxx.get("BRXBDM")+"</genderCode><gender>"+brxx.get("BRXB")+"</gender>" +
						"<birthday>"+brxx.get("CSNY")+"</birthday><cardTypeCode>01</cardTypeCode><cardType>居民身份证</cardType>" +
						"<cardNo>"+brxx.get("SFZH")+"</cardNo><occupationCode></occupationCode><occupation></occupation><marriedCode></marriedCode><married></married><countryCode></countryCode><country></country><nationalityCode></nationalityCode><nationality></nationality><provinceCode></provinceCode><cityCode></cityCode><areaCode>320000</areaCode><province></province><city></city><area></area><streetInfo></streetInfo><postcode></postcode><ctName></ctName><ctRoleId></ctRoleId><ctAddr></ctAddr><ctTelephone></ctTelephone>" +
						"<patiTypeCode>"+brxx.get("BRXZDM")+"</patiTypeCode><patiType>"+brxx.get("BRXZ")+"</patiType>" +
						"<telephone>"+brxx.get("LXDH")+"</telephone><outpatientNo>"+jzxh+"</outpatientNo>" +
						"<orgCode>"+brxx.get("ORGANIZCODE_TCM")+"</orgCode></Patient></Request>";
				break;
			}		
			Tcmservices.Tcm01._Proxy7ServiceLocator service = new Tcmservices.Tcm01._Proxy7ServiceLocator();
			// 获取系统参数中医馆平台接口地址（推送患者信息）
			service.setTCM_HIS_01EndpointAddress(apiAddress);
			Tcmservices.Tcm01.TCM_HIS_01SoapBindingStub th01 = (Tcmservices.Tcm01.TCM_HIS_01SoapBindingStub)service.getTCM_HIS_01();
			th01.setTimeout(2000);
			String res = th01.acceptMessage(reqMessage);
			if(res.contains("has been successfully received")){
				//更新ys_mz_jzls表
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.beginTransaction();
				parameters.put("JZXH",Long.parseLong(jzxh));
				StringBuffer removeHql = new StringBuffer();
				removeHql
						.append("update "
								+ "YS_MZ_JZLS"
								+ " set TCM_BRXX=1 where JZXH=:JZXH");
				try {
					dao.doUpdate(removeHql.toString(), parameters);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
					ss.getTransaction().rollback();
					throw new ModelDataOperationException("更新ys_mz_jzls表字段TCM_BRXX失败", e);
				}
				ss.getTransaction().commit();
				req.put("msg","success");
			}else
			{
				req.put("msg","failed");
			}
		} catch (Exception e) {
			req.put("msg","failed");
			if(e.getMessage().contains("timed out")){
				logger.error("省中医馆平台接口调用超时:"+apiAddress);
				return req;
			}
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "推送患者信息到省中医馆平台失败.");
		}
		return req;
	}
	/**
	 * 根据jgid获取对照的省中医馆平台机构编码
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doGetTcmJgid(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		try {
			//获取当前机构编码对应的省中医馆系统中机构编码
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT ORGANIZCODE_TCM AS ORGANIZCODE_TCM FROM SYS_ORGANIZATION WHERE ORGANIZCODE=:ORGANIZCODE");	
			parameters.put("ORGANIZCODE",jgid.substring(0,9));
			List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(), parameters);
			if(listData != null){
				for (Map<String, Object> list : listData) {
					if (list.get("ORGANIZCODE_TCM")!=null) {
						req.put("tcmjgid",list.get("ORGANIZCODE_TCM").toString());
						return req;
					}
				}
			}
		} catch (Exception e) {
			logger.error("根据jgid获取对照的省中医馆平台机构编码.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "根据jgid获取对照的省中医馆平台机构编码.");
		}
		req.put("tcmjgid","");
		return req;
	}
	/**
	 * 从省中医馆平台获取电子病历信息
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doGetDzblFromTcm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String tcmjgid = "";
		String jzxh = body.get("jzxh") + "";
		String jgid = user.getManageUnitId();
		String apiAddress09 = ParameterUtil.getParameter(user.getManageUnitId().substring(0,4),"TCM_API_09", ctx);
		String apiAddress07 = ParameterUtil.getParameter(user.getManageUnitId().substring(0,4),"TCM_API_07", ctx);
		try {
			//获取当前机构编码对应的省中医馆系统中机构编码
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT ORGANIZCODE_TCM AS ORGANIZCODE_TCM FROM SYS_ORGANIZATION WHERE ORGANIZCODE=:ORGANIZCODE");	
			parameters.put("ORGANIZCODE",jgid.substring(0,9));
			List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(), parameters);
			if(listData != null){
				for (Map<String, Object> list : listData) {
					if (list.get("ORGANIZCODE_TCM")!=null) {
						tcmjgid =list.get("ORGANIZCODE_TCM").toString();
						break;
					}
				}
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式		
			Date now = new Date(); 
			String strDate = dateFormat.format(now); 
			
			// 获取系统参数中医馆平台接口地址（接收诊断信息）
			Tcmservices.Tcm09.CommonService9Locator service09 = new Tcmservices.Tcm09.CommonService9Locator();
			service09.setTCM_HIS_09EndpointAddress(apiAddress09);
			Tcmservices.Tcm09.CommonService9SoapBindingStub th09 = (Tcmservices.Tcm09.CommonService9SoapBindingStub)service09.getTCM_HIS_09();
			th09.setTimeout(2000);
			String reqMessage = "<Request><Header><sender>EMR</sender><receiver>HIS</receiver><sendTime>"+strDate+"</sendTime>" +
					"<msgType>TCM_HIS_09</msgType><msgID>HIS"+strDate+"</msgID></Header><orgCode>"+tcmjgid+"</orgCode>" +
					"<serialNo>"+jzxh+"</serialNo></Request>";
			String res = th09.acceptMessage(reqMessage);
			//String res = "<Request><Header><sender>EMR</sender><receiver>HIS</receiver><sendTime>20180723194906</sendTime><msgID>EMR20180723194906</msgID><msgType>TCM_HIS_09</msgType></Header><DiagInfo><serialNo>443000</serialNo><diagnosislist><Diagnosis><level>1</level><cate>1</cate><description></description><name>内伤咳嗽病|热毒上攻证</name><code>BNF012|ZBRDC00</code><group></group><type>0</type></Diagnosis><Diagnosis><level>1</level><cate>0</cate><description></description><name>上呼吸道感染</name><code>J06.903</code><group></group><type>1</type></Diagnosis></diagnosislist><orgCode>426051235</orgCode></DiagInfo></Request>";
			List<Map<String, Object>> zdlistData = new ArrayList<Map<String, Object>>();
			Document doc = null;
			doc = DocumentHelper.parseText(res);
			if(res.contains("<diagnosislist><Diagnosis>")){
				doc = DocumentHelper.parseText(res);
				Element diagInfoElt = (Element) doc.getRootElement().element("DiagInfo"); // 获取body节点
				Iterator iters = diagInfoElt.elementIterator("diagnosislist");
				while (iters.hasNext()) {
					SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式		
					Date now2 = new Date(); 
					String strDate2 = dateFormat2.format(now2); 
	                Element recordEle1 = (Element) iters.next();
	                Iterator iter = recordEle1.elementIterator("Diagnosis");
	                while (iter.hasNext()) {
	                	Map<String, Object> zdData = new HashMap<String,Object>();
                    	zdData.put("JZXH", jzxh);
	                	Element recordEle = (Element) iter.next();
	                    String cate = recordEle.elementTextTrim("cate");
	                    if(cate.equals("0")){//西医诊断
	                    	Map<String, Object> jbmap = getHisJbbmByTcm("gy_jbbm",recordEle.elementTextTrim("code"),recordEle.elementTextTrim("name"));
	                    	if(jbmap.size()==0 || checkbrJbbm(jzxh,jbmap.get("jbdm").toString(),jbmap.get("jbmc").toString())){
	                    		continue;
	                    	}
	                    	zdData.put("ICD10", jbmap.get("jbdm").toString());//GY_JBBM,EMR_ZYJB,EMR_ZYZH
	                    	zdData.put("ZDMC", jbmap.get("jbmc").toString());
	                    	zdData.put("ZDXH", jbmap.get("jbxh").toString());//诊断序号，对应诊断字典表主键
	                    	zdData.put("CFLX", "1");
	                    	zdData.put("CFLX_text", "西药");
	                    	zdData.put("DEEP", 0);
	                    	zdData.put("FBRQ", strDate2);
	                    	zdData.put("FZBZ", "0");
	                    	zdData.put("FZBZ_text", "初诊");
	                    	zdData.put("JBBGK", "");
	                    	zdData.put("JBBGK_text", "");
	                    	zdData.put("JBPB", "");
	                    	zdData.put("JBPB_text", "");
	                    	zdData.put("MC", "");
	                    	zdData.put("PLXH", "0");
	                    	zdData.put("SJZD", "0");
	                    	zdData.put("ZDBW", "0");//部位ID/证侯ID
	                    	zdData.put("ZDLB", "1");
	                    	zdData.put("ZDLB_text", "门急诊诊断");
	                    	zdData.put("ZDSJ", strDate2);
	                    	zdData.put("ZXLB", "1");
	                    	zdData.put("ZXLB_text", "西医");
	                    	zdData.put("ZZBZ", recordEle.elementTextTrim("level"));
	                    	zdlistData.add(zdData);	
	                    }
	                    else if(cate.equals("1")){//中医诊断
	                    	String name = recordEle.elementTextTrim("name");
	                    	String code = recordEle.elementTextTrim("code");
	                    	Map<String, Object> jbmap = getHisJbbmByTcm("emr_zyjb",code.split("[|]")[0],name.split("[|]")[0]);
	                    	if(jbmap.size()==0 || checkbrJbbm(jzxh,jbmap.get("jbdm").toString(),jbmap.get("jbmc").toString())){
	                    		continue;
	                    	}
	                    	zdData.put("ICD10", jbmap.get("jbdm").toString());//emr_zyjb表jbdm
	                    	zdData.put("ZDMC", jbmap.get("jbmc").toString());//emr_zyjb表jbmc 
	                    	zdData.put("ZDXH", jbmap.get("jbxh").toString());//诊断序号，对应诊断字典表主键
	                    	jbmap = getHisJbbmByTcm("emr_zyzh",code.split("[|]")[1],name.split("[|]")[1]);
	                    	zdData.put("ZDBW", jbmap.get("jbxh").toString());//部位ID/证侯ID
	                    	zdData.put("MC", jbmap.get("jbmc").toString());//中医证候名称
	                    	zdData.put("CFLX", "2");
	                    	zdData.put("CFLX_text", "中药");
	                    	zdData.put("DEEP", 0);
	                    	zdData.put("FBRQ", strDate2);
	                    	zdData.put("FZBZ", "0");
	                    	zdData.put("FZBZ_text", "初诊");
	                    	zdData.put("JBBGK", "");
	                    	zdData.put("JBBGK_text", "");
	                    	zdData.put("JBPB", "");
	                    	zdData.put("JBPB_text", "");
	                    	zdData.put("PLXH", "0");
	                    	zdData.put("SJZD", "0");
	                    	zdData.put("ZDLB", "1");
	                    	zdData.put("ZDLB_text", "");
	                    	zdData.put("ZDSJ", strDate2);
	                    	zdData.put("ZXLB", "2");
	                    	zdData.put("ZXLB_text", "中医");
	                    	zdData.put("ZZBZ", "0");
	                    	zdlistData.add(zdData);		                    	
	                    }
				req.put("tcmzd",zdlistData);
			}
				}
			// 获取系统参数中医馆平台接口地址（接收病历信息）
			Tcmservices.Tcm07.CommonService7_ServiceLocator service07 = new Tcmservices.Tcm07.CommonService7_ServiceLocator();
			service07.setTCM_HIS_07EndpointAddress(apiAddress07);
			Tcmservices.Tcm07.CommonService7SoapBindingStub th07 = (Tcmservices.Tcm07.CommonService7SoapBindingStub)service07.getTCM_HIS_07();
			th07.setTimeout(2000);
			reqMessage = "<Request><Header><sender>EMR</sender><receiver>HIS</receiver><sendTime>"+strDate+"</sendTime>" +
					"<msgType>TCM_HIS_07</msgType><msgID>HIS"+strDate+"</msgID></Header><orgCode>"+tcmjgid+"</orgCode>" +
					"<serialNo>"+jzxh+"</serialNo></Request>";
			res = th07.acceptMessage(reqMessage);
			//res = "<Request><Record><recordId></recordId><allergicHis>ss</allergicHis><palpation>fs</palpation><smelling></smelling><priDepict>sss</priDepict><fhx></fhx><inspection>ds</inspection><clinicDate>2018-07-21 17:36:59.0</clinicDate><mcObstetrical></mcObstetrical><serialNo>443001</serialNo><localDisHis>sss</localDisHis><individualHis></individualHis><doctorId>10130016</doctorId><psyCheck>fs</psyCheck><orgCode>426051235</orgCode><suggession>fsfa</suggession><name>test06</name><age>33岁</age><anamnesis></anamnesis><syndrome></syndrome><gender>男</gender><doctorName></doctorName><askinfo></askinfo><patiId>237001</patiId></Record><Header><sender>EMR</sender><receiver>HIS</receiver><sendTime>20180721173744</sendTime><msgID>EMR20180721173744</msgID><msgType>TCM_HIS_07</msgType></Header></Request>";
			Map<String,Object> dzblMap = new HashMap<String,Object>();
			if(res.contains("<Request><Record>")){				
				doc = DocumentHelper.parseText(res);
				Element recordElt = (Element) doc.getRootElement().element("Record"); // 获取body节点	
				dzblMap.put("JZXH", recordElt.elementTextTrim("serialNo"));//就诊流水号
				dzblMap.put("ZSXX", recordElt.elementTextTrim("priDepict"));//主诉信息
				dzblMap.put("XBS", recordElt.elementTextTrim("localDisHis"));//现病史
				dzblMap.put("GMS", recordElt.elementTextTrim("allergicHis"));//过敏史
				dzblMap.put("JWS", recordElt.elementTextTrim("anamnesis"));//既往史
				dzblMap.put("TGJC", recordElt.elementTextTrim("psyCheck"));//体格检查
				dzblMap.put("BQGZ", recordElt.elementTextTrim("suggession"));//病情告知
				dzblMap.put("ZZDM", "");//症状编码
				dzblMap.put("ZZMC", "");//症状名称			
				dzblMap.put("FZJC", "");//辅助检查
				dzblMap.put("ZZ", "");//治则
				dzblMap.put("RSBZ", "");//妊娠标志
				dzblMap.put("DPY", "0");//代配药
				dzblMap.put("T", "");//体温
				dzblMap.put("R", "");//呼吸
				dzblMap.put("P", "");//脉搏
				dzblMap.put("H", "");//身高
				dzblMap.put("W", "");//体重
				dzblMap.put("BMI", "");//
				dzblMap.put("SSY", "");//收缩压
				dzblMap.put("SZY", "");//舒张压
				dzblMap.put("LEFTVISION", "");//左眼视力
				dzblMap.put("RIGHTVISION", "");//右眼视力
				dzblMap.put("LEFTCORRECTEDVISION", "");//左眼矫正视力
				dzblMap.put("RIGHTCORRECTEDVISION", "");//右眼矫正视力
				dzblMap.put("KS", "0");//咳嗽
				dzblMap.put("YT", "0");//咽痛
				dzblMap.put("HXKN", "0");//呼吸困难
				dzblMap.put("OT",  "0");//呕吐
				dzblMap.put("FT", "0");//腹痛
				dzblMap.put("FX", "0");//腹泻
				dzblMap.put("PZ", "0");//皮疹
				dzblMap.put("QT", "0");//其他
				dzblMap.put("JKCF", "");//健康教育
				req.put("tcmdzbl",dzblMap);
			}		
			}
		} catch (Exception e) {
			req.put("msg","failed");
			if(e.getMessage().contains("timed out")){
				logger.error("省中医馆平台接口调用超时:"+apiAddress09 +"、"+apiAddress07);
				return req;
			}
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "从省中医馆平台获取电子病历信息失败.");
		}
		return req;
	}
			/**
			 * 获取省中医馆平台疾病编码对应的HIS疾病编码信息
			 * 
			 * @param 
			 * @param 
			 * @return
			 * @throws 
			 */
			@SuppressWarnings("unchecked")
			private Map<String, Object> getHisJbbmByTcm(String tablename,String jbdm,String jbmc){
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer hql = new StringBuffer();
				Map<String,Object> jbMap = new HashMap<String,Object>();
				try {
					parameters.put("JBDM", jbdm);
					parameters.put("JBMC", jbmc);
					/*switch (getIntercodeItem(tablename)) {
						case GY_JBBM:
							hql.append("SELECT JBXH as JBXH,ICD10 AS JBDM,JBMC AS JBMC FROM GY_JBBM_DZ WHERE ICD10_TCM=:JBDM AND JBMC_TCM=:JBMC");
							break;
						case EMR_ZYJB:
							hql.append("SELECT JBBS as JBXH,JBDM AS JBDM,JBMC AS JBMC FROM EMR_ZYJB_DZ WHERE JBDM_TCM=:JBDM AND JBMC_TCM=:JBMC");
							break;
						case EMR_ZYZH:
							hql.append("SELECT ZHBS as JBXH,ZHDM AS JBDM,ZHMC AS JBMC FROM EMR_ZYZH_DZ WHERE ZHDM_TCM=:JBDM AND ZHMC_TCM=:JBMC");
							break;
						default:
							break;
					}*/
					if(tablename.equals("gy_jbbm")){
						hql.append("SELECT JBXH as JBXH,ICD10 AS JBDM,JBMC AS JBMC FROM GY_JBBM_DZ WHERE ICD10_TCM=:JBDM AND JBMC_TCM=:JBMC");
					}
					else if(tablename.equals("emr_zyjb")){
						hql.append("SELECT JBBS as JBXH,JBDM AS JBDM,JBMC AS JBMC FROM EMR_ZYJB_DZ WHERE JBDM_TCM=:JBDM AND JBMC_TCM=:JBMC");
					}
					else if(tablename.equals("emr_zyzh")){
						hql.append("SELECT ZHBS as JBXH,ZHDM AS JBDM,ZHMC AS JBMC FROM EMR_ZYZH_DZ WHERE ZHDM_TCM=:JBDM AND ZHMC_TCM=:JBMC");
					}
					List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(),
							parameters);
					if (listData != null) {
						for (Map<String, Object> list : listData) {
							jbMap.put("jbxh", list.get("JBXH").toString());
							jbMap.put("jbdm", list.get("JBDM").toString());
							jbMap.put("jbmc", list.get("JBMC").toString());
							break;
						}
					}
					else{
						jbMap.put("jbxh", "");
						jbMap.put("jbdm", "");
						jbMap.put("jbmc", "");
					}
				} catch (Exception e) {
					logger.error("获取省中医馆平台疾病编码对应的HIS疾病编码信息.", e);
				}
				return jbMap;
			}
			/*private enum intercodeItem{
				GY_JBBM,EMR_ZYJB,EMR_ZYZH
			    }
		    public intercodeItem getIntercodeItem(String s){
		     return intercodeItem.valueOf(s.toUpperCase());
		    }*/
			/**
			 * 校验病人是否已有该诊断信息
			 * 
			 * @param 
			 * @param 
			 * @return
			 * @throws 
			 */
			@SuppressWarnings("unchecked")
			private Boolean checkbrJbbm(String jzxh,String jbdm,String jbmc){
				try {
					String hqlcount = "SELECT COUNT(*) TOTALCOUNT FROM MS_BRZD WHERE JZXH=:JZXH AND ICD10=:JBDM AND ZDMC=:JBMC";
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", jzxh);
					parameters.put("JBDM", jbdm);
					parameters.put("JBMC", jbmc);
					long count = Long.parseLong(dao.doSqlLoad(hqlcount, parameters).get("TOTALCOUNT")+"");
					if(count>0){
						return true;
					}
				} catch (Exception e) {
					logger.error("校验病人是否已有该诊断信息.",	e);
				}
				return false;
			}
			/**
			 * 从省中医馆平台获取处方信息
			 * 
			 * @param body
			 * @param ctx
			 * @return
			 * @throws ModelDataOperationException
			 */
			@SuppressWarnings("unchecked")
			public Map<String, Object> doGetCfxxFromTcm(Map<String, Object> body,
					Context ctx) throws ModelDataOperationException {
				Map<String, Object> req = new HashMap<String, Object>();
				Map<String, Object> parameters = new HashMap<String, Object>();
				UserRoleToken user = UserRoleToken.getCurrent();
				String tcmjgid = "";
				String jzxh = body.get("jzxh") + "";
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式		
				Date now = new Date(); 
				String strDate = dateFormat.format(now); 
				String jgid = user.getManageUnitId();
				String apiAddress = ParameterUtil.getParameter(user.getManageUnitId().substring(0,4),"TCM_API_08", ctx);
				try {
					//获取当前机构编码对应的省中医馆系统中机构编码
					StringBuffer hql = new StringBuffer();
					hql.append("SELECT ORGANIZCODE_TCM AS ORGANIZCODE_TCM FROM SYS_ORGANIZATION WHERE ORGANIZCODE=:ORGANIZCODE");	
					parameters.put("ORGANIZCODE",jgid.substring(0,9));
					List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(), parameters);
					if(listData != null){
						for (Map<String, Object> list : listData) {
							if (list.get("ORGANIZCODE_TCM")!=null) {
								tcmjgid =list.get("ORGANIZCODE_TCM").toString();
								break;
							}
						}
					}					
					// 获取系统参数中医馆平台接口地址（接收处方信息）
					Tcmservices.Tcm08.CommonService8Locator service08 = new Tcmservices.Tcm08.CommonService8Locator();
					service08.setTCM_HIS_08EndpointAddress(apiAddress);
					Tcmservices.Tcm08.CommonService8SoapBindingStub th08 = (Tcmservices.Tcm08.CommonService8SoapBindingStub)service08.getTCM_HIS_08();
					th08.setTimeout(2000);
					String reqMessage = "<Request><Header><sender>EMR</sender><receiver>HIS</receiver><sendTime>"+strDate+"</sendTime>" +
							"<msgType>TCM_HIS_08</msgType><msgID>HIS"+strDate+"</msgID></Header><orgCode>"+tcmjgid+"</orgCode>" +
							"<serialNo>"+jzxh+"</serialNo></Request>";
					String res = th08.acceptMessage(reqMessage);
					//String res = "<Request><Header><sender>EMR</sender><receiver>HIS</receiver><sendTime>20180530162316</sendTime><msgID>EMR20180530162316</msgID><msgType>TCM_HIS_08</msgType></Header><Prescription><createTime>2018-05-28 23:23:26</createTime><serialNo>443002</serialNo><preName/><orgCode>320903102010001</orgCode><drugDataList><DrugData><drugQuantity>9</drugQuantity><drugCode>2798</drugCode><drugUnit/><drugName>威灵仙颗粒</drugName><note>切厚片，生用</note></DrugData></drugDataList></Prescription><Prescription><createTime>2018-05-2821:04:40</createTime><serialNo>2918</serialNo><preName>酸枣仁颗粒</preName><orgCode>320903102010001</orgCode><drugDataList><DrugData><drugQuantity>9</drugQuantity><drugCode>2809</drugCode><drugUnit/><drugName>猪苓颗粒</drugName><note/></DrugData></drugDataList></Prescription></Request>";
					listData = new ArrayList<Map<String, Object>>();
					Document doc = null;
					doc = DocumentHelper.parseText(res);
					if(res.contains("<Prescription><createTime>")){
						doc = DocumentHelper.parseText(res);
						Element bodyElt = doc.getRootElement();
						//Element bodyElt = (Element) doc.getRootElement().element("Request"); // 获取body节点
						Iterator iters = bodyElt.elementIterator("Prescription");
						while (iters.hasNext()) {
							Map<String, Object> map_prescription = new HashMap<String, Object>();
			                Element recordEle1 = (Element) iters.next();
			                Iterator iter = recordEle1.elementIterator("drugDataList");
							List<Map<String, Object>> listData_ypxx = new ArrayList<Map<String, Object>>();
							String ypxhs = "";
			                while (iter.hasNext()) {
				                Element recordEle2 = (Element) iter.next();
								Iterator iter2 = recordEle2.elementIterator("DrugData");
				                while (iter2.hasNext()) {
				                	Map<String, Object> data = new HashMap<String,Object>();
				                	data.put("JZXH", jzxh);
				                	Element recordEle = (Element) iter2.next();
				                    String drugCode = recordEle.elementTextTrim("drugCode");//药品编码
				                    //String note = recordEle.elementTextTrim("note");//备注
			                    	Map<String, Object> ypmap = getHisYpxx(drugCode);
			                    	if(ypmap.size()==0){
			                    		continue;
			                    	}
			                    	data.put("BZXX",0);
			                    	data.put("CFLX",3);
			                    	data.put("CFLX_TEXT","中药");
			                    	data.put("CFTS", 0);
			                    	data.put("FYGB",4);
			                    	data.put("GMYWLB",0);
			                    	data.put("GYTJ",ypmap.get("GYTJ").toString());
			                    	data.put("GYTJ_TEXT",ypmap.get("GYTJ_TEXT").toString());
			                    	data.put("JLDW",ypmap.get("JLDW").toString());
			                    	data.put("JYLX",1);
			                    	data.put("JYLX_TEXT","非基本药物");
			                    	data.put("KPDY",0);
			                    	data.put("KSBZ",0);
			                    	data.put("KSSDJ","");
			                    	data.put("MRCS",1);
			                    	data.put("MTSL",1);
			                    	data.put("PSPB",0);
			                    	data.put("SFJG",0);
			                    	data.put("TSYP",0);
			                    	data.put("TYPE",3);
			                    	data.put("YCJL",recordEle.elementTextTrim("drugQuantity"));//一次剂量
			                    	data.put("YCYL",0);
			                    	data.put("YFBZ",1);
			                    	data.put("YFDW",ypmap.get("YFDW").toString());
			                    	data.put("YFGG",ypmap.get("YFGG").toString());//药房规格
			                    	data.put("YPCD",Long.parseLong(ypmap.get("YPCD").toString()));//药品产地
			                    	data.put("YPCD_TEXT",ypmap.get("YPCD_TEXT").toString());
			                    	data.put("YPDJ", Double.parseDouble(ypmap.get("YPDJ").toString()));//药品单价
			                    	data.put("YPJL",ypmap.get("YPJL").toString());//药品剂量
			                    	data.put("YPMC",recordEle.elementTextTrim("drugName"));//药品名称
			                    	data.put("YPSL",recordEle.elementTextTrim("drugQuantity"));//药品数量
			                    	data.put("YPXH",Long.parseLong(drugCode));//药品序号
			                    	data.put("YPYF","qd");
			                    	data.put("YPYF_text","QD");
			                    	data.put("YPZH_SHOW","1");
			                    	data.put("YPZS",0);
			                    	data.put("YQSY",0);
			                    	data.put("YQSYFS","");
			                    	data.put("YYTS",1);
			                    	data.put("ZBY",0);
			                    	data.put("ZFBL",1);
			                    	data.put("ZFPB","");
			                    	data.put("ZFYP",0);
			                    	//data.put("uniqueId","");
			                    	listData_ypxx.add(data);	
			                    	ypxhs += "'"+drugCode+"',";
				                }
			                }
			                //校验HIS系统中是否已存在该处方	                    
	                    	if(ypxhs.equals("") || checkcfxx(jzxh,ypxhs.substring(0,ypxhs.length()-1))){
	                    		continue;
	                    	}
			                map_prescription.put("ypxxs", listData_ypxx);
			                map_prescription.put("kfrq", recordEle1.elementTextTrim("createTime"));
			                listData.add(map_prescription);
				        }
	                    req.put("tcmprescription",listData);
					}
				} catch (Exception e) {
					req.put("msg","failed");
					if(e.getMessage().contains("timed out")){
						logger.error("省中医馆平台接口调用超时:"+apiAddress);
						return req;
					}
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "从省中医馆平台获取处方信息失败.");
				}
				return req;
			}
			/**
			 * 根据jgid、ypxh获取HIS系统中草药信息
			 * 
			 * @param 
			 * @param 
			 * @return
			 * @throws 
			 */
			@SuppressWarnings("unchecked")
			private Map<String, Object> getHisYpxx(String ypxh){
				UserRoleToken user = UserRoleToken.getCurrent();
				String jgid = user.getManageUnitId();
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer hql = new StringBuffer();
				Map<String,Object> data = new HashMap<String,Object>();
				try {
					hql.append("SELECT C.GYFF as GYTJ,E.XMMC as GYTJ_TEXT,C.JLDW as JLDW,B.YFGG as YFGG,B.YFDW as YFDW,A.YPCD as YPCD," +
							"D.CDMC as YPCD_TEXT,A.LSJG as YPDJ,C.YPJL as YPJL,A.YPXH as YPXH " +
							"FROM YF_KCMX A,YF_YPXX B,YK_TYPK C,YK_CDDZ D,ZY_YPYF E " +
							"WHERE A.JGID=B.JGID AND A.YPXH=B.YPXH AND A.YPXH=C.YPXH " +
							"AND A.YPCD=D.YPCD AND C.GYFF=E.YPYF AND A.JGID=:JGID AND A.YPXH=:YPXH");
					parameters.put("JGID", jgid.substring(0,9));
					parameters.put("YPXH", ypxh);
					List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(),
							parameters);
					if (listData != null) {
						for (Map<String, Object> list : listData) {
							data.put("GYTJ", list.get("GYTJ").toString());
							data.put("GYTJ_TEXT", list.get("GYTJ_TEXT").toString());
							data.put("JLDW", list.get("JLDW").toString());
							data.put("YFGG", list.get("YFGG").toString());
							data.put("YFDW", list.get("YFDW").toString());
							data.put("YPCD", list.get("YPCD").toString());
							data.put("YPCD_TEXT", list.get("YPCD_TEXT").toString());
							data.put("YPDJ", list.get("YPDJ").toString());
							data.put("YPJL", list.get("YPJL").toString());
							break;
						}
					}
					else{
						data.put("GYTJ", "");
						data.put("GYTJ_TEXT", "");
						data.put("JLDW", "");
						data.put("YFGG", "");
						data.put("YFDW", "");
						data.put("YPCD", "");
						data.put("YPCD_TEXT", "");
						data.put("YPDJ", "");
						data.put("YPJL", "");
					}
				} catch (Exception e) {
					logger.error("根据jgid、ypxh获取HIS系统中草药信息.", e);
				}
				return data;
			}
			/**
			 * 校验HIS系统中是否已存在该处方：同一处方中存在相同种类的草药信息即视为存在
			 * 
			 * @param 
			 * @param 
			 * @return
			 * @throws 
			 */
			@SuppressWarnings("unchecked")
			private Boolean checkcfxx(String jzxh,String ypxhs){
				try {
					Integer ypcount = ypxhs.split(",").length;
					String hqlcount = " SELECT COUNT(1) AS TOTALCOUNT FROM ( " +
							"SELECT A.CFSB,COUNT(1) AS YPCOUNT FROM MS_CF02 A,MS_CF01 B  " +
							"WHERE A.CFSB=B.CFSB AND B.ZFPB=0 AND B.JZXH=:JZXH AND A.YPXH IN("+ypxhs+") GROUP BY A.CFSB " +
							") C WHERE C.YPCOUNT=:YPCOUNT";
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", jzxh);
					parameters.put("YPCOUNT", ypcount);
					long count = Long.parseLong(dao.doSqlLoad(hqlcount, parameters).get("TOTALCOUNT")+"");
					if(count>0){
						return true;
					}
				} catch (Exception e) {
					logger.error("校验HIS系统中是否已存在该处方.",	e);
					return  true;
				}
				return false;
			}
			/**
			 * 推送费用信息到省中医馆平台
			 * 
			 * @param body
			 * @param ctx
			 * @return
			 * @throws ModelDataOperationException
			 */
			@SuppressWarnings("unchecked")
			public Map<String, Object> doUploadFyxxToTcm(Map<String, Object> body,
					Context ctx) throws ModelDataOperationException {
				Map<String, Object> req = new HashMap<String, Object>();
				Map<String, Object> parameters = new HashMap<String, Object>();
				String jzxh = body.get("jzxh") + "";
				String fphm = body.get("fphm") + "";
				UserRoleToken user = UserRoleToken.getCurrent();
				String jgid = user.getManageUnitId();
				String apiAddress = ParameterUtil.getParameter(jgid.substring(0,4),"TCM_API_01", ctx);
				try {
					//根据就诊序号获取病人信息
					StringBuffer hql = new StringBuffer();
					hql.append("SELECT B.BRID AS BRID,B.BRXM AS BRXM,B.BRXB AS BRXBDM,DECODE(B.BRXB,0,'未知的性别',1,'男',2,'女','未说明的性别') AS BRXB," +
							"TO_CHAR(B.CSNY,'YYYYMMDD') AS CSNY,B.SFZH AS SFZH,A.JZXH AS JZXH,C.ORGANIZCODE_TCM AS ORGANIZCODE_TCM," +
							"A.TCM_BRXX  AS TCM_BRXX,B.BRXZ AS BRXZDM,DECODE(B.BRXZ,2000,'医保',3000,'医保',6000,'新农合','自费') AS BRXZ,B.LXDH AS LXDH  " +
							"FROM YS_MZ_JZLS A,MS_BRDA B,SYS_ORGANIZATION C WHERE A.BRBH=B.BRID AND A.JGID=C.ORGANIZCODE " +
							" AND A.JZXH="+jzxh);	
					List<Map<String, Object>> listData = dao.doSqlQuery(hql.toString(), parameters);
					if(listData == null){
						req.put("msg","failed");
						return req;
					}
					String reqMessage = "";
					for (Map<String, Object> brxx : listData) {
						if (brxx.get("TCM_BRXX")!=null && brxx.get("TCM_BRXX").toString().equals("1")) {
							req.put("msg","success");
							return req;
						}
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式		
						Date now = new Date(); 
						String strDate = dateFormat.format(now); 
						reqMessage = "<Request><Header><sender>HIS</sender><receiver>EMR,HEAL</receiver><sendTime>"+strDate+"</sendTime>" +
								"<msgType>TCM_HIS_01</msgType><msgID>HIS"+strDate+"</msgID></Header><Patient><id>"+brxx.get("BRID")+"</id>" +
								"<name>"+brxx.get("BRXM")+"</name><genderCode>"+brxx.get("BRXBDM")+"</genderCode><gender>"+brxx.get("BRXB")+"</gender>" +
								"<birthday>"+brxx.get("CSNY")+"</birthday><cardTypeCode>01</cardTypeCode><cardType>居民身份证</cardType>" +
								"<cardNo>"+brxx.get("SFZH")+"</cardNo><occupationCode></occupationCode><occupation></occupation><marriedCode></marriedCode><married></married><countryCode></countryCode><country></country><nationalityCode></nationalityCode><nationality></nationality><provinceCode></provinceCode><cityCode></cityCode><areaCode>320000</areaCode><province></province><city></city><area></area><streetInfo></streetInfo><postcode></postcode><ctName></ctName><ctRoleId></ctRoleId><ctAddr></ctAddr><ctTelephone></ctTelephone>" +
								"<patiTypeCode>"+brxx.get("BRXZDM")+"</patiTypeCode><patiType>"+brxx.get("BRXZ")+"</patiType>" +
								"<telephone>"+brxx.get("LXDH")+"</telephone><outpatientNo>"+jzxh+"</outpatientNo>" +
								"<orgCode>"+brxx.get("ORGANIZCODE_TCM")+"</orgCode></Patient></Request>";
						break;
					}		
					Tcmservices.Tcm01._Proxy7ServiceLocator service = new Tcmservices.Tcm01._Proxy7ServiceLocator();
					// 获取系统参数中医馆平台接口地址（推送患者信息）
					service.setTCM_HIS_01EndpointAddress(apiAddress);
					Tcmservices.Tcm01.TCM_HIS_01SoapBindingStub th01 = (Tcmservices.Tcm01.TCM_HIS_01SoapBindingStub)service.getTCM_HIS_01();
					th01.setTimeout(2000);
					String res = th01.acceptMessage(reqMessage);
					if(res.contains("has been successfully received")){
						//更新ys_mz_jzls表
						Session ss = (Session) ctx.get(Context.DB_SESSION);
						ss.beginTransaction();
						parameters.put("JZXH",Long.parseLong(jzxh));
						StringBuffer removeHql = new StringBuffer();
						removeHql
								.append("update "
										+ "YS_MZ_JZLS"
										+ " set TCM_BRXX=1 where JZXH=:JZXH");
						try {
							dao.doUpdate(removeHql.toString(), parameters);
						} catch (PersistentDataOperationException e) {
							e.printStackTrace();
							ss.getTransaction().rollback();
							throw new ModelDataOperationException("更新ys_mz_jzls表字段TCM_BRXX失败", e);
						}
						ss.getTransaction().commit();
						req.put("msg","success");
					}else
					{
						req.put("msg","failed");
					}
				} catch (Exception e) {
					req.put("msg","failed");
					if(e.getMessage().contains("timed out")){
						logger.error("省中医馆平台接口调用超时:"+apiAddress);
						return req;
					}
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "推送费用信息到省中医馆平台失败.");
				}
				return req;
			}
}
