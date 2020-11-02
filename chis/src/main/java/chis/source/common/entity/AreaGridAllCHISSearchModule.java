/**
 * @(#)MedicineAllCHISSearchModule.java Created on 2015-1-20 上午11:22:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.common.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class AreaGridAllCHISSearchModule extends AbstractSearchModule {
	/**
	 * 公卫用实现药品查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken token = UserRoleToken.getCurrent();
		String regionCode=token.getRegionCode();
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toLowerCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String strFilterType = req.get("filterType").toString();//过滤条件，默认为bottom 子节点
		try {
			String isFamily=" ";
			if(!strFilterType.equals("bottom"))
			{
				isFamily+=" and a.isFamily='1' ";				
			}
			String isRegionCode=" ";
			if(regionCode!=null)
			{
				isRegionCode=" and a.regionCode like '"+regionCode+"%'";
			}
			String hql = "select DISTINCT new chis.source.common.entity.AreaGrid(a.regionName,a.regionCode) from EHR_AreaGridSearch a where a.isBottom='y' "+isFamily+" and substr(isFamily,0,1) not in ('b','c','d','e') and a.pyCode"
					+ " LIKE '"
					+ searchText
					+ "%' "+isRegionCode+" order by length(a.regionName),a.regionCode";
			String hql_count = "select count(a.regionCode) from EHR_AreaGridSearch a where a.isBottom='y' "+isFamily+" and substr(isFamily,0,1) not in ('b','c','d','e') and a.pyCode"
				    + " LIKE '" + searchText + "%' "+isRegionCode;
			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<AreaGrid> AreaGrids = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();

			String treeHql= "select regionCode,regionName,substr(isFamily,0,1) isFamily"
						   +" from EHR_AreaGrid where substr(isFamily,0,1) in ('d','e','f')"
						   +" start with regionCode=:regionCode"
						   +" connect by prior parentCode=regionCode "
						   +" order by length(regionCode)";
			
			for (int i = 0; i < AreaGrids.size(); i++) {
				AreaGrid ag=AreaGrids.get(i);
				Map tempMap=new HashMap();
				Query q=ss.createSQLQuery(treeHql);
				q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				q.setParameter("regionCode", ag.getRegionCode());
				List<Map> arealist=q.list();
				
				if(arealist.size()==3){
				ag.setParentCode3((String)(arealist.get(0)).get("REGIONCODE"));
				ag.setParentCode4((String)(arealist.get(1)).get("REGIONCODE"));
				ag.setParentCode5((String)(arealist.get(2)).get("REGIONCODE"));
				
				ag.setParentName3((String)(arealist.get(0)).get("REGIONNAME"));
				ag.setParentName4((String)(arealist.get(1)).get("REGIONNAME"));
				ag.setParentName5((String)(arealist.get(2)).get("REGIONNAME"));
				}
				else if(arealist.size()==2){
					ag.setParentCode3((String)(arealist.get(0)).get("REGIONCODE"));
					ag.setParentCode4((String)(arealist.get(1)).get("REGIONCODE"));
					
					ag.setParentName3((String)(arealist.get(0)).get("REGIONNAME"));
					ag.setParentName4((String)(arealist.get(1)).get("REGIONNAME"));
				}
				else if(arealist.size()==1){
					ag.setParentCode3((String)(arealist.get(0)).get("REGIONCODE"));
					ag.setParentName3((String)(arealist.get(0)).get("REGIONNAME"));
				}
				ag.setNumKey((i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("ags", JSONUtil.ConvertObjToMapList(AreaGrids));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
