package phis.application.wjz.source;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
/**
 * 危机值处理model
 * @author caijy
 *
 */
public class WjzManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(WjzManageModel.class);
	public WjzManageModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-7
	 * @description 危机值提醒数据查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> queryWjztx(Map<String, Object> req,Context ctx) throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("totalCount", 0);
		map_ret.put("msg", "");
		UserRoleToken user = UserRoleToken.getCurrent();
		//String jgid = user.getManageUnit().getId();// 用户的机构ID
//		int WJZCLSX =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
//				BSPHISSystemArgument.WJZCLSX, ctx));//危机值处理时限
//		int WJZCSSX =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
//				BSPHISSystemArgument.WJZCSSX, ctx));//危机值超时时限
		//危机值处理时限和危机值超时时限需要限制代码写在下面
		//....
		String userid=user.getUserId();
		if("".equals(userid)){
			throw new ModelDataOperationException("无法获取当前操作员ID");
		}
		//dao = new BaseDAO();
		//String userid = "";
		try {
			//List<Map<String, Object>> userlist = dao.doQuery("select YGDM as YGDM from SYS_Personnel where PERSONID='"+newUserid+"'",null);
			//userid = userlist.get(0).get("YGDM").toString();
		
		//String ks=MedicineUtils.parseString(user.getProperty("biz_departmentId"));//当前科室
		//String bq=MedicineUtils.parseString(user.getProperty("wardId"));//当前病区
		StringBuffer sql=new StringBuffer();
//		//1.已发布未处理 2.未超过处理超时时限 3.门诊医生、住院医生、门诊科室、住院科室、病区
//		sql.append("select a.JLXH as JLXH, a.CARDID as CARDID, a.BRXM as BRXM, a.TXNR as TXNR, a.BGDH as BGDH, a.SHSJ as SHSJ, a.FBSJ as FBSJ, a.JZLB as JZLB, a.BRCH as BRCH, b.KSMC as KSMC, a.ZRYSXM as ZRYSXM from JG_WJZJL a left outer join GY_KSDM b on a.BRKS=to_char(b.KSDM) where a.FBZT<>3 and  a.CLZT = 3 and (sysdate-a.FBSJ)*24*60<=:fbsjsx and (a.ZRYS =:userid or a.BRKS =:ks or :bq in (select distinct to_char(KSDM) from ZY_CWSZ where BRCH = a.BRCH))");
//		//1.已发布未处理 2.超过1级处理提醒时限 3.未超过1级处理超时时限 4.科室负责人员
//		sql.append(" union all select a.JLXH as JLXH, a.CARDID as CARDID, a.BRXM as BRXM, a.TXNR as TXNR, a.BGDH as BGDH, a.SHSJ as SHSJ, a.FBSJ as FBSJ, a.JZLB as JZLB, a.BRCH as BRCH, d.KSMC as KSMC, a.ZRYSXM as ZRYSXM from JG_WJZTXFJ b, JG_WJZTXFJ_KSRY c,JG_WJZJL a left outer join GY_KSDM d on a.BRKS=to_char(d.KSDM) where a.FBZT<>3 and  a.CLZT = 3 and c.YWLB=2 and c.TXJB = 1 and (sysdate-a.FBSJ)*24*60>=b.TXSJ and (sysdate-a.FBSJ)*24*60<b.TXSJ+:cssx and c.YWLB = b.YWLB and c.TXJB = b.TXJB and a.BRKS=to_char(c.KSDM) and c.YGDM=:userid ");
//		//1.已发布未处理 2.超过1级以上提醒时限 3.未超过相应级别超时时限
//		sql.append(" union all select a.JLXH as JLXH, a.CARDID as CARDID, a.BRXM as BRXM, a.TXNR as TXNR, a.BGDH as BGDH, a.SHSJ as SHSJ, a.FBSJ as FBSJ, a.JZLB as JZLB, a.BRCH as BRCH, d.KSMC as KSMC, a.ZRYSXM as ZRYSXM from JG_WJZTXFJ b, JG_WJZTXFJ_KSRY c,JG_WJZJL a left outer join GY_KSDM d on a.BRKS=to_char(d.KSDM) where a.FBZT<>3 and  a.CLZT = 3 and c.YWLB=2 and c.TXJB > 1 and (sysdate-a.FBSJ)*24*60>=b.TXSJ and (sysdate-a.FBSJ)*24*60<b.TXSJ+:cssx and c.YWLB = b.YWLB and c.TXJB = b.TXJB and a.BRKS=to_char(c.KSDM) and c.YGDM=:userid ");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("userid", userid);
//		map_par.put("ks", ks);
//		map_par.put("bq", bq);
//		map_par.put("fbsjsx", WJZCLSX+WJZCSSX);
//		map_par.put("cssx", WJZCSSX);
		Map<String,Object> map_par=new HashMap<String,Object>();
		if(!req.containsKey("type")){
		sql.append("select SQDH as SQDH,KDKS||'' as KSMC,KDYS as KDYS,WJZFSSJ as FBSJ,BRXM as BRXM,WJZNR as WJZNR,1 as JC from EMR_JCSQ where (WJZCLZT=0 or WJZCLZT is null)");
		sql.append(" union all ");
		sql.append("select JLXH as SQDH,BGKSMC as KSMC,BGYS as KDYS,BGSJ as FBSJ,BRXM as BRXM,WJZJG as WJZNR,0 as JC from EMR_WJZDJ where CLJL is null");
		}else{
			if(req.containsKey("id")){//如果有id则查询单独的一条记录
				if(MedicineUtils.parseInt(req.get("type"))==1){//检验
					sql.append("select a.JLXH as ID, b.BRID as GRBM,a.BRXM as NAME,b.BRXB as GENDER,'0' as BIRTHDAY,to_char(a.JZXH) as CLINICID,a.ZYHM as PATIENTID,0 as TELEPHONE,b.DJRQ as VISITDATE,a.BRKS as DEPTID,c.officeName as DEPTNAME,a.BGYS as DOCTORID,d.personName as DOCTORNAME,a.BGSJ as REPORTTIME,1 as status,1 as type,'0' as EXAMCONTENT,'0' as EXAMID,0 as SUPPLYID from EMR_WJZDJ a,ZY_BRRY b,SYS_Office c,SYS_Personnel d where a.ZYHM=b.ZYHM and  CLJL is null and a.BRKS=c.ID and a.BGYS=d.personId and a.JLXH=:id");
				}else{//检查
					sql.append("select a.SQDH as ID,a.BRID as GRBM,a.BRXM as NAME,a.BRXB as GENDER,a.BRNL as BIRTHDAY,a.JZHM as CLINICID,a.JZHM as PATIENTID,0 as TELEPHONE,a.JCSJ as VISITDATE,a.BRKS as DEPTID,b.officeName as DEPTNAME,a.BGYS as DOCTORID,c.personName as DOCTORNAME,a.BGSJ as REPORTTIME,1 as status,2 as type,a.JCXM as EXAMCONTENT,a.RISID as EXAMID,a.SQDH as SUPPLYID from EMR_JCSQ a,SYS_Office b,SYS_Personnel c where (WJZCLZT=0 or WJZCLZT is null) and a.BRKS=b.ID and a.BGYS=c.personId and a.SQDH=:id");
				}
				map_par.put("id", MedicineUtils.parseLong(req.get("id")));
			}else{
			sql.append("select a.SQDH as ID,a.BRID as GRBM,a.BRXM as NAME,a.BRXB as GENDER,a.BRNL as BIRTHDAY,a.JZHM as CLINICID,a.JZHM as PATIENTID,0 as TELEPHONE,a.JCSJ as VISITDATE,a.BRKS as DEPTID,b.officeName as DEPTNAME,a.BGYS as DOCTORID,c.personName as DOCTORNAME,a.BGSJ as REPORTTIME,1 as status,2 as type,a.JCXM as EXAMCONTENT,a.RISID as EXAMID,a.SQDH as SUPPLYID from EMR_JCSQ a,SYS_Office b,SYS_Personnel c where (WJZCLZT=0 or WJZCLZT is null) and a.BRKS=b.ID and a.BGYS=c.personId");
			sql.append(" union all ");
			sql.append("select a.JLXH as ID, b.BRID as GRBM,a.BRXM as NAME,b.BRXB as GENDER,'0' as BIRTHDAY,to_char(a.JZXH) as CLINICID,a.ZYHM as PATIENTID,0 as TELEPHONE,b.DJRQ as VISITDATE,a.BRKS as DEPTID,c.officeName as DEPTNAME,a.BGYS as DOCTORID,d.personName as DOCTORNAME,a.BGSJ as REPORTTIME,1 as status,1 as type,'0' as EXAMCONTENT,'0' as EXAMID,0 as SUPPLYID from EMR_WJZDJ a,ZY_BRRY b,SYS_Office c,SYS_Personnel d where a.ZYHM=b.ZYHM and  CLJL is null and a.BRKS=c.ID and a.BGYS=d.personId");
			}
			}
		List<Map<String,Object>> list =dao.doSqlQuery(sql.toString(), map_par);
		if(list==null||list.size()==0){
			return map_ret;
		}
		if(!req.containsKey("type")){//本地系统使用
			String msg=parseHtml(list);
			map_ret.put("totalCount", list.size());
			map_ret.put("msg", msg);
		}else{//返回给移动端
			List<Map<String,Object>> l_ret=new ArrayList<Map<String,Object>>();
			for(Map<String,Object> map_temp:list){
				Map<String,Object> m_ret=new HashMap<String,Object>();
				m_ret.put("id", map_temp.get("ID"));
				m_ret.put("grbm", map_temp.get("GRBM"));
				m_ret.put("name", map_temp.get("NAME"));
				m_ret.put("gender", map_temp.get("GENDER"));
				m_ret.put("birthday", map_temp.get("BIRTHDAY"));
				m_ret.put("clinicId", map_temp.get("CLINICID"));
				m_ret.put("patientId", map_temp.get("PATIENTID"));
				m_ret.put("telephone", map_temp.get("TELEPHONE"));
				m_ret.put("visitDate", map_temp.get("VISITDATE"));
				m_ret.put("deptId", map_temp.get("DEPTID"));
				m_ret.put("deptName", map_temp.get("DEPTNAME"));
				m_ret.put("doctorId", map_temp.get("DOCTORID"));
				m_ret.put("doctorName", map_temp.get("DOCTORNAME"));
				m_ret.put("reportTime", map_temp.get("REPORTTIME"));
				m_ret.put("status", map_temp.get("STATUS"));
				m_ret.put("type", map_temp.get("TYPE"));
				if("1".equals(map_temp.get("TYPE")+"")){
					Map<String,Object> labReport=new HashMap<String,Object>();
					labReport.put("sampleNo", "0");
					labReport.put("title", "0");
					List<Map<String,Object>> labReportDetails=new ArrayList<Map<String,Object>>();
					//此处省略获取明细
					Map<String,Object> labReportDetail=new HashMap<String,Object>();
					labReportDetail.put("observationsubId", "0");
					labReportDetail.put("observationsubName", "0");
					labReportDetail.put("observationvalue", "0");
					labReportDetail.put("units", "0");
					labReportDetail.put("referencerange", "0");
					labReportDetail.put("memo", "0");
					labReportDetail.put("sort", "0");
					labReportDetail.put("urgentflag", "0");
					labReportDetail.put("topvalue", "0");
					labReportDetail.put("bottomvalue", "0");
					labReportDetails.add(labReportDetail);
					labReport.put("labReportDetails", labReportDetails);
					m_ret.put("labReport", labReport);
				}else{
					Map<String,Object> clinicExam=new HashMap<String,Object>();
					clinicExam.put("examContent", map_temp.get("EXAMCONTENT"));
					clinicExam.put("examId", map_temp.get("EXAMID"));
					clinicExam.put("supplyId", map_temp.get("SUPPLYID"));
					m_ret.put("clinicExam", clinicExam);
				}
				l_ret.add(m_ret);
			}
			map_ret.clear();
			map_ret.put("data", l_ret);
		}
		return map_ret;
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "危机值查询失败", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-8
	 * @description 将数据转成界面显示的Html
	 * @updateInfo
	 * @param l
	 * @return
	 */
	public String parseHtml(List<Map<String,Object>> l){
		StringBuffer html=new StringBuffer();
		html.append("<table id='wjzTable'><tbody>");
		for(Map<String,Object> m:l){
			html.append("<tr><td>开单科室:").append(m.get("KSMC")).append("&nbsp;&nbsp;&nbsp;医生:").append(m.get("KDYS")).append("&nbsp;&nbsp;&nbsp;单号:").append(m.get("SQDH"));
			html.append("<br>发布时间:").append(m.get("FBSJ")).append("&nbsp;&nbsp;&nbsp").append("<span style='font-size:22px;'>").append(m.get("BRXM")).append("</span>");
			html.append("<br><span style='color:red;'>").append(m.get("WJZNR")).append("</span><div style='display: none;'>").append("{'SQDH':"+m.get("SQDH")+",'BRXM':'"+m.get("BRXM")+"','JC':'"+m.get("JC")+"'}</div>").append("</td></tr>");
		}
		html.append("</tbody></table>");
		return html.toString();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-8
	 * @description 保存危机值处理
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveWjzcz(Map<String,Object> body,Context ctx) throws ModelDataOperationException{
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String userid=user.getUserId(); 
//		StringBuffer hql_count=new StringBuffer();//查询是否处理过
//		hql_count.append(" WJZXH =:wjzxh and CZRY=:czry and CZNR=:cznr");
//		StringBuffer hql_jlupdate=new StringBuffer();//更新记录表
//		hql_jlupdate.append("update JG_WJZJL set CLZT = (case when (FBSJ+:clsx>sysdate) then 1 else 2 end),CLSJ = :d,CLRY=:userid,CLRYXM =:username,CLQK=:cznr where JLXH=:jlxh and CLZT = 3 ");
//		Map<String,Object> map_par_count=new HashMap<String,Object>();
//		map_par_count.put("wjzxh", MedicineUtils.parseLong(body.get("JLXH")));
//		map_par_count.put("czry", userid);
//		map_par_count.put("cznr", MedicineUtils.parseString(body.get("CZNR")));
//		long l;
//		Date d=new Date();
//		try {
//			l = dao.doCount("JG_WJZCZ", hql_count.toString(), map_par_count);
//			if(l==0){
//				Map<String,Object> map_wjzcz=new HashMap<String,Object>();
//				map_wjzcz.put("WJZXH", MedicineUtils.parseLong(body.get("JLXH")));
//				map_wjzcz.put("CZSJ", d);
//				map_wjzcz.put("CZKS", MedicineUtils.parseString(user.getProperty("biz_departmentId")));
//				map_wjzcz.put("CZRY", userid);
//				map_wjzcz.put("CZRYXM", user.getUserName());
//				map_wjzcz.put("CZNR", MedicineUtils.parseString(body.get("CZNR")));
//				map_wjzcz.put("TIMESTAMP", d);
//				dao.doSave("create", "phis.application.wjz.schemas.JG_WJZCZ", map_wjzcz, false);
//			}
//			Map<String,Object> map_par=new HashMap<String,Object>();
//			map_par.put("d", d);
//			map_par.put("clsx", MedicineUtils.parseInt(ParameterUtil.getParameter(user.getManageUnit().getId(),BSPHISSystemArgument.WJZCLSX, ctx)));
//			map_par.put("userid", userid);
//			map_par.put("username",  user.getUserName());
//			map_par.put("cznr", MedicineUtils.parseString(body.get("CZNR")));
//			map_par.put("jlxh",MedicineUtils.parseLong(body.get("JLXH")));
//			dao.doUpdate(hql_jlupdate.toString(), map_par);
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "危机值处理保存失败", e);
//		} catch (ctd.validator.ValidateException e) {
//			MedicineUtils.throwsException(logger, "危机值处理保存失败", e);
//		} 
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid=user.getUserId(); 
		StringBuffer hql_jlupdate=new StringBuffer();//更新记录表
		if(MedicineUtils.parseInt(body.get("JC"))==1){
			hql_jlupdate.append("update EMR_JCSQ set WJZCLZT=1,CLYJ=:clyj,QRYS=:userid,QRSJ=sysdate where SQDH=:sqdh");
		}else{
			hql_jlupdate.append("update EMR_WJZDJ set CLJL=:clyj,JSYS=:userid,JSSJ=sysdate where JLXH=:sqdh");
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("clyj", MedicineUtils.parseString(body.get("CZNR")));
		map_par.put("userid", userid);
		map_par.put("sqdh", MedicineUtils.parseLong(body.get("SQDH")));
		try {
			dao.doUpdate(hql_jlupdate.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "危机值处理保存失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-15
	 * @description 危机值常用语查询
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadWjzcyy() throws ModelDataOperationException {
		List<Map<String,Object>> l=new ArrayList<Map<String,Object>>();
		try {
			l= dao.doQuery("select JLXH as JLXH,CYY as CYY,SXH  as SXH from JG_WJZCYY ",null);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "危机值常用语查询失败", e);
		}
		return l;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-12-17
	 * @description 保存危机值数据
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveWjz(List<Map<String,Object>> body,Context ctx) throws ModelDataOperationException{
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		if(body==null){
			map_ret.put("code", 9000);
			map_ret.put("msg", "参数为空");
			return map_ret;
		}
		try {
		for(Map<String,Object> map:body){
			if(map.containsKey("SQDH")){//有申请单号的是检查
				dao.doSave("create", "phis.application.wjz.schemas.EMR_JCSQ", map, false);
			}else{
				dao.doSave("create", "phis.application.wjz.schemas.EMR_WJZDJ", map, false);
			}
		}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "危机值数据保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "危机值数据保存失败", e);
		}
		return map_ret;
	}
}
