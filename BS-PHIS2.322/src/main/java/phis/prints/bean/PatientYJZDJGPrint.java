package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PatientYJZDJGPrint implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		// User user = (User) ctx.get("user.instance");
		// int KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		// BaseDAO dao = new BaseDAO(ctx);

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();// 用户的机构名称
		String jgid = (String) user.getProperty("biz_MedicalId");// 用户的机构ID
		String brid = String.valueOf(request.get("brid"));
		String mzzypb = String.valueOf(request.get("mzzypb"));
		String zdid = String.valueOf(request.get("zdid"));
		Map<String, Object> p = new HashMap<String, Object>();
		BaseDAO dao = new BaseDAO(ctx);
		String hql = null;
		switch (Integer.parseInt(mzzypb)) {
		case 0:// 门诊病人基本信息查询
			p.put("BRID", brid);
			hql = "select BRXB as BRXB,BRXM as BRXM,CSNY as NL,MZHM as MZZYHM from MS_BRDA  where BRID =:BRID";
			break;
		case 1:// 住院病人基本信息查询
			p.put("BRID", Long.parseLong(brid));
			hql = "select b.BRXB as BRXB,b.BRXM as BRXM,b.CSNY as NL,a.ZYHM as MZZYHM from ZY_BRRY a,MS_BRDA b  where a.BRID=b.BRID and a.BRID =:BRID";
			break;
		default:
			p.put("BRID", Long.parseLong(brid));
			String zyh = String.valueOf(request.get("zyh"));
			p.put("ZYH", Long.parseLong(zyh));
			hql = "select b.BRXB as BRXB,b.BRXM as BRXM,b.CSNY as NL,c.ZYHM as MZZYHM from JC_BRRY a,MS_BRDA b,JC_YJ01 c where a.ZYH=c.ZYH and a.BRID=b.BRID and a.BRID =:BRID and a.ZYH=:ZYH";
		}
		try {

			Map<String, Object> r = dao.doQuery(hql, p).get(0);
			p = new HashMap<String, Object>();
			p.put("ZDID", Long.parseLong(zdid));
			String zdmc = (String) (dao.doQuery(
					"select ZDMC as ZDMC from YJ_ZDJG where ZDID = :ZDID", p)
					.get(0).get("ZDMC"));
			r.put("Title", jgname + "医技项目执行诊断结果打印");
			r.put("ZXYS", user.getUserId());
			r.put("ZXKS", jgid);
			r.put("ZDJG", zdmc);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date csny = sdf.parse(r.get("NL") + "");
			Map<String, Object> ageMap = BSPHISUtil.getPersonAge(csny,
					new Date());
			r.put("NL", ageMap.get("age"));
			SchemaUtil.setDictionaryMassageForForm(r,
					BSPHISEntryNames.ZY_BRRY_DT2);
			SchemaUtil.setDictionaryMassageForForm(r, BSPHISEntryNames.YJ_MZYW);
			Map<String, Object> r2 = null;
			if (r.get("ZXKS") != null) {
				r2 = (Map) r.get("ZXKS");
				r.put("JCKS", r2.get("text"));
			}
			if (r.get("ZXYS") != null) {
				r2 = (Map) r.get("ZXYS");
				r.put("JCYS", r2.get("text"));
			}
			if (r.get("BRXB") != null) {
				r2 = (Map) r.get("BRXB");
				r.put("XB", r2.get("text"));
			}
			switch (Integer.parseInt(mzzypb)) {
			case 0:// 门诊病人基本信息查询
				r.put("MZZYPB", "门诊号码:");
				break;
			case 1:// 住院病人基本信息查询
				r.put("MZZYPB", "住院号码:");
				break;
			default:
				r.put("MZZYPB", "家床号:");
			}
			response.putAll(r);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
