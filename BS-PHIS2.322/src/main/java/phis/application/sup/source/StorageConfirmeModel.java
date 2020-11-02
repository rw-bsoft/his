package phis.application.sup.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class StorageConfirmeModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(StorageConfirmeModel.class);
	public StorageConfirmeModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	public void doSaveCommit(List<Map<String, Object>> body,Context ctx)throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			String KFXH = user.getProperty("treasuryId").toString();
			long al_lzfs = Long.parseLong(body.get(0).get("LZFS")+"");
			long DJXH = Long.parseLong(body.get(0).get("DJXH")+"");
			
		    for (int i = 0; i < body.size(); i++) {
		    	if(body.get(i).get("WZXH") == null || body.get(i).get("WZXH") ==""){
		    		throw new RuntimeException("物资序号不能为空！");
		    	}
				long WZXH = Long.parseLong(body.get(i).get("WZXH")+"");
		    	
				Map<String, Object> perMap = new HashMap<String, Object>();
				perMap.put("WZXH", WZXH);
				perMap.put("JGID", JGID);
				perMap.put("KFXH", Integer.parseInt(KFXH));
				
				long l = dao.doCount("WL_EJJK", "(KFXH=:KFXH or (JGID =:JGID and KSDM = 0) or (KFXH=0)) and WZXH =:WZXH", perMap);
				if (l<1) {
					throw new ModelDataOperationException(9000, "当前库房未找到["+body.get(i).get("WZMC")+"]物资信息");
				}
			}
		    boolean back = true ;
		    if (body.size()>0) {
		    	back =  BSPHISUtil.Uf_access(body, al_lzfs, dao, ctx);
			}
			if (!back) {
			  throw new RuntimeException("确认失败！");
			}else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("DJXH", DJXH);
				map.put("QRBZ", 1);
				map.put("QRRK", al_lzfs);
				map.put("QRRQ", new Date());
				map.put("QRGH", user.getProperty("satffId"));
				dao.doSave("update", BSPHISEntryNames.WL_CK01, map,false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		   }catch (RuntimeException e) {
			   e.printStackTrace();
			   logger.error("Save failed.", e);
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		} catch (PersistentDataOperationException e) {
			 logger.error("Save failed.", e);
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		} catch (ValidateException e) {
			 logger.error("Save failed.", e);
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}
}
