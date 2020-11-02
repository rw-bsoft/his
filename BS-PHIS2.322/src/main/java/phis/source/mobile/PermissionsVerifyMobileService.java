/**
 * @(#)PermissionsVerifyMobileService.java 创建于 2013年6月24日18:12:35
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package phis.source.mobile;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.base.PhisAspectLogon;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:suny@bsoft.com.cn">suny</a>
 */
public class PermissionsVerifyMobileService extends AbstractActionService
		implements DAOSupportable {

	Logger logger = LoggerFactory
			.getLogger(PermissionsVerifyMobileService.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doFilterPermissions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = new HashMap<String, Object>();
		String YWLB = "2";
		try {
			List<Map<String, Object>> list = dao
					.doQuery(
							"select a.KSDM as KSDM,b.KSMC as KSMC,b.MZKS as MZKS,(select OFFICENAME from SYS_Office where ID=b.MZKS) as MZKSMC,a.MRBZ as MRBZ from GY_QXKZ a,MS_GHKS b where a.KSDM=b.KSDM and a.YGDM='"
									+ user.getUserId()
									+ "' and a.JGID='"
									+ manageUnit
									+ "' and a.YWLB='"
									+ YWLB
									+ "'", null);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("MRBZ") != null
						&& list.get(i).get("MRBZ").toString().equals("1")) {
					long ksdm = (Long) list.get(i).get("MZKS");
					String ksmc = (String) list.get(i).get("MZKSMC");
					body.put("ksdm", ksdm);
					body.put("ksmc", ksmc);
					Long ghks = (Long) list.get(i).get("KSDM");
					int ghrq = -1;
					int zblb = 1;
					Date date = new Date();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					int AM_PM = calendar.get(Calendar.AM_PM);
					if (AM_PM == 0) {

					} else {
						zblb = 2;
					}
					ghrq = calendar.get(Calendar.DAY_OF_WEEK);
					// 科室排班
					String hql = "select a.GHKS as GHKS ,a.JGID as JGID , a.GHRQ as GHRQ , a.ZBLB as ZBLB , a.YYRS as YYRS ,"
							+ " a.JZXH as JZXH  ,a.GHXE as GHXE ,a.YYXE as YYXE ,a.TGBZ as TGBZ ,a.YGRS as YGRS  "
							+ " from MS_KSPB a where "
							+ "a.GHKS="
							+ "'"
							+ ghks
							+ "'"
							+ "  and "
							+ " a.JGID="
							+ "'"
							+ manageUnit
							+ "'"
							+ " and "
							+ "a.GHRQ="
							+ "'"
							+ ghrq
							+ "'"
							+ " and " + "a.ZBLB=" + "'" + zblb + "'";

					List<Map<String, Object>> MS_KSPB = dao.doQuery(hql, null);
					if (MS_KSPB != null && !MS_KSPB.isEmpty()) {
						body.put("kspb", "0");
					} else {
						body.put("kspb", "1");
					}
				}
			}
			List cnd1 = CNDHelper.createSimpleCnd("eq", "PRESCRIBERIGHT", "s",
					"1");
			List cnd2 = CNDHelper.createSimpleCnd("ne", "LOGOFF", "i", 1);
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> sys = dao.doList(cnd, null,
					BSPHISEntryNames.SYS_Personnel);
			for (Map<String, Object> map : sys) {
				String PERSONID = map.get("PERSONID") + "";
				if (PERSONID.equals(user.getUserId())) {
					String PERSONNAME = map.get("PERSONNAME") + "";
					body.put("ysdm", PERSONID);
					body.put("ysmc", PERSONNAME);
				}
			}

			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("科室查询失败，请联系管理员!", e);
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean checkKSPB(Long ksdm, String userId, String jgid) {

		return false;
	}
}
