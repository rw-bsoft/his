/**
 * @(#)PublicConfigControllor.java 创建于 2011-1-7 下午06:20:22
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.net.rpc.Client;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PublicConfigControllor extends AbstractConfigControllor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.AbstractConfigControllor#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bsoft.hzehr.biz.conf.control.ConfigControllor#isReadOnly()
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> isReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		String cnd = "['gt',['len','regioncode'],['d','6']]";

		int l = 0;
		try {
			HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
			Object[] paras = new Object[] { "area",
			CNDHelper.toListCnd(cnd.toString()), new QueryContext() };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client
					.rpcInvoke(
							AppContextHolder.getConfigServiceId("daoService"),
							"find", paras, header);

			List<Map<String, Object>> result = qr.getItems();
			if (result != null ) {
				l = result.size();
			}
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(moduleId + "_readOnly", l > 0 ? true : false);
		return map;
	}
}
