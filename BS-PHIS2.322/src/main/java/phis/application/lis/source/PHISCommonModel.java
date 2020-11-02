package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.util.context.Context;

/**
 * @description 公用信息相关操作Model
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class PHISCommonModel extends PHISBaseModel {
	
	protected Logger logger = LoggerFactory.getLogger(PHISCommonModel.class);
	protected BaseDAO dao;
	/**
	 * 构造方法
	 * @param ctx
	 */
	public PHISCommonModel(Context ctx) {
		super(ctx);
		dao = new BaseDAO(ctx);
	}
	
	public PHISCommonModel(BaseDAO dao){
		this.dao = dao;
	}
	
	/**
	 * 获取费用信息
	 * @param body
	 * @return
	 * @throws ModelOperationException
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> getzfbl(Map<String, Object> body)
			throws ModelOperationException {
		
		BSPHISUtil phisUtil = new BSPHISUtil();
		
		try {
			return phisUtil.getzfbl(body, ctx, dao);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelOperationException("Get person data failed.", e);
		}
	}
	/**
	 * 获取门诊诊断
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> getPatientDiagnose(Map<String, Object> body,Context ctx)
	throws ModelOperationException {
		String jgid = body.get("jgid").toString();
		Long brid = Long.parseLong(body.get("brid").toString());
		Long jzxh = Long.parseLong(body.get("jzxh").toString());
		StringBuffer zd_hql = new StringBuffer();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JGID", jgid);
		params.put("BRID", brid);
		params.put("JZXH", jzxh);
		if(brid != null && jgid!="" && jzxh!= null){
			zd_hql.append("select ZDXH as zdxh,ZDMC as zdmc from MS_BRZD "
					+" where JGID=:JGID and BRID=:BRID and JZXH=:JZXH order by ZZBZ DESC");
		}else{
			zd_hql.append("select ZDXH as zdxh,ZDMC as zdmc from MS_BRZD "
					+"where JGID=:JGID and BRID=:BRID and JZXH=:JZXH order by ZZBZ DESC");
		}
		List<Map<String, Object>> zd_list = new ArrayList<Map<String, Object>>();
		try {
			zd_list = dao.doQuery(zd_hql.toString(), params);		
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		
		if(zd_list.size() == 0){
			res.put("ZDXH", 0);
			res.put("ZDMC", "");
			res.put("MSG", "未查询到诊断信息!");
			logger.error("未查询到机构ID: "+jgid+" ,病人ID："+brid+" ,就诊序号:"+jzxh+" ,的诊断信息！");
		}else{
			res.put("ZDXH", zd_list.get(0).get("zdxh"));
			res.put("ZDMC", zd_list.get(0).get("zdmc"));			
		}
		return res;
	}
	/**
	 * 获取住院诊断
	 * @author zhouyl
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> getPatientHospital(Map<String, Object> body,Context ctx)
	throws ModelOperationException {
		String jgid = body.get("jgid").toString();
		Long zyh = Long.parseLong(body.get("zyh").toString());
		StringBuffer zd_hql = new StringBuffer();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> params1 = new HashMap<String, Object>();
		Map<String, Object> params2 = new HashMap<String, Object>();
		params1.put("JGID", jgid);
		params1.put("ZYH", zyh);
		//判读诊断类别ZDLB>=2 ZDLB排序 取第一条
		if(jgid!="" && zyh!= null){
			zd_hql.append("select ZDXH as zdxh from ZY_RYZD where JGID=:JGID  and ZYH=:ZYH and ZDLB >= 2 order by ZDLB");
		}else{
			zd_hql.append("select ZDXH as zdxh from ZY_RYZD where JGID=:JGID  and ZYH=:ZYH and ZDLB >= 2 order by ZDLB");
		}
		List<Map<String, Object>> zd_list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zd_list2 = new ArrayList<Map<String, Object>>();
		try {
			zd_list1 = dao.doQuery(zd_hql.toString(), params1);		
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}		
		if(zd_list1.size() == 0){
			res.put("ZDXH", 0);
			res.put("JBMC", "");
			logger.error("未查询到机构ID: "+jgid+",就诊序号:"+zyh+" ,的住院诊断信息！");
		}else{
			res.put("ZDXH", zd_list1.get(0).get("zdxh"));
			params2.put("JBXH", zd_list1.get(0).get("zdxh"));
			//根据ZDXH 查询表gy_jbbm jbmc
			StringBuffer hql = new StringBuffer();
			hql.append("select JBMC as jbmc from GY_JBBM where JBXH=:JBXH");		
			try {
				zd_list2 = dao.doQuery(hql.toString(),params2);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}		
			if(zd_list2.size() == 0){
				logger.error("未查询到疾病序号为"+ zd_list1.get(0).get("zdxh")+"的疾病名称信息！");
			}else{
			res.put("JBMC", zd_list2.get(0).get("jbmc"));
			}
		}
		return res;
	}	
	
	/**
	 * 根据挂号科室查科室代码
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> findKsdm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res= new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("KSDM", Long.parseLong(body.get("ksdm") + ""));
		String hql = "select a.MZKS as MZKS from MS_GHKS a where a.KSDM=:KSDM ";
		
//		Map<String, Object> params_xtcs = new HashMap<String, Object>();
//		params_xtcs.put("CSMC", "JIANYANSERVERIP");
//		String hql_xtcs = "select a.CSZ as CSZ from GY_XTCS a where a.CSMC=:CSMC ";
		
		try {
			List<Map<String,Object>> list = dao.doSqlQuery(hql,params);
			res.put("MZKS", list.get(0).get("MZKS"));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据挂号科室代码查询门诊科室代码查询出错！", e);
		}
		
//		try {
//			List<Map<String,Object>> list_xtcs = dao.doSqlQuery(hql_xtcs,params_xtcs);
			
			res.put("JIANYANSERVERIP", ParameterUtil.getParameter(ParameterUtil.getTopUnitId(), "JIANYANSERVERIP", ctx));
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException("查询检验服务器IP地址失败！", e);
//		}
		return res;
	}
	
	/**
	 * 获取检验系统参数
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLisXTCS(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res= new HashMap<String, Object>();
//		
//		Map<String, Object> params_xtcs = new HashMap<String, Object>();
//		params_xtcs.put("CSMC", "JIANYANSERVERIP");
//		String hql_xtcs = "select a.CSZ as CSZ from GY_XTCS a where a.CSMC=:CSMC ";
		
//		try {
//			List<Map<String,Object>> list_xtcs = dao.doSqlQuery(hql_xtcs,params_xtcs);
			res.put("JIANYANSERVERIP", ParameterUtil.getParameter(ParameterUtil.getTopUnitId(), "JIANYANSERVERIP", ctx));
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException("查询检验服务器IP地址失败！", e);
//		}
		return res;
	}
}
