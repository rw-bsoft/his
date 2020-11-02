package ctd.service.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class SimpleQuery extends DBService {
	private static final int DEFAULT_PAGESIZE = 25;
	private static final int DEFAULT_PAGENO = 1;

	// private static Logger LOGGER =
	// LoggerFactory.getLogger(SimpleQuery.class);
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		int code = 200;
		String msg = "Success";
		String schemaId = (String) req.get("schema");
		if (StringUtils.isEmpty(schemaId)) {
			code = 401;
			msg = "SchemaIdMissed";
			res.put(Service.RES_CODE, code);
			res.put(Service.RES_MESSAGE, msg);
			return;
		}
		Schema sc = SchemaController.instance().getSchema(schemaId);
		if (sc == null) {
			code = 402;
			msg = "NoSuchSchema";
			res.put(Service.RES_CODE, code);
			res.put(Service.RES_MESSAGE, msg);
			return;
		}
		doBeforeExec(req, res, ctx);
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		SimpleDAO dao = null;
		try {
			dao = new SimpleDAO(sc, ctx);
			// add by yangl remoteSort
			String sort = null;
			String dir = null;
			if (ContextUtils
					.get(Context.HTTP_REQUEST, HttpServletRequest.class) != null) {
				sort = ContextUtils.get(Context.HTTP_REQUEST,
						HttpServletRequest.class).getParameter("sort");
				dir = ContextUtils.get(Context.HTTP_REQUEST,
						HttpServletRequest.class).getParameter("dir");
			}
			QueryResult qr = dao.find(queryCnd, pageNo, pageSize,
					queryCndsType, sortInfo);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("totalCount", qr.getTotalCount());
			res.put("body", qr.getRecords());
			doAfterExec(req, res, ctx);
			long costTime = qr.getCostTime();
			ctx.put("$P_SQL_COST_TIME", costTime);
		} catch (DataAccessException e) {
			if (AppContextHolder.isDevMode()) {
				e.printStackTrace();
			}
			code = 501;
			msg = "DataAccessException:" + e.getMessage();
			res.put(Service.RES_CODE, code);
			res.put(Service.RES_MESSAGE, msg);
		}
	}

	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
	}

	public void doBeforeExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
	}

}