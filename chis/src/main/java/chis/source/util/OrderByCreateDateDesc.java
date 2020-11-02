/**
 * @(#)OrderByCreateDate.java Created on 2012-3-27 下午03:48:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.util;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class OrderByCreateDateDesc implements Comparator{
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		Map<String,Object > m1 = (Map<String,Object>)o1;
		Map<String,Object > m2 = (Map<String,Object>)o2;
		Date d1 = (Date)m1.get("createDate");
		Date d2 = (Date)m2.get("createDate");
		if(d1==null || d2==null){
			return 1;
		}
		
		return BSCHISUtil.dateCompare(d2, d1);
	}
}
