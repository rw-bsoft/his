/**
 * @(#)MyWorkList.java Created on 2012-6-12 上午11:16:00
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.worklist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.WorkType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.account.accredit.list.AccreditList;
import ctd.account.role.Role;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class MyWorkListService extends AbstractActionService implements
		DAOSupportable {

	private static Log logger = LogFactory.getLog(MyWorkListService.class);

	//获取用户工作任务列表
	protected void doGetMyWorkList(Map<String, Object> req,Map<String, Object> res, BaseDAO dao,Context ctx)
		throws ServiceException, ControllerException {
//		Role role = UserRoleToken.getCurrent().getRole();
//		AccreditList accreditList = role.getAccreditList("workList");
		// @@ 未定义任务。
//		if (accreditList == null || accreditList.containers() == null) {
//			return;
//		}
		List<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
		res.put("body", resBody);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		String userId = UserUtil.get(UserUtil.USER_ID);
		String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		//List<String> workTypes = getWorkTypeFromAccredit(accreditList);
		List<String> workTypes= new ArrayList<String>();
		 workTypes.add("18");
		 workTypes.add("21");
		 workTypes.add("25");
		 workTypes.add("26");
		 workTypes.add("27");
		 workTypes.add("28");
//		if(workTypes==null || workTypes.size()<1) {
//			return;
//		}
		WorkListModel wlm = new WorkListModel(dao);
		for (String workType : workTypes) {
			Map<String, Object> result = null;
			try {
				if(workType.equals(WorkType.MOV_EHR_CONFIRM)
						|| workType.equals(WorkType.MOV_CDH_CONFIRM)
						|| workType.equals(WorkType.MOV_MHC_CONFIRM)
						|| workType.equals(WorkType.MOV_BATCH_CONFIRM)
						|| workType.equals(WorkType.MOV_MANAGEINFO_CONFIRM)
						|| workType.equals(WorkType.HYPERTENSION_YEAR_FIXGROUP)) {
					result = wlm.getWrokList(manaUnitId, workType);
				}else if(manaUnitId.length()>=6 && workType.equals("28")){
					//老年人体检
					result=getOldpeopleHelathCheckWork(manaUnitId,dao);
				}else{
					//result = wlm.getWrokList(userId, manaUnitId, workType);
					result = wlm.getWrokList( manaUnitId, workType);
				}
			} catch (ModelDataOperationException e) {
				e.getStackTrace();
				logger.info("failed to get work list for user [ " + userId
						+ "]");
				throw new ServiceException(e);
			}
			if (result == null || result.size() < 1) {
				continue;
			}
			result.put("workIcon", "<img src='resources/chis/resources/app/desktop/images/homepage/List_ico.gif'/>");
			data.add(result);
		}
		if (data.size() > 0) {
			resBody.addAll(SchemaUtil.setDictionaryMessageForList(data,PUB_WorkList));
		}
	}
	@SuppressWarnings("unchecked")
	protected void doGetMonthPlanReminder(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException, ControllerException {
		Role role = UserRoleToken.getCurrent().getRole();
		AccreditList accreditList = role.getAccreditList("reminderList");
		// @@ 未定义任务。
		if (accreditList == null || accreditList.containers() == null) {
			return;
		}
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Date d = BSCHISUtil.toDate((String) body.get("date")) ;
		Calendar c = Calendar.getInstance();
		if(d!= null){
			c.setTime(d);
		}
		List<Integer> reminderTypes = getReminderTypeFromAccredit(accreditList);
		BSCHISUtil.sort(reminderTypes);
		WorkListModel wlm = new WorkListModel(dao);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("firstDay", BSCHISUtil.getFirstDayOfMonth(c.getTime()));
		map.put("lastDay", BSCHISUtil.getLastDayOfMonth(c.getTime()));
		map.put("currentDay",d);
		Map<Integer,Object> m = wlm.getMonthPlanReminderList(map, reminderTypes);
		res.put("body", m);
	}
	//获取用户任务列表类型
	public List<String> getWorkTypeFromAccredit(AccreditList list) {
		Set<String> keys = list.containers().keySet();
		List<String> workTypes = new ArrayList<String>();
		for (String key : keys) {
			workTypes.add(key);
		}
		return workTypes;
	}
	//获取用户任务列表类型
	public List<Integer> getReminderTypeFromAccredit(AccreditList list) {
		Set<String> keys = list.containers().keySet();
		List<Integer> workTypes = new ArrayList<Integer>();
		for (String key : keys) {
			workTypes.add(Integer.valueOf(key));
		}
		return workTypes;
	}
	//获取老年人体检工作
	public Map<String, Object> getOldpeopleHelathCheckWork(String manaUnitId,BaseDAO dao){
		Map<String,Object> d=new HashMap<String,Object>();
		StringBuffer s=new StringBuffer();
		Calendar c=Calendar.getInstance();
		c.setTime(new Date());
		s.append("select count(1) as COUNT from EHR_HealthRecord a,MPI_DemographicInfo b where a.empiId=b.empiId")
		.append(" and a.manaUnitId like '"+manaUnitId+"%' and floor(months_between(SYSDATE,b.birthday)/12)>=65")
		.append(" and not exists (select 1 from HC_HealthCheck c where a.empiId=c.empiId and to_char(c.checkdate,'yyyy')=:year)");
		Map<String,Object>p=new HashMap<String,Object>();
		p.put("year",c.get(Calendar.YEAR)+"");
		try{
			d.put("count",dao.doSqlLoad(s.toString(),p).get("COUNT"));
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		d.put("workType",28);
		return d;
	}
}
