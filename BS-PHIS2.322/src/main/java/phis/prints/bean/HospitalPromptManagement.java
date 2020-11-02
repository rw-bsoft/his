package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalPromptManagement implements IHandler {
	
	List<Map<String, Object>> list;
	String 	bqks = "";
	String text = "";

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		try {
			//获得当前机构名称
			String JGMC = new String(request.get("jgmc").toString()
					.getBytes("iso8859_1"), "UTF-8");
//			String CKJE = (String)request.get("CKJE");
//			if(CKJE.length() == 0){
//				CKJE = "0";
//			}

			for(int i=0;i < list.size();i++){
				Map<String, Object> map = list.get(i);
				map.put("BQKS", bqks);// 病区 还是 科室
				map.put("KSBQ", text);// 哪个病区或者哪个科室
				map.put("JGMC", JGMC);
//				map.put("BJJE", CKJE);
				records.add(map);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {

		// 根据queryType判断  是切换Tab页打印还是搜索打印
		String queryType = (String) request.get("queryType");
		if(queryType.equals("1")){
			queryTab(request,response,ctx);
		}else if(queryType.equals("2")){
			queryCnd(request,response,ctx);
		}
	}
	
	public void queryTab(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		//获得前台用户选择”是“的住院号 数据集
		String zyhStr = request.get("zyhStr")+"";
		String[] zyhs = zyhStr.split(",");
		String ckjeStr = request.get("ckjeStr")+"";
		String[] ckjes = ckjeStr.split(",");

		List<Map<String, Object>> useList = getLMap(request,response,ctx);
		
		//从检索出数据中 取出用户选择”是“的数据  并放入到集合中
		list = new ArrayList<Map<String,Object>>();
		
		for(int i=0;i < zyhs.length;i++){
			String zyh = zyhs[i];
			String ckje = ckjes[i];
			for(int j=0;j < useList.size();j++){
				Map<String,Object> o = useList.get(j);
				String zyh2 = (Long)o.get("ZYH")+"";
				if(zyh.equals(zyh2)){
					o.put("BJJE", ckje);
					list.add(o);
					break;
				}
			}
		}
	}
	
	public void queryCnd(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		
		list = getLMap(request,response,ctx);
	}
	
	public void responseToPanel(Map<String, Object> response,Map<String,Object> map){
		
		response.put("Year", map.get("Year") + "");// 年
		response.put("Month", map.get("Month") + "");// 月
		response.put("Day", map.get("Day") + "");// 日
		response.put("BQKS", bqks);// 病区 还是 科室
		response.put("KSBQ", text);// 哪个病区或者哪个科室
		response.put("BRXM", map.get("BRXM") + "");// 病人姓名
		response.put("BRCH", map.get("BRCH") + "");// 病人床号
		response.put("ZYHM", map.get("ZYHM") + "");// 住院号码
		response.put("ZFJE", map.get("ZFJE") + "");// 医疗费用
		response.put("JKJE", map.get("JKJE") + "");// 预交款
		response.put("QFJE", map.get("QFJE") + "");// 欠费金额
		response.put("BJJE", map.get("QFJE") + "");// 补交金额
													// 当前设置补交金额等于欠费金额
	}
	
	public List<Map<String, Object>> getLMap(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		StringBuffer sql = new StringBuffer();
		StringBuffer sql_JKJE = new StringBuffer();
		
		List<Map<String, Object>> body = null;

		try {
			// 获取表单参数
			String ksType = (String) request.get("ksType");// 按科室催款 还是按病区催款
			// 获得选中的科室名称
			text = new String(request.get("text").toString()
					.getBytes("iso8859_1"), "UTF-8");

			String dicValue = (String) request.get("dicValue");// 获得选中的科室代码

			if (ksType.equals("1")) {
				bqks = "科室：";
				dicValue = " and a.BRKS ='" + dicValue + "' ";
			} else if (ksType.equals("2")) {
				bqks = "病区：";
				dicValue = " and a.BRBQ ='" + dicValue + "' ";
			}

			// 缴款金额JKJE
			sql_JKJE.append("SELECT b.ZYH as ZYH,sum(b.JKJE) AS JKJE FROM ZY_TBKK b,ZY_BRRY a WHERE ( b.ZYH  "
					+ "= a.ZYH ) AND ( b.JSCS = 0 ) AND ( b.ZFPB = 0 ) AND ( a.CYPB = 0 or a.CYPB = 1 ) "
					+ "and b.JGID = '"
					+ JGID
					+ "' "
					+ dicValue
					+ " GROUP BY b.ZYH");

			sql.append("SELECT a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXZ as BRXZ,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.RYRQ as RYRQ,a.CYRQ AS XTRQ,a.BRCH as BRCH,a.BRKS as BRKS,a.BRBQ as BRBQ,sum(b.ZFJE) AS ZFJE,sum(b.ZJJE) AS ZJJE,0.00 AS JKJE,0.00 AS QFJE,0.00 AS CKJE,0.00 AS CJJE,'      ' AS QKYK,'' AS CSNY_1,1    AS CKBZ,a.JGID as JGID FROM ZY_BRRY a,ZY_FYMX b WHERE ( a.ZYH  = b.ZYH ) AND ( a.CYPB = 0 or a.CYPB = 1) AND ( b.JSCS = 0 ) "
					+ dicValue
					+ " GROUP BY a.ZYH,a.ZYHM,a.BRXZ,a.BRXM,a.BRXB,a.CSNY,a.RYRQ,a.CYRQ,a.BRCH,a.BRKS,a.BRBQ,a.JGID");

			Map<String, Object> parameters = new HashMap<String, Object>();

			String schema = "phis.application.hos.schemas.ZY_QFQD";

			body = dao.doSqlQuery(sql.toString(),
					parameters);
			List<Map<String, Object>> body_JKJE = dao.doSqlQuery(
					sql_JKJE.toString(), parameters);
			body_JKJE = SchemaUtil.setDictionaryMassageForList(body_JKJE,
					schema);
			body = SchemaUtil.setDictionaryMassageForList(body, schema);

			for (int j = 0; j < body.size(); j++) {
				Map<String, Object> qfMap = body.get(j);

				// 当前时间
				Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
				int Year = c.get(Calendar.YEAR);
				int Month = c.get(Calendar.MONTH);
				int Day = c.get(Calendar.DATE); 

				BigDecimal ZFJE = (BigDecimal) qfMap.get("ZFJE");// 医疗费用
				BigDecimal zyh2 = (BigDecimal) qfMap.get("ZYH");
				qfMap.put("ZYH", zyh2.longValue());
				qfMap.put("ZFJE", ZFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				qfMap.put("QY", "尚欠:");
				qfMap.put("QFJE", ZFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				qfMap.put("BJJE", ZFJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

				qfMap.put("Year", Year);
				qfMap.put("Month", Month+1);
				qfMap.put("Day", Day);
			}

			// 根据查询到的缴款金额List匹配 欠费清单中的记录并计算出欠费清单
			for (int i = 0; i < body_JKJE.size(); i++) {
				Map<String, Object> jkMap = body_JKJE.get(i);
				for (int j = 0; j < body.size(); j++) {
					Map<String, Object> qfMap = body.get(j);

					BigDecimal zyh = (BigDecimal) jkMap.get("ZYH");
					BigDecimal JKJE = (BigDecimal) jkMap.get("JKJE");
					long zyh2 = (Long) qfMap.get("ZYH");
					double ZFJE = (Double) qfMap.get("ZFJE");
					if (zyh.longValue() == zyh2) {
//						if(JKJE.doubleValue() - ZFJE > 0){
//							body.remove(j);
//						}else{
							qfMap.put("JKJE", JKJE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						
							//四舍五入   保留两位小数
							BigDecimal   b   =   new   BigDecimal(ZFJE - JKJE.doubleValue());  
							double qfje   =   b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
							if(qfje>0){
								qfMap.put("QY", "尚欠:");
							}else{
								qfMap.put("QY", "尚余:");
							}
							qfMap.put("QFJE", Math.abs(qfje));
							qfMap.put("BJJE", Math.abs(qfje));
//						}
					}
				}
			}
			
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return body;
	}
	
	
}
