package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ClinicChargesSummaryFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构名称
		String dateFrom = (String) request.get("beginDate");
		String dateTo = (String) request.get("endDate");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
			BaseDAO dao = new BaseDAO(ctx);
			parameters.put("JGID", jgid+"%");
			parameters.put("beginDate", dateFrom);
			parameters.put("endDate", dateTo);
			String hzrb_sql = "select CZGH as YGDM,0 as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,0 as GHRC,sum(FPZS) as FPZS,0 as THSL," +
					" sum(ZFZS) as ZFZS,sum(ZFFSHJ) as ZFFSHJ,sum(NHZFHJ) as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" sum(NHJZ) as NHJZ,sum(YBJZ) as YBJZ,sum(JZJEST) as JZJEST,sum(JZZE) as JZZE," +
					" sum(ZFZF) as ZFZF" +
					" from MS_HZRB where JGID like :JGID " +
					" and to_char(HZRQ,'yyyy-mm-dd')>=:beginDate" +
					" and to_char(HZRQ,'yyyy-mm-dd')<=:endDate group by CZGH";
			String ghrb_sql = "select CZGH as YGDM,sum(ZJJE) as GHJE,0 as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(FPZS) as GHRC,0 as FPZS," +
					" sum(THSL) as THSL,0 as ZFZS,0 as ZFFSHJ,0 as NHZFHJ,0 as YBZFHJ," +
					" 0 as NHJZ,0 as YBJZ,0 as JZJEST,0 as JZZE,0 as ZFZF " +
					" from MS_GHRB where JGID like :JGID " +
					" and to_char(HZRQ,'yyyy-mm-dd')>=:beginDate " +
					" and to_char(HZRQ,'yyyy-mm-dd')<=:endDate group by CZGH";
			String hzghrb_sql = "select YGDM as YGDM,sum(GHJE) as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(GHRC) as GHRC,sum(FPZS) as FPZS," +
					" sum(THSL) as THSL,sum(ZFZS) as ZFZS,sum(ZFFSHJ) as ZFFSHJ,sum(NHZFHJ) as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" sum(NHJZ) as NHJZ,sum(YBJZ) as YBJZ,sum(JZJEST) as JZJEST,sum(JZZE) as JZZE," +
					" sum(ZFZF) as ZFZF from ("
					+ hzrb_sql + " union all " + ghrb_sql + ") group by YGDM";
			List<Map<String, Object>> hzghrb_list = dao.doSqlQuery(hzghrb_sql,parameters);
			// 收费现金金额和货币误差赋值
			for (int i = 0; i < hzghrb_list.size(); i++) {
				Map<String, Object> xj_hz_map = new HashMap<String, Object>();
				hzghrb_list.get(i).put("CZGH",DictionaryController.instance().get("phis.dictionary.doctor").getText(
										hzghrb_list.get(i).get("YGDM").toString()));
				if (xj_hz_map != null && xj_hz_map.size() > 0) {
					hzghrb_list.get(i).putAll(xj_hz_map);// 接着放到List 接着对比
				}
			}
			int ghcount = 0;// 人次合计
			Double ghAmount = 0.0;// 挂号金额合计
			int thcount = 0;// 退号合计
			int sfcount = 0;// 发票张数合计
			Double sfamount = 0.0;// 收费金额合计
			int zfcount = 0;// 发票作废合计
			Double totals = 0.0;// 总的合计
			Double xjAmount = 0.0;// 现金合计
			Double qtysAmount = 0.0;// 其他应收
			Double hbwcAmount = 0.0;// 货币误差	
            double ZFFSHJAmount=0.0;//自费金额合计
            double NHZFHJAmount=0.0;//农合自费金额合计
            double YBZFHJAmount=0.0;//医保自费金额合计
            double XJJEAmount=0.0;//现金金额合计
            double ZFZFAmount=0.0;//账户支付合计
            double NHJZAmount=0.0;//农合记账合计
            double YBJZAmount=0.0;//医保记账
            double JZZEAmount=0.0;//记账总额合计
			java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");
			for (int j = 0; j < hzghrb_list.size(); j++) {
				Map<String, Object> m = new HashMap<String, Object>();
				ghcount += parseInt(hzghrb_list.get(j).get("GHRC"));
				thcount += parseInt(hzghrb_list.get(j).get("THSL"));
				ghAmount += parseDouble(hzghrb_list.get(j).get("GHJE"));
				sfcount += parseInt(hzghrb_list.get(j).get("FPZS"));
				zfcount += parseInt(hzghrb_list.get(j).get("ZFZS"));
				sfamount += parseDouble(hzghrb_list.get(j).get("ZJJE"));
				totals += BSPHISUtil.getDouble(
						parseDouble(hzghrb_list.get(j).get("ZJJE"))
								+ parseDouble(hzghrb_list.get(j).get("GHJE")),
						2);
				xjAmount += parseDouble(hzghrb_list.get(j).get("XJJE"));
				qtysAmount += parseDouble(hzghrb_list.get(j).get("QTYS"));
				hbwcAmount += parseDouble(hzghrb_list.get(j).get("HBWC"));
				
				ZFFSHJAmount += parseDouble(hzghrb_list.get(j).get("ZFFSHJ"));
				NHZFHJAmount += parseDouble(hzghrb_list.get(j).get("NHZFHJ"));
				YBZFHJAmount += parseDouble(hzghrb_list.get(j).get("YBZFHJ"));
				XJJEAmount += parseDouble(hzghrb_list.get(j).get("XJJE"));
				ZFZFAmount += parseDouble(hzghrb_list.get(j).get("ZFZF"));
				NHJZAmount += parseDouble(hzghrb_list.get(j).get("NHJZ"));
				YBJZAmount += parseDouble(hzghrb_list.get(j).get("YBJZ"));
				JZZEAmount += parseDouble(hzghrb_list.get(j).get("JZZE"));
				m.put("ZFFSHJ", hzghrb_list.get(j).get("ZFFSHJ")+"");
				m.put("NHZFHJ", hzghrb_list.get(j).get("NHZFHJ")+"");
				m.put("YBZFHJ", hzghrb_list.get(j).get("YBZFHJ")+"");
				m.put("ZFZF", hzghrb_list.get(j).get("ZFZF")+"");
				m.put("NHJZ", hzghrb_list.get(j).get("NHJZ")+"");
				m.put("YBJZ", hzghrb_list.get(j).get("YBJZ")+"");
				m.put("JZZE", df.format(parseDouble(hzghrb_list.get(j).get("JZZE"))+parseDouble(hzghrb_list.get(j).get("ZFZF")))+"");
				m.put("GHJE",String.format("%1$.2f", hzghrb_list.get(j).get("GHJE")));
				m.put("GHRC", hzghrb_list.get(j).get("GHRC") + "");
				m.put("THSL", hzghrb_list.get(j).get("THSL") + "");
				m.put("CZGH", hzghrb_list.get(j).get("CZGH") + "");
				m.put("ZJJE",String.format("%1$.2f", hzghrb_list.get(j).get("ZJJE")));
				m.put("FPZS", hzghrb_list.get(j).get("FPZS") + "");
				m.put("ZFZS", hzghrb_list.get(j).get("ZFZS") + "");
				m.put("QTYS",String.format("%1$.2f", hzghrb_list.get(j).get("QTYS")));
				m.put("HBWC",String.format("%1$.2f", hzghrb_list.get(j).get("HBWC")));
				m.put("XJJE",String.format("%1$.2f", parseDouble(hzghrb_list.get(j).get("XJJE"))));
				m.put("TOTALAMOUNT", String.format("%1$.2f", BSPHISUtil
						.getDouble(parseDouble(hzghrb_list.get(j).get("ZJJE"))
								+ parseDouble(hzghrb_list.get(j).get("GHJE")),
								2)));
			    li.add(m);
			}
			Map<String, Object> hzrq = new HashMap<String, Object>();
			Map<String, Object> hz = new HashMap<String, Object>();
			hzrq.put("JGID", jgid);
			// 查询上次结账时间
			
			hz.put("CZGH", "合计：");
			hz.put("TOTALAMOUNT", String.format("%1$.2f", totals));
			hz.put("GHRC", ghcount + "");
			hz.put("THSL", thcount + "");
			hz.put("GHJE", String.format("%1$.2f", ghAmount));
			hz.put("FPZS", sfcount + "");
			hz.put("ZFZS", zfcount + "");
			hz.put("ZJJE", String.format("%1$.2f", sfamount));
			hz.put("xjAmount", String.format("%1$.2f", xjAmount));
			hz.put("QTYS", String.format("%1$.2f", qtysAmount));
			hz.put("HBWC", String.format("%1$.2f", hbwcAmount));
			hz.put("ZFFSHJ", String.format("%1$.2f", ZFFSHJAmount));
			hz.put("NHZFHJ", String.format("%1$.2f", NHZFHJAmount));
			hz.put("YBZFHJ", String.format("%1$.2f", YBZFHJAmount));
			hz.put("XJJE", String.format("%1$.2f", XJJEAmount));
			hz.put("NHJZ", String.format("%1$.2f", NHJZAmount));
			hz.put("ZFZF", String.format("%1$.2f", ZFZFAmount));
			hz.put("YBJZ", String.format("%1$.2f", YBJZAmount));
			hz.put("JZZE", String.format("%1$.2f", JZZEAmount+ZFZFAmount));
			
			records.addAll(li);
			records.add(hz);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("preparedby", user.getManageUnitName());
		response.put("startSummaryDate", request.get("beginDate"));
		response.put("summaryDate", request.get("endDate"));
		response.put("Lister", user.getUserName());
		response.put("DateTabling", BSHISUtil.getDate());
	}
	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}	
}
