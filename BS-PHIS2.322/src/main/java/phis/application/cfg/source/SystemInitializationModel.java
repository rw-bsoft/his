package phis.application.cfg.source;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class SystemInitializationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(SystemInitializationModel.class);

	public SystemInitializationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 系统初始化
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveSystemInit(Map<String, Object> req,
			Map<String, Object> res,Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user =UserRoleToken.getCurrent() ;
		String userId = user.getUserId();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		List<Map<String,Object>> datas = (List<Map<String,Object>>)req.get("body");
		String password = MD5StringUtil.MD5Encode((String)req.get("password"));
		Map<String,Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("USERID", userId);
			parameters.put("PASSWORD", password);
			long count1 = dao.doCount("ctd.account.user.User", "ID=:USERID and PASSWORD=:PASSWORD", parameters);
			if(count1==0){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "输入密码错误!");
			}
			parameters.clear();
			res.put("body",true);
			for(int i = 0 ; i < datas.size() ; i ++){
				Map<String,Object> data = datas.get(i);
				parameters.put("JGID", manaUnitId);
				parameters.put("GROUPID", Long.parseLong(data.get("GROUPID")+""));
				parameters.put("OFFICEID", Long.parseLong(data.get("OFFICEID")+""));
				if("1".equals(data.get("GROUPID")+"")){
					long count = dao.doCount("GY_CSH", "JGID = :JGID and GROUPID = :GROUPID and OFFICEID = :OFFICEID and INIT = 1", parameters);
					if(!(count>0)){
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_THMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YYGH_CIC);
						Map<String,Object> MS_GH_FKXXparameters = new HashMap<String, Object>();
						MS_GH_FKXXparameters.put("JGID", manaUnitId);
						dao.doUpdate("delete from MS_GH_FKXX where SBXH IN (SELECT SBXH FROM MS_GHMX WHERE JGID = :JGID)", MS_GH_FKXXparameters);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_GH_FKXX_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_GHMX);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_GHMX_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_GRMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_GHRB_FKMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_GHRB);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_RBMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_XZMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_SFRB_FKMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_HZRB);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YJ02_CIC);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YJ01_CIC);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YJ02_LS);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YJ01_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_CF02_CIC);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_CF01_CIC);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_CF02_LS);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_CF01_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_SFMX);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_SFMX_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_ZFFP);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_MZXX);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_MZXX_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_FKXX);
						//dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_FKXX_LS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_MZMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_MZHS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YJMX);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_YJHS);
						dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.MS_KSPB);
						dao.doUpdate("UPDATE MS_GHKS set YGRS = 0", null);
						dao.doUpdate("UPDATE MS_YSPB Set YGRS = 0", null);
						int count2 = dao.doUpdate("UPDATE GY_CSH Set INIT = 1 Where JGID = '"+manaUnitId+"' and GROUPID = "+data.get("GROUPID")+" and OFFICEID = "+data.get("OFFICEID"),null);
						if(count2 == 0){
							Map<String,Object> GY_CSH = new HashMap<String, Object>();
							GY_CSH.put("GROUPID", data.get("GROUPID"));
							GY_CSH.put("OFFICEID", data.get("OFFICEID"));
							GY_CSH.put("INIT", 1);
							GY_CSH.put("JGID", manaUnitId);
							dao.doInsert(BSPHISEntryNames.GY_CSH_CFG, GY_CSH, false);
						}
					}
				}else if("2".equals(data.get("GROUPID")+"")){
					long count = dao.doCount("GY_CSH", "JGID = :JGID and GROUPID = :GROUPID and OFFICEID = :OFFICEID and INIT = 1", parameters);
					if(!(count>0)){
						dao.doUpdate("delete from YK_SWYJ where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_YJJG where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_JZJL where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_RK02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_RK01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_CK02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_CK01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_TJJL where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_TJ02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_TJ01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_PD02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_PD01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_JH02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_JH01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_YH02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_YH01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_PZ02 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_PZ01 where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);
						dao.doUpdate("delete from YK_KCFL where JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"), null);

						dao.doSqlUpdate("delete from YK_KCMX a where a.JGID = '"+manaUnitId+"' and EXISTS (SELECT YPXH FROM YK_YPXX b WHERE JGID = '"+manaUnitId+"' AND YKSB = "+data.get("OFFICEID")+" AND a.YPXH = b.YPXH)", null);
						dao.doSqlUpdate("UPDATE YK_CDXX a SET KCSL = 0 , LSJE = 0 , PFJE = 0 , JHJE = 0 WHERE a.JGID = '"+manaUnitId+"' AND EXISTS ( SELECT YPXH FROM YK_YPXX b WHERE JGID = '"+manaUnitId+"' AND YKSB = "+data.get("OFFICEID")+" AND b.YPXH = a.YPXH)",null) ;
						Calendar startc = Calendar.getInstance();
						int year = startc.get(Calendar.YEAR);
						int ll_djhm =year%100*10000+1;
						dao.doUpdate("UPDATE YK_RKFS SET RKDH = "+ll_djhm+" , YSDH = "+ll_djhm+" WHERE JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"),null);
						dao.doUpdate("UPDATE YK_CKFS SET CKDH = "+ll_djhm+" WHERE JGID = '"+manaUnitId+"' and XTSB = "+data.get("OFFICEID"),null);
						dao.doUpdate("UPDATE YK_YKLB SET SYBZ = 1 WHERE JGID = '"+manaUnitId+"' and YKSB = "+data.get("OFFICEID"), null);
						int count2 = dao.doUpdate("UPDATE GY_CSH Set INIT = 1 Where JGID = '"+manaUnitId+"' and GROUPID = "+data.get("GROUPID")+" and OFFICEID = "+data.get("OFFICEID"),null);
						if(count2 == 0){
							Map<String,Object> GY_CSH = new HashMap<String, Object>();
							GY_CSH.put("GROUPID", data.get("GROUPID"));
							GY_CSH.put("OFFICEID", data.get("OFFICEID"));
							GY_CSH.put("INIT", 1);
							GY_CSH.put("JGID", manaUnitId);
							dao.doInsert(BSPHISEntryNames.GY_CSH_CFG, GY_CSH, false);
						}
					}
				}else if("3".equals(data.get("GROUPID")+"")){
					dao.doUpdate("delete from YF_RK02 where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_RK01 where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_CK02 where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_CK01 where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_TJJL where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_MZFYMX where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					//dao.doUpdate("delete from YF_YPXX where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_KCMX where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_RBMX where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_YFRB where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_YJJG where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_JZJL where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_ZYFYMX where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_FYJL where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_DB02 where JGID = '"+manaUnitId+"' and SQYF = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_DB01 where JGID = '"+manaUnitId+"' and SQYF = "+data.get("OFFICEID"), null);
					
					dao.doUpdate("delete from YF_MZ_SFJL where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_YK02_GRLR where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_YK02 where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);
					dao.doUpdate("delete from YF_YK01 where JGID = '"+manaUnitId+"' and YFSB = "+data.get("OFFICEID"), null);

					Calendar startc = Calendar.getInstance();
					int year = startc.get(Calendar.YEAR);
					int ll_djhm = year%100*10000+1;
					dao.doUpdate("UPDATE YF_RKFS Set RKDH = "+ll_djhm+" Where JGID = '"+manaUnitId+"' And YFSB = "+data.get("OFFICEID"),null);
					dao.doUpdate("UPDATE YF_CKFS Set CKDH = "+ll_djhm+" Where JGID = '"+manaUnitId+"' And YFSB = "+data.get("OFFICEID"),null);
					int count = dao.doUpdate("UPDATE GY_CSH Set INIT = 1 Where JGID = '"+manaUnitId+"' and GROUPID = "+data.get("GROUPID")+" and OFFICEID = "+data.get("OFFICEID"),null);
					if(count == 0){
						Map<String,Object> GY_CSH = new HashMap<String, Object>();
						GY_CSH.put("GROUPID", data.get("GROUPID"));
						GY_CSH.put("OFFICEID", data.get("OFFICEID"));
						GY_CSH.put("INIT", 1);
						GY_CSH.put("JGID", manaUnitId);
						dao.doInsert(BSPHISEntryNames.GY_CSH_CFG, GY_CSH, false);
					}
					Map<String,Object> BQ_TJ = new HashMap<String, Object>();
					BQ_TJ.put("JGID", manaUnitId);
					BQ_TJ.put("TJYF", Long.parseLong(data.get("OFFICEID")+""));
					long ll_count = dao.doCount("BQ_TJ02 a,BQ_TJ01 b", "a.TJXH = b.TJXH AND b.JGID = :JGID and b.TJYF = :TJYF", BQ_TJ);
					if(ll_count > 0){
						dao.doUpdate("DELETE FROM BQ_TJ02 b WHERE EXISTS (SELECT TJXH FROM BQ_TJ01 a WHERE a.JGID = :JGID and a.TJYF =:TJYF AND a.TJXH = b.TJXH)", BQ_TJ);
						dao.doUpdate("DELETE FROM BQ_TJ01 WHERE JGID = :JGID and TJYF=:TJYF",BQ_TJ);
					}
				}else if("4".equals(data.get("GROUPID")+"")){
					dao.doUpdate("delete from YS_MZ_PSJL where JGID = '"+manaUnitId+"'", null);
					dao.doUpdate("delete from YS_MZ_JZLS where JGID = '"+manaUnitId+"'", null);
					dao.doUpdate("delete from MS_BRZD where JGID = '"+manaUnitId+"'", null);
					dao.doUpdate("delete from MS_BCJL where JGID = '"+manaUnitId+"'", null);
					int count = dao.doUpdate("UPDATE GY_CSH Set INIT = 1 Where JGID = '"+manaUnitId+"' and GROUPID = "+data.get("GROUPID")+" and OFFICEID = "+data.get("OFFICEID"),null);
					if(count == 0){
						Map<String,Object> GY_CSH = new HashMap<String, Object>();
						GY_CSH.put("GROUPID", data.get("GROUPID"));
						GY_CSH.put("OFFICEID", data.get("OFFICEID"));
						GY_CSH.put("INIT", 1);
						GY_CSH.put("JGID", manaUnitId);
						dao.doInsert(BSPHISEntryNames.GY_CSH_CFG, GY_CSH, false);
					}
				}else if("5".equals(data.get("GROUPID")+"")){
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_BQYZ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.BQ_TYMX_WAR);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.YS_ZY_HZSQ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.YS_ZY_HZYJ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.YS_ZY_HZYQ);
					int count = dao.doUpdate("UPDATE GY_CSH Set INIT = 1 Where JGID = '"+manaUnitId+"' and GROUPID = "+data.get("GROUPID")+" and OFFICEID = "+data.get("OFFICEID"),null);
					if(count == 0){
						Map<String,Object> GY_CSH = new HashMap<String, Object>();
						GY_CSH.put("GROUPID", data.get("GROUPID"));
						GY_CSH.put("OFFICEID", data.get("OFFICEID"));
						GY_CSH.put("INIT", 1);
						GY_CSH.put("JGID", manaUnitId);
						dao.doInsert(BSPHISEntryNames.GY_CSH_CFG, GY_CSH, false);
					}
				}else if("6".equals(data.get("GROUPID")+"")){
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_SRHZ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_FYHZ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_JZHZ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_ZFPJ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_JZXX);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_JSZF);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_JSMX_HOS);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_FKXX);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_ZYJS);
//					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_FYMX_CY);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_FYMX);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_FYMX_JS);
//					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_BQYZ_CY);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_BQYZ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_JKZF);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_TBKK);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_HCMX);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_RYZD_WAR);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_YPGM_WAR);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_BRRY);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_CWTJ);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_YGPJ_HOS);
					dao.doRemove("JGID", manaUnitId, BSPHISEntryNames.ZY_RCJL);
					dao.doUpdate("UPDATE ZY_CWSZ Set ZYH = NULL Where JGID = '"+manaUnitId+"'",null);
					int count = dao.doUpdate("UPDATE GY_CSH Set INIT = 1 Where JGID = '"+manaUnitId+"' and GROUPID = "+data.get("GROUPID")+" and OFFICEID = "+data.get("OFFICEID"),null);
					if(count == 0){
						Map<String,Object> GY_CSH = new HashMap<String, Object>();
						GY_CSH.put("GROUPID", data.get("GROUPID"));
						GY_CSH.put("OFFICEID", data.get("OFFICEID"));
						GY_CSH.put("INIT", 1);
						GY_CSH.put("JGID", manaUnitId);
						dao.doInsert(BSPHISEntryNames.GY_CSH_CFG, GY_CSH, false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("fail to save medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "系统初始化失败.");
		}	catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "系统初始化失败.");
		}
	}
}
