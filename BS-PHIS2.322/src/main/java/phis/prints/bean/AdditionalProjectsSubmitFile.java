package phis.prints.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.war.source.DoctorAdviceExecuteModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class AdditionalProjectsSubmitFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		StringBuffer hql=new StringBuffer();
		hql.append("select OFFICENAME as OFFICENAME from SYS_Office where ID=:id");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("id", MedicineUtils.parseLong(user.getProperty("wardId")));
		try {
			Map<String,Object> map_ksmc=dao.doLoad(hql.toString(), map_par);
			response.put("TITLE", jgname+map_ksmc.get("OFFICENAME")+"药品附加计价单");
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"打印数据查询失败"+e.getMessage());
		}
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		String jlxhs=MedicineUtils.parseString(request.get("JLXHS"));
		String zxsls=MedicineUtils.parseString(request.get("ZXSLS"));
		
		String[] jlxh=jlxhs.split(",");
		String[] zxsl=zxsls.split(",");
		List<Long> list_jlxhs=new ArrayList<Long>();
		Map<String,Object> map=new HashMap<String,Object>();
		for(int i=0;i<jlxh.length;i++){
			map.put(jlxh[i], zxsl[i]);
			list_jlxhs.add(MedicineUtils.parseLong(jlxh[i]));
		}
		Map<String,Object> req=new HashMap<String,Object>();
		if(request.containsKey("ZYHS")){
			String zyhs=MedicineUtils.parseString(request.get("ZYHS"));
			List<Long> lit_zyhs=new ArrayList<Long>();
			String[] zyh=zyhs.split(",");
			for(String z:zyh){
				lit_zyhs.add(MedicineUtils.parseLong(z));
			}
			req.put("ZYHS", lit_zyhs);
		}
		if(request.containsKey("XMXHS")){
			String xmxhs=MedicineUtils.parseString(request.get("XMXHS"));
			List<Long> lit_xmxhs=new ArrayList<Long>();
			String[] xmxh=xmxhs.split(",");
			for(String z:xmxh){
				lit_xmxhs.add(MedicineUtils.parseLong(z));
			}
			req.put("XMXHS", lit_xmxhs);
		}
		Map<String,Object> res=new HashMap<String,Object>();
		DoctorAdviceExecuteModel model=new DoctorAdviceExecuteModel(dao);
		try {
			model.doAdditionProjectsQuery(req,res,ctx);
			List<Map<String,Object>> list_ret=(List<Map<String,Object>>)res.get("body");
			for(Map<String,Object> m:list_ret){
				int i=0;
				for(long j:list_jlxhs){
					if(j==MedicineUtils.parseLong(m.get("JLXH"))){
						i=1;
						break;
					}
				}
				if(i==0){
					continue;
				}
				Map<String,Object> map_temp=new HashMap<String,Object>();
				map_temp.putAll(m);
				//map_temp.put("YZMC", m.get("YZMC")+"/"+m.get("YFDW"));
//				for(String key:map.keySet()){
//					if(key.equals(m.get("JLXH")+"")){
//						map_temp.put("YZSL", map.get(key));
//						break;
//					}
//				}
				map_temp.put("YZSL", m.get("YCSL"));
				map_temp.put("SYPC", m.get("SYPC_text"));
				map_temp.put("JE", MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(m.get("YPDJ"))*MedicineUtils.parseDouble(map_temp.get("YZSL"))));
				records.add(map_temp);
			}
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,"打印数据查询失败"+e.getMessage());
		} catch (ParseException e) {
			throw new PrintException(9000,"打印数据查询失败"+e.getMessage());
		}
		
	}

}
