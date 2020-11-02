package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.application.pcm.source.PrescriptionCommentsModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.CNDHelper;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

public class PrescriptionCommentsTjMXFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		try {
		List<?> cnd= CNDHelper.toListCnd(request.get("cnd")+"");
		BaseDAO dao = new BaseDAO(ctx);
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> map_ret=model.queryTJBBSJMX(cnd,999,1);
			if(map_ret.get("body")!=null){
				List<Map<String, Object>> body=(List<Map<String, Object>>)map_ret.get("body");
				for(Map<String,Object> m:body){
					m.put("DPRQ", m.get("DPRQ").toString()==null?"":m.get("DPRQ").toString().substring(0, m.get("DPRQ").toString().length()-2));
					records.add(m);
				}
			}
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (ExpException e) {
			throw new PrintException(9000,e.getMessage());
		}
	}

}
