package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;

public class ConfigConsociateControlModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigConsociateControlModel.class);

	public ConfigConsociateControlModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doSimpleQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();

		StringBuffer sql = new StringBuffer(
				"select personId as personId,personName as personName," +
				"pyCode as pyCode,officeCode as officeCode,organizCode as JGID  " +
				"from SYS_Personnel a " +
				"where personId in (select userId FROM BASE_UserRoles " +
				"WHERE MANAGEUNITID = :JGID and domain=:domain and logoff='0')");

		List<Object> cnd = null;
		if (req.containsKey("cnd")) {
			cnd = (List<Object>) req.get("cnd");
		}

		try {
			if (cnd != null) {
				String c = ExpressionProcessor.instance().toString(cnd);
				sql.append(" and " + c);
			}

		} catch (ExpException e1) {
			e1.printStackTrace();
		}

		sql.append(" order by a.personId");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", JGID);
//		parameters.put("REF_JGID", user.getManageUnit().getRef());
		parameters.put("domain", user.getDomain());
		String schema = (String) req.get("schema");

		try {
			List<Map<String, Object>> body = dao.doSqlQuery(sql.toString(),
					parameters);
			body = SchemaUtil.setDictionaryMassageForList(body, schema);
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}
}