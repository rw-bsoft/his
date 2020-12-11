package phis.application.hos.source;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.application.ivc.source.TreatmentNumberModule;
import phis.application.xnh.source.XnhModel;
import phis.application.yb.source.MedicareModel;
import phis.application.yb.source.YBModel;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class HospitalPatientSelectionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(TreatmentNumberModule.class);

	public HospitalPatientSelectionModel(BaseDAO dao) {
		this.dao = dao;
	}
	@SuppressWarnings("unchecked")
	public void doGetPatientList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		List<Object> cnd = (List<Object>) req.get("cnd");
		String schema = (String) req.get("schema");
		try {
			res.put("body",dao.doList(cnd, "", "phis.application.hos.schemas."+ schema));
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}
	//住院结算管理：医保病人分离自费和医保部分
	@SuppressWarnings("unchecked")
	public void doGetPatientList_JSGL(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String schema = (String) req.get("schema");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		//StringBuffer hql = new StringBuffer("select a.BRXM as BRXM,a.ZYHM as ZYHM,a.JGID as JGID,a.BRID as BRID,a.BRCH as BRCH,a.ZYH as ZYH,a.NJJBYLLB as NJJBYLLB,a.YBMC as YBMC,a.BRXZ as BRXZ,a.NJJBLSH as NJJBLSH,a.JSRQ as JSRQ,a.CYRQ as CYRQ,a.RYRQ as RYRQ,a.CYPB as CYPB from ZY_BRRY a where 0=0 ");
		String hql = "";
		String sql_jgid = manaUnitId.equals("320124005016")?"substr(c.jgid,0,9)=d.jgid":"c.jgid=d.jgid";
		int JSLX = Integer.parseInt(req.get("JSLX")+"");
		switch(JSLX){
		case 1://中途结算
			//(a.BRXZ not in(select BRXZ from GY_BRXZ where DBPB > 0) and a.JGID = '320124004' and a.CYPB = 0)
			//ZY_BRRY_ZT
			hql="";
			break;
		case 4://出院终结
		case 5://出院结算
			//(a.JGID = '320124004' and a.CYPB = 1)
			//ZY_BRRY_CYLB_ZC
			hql="select a.JSCS as JSCS,a.BRXM as BRXM,a.ZYHM as ZYHM,a.JGID as JGID,a.BRID as BRID,a.BRCH as BRCH,a.ZYH as ZYH," +
					"a.NJJBYLLB as NJJBYLLB,a.YBMC as YBMC,a.BRXZ as BRXZ,a.NJJBLSH as NJJBLSH,a.JSRQ as JSRQ," +
					"a.CYRQ as CYRQ,a.RYRQ as RYRQ,a.CYPB as CYPB,case when a.BRXZ=1000 then '全自费' else decode(a.JSXZ,1000,'全自费','医保') end as JSXZ from (" +
					"select a.JSCS,a.BRXM,a.ZYHM,a.JGID,a.BRID,a.BRCH,a.ZYH,a.NJJBYLLB,a.YBMC,a.BRXZ,a.NJJBLSH,a.JSRQ,a.CYRQ,a.RYRQ,a.CYPB," +
					"case when brxz=1000 then 1000 when brxz=2000 and exists (select c.fyxh,d.yyzbm from zy_fymx c,yk_cdxx d " +
					"where "+sql_jgid+" and c.fyxh=d.ypxh and c.ypcd=d.ypcd and c.zyh=a.zyh and c.ypcd>0 and d.yyzbm is null " +
					"union " +
					"select c.fyxh,d.yyzbm from ZY_FYMX c,gy_ylmx d " +
					"where "+sql_jgid+" and c.fyxh=d.fyxh and c.zyh=a.zyh and c.ypcd=0 and d.yyzbm is null) then 1000 else 0 end as jsxz," +
					"case when exists (select zyh from zy_zyjs where zyh=a.zyh and jsxz=1000 and zfpb=0) then 1 else 0 end as isJs from zy_brry a " +
					"where a.jgid=:JGID and a.cypb=1 " +
					"union " +
					"select a.JSCS,a.BRXM,a.ZYHM,a.JGID,a.BRID,a.BRCH,a.ZYH,a.NJJBYLLB,a.YBMC,a.BRXZ,a.NJJBLSH,a.JSRQ,a.CYRQ,a.RYRQ,a.CYPB," +
					"case when brxz=2000 and exists (select c.fyxh,d.yyzbm from zy_fymx c,yk_cdxx d " +
					"where "+sql_jgid+" and c.fyxh=d.ypxh and c.ypcd=d.ypcd and c.zyh=a.zyh and c.ypcd>0 and d.yyzbm is not null " +
					"union " +
					"select c.fyxh,d.yyzbm from ZY_FYMX c,gy_ylmx d " +
					"where "+sql_jgid+" and c.fyxh=d.fyxh and c.zyh=a.zyh and c.ypcd=0 and d.yyzbm is not null) then 2000 else 0 end as jsxz," +
					"case when exists (select zyh from zy_zyjs where zyh=a.zyh and jsxz=2000 and zfpb=0) then 1 else 0 end as isJs from zy_brry a " +
					"where a.jgid=:JGID and a.cypb=1) a where a.jsxz>0 and a.isJs=0";
			break;
		case 10://发票作废 
			//and ((b.ZFPB = 0 and a.JGID = '320124004') and (b.JSLX = 5 or b.JSLX = 1 and a.CYPB <> 8))
			//ZY_BRRY_CYLB
			hql ="select a.ZYH as ZYH,a.JGID as JGID,a.BRID as BRID,a.ZYHM as ZYHM,a.BRXM as BRXM,a.BRCH as BRCH," +
					"a.BRXZ as BRXZ,a.RYRQ as RYRQ,a.CYRQ as CYRQ,a.NJJBLSH as NJJBLSH,a.NJJBYLLB as NJJBYLLB," +
					"a.YBMC as YBMC,a.JSRQ as JSRQ,a.JSCS as JSCS,a.CYPB as CYPB,case when a.BRXZ=1000 then '全自费' else decode(a.JSXZ,1000,'全自费','医保') end as JSXZ," +
					"b.FPHM as FPHM,b.JSLX as JSLX,b.JSCS as JSCS,b.KSRQ as KSRQ,b.ZZRQ as ZZRQ,b.FYHJ as FYHJ," +
					"b.ZFHJ as ZFHJ,b.JKHJ as JKHJ,b.JSRQ as JSRQ,b.CZGH as CZGH " +
					"from (select a.ZYH,a.JGID,a.BRID,a.ZYHM,a.BRXM,a.BRCH,a.BRXZ,a.RYRQ,a.CYRQ,a.NJJBLSH,a.NJJBYLLB,a.YBMC,a.JSRQ,a.JSCS,a.CYPB," +
					"case when a.brxz=1000 then 1000 when exists (select c.fyxh,d.yyzbm from zy_fymx c,yk_cdxx d " +
					"where "+sql_jgid+" and c.fyxh=d.ypxh and c.ypcd=d.ypcd and c.zyh=a.zyh and c.ypcd>0 and d.yyzbm is null " +
					"union " +
					"select c.fyxh,d.yyzbm from ZY_FYMX c,gy_ylmx d " +
					"where "+sql_jgid+" and c.fyxh=d.fyxh and c.zyh=a.zyh and c.ypcd=0 and d.yyzbm is null) then 1000 else 0 end as jsxz," +
					"case when exists (select zyh from zy_zyjs where zyh=a.zyh and jsxz=1000) then 1 else 0 end as isJs " +
					"from zy_brry a where a.jgid=:JGID and (a.cypb = 8 or a.cypb = 1) " +
					"union " +
					"select a.ZYH,a.JGID,a.BRID,a.ZYHM,a.BRXM,a.BRCH,a.BRXZ,a.RYRQ,a.CYRQ,a.NJJBLSH,a.NJJBYLLB,a.YBMC,a.JSRQ,a.JSCS,a.CYPB," +
					"case when exists (select c.fyxh,d.yyzbm from zy_fymx c,yk_cdxx d " +
					"where "+sql_jgid+" and c.fyxh=d.ypxh and c.ypcd=d.ypcd and c.zyh=a.zyh and c.ypcd>0 and d.yyzbm is not null " +
					"union " +
					"select c.fyxh,d.yyzbm from ZY_FYMX c,gy_ylmx d " +
					"where "+sql_jgid+" and c.fyxh=d.fyxh and c.zyh=a.zyh and c.ypcd=0 and d.yyzbm is not null) then 2000 else 0 end as jsxz," +
					"case when exists (select zyh from zy_zyjs where zyh=a.zyh and jsxz=2000) then 1 else 0 end as isJs " +
					"from zy_brry a where a.jgid=:JGID and (a.cypb = 8 or a.cypb = 1)) a,ZY_ZYJS b " +
					"where a.zyh=b.zyh and a.jsxz=b.jsxz and a.jsxz>0 and a.isJs=1 and b.ZFPB = 0 and (b.JSLX = 5 or b.JSLX = 1)";
			break;
		default:
			break;
		}
		try {
			if (req.get("cnd") != null) {
				List<Object> cnd = (List<Object>) req.get("cnd");
				//hql.append(" and " + ExpRunner.toString(cnd, ctx));
				//hql+=" and " + ExpRunner.toString(cnd, ctx);
			}
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list,"phis.application.hos.schemas.ZY_BRRY_CYLB_ZC");
			res.put("body",list);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}  /*catch (ExpException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
			}*/
	}
	
	/**
	  * 获取jsxz
	  * 
	  * @param req
	  * @param ctx
	  */
	 public Map<String, Object> doGetjsxz(String req, Context ctx)throws ModelDataOperationException {

	  Map<String, Object> parameters = new HashMap<String, Object>();
	  parameters.put("FPHM", req);
	  Map<String, Object> res = new HashMap<String, Object>();
	 
	   List<Map<String, Object>> JSXZ;
	   try {
	    JSXZ = dao.doSqlQuery("select jsxz as JSXZ from ZY_ZYJS where FPHM=:FPHM", parameters);
	    if (JSXZ.size()>0) {
	     res.put("JSXZ", JSXZ.get(0).get("JSXZ"));
	    } else {
	     res.put("JSXZ", "");
	    }
	   } catch (PersistentDataOperationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	   }
	   return res;
	 }

	@SuppressWarnings("unchecked")
	public void doQuerySelectionForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		doGetSelectionForm(req, res, ctx);
		body.putAll((Map<String, Object>) res.get("body"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		int ZYH = Integer.parseInt(body.get("ZYH") + "");
		int JSCS = Integer.parseInt(body.get("JSCS") + "");
		String HM = "";
		if(body.get("FPHM")!=null && !((body.get("FPHM") + "").equals(""))){
			HM = body.get("FPHM") + "";
		}
		Map<String, Object> js = doGetjsxz(HM, ctx);
		int JSXZ = ((js.get("JSXZ") + "").equals("全自费")|(js.get("JSXZ") + "").equals("1000"))? 1000:2000;
		parameters.put("ZYH", ZYH);
		parameters.put("JSCS", JSCS);
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "ZYH", "i", ZYH);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "JSCS", "i", JSCS);
			List cnd3 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List cnd4 = CNDHelper.createArrayCnd("and", cnd3, CNDHelper.createSimpleCnd("eq", "JSXZ", "i", JSXZ));
			List cnd = null;
			if(body.get("FPHM")!=null && !((body.get("FPHM") + "").equals(""))){
				String FPHM = body.get("FPHM") + "";
				List cnd5 = CNDHelper.createSimpleCnd("eq", "FPHM", "s", FPHM);
				cnd = CNDHelper.createArrayCnd("and", cnd4, cnd5);
			}else{
				cnd = cnd4;
			}
			Map<String, Object> BRRY = dao
					.doLoad(BSPHISEntryNames.ZY_BRRY, ZYH);
			Map<String, Object> ZY_ZYJS = dao.doLoad(cnd,BSPHISEntryNames.ZY_ZYJS);
			body.putAll(BRRY);
			body.putAll(ZY_ZYJS);
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQuerySelectionList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		parameters.put("ZYH", ZYH);
		int JSXZ = ((body.get("JSXZ") + "").equals("全自费")|(body.get("JSXZ") + "").equals("1000"))? 1000:2000;
		int jscs = (Integer) body.get("JSCS");
		try {
			String sql_jsxz = "";
			if(body.get("FPHM") != null && !(body.get("FPHM")+"").equals("")){
				sql_jsxz = " and b.JSXZ="+JSXZ;
			}
			List<Map<String, Object>> FYZKBD = dao.doSqlQuery(
							" select distinct ZYH as ZYH,JSCS as JSCS,ZYGB as ZYGB,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE," +
							" sum(ZLJE) as ZLJE from (SELECT b.ZYH as ZYH,b.JSCS as JSCS,a.ZYGB as ZYGB," +
							" sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE,sum(b.ZLJE) as ZLJE FROM "+ 
							" GY_SFXM a,ZY_FYMX b WHERE a.SFXM = b.FYXM AND b.ZYH = '"+ZYH+"' AND b.JSCS = "+ jscs + sql_jsxz +
							" GROUP BY a.ZYGB,b.ZYH,b.JSCS union select "+ body.get("ZYH")+ " as ZYH,"+ jscs+ 
							" as JSCS,ZYGB as ZYGB,0 as ZJJE,0 as ZFJE,0 as ZLJE from "+ 
							" GY_SFXM a where a.ZYSY = 1 and a.sfxm not in (SELECT b.FYXM from "+ 
							" ZY_FYMX b where b.ZYH = '"+ZYH+"'" + sql_jsxz + ")) GROUP BY ZYH,ZYGB,JSCS order by ZYGB",
							null);
			SchemaUtil.setDictionaryMassageForList(FYZKBD,
					"phis.application.hos.schemas.ZY_JSGL_LIST");
			List<Map<String, Object>> RFYZKBD = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < FYZKBD.size(); i = i + 2) {
				Map<String, Object> FYZK = new HashMap<String, Object>();
				for (Map.Entry<String, Object> m : FYZKBD.get(i).entrySet()) {
					FYZK.put(m.getKey(), m.getValue());
				}
				if (i + 1 < FYZKBD.size()) {
					for (Map.Entry<String, Object> m : FYZKBD.get(i + 1)
							.entrySet()) {
						if (m.getKey().indexOf("text") > 0) {
							FYZK.put("ZYGB2_text", m.getValue());
						} else {
							FYZK.put(m.getKey() + "2", m.getValue());
						}
					}
				}
				RFYZKBD.add(FYZK);
			}
			res.put("body", RFYZKBD);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}

	/*
	 * 住院结算：加载表单信息
	 */
	@SuppressWarnings("unchecked")
	public void doGetSelectionForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		int JSXZ = ((body.get("JSXZ") + "").equals("全自费")|(body.get("JSXZ") + "").equals("1000"))? 1000:2000;
		parameters.put("ZYH", ZYH);
		try {
			if ("5".equals(body.get("JSLX") + "")|| "4".equals(body.get("JSLX") + "")) {
				long count = dao.doCount("ZY_BRRY", "CYPB = 1 AND ZYH = :ZYH",parameters);
				if (count == 0) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在结算病人列表!");
				}
				parameters.put("JSXZ", JSXZ);
				count = dao.doSqlCount("ZY_ZYJS", "ZFPB = 0 and ZYH = :ZYH and JSXZ=:JSXZ",parameters);
				parameters.remove("JSXZ");
				if (count > 0) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该结算单已结算!");
				}
			} else if ("10".equals(body.get("JSLX") + "")) {
				parameters.put("JSXZ", JSXZ);
				long count = dao.doSqlCount("ZY_BRRY a,ZY_ZYJS b",
								"a.ZYH = b.ZYH and b.ZFPB = 0 and b.JSLX <> 4 and a.ZYH = :ZYH and b.JSXZ=:JSXZ",
								parameters);
				parameters.remove("JSXZ");
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在发票作废病人列表!");
				}
			} else if ("1".equals(body.get("JSLX") + "")) {
				long count = dao.doCount("ZY_BRRY",
								"CYPB = 0 AND BRXZ NOT IN ( SELECT BRXZ FROM GY_BRXZ WHERE DBPB > 0 )  AND ZYH = :ZYH",
								parameters);
				if (count == 0) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在中途结算病人列表!");
				}
			}
			Map<String, Object> BRXX = dao.doLoad("SELECT ZYH as ZYH,BAHM as BAHM,ZYHM as ZYHM,BRCH as BRCH," +
					" BRXM as BRXM,BRQM as BRQM,BRXZ as BRXZ,BRKS as BRKS,BRBQ as BRBQ,RYRQ as RYRQ,CYRQ as CYRQ," +
					" CYPB as CYPB,0 as JSLX,0.00 as FYHJ,0.00 as ZFHJ,0.00 as JKHJ,JSRQ as JSRQ,nvl(JSCS,0) as JSCS," +
					" KSRQ as KSRQ,KSRQ as LJRQ,HZRQ as HZRQ,HZRQ as XTRQ,'' as REMARK,0 as ZFPB,SJZY as SJZY," +
					" DJID as DJID FROM ZY_BRRY" + " WHERE ZYH = :ZYH ", parameters);
			if ("5".equals(body.get("JSLX") + "")|| "4".equals(body.get("JSLX") + "")) {
				Map<String, Object> CYRQ = dao.doLoad("select CYRQ as JSRQ from " + "ZY_BRRY where ZYH = :ZYH",
						parameters);
				BRXX.putAll(CYRQ);
			}
			if ("-1".equals(body.get("JSLX") + "")) {
				BRXX.put("JSLX", body.get("JSLX"));
			}
			if (body.containsKey("JSCS")) {
				BRXX.put("JSCS", body.get("JSCS"));
			}
			if (body.get("FPHM")!=null && !(body.get("FPHM")+"").equals("")) {
				BRXX.put("FPHM", body.get("FPHM"));
				parameters.put("FPHM", body.get("FPHM")+"");
				parameters.put("JSXZ", JSXZ);
				Map<String, Object> ZY_ZYJS = dao.doSqlLoad("SELECT FKFS as FKFS FROM ZY_ZYJS" + " WHERE ZYH = :ZYH AND FPHM=:FPHM and JSXZ=:JSXZ", parameters);
				BRXX.put("FKFS", ZY_ZYJS.get("FKFS"));
				parameters.remove("FPHM");
				parameters.remove("JSXZ");
			}
			if (BRXX.get("JSRQ") == null) {
				BRXX.put("JSRQ", body.get("JSRQ"));
			}
			if ("10".equals(body.get("JSLX") + "")) {
				if ("1000".equals(BRXX.get("BRXZ") + "")) {
					BRXX = BSPHISUtil.gf_zy_gxmk_getjsje(BRXX, dao, ctx);
				}else{
					BRXX = BSPHISUtil.gf_zy_gxmk_getjsje(BRXX, JSXZ, dao, ctx);
				}
				BRXX.put("ZYHM", (String) body.get("FPHM"));// 用于LoadData数据时，将发票号码
															// 显示在住院号码的TextField上
			} else if ("5".equals(body.get("JSLX") + "")
					|| "4".equals(body.get("JSLX") + "")
					|| "1".equals(body.get("JSLX") + "")
					|| "0".equals(body.get("JSLX") + "")
					|| "-1".equals(body.get("JSLX") + "")) {
				Map<String, Object> GY_SFXMparameters = new HashMap<String, Object>();
				GY_SFXMparameters.put("ZYH",
						Long.parseLong(body.get("ZYH") + ""));
				GY_SFXMparameters.put("JGID", JGID);

				String jscs = "";
				if ("10".equals(body.get("JSLX") + "")) {
					jscs = "";
				} else if ("-1".equals(body.get("JSLX") + "")) {
					jscs = " and JSCS = " + body.get("JSCS");
				} else {
					jscs = " and JSCS = 0 ";
				}
				Map<String, Object> FYXX = new HashMap<String, Object>();
				Map<String, Object> YL_FYXX = new HashMap<String, Object>();
				Map<String, Object> YP_FYXX = new HashMap<String, Object>();
				String sql_fyxx = "";
				String sql_yl_fyxx = "";
				String sql_yp_fyxx = "";
				sql_fyxx = "SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a WHERE a.ZYH = :ZYH "+ jscs + " and a.JGID = :JGID";
				sql_yl_fyxx = "SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a WHERE a.ZYH = :ZYH and a.JGID = :JGID and YPLX = 0"+ jscs;
				sql_yp_fyxx = "SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a WHERE a.ZYH = :ZYH and a.JGID = :JGID and YPLX <> 0"+ jscs;
				if(BRXX.get("FPHM") == null){
					if ("2000".equals(BRXX.get("BRXZ") + "")) {
						String sql_jsxz = "";
						if(JSXZ==1000){
							sql_jsxz = " and b.YYZBM is null ";
						}else{
							//医保病人：当前若为医保结算时，需判定是否存在自费单据
/*							long count = dao.doSqlCount("ZY_FYMX a",
											"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and JSCS = 0 and exists (select yyzbm from yk_cdxx " +
											"where jgid = a.jgid and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is null" +
											" union " +
											"select yyzbm from gy_ylmx " +
											"where jgid = a.jgid and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is null)",
											null);
							if (count > 0) {
							}*/
							sql_jsxz = " and b.YYZBM is not null ";
						}
						sql_fyxx = "SELECT sum(a.ZJJE) as ZJJE from (SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a,YK_CDXX b WHERE a.ZYH = :ZYH "+ jscs + " and a.JGID = :JGID and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.YPXH and a.YPCD=b.YPCD and a.YPLX <> 0 "+sql_jsxz+
								" UNION SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a,GY_YLMX b WHERE a.ZYH = :ZYH "+ jscs + " and a.JGID = :JGID and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.FYXH and a.YPLX = 0 "+sql_jsxz+") a";
						sql_yl_fyxx = "SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a,GY_YLMX b WHERE a.ZYH = :ZYH "+ jscs + " and a.JGID = :JGID and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.FYXH and a.YPLX = 0 "+sql_jsxz;
						sql_yp_fyxx = "SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a,YK_CDXX b WHERE a.ZYH = :ZYH "+ jscs + " and a.JGID = :JGID and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.YPXH and a.YPCD=b.YPCD and a.YPLX <> 0 "+sql_jsxz;
						GY_SFXMparameters.remove("JSXZ");
					}
				}else{
					sql_fyxx += " and JSXZ=:JSXZ";
					sql_yl_fyxx += " and JSXZ=:JSXZ";
					sql_yp_fyxx += " and JSXZ=:JSXZ";
					GY_SFXMparameters.put("JSXZ", JSXZ);
				}
				FYXX = dao.doSqlLoad(sql_fyxx,GY_SFXMparameters);
				YL_FYXX = dao.doSqlLoad(sql_yl_fyxx,GY_SFXMparameters);
				YP_FYXX = dao.doSqlLoad(sql_yp_fyxx,GY_SFXMparameters);
/*				if ("2000".equals(BRXX.get("BRXZ") + "")) {
					BRXX = BSPHISUtil.gf_zy_gxmk_getjkhj(BRXX, JSXZ, dao, ctx);
					BRXX = BSPHISUtil.gf_zy_gxmk_getzfhj(BRXX, JSXZ, dao, ctx);
				}*/
				BRXX = BSPHISUtil.gf_zy_gxmk_getjkhj(BRXX, JSXZ, dao, ctx);
				BRXX = BSPHISUtil.gf_zy_gxmk_getzfhj(BRXX, JSXZ, dao, ctx);
				BRXX.put("FYHJ", 0);
				if (FYXX.get("ZJJE") != null) {
					BRXX.put("FYHJ", FYXX.get("ZJJE"));
				} else {
					BRXX.put("FYHJ", 0);
				}
				if (YL_FYXX.get("ZJJE") != null) {
					BRXX.put("YL_FYHJ", YL_FYXX.get("ZJJE"));
				} else {
					BRXX.put("YL_FYHJ", 0);
				}
				if (YP_FYXX.get("ZJJE") != null) {
					BRXX.put("YP_FYHJ", YP_FYXX.get("ZJJE"));
				} else {
					BRXX.put("YP_FYHJ", 0);
				}
			}
			BRXX.put("TCJE", "0.00");
			BRXX.put("ZHZF", "0.00");
			// if ("5".equals(body.get("JSLX") + "")){
			// if(body.get("YWLSH")!=null&&body.get("YWLSH")!=""&&(Long.parseLong(body.get("YWLSH")+"")>0l)){
			// JXMedicareModel jxmm = new JXMedicareModel(dao);
			// String cyyy = "";
			// Map<String,Object> ZY_BRRY = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
			// ZYH);
			// SchemaUtil.setDictionaryMassageForList(ZY_BRRY,"ZY_BRRY_CY2");
			// cyyy = ZY_BRRY.get("CYFS_text")+"";
			// String cyzdbm = "";
			// String cyzdmc = "";
			// List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "ZYH", "i",
			// (int)ZYH);
			// List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "ZDLB", "i",
			// 3);
			// List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			// List<Map<String,Object>> cyzd = dao.doList(cnd, "ZDXH",
			// BSPHISEntryNames.ZY_RYZD);
			// if(cyzd!=null&&cyzd.size()>0){
			// cyzdbm = cyzd.get(0).get("ZDXH")+"";
			// cyzdmc = dao.doLoad(BSPHISEntryNames.GY_JBBM,
			// Long.parseLong(cyzdbm)).get("JBMC")+"";
			// }
			// Map<String,Object> ybmap = new HashMap<String, Object>();
			// ybmap.put("YWLSH", body.get("YWLSH"));//医院流水号
			// ybmap.put("CYYY", cyyy);//出院原因
			// ybmap.put("CYZDBM", cyzdbm);//出院诊断编码
			// ybmap.put("CYZDMC", cyzdmc);//出院诊断名称
			// ybmap.put("ZHSYBZ", 1);//帐号使用标志
			// ybmap.put("ZTJSBZ", 0);//TJSBZ中途结算标志0非中途1中途结算
			// Map<String,Object> YBJS =
			// jxmm.saveHospitalizationPreSettlementCosts(ybmap, ctx);
			// BRXX.put("ZFHJ", YBJS.get("自费费用"));
			// BRXX.put("TCJE", YBJS.get("统筹支出"));
			// BRXX.put("ZHZF", YBJS.get("本次帐户支付"));
			// BRXX.putAll(YBJS);
			// }
			// }
			res.put("body", BRXX);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetSelectionList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String sql_jgid = JGID.equals("320124005016")?"substr(c.jgid,0,9)=d.jgid":"c.jgid=d.jgid";
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Long ZYH = Long.parseLong(body.get("ZYH") + "");
		parameters.put("ZYH", ZYH);
		String jscs = " AND b.JSCS = 0";
		int ijscs = 0;
		try {
			int JSXZ = (body.get("JSXZ") + "").equals("1000") || (body.get("JSXZ") + "").equals("全自费")? 1000:2000;
			Boolean ybbr_existsZfdj = false;
			Boolean ybbr_existsYbdj = false;
			Boolean ybbr_zfdj_wjs = false;//医保病人自费单据未结算
			if(Integer.parseInt(body.get("BRXZ")+"")==2000){
				if(JSXZ==1000){
					ybbr_existsZfdj = true;
					Long count = dao.doSqlCount("ZY_FYMX a",
							"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and exists (select yyzbm from yk_cdxx " +
							"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is not null" +
							" union " +
							"select yyzbm from gy_ylmx " +
							"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is not null)",
							null);
					if (count > 0) {
						ybbr_existsYbdj = true;
					}
				}else{
					ybbr_existsYbdj = true;
					long count = dao.doSqlCount("ZY_FYMX a",
									"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and exists (select yyzbm from yk_cdxx " +
									"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is null" +
									" union " +
									"select yyzbm from gy_ylmx " +
									"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is null)",
									null);
					if (count > 0) {
						ybbr_existsZfdj = true;
						count = dao.doCount("ZY_ZYJS","ZYH=:ZYH and ZFPB=0 and JSXZ=1000", parameters);
						if (count == 0) {
							ybbr_zfdj_wjs = true;
						}
					}
				}
			}
			if ("10".equals(body.get("JSLX") + "")) {
				if(ybbr_zfdj_wjs){
					jscs = " AND b.JSCS = " +( (Integer) body.get("JSCS")+1);
					ijscs = (Integer) body.get("JSCS")+1;
				}else{
					jscs = " AND b.JSCS = " + (Integer) body.get("JSCS");
					ijscs = (Integer) body.get("JSCS");
				}
			} else if (("5".equals(body.get("JSLX") + ""))
					|| ("1".equals(body.get("JSLX") + ""))) {
				jscs = " AND b.JSCS = 0";
			}
			List<Map<String, Object>> FYZKBD = new ArrayList<Map<String, Object>>();
			if(ybbr_existsYbdj && ybbr_existsZfdj){
				String sql_jsxz = "";
				if(JSXZ==1000){
					sql_jsxz = " and d.YYZBM is null ";
				}else{
					sql_jsxz = " and d.YYZBM is not null ";
				}
				FYZKBD = dao.doSqlQuery(
						" select distinct ZYH as ZYH,JSCS as JSCS,ZYGB as ZYGB,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE," +
						" sum(ZLJE) as ZLJE from (SELECT b.ZYH as ZYH,b.JSCS as JSCS,a.ZYGB as ZYGB," +
						" sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE,sum(b.ZLJE) as ZLJE FROM "+
						" GY_SFXM a,(select c.ZYH,c.JSCS,c.ZJJE,c.ZFJE,c.ZLJE,c.FYXM " +
						"from ZY_FYMX c,yk_cdxx d where "+sql_jgid+" and c.fyxh=d.ypxh and c.ypcd=d.ypcd " +
						"and c.zyh=:ZYH and c.yplx<>0 " + sql_jsxz +
						"union all " +
						"select c.ZYH,c.JSCS,c.ZJJE,c.ZFJE,c.ZLJE,c.FYXM from ZY_FYMX c,gy_ylmx d " +
						"where "+sql_jgid+" and c.fyxh=d.fyxh and c.zyh=:ZYH and c.yplx=0 "+sql_jsxz+") b " +
						"WHERE a.SFXM = b.FYXM AND b.ZYH = :ZYH "+ jscs+
						" GROUP BY a.ZYGB,b.ZYH,b.JSCS union select "+ body.get("ZYH")+
						" as ZYH,"+ ijscs+" as JSCS,ZYGB as ZYGB,0 as ZJJE,0 as ZFJE,0 as ZLJE from "+
						" GY_SFXM"+" a where a.ZYSY = 1 and a.sfxm not in (" +
								"select c.FYXM from ZY_FYMX c,yk_cdxx d where "+sql_jgid+" and c.fyxh=d.ypxh " +
								"and c.ypcd=d.ypcd and c.zyh=:ZYH and c.yplx<>0 " + sql_jsxz +
								"union all " +
								"select c.FYXM from ZY_FYMX c,gy_ylmx d where "+sql_jgid+" and c.fyxh=d.fyxh " +
								"and c.zyh=:ZYH and c.yplx=0 "+sql_jsxz+")) GROUP BY ZYH,ZYGB,JSCS order by ZYGB",
						parameters);
			}else{
				FYZKBD = dao.doSqlQuery(
						" select distinct ZYH as ZYH,JSCS as JSCS,ZYGB as ZYGB,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE," +
						" sum(ZLJE) as ZLJE from (SELECT b.ZYH as ZYH,b.JSCS as JSCS,a.ZYGB as ZYGB," +
						" sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE,sum(b.ZLJE) as ZLJE FROM "+
						" GY_SFXM a,ZY_FYMX b WHERE a.SFXM = b.FYXM AND b.ZYH = :ZYH "+ jscs+
						" GROUP BY a.ZYGB,b.ZYH,b.JSCS union select "+ body.get("ZYH")+
						" as ZYH,"+ ijscs+" as JSCS,ZYGB as ZYGB,0 as ZJJE,0 as ZFJE,0 as ZLJE from "+
						" GY_SFXM"+" a where a.ZYSY = 1 and a.sfxm not in (SELECT FYXM from "+
						" ZY_FYMX where ZYH = :ZYH)) GROUP BY ZYH,ZYGB,JSCS order by ZYGB",
						parameters);	
			}
			SchemaUtil.setDictionaryMassageForList(FYZKBD,
					"phis.application.hos.schemas.ZY_JSGL_LIST");
			List<Map<String, Object>> RFYZKBD = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < FYZKBD.size(); i = i + 2) {
				Map<String, Object> FYZK = new HashMap<String, Object>();
				for (Map.Entry<String, Object> m : FYZKBD.get(i).entrySet()) {
					FYZK.put(m.getKey(), m.getValue());
				}
				if (i + 1 < FYZKBD.size()) {
					for (Map.Entry<String, Object> m : FYZKBD.get(i + 1)
							.entrySet()) {
						if (m.getKey().indexOf("text") > 0) {
							FYZK.put("ZYGB2_text", m.getValue());
						} else {
							FYZK.put(m.getKey() + "2", m.getValue());
						}
					}
				}
				RFYZKBD.add(FYZK);
			}
			res.put("body", RFYZKBD);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetSelectionDetailsList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long zyh = Long.parseLong(body.get("ZYH") + "");
		long fyxm = Long.parseLong(body.get("ZYGB") + "");
		String jscs = body.get("JSCS") + "";
		parameters.put("ZYH", zyh);
		parameters.put("FYXM", fyxm);
		try {
			List<Map<String, Object>> FYZKBD = dao.doSqlQuery(
					" SELECT ZYH as ZYH,JSCS as JSCS,ZJJE as ZJJE,ZFJE as ZFJE,to_char(FYRQ,'yyyy-mm-dd') as FYRQ," +
					" FYXM as FYXM,YSGH as YSGH,FYKS as FYKS,ZXKS as ZXKS,FYSL as FYSL,FYDJ as FYDJ,ZFBL as ZFBL," +
					" FYMC as FYMC,FYXH as FYXH FROM ZY_FYMX WHERE ZYH = :ZYH and FYXM = :FYXM and JSCS = "+ jscs,
					parameters);
			SchemaUtil.setDictionaryMassageForList(FYZKBD,
					"phis.application.hos.schemas.ZY_FYMX_MX");
			res.put("body", FYZKBD);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetSelectionFeesDetailsList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long zyh = Long.parseLong(body.get("ZYH") + "");
		long fyxm = Long.parseLong(body.get("ZYGB") + "");
		String jscs = body.get("JSCS") + "";
		String openBy = req.get("openBy") + "";
		parameters.put("ZYH", zyh);
		parameters.put("FYXM", fyxm);
		try {
			if ("sfxm".equals(openBy)) {
				List<Map<String, Object>> FYZKBD = dao.doSqlQuery(
						" SELECT ZYH as ZYH,JSCS as JSCS,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,sum(ZLJE) as ZLJE," +
						" to_char(FYRQ,'yyyy-mm-dd') as FYRQ,FYXM as FYXM,YSGH as YSGH,FYKS as FYKS,ZXKS as ZXKS " +
						" FROM ZY_FYMX WHERE ZYH = :ZYH and FYXM = :FYXM and JSCS = "+ jscs+
						" GROUP BY ZYH,JSCS,to_char(FYRQ,'yyyy-mm-dd'),FYXM,YSGH,FYKS,ZXKS",
						parameters);
				SchemaUtil.setDictionaryMassageForList(FYZKBD,
						"phis.application.hos.schemas.ZY_FYMX_SFXM");
				res.put("body", FYZKBD);
			} else if ("mxxm".equals(openBy)) {
				List<Map<String, Object>> FYZKBD = dao
						.doSqlQuery(
								"SELECT ZYH as ZYH,JSCS as JSCS,sum(FYSL) as FYSL,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,FYXH as FYXH,FYMC as FYMC FROM ZY_FYMX WHERE ZYH = :ZYH and FYXM = :FYXM and JSCS = "
										+ jscs + " GROUP BY ZYH,JSCS,FYXH,FYMC",
								parameters);
				SchemaUtil.setDictionaryMassageForList(FYZKBD,
						"phis.application.hos.schemas.ZY_FYMX_MXXM");
				res.put("body", FYZKBD);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}

	/**
	 * 获取发票号码
	 *
	 * @param res
	 * @param ctx
	 */
	public void doGetSelectionFPHM(Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String fphm = BSPHISUtil.Getbillnumber("发票", dao, ctx);
		if (!"false".equals(fphm)) {
			res.put("FPHM", fphm);
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			Map<String, Object> body = new HashMap<String, Object>();
			Map<String, Object> FKFS = dao.doLoad(
					"SELECT FKFS as FKFS,FKJD as FKJD FROM " + "GY_FKFS"
							+ " WHERE SYLX = 2 AND MRBZ = 1", parameters);
			if (FKFS != null) {
				body.putAll(FKFS);
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "默认付款方式未设置!");
			}
			Map<String, Object> HBWC = dao.doLoad(
					"SELECT FKFS as WCFS,FKJD as WCJD FROM " + "GY_FKFS"
							+ " WHERE SYLX = 2 AND HBWC = 1", parameters);
			if (HBWC != null) {
				body.putAll(HBWC);
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "默认货币误差未设置!");
			}
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "付款方式获取失败");
		}
	}
	public void saveNjjbjsxx(String op, Map<String, Object> jsxx, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doSave(op, "phis.application.njjb.schemas.NJJB_JSXX", jsxx, false);
		} catch (ValidateException e) {
			logger.error("南京金保住院结算保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "南京金保住院保存失败!");
		} catch (PersistentDataOperationException e) {
			logger.error("南京金保住院保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "南京金保住院保存失败!");
		}
	}
	/**
	 * 住院结算
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSettleAccounts(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String userId = (String) user.getUserId();
		Map<String, Object> map_jsxx = (Map<String, Object>) req.get("body");
		Map<String, Object> body = (Map<String, Object>) map_jsxx.get("JSXX");
		Map<String, Object> parameters = new HashMap<String, Object>();
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		Integer JSCS= Integer.parseInt(body.get("JSCS")+"");
		Date date = new Date();
		Calendar d = Calendar.getInstance();
		d.setTime(date);
		parameters.put("ZYH", ZYH);
		String jslx=body.get("JSLX") + "";
		// 加个锁
		String fphm = BSPHISUtil.Getbillnumber("发票", dao, ctx);
		String bxid="";//农合报销id
		String drgs="";
		//zhaojian 2019-06-18 增加结算性质：1全自费 2医保
		int JSXZ = (body.get("JSXZ") + "").equals("全自费")? 1000:2000;
		
		/*******************add by lizhi 判断医保总费用和系统费用是否一致*********************/
		if(body.containsKey("FILEDATA")){
			Map<String, Object> ybData = (Map<String, Object>) body.get("FILEDATA");
			Double zfy = Double.parseDouble(ybData.get("ZFY")+"");//医保总金额
			Double zhzf = Double.parseDouble(ybData.get("ZHZF")+"");//医保账户支付
//				Double ybzf = Double.parseDouble(ybData.get("YBZF")+"");//医保医保支付
//				Double mzbz = Double.parseDouble(ybData.get("MZBZ")+"");//医保民政补助
			Double xjzf = Double.parseDouble(ybData.get("XJZF")+"");//医保现金支付
			Double tczf = Double.parseDouble(ybData.get("TCZF")+"");//医保统筹支付
			
			Double zjje = Double.parseDouble(body.get("FYHJ")+"");//住院总金额
			Double zhje = Double.parseDouble(body.get("ZHZF")+"");//住院账户金额
			Double TCJE = Double.parseDouble(body.get("TCJE")+"");//住院统筹金额
			Double ZFHJ = Double.parseDouble(body.get("ZFHJ")+"");//住院现金金额
			
			Double ybzje = zhzf+tczf+xjzf;//个人账户支付+统筹支付+现金支付
			Double zyZfy = zhje+TCJE+ZFHJ;//账户金额+统筹金额+现金金额
			if(Math.abs(zfy-zjje)>=0.05 || Math.abs(ybzje-zyZfy)>=0.05){//误差不能超过0.05
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "医保总费用和系统总费用不一致，门诊结算失败！");
			}
		 }
		 /*******************add by lizhi 判断医保总费用和系统总费用是否一致*********************/
		try {
			//医保病人：当前若为全自费结算时，需判定是否存在未结算的医保单据
			Boolean existsZfdj = false;//存在自费单据
			Boolean existsYbdj = false;//存在医保单据
			Boolean ybbr_zfdj_wjs = false;//医保病人自费单据未结算
			if(JSXZ==1000){
				existsZfdj = true;
				long count = dao.doSqlCount("ZY_FYMX a",
						"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and exists (select yyzbm from yk_cdxx " +
						"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is not null" +
						" union " +
						"select yyzbm from gy_ylmx " +
						"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is not null)",
						null);
				if (count > 0) {
					existsYbdj = true;
				}
			}
			else if(JSXZ==2000){
				existsYbdj = true;
				long count = dao.doSqlCount("ZY_FYMX a",
						"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and exists (select yyzbm from yk_cdxx " +
						"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is null" +
						" union " +
						"select yyzbm from gy_ylmx " +
						"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is null)",
						null);
				if (count > 0) {
					existsZfdj = true;
				}
			}
			if(Integer.parseInt(body.get("BRXZ")+"")==2000){
				long count = dao.doSqlCount("ZY_FYMX a",
						"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and JSCS=0 and exists (select yyzbm from yk_cdxx " +
						"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is null" +
						" union " +
						"select yyzbm from gy_ylmx " +
						"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is null)",
						null);
				if (count > 0) {
					ybbr_zfdj_wjs = true;
				}
			}
			//首先进行判断农合结算
			if(jslx.equals("1")||jslx.equals("4")||jslx.equals("5")){
				if(map_jsxx.containsKey("JSXX")){
					Map<String, Object> JSXX=(Map<String, Object>)map_jsxx.get("JSXX");
					if(JSXX.containsKey("nhyjsxx")){
						Map<String, Object> nhyjsxx=(Map<String, Object>)JSXX.get("nhyjsxx");
						nhyjsxx.put("ZYH", ZYH);
						nhyjsxx.put("FPHM", fphm);
						nhyjsxx.put("FPRQ", (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime()));
						nhyjsxx.put("JZLX", "2");
						nhyjsxx.put("JSCS", JSCS+1);
						nhyjsxx.put("ZJJE",parseDouble(JSXX.get("FYHJ")));
						nhyjsxx.put("SUM31",parseDouble(JSXX.get("FYHJ")));
						if(jslx.equals("1")){
							nhyjsxx.put("ZTJSBJ", "1");
						}else{
							nhyjsxx.put("ZTJSBJ", "0");
						}
						XnhModel xm=new XnhModel(dao);
						try {
							//农合报销结算
							bxid=xm.FybxJs(nhyjsxx, res,ctx);
							drgs=nhyjsxx.get("DRGS")+"";
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
					} else if(JSXX.containsKey("NJJB")){
						Map<String, Object> map_zyjs = (Map<String, Object>) map_jsxx
								.get("YBJS");
						map_zyjs.put("FPHM", fphm);
						map_zyjs.put("JGID", JGID);
						map_zyjs.put("JSSJ", new Date());
						map_zyjs.put("CZGH", userId);
						saveNjjbjsxx("create", map_zyjs, ctx);
						
					}
				}
			}
			Date ksrq = BSHISUtil.toDate(body.get("KSRQ") + "");
			if (jslx.equals("4")) {
				long count = dao.doCount("ZY_BRRY", "CYPB = 1 AND ZYH = :ZYH",
						parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在结算病人列表!");
				}
				Map<String, Object> ZY_ZYJS = new HashMap<String, Object>();
				ZY_ZYJS.put("ZYH", ZYH);
				ZY_ZYJS.put("JSCS", JSCS+1);
				ZY_ZYJS.put("JSLX", body.get("JSLX"));
				if(JSCS>=1){
					long countjs = dao.doCount("ZY_ZYJS", "ZFPB = 0 AND ZYH = :ZYH",
							parameters);
					if (countjs > 0) {
						Map<String, Object> KSRQ = dao.doLoad(
								"select max(ZZRQ) as ZZRQ from ZY_ZYJS where ZYH = :ZYH and ZFPB=0",
								parameters);
						ksrq = (Date) KSRQ.get("ZZRQ");
					}
				}
				ZY_ZYJS.put("KSRQ", ksrq);
				ZY_ZYJS.put("ZZRQ", date);
				//ZY_ZYJS.put("BRXZ", body.get("BRXZ"));
				ZY_ZYJS.put("BRXZ", JSXZ);
				ZY_ZYJS.put("FYHJ", body.get("FYHJ"));//总费用
				ZY_ZYJS.put("ZFHJ", body.get("ZFHJ"));//自负金额
				ZY_ZYJS.put("CZGH", userId);
				ZY_ZYJS.put("ZFPB", 0);
				ZY_ZYJS.put("FYZJ", body.get("FYHJ"));//费用合计
				ZY_ZYJS.put("ZFZJ", body.get("ZFHJ"));//自负合计
				ZY_ZYJS.put("JSZJ", body.get("JSJE"));//结算金额
				ZY_ZYJS.put("JGID", JGID);
				ZY_ZYJS.put("JKHJ", body.get("JKHJ"));//缴款合计
				ZY_ZYJS.put("JKZJ", body.get("JKHJ"));// 缴款总计
				ZY_ZYJS.put("YSJE", body.get("YSJE"));//应收金额
				ZY_ZYJS.put("JSRQ", date);
				ZY_ZYJS.put("FPHM", "");
				ZY_ZYJS.put("TCJE", body.get("TCJE"));//统筹金额
				ZY_ZYJS.put("ZHZF", body.get("ZHZF"));//账户支付
				if(bxid!=null && bxid.length() >0 ){
					ZY_ZYJS.put("NHBXID", bxid);
				}
				if(drgs!=null && !drgs.equals("null")&& drgs.length() >0 ){
					ZY_ZYJS.put("DRGS", drgs);
				}
				// 将退补缴款ZY_TBKK打上结算次数
				Map<String, Object> ZY_TBKKparameters = new HashMap<String, Object>();
				ZY_TBKKparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
				ZY_TBKKparameters.put("ZYH",ZYH);
				ZY_TBKKparameters.put("JGID", JGID);
				dao.doUpdate(
						"UPDATE ZY_TBKK SET JSCS = :UJSCS Where ZYH = :ZYH  And JGID = :JGID and JSCS = 0 ",
						ZY_TBKKparameters);
				// 将ZY_FYMX打上结算次数
				Map<String, Object> ZY_FYMXparameters = new HashMap<String, Object>();
				ZY_FYMXparameters.put("UJSCS", JSCS + 1);
				ZY_FYMXparameters.put("ZYH",ZYH);
				ZY_FYMXparameters.put("JGID", JGID);
				dao.doUpdate(
						"UPDATE ZY_FYMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
						ZY_FYMXparameters);
				// 将ZY_HCMX打上结算次数
				dao.doUpdate(
						"UPDATE ZY_HCMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
						ZY_FYMXparameters);
				// 写结算费用明细表ZY_FYMX_JS
				Map<String, Object> ZY_FYMX_JSparameters = new HashMap<String, Object>();
				ZY_FYMX_JSparameters.put("UJSCS",JSCS + 1);
				ZY_FYMX_JSparameters.put("ZYH",ZYH);
				dao.doSqlUpdate(
						"INSERT INTO ZY_FYMX_JS SELECT * FROM ZY_FYMX WHERE ZYH = :ZYH And JSCS = :UJSCS",
						ZY_FYMX_JSparameters);
				// 清空病人床位
				Map<String, Object> ZY_CWSZparameters = new HashMap<String, Object>();
				ZY_CWSZparameters.put("JGID", JGID);
				ZY_CWSZparameters.put("ZYH",ZYH);

				Map<String, Object> ZY_BRRYparameters = new HashMap<String, Object>();
				ZY_BRRYparameters.put("ZZRQ", ZY_ZYJS.get("ZZRQ"));
				ZY_BRRYparameters.put("UJSCS", JSCS + 1);
				ZY_BRRYparameters.put("ZYH",ZYH);
				ZY_BRRYparameters.put("JGID", JGID);
				// 根据不同结算类型 清空床位方式不同
				dao.doUpdate(
						"UPDATE ZY_BRRY SET JSRQ = :ZZRQ,JSCS = :UJSCS,CYRQ = :ZZRQ ,CYPB = 9 Where ZYH = :ZYH And JGID = :JGID",
						ZY_BRRYparameters);
				dao.doUpdate(
						"UPDATE  ZY_CWSZ Set ZYH = Null,YEWYH = Null Where ZYH = :ZYH  And JGID = :JGID",
						ZY_CWSZparameters);
				// 准备住院付款信息表单数据
				Map<String, Object> ZY_FKXX = new HashMap<String, Object>();
				ZY_FKXX.put("FKFS", body.get("FKFS"));
				ZY_FKXX.put("FKJE", body.get("YSJE"));
				ZY_FKXX.put("JGID", JGID);
				ZY_FKXX.put("ZYH", ZYH);
				ZY_FKXX.put("JSCS", JSCS + 1);
				dao.doSave("create", BSPHISEntryNames.ZY_ZYJS, ZY_ZYJS, false);
				Session session = (Session) ctx.get(Context.DB_SESSION);
				session.flush();
				// 写结算明细(ZY_JSMX)
				Map<String, Object> ZY_JSMXparameters = new HashMap<String, Object>();
				ZY_JSMXparameters.put("UJSCS", JSCS + 1);
				ZY_JSMXparameters.put("ZYH",ZYH);
				dao.doSqlUpdate(
						"INSERT INTO "
								+ "ZY_JSMX"
								+ " (ZYH,JSCS,KSDM,FYXM,ZJJE,ZFJE,ZLJE,JGID,JSXZ,FPHM) SELECT ZYH,:UJSCS,FYKS,FYXM,sum(ZJJE) ZJJE,sum(ZFJE) ZFJE,sum(ZLJE) ZLJE,JGID,"+JSXZ+" as JSXZ,'"+fphm+"' as FPHM FROM "
								+ "ZY_FYMX"
								+ " WHERE ZYH = :ZYH AND JSCS = :UJSCS GROUP BY ZYH,FYKS,FYXM,JGID Having ( sum(ZJJE) <> 0 Or sum(ZFJE) <> 0 Or sum(ZLJE) <> 0 )",
						ZY_JSMXparameters);
				// Map<String,Object> JLXH =
				dao.doSave("create", BSPHISEntryNames.ZY_FKXX, ZY_FKXX, false);
				if (Double.parseDouble(body.get("WCJE") + "") != 0) {
					Map<String, Object> ZY_HBWC = new HashMap<String, Object>();
					ZY_HBWC.put("FKFS", body.get("WCFS"));
					ZY_HBWC.put("FKJE", body.get("WCJE"));
					ZY_HBWC.put("JGID", JGID);
					ZY_HBWC.put("ZYH", ZYH);
					ZY_HBWC.put("JSCS", JSCS + 1);
					dao.doSave("create", BSPHISEntryNames.ZY_FKXX, ZY_HBWC,
							false);
				}
				return;
			}
			Date zzrq = date;
			if (jslx.equals("5")) {
				if(Integer.parseInt(body.get("BRXZ")+"")==2000 && JSXZ==1000 && existsYbdj){
					long count = dao.doCount("ZY_ZYJS","ZYH=:ZYH and ZFPB=0 and JSXZ=2000", parameters);
					if (count == 0) {
						throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"当前病人医保单据还没有结算,不能进行自费单据结算!");
					}
				}
				long count = dao.doCount("ZY_BRRY", "CYPB = 1 AND ZYH = :ZYH",
						parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在结算病人列表!");
				}
				Map<String, Object> CYRQ = dao.doLoad(
						"select CYRQ as CYRQ from ZY_BRRY where ZYH = :ZYH",
						parameters);
				zzrq = (Date) CYRQ.get("CYRQ");
				if(Integer.parseInt(body.get("JSCS") + "")>=1){
					long countjs = dao.doCount("ZY_ZYJS", "ZFPB = 0 AND ZYH = :ZYH",
							parameters);
					if (countjs > 0) {
						Map<String, Object> KSRQ = dao.doLoad(
								"select max(ZZRQ) as ZZRQ from ZY_ZYJS where ZYH = :ZYH and ZFPB=0",
								parameters);
						ksrq = (Date) KSRQ.get("ZZRQ");
					}
				}
				// CYRQ =
				// dao.doLoad("select CYSJ as CYRQ from "+BSPHISEntryNames.ZY_CYJL+" where JLXH=(select max(JLXH) from "+BSPHISEntryNames.ZY_CYJL+" where ZYH=:ZYH and CZLX=3)",
				// parameters);
			} else if (jslx.equals("1")) {
				long count = dao
						.doCount(
								"ZY_BRRY",
								"CYPB = 0 AND BRXZ NOT IN ( SELECT BRXZ FROM GY_BRXZ WHERE DBPB > 0 )  AND ZYH = :ZYH",
								parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在中途结算病人列表!");
				}
				if(Integer.parseInt(body.get("JSCS") + "")>=1){
					long countjs = dao.doCount("ZY_ZYJS", "ZFPB = 0 AND ZYH = :ZYH",
							parameters);
					if (countjs > 0) {
						Map<String, Object> KSRQ = dao.doLoad(
								"select max(ZZRQ) as ZZRQ from ZY_ZYJS where ZYH = :ZYH and ZFPB=0",
								parameters);
						ksrq = (Date) KSRQ.get("ZZRQ");
					}
				}
			}

			Map<String, Object> ZY_ZYJS = new HashMap<String, Object>();
			ZY_ZYJS.put("ZYH", body.get("ZYH"));
			ZY_ZYJS.put("JSCS", Integer.parseInt(body.get("JSCS") + "") + 1);
			ZY_ZYJS.put("JSLX", body.get("JSLX"));
			ZY_ZYJS.put("KSRQ", ksrq);
			ZY_ZYJS.put("ZZRQ", zzrq);
			//ZY_ZYJS.put("BRXZ", body.get("BRXZ"));
			ZY_ZYJS.put("BRXZ", JSXZ);
			ZY_ZYJS.put("FYHJ", body.get("FYHJ"));
			ZY_ZYJS.put("ZFHJ", body.get("ZFHJ"));
			ZY_ZYJS.put("CZGH", userId);
			ZY_ZYJS.put("ZFPB", 0);
			ZY_ZYJS.put("FYZJ", body.get("FYHJ"));
			ZY_ZYJS.put("ZFZJ", body.get("ZFHJ"));
			ZY_ZYJS.put("JSZJ", body.get("JSJE"));
			ZY_ZYJS.put("JGID", JGID);
			ZY_ZYJS.put("JKHJ", body.get("JKHJ"));
			ZY_ZYJS.put("JKZJ", body.get("JKHJ"));// 缴款总计
			ZY_ZYJS.put("YSJE", body.get("YSJE"));
			ZY_ZYJS.put("JSRQ", date);
			ZY_ZYJS.put("TCJE", body.get("TCJE"));//统筹金额
			ZY_ZYJS.put("ZHZF", body.get("ZHZF"));//账户支付
			ZY_ZYJS.put("FPHM", fphm);
			ZY_ZYJS.put("JSXZ", JSXZ);
			if(bxid!=null && bxid.length() >0 ){
				ZY_ZYJS.put("NHBXID", bxid);
			}
			if(drgs!=null && !drgs.equals("null") && drgs.length() >0 ){
				ZY_ZYJS.put("DRGS", drgs);
			}
			if (jslx.equals("5")) {// 正常结算

			}
			if (BSPHISUtil.SetBillNumber("发票", fphm, dao, ctx)) {

			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "更新发票号码失败!");
			}
			//除一种情况外（医保病人既有全自费又有医保报销单据并且在最后一次结算全自费的时候不使用预交款）不使用预交款外，其他情况都使用预交款
			if(!(Integer.parseInt(body.get("BRXZ")+"")==2000 && JSXZ==1000 && existsYbdj)){
				// 将ZY_TBKK打上结算次数
				Map<String, Object> ZY_TBKKparameters = new HashMap<String, Object>();
				ZY_TBKKparameters.put("UJSCS", JSCS + 1);
				ZY_TBKKparameters.put("ZYH", ZYH);
				ZY_TBKKparameters.put("JGID", JGID);
				ZY_TBKKparameters.put("JSXZ", JSXZ);
				dao.doUpdate(
						"UPDATE ZY_TBKK SET JSCS = :UJSCS,JSXZ=:JSXZ Where ZYH = :ZYH  And JGID = :JGID and JSCS = 0 ",
						ZY_TBKKparameters);
			}
			// 将ZY_FYMX打上结算次数
			Map<String, Object> ZY_FYMXparameters = new HashMap<String, Object>();
			ZY_FYMXparameters.put("UJSCS", JSCS + 1);
			ZY_FYMXparameters.put("ZYH", ZYH);
			ZY_FYMXparameters.put("JGID", JGID);
			ZY_FYMXparameters.put("JSXZ", JSXZ);

			String whereSql = "";
			//医保病人
			if(Integer.parseInt(body.get("BRXZ")+"")==2000 && existsYbdj && existsZfdj){
				if(JSXZ==2000){//医保结算
					whereSql = "yyzbm is not null";
				}else if(JSXZ==1000){//全自费结算
					whereSql = "yyzbm is null";
				}
				whereSql = " and exists (select yyzbm from yk_cdxx " +
				"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and " + whereSql +
				" union " +
				"select yyzbm from gy_ylmx " +
				"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and "+whereSql+")";
			}
			dao.doSqlUpdate(
					"UPDATE ZY_FYMX a SET a.JSCS = :UJSCS,a.JSXZ=:JSXZ WHERE a.ZYH = :ZYH AND a.JSCS = 0 And a.JGID = :JGID"+whereSql,
					ZY_FYMXparameters);
			//医保自费分开结算，当进行最后一次结算时才更新换床明细信息
			if(!(Integer.parseInt(body.get("BRXZ")+"")==2000 && JSXZ==2000 && ybbr_zfdj_wjs)){
				ZY_FYMXparameters.remove("JSXZ");
				// 将ZY_HCMX打上结算次数
				dao.doUpdate(
						"UPDATE ZY_HCMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
						ZY_FYMXparameters);
			}
			// 写结算费用明细表ZY_FYMX_JS
			Map<String, Object> ZY_FYMX_JSparameters = new HashMap<String, Object>();
			ZY_FYMX_JSparameters.put("UJSCS", JSCS + 1);
			ZY_FYMX_JSparameters.put("ZYH",ZYH);
