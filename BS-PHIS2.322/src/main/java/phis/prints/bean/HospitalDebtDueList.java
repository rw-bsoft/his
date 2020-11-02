package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalDebtDueList implements IHandler {

	public List<Map<String,Object>> list;
	
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String zyhStr = (String)request.get("zyhStr");//获得用户选择是的 zyh 集合
		int l = 0;
		String[] zyhs = null;
		if(zyhStr.length() != 0){//用于控制 全部选‘否’的情况
			zyhs = zyhStr.split(",");
			l = zyhs.length;
		}
		for(int i=0;i < this.list.size();i++){
			Map<String,Object> map = list.get(i);
			for(int j=0;j < l;j++){
				String s = zyhs[j];
				if(Long.parseLong(s) == ((BigDecimal)map.get("ZYH")).longValue()){
					map.put("ck", "√");
					break;
				}
			}
			records.add(map);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {

		String bqks = (String) request.get("ksType");
		String text = "";
		if (request.get("text") != null) {
			try {
				text= new String(request.get("text").toString().getBytes ("iso8859_1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if(bqks.equals("1")){
			bqks = "科室：";
			text = DictionaryController.instance().getDic("phis.dictionary.hospitalOffice").getText(request.get("dicValue").toString());
		}else if(bqks.equals("2")){
			bqks = "病区：";
			text = DictionaryController.instance().getDic("phis.dictionary.lesionOffice").getText(request.get("dicValue").toString());
		}
		
		List<Map<String,Object>> recordlist = getList(request,response,ctx);
		this.list = recordlist;
		
		int TotalCount = recordlist.size();//总人数
		double zfTotal = 0;//自负总金额
		double jkTotal = 0;//缴款总金额
		int qfCount = 0;//欠费人数
		double qfTotal = 0;//欠费总金额
		
		for(int i=0;i < recordlist.size();i++){
			Map<String,Object> map = recordlist.get(i);
			double zf = (Double)map.get("ZFJE");
			double jk = (Double)map.get("JKJE");
			
			jkTotal += jk;
			zfTotal += zf;
			
			if((zf - jk) >= 0){
				qfCount++;
				qfTotal += zf - jk;
			}
		}
		BigDecimal zfTotalB = new BigDecimal(zfTotal);
		zfTotal = zfTotalB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		BigDecimal jkTotalB = new BigDecimal(jkTotal);
		jkTotal = jkTotalB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		BigDecimal qfTotalB = new BigDecimal(qfTotal);
		qfTotal = qfTotalB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		response.put("BQKS", bqks);
		response.put("KSBQ", text);
		//现在时间
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int Year = c.get(Calendar.YEAR);
		int Month = c.get(Calendar.MONTH);
		int Day = c.get(Calendar.DATE); 

		response.put("Year", Year+"");
		response.put("Month", Month+1+"");
		response.put("Day", Day+"");
		response.put("TotalCount", TotalCount+"");//总人数
		response.put("qfCount", qfCount+"");//欠费人数
		response.put("zfTotal", zfTotal+"");//自负总金额
		response.put("jkTotal", jkTotal+"");//缴款总金额
		response.put("qfTotal", qfTotal+"");//欠费总金额
	}
	
	public List<Map<String,Object>> getList(Map<String, Object> request,
			Map<String, Object> response, Context ctx){
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String CKWH = ParameterUtil.getParameter(JGID, BSPHISSystemArgument.CKWH, ctx);
		StringBuffer sql = new StringBuffer();

		// 获取表单参数
		String ksType = (String) request.get("ksType");// 按科室催款 还是按病区催款
		String dicValue = (String) request.get("dicValue");// 获得选中的科室代码
		String ksorbq = "";
//		String dicValue = "";
//		if (dicValues.size() == 1) {
//			dicValue = (String) (dicValues.get(0));
		if (ksType.equals("1")) {
				ksorbq = " and a.BRKS =";
		} else if (ksType.equals("2")) {
				ksorbq = " and a.BRBQ =";
		}
//		}
		if("1".equals(CKWH)){//按科室或病区
			sql.append("select CKBL as CKBL,CKJE as CKJE,DJJE as DJJE,ZDXE as ZDXE,ZYH as ZYH,ZYHM as ZYHM,BRXZ as BRXZ,")
					.append("BRXM as BRXM,BRXB as BRXB,CSNY as CSNY,RYRQ as RYRQ,XTRQ AS XTRQ,BRCH as BRCH,BRKS as BRKS,")
					.append("BRBQ as BRBQ,JGID as JGID,JKJE as JKJE,ZFJE as ZFJE,ZJJE as ZJJE,ZFJE-JKJE as QFJE from ")
					.append("(select CKBL,CKJE,DJJE,ZDXE,ZYH,ZYHM,BRXZ,BRXM,BRXB,CSNY,RYRQ,XTRQ,BRCH,BRKS,")
					.append("BRBQ,JGID,sum(JKJE) as JKJE,sum(ZFJE) as ZFJE,sum(ZJJE) as ZJJE from ")
					.append("(SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
					.append("a.BRBQ,a.JGID,sum(b.JKJE) as JKJE,0 as ZFJE,0 as ZJJE FROM ZY_TBKK b,ZY_BRRY a,ZY_CKWH c ")
					.append("WHERE b.ZYH = a.ZYH AND b.JSCS = 0 AND b.ZFPB = 0 AND a.CYPB = 0 and b.JGID = '")
					.append(JGID).append("' ").append(ksorbq).append("'").append(dicValue).append("'")
					.append(" and a.BRKS =c.KSDM and a.JGID=c.JGID GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,")
					.append("a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID,c.CKBL,c.CKJE,c.DJJE,c.ZDXE")
					.append(" union all ")
					.append("SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
					.append("a.BRBQ,a.JGID,0 as JKJE,sum(b.ZFJE) as ZFJE,sum(b.ZJJE) as ZJJE FROM ZY_BRRY a,ZY_FYMX b,ZY_CKWH c")
					.append(" WHERE a.ZYH = b.ZYH AND a.CYPB = 0 AND b.JSCS = 0 and a.JGID = '")
					.append(JGID).append("' ").append(ksorbq).append("'").append(dicValue).append("'")
					.append(" and a.BRKS =c.KSDM and a.JGID=c.JGID GROUP BY c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,")
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
			.append(JGID).append("' ").append(ksorbq).append("'").append(dicValue).append("'")
			.append(" and a.BRXZ=c.BRXZ and a.JGID=c.JGID GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,")
			.append("a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID,c.CKBL,c.CKJE,c.DJJE,c.ZDXE")
			.append(" union all ")
			.append("SELECT c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ as XTRQ,a.BRCH,a.BRKS,")
			.append("a.BRBQ,a.JGID,0 as JKJE,sum(b.ZFJE) as ZFJE,sum(b.ZJJE) as ZJJE FROM ZY_BRRY a,ZY_FYMX b,ZY_CKWH c")
			.append(" WHERE a.ZYH = b.ZYH AND a.CYPB = 0 AND b.JSCS = 0 and a.JGID = '")
			.append(JGID).append("' ").append(ksorbq).append("'").append(dicValue).append("'")
			.append(" and a.BRXZ=c.BRXZ and a.JGID=c.JGID GROUP BY c.CKBL,c.CKJE,c.DJJE,c.ZDXE,a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,")
			.append("a.BRBQ,a.JGID) group by CKBL,CKJE,DJJE,ZDXE,ZYH,ZYHM,BRXZ,BRXM,BRXB,CSNY,RYRQ,XTRQ,BRCH,BRKS,BRBQ,JGID)")
			.append(" where (JKJE-(ZFJE*CKBL))<ZDXE");
		}
//		// 缴款金额JKJE
//		sql_JKJE.append("SELECT b.ZYH as ZYH,sum(b.JKJE) AS JKJE FROM ZY_TBKK b,ZY_BRRY a WHERE ( b.ZYH  "
//				+ "= a.ZYH ) AND ( b.JSCS = 0 ) AND ( b.ZFPB = 0 ) AND ( a.CYPB = 0 ) "
//				+ "and b.JGID = '" + JGID + "' " + dicValue + " GROUP BY b.ZYH");
//
//		sql.append("SELECT a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXZ as BRXZ,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.RYRQ as RYRQ,a.CYRQ AS XTRQ,a.BRCH as BRCH,a.BRKS as BRKS,a.BRBQ as BRBQ,sum(b.ZFJE) AS ZFJE,sum(b.ZJJE) AS ZJJE,0.00 AS JKJE,0.00 AS QFJE,0.00 AS CKJE,0.00 AS CJJE,'      ' AS QKYK,'' AS CSNY_1,1    AS CKBZ,a.JGID as JGID FROM ZY_BRRY a,ZY_FYMX b WHERE ( a.ZYH  = b.ZYH ) AND ( a.CYPB = 0 ) AND ( b.JSCS = 0 ) "
//				+ dicValue
//				+ " GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID");

//		Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("JGID", JGID);
		String schema = "phis.application.hos.schemas.ZY_QFQD";

		try {
			List<Map<String, Object>> body = dao.doSqlQuery(sql.toString(),
					null);
			body = SchemaUtil.setDictionaryMassageForList(body, schema);

//			List<Map<String, Object>> body_JKJE = dao.doSqlQuery(
//					sql_JKJE.toString(), null);
//			body_JKJE = SchemaUtil.setDictionaryMassageForList(body_JKJE,
//					schema);

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

				BigDecimal ZFJE = (BigDecimal) qfMap.get("ZFJE");
				BigDecimal JKJE = (BigDecimal) qfMap.get("JKJE");
				BigDecimal QFJE = (BigDecimal) qfMap.get("QFJE");
				qfMap.put("JKJE", JKJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				qfMap.put("ZFJE",ZFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				qfMap.put("QFJE",QFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				
				qfMap.put("CSNY", age);
				qfMap.put("RYRQ", days + 1);
				qfMap.put("ck", "");
			}

//			// 根据查询到的缴款金额List匹配 欠费清单中的记录并计算出欠费清单
//			for (int i = 0; i < body_JKJE.size(); i++) {
//				Map<String, Object> jkMap = body_JKJE.get(i);
//				for (int j = 0; j < body.size(); j++) {
//					Map<String, Object> qfMap = body.get(j);
//
//					BigDecimal zyh = (BigDecimal) jkMap.get("ZYH");
//					BigDecimal JKJE = (BigDecimal) jkMap.get("JKJE");
//					BigDecimal zyh2 = (BigDecimal) qfMap.get("ZYH");
//					double ZFJE = (Double) qfMap.get("ZFJE");
//					if (zyh.longValue() == zyh2.longValue()) {
//						if(JKJE.doubleValue() - ZFJE > 0){
//							body.remove(j);
//						}else{
//							qfMap.put("JKJE", JKJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//							//四舍五入   保留两位小数
//							BigDecimal   b   =   new   BigDecimal(ZFJE - JKJE.doubleValue());  
//							ZFJE   =   b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
//							qfMap.put("QFJE",ZFJE);
//						}
//					}
//				}
//			}
			
			//将自负金额为负的记录筛选掉
//			Iterator<Map<String,Object>> iter = body.iterator();
//			while(iter.hasNext()){
//				Map<String,Object> o = iter.next();
//				if((Double)o.get("ZFJE") <= 0 ){
////					iter.remove();
//					body.remove(o);
//				}
//				if((Double)o.get("QFJE") == 0 ){//欠费金额为0时，不显示在欠费清单列表
////					iter.remove();
//					body.remove(o);
//				}
//			}

//			for(int i=0;i<body.size();i++){
//				Map<String,Object> o = body.get(i);
//				if((Double)o.get("ZFJE") <= 0 ){
//					body.remove(o);
//				}
//				if((Double)o.get("QFJE") == 0 ){//欠费金额为0时，不显示在欠费清单列表
//					body.remove(o);
//				}
//			}
			return body;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			return null;
		}
	}

}
