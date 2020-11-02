/**
 * @(#)ConsultationApplyModel.java Created on 2013-5-2 下午9:06:09
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class ConsultationApplyModel implements BSPHISEntryNames {
	private BaseDAO dao;

	public ConsultationApplyModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取病人信息
	 * 
	 * @param zyh
	 * @param jgid
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getBRInfo(long zyh, String jgid)
			throws ModelDataOperationException {
		String hql = "SELECT a.ZYHM as ZYHM,a.BRXM as BRXM,a.BRKS as BRKS,a.BRXB as BRXB,a.BRCH as BRCH,a.ZYH as ZYH,b.KSDM as KSDM "
				+ " FROM ZY_BRRY a, ZY_CWSZ b "
				+ " WHERE a.BRCH = b.BRCH AND a.JGID = b.JGID AND a.ZYH = :al_zyh and a.JGID = :al_jgid ";
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("al_zyh", zyh);
		paraMap.put("al_jgid", jgid);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取病人信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 获取病区信息
	 * 
	 * @Description:
	 * @param jgid
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-3 上午9:50:10
	 * @Modify:
	 */
	public List<Map<String, Object>> getBQInfo(String jgid)
			throws ModelDataOperationException {
		String hql = "SELECT a.OFFICENAME as OFFICENAME, a.PYCODE as PYCODE, a.ID as ID, a.PARENTID as PARENTID "
				+ " FROM SYS_Office a "
				+ " WHERE a.HOSPITALAREA = '1' AND a.ORGANIZCODE = :al_jgid1 "
				+ " AND a.ID NOT IN (SELECT b.PARENTID as PARENTID FROM SYS_Office b WHERE b.ID <> b.PARENTID AND b.ORGANIZCODE = :al_jgid2)";
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("al_jgid1", jgid);
		paraMap.put("al_jgid2", jgid);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取病区信息失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:更新会诊科室
	 * @param HZKS
	 * @param ZYH
	 * @param JGID
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-4 下午5:11:46
	 * @Modify:
	 */
	public void updateConsApply(long HZKS, long ZYH, String JGID)
			throws ModelDataOperationException {
		String hql = "UPDATE ZY_BRRY SET HZKS=:ll_bq1 WHERE ZYH=:ll_zyh and JGID = :gl_jgid";
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("ll_bq1", HZKS);
		paraMap.put("ll_zyh", ZYH);
		paraMap.put("gl_jgid", JGID);
		try {
			dao.doUpdate(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新会诊科室失败！", e);
		}
	}
}
