/**
 * @(#)ZHTJPeopleInfoModel.java Created on 2014-3-7 下午1:57:11
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.phsa.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class PHSAPeopleInfoModel {

	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PHSAPeopleInfoModel.class);

	public PHSAPeopleInfoModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> queryPeopleInfo(String BRLB, List queryCnd,
			String queryCndsType, String sortInfo, int pageSize, int pageNo,
			String schemaId) throws ModelDataOperationException {
		String sql = "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if ("1".equals(BRLB)) {
				sql = "select a.BRID as BRID,a.EMPIID as EMPIID,min(b.JZXH) as JZXH,a.MZHM as MZHM, "
						+ "a.BRXZ as BRXZ,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY, c.WORKPLACE as DWMC, "
						+ " a.JDSJ as JDSJ "
						+ "from MS_BRDA a,YS_MZ_JZLS b,MPI_DEMOGRAPHICINFO c "
						+ "where a.BRID=b.BRBH(+)  and  C.EMPIID=A.EMPIID and "
						+ ExpressionProcessor.instance().toString(queryCnd)
						+ " group by a.BRID,a.EMPIID,a.MZHM, "
						+ "a.BRXZ,a.BRXM,a.BRXB,a.CSNY,c.WORKPLACE,a.JDSJ order by a.MZHM";
			} else if ("2".equals(BRLB)) {
				sql = "select a.ZYH as ZYH,b.BRID as BRID,a.BRBQ as BRBQ,b.EMPIID as EMPIID,a.ZYHM as ZYHM,a.BAHM as BAHM,"
						+ "b.BRXM as BRXM,b.MZHM as MZHM,b.BRXZ as BRXZ,b.BRXB as BRXB,"
						+ "b.CSNY as CSNY,b.DWMC as GZDW,a.BRKS as BRKS, a.BRCH as BRCH, "
						+ "a.RYRQ as RYRQ,a.CYRQ as CYRQ,a.CYPB as CYPB,b.SFZH as SFZH,"
						+ "b.LXDH as LXDH, b.YBKH as YBKH from ZY_BRRY a,MS_BRDA b where a.BRID=b.BRID and "
						+ ExpressionProcessor.instance().toString(queryCnd)
						+ " order by a.ZYHM";
			}
//			param.put("JGID", manageUnit);
			List<Map<String, Object>> body1 = dao.doSqlQuery(sql, param);
			param.put("first", pageSize * (pageNo - 1));
			param.put("max", pageSize);
			List<Map<String, Object>> body2 = dao.doSqlQuery(sql, param);
			SchemaUtil.setDictionaryMassageForList(body2, schemaId);
			result.put("totalCount", body1.size());
			result.put("body", body2);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息列表查询操作失败");
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息列表查询操作失败");
		}
		return result;
	}

	public String checkHasHealthRecord(String empiId)
			throws ModelDataOperationException {
		String sql = "select empiId as empiId from EHR_HealthRecord"
				+ " where empiId=:empiId";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		String flag = "0";
		try {
			List<Map<String, Object>> body = dao.doSqlQuery(sql, param);
			if (body != null && body.size() > 0) {
				flag = "1";
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查是否存在健康档案失败");
		}
		return flag;
	}

	public void getMZZYXX(String empiId, Map<String, Object> res) throws ModelDataOperationException {
		List cnd1=CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		String JGID=UserRoleToken.getCurrent().getManageUnitId();
		try {
			List<Map<String, Object>> list1 = dao.doList(cnd1, "", BSPHISEntryNames.MS_BRDA);
			if(list1!=null&&list1.size()>0){
				String BRID=(String) list1.get(0).get("BRID");
				List cnd2=CNDHelper.createSimpleCnd("eq", "BRID", "s", BRID);
				List cnd3=CNDHelper.createSimpleCnd("eq", "JGID", "s", JGID);
				List cnd4=CNDHelper.createArrayCnd("and", cnd2, cnd3);
				List<Map<String, Object>> list2 = dao.doList(cnd4, "", BSPHISEntryNames.ZY_BRRY);
				if(list2!=null&&list2.size()>0){
					res.put("ZYH", list2.get(0).get("ZYH"));
				}
				cnd2=CNDHelper.createSimpleCnd("eq", "a.BRBH", "s", BRID);
				cnd3=CNDHelper.createSimpleCnd("eq", "a.JGID", "s", JGID);
				cnd4=CNDHelper.createArrayCnd("and", cnd2, cnd3);
				List<Map<String, Object>> list3 = dao.doList(cnd4, "", BSPHISEntryNames.YS_MZ_JZLS);
				if(list3!=null&&list3.size()>0){
					res.put("JZXH", list3.get(0).get("JZXH"));
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询住院号和就诊序号失败");
		}
		
	}
}
