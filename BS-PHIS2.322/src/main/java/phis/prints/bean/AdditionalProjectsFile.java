package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.war.source.DoctorAdviceExecuteModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class AdditionalProjectsFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// TODO Auto-generated method stub
		response.put("MYHS", 28);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);		
		String jlxhs=MedicineUtils.parseString(request.get("JLXHS"));
		List<Long> list_jlxh=new ArrayList<Long>();
		String[] s=jlxhs.split(",");
		for(String s1:s){
			list_jlxh.add(MedicineUtils.parseLong(s1));	
		}
		Map<String,Object> req=new HashMap<String,Object>();
		req.put("JLXHS", list_jlxh);
		Map<String,Object> res=new HashMap<String,Object>();
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doDetailChargeQuery(req,res,ctx);
			List<Map<String,Object>> ret=(List<Map<String,Object>>)res.get("body");
			for(Map<String,Object> m:ret){
				m.put("ZJE", MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(m.get("YCSL"))*MedicineUtils.parseDouble(m.get("YPDJ")))+"*"+String.format("%1$.0f", MedicineUtils.parseDouble(m.get("FYCS"))));
				records.add(m);
			}
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,"打印失败"+e.getMessage());
		}
	}

}