/*			dao.doSqlUpdate(
					"INSERT INTO ZY_FYMX_JS SELECT a.*,"+JSXZ+" as JSXZ,'"+fphm+"' as FPHM FROM ZY_FYMX a WHERE a.ZYH = :ZYH And a.JSCS = :UJSCS"+whereSql,
					ZY_FYMX_JSparameters);*/
			dao.doSqlUpdate(
					"INSERT INTO ZY_FYMX_JS SELECT a.JLXH,a.JGID,a.ZYH,a.FYRQ,a.FYXH,a.FYMC,a.YPCD,a.FYSL,a.FYDJ,a.ZJJE,a.ZFJE,a.YSGH,a.SRGH,a.QRGH,a.FYBQ," +
					"a.FYKS,a.ZXKS,a.JFRQ,a.XMLX,a.YPLX,a.FYXM,a.JSCS,a.ZFBL,a.YZXH,a.HZRQ,a.YJRQ,a.ZLJE,a.ZLXZ,a.YEPB,a.DZBL,a.GLJLXH,a.SCBZ,a.NHSCBZ," +
					JSXZ + " as JSXZ,'" + fphm + "' as FPHM FROM ZY_FYMX a WHERE a.ZYH = :ZYH And a.JSCS = :UJSCS"+ whereSql,
					ZY_FYMX_JSparameters);
			// 清空病人床位
			Map<String, Object> ZY_CWSZparameters = new HashMap<String, Object>();
			ZY_CWSZparameters.put("JGID", JGID);
			ZY_CWSZparameters.put("ZYH", ZYH);

			Map<String, Object> ZY_BRRYparameters = new HashMap<String, Object>();
			ZY_BRRYparameters.put("ZZRQ", ZY_ZYJS.get("ZZRQ"));
			ZY_BRRYparameters.put("UJSCS", JSCS + 1);
			ZY_BRRYparameters.put("ZYH", ZYH);
			ZY_BRRYparameters.put("JGID", JGID);
			// 根据不同结算类型 清空床位方式不同
			if (jslx.equals("5")) {// 正常结算
				//医保自费分开结算，最后一次结算才更新住院状态及床位清空
				if(!(Integer.parseInt(body.get("BRXZ")+"")==2000 && JSXZ==2000 && ybbr_zfdj_wjs)){
					dao.doUpdate(
							"UPDATE ZY_BRRY SET JSRQ = :ZZRQ,JSCS = :UJSCS,CYRQ = :ZZRQ ,CYPB = 8 Where ZYH = :ZYH And JGID = :JGID",
							ZY_BRRYparameters);
					dao.doUpdate(
							"UPDATE  ZY_CWSZ Set ZYH = Null,YEWYH = Null Where ZYH = :ZYH  And JGID = :JGID",
							ZY_CWSZparameters);
				}
				// modify by yangl 去除清空BRCH操作
				// dao.doUpdate(
				// "UPDATE "
				// + BSPHISEntryNames.ZY_BRRY
				// + " Set BRCH = Null Where ZYH = :ZYH  And JGID = :JGID",
				// ZY_CWSZparameters);
			} else if (jslx.equals("1")) {// 中途结算
				dao.doUpdate(
						"UPDATE ZY_BRRY SET JSRQ = :ZZRQ,JSCS = :UJSCS,CYRQ = null ,CYPB = 0 Where ZYH = :ZYH And JGID = :JGID",
						ZY_BRRYparameters);
			}

			// 准备住院付款信息表单数据
			Map<String, Object> ZY_FKXX = new HashMap<String, Object>();
			ZY_FKXX.put("FKFS", body.get("FKFS"));
			ZY_FKXX.put("FKJE", body.get("YSJE"));
			ZY_FKXX.put("JGID", JGID);
			ZY_FKXX.put("ZYH", ZYH);
			ZY_FKXX.put("JSCS", JSCS + 1);
			ZY_FKXX.put("JSXZ", JSXZ);
			ZY_FKXX.put("FPHM", fphm);
			ZY_ZYJS.put("FKFS", body.get("FKFS"));
			dao.doSave("create", BSPHISEntryNames.ZY_ZYJS, ZY_ZYJS, false);
			res.put("FPHM", fphm);
			Session session = (Session) ctx.get(Context.DB_SESSION);
			session.flush();
			// 写结算明细(ZY_JSMX)
			Map<String, Object> ZY_JSMXparameters = new HashMap<String, Object>();
			ZY_JSMXparameters.put("UJSCS", JSCS + 1);
			ZY_JSMXparameters.put("ZYH", ZYH);
			dao.doSqlUpdate(
					"INSERT INTO "
							+ "ZY_JSMX"
							+ " (ZYH,JSCS,KSDM,FYXM,ZJJE,ZFJE,ZLJE,JGID,JSXZ,FPHM) SELECT ZYH,:UJSCS,FYKS,FYXM,sum(ZJJE) ZJJE,sum(ZFJE) ZFJE,sum(ZLJE) ZLJE,JGID,"+JSXZ+" as JSXZ,'"+fphm+"' as FPHM FROM "
							+ "ZY_FYMX a"
							+ " WHERE ZYH = :ZYH AND JSCS = :UJSCS "+whereSql+" GROUP BY ZYH,FYKS,FYXM,JGID Having ( sum(ZJJE) <> 0 Or sum(ZFJE) <> 0 Or sum(ZLJE) <> 0 )",
					ZY_JSMXparameters);
			// Map<String,Object> JLXH =
			dao.doSave("create", BSPHISEntryNames.ZY_FKXX, ZY_FKXX, false);
			if (Double.parseDouble(body.get("WCJE") + "") != 0) {
				Map<String, Object> ZY_HBWC = new HashMap<String, Object>();
				ZY_HBWC.put("FKFS", body.get("WCFS"));
				ZY_HBWC.put("FKJE", body.get("WCJE"));
				ZY_HBWC.put("JGID", JGID);
				ZY_HBWC.put("ZYH", ZYH);
				ZY_HBWC.put("JSCS", JSCS + 1);
				ZY_HBWC.put("JSXZ", JSXZ);
				ZY_HBWC.put("FPHM", fphm);
				dao.doSave("create", BSPHISEntryNames.ZY_FKXX, ZY_HBWC, false);
			}
			if (jslx.equals("5")) {
				// try{
				// if(map_jsxx.containsKey("YBJS")){
				// Map<String,Object>
				// map_zyjs=(Map<String,Object>)map_jsxx.get("YBJS");
				// map_zyjs.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				// map_zyjs.put("JSRQ", new Date());
				// map_zyjs.put("CZGH", userId);
				// map_zyjs.put("FPHM", body.get("FPHM"));
				// map_zyjs.put("JGID", JGID);
				// map_zyjs.put("BRXZ", Integer.parseInt(body.get("BRXZ")+""));
				// dao.doSave("create", BSPHISEntryNames.YB_ZYJS, map_zyjs,
				// false);
				// }else if(map_jsxx.containsKey("SZYB")){//省医保
				//
				// MedicareSYBModel mm = new MedicareSYBModel(dao);
				// Map<String, Object> jyqr = (Map<String, Object>) map_jsxx
				// .get("jyqr");
				// mm.doSaveSzYbjyqr("update", jyqr, res, ctx);
				// Map<String,Object>
				// map_zyjs=(Map<String,Object>)map_jsxx.get("SZYBJS");
				// // map_zyjs.put("MZXH", mzxh.get("MZXH"));
				// map_zyjs.put("FPHM", ZY_ZYJS.get("FPHM"));
				// map_zyjs.put("JKFS", ZY_FKXX.get("FKFS"));
				// map_zyjs.put("JGID", JGID);
				// map_zyjs.put("JSRQ", new Date());
				// map_zyjs.put("CZGH", userId);
				// mm.saveSzYbzyjsxx("create",map_zyjs, ctx);
				// }
				// }catch(Exception e){
				// throw new ModelDataOperationException(
				// ServiceCode.CODE_DATABASE_ERROR,
				// "出院结算失败,医保端结算成功,本地结算失败:"+e.getMessage());
				// }
			}
			
			/**************add by lizhi 2017-11-02以下是医保结算保存************************/
			 if(body.containsKey("FILEDATA")){
				Map<String, Object> ybData = (Map<String, Object>) body.get("FILEDATA");
				Map<String, Object> zyxx = new HashMap<String, Object>();
				zyxx.put("ZYH", body.get("ZYH"));
				zyxx.put("FILEDATA", ybData);
				YBModel yb = new YBModel(dao);
				yb.doSaveZyjs(zyxx, null, null);
			 }
			/**************add by lizhi 2017-11-02医保结算保存结束************************/
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出院结算失败!");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出院结算失败!");
		}
	}

	/**
	 * 取消结算，作废
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateSettleAccounts(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String userId = (String) user.getUserId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		int JSCS = (Integer) body.get("JSCS");
		String BRCH = (String) body.get("BRCH");
		int JSXZ = (body.get("JSXZ") + "").equals("全自费")? 1000:2000;
		String FPHM = (String) body.get("FPHM");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
		try {
/*			if(JSXZ==1000 || JSXZ==2000){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在发票作废病人列表!");
			}*/
			//医保病人：当前若为全自费结算时，需判定是否存在未结算的医保单据
			Boolean ybbr_existsZfdj = false;
			Boolean ybbr_existsYbdj = false;
			Boolean ybbr_zfdj_wjs = false;//医保病人自费单据未结算
			//Boolean ybbr_zfdj_yjs = false;//医保病人自费单据已结算
			if(Integer.parseInt(body.get("BRXZ")+"")==2000){
				if(JSXZ==1000){
					//ybbr_zfdj_yjs = true;
					long count = dao.doCount("ZY_ZYJS","ZYH=:ZYH and ZFPB=0 and JSXZ=2000", parameters);
					if (count > 0) {
						throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"当前病人医保结算发票还没有作废,不能进行全自费发票作废!");
					}
					ybbr_existsZfdj = true;
					count = dao.doSqlCount("ZY_FYMX a",
							"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and exists (select yyzbm from yk_cdxx " +
							"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is not null" +
							" union " +
							"select yyzbm from gy_ylmx " +
							"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is not null)",
							null);
					if (count > 0) {
						ybbr_existsYbdj = true;
					}
				}else{
					ybbr_existsYbdj = true;
					long count = dao.doSqlCount("ZY_FYMX a",
									"ZYH = "+ZYH+"  And JGID = '"+JGID+"' and exists (select yyzbm from yk_cdxx " +
									"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and ypxh = a.fyxh and ypcd = a.ypcd and a.ypcd > 0 and yyzbm is null" +
									" union " +
									"select yyzbm from gy_ylmx " +
									"where "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=jgid":"jgid = a.jgid")+" and fyxh = a.fyxh and a.ypcd = 0 and yyzbm is null)",
									null);
					if (count > 0) {
						ybbr_existsZfdj = true;
						count = dao.doCount("ZY_ZYJS","ZYH=:ZYH and ZFPB=0 and JSXZ=1000", parameters);
						if (count > 0) {
							//ybbr_zfdj_yjs = true;
						}else{
							ybbr_zfdj_wjs = true;
							JSCS ++;
						}
					}
				}
			}
			long count = dao.doCount("ZY_BRRY a,ZY_ZYJS b",
							"a.ZYH = b.ZYH and b.ZFPB = 0 and b.JSLX <> 4 and a.ZYH = :ZYH and JSXZ="+JSXZ,
							parameters);
			if (count == 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在发票作废病人列表!");
			}
			Map<String, Object> parameters2 = new HashMap<String, Object>();
			parameters2.put("BAHM", body.get("BAHM"));
			parameters2.put("JGID", JGID);
			count = dao.doCount("ZY_BRRY","BAHM=:BAHM and JGID=:JGID and cypb<8", parameters2);
			if (count > 0) {
				if ("5".equals(body.get("JSBS") + "") && !((JSXZ==1000 || (JSXZ==2000 && ybbr_zfdj_wjs)) && ybbr_existsYbdj && ybbr_existsZfdj)) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"当前病人已在院,不能进行发票作废!");
				}
			}
			//农合的进行农合退费
			if(body.get("BRXZ").toString().equals("6000")){
				XnhModel xm=new XnhModel(dao);
				try {
					xm.ReSaveFybx(body,res);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			
			// 将结算发票作废
			Map<String, Object> ZY_ZYJSparameters = new HashMap<String, Object>();
			ZY_ZYJSparameters.put("ZFRQ", new Date());
			ZY_ZYJSparameters.put("ZFGH", userId);
			ZY_ZYJSparameters.put("ZYH", ZYH);
			ZY_ZYJSparameters.put("JGID", JGID);
			ZY_ZYJSparameters.put("JSXZ", JSXZ);
			ZY_ZYJSparameters.put("FPHM", FPHM);
			dao.doUpdate("UPDATE ZY_ZYJS SET ZFPB = 1,ZFRQ = :ZFRQ,ZFGH = :ZFGH WHERE ZYH=:ZYH AND JGID = :JGID AND ZFPB = 0 And JSXZ =:JSXZ And FPHM =:FPHM",
					ZY_ZYJSparameters);
			Map<String, Object> ZY_JSZF = new HashMap<String, Object>();
			ZY_JSZF.put("ZYH", ZYH);
			ZY_JSZF.put("JSCS", JSCS);
			ZY_JSZF.put("JGID", JGID);
			ZY_JSZF.put("ZFGH", userId);
			ZY_JSZF.put("ZFRQ", new Date());
			ZY_JSZF.put("JSXZ", JSXZ);
			ZY_JSZF.put("FPHM", FPHM);
			dao.doSave("create", BSPHISEntryNames.ZY_JSZF, ZY_JSZF, false);
			// 清除ZY_FYMX中结算次数
			Map<String, Object> ZY_FYMXparameters = new HashMap<String, Object>();
			ZY_FYMXparameters.put("ZYH", ZYH);
			ZY_FYMXparameters.put("JSXZ", JSXZ);
			ZY_FYMXparameters.put("JGID", JGID);
			ZY_FYMXparameters.put("FPHM", FPHM);
/*			dao.doSqlUpdate("UPDATE ZY_FYMX Set JSCS = 0 Where ZYH = :ZYH AND JSCS = :JSCS and JGID = :JGID",
					ZY_FYMXparameters);*/
			dao.doSqlUpdate("UPDATE ZY_FYMX Set JSCS = 0 Where ZYH = :ZYH and JGID = :JGID AND FYXH in(select FYXH from ZY_FYMX_JS where  ZYH = :ZYH AND JSXZ = :JSXZ and JGID = :JGID and FPHM=:FPHM)",
					ZY_FYMXparameters);
			// 清除ZY_TBKK中结算次数
			Map<String, Object> ZY_TBKKparameters = new HashMap<String, Object>();
			ZY_TBKKparameters.put("ZYH", ZYH);
			ZY_TBKKparameters.put("JSCS", JSCS);
			ZY_TBKKparameters.put("JGID", JGID);
			ZY_TBKKparameters.put("JSXZ", JSXZ);
			long ll_Count = dao.doCount("ZY_TBKK","ZYH = :ZYH AND JSCS = :JSCS  and JGID = :JGID AND JSXZ = :JSXZ",ZY_TBKKparameters);
			//医保病人医保部分结算不撤销退补缴款信息
			if (ll_Count > 0) {
				dao.doUpdate("UPDATE ZY_TBKK Set JSCS = 0,JSXZ=null Where ZYH = :ZYH AND JSCS = :JSCS  and JGID = :JGID AND JSXZ = :JSXZ",
						ZY_TBKKparameters);
			}
			ZY_TBKKparameters.remove("JSXZ");
			ll_Count = dao.doCount("ZY_HCMX","ZYH = :ZYH AND JSCS = :JSCS and JGID = :JGID",ZY_TBKKparameters);
			if (ll_Count > 0) {
				dao.doUpdate("UPDATE ZY_HCMX Set JSCS = 0 Where ZYH = :ZYH AND JSCS =:JSCS and JGID = :JGID",ZY_TBKKparameters);
			}
			Map<String, Object> ZY_BRRYparameters = new HashMap<String, Object>();
			ZY_BRRYparameters.put("ZYH", ZYH);
			ZY_BRRYparameters.put("JGID", JGID);
			if ("5".equals(body.get("JSBS") + "")) {
				//既有全自费结算又有医保结算的医保病人：医保发票作废（全自费未结算）时不更新出院状态
				if(Integer.parseInt(body.get("BRXZ")+"")==2000 && ybbr_existsZfdj && ybbr_existsYbdj && (JSXZ==2000 && ybbr_zfdj_wjs)){
/*					ZY_BRRYparameters.put("JSCS", JSCS-1);
					dao.doUpdate("UPDATE ZY_BRRY SET CYPB = 1,JSRQ = null,JSCS=:JSCS Where ZYH  = :ZYH  and JGID = :JGID",ZY_BRRYparameters);
					ZY_BRRYparameters.remove("JSCS");*/
				}else{
					dao.doUpdate("UPDATE ZY_BRRY SET CYPB = 1,JSRQ = null Where ZYH  = :ZYH  and JGID = :JGID",ZY_BRRYparameters);
				}
			} else if ("1".equals(body.get("JSBS") + "")) {
				dao.doUpdate("UPDATE ZY_BRRY SET CYPB = 0,JSRQ = null Where ZYH  = :ZYH  and JGID = :JGID",ZY_BRRYparameters);
			}
			// 恢复病人床位
			String Ls_brch = "";
			Map<String, Object> ZY_HCMXparameters = new HashMap<String, Object>();
			ZY_HCMXparameters.put("ZYH", ZYH);
			List<Map<String, Object>> ZY_HCMX = dao.doQuery(
							"SELECT HQCH as HQCH,HHCH as HHCH FROM ZY_HCMX WHERE ZYH = :ZYH AND HCRQ = (SELECT MAX(HCRQ) as HCRQ FROM ZY_HCMX WHERE ZYH = :ZYH)",
							ZY_HCMXparameters);
			if (ZY_HCMX != null && ZY_HCMX.size() > 0) {
				if (ZY_HCMX.get(0) != null) {
					if (ZY_HCMX.get(0).get("HHCH") != null) {
						Ls_brch = ZY_HCMX.get(0).get("HHCH") + "";
					} else {
						Ls_brch = ZY_HCMX.get(0).get("HQCH") + "";
					}
				}
			}
			//modify by yangl 中途结算不恢复床位
			//医保病人自费部分结算不恢复床位
			if (Ls_brch.length() > 0 && "5".equals(body.get("JSBS") + "")  && !(Integer.parseInt(body.get("BRXZ")+"")==2000 && ybbr_existsZfdj && ybbr_existsYbdj && (JSXZ==2000 && ybbr_zfdj_wjs))) { 
				Map<String, Object> ZY_CWSZparameters = new HashMap<String, Object>();
				ZY_CWSZparameters.put("BRCH", Ls_brch);
				ZY_CWSZparameters.put("JGID", JGID);
				Map<String, Object> ZY_CWSZ_ZYH = dao.doLoad(
						"SELECT ZYH as ZYH FROM " + "ZY_CWSZ"
								+ " WHERE BRCH = :BRCH  and JGID = :JGID",
						ZY_CWSZparameters);
				long ll_zyh = 0;
				if (ZY_CWSZ_ZYH != null) {
					if (ZY_CWSZ_ZYH.get("ZYH") != null) {
						ll_zyh = Long.parseLong(ZY_CWSZ_ZYH.get("ZYH") + "");
					}
				}
				if (ll_zyh == 0) {
					ZY_BRRYparameters.put("BRCH", Ls_brch);
					dao.doUpdate("UPDATE ZY_CWSZ SET ZYH = :ZYH WHERE BRCH = :BRCH and JGID = :JGID",ZY_BRRYparameters);
					dao.doUpdate("UPDATE ZY_BRRY SET BRCH = :BRCH WHERE ZYH = :ZYH and JGID = :JGID",ZY_BRRYparameters);
					Map<String, Object> ZY_CWSZparameters1 = new HashMap<String, Object>();
					ZY_CWSZparameters1.put("BRCH", BRCH);
					ZY_CWSZparameters1.put("JGID", JGID);
					ZY_CWSZparameters1.put("ZYH", ZYH);
					dao.doUpdate("UPDATE ZY_CWSZ SET ZYH = :ZYH WHERE BRCH = :BRCH and JGID = :JGID",
							ZY_CWSZparameters1);
					Map<String, Object> ZY_CWSZ = dao.doLoad(
							"SELECT CWKS as CWKS,KSDM as KSDM FROM ZY_CWSZ Where BRCH = :BRCH and JGID = :JGID",
							ZY_CWSZparameters);
					if (ZY_CWSZ != null) {
						long KSDM = Long.parseLong(ZY_CWSZ.get("KSDM") + "");
						Map<String, Object> cwtjparameters = new HashMap<String, Object>();
						cwtjparameters.put("KSDM", KSDM);
						cwtjparameters.put("BQPB", 0);
						int li_ysys = BSPHISUtil.cwtj(cwtjparameters, dao, ctx);
						li_ysys = li_ysys - 1;
						int li_xsys = li_ysys + 1;
						Map<String, Object> ZY_CWTJ1 = new HashMap<String, Object>();
						ZY_CWTJ1.put("JGID", JGID);
						ZY_CWTJ1.put("CZRQ", new Date());
						ZY_CWTJ1.put("CZLX", 1);
						ZY_CWTJ1.put("ZYH", ZYH);
						ZY_CWTJ1.put("BRKS", KSDM);
						ZY_CWTJ1.put("YSYS", li_ysys);
						ZY_CWTJ1.put("XSYS", li_xsys);
						ZY_CWTJ1.put("BQPB", 0);
						dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,ZY_CWTJ1, false);
						cwtjparameters.put("BQPB", 1);
						li_ysys = BSPHISUtil.cwtj(cwtjparameters, dao, ctx);
						li_ysys = li_ysys - 1;
						li_xsys = li_ysys + 1;
						Map<String, Object> ZY_CWTJ2 = new HashMap<String, Object>();
						ZY_CWTJ2.put("JGID", JGID);
						ZY_CWTJ2.put("CZRQ", new Date());
						ZY_CWTJ2.put("CZLX", 1);
						ZY_CWTJ2.put("ZYH", ZYH);
						ZY_CWTJ2.put("BRKS", KSDM);
						ZY_CWTJ2.put("YSYS", li_ysys);
						ZY_CWTJ2.put("XSYS", li_xsys);
						ZY_CWTJ2.put("BQPB", 1);
						dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,ZY_CWTJ2, false);
					}
				} else {
					dao.doUpdate(
							"UPDATE ZY_BRRY SET BRCH =''  WHERE ZYH = :ZYH and JGID = :JGID",
							ZY_BRRYparameters);
				}
			}
			//南京金保结算表作废
			if(body.get("BRXZ").toString().equals("2000") && JSXZ==2000){
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("ZYH", ZYH);
				dao.doUpdate("UPDATE NJJB_JSXX SET ZFPB='1'  WHERE ZYH =:ZYH",pa);
				dao.doUpdate("UPDATE ZY_FYMX SET SCBZ='0'  WHERE ZYH =:ZYH",pa);
			}
			
			// if(body.containsKey("jyqr")){
			// Map<String,Object> jyqr = (Map<String,Object>)body.get("jyqr");
			// MedicareSYBModel msm = new MedicareSYBModel(dao);
			// msm.doSaveSzYbjyqr("update", jyqr, res, ctx);
			// }
			// try{
			// if(body.get("YWLSH")!=null&&body.get("YWLSH")!=""&&(Long.parseLong(body.get("YWLSH")+"")>0l)){
			// JXMedicareModel jxmm = new JXMedicareModel(dao);
			// Map<String,Object> ZY_ZYJSPkey = new HashMap<String, Object>();
			// ZY_ZYJSPkey.put("ZYH",ZYH);
			// ZY_ZYJSPkey.put("JSCS",JSCS);
			// StringBuffer hql_zyjs=new StringBuffer();
			// hql_zyjs.append("select JSRQ as JSRQ from ").append(BSPHISEntryNames.ZY_ZYJS).append(" where ZYH=:ZYH and JSCS=:JSCS");
			// StringBuffer hql_fkxx=new StringBuffer();
			// hql_fkxx.append("select min(JLXH) as JLXH from ").append(BSPHISEntryNames.ZY_FKXX).append(" where ZYH=:ZYH and JSCS=:JSCS");
			// Map<String,Object> ZY_ZYJS = dao.doLoad(hql_zyjs.toString(),
			// ZY_ZYJSPkey);
			// Map<String,Object> ZY_FKXX = dao.doLoad(hql_fkxx.toString(),
			// ZY_ZYJSPkey);
			// Map<String,Object> ybmap = new HashMap<String, Object>();
			// ybmap.put("YWLSH", body.get("YWLSH"));
			// ybmap.put("JSRQ", ZY_ZYJS.get("JSRQ"));
			// ybmap.put("DJH", ZY_FKXX.get("JLXH"));
			// jxmm.saveHospitalizationPreSettlementCostsCancel(ybmap,
			// ctx);//取消结算
			// // ybmap.remove("JSRQ");
			// // ybmap.remove("DJH");
			// // jxmm.saveFeesUploadCancel(ybmap,ctx);//上传撤销
			// }}catch(Exception e){
			// throw new ModelDataOperationException(
			// ServiceCode.CODE_DATABASE_ERROR, "医保取消结算失败:"+e.getMessage());
			// }
			// if(body.containsKey("YBXX")){
			// MedicareModel mm = new MedicareModel(dao);
			// mm.saveHospitalCancelSettlement(
			// (Map<String, Object>) body.get("YBXX"), ctx);
			// }
			// if(body.containsKey("SZYB")){
			// Map<String, Object> jyqr = (Map<String, Object>)
			// body.get("jyqr");
			// MedicareSYBModel msm = new MedicareSYBModel(dao);
			// msm.doSaveSzYbjyqr("update", jyqr, res, ctx);
			// Map<String, Object> szybzf = (Map<String, Object>)
			// body.get("szybzf");
			// szybzf.put("ZFPB", 1);
			// szybzf.put("ZFRQ", new Date());
			// // szybzf.put("ZFGH", userId);
			// msm.saveSzYbzyjsxx("update", szybzf, ctx);
			// }
			if (body.containsKey("isYb")) {// 如果是医保,更新医保结算表
				MedicareModel model = new MedicareModel(dao);
				Map<String, Object> map_body = new HashMap<String, Object>();
				map_body.put("zyh", ZYH);
				model.updateYbzyjsxx(map_body);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "结算作废失败!");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "结算作废失败!");
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fphm = request.get("fphm") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> JSXX = new HashMap<String, Object>();
		List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
		Map<String, Object> JKJES = new HashMap<String, Object>();
		Map<String, Object> YBJES = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer(
				" select a.JSXZ as JSXZ,a.ZYH as ZYH ,b.BAHM as BAHM , a.BRXZ as BRXZ,a.JSCS as JSCS," +
				" a.JKHJ as JKHJ,d.XZMC as RYLB,c.PERSONNAME as SYY,b.BRXB as XB," +
				" b.ZYH as ZYH,b.GZDW as GZDW,b.BRCH as BRCH,b.ZYHM as XLH,b.BRXM as XM," +
				" to_char(b.RYRQ, 'yyyy-mm-dd') as RYRQ, to_char(b.CYRQ, 'yyyy-mm-dd') as CYRQ," +
				" to_char(b.RYRQ, 'yyyy') as RYYY,to_char(b.RYRQ, 'mm') as RYMM,to_char(b.RYRQ, 'dd') as RYDD," +
				" to_char(b.CYRQ, 'yyyy') as CYYY,to_char(b.CYRQ, 'mm') as CYMM,to_char(b.CYRQ, 'dd') as CYDD," +
				" a.FYHJ as HJJE,a.ZFHJ as ZFJE,to_char(a.JSRQ, 'yyyy') as YYYY," +
				" to_char(a.JSRQ, 'mm') as MM,to_char(a.JSRQ, 'dd') as DD,e.NHKH as NHKH," +
				" b.YBKH as YBKH,a.BRXZ as BRXZ,a.DRGS as DRGS,e.SHBZKH as SHBZKH " +
				" from ZY_ZYJS a,ZY_BRRY b, SYS_Personnel c, GY_BRXZ d ,MS_BRDA e ");
		hql.append(" where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.ZYH = b.ZYH " +
				   " and a.FPHM = :FPHM and a.JGID = :JGID and b.BRID=e.BRID");
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		StringBuffer hql1 = new StringBuffer(
				"select  nvl(b.MCSX,b.SFMC) as MCSX,sum(a.ZJJE) as ZJJE from ");
		hql1.append("GY_SFXM");
		hql1.append(" b join ZY_FYMX a on a.FYXM = b.SFXM and a.ZYH = :ZYH and a.JSCS = :JSCS and a.JSXZ = :JSXZ group by b.MCSX,b.SFMC");
		
		StringBuffer hql2 = new StringBuffer("select sum(a.JKJE) as JKJE from ");
		hql2.append("ZY_TBKK a where a.ZYH = :ZYH and a.JSCS=:JSCS and a.ZFPB=0 and a.JSXZ = :JSXZ");

		try {
			JSXX=dao.doSqlLoad(hql.toString(), parameters);
			if(JSXX!=null && JSXX.size() >0){
				response.put("ZYH",JSXX.get("ZYH")+"");
				response.put("BAHM",JSXX.get("BAHM")+"");
				response.put("ZYHM",JSXX.get("XLH")+"");
				response.put("RYYY",JSXX.get("RYYY")+"");
				response.put("RYMM",JSXX.get("RYMM")+"");
				response.put("RYDD",JSXX.get("RYDD")+"");
				response.put("CYYY",JSXX.get("CYYY")==null?"":JSXX.get("CYYY")+"");
				response.put("CYMM",JSXX.get("CYMM")==null?"":JSXX.get("CYMM")+"");
				response.put("CYDD",JSXX.get("CYDD")==null?"":JSXX.get("CYDD")+"");
				response.put("XM",JSXX.get("XM")+"");
				response.put("XB",DictionaryController.instance().getDic("phis.dictionary.gender").getText(JSXX.get("XB")+""));
				response.put("JSFS",JSXX.get("RYLB")+"");
				String brxz=JSXX.get("BRXZ")==null?"":JSXX.get("BRXZ")+"";
				response.put("BRXZ",brxz);
				// 年月日
				response.put("N", JSXX.get("YYYY") + "");
				response.put("Y", JSXX.get("MM") + "");
				response.put("R", JSXX.get("DD") + "");
				response.put("SYY", JSXX.get("SYY") + "");// 收银员
				response.put("JGMC", user.getManageUnit().getName());
				String bz="";
				if(JSXX.get("DRGS")!=null){
					try {
						bz=bz+"单病种："+DictionaryController.instance().get("phis.dictionary.drgs").getText(JSXX.get("DRGS")+"");
					} catch (ControllerException e) {
						e.printStackTrace();
					}
				}
				parameters1.put("ZYH", JSXX.get("ZYH"));
				parameters1.put("JSCS", JSXX.get("JSCS"));
				parameters1.put("JSXZ", JSXX.get("JSXZ"));
				if(brxz.equals("6000")){
					response.put("YLZH",JSXX.get("NHKH")+"");
					Map<String, Object> bzmap=dao.doSqlLoad("select a.BZ as BZ from NH_BSOFT_JSJL a " +
							" where a.ZYH=:ZYH and JSCS=:JSCS", parameters1);
					if(bzmap!=null){
						bz=bz+" "+bzmap.get("BZ")+"";
					}
				}
				if(brxz.equals("3000")){
					response.put("YLZH",JSXX.get("YBKH")+"");
				}
				//查询南京金保结算信息
				if("2000".equals(brxz)){
					response.put("YLZH", JSXX.get("SHBZKH")+"");
					List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "ZYH", "s",JSXX.get("ZYH")+"");
					List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "FPHM", "s",fphm);
					List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
					List<Map<String, Object>> jbjsl=dao.doList(cnd, "", BSPHISEntryNames.NJJB_JSXX);
					if(jbjsl!=null && jbjsl.size()==1){
						response.put("njjbjsxx", jbjsl.get(0));
					}else{
						System.out.println("未查询到金保结算记录或者存在多条记录请查看");
					}
				}
				SFXMS = dao.doSqlQuery(hql1.toString(), parameters1);
				//缴款总金额
				List<Map<String, Object>> jkjelist=dao.doSqlQuery(hql2.toString(), parameters1);
				if(jkjelist!=null && jkjelist.size() >0){
					JKJES=jkjelist.get(0);
				}
				double xjje1=0.0,xjje2=0.0,xjje3=0.0;
				for (int i = 0; i < SFXMS.size(); i++) {
					Map<String, Object> SFXM = SFXMS.get(i);
					response.put("XMMC" + (i+1),SFXM.get("MCSX") + "");
					if (SFXM.get("ZJJE") != null&& (SFXM.get("ZJJE") + "").length() > 0) {
						response.put("XMJE" + (i+1),SFXM.get("ZJJE") + "");
						if(i%3==0){
							xjje1=xjje1+Double.parseDouble(SFXM.get("ZJJE") + "");
						}
						if(i%3==1){
							xjje2=xjje2+Double.parseDouble(SFXM.get("ZJJE") + "");
						}
						if(i%3==2){
							xjje3=xjje3+Double.parseDouble(SFXM.get("ZJJE") + "");
						}
					} 
					else {
						response.put("XMJE" + + (i+1),"0.00");
					}
				}
				response.put("XJJE1", xjje1);
				response.put("XJJE2", xjje2);
				response.put("XJJE3", xjje3);
				if (response.containsKey("QTJE")) {
					if (response.containsKey("XMJE" + response.get("QTPL"))
							&& (response.get("XMJE" + response.get("QTPL")) + "").length() > 0) {
						response.put("XMJE" + response.get("QTPL"),
								Double.parseDouble(response.get("XMJE"+ response.get("QTPL"))+ "")
										+ Double.parseDouble(response.get("QTJE") + ""));
					} else {
						response.put("XMJE" + response.get("QTPL"),Double.parseDouble(response.get("QTJE") + ""));
					}
				} else {
					if (response.containsKey("SFXM" + response.get("QTPL"))) {
						response.remove("SFXM" + response.get("QTPL"));
						response.remove("XMJE" + response.get("QTPL"));
					}
				}
				
				double jkje = 0.00;
				if (JKJES.get("JKJE") != null) {
					jkje = Double.parseDouble(JKJES.get("JKJE") + "");
				}
				response.put("ZYYJJ", String.format("%1$.2f", jkje));

				if (JSXX.get("BRCH") != null) {
					response.put("CWH", JSXX.get("BRCH") + "");
				}
				
				int days = 0;
				if (JSXX.get("RYRQ") != null && JSXX.get("CYRQ") != null) {
					days = BSHISUtil.getDifferDays(sdftime.parse((JSXX.get("CYRQ") + "").substring(0,
									10) + " 00:00:00"),
							sdftime.parse((JSXX.get("RYRQ") + "").substring(0,
									10) + " 00:00:00"));
				}
				if(JSXX.get("RYRQ") != null && JSXX.get("CYRQ")==null) {
					days = BSHISUtil.getDifferDays(
							sdftime.parse((sdftime.format(new Date())).substring(0, 10) + " 00:00:00"),
							sdftime.parse((JSXX.get("RYRQ") + "").substring(0, 10) + " 00:00:00"));
				}
				
				response.put("DAYS", days + "");

				double hjje = 0.00;
				if (JSXX.get("HJJE") != null) {
					hjje = Double.parseDouble(JSXX.get("HJJE") + "");
				}
				response.put("DXZJE", numberToRMB(hjje));
				
				response.put("FYHJ", String.format("%1$.2f", hjje));// 费用合计
				double zfje = parseDouble(JSXX.get("ZFJE"));// 自费金额(自理自费)
				response.put("ZFJE", String.format("%1$.2f", zfje));
				response.put("HJDX", numberToRMB(hjje));
				double jkhj = Double.parseDouble(parseDouble(JSXX.get("JKHJ"))+ "");// 预缴款
				response.put("JKHJ", String.format("%1$.2f", jkhj));//缴款金额
				
				if (zfje - jkhj >= 0) {
					response.put("CYBJ", String.format("%1$.2f", (zfje - jkhj)));// 补缴
					response.put("BJXJ", String.format("%1$.2f", (zfje - jkhj)));// 补缴现金
					response.put("CYTK", "0.00");// 出院退款
					response.put("TKXJ", "0.00");// 退款现金
				} else {
					response.put("CYBJ", "0.00");
					response.put("JSMXXJ", "0,00");
					response.put("CYTK",String.format("%1$.2f", -(zfje - jkhj)));// 出院退款
					StringBuffer hqlTKXJ = new StringBuffer(
							"select -sum(a.FKJE) as TKXJ from ZY_FKXX a,ZY_ZYJS b,GY_FKFS c where a.ZYH=b.ZYH and a.JSCS=b.JSCS and a.FKFS=c.FKFS and c.HBWC=0  and a.ZYH = :ZYH and a.JSCS = :JSCS");
					Map<String, Object> parametersTKXJ = new HashMap<String, Object>();
					parametersTKXJ.put("ZYH",JSXX.get("ZYH"));
					parametersTKXJ.put("JSCS",JSXX.get("JSCS"));
					Map<String, Object> TKXJ_map = dao.doSqlLoad(hqlTKXJ.toString(), parametersTKXJ);
					response.put("TKXJ",String.format("%1$.2f", TKXJ_map.get("TKXJ")));// 退款现金
				}
				
				response.put("FPHM", fphm);// 医疗卡号
				response.put("BNZHZF", "0.00");
				response.put("LNZHZF", "0.00");
				response.put("YBZH", "0.00");
				response.put("YBHJ", String.format("%1$.2f", hjje - zfje)); // 医保合计
				response.put("BZ", bz);
			}
			response.put(BSPHISSystemArgument.FPYL, ParameterUtil.getParameter(
					JGID, BSPHISSystemArgument.FPYL, ctx));
			response.put(BSPHISSystemArgument.ZYJSDYJMC, ParameterUtil
					.getParameter(JGID, BSPHISSystemArgument.ZYJSDYJMC, ctx));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static String numberToZH4(String s) {
		String zhnum_0 = "零壹贰叁肆伍陆柒捌玖";
		String[] zhnum1_0 = { "", "拾", "佰", "仟" };
		StringBuilder sb = new StringBuilder();
		if (s.length() != 4)
			return null;
		for (int i = 0; i < 4; i++) {
			char c1 = s.charAt(i);
			if (c1 == '0' && i > 1 && s.charAt(i - 1) == '0')
				continue;
			if (c1 != '0' && i > 1 && s.charAt(i - 1) == '0')
				sb.append('零');
			if (c1 != '0') {
				sb.append(zhnum_0.charAt(c1 - 48));
				sb.append(zhnum1_0[4 - i - 1]);
			}
		}
		return new String(sb);
	}

	public static String numberToZH(long n) {
		String[] zhnum2 = { "", "万", "亿", "万亿", "亿亿" };
		StringBuilder sb = new StringBuilder();
		String strN = "000" + n;
		int strN_L = strN.length() / 4;
		strN = strN.substring(strN.length() - strN_L * 4);
		for (int i = 0; i < strN_L; i++) {
			String s1 = strN.substring(i * 4, i * 4 + 4);
			String s2 = numberToZH4(s1);
			sb.append(s2);
			if (s2.length() != 0)
				sb.append(zhnum2[strN_L - i - 1]);
		}
		String s = new String(sb);
		if (s.length() != 0 && s.startsWith("零"))
			s = s.substring(1);
		return s;
	}

	public static String numberToZH(double d) {
		return numberToZH("" + d);
	}

	/**
	 * Description: 数字转化成整数
	 * 
	 * @param str
	 * @return
	 */
	public static String numberToZH(String str) {
		String zhnum_0 = "零壹贰叁肆伍陆柒捌玖";
		StringBuilder sb = new StringBuilder();
		int dot = str.indexOf(".");
		if (dot < 0)
			dot = str.length();

		String zhengshu = str.substring(0, dot);
		sb.append(numberToZH(Long.parseLong(zhengshu)));
		if (dot != str.length()) {
			sb.append("点");
			String xiaoshu = str.substring(dot + 1);
			for (int i = 0; i < xiaoshu.length(); i++) {
				sb.append(zhnum_0.charAt(Integer.parseInt(xiaoshu.substring(i,
						i + 1))));
			}
		}
		String s = new String(sb);
		if (s.startsWith("零"))
			s = s.substring(1);
		if (s.startsWith("一十"))
			s = s.substring(1);
		while (s.endsWith("零")) {
			s = s.substring(0, s.length() - 1);
		}
		if (s.endsWith("点"))
			s = s.substring(0, s.length() - 1);
		return s;
	}

	public String numberToRMB(double rmb) {
		String strRMB = "" + rmb;
		DecimalFormat nf = new DecimalFormat("#.#");
		nf.setMaximumFractionDigits(2);
		strRMB = nf.format(rmb).toString();
		strRMB = numberToZH(strRMB);
		if (strRMB.indexOf("点") >= 0) {
			strRMB = strRMB + "零";
			strRMB = strRMB.replaceAll("点", "圆");
			String s1 = strRMB.substring(0, strRMB.indexOf("圆") + 1);
			String s2 = strRMB.substring(strRMB.indexOf("圆") + 1);
			strRMB = s1 + s2.charAt(0) + "角" + s2.charAt(1) + "分整";
		} else {
			strRMB = strRMB + "圆整";
		}
		return strRMB;
	}

	/**
	 * 空转换double
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0.00);
		}
		return Double.parseDouble(o + "");
	}
	public void dochangezfbl(Map<String, Object> req,Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> data=(Map<String, Object>)req.get("data");
		Map<String, Object> pa=new HashMap<String, Object>();
		pa.put("zyh",Long.parseLong(data.get("ZYH")+""));
		try {
			dao.doSqlUpdate("update zy_fymx set (zfbl,zfje)=(select nvl(b.zfbl,1),zy_fymx.fysl*zy_fymx.fydj*nvl(b.zfbl,1) from NH_BSOFT_SPML b"+
					" where substr(zy_fymx.jgid,0,9)=b.jgid and zy_fymx.fyxh*10000+zy_fymx.ypcd=b.yybm and b.xmlx='1')"+
					" where zy_fymx.zyh=:zyh and zy_fymx.ypcd >0 and exists (select 1 from  NH_BSOFT_SPML b"+
					" where substr(zy_fymx.jgid,0,9)=b.jgid and zy_fymx.fyxh*10000+zy_fymx.ypcd=b.yybm and b.xmlx='1')", pa);
			dao.doSqlUpdate("update zy_fymx  set (zfbl,zfje)=(select nvl(b.zfbl,1),zy_fymx.fysl*zy_fymx.fydj*nvl(b.zfbl,1) from NH_BSOFT_SPML b"+ 
					" where substr(zy_fymx.jgid,0,9)=b.jgid and zy_fymx.fyxh=b.yybm and b.xmlx='2')"+
					" where zy_fymx.zyh=:zyh and zy_fymx.ypcd =0 and exists (select 1 from  NH_BSOFT_SPML b"+
					" where substr(zy_fymx.jgid,0,9)=b.jgid and zy_fymx.fyxh=b.yybm and b.xmlx='2')", pa);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
