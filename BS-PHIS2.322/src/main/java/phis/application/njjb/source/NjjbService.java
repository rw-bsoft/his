package phis.application.njjb.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;

public class NjjbService extends AbstractActionService implements DAOSupportable {
	private static final Logger logger = LoggerFactory.getLogger(NjjbService.class);
	//签到
	protected void doSavesign(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String USERID=body.get("USERID")+"";
		String ywzqh=body.get("YWZQH")+"";
		Map<String, Object> p=new HashMap<String, Object>();
		body.put("QDSJ", new Date());
		p.put("USERID", USERID);
		p.put("YWZQH", ywzqh);
		body.put("STATUS","0");
		try {
			Long qd=dao.doCount("NJJB_QD", "USERID=:USERID and YWZQH<>:YWZQH ", p);
			if(qd>0){
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("USERID", USERID);
				dao.doSqlUpdate("update NJJB_QD set STATUS='1' where USERID=:USERID ", pa);
			}

			Long row=dao.doCount("NJJB_QD", "USERID=:USERID and YWZQH=:YWZQH ", p);
			if(row >0){
				dao.doSave("update", NJJB_QD, body, false);
			}else{
				dao.doSave("create", NJJB_QD, body, false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//签退
	protected void doCheckout(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String USERID=body.get("USERID")+"";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("USERID", USERID);
		p.put("QTSJ", new Date());
		try {
			String sql="update NJJB_QD set STATUS='1',QTSJ=:QTSJ where USERID=:USERID and STATUS='0' ";
			dao.doSqlUpdate(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//获取业务周期号
	protected void doGetywzqh(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String USERID=body.get("USERID")+"";
		String sql="select YWZQH as YWZQH from NJJB_QD where USERID=:USERID and STATUS='0' ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("USERID", USERID);
		try {
			Map<String, Object> data=dao.doSqlLoad(sql, p);
			if(data!=null && data.size()>0){
				res.put("YWZQH", data.get("YWZQH")+"");
			}else{
				res.put("code","406");
				res.put("msg","未获取到签到的业务流水号，请去签到");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//保存金保返回的卡信息
	protected synchronized void doSavenjjbkxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		body.put("bloodTypeCode", "5");
		body.put("maritalStatusCode", "5");
		NjjbModel jb=new NjjbModel(dao);
		jb.Savenjjbkxx(body,res,dao,ctx);

	}
	//获取卡信息
	protected void doGetnjjbkxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String sql="";
		if(body.containsKey("empiId")){
			sql="select a.SHBZKH as SHBZKH,a.YLRYLB as YLRYLB,b.LXDH as LXDH,a.SFZH as SFZH,a.DQZHYE as DQZHYE" +
					" from NJJB_KXX a ,MS_BRDA b where b.empiId=:empiId " +
					" and a.SHBZKH=b.SHBZKH ";
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("empiId", body.get("empiId")+"");
			Map<String, Object> kxx=new HashMap<String, Object>();
			try {
				kxx = dao.doSqlLoad(sql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(kxx!=null){
				res.put("jbkxx", kxx);
			}
		}else{
			res.put("code","402");
			res.put("msg","未获取到出入的empiid值");
		}
	}
	//获取流水号
	protected void doGetnjjblsh(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		String sql="select seq_njjb_lsh.nextval as LSH from dual where 1=:sq";
		Map<String, Object> lsh=new HashMap<String, Object>();
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("sq",1);
		try {
			lsh=dao.doSqlLoad(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(lsh !=null){
			res.put("lsh",lsh);
		}else{
			res.put("code","402");
			res.put("msg","获取流水号失败");
		}
	}
	//获取流水号和结算次数
	protected void doGetnjjblshbyqx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String ywlx=body.get("ywlx")+"";//1：门诊，2：住院
		String sql="";
		String countsql="";
		if(ywlx.equals("1")){
			sql="select a.NJJBLSH as NJJBLSH,to_char(a.GHSJ,'yyyymmddHH24Miss') as GHSJ," +
					" a.NJJBYLLB as NJJBYLLB,a.YBZY as YBZY,a.YBMC as YBMC from MS_GHMX a where a.SBXH=:SBXH";
			countsql="select count(1) as JSCS from MS_MZXX a where a.GHGL=:SBXH and a.BRXZ=2000";
		}
		Map<String, Object> p=new HashMap<String, Object>();
		Map<String, Object> lsh=new HashMap<String, Object>();
		Map<String, Object> jscs=new HashMap<String, Object>();
		p.put("SBXH", body.get("SBXH")+"");
		try {
			lsh=dao.doSqlLoad(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(lsh !=null){
			res.put("lsh",lsh);
		}else{
			res.put("code", "408");
			res.put("msg","未获取到流水号");
		}
		try {
			jscs=dao.doSqlLoad(countsql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jscs!=null && jscs.size() >0){
			res.put("JSCS",jscs.get("JSCS")+"");
		}
	}
	//获取门诊收费明细
	protected void doGetnjjbsfmx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		NjjbModel jb=new NjjbModel(dao);
		jb.Getnjjbsfmx(req, res, dao, ctx);
	}
	//获取挂号信息
	protected void doGetnjjbghxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		int adt_begin = 1;
		String adt_beginStr = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.GHXQ, ctx);
		if (adt_beginStr != "") {
			adt_begin = Integer.parseInt(adt_beginStr);
		}
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String XQJSFS = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
		//挂号效期
		Date adt_begin_data = DateUtils.addDays(new Date(), -adt_begin);
		if ("1".equals(XQJSFS)) {
			try {
				adt_begin_data = DateUtils.addDays(
						matter.parse(BSHISUtil.getDate() + " 23:59:59"), -adt_begin);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String sql="select a.SBXH as SBXH,a.NJJBYLLB as NJJBYLLB,a.YBMC as YBMC from MS_GHMX a " +
				" join MS_BRDA b on a.BRID=b.BRID" +
				" where  b.SHBZKH=:SHBZKH and a.GHSJ >=:GHSJ and  a.JGID=:JGID " +
				" and a.SBXH=:SBXH ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("SHBZKH", body.get("SHBZKH")+"");
		p.put("GHSJ", adt_begin_data);
		p.put("JGID", jgid);
		p.put("SBXH", Long.parseLong(body.get("SBXH")+""));
		Map<String, Object> data= new HashMap<String, Object>();
		try {
			List<Map<String, Object>> ghxxlist=dao.doSqlQuery(sql, p);
			if(ghxxlist!=null && ghxxlist.size()>0 ){
				data.put("SBXH", ghxxlist.get(0).get("SBXH")+"");
				data.put("NJJBYLLB", ghxxlist.get(0).get("NJJBYLLB")+"");
				data.put("YBMC", ghxxlist.get(0).get("YBMC")+"");
			}else{
				res.put("code", "503");
				res.put("msg", "未找到病人("+body.get("SHBZKH")+")有效挂号时间段内的挂号信息！");
				return;
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("body", data);
	}
	//保存南京金保医疗类别
	protected void doSavenjjbghyllb(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String sbxh=body.get("SBXH")+"";
		if(!(sbxh==null || sbxh.equals(""))){
			String sql="update MS_GHMX set NJJBYLLB=:NJJBYLLB,YBZY=:YBZY,YBMC=:YBMC where SBXH=:SBXH";
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("SBXH", Long.parseLong(sbxh));
			p.put("NJJBYLLB", body.get("NJJBYLLB")+"");
			p.put("YBZY", body.get("YBZY")+"");
			p.put("YBMC", body.get("YBMC")+"");
			try {
				dao.doSqlUpdate(sql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{

		}
	}
	//查询医保对账本地总额数据 
	@SuppressWarnings("unchecked")
	public void doQueryNjjbDz(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx){
		NjjbModel mm = new NjjbModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		String DZJSSJ =body.get("DZJSSJ")+"" ;
		String DZKSSJ =body.get("DZKSSJ")+"" ;
		try {
			Map<String,Object> fhjg = mm.queryNjjbz(DZKSSJ,DZJSSJ);
			res.put("code", 200);
			res.put("ZXYLFZE", fhjg.get("ZXYLFZE"));
			res.put("ZXTCZFJE", fhjg.get("ZXTCZFJE"));
			res.put("ZXDBJZZF", fhjg.get("ZXDBJZZF"));
			res.put("ZXDBBXZF", fhjg.get("ZXDBBXZF"));
			res.put("ZXMZBZZF", fhjg.get("ZXMZBZZF"));
			res.put("ZXZHZFZE", fhjg.get("ZXZHZFZE"));
			res.put("ZXXJZFZE", fhjg.get("ZXXJZFZE"));
		} catch (ModelDataOperationException e) {
			res.put("code", 300);
			res.put(RES_MESSAGE, "生成本地南京医保对账数据出错。 "+e.getMessage());
		}
	}
	//查询新医保明细对账本地数据
	public void doQueryForXYbdzMxbdList(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		NjjbModel mm = new NjjbModel(dao);
		mm.qeryForXYbdzMxbdList(req,res,ctx);
	}
	//获取住院明细信息
	public void doGetZymxxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		NjjbModel mm = new NjjbModel(dao);
		try {
			res.put("body", mm.doGetZymxxx(req,ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	//获取卡号
	public void doGetNjjbKh(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String sql="";
		if(req.containsKey("brid")){
			sql="select SHBZKH as SHBZKH from MS_BRDA where BRID=:BRID";
			Map<String,Object> pa=new HashMap<String, Object>();
			pa.put("BRID", req.get("brid")+"");
			try {
				res.putAll(dao.doLoad(sql, pa));
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}
	//检查明细上传
	public void doCheckMxSc(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		Long zyh=Long.parseLong(body.get("ZYH")+"");
		Map<String,Object> pa=new HashMap<String, Object>();
		pa.put("ZYH",zyh);
		try {
			//2019-06-17 zhaojian 住院结算医保病人自费和医保分离 
			//res.put("WSCS", dao.doCount("ZY_FYMX", "ZYH=:ZYH and (SCBZ is null or SCBZ=0)", pa));
			Map<String,Object> r = dao.doSqlLoad("select count(9) as wscs from (" +
					"select a.fyxh,a.ypcd,b.yyzbm from ZY_FYMX a,yk_cdxx b " +
					"where decode(a.jgid,'320124005016','320124005',a.jgid)=b.jgid and a.fyxh=b.ypxh and a.ypcd=b.ypcd and ZYH=:ZYH and a.ypcd>0 and (SCBZ is null or SCBZ=0) " +
					"union all " +
					"select a.fyxh,a.ypcd,b.yyzbm from ZY_FYMX a,gy_ylmx b " +
					"where decode(a.jgid,'320124005016','320124005',a.jgid)=b.jgid and a.fyxh=b.fyxh and ZYH=:ZYH and a.ypcd=0 and (SCBZ is null or SCBZ=0)) c where c.yyzbm is not null", pa);
			res.put("WSCS", r.get("WSCS"));	
			//医保住院病人结算：判定自费部分是否已经结算
/*			Map<String,Object> r2 = dao.doSqlLoad("select count(1) as wjss_zf from (" +
					"select a.zyh,case when exists (" +
					"select c.jlxh from ZY_FYMX c,yk_cdxx d where c.zyh=a.zyh and c.jgid=d.jgid and c.fyxh=d.ypxh and c.ypcd=d.ypcd and c.ypcd>0 and d.yyzbm is null " +
					"union " +
					"select c.jlxh from ZY_FYMX c,gy_ylmx d where c.zyh=a.zyh and c.jgid=d.jgid and c.fyxh=d.fyxh and c.ypcd=0 and d.yyzbm is null" +
					") then 1 else 0 end as existsZf," +
					"case when exists(select zyh from zy_zyjs where zyh=a.zyh and zfpb=0 and jsxz=1) then 1 else 0 end as existsZf_js " +
					"from zy_brry a where a.zyh=:ZYH) where existsZf=1 and existsZf_js=0", pa);
			res.put("WJSS_ZF", r2.get("WJSS_ZF"));*/
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//检查交易信息
	public void doCheckJyXx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String zxlsh=req.get("zxlsh")+"";
		Map<String,Object> pa=new HashMap<String, Object>();
		pa.put("JYLSH", zxlsh);
		String jy="n";
		try {
			long l=dao.doCount("NJJB_JSXX", "JYLSH=:JYLSH", pa);
			if(l>0){
				if(dao.doCount("NJJB_JSXX a ,MS_MZXX b ","a.MZXH=b.MZXH and a.FPHM=b.FPHM and a.JYLSH=:JYLSH ", pa)>0){
					jy="y";
				};
				if(dao.doCount("NJJB_JSXX a ,ZY_ZYJS b ","a.ZYH=b.ZYH and a.FPHM=b.FPHM and a.JYLSH=:JYLSH ", pa)>0){
					jy="y";
				};
			}
			res.put("jy", jy);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//获取医疗类别by疾病
	public void doGetYllbByJb(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String YBMC=body.get("YBMC")+"";
		String NJJBYLLB=body.get("NJJBYLLB")+"";
		Map<String,Object> pa=new HashMap<String, Object>();
		pa.put("YBMC", YBMC);
		try {
			String sql="select YLLB as YLLB from YB_TSBZ where JBBM=:YBMC";
			Map<String,Object> re=dao.doSqlLoad(sql, pa);
			if(re!=null && re.size() >0){
				NJJBYLLB=re.get("YLLB")+"";
			}
			res.put("NJJBYLLB", NJJBYLLB);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//获取家庭医生签约信息，判断是否需要减免一元挂号费
	public Map<String, Object> doGetjypackage(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String sfzh = body.get("SFZH")+"";
		Map<String,Object> parameter = new HashMap<String, Object>();
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameter.put("SFZH", sfzh);
		String spids;
		boolean flage = false; 
		try {
			//获取签约包id
			String sql="select signservicepackages as SPIDS from scm_signcontractrecord where favoreeempiid in  " +
					   "(select empiid from mpi_demographicinfo where idcard=:SFZH) and signflag=1 and enddate>=sysdate";
			List<Map<String,Object>> re = dao.doSqlQuery(sql, parameter);
			//获取基本包
			String sql_spids="select spid as SPID from scm_servicepackage where remark =1 and packagename not like '%不含基本医疗服务%'  ";
			List<Map<String,Object>> list = dao.doSqlQuery(sql_spids, parameters);
			if(re!=null && re.size() >0){
				for(Map<String,Object> data : re){
					spids=data.get("SPIDS")+"";
					String [] spid = spids.split(",");
				    for(int a = 0;a<spid.length;a++){
				    	 for (int i = 0; i < list.size(); i++) {
				    		 if(list.get(i).get("SPID").equals(spid[a]+"")){
				    			 flage=true;
				    		 }
				    	 }
				     }
				}	
			}
			res.put("FLAGE", flage);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return res;
	}
}
