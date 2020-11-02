package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.ivc.source.ChargesProduce;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HospitalChargesSummaryFile implements IHandler {
	ChargesProduce cck = ChargesProduce.getInstance();

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		Map<String, Object> r=new HashMap<String, Object>();
		records.add(r);
//		String saveSign = "";
//		if (request.get("save") != null) {
//			saveSign = request.get("save").toString();
//		}
//		if (!saveSign.equals("1")) {
//			cck.doGetFields(request, records, ctx);
//		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		String beginDate=request.get("beginDate").toString();
		String endDate=request.get("endDate").toString();
		
		HashMap<String, Object> p=new HashMap<String, Object>();
		p.put("jgid", jgid);
		p.put("beginDate", beginDate);
		p.put("endDate", endDate);
		response.put("KSSJ",beginDate);
		response.put("JSSJ",endDate);
		try {
			double mzzfy=0.0,zyzfy=0.0,dbzzfy=0.0;//门诊/住院/单病种合计金额
			double mznhjj=0.0,zynhjj=0.0,dbznhjj=0.0;//门诊/住院/单病种农合基金
			double mzgrxj=0.0,zygrxj=0.0,dbzgrxj=0.0;//门诊/住院/单病种个人现金
			double mzyb=0.0,zyyb=0.0;//门诊/住院医保
			long mzrc=0,zyrc=0,dbzrc=0;//门诊/住院/单病种人次
			String ghhzsql="select r.brxz,sum(r.xjje) as xjje,sum(r.zhje) as zhje,sum(r.zpje) as zpje,sum(r.qtys) as qtys,sum(r.hbwc) as hbwc from ("+
					" select a.brxz,a.xjje as XJJE,a.zpje as ZPJE,a.zhje as ZHJE,a.qtys as QTYS,a.hbwc as HBWC"+
					" from ms_ghmx a where a.jgid=:jgid and  to_char(a.ghsj,'yyyy-mm-dd')>=:beginDate" +
					" and a.thbz='0' and to_char(a.ghsj,'yyyy-mm-dd')<=:endDate"+
					") r group by r.brxz";
			List<Map<String, Object>> ghhzlist=dao.doSqlQuery(ghhzsql.toString(), p);
			for(int i=0;i<ghhzlist.size();i++){
				Map<String, Object> one =ghhzlist.get(i);
				mzzfy+= Double.parseDouble(one.get("XJJE")+"")+Double.parseDouble(one.get("ZHJE")+"")
						+Double.parseDouble(one.get("QTYS")+"")+Double.parseDouble(one.get("HBWC")+"")+Double.parseDouble(one.get("ZPJE")+"");
				if((one.get("BRXZ")+"").equals("3000") ||(one.get("BRXZ")+"").equals("2000")){
					mzyb+=Double.parseDouble(one.get("QTYS")+"")+Double.parseDouble(one.get("ZHJE")+"");
				}
				if((one.get("BRXZ")+"").equals("6000")){
					mznhjj+=Double.parseDouble(one.get("QTYS")+"");
				}
				if((one.get("BRXZ")+"").equals("5000") ){
					mzyb+=Double.parseDouble(one.get("XJJE")+"");
				}else{
					mzgrxj+=Double.parseDouble(one.get("XJJE")+"")+Double.parseDouble(one.get("ZPJE")+"");
				}
			}
			String mzhzsql=" select BRXZ,sum(zjje)  as ZJJE ,sum(zpje) as ZPJE,sum(zfje) as ZFJE,sum(zhje) as ZHJE, sum(qtys) as QTYS,"+
					" sum(hbwc) as HBWC,sum(jscs) as JSCS "+
					" from (select a.brxz, a.zjje as zjje,a.zpje as zpje,a.zfje as zfje,a.zhje as zhje,a.qtys as qtys ,"+
					" a.hbwc as hbwc ,1 as jscs from ms_mzxx a where a.zfpb='0' and a.jgid=:jgid "+
					" and to_char(a.sfrq,'yyyy-mm-dd') >=:beginDate and to_char(a.sfrq,'yyyy-mm-dd')<=:endDate)"+
					" group by brxz";
			List<Map<String, Object>> mzhzlist=dao.doSqlQuery(mzhzsql.toString(), p);
			for(int i=0;i<mzhzlist.size();i++){
				Map<String, Object> one =mzhzlist.get(i);
				mzzfy+=Double.parseDouble(one.get("ZJJE")+"");
				if((one.get("BRXZ")+"").equals("6000")){
					mznhjj+=Double.parseDouble(one.get("QTYS")+"");
				}
				if((one.get("BRXZ")+"").equals("3000") ||(one.get("BRXZ")+"").equals("2000")){
					mzyb+=Double.parseDouble(one.get("QTYS")+"")+Double.parseDouble(one.get("ZHJE")+"");
				}
				if((one.get("BRXZ")+"").equals("5000") ){
					mzyb+=Double.parseDouble(one.get("ZFJE")+"");
				}else{
					mzgrxj+=Double.parseDouble(one.get("ZFJE")+"");
				}
				mzrc+=Long.parseLong(one.get("JSCS")+"");
			}
			try {
				String qzjstr=DictionaryController.instance().get("phis.dictionary.Hcnqzj").getText(jgid);
				if(qzjstr!=null && qzjstr.length() >0){
					String args[]=qzjstr.split(":");
					response.put("YYBH", args[0]);
				}
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			String zyhzsql=" select r.BRXZ,r.DRGS,sum(r.FYHJ) as FYHJ,sum(ZFHJ) as ZFHJ,sum(QTYS) as QTYS,sum(JSRC) as JSRC from ("+
					" select a.brxz as BRXZ,case when a.drgs is null then '0' else '1' end as DRGS,"+
					" a.fyhj as FYHJ,a.zfhj as ZFHJ ,a.fyhj-a.zfhj as QTYS,1 as JSRC"+
					" from ZY_ZYJS a where a.jgid=:jgid and to_char(a.jsrq,'yyyy-mm-dd') >=:beginDate"+
					" and  to_char(a.jsrq,'yyyy-mm-dd') <=:endDate"+ 
					" union all"+ 
					" select a.brxz as BRXZ,case when a.drgs is null then '0' else '1' end as DRGS,"+
					" -a.fyhj as FYHJ,-a.zfhj as ZFHJ ,-(a.fyhj-a.zfhj) as QTYS,-1 as JSRC"+
					" from ZY_ZYJS a where a.jgid=:jgid and to_char(a.zfrq,'yyyy-mm-dd') >=:beginDate"+
					" and  to_char(a.zfrq,'yyyy-mm-dd') <=:endDate"+
					") r group by r.brxz,r.drgs";
			List<Map<String, Object>> zyhzlist=dao.doSqlQuery(zyhzsql.toString(), p);
			for(int i=0;i<zyhzlist.size();i++){
				Map<String, Object> one =zyhzlist.get(i);
				zyzfy+=Double.parseDouble(one.get("FYHJ")+"");
				if((one.get("BRXZ")+"").equals("6000")){
					zynhjj+=Double.parseDouble(one.get("QTYS")+"");
				}
				if((one.get("BRXZ")+"").equals("3000") ||(one.get("BRXZ")+"").equals("2000")){
					zyyb+=Double.parseDouble(one.get("QTYS")+"");
				}
				if((one.get("BRXZ")+"").equals("5000") ){
					zyyb+=Double.parseDouble(one.get("ZFHJ")+"");
				}else{
					zygrxj+=Double.parseDouble(one.get("ZFHJ")+"");
				}
				zyrc+=Long.parseLong(one.get("JSRC")+"");
				if((one.get("DRGS")+"").equals("1")){
					dbzrc+=Long.parseLong(one.get("JSRC")==null?"0":one.get("JSRC")+"");
					dbzzfy+=Double.parseDouble(one.get("FYHJ")+"");
					if((one.get("BRXZ")+"").equals("6000")){
						dbznhjj+=Double.parseDouble(one.get("QTYS")+"");
					}
					if(!(one.get("BRXZ")+"").equals("5000") ){
						dbzgrxj+=Double.parseDouble(one.get("ZFHJ")+"");
					}
				}
			}
			double zyjzjj=0.0,dbzjzjj=0.0;//住院、单病种救助基金
			double zydbjj=0.0,dbzdbjj=0.0;//住院、单病种大病基金
			String zymzjzsql="select r.DRGS as DRGS,sum(r.JZJJ) as JZJJ,sum(r.DBJJ) as DBJJ from ("+
					" select case when a.drgs is null then '0' else '1' end as DRGS,"+
					" b.sum12 as JZJJ,b.sum39 as DBJJ"+
					" from zy_zyjs a ,nh_bsoft_jsjl b where a.nhbxid=b.bxid and to_char(a.jsrq,'yyyy')=b.year and a.jgid=:jgid"+
					" and to_char(a.jsrq,'yyyy-mm-dd') >=:beginDate"+ 
					" and  to_char(a.jsrq,'yyyy-mm-dd') <=:endDate"+
					" union all"+ 
					" select case when a.drgs is null then '0' else '1' end as DRGS,"+ 
					" -b.sum12 as JZJJ,-b.sum39 as DBJJ"+
					" from zy_zyjs a ,nh_bsoft_jsjl b where a.nhbxid=b.bxid  and to_char(a.zfrq,'yyyy')=b.year and a.jgid=:jgid"+
					" and to_char(a.zfrq,'yyyy-mm-dd') >=:beginDate"+
					" and  to_char(a.zfrq,'yyyy-mm-dd') <=:endDate"+           
					") r group by r.DRGS";
			List<Map<String, Object>> zyjzjjist=dao.doSqlQuery(zymzjzsql.toString(), p);
			for(int i=0;i <zyjzjjist.size();i++){
				Map<String, Object> one =zyjzjjist.get(i);
				zyjzjj+=Double.parseDouble(one.get("JZJJ")==null?"0":one.get("JZJJ")+"");
				zydbjj+=Double.parseDouble(one.get("DBJJ")==null?"0":one.get("DBJJ")+"");
				if((one.get("DRGS")+"").equals("1")){
					dbzjzjj+=Double.parseDouble(one.get("JZJJ")==null?"0":one.get("JZJJ")+"");
					dbzdbjj+=Double.parseDouble(one.get("DBJJ")==null?"0":one.get("DBJJ")+"");
				}
			}
			String mzjzsql="select  sum(SUM11) as SUM11,sum(sum39) as DBJJ from ("+
					" select c.SUM11 as SUM11,c.sum39 as sum39 from ms_mzxx a ,nh_bsoft_jsjl c "+
					" where a.jgid=:jgid and a.mzxh=c.mzxh "+
					" and to_char(a.sfrq,'yyyy-mm-dd') >=:beginDate " +
					" and to_char(a.sfrq,'yyyy-mm-dd')<=:endDate "+
					" union all "+
					" select -c.SUM11 as SUM11,-c.sum39 as sum39  from ms_mzxx a,ms_zffp b ,nh_bsoft_jsjl c " +
					" where a.mzxh=b.mzxh and a.mzxh=c.mzxh"+
					" and  a.jgid=:jgid and to_char(b.zfrq,'yyyy-mm-dd') >=:beginDate" +
					" and to_char(b.zfrq,'yyyy-mm-dd')<=:endDate)";
			//门诊救助基金
			Map<String, Object> mzjz=dao.doSqlLoad(mzjzsql, p);
			response.put("MZJZJJ", BSPHISUtil.getDouble(mzjz.get("SUM11")==null?"0.0":mzjz.get("SUM11")+"",3));
			
			response.put("MZRC", mzrc);
			response.put("MZCR", 0);
			response.put("MZZFY", BSPHISUtil.getDouble(mzzfy+"", 3));
			response.put("MZNHJJ", BSPHISUtil.getDouble(mznhjj+"", 3));
			response.put("MZGRXJ", BSPHISUtil.getDouble(mzgrxj+"", 3));
			response.put("MZDBJJ", BSPHISUtil.getDouble(mzjz.get("DBJJ")==null?"0.0":mzjz.get("DBJJ")+"",2));
			response.put("ZYRC", zyrc);
			response.put("ZYZFY", BSPHISUtil.getDouble(zyzfy+"", 3));
			response.put("ZYNHJJ", BSPHISUtil.getDouble(zynhjj+"", 3));
			response.put("ZYGRXJ", BSPHISUtil.getDouble(zygrxj+"", 3));
			response.put("ZYDBJJ", zydbjj);
			String zycrsql="select sum(ZYCR) as ZYCR from ("+
					" select  nvl(sum(round(a.cyrq-a.ryrq)),0) as ZYCR from zy_brry a where a.zyh in"+
					" (select distinct b.zyh from zy_zyjs  b where b.jgid=:jgid"+
					" and to_char(b.jsrq,'yyyy-mm-dd') >=:beginDate"+
					" and  to_char(b.jsrq,'yyyy-mm-dd') <=:endDate)"+
					" union all "+
					" select nvl(-sum(round(a.cyrq-a.ryrq)),0) as ZYCR from "+
					" zy_brry a where a.zyh in "+
					" (select distinct b.zyh from zy_zyjs  b where b.jgid=:jgid"+
					" and to_char(b.zfrq,'yyyy-mm-dd') >=:beginDate "+
					" and  to_char(b.zfrq,'yyyy-mm-dd') <=:endDate ) )";
			
			Map<String, Object> zycr=dao.doSqlLoad(zycrsql, p);
			response.put("ZYCR",zycr.get("ZYCR")==null?"0":zycr.get("ZYCR")+"");
			response.put("ZYJZJJ", BSPHISUtil.getDouble(zyjzjj+"", 3));
			response.put("DBZRC",dbzrc);
			response.put("DBZZFY",BSPHISUtil.getDouble(dbzzfy+"", 3));
			response.put("DBZNHJJ",BSPHISUtil.getDouble(dbznhjj+"", 3));
			response.put("DBZGRXJ",BSPHISUtil.getDouble(dbzgrxj+"", 3));
			response.put("DBZJZJJ",BSPHISUtil.getDouble(dbzjzjj+"", 3));
			response.put("DBZDBJJ", dbzdbjj);
			String dbzzycrsql="select sum(ZYCR) as ZYCR  from ("+ 
					" select  nvl(sum(round(a.cyrq-a.ryrq)),0) as ZYCR from zy_brry a where a.zyh in"+ 
					" (select distinct b.zyh from zy_zyjs  b where b.jgid=:jgid  "+ 
					" and to_char(b.jsrq,'yyyy-mm-dd') >=:beginDate  "+ 
					" and  to_char(b.jsrq,'yyyy-mm-dd') <=:endDate and b.drgs is not null )"+ 
					" union all"+ 
					" select  nvl(-sum(round(a.cyrq-a.ryrq)),0) as ZYCR from zy_brry a where a.zyh in "+ 
					" (select distinct b.zyh from zy_zyjs  b where b.jgid=:jgid  "+ 
					" and to_char(b.zfrq,'yyyy-mm-dd') >=:beginDate  "+ 
					" and  to_char(b.zfrq,'yyyy-mm-dd') <=:endDate and b.drgs is not null )) ";
			Map<String, Object> dbzzrcr=dao.doSqlLoad(dbzzycrsql, p);
			response.put("DBZCR", dbzzrcr.get("ZYCR")==null?"0":dbzzrcr.get("ZYCR")+"");
			response.put("YB","门诊医保："+ BSPHISUtil.getDouble(mzyb+"",3)+";住院医保："+ BSPHISUtil.getDouble(zyyb+"",3));
			response.put("ZBRQ",BSHISUtil.getDate());
			response.put("YYMC",user.getManageUnitName());
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}
}
