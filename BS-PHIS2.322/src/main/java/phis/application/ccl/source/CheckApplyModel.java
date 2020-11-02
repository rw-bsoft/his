package phis.application.ccl.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.service.remind.RemindServer;

import phis.application.ccl.source.QybrDaoImpl;
import phis.application.gp.source.GeneralPractitionersModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.service.KeyCreator;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

public class CheckApplyModel {
	private final int ECG = 1;
	private final int RADIATE = 2;
	private final int BC = 3;
	private final int WCJ = 4;
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(CheckApplyModel.class);

	public CheckApplyModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 删除检查申请的对应关系(三种情况的删除)
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void removeCheckApplyRelation(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();  
		String jgid = user.getManageUnit().getId();// 用户的MC  
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		// 用object主要为了方便后面判断是否为空
		Object lbid = body.get("lbid");// 前端获取的类别id
		Object bwid = body.get("bwid");// 前端获取的部位id
		Object xmid = body.get("xmid");// 前端获取的项目id
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer removeHql = new StringBuffer();
		StringBuffer removeFeeBoundHql = new StringBuffer();
		removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_XMDY+ " where LBID=:LBID");
		removeFeeBoundHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_SFMX+ " where JGID=:JGID and LBID=:LBID");
		parameters.put("LBID", Long.parseLong(lbid + ""));
		// 删除部位和项目时，bwid不为空
		if (bwid != null) {
			removeHql.append(" and BWID=:BWID ");
			removeFeeBoundHql.append(" and BWID=:BWID ");
			parameters.put("BWID", Long.parseLong(bwid + ""));
		}
		// 删除项目时，xmid不为空
		if (xmid != null) {
			removeHql.append(" and XMID=:XMID");
			removeFeeBoundHql.append(" and XMID=:XMID");
			parameters.put("XMID", Long.parseLong(xmid + ""));
		}
		try {
			dao.doUpdate(removeHql.toString(), parameters);
			parameters.put("JGID", jgid);
			dao.doUpdate(removeFeeBoundHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除检查申请对应关系失败", e);
		}
		ss.getTransaction().commit();
	}

	/**
	 * 保存检查申请的对应关系
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveCheckApplyRelation(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ValidateException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		UserRoleToken user = UserRoleToken.getCurrent();  
		String jgid = user.getManageUnit().getId();// 用户的MC  
		List<Map<String, Object>> list = (List<Map<String, Object>>) body
				.get("list");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			for (int i = 0; i < list.size(); i++) {
				parameters.clear();
				Long lbid = Long.parseLong(list.get(i).get("LBID") + "");
				Long bwid = Long.parseLong(list.get(i).get("BWID") + "");
				Long xmid = Long.parseLong(list.get(i).get("XMID") + "");
				
				parameters.put("LBID", lbid);
				parameters.put("BWID", bwid);
				parameters.put("XMID", xmid);
				parameters.put("JGID", jgid);
				// 保存的时候，若记录在表里已存在，则不做操作
				long count = dao.doCount("YJ_JCSQ_XMDY",
						"LBID=:LBID and BWID=:BWID and XMID=:XMID and JGID=:JGID", parameters);
				if (count == 0) {
					// 若不存在，插入一条新的记录
					dao.doInsert(BSPHISEntryNames.YJ_JCSQ_XMDY, parameters,
							false);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存检查申请对应关系失败", e);
		}
		ss.getTransaction().commit();

	}

	/**
	 * 根据类别获得检查申请的部位信息(因为要去重不得新写后台)
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void getCheckPaintList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("LBID", Long.parseLong(req.get("lbid") + ""));
			UserRoleToken user = UserRoleToken.getCurrent();  
			String jgid = user.getManageUnit().getId();// 用户的MC  
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct a.lbid as LBID,b.bwid as BWID,b.bwmc as BWMC,b.pydm as PYDM from yj_jcsq_xmdy a,yj_jcsq_jcbw b "
					+ "where a.lbid=:LBID and a.bwid=b.bwid and a.jgid=:jgid");
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int first = 0;
			if (req.containsKey("pageNo")) {
				first = (Integer) req.get("pageNo") - 1;
			}
			parameters.put("jgid", jgid);
			long total = dao.doSqlQuery(hql.toString(), parameters).size();
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			res.put("body", dao.doSqlQuery(hql.toString(), parameters));
		} catch (PersistentDataOperationException e) {
			logger.error("根据类别获得检查申请的部位信息失败", e);
			throw new ModelDataOperationException("根据类别获得检查申请的部位信息失败!", e);
		}
	}

	/**
	 * 删除检查维护信息
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void removeCheckApplyWH(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		UserRoleToken user = UserRoleToken.getCurrent();  
		String jgid = user.getManageUnit().getId();// 用户的MC  
		Object lbid = body.get("lbid");
		Object bwid = body.get("bwid");
		Object xmid = body.get("xmid");
		StringBuffer removeHql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if (lbid != null) {
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_JCLB
						+ " where LBID=:LBID");
				parameters.put("LBID", Long.parseLong(lbid + ""));
				dao.doUpdate(removeHql.toString(), parameters);
				removeHql.setLength(0);
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_XMDY
						+ " where LBID=:LBID");
				dao.doUpdate(removeHql.toString(), parameters);
				removeHql.setLength(0);
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_SFMX
						+ " where LBID=:LBID and JGID=:JGID");
				parameters.put("JGID", jgid);
				dao.doUpdate(removeHql.toString(), parameters);
			} else if (bwid != null) {
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_JCBW
						+ " where BWID=:BWID");
				parameters.put("BWID", Long.parseLong(bwid + ""));
				dao.doUpdate(removeHql.toString(), parameters);
				removeHql.setLength(0);
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_XMDY
						+ " where BWID=:BWID");
				dao.doUpdate(removeHql.toString(), parameters);
				removeHql.setLength(0);
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_SFMX
						+ " where BWID=:BWID and JGID=:JGID");
				parameters.put("JGID", jgid);
				dao.doUpdate(removeHql.toString(), parameters);
			} else if (xmid != null) {
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_JCXM
						+ " where XMID=:XMID");
				parameters.put("XMID", Long.parseLong(xmid + ""));
				dao.doUpdate(removeHql.toString(), parameters);
				removeHql.setLength(0);
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_XMDY
						+ " where XMID=:XMID");
				dao.doUpdate(removeHql.toString(), parameters);
				removeHql.setLength(0);
				removeHql.append("delete from " + BSPHISEntryNames.YJ_JCSQ_SFMX
						+ " where XMID=:XMID and JGID=:JGID");
				parameters.put("JGID", jgid);
				dao.doUpdate(removeHql.toString(), parameters);
			}
			ss.getTransaction().commit();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除检查维护信息失败!", e);
		} catch (Exception e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除检查维护信息失败!", e);
		}

	}

	/**
	 * 验证是否存在业务数据，存在则不可删除和修改字典
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void checkBusinessData(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Object lbid = body.get("lbid");
		Object bwid = body.get("bwid");
		Object xmid = body.get("xmid");
		Map<String, Object> parameters = new HashMap<String, Object>();
		long count = 0;
		try {
			if (lbid != null) {
				parameters.put("LBID", Long.parseLong(lbid+""));
				count = dao.doCount(BSPHISEntryNames.YJ_JCSQ_KD02, "LBID=:LBID",parameters);
			} else if (bwid != null) {
				parameters.put("BWID", Long.parseLong(bwid+""));
				count = dao.doCount(BSPHISEntryNames.YJ_JCSQ_KD02, "BWID=:BWID",parameters);
			} else if (xmid != null) {
				parameters.put("XMID", Long.parseLong(xmid+""));
				count = dao.doCount(BSPHISEntryNames.YJ_JCSQ_KD02, "XMID=:XMID",parameters);
			}
			if(count>0){
				res.put("hasData", true);
			}else{
				res.put("hasData", false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("验证是否存在业务数据失败", e);
		}
	}

	/**
	 * 删除检查申请的项目与组套绑定信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void removeCheckApplyFeeDetails(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		String jgid = body.get("jgid") + "";// 前端获取的机构id
		Long lbid = Long.parseLong(body.get("lbid") + "");// 前端获取的类别id
		Long bwid = Long.parseLong(body.get("bwid") + "");// 前端获取的部位id
		Long xmid = Long.parseLong(body.get("xmid") + "");// 前端获取的项目id
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("LBID", lbid);
		parameters.put("BWID", bwid);
		parameters.put("XMID", xmid);
		StringBuffer removeHql = new StringBuffer();
		removeHql
				.append("delete from "
						+ "YJ_JCSQ_SFMX"
						+ " where LBID=:LBID and BWID=:BWID and XMID=:XMID and JGID=:JGID");
		try {
			dao.doUpdate(removeHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("删除检查申请项目与组套的关系失败", e);
		}
		ss.getTransaction().commit();
	}

	/**
	 * 保存检查申请的项目与组套绑定信息
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveCheckApplyFeeDetails(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		List<Map<String, Object>> list = (List<Map<String, Object>>) body
				.get("list");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			for (int i = 0; i < list.size(); i++) {
				parameters.clear();
				String jgid = list.get(i).get("JGID") + "";
				Long lbid = Long.parseLong(list.get(i).get("LBID") + "");
				Long bwid = Long.parseLong(list.get(i).get("BWID") + "");
				Long xmid = Long.parseLong(list.get(i).get("XMID") + "");
				Long ztbh = Long.parseLong(list.get(i).get("ZTBH") + "");
				parameters.put("LBID", lbid);
				parameters.put("BWID", bwid);
				parameters.put("XMID", xmid);
				parameters.put("JGID", jgid);
				parameters.put("ZTBH", ztbh);

				// 保存的时候，若记录在表里已存在，则不做操作
				long count = dao
						.doCount(
								"YJ_JCSQ_SFMX",
								"LBID=:LBID and BWID=:BWID and XMID=:XMID and JGID=:JGID and ZTBH=:ZTBH",
								parameters);
				if (count == 0) {
					// 若不存在，插入一条新的记录
					dao.doInsert(BSPHISEntryNames.YJ_JCSQ_SFMX, parameters,
							false);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存检查申请项目与组套绑定信息失败", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("保存检查申请项目与组套绑定信息失败", e);
		}
		ss.getTransaction().commit();
	}
    	//根据名称获取省编码
	private String getSBMByName(String name) throws PersistentDataOperationException {
		//根据名称获取省编码
		String dyHql = "select SBM as SBM from GY_YLSF where FYMC=:name";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", name);
		List<Map<String, Object>> dy = dao.doSqlQuery(dyHql, parameters);
		if (dy != null&&dy.size()>0) {
			Map<String, Object> map = dy.get(0);
			return map.get("SBM") +"";
		}
		return "";
	}

	/**
	 * 检查提醒
	 */
	@SuppressWarnings(value = {"unchecked", "unused"})
	public void SendMsg(Map<String, Object> req, Map<String, Object> res, Context ctx) throws PersistentDataOperationException {
		String cfddm = req.get("cfddm") +"";
		String kh = req.get("kh")+"";
		String manageUnit = (String) req.get("manageUnit");//机构
		String YYKSBM = req.get("YYKSBM") +"";//科室编码
		String YYYSGH = req.get("YYYSGH") +"";//医生工号
		String YSXM = req.get("YSXM") +"";//姓名
		String AGENTIP =  req.get("AGENTIP")+"";//ip

		/** 查询医疗机构代码 */
		String dyHql = "select jgdm_dr as YLJGDM from ehr_manageunit where manageunitid='" + manageUnit + "'";
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> dy = dao.doSqlLoad(dyHql, parameters);
		String yljgdm = "";
		if (dy != null) {
			yljgdm = (String) dy.get("YLJGDM");
		}
		//获取list数据
		List<Map<String, Object>> list = (List<Map<String, Object>>) req
				.get("list");
		StringBuffer sb = new StringBuffer();
		//list.get("BWMC") 检查部位中文
//        list.get("FYMC") 检查项目名称
		for (Map<String, Object> map : list) {
			//检查部位编码
			String sbm = getSBMByName((String) map.get("FYMC"));
			String JCBW = (String) map.get("BWMC");
			String sxml =
					"    <ITEM>                                         " +
							"<FWLB>3</FWLB>\n" +
							"      <BMLX>" + sbm + "</BMLX>\n" +
							"      <YBXMDM>" + "ddd" + "</YBXMDM>\n" +
							"      <JCBWBM>检查部位编码</JCBWBM>  \n" +
							"      <JCBW>" + JCBW + "</JCBW>\n" +
							"    </ITEM>              ";
			sb.append(sxml);
		}

		String postxml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
				"<YL_ACTIVE_ROOT>\n" +
				"  <CFDDM>3103</CFDDM>           \n" +
				"  <KH>" + kh + "</KH>\n" +
				"  <KLX>2</KLX>           \n" +
				"  <YLJGDM>" + yljgdm + "</YLJGDM>\n" +
				"  <JZLX>100</JZLX>\n" +
				"  <YYKSBM>" + YYKSBM + "</YYKSBM>\n" +
				"  <YYYSGH>" + YYYSGH + "</YYYSGH>\n" +
				"  <YSXM>" + YYYSGH + "</YSXM>\n" +
				"  <AGENTIP>10.1.7.11</AGENTIP>\n" +
				"  <AGENTMAC>ff-ff-ff-ff-ff</AGENTMAC>\n" +
				"  <FWXM> \n" +
				sb.toString() +


				"  </FWXM>         \n" +
				"</YL_ACTIVE_ROOT>";

		RemindServer.sendMsgToRemind(postxml, AGENTIP);

	}
	/**
	 * 获得检查申请的项目与组套绑定信息,即收费明细
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void getCheckApplyFeeDetailsInfo(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();  
		String jgid = user.getManageUnit().getId();// 用户的MC 
		//String topjgid=ParameterUtil.getTopUnitId();
		try {
			Long lbid = Long.parseLong(body.get("lbid") + "");// 前端获取的类别id
			Long bwid = Long.parseLong(body.get("bwid") + "");// 前端获取的部位id
			Long xmid = Long.parseLong(body.get("xmid") + "");// 前端获取的项目id
//			Map<String, Object> parameter = new HashMap<String, Object>();
//			parameter.put("JGID", jgid);
//			parameter.put("LBID", lbid);
//			parameter.put("BWID", bwid);
//			parameter.put("XMID", xmid);
//			StringBuffer UpdateHql = new StringBuffer();
//			UpdateHql
//					.append("Update "
//							+ "YJ_JCSQ_SFMX set JGID=:JGID"
//							+ " where LBID=:LBID and BWID=:BWID and XMID=:XMID and JGID=:JGID");
//			dao.doUpdate(UpdateHql.toString(), parameter);
			StringBuffer hql = new StringBuffer();
			hql.append("select d.xmbh as XMBH,trim(d.xmmc) as XMMC,d.xmsl as XMSL,b.fydj as FYDJ,d.xmsl*b.fydj as FYZJ,e.FYDW as FYDW,e.FYXH as FYXH from yj_jcsq_sfmx a,gy_ylmx b,"
					+ "ys_mz_zt01 c,ys_mz_zt02 d,gy_ylsf e where a.JGID=:JGID and a.LBID=:LBID and a.BWID=:BWID and a.XMID=:XMID and a.ZTBH=c.ZTBH"
					+ " and c.ZTBH=d.ZTBH and d.XMBH=b.FYXH and b.JGID=:JGID and c.ZTLB=4  and b.FYXH=E.FYXH");
			Map<String, Object> parameters = new HashMap<String, Object>();
			//parameters.put("JGID", "33010500204".equals(jgid)?jgid:jgid.substring(0, 9));//特殊处理 若为湖墅分中心，取自己的，若为其他中心或站点，取中心收费价格
			parameters.put("JGID",jgid);
			//parameters.put("JGID2",topjgid);
			parameters.put("LBID", lbid);
			parameters.put("BWID", bwid);
			parameters.put("XMID", xmid);
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			if (list.size() > 0) {
				Map<String, Object> req = new HashMap<String, Object>();
				req.put("body",body);
				List<Map<String, Object>> gplist = new GeneralPractitionersModel(dao).queryGpDetil(req);
				for (Map<String, Object> map : list) {
					boolean isexists = false;
					for (Map<String, Object> gpmap : gplist) {
						if((gpmap.get("FYXH") + "").equals(map.get("FYXH")+"")){
							isexists = true;
							break;
						}
					}
					if(isexists){
						map.put("JYBS","签");
					}else{
						map.put("JYBS","");
					}
				}
				res.put("list", list);
			} else {
				res.put("list", null);
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("获得检查申请的项目与组套绑定信息失败", e);
		}
	}

	/**
	 * 提交检查申请项目_门诊
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ControllerException 
	 */
	@SuppressWarnings(value = { "unchecked", "unused" })
	public void commitCheckApplyProject_CIC(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ControllerException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		UserRoleToken user = UserRoleToken.getCurrent();  
		String jgid = user.getManageUnit().getId();// 用户的MC  
		//String topjgid = user.getManageUnit().
		body.put("jgid", jgid);
		// 前台传过来的数据
		List<Map<String, Object>> list = (List<Map<String, Object>>) body
				.get("list");
		String mzhm = body.get("mzhm") + "";// 门诊号码
		int brid = Integer.parseInt(body.get("brid") + "");// 病人ID
		String brxm = body.get("brxm") + "";// 病人姓名
		String clinicId = body.get("clinicId") + "";// 就诊序号
		String brxb = body.get("brxb") + "";// 病人性别
		String brxb_text = body.get("brxb_text") + "";// 病人性别_文本
		String birthday = body.get("birthday") + "";// 生日
		String address = body.get("address") + "";// 地址
		String phoneNumber = body.get("phoneNumber") + "";// 联系方式
		String ysdm = body.get("ysdm") + "";// 医生代码
		Map mapx=user.getProperties();
		String ZXKS=(String) mapx.get("biz_departmentId");
		
		String ysxm = body.get("ysxm") + "";// 医生姓名
		String ksdm = ZXKS;// 科室代码
		body.put("ksdm", ksdm);
		String ksmc = body.get("ksmc") + "";// 科室名称
		String lczd = body.get("lczd") + "";// 临床诊断
		// String bgms = body.get("bgms") + "";// 病情描述

		try {
			/**
			 * 将前台传过来的list分类，对于不同的类型存入不同的医技执行单(ms_yj01有字段执行判别，
			 * 同一类型的可以统一执行完毕修改判别的存一张单子),并且做不同接口
			 **/
			List<Map<String, Object>> ecgList = new ArrayList<Map<String, Object>>();// 心电
			List<Map<String, Object>> radiateList = new ArrayList<Map<String, Object>>();// 放射
			List<Map<String, Object>> bcList = new ArrayList<Map<String, Object>>();// B超
			List<Map<String, Object>> wcjList = new ArrayList<Map<String, Object>>();// 胃肠镜
			for (Map<String, Object> map : list) {
				int sslx = Integer.parseInt(map.get("SSLX") + "");
				switch (sslx) {
				case 1:// 心电
					ecgList.add(map);
					break;
				case 2:// 放射
					radiateList.add(map);
					break;
				case 3:// B超
					bcList.add(map);
					break;
				case 4:// 胃肠镜
					wcjList.add(map);
					break;	
				}
			}
			//add by caijy at 2016年3月17日 10:52:02 for 数据返回前台打印
			List<Map<String,Object>> l_print=new ArrayList<Map<String,Object>>();
			/****************************************** 胃肠镜部分 ******************************************/
			Map<String, Object> wcjDetailsMap = new HashMap<String, Object>();// 胃肠镜分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : wcjList) {
				String lbid = map.get("LBID") + "";
				if (!wcjDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>>  wcjDetailsList = new ArrayList<Map<String, Object>>();
					wcjDetailsList.add(map);
					wcjDetailsMap.put(lbid, wcjDetailsList);
				} else {
					List<Map<String, Object>> wcjDetailsList = (List<Map<String, Object>>) wcjDetailsMap
							.get(lbid);
					wcjDetailsList.add(map);
				}
			}
			// 放射迭代，//插入医技表和开单表
			//int sslx = Integer.parseInt(map.get("SSLX") + "");
			for (Map.Entry<String, Object> enrty : wcjDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> wcjDetailsList = (List<Map<String, Object>>) enrty
						.getValue();
				// 插入医技表和开单表
				long radiateYjxh = insertYjTable(wcjDetailsList, WCJ,
						body);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", radiateYjxh);
				m_print.put("SSLB", WCJ);
				l_print.add(m_print);
			}
			/****************************************** 心电部分 ******************************************/
			// 心电图插入医技表和开单表
//			if (ecgList.size() > 0) {
//				long ecgYjxh = insertYjTable(ecgList, ECG, body);
//			}
			Map<String, Object> ecgDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : ecgList) {
				String lbid = map.get("LBID") + "";
				if (!ecgDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> ecgDetailsList = new ArrayList<Map<String, Object>>();
					ecgDetailsList.add(map);
					ecgDetailsMap.put(lbid, ecgDetailsList);
				} else {
					List<Map<String, Object>> ecgDetailsList = (List<Map<String, Object>>) ecgDetailsMap
							.get(lbid);
					ecgDetailsList.add(map);
				}
			}
			// 放射迭代，//插入医技表和开单表
			//int sslx = Integer.parseInt(map.get("SSLX") + "");
			for (Map.Entry<String, Object> enrty : ecgDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> ecgDetailsList = (List<Map<String, Object>>) enrty
						.getValue();
				// 插入医技表和开单表
				long radiateYjxh = insertYjTable(ecgDetailsList, ECG,
						body);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", radiateYjxh);
				m_print.put("SSLB", ECG);
				l_print.add(m_print);
			}
			/*****************************************************************************************/

			/****************************************** 放射部分 ******************************************/
			Map<String, Object> radiateDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : radiateList) {
				String lbid = map.get("LBID") + "";
				if (!radiateDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> radiateDetailsList = new ArrayList<Map<String, Object>>();
					radiateDetailsList.add(map);
					radiateDetailsMap.put(lbid, radiateDetailsList);
				} else {
					List<Map<String, Object>> radiateDetailsList = (List<Map<String, Object>>) radiateDetailsMap
							.get(lbid);
					radiateDetailsList.add(map);
				}
			}
			// 放射迭代，//插入医技表和开单表
			for (Map.Entry<String, Object> enrty : radiateDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> radiateDetailsList = (List<Map<String, Object>>) enrty
						.getValue();
				// 插入医技表和开单表
				long radiateYjxh = insertYjTable(radiateDetailsList, RADIATE,
						body);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", radiateYjxh);
				m_print.put("SSLB", RADIATE);
				l_print.add(m_print);
			}
			/****************************************** B超部分 ******************************************/
			// B超插入医技表和开单表
