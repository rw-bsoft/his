/**
 * @(#)PublicService.java Created on 2012-1-5 上午11:40:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pub.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.accredit.Permission;
import ctd.account.role.Role;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author yangl
 */
public class PermissionsVerifyService extends AbstractActionService implements
		DAOSupportable {

	public void doSaveInitSystemParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		ParameterUtil.initParameters(manageUnit, ContextUtils.getContext());
	}

	@SuppressWarnings("unchecked")
	public void doFilterPermissions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<String> pvs = (List<String>) req.get("body");
		Map<String, Object> PermissionsVerifyResult = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		HashMap<String, Object> prop = null;
		if (httpSession != null) {
			prop = (HashMap<String, Object>) httpSession
					.getAttribute("properties");
			// throw new ServiceException(HttpServletResponse.SC_FORBIDDEN,
			// "当前连接已经失效，请尝试重新登录!");
		}

		if (prop == null) {
			prop = new HashMap<String, Object>();
		}
		String manageUnit = user.getManageUnit().getId();
		Role role = UserRoleToken.getCurrent().getRole();
		for (String pv : pvs) {
			String modulFullId = "phis.application.top.TOP/"
					+ pv.replace(".", "/");
			Permission ar = role.lookupPermission("apps", modulFullId);
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("hasPvs", ar.getMode().isAccessible());
			// 判断是否有对应业务权限
			if (ar.getMode().isAccessible()) {
				if (pv.equals("TOPFUNC.MedicalSwitch")) {// 查询是否有默认科室
					try {
						String YWLB = "9";

						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.OFFICENAME as KSMC,a.MRBZ as MRBZ from GY_QXKZ a,SYS_Office b where a.KSDM=b.ID and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						if (list.size() == 0) {
							obj.put("showWin", "false");
						}
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置默认科室
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("KSMC");
								obj.put("MedicalId", ksdm);
								obj.put("MedicalName", ksmc);
								prop.put("biz_MedicalId", String.valueOf(ksdm));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员!", e);
					}
				}
				if (pv.equals("TOPFUNC.DepartmentSwitch_out")
						|| pv.equals("TOPFUNC.DepartmentSwitch_in")) {// 查询是否有默认科室
					try {
						String YWLB = pv.equals("TOPFUNC.DepartmentSwitch_out") ? "2"
								: "3";

						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.KSMC as KSMC,b.MZKS as MZKS,(select OFFICENAME from SYS_Office where ID=b.MZKS) as MZKSMC,(select ORGANIZTYPE from SYS_Office where ID=b.MZKS) as MZKSLX,a.MRBZ as MRBZ from GY_QXKZ a,MS_GHKS b where a.KSDM=b.KSDM and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						if (list.size() == 0) {
							obj.put("showWin", "false");
						}
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置默认科室
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("KSMC");
								long mzks = (Long) list.get(i).get("MZKS");
								String mzksmc = (String) list.get(i).get(
										"MZKSMC");
								String mzkslx = list.get(i).get("MZKSLX")==null?"":((String) list.get(i).get("MZKSLX"));
								obj.put("departmentType", mzkslx);
								obj.put("departmentId", mzks);
								obj.put("departmentName", mzksmc);
								obj.put("reg_departmentId", ksdm);
								obj.put("reg_departmentName", ksmc);
								prop.put("biz_departmentId",
										String.valueOf(mzks));
								prop.put("reg_departmentId",
										String.valueOf(ksdm));
								// 调用门诊排队科室登录初始化
								// String qymzpd = ParameterUtil.getParameter(
								// manageUnit,
								// BSPHISSystemArgument.QYMZPD, ctx);
								// if (qymzpd != null && qymzpd.equals("1")) {
								// ClinicManageModel cmm = new
								// ClinicManageModel(
								// dao);
								// try {
								// cmm.doSavePdKsxxIn(ctx);
								// } catch (ModelDataOperationException e) {
								// throw new ServiceException(e);
								// }
								// }
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员!", e);
					}
				}
				if (pv.equals("TOPFUNC.PharmacySwitch")) {// 查询是否有默认药房
					try {
						String YWLB = "1";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.YFMC as YFMC,a.MRBZ as MRBZ from GY_QXKZ a,YF_YFLB b where a.KSDM=b.YFSB and b.ZXBZ=0 and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						if (list.size() == 0) {
							obj.put("showWin", "false");
						}
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置药房
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("YFMC");
								obj.put("pharmacyId", ksdm);
								obj.put("pharmacyName", ksmc);
								prop.put("pharmacyId", String.valueOf(ksdm));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员！", e);
					}
				}
				if (pv.equals("TOPFUNC.StoreHouseSwitch")) {// 查询是否有默认药库
					try {
						String YWLB = "5";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.YKMC as YKMC,a.MRBZ as MRBZ from GY_QXKZ a,YK_YKLB b where a.KSDM=b.YKSB and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						if (list.size() == 0) {
							obj.put("showWin", "false");
						}
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置药房
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("YKMC");
								obj.put("storehouseId", ksdm);
								obj.put("storehouseName", ksmc);
								prop.put("storehouseId", String.valueOf(ksdm));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员！", e);
					}
				}
				if (pv.equals("TOPFUNC.TreasurySwitch")) {// 查询是否有默认库房
					try {
						String YWLB = "6";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						if (list.size() == 0) {
							obj.put("showWin", "false");
						}
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置药房
								long ksdm = (Long) list.get(i).get("KSDM");
								String kfmc = (String) list.get(i).get("KFMC");
								long ejkf = 0L;
								if (list.get(i).get("EJKF") != null) {
									ejkf = (Long) list.get(i).get("EJKF");
								}
								int lbxh = (Integer) list.get(i).get("LBXH");
								int kflb = (Integer) list.get(i).get("KFLB");
								int kfzt = (Integer) list.get(i).get("KFZT");
								String kfzb = "";
								if (list.get(i).get("KFZB") != null) {
									kfzb = list.get(i).get("KFZB") + "";
								}
								int glkf = (Integer) list.get(i).get("GLKF");
								int wxkf = (Integer) list.get(i).get("WXKF");
								int ckfs = (Integer) list.get(i).get("CKFS");
								int csbz = (Integer) list.get(i).get("CSBZ");
								int zjbz = (Integer) list.get(i).get("ZJBZ");
								String zjyf = "";
								if (list.get(i).get("ZJYF") != null) {
									zjyf = list.get(i).get("ZJYF") + "";
								}
								int hzpd = (Integer) list.get(i).get("HZPD");
								int pdzt = (Integer) list.get(i).get("PDZT");
								obj.put("treasuryId", ksdm);
								obj.put("treasuryName", kfmc);
								obj.put("treasuryEjkf", ejkf);
								obj.put("treasuryLbxh", lbxh);
								obj.put("treasuryKflb", kflb);
								obj.put("treasuryKfzt", kfzt);
								obj.put("treasuryKfzb", kfzb);
								obj.put("treasuryGlkf", glkf);
								obj.put("treasuryWxkf", wxkf);
								obj.put("treasuryCkfs", ckfs);
								obj.put("treasuryCsbz", csbz);
								obj.put("treasuryZjbz", zjbz);
								obj.put("treasuryZjyf", zjyf);
								obj.put("treasuryHzpd", hzpd);
								obj.put("treasuryPdzt", pdzt);
								prop.put("treasuryId",
										Integer.parseInt(ksdm + ""));
								prop.put("treasuryName", String.valueOf(kfmc));
								prop.put("treasuryEjkf", String.valueOf(ejkf));
								prop.put("treasuryLbxh", String.valueOf(lbxh));
								prop.put("treasuryKflb", String.valueOf(kflb));
								prop.put("treasuryKfzt", String.valueOf(kfzt));
								prop.put("treasuryKfzb", String.valueOf(kfzb));
								prop.put("treasuryGlkf", String.valueOf(glkf));
								prop.put("treasuryWxkf", String.valueOf(wxkf));
								prop.put("treasuryCkfs", String.valueOf(ckfs));
								prop.put("treasuryCsbz", String.valueOf(csbz));
								prop.put("treasuryZjbz", String.valueOf(zjbz));
								prop.put("treasuryZjyf", String.valueOf(zjyf));
								prop.put("treasuryHzpd", String.valueOf(hzpd));
								prop.put("treasuryPdzt", String.valueOf(pdzt));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认库房查询失败，请联系管理员！", e);
					}
				}
				if (pv.equals("TOPFUNC.WardSwitch")) {// 查询是否有默认病区
					try {
						String YWLB = "4";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.OFFICENAME as KSMC,a.MRBZ as MRBZ from GY_QXKZ a,SYS_Office b where a.KSDM=b.ID and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						if (list.size() == 0) {
							obj.put("showWin", "false");
						}
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置病区
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("KSMC");
								obj.put("wardId", ksdm);
								obj.put("wardName", ksmc);
								prop.put("wardId", String.valueOf(ksdm));
								prop.put("wardName", ksmc);
								user.setProperty("wardId", ksdm);
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认病区查询失败，请联系管理员！", e);
					}
				}
			}
			// 只有物流管理的库房使用
			if (pv.equals("TOPFUNC.TreasurySwitch")) {
				if (ar.toString().equals("false")) {
					prop.put("treasuryId", String.valueOf(0));
				}
			}
			PermissionsVerifyResult.put(pv, obj);
		}
		if (httpSession != null) {
			httpSession.setAttribute("properties", prop);
		}
		res.put("pvRet", PermissionsVerifyResult);
		res.put("x-response-code", "200");
		res.put("x-response-msg", "Success");
	}

	@SuppressWarnings("unchecked")
	/**
	 * 后台切换角色登录专用
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadPermissions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<String> pvs = (List<String>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Role role = UserRoleToken.getCurrent().getRole();
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		HashMap<String, Object> prop = null;
		if (httpSession != null) {
			prop = (HashMap<String, Object>) httpSession
					.getAttribute("properties");
		}
		if (prop == null) {
			prop = new HashMap<String, Object>();
		}
		for (String pv : pvs) {
			String modulFullId = "phis.application.top.TOP/"
					+ pv.replace(".", "/");
			Permission ar = role.lookupPermission("apps", modulFullId);
			// Map<String, Object> obj = new HashMap<String, Object>();
			// obj.put("hasPvs", ar.getMode().isAccessible());
			// 判断是否有对应业务权限
			//if (ar.getMode().isAccessible()) {
				if (pv.equals("TOPFUNC.MedicalSwitch")) {// 查询是否有默认科室
					try {
						String YWLB = "9";

						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.OFFICENAME as KSMC,a.MRBZ as MRBZ from GY_QXKZ a,SYS_Office b where a.KSDM=b.ID and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置默认科室
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("KSMC");
								res.put("MedicalId", ksdm);
								res.put("MedicalName", ksmc);
								prop.put("biz_MedicalId", String.valueOf(ksdm));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员!", e);
					}
				}
				if (pv.equals("TOPFUNC.DepartmentSwitch_out")
						|| pv.equals("TOPFUNC.DepartmentSwitch_in")) {// 查询是否有默认科室
					try {
						String YWLB = pv.equals("TOPFUNC.DepartmentSwitch_out") ? "2"
								: "3";

						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.KSMC as KSMC,b.MZKS as MZKS,(select OFFICENAME from SYS_Office where ID=b.MZKS) as MZKSMC,a.MRBZ as MRBZ from GY_QXKZ a,MS_GHKS b where a.KSDM=b.KSDM and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置默认科室
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("KSMC");
								long mzks = (Long) list.get(i).get("MZKS");
								String mzksmc = (String) list.get(i).get(
										"MZKSMC");
								res.put("departmentId", mzks);
								res.put("departmentName", mzksmc);
								res.put("reg_departmentId", ksdm);
								res.put("reg_departmentName", ksmc);
								prop.put("biz_departmentId",
										String.valueOf(mzks));
								prop.put("reg_departmentId",
										String.valueOf(ksdm));
								// 调用门诊排队科室登录初始化
								// String qymzpd = ParameterUtil.getParameter(
								// manageUnit,
								// BSPHISSystemArgument.QYMZPD, ctx);
								// if (qymzpd != null && qymzpd.equals("1")) {
								// ClinicManageModel cmm = new
								// ClinicManageModel(
								// dao);
								// try {
								// cmm.doSavePdKsxxIn(ctx);
								// } catch (ModelDataOperationException e) {
								// throw new ServiceException(e);
								// }
								// }
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员!", e);
					}
				}
				if (pv.equals("TOPFUNC.PharmacySwitch")) {// 查询是否有默认药房
					try {
						String YWLB = "1";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.YFMC as YFMC,a.MRBZ as MRBZ from GY_QXKZ a,YF_YFLB b where a.KSDM=b.YFSB and b.ZXBZ=0 and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置药房
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("YFMC");
								res.put("pharmacyId", ksdm);
								res.put("pharmacyName", ksmc);
								prop.put("pharmacyId", String.valueOf(ksdm));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员！", e);
					}
				}
				if (pv.equals("TOPFUNC.StoreHouseSwitch")) {// 查询是否有默认药库
					try {
						String YWLB = "5";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.YKMC as YKMC,a.MRBZ as MRBZ from GY_QXKZ a,YK_YKLB b where a.KSDM=b.YKSB and b.SYBZ!=0 and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置药房
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("YKMC");
								res.put("storehouseId", ksdm);
								res.put("storehouseName", ksmc);
								prop.put("storehouseId", String.valueOf(ksdm));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认科室查询失败，请联系管理员！", e);
					}
				}
				if (pv.equals("TOPFUNC.TreasurySwitch")) {// 查询是否有默认库房
					try {
						String YWLB = "6";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置药房
								long ksdm = (Long) list.get(i).get("KSDM");
								String kfmc = (String) list.get(i).get("KFMC");
								long ejkf = 0L;
								if (list.get(i).get("EJKF") != null) {
									ejkf = (Long) list.get(i).get("EJKF");
								}
								int lbxh = (Integer) list.get(i).get("LBXH");
								int kflb = (Integer) list.get(i).get("KFLB");
								int kfzt = (Integer) list.get(i).get("KFZT");
								String kfzb = "";
								if (list.get(i).get("KFZB") != null) {
									kfzb = list.get(i).get("KFZB") + "";
								}
								int glkf = (Integer) list.get(i).get("GLKF");
								int wxkf = (Integer) list.get(i).get("WXKF");
								int ckfs = (Integer) list.get(i).get("CKFS");
								int csbz = (Integer) list.get(i).get("CSBZ");
								int zjbz = (Integer) list.get(i).get("ZJBZ");
								String zjyf = "";
								if (list.get(i).get("ZJYF") != null) {
									zjyf = list.get(i).get("ZJYF") + "";
								}
								int hzpd = (Integer) list.get(i).get("HZPD");
								int pdzt = (Integer) list.get(i).get("PDZT");
								res.put("treasuryId", ksdm);
								res.put("treasuryName", kfmc);
								res.put("treasuryEjkf", ejkf);
								res.put("treasuryLbxh", lbxh);
								res.put("treasuryKflb", kflb);
								res.put("treasuryKfzt", kfzt);
								res.put("treasuryKfzb", kfzb);
								res.put("treasuryGlkf", glkf);
								res.put("treasuryWxkf", wxkf);
								res.put("treasuryCkfs", ckfs);
								res.put("treasuryCsbz", csbz);
								res.put("treasuryZjbz", zjbz);
								res.put("treasuryZjyf", zjyf);
								res.put("treasuryHzpd", hzpd);
								res.put("treasuryPdzt", pdzt);
								prop.put("treasuryId",
										Integer.parseInt(ksdm + ""));
								prop.put("treasuryName", String.valueOf(kfmc));
								prop.put("treasuryEjkf", String.valueOf(ejkf));
								prop.put("treasuryLbxh", String.valueOf(lbxh));
								prop.put("treasuryKflb", String.valueOf(kflb));
								prop.put("treasuryKfzt", String.valueOf(kfzt));
								prop.put("treasuryKfzb", String.valueOf(kfzb));
								prop.put("treasuryGlkf", String.valueOf(glkf));
								prop.put("treasuryWxkf", String.valueOf(wxkf));
								prop.put("treasuryCkfs", String.valueOf(ckfs));
								prop.put("treasuryCsbz", String.valueOf(csbz));
								prop.put("treasuryZjbz", String.valueOf(zjbz));
								prop.put("treasuryZjyf", String.valueOf(zjyf));
								prop.put("treasuryHzpd", String.valueOf(hzpd));
								prop.put("treasuryPdzt", String.valueOf(pdzt));
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认库房查询失败，请联系管理员！", e);
					}
				}
				if (pv.equals("TOPFUNC.WardSwitch")) {// 查询是否有默认病区
					try {
						String YWLB = "4";
						List<Map<String, Object>> list = dao
								.doQuery(
										"select a.KSDM as KSDM,b.OFFICENAME as KSMC,a.MRBZ as MRBZ from GY_QXKZ a,SYS_Office b where a.KSDM=b.ID and a.YGDM='"
												+ user.getUserId()
												+ "' and a.JGID='"
												+ manageUnit
												+ "' and a.YWLB='" + YWLB + "'",
										null);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).get("MRBZ") != null
									&& list.get(i).get("MRBZ").toString()
											.equals("1")) {
								// 有设置病区
								long ksdm = (Long) list.get(i).get("KSDM");
								String ksmc = (String) list.get(i).get("KSMC");
								res.put("wardId", ksdm);
								res.put("wardName", ksmc);
								prop.put("wardId", String.valueOf(ksdm));
								prop.put("wardName", ksmc);
								// user.setProperty("wardId", ksdm);
							}
						}
					} catch (PersistentDataOperationException e) {
						throw new ServiceException("默认病区查询失败，请联系管理员！", e);
					}
				}
			//}
			// 只有物流管理的库房使用
			if (pv.equals("TOPFUNC.TreasurySwitch")) {
				if (ar.toString().equals("false")) {
					prop.put("treasuryId", String.valueOf(0));
				}
			}
		}
		for (String key : prop.keySet()) {
			user.setProperty(key, prop.get(key));
		}
		if (httpSession != null) {
			httpSession.setAttribute("properties", prop);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveDefaultDepartment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		HashMap<String, Object> prop = null;
		if (httpSession != null) {
			prop = (HashMap<String, Object>) httpSession
					.getAttribute("properties");
		}
		if (prop == null) {
			prop = new HashMap<String, Object>();
		}
		try {
			String YWLB = (String) body.get("YWLB");
			Long KSDM = Long.parseLong(body.get("KSDM").toString());
			String manageUnit = user.getManageUnit().getId();
			dao.doUpdate("update GY_QXKZ set MRBZ=0 where YWLB='" + YWLB
					+ "' and YGDM='" + user.getUserId() + "' and JGID='"
					+ manageUnit + "'", null);
			dao.doUpdate("update GY_QXKZ set MRBZ=1 where YWLB='" + YWLB
					+ "' and YGDM='" + user.getUserId() + "' and JGID='"
					+ manageUnit + "' and KSDM=" + KSDM, null);
			if (YWLB.equals("1")) {// 药房
				retMap = dao.doLoad(BSPHISEntryNames.YF_YFLB, KSDM);
				prop.put("pharmacyId", String.valueOf(retMap.get("YFSB")));

			} else if (YWLB.equals("2")) {// 门诊
				Map<String, Object> ghks = dao.doLoad(BSPHISEntryNames.MS_GHKS,
						KSDM);
				retMap = dao.doLoad(BSPHISEntryNames.SYS_Office,
						ghks.get("MZKS"));
				prop.put("biz_departmentId", String.valueOf(retMap.get("ID")));
				prop.put("reg_departmentId", String.valueOf(KSDM));
				prop.put("DDDM",
						ghks.get("DDDM") == null ? "" : ghks.get("DDDM")
								.toString());
				prop.put("DDXX",
						ghks.get("DDXX") == null ? "" : ghks.get("DDXX")
								.toString());
				retMap.put("REG_KSDM", KSDM);
				retMap.put("REG_KSMC", ghks.get("KSMC"));
				// 调用门诊排队科室登录初始化
				// String qymzpd = ParameterUtil.getParameter(manageUnit,
				// BSPHISSystemArgument.QYMZPD, ctx);
				// if (qymzpd != null && qymzpd.equals("1")) {
				// ClinicManageModel cmm = new ClinicManageModel(dao);
				// try {
				// cmm.doSavePdKsxxIn(ctx);
				// } catch (ModelDataOperationException e) {
				// throw new ServiceException(e);
				// }
				// }
			} else if (YWLB.equals("4")) {// 门诊
				Map<String, Object> ksdm = dao.doLoad(
						BSPHISEntryNames.SYS_Office, KSDM);
				prop.put("wardId", String.valueOf(ksdm.get("ID")));
				retMap.put("REG_KSDM", KSDM);
				retMap.put("REG_KSMC", ksdm.get("OFFICENAME"));
			} else if (YWLB.equals("5")) {// 药库
				retMap = dao.doLoad(BSPHISEntryNames.YK_YKLB, KSDM);
				prop.put("storehouseId", String.valueOf(retMap.get("YKSB")));

			} else if (YWLB.equals("6")) {// 库房
				retMap = dao.doLoad(BSPHISEntryNames.WL_KFXX, KSDM);
				prop.put("treasuryName", String.valueOf(retMap.get("KFMC")));
				if (retMap.get("EJKF") != null) {
					prop.put("treasuryEjkf", String.valueOf(retMap.get("EJKF")));
				} else {
					prop.put("treasuryEjkf", String.valueOf(0));
				}
				prop.put("treasuryId",
						Integer.parseInt(String.valueOf(retMap.get("KFXH"))));
				prop.put("treasuryLbxh", String.valueOf(retMap.get("LBXH")));
				prop.put("treasuryKflb", String.valueOf(retMap.get("KFLB")));
				prop.put("treasuryKfzt", String.valueOf(retMap.get("KFZT")));
				if (retMap.get("KFZB") != null) {
					prop.put("treasuryKfzb", String.valueOf(retMap.get("KFZB")));
				} else {
					prop.put("treasuryKfzb", String.valueOf(""));
				}
				prop.put("treasuryGlkf", String.valueOf(retMap.get("GLKF")));
				prop.put("treasuryWxkf", String.valueOf(retMap.get("WXKF")));
				prop.put("treasuryCkfs", String.valueOf(retMap.get("CKFS")));
				prop.put("treasuryCsbz", String.valueOf(retMap.get("CSBZ")));
				prop.put("treasuryZjbz", String.valueOf(retMap.get("ZJBZ")));
				if (retMap.get("ZJYF") != null) {
					prop.put("treasuryZjyf", String.valueOf(retMap.get("ZJYF")));
				} else {
					prop.put("treasuryZjyf", String.valueOf(""));
				}
				prop.put("treasuryHzpd", String.valueOf(retMap.get("HZPD")));
				prop.put("treasuryPdzt", String.valueOf(retMap.get("PDZT")));
			} else if (YWLB.equals("9")) {// 医技科室
				retMap = dao.doLoad(BSPHISEntryNames.SYS_Office, KSDM);
				prop.put("biz_MedicalId", String.valueOf(retMap.get("KSDM")));
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("未知错误，请联系管理员！", e);
		}
		if (retMap.get("EJKF") == null) {
			retMap.put("EJKF", 0);
		}
		for (String key : prop.keySet()) {
			user.setProperty(key, prop.get(key));
		}
		if (httpSession != null) {
			httpSession.setAttribute("properties", prop);
		}
		res.put("ksxx", retMap);
		res.put("x-response-code", "200");
		res.put("x-response-msg", "Success");
	}
}
