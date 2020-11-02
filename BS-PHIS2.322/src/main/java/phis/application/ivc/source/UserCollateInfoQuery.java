package phis.application.ivc.source;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.Dictionary;
import ctd.util.context.Context;

public class UserCollateInfoQuery extends AbstractActionService implements
		DAOSupportable {

	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(UserCollateInfoQuery.class);

	public void doGetCollateInfoList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		// String sql = "select a.JGID   as JGID, "
		// + "a.YGDM   as YGDM,"
		// + "a.YGBH   as YGBH, "
		// + "a.YGXM   as YGXM,"
		// + "a.YGXB  as YGXB, "
		// + "b.USERID  as USERID, "
		// + "b.USERNAME as USERNAME,"
		// + "b.PASSWORD   as PASSWORD,"
		// + "b.MANAUNITID   as MANAUNITID, "
		// + "b.MANAUNITID_TEXT as MANAUNITID_TEXT, "
		// + "b.JOBID  as JOBID, "
		// + "b.JOBID_TEXT  as JOBID_TEXT "
		// + "from SYS_Personnel a left join SYS_USERCOLLATE b "
		// + "on b.YGDM = a.YGDM "
		// + "where (a.JGID like '"
		// + req.get("nodId") + "%')";
		String sql = "select b.manaUnitId as JGID,a.userId as YGDM,a.loginName as YGBH,a.userName as YGXM,a.gender as YGXB,c.USERID as USERID,c.USERNAME as USERNAME,c.PASSWORD as PASSWORD,c.MANAUNITID as MANAUNITID,c.MANAUNITID_TEXT as MANAUNITID_TEXT,c.JOBID as JOBID,c.JOBID_TEXT as JOBID_TEXT from SYS_USERS a,SYS_USERPROP b left join SYS_USERCOLLATE c on c.YGDM = b.userId and c.JGID = b.manaUnitId where a.userId=b.userId and (b.manaUnitId like '"
				+ req.get("nodId") + "%')";
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, null);
			Dictionary dic_manageUnit = DictionaryController.instance().get(
					"manageUnit");
			Dictionary dic_gender;
			dic_gender = DictionaryController.instance().get("gender");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put(
						"JGID_text",
						dic_manageUnit.getText(list.get(i).get("JGID")
								.toString()));
				list.get(i).put("YGXB_text",
						dic_gender.getText(list.get(i).get("YGXB").toString()));
			}
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
