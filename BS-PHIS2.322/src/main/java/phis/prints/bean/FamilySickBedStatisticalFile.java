package phis.prints.bean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
/**
 * 家庭病床建床情况统计
 * @author Bollen
 *
 */
public class FamilySickBedStatisticalFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid=user.getManageUnitId();
		String dateFrom = request.get("dateFrom") + "";
		String dateTo = request.get("dateTo") + "";
		StringBuffer hql =new StringBuffer();
		hql.append("select rownum as XH, ZYHM as JCH, BRXM as BRXM,to_number(to_char(SYSDATE,'yyyy')) - to_number(to_char(CSNY,'yyyy'))  as NL,  jczd as JBZD, ksrq as KSRQ,jsrq as CCRQ, trunc(jsrq) -  trunc(ksrq) as JCTS,decode(jclx,1,'治疗型',2,'康复型',3,'舒缓照顾型') as JCLX, b.personname as ZRYS,c.personname as zrhs from jc_brry a left join sys_personnel b on a.zrys=b.personid left join sys_personnel c on a.zrhs=c.personid where   a.JGID=:jgid   and to_char(ksrq,'yyyy-mm-dd')>=:begin and to_char(ksrq,'yyyy-mm-dd')<=:end ");
		Map<String,Object> map_par_hql=new HashMap<String,Object>();
		map_par_hql.put("jgid", jgid);
		map_par_hql.put("begin", dateFrom);
		map_par_hql.put("end", dateTo);
		List<Map<String, Object>> ret;
		try {
			ret = dao.doSqlQuery(hql.toString(), map_par_hql);
		if(ret==null||ret.size()==0){
			return;
		}
		records.addAll(ret);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("TITLE", jgname+"家庭病床建床情况统计");
		String TJSJFW = request
				.get("dateFrom")
				.toString()
				.substring(0, 10)
				.replaceFirst("-", ".")
				.replaceFirst("-", ".")
				.concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10)
						.replaceFirst("-", ".").replaceFirst("-", "."));
		response.put("TJSJFW", TJSJFW);				
	}
	public long parseLong(Object o) {
		if (o == null || "null".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
	public int parseInt(Object o) {
		if (o == null || "null".equals(o)) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