//			if (bcList.size() > 0) {
//				long bcYjxh = insertYjTable(bcList, BC, body);
//			}
			Map<String, Object> bcDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : bcList) {
				String lbid = map.get("LBID") + "";
				if (!bcDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> bcDetailsList = new ArrayList<Map<String, Object>>();
					bcDetailsList.add(map);
					bcDetailsMap.put(lbid, bcDetailsList);
				} else {
					List<Map<String, Object>> bcDetailsList = (List<Map<String, Object>>) bcDetailsMap
							.get(lbid);
					bcDetailsList.add(map);
				}
			}
			// 放射迭代，//插入医技表和开单表
			for (Map.Entry<String, Object> enrty : bcDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> bcDetailsList = (List<Map<String, Object>>) enrty
						.getValue();
				// 插入医技表和开单表
				long radiateYjxh = insertYjTable(bcDetailsList, BC,
						body);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", radiateYjxh);
				m_print.put("SSLB", BC);
				l_print.add(m_print);
			}
			/*****************************************************************************************/
			if(body.containsKey("njjbmsg")){
				res.put("njjbmsg", body.get("njjbmsg"));
			}
			res.put("print", l_print);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("提交检查申请信息-门诊失败", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("提交检查申请信息-门诊失败", e);
		}
		// catch (ExpException e) {
		// e.printStackTrace();
		// ss.getTransaction().rollback();
		// throw new ModelDataOperationException("提交检查申请信息-门诊失败", e);
		// }
		ss.getTransaction().commit();
	}

	/**
	 * 提交检查申请项目_住院
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ControllerException 
	 */
	@SuppressWarnings(value = { "unchecked", "unused" })
	public void commitCheckApplyProject_WAR(Map<String, Object> body,Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ControllerException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		User user = (User) ctx.get("user.instance");
		UserRoleToken user1 = UserRoleToken.getCurrent();  
		String jgid = user1.getManageUnit().getId();// 用户的MC  
		long yzzh = -1;// 医嘱嘱号，记录，获得并更新最大值
		body.put("jgid", jgid);
		// 前台传过来的数据
		List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
		int brid = Integer.parseInt(body.get("brid")+"");// 病人ID
		String brxm = body.get("brxm")+"";// 病人姓名
		String brxb = body.get("brxb")+"";// 病人性别
		String brxb_text = body.get("brxb_text")+"";// 病人性别_文本
		String birthday = body.get("birthday")+"";// 生日
		int ksdm = Integer.parseInt(body.get("ksdm")+"");// 科室代码
		String ksmc = body.get("ksmc")+"";// 科室名称
		String ryzd = body.get("ryzd")+"";// 入院诊断
		String bgms = body.get("bgms")+"";// 病情描述
		String mzhm = "";
		String address = "";
		String phoneNumber = "";
		try {
			/** 获得接口需要病人的信息 **/
			StringBuffer getBrxxHql = new StringBuffer();
			getBrxxHql.append("select MZHM as MZHM,LXDZ as LXDZ,JTDH as JTDH from MS_BRDA where BRID=:BRID");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BRID",brid + "");
			Map<String, Object> brxxMap = dao.doLoad(getBrxxHql.toString(),parameters);
			mzhm = brxxMap.get("MZHM") + "";
			address = brxxMap.get("LXDZ") + "";
			phoneNumber = brxxMap.get("JTDH") + "";
			/**
			 * 将前台传过来的list分类，对于不同的类型存入不同的医技执行单(ms_yj01有字段执行判别，
			 * 同一类型的可以统一执行完毕修改判别的存一张单子),并且做不同接口
			 **/
			List<Map<String, Object>> ecgList = new ArrayList<Map<String, Object>>();// 心电
			List<Map<String, Object>> radiateList = new ArrayList<Map<String, Object>>();// 放射
			List<Map<String, Object>> bcList = new ArrayList<Map<String, Object>>();// B超
			List<Map<String, Object>> wcjList = new ArrayList<Map<String, Object>>();// 胃肠镜

			for (Map<String, Object> map : list) {
				int sslx = Integer.parseInt(map.get("SSLX") + "");
				switch (sslx) {
				case ECG:// 心电
					ecgList.add(map);
					break;
				case RADIATE:// 放射
					radiateList.add(map);
					break;
				case BC:// B超
					bcList.add(map);
					break;
				case WCJ:// 胃肠镜
					wcjList.add(map);
					break;		
				}
			}
			//add by Wangjinglong at 2018年3月22日 10:52:02 for 数据返回前台打印
			List<Map<String,Object>> l_print=new ArrayList<Map<String,Object>>();
			/****************************************** 胃肠镜部分 ******************************************/

			Map<String, Object> wcjDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : wcjList) {
				String lbid = map.get("LBID") + "";
				if (!wcjDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> wcjDetailsList = new ArrayList<Map<String, Object>>();
					wcjDetailsList.add(map);
					wcjDetailsMap.put(lbid, wcjDetailsList);
				} else {
					List<Map<String, Object>> wcjDetailsList = (List<Map<String, Object>>) wcjDetailsMap.get(lbid);
					wcjDetailsList.add(map);
				}
			}
			for (Map.Entry<String, Object> enrty : wcjDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> wcjDetailsList = (List<Map<String, Object>>) enrty.getValue();
				// 插入医技表和开单表
				yzzh = insertYzTable(wcjDetailsList, WCJ,body,yzzh,ctx);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", yzzh);
				m_print.put("SSLB", WCJ);
				l_print.add(m_print);
			}
			/*****************************************************************************************/
			/****************************************** 心电部分 ******************************************/
			// 插入医嘱表和开单表
//			if (ecgList.size() > 0) {
//				yzzh = insertYzTable(ecgList, ECG, body, yzzh,ctx);
//			}
			Map<String, Object> ecgDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : ecgList) {
				String lbid = map.get("LBID") + "";
				if (!ecgDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> ecgDetailsList = new ArrayList<Map<String, Object>>();
					ecgDetailsList.add(map);
					ecgDetailsMap.put(lbid, ecgDetailsList);
				} else {
					List<Map<String, Object>> ecgDetailsList = (List<Map<String, Object>>) ecgDetailsMap.get(lbid);
					ecgDetailsList.add(map);
				}
			}
			for (Map.Entry<String, Object> enrty : ecgDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> ecgDetailsList = (List<Map<String, Object>>) enrty.getValue();
				// 插入医技表和开单表
				yzzh = insertYzTable(ecgDetailsList, ECG,body,yzzh,ctx);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", yzzh);
				m_print.put("SSLB", ECG);
				l_print.add(m_print);
			}
			/*****************************************************************************************/

			/****************************************** 放射部分 ******************************************/
			Map<String, Object> radiateDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : radiateList) {
				String lbid = map.get("LBID") + "";
				if (!radiateDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> radiateDetailsList = new ArrayList<Map<String, Object>>();
					radiateDetailsList.add(map);
					radiateDetailsMap.put(lbid, radiateDetailsList);
				} else {
					List<Map<String, Object>> radiateDetailsList=(List<Map<String, Object>>) radiateDetailsMap.get(lbid);
					radiateDetailsList.add(map);
				}
			}
			// 迭代，//插入医嘱表和开单表
			for (Map.Entry<String, Object> enrty : radiateDetailsMap.entrySet()) {
				List<Map<String, Object>> radiateDetailsList = (List<Map<String, Object>>) enrty.getValue();
				yzzh = insertYzTable(radiateDetailsList, RADIATE, body, yzzh,ctx);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", yzzh);
				m_print.put("SSLB", RADIATE);
				l_print.add(m_print);
			}
			/****************************************** B超部分 ******************************************/
