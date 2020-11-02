/**
 * @(#)BizSimpleQuery.java Created on 2012-1-16 下午03:59:16
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.control.ControlRunner;
//import ctd.dao.MySimpleDAO;
import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description 用于替换simpleQuery的查询服务.
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class BizSimpleQuery extends SimpleQuery {

	Logger log = LoggerFactory.getLogger(BizSimpleQuery.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.service.core.Service#execute(java.util.Map, java.util.Map,
	 * ctd.util.context.Context)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		List<?> cnd =(List<?>) req.get("cnd");
		if(cnd ==null || cnd.size()==0){
			req.put("cnd", null);
		}
		
		try {
			 int code = 200;
		        String msg = "Success";
		        String schemaId = (String)req.get("schema");
		        if(StringUtils.isEmpty(schemaId))
		        {
		            code = 401;
		            msg = "SchemaIdMissed";
		            res.put("code", Integer.valueOf(code));
		            res.put("msg", msg);
		            return;
		        }
		        Schema sc = SchemaController.instance().getSchema(schemaId);
		        if(sc == null)
		        {
		            code = 402;
		            msg = "NoSuchSchema";
		            res.put("code", Integer.valueOf(code));
		            res.put("msg", msg);
		            return;
		        }
		        doBeforeExec(req, res, ctx);
		        List queryCnd = null;
		        if(req.containsKey("cnd"))
		            queryCnd = (List)req.get("cnd");
		        String queryCndsType = null;
		        if(req.containsKey("queryCndsType"))
		            queryCndsType = (String)req.get("queryCndsType");
		        String sortInfo = null;
		        if(req.containsKey("sortInfo"))
		            sortInfo = (String)req.get("sortInfo");
		        int pageSize = 25;
		        if(req.containsKey("pageSize"))
		            pageSize = ((Integer)req.get("pageSize")).intValue();
		        int pageNo = 1;
		        if(req.containsKey("pageNo"))
		            pageNo = ((Integer)req.get("pageNo")).intValue();
		        SimpleDAO dao = null;
		        try
		        {
		            dao = new SimpleDAO(sc, ctx);
		            QueryResult qr = dao.find(queryCnd, pageNo, pageSize, queryCndsType, sortInfo);
		            res.put("pageSize", Integer.valueOf(pageSize));
		            res.put("pageNo", Integer.valueOf(pageNo));
		            res.put("totalCount", Long.valueOf(qr.getTotalCount()));
		            res.put("body", qr.getRecords());
		            doAfterExec(req, res, ctx);
		            long costTime = qr.getCostTime();
		            ctx.put("$P_SQL_COST_TIME", Long.valueOf(costTime));
		        }
		        catch(DataAccessException e)
		        {
		            if(AppContextHolder.isDevMode())
		                e.printStackTrace();
		            code = 501;
		            msg = (new StringBuilder()).append("DataAccessException:").append(e.getMessage()).toString();
		            res.put("code", Integer.valueOf(code));
		            res.put("msg", msg);
		        }
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String actions =(String) req.get("actions");
		//需要进行权限验证
		if(actions!=null){
			if("".equals(actions)){
				actions=ControlRunner.UPDATE;
			}
			String schema = (String)req.get("schema");
			Schema sc = SchemaController.instance().getSchema(schema);
			String entryName = schema;
			String tableName = sc.getTableName();
			if (tableName != null && !"".equals(tableName)) {
				entryName = tableName;
			}
			List<Map<String,Object>> body = (List<Map<String,Object>>)res.get("body");
			String[] actionArray = actions.split(",");
			try {
				ControlRunner.run(entryName, body, ctx, actionArray);
			} catch (ServiceException e) {
				res.put(Service.RES_CODE,500);
				res.put(Service.RES_MESSAGE,"列表数据验证失败!");
				e.printStackTrace();
			}
		}
	}
}
