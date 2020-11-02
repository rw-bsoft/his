package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class SummarycategoryHospitalFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String Gl_jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", Gl_jgid);
		try {
			Date stardate = sdfdate.parse(request.get("beginDate") + "");
			Date enddate = sdfdate.parse(request.get("endDate") + "");
			parameters.put("adt_hzrq", stardate);
			parameters.put("adt_hzrq_end", enddate);
			List<Map<String, Object>> xmflList = dao
					.doQuery(
							"SELECT b.ZYGB as ZYGB,b.SFMC as SFMC,sum(a.ZJJE) as ZJJE FROM ZY_SRHZ a,GY_SFXM b WHERE ( a.SFXM = b.SFXM ) AND ( a.KSLB = 1 ) AND ( a.HZRQ >= :adt_hzrq ) and ( a.HZRQ <= :adt_hzrq_end ) AND a.JGID = :al_jgid GROUP BY b.ZYGB,b.SFMC",
							parameters);
			for (int i = 0; i < xmflList.size(); i = i + 3) {
				Map<String, Object> cf = new HashMap<String, Object>();
				cf.put("XM1", xmflList.get(i).get("SFMC"));
				cf.put("JE1",
						String.format("%1$.2f", xmflList.get(i).get("ZJJE")));
				if (i + 1 < xmflList.size()) {
					cf.put("XM2", xmflList.get(i + 1).get("SFMC"));
					cf.put("JE2",
							String.format("%1$.2f",
									xmflList.get(i + 1).get("ZJJE")));
				}
				if (i + 2 < xmflList.size()) {
					cf.put("XM3", xmflList.get(i + 2).get("SFMC"));
					cf.put("JE3",
							String.format("%1$.2f",
									xmflList.get(i + 2).get("ZJJE")));
				}
				records.add(cf);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String Gl_jgid = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String czy = (String) user.getUserName();
		response.put("title", jgname);
		response.put("CZY", czy);
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("al_jgid", Gl_jgid);
		try {
			Date stardate = sdfdate.parse(request.get("beginDate") + "");
			Date enddate = sdfdate.parse(request.get("endDate") + "");
			String[] lS_ksrq = (request.get("beginDate") + "").split("-| ");
			String ksrq = lS_ksrq[0] + "年" + lS_ksrq[1] + "月" + lS_ksrq[2]
					+ "日";
			String[] lS_jsrq = (request.get("endDate") + "").split("-| ");
			String jsrq = lS_jsrq[0] + "年" + lS_jsrq[1] + "月" + lS_jsrq[2]
					+ "日";
			response.put("HZRQ",ksrq + " 至 " + jsrq);
			parameter.put("adt_hzrq", stardate);
			parameter.put("adt_hzrq_end", enddate);
			Map<String, Object> xmflMap = dao
					.doLoad("SELECT nvl(sum(a.ZJJE),0) as ZJJE FROM ZY_SRHZ a,GY_SFXM b WHERE ( a.SFXM = b.SFXM ) AND ( a.KSLB = 1 ) AND ( a.HZRQ >= :adt_hzrq ) and ( a.HZRQ <= :adt_hzrq_end ) AND a.JGID = :al_jgid",
							parameter);
			response.put("HJJE", String.format("%1$.2f", xmflMap.get("ZJJE")));//总计金额
			
//			Map<String, Object> xmflMap2 = dao
//			.doLoad("SELECT sum(a.GRXJZF) as XJJE,sum(a.TCZC) as TCZF,sum(a.BCZHZF) as ZHZF,sum(a.YJQT) as JZJE FROM ZY_JZXX a WHERE ( HZRQ >= :adt_hzrq ) and ( HZRQ <= :adt_hzrq_end ) AND JGID = :al_jgid",
//					parameter);
//			response.put("XJJE", String.format("%1$.2f", xmflMap2.get("XJJE")));//现金金额:
//			response.put("TCZF", String.format("%1$.2f", xmflMap2.get("TCZF")));//统筹支付:
//			response.put("ZHZF", String.format("%1$.2f", xmflMap2.get("ZHZF")));//账户支付:
//			response.put("JZJE", String.format("%1$.2f", xmflMap2.get("JZJE")));//记账金额:
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
