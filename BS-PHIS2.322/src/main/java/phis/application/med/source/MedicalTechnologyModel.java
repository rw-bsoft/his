package phis.application.med.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

public class MedicalTechnologyModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(MedicalTechnologyModel.class);

	public MedicalTechnologyModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-4-28
	 * @description 医技收入统计左边list查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryMedicalTechnology(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer hql=new StringBuffer();
		hql.append("select '门诊部' as SJBMMC,1 as SJBM,nvl(count(b.SBXH),0) as JCRS,nvl(sum(b.YLSL*b.YLDJ*b.ZFBL),0) as HJJE from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid");
		hql.append(" union all select '住院部' as SJBMMC,2 as SJBM,nvl(count(b.SBXH),0) as JCRS,nvl(sum(b.YLSL*b.YLDJ*b.ZFBL),0) as HJJE from YJ_ZY01 a,YJ_ZY02 b where a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid");
		hql.append(" union all select '家庭病床部' as SJBMMC,3 as SJBM,nvl(count(b.SBXH),0) as JCRS,nvl(sum(b.YLSL*b.YLDJ*b.ZFBL),0) as HJJE from JC_YJ01 a,JC_YJ02 b where a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid");
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			map_par.put("dateFrom", sdf.parse(MedicineUtils.parseString(body.get("DATEFROM"))+" 00:00:00"));
			map_par.put("dateTo", sdf.parse(MedicineUtils.parseString(body.get("DATETO"))+" 23:59:59"));
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
			double hjje=0;
			int hjrs=0;
			for(Map<String,Object> m:list_ret){
				hjje+=MedicineUtils.parseDouble(m.get("HJJE"));
				hjrs+=MedicineUtils.parseInt(m.get("JCRS"));
			}
			Map<String,Object> map_temp=new HashMap<String,Object>();
			map_temp.put("SJBMMC", "合计");
			map_temp.put("SJBM", 4);
			map_temp.put("JCRS", hjrs);
			map_temp.put("HJJE", MedicineUtils.formatDouble(4, hjje));
			list_ret.add(map_temp);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期格式错误", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医技收入查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-4-29
	 * @description 医技收入统计右边list查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryMedicalTechnologyDetail(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int tjfs=MedicineUtils.parseInt(body.get("TJFS"));//统计方式
		int tjbm=MedicineUtils.parseInt(body.get("TJBM"));//统计部门,门诊/住院/家床
		StringBuffer tableNames=new StringBuffer();
		if(tjbm==1){
			tableNames.append("MS_YJ01 a,MS_YJ02 b,");
		}else if(tjbm==2){
			tableNames.append("YJ_ZY01 a,YJ_ZY02 b,");
		}else if(tjbm==3){
			tableNames.append("JC_YJ01 a,JC_YJ02 b,");
		}
		StringBuffer hql=new StringBuffer();
		if(tjfs==1){//按申检项目
			hql.append("select count(b.SBXH) as JCRS,c.FYMC as JCXM,sum(b.YLSL*b.YLDJ*b.ZFBL) as HJJE from ").append(tableNames).append(" GY_YLSF c where b.YLXH=c.FYXH and a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid group by c.FYMC");
		}else if(tjfs==2){//按申检医生
			hql.append("select count(b.SBXH) as JCRS,c.PERSONNAME as JCXM,sum(b.YLSL*b.YLDJ*b.ZFBL) as HJJE from ").append(tableNames).append(" SYS_Personnel c where a.YSDM=c.PERSONID and a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid group by c.PERSONNAME");
		}else if(tjfs==3){//按申检科室
			hql.append("select count(b.SBXH) as JCRS,c.OFFICENAME as JCXM,sum(b.YLSL*b.YLDJ*b.ZFBL) as HJJE from ").append(tableNames).append(" SYS_Office c where a.KSDM=c.ID and a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid group by c.OFFICENAME");
		}else if(tjfs==4){//按执行科室
			hql.append("select count(b.SBXH) as JCRS,c.OFFICENAME as JCXM,sum(b.YLSL*b.YLDJ*b.ZFBL) as HJJE from ").append(tableNames).append(" SYS_Office c where a.ZXKS=c.ID and a.YJXH=b.YJXH and a.ZXRQ >=:dateFrom and a.ZXRQ<=:dateTo and a.JGID=:jgid group by c.OFFICENAME");
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			map_par.put("dateFrom", sdf.parse(MedicineUtils.parseString(body.get("DATEFROM"))+" 00:00:00"));
			map_par.put("dateTo", sdf.parse(MedicineUtils.parseString(body.get("DATETO"))+" 23:59:59"));
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
			if(list_ret!=null&&list_ret.size()>0){
				double hjje=0;
				int hjrs=0;
				for(Map<String,Object> m:list_ret){
					hjje+=MedicineUtils.parseDouble(m.get("HJJE"));
					hjrs+=MedicineUtils.parseInt(m.get("JCRS"));
				}
				Map<String,Object> map_temp=new HashMap<String,Object>();
				map_temp.put("JCXM", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;合&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 计 ");
				map_temp.put("JCRS", hjrs);
				map_temp.put("HJJE", MedicineUtils.formatDouble(4, hjje));
				list_ret.add(map_temp);
			}
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期格式错误", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医技收入查询失败", e);
		}
		return list_ret;
	}
}
