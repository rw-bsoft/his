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
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class RegistHospitalReqOrder implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		//String quhaomm = String.valueOf(request.get("quhaomm"));
		String zhuanzhendh = String.valueOf(request.get("zhuanzhendh"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			//parameters.put("QUHAOMM", quhaomm);
			parameters.put("ZHUANZHENDH", zhuanzhendh);
			String sql = "select a.ZHUANZHENZYSX as ZLJG,a.ZHUANZHENZD as MSZD,a.BINGRENXM as BRXM,a.BINGRENNL as NL,a.BINGRENXB as BRXB,a.BINGRENLXDZ as LXDZ,a.BINGRENLXDH as LXDH,a.SHENQINGYSDH as YSDH,a.ZHUANZHENYY as ZZYY,a.BINQINGMS as BQMS," +
					"a.SHENQINGRQ as ZRRQ,a.SHENQINGYS as SQYS from DR_CLINICRECORDLHISTORY a where ZHUANZHENDH=:ZHUANZHENDH ";
			List<Map<String, Object>> list = dao.doQuery(sql, parameters);	
			String bt = "住院转诊申请单（双向转诊）";
			Map<String, Object> map = null;
			if (list.size() > 0) {
				 map = list.get(0);
				String sqrq = (String)map.get("ZRRQ");
				sqrq = sqrq.substring(0, 10);
				map.put("ZRRQ", sqrq);
				if ("470032824".equals(map.get("ZHUANRUYYDM"))) {
					bt = "住院转诊申请单";
				}
				if(map.get("BRXB")!=null&&"1".equals(map.get("BRXB"))){
					map.put("BRXB","男");
				}else if(map.get("BRXB")!=null&&"2".equals(map.get("BRXB"))){
					map.put("BRXB","女");
				}else{
					map.put("BRXB","未知");
				}
				Map<String,Object> par = new HashMap<String, Object>();
				String sqys = (String)map.get("SQYS");
						String sqlName = "select a.PERSONNAME as ys from SYS_Personnel a where a.PERSONID='"+sqys+"'";
						List<Map<String,Object>> mapPerSonName = dao.doQuery(sqlName, par);
						Map<String,Object> maps = mapPerSonName.get(0);
						map.put("SQYS",maps.get("ys"));
				response.putAll(map);
			}	
			response.put("BIAOTI", bt);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

}

