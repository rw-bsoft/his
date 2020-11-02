//package chis.source.admin;
//
//import java.util.Map;
//
//import org.dom4j.Document;
//
//import ctd.account.user.User;
//import ctd.account.user.UserController;
//import ctd.dao.exception.InsertDataAccessException;
//import ctd.util.context.Context;
//
//public class BschisUserConfig extends UserConfig {
//
//public void doRemove(String cmd, Map<String, Object> req, Map<String, Object> res,Context ctx){
//		
//		Map<String,Object> rec = checkData(req,res);
//		if(rec == null){
//			return;
//		}	
//		String id = (String)rec.get(DatabaseUserLoader.fieldUserId);
//		User user = (User)ctx.get("user.instance");
//		if(id.equals(user.getId())){
//			int code = 405;
//			String msg = "当前用户不能删除";
//			CodeTool.setCodeToResponse(res, code, msg);
//			return;
//		}
//		
//	//更新mongodb	
//		Document doc = DatabaseUserLoader.getUserDoc(id);
//		if(doc == null){
//			int code = 503;
//			String msg = "user["+id+"] not found";
//			CodeTool.setCodeToResponse(res, code, msg);
//			return;
//		}
//		String Sfver = doc.getRootElement().attributeValue("version");
//		long fver = Long.valueOf(Sfver);
//		doc.getRootElement().addAttribute("version", String.valueOf(fver-1));	//版本降低	
//		UserController.instance().reload(id);
//
//	//删除数据	
//		try {
//			BschisDatabaseUserLoader.removeUser(rec, ctx);
//		} catch (InsertDataAccessException e) {
//			int code = 501;
//			String msg = "DataAccessException:" + e.getMessage();
//			CodeTool.setCodeToResponse(res, code, msg);
//		}
//	}
//}
