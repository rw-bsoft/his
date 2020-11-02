package platform.dataset.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

public class ElementAndGroupQuery extends SimpleQuery {
	
	private static Pattern pattern = Pattern.compile("%([^%]+)%");
	
	@Override
	public void doBeforeExec(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ServiceException {
		//true：不属于任何组的数据元
		Boolean withOutInGroup = (Boolean) req.get("withOutInGroup");
		if(Boolean.TRUE.equals(withOutInGroup)){
			List<?> cnd = (List<?>) req.get("cnd");
			List<?> c1 = JSONUtils.parse("['isNull',['$','a.DataGroup']]", List.class);
			List<Object> finalCnd = new ArrayList<Object>();
			finalCnd.add("and");
			finalCnd.add(cnd);
			finalCnd.add(c1);
			req.put("cnd", finalCnd);
		}else{
			Integer gId = Integer.valueOf(String.valueOf(req.get("gId")));
			List<?> cnd = (List<?>) req.get("cnd");
			List<?> c1 = JSONUtils.parse("['eq',['$','a.DataGroup'],['i',"+gId+"]]", List.class);
			List<Object> finalCnd = new ArrayList<Object>();
			finalCnd.add("and");
			finalCnd.add(cnd);
			finalCnd.add(c1);
			req.put("cnd", finalCnd);
		}
	}
	
	@Override
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ServiceException {
		if((Integer)res.get(Service.RES_CODE) == 200){
			long totalCount = (Long) res.get("totalCount");
			List<Map<String, Object>> body = (List<Map<String, Object>>) res.get("body");
			for(Map<String, Object> m:body){
				if(StringUtils.isEmpty((String) m.get("CustomIdentify"))){
					m.put("CustomIdentify", m.get("bCustomIdentify"));
				}
				if(StringUtils.isEmpty((String) m.get("DName"))){
					m.put("DName", m.get("bDName"));
				}
				if(StringUtils.isEmpty((String) m.get("DComment"))){
					m.put("DComment", m.get("bDComment"));
				}
			}
			Boolean withOutInGroup = (Boolean) req.get("withOutInGroup");
			String c = null;
			try {
				List<?> cnd = (List<?>) req.get("cnd");
				String groupWhere = ExpRunner.toString(cnd, ctx);
				Matcher matcher = pattern.matcher(groupWhere);
				if(matcher.find()){
					c = matcher.group(1);
					c = "%"+c+"%";
				}
			} catch (ExpException e1) {
				throw new ServiceException(e1);
			}
			//查询出对应的数据组
			if(Boolean.TRUE.equals(withOutInGroup)){
				Integer setId = Integer.valueOf(String.valueOf(req.get("setId")));
				StringBuilder hql = new StringBuilder("select distinct a.DataGroupId,a.GroupIdentify,a.DName,a.ParentGroupId ")
					.append(" from RES_DataGroup a,RES_DataSetContent b where a.DataGroupId=b.DataGroup")
					.append(" and b.DataSetId=:setId");
				if(!S.isEmpty(c)){
					hql.append(" and (a.GroupIdentify like :gi or a.DName like :dn)");
				}
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				Query q = ss.createQuery(hql.toString());
				q.setInteger("setId", setId);
				if(!S.isEmpty(c)){
					q.setString("gi", c);
					q.setString("dn", c);
				}
				List<Object[]> result = q.list();
				for(Object[] r:result){
					Object pg = r[3];
					//加入第一层数据组
					if(pg == null){
						Map<String, Object> m = new HashMap<String, Object>();
						m.put("DataGroupId", r[0]);
						m.put("DataSetId", setId);
						m.put("CustomIdentify", r[1]);
						m.put("DName", r[2]);
						body.add(0, m);
					}
				}
				// 把最顶层的数据组加入到结果中
				for(Object[] r:result){
					Object pg = r[3];
					if(pg != null){
						addGroup(body, (Integer)pg, ss, setId);
					}
				}
				res.put("totalCount", body.size());
			}else{
				Integer setId = Integer.valueOf(String.valueOf(req.get("setId")));
				Integer gId = Integer.valueOf(String.valueOf(req.get("gId")));
				StringBuilder hql = new StringBuilder("select a.DataGroupId,a.GroupIdentify,a.DName from RES_DataGroup a ")
					.append("where a.ParentGroupId = :ParentGroupId");
				if(!S.isEmpty(c)){
					hql.append(" and (a.GroupIdentify like :gi or a.DName like :dn)");
				}
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				Query q = ss.createQuery(hql.toString());
				q.setInteger("ParentGroupId", gId);
				if(!S.isEmpty(c)){
					q.setString("gi", c);
					q.setString("dn", c);
				}
				List<Object[]> result = q.list();
				for(Object[] r:result){
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("DataGroupId", r[0]);
					m.put("DataSetId", setId);
					m.put("CustomIdentify", r[1]);
					m.put("DName", r[2]);
					body.add(0, m);
				}
				res.put("totalCount", body.size());
			}
		}
	}
	
	private void addGroup(List<Map<String, Object>> body, Integer gId, Session ss, Integer setId){
		Query q = ss.createQuery("select a.DataGroupId,a.GroupIdentify,a.DName,a.ParentGroupId from RES_DataGroup a where a.DataGroupId = ?");
		q.setInteger(0, gId);
		Object[] r = (Object[]) q.uniqueResult();
		if(r != null){
			if(r[3] != null){
				addGroup(body, (Integer)r[3], ss, setId);
			}else{
				boolean add = true;
				for(Map<String, Object> rr:body){
					if(r[0].equals(rr.get("DataGroupId"))){
						add = false;
						break;
					}
				}
				if(add){
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("DataGroupId", r[0]);
					m.put("DataSetId", setId);
					m.put("CustomIdentify", r[1]);
					m.put("DName", r[2]);
					body.add(0, m);
				}
			}
		}
	}
}
