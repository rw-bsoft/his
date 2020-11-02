package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.sto.source.StorehouseAccountingBooksModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class StorehouseAccountingBooksFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("KJSJ", request.get("QSSJ")+"--"+request.get("ZZSJ"));
		response.put("JGMC", user.getManageUnitName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String,Object> req=new HashMap<String,Object>();
		req.put("body", request);
		StorehouseAccountingBooksModel model=new StorehouseAccountingBooksModel(dao);
		try {
			Map<String,Object> map_ret=model.queryStorehouseAccountingBooks(req,ctx);
			if(map_ret!=null&&map_ret.size()>0&&map_ret.get("body")!=null){
				List<Map<String,Object>> list=(List<Map<String,Object>>)map_ret.get("body");
				for(Map<String,Object> map:list){
					map.put("KCSL", String.format("%1$.2f", map.get("KCSL")));
					map.put("JHJE", String.format("%1$.4f", map.get("JHJE")));
					map.put("LSJE", String.format("%1$.4f", map.get("LSJE")));
				}
				records.addAll(list);
			}
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,"查询会计账簿失败"+e.getMessage());
		}
	}

}
