package phis.application.pha.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.util.exp.ExpressionProcessor;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.SchemaUtil;

/**
 * 药房发药统计Model
 * 
 * @author caijy
 * 
 */
public class PharmacyStatisticalModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyStatisticalModel.class);

	public PharmacyStatisticalModel(BaseDAO dao) {
		this.dao = dao;
	}

//	/**
//	 * 
//	 * @author caijy
//	 * @createDate 2014-2-27
//	 * @description 药房发药统计查询
//	 * @updateInfo
//	 * @param body
//	 * @param ctx
//	 * @return
//	 * @throws ModelDataOperationException
//	 */
//	public List<Map<String, Object>> yffytjQuery1(Map<String, Object> body,
//			Context ctx) throws ModelDataOperationException {
//		int tjfs = MedicineUtils.parseInt(body.get("TJFS"));// 获取统计方式
//		String sql = "", dicSql = "";
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String manageUnit = user.getManageUnit().getId();
//		String ref = user.getManageUnit().getRef();
//		PharmacyDispensingStatisticsUtil pdsUtil = new PharmacyDispensingStatisticsUtil();
//		// key sql中的key valueKey dicSql中的value tmpKey dicSql中的key
//		String key = "", valueKey = "", tmpKey = "";
//		Map<String, Object> dicPar = new HashMap<String, Object>();
//		if (tjfs == 1) {// 按发药窗口
//			sql = pdsUtil.CKStatisticalMethod(body);
//			dicSql = pdsUtil.CKDataSql();
//			key = "FYCK";
//			valueKey = "CKMC";
//			tmpKey = "CKBH";
//			dicPar.put("al_jgid", manageUnit);
//			dicPar.put("al_yfsb",
//					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
//		} else if (tjfs == 2) {// 按病人性质
//			sql = pdsUtil.BRXZStatisticalMethod(body);
//			dicSql = pdsUtil.BRXZDataSql();
//			key = "BRXZ";
//			valueKey = "XZMC";
//			tmpKey = "BRXZ";
//		} else if (tjfs == 3) {// 按特殊药品
//			sql = pdsUtil.TSYPStatisticalMethod(body);
//			// dicSql = pdsUtil.TSYPDataSql();
//			key = "TSYP";
//			valueKey = "DMMC";
//			tmpKey = "DMSB";
//		} else if (tjfs == 4) {// 按开单科室
//			sql = pdsUtil.KDKSStatisticalMethod(body);
//			dicSql = pdsUtil.KDKSDataSql();
//			dicPar.put("al_jgid", ref);
//			key = "KSDM";
//			valueKey = "KSMC";
//			tmpKey = "KSDM";
//		} else if (tjfs == 5) {// 按发药人
//			sql = pdsUtil.FYRStatisticalMethod(body);
//			dicSql = pdsUtil.FYRDataSql();
//			dicPar.put("al_jgid", user.getManageUnit().getRef());
//			key = "FYGH";
//			valueKey = "YGXM";
//			tmpKey = "YGDM";
//
//		} else {// 其余返回
//			throw new ModelDataOperationException("统计方式参数不正确!");
//		}
//		Map<String, Object> parameter = new HashMap<String, Object>();
//		parameter.put("adt_start", String.valueOf(body.get("KSSJ")));
//		parameter.put("adt_end", String.valueOf(body.get("JSSJ")));
//		parameter.put("al_jgid", manageUnit);// 机构ID
//		parameter.put("al_yfsb",
//				MedicineUtils.parseLong(user.getProperty("pharmacyId")));// 药房识别
//		try {
//			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
//			parameter.remove("adt_start");
//			parameter.remove("adt_end");
//			List<Map<String, Object>> dicItems = null;
//			if (tjfs != 3) {// 非特殊药品时需要查询
//				dicItems = dao.doSqlQuery(dicSql, dicPar);
//			}
//			String value = "";
//			double pjcfe = 0, sum_cfs = 0;
//			if (list != null) {
//				for (Map<String, Object> map : list) {
//					value = "<全部>";
//					map.put("FYJE", String.format("%1$.2f", map.get("FYJE")));
//					if (dicItems != null) {
//						for (Map<String, Object> item : dicItems) {
//							if (String.valueOf(map.get(key)).equals(
//									String.valueOf(item.get(tmpKey)))) {
//								value = String.valueOf(item.get(valueKey));
//							}
//						}
//					} else if (tjfs == 3) {// 特殊药品时直接从pecialMedicines中获取项目
//						value = DictionaryController.instance()
//								.get("phis.dictionary.pecialMedicines")
//								.getText(map.get(key) + "");
//						if ("".equals(value) || "null".equals(value)) {
//							value = "<全部>";
//						}
//					}
//					pjcfe = MedicineUtils.formatDouble(
//							2,
//							Double.parseDouble(map.get("FYJE") + "")
//									/ Double.parseDouble(map.get("CFZS") + ""));
//					sum_cfs += Double.parseDouble(map.get("CFZS") + "");// 所有的处方总数
//					map.put("YPMC", value);
//					map.put("PJCFE", pjcfe);
//					// 虚拟字段
//					map.put("VIRTUAL_FIELD", String.valueOf(map.get(key)));
//				}
//				// 计算比例
//				for (Map<String, Object> map : list) {
//					map.put("BL", Double.parseDouble(map.get("CFZS") + "")
//							/ sum_cfs * 100);
//				}
//			}
//
//			// SchemaUtil.setDictionaryMassageForList(list,
//			// "STATISTICALMETHOD_CK_LIST");
//			return list;
//			// res.put("body", list);
//		} catch (Exception e) {
//			throw new ModelDataOperationException("药房发药统计查询失败!"
//					+ e.getMessage(), e);
//		}
//	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 药房发药统计汇总查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> yffytjQuery(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		int tjfs = MedicineUtils.parseInt(body.get("TJFS"));// 获取统计方式
		int fylb = MedicineUtils.parseInt(body.get("FYLB"));// 获取发药类别
		List<?> cnd1=(List<?>)body.get("cnd1");//门诊查询条件
		List<?> cnd2=(List<?>)body.get("cnd2");//住院查询条件
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		if (tjfs == 1) {// 按发药窗口
			list_ret= getFytjByFYCK(cnd1,ctx);
		}else if(tjfs == 2) {// 按病人性质
			list_ret= getFytjByBRXZ(cnd1,cnd2,fylb,ctx);
		}else if (tjfs == 3) {// 按特殊药品
			list_ret= getFytjByTSYP(cnd1,cnd2,fylb,ctx);
		 }else if (tjfs == 4) {// 按开单科室
			list_ret= getFytjByKDKS(cnd1,ctx);
		}else if (tjfs == 5) {// 按发药人
			list_ret= getFytjByFYR(cnd1,cnd2,fylb,ctx);
		}else if (tjfs == 6) {// 按开单医生
			list_ret= getFytjByKDYS(cnd1,ctx);
		}else if (tjfs == 7) {// 按基本药品（全院）
			list_ret= getFytjByJYLX(cnd1,cnd2, fylb, ctx);
		}else if (tjfs == 8) {// 按基本药品（医生）
			list_ret= getFytjYSByJYLX(cnd1,cnd2, fylb, ctx);
		}
		double pjcfe = 0, sum_cfs = 0;
		for(Map<String,Object> map:list_ret){
			if(Double.parseDouble(map.get("CFZS") + "")==0){
				pjcfe=0;
			}else{
			pjcfe = MedicineUtils.formatDouble(
					2,
					Double.parseDouble(map.get("FYJE") + "")
							/ Double.parseDouble(map.get("CFZS") + ""));
			}
			map.put("PJCFE", pjcfe);
			sum_cfs += Double.parseDouble(map.get("CFZS") + "");// 所有的处方总数
		}
		if(tjfs == 8) {// 按基本药品（医生）BL重写
			for(Map<String,Object> map:list_ret){							
				map.put("BL", MedicineUtils.formatDouble(2, Double.parseDouble(map.get("FYJE") + "")/ Double.parseDouble(map.get("SUM") + "") * 100)) ;
			}
		}else{		
			for(Map<String,Object> map:list_ret){
				map.put("BL", MedicineUtils.formatDouble(2, Double.parseDouble(map.get("CFZS") + "")/ sum_cfs * 100)) ;
			}
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 按发药窗口查询汇总信息
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjByFYCK(List<?> cnd,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql=new StringBuffer();
		hql.append("select nvl(c.CKMC,'<全部>') as YPMC,a.FYCK as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE  from  MS_CF02 b,MS_CF01 a left outer join YF_CKBH c on c.YFSB=a.YFSB and c.JGID=a.JGID and c.CKBH=a.FYCK  where a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb and ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			hql.append(" group by c.CKMC,a.FYCK");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 按病人性质查询汇总信息
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjByBRXZ(List<?> cnd1,List<?> cnd2,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql_mz=new StringBuffer();
		hql_mz.append("select nvl(d.XZMC,'<全部>') as YPMC,c.BRXZ as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE  from  MS_CF02 b,MS_CF01 a, MS_BRDA c left outer join GY_BRXZ d on d.BRXZ=c.BRXZ where c.BRID = a.BRID and a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_zy=new StringBuffer();
		hql_zy.append("select nvl(c.XZMC,'<全部>') as YPMC,c.BRXZ as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_ZYFYMX a,ZY_BRRY b left outer join GY_BRXZ c on c.BRXZ=b.BRXZ where a.ZYH=b.ZYH and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_jc=new StringBuffer();
		hql_jc.append("select nvl(c.XZMC,'<全部>') as YPMC,c.BRXZ as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_JCFYMX a,JC_BRRY b left outer join GY_BRXZ c on c.BRXZ=b.BRXZ where a.ZYH=b.ZYH and a.JGID = :jgid and a.YFSB = :yfsb ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_mz.append(" group by d.XZMC,c.BRXZ");
			hql_zy.append(" group by c.XZMC,c.BRXZ");
			hql_jc.append(" group by c.XZMC,c.BRXZ");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			if(fylb==1){
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				return list_mz;
			}
			else if(fylb==2){
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				return list_zy;
			}else if(fylb==3){
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				return list_jc;
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret=hbList(list_mz,list_zy,list_jc,"VIRTUAL_FIELD");
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 按特殊药品查询汇总信息
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjByTSYP(List<?> cnd1,List<?> cnd2,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql_mz=new StringBuffer();
		hql_mz.append("select '' as YPMC,c.TSYP as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_zy=new StringBuffer();
		hql_zy.append("select '' as YPMC,b.TSYP as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b  where a.YPXH=b.YPXH and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_jc=new StringBuffer();
		hql_jc.append("select '' as YPMC,b.TSYP as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b  where a.YPXH=b.YPXH and a.JGID = :jgid and a.YFSB = :yfsb ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_mz.append(" group by c.TSYP");
			hql_zy.append(" group by b.TSYP");
			hql_jc.append(" group by b.TSYP");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}
			else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret=hbList(list_mz,list_zy,list_jc,"VIRTUAL_FIELD");
			}
			for(Map<String,Object> map:list_ret){
				String value = DictionaryController.instance()
						.get("phis.dictionary.pecialMedicines")
						.getText(map.get("VIRTUAL_FIELD") + "");
				if(value==null||"".equals(value)){
					value="<全部>";
				}
				map.put("YPMC", value);
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	
	/**
	 * 
	 * @author hujian
	 * @createDate 2020-05-21
	 * @description 按基本药品查询汇总信息（全院）
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjByJYLX(List<?> cnd1,List<?> cnd2,int fylb,Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql_mz=new StringBuffer();
		hql_mz.append("select '' as YPMC,c.JYLX as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_zy=new StringBuffer();
		hql_zy.append("select '' as YPMC,b.JYLX as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b  where a.YPXH=b.YPXH and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_jc=new StringBuffer();
		hql_jc.append("select '' as YPMC,b.JYLX as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b  where a.YPXH=b.YPXH and a.JGID = :jgid and a.YFSB = :yfsb ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_mz.append(" group by c.JYLX");
			hql_zy.append(" group by b.JYLX");
			hql_jc.append(" group by b.JYLX");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}
			else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret=hbList(list_mz,list_zy,list_jc,"VIRTUAL_FIELD");
			}
			for(Map<String,Object> map:list_ret){
				String value = DictionaryController.instance()
						.get("phis.dictionary.jylx")
						.getText(map.get("VIRTUAL_FIELD") + "");
				if(value==null||"".equals(value)){
					value="<全部>";
				}
				map.put("YPMC", value);
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	
	/**
	 * 
	 * @author hujian
	 * @createDate 2020-05-21
	 * @description 按基本药品查询汇总信息（医生）
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjYSByJYLX(List<?> cnd1,List<?> cnd2,int fylb,Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql_mz=new StringBuffer();
		hql_mz.append("select '' as YPMC,c.JYLX as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_zy=new StringBuffer();
		hql_zy.append("select '' as YPMC,b.JYLX as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE,c.ysgh as YSDM,(select d.personname from sys_personnel d where  d.personid=c.ysgh) as YSXM from YF_ZYFYMX a,YK_TYPK b,zy_bqyz c  where a.YPXH=b.YPXH and a.JGID = :jgid and a.YFSB = :yfsb and a.yzxh=c.jlxh ");
		StringBuffer hql_jc=new StringBuffer();
		hql_jc.append("select '' as YPMC,b.JYLX as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE,0 as YSDM from YF_JCFYMX a,YK_TYPK b  where a.YPXH=b.YPXH and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_sum1=new StringBuffer();
		StringBuffer hql_sum2=new StringBuffer();
		StringBuffer hql_sum3=new StringBuffer();
		StringBuffer hql_mznew=new StringBuffer();
		StringBuffer hql_zynew=new StringBuffer();
		StringBuffer hql_jcnew=new StringBuffer();
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_mz.append(" group by c.JYLX,a.ysdm order by ysdm,c.JYLX");
			hql_zy.append(" group by b.JYLX,c.ysgh order by ysdm,b.JYLX");
			hql_jc.append(" group by b.JYLX");
			hql_sum1.append("select sum(FYJE) as sum,YSDM as YSID from ("+hql_mz+") group by YSDM");
			hql_sum2.append("select sum(FYJE) as sum,YSDM as YSID from ("+hql_zy+") group by YSDM");
			hql_sum3.append("select sum(FYJE) as sum,YSDM as YSID from ("+hql_jc+") group by YSDM");
			hql_mznew.append("select * from ("+hql_mz+") tb1,("+hql_sum1+") tb2 where tb1.YSDM = tb2.YSID");
			hql_zynew.append("select * from ("+hql_zy+") tb1,("+hql_sum2+") tb2 where tb1.YSDM = tb2.YSID");	
			hql_jcnew.append("select * from ("+hql_jc+") tb1,("+hql_sum3+") tb2 where tb1.YSDM = tb2.YSID");	
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			if(fylb==1){				
								
				list_ret=dao.doSqlQuery(hql_mznew.toString(), map_par);
				//list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);				
			}
			else if(fylb==2){				
							
				list_ret=dao.doSqlQuery(hql_zynew.toString(), map_par);
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){				
							
				list_ret=dao.doSqlQuery(hql_jcnew.toString(), map_par);
				//list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				//List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				//List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				//List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mznew.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zynew.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jcnew.toString(), map_par);
				for(Map<String,Object> data : list_zy){
					list_mz.add(data);
				}
				for(Map<String,Object> data : list_jc){
					list_mz.add(data);
				}
				list_ret=list_mz;				
			}
			for(Map<String,Object> map:list_ret){
				String value = DictionaryController.instance()
						.get("phis.dictionary.jylx")
						.getText(map.get("VIRTUAL_FIELD") + "");
				if(value==null||"".equals(value)){
					value="<全部>";
				}
				map.put("YPMC", value);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;		
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 按开单科室查询汇总信息
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjByKDKS(List<?> cnd,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql=new StringBuffer();
		hql.append("select nvl(c.OFFICENAME,'<全部>') as YPMC,a.KSDM as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE  FROM  MS_CF02 b,MS_CF01 a left outer join SYS_Office c on c.ORGANIZCODE=a.JGID and c.ID=a.KSDM  where a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb and ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			hql.append(" group by c.OFFICENAME,a.KSDM");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	//按开单医生统计
	public List<Map<String, Object>> getFytjByKDYS(List<?> cnd,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql=new StringBuffer();
		hql.append("select nvl(c.PERSONNAME,'<全部>') as YPMC,a.YSDM as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS," +
				" sum(b.HJJE) as FYJE  FROM  MS_CF02 b,MS_CF01 a left outer join SYS_Personnel c on c.PERSONID=a.YSDM " +
				" where a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb and ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			hql.append(" group by c.PERSONNAME,a.YSDM");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 按发药人查询汇总信息
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjByFYR(List<?> cnd1,List<?> cnd2,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb);
		StringBuffer hql_mz=new StringBuffer();
		hql_mz.append("select nvl(c.PERSONNAME,'<全部>') as YPMC,a.FYGH as VIRTUAL_FIELD, count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE  FROM  MS_CF02 b,MS_CF01 a left outer join SYS_Personnel c on a.FYGH=c.PERSONID where  a.CFSB = b.CFSB and  a.ZFPB = 0  and a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_zy=new StringBuffer();
		hql_zy.append("select nvl(b.PERSONNAME,'<全部>') as YPMC,a.QRGH as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_ZYFYMX a left outer join SYS_Personnel b on a.QRGH=b.PERSONID where  a.JGID = :jgid and a.YFSB = :yfsb ");
		StringBuffer hql_jc=new StringBuffer();
		hql_jc.append("select nvl(b.PERSONNAME,'<全部>') as YPMC,a.QRGH as VIRTUAL_FIELD,0 as CFZS,sum(a.FYJE) as FYJE from YF_JCFYMX a left outer join SYS_Personnel b on a.QRGH=b.PERSONID where  a.JGID = :jgid and a.YFSB = :yfsb ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
			hql_mz.append(" group by c.PERSONNAME,a.FYGH");
			hql_zy.append(" group by b.PERSONNAME,a.QRGH");
			hql_jc.append(" group by b.PERSONNAME,a.QRGH");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			if(fylb==1){
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				return list_mz;
			}
			else if(fylb==2){
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				return list_zy;
			}else if(fylb==3){
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				return list_jc;
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret=hbList(list_mz,list_zy,list_jc,"VIRTUAL_FIELD");
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 2个list,将相同的记录合并金额
	 * @updateInfo by caijy at 2015-3-11 for 增加家床部分
	 * @param list_o
	 * @param list_t
	 * @param text 用哪个列名来判断是否需要归并
	 * @return
	 */
	public List<Map<String,Object>> hbList(List<Map<String,Object>> list_o,List<Map<String,Object>> list_t,List<Map<String,Object>> list_s,String text){
		Set<Long> set = new HashSet<Long>();
		List<Long> keyList = new ArrayList<Long>();
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map:list_o){
			if(set.add(MedicineUtils.parseLong(map.get(text)))){
				keyList.add(MedicineUtils.parseLong(map.get(text)));
			}
		}
		for(Map<String,Object> map:list_t){
			if(set.add(MedicineUtils.parseLong(map.get(text)))){
				keyList.add(MedicineUtils.parseLong(map.get(text)));
			}
		}
		for(Map<String,Object> map:list_s){
			if(set.add(MedicineUtils.parseLong(map.get(text)))){
				keyList.add(MedicineUtils.parseLong(map.get(text)));
			}
		}
		for(long key:keyList){
			Map<String,Object> m=new HashMap<String,Object>();
			double fyje=0;
			for(Map<String,Object> map_o:list_o){
				if(MedicineUtils.parseLong(map_o.get(text))==key){
					fyje=MedicineUtils.parseDouble(map_o.get("FYJE"));
					m.putAll(map_o);
					continue;
				}
			}
			for(Map<String,Object> map_t:list_t){
				if(MedicineUtils.parseLong(map_t.get(text))==key){
					if(m.size()==0){
						m.putAll(map_t);
					}else{
						fyje+=MedicineUtils.parseDouble(map_t.get("FYJE"));
						m.put("FYJE", fyje);
					}
					continue;
				}
			}
			for(Map<String,Object> map_s:list_s){
				if(MedicineUtils.parseLong(map_s.get(text))==key){
					if(m.size()==0){
						m.putAll(map_s);
					}else{
						fyje+=MedicineUtils.parseDouble(map_s.get("FYJE"));
						m.put("FYJE", fyje);
					}
					continue;
				}
			}
			list_ret.add(m);
		}
		return list_ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 药房发药统计明细查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
//	public List<Map<String, Object>> yffyDetailsQuery1(Map<String, Object> body,
//			Context ctx) throws ModelDataOperationException {
//		int tjfs = MedicineUtils.parseInt(body.get("TJFS"));// 获取统计方式
//		String sql = "";
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String manageUnit = user.getManageUnit().getId();
//		PharmacyDispensingStatisticsUtil pdsUtil = new PharmacyDispensingStatisticsUtil();
//		if (tjfs == 1) {// 按发药窗口
//			sql = pdsUtil.CKStatisticalDetails(body);
//		} else if (tjfs == 2) {// 按病人性质
//			sql = pdsUtil.BRXZStatisticalDetails(body);
//		} else if (tjfs == 3) {// 按特殊药品
//			sql = pdsUtil.TSYPStatisticalDetails(body);
//		} else if (tjfs == 4) {// 按开单科室
//			sql = pdsUtil.KDKSStatisticalDetails(body);
//		} else if (tjfs == 5) {// 按发药人
//			sql = pdsUtil.FYRStatisticalDetails(body);
//		} else {// 其余返回
//			throw new ModelDataOperationException("统计方式参数不正确!");
//		}
//		Map<String, Object> parameter = new HashMap<String, Object>();
//		parameter.put("adt_start", String.valueOf(body.get("KSSJ")));
//		parameter.put("adt_end", String.valueOf(body.get("JSSJ")));
//		parameter.put("al_jgid", manageUnit);// 机构ID
//		parameter.put("al_yfsb",
//				Long.parseLong((String) user.getProperty("pharmacyId")));// 药房识别
//		try {
//			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
//			return list;
//		} catch (Exception e) {
//			throw new ModelDataOperationException("药房发药统计明细查询失败!"
//					+ e.getMessage(), e);
//		}
//	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计明细查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> yffyDetailsQuery(Map<String, Object> body,Map<String, Object> req,
			Context ctx) throws ModelDataOperationException {
		int tjfs = MedicineUtils.parseInt(body.get("TJFS")+"");// 获取统计方式
		int fylb = MedicineUtils.parseInt(body.get("FYLB"));// 获取发药类别
		List<?> cnd1=(List<?>)body.get("cnd1");//门诊查询条件
		List<?> cnd2=(List<?>)body.get("cnd2");//住院查询条件
		String key=MedicineUtils.parseString(body.get("VIRTUAL_FIELD"));
		int tag=0;//用于标识是显示在界面上 还是显示在报表中, 0是界面 1是报表
		if(body.containsKey("TAG")){
			tag=MedicineUtils.parseInt(body.get("TAG"));
		}else
		if (tjfs == 1) {// 按发药窗口
			return getFytjmxByFYCK(cnd1,key,req,ctx,tag);
		}else
		if (tjfs == 2) {// 按病人性质
			return getFytjmxByBRXZ(cnd1,cnd2,key,fylb,req,ctx,tag);
		}else
		if (tjfs == 3) {// 按特殊药品
			return getFytjmxByTSYP(cnd1,cnd2,key,fylb,req,ctx,tag);
		}else
		if (tjfs == 4) {// 按开单科室
			return getFytjmxByKDKS(cnd1,key,req,ctx,tag);
		}else
		if (tjfs == 5) {// 按发药人
			return getFytjmxByFYR(cnd1,cnd2,key,fylb,req,ctx,tag);
		}else
		if (tjfs == 6) {// 按开单医生
			return getFytjmxByKDYS(cnd1,key,req,ctx,tag);
		}else if (tjfs == 7) {// 基药（全院）
			return getFytjmxByJYLX(cnd1,cnd2,key,fylb,req,ctx,tag);
		}else if (tjfs == 8) {// 基药（医生）
			return getFytjmxYSByJYLX(cnd1,cnd2,key,fylb,req,ctx,tag);
		}
		return null;
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计明细按发药窗口查询
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxByFYCK(List<?> cnd,String key,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql=new StringBuffer();
		hql.append("select * from (");
		hql.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f where a.FYCK=:fyck and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid and ");
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			hql.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
			hql.append(") g order by YPMC");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("fyck", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",FYCK="+MedicineUtils.parseInt(key));
			if(tag==0){
				ret=getPageInfoRecord(req, map_par, hql.toString(), null);
			}else{
				List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
				ret.put("body", list_ret);
			}
			//ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计明细按病人性质查询
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxByBRXZ(List<?> cnd1,List<?> cnd2,String key,int fylb,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,MS_BRDA g where g.BRID=a.BRID and g.BRXZ=:brxz and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,ZY_BRRY f where a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.ZYH=f.ZYH and f.BRXZ=:brxz and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,JC_BRRY f where a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.ZYH=f.ZYH and f.BRXZ=:brxz and a.YFSB=:yfsb and a.JGID=:jgid ");
		//List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
			hql_mz.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_zy.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			hql_jc.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("brxz", MedicineUtils.parseLong(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",BRXZ="+MedicineUtils.parseLong(key));
			if(fylb==1){
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_mz.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
					ret.put("body", list_ret);
				}
				//list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				if(tag==0){
				ret=getPageInfoRecord(req, map_par, hql_zy.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
					ret.put("body", list_ret);
				}
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				if(tag==0){
				ret=getPageInfoRecord(req, map_par, hql_jc.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
					ret.put("body", list_ret);
				}
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else{
				StringBuffer sql=new StringBuffer();
				sql.append(hql_mz).append(" union all ").append(hql_zy).append(" union all ").append(hql_jc);
//				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
//				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
//				list_ret=hbList(list_mz,list_zy,"YPXH");
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, sql.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(sql.toString(), map_par);
					ret.put("body", list_ret);
				}
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计明细按特殊药品查询
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxByTSYP(List<?> cnd1,List<?> cnd2,String key,int fylb,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f where c.TSYP=:tsyp and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where b.TSYP=:tsyp and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH  and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where b.TSYP=:tsyp and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH  and a.YFSB=:yfsb and a.JGID=:jgid ");
		//List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
			hql_mz.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_zy.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			hql_jc.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("tsyp", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",BRXZ="+MedicineUtils.parseInt(key));
			if(fylb==1){
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_mz.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
					ret.put("body", list_ret);
				}
				//list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_zy.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else if(fylb==3){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_jc.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else{
//				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
//				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
//				list_ret=hbList(list_mz,list_zy,"YPXH");
				StringBuffer sql=new StringBuffer();
				sql.append(hql_mz).append(" union all ").append(hql_zy).append(" union all ").append(hql_jc);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, sql.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(sql.toString(), map_par);
					ret.put("body", list_ret);
				}
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
		
	}
	
	/**
	 * 
	 * @author hujian
	 * @createDate 2020-5-21
	 * @description 发药统计明细按基药类型品查询（全院）
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxByJYLX(List<?> cnd1,List<?> cnd2,String key,int fylb,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f where c.JYLX=:JYLX and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where b.JYLX=:JYLX and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH  and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where b.JYLX=:JYLX and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH  and a.YFSB=:yfsb and a.JGID=:jgid ");
		//List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
			hql_mz.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_zy.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			hql_jc.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("JYLX", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",JYLX="+MedicineUtils.parseInt(key));
			if(fylb==1){
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_mz.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
					ret.put("body", list_ret);
				}
				//list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_zy.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else if(fylb==3){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_jc.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else{
//				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
//				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
//				list_ret=hbList(list_mz,list_zy,"YPXH");
				StringBuffer sql=new StringBuffer();
				sql.append(hql_mz).append(" union all ").append(hql_zy).append(" union all ").append(hql_jc);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, sql.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(sql.toString(), map_par);
					ret.put("body", list_ret);
				}
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
		
	}
	
	/**
	 * 
	 * @author hujian
	 * @createDate 2020-5-21
	 * @description 发药统计明细按基药类型品查询（医生）
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxYSByJYLX(List<?> cnd1,List<?> cnd2,String key,int fylb,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f where c.JYLX=:JYLX and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where b.JYLX=:JYLX and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH  and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where b.JYLX=:JYLX and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH  and a.YFSB=:yfsb and a.JGID=:jgid ");
		//List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
			hql_mz.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_zy.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			hql_jc.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("JYLX", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",JYLX="+MedicineUtils.parseInt(key));
			if(fylb==1){
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_mz.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
					ret.put("body", list_ret);
				}
				//list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_zy.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else if(fylb==3){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_jc.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else{
//				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
//				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
//				list_ret=hbList(list_mz,list_zy,"YPXH");
				StringBuffer sql=new StringBuffer();
				sql.append(hql_mz).append(" union all ").append(hql_zy).append(" union all ").append(hql_jc);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, sql.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(sql.toString(), map_par);
					ret.put("body", list_ret);
				}
			}
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
		
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计明细按开单科室查询
	 * @updateInfo
	 * @param cnd
	 * @param key
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxByKDKS(List<?> cnd,String key,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql=new StringBuffer();
		hql.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f where  a.KSDM=:ksdm and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid and ");
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			hql.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ksdm", MedicineUtils.parseLong(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",KSDM="+MedicineUtils.parseLong(key));
			if(tag==0){
				ret=getPageInfoRecord(req, map_par, hql.toString(), null);
			}else{
				List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
				ret.put("body", list_ret);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
	}
	public Map<String, Object> getFytjmxByKDYS(List<?> cnd,String key,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql=new StringBuffer();
		hql.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL," +
				" sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f" +
				" where  a.YSDM=:ysdm and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID" +
				" and a.FYBZ!=0 and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB " +
				" and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid and ");
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			hql.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ysdm", key);
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YSDM="+key);
			if(tag==0){
				ret=getPageInfoRecord(req, map_par, hql.toString(), null);
			}else{
				List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
				ret.put("body", list_ret);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计明细按发药人查询
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFytjmxByFYR(List<?> cnd1,List<?> cnd2,String key,int fylb,Map<String, Object> req,
			Context ctx,int tag)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select c.YPMC as YPMC, c.YPXH as YPXH, e.YFGG as YPGG, e.YFDW as YPDW,sum(b.YPSL*b.CFTS) as FYSL,sum(b.HJJE) as FYJE from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f where a.FYGH=:fygh and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where a.QRGH=:fygh and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD  and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select  b.YPMC as YPMC, b.YPXH as YPXH,c.YFGG as YPGG, c.YFDW as YPDW,sum(a.YPSL) as FYSL,sum(a.FYJE) as FYJE from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e where a.QRGH=:fygh and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD  and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		//List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
			hql_mz.append(" group by c.YPMC,c.YPXH,e.YFGG,e.YFDW");
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			hql_zy.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			hql_jc.append(" group by  b.YPMC,b.YPXH,c.YFGG,c.YFDW");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			//zhaojian 2017-08-24 解决用户账号10位时超过int类型最大值或X结尾时此处转换报错问题
			//map_par.put("fygh", MedicineUtils.parseInt(key));
			map_par.put("fygh", key);
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",FYGH="+key);
			if(fylb==1){
				//list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_mz.toString(), null);	
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
					ret.put("body", list_ret);
				}
			}else if(fylb==2){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_zy.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
					ret.put("body", list_ret);
				}
				
			}else if(fylb==3){
				//list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, hql_jc.toString(), null);
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
					ret.put("body", list_ret);
				}
				
			}else{
//				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
//				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
//				list_ret=hbList(list_mz,list_zy,"YPXH");
				StringBuffer sql=new StringBuffer();
				sql.append(hql_mz).append(" union all ").append(hql_zy).append(" union all ").append(hql_jc);
				if(tag==0){
					ret=getPageInfoRecord(req, map_par, sql.toString(), null);	
				}else{
					List<Map<String,Object>> list_ret=dao.doSqlQuery(sql.toString(), map_par);
					ret.put("body", list_ret);
				}
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计,发药明细查询(按药品)
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> yffyDetailsDetailsQuery(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		int tjfs = MedicineUtils.parseInt(body.get("TJFS"));// 获取统计方式
		int fylb = MedicineUtils.parseInt(body.get("FYLB"));// 获取发药类别
		long ypxh=MedicineUtils.parseLong(body.get("YPXH"));
		List<?> cnd1=(List<?>)body.get("cnd1");//门诊查询条件
		List<?> cnd2=(List<?>)body.get("cnd2");//住院查询条件
		String key=MedicineUtils.parseString(body.get("VIRTUAL_FIELD"));
		if (tjfs == 1) {// 按发药窗口
			return getFytjmxfymxByFYCK(cnd1,key,ypxh,ctx);
		}
		if (tjfs == 2) {// 按病人性质
			return getFytjmxfymxByBRXZ(cnd1,cnd2,key,ypxh,fylb,ctx);
		}
		if (tjfs == 3) {// 按特殊药品
			return getFytjmxfymxByTSYP(cnd1,cnd2,key,ypxh,fylb,ctx);
		}
		if (tjfs == 4) {// 按开单科室
			return getFytjmxfymxByKDKS(cnd1,key,ypxh,ctx);
		}
		if (tjfs == 5) {// 按发药人
			return getFytjmxfymxByFYR(cnd1,cnd2,key,ypxh,fylb,ctx);
		}
		if (tjfs == 7) {// 按基药类型（全院）
			return getFytjmxfymxByJYLX(cnd1,cnd2,key,ypxh,fylb,ctx);
		}
		if (tjfs == 8) {// 按基药类型（医生）
			return getFytjmxfymxYSByJYLX(cnd1,cnd2,key,ypxh,fylb,ctx);
		}
		return null;
		
	}
/**
 * 
 * @author caijy
 * @createDate 2014-6-24
 * @description 发药统计,发药明细查询(按药品)--按发药窗口
 * @updateInfo
 * @param cnd
 * @param key
 * @param ypxh
 * @param ctx
 * @return
 * @throws ModelDataOperationException
 */
	public List<Map<String, Object>> getFytjmxfymxByFYCK(List<?> cnd,String key,long ypxh,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql=new StringBuffer();
		hql.append("select a.CFHM as CFHM, c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,g.PERSONNAME as FYR,h.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,SYS_Personnel g,MS_BRDA h where h.BRID=a.BRID and g.PERSONID=a.FYGH and b.YPXH=:ypxh and a.FYCK=:fyck and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid and ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			map_par.put("fyck", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",FYKC="+MedicineUtils.parseInt(key));
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计,发药明细查询(按药品)--按病人性质
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param ypxh
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjmxfymxByBRXZ(List<?> cnd1,List<?> cnd2,String key,long ypxh,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select a.CFHM as CFHM,c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,h.PERSONNAME as FYR ,g.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,MS_BRDA g,SYS_Personnel h where h.PERSONID=a.FYGH and b.YPXH=:ypxh and g.BRID=a.BRID and g.BRXZ=:brxz and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select '住院发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,g.PERSONNAME as FYR,f.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,ZY_BRRY f,SYS_Personnel g where g.PERSONID=a.QRGH and a.YPXH=:ypxh and  a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.ZYH=f.ZYH and f.BRXZ=:brxz and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select '家床发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,g.PERSONNAME as FYR,f.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,ZY_BRRY f,SYS_Personnel g where g.PERSONID=a.QRGH and a.YPXH=:ypxh and  a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.ZYH=f.ZYH and f.BRXZ=:brxz and a.YFSB=:yfsb and a.JGID=:jgid ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			map_par.put("brxz", MedicineUtils.parseLong(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",BRXZ="+MedicineUtils.parseLong(key));
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret.addAll(list_mz);
				list_ret.addAll(list_zy);
				list_ret.addAll(list_jc);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计,发药明细查询(按药品)--按特殊药品
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param ypxh
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjmxfymxByTSYP(List<?> cnd1,List<?> cnd2,String key,long ypxh,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select a.CFHM as CFHM,c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,g.PERSONNAME as FYR,h.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,SYS_Personnel g,MS_BRDA h where h.BRID=a.BRID and g.PERSONID=a.FYGH and b.YPXH=:ypxh and c.TSYP=:tsyp and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select '住院发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW ,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,ZY_BRRY g where a.ZYH=g.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and b.TSYP=:tsyp and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select '家床发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW ,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,JC_BRRY g where a.ZYH=g.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and b.TSYP=:tsyp and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			map_par.put("tsyp", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",TSYP="+MedicineUtils.parseInt(key));
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret.addAll(list_mz);
				list_ret.addAll(list_zy);
				list_ret.addAll(list_jc);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2020-05-21
	 * @description 发药统计,发药明细查询(按药品)--按基药类型（全院）
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param ypxh
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjmxfymxByJYLX(List<?> cnd1,List<?> cnd2,String key,long ypxh,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select a.CFHM as CFHM,c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,g.PERSONNAME as FYR,h.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,SYS_Personnel g,MS_BRDA h where h.BRID=a.BRID and g.PERSONID=a.FYGH and b.YPXH=:ypxh and c.JYLX=:jylx and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select '住院发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW ,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,ZY_BRRY g where a.ZYH=g.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and b.JYLX=:jylx and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select '家床发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW ,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,JC_BRRY g where a.ZYH=g.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and b.JYLX=:jylx and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			map_par.put("jylx", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",JYLX="+MedicineUtils.parseInt(key));
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret.addAll(list_mz);
				list_ret.addAll(list_zy);
				list_ret.addAll(list_jc);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2020-05-21
	 * @description 发药统计,发药明细查询(按药品)--按基药类型（医生）
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param ypxh
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjmxfymxYSByJYLX(List<?> cnd1,List<?> cnd2,String key,long ypxh,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select a.CFHM as CFHM,c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,g.PERSONNAME as FYR,h.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,SYS_Personnel g,MS_BRDA h where h.BRID=a.BRID and g.PERSONID=a.FYGH and b.YPXH=:ypxh and c.JYLX=:jylx and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select '住院发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW ,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,ZY_BRRY g where a.ZYH=g.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and b.JYLX=:jylx and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select '家床发药' as CFHM, b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW ,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,JC_BRRY g where a.ZYH=g.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and b.JYLX=:jylx and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			map_par.put("jylx", MedicineUtils.parseInt(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",JYLX="+MedicineUtils.parseInt(key));
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret.addAll(list_mz);
				list_ret.addAll(list_zy);
				list_ret.addAll(list_jc);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计,发药明细查询(按药品)--按开单科室
	 * @updateInfo
	 * @param cnd
	 * @param key
	 * @param ypxh
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjmxfymxByKDKS(List<?> cnd,String key,long ypxh,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql=new StringBuffer();
		hql.append("select a.CFHM as CFHM,c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,g.PERSONNAME as FYR,h.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,SYS_Personnel g,MS_BRDA h where h.BRID=a.BRID and g.PERSONID=a.FYGH and b.YPXH=:ypxh and a.KSDM=:ksdm and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid and ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			hql.append(ExpressionProcessor.instance().toString(cnd));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			map_par.put("ksdm", MedicineUtils.parseLong(key));
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",KSDM="+MedicineUtils.parseLong(key));
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-6-24
	 * @description 发药统计,发药明细查询(按药品)--按发药人
	 * @updateInfo
	 * @param cnd1
	 * @param cnd2
	 * @param key
	 * @param ypxh
	 * @param fylb
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFytjmxfymxByFYR(List<?> cnd1,List<?> cnd2,String key,long ypxh,int fylb,
			Context ctx)throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql_mz=new StringBuffer();//统计门诊
		hql_mz.append("select a.CFHM as CFHM,c.YPMC as YPMC, c.YPXH as YPXH, e.YFDW as YPDW,b.YPSL*b.CFTS as FYSL,b.HJJE as FYJE,a.FYRQ as FYRQ,g.PERSONNAME as FYR,h.BRXM as BRXM from  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d,YF_YPXX e,YK_CDDZ f,SYS_Personnel g,MS_BRDA h where a.BRID=h.BRID and  g.PERSONID=a.FYGH and b.YPXH=:ypxh and a.FYGH=:fygh and f.YPCD=d.YPCD and d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0  and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.YFSB=e.YFSB and b.YPXH=e.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_zy=new StringBuffer();//统计住院
		hql_zy.append("select  '住院发药' as CFHM,b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW, c.YFDW as YPDW,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,ZY_BRRY g where g.ZYH=a.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and a.QRGH=:fygh and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		StringBuffer hql_jc=new StringBuffer();//统计家床
		hql_jc.append("select  '家床发药' as CFHM,b.YPMC as YPMC, b.YPXH as YPXH,c.YFDW as YPDW, c.YFDW as YPDW,a.YPSL as FYSL,a.FYJE as FYJE,a.JFRQ as FYRQ,f.PERSONNAME as FYR,g.BRXM as BRXM from YF_JCFYMX a,YK_TYPK b,YF_YPXX c,YK_CDXX d,YK_CDDZ e,SYS_Personnel f,JC_BRRY g where g.ZYH=a.ZYH and f.PERSONID=a.QRGH and a.YPXH=:ypxh and a.QRGH=:fygh and a.YPXH=b.YPXH and a.YFSB=c.YFSB and a.YPXH=c.YPXH and a.YPCD=e.YPCD and a.JGID=d.JGID and e.YPCD=d.YPCD and a.YPXH=d.YPXH and a.YFSB=:yfsb and a.JGID=:jgid ");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		try {
			if(cnd1!=null){
				hql_mz.append(" and "+ExpressionProcessor.instance().toString(cnd1));
			}
			if(cnd2!=null){
				hql_zy.append(" and "+ExpressionProcessor.instance().toString(cnd2));
				hql_jc.append(" and "+ExpressionProcessor.instance().toString(cnd2));
			}
//			hql_mz.append(ExpressionProcessor.instance().toString(cnd1));
//			hql_zy.append(ExpressionProcessor.instance().toString(cnd2));
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("jgid", jgid);
			map_par.put("ypxh", ypxh);
			//zhaojian 2017-08-24 解决用户账号10位时超过int类型最大值或X结尾时此处转换报错问题
			//map_par.put("fygh", MedicineUtils.parseInt(key));
			map_par.put("fygh", key);
			System.out.println("==============>  JGID="+jgid+",YFSB="+yfsb+",YPXH="+ypxh+",FYGH="+key);
			if(fylb==1){
				list_ret=dao.doSqlQuery(hql_mz.toString(), map_par);
			}else if(fylb==2){
				list_ret=dao.doSqlQuery(hql_zy.toString(), map_par);
			}else if(fylb==3){
				list_ret=dao.doSqlQuery(hql_jc.toString(), map_par);
			}else{
				List<Map<String,Object>> list_mz=dao.doSqlQuery(hql_mz.toString(), map_par);
				List<Map<String,Object>> list_zy=dao.doSqlQuery(hql_zy.toString(), map_par);
				List<Map<String,Object>> list_jc=dao.doSqlQuery(hql_jc.toString(), map_par);
				list_ret.addAll(list_mz);
				list_ret.addAll(list_zy);
				list_ret.addAll(list_jc);
			}
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "药房发药统计查询失败!", e);
		}
		return list_ret;
	}
	
	
	public  Map<String,Object> getPageInfoRecord(Map<String,Object> req,Map<String,Object> map_par,String hql,String scamelName)throws ModelDataOperationException{
		StringBuffer hql_count = new StringBuffer();
		hql_count.append("select count(*) as NUM from (")
		.append(hql).append(")");
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
		List<Map<String, Object>> list_count = dao.doSqlQuery(
					hql_count.toString(), map_par);
		if(list_count==null||list_count.size()==0){
			ret.put("totalCount", 0);
			ret.put("body", null);
			return ret;
		}
		ret.put("totalCount", list_count.get(0).get("NUM"));
		MedicineUtils.getPageInfo(req, map_par);
		List<Map<String,Object>> list = dao.doSqlQuery("select * from("+hql+") g order by YPMC", map_par);
		if(scamelName!=null){
			SchemaUtil.setDictionaryMassageForList(list, scamelName);
		}
		ret.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		return ret;
	}
	
}