//			// 插入医嘱表和开单表
//			if (bcList.size() > 0) {
//				yzzh = insertYzTable(bcList, BC, body, yzzh,ctx);
//			}
			Map<String, Object> bcDetailsMap = new HashMap<String, Object>();// 放射分类
			// 将放射根据类别分类，分成不同的类别，如CR,DR等
			for (Map<String, Object> map : bcList) {
				String lbid = map.get("LBID") + "";
				if (!bcDetailsMap.containsKey(lbid)) {
					List<Map<String, Object>> bcDetailsList = new ArrayList<Map<String, Object>>();
					bcDetailsList.add(map);
					bcDetailsMap.put(lbid, bcDetailsList);
				} else {
					List<Map<String, Object>> bcDetailsList = (List<Map<String, Object>>) bcDetailsMap.get(lbid);
					bcDetailsList.add(map);
				}
			}
			for (Map.Entry<String, Object> enrty : bcDetailsMap.entrySet()) {
				// 获得放射申请单详细信息
				List<Map<String, Object>> bcDetailsList = (List<Map<String, Object>>) enrty.getValue();
				// 插入医技表和开单表
				yzzh = insertYzTable(bcDetailsList, BC,body,yzzh,ctx);
				Map<String,Object> m_print=new HashMap<String,Object>();
				m_print.put("SQDH", yzzh);
				m_print.put("SSLB", BC);
				l_print.add(m_print);
			}
			/*****************************************************************************************/
			/*****************************************************************************************/
			if(body.containsKey("njjbmsg")){
				res.put("njjbmsg", body.get("njjbmsg"));
			}
			res.put("print", l_print);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("提交检查申请信息-住院失败", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("提交检查申请信息-住院失败", e);
		}
		ss.getTransaction().commit();
	}

	/**
	 * 删除已开的检查申请单
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unused")
	public void removeCheckApplyProject(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		User user = (User) ctx.get("user.instance");
		UserRoleToken user1 = UserRoleToken.getCurrent();  
		String jgid = user1.getManageUnit().getId();// 用户的MC  
		String ysdm = body.get("ysdm").toString();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		try {
			String yjxh = body.get("yjxh") + "";// 申请单号
			String lb = body.get("lb") + "";// 1.门诊2.住院
			String zt = body.get("zt") + "";// 1.删除2.取消执行
			/******************** 主要业务操作 ********************/
			if ("1".equals(zt)) {
				res = removeCheckApply(lb, yjxh, res);
			} else if ("2".equals(zt)) {
				res = cancelCheckApply(ysdm, lb, yjxh, res);
			}
			/***********************************************/
			ss.getTransaction().commit();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("检查申请单删除或取消执行失败!", e);
		} catch (ExpException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("检查申请单删除或取消执行失败!", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("检查申请单删除或取消执行失败!", e);
		} catch (ParseException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
			throw new ModelDataOperationException("检查申请单删除或取消执行失败!", e);
		}
	}

	/**
	 * 获取住院已开检查申请单(根据医嘱组号作为单号，会导致显示多条相同记录，schema不能去重，故后台处理)
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void getCheckApplyExchangeApplication_WAR(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();  
		String jgid = user.getManageUnit().getId();// 用户的jgid  
		try {
			String dateFrom = body.get("dateFrom") + "";
			String dateTo = body.get("dateTo") + "";
			String zyh = body.get("zyh") + "";
			StringBuffer hql = new StringBuffer();
			//and b.jfbz=9 and b.yzpx is null   2016-03-16 住院已开检查单记录串号修改
			hql.append("select distinct a.SQDH as SQDH,a.YLLB as YLLB,a.SSLX as SSLX,CASE(a.SSLX) WHEN 1 THEN '心电' WHEN 2 THEN '放射' WHEN 3 THEN 'B超' END as SSLXTEXT,"
					+ "b.JGID as JGID,to_char(b.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ,c.YGXM as CZGH,b.ZYH as ZYH,b.LSBZ as LSBZ,CASE(b.LSBZ) WHEN 0 THEN '未执行' WHEN 1 THEN '已执行' END as LSBZTEXT "
					+ "from YJ_JCSQ_KD01 a,ZY_BQYZ b,GY_YGDM c where a.SQDH=b.YZZH and b.JGID=:JGID and b.KSSJ>=to_date(:DATEFROM,'yyyy-mm-dd hh24:mi:ss') "
					+ "and b.KSSJ<=to_date(:DATETO,'yyyy-mm-dd hh24:mi:ss') and b.ZYH=:ZYH and b.JFBZ=9 and b.YZPX is null and b.LSBZ in (0,1) and a.YLLB=2 and b.CZGH=c.YGDM");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", jgid);
			parameters.put("DATEFROM", dateFrom);
			parameters.put("DATETO", dateTo);
			parameters.put("ZYH", zyh);
			int pageSize = 25;
			int first = 0;
			long total = dao.doSqlQuery(hql.toString(), parameters).size();
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			res.put("body", dao.doSqlQuery(hql.toString(), parameters));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("获得检住院已开检查申请单失败", e);
		}
	}

	/**
	 * 获得未绑定费用的检查项目列表
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void getCheckApplyUnboundProject(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String jgid = body.get("jgid") + "";
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			hql.append("select a.LBID as LBID,a.BWID as BWID,a.XMID as XMID,b.LBMC as LBMC,c.BWMC as BWMC,d.XMMC as XMMC"
					+ " from YJ_JCSQ_XMDY a,YJ_JCSQ_JCLB b,YJ_JCSQ_JCBW c,YJ_JCSQ_JCXM d where a.LBID=b.LBID and a.BWID=c.BWID"
					+ " and a.XMID=d.XMID and a.jgid=:jgid order by a.LBID,a.BWID,a.XMID");
			parameters.put("jgid", jgid);
			List<Map<String, Object>> reslist = dao.doSqlQuery(hql.toString(),
					parameters);
			for (Map<String, Object> map : reslist) {
				parameters.clear();
				long lbid = Long.parseLong(map.get("LBID") + "");
				long bwid = Long.parseLong(map.get("BWID") + "");
				long xmid = Long.parseLong(map.get("XMID") + "");
				parameters.put("LBID", lbid);
				parameters.put("BWID", bwid);
				parameters.put("XMID", xmid);
				parameters.put("JGID", jgid);
				long count = dao
						.doCount(
								"YJ_JCSQ_SFMX",
								"LBID=:LBID and BWID=:BWID and XMID=:XMID and JGID=:JGID",
								parameters);
				if (count == 0) {
					list.add(map);// 不存在的显示
				}
			}
//			int pageSize = 25;
//			int first = 0;
//			long total = list.size();
//			res.put("totalCount", total);
//			res.put("pageSize", pageSize);
//			res.put("pageNo", first);
//			parameters.put("first", first * pageSize);
//			parameters.put("max", pageSize);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("获得未绑定费用的检查项目列表失败", e);
		}
	}

	/**
	 * 插入医技01,02表,检查开单01,02表的私有方法，主要为了区分不同类型的单据开不同的单子
	 * 
	 * @param list
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 * @throws ControllerException 
	 */
	private long insertYjTable(List<Map<String, Object>> list, int sslx,
			Map<String, Object> body) throws ValidateException,
			PersistentDataOperationException,ModelDataOperationException, ControllerException {
		int brid = Integer.parseInt(body.get("brid") + "");// 病人ID
		String brxm = body.get("brxm") + "";// 病人姓名
		Date kdrq = new Date();// 开单日期
		int ksdm = Integer.parseInt(body.get("ksdm") + "");// 科室代码
		String ysdm = body.get("ysdm") + "";// 医生代码
		String clinicId = body.get("clinicId") + "";// 就诊序号
		String bgms = body.get("bgms") + "";// 病情描述
		String jgid = body.get("jgid") + "";// 机构代码
		String zsxx = body.get("zsxx") + "";// 主诉信息
		String xbs = body.get("xbs") + "";// 现病史
		String jws = body.get("jws") + "";// 现病史
		String gms = body.get("gms") + "";// 现病史
		String fzjc = body.get("fzjc") + "";// 现病史
		String tgjc = body.get("tgjc") + "";// 现病史
		String bzxx = body.get("bzxx") + "";// 备注信息
		String xl = body.get("xl") + "";// 心率
		String xlv = body.get("xlv") + "";// 心律
		//String ctxx = body.get("ctxx") + "";// 查体信息
		//String syxx = body.get("syxx") + "";// 实验室和器材检查
		//String xj = body.get("xj") + "";// 心界
		//String xy = body.get("xy") + "";// 心音
//		String xbd = body.get("xbd") + "";// 心搏动
//		String xzy = body.get("xzy") + "";// 心杂音
		//String xlsj = body.get("xlsj") + "";// 心力衰竭
		//String xgjc = body.get("xgjc") + "";// 心光检查
		/** 插入ms_yj01表 **/
		Map<String, Object> yj01Parameters = new HashMap<String, Object>();
		yj01Parameters.put("JGID", jgid);
		yj01Parameters.put("BRID", brid);
		yj01Parameters.put("BRXM", brxm);
		yj01Parameters.put("KDRQ", kdrq);
		yj01Parameters.put("KSDM", ksdm);
		yj01Parameters.put("YSDM", ysdm);
		yj01Parameters.put("ZXKS", ksdm);
		yj01Parameters.put("ZXPB", 0);
		yj01Parameters.put("ZFPB", 0);
		yj01Parameters.put("CFBZ", 0);
		yj01Parameters.put("JZXH", clinicId);
		yj01Parameters.put("DJZT", 0);
		yj01Parameters.put("DJLY", 9);// 检查开单单据来源都为9
		yj01Parameters.put("GDBZ", 0);
		yj01Parameters.put("LCZL", bgms);
		// 2016-04-15 add bck 检查单减免标志默认为0
		//yj01Parameters.put("JMBZ", 0);
		//yj01Parameters.put("ZDDM", BSPHISUtil.getZDDMforKSDM(ksdm, dao));
		Map<String, Object> yj01Res = dao.doSave("create",
				BSPHISEntryNames.MS_YJ01_CIC, yj01Parameters, false); 
		long yjxh = Long.parseLong(yj01Res.get("YJXH") + "");// 获取ms_yj01的主键

		Map<String, Object> yj02Parameters = new HashMap<String, Object>();// 插入ms_yj02表的参数
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql="select b.BRXZ as BRXZ from MS_JZLS a ,MS_GHMX b where a.JZXH=:JZXH and a.GHXH=b.SBXH";
		parameters.put("JZXH",Long.parseLong(clinicId));
		Map<String, Object> brxzmap=dao.doSqlLoad(sql, parameters);
		parameters.remove("JZXH");
		String brxz=brxzmap.get("BRXZ")==null?"":brxzmap.get("BRXZ")+"";
		String njjbmsg="";
		/** 插入ms_yj02表 **/
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			if(brxz.equals("2000")){
				String zbmjgid="";
				Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
				zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
				String tsql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("jgid",zbmjgid);
				p.put("fyxh",Long.parseLong(map.get("FYXH")+""));
				Map<String, Object> zbmmap=dao.doSqlLoad(tsql,p);
				if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
					if(njjbmsg.length()==0){
						njjbmsg+="所开项目中"+map.get("FYMC");
					}else{
						njjbmsg+="、"+map.get("FYMC");
					}
				}
			}
			yj02Parameters.clear();
			parameters.clear();
			parameters.put("FYXH", Long.parseLong(map.get("FYXH") + ""));
			yj02Parameters.put("JGID", jgid);
			yj02Parameters.put("YJXH", yjxh);
			yj02Parameters.put("YLXH", Integer.parseInt(map.get("FYXH") + ""));
			Map<String, Object> ylsfMap = dao.doLoad("select XMLX as XMLX,FYGB as FYGB from GY_YLSF where FYXH=:FYXH",
							parameters);
			Integer xmlx = ylsfMap.get("XMLX") == null ? 0 : Integer.parseInt(ylsfMap.get("XMLX") + "");
			yj02Parameters.put("XMLX", xmlx);
			yj02Parameters.put("YJZX", i == 0 ? 1 : 0);
			yj02Parameters.put("YLDJ", Double.parseDouble(map.get("FYDJ") + ""));
			yj02Parameters.put("YLSL", Integer.parseInt(map.get("FYSL") + ""));
			yj02Parameters.put("HJJE", Double.parseDouble(map.get("FYZJ") + ""));
			Integer fygb = ylsfMap.get("FYGB") == null ? null : Integer.parseInt(ylsfMap.get("FYGB") + "");
			yj02Parameters.put("FYGB", fygb);// 费用归并
			yj02Parameters.put("ZFBL", 1);
			yj02Parameters.put("DZBL", 1);
			yj02Parameters.put("YJZH", 0);// 医技组号
			yj02Parameters.put("CXBZ", 0);
			yj02Parameters.put("JCBWDM", map.get("BWID")+ "");
			yj02Parameters.put("JCBWMC", map.get("BWMC")+ "");
			if(map.get("QEZFBZ")!=null && Integer.parseInt(map.get("QEZFBZ")+"")==1){
				yj02Parameters.put("QEZFBZ", 1);
			}
			Map<String, Object> ylsfMap1=dao.doSave("create", BSPHISEntryNames.MS_YJ02, yj02Parameters,false);
		}
		if(njjbmsg.length() >0 ){
			body.put("njjbmsg",njjbmsg+"未维护医保自编码，将导致收费处不能收费！");
		}
		/** 插入yj_jcsq_kd01表 **/
		Map<String, Object> kd01Parameters = new HashMap<String, Object>();
		kd01Parameters.put("SQDH", yjxh);
		kd01Parameters.put("YLLB", 1);
		kd01Parameters.put("SSLX", sslx);
		kd01Parameters.put("ZSXX", zsxx);
		kd01Parameters.put("XBS", xbs);
		kd01Parameters.put("JWS", jws);
		kd01Parameters.put("GMS", gms);
		kd01Parameters.put("FZJC", fzjc);
		kd01Parameters.put("TGJC", tgjc);
		//kd01Parameters.put("CTXX", ctxx);
		//kd01Parameters.put("SYXX", syxx);
		kd01Parameters.put("BZXX", bzxx);
		kd01Parameters.put("XLV", xlv);
		kd01Parameters.put("XL", xl);
		//kd01Parameters.put("XJ", xj);
		//kd01Parameters.put("XY", xy);
