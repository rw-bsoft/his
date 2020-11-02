package phis.prints.bean; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map; 

import phis.application.mds.source.MedicineUtils;
import phis.application.sto.source.StorehouseDrugsExpireTipsModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 
public class DrugsExpireTipsFile implements IHandler {
	@SuppressWarnings("unchecked")
	@Override
	public void getFields(Map<String, Object> req,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		StorehouseDrugsExpireTipsModel model=new StorehouseDrugsExpireTipsModel(dao);
		try {
			req.put("print", true);
			Map<String,Object> map_ret=model.loadStorehouseDrugsExpireTipsList(req,req,ctx);
			if(map_ret!=null){
				records.addAll((List<Map<String,Object>>)map_ret.get("body"));	
			}
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		}
//		BaseDAO dao = new BaseDAO(ctx);
//		int kfsb = Integer.parseInt(req.get("kfsb")+"");
//		String adt_date = req.get("jzrq")+"";
//		Map<String,Object> parameters = new HashMap<String, Object>();
//		String sql = "SELECT DISTINCT  YK_TYPK.YPMC as YPMC , YK_TYPK.YPGG as YPGG,  YK_TYPK.YPDW as YPDW , YK_CDDZ.CDMC as CDMC, YK_KCMX.KCSL as KCSL, YK_KCMX.YPXQ as YPXQ,  YK_TYPK.PYDM as PYDM"+    
//        " FROM YK_CDDZ ,YK_TYPK , YK_KCMX , YK_YPXX"+    
//       " WHERE  ( YK_TYPK.YPXH = YK_KCMX.YPXH ) and ( YK_KCMX.YPCD = YK_CDDZ.YPCD ) and  ( YK_KCMX.ypxh = YK_YPXX.YPXH ) and "+
//              "( YK_KCMX.jgid = YK_YPXX.jgid ) and "+
//              "( YK_YPXX.YKSB = "+kfsb+" ) and"+
//             " ( YK_KCMX.KCSL > 0 ) and "+
//         " ( YK_TYPK.Zfpb=0 ) and ( to_char(YK_KCMX.YPXQ,'yyyymmdd') <= "+adt_date+" ) ORDER BY YK_TYPK.PYDM          ASC  ";
//			try{
//			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//			list = dao.doSqlQuery(sql, parameters);
//			DecimalFormat df = new DecimalFormat("0.00");
//			for (Map<String,Object> map : list) {
//				map.put("KCSL",df.format(map.get("KCSL")));
//			}
//			 records.addAll(list);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));
		BaseDAO dao = new BaseDAO(ctx);
		String CZY;
		try {
			Map<String,Object> map_yk=dao.doLoad("phis.application.sto.schemas.YK_YKLB", yksb);
			if(map_yk==null||map_yk.size()==0){
				throw new PrintException(9000,"当前药库未设置");
			}
			CZY = DictionaryController.instance().get("phis.dictionary.doctor").getText(userid);
			String JGMC = user.getManageUnitName()+map_yk.get("YKMC"); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMdd");
			String ZBRQ = sdf.format(new java.util.Date());
			response.put("CZY", CZY);
			response.put("JGMC", JGMC);
			response.put("ZBRQ", ZBRQ);
			response.put("JZRQ", sdf.format(sdf1.parse(request.get("JZRQ")+"")));
		} catch (ControllerException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (ParseException e) {
			throw new PrintException(9000,"截至日期格式错误");
		} 
	}
}
