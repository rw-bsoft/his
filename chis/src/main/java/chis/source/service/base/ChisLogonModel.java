/**
 * @(#)ChisLogonModel.java Created on 2014-6-23 下午4:00:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.service.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class ChisLogonModel {
	private static final Logger logger = LoggerFactory.getLogger(ChisLogonModel.class);
	/**
	 * 
	 * @Description:获取家庭医生助理的 全部家庭（责任）医生(ID)，以 'a','b' 形式返回
	 * @return
	 * @author ChenXianRui 2014-6-23 下午4:13:40
	 * @Modify:
	 */
	public String getFamilyDoctorOfAssistant() {
		StringBuffer fds = new StringBuffer("'"+UserUtil.get(UserUtil.USER_ID)+"'");
		String roleId = UserUtil.get(UserUtil.JOB_ID);
		if ("chis.101".equals(roleId)) {
			String uid = UserUtil.get(UserUtil.USER_ID);
			String hql = new StringBuffer("select fd as fd from ")
					.append(BSCHISEntryNames.REL_RelevanceDoctor)
					.append(" where fda=:fda").toString();
			BaseDAO dao = new BaseDAO();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("fda", uid);
			try {
				List<Map<String, Object>> fdList = dao.doQuery(hql, parameters);
				StringBuffer fdBuffer = new StringBuffer();
				if(fdList != null && fdList.size() > 0){
					for(int i = 0 ,len=fdList.size();i<len;i++){
						Map<String, Object> fdMap = fdList.get(i);
						fdBuffer.append("'"+fdMap.get("fd")+"'");
						if(i < len-1){
							fdBuffer.append(",");
						}
					}
					fds.append(",").append(fdBuffer.toString());
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Get family doctor of the family doctor assistant!",e);
				e.printStackTrace();
			}
		}
		return fds.toString();
	}
	public String getResponsibleDoctorOfAssistant() {
		StringBuffer rds = new StringBuffer("'"+UserUtil.get(UserUtil.USER_ID)+"'");
		String roleId = UserUtil.get(UserUtil.JOB_ID);
		if ("chis.20".equals(roleId)) {
			String uid = UserUtil.get(UserUtil.USER_ID);
			String hql = new StringBuffer("select doctorId as doctorId from ")
					.append(BSCHISEntryNames.REL_ResponsibleDoctor)
					.append(" where assistantId=:assistantId and status='0' ").toString();
			BaseDAO dao = new BaseDAO();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("assistantId", uid);
			try {
				List<Map<String, Object>> fdList = dao.doQuery(hql, parameters);
				StringBuffer fdBuffer = new StringBuffer();
				if(fdList != null && fdList.size() > 0){
					for(int i = 0 ,len=fdList.size();i<len;i++){
						Map<String, Object> fdMap = fdList.get(i);
						fdBuffer.append("'"+fdMap.get("doctorId")+"'");
						if(i < len-1){
							fdBuffer.append(",");
						}
					}
					rds.append(",").append(fdBuffer.toString());
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Get family doctor of the family doctor assistant!",e);
				e.printStackTrace();
			}
		}
		return rds.toString();
	}
}