//		kd01Parameters.put("XBD", xbd);
//		kd01Parameters.put("XZY", xzy);
		//kd01Parameters.put("XLSJ", xlsj);
		//kd01Parameters.put("XGJC", xgjc);
		kd01Parameters.put("DJZT", 0);// 未登记
		dao.doSave("create", BSPHISEntryNames.YJ_JCSQ_KD01, kd01Parameters,
				false);
		// 获得实际执行的项目
		List<String> jcxmList = new ArrayList<String>();
		for (Map<String, Object> map : list) {
			String lbid = map.get("LBID") + "";
			String bwid = map.get("BWID") + "";
			String xmid = map.get("XMID") + "";
			String zh = lbid + "," + bwid + "," + xmid;
			if (!jcxmList.contains(zh)) {
				jcxmList.add(zh);
			}
		}
		/** 插入yj_jcsq_kd02表 **/
		Map<String, Object> kd02Parameters = new HashMap<String, Object>();
		for (String jcxm : jcxmList) {
			kd02Parameters.clear();
			String[] str = jcxm.split(",");
			kd02Parameters.put("SQDH", yjxh);
			kd02Parameters.put("YLLB", 1);
			kd02Parameters.put("LBID", Long.parseLong(str[0]));
			kd02Parameters.put("BWID", Long.parseLong(str[1]));
			kd02Parameters.put("XMID", Long.parseLong(str[2]));
			dao.doSave("create", BSPHISEntryNames.YJ_JCSQ_KD02, kd02Parameters,
					false);
		}
		return yjxh;
	}

	/**
	 * 插入住院医嘱表,检查开单01,02表的私有方法，主要为了区分不同类型的单据开不同的单子
	 * 
	 * @param list
	 * @param sslx
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 * @throws ControllerException 
	 */
	private long insertYzTable(List<Map<String, Object>> list, int sslx,
			Map<String, Object> body, long yzzh,Context ctx) throws ValidateException,
			PersistentDataOperationException, ControllerException {
		String jgid = body.get("jgid") + "";// 机构代码
		long zyh = Long.parseLong(body.get("zyh") + "");// 住院号
		String zrysdm = body.get("zrysdm") + "";// 主任医生代码
		String ysdm = body.get("zrysdm") + ""; // 操作医生代码
		long brksdm = Long.parseLong(body.get("ksdm") + ""); // 病人科室代码
		long brbq = Long.parseLong(body.get("brbq") + ""); // 病人病区
		String brch = body.get("brch") + "";// 病人床号
		String zsxx = body.get("zsxx") + "";// 主诉信息
		String xbs = body.get("xbs") + "";// 现病史
		String jws = body.get("jws") + "";// 既往史
		String gms = body.get("gms") + "";// 过敏史
		String tgjc = body.get("tgjc") + "";// 体格检查
		String fzjc = body.get("fzjc") + "";// 辅助检查
		String ctxx = body.get("ctxx") + "";// 查体信息
		String syxx = body.get("syxx") + "";// 实验室和器材检查
		String bzxx = body.get("bzxx") + "";// 备份信息
		String xj = body.get("xj") + "";// 心界
		String xl = body.get("xl") + "";// 心率
		String xy = body.get("xy") + "";// 心音
		String xlv = body.get("xlv") + "";// 心律
//		String xbd = body.get("xbd") + "";// 心搏动
//		String xzy = body.get("xzy") + "";// 心杂音
		String xlsj = body.get("xlsj") + "";// 心力衰竭
		String xgjc = body.get("xgjc") + "";// 心光检查
		Date date = new Date();
		/** 获得医嘱组号的最大值 **/
//			Map<String, Object> parameters = new HashMap<String, Object>();
//			StringBuffer getYzzhHql = new StringBuffer();
//			getYzzhHql.append("select max(yzzh) as YZZH from "+ "ZY_BQYZ");
//			Map<String, Object> yzzhMap = dao.doSqlQuery(getYzzhHql.toString(),parameters).get(0);
//			yzzh = Long.parseLong(yzzhMap.get("YZZH") + "") + 1;
		Schema scm;
		try {
			scm = SchemaController.instance().get("phis.application.war.schemas.ZY_BQYZ_LS");
			List<SchemaItem> items = scm.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if ("YZZH".equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			yzzh = Long.parseLong(KeyManager.getKeyByName("ZY_BQYZ",item.getKeyRules(),item.getId(),ctx));
		} catch (ControllerException e) {
			e.printStackTrace();
		}catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (KeyManagerException e) {
			e.printStackTrace();
		}
		Map<String, Object> p = new HashMap<String, Object>();
		String brxzsql="select a.BRXZ as BRXZ from ZY_BRRY a where a.zyh=:ZYH";
		p.put("ZYH",zyh);
		Map<String, Object> brxzmap=dao.doSqlLoad(brxzsql,p);
		p=null;
		String brxz=brxzmap.get("BRXZ")==null?"":brxzmap.get("BRXZ")+"";
		String njjbmsg="";
		Map<String, Object> zybqyzParameters = new HashMap<String, Object>();
		for (Map<String, Object> map : list) {
			if(brxz.equals("2000")){
				String zbmjgid="";
				Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
				zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
				String tsql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("jgid",zbmjgid);
				pa.put("fyxh",Long.parseLong(map.get("FYXH")+""));
				Map<String, Object> zbmmap=dao.doSqlLoad(tsql,pa);
				if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
					if(njjbmsg.length()==0){
						njjbmsg+="所开项目中"+map.get("FYMC");
					}else{
						njjbmsg+="、"+map.get("FYMC");
					}
				}
			}
			zybqyzParameters.clear();
			zybqyzParameters.put("JGID", jgid);
			zybqyzParameters.put("ZYH", zyh);
			zybqyzParameters.put("YZMC", (map.get("FYMC") + "").trim() + "/"+ map.get("FYDW"));
			zybqyzParameters.put("YPXH", Long.parseLong(map.get("FYXH") + ""));
			zybqyzParameters.put("YPCD", 0l);
			zybqyzParameters.put("XMLX", 4);
			zybqyzParameters.put("YPLX", 0);
			zybqyzParameters.put("MRCS", 1);
			zybqyzParameters.put("YCJL", 0.0);
			zybqyzParameters.put("YCSL",Double.parseDouble(map.get("FYSL") + ""));
			zybqyzParameters.put("MZCS", 0);
			zybqyzParameters.put("KSSJ", date);
			zybqyzParameters.put("TZSJ", date);
			zybqyzParameters.put("YPDJ",Double.parseDouble(map.get("FYDJ") + ""));
			zybqyzParameters.put("YPYF", 0l);
			zybqyzParameters.put("YSGH", zrysdm);
			zybqyzParameters.put("TZYS", zrysdm);
			zybqyzParameters.put("CZGH", ysdm);
			zybqyzParameters.put("SYBZ", 0);
			zybqyzParameters.put("SRKS", brbq);
			zybqyzParameters.put("ZFPB", 0);
			zybqyzParameters.put("YJZX", 0);
			zybqyzParameters.put("YJXH", 0l);
			zybqyzParameters.put("YZZH", yzzh);
			zybqyzParameters.put("SYPC", "st");//使用频次
			zybqyzParameters.put("FYSX", 0);
			zybqyzParameters.put("YEPB", 0);
			zybqyzParameters.put("YFSB", 0l);
			zybqyzParameters.put("LSYZ", 1);
			zybqyzParameters.put("LSBZ", 0);
			zybqyzParameters.put("YZPB", 0);
			zybqyzParameters.put("JFBZ", 9);
			zybqyzParameters.put("TPN", 0);
			zybqyzParameters.put("YSBZ", 1);
			zybqyzParameters.put("YSTJ", 1);//医生提交
			//zybqyzParameters.put("YZPH", 0);
			zybqyzParameters.put("ZFBZ", 0);
			zybqyzParameters.put("SRCS", 0);
			zybqyzParameters.put("SFJG", 0);
			zybqyzParameters.put("BRKS", brksdm);
			zybqyzParameters.put("BRBQ", brksdm);
			zybqyzParameters.put("BRCH", brch);
			zybqyzParameters.put("YZZXSJ", "23:00");
			zybqyzParameters.put("FHBZ", 0);
			zybqyzParameters.put("TZFHBZ", 0);
			zybqyzParameters.put("PSPB", 0);
			zybqyzParameters.put("BZXX", "检查开单项目,若要修改请先在开单界面删除再重开");
			zybqyzParameters.put("ZTMC", map.get("LBMC") + "");
			zybqyzParameters.put("JCBWDM", map.get("BWID") + "");
			zybqyzParameters.put("JCBWMC", map.get("BWMC") + "");
			zybqyzParameters.put("SQID", yzzh);
			try {
				Schema sc= SchemaController.instance().get(BSPHISEntryNames.ZY_BQYZ);
				List<SchemaItem> items=sc.getItems();
				SchemaItem item=null;
				for(SchemaItem sit : items){
					if("JLXH".equals(sit.getId())){
						item=sit;
						break;
					}					
				}
				/*********modify by zhaojian 2017-08-28 解决主键冲突问题：改为从序列中获取*******************/
				Long pkey=Long.parseLong(KeyCreator.create("ZY_BQYZ", item.getKeyRules(), item.getId()));
				zybqyzParameters.put("JLXH", pkey);
				String sql="insert into ZY_BQYZ (JGID,ZYH,YZMC,YPXH,YPCD,XMLX,YPLX," +
						"MRCS,YCJL,YCSL,MZCS,KSSJ,TZSJ,YPDJ,YPYF,YSGH,TZYS,CZGH,SYBZ,SRKS,ZFPB,YJZX,YJXH,YZZH,SYPC,FYSX,YEPB,YFSB," +
						"LSYZ,LSBZ,YZPB,JFBZ,TPN,YSBZ,YSTJ,ZFBZ,SRCS,SFJG,BRKS,BRBQ,BRCH,YZZXSJ,FHBZ,TZFHBZ,PSPB,BZXX,JLXH,JCBWDM,JCBWMC,ZTMC,SQID) values (:JGID,:ZYH,:YZMC,:" +
						"YPXH,:YPCD,:XMLX,:YPLX,:MRCS,:YCJL,:YCSL,:MZCS,:KSSJ,:TZSJ,:YPDJ,:YPYF,:YSGH,:TZYS,:CZGH,:SYBZ,:SRKS" +
						",:ZFPB,:YJZX,:YJXH,:YZZH,:SYPC,:FYSX,:YEPB,:YFSB,:LSYZ,:LSBZ," +
						":YZPB,:JFBZ,:TPN,:YSBZ,:YSTJ,:ZFBZ,:SRCS,:SFJG,:BRKS,:BRBQ,:BRCH,:YZZXSJ,:FHBZ,:TZFHBZ,:PSPB,:BZXX,:JLXH,:JCBWDM,:JCBWMC,:ZTMC,:SQID)";
				dao.doSqlUpdate(sql, zybqyzParameters);
			} catch (ControllerException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Long pkey =Long.parseLong(KeyManagerCreator.)
//			dao.doSqlUpdate(sql, zybqyzParameters);
			//dao.doInsert(BSPHISEntryNames.ZY_BQYZ, zybqyzParameters, false);
		}
		if(njjbmsg.length() >0 ){
			body.put("njjbmsg",njjbmsg+"未维护医保自编码，将导致收费处不能收费！");
		}
		/** 插入yj_jcsq_kd01表 **/
		Map<String, Object> kd01Parameters = new HashMap<String, Object>();
		kd01Parameters.put("SQDH", yzzh);
		kd01Parameters.put("YLLB", 2);
		kd01Parameters.put("SSLX", sslx);
		kd01Parameters.put("ZSXX", zsxx);
		kd01Parameters.put("CTXX", ctxx);
		kd01Parameters.put("XBS", xbs);
		kd01Parameters.put("JWS", jws);
		kd01Parameters.put("GMS", gms);
		kd01Parameters.put("TGJC", tgjc);
		kd01Parameters.put("FZJC", fzjc);
		kd01Parameters.put("SYXX", syxx);
		kd01Parameters.put("BZXX", bzxx);
		kd01Parameters.put("XJ", xj);
		kd01Parameters.put("XL", xl);
		kd01Parameters.put("XY", xy);
		kd01Parameters.put("XLV", xlv);
//		kd01Parameters.put("XBD", xbd);
//		kd01Parameters.put("XZY", xzy);
		kd01Parameters.put("XLSJ", xlsj);
		kd01Parameters.put("XGJC", xgjc);
		kd01Parameters.put("DJZT", 0);// 未登记
		Map<String,Object> list1=dao.doSave("create",BSPHISEntryNames.YJ_JCSQ_KD01,kd01Parameters,false);
		// 获得实际执行的项目
		List<String> jcxmList = new ArrayList<String>();
		for (Map<String, Object> map : list) {
			String lbid = map.get("LBID") + "";
			String bwid = map.get("BWID") + "";
			String xmid = map.get("XMID") + "";
			String zh = lbid + "," + bwid + "," + xmid;
			if (!jcxmList.contains(zh)) {
				jcxmList.add(zh);
			}
		}
		/** 插入yj_jcsq_kd02表 **/
		Map<String, Object> kd02Parameters = new HashMap<String, Object>();
		for (String jcxm : jcxmList) {
			kd02Parameters.clear();
			String[] str = jcxm.split(",");
			kd02Parameters.put("SQDH", yzzh);
			kd02Parameters.put("YLLB", 2);
			kd02Parameters.put("LBID", Long.parseLong(str[0]));
			kd02Parameters.put("BWID", Long.parseLong(str[1]));
			kd02Parameters.put("XMID", Long.parseLong(str[2]));
			dao.doSave("create", BSPHISEntryNames.YJ_JCSQ_KD02, kd02Parameters,false);
		}
		return yzzh;
	}

	/**
	 * 删除检查项目
	 * 
	 * @param sslx
	 * @param yjxh
	 * @throws PersistentDataOperationException
	 * @throws ExpException
	 */
	private Map<String, Object> removeCheckApply(String lb, String yjxh,
			Map<String, Object> res) throws PersistentDataOperationException,
			ExpException {
		boolean hasPay = false;
		Map<String, Object> parameters = new HashMap<String, Object>();
//		List<Map<String,Object>> temp_list = new ArrayList<Map<String,Object>>();
		if ("1".equals(lb)) {// 门诊
			parameters.put("YJXH", Long.parseLong(yjxh));
			long count = dao.doCount("MS_YJ01",
					"YJXH=:YJXH and FPHM is not null", parameters);
			if (count > 0) {
				// 已收费不可以删
				hasPay = true;
			}
		} else if ("2".equals(lb)) {// 住院
			parameters.put("YJXH", Long.parseLong(yjxh));
			List<Map<String, Object>> list = dao.doSqlQuery(
					"select JLXH as JLXH from " + "ZY_BQYZ"
							+ " where YZZH=:YJXH and LSBZ=0 and QRSJ is null and JFBZ=9", parameters);
			for (Map<String, Object> map : list) {
//				temp_list.add(map);
				parameters.clear();
				parameters.put("JLXH", Long.parseLong(map.get("JLXH") + ""));
				long count = dao.doCount("ZY_FYMX",
						"YZXH=:JLXH", parameters);
				if (count > 0) {
					// 已计费不可以删
					hasPay = true;
					break;
				}
			}
		}
		// 已收费，直接返回前台，提示已收费不能删除
		if (hasPay) {
			res.put("code", 500);
			res.put("msg", "该申请单已收费，删除失败");
			return res;
		} else {
			parameters.clear();
			parameters.put("SQDH", Long.parseLong(yjxh));
			if ("1".equals(lb)) {// 门诊
				dao.doRemove(Long.parseLong(yjxh), BSPHISEntryNames.MS_YJ01_CIC);
				dao.doRemove("YJXH", Long.parseLong(yjxh), BSPHISEntryNames.MS_YJ02);
			} else if ("2".equals(lb)) {// 住院
//				dao.doRemove("YZZH", yjxh, "ZY_BQYZ_LS");
				dao.doUpdate("delete from " + "ZY_BQYZ"
					+ " where YZZH=:SQDH and LSBZ=0 and QRSJ is null and JFBZ=9", parameters);
			}
			parameters.put("YLLB", Integer.parseInt(lb));
			dao.doUpdate("delete from " + "YJ_JCSQ_KD01"
					+ " where SQDH=:SQDH and YLLB=:YLLB", parameters);
			dao.doUpdate("delete from " + "YJ_JCSQ_KD02"
					+ " where SQDH=:SQDH and YLLB=:YLLB", parameters);
			res.put("code", 200);
			return res;
		}
	}

	private Map<String, Object> cancelCheckApply(String ysdm, String lb,
			String yjxh, Map<String, Object> res)
			throws PersistentDataOperationException, ExpException,
			ParseException, ValidateException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		if ("1".equals(lb)) {// 门诊
			hql.append("update MS_YJ01 set ZXPB=0 where YJXH=:YJXH");
			parameters.put("YJXH", Long.parseLong(yjxh));
			dao.doUpdate(hql.toString(), parameters);
		} else if ("2".equals(lb)) {// 住院
			hql.append("update ZY_BQYZ set LSBZ=0  where YZZH=:YZZH");
			parameters.put("YZZH", Long.parseLong(yjxh));
			dao.doUpdate(hql.toString(), parameters);
			// 获取原有计费明细，插入负记录
			hql.setLength(0);
			hql.append("select JLXH as JLXH from ZY_BQYZ  where YZZH=:YZZH and JFBZ=9");
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			for (Map<String, Object> fymxMap : list) {
				parameters.clear();
				hql.setLength(0);
				hql.append("select JGID as JGID,ZYH as ZYH,FYXH as FYXH,FYMC as FYMC,YPCD as YPCD,"
						+ "-FYSL as FYSL,-FYDJ as FYDJ,-ZJJE as ZJJE,-ZFJE as ZFJE,YSGH as YSGH,:YSDM as SRGH,"
						+ ":YSDM as QRGH,FYBQ as FYBQ,FYKS as FYKS,ZXKS as ZXKS,XMLX as XMLX,"
						+ "YPLX as YPLX,FYXM as FYXM,JSCS as JSCS,ZFBL as ZFBL,YZXH as YZXH,ZLJE as ZLJE,ZLXZ as ZLXZ,"
						+ "YEPB as YEPB,DZBL as DZBL from ZY_FYMX where YZXH=:JLXH");
				parameters.put("JLXH", fymxMap.get("JLXH") + "");
				parameters.put("YSDM", ysdm);
				Map<String, Object> fymxNewMap = dao.doSqlQuery(hql.toString(),
						parameters).get(0);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				fymxNewMap.put("FYRQ", sdf.parse(sdf.format(new Date())));
				fymxNewMap.put("JFRQ", sdf.parse(sdf.format(new Date())));
				dao.doSave("create", BSPHISEntryNames.ZY_FYMX, fymxNewMap,
						false);
			}
		}
		res.put("code", 200);
		return res;
	}
	//放射报告（访问视图）
	public Map<String, Object> doGetFsinfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
	throws ModelDataOperationException {
		String jgid = req.get("jgid").toString();
		String  dateFrom = req.get("dateFrom").toString();
		String  dateTo = req.get("dateTo").toString();
		//String  brid = req.get("bird").toString(); 
		String  yllb = req.get("yllb").toString(); 
		String  jclb = req.get("jclb").toString();
		//System.out.println(jclb);
		String  mzxh = req.get("mzxh").toString();
		QybrDaoImpl fs=new QybrDaoImpl();//and a.MGRHospital='"+jgid+"'
		String sql="SELECT REQUESTELECTRONIC as SQDH,REQUESTDOCTORALIAS as YSMC ,APPROVEDOCTORALIAS as YSMC ,APPROVEDATE as ZXSJ,REPORTSCONCLUSION as BGZD,REPORTSEVIDENCES as BGMS FROM vhis a" +
				" where a.AdmissionID='"+mzxh+"' and a.AdmissionSource='"+yllb+"' and a.StudiesModalities in "+jclb+" and APPROVEDATE>='"+dateFrom+"' and  APPROVEDATE<='"+dateTo+"'";
		List<Map<String, Object>> list = fs.findQyMap(sql);
		if(yllb.equals("50")){
			res.put("body", SchemaUtil.setDictionaryMassageForList(list, "YJ_JCSQ_FS_CIC"));
		}else {
			res.put("body", SchemaUtil.setDictionaryMassageForList(list, "YJ_JCSQ_FS_WAR"));
		}
		
		return res;
	}
	//B超报告（访问视图）
	public Map<String, Object> doGetBcinfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
	throws ModelDataOperationException {
		String jgid = req.get("jgid").toString();
		String  dateFrom = req.get("dateFrom").toString();
		String  dateTo = req.get("dateTo").toString();
		//String  brid = req.get("bird").toString(); 
		String  yllb = req.get("yllb").toString(); 
		String  jclb = req.get("jclb").toString();
		//System.out.println(jclb);
		String  mzxh = req.get("mzxh").toString();
		QybrDaoImpl fs=new QybrDaoImpl();//and a.MGRHospital='"+jgid+"'
		String sql="SELECT REQUESTELECTRONIC as SQDH,REQUESTDOCTORALIAS as YSMC ,APPROVEDOCTORALIAS as YSMC ,APPROVEDATE as ZXSJ,REPORTSCONCLUSION as BGZD,REPORTSEVIDENCES as BGMS FROM vhis a" +
				" where a.AdmissionID='"+mzxh+"' and a.AdmissionSource='"+yllb+"' and a.StudiesModalities in "+jclb+" and APPROVEDATE>='"+dateFrom+"' and  APPROVEDATE<='"+dateTo+"'";
		List<Map<String, Object>> list = fs.findQyMap(sql);
		if(yllb.equals("50")){
			res.put("body", SchemaUtil.setDictionaryMassageForList(list, "YJ_JCSQ_BC_CIC"));
		}else{
			res.put("body", SchemaUtil.setDictionaryMassageForList(list, "YJ_JCSQ_BC_WAR"));
		}
		
		return res;
	}
	/**
	 * 获取住院已开检查申请单主诉信息
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void getZsxx_WAR(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		try {
			String zyh = body.get("zyh") + "";
			String sqlsqdh="select max(SQDH) as SQDH from YJ_JCSQ_KD01 a, ZY_BQYZ b where a.SQDH = b.YZZH  and b.ZYH =:ZYH and a.YLLB = 2";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", zyh);
			Map<String, Object> sqdhmap=dao.doSqlLoad(sqlsqdh,parameters);
			String sqdh=sqdhmap.get("SQDH")==null?"":sqdhmap.get("SQDH")+"";
			String sqlzsxx="select  a.zsxx as ZSXX, a.xbs as XBS, a.gms as GMS, a.tgjc as TGJC, a.fzjc as FZJC, a.jws as JWS,a.xl as XL,a.xlv as XLV,case a.bzxx when 'null' then null else a.bzxx end as BZXX  from YJ_JCSQ_KD01 a where a.sqdh=:SQDH";
			Map<String, Object> parameters1 = new HashMap<String, Object>();
			parameters1.put("SQDH", sqdh);
			Map<String, Object> zsxxmap=dao.doSqlLoad(sqlzsxx,parameters1);

			res.put("body", zsxxmap);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("获得住院检查申请单信息失败", e);
		}
	}
}