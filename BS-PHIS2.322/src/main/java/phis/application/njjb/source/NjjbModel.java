package phis.application.njjb.source;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

import phis.application.pix.source.EmpiModel;
import phis.application.pix.source.EmpiUtil;
import phis.application.xnh.source.XnhModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;

public class NjjbModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(XnhModel.class);
	public NjjbModel(BaseDAO dao) {
		this.dao = dao;
	}
	public void Savenjjbkxx(Map<String, Object> body,Map<String, Object> res, BaseDAO dao, Context ctx) throws ValidateException
	{
		//先判断保存更新卡信息
		Map<String, Object> kxx_p=new HashMap<String, Object>();
		kxx_p.put("SHBZKH",body.get("SHBZKH")+"");
		Long count;
		try {
			count = dao.doCount("NJJB_KXX", "SHBZKH=:SHBZKH", kxx_p);
			if(count >0){
				dao.doSave("update", BSPHISEntryNames.NJJB_KXX, body, false);
			}else{
				dao.doSave("create", BSPHISEntryNames.NJJB_KXX, body, false);
			}
		} catch (PersistentDataOperationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("idCard", body.get("SFZH")+"");
		body.put("idCard", body.get("SFZH")+"");
		body.put("sexCode", body.get("XB")+"");
		body.put("personName", body.get("XM")+"");
		body.put("BRXZ", "2000");
		String sfzh=body.get("SFZH")+"";
		if(sfzh.length()==18){
			body.put("birthday",sfzh.substring(6,10)+"-"+sfzh.substring(10,12)+"-"+sfzh.substring(12,14));
		}else if(sfzh.length()==15){
			body.put("birthday","19"+sfzh.substring(6,8)+"-"+sfzh.substring(8,10)+"-"+sfzh.substring(10,12));
		}
		Map<String, Object> query=new HashMap<String, Object>();
		try {
			query = EmpiUtil.queryByIdCardAndName(dao, ctx, reqBody);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String, Object>> list=(List<Map<String, Object>>)query.get("body");
		Map<String, Object> result =new HashMap<String, Object>();
		result.put("SHBZKH", body.get("SHBZKH").toString());
		result.put("BRXZ", 2000);
//		result.put("YLLB", body.get("YLLB").toString());
		
		EmpiModel em=new EmpiModel(dao);
		if(list==null){
			List<Map<String, Object>> cards= new ArrayList<Map<String,Object>>();
			//封装市民卡
			Map<String, Object> card1=new HashMap<String, Object>();
			card1.put("cardNo", body.get("SHBZKH").toString());
			card1.put("cardTypeCode", "02");
			//封装就诊卡
			Map<String, Object> card2=new HashMap<String, Object>();
			String mzhm="";
			try {
				mzhm=em.doOutPatientNumber(ctx);
			} catch (ModelDataOperationException e) {
				e.printStackTrace();
			}
			if(mzhm==null || mzhm.length() <=0){
				res.put("code", "502");
				res.put("msg", "请先维护门诊号码段");
				return;
			}
			card2.put("cardNo", mzhm);
			body.put("MZHM", mzhm);
			card2.put("cardTypeCode", "04");
			cards.add(card1);
			cards.add(card2);
			body.put("cards", cards);
			
			Map<String, Object> records = new HashMap<String, Object>();
			records.putAll(body);
			records = EmpiUtil.changeToPIXFormat(records);
			records.put("photo","");
			//保存人员信息
			try {
				result=EmpiUtil.submitPerson(dao, ctx, body,records);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(list.size() >1){
			res.put("code", "502");
			res.put("msg", "同一身份证号在档案表有两份档案！请联系管理员处理！");
			return;
		}else{
			Map<String, Object> brxx=list.get(0);
			String empiid=brxx.get("empiId").toString();
			result.put("empiid", empiid);
			//yx-2017-05-07-开始判断保存农合卡号
			String cardsql="select cardNo as cardNo from MPI_Card where empiId=:empiId and cardTypeCode=:cardTypeCode";
			Map<String, Object> pa=new HashMap<String, Object>();
			pa.put("empiId", empiid);
			pa.put("cardTypeCode", "02");
			try {
				Map<String, Object> cardno=dao.doLoad(cardsql, pa);
				if(cardno==null  || cardno.size() <=0){
					Map<String, Object> cardxx=new HashMap<String, Object>();
					cardxx.put("empiId", empiid);
					cardxx.put("cardTypeCode", "02");
					cardxx.put("cardNo", body.get("SHBZKH").toString());
					try {
						em.saveCard(cardxx);
					} catch (ValidateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ModelDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {//如果卡号不对应去更新卡号
					if(!(cardno.get("cardNo")+"").equals(body.get("SHBZKH").toString())){
						String upcardsql="update MPI_Card set cardNo=:cardNo where empiId=:empiId " +
								"and cardTypeCode=:cardTypeCode";
						pa.put("cardNo", body.get("SHBZKH").toString());
						dao.doSqlUpdate(upcardsql, pa);
						pa.remove("cardTypeCode");
						String upbrdasql="update MS_BRDA set SHBZKH=:cardNo where empiId=:empiId ";
						dao.doSqlUpdate(upbrdasql, pa);
					}
				}
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
			//yx-2017-05-07-结束判断保存农合卡号
			//yx-2017-05-10-保存门诊号到卡号表-开始
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "EMPIID", "s", empiid);
			Map<String, Object> brda=new HashMap<String, Object>();
			try {
				brda=dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA);
			} catch (PersistentDataOperationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(brda==null || brda.size()<=0){
				String tempmzhm="";
				//判断是否存在门诊号
				String sql="select cardNo as cardNo from MPI_Card where empiId=:empiId " +
						" and cardTypeCode=:cardTypeCode";
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("empiId", empiid);
				p.put("cardTypeCode", "04");
				boolean cardinsertflag=true;
				try {
					Map<String, Object> cardxx=dao.doSqlLoad(sql, p);
					if(cardxx!=null && cardxx.size() >0){
						tempmzhm=cardxx.get("CARDNO")+"";
						cardinsertflag=false;
					}
				} catch (PersistentDataOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(tempmzhm.equals("") || tempmzhm.length() <=1){
					try {
						tempmzhm = em.doOutPatientNumber(ctx);
					} catch (ModelDataOperationException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(tempmzhm==null || tempmzhm.length() <=0){
					res.put("code", "502");
					res.put("msg", "请先维护门诊号码段");
					return;
				}
				brxx.put("MZHM", tempmzhm);
				brxx.put("BRXZ", 2000);
				brxx.put("SHBZKH", body.get("SHBZKH"));
				brxx.put("sexCode", body.get("XB")+"");
				brxx.put("personName", body.get("XM")+"");
				if(cardinsertflag){
				Map<String, Object> card=new HashMap<String, Object>();
				card.put("cardNo", tempmzhm);
				card.put("cardTypeCode", "04");
				card.put("empiId", empiid);
				
				try {
					try {
						em.saveCard(card);
					} catch (ValidateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ModelDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				try {
					//保存病人档案
					try {
						em.saveBRDA(brxx, ctx);
					} catch (ValidateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ModelDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result.put("MZHM", tempmzhm);
			}else{
				if(brda.get("SHBZKH")==null || brda.get("SHBZKH").toString().length() <=0){
					brxx.put("SHBZKH", body.get("SHBZKH"));
				}
				brxx.put("BRXZ", "2000");
				brxx.put("sexCode", body.get("XB")+"");
				brxx.put("personName", body.get("XM")+"");
				brxx.put("birthday", body.get("birthday")+"");
				try {
					try {
						em.saveBRDA(brxx, ctx);
					} catch (ValidateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ModelDataOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				result.put("MZHM", brda.get("MZHM")+"");
				String sql="select cardNo as cardNo from MPI_Card where empiId=:empiId " +
						" and cardTypeCode=:cardTypeCode and cardNo=:cardNo";	
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("empiId", empiid);
				p.put("cardTypeCode", "04");
				p.put("cardNo", brda.get("MZHM")+"");
				try {
					Map<String, Object> mzcard=dao.doLoad(sql, p);
					if(mzcard==null || mzcard.size() <=0){
						Map<String, Object> card=new HashMap<String, Object>();
						card.put("cardNo", brda.get("MZHM")+"");
						card.put("cardTypeCode", "04");
						card.put("empiId", empiid);
						try {
							try {
								em.saveCard(card);
							} catch (ValidateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (ModelDataOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		res.put("body", result);
	}
	public void Getnjjbsfmx(Map<String, Object> body,Map<String, Object> res, BaseDAO dao, Context ctx) throws ValidateException
	{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		String zbmjgid="";
		List<Map<String, Object>> datas = (List<Map<String, Object>>) body.get("body");
		if(datas.size()>0){
			try{
				Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
				zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			}catch(ControllerException e1){
				e1.printStackTrace();
				res.put("msg","字典phis.dictionary.NJJB未维护完整，请联系工程师！");
				res.put("code","408");
				return;
			}
			if(zbmjgid.length()==0||zbmjgid.equals("null")){
				res.put("msg","字典phis.dictionary.NJJB未维护完整，请联系工程师！");
				res.put("code","408");
				return;
			}
			List<Map<String,Object>> ybMapList = new ArrayList<Map<String,Object>>();
			boolean hasAllZbm = true;
			for (int i = 0; i < datas.size(); i++) {
				Map<String,Object> ybMap = new HashMap<String, Object>();
				Map<String, Object> data = datas.get(i);
				Map<String,Object> p=new HashMap<String, Object>();
				p.put("JGID",zbmjgid);
				p.put("YPXH",Long.parseLong(data.get("YPXH")+""));
				List<Map<String, Object>> ybypList = new ArrayList<Map<String,Object>>();
				if ("0".equals(data.get("CFLX") + "")) {//项目
					try {
						ybypList = dao.doQuery("select YYZBM as YYZBM,1 as CLXS from GY_YLMX" +
								" where JGID=:JGID and FYXH=:YPXH", p);
					} catch (PersistentDataOperationException e) {
						e.printStackTrace();
					}
					ybMap.put("XMZL", 2);//标志:非空,1-药品，2-治疗费
					String fygb=data.get("FYGB")==null?"":data.get("FYGB")+"";
					if(fygb.equals("32")){
						ybMap.put("XMZL",3);
					}
				}else{//药品
					p.put("YPCD",Long.parseLong(data.get("YPCD")+""));
					try {
						ybypList = dao.doQuery("select a.YYZBM as YYZBM,b.ZXBZ/b.YFBZ as CLXS from YK_CDXX a,YK_TYPK b" +
								" where a.JGID=:JGID and a.YPXH=:YPXH and a.YPCD=:YPCD and a.YPXH=b.YPXH", p);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ybMap.put("XMZL", 1);//标志:非空,1-药品，2-治疗费,3-材料
					String fygb=data.get("FYGB")==null?"":data.get("FYGB")+"";
					if(!(fygb.equals("2") || fygb.equals("3") || fygb.equals("4"))){
						if(fygb.equals("32")){
							ybMap.put("XMZL",3);
						}else{
							ybMap.put("XMZL",2);
						}
					}
				}
				if(ybypList.size()>0){
					Map<String, Object> ybyp = ybypList.get(0);
					if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+"")) && Double.parseDouble(data.get("YPDJ")+"")>0.0){//自编码为空
						hasAllZbm = false;
					}
					if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+"")) && Double.parseDouble(data.get("YPDJ")+"")==0.0){
						continue;
					}
					ybMap.put("ZBM", ybyp.get("YYZBM"));//药品/项目自编码
					Long clxs=Long.parseLong(ybyp.get("CLXS")+"");
					if(clxs>1){
						ybMap.put("JJDW","1");
					}else{
						ybMap.put("JJDW","0");
					}
					if("3".equals(data.get("CFLX")+"")){
						ybMap.put("SL", Long.parseLong(data.get("CFTS")+"") * Double.parseDouble(data.get("YPSL")+""));//中药数量
					}else{
						ybMap.put("SL", data.get("YPSL"));//数量
					}
					ybMap.put("DJ", data.get("YPDJ"));//单价
					ybMap.put("HJJE", data.get("HJJE"));//合计
					ybMap.put("KFRQ", (data.get("KFRQ")+"").replaceAll("-","").replaceAll(":","").replaceAll(" ", ""));//开方时间
					ybMap.put("CFTS", data.get("CFTS"));//处方贴数
					ybMap.put("KSDM", data.get("KSDM"));//科室
					ybMap.put("YSDM", data.get("YSDM"));//医生
					ybMapList.add(ybMap);
				}
			}
			if(!hasAllZbm){
				res.put("msg", "有收费项目未维护自编码，请仔细核对！");
				res.put("code", "408");
			}else{
				res.put("fyxxlist",ybMapList );
			}
		}
	}
	public Map<String, Object> queryNjjbz(String DZKSSJ,String DZJSSJ)throws ModelDataOperationException {
		Map<String, Object> map_ywzqh = new HashMap<String, Object>();
		map_ywzqh.put("DZKSSJ", DZKSSJ);
		map_ywzqh.put("DZJSSJ", DZJSSJ);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		String sjjgid=jgid.substring(0,9);
		StringBuffer jgsql=new StringBuffer();
		jgsql.append("select a.ORGANIZCODE as ORGANIZCODE from SYS_ORGANIZATION a")
		.append(" where a.ORGANIZCODE like '"+sjjgid+"%' order by a.organizcode");
		List<Map<String,Object>> jgl=new ArrayList<Map<String,Object>>();
		try{
			jgl=dao.doSqlQuery(jgsql.toString(),null);
		}catch(PersistentDataOperationException e1){
			e1.printStackTrace();
		}
		String jgids="";
		if(jgl!=null && jgl.size()>0){
			try{
				Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
				String ybjgbm=njjb.getText(jgid);
				if(ybjgbm.length()==0 && ybjgbm.equals("null")){
					logger.info("phis.dictionary.NJJB未配置"+jgid);
					return new HashMap<String,Object>();
				}
				for(Map<String,Object> m:jgl){
					if(ybjgbm.equals(njjb.getText(m.get("ORGANIZCODE")+""))){
						jgids+=",'"+m.get("ORGANIZCODE")+"'";
					}
				}
				jgids="("+jgids.substring(1)+")";
			}catch(ControllerException e){
				e.printStackTrace();
			}
		}
		// TODO 这里的金额统计方法需要核对
		String sjyb_mzjs = "SELECT nvl(SUM(BCYLFZE),0) as BCYLFZE,nvl(SUM(BCTCZFJE),0) as BCTCZFJE,"+
				" nvl(SUM(BCDBJZZF),0) as BCDBJZZF, nvl(SUM(BCDBBXZF),0) as BCDBBXZF, nvl(sum(BCMZBZZF),0) as BCMZBZZF,"+
				" nvl(sum(BCZHZFZE),0) as BCZHZFZE, nvl(sum(BCXZZFZE),0) as BCXZZFZE "+
				" FROM NJJB_JSXX WHERE to_char(jssj,'yyyyMMdd') <=:DZJSSJ and to_char(jssj,'yyyyMMdd') >=:DZKSSJ "+
				" and mzxh is not null and jgid in "+jgids;
		String sjyb_mzjs_zf = "SELECT nvl(SUM(a.BCYLFZE),0) as BCYLFZE,nvl(SUM(a.BCTCZFJE),0) as BCTCZFJE,"+
				" nvl(SUM(a.BCDBJZZF),0) as BCDBJZZF,nvl(SUM(a.BCDBBXZF),0) as BCDBBXZF,nvl(sum(a.BCMZBZZF),0) as BCMZBZZF," +
				" nvl(sum(a.BCZHZFZE),0) as BCZHZFZE,nvl(sum(a.BCXZZFZE),0) as BCXZZFZE"+
				" FROM NJJB_JSXX a, MS_ZFFP b WHERE to_char(b.zfrq,'yyyyMMdd')<=:DZJSSJ"+
				" and to_char(b.zfrq,'yyyyMMdd')>=:DZKSSJ"+
				" and a.mzxh=b.mzxh and a.mzxh is not null and a.jgid in "+jgids;
		String sjyb_ghjs="SELECT nvl(SUM(BCYLFZE),0) as BCYLFZE,nvl(SUM(BCTCZFJE),0) as BCTCZFJE,"+
				" nvl(SUM(BCDBJZZF),0) as BCDBJZZF, nvl(SUM(BCDBBXZF),0) as BCDBBXZF, nvl(sum(BCMZBZZF),0) as BCMZBZZF,"+
				" nvl(sum(BCZHZFZE),0) as BCZHZFZE, nvl(sum(BCXZZFZE),0) as BCXZZFZE "+
				" FROM njjb_jsxx WHERE to_char(jssj,'yyyyMMdd')<=:DZJSSJ and to_char(jssj,'yyyyMMdd')>=:DZKSSJ"+
				" and ghxh is not null and jgid  in "+jgids;
		String sjyb_ghjs_zf="SELECT nvl(SUM(BCYLFZE),0) as BCYLFZE,nvl(SUM(BCTCZFJE),0) as BCTCZFJE,"+
				" nvl(SUM(BCDBJZZF),0) as BCDBJZZF,nvl(SUM(BCDBBXZF),0) as BCDBBXZF,nvl(sum(BCMZBZZF),0) as BCMZBZZF,"+
				" nvl(sum(BCZHZFZE),0) as BCZHZFZE,nvl(sum(BCXZZFZE),0) as BCXZZFZE"+
				" FROM njjb_jsxx a,ms_thmx b WHERE to_char(b.thrq,'yyyyMMdd') <=:DZJSSJ and to_char(b.thrq,'yyyyMMdd')>=:DZKSSJ"+
				" and a.ghxh=b.sbxh and ghxh is not null and a.jgid in "+jgids;
		String sjyb_zyjs="SELECT nvl(SUM(BCYLFZE),0) as BCYLFZE,nvl(SUM(BCTCZFJE),0) as BCTCZFJE," +
				" nvl(SUM(BCDBJZZF),0) as BCDBJZZF, nvl(SUM(BCDBBXZF),0) as BCDBBXZF," +
				" nvl(sum(BCMZBZZF),0) as BCMZBZZF, nvl(sum(BCZHZFZE),0) as BCZHZFZE," +
				" nvl(sum(BCXZZFZE),0) as BCXZZFZE " +
				" FROM njjb_jsxx WHERE to_char(jssj,'yyyyMMdd')<=:DZJSSJ and to_char(jssj,'yyyyMMdd')>=:DZKSSJ" +
				" and zyh is not null and jgid in "+jgids;
		String sjyb_zyjs_zf="SELECT nvl(SUM(BCYLFZE),0) as BCYLFZE,nvl(SUM(BCTCZFJE),0) as BCTCZFJE," +
				"nvl(SUM(BCDBJZZF),0) as BCDBJZZF,nvl(SUM(BCDBBXZF),0) as BCDBBXZF," +
				"nvl(sum(BCMZBZZF),0) as BCMZBZZF, nvl(sum(BCZHZFZE), 0) as BCZHZFZE," +
				"nvl(sum(BCXZZFZE), 0) as BCXZZFZE " +
				" FROM NJJB_JSXX a,ZY_ZYJS b WHERE to_char(b.zfrq,'yyyMMdd')<=:DZJSSJ and to_char(b.zfrq,'yyyyMMdd')>=:DZKSSJ" +
				" and a.zyh=b.zyh and a.fphm=b.fphm and a.zyh is not null and a.jgid in "+jgids;

		try {
			Map<String, Object> map_mzjs = dao.doSqlQuery(sjyb_mzjs, map_ywzqh).get(0);
			Map<String, Object> map_mzjs_zf = dao.doSqlQuery(sjyb_mzjs_zf,map_ywzqh).get(0);
			Map<String, Object> map_ghjs = dao.doSqlQuery(sjyb_ghjs, map_ywzqh).get(0);
			Map<String, Object> map_ghjs_zf = dao.doSqlQuery(sjyb_ghjs_zf,map_ywzqh).get(0);
			Map<String, Object> map_zyjs = dao.doSqlQuery(sjyb_zyjs, map_ywzqh).get(0);
			Map<String, Object> map_zyjs_zf = dao.doSqlQuery(sjyb_zyjs_zf,map_ywzqh).get(0);
			Double ZXYLFZE  = getDouble(map_mzjs.get("BCYLFZE")+"") +  getDouble(map_ghjs.get("BCYLFZE")+"") + getDouble(map_zyjs.get("BCYLFZE")+"") - getDouble(map_mzjs_zf.get("BCYLFZE")+"") -  getDouble(map_ghjs_zf.get("BCYLFZE")+"") - getDouble(map_zyjs_zf.get("BCYLFZE")+"");
			Double ZXTCZFJE = getDouble(map_mzjs.get("BCTCZFJE")+"") +  getDouble(map_ghjs.get("BCTCZFJE")+"") + getDouble(map_zyjs.get("BCTCZFJE")+"") - getDouble(map_mzjs_zf.get("BCTCZFJE")+"") -  getDouble(map_ghjs_zf.get("BCTCZFJE")+"") - getDouble(map_zyjs_zf.get("BCTCZFJE")+"");
			Double ZXDBJZZF = getDouble(map_mzjs.get("BCDBJZZF")+"") +  getDouble(map_ghjs.get("BCDBJZZF")+"") + getDouble(map_zyjs.get("BCDBJZZF")+"") - getDouble(map_mzjs_zf.get("BCDBJZZF")+"") -  getDouble(map_ghjs_zf.get("BCDBJZZF")+"") - getDouble(map_zyjs_zf.get("BCDBJZZF")+"");
			Double ZXDBBXZF = getDouble(map_mzjs.get("BCDBBXZF")+"") +  getDouble(map_ghjs.get("BCDBBXZF")+"") + getDouble(map_zyjs.get("BCDBBXZF")+"") - getDouble(map_mzjs_zf.get("BCDBBXZF")+"") -  getDouble(map_ghjs_zf.get("BCDBBXZF")+"") - getDouble(map_zyjs_zf.get("BCDBBXZF")+"");
			Double ZXMZBZZF = getDouble(map_mzjs.get("BCMZBZZF")+"") +  getDouble(map_ghjs.get("BCMZBZZF")+"") + getDouble(map_zyjs.get("BCMZBZZF")+"") - getDouble(map_mzjs_zf.get("BCMZBZZF")+"") -  getDouble(map_ghjs_zf.get("BCMZBZZF")+"") - getDouble(map_zyjs_zf.get("BCMZBZZF")+"");
			Double ZXZHZFZE = getDouble(map_mzjs.get("BCZHZFZE")+"") +  getDouble(map_ghjs.get("BCZHZFZE")+"") + getDouble(map_zyjs.get("BCZHZFZE")+"") - getDouble(map_mzjs_zf.get("BCZHZFZE")+"") -  getDouble(map_ghjs_zf.get("BCZHZFZE")+"") - getDouble(map_zyjs_zf.get("BCZHZFZE")+"");
			Double ZXXJZFZE = getDouble(map_mzjs.get("BCXZZFZE")+"") +  getDouble(map_ghjs.get("BCXZZFZE")+"") + getDouble(map_zyjs.get("BCXZZFZE")+"") - getDouble(map_mzjs_zf.get("BCXZZFZE")+"") -  getDouble(map_ghjs_zf.get("BCXZZFZE")+"") - getDouble(map_zyjs_zf.get("BCXZZFZE")+"");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("ZXYLFZE", BSPHISUtil.getDouble(ZXYLFZE+"",2));
			map.put("ZXTCZFJE",BSPHISUtil.getDouble(ZXTCZFJE+"",2));
			map.put("ZXDBJZZF",BSPHISUtil.getDouble(ZXDBJZZF+"",2));
			map.put("ZXDBBXZF",BSPHISUtil.getDouble(ZXDBBXZF+"",2));
			map.put("ZXMZBZZF",BSPHISUtil.getDouble(ZXMZBZZF+"",2));
			map.put("ZXZHZFZE",BSPHISUtil.getDouble(ZXZHZFZE+"",2));
			map.put("ZXXJZFZE",BSPHISUtil.getDouble(ZXXJZFZE+"",2));
			return map;
		} catch (PersistentDataOperationException e) {
			logger.error("南京医保查询本地金额出错。");
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR,"南京医保查询本地数据出错：" + e.getMessage());
		}
	}
	public Map<String,Object> doGetZymxxx(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		
		long ZYH = Long.parseLong(req.get("ZYH")+"");
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		String sql_zyxx = "select b.ryzd as RYZD, b.njjblsh as ZYLSH, b.czgh as CZGH,d.personname as YSXM," +
				" b.zyzd as ICD10, b.brks as BRKS, c.shbzkh as SHBZKH,b.NJJBYLLB as NJJBYLLB," +
				" b.YBMC as YBMC,b.CYFS as CYFS,b.YBZY as YBZY,b.CYRQ as CYRQ " +
				" from zy_brry b , sys_personnel d, " +
				" ms_brda c,njjb_kxx d where b.zyh = :ZYH and  b.czgh = d.personid and b.brid=c.brid  and c.sfzh=d.sfzh";
		StringBuffer input = new StringBuffer();
		try {
			List<Map<String,Object>> list = dao.doSqlQuery(sql_zyxx, parameters);
			if(list.size()==0){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "查询病人信息失败，请联系管理员!");
			}
			String zylshcs = list.get(0).get("ZYLSH")+"|";//门诊/住院流水号
			String zylsh = list.get(0).get("ZYLSH")+"";
			String cyfs=list.get(0).get("ZYLSH")+"";//出院方式
			String trancyfs="01";//
			String ybzy="";
			if(cyfs.equals("2")){
				trancyfs="05";
			}else if(cyfs.equals("3")||cyfs.equals("4")||cyfs.equals("6")){
				trancyfs="04";
			}else if(cyfs.equals("5")){
				trancyfs="02";
			}else if(cyfs.equals("7")){
				trancyfs="03";
				ybzy=list.get(0).get("YBZY")==null?"":list.get(0).get("YBZY")+"";
			}
			String cs="";
			String fmrq="";
			String trs="";
			if("52".equals(list.get(0).get("NJJBYLLB")+"")){
				cs="1";
				fmrq=sf.format(new Date());
				trs="1";
			}
			/**begin********zhaojian 2019-01-31 解决出院日期取当前日期导致医保结算金额不正确的问题******/
			Date cyrq = new Date();
			if (list.get(0).get("CYRQ") != null) {
				cyrq = (java.util.Date)list.get(0).get("CYRQ");
			}
			/**end********zhaojian 2019-01-31 解决出院日期取当前日期导致医保结算金额不正确的问题******/
			input.append(list.get(0).get("NJJBYLLB")+"").append("|")//医疗类别
			     .append(sf.format(new Date())).append("|")//结算日期
			     //.append(sf.format(new Date())).append("|")//出院日期
			     .append(sf.format(cyrq)).append("|")//出院日期 zhaojian 2019-01-31 解决出院日期取当前日期导致医保结算金额不正确的问题
			     .append(trancyfs).append("|")//出院原因
			     .append(list.get(0).get("YBMC")+"").append("|")//出院诊断疾病编码
			     .append("03").append("|")//月结算类别
			     .append("0").append("|")//中途结算标志
			     .append(list.get(0).get("YSXM")).append("|")//经办人
			     .append(fmrq).append("|")//分娩日期
			     .append(cs).append("|")//产次
			     .append(trs).append("|")//胎儿数
			     .append(list.get(0).get("SHBZKH")).append("|")//社会保障卡号
			     .append(ybzy).append("|")//转院医院编号
			     .append(list.get(0).get("BRKS")).append("|")//科室编码
			     .append(list.get(0).get("CZGH")).append("|")//医生编码
			     .append("").append("|")//是否为挂号费结算
			     .append("").append("|")//准生儿社会保障卡号
			     .append("").append("|");//手术是否成功标志
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("ZYLSHCS", zylshcs);
			map.put("ZYLSH", zylsh);
			map.put("CS", input+"");
			return map;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医保费用失败，请联系管理员!");
		}
		
		
	}
	public double getDouble(String str) {
		double d = Double.parseDouble(str);
		return d;
		
	}
	@SuppressWarnings("unchecked")
	public void qeryForXYbdzMxbdList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> mapbody = (Map<String, Object>) req.get("body");
		String DZKSSJ = mapbody.get("DZKSSJ") + "";
		String DZJSSJ = mapbody.get("DZJSSJ") + "";
		res.put("body", queryXBdYBMxDz(DZKSSJ.trim(),DZJSSJ.trim()));
	}
	public List<Map<String, Object>> queryXBdYBMxDz(String DZKSSJ,String DZJSSJ)
			throws ModelDataOperationException {
		StringBuffer bdmx = new StringBuffer();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		String sjjgid=jgid.substring(0,9);
		StringBuffer jgsql=new StringBuffer();
		jgsql.append("select a.ORGANIZCODE as ORGANIZCODE from SYS_ORGANIZATION a")
		.append(" where a.ORGANIZCODE like '"+sjjgid+"%' order by a.organizcode");
		List<Map<String,Object>> jgl=new ArrayList<Map<String,Object>>();
		try{
			jgl=dao.doSqlQuery(jgsql.toString(),null);
		}catch(PersistentDataOperationException e1){
			e1.printStackTrace();
		}
		if(jgl!=null && jgl.size()>0){
			try{
				Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
				String ybjgbm=njjb.getText(jgid);
				if(ybjgbm.length()==0 && ybjgbm.equals("null")){
					logger.info("phis.dictionary.NJJB未配置"+jgid);
					return new ArrayList<Map<String,Object>>();
				}
				String jgids="";
				for(Map<String,Object> m:jgl){
					if(ybjgbm.equals(njjb.getText(m.get("ORGANIZCODE")+""))){
						jgids+=",'"+m.get("ORGANIZCODE")+"'";
					}
				}
				jgids="("+jgids.substring(1)+")";
				bdmx.append("select a.FPHM as FPHM,a.LSH as LSH, nvl(a.BCYLFZE,0) as BCYLFZE,nvl(a.BCTCZFJE,0) as BCTCZFJE,"+
				"nvl(a.BCDBJZZF,0) as BCDBJZZF,nvl(a.BCDBBXZF,0) as BCDBBXZF,nvl(a.BCMZBZZF,0) as BCMZBZZF,"+
				"nvl(a.BCZHZFZE,0) as BCZHZFZE,nvl(a.BCXZZFZE,0) as BCXZZFZE,b.brxm as BRXM,"+
				"a.JSSJ as JSSJ,'门诊' as JSXL, a.jylsh as JYLSH "+
				"from njjb_jsxx a,ms_mzxx b where a.mzxh=b.mzxh and a.zfpb=0 " +
				"and to_char(a.jssj,'yyyyMMdd')<=:DZJSSJ and to_char(a.jssj,'yyyyMMdd')>=:DZKSSJ " +
				"and a.jgid in "+jgids+
				" union all " +
				"select a.fphm as FPHM, a.LSH as LSH," +
				"nvl(a.BCYLFZE,0) as BCYLFZE,nvl(a.BCTCZFJE, 0) as BCTCZFJE,nvl(a.BCDBJZZF, 0) as BCDBJZZF," +
				"nvl(a.BCDBBXZF,0) as BCDBBXZF,nvl(a.BCMZBZZF, 0) as BCMZBZZF,nvl(a.BCZHZFZE, 0) as BCZHZFZE," +
				"nvl(a.BCXZZFZE,0) as BCXZZFZE,c.brxm as BRXM, a.jssj as JSSJ,'挂号' as JSXL," +
				"a.jylsh as JYLSH from NJJB_JSXX a,MS_GHMX b,MS_BRDA c where a.GHXH=b.SBXH " +
				"and b.brid=c.brid and a.zfpb=0 and to_char(a.jssj,'yyyyMMdd')<=:DZJSSJ " +
				"and to_char(a.jssj,'yyyyMMdd')>=:DZKSSJ and a.jgid in "+jgids+
				" union all " +
				"select a.fphm as FPHM, a.LSH as LSH, nvl(a.BCYLFZE, 0) as BCYLFZE," +
				"nvl(a.BCTCZFJE,0) as BCTCZFJE,nvl(a.BCDBJZZF,0) as BCDBJZZF,nvl(a.BCDBBXZF,0) as BCDBBXZF," +
				"nvl(a.BCMZBZZF,0) as BCMZBZZF,nvl(a.BCZHZFZE,0) as BCZHZFZE,nvl(a.BCXZZFZE,0) as BCXZZFZE," +
				"b.brxm as BRXM,a.jssj as JSSJ,'住院' as jsxl,a.jylsh as JYLSH " +
				"from NJJB_JSXX a,ZY_BRRY b where a.ZYH=b.ZYH and a.ZFPB=0 " +
				"and to_char(a.jssj,'yyyyMMdd')<=:DZJSSJ and to_char(a.jssj,'yyyyMMdd')>=:DZKSSJ " +
				"and a.jgid in "+jgids);
				Map<String, Object> map_mxjs = new HashMap<String, Object>();
				map_mxjs.put("DZKSSJ", DZKSSJ);
				map_mxjs.put("DZJSSJ", DZJSSJ);
				try {
					String queryHql=bdmx.toString();
					List<Map<String,Object>> map_mxjs_Value=dao.doSqlQuery(queryHql,map_mxjs);
					logger.debug("返回本地对账明细数据");
					return map_mxjs_Value;
				} catch (PersistentDataOperationException e) {
					System.out.println("错误信息："+e.toString()+"<<<终结。");
					logger.error("查询本地对账明细数据失败。");
					throw new ModelDataOperationException(ServiceCode.NO_RECORD,"查询本地对账明细数据失败。");
				}
			}catch(ControllerException e){
				e.printStackTrace();
			}
		}
		logger.info("程序有问题才能运行到这，请检查");
		return new ArrayList<Map<String,Object>>();
	}
}
