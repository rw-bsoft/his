package phis.application.plw.source;

import com.bsoft.mpi.util.CndHelper;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import ctd.validator.Validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.ClinicChargesDiscount;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: Fengld
 * @Date: 2019/3/4 15:57
 * @Description: 家医签约实现层
 */
public class PlwModel {
    protected BaseDAO dao;
    protected Logger logger = LoggerFactory.getLogger(PlwModel.class);


    public PlwModel(BaseDAO dao) {
        this.dao = dao;
    }



	public List<Map<String, Object>> queryIsPLW(Map<String, Object> req)
			throws NumberFormatException, ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String oneYjxh = body.get("oneYjxh").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> plwbrda = null;
		params.put("YJXH", oneYjxh);		
		String sql = "select b.YJXH as YJXH, a.YCFJM as YCFJM "
				+ "from MS_BCJL a "
				+ "left join MS_YJ01 b on a.JZXH = b.JZXH "
				+ "where a.JZXH in (select JZXH from MS_YJ01 where YJXH = :YJXH) and ZFPB <> 1";
		
		plwbrda = dao.doSqlQuery(sql, params);		
		return plwbrda;
    }
	
	public Map<String,Object> queryLastRecord(Map<String, Object> req, Map<String, Object> res,
            BaseDAO dao, Context ctx) throws ParseException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
		params.put("BRID", body.get("BRID"));
		String sql = "select SFRQ as SFRQ from (select * from MS_MZXX where YCFJMJE > 0 and BRID = :BRID and ZFPB <> 1 order by SFRQ desc) where rownum = 1";
		try {
			resList = dao.doSqlQuery(sql, params);
			if(resList.size() > 0){
				if(resList.get(0).get("SFRQ") != null){
					Date today = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar lastCldr = Calendar.getInstance();
					Calendar nowCldr = Calendar.getInstance();
					lastCldr.setTime(sdf.parse(resList.get(0).get("SFRQ").toString()));
					nowCldr.setTime(today);
					int month = (nowCldr.get(Calendar.YEAR) - lastCldr.get(Calendar.YEAR)) * 12;
					int result = nowCldr.get(Calendar.MONTH) - lastCldr.get(Calendar.MONTH);
					int diff = Math.abs(month + result);				
					resList.get(0).put("DIFF", diff);
					res.put("body", resList.get(0));
				}			
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return res;
	}
}
