package phis.application.twr.source;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import com.bsoft.dr.server.DRServiceException;
import com.bsoft.dr.server.rpc.IDRProvider;
import com.mongodb.util.Hash;

import ctd.account.UserRoleToken;
import ctd.net.rpc.Client;
import ctd.spring.AppDomainContext;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ReferralModel {
	protected BaseDAO dao;
	private final String SUCCESS = "success";
	protected Logger logger = LoggerFactory.getLogger(ReferralModel.class);

	public ReferralModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @description 查询预约信息
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getServationInformation(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		IDRProvider provider = getIDRProvider(ctx);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hospitalCode", "02");
		if (body.get("departmentCode") != null
				&& !"".equals(body.get("departmentCode"))) {
			params.put("departmentCode", body.get("departmentCode"));
		}
		if (body.get("hospitalCode") != null
				&& !"".equals(body.get("hospitalCode"))) {
			params.put("hospitalCode", body.get("hospitalCode"));
		}
		if (body.get("itemCode") != null && !"".equals(body.get("itemCode"))) {
			params.put("itemCode", body.get("itemCode"));
		}
		// params.put("workDate", "2013-03-29");
		params.put("planType", body.get("plan") + "");
		Map<String, Object> data;
		try {
			data = provider.getPlan(params);
			int code = (Integer) data.get("code");
			String message = data.get("msg") == null ? "转诊信息查询失败"
					: (String) data.get("msg");
			if (code != 200) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, message);
			}
			return (List<Map<String, Object>>) data.get("plans");
		} catch (DRServiceException e) {
			logger.error("药房药品信息导入失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转诊信息查询失败");
		}

	}

	/**
	 * @description 获取医院信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getClinicHospital(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {

		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		detailData.put("RIQI", temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// detailData = null;
		Map<String, Object> providerData = null;
		String message = "获取医院信息错误";
		try {
			// System.out.println(params.toString());
			// providerData = provider.getHospitalInfo(params);
			System.out.println("==>获取医院列表 request" + params);
			providerData = (Map<String, Object>) Client
					.rpcInvoke("dr.drProvider", "getHospitalInfo",
							new Object[] { params });

			System.out.println("==>获取医院列表 return" + providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}

			return (List<Map<String, Object>>) providerData.get("data");
		} catch (DRServiceException e) {
			logger.error("获取医院信息列表", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取医院信息列表", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			logger.error("获取医院信息列表", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}

	}

	/**
	 * @description 获取门诊挂号科室信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getClinicDept(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {

		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		detailData.put("RIQI", null);

		temp = body.get("registerType");
		detailData.put("GUAHAOFS", temp == null ? "0" : temp);

		// 挂号班次
		temp = body.get("registerSchedule");
		detailData.put("GUAHAOBC", temp == null ? "0" : temp);

		// 挂号类别
		temp = body.get("registerCategory");
		detailData.put("GUAHAOLB", temp == null ? "0" : temp);

		// 医院编码
		temp = body.get("hospitalCode");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

		// 医院编码
		// temp = body.get("hospital");
		// params.put("YYMC", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// detailData = null;
		Map<String, Object> providerData;
		String message = "获取门诊挂号科室信息失败";
		try {

			System.out.println("==registerDepartment request==>" + params);
			// providerData = provider.registerDepartment(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "registerDepartment",
					new Object[] { params });
			System.out.println("==>registerDepartment return ==>"
					+ providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			List<Map<String, Object>> tempList = (List<Map<String, Object>>) ((Map<String, Object>) providerData
					.get("data")).get("KESHIMX");
			ArrayList<Map<String, Object>> KESHIMXList = new ArrayList<Map<String, Object>>();
			boolean isExist = false;
			for (Map<String, Object> tm : tempList) {
				isExist = false;
				for (Map<String, Object> m : KESHIMXList) {
					if (parseLong(tm.get("KESHIDM")) == parseLong(m
							.get("KESHIDM"))) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					// System.out.println("-------------------------");
					KESHIMXList.add(tm);
				}
			}
			// System.out.println(KESHIMXList.toString());

			return KESHIMXList;

		} catch (DRServiceException e) {
			logger.error("获取门诊挂号科室信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取门诊挂号科室信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			logger.error("获取门诊挂号科室信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}
	}

	/**
	 * @description 获取住院科室信息
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getHospitalDepartments(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {

		Map<String, Object> detailData = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		try {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			detailData.put("RIQI", df.format(df.parse((String) temp)));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		temp = body.get("registerType");
		detailData.put("GUAHAOFS", temp == null ? "0" : temp);

		// 挂号班次
		temp = body.get("registerSchedule");
		detailData.put("GUAHAOBC", temp == null ? "0" : temp);

		// 挂号类别
		temp = body.get("registerCategory");
		detailData.put("GUAHAOLB", temp == null ? "0" : temp);

		// 医院编码
		temp = body.get("hospitalCode");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

		// 医院编码
		// temp = body.get("hospital");
		// detailData.put("YYMC", temp == null ? "" : temp);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// params.putAll(detailData);
		detailData = null;
		Map<String, Object> providerData;
		String message = "获取挂号科室信息失败";
		try {

			System.out.println("==getHospitalDepartments request==>" + params);
			// providerData = provider.registerDepartment(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "getHospitalDepartments",
					new Object[] { params });
			System.out.println("==>getHospitalDepartments return ==>"
					+ providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}

			return (List<Map<String, Object>>) ((Map<String, Object>) providerData
					.get("data")).get("KESHIMX");

		} catch (DRServiceException e) {
			logger.error("获取住院科室信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取住院科室信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			logger.error("获取住院科室信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}
	}

	/**
	 * @description 获取门诊挂号医生
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getRegisterDoctor(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {

		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		// detailData.put("RIQI", temp);

		temp = body.get("registerType");
		detailData.put("GUAHAOFS", temp == null ? "0" : temp);

		// 挂号班次
		temp = body.get("registerSchedule");
		detailData.put("GUAHAOBC", temp == null ? "0" : temp);

		// 挂号类别
		temp = body.get("registerCategory");
		detailData.put("GUAHAOLB", temp == null ? "0" : temp);

		// 医院编码
		temp = body.get("hospitalCode");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

		// 科室编码
		temp = body.get("departmentCode");
		detailData.put("KESHIDM", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// detailData = null;
		Map<String, Object> providerData;
		String message = "获取门诊医生信息失败";
		try {

			System.out.println("==>getRegisterDoctor request==>" + params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "registerDoctor", new Object[] { params });
			System.out
					.println("==>getRegisterDoctor return ==>" + providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			List<Map<String, Object>> tempList = (List<Map<String, Object>>) ((Map<String, Object>) providerData
					.get("data")).get("YISHENMX");
			List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> tempMap : tempList) {
				boolean isExist = false;
				for (Map<String, Object> returnMap : returnList) {
					if ((tempMap.get("YISHENGDM") + "").equals(returnMap
							.get("YISHENGDM") + "")) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					returnList.add(tempMap);
				}
			}

			return returnList;
			// return (List<Map<String, Object>>) ((Map<String, Object>)
			// providerData
			// .get("data")).get("YISHENMX");

		} catch (DRServiceException e) {
			logger.error("获取门诊医生信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取门诊医生信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}
	}

	/**
	 * @description 获取门诊挂号医生排班（获取挂号号码段）
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> registerSourceHMD(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {

		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		Object tempso = body.get("referralDate");
		String[] temps = tempso.toString().split(",");
		String message = "获取门诊挂号号源信息失败";
		List<Map<String, Object>> allList = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < temps.length; i++) {
				Object temp = temps[i];

				detailData.put("RIQI", temp);

				temp = body.get("registerType");
				detailData.put("GUAHAOFS", temp == null ? "0" : temp);

				// 挂号班次
				temp = body.get("registerSchedule");
				detailData.put("GUAHAOBC", temp == null ? "0" : temp);

				// 挂号类别
				temp = body.get("registerCategory");
				detailData.put("GUAHAOLB", temp == null ? "0" : temp);

				// 医院编码
				temp = body.get("hospitalCode");
				detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

				// 科室编码
				temp = body.get("departmentCode");
				detailData.put("KESHIDM", temp == null ? "" : temp);

				// 医生编码
				temp = body.get("doctorId");
				detailData.put("YISHENGDM", temp == null ? "" : temp);

				params.put("BASEINFO", getBaseInfo(ctx));
				params.put("BODY", detailData);
				// detailData = new HashMap<String, Object>();
				Map<String, Object> providerData;

				System.out.println("==>params ==>" + params);
				// providerData = provider.registerSource(params);
				providerData = (Map<String, Object>) Client.rpcInvoke(
						"dr.drProvider", "registerSource",
						new Object[] { params });
				System.out.println("==>registerSourceHMD return ==>"
						+ providerData);
				int code = (Integer) providerData.get("code");

				message = providerData.get("msg") == null ? message
						: (String) providerData.get("msg");

				boolean isSuccess = SUCCESS
						.equalsIgnoreCase((String) providerData.get("msg"));

				if (code != 200 || !isSuccess) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_BUSINESS_DATA_NULL, message);
				}
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				if (!"".equals(((Map<String, Object>) providerData.get("data"))
						.get("HAOYUANMX"))) {
					list = (List<Map<String, Object>>) ((Map<String, Object>) providerData
							.get("data")).get("HAOYUANMX");
				}
				allList.addAll(list);
			}
			List<Map<String, Object>> lastList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < allList.size(); i++) {
				Map<String, Object> map1 = allList.get(i);
				boolean iscontinue = true;
				for (int j = 0; j < lastList.size(); j++) {
					Map<String, Object> map2 = lastList.get(j);
					if (map2.get("JIUZHENSJ").equals(map1.get("JIUZHENSJ"))) {
						map2.put(map1.get("RIQI").toString(), "1");
						iscontinue = false;
					}
				}
				if (iscontinue) {
					map1.put(map1.get("RIQI").toString(), "1");
					lastList.add(map1);
				}
			}
			return lastList;
		} catch (DRServiceException e) {
			logger.error("门诊挂号号源信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("门诊挂号号源信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}
	}

	/**
	 * @description 获取门诊挂号医生排班（获取挂号号源）
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> registerSourceHY(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {

		Map<String, Object> detailData = new HashMap<String, Object>();
		String message = "获取门诊挂号号源信息失败";
		List<Map<String, Object>> allList = new ArrayList<Map<String, Object>>();
		try {
			Object temp = body.get("referralDate");
			detailData.put("RIQI", temp);
			temp = body.get("registerType");
			detailData.put("GUAHAOFS", temp == null ? "0" : temp);
			// 挂号班次
			temp = body.get("registerSchedule");
			detailData.put("GUAHAOBC", temp == null ? "0" : temp);

			// 挂号类别
			temp = body.get("registerCategory");
			detailData.put("GUAHAOLB", temp == null ? "0" : temp);

			// 医院编码
			temp = body.get("hospitalCode");
			detailData.put("YYDM", temp == null ? "" : temp);

			// 科室编码
			temp = body.get("departmentCode");
			detailData.put("KESHIDM", temp == null ? "" : temp);

			// 医生编码
			temp = body.get("doctorId");
			detailData.put("YISHENGDM", temp == null ? "" : temp);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("BASEINFO", getBaseInfo(ctx));
			params.put("BODY", detailData);
			// detailData = new HashMap<String, Object>();
			Map<String, Object> providerData;

			System.out.println("==>params return ==>" + params);
			// providerData = provider.registerSource(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "registerSource", new Object[] { params });
			System.out.println("==>registerSource return ==>" + providerData);
			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (!"".equals(((Map<String, Object>) providerData.get("data"))
					.get("HAOYUANMX"))) {
				list = (List<Map<String, Object>>) ((Map<String, Object>) providerData
						.get("data")).get("HAOYUANMX");
			}
			return list;
		} catch (DRServiceException e) {
			logger.error("门诊挂号号源信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("门诊挂号号源信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}
	}

	/**
	 * @description 挂号预约取消
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> registerCancel(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Object temp = null;
		// 就诊卡类型 2，社保卡，3，就诊卡
		temp = body.get("JIUZHENKLX");
		detailData.put("JIUZHENKLX", temp == null ? "" : temp);
		// 就诊卡号
		temp = body.get("JIUZHENKH");
		detailData.put("JIUZHENKH", temp == null ? "" : temp);
		// 证件类型
		temp = null;
		detailData.put("ZHENGJIANLX", temp == null ? "" : temp);
		// 证件号码 必填
		temp = body.get("BINGRENSFZH");
		detailData.put("ZHENGJIANHM", temp == null ? "" : temp);
		// 姓名 必填
		temp = body.get("BINGRENXM");
		detailData.put("XINGMING", temp == null ? "" : temp);
		// 预约来源 必填 1.院内 2社区
		temp = body.get("YUYUELY");
		detailData.put("YUYUELY", temp == null ? "2" : temp);
		// 取号密码 必填
		temp = body.get("QUHAOMM");
		detailData.put("QUHAOMM", temp == null || temp == "" ? "123456" : temp);
		// 转入医院代码
		temp = body.get("ZHUANRUYYDM");
		detailData
				.put("ZHUANRUYYDM", temp == null || temp == "" ? "112" : temp);
		// 转入科室代码 必填
		temp = body.get("ZHUANRUKSDM");
		detailData.put("KESHIDM", temp == null || temp == "" ? "" : temp);
		// 医生代码 必填
		temp = body.get("YISHENGDM");
		detailData.put("YISHENGDM", temp == null || temp == "" ? "" : temp);
		// 挂号序号 必填
		temp = body.get("GUAHAOXH");
		detailData.put("GUAHAOXH", temp == null || temp == "" ? "" : temp);
		// 日期 必填
		temp = body.get("ZHUANZHENRQ");
		detailData.put("RIQI", temp == null ? "" : temp);

		params.put("BODY", detailData);
		params.put("BASEINFO", getBaseInfo(ctx));
		// params.putAll(detailData);
		Map<String, Object> data;
		String message = "挂号预约取消错误";
		try {
			System.out.println("==>registerCancel request ==>" + params);
			data = (Map<String, Object>) Client.rpcInvoke("dr.drProvider",
					"registerCancel", new Object[] { params });
			System.out.println("==>registerCancel return ==>" + params);
			int code = (Integer) data.get("code");
			message = data.get("msg") == null ? "挂号预约取消错误" : (String) data
					.get("msg");
			if (code != 200) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, message);
			}
			// 获取返回内容
			return (List<Map<String, Object>>) data.get("plans");
		} catch (DRServiceException e) {
			logger.error("挂号预约取消错误", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, e.getMessage());
		} catch (Exception e) {
			logger.error("挂号预约取消错误", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		}
	}

	/**
	 * @description 设备预约取消
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> registerDevicerCancel(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> detailData = new HashMap<String, Object>();

		// mpiId
		detailData.put("EMPIID",
				body.get("EMPIID") == null ? "" : body.get("EMPIID"));
		// 预约申请单编号
		detailData.put("YUYUESQDBH",
				body.get("YUYUESQDBH") == null ? "" : body.get("YUYUESQDBH"));

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// params.putAll(detailData);

		Map<String, Object> data;
		try {
			System.out.println("==>registerDevicerCancel request ==>" + params);
			// data = provider.registerDevicerCancel(params);
			data = (Map<String, Object>) Client.rpcInvoke("dr.drProvider",
					"registerDevicerCancel", new Object[] { params });
			System.out.println("==>registerDevicerCancel return ==>" + data);
			int code = (Integer) data.get("code");
			String message = data.get("msg") == null ? "设备预约取消错误"
					: (String) data.get("msg");
			if (code != 200) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, message);
			}
			// 获取返回内容
			return (List<Map<String, Object>>) data.get("plans");
		} catch (DRServiceException e) {
			logger.error("设备预约取消错误", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "设备预约取消失败");
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}
	}

	/**
	 * @description 门诊上转申请
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveSendExchange(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Object temp = body.get("EMPIID");
		detailData.put("EMPIID", temp == null ? "" : temp);

		temp = body.get("MZHM");
		detailData.put("MZHM", temp == null ? "" : temp);
		// 就诊卡类型 2，社保卡，3，就诊卡
		temp = null;
		detailData.put("JIUZHENKLX", temp == null ? "3" : temp);
		temp = body.get("JIUZHENKH");
		detailData.put("JIUZHENKH", temp == null ? "" : temp);
		// 医保卡类型
		temp = null;
		detailData.put("YIBAOKLX", temp == null ? "" : temp);
		// 医保卡信息
		temp = null;
		detailData.put("YIBAOKXX", temp == null ? "" : temp);
		// 业务类型
		temp = body.get("YEWULX");
		detailData.put("YEWULX", temp == null ? "" : temp);

		temp = body.get("BINGREMXM");
		detailData.put("BINGREMXM", temp == null ? "" : temp);
		detailData.put("BINGRENXM", temp == null ? "" : temp);

		temp = body.get("BINGRENXB");
		detailData.put("BINGRENXB", temp == null ? "" : temp);

		temp = body.get("BINGRENCSRQ");
		detailData.put("BINGRENCSRQ", temp == null ? "" : temp);

		temp = body.get("BINGRENNL");
		detailData.put("BINGRENNL", temp == null ? "" : temp);

		temp = body.get("BINGRENSFZH");
		detailData.put("BINGRENSFZH", temp == null ? "" : temp);

		temp = body.get("BINGRENLXDH");
		detailData.put("BINGRENLXDH", temp == null ? "" : temp);

		temp = body.get("BINGRENLXDZ");
		detailData.put("BINGRENLXDZ", temp == null ? "" : temp);

		temp = null;
		detailData.put("BINGRENFYLB", temp == null ? "" : temp);

		temp = body.get("SHENQINGJGDM");
		detailData.put("SHENQINGJGDM", temp == null ? "" : temp);

		temp = body.get("SHENQINGJGMC");
		detailData.put("SHENQINGJGMC", temp == null ? "" : temp);

		temp = body.get("SHENQINGJGLXDH");
		detailData.put("SHENQINGJGLXDH", temp == null ? "" : temp);

		temp = body.get("SHENQINGYS");
		detailData.put("SHENQINGYS", temp == null ? "" : temp);

		temp = body.get("SHENQINGYSDH");
		detailData.put("SHENQINGYSDH", temp == null ? "" : temp);

		temp = body.get("SHENQINGRQ");
		detailData.put("SHENQINGRQ", temp == null ? "" : temp);

		temp = body.get("ZHUANZHENYY");
		detailData.put("ZHUANZHENYY", temp == null ? "" : temp);

		temp = body.get("BINQINGMS");
		detailData.put("BINQINGMS", temp == null ? "" : temp);

		temp = body.get("ZHUANZHENZYSX");
		detailData.put("ZHUANZHENZYSX", temp == null ? "" : temp);

		temp = body.get("ZHUANRUYYDM");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

		temp = body.get("ZHUANRUYYMC");
		detailData.put("ZHUANRUYYMC", temp == null ? "" : temp);

		temp = body.get("ZHUANRUKSDM");
		detailData.put("ZHUANRUKSDM", temp == null ? "" : temp);
		detailData.put("KESHIDM", temp == null ? "" : temp);

		temp = body.get("ZHUANRUKSMC");
		detailData.put("ZHUANRUKSMC", temp == null ? "" : temp);
		detailData.put("KESHIMC", temp == null ? "" : temp);

		temp = body.get("YISHENGDM");
		detailData.put("YISHENGDM", temp == null ? "" : temp);

		temp = body.get("YISHENGXM");
		detailData.put("YISHENGXM", temp == null ? "" : temp);

		temp = body.get("ZHUANZHENRQ");
		detailData.put("RIQI", temp == null ? "" : temp);
		detailData.put("ZHUANZHENRQ", temp == null ? "" : temp);
		detailData.put("YUYUERQ", temp == null ? "" : temp);

		temp = body.get("GUAHAOXH");
		detailData.put("GUAHAOXH", temp == null ? "" : temp);

		temp = body.get("GUAHAOBC");
		detailData.put("GUAHAOBC", temp == null ? "" : temp);

		temp = body.get("JIUZHENSJ");
		detailData.put("JIUZHENSJ", temp == null ? "" : temp);
		// 一周排班ID 必填
		temp = body.get("YIZHOUPBID");
		detailData.put("YIZHOUPBID", temp == null ? "" : temp);
		// 当天排班ID 必填
		temp = body.get("DANGTIANPBID");
		detailData.put("DANGTIANPBID", temp == null ? "" : temp);
		// 医生代码 必填 普通号用*号代替
		temp = body.get("YISHENGDM");
		detailData.put("YISHENGDM", temp == null ? "" : temp);
		// 代收费用 默认传1 0 不代收 1 代收
		temp = null;
		detailData.put("DAISHOUFY", temp == null ? "1" : temp);
		// 转诊单号
		temp = null;
		detailData.put("ZHUANZHENDH", temp == null ? "" : temp);
		// try {
		// DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		// detailData.put("SHENQINGRQ", df.format(df.parse((String) temp)));
		// detailData.put("ZHUANZHENRQ", df.format(df.parse((String) temp)));
		// } catch (ParseException e1) {
		// e1.printStackTrace();
		// }
		//
		String message = "接收上转申请失败";
		try {
			// long jzxh = Long.parseLong(body.get("JZXH") + "");
			temp = null;

			params.put("BASEINFO", getBaseInfo(ctx));
			// params.put("CHUFANGMX", this.getCFXX(jzxh, dao, ctx));
			params.put("BODY", detailData);
			// IDRProvider provider = getIDRProvider(ctx);
			Map<String, Object> providerData;
			System.out.println("sendExchange==>send msg " + params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "sendExchange", new Object[] { params });
			System.out.println("sendExchange==>receive msg " + providerData);
			// params = new HashMap<String, Object>();
			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));
			String referralId = null;
			if (providerData.containsKey("data")) {
				referralId = ((Map<String, String>) providerData.get("data"))
						.get("ZHUANZHENDH");
			}
			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			// 获取返回内容
			// System.out.println("--------------------"+referralId);
			detailData.put("ZHUANZHENDH", referralId == null ? "" : referralId);
			detailData.put("ZHUANZHENZD", body.get("ZHUANZHENZD"));
			providerData = null;

			temp = (referralId == null ? "" : referralId).isEmpty() ? "0" : "1";
			detailData.put("STATUS", temp);
			referralId = null;
			temp = null;

			return detailData;
		} catch (DRServiceException e) {
			logger.error("接收上转申请", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("接收上转申请", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (Exception e) {
			logger.error("接收上转申请", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		}
	}

	/**
	 * 挂号处理
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> registerHandle(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> responseData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> scheduleInfo = (Map<String, Object>) body
				.get("scheduleInfo");
		Map<String, Object> personInfo = (Map<String, Object>) body
				.get("personInfo");
		// IDRProvider provider = getIDRProvider(ctx);

		Object temp = personInfo.get("empiId");
		detailData.put("EMPIID", temp == null ? "" : temp);
		responseData.put("EMPIID", temp == null ? "" : temp);
		// 就诊卡类型 2，社保卡，3，就诊卡
		temp = null;
		detailData.put("JIUZHENKLX", temp == null ? "" : temp);
		// 就诊卡号
		temp = personInfo.get("MZHM");
		detailData.put("JIUZHENKH", temp == null ? "" : temp);
		responseData.put("MZHM", temp == null ? "" : temp);
		// 证件类型
		temp = null;
		detailData.put("ZHENGJIANLX", temp == null ? "" : temp);
		// 证件号码 必填
		temp = body.get("idCard");
		detailData.put("ZHENGJIANHM", temp == null ? "" : temp);
		// 姓名 必填
		temp = personInfo.get("personName");
		detailData.put("XINGMING", temp == null ? "" : temp);
		// 一周排班ID 必填
		temp = scheduleInfo.get("YIZHOUPBID");
		detailData.put("YIZHOUPBID", temp == null ? "" : temp);
		// 当天排班ID 必填
		temp = scheduleInfo.get("DANGTIANPBID");
		detailData.put("DANGTIANPBID", temp == null ? "" : temp);
		// 日期 必填
		temp = scheduleInfo == null ? null : scheduleInfo.get("referralDate");
		detailData.put("RIQI", temp == null ? "" : temp);
		// 挂号班次 必填 1上午 2 下午
		temp = scheduleInfo == null ? null : scheduleInfo.get("GUAHAOBC");
		detailData.put("GUAHAOBC", temp == null ? "" : temp);
		// 挂号类别 必填 (取HIS的门诊类别)
		temp = scheduleInfo == null ? null : scheduleInfo.get("GUAHAOLB");
		detailData.put("GUAHAOLB", temp == null ? "" : temp);
		// 科室代码 必填
		temp = scheduleInfo == null ? null : scheduleInfo.get("departmentCode");
		detailData.put("KESHIDM", temp == null ? "" : temp);
		// 医生代码 必填 普通号用*号代替
		temp = scheduleInfo == null ? null : scheduleInfo.get("doctorId");
		detailData.put("YISHENGDM", temp == null ? "" : temp);
		// 挂号序号 必填 传0则HIS分配
		temp = scheduleInfo == null ? null : scheduleInfo.get("scheduleCode");
		detailData.put("GUAHAOXH", temp == null ? "0" : temp);
		// 挂号ID 默认传0 社区挂号时传0
		temp = null;
		detailData.put("GUAHAOID", temp == null ? "0" : temp);
		// 代收费用 默认传1 0 不代收 1 代收
		temp = null;
		detailData.put("DAISHOUFY", temp == null ? "1" : temp);
		// 预约来源 必填 1.院内 2社区
		temp = null;
		detailData.put("YUYUELY", temp == null ? "2" : temp);
		// 转诊单号
		temp = body == null ? null : body.get("ZHUANZHENDH");
		detailData.put("ZHUANZHENDH", temp == null ? "" : temp);
		// 转入医院代码
		temp = scheduleInfo == null ? null : scheduleInfo.get("hospitalCode");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// params.putAll(detailData);
		Map<String, Object> providerData;
		String message = "";
		try {
			// providerData = provider.registerHandle(params);
			System.out.println("registerHandle==>send msg " + params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "registerHandle", new Object[] { params });
			System.out.println("registerHandle==>receive msg " + providerData);
			params = null;
			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			temp = (String) ((Map<String, Object>) providerData.get("data"))
					.get("GUAHAOID");
			responseData.put("GUAHAOID", temp == null ? "" : temp);

			temp = (String) ((Map<String, Object>) providerData.get("data"))
					.get("GUAHAOXH");
			responseData.put("GUAHAOXH", temp == null ? "" : temp);

			temp = (String) ((Map<String, Object>) providerData.get("data"))
					.get("JIUZHENSJ");
			responseData.put("JIUZHENSJ", temp == null ? "" : temp);

			temp = (String) ((Map<String, Object>) providerData.get("data"))
					.get("JIUZHENDD");
			responseData.put("JIUZHENDD", temp == null ? "" : temp);

			List<Map<String, Object>> fyList = (List<Map<String, Object>>) ((Map<String, Object>) providerData
					.get("data")).get("FEIYONGMX");
			responseData.put("FEIYONGMXLIST", fyList == null ? "" : fyList);

			List<Map<String, Object>> jsList = (List<Map<String, Object>>) ((Map<String, Object>) providerData
					.get("data")).get("JIESUANJG");
			responseData.put("JIESUANJGLIST", jsList == null ? "" : jsList);

			Map<String, Object> jsxxList = (Map<String, Object>) ((Map<String, Object>) providerData
					.get("data")).get("XIANGXIJSJG");
			responseData.put("XIANGXIJSJGLIST", jsxxList == null ? ""
					: jsxxList);

			return responseData;
		} catch (DRServiceException e) {
			logger.error("挂号处理", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					message);
		} catch (Exception e) {
			logger.error("挂号处理", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}

	}

	/**
	 * @author
	 * @description 住院上转申请
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveZySendExchange(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Object temp = body.get("EMPIID");
		detailData.put("EMPIID", temp == null ? "" : temp + "");
		temp = body.get("JIUZHENKLX");
		detailData.put("JIUZHENKLX", temp == null ? "" : temp + "");

		temp = body.get("JIUZHENKH");
		detailData.put("JIUZHENKH", temp == null ? "" : temp + "");
		detailData.put("MZHM", temp == null ? "" : temp + "");

		temp = null;
		detailData.put("ZYH", temp == null ? "" : temp + "");

		temp = body.get("YEWULX");
		detailData.put("YEWULX", temp == null ? "" : temp + "");// 业务类型

		temp = body.get("BINGREMXM");
		detailData.put("BINGREMXM", temp == null ? "" : temp + "");
		detailData.put("BINGRENXM", temp == null ? "" : temp + "");

		temp = body.get("BINGRENXB");
		detailData.put("BINGRENXB", temp == null ? "" : temp + "");

		temp = body.get("BINGRENCSRQ");
		detailData.put("BINGRENCSRQ", temp == null ? "" : temp + "");

		temp = body.get("BINGRENNL");
		detailData.put("BINGRENNL", temp == null ? "" : temp + "");

		temp = body.get("BINGRENSFZH");
		detailData.put("BINGRENSFZH", temp == null ? "" : temp + "");

		temp = body.get("BINGRENLXDH");
		detailData.put("BINGRENLXDH", temp == null ? "" : temp + "");

		temp = body.get("BINGRENLXDZ");
		detailData.put("BINGRENLXDZ", temp == null ? "" : temp + "");

		temp = null;
		detailData.put("BINGRENFYLB", temp == null ? "" : temp + "");

		temp = body.get("SHENQINGJGDM");
		detailData.put("SHENQINGJGDM", temp == null ? "" : temp + "");

		temp = body.get("SHENQINGJGMC");
		detailData.put("SHENQINGJGMC", temp == null ? "" : temp + "");

		temp = body.get("SHENQINGJGLXDH");
		detailData.put("SHENQINGJGLXDH", temp == null ? "" : temp + "");

		temp = body.get("SHENQINGYS");
		detailData.put("SHENQINGYS", temp == null ? "" : temp + "");

		temp = body.get("SHENQINGYSDH");
		detailData.put("SHENQINGYSDH", temp == null ? "" : temp + "");

		temp = body.get("SHENQINGRQ");
		try {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			detailData.put("SHENQINGRQ", df.format(df.parse((String) temp)));
			detailData.put("ZHUANZHENRQ", df.format(df.parse((String) temp)));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		temp = body.get("ZHUANZHENYY");
		detailData.put("ZHUANZHENYY", temp == null ? "" : temp + "");

		temp = body.get("BINQINGMS");
		detailData.put("BINQINGMS", temp == null ? "" : temp + "");

		temp = body.get("ZHUANZHENZYSX");
		detailData.put("ZHUANZHENZYSX", temp == null ? "" : temp + "");

		temp = body.get("ZHUANRUYYDM");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp + "");

		temp = body.get("ZHUANRUKSDM");
		detailData.put(
				"ZHUANRUKSDM",
				temp == null ? "" : (temp + "").substring(0,
						(temp + "").length() - 2));

		temp = body.get("ZHUANRUKSMC");
		detailData.put("ZHUANRUKSMC", temp == null ? "" : temp + "");

		temp = null;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);

		// IDRProvider provider = getIDRProvider(ctx);
		Map<String, Object> providerData;
		String message = "接收上转申请失败";

		try {
			System.out.println("===>住院sendExchange" + params);
			// providerData = provider.sendExchange(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "sendExchange", new Object[] { params });
			System.out.println("===>住院sendExchange" + providerData);
			params = null;

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			temp = body.get("ZHUANZHENZD");
			detailData.put("ZHUANZHENZD", temp == null ? "" : temp + "");
			temp = body.get("ZHUANRUYYMC");
			detailData.put("ZHUANRUYYMC", temp == null ? "" : temp + "");
			// temp = body.get("ZHUANRUKSDM");
			// detailData.put("ZHUANRUKSDM", temp == null ? "" : temp + "");

			// 获取返回内容
			String referralId = ((Map<String, String>) providerData.get("data"))
					.get("ZHUANZHENDH");
			detailData.put("ZHUANZHENDH", referralId == null ? "" : referralId);
			providerData = null;

			temp = (referralId == null ? "" : referralId).isEmpty() ? "0" : "1";
			detailData.put("STATUS", temp);
			referralId = null;
			temp = null;

			return detailData;
		} catch (DRServiceException e) {
			logger.error("接收上转申请", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("接收上转申请", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "住院上转申请失败");
		}
	}

	/**
	 * @description 转诊记录
	 */
	public void saveClinicRecordlHistory(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			// System.out.println("saveClinicRecordlHistory=====:" + body);
			if (body.containsKey("EMPIID")) {
				String empiid = (String) body.get("EMPIID");
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("EMPIID", empiid);
				Map<String, Object> map_mzhm = dao
						.doLoad("select max(MZHM) as MZHM from MS_BRDA where EMPIID=:EMPIID",
								parameters);
				if (map_mzhm != null && map_mzhm.containsKey("MZHM")) {
					body.put("MZHM", map_mzhm.get("MZHM"));
				}
			}
			body.put("RESERVENO", body.get("ZHUANZHENDH"));
			body.put("SUBMITAGENCY", body.get("SHENQINGJGDM"));
			body.put("SUBMITORDOCTOR", body.get("SHENQINGYS"));

			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
			if ((body.get("SHENQINGRQ") + "").contains("-")) {
				sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			}
			Date sqdate = sdf1.parse(body.get("SHENQINGRQ") + "");
			body.put("SHENQINGRQ", sqdate);

			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
			if ((body.get("ZHUANZHENRQ") + "").contains("-")) {
				sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			}

			Date zzdate = sdf2.parse(body.get("ZHUANZHENRQ") + "");
			body.put("ZHUANZHENRQ", zzdate);

			dao.doSave("create",
					"phis.application.twr.schemas.DR_CLINICRECORDLHISTORY",
					body, true);
		} catch (ValidateException e) {
			logger.error("转诊记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("转诊记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		} catch (ParseException e) {
			logger.error("转诊记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	private static Map<String, Object> verifyCommonData(
			Map<String, Object> body, Context ctx, String msg)
			throws ModelDataOperationException {
		if (null == body || null == ctx) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, msg + "-请求数据为空");
		}

		Map<String, Object> commonData = new HashMap<String, Object>();

		String referralDate = String.valueOf(body.get("referralDate"));

		if (null == referralDate || referralDate.isEmpty()) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, msg + "-转诊时间不能为空");
		}
		commonData.put("referralDate", referralDate);
		referralDate = null;

		IDRProvider provider = getIDRProvider(ctx);
		if (null == provider) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, msg + "获取转诊平台服务失败");
		}
		commonData.put("IDRProvider", provider);
		provider = null;

		Map<String, Object> baseInfo = getBaseInfo(ctx);
		if (null == baseInfo) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, msg + "-后台服务错误");
		}
		commonData.put("baseInfo", baseInfo);
		baseInfo = null;

		return commonData;
	}

	private static Map<String, Object> getBaseInfo(Context ctx) {
		if (null == ctx) {
			return null;
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		if (null == user) {
			return null;
		}
		Map<String, Object> baseInfo = new HashMap<String, Object>();
		// 必填
		baseInfo.put("CAOZUOYDM", user.getUserId());
		baseInfo.put("CAOZUOYXM", user.getUserName());
		baseInfo.put("CAOZUORQ",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		baseInfo.put("JIGOUDM", user.getManageUnit().getId());
		// 非必填
		baseInfo.put("MessageId", UUID.randomUUID().toString());

		return baseInfo;
	}

	private static IDRProvider getIDRProvider(Context ctx) {
		// WebApplicationContext wac = (WebApplicationContext) ctx
		// .get(Context.APP_CONTEXT);
		// IDRProvider dr = (IDRProvider) wac.getBean("dr.drProvider");
		IDRProvider dr = (IDRProvider) AppDomainContext
				.getBean("dr.drProvider");
		return dr;
	}

	/**
	 * 获取检查分类信息
	 */
	@SuppressWarnings("unchecked")
	public Object getItemCategory(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> detailData = new HashMap<String, Object>();
		Object temp = body.get("referralDate");
		// RPC需要的参数
		detailData.put("Date", temp == null ? "" : temp);
		temp = body.get("hospitalCode");
		detailData.put("hospitalCode", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);

		Map<String, Object> providerData;
		String message = "获取检查分类失败";
		try {

			System.out.println("==>getCheckClassify request==>" + params);
			// providerData = provider.getCheckClassify(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "getCheckClassify",
					new Object[] { params });
			System.out.println("==>getCheckClassify return==>" + providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			return (List<Map<String, Object>>) providerData.get("data");

		} catch (DRServiceException e) {
			logger.error("获取检查分类信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取检查分类信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}

	}

	/**
	 * 获取检查方向信息
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object getCheckDirection(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		// RPC需要的参数
		detailData.put("Date", temp == null ? "" : temp);
		temp = body.get("hospitalCode");
		detailData.put("hospitalCode", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// IDRProvider provider = getIDRProvider(ctx);

		Map<String, Object> providerData;
		String message = "获取检查方向失败";
		try {

			System.out.println("==>getCheckDirection request==>" + params);
			// providerData = provider.getCheckDirection(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "getCheckDirection",
					new Object[] { params });
			System.out.println("==>getCheckDirection return==>" + providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			return (List<Map<String, Object>>) providerData.get("data");

		} catch (DRServiceException e) {
			logger.error("获取检查方向信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取检查方向信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}

	}

	/**
	 * 获取检查部位信息
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Object getCheckPart(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> detailData = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		// RPC需要的参数
		detailData.put("Date", temp == null ? "" : temp);
		temp = body.get("hospitalCode");
		detailData.put("hospitalCode", temp == null ? "" : temp);

		// IDRProvider provider = getIDRProvider(ctx);
		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);

		Map<String, Object> providerData;
		String message = "获取检查部位失败";
		try {

			System.out.println("==>getCheckPart request==>" + params);
			// providerData = provider.getCheckPart(params);
			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "getCheckPart", new Object[] { params });
			System.out.println("==>getCheckPart return ==>" + providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			return (List<Map<String, Object>>) providerData.get("data");

		} catch (DRServiceException e) {
			logger.error("获取检查部位信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取检查部位信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}

	}

	/**
	 * 获取检查项目
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Object getCheckItems(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> detailData = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		// RPC需要的参数
		detailData.put("Date", temp == null ? "" : temp);

		temp = body.get("hospitalCode");
		// RPC需要的参数
		detailData.put("hospitalCode", temp == null ? "" : temp);
		temp = body.get("classifyCode");
		// RPC需要的参数
		detailData.put("classifyCode", temp == null ? "" : temp);
		// IDRProvider provider = getIDRProvider(ctx);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);

		Map<String, Object> providerData;
		String message = "获取检查项目失败";
		try {

			System.out.println("==>getCheckItems request ==>" + params);
			// providerData = provider.getCheckItems(params);

			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "getCheckItems", new Object[] { params });

			System.out.println("==>getCheckItems return ==>" + providerData);

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			return (List<Map<String, Object>>) providerData.get("data");

		} catch (DRServiceException e) {
			logger.error("获取检查项目信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取检查项目信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}

	}

	/**
	 * 获取检查设备
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Object getMedicalStatus(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> detailData = new HashMap<String, Object>();

		Object temp = body.get("referralDate");
		detailData.put("YUYUERQ",
				temp == null ? "" : (temp + "").replace("/", "-"));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currDate = sdf.format(new Date());
		if (temp.toString() == currDate || currDate.equals(temp.toString())) {
			SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm:ss");
			detailData.put("YUYUESJ", sdfh.format(new Date()));
		} else {
			detailData.put("YUYUESJ", "00:00:00");
		}

		temp = body.get("itemCode");
		detailData.put("JIANCHAXMDM", temp == null ? "" : temp);

		temp = body.get("hospitalCode");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// params.putAll(detailData);
		// IDRProvider provider = getIDRProvider(ctx);

		Map<String, Object> providerData;
		String message = "获取检查设备失败";
		try {

			System.out.println("==>getRegisterInfo request==>" + params);
			// providerData = provider.getRegisterInfo(params);

			providerData = (Map<String, Object>) Client
					.rpcInvoke("dr.drProvider", "getRegisterInfo",
							new Object[] { params });
			System.out.println("==>getRegisterInfo return ==>" + providerData);

			// ----------------------------------------------------------------
			// if (providerData.get("data") == null) {
			// List<Map<String, Object>> SHEBEIYYXXXXList = new
			// ArrayList<Map<String, Object>>();
			// for (int i = 1; i <= 3; i++) {
			// Map<String, Object> tempMap = new HashMap<String, Object>();
			// tempMap.put("JIANCHASBDM", "100" + i);
			// tempMap.put("JIANCHASBMC", "测试检查" + i);
			// tempMap.put("JIANCHASBDD", "测试检查地点" + i);
			// tempMap.put("YUYUERQ", detailData.get("YUYUERQ"));
			// tempMap.put("YUYUEKSSJ", "12:00:00");
			// tempMap.put("YUYUEJSSJ", "13:00:00");
			// tempMap.put("YUYUEJCBW", "测试部位" + i);
			// tempMap.put("JIANCHAYYLX", "2");
			// SHEBEIYYXXXXList.add(tempMap);
			// }
			// Map<String, Object> lMap = new HashMap<String, Object>();
			// lMap.put("SHEBEIYYXXXX", SHEBEIYYXXXXList);
			// providerData.put("data", lMap);
			// }
			// ----------------------------------------------------------------

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			// List<Map<String, Object>> responseData = new
			// ArrayList<Map<String, Object>>();
			// if (providerData.get("data") != null
			// && ((Map<String, Object>) providerData.get("data"))
			// .get("SHEBEIYYXXXX") != null
			// && !"".equals(((Map<String, Object>) providerData
			// .get("data")).get("SHEBEIYYXXXX"))) {
			// List<Map<String, Object>> sbList = (List<Map<String, Object>>)
			// ((Map<String, Object>) providerData
			// .get("data")).get("SHEBEIYYXXXX");
			// for (int i = 0; i < sbList.size(); i++) {
			// Map<String, Object> sbMap = sbList.get(i);
			// if (!"0".equals((String) sbMap.get("YUYUEHZS"))) {
			// List<Map<String, Object>> yyhList = (List<Map<String, Object>>)
			// sbMap
			// .get("YUYUEHXXXX");
			// for (int k = 0; k < yyhList.size(); k++) {
			// Map<String, Object> yyhMap = yyhList.get(k);
			// List<Map<String, Object>> yyList = (List<Map<String, Object>>)
			// yyhMap
			// .get("YUYUEHXX");
			// if (yyList.size() > 0) {
			// for (int j = 0; j < yyList.size(); j++) {
			// Map<String, Object> yyMap = yyList.get(j);
			// // System.out.println("sbMap===" + sbMap);
			// if ("0".equals((String) yyMap
			// .get("YUYUEZT"))) {
			// Map<String, Object> resMap = new HashMap<String, Object>();
			// resMap.put("JIANCHASBDM",
			// sbMap.get("JIANCHASBDM"));
			// resMap.put("JIANCHASBMC",
			// sbMap.get("JIANCHASBMC"));
			// resMap.put("JIANCHASBDD",
			// sbMap.get("JIANCHASBDD"));
			// resMap.put("YUYUEKSSJ",
			// sbMap.get("YUYUEKSSJ"));
			// resMap.put("YUYUEJSSJ",
			// sbMap.get("YUYUEJSSJ"));
			// resMap.put("YUYUEH",
			// yyMap.get("YUYUEH"));
			// resMap.put("YUYUEZT",
			// yyMap.get("YUYUEZT"));
			// responseData.add(resMap);
			// }
			//
			// }
			// }
			// }
			// }
			// }
			// }
			// System.out.println("responseData==" + responseData);
			return (List<Map<String, Object>>) providerData.get("data");
		} catch (DRServiceException e) {
			logger.error("获取检查设备信息", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("获取检查设备信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			logger.error("获取检查设备信息", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}
	}

	/**
	 * 发送检查单
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveSendExamine(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> detailData = new HashMap<String, Object>();
		String username = user.getUserName();
		String userid = user.getUserId();
		String ManageUnitId = user.getManageUnitId();
		String ManageUnitName = user.getManageUnitName();
		detailData.put("SHENQINGYSMC", username);
		detailData.put("SHENQINGYSDM", userid);
		detailData.put("SHENQINGJGDM", ManageUnitId);
		detailData.put("SHENQINGJGMC", ManageUnitName);
		Object temp = body.get("JIUZHENKLX");
		detailData.put("JIUZHENKLX", temp == null ? "" : temp);
		temp = body.get("JIUZHENKH");
		detailData.put("JIUZHENKH", temp == null ? "" : temp);
		temp = body.get("BINGRENXB");
		detailData.put("BINGRENXB", temp == null ? "" : temp.toString());
		temp = body.get("BINGRENCSRQ");
		detailData.put("BINGRENCSRQ", temp == null ? "" : temp);
		temp = body.get("BINGRENNL");
		detailData.put("BINGRENNL", temp == null ? "" : temp);
		temp = body.get("BINGRENSFZH");
		detailData.put("BINGRENSFZH", temp == null ? "" : temp);
		temp = body.get("BINGRENLXDH");
		detailData.put("BINGRENLXDH", temp == null ? "" : temp);
		temp = body.get("BINGRENLXDZ");
		detailData.put("BINGRENLXDZ", temp == null ? "" : temp);
		temp = body.get("BINGREMXM");
		detailData.put("BINGREMXM", temp == null ? "" : temp);
		detailData.put("BINGRENXM", temp == null ? "" : temp);
		temp = body.get("BINGRENLB");
		detailData.put("BINGRENLB", temp == null ? "" : temp);
		detailData.put("SONGJIANYS","");
		temp = body.get("SONGJIANKS");
		detailData.put("SONGJIANKS", temp == null ? "" : temp);
		temp = body.get("SONGJIANKSMC");
		detailData.put("SONGJIANKSMC", temp == null ? "" : temp);
		temp = body.get("SHOUFEISB");
		detailData.put("SHOUFEISB", temp == null ? "" : temp);
		temp = body.get("YEWULX");
		detailData.put("YEWULX", temp == null ? "" : temp);
		temp = body.get("BINGQU");
		detailData.put("BINGQU", temp == null ? "" : temp);
		temp = body.get("BINGCHUANGHAO");
		detailData.put("BINGCHUANGHAO", temp == null ? "" : temp);
		temp = body.get("JIANCHAYUYUESJ").toString().replaceAll("/", "-");
		detailData.put("JIANCHAYUYUESJ", temp == null ? "" : temp);
		detailData.put("SONGJIANRQ", temp == null ? "" : temp);
		temp = body.get("BINGQINGMS");
		detailData.put("BINGQINGMS", temp == null ? "" : temp);
		temp = body.get("ZHENDUAN");
		detailData.put("ZHENDUAN", temp == null ? "" : temp);
		temp = body.get("BINGRENTZ");
		detailData.put("BINGRENTZ", temp == null ? "" : temp);
		temp = body.get("QITAJC");
		detailData.put("QITAJC", temp == null ? "" : temp);
		temp = body.get("BINGRENZS");
		detailData.put("BINGRENZS", temp == null ? "" : temp);
		temp = body.get("JIANCHALY");
		detailData.put("JIANCHALY", temp == null ? "" : temp);
		temp = body.get("ZHUANRUYYDM");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);
		temp = body.get("JIANCHAXMBH");
		detailData.put("JIANCHAXMBH", temp == null ? "" : temp);
		temp = body.get("JIANCHAXMMC");
		detailData.put("JIANCHAXMMC", temp == null ? "" : temp);
		temp = body.get("JIANCHAFLBM");
		detailData.put("JIANCHAFLBM", temp == null ? "" : temp);
		temp = body.get("JIANCHASTBW");
		detailData.put("JIANCHASTBW", temp == null ? "" : temp);
		temp = body.get("JIANCHAFXDM");
		detailData.put("JIANCHAFXDM", temp == null ? "" : temp);
		temp = body.get("JIANCHAZYDM");
		detailData.put("JIANCHAZYDM", temp == null ? "" : temp);
		temp = body.get("JIANCHATS");
		detailData.put("JIANCHATS", temp == null ? "" : temp);
		temp = body.get("EMPIID");
		detailData.put("EMPIID", temp == null ? "" : temp);
		temp = body.get("MZHM");
		detailData.put("MZHM", temp == null ? "" : temp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		detailData.put("SHENQINGRQ", sdf.format(new Date()));
//		Map<String, Object> jianChaMx = new HashMap<String, Object>();
//		temp = body == null ? null : body.get("JIANCHAXMDM");
//		jianChaMx.put("JIANCHAXMBH", temp == null ? "" : temp);
//
//		temp = body == null ? null : body.get("JIANCHAXMMC");
//		jianChaMx.put("JIANCHAXMMC", temp == null ? "" : temp);
//
//		temp = body == null ? null : body.get("JIANCHAXMLX");
//		jianChaMx.put("JIANCHAFLBM", temp == null ? "" : temp);
//
//		temp = body == null ? null : body.get("JIANCHABWDM");
//		jianChaMx.put("JIANCHASTBW", temp == null ? "" : temp);
//
//		temp = body == null ? null : body.get("YINGXIANGFX");
//		jianChaMx.put("JIANCHAFXDM", temp == null ? "" : temp);
//
//		temp = null;
//		jianChaMx.put("JIANCHAZYDM", temp == null ? "" : temp);
//
//		temp = null;
//		jianChaMx.put("JIANCHASQDH", temp == null ? "" : temp);

//		temp = body == null ? null : body.get("referralDate");
//		detailData.put("YUYUERQ", temp == null ? "" : temp);
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		try {
//			detailData.put("SONGJIANRQ",
//					temp == null ? new Date() : sdf.parse(temp.toString()));
//		} catch (ParseException e1) {
//			logger.error("日期格式转化错误");
//			e1.printStackTrace();
//		}

		// Map<String, Object> jianChaLb = new HashMap<String, Object>();
		// jianChaLb.put("JIANCHAXX", jianChaMx);

//		detailData.put("JIANCHAMX", jianChaMx);

		temp = null;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);

		// params.putAll(detailData);

		// IDRProvider provider = getIDRProvider(ctx);
		Map<String, Object> providerData;
		String message = "检查申请失败";

		try {

			System.out.println("==>checklistAccept==>request==>" + params);
			// providerData = provider.checklistAccept(params);

			providerData = (Map<String, Object>) Client
					.rpcInvoke("dr.drProvider", "checklistAccept",
							new Object[] { params });
			System.out.println("==>checklistAccept return ==>" + providerData);
			params = null;

			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			// 获取返回内容
			String referralId = "";
			if (providerData.containsKey("data")
					&& ((Map<String, String>) providerData.get("data"))
							.containsKey("JIANCHASQDH")) {
				referralId = ((Map<String, String>) providerData.get("data"))
						.get("JIANCHASQDH");
			}
			detailData.put("JIANCHASQDH", referralId);
			providerData = null;

			temp = (referralId == null ? "" : referralId).isEmpty() ? "0" : "1";
			detailData.put("STATUS", temp);
			referralId = null;
			temp = null;
			temp = body.get("SONGJIANYS");
			detailData.put("SONGJIANYS", temp == null ? "" : temp);
			return detailData;
		} catch (DRServiceException e) {
			logger.error("检查申请", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (ModelDataOperationException e) {
			logger.error("检查申请", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		} catch (Exception e) {
			logger.error("检查申请", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}
	}

	/**
	 * 设备预约
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveSendEquipment(Map<String, Object> body,
			Context ctx, Map<String, Object> map)
			throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		// IDRProvider provider = getIDRProvider(ctx);

		Object temp = body.get("JIANCHAKSDM");
		detailData.put("JIANCHAKSDM", temp == null ? "" : temp);

		temp = body.get("JIANCHAKSMC");
		detailData.put("JIANCHAKSMC", temp == null ? "" : temp);

		temp = body.get("BINGRENFPH");
		detailData.put("BINGRENFPH", temp == null ? "" : temp);
		// 病人类型
		temp = body.get("BINGRENLX");
		detailData.put("BINGRENLX", temp == null ? "1" : temp);
		// 病人类型名称
		temp = body.get("BINGRENLXMC");
		detailData.put("BINGRENLXMC", temp == null ? "" : temp);
		// 病人卡号
		temp = body.get("BINGRENKH");
		detailData.put("BINGRENKH", temp == null ? "" : temp);
		// 病人门诊号
		temp = body.get("BINGRENMZH");
		detailData.put("BINGRENMZH", temp == null ? "" : temp);
		// 病人住院号
		temp = body.get("BINGRENZYH");
		detailData.put("BINGRENZYH", temp == null ? "" : temp);
		// 病人病区代码
		temp = body.get("BINGRENBQDM");
		detailData.put("BINGRENBQDM", temp == null ? "" : temp);
		// 病人病区名称
		temp = body.get("BINGRENBQMC");
		detailData.put("BINGRENBQMC", temp == null ? "" : temp);
		// 病人床位号
		temp = body.get("BINGRENCWH");
		detailData.put("BINGRENCWH", temp == null ? "" : temp);
		// 病人姓名
		temp = body.get("BINGRENXM");
		detailData.put("BINGRENXM", temp == null ? "" : temp);
		// 病人性别
		temp = body.get("BINGRENXB");
		detailData.put("BINGRENXB", temp == null ? "" : temp);
		// 病人年龄
		temp = body.get("BINGRENNL");
		detailData.put("BINGRENNL", temp == null ? "" : temp);
		// 病人出生日期
		temp = body.get("BINGRENCSRQ");
		detailData.put("BINGRENCSRQ", temp == null ? "" : temp);
		// 病人联系地址
		temp = body.get("BINGRENLXDZ");
		detailData.put("BINGRENLXDZ", temp == null ? "" : temp);
		// 病人联系电话
		temp = body.get("BINGRENLXDH");
		detailData.put("BINGRENLXDH", temp == null ? "" : temp);
		// 申请医生工号
		temp = body.get("SHENQINGYSGH");
		detailData.put("SHENQINGYSGH", temp == null ? "" : temp);
//		// 申请医生姓名
//		temp = body.get("SHENQINGYSMC");
//		detailData.put("SHENQINGYSMC", temp == null ? "" : temp);
		// 申请医院代码
		temp = body.get("SHENQINGYYMC");
		detailData.put("SHENQINGYYMC", temp == null ? "" : temp);
		// 申请医院名称
		temp = body.get("SHENQINGYYMC");
		detailData.put("SHENQINGYYMC", temp == null ? "" : temp);
		// 检查项目代码
		temp = body.get("JIANCHAXMDM");
		detailData.put("JIANCHAXMDM", temp == null ? "" : temp);
		// 检查项目名称
		temp = body.get("JIANCHAXMMC");
		detailData.put("JIANCHAXMMC", temp == null ? "" : temp);
		// 检查项目类型
		temp = body.get("JIANCHAXMLX");
		detailData.put("JIANCHAXMLX", temp == null ? "" : temp);
		// 检查部位代码
		temp = body.get("JIANCHABWDM");
		detailData.put("JIANCHABWDM", temp == null ? "" : temp);
		// 检查部位名称
		temp = body.get("JIANCHABWMC");
		detailData.put("JIANCHABWMC", temp == null ? "" : temp);
		// 预约日期
		temp = body.get("YUYUERQ");
		detailData.put("YUYUERQ", temp == null ? "" : temp.toString().replaceAll("/","-"));
		// 检查医院代码
		temp = body.get("ZHUANRUYYDM");
		detailData.put("JIANCHAYYDM", temp == null ? "" : temp);
		// 检查医院名称
		temp = body.get("ZHUANRUYYMC");
		detailData.put("JIANCHAYYMC", temp == null ? "" : temp);
		// 预约时间
		temp = body.get("YUYUESJ");
		detailData.put("YUYUESJ", temp == null ? "" : temp.toString());
		// 预约号
		temp = body.get("YUYUEH");
		detailData.put("YUYUEH", temp == null ? "" : temp);
		detailData.put("YIQIYUYUEH", temp == null ? "" : temp);
		// 检查设备代码
		temp = body.get("JIANCHASBDM");
		detailData.put("JIANCHASBDM", temp == null ? "" : temp);
		// 检查设备名称
		temp = body.get("JIANCHASBMC");
		detailData.put("JIANCHASBMC", temp == null ? "" : temp);
		// 检查设备地点
		temp = body.get("JIANCHASBDD");
		detailData.put("JIANCHASBDD", temp == null ? "" : temp);
		// 身份证号
		temp = body.get("SHENFENZH");
		detailData.put("SHENFENZH", temp == null ? "" : temp);
		// 预约收费 默认0 0未收费，1已收费
		temp = body.get("YUYUESF");
		detailData.put("YUYUESF", temp == null ? "0" : temp);
		// 预约收费 默认1 0需确认，1无需确认
		temp = body.get("YUYUEZT");
		detailData.put("YUYUEZT", temp == null ? "1" : temp);
		// 检查申请单编号
		temp = map.get("JIANCHASQDH");
		detailData.put("JIANCHASQDBH", temp == null ? "" : temp);
		// 影向方向代码
		temp = body.get("YINGXIANGFX");
		detailData.put("YINGXIANGFX", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// params.putAll(detailData);
		Map<String, Object> providerData;
		String message = "";
		try {
			System.out.println("==>registerDevice request==>" + params);
			// providerData = provider.registerDevice(params);

			providerData = (Map<String, Object>) Client.rpcInvoke(
					"dr.drProvider", "registerDevice", new Object[] { params });
			System.out.println("==>registerDevice return ==>" + providerData);
			params = null;
			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: (String) providerData.get("msg");

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			temp = body.get("EMPIID");
			detailData.put("EMPIID", temp == null ? "" : temp);
			// 影像方向名称
			temp = body.get("directionName");
			detailData.put("YINGXIANGFX", temp == null ? "" : temp);

			if (providerData.containsKey("data")) {
				String YUYUESQDBH = (String) ((Map<String, Object>) providerData
						.get("data")).get("YUYUESQDBH");
				detailData.put("YUYUESQDBH", YUYUESQDBH == null ? ""
						: YUYUESQDBH);

				String YUYUEH = (String) ((Map<String, Object>) providerData
						.get("data")).get("YUYUEH");
				detailData.put("YUYUEH", YUYUEH == null ? "" : YUYUEH);

				String YUYUESJ = (String) ((Map<String, Object>) providerData
						.get("data")).get("YUYUESJ");
				detailData.put("YUYUESJ", YUYUESJ == null ? "" : YUYUESJ);
			}

			return detailData;
		} catch (DRServiceException e) {
			logger.error("设备预约", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					"获取转诊平台服务失败,请检查连接情况");
		} catch (Exception e) {
			logger.error("设备预约", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, "设备预约取消失败");
		}

	}

	/**
	 * 接收预约挂号请求
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> registerRequest(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> detailData = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> responseData = new HashMap<String, Object>();
		// IDRProvider provider = getIDRProvider(ctx);
		Object temp = body.get("EMPIID");
		responseData.put("EMPIID", temp == null ? "" : temp);
		// 就诊卡类型 2，社保卡，3，就诊卡
		temp = null;
		detailData.put("JIUZHENKLX", temp == null ? "" : temp);
		// 就诊卡号
		temp = body.get("JIUZHENKH");
		detailData.put("JIUZHENKH", temp == null ? "" : temp);
		// 证件类型
		temp = null;
		detailData.put("ZHENGJIANLX", temp == null ? "" : temp);
		// 证件号码 必填
		temp = body.get("BINGRENSFZH");
		detailData.put("ZHENGJIANHM", temp == null ? "" : temp);
		// 姓名 必填
		temp = body.get("BINGREMXM");
		detailData.put("XINGMING", temp == null ? "" : temp);
		// 一周排班ID 必填
		temp = body.get("YIZHOUPBID");
		detailData.put("YIZHOUPBID", temp == null ? "" : temp);
		// 当天排班ID 必填
		temp = body.get("DANGTIANPBID");
		detailData.put("DANGTIANPBID", temp == null ? "" : temp);
		// 日期 必填
		temp = body == null ? null : body.get("ZHUANZHENRQ");
		detailData.put("RIQI", temp == null ? "" : temp);
		// 挂号班次 必填 1上午 2 下午
		temp = body == null ? null : body.get("GUAHAOBC");
		detailData.put("GUAHAOBC", temp == null ? "" : temp);
		// 挂号类别 必填 (取HIS的门诊类别)
		temp = body == null ? null : body.get("GUAHAOLB");
		detailData.put("GUAHAOLB", temp == null ? "" : temp);
		// 科室代码 必填
		temp = body == null ? null : body.get("ZHUANRUKSDM");
		detailData.put("KESHIDM", temp == null ? "" : temp);
		// 医生代码 必填 普通号用*号代替
		temp = body == null ? null : body.get("YISHENGDM");
		detailData.put("YISHENGDM", temp == null ? "" : temp);
		// 挂号序号 必填 传0则HIS分配
		temp = body == null ? null : body.get("GUAHAOXH");
		detailData.put("GUAHAOXH", temp == null ? "0" : temp);
		// 预约来源 必填 1.院内 2社区
		temp = "2";
		detailData.put("YUYUELY", temp == null ? "" : temp);
		// 转入医院代码
		temp = body == null ? null : body.get("ZHUANRUYYDM");
		detailData.put("ZHUANRUYYDM", temp == null ? "" : temp);
		// 转诊单号
		temp = body == null ? null : body.get("ZHUANZHENDH");
		detailData.put("ZHUANZHENDH", temp == null ? "" : temp);

		params.put("BASEINFO", getBaseInfo(ctx));
		params.put("BODY", detailData);
		// params.putAll(detailData);

		Map<String, Object> providerData;
		String message = "";
		try {
			System.out.println("==>registerRequest request==>" + params);
			// providerData = provider.registerRequest(params);

			providerData = (Map<String, Object>) Client
					.rpcInvoke("dr.drProvider", "registerRequest",
							new Object[] { params });
			System.out.println("==>registerRequest return ==>" + providerData);
			params = null;
			int code = (Integer) providerData.get("code");

			message = providerData.get("msg") == null ? message
					: providerData.get("msg").toString();

			boolean isSuccess = SUCCESS.equalsIgnoreCase((String) providerData
					.get("msg"));

			if (code != 200 || !isSuccess) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_BUSINESS_DATA_NULL, message);
			}
			temp = ((Map<String, Object>) providerData.get("data"))
					.get("QUHAOMM").toString();
			responseData.put("QUHAOMM", temp == null ? "" : temp);

			temp = ((Map<String, Object>) providerData.get("data"))
					.get("GUAHAOXH").toString();
			responseData.put("GUAHAOXH", temp == null ? "" : temp);

			temp = ((Map<String, Object>) providerData.get("data"))
					.get("JIUZHENSJ").toString();
			responseData.put("JIUZHENSJ", temp == null ? "" : temp);

			List<Map<String, Object>> fyList = (List<Map<String, Object>>) ((Map<String, Object>) providerData
					.get("data")).get("FEIYONGMX");
			responseData.put("FEIYONGMXLIST", fyList == null ? "" : fyList);
			// System.out.println("registerRequest==> return data" +
			// responseData);
			return responseData;
		} catch (DRServiceException e) {
			logger.error("接收预约挂号请求", e);
			throw new ModelDataOperationException(ServiceCode.CODE_NOT_FOUND,
					e.getMessage());
		} catch (Exception e) {
			logger.error("接收预约挂号请求", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, message);
		}

	}

	/**
	 * @description 检查申请记录
	 */
	public void saveClinicCheckHistory(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doSave("create",
					"phis.application.twr.schemas.DR_ClinicCheckHistory", body,
					false);
		} catch (ValidateException e) {
			logger.error("检查申请记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("检查申请记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * @description 挂号处理记录
	 */
	public void saveClinicRegisterHistory(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doSave("create",
					"phis.application.twr.schemas.DR_CLINICREGISTERHISTORY",
					body, true);
		} catch (ValidateException e) {
			logger.error("挂号处理记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("挂号处理记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * @description 预约挂号记录
	 */
	public void saveClinicRegisterReqHistory(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			dao.doSave("create",
					"phis.application.twr.schemas.DR_CLINICREGISTERREQHISTORY",
					body, false);
		} catch (ValidateException e) {
			logger.error("预约挂号记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("预约挂号记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * @description 删除挂号预约记录
	 */
	public void delClinicRegisterReqHistory(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			dao.doRemove("QUHAOMM", body.get("QUHAOMM"),
					"phis.application.twr.schemas.DR_CLINICREGISTERREQHISTORY");
			dao.doRemove("QUHAOMM", body.get("QUHAOMM"),
					"phis.application.twr.schemas.DR_CLINICZZRECORDHISTORY");
		} catch (PersistentDataOperationException e) {
			logger.error("删除挂号预约记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	/**
	 * @description 删除设备预约记录
	 */
	public void delClinicXxEquipmentHistory(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			dao.doRemove("YUYUESQDBH", body.get("YUYUESQDBH"),
					"phis.application.twr.schemas.DR_ClinicXxEquipmentHistory");
		} catch (PersistentDataOperationException e) {
			logger.error("删除设备预约记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	/**
	 * @description 设备预约详细记录
	 */
	public void saveClinicXxEquipmentHistory(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			dao.doSave("create",
					"phis.application.twr.schemas.DR_ClinicXxEquipmentHistory",
					body, false);
		} catch (ValidateException e) {
			logger.error("设备预约详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("设备预约详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * @description 转诊详细记录
	 */
	public void saveClinicZzRecordHistory(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doSave("create",
					"phis.application.twr.schemas.DR_CLINICZZRECORDHISTORY",
					body, false);
		} catch (ValidateException e) {
			logger.error("转诊详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (Exception e) {
			logger.error("转诊详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * @description 保存挂号或者预约挂号返回的费用明细
	 */
	public void saveClinicFeiYongMxRecord(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Object temp;
		Map<String, Object> saveMap = new HashMap<String, Object>();
		saveMap.put("EMPIID", body.get("EMPIID"));
		saveMap.put("GUAHAOID", body.get("GUAHAOID"));
		saveMap.put("QUHAOMM", body.get("QUHAOMM"));
		saveMap.put("MZHM", body.get("MZHM"));
		try {
			if (body.containsKey("FEIYONGMXLIST")
					&& body.get("FEIYONGMXLIST") != "") {
				List<Map<String, Object>> fyList = (List<Map<String, Object>>) body
						.get("FEIYONGMXLIST");
				for (int i = 0; i < fyList.size(); i++) {
					Map<String, Object> fyMap = fyList.get(i);
					temp = fyMap.get("XIANGMUCDMC");
					saveMap.put("XIANGMUCDMC", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUGLMC");
					saveMap.put("XIANGMUGLMC", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUGG");
					saveMap.put("XIANGMUGG", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUGL");
					saveMap.put("XIANGMUGL", temp == null ? "" : temp);

					temp = fyMap.get("ZILIJE");
					saveMap.put("ZILIJE", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUJX");
					saveMap.put("XIANGMUJX", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUCDDM");
					saveMap.put("XIANGMUCDDM", temp == null ? "" : temp);

					temp = fyMap.get("SHULIANG");
					saveMap.put("SHULIANG", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUDW");
					saveMap.put("XIANGMUDW", temp == null ? "" : temp);

					temp = fyMap.get("SHENGPIBH");
					saveMap.put("SHENGPIBH", temp == null ? "" : temp);

					temp = fyMap.get("YIBAODM");
					saveMap.put("YIBAODM", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUXH");
					saveMap.put("XIANGMUXH", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUXJ");
					saveMap.put("XIANGMUXJ", temp == null ? "" : temp);

					temp = fyMap.get("ZIFEIBZ");
					saveMap.put("ZIFEIBZ", temp == null ? "" : temp);

					temp = fyMap.get("JINE");
					saveMap.put("JINE", temp == null ? "" : temp);

					temp = fyMap.get("ZIFEIJE");
					saveMap.put("ZIFEIJE", temp == null ? "" : temp);

					temp = fyMap.get("ZIFUBL");
					saveMap.put("ZIFUBL", temp == null ? "" : temp);

					temp = fyMap.get("DANJIA");
					saveMap.put("DANJIA", temp == null ? "" : temp);

					temp = fyMap.get("XIANGMUMC");
					saveMap.put("XIANGMUMC", temp == null ? "" : temp);

					temp = fyMap.get("FEIYONGLX");
					saveMap.put("FEIYONGLX", temp == null ? "" : temp);

					temp = fyMap.get("MINGXIXH");
					saveMap.put("MINGXIXH", temp == null ? "" : temp);

					temp = fyMap.get("YIBAOZFBL");
					saveMap.put("YIBAOZFBL", temp == null ? "" : temp);

					temp = fyMap.get("YIBAODJ");
					saveMap.put("YIBAODJ", temp == null ? "" : temp);
					dao.doSave(
							"create",
							"phis.application.drc.schemas.DR_CLINICFEIYONGMXRECORD",
							saveMap, false);
				}
			}

		} catch (ValidateException e) {
			logger.error("转诊详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("转诊详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * @description 保存挂号或者预约挂号返回的费用明细
	 */
	public void saveClinicJieSuanJgRecord(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Object temp;
		Map<String, Object> saveMap = new HashMap<String, Object>();
		saveMap.put("EMPIID", body.get("EMPIID"));
		saveMap.put("GUAHAOID", body.get("GUAHAOID"));
		saveMap.put("MZHM", body.get("MZHM"));
		try {
			if (body.containsKey("FEIYONGMXLIST")
					&& body.get("FEIYONGMXLIST") != "") {
				List<Map<String, Object>> fyList = (List<Map<String, Object>>) body
						.get("JIESUANJGLIST");
				for (int i = 0; i < fyList.size(); i++) {
					Map<String, Object> fyMap = fyList.get(i);
					temp = fyMap.get("ZIFEIJE");
					saveMap.put("ZIFEIJE", temp == null ? "" : temp);

					temp = fyMap.get("ZILIJE");
					saveMap.put("ZILIJE", temp == null ? "" : temp);

					temp = fyMap.get("FEIYONGZE");
					saveMap.put("FEIYONGZE", temp == null ? "" : temp);

					temp = fyMap.get("ZIFUJE");
					saveMap.put("ZIFUJE", temp == null ? "" : temp);

					temp = fyMap.get("DAISHOUJE");
					saveMap.put("DAISHOUJE", temp == null ? "" : temp);

					temp = fyMap.get("YOUHUIJE");
					saveMap.put("YOUHUIJE", temp == null ? "" : temp);

					temp = fyMap.get("YIYUANCDJE");
					saveMap.put("YIYUANCDJE", temp == null ? "" : temp);

					temp = fyMap.get("BAOXIAOJE");
					saveMap.put("BAOXIAOJE", temp == null ? "" : temp);

					temp = fyMap.get("XIANJINZF");
					saveMap.put("XIANJINZF", temp == null ? "" : temp);

					temp = fyMap.get("DONGJIEJE");
					saveMap.put("DONGJIEJE", temp == null ? "" : temp);
					dao.doSave(
							"create",
							"phis.application.drc.schemas.DR_CLINICJIESUANJGRECORD",
							saveMap, false);
				}
			}
		} catch (ValidateException e) {
			logger.error("转诊详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("转诊详细记录", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	/**
	 * 根据系统参数名称获取系统参数值
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getXTCSByCsmc(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();

		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID

		res.put("CSZ", ParameterUtil.getParameter(manaUnitId, "QYSXZZ", ctx));
		// Map<String, Object> params_xtcs = new HashMap<String, Object>();
		// params_xtcs.put("CSMC", body.get("CSMC"));
		// params_xtcs.put("JGID", manaUnitId);
		//
		// String hql_xtcs =
		// "select a.CSZ as CSZ from GY_XTCS a where a.CSMC=:CSMC and a.JGID=:JGID";
		//
		// try {
		// List<Map<String,Object>> list_xtcs =
		// dao.doSqlQuery(hql_xtcs,params_xtcs);
		// res.put("CSZ", list_xtcs.get(0).get("CSZ"));
		// } catch (PersistentDataOperationException e) {
		// throw new ModelDataOperationException("查询系统参数值失败！", e);
		// }
		return res;
	}

	public Map<String, Object> doGetDay(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> res = new HashMap<String, Object>();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		Map<String, Object> dayMap = new HashMap<String, Object>();
		Map<String, Object> rqMap = new HashMap<String, Object>();
		rqMap.put("rq1", matter.format(BSHISUtil.getDateAfter(date, 1)));
		rqMap.put("rq2", matter.format(BSHISUtil.getDateAfter(date, 2)));
		rqMap.put("rq3", matter.format(BSHISUtil.getDateAfter(date, 3)));
		rqMap.put("rq4", matter.format(BSHISUtil.getDateAfter(date, 4)));
		rqMap.put("rq5", matter.format(BSHISUtil.getDateAfter(date, 5)));
		rqMap.put("rq6", matter.format(BSHISUtil.getDateAfter(date, 6)));
		rqMap.put("rq7", matter.format(BSHISUtil.getDateAfter(date, 7)));
		dayMap.put("day3",
				matter.format(BSHISUtil.getDateAfter(date, 3)).substring(5, 7)
						+ "."
						+ matter.format(BSHISUtil.getDateAfter(date, 3))
								.substring(8));
		dayMap.put("day4",
				matter.format(BSHISUtil.getDateAfter(date, 4)).substring(5, 7)
						+ "."
						+ matter.format(BSHISUtil.getDateAfter(date, 4))
								.substring(8));
		dayMap.put("day5",
				matter.format(BSHISUtil.getDateAfter(date, 5)).substring(5, 7)
						+ "."
						+ matter.format(BSHISUtil.getDateAfter(date, 5))
								.substring(8));
		dayMap.put("day6",
				matter.format(BSHISUtil.getDateAfter(date, 6)).substring(5, 7)
						+ "."
						+ matter.format(BSHISUtil.getDateAfter(date, 6))
								.substring(8));
		dayMap.put("day7",
				matter.format(BSHISUtil.getDateAfter(date, 7)).substring(5, 7)
						+ "."
						+ matter.format(BSHISUtil.getDateAfter(date, 7))
								.substring(8));
		res.put("day", day);
		res.put("days", dayMap);
		res.put("rqMap", rqMap);
		return res;
	}

	public Map<String, Object> doGetNextWeek(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> weekMap = new HashMap<String, Object>();
		Map<String, Object> dayMap = new HashMap<String, Object>();
		Map<String, Object> rqMap = new HashMap<String, Object>();
		try {
			Date date1 = matter.parse(body.get("ycrq1") + "");
			Date date2 = matter.parse(body.get("ycrq2") + "");
			Date date3 = matter.parse(body.get("ycrq3") + "");
			Date date4 = matter.parse(body.get("ycrq4") + "");
			Date date5 = matter.parse(body.get("ycrq5") + "");
			Date date6 = matter.parse(body.get("ycrq6") + "");
			Date date7 = matter.parse(body.get("ycrq7") + "");
			dayMap.put(
					"day1",
					matter.format(BSHISUtil.getDateAfter(date1, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date1, 7))
									.substring(8));
			dayMap.put(
					"day2",
					matter.format(BSHISUtil.getDateAfter(date2, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date2, 7))
									.substring(8));
			dayMap.put(
					"day3",
					matter.format(BSHISUtil.getDateAfter(date3, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date3, 7))
									.substring(8));
			dayMap.put(
					"day4",
					matter.format(BSHISUtil.getDateAfter(date4, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date4, 7))
									.substring(8));
			dayMap.put(
					"day5",
					matter.format(BSHISUtil.getDateAfter(date5, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date5, 7))
									.substring(8));
			dayMap.put(
					"day6",
					matter.format(BSHISUtil.getDateAfter(date6, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date6, 7))
									.substring(8));
			dayMap.put(
					"day7",
					matter.format(BSHISUtil.getDateAfter(date7, 7)).substring(
							5, 7)
							+ "."
							+ matter.format(BSHISUtil.getDateAfter(date7, 7))
									.substring(8));
			rqMap.put("rq1", BSHISUtil.getDateAfter(date1, 7));
			rqMap.put("rq2", BSHISUtil.getDateAfter(date2, 7));
			rqMap.put("rq3", BSHISUtil.getDateAfter(date3, 7));
			rqMap.put("rq4", BSHISUtil.getDateAfter(date4, 7));
			rqMap.put("rq5", BSHISUtil.getDateAfter(date5, 7));
			rqMap.put("rq6", BSHISUtil.getDateAfter(date6, 7));
			rqMap.put("rq7", BSHISUtil.getDateAfter(date7, 7));
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date1);
			int day1 = cal1.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day1", day1);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);
			int day2 = cal2.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day2", day2);
			Calendar cal3 = Calendar.getInstance();
			cal3.setTime(date3);
			int day3 = cal3.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day3", day3);
			Calendar cal4 = Calendar.getInstance();
			cal4.setTime(date4);
			int day4 = cal4.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day4", day4);
			Calendar cal5 = Calendar.getInstance();
			cal5.setTime(date5);
			int day5 = cal5.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day5", day5);
			Calendar cal6 = Calendar.getInstance();
			cal6.setTime(date6);
			int day6 = cal6.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day6", day6);
			Calendar cal7 = Calendar.getInstance();
			cal7.setTime(date7);
			int day7 = cal7.get(Calendar.DAY_OF_WEEK);
			weekMap.put("day7", day7);
			res.put("weekMap", weekMap);
			res.put("days", dayMap);
			res.put("rqMap", rqMap);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	public Map<String, Object> doGetBeforWeek(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> weekMap = new HashMap<String, Object>();
		Map<String, Object> dayMap = new HashMap<String, Object>();
		Map<String, Object> rqMap = new HashMap<String, Object>();
		try {
			Date dqdate = new Date();
			Date date1 = matter.parse(body.get("ycrq1") + "");
			Date date2 = matter.parse(body.get("ycrq2") + "");
			Date date3 = matter.parse(body.get("ycrq3") + "");
			Date date4 = matter.parse(body.get("ycrq4") + "");
			Date date5 = matter.parse(body.get("ycrq5") + "");
			Date date6 = matter.parse(body.get("ycrq6") + "");
			Date date7 = matter.parse(body.get("ycrq7") + "");
			int xcday = BSHISUtil.getDifferDays(date1, dqdate);
			if (xcday == 6) {
				this.doGetDay(body, ctx);
				res.put("xcday", 6);
			} else {
				dayMap.put(
						"day1",
						matter.format(BSHISUtil.getDateBefore(date1, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date1, 7))
										.substring(8));
				dayMap.put(
						"day2",
						matter.format(BSHISUtil.getDateBefore(date2, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date2, 7))
										.substring(8));
				dayMap.put(
						"day3",
						matter.format(BSHISUtil.getDateBefore(date3, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date3, 7))
										.substring(8));
				dayMap.put(
						"day4",
						matter.format(BSHISUtil.getDateBefore(date4, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date4, 7))
										.substring(8));
				dayMap.put(
						"day5",
						matter.format(BSHISUtil.getDateBefore(date5, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date5, 7))
										.substring(8));
				dayMap.put(
						"day6",
						matter.format(BSHISUtil.getDateBefore(date6, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date6, 7))
										.substring(8));
				dayMap.put(
						"day7",
						matter.format(BSHISUtil.getDateBefore(date7, 7))
								.substring(5, 7)
								+ "."
								+ matter.format(
										BSHISUtil.getDateBefore(date7, 7))
										.substring(8));
				rqMap.put("rq1", BSHISUtil.getDateBefore(date1, 7));
				rqMap.put("rq2", BSHISUtil.getDateBefore(date2, 7));
				rqMap.put("rq3", BSHISUtil.getDateBefore(date3, 7));
				rqMap.put("rq4", BSHISUtil.getDateBefore(date4, 7));
				rqMap.put("rq5", BSHISUtil.getDateBefore(date5, 7));
				rqMap.put("rq6", BSHISUtil.getDateBefore(date6, 7));
				rqMap.put("rq7", BSHISUtil.getDateBefore(date7, 7));
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				int day1 = cal1.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day1", day1);
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(date2);
				int day2 = cal2.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day2", day2);
				Calendar cal3 = Calendar.getInstance();
				cal3.setTime(date3);
				int day3 = cal3.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day3", day3);
				Calendar cal4 = Calendar.getInstance();
				cal4.setTime(date4);
				int day4 = cal4.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day4", day4);
				Calendar cal5 = Calendar.getInstance();
				cal5.setTime(date5);
				int day5 = cal5.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day5", day5);
				Calendar cal6 = Calendar.getInstance();
				cal6.setTime(date6);
				int day6 = cal6.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day6", day6);
				Calendar cal7 = Calendar.getInstance();
				cal7.setTime(date7);
				int day7 = cal7.get(Calendar.DAY_OF_WEEK);
				weekMap.put("day7", day7);
				res.put("weekMap", weekMap);
				res.put("days", dayMap);
				res.put("rqMap", rqMap);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	public void doQueryBrInfo(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String empiid = body.get("EMPIID").toString();
		Long departmentId = parseLong(body.get("departmentId"));
		Map<String, Object> par = new HashMap<String, Object>();
		Map<String, Object> par1 = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		par.put("EMPIID", empiid);
		par.put("JDJG", user.getManageUnitId());
		par.put("JZKS", departmentId);
		par1.put("PERSONID", user.getUserId());
		String brhql = "select a.BRID as BRID,a.MZHM as MZHM,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,"
				+ " a.SFZH as SFZH,a.LXDH as LXDH,a.LXDZ as LXDZ,b.ZDXH as ZDXH,b.ZDMC as ZDMC,c.ZSXX as ZSXX,c.JZKS as JZKS"
				+ " from MS_BRDA a "
				+ " left join MS_BCJL c on a.BRID=c.BRID and c.JZKS =:JZKS"
				+ " left outer join MS_BRZD b "
				+ " on a.BRID=b.BRID and b.ZZBZ=1 and c.JZKS =:JZKS"
				+ " where a.JDJG=:JDJG and a.EMPIID=:EMPIID order by b.JLBH desc,c.JZXH desc";
		String yshal = "select MOBILE as MOBILE from SYS_Personnel where PERSONID=:PERSONID";
		try {
			List<Map<String, Object>> brlist = dao.doSqlQuery(brhql, par);
			Map<String, Object> ysmap = dao.doLoad(yshal, par1);
			Map<String, Object> brmap = new HashMap<String, Object>();
			if (brlist.size() > 0) {
				brmap = brlist.get(0);
			}
			if (brmap.containsKey("CSNY") && brmap.get("CSNY") != "") {
				Date birthday = matter.parse(brmap.get("CSNY") + "");
				Map<String, Object> ageMap = BSPHISUtil.getPersonAge(birthday,
						new Date());
				brmap.put("AGE", ageMap.get("age"));
			}
			brmap.put("YSDH", ysmap.get("MOBILE"));
			res.put("body", brmap);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void doQueryMzScInfo(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String zhuanzhendh = body.get("ZHUANZHENDH").toString();
		Map<String, Object> par = new HashMap<String, Object>();
		par.put("ZHUANZHENDH", zhuanzhendh);
		par.put("YEWULX", "1");
		String brhql = "select a.EMPIID as EMPIID,a.MZHM as MZHM,a.BINGRENXM as BINGRENXM,a.BINGRENXB as BINGRENXB,"
				+ " a.BINGRENNL as BINGRENNL,a.BINGRENSFZH as BINGRENSFZH,a.BINGRENLXDH as BINGRENLXDH,"
				+ " a.BINGRENLXDZ as BINGRENLXDZ,a.ZHUANZHENZD as ZHUANZHENZD,a.SHENQINGJGMC as SHENQINGJGMC,"
				+ " a.SHENQINGJGLXDH as SHENQINGJGLXDH,c.PERSONNAME as SHENQINGYS,a.SHENQINGYSDH as SHENQINGYSDH,"
				+ " b.JBMC as JBMC,a.ZHUANZHENYY as ZHUANZHENYY,a.BINQINGMS as BINQINGMS,a.ZHUANZHENZYSX as ZHUANZHENZYSX,"
				+ " a.SHENQINGJGMC as SHENQINGJGMC,a.SHENQINGJGLXDH as SHENQINGJGLXDH "
				+ " from DR_CLINICRECORDLHISTORY a,GY_JBBM b,SYS_Personnel c "
				+ " where a.ZHUANZHENDH=:ZHUANZHENDH and a.ZHUANZHENZD=b.JBXH and a.YEWULX=:YEWULX and a.SHENQINGYS=c.PERSONID";
		try {
			Map<String, Object> brmap = dao.doLoad(brhql, par);
			res.put("body", brmap);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doQueryZyScInfo(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String zhuanzhendh = body.get("ZHUANZHENDH").toString();
		Map<String, Object> par = new HashMap<String, Object>();
		par.put("ZHUANZHENDH", zhuanzhendh);
		par.put("YEWULX", "2");
		String brhql = "select a.EMPIID as EMPIID,a.MZHM as MZHM,a.BINGRENXM as BINGRENXM,a.BINGRENXB as BINGRENXB,"
				+ " a.BINGRENNL as BINGRENNL,a.BINGRENSFZH as BINGRENSFZH,a.BINGRENLXDH as BINGRENLXDH,"
				+ " a.BINGRENLXDZ as BINGRENLXDZ,a.ZHUANZHENZD as ZHUANZHENZD,a.SHENQINGJGMC as SHENQINGJGMC,"
				+ " a.SHENQINGJGLXDH as SHENQINGJGLXDH,c.PERSONNAME as SHENQINGYS,a.SHENQINGYSDH as SHENQINGYSDH,"
				+ " b.JBMC as JBMC,a.ZHUANZHENYY as ZHUANZHENYY,a.BINQINGMS as BINQINGMS,a.ZHUANZHENZYSX as ZHUANZHENZYSX,"
				+ " a.SHENQINGJGMC as SHENQINGJGMC,a.SHENQINGJGLXDH as SHENQINGJGLXDH "
				+ " from DR_CLINICRECORDLHISTORY a,GY_JBBM b,SYS_Personnel c  "
				+ " where a.ZHUANZHENDH=:ZHUANZHENDH and a.ZHUANZHENZD=b.JBXH and a.YEWULX=:YEWULX and a.SHENQINGYS=c.PERSONID";
		try {
			Map<String, Object> brmap = dao.doLoad(brhql, par);
			res.put("body", brmap);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doQueryJcScInfoo(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		String jianchasqdh = body.get("JIANCHASQDH").toString();
		Map<String, Object> par = new HashMap<String, Object>();
		Map<String, Object> par1 = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		par.put("JIANCHASQDH", jianchasqdh);
		par1.put("PERSONID", user.getUserId());
		String brhql = "select a.BINGRENXM as BINGRENXM,b.BRXB as BRXB,b.CSNY as CSNY,b.LXDZ as LXDZ,"
				+ " b.LXDH as LXDH,a.BINGRENTZ as BINGRENTZ,a.BINGQINGMS as BINGQINGMS,a.BINGRENSFZH as BINGRENSFZH,"
				+ " a.SONGJIANKSMC as SONGJIANKSMC,a.SONGJIANYS as SONGJIANYS,a.ZHENDUAN as JBMC,a.JIANCHAXMMC as JIANCHAXMMC,a.JIANCHATS as JIANCHATS "
				+ " from DR_CLINICCHECKHISTORY a,MS_BRDA b"
				+ " where a.EMPIID=b.EMPIID and a.JIANCHASQDH=:JIANCHASQDH";
		try {
			Map<String, Object> brmap = dao.doLoad(brhql, par);
			Date birthday;
			birthday = matter.parse(brmap.get("CSNY") + "");
			Map<String, Object> ageMap = BSPHISUtil.getPersonAge(birthday,
					new Date());
			brmap.put("AGE", ageMap.get("age"));
			res.put("body", brmap);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取处方信息
	 * 
	 * @param jzxh
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCFXX(long jzxh, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> cfParameters = new HashMap<String, Object>();
		cfParameters.put("JZXH", jzxh);
		try {
			List<Map<String, Object>> cf01s = dao
					.doSqlQuery(
							"select CFSB as CHUFANGID,DJLY as CHUFANGLY,case CFLX when 3 then 4 end as CHUFANGLX,"
									+ " KFRQ as KAIFANGRQ,'' as BEIZHU,CFTS as CFTS,FYRQ as FYRQ "
									+ " from MS_CF01 where JZXH=:JZXH",
							cfParameters);
			for (int i = 0; i < cf01s.size(); i++) {
				Map<String, Object> cf01 = cf01s.get(i);
				Map<String, Object> cfdParameters = new HashMap<String, Object>();
				cfdParameters.put("CFSB", cf01.get("CHUFANGID"));
				StringBuilder cf02hql = new StringBuilder("select ");
				cf02hql.append(cf01.get("CHUFANGLX"))
						.append(" as FEIYONGLX,b.YPMC as XIANGMUMC,b.YPMC as YAOPINTYM,b.YPMC as YAOPINSPM,"
								+ " c.CDMC as CHANGDIMC,a.YFGG as YAOPINGG,a.YFDW as DANGWEI,a.YPSL as SHULIANG,"
								+ " a.YPYF as PINLV,a.GYTJ as GEIYAOTJ,a.YYTS as YONGYAOTS,a.YCJL as DANCIYL,"
								+ " b.JLDW as YONGLIANGDW,a.PSJG as PISHIJG,")
						.append(cf01.get("CFTS"))
						.append(" as ZHONGCHAOYTS,")
						.append(cf01.get("FYRQ"))
						.append(" as FAYAORQ")
						.append(" from MS_CF02 a,YK_TYPK b,YK_CDDZ c where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.CFSB=:CFSB");
				// System.out.println(cf02h);
				List<Map<String, Object>> cf02s = dao.doSqlQuery(
						cf02hql.toString(), cfdParameters);
				cf01s.get(i).put("{CHUFANGXXMX}", cf02s);
			}
			return cf01s;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取处方信息失败");
		}
	}

	/**
	 * 获取处置信息
	 * 
	 * @param jzxh
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCZXX(long jzxh, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> czParameters = new HashMap<String, Object>();
		czParameters.put("JZXH", jzxh);
		try {
			List<Map<String, Object>> cz01s = dao.doSqlQuery("", czParameters);
			return cz01s;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取处方信息失败");
		}
	}

	public List<Map<String, Object>> getYZXX(long zyh, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> yzParameters = new HashMap<String, Object>();
		yzParameters.put("ZYH", zyh);
		try {
			// YIZHULX 1治疗、2药品、8检验、9检查 GEIYAOTJ 给药途径
			List<Map<String, Object>> cz01s = dao
					.doSqlQuery(
							"select a.JLXH as YIZHUXH,'' as FUYIZXH,case a.XMLX when 1 then 2 when 2 then 2 when 3 then 2 when 7 then 1 when 5 then 8 when 4 then 7 end as YIZHULX,a.YZZH as YIZHUZH,a.YZMC as YIZHUMC,a.KSSJ as KAISHISJ,a.TZSJ as TINGZHISJ,a.YCJL as YICISL,b.JLDW as YONGLIANGDW,a.YZZXSJ as ZHIXINGRQ,a.SYPC as PINGLV,a.YZPB as YIZHULB,c.NAME as KAIDANYS,a.PSJG as PISHIJG,'' as GEIYAOTJ from ZY_BQYZ a,YK_TYPK b,BASE_User c where a.YPXH=b.YPXH and a.YSGH=c.id and a.ZYH=:ZYH and a.XMLX not in (6,8,9,10)",
							yzParameters);
			return cz01s;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取医嘱信息失败");
		}
	}

	public void doGetXTCS(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String sjdorehy = ParameterUtil.getParameter(manaUnitId, "SJDOREHY",
				"0", "病区提交天数启用 0.不启用1.启用", ctx);
		res.put("body", sjdorehy);
	}

	/**
	 * 查询门诊转诊信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryZzInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		parameters.put("SHENQINGJGDM", jgid);
		parameterssize.put("SHENQINGJGDM", jgid);
		parameters.put("YEWULX", "1");
		parameterssize.put("YEWULX", "1");
		String sql = "select a.ID as ID,a.EMPIID as EMPIID,a.MZHM as MZHM,a.STATUS as STATUS,a.JIUZHENKLX as JIUZHENKLX,"
				+ " a.JIUZHENKH as JIUZHENKH,a.YIBAOKLX as YIBAOKLX,a.YIBAOKXX as YIBAOKXX,a.YEWULX,a.BINGRENXM as BINGRENXM,"
				+ " a.BINGRENXB as BINGRENXB,a.BINGRENCSRQ as BINGRENCSRQ,a.BINGRENNL as BINGRENNL,a.BINGRENSFZH as BINGRENSFZH,"
				+ " a.BINGRENLXDH as BINGRENLXDH,a.BINGRENLXDZ as BINGRENLXDZ,a.BINGRENFYLB as BINGRENFYLB,"
				+ " a.SHENQINGJGDM as SHENQINGJGDM,a.SHENQINGJGMC as SHENQINGJGMC,a.SHENQINGJGLXDH as SHENQINGJGLXDH,"
				+ " a.SHENQINGYSDH as SHENQINGYSDH,a.ZHUANZHENYY as ZHUANZHENYY,a.BINQINGMS as BINQINGMS,"
				+ " a.ZHUANZHENZYSX as ZHUANZHENZYSX,a.ZHUANZHENDH as ZHUANZHENDH,a.SHENQINGRQ as SHENQINGRQ,"
				+ " substr(a.ZHUANZHENRQ,0,10) as ZHUANZHENRQ,a.SHENQINGYS as SHENQINGYS,a.ZYH as ZYH,b.ZHUANRUYYMC as ZHUANRUYYMC,"
				+ " b.GUAHAOBC as GUAHAOBC,b.JIUZHENSJ as JIUZHENSJ,b.YIZHOUPBID as YIZHOUPBID,b.DANGTIANPBID as DANGTIANPBID,"
				+ " b.GUAHAOID as GUAHAOID,b.QUHAOMM as QUHAOMM,b.GUAHAOXH as GUAHAOXH,a.ZHUANRUYYDM as ZHUANRUYYDM,b.YISHENGDM as YISHENGDM,a.ZHUANRUKSDM as ZHUANRUKSDM"
				+ " from DR_CLINICRECORDLHISTORY a,DR_CLINICZZRECORDHISTORY b "
				+ " where a.ZHUANZHENDH=b.ZHUANZHENDH and a.SHENQINGJGDM=:SHENQINGJGDM and a.YEWULX=:YEWULX ";
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qurKey = que[2].substring(3, que[2].indexOf("]"));
			String qurValue = que[4].substring(0, que[4].indexOf("]")).trim();
			String qur = "";
			if (qurKey.equals("MZHM") || qurKey.equals("BINGRENXM")
					|| qurKey.equals("BINGRENSFZH")) {
				qur = "and a." + qurKey + " like '" + qurValue + "'";
			} else {
				qur = "and a." + qurKey + " = " + qurValue;
			}
			sql += qur;
		}
		try {
			list = dao.doSqlQuery(sql, parameters);
			listsize = dao.doSqlQuery(sql, parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.twr.schemas.DR_CLINICRECORDLHISTORYLIST");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询住院转诊信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryZyZzInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		parameters.put("SHENQINGJGDM", jgid);
		parameterssize.put("SHENQINGJGDM", jgid);
		parameters.put("YEWULX", "2");
		parameterssize.put("YEWULX", "2");
		String sql = "select a.ID as ID,a.EMPIID as EMPIID,a.MZHM as MZHM,a.STATUS as STATUS,"
				+ " a.JIUZHENKLX as JIUZHENKLX,a.JIUZHENKH as JIUZHENKH,a.YIBAOKLX as YIBAOKLX,"
				+ " a.YIBAOKXX as YIBAOKXX,a.YEWULX,a.BINGRENXM as BINGRENXM,a.BINGRENXB as BINGRENXB,"
				+ " a.BINGRENCSRQ as BINGRENCSRQ,a.BINGRENNL as BINGRENNL,a.BINGRENSFZH as BINGRENSFZH,"
				+ " a.BINGRENLXDH as BINGRENLXDH,a.BINGRENLXDZ as BINGRENLXDZ,a.BINGRENFYLB as BINGRENFYLB,"
				+ " a.SHENQINGJGDM as SHENQINGJGDM,a.SHENQINGJGMC as SHENQINGJGMC,a.SHENQINGJGLXDH as SHENQINGJGLXDH,"
				+ " a.SHENQINGYSDH as SHENQINGYSDH,a.ZHUANZHENYY as ZHUANZHENYY,a.BINQINGMS as BINQINGMS,"
				+ " a.ZHUANZHENZYSX as ZHUANZHENZYSX,a.ZHUANZHENDH as ZHUANZHENDH,a.SHENQINGRQ as SHENQINGRQ,"
				+ " a.ZHUANZHENRQ as ZHUANZHENRQ,a.SHENQINGYS as SHENQINGYS,a.ZYH as ZYH,a.ZHUANRUYYMC as ZHUANRUYYMC,"
				+ " a.ZHUANZHENRQ as JIUZHENSJ from DR_CLINICRECORDLHISTORY a where a.SHENQINGJGDM=:SHENQINGJGDM and a.YEWULX=:YEWULX ";
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qurKey = que[2].substring(3, que[2].indexOf("]"));
			String qurValue = que[4].substring(0, que[4].indexOf("]")).trim();
			String qur = "";
			if (qurKey.equals("BINGRENXM") || qurKey.equals("BINGRENSFZH")) {
				qur = "and a." + qurKey + " like '" + qurValue + "'";
			} else {
				qur = "and a." + qurKey + " = " + qurValue;
			}
			sql += qur;
		}

		try {
			list = dao.doSqlQuery(sql, parameters);
			listsize = dao.doSqlQuery(sql, parameterssize);
			SchemaUtil
					.setDictionaryMassageForList(list,
							"phis.application.twr.schemas.DR_ClinicCheckHistoryHospitalList");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询检查转诊信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryjcZzInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		// parameters.put("SONGJIANKS", jgid);
		// parameterssize.put("SONGJIANKS", jgid);
		String sql = "select a.ID as ID,a.EMPIID as EMPIID,a.MZHM as MZHM,a.ZHENDUAN as ZHENDUAN,a.BINGRENXM as BINGRENXM,a.JIANCHASQDH as JIANCHASQDH,"
				+ " c.BRXB as BINGRENXB,c.CSNY as CSNY,a.BINGRENSFZH as BINGRENSFZH,a.SHENQINGRQ as SHENQINGRQ,"
				+ " a.SONGJIANKSMC as SONGJIANKSMC,"
				+ " a.SONGJIANRQ as SONGJIANRQ,"
				+ " a.STATUS as STATUS,a.BINGRENTZ as BINGRENTZ,a.BINGQINGMS as BINGQINGMS,"
				+ " a.SONGJIANYS as SONGJIANYS,a.JIANCHAXMMC as JIANCHAXMMC  "
				+ " from DR_ClinicCheckHistory a,MS_BRDA c "
				+ " where a.EMPIID=c.EMPIID ";

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qurKey = que[2].substring(3, que[2].indexOf("]"));
			String qurValue = que[4].substring(0, que[4].indexOf("]")).trim();
			String qur = "";
			if (qurKey.equals("MZHM") || qurKey.equals("BINGRENXM")
					|| qurKey.equals("BINGRENSFZH")) {
				qur = " and a." + qurKey + " like '" + qurValue + "'";
			} else if (qurKey.equals("BINGRENXB")) {
				qur = " and c.BRXB =" + qurValue;
			} else {
				qur = " and a." + qurKey + " = " + qurValue;
			}
			sql += qur;
		}

		try {
			sql += " order by a.ID desc";
			list = dao.doSqlQuery(sql, parameters);
			listsize = dao.doSqlQuery(sql, parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.twr.schemas.DR_ClinicCheckHistory");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @description 保存挂号或者预约挂号返回的费用明细
	 */
	public void doSaveHZDJ(Map<String, Object> body, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try {
			dao.doSave("create", "phis.application.twr.schemas.TWR_HZDJ", body,
					false);
		} catch (ValidateException e) {
			logger.error("住院回转登记保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_BUSINESS_DATA_NULL, e.getMessage());
		} catch (PersistentDataOperationException e) {
			logger.error("住院回转登记保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATE_PASE_ERROR, e.getMessage());
		}
	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
}
