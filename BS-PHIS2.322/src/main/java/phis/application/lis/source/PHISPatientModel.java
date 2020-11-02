package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.gp.source.GeneralPractitionersModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.utils.CNDHelper;
import ctd.util.context.Context;

/**
 * @description 病人信息相关操作Model
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class PHISPatientModel extends PHISBaseModel {
	
	protected Logger logger = LoggerFactory.getLogger(PHISPatientModel.class);
	protected BaseDAO dao;
	/**
	 * 构造方法
	 * @param ctx
	 */
	public PHISPatientModel(Context ctx) {
		super(ctx);
		dao = new BaseDAO(ctx);
	}

	/**
	 * 获取病人信息
	 * @param map
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> getPersonData(Map<String, Object> map)
			throws ModelOperationException {
		String al_yjlb = (String) map.get("al_yjlb");
		String al_brsy = (String) map.get("al_brsy");
		String al_jgid = (String) map.get("al_jgid");
		Map<String, Object> res = new HashMap<String, Object>();
		
		try {
			List<?> cnd ;
			List<Map<String,Object>> list ;
			if(al_yjlb.equals("1")){//门诊
				  cnd = CNDHelper.createSimpleCnd("eq", "BRID", "s", al_brsy);
		    	  list = dao.doQuery(cnd, "BRID",BSPHISEntryNames.MS_BRDA );		    	  
			}else{//住院
				  cnd = CNDHelper.createSimpleCnd("eq", "ZYH", "s", al_brsy);
		    	  list = dao.doQuery(cnd, "ZYH",BSPHISEntryNames.ZY_BRRY );
			}
			if(list.size() == 0){
	    		 throw new ModelOperationException(" query patient 0 reocord with id:"+al_brsy);
	    	}
			res = list.get(0);

			/**************************begin 2019-07-16 zhaojian 家医签约病人检验开单增加签约标识*************************/
			try {
					Map<String, Object> parmap = new HashMap<String, Object>();
					Map<String, Object> body = new HashMap<String, Object>();
					if(al_yjlb.equals("1")){//门诊
						body.put("EMPIID", res.get("EMPIID"));
					}else{//住院BRID
						  cnd = CNDHelper.createSimpleCnd("eq", "BRID", "s", res.get("BRID"));
						  Map<String,Object> brxx = dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA );
						  body.put("EMPIID", brxx.get("EMPIID"));
					}
					parmap.put("body", body);
					List<Map<String, Object>> gplist = new GeneralPractitionersModel(dao).queryGpDetil(parmap);
					List<Map<String, Object>> tempgplist = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> gpmap : gplist){
						Map<String, Object> tempmap = new HashMap<String, Object>();
						tempmap.put("FYXH",gpmap.get("FYXH"));
						tempmap.put("FYMC",gpmap.get("FYMC"));
						tempgplist.add(tempmap);
					}
					res.put("gpxmlist", tempgplist);
			} catch (Exception e) {
			}
			/**************************end 2019-07-16 zhaojian 家医签约病人检验开单增加签约标识*************************/
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelOperationException("Get person data failed.", e);
		}
	}

	/**
	 * 门诊病人信息修改
	 * @param op "create"表示新建,"update"表示修改
	 * @param brxx
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> updateClinicPatientDate(String op,
			Map<String, Object> brxx) throws ModelOperationException {
		if(op.equalsIgnoreCase("create")){//新建病人信息
			//新建操作...
		}else if(op.equalsIgnoreCase("update")){//修改病人信息
			//修改操作...
		}
		return null;
	}

	
	/**
	 * 住院病人信息修改
	 * @param op "create"表示新建,"update"表示修改
	 * @param brxx
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> updateInhospPatientDate(String op,
			Map<String, Object> brxx) throws ModelOperationException {
		if(op.equalsIgnoreCase("create")){//新建病人信息
			//新建操作...
		}else if(op.equalsIgnoreCase("update")){//修改病人信息
			//修改操作...
		}
		return null;
	}
}
