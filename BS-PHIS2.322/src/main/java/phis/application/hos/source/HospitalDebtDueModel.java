package phis.application.hos.source;

//import java.math.BigDecimal;
//import java.util.Calendar;
//import java.util.Date;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class HospitalDebtDueModel extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalDebtDueModel.class);

	public Session session;

	public HospitalDebtDueModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doSimpleQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		
		UserRoleToken user = UserRoleToken.getCurrent();
		session = (Session) ctx.get(Context.DB_SESSION);
		String JGID = user.getManageUnit().getId();
		String CKWH = ParameterUtil.getParameter(JGID, BSPHISSystemArgument.CKWH, ctx);
		StringBuffer sql = new StringBuffer();
//		StringBuffer sql_JKJE = new StringBuffer();

		// 获取表单参数
		int ksType = (Integer) req.get("ksType");// 按科室催款 还是按病区催款
		String dicValues = req.get("dicValue").toString();// 获得选中的科室代码

		if (dicValues.length() != 0) {
			String ksorbq = "";
//			String dicValue = "";
//			if (dicValues.size() == 1) {
//				dicValue = (String) (dicValues.get(0));
				if (ksType == 1) {
					ksorbq = " and a.BRKS =";
				} else if (ksType == 2) {
					ksorbq = " and a.BRBQ =";
				}
//			}
			if("1".equals(CKWH)){//按科室或病区
				sql.append("select CKBL as CKBL,CKJE as CKJE,DJJE as DJJE,ZDXE as ZDXE,ZYH as ZYH,ZYHM as ZYHM,BRXZ as BRXZ,")
						.append("BRXM as BRXM,BRXB as BRXB,CSNY as CSNY,RYRQ as RYRQ,XTRQ AS XTRQ,BRCH as BRCH,BRKS as BRKS,")
						.append("BRBQ as BRBQ,JGID as JGID,JKJE as JKJE,ZFJE as ZFJE,ZJJE as ZJJE,ZFJE-JKJE as QFJE from ")
						.append("(select CKBL,CKJE,DJJE,ZDXE,ZYH,ZYHM,BRXZ,BRXM,BRXB,CSNY,RYRQ,XTRQ,BRCH,BRKS,")
						.append("BRBQ,JGID,sum(JKJE) as JKJE,sum(ZFJE) as ZFJE,sum(ZJJE) as ZJJE from ")
						.append("(SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
						.append("a.BRBQ,a.JGID,sum(b.JKJE) as JKJE,0 as ZFJE,0 as ZJJE FROM ZY_TBKK b,ZY_BRRY a,ZY_CKWH c ")
						.append("WHERE b.ZYH = a.ZYH AND b.JSCS = 0 AND b.ZFPB = 0 AND a.CYPB = 0 and b.JGID = '")
						.append(JGID).append("' ").append(ksorbq).append("'").append(dicValues).append("'")
						.append(" and a.BRKS = c.KSDM and a.JGID=c.JGID GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,")
						.append("a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID,c.CKBL,c.CKJE,c.DJJE,c.ZDXE")
						.append(" union all ")
						.append("SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
						.append("a.BRBQ,a.JGID,0 as JKJE,sum(b.ZFJE) as ZFJE,sum(b.ZJJE) as ZJJE FROM ZY_BRRY a,ZY_FYMX b,ZY_CKWH c")
						.append(" WHERE a.ZYH = b.ZYH AND a.CYPB = 0 AND b.JSCS = 0 and a.JGID = '")
						.append(JGID).append("' ").append(ksorbq).append("'").append(dicValues).append("'")
						.append(" and a.BRKS = c.KSDM and a.JGID=c.JGID GROUP BY c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,")
						.append("a.BRBQ,a.JGID) group by CKBL,CKJE,DJJE,ZDXE,ZYH,ZYHM,BRXZ,BRXM,BRXB,CSNY,RYRQ,XTRQ,BRCH,BRKS,BRBQ,JGID)")
						.append(" where (JKJE-(ZFJE*CKBL))<ZDXE");
			}else{
				sql.append("select CKBL as CKBL,CKJE as CKJE,DJJE as DJJE,ZDXE as ZDXE,ZYH as ZYH,ZYHM as ZYHM,BRXZ as BRXZ,")
				.append("BRXM as BRXM,BRXB as BRXB,CSNY as CSNY,RYRQ as RYRQ,XTRQ AS XTRQ,BRCH as BRCH,BRKS as BRKS,")
				.append("BRBQ as BRBQ,JGID as JGID,JKJE as JKJE,ZFJE as ZFJE,ZJJE as ZJJE,ZFJE-JKJE as QFJE from ")
				.append("(select CKBL,CKJE,DJJE,ZDXE,ZYH,ZYHM,BRXZ,BRXM,BRXB,CSNY,RYRQ,XTRQ,BRCH,BRKS,")
				.append("BRBQ,JGID,sum(JKJE) as JKJE,sum(ZFJE) as ZFJE,sum(ZJJE) as ZJJE from ")
				.append("(SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
				.append("a.BRBQ,a.JGID,sum(b.JKJE) as JKJE,0 as ZFJE,0 as ZJJE FROM ZY_TBKK b,ZY_BRRY a,ZY_CKWH c ")
				.append("WHERE b.ZYH = a.ZYH AND b.JSCS = 0 AND b.ZFPB = 0 AND a.CYPB = 0 and b.JGID = '")
				.append(JGID).append("' ").append(ksorbq).append("'").append(dicValues).append("'")
				.append(" and a.BRXZ=c.BRXZ and a.JGID=c.JGID GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,")
				.append("a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID,c.CKBL,c.CKJE,c.DJJE,c.ZDXE")
				.append(" union all ")
				.append("SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
				.append("a.BRBQ,a.JGID,0 as JKJE,sum(b.ZFJE) as ZFJE,sum(b.ZJJE) as ZJJE FROM ZY_BRRY a,ZY_FYMX b,ZY_CKWH c")
				.append(" WHERE a.ZYH = b.ZYH AND a.CYPB = 0 AND b.JSCS = 0 and a.JGID = '")
				.append(JGID).append("' ").append(ksorbq).append("'").append(dicValues).append("'")
				.append(" and a.BRXZ=c.BRXZ and a.JGID=c.JGID GROUP BY c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,")
				.append("a.BRBQ,a.JGID) group by CKBL,CKJE,DJJE,ZDXE,ZYH,ZYHM,BRXZ,BRXM,BRXB,CSNY,RYRQ,XTRQ,BRCH,BRKS,BRBQ,JGID)")
				.append(" where (JKJE-(ZFJE*CKBL))<ZDXE");
			}
			// 缴款金额JKJE
//			sql_JKJE.append("SELECT b.ZYH as ZYH,sum(b.JKJE) AS JKJE FROM ZY_TBKK b,ZY_BRRY a WHERE ( b.ZYH  "
//					+ "= a.ZYH ) AND ( b.JSCS = 0 ) AND ( b.ZFPB = 0 ) AND ( a.CYPB = 0 ) "
//					+ "and b.JGID = '"
//					+ JGID
//					+ "' "
//					+ dicValue
//					+ " GROUP BY b.ZYH");

//			sql.append("SELECT a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXZ as BRXZ,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.RYRQ as RYRQ,a.CYRQ AS XTRQ,a.BRCH as BRCH,a.BRKS as BRKS,a.BRBQ as BRBQ,sum(b.ZFJE) AS ZFJE,sum(b.ZJJE) AS ZJJE,0.00 AS JKJE,0.00 AS QFJE,0.00 AS CKJE,0.00 AS CJJE,'      ' AS QKYK,'' AS CSNY_1,1    AS CKBZ,a.JGID as JGID FROM ZY_BRRY a,ZY_FYMX b WHERE ( a.ZYH  = b.ZYH ) AND ( a.CYPB = 0 ) AND ( b.JSCS = 0 ) "
//					+ dicValue
//					+ " GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID");

			// parameters.put("JGID", JGID);
			String schema = (String) req.get("schema");

			try {
				List<Map<String, Object>> body = dao.doSqlQuery(sql.toString(),
						null);
				body = SchemaUtil.setDictionaryMassageForList(body, schema);

//				List<Map<String, Object>> body_JKJE = dao.doSqlQuery(
//						sql_JKJE.toString(), null);
//				body_JKJE = SchemaUtil.setDictionaryMassageForList(body_JKJE,
//						schema);
//
				for (int j = 0; j < body.size(); j++) {
					Map<String, Object> qfMap = body.get(j);

					Date csny = (Date) qfMap.get("CSNY");// 出生日期
					Date RYRQ = (Date) qfMap.get("RYRQ");// 入院日期
					Date nowDate = new Date();
					
					// 出生日期
					Calendar cs = Calendar.getInstance();
					cs.setTime(csny);
					int borthYear = cs.get(Calendar.YEAR);
					int borthMouth = cs.get(Calendar.MONTH) + 1;
					int borthDay = cs.get(Calendar.DATE);
					// 当前时间
					Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
					c.setTime(nowDate);
					int nowYear = c.get(Calendar.YEAR);
					int nowMouth = c.get(Calendar.MONTH) + 1;
					int nowDay = c.get(Calendar.DATE);
					
					long days = (nowDate.getTime() - RYRQ.getTime())
					/ (1000 * 60 * 60 * 24);
					
					// 计算年龄
					int age = nowYear - borthYear - 1;
					if (borthMouth < nowMouth || borthMouth == nowMouth
							&& borthDay <= nowDay) {
						age++;
					}
					
					if(age < 0){
						age = 0;
					}
					
//					BigDecimal ZFJE = (BigDecimal) qfMap.get("ZFJE");
//					qfMap.put("ZFJE",ZFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//
//					qfMap.put("QFJE",ZFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

					qfMap.put("CSNY", age);
					qfMap.put("RYRQ", days+1);
				}
//
//				// 根据查询到的缴款金额List匹配 欠费清单中的记录并计算出欠费清单
//				for (int i = 0; i < body_JKJE.size(); i++) {
//					Map<String, Object> jkMap = body_JKJE.get(i);
//					for (int j = 0; j < body.size(); j++) {
//						Map<String, Object> qfMap = body.get(j);
//
//						BigDecimal zyh = (BigDecimal) jkMap.get("ZYH");
//						BigDecimal JKJE = (BigDecimal) jkMap.get("JKJE");
//						BigDecimal zyh2 = (BigDecimal) qfMap.get("ZYH");
//						double ZFJE = (Double) qfMap.get("ZFJE");
//						if (zyh.longValue() == zyh2.longValue()) {
//							if(JKJE.doubleValue() - ZFJE > 0){
//								body.remove(j);
//							}else{
//								qfMap.put("JKJE", JKJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//								
//								//四舍五入   保留两位小数
//								BigDecimal   b   =   new   BigDecimal(ZFJE - JKJE.doubleValue());  
//								ZFJE   =   b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
//								qfMap.put("QFJE",ZFJE);
//							}
//						}
//					}
//				}
//				
//				for(int i = 0 ; i < body.size() ; i++){
//					Map<String,Object> o = body.get(i);
//					if((Double)o.get("ZFJE") <= 0 ){
////						iter.remove();
//						body.remove(o);
//						i--;
//					}else if((Double)o.get("QFJE") == 0 ){//欠费金额为0时，不显示在欠费清单列表
////						iter.remove();
//						body.remove(o);
//						i--;
//					}
//				}
//				//将自负金额为负的记录筛选掉
////				Iterator<Map<String,Object>> iter = body.iterator();
////				while(iter.hasNext()){
////					Map<String,Object> o = iter.next();
////					if((Double)o.get("ZFJE") <= 0 ){
//////						iter.remove();
////						body.remove(o);
////						continue;
////					}
////					if((Double)o.get("QFJE") == 0 ){//欠费金额为0时，不显示在欠费清单列表
//////						iter.remove();
////						body.remove(o);
////					}
////				}
				res.put("body", body);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		} else {
			res.put("body", null);
		}

	}
	
	/**
	 * 按性质催款查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	protected List<Map<String,Object>> doQueryNatureDunningConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		String sql = "SELECT OP_STATUS as OP_STATUS,0 as KSDM,BRXZ as BRXZ,CKBL as CKBL,CKJE as CKJE,DJJE as DJJE,ZDXE as ZDXE FROM (SELECT 'UPDATE' as OP_STATUS,BRXZ,CKBL,CKJE,DJJE,ZDXE FROM ZY_CKWH WHERE JGID=:JGID and BRXZ<>0 UNION ALL SELECT 'CREATE' as OP_STATUS,BRXZ,0,0,0,0 FROM GY_BRXZ WHERE ZYSY=1 AND BRXZ NOT IN (SELECT SJXZ FROM GY_BRXZ) AND  BRXZ NOT IN (SELECT BRXZ FROM ZY_CKWH WHERE JGID=:JGID and BRXZ<>0 )) ORDER BY BRXZ";
		try {
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", jgid);
			List<Map<String,Object>> list = dao.doSqlQuery(sql, parameters);
			SchemaUtil.setDictionaryMassageForList(list, "phis.application.hos.schemas.ZY_CKWH_XZ");
			return list;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取性质列表失败!");
		}
	}
	
	/**
	 * 按科室催款查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	protected List<Map<String,Object>> doQueryDepartmentDunningConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		String ref = user.getManageUnit().getRef();
		String sql = "SELECT OP_STATUS as OP_STATUS,KSDM as KSDM,0 as BRXZ,CKBL as CKBL,CKJE as CKJE,DJJE as DJJE,ZDXE as ZDXE FROM (SELECT 'UPDATE' as OP_STATUS,KSDM,CKBL,CKJE,DJJE,ZDXE FROM ZY_CKWH WHERE JGID=:JGID and KSDM<>0 UNION ALL SELECT 'CREATE' as OP_STATUS,ID as KSDM,0,0,0,0 FROM SYS_Office WHERE ORGANIZCODE=:REF AND HOSPITALDEPT=1 AND LOGOFF=0 AND ID NOT IN (SELECT PARENTID from SYS_Office where ORGANIZCODE != PARENTID and LOGOFF=0 and ORGANIZCODE=:REF) AND  ID NOT IN (SELECT KSDM FROM ZY_CKWH WHERE JGID=:JGID and KSDM<>0)) ORDER BY KSDM";
		try {
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("REF", ref);
			parameters.put("JGID", jgid);
			List<Map<String,Object>> list = dao.doSqlQuery(sql, parameters);
			SchemaUtil.setDictionaryMassageForList(list, "phis.application.hos.schemas.ZY_CKWH_KS");
			return list;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取科室列表失败!");
		}
	}
	
	/**
	 * 保存催款维护
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDunningConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		List<Map<String,Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			for(int i = 0 ; i < body.size() ; i ++){
				Map<String,Object> data = body.get(i);
				if("UPDATE".equals(data.get("OP_STATUS").toString())){
					data.remove("OP_STATUS");
					data.put("JGID", jgid);
					dao.doSave("update", "phis.application.hos.schemas.ZY_CKWH", data, false);
				}else if("CREATE".equals(data.get("OP_STATUS").toString())){
					data.remove("OP_STATUS");
					data.put("JGID", jgid);
					dao.doSave("create", "phis.application.hos.schemas.ZY_CKWH", data, false);
				}
			}
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "催款维护保存失败!");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "催款维护保存失败!");
		}
	}

}