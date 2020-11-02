/**
 * @(#)EmpiService2.java Created on 2012-7-2 下午03:53:53
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.empi;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class EmpiService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(EmpiService.class);

	private EmpiInterfaceImpi empiInterfaceImpi = null;
	private VisitPlanCreator visitPlanCreator = null;

	/**
	 * 是否是二级平台。
	 */
	private boolean secondary = false;
	private String mpiClass = "chis.script.mpi.EMPIInfoModule2";
	private String cityServerAddr = "http://127.0.0.1/HZEHR";

	/**
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 执行查询是否可以调用诊疗数据 add by zhangwei 2015-07-07
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckZlls(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		ResultSet rs = null;
		ResultSet rs1 = null;
		Statement stmt = null;
		Connection conn = null;
		String haveZlls = "no";
		try {
			Map<String, Object> body = (Map<String, Object>) jsonReq
					.get("body");
			String idcard = (String) body.get("idcard");
			if (idcard.trim().length() == 0) {
				String msg = "身份证不能为空格。";
				throw new ServiceException(msg);
			}
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@32.33.1.93:1521:orcl", "ehr", "bsoft");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct a.mpiid from mpi.mpi_card t,mpi.mpi_certificate a where t.mpiid = a.mpiid and  a.certificateno ='"+ idcard + "'");
			
			while (rs.next()) {
				int cnt = 0;
				String mpiid = rs.getString("mpiid");
				// module-config
				/*
				<module id="ClinicRecord" title="门诊" entryName="Opt_Record" script="ehrview.widget.medical.ClinicRecord"/>
				<module id="TreatmentRecord" title="住院" entryName="Ipt_Record" script="ehrview.widget.medical.TreatmentRecord"/>		
				<module id="MedicalRecord" title="用药" entryName="SUMMARY_Hist_Drug" script="ehrview.widget.medical.MedicalRecord"/>
				<module id="LabRecord" title="检验" entryName="Pt_LabReport" script="ehrview.widget.medical.ExamRecord"/>
				<module id="ExaminationRecord" title="检查" entryName="Pt_ExamReport" script="ehrview.widget.medical.CheckReport"/>
				<module id="OperationRecord" title="手术" entryName="Pt_Operation" script="ehrview.widget.medical.OperationRecord"/>
				<module id="TransfusionRecord" title="输血" entryName="Pt_Transfusion" script="ehrview.widget.medical.TransfusionRecord"/>
				*/
				rs1 = stmt.executeQuery(" select count(1) cnt from ( select mpiid from Opt_Record t where t.mpiid ='"+mpiid+"' "
												+" 	union all select mpiid from Ipt_Record t where t.mpiid = '"+mpiid+"' "
												+"		union all select mpiid from SUMMARY_Hist_Drug t where t.mpiid = '"+mpiid+"' "
												+"		union all select mpiid from Pt_LabReport t where t.mpiid = '"+mpiid+"' "
												+"		union all select mpiid from Pt_ExamReport t where t.mpiid = '"+mpiid+"' "
												+"		union all select mpiid from Pt_Operation t where t.mpiid = '"+mpiid+"' "
												+"		union all select mpiid from Pt_Transfusion t where t.mpiid = '"+mpiid+"') ");
				while (rs1.next()) {
					cnt = rs1.getInt("cnt");
				}
				if(cnt>0){
					haveZlls="yes";
				}
			}
			jsonRes.put("body", haveZlls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ResultSet rs = null;
		ResultSet rs1 = null;
		Statement stmt = null;
		Connection conn = null;
		String haveZlls = "no";
		try {
			String idcard = "320111090650144201";
			if (idcard.trim().length() == 0) {
				String msg = "身份证不能为空格。";
			}
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@32.33.1.61:1521:orcl", "mpi", "bsoft");
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("select mpiid  from mpi_card t where t.cardno = '"
							+ idcard + "'");
			while (rs.next()) {
				int a = 0;
				String mpiid = rs.getString("mpiid");
				rs1 = stmt
						.executeQuery("select count(1) cnt from ehr.opt_record t where t.mpiid = '"
								+ mpiid + "'");
				while (rs1.next()) {
					if (rs1.getInt("cnt") > 0) {
						a++;
					}
				}
				rs1 = stmt
						.executeQuery("select count(1) cnt from ehr.ipt_record t where t.mpiid = '"
								+ mpiid + "'");
				while (rs1.next()) {
					if (rs1.getInt("cnt") > 0) {
						a++;
					}
				}
				if(a>0){
					haveZlls="yes";
				}
			}
			System.out.println(haveZlls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 执行注册个人信息的请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doSubmitPerson(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String personName = (String) body.get("personName");
		if (personName.trim().length() == 0) {
			String msg = "姓名不能为空格。";
			throw new ServiceException(msg);
		}
		// EmpiModel empiModel = new EmpiModel(dao);
		Map<String, Object> records = new HashMap<String, Object>();
		records.putAll(body);
		// empiModel.putLastModifyUserAndUnit(records, dao);
		records = EmpiUtil.changeToPIXFormat(records);
		Map<String, Object> result = EmpiUtil.submitPerson(dao, ctx, body,
				records);
		jsonRes.put("body", result);
	}

	/**
	 * 执行更新个人信息的请求。
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	protected void doUpdatePerson(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		String empiId = (String) reqBody.get("empiId");
		String idCard = (String) reqBody.get("idCard");
		// 检查证件是否被占用。
		if (true == EmpiUtil.checkIdCardUsed(idCard, empiId, dao)) {
			throw new ServiceException("该身份证件号码已被使用,请修改!");
		}

		// EmpiModel empiModel = new EmpiModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		body = EmpiUtil.changeToPIXFormat(reqBody);
		// empiModel.putLastModifyUserAndUnit(body, dao);//新建档不需要最后修改机构,否则mpi会报错
		Map<String, Object> result = EmpiUtil.updatePerson(dao, ctx, reqBody,
				body, jsonRes);
		jsonRes.put("body", result);
	}

	/**
	 * 执行个人信息高级查询请求。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doAdvancedSearch(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> map = new HashMap<String, Object>();
		if (reqBody.get("cardNo") != null) {
			map = EmpiUtil.queryByCardNo(dao, ctx, reqBody);
		} else if (reqBody.get("idCard") != null) {
			map = EmpiUtil.queryByIdCardAndName(dao, ctx, reqBody);
		} else if (reqBody.get("personName") != null
				&& reqBody.get("birthday") != null
				&& reqBody.get("sexCode") != null) {
			map = EmpiUtil.queryByPersonInfo(dao, ctx, reqBody);
		}
		int pageSize = (Integer) jsonReq.get("pageSize") == null ? 50
				: ((Integer) jsonReq.get("pageSize")).intValue();
		int pageNo = (Integer) jsonReq.get("pageNo") == null ? 1
				: ((Integer) jsonReq.get("pageNo")).intValue();
		jsonRes.put("pageSize", pageSize);
		jsonRes.put("pageNo", pageNo);
		if (map.get("dataSource") != null
				&& "pix".equals(map.get("dataSource"))) {
			List<Map<String, Object>> data = (List<Map<String, Object>>) map
					.get("body");
			data = EmpiUtil.changeToBSInfo(data);
			data = SchemaUtil.setDictionaryMessageForList(data,
					MPI_DemographicInfo);
			jsonRes.put("dataSource", "pix");
			jsonRes.put("body", data);
		} else {
			jsonRes.putAll(map);
		}
	}

	/**
	 * 判断卡是否已经存在
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doIfCardExists(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> res = new HashMap<String, Object>();
		boolean ifExists = false;
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			if (reqBody.get("cards") != null) {
				List<Map<String, Object>> cards = (List<Map<String, Object>>) reqBody
						.get("cards");
				String cardTypeCode = (String) cards.get(0).get("cardTypeCode");
				String cardNo = (String) cards.get(0).get("cardNo");
				ifExists = empiModel.checkCardExist(cardTypeCode, cardNo);

			} else if (reqBody.get("certificates") != null) {
				List<Map<String, Object>> certificates = (List<Map<String, Object>>) reqBody
						.get("certificates");
				String certificateTypeCode = (String) certificates.get(0).get(
						"certificateTypeCode");
				String certificateNo = (String) certificates.get(0).get(
						"certificateNo");
				ifExists = empiModel.checkCertificateExist(certificateTypeCode,
						certificateNo);
			}
		} catch (ModelDataOperationException e) {
			logger.error("If Card Exists is fail.");
			throw new ServiceException(e);
		}
		res.put("ifExists", ifExists);
		jsonRes.put("body", res);
	}

	/**
	 * 获取直系亲属列表。
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param ctx
	 */
	protected void doGetParentService(HashMap<String, Object> req,
			HashMap<String, Object> res, BaseDAO dao, Context ctx) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null) {

		}
		String idCard = (String) body.get("idCard");
		if (idCard == null || idCard.trim().length() == 0) {

		}
		EmpiModel empiModule = new EmpiModel(dao);
		List<Map<String, Object>> parents = null;
		try {
			parents = empiModule.getRelative(idCard);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, e.getMessage());
		}
		res.put("body", parents);
	}

	/**
	 * 检验同类型的卡号是否已经存在
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckCardExist(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String cardTypeCode = (String) body.get("cardTypeCode");
		String cardNo = (String) body.get("cardNo");
		EmpiModel em = new EmpiModel(dao);
		try {
			boolean result = em.checkCardExist(cardTypeCode, cardNo);
			res.put("result", result);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取url的根地址。
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public String getCityServerRootAddr() {
		StringBuffer sb = cityServerAddr.startsWith("http://") ? new StringBuffer(
				cityServerAddr) : new StringBuffer("http://")
				.append(cityServerAddr);
		return sb.append(cityServerAddr.endsWith("/") ? "" : "/")
				.append("interface.jshtml?").toString();
	}

	@SuppressWarnings("unchecked")
	protected void doSavePerson(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body1 = (HashMap<String, Object>) jsonReq
				.get("body");
		if (StringUtils.trimToEmpty((String) jsonReq.get("op"))
				.equals("create")) {
			body1.put("createUser", UserRoleToken.getCurrent().getUserId());
			body1.put("createUnit", UserRoleToken.getCurrent()
					.getManageUnitId());
			this.doSubmitPerson(jsonReq, jsonRes, dao, ctx);
			HashMap<String, Object> req = new HashMap<String, Object>();
			HashMap<String, Object> res = new HashMap<String, Object>();
			HashMap<String, Object> body = new HashMap<String, Object>();
			req.put("op", "create");
			req.put("body", body);
			req.put("schema", PUB_Log);
			body.put("operType", "3");
			body.put("recordType", "0");
			body.put("empiId",
					StringUtils.trimToEmpty((String) body1.get("empiId")));
			body.put("personName",
					StringUtils.trimToEmpty((String) body1.get("personName")));
			body.put("idCard",
					StringUtils.trimToEmpty((String) body.get("idCard")));
			this.execute(req, res, ctx);
		} else {
			body1.put("addressChange", false);
			body1.put("certificateChange", false);
			body1.put("telephoneChange", false);
			body1.put("cardChange", false);
			body1.put("extensionChange", false);
			this.doUpdatePerson(jsonReq, jsonRes, dao, ctx);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doQueryByEmpiId(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> map = new HashMap<String, Object>();
		if (reqBody.get("empiId") == null) {
			throw new ServiceException("empiId不能为空");
		}
		map = EmpiUtil.queryByEmpiId(dao, ctx, reqBody);
		int pageSize = (Integer) jsonReq.get("pageSize") == null ? 50
				: ((Integer) jsonReq.get("pageSize")).intValue();
		int pageNo = (Integer) jsonReq.get("pageNo") == null ? 1
				: ((Integer) jsonReq.get("pageNo")).intValue();
		jsonRes.put("pageSize", pageSize);
		jsonRes.put("pageNo", pageNo);
		if (map != null) {
			if (map.get("dataSource") != null
					&& "pix".equals(map.get("dataSource"))) {
				List<Map<String, Object>> data = (List<Map<String, Object>>) map
						.get("body");
				data = EmpiUtil.changeToBSInfo(data);
				data = SchemaUtil.setDictionaryMessageForList(data,
						MPI_DemographicInfo);
				jsonRes.put("dataSource", "pix");
				jsonRes.put("body", data);
			} else {
				jsonRes.putAll(map);
			}
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void doEmpiIdExists(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		String empiId = (String) reqBody.get("empiId");
		EmpiModel em = new EmpiModel(dao);
		try {
			Map<String, Object> info = em.getEmpiInfoByEmpiid(empiId);
			if (info != null) {
				reqBody.put("exists", true);
			} else {
				reqBody.put("exists", false);
			}

			jsonRes.put("body", reqBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 获得cityServerAddr。
	 * 
	 * @return the cityServerAddr
	 */
	public String getCityServerAddr() {
		return cityServerAddr;
	}

	/**
	 * 设置cityServerAddr。
	 * 
	 * @param cityServerAddr
	 *            the cityServerAddr to set
	 */
	public void setCityServerAddr(String cityServerAddr) {
		this.cityServerAddr = cityServerAddr;
	}

	/**
	 * 获得mpiClass。
	 * 
	 * @return the mpiClass
	 */
	public String getMpiClass() {
		return mpiClass;
	}

	/**
	 * 设置mpiClass。
	 * 
	 * @param mpiClass
	 *            the mpiClass to set
	 */
	public void setMpiClass(String mpiClass) {
		this.mpiClass = mpiClass;
	}

	/**
	 * 获得secondary。
	 * 
	 * @return the secondary
	 */
	public boolean isSecondary() {
		return secondary;
	}

	/**
	 * 设置secondary。
	 * 
	 * @param secondary
	 *            the secondary to set
	 */
	public void setSecondary(boolean secondary) {
		this.secondary = secondary;
	}

	public EmpiInterfaceImpi getEmpiInterfaceImpi() {
		return empiInterfaceImpi;
	}

	public void setEmpiInterfaceImpi(EmpiInterfaceImpi empiInterfaceImpi) {
		this.empiInterfaceImpi = empiInterfaceImpi;
	}

	/**
	 * @see com.bsoft.hzehr.biz.AbstractService#getTransactedActions()
	 */
	@Override
	public List<String> getTransactedActions() {
		List<String> list = new ArrayList<String>();
		list.add("updatePerson");
		list.add("submitPerson");
		list.add("writeOffPerson");
		return list;
	}

	/**
	 * @see com.bsoft.hzehr.biz.AbstractService#getNoDBActions()
	 */
	public List<String> getNoDBActions() {
		List<String> list = new ArrayList<String>();
		list.add("getEmpiId");
		list.add("registerCard");
		list.add("lockCard");
		list.add("unlockCard");
		list.add("writeOffCard");
		return list;
	}

	/**
	 * 
	 * @Description:加载个人基本信息和卡信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-26 上午11:03:26
	 * @Modify:
	 */
	public void doGetDemographicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("pkey");
		EmpiModel eModel = new EmpiModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = eModel.getDemographicInfo(empiId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		if (rsMap != null) {
			resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					MPI_DemographicInfo);
			try {
				resBodyMap.put("cards", eModel.getCardsList(empiId));
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}
		res.put("body", resBodyMap);
	}

	@SuppressWarnings("unchecked")
	public void doChangeCardStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			empiModel.doChangeCardStatus(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
