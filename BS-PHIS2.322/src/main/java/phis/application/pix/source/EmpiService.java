/**
 * @(#)EmpiService2.java Created on 2012-7-2 下午03:53:53
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pix.source;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pub.source.PublicModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class EmpiService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(EmpiService.class);

	private EmpiInterfaceImpi empiInterfaceImpi = null;
	/**
	 * 是否是二级平台。
	 */
	private boolean secondary = false;
	private String mpiClass = "app.biz.empi.EMPIInfoModule2";
	private String cityServerAddr = "http://127.0.0.1/HZEHR";

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
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			long num = empiModel.getIfMZHM((String) body.get("MZHM"));
			if (num > 0) {
				String msg = "该门诊号码已使用!";
				logger.error(msg);
				jsonRes.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
				jsonRes.put(RES_MESSAGE, msg);
				return;
			}
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		Map<String, Object> records = new HashMap<String, Object>();
		records.putAll(body);
		records = EmpiUtil.changeToPIXFormat(records);
		records.put("photo","");
		//empiModel.putLastModifyUserAndUnit(records, dao);
		Map<String, Object> result = EmpiUtil.submitPerson(dao, ctx, body,records);
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
		EmpiModel empiModel = new EmpiModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		reqBody.put("lastModifyTime", new Date());
		Map<String, Object> body = new HashMap<String, Object>();
		body = EmpiUtil.changeToPIXFormat(reqBody);
		//empiModel.putLastModifyUserAndUnit(body, dao);
		if(body.containsKey("lastModifyUnit")){
			body.remove("lastModifyUnit");
		}
		if(body.containsKey("lastModifyTime")){
			body.remove("lastModifyTime");
		}
		if(body.containsKey("lastModifyUser")){
			body.remove("lastModifyUser");
		}
		body.put("photo","");
		Map<String, Object> result = EmpiUtil.updatePerson(dao, ctx, reqBody,
				body);
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
		if (reqBody.get("MZHM") != null) {
			map = EmpiUtil.queryByMZHM(dao, ctx, reqBody);
		}
		if (reqBody.get("cardNo") != null) {
			map = EmpiUtil.queryByCardNo(dao, ctx, reqBody);
		}
		if (reqBody.get("idCard") != null) {
			map = EmpiUtil.queryByIdCardAndName(dao, ctx, reqBody);
		}
		if (reqBody.get("personName") != null
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
			Map<String, Object> parameters = new HashMap<String, Object>();
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).containsKey("empiId")) {
					parameters.put("empiId", data.get(i).get("empiId"));
				} else {
					parameters.put("empiId", data.get(i).get("mpiId"));
				}
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd");
				data.get(i).put("birthday", sdf.format((Date)data.get(i).get("birthday")));
				try {
					List<Map<String, Object>> list = dao
							.doQuery(
									"select a.MZHM as MZHM,a.BRXZ as BRXZ from MS_BRDA a where a.EMPIID = :empiId",
									parameters);
					if (list.size() > 0) {
						data.get(i).putAll(list.get(0));
					}
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			data = SchemaUtil.setDictionaryMassageForList(data,
					MPI_DemographicInfo);
			jsonRes.put("dataSource", "pix");
			jsonRes.put("body", data);
		} else {
			try {
				List<Map<String, Object>> body = (List<Map<String, Object>>) map
						.get("body");
				if (body != null) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					for (int i = 0; i < body.size(); i++) {
						parameters.put("empiId", body.get(i).get("empiId"));
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.MZHM as MZHM,a.BRXZ as BRXZ from MS_BRDA a where a.EMPIID = :empiId",
										parameters);
						if (list.size() > 0) {
							body.get(i).putAll(list.get(0));
						}
						PublicModel pm = new PublicModel();
						pm.doPersonAge((Date) body.get(i).get("birthday"),
								body.get(i));
						body.get(i).put("age", ((Map<String, Object>)body.get(i).get("body")).get("ages"));
						body.get(i).remove("body");
						body.set(
								i,
								SchemaUtil.setDictionaryMassageForList(
										body.get(i), MPI_DemographicInfo));
						
					}
					map.put("body", body);
				}
				jsonRes.putAll(map);
				// System.out.println(map);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// 人员信息回填
	@SuppressWarnings("unused")
	private void doPersonLoad(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String pkey = (String) req.get("pkey");
		EmpiModel em = new EmpiModel(dao);
		try {
			Map<String, Object> rsMap = em.doPersonLoad(pkey, ctx);
			Map<String, Object> resBodyMap = SchemaUtil.setDictionaryMassageForForm(rsMap, BSPHISEntryNames.MPI_DemographicInfo);
			res.put("body", resBodyMap);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
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
			body1.put("createUser", ((User) ctx.get("user.instance")).getId());
			// jsonReq.optHashMap<String,Object>("body").put("createTime", new
			// Date());
			body1.put("createUnit",
					((User) ctx.get("user.instance")).getProperty("manaUnitId"));
			this.doSubmitPerson(jsonReq, jsonRes, dao, ctx);
			HashMap<String, Object> req = new HashMap<String, Object>();
			HashMap<String, Object> res = new HashMap<String, Object>();
			HashMap<String, Object> body = new HashMap<String, Object>();
			req.put("op", "create");
			req.put("body", body);
			req.put("schema", "PUB_Log");
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
	 * 门诊号获取
	 */
	public void doOutPatientNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmpiModel em = new EmpiModel(dao);
		try {
			String re = em.doOutPatientNumber(ctx);
			res.put("MZHM", re);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-22
	 * @description 新增时查询系统参数里面的病人性质
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryNature(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			Map<String, Object> ret = empiModel.queryNature(ctx);
			res.put("brxz", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	// 并发测试中心MPI服务
	public void doTestMpi(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		for (int i = 0; i < 10000; i++) {
			EmpiUtil.queryWithPIX("76add54651554ead99c2c5b22600f9ca", dao, ctx);
		}
	}

	/**
	 * 获取GY_XTCS表系统参数
	 */
	@SuppressWarnings("static-access")
	public void doSystemParam(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {

		String csmcvalue = (String) req.get("csmc");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();

		ParameterUtil util = new ParameterUtil();
		if (csmcvalue.equals("") || manageUnit == null) {
			res.put(RES_CODE, "");
			res.put(RES_MESSAGE, "");
			throw new ServiceException("cannot get user ctx or params csmc!");
		}
		String csz = util.getParameter(manageUnit, csmcvalue, ctx);
		res.put("CSZ", csz);
	}
}
