/**
 * @(#)PatientDepartmentDailyFile.java Created on 2013-8-9 下午5:12:18
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class FsbPatientDepartmentDailyFile implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String jrxml = (String) request.get("jrxml");
		UserRoleToken user = UserRoleToken.getCurrent();
		String title = user.getManageUnit().getName();
		response.put("title", title);
		response.put("CXRQ", request.get("jzrq").toString().substring(0, 10)
				.replaceFirst("-", "年").replaceFirst("-", "月").concat("日"));
		boolean hasRecordMore = checkRecordCount(request, ctx);
		request.put("hasRecordMore", hasRecordMore);
		try {
			if (jrxml.equals("phis.prints.jrxml.FsbAccountsDetail")) {
				getAccountsDetailPara(request, response, ctx);
			} else if (jrxml.equals("phis.prints.jrxml.FsbDeliveryDetail")) {
				getDeliveryDetailPara(request, response, ctx);
			} else if (jrxml.equals("phis.prints.jrxml.FsbRefundDetail")) {
				getRefundDetailPara(request, response, ctx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getRefundDetailPara(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PersistentDataOperationException, ParseException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId()+"";
		String JGID = user.getManageUnit().getId();
		String JZRQ = (String) request.get("jzrq");
		String JZBS = (String) request.get("jzbs");
		String sql = "SELECT  sum(JC_TBKK.JKJE) as JKJE_ALL "
				+ "FROM JC_TBKK JC_TBKK,JC_BRRY JC_BRRY,GY_FKFS GY_FKFS,JC_JCJS JC_JCJS "
				+ "WHERE ( JC_TBKK.ZYH  = JC_BRRY.ZYH ) AND "
				+ " (JC_BRRY.ZYH = JC_JCJS.ZYH) AND "
				+ " (JC_TBKK.JSCS = JC_JCJS.JSCS) AND "
				+ "( JC_TBKK.JKFS = GY_FKFS.FKFS ) "
				+ "AND ( JC_JCJS.CZGH =:as_czgh ) "
				+ "AND ( JC_TBKK.JGID =:al_jgid ) "
				+ "AND ( JC_TBKK.ZFPB  = '0' ) ";
		String sql_zf = "SELECT  sum(-JC_TBKK.JKJE) as JKJE_ALL "
				+ "FROM JC_TBKK JC_TBKK,JC_BRRY JC_BRRY,JC_JKZF JC_JKZF,GY_FKFS GY_FKFS,JC_JCJS JC_JCJS "
				+ "WHERE ( JC_TBKK.ZYH  = JC_BRRY.ZYH ) AND "
				+ " (JC_BRRY.ZYH = JC_JCJS.ZYH) AND "
				+ "( JC_TBKK.JKXH = JC_JKZF.JKXH ) AND "
				+ " (JC_TBKK.JSCS = JC_JCJS.JSCS) AND "
				+ "( JC_TBKK.JKFS = GY_FKFS.FKFS ) "
				+ "AND ( JC_JCJS.CZGH =:as_czgh ) "
				+ "AND ( JC_TBKK.JGID =:al_jgid ) "
				+ "AND ( JC_TBKK.ZFPB  = '1' ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", JGID);
		parameters.put("as_czgh", userid);
		parameters.put("idt_jzrq", sdfdate.parse(JZRQ));
		boolean hasRecordMore = (Boolean) request.get("hasRecordMore");
		String ls_adt_clrq_b = JZRQ.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = JZRQ.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		if (JZBS.equals("0")) {
			sql = sql
					+ "AND  JC_JCJS.JZRQ IS NULL AND JC_TBKK.JKRQ <=:idt_jzrq ";
			sql_zf = sql_zf
					+ " AND  JC_JCJS.JZRQ IS NULL AND JC_JKZF.ZFRQ <=:idt_jzrq ";
		} else {
			if (hasRecordMore) {
				parameters.remove("idt_jzrq");
				parameters.put("adt_jzrq_s", adt_clrq_b);
				parameters.put("adt_jzrq_e", adt_clrq_e);
				sql = sql
						+ "AND ( JC_JCJS.JZRQ >= :adt_jzrq_s ) AND  ( JC_JCJS.JZRQ < :adt_jzrq_e ) ";
				sql_zf = sql_zf
						+ "AND ( JC_JCJS.JZRQ >= :adt_jzrq_s ) AND  ( JC_JCJS.JZRQ < :adt_jzrq_e )";
			} else {
				sql = sql + "AND JC_JCJS.JZRQ =:idt_jzrq ";
				sql_zf = sql_zf + "AND JC_JCJS.JZRQ =:idt_jzrq ";
			}
		}
		sql = sql + " order by JC_BRRY.ZYH";
		sql_zf = sql_zf + " order by JC_BRRY.ZYH";
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		List<Map<String, Object>> list_zf = dao.doQuery(sql_zf, parameters);
		double je = list.get(0).get("JKJE_ALL") == null ? 0 : (Double) list
				.get(0).get("JKJE_ALL");
		double zf = list_zf.get(0).get("JKJE_ALL") == null ? 0
				: (Double) list_zf.get(0).get("JKJE_ALL");
		response.put("JKJE_ALL", String.format("%1$.2f", je));
	}

	private void getDeliveryDetailPara(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PersistentDataOperationException, ParseException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId()+"";
		String JGID = user.getManageUnit().getId();
		String JZRQ = (String) request.get("jzrq");
		String JZBS = (String) request.get("jzbs");
		String sql = "SELECT  sum(a.JKJE) as JKJE_ALL "
				+ "FROM JC_TBKK a,JC_BRRY b,GY_FKFS d,ctd.account.user.User e "
				+ "WHERE (a.ZYH= b.ZYH) AND "
				+ "( a.JKFS = d.FKFS ) " + "AND   ( a.CZGH = e.id )	"
				+ "AND (a.CZGH =:as_czgh ) " + "AND ( a.JGID =:al_jgid ) ";
		String sql_zf = "SELECT  sum(-a.JKJE) as JKJE_ALL "
				+ "FROM JC_TBKK a,JC_BRRY b,JC_JKZF g,GY_FKFS d,ctd.account.user.User e "
				+ "WHERE ( a.ZYH  = b.ZYH ) AND "
				+ "( a.JKXH = g.JKXH ) AND " + "( a.JKFS = d.FKFS ) "
				+ "AND   ( a.CZGH = e.id )	" + "AND (a.CZGH =:as_czgh ) "
				+ "AND ( a.JGID =:al_jgid ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", JGID);
		parameters.put("as_czgh", userid);
		parameters.put("idt_jzrq", sdfdate.parse(JZRQ));
		boolean hasRecordMore = (Boolean) request.get("hasRecordMore");
		String ls_adt_clrq_b = JZRQ.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = JZRQ.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		if (JZBS.equals("0")) {
			sql = sql + "AND  a.JZRQ IS NULL AND a.JKRQ <=:idt_jzrq ";
			sql_zf = sql_zf + " AND  g.JZRQ IS NULL AND g.ZFRQ <=:idt_jzrq ";
		} else {
			if (hasRecordMore) {
				parameters.remove("idt_jzrq");
				parameters.put("adt_jzrq_s", adt_clrq_b);
				parameters.put("adt_jzrq_e", adt_clrq_e);
				sql = sql
						+ "AND ( a.JZRQ >= :adt_jzrq_s ) AND  ( a.JZRQ < :adt_jzrq_e ) ";
				sql_zf = sql_zf
						+ "AND ( g.JZRQ >= :adt_jzrq_s ) AND  ( g.JZRQ < :adt_jzrq_e )";
			} else {
				sql = sql + "AND a.JZRQ =:idt_jzrq ";
				sql_zf = sql_zf + "AND g.JZRQ =:idt_jzrq ";
			}
		}
		sql = sql + " order by b.ZYH";
		sql_zf = sql_zf + " order by b.ZYH";
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		List<Map<String, Object>> list_zf = dao.doQuery(sql_zf, parameters);
		double je = list.get(0).get("JKJE_ALL") == null ? 0 : (Double) list
				.get(0).get("JKJE_ALL");
		double zf = list_zf.get(0).get("JKJE_ALL") == null ? 0
				: (Double) list_zf.get(0).get("JKJE_ALL");
		response.put("JKJE_ALL", String.format("%1$.2f", je + zf));
	}

	private void getAccountsDetailPara(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws ParseException,
			PersistentDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid =user.getUserId()+"";
		String JGID = user.getManageUnit().getId();
		String JZRQ = (String) request.get("jzrq");
		String JZBS = (String) request.get("jzbs");
		String sql = "SELECT sum(a.FYHJ) as FYHJ_ALL,"
				+ "sum(a.ZFHJ) as ZFHJ_ALL," + "sum(a.JKHJ) as JKHJ_ALL,"
				+ "sum(a.ZFHJ-a.JKHJ) as JSJE_ALL "
				+ "  FROM JC_JCJS a,JC_BRRY b,ctd.account.user.User d "
				+ " WHERE a.ZYH=b.ZYH and  " 
				+ "( a.CZGH = d.id ) AND" + " ( a.CZGH =:as_czgh ) "
				+ "AND ( a.JGID =:al_jgid ) ";
		String sql_zf = "SELECT sum(a.FYHJ) as FYHJ_ALL,"
				+ "sum(a.ZFHJ) as ZFHJ_ALL,"
				+ "sum(a.JKHJ) as JKHJ_ALL,"
				+ "sum(a.ZFHJ-a.JKHJ) as JSJE_ALL "
				+ "FROM  JC_JCJS a,JC_BRRY b,ctd.account.user.User d,JC_JSZF  e "
				+ "WHERE (a.ZYH=b.ZYH ) and ( a.ZYH  = e.ZYH  ) "
				+ "AND ( a.JSCS =e.JSCS ) "
				+ " AND ( a.CZGH = d.id ) "
				+ "AND ( e.ZFGH =:as_czgh ) " + "AND ( e.JGID =:al_jgid ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", JGID);
		parameters.put("as_czgh", userid);
		parameters.put("idt_jzrq", sdfdate.parse(JZRQ));
		boolean hasRecordMore = (Boolean) request.get("hasRecordMore");
		String ls_adt_clrq_b = JZRQ.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = JZRQ.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		if (JZBS.equals("0")) {
			sql = sql + "AND  a.JZRQ IS NULL AND a.JSRQ <=:idt_jzrq";
			sql_zf = sql_zf + "AND  e.JZRQ IS NULL AND e.ZFRQ <=:idt_jzrq";
		} else {
			if (hasRecordMore) {
				parameters.remove("idt_jzrq");
				parameters.put("adt_jzrq_s", adt_clrq_b);
				parameters.put("adt_jzrq_e", adt_clrq_e);
				sql = sql
						+ "AND ( a.JZRQ >= :adt_jzrq_s ) AND  ( a.JZRQ < :adt_jzrq_e ) ";
				sql_zf = sql_zf
						+ "AND ( e.JZRQ >= :adt_jzrq_s ) AND  ( e.JZRQ < :adt_jzrq_e )";
			} else {
				sql = sql + "AND a.JZRQ =:idt_jzrq";
				sql_zf = sql_zf + "AND e.JZRQ =:idt_jzrq";
			}
		}
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		List<Map<String, Object>> list_zf = dao.doQuery(sql_zf, parameters);
		double je = list.get(0).get("FYHJ_ALL") == null ? 0 : (Double) list
				.get(0).get("FYHJ_ALL");
		double zf = list_zf.get(0).get("FYHJ_ALL") == null ? 0
				: (Double) list_zf.get(0).get("FYHJ_ALL");
		response.put("FYHJ_ALL", String.format("%1$.2f", je - zf));
		je = list.get(0).get("ZFHJ_ALL") == null ? 0 : (Double) list.get(0)
				.get("ZFHJ_ALL");
		zf = list_zf.get(0).get("ZFHJ_ALL") == null ? 0 : (Double) list_zf.get(
				0).get("ZFHJ_ALL");
		response.put("ZFHJ_ALL", String.format("%1$.2f", je - zf));
		je = list.get(0).get("JKHJ_ALL") == null ? 0 : (Double) list.get(0)
				.get("JKHJ_ALL");
		zf = list_zf.get(0).get("JKHJ_ALL") == null ? 0 : (Double) list_zf.get(
				0).get("JKHJ_ALL");
		response.put("JKHJ_ALL", String.format("%1$.2f", je - zf));
		je = list.get(0).get("JSJE_ALL") == null ? 0 : (Double) list.get(0)
				.get("JSJE_ALL");
		zf = list_zf.get(0).get("JSJE_ALL") == null ? 0 : (Double) list_zf.get(
				0).get("JSJE_ALL");
		response.put("JSJE_ALL", String.format("%1$.2f", je - zf));
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String jrxml = (String) request.get("jrxml");
		boolean hasRecordMore = checkRecordCount(request, ctx);
		request.put("hasRecordMore", hasRecordMore);
		try {
			if (jrxml.equals("phis.prints.jrxml.FsbAccountsDetail")) {
				getAccountsDetailFields(request, records, ctx);
			} else if (jrxml.equals("phis.prints.jrxml.FsbDeliveryDetail")) {
				getDeliveryDetailFields(request, records, ctx);
			} else if (jrxml.equals("phis.prints.jrxml.FsbRefundDetail")) {
				getRefundDetailFields(request, records, ctx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkRecordCount(Map<String, Object> request, Context ctx) {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId()+"";
		String jgid = user.getManageUnit().getId();
		String jzrq = request.get("jzrq") + "";
		String ls_adt_clrq_b = jzrq.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = jzrq.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", jgid);
		parameters.put("as_czgh", userid);
		parameters.put("adt_jzrq_s", adt_clrq_b);
		parameters.put("adt_jzrq_e", adt_clrq_e);
		String hql = "SELECT  a.JZRQ as JZRQ, a.CZGH as CZGH,a.CYSR as CYSR,a.YJJE as YJJE,a.YJXJ as YJXJ, a.YJZP as YJZP,a.QZPJ as QZPJ,a.QZSJ as QZSJ,a.FPZS as FPZS, a.SJZS as SJZS,a.YSJE as YSJE,a.YSXJ as YSXJ,a.YSZP as YSZP,a.ZPZS as ZPZS, a.TYJJ as TYJJ,a.TJKS as TJKS,a.KBJE as KBJE,a.KBZP as KBZP, a.YSQT as YSQT,a.QTZS as QTZS,a.SRJE as SRJE,a.YJYHK as YJYHK, a.YSYHK as YSYHK, a.YSYH  as YSYH, a.JGID  as JGID FROM JC_JZXX  a WHERE ( a.JZRQ >= :adt_jzrq_s ) AND  ( a.JZRQ < :adt_jzrq_e ) AND  ( a.CZGH = :as_czgh ) AND   a.JGID = :al_jgid order by a.JZRQ";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list != null && list.size() > 1) {
			return true;
		}
		return false;
	}

	private void getAccountsDetailFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PersistentDataOperationException, ParseException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId()+"";
		String JGID = user.getManageUnit().getId();
		String JZRQ = (String) request.get("jzrq");
		String JZBS = (String) request.get("jzbs");
		String sql = " SELECT b.ZYH as ZYH," + " b.ZYHM as ZYHM," + "	b.BRXM as BRXM,"
				+ "a.JSCS as JSCS," + "	a.FPHM as FPHM," + "	a.FYHJ as FYHJ,"
				+ "	a.ZFHJ as ZFHJ," + "a.JKHJ as JKHJ,"
				+ "d.id as CZGH, " + "a.ZFHJ-a.JKHJ as JSJE,  "
				+ "	0 AS ZFPB  "
				+ "  FROM JC_JCJS a,JC_BRRY b,ctd.account.user.User d "
				+ " WHERE a.ZYH=b.ZYH and  " 
				+ "( a.CZGH = d.id ) AND" + " ( a.CZGH =:as_czgh ) "
				+ "AND ( a.JGID =:al_jgid ) ";
		String sql_zf = "SELECT b.ZYH as ZYH,"
				+ "b.ZYHM as ZYHM,"
				+ "b.BRXM as BRXM,"
				+ "a.JSCS as JSCS,"
				+ "a.FPHM as FPHM,"
				+ "-a.FYHJ as FYHJ,"
				+ "-a.ZFHJ as ZFHJ,"
				+ "-a.JKHJ as JKHJ,"
				+ "d.id as CZGH, "
				+ "-a.ZFHJ+a.JKHJ as JSJE,"
				+ "1 AS ZFPB  "
				+ "FROM  JC_JCJS a,JC_BRRY b,ctd.account.user.User d,JC_JSZF  e "
				+ "WHERE (a.ZYH=b.ZYH ) and ( a.ZYH  = e.ZYH  ) "
				+ "AND ( a.JSCS =e.JSCS ) "
				+ " AND ( a.CZGH = d.id ) "
				+ "AND ( e.ZFGH =:as_czgh ) " + "AND ( e.JGID =:al_jgid ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", JGID);
		parameters.put("as_czgh", userid);
		parameters.put("idt_jzrq", sdfdate.parse(JZRQ));

		boolean hasRecordMore = (Boolean) request.get("hasRecordMore");
		String ls_adt_clrq_b = JZRQ.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = JZRQ.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		if (JZBS.equals("0")) {
			sql = sql + "AND  a.JZRQ IS NULL AND a.JSRQ <=:idt_jzrq";
			sql_zf = sql_zf + "AND  e.JZRQ IS NULL AND e.ZFRQ <=:idt_jzrq";
		} else {
			if (hasRecordMore) {
				parameters.remove("idt_jzrq");
				parameters.put("adt_jzrq_s", adt_clrq_b);
				parameters.put("adt_jzrq_e", adt_clrq_e);
				sql = sql
						+ "AND ( a.JZRQ >= :adt_jzrq_s ) AND  ( a.JZRQ < :adt_jzrq_e ) ";
				sql_zf = sql_zf
						+ "AND ( e.JZRQ >= :adt_jzrq_s ) AND  ( e.JZRQ < :adt_jzrq_e )";
			} else {
				sql = sql + "AND a.JZRQ =:idt_jzrq";
				sql_zf = sql_zf + "AND e.JZRQ =:idt_jzrq";
			}
		}
		sql = sql + " order by b.ZYH";
		sql_zf = sql_zf + " order by b.ZYH";
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		List<Map<String, Object>> list_zf = dao.doQuery(sql_zf, parameters);
		records.addAll(list);
		records.addAll(list_zf);
	}

	private void getDeliveryDetailFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PersistentDataOperationException, ParseException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId()+"";
		String JGID = user.getManageUnit().getId();
		String JZRQ = (String) request.get("jzrq");
		String JZBS = (String) request.get("jzbs");
		String sql = "SELECT b.ZYH as ZYH," + "b.ZYHM as ZYHM," + "b.BRXM as BRXM,"
				+ "a.JKXH as JKXH," + "a.SJHM as SJHM," + "a.JKJE as JKJE,"
				+ "a.JKFS as FKFS," + "d.FKMC as JKFS," + "a.ZPHM as ZPHM,"
				+ "e.id as CZGH," + "0 AS ZFPB "
				+ "FROM JC_TBKK a,JC_BRRY b,GY_FKFS d,ctd.account.user.User e "
				+ "WHERE (a.ZYH= b.ZYH) AND " 
				+ "( a.JKFS = d.FKFS ) " + "AND   ( a.CZGH = e.id )	"
				+ "AND (a.CZGH =:as_czgh ) " + "AND ( a.JGID =:al_jgid ) ";
		String sql_zf = "SELECT b.ZYH as ZYH,"
				+ "b.ZYHM as ZYHM,"
				+ "b.BRXM as BRXM,"
				+ "a.JKXH as JKXH,"
				+ "a.SJHM as SJHM,"
				+ "-a.JKJE as JKJE,"
				+ "a.JKFS as FKFS,"
				+ "d.FKMC as JKFS,"
				+ "a.ZPHM as ZPHM,"
				+ "e.id as CZGH,"
				+ "1 AS ZFPB "
				+ "FROM JC_TBKK a,JC_BRRY b,JC_JKZF g,GY_FKFS d,ctd.account.user.User e "
				+ "WHERE ( a.ZYH  = b.ZYH ) AND " 
				+ "( a.JKXH = g.JKXH ) AND " + "( a.JKFS = d.FKFS ) "
				+ "AND   ( a.CZGH = e.id )	" + "AND (a.CZGH =:as_czgh ) "
				+ "AND ( a.JGID =:al_jgid ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", JGID);
		parameters.put("as_czgh", userid);
		parameters.put("idt_jzrq", sdfdate.parse(JZRQ));
		boolean hasRecordMore = (Boolean) request.get("hasRecordMore");
		String ls_adt_clrq_b = JZRQ.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = JZRQ.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		if (JZBS.equals("0")) {
			sql = sql + "AND  a.JZRQ IS NULL AND a.JKRQ <=:idt_jzrq ";
			sql_zf = sql_zf + " AND  g.JZRQ IS NULL AND g.ZFRQ <=:idt_jzrq ";
		} else {
			if (hasRecordMore) {
				parameters.remove("idt_jzrq");
				parameters.put("adt_jzrq_s", adt_clrq_b);
				parameters.put("adt_jzrq_e", adt_clrq_e);
				sql = sql
						+ "AND ( a.JZRQ >= :adt_jzrq_s ) AND  ( a.JZRQ < :adt_jzrq_e ) ";
				sql_zf = sql_zf
						+ "AND ( g.JZRQ >= :adt_jzrq_s ) AND  ( g.JZRQ < :adt_jzrq_e )";
			} else {
				sql = sql + "AND a.JZRQ =:idt_jzrq ";
				sql_zf = sql_zf + "AND g.JZRQ =:idt_jzrq ";
			}
		}
		sql = sql + " order by b.ZYH";
		sql_zf = sql_zf + " order by b.ZYH";
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		List<Map<String, Object>> list_zf = dao.doQuery(sql_zf, parameters);
		records.addAll(list);
		records.addAll(list_zf);
	}

	private void getRefundDetailFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PersistentDataOperationException, ParseException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId()+"";
		String JGID = user.getManageUnit().getId();
		String JZRQ = (String) request.get("jzrq");
		String JZBS = (String) request.get("jzbs");
		String sql = "SELECT JC_BRRY.ZYH as ZYH,"
				+ "JC_BRRY.ZYHM as ZYHM,"
				+ "JC_BRRY.BRXM as BRXM,"
				+ "JC_TBKK.JKXH as JKXH,"
				+ "JC_TBKK.SJHM as SJHM,"
				+ "JC_TBKK.JKJE as JKJE,"
				+ "JC_TBKK.JKFS as FKFS,"
				+ "GY_FKFS.FKMC as JKFS,"
				+ "JC_TBKK.ZPHM as ZPHM,"
				+ "g.id as CZGH,"
				+ "0 AS ZFPB "
				+ "FROM JC_TBKK JC_TBKK,JC_BRRY JC_BRRY,GY_FKFS GY_FKFS ,JC_JCJS JC_JCJS,ctd.account.user.User g "
				+ "WHERE (JC_TBKK.ZYH= JC_BRRY.ZYH) AND "
				+ "(JC_BRRY.ZYH = JC_JCJS.ZYH) AND "
				+ " (JC_TBKK.JSCS = JC_JCJS.JSCS) AND "
				+ "( JC_TBKK.JKFS = GY_FKFS.FKFS ) "
				+ "AND   ( JC_TBKK.CZGH = g.id )	"
				+ "AND (JC_JCJS.CZGH =:as_czgh ) "
				+ "AND ( JC_TBKK.JGID =:al_jgid ) "
				+ " ";
		String sql_zf = "SELECT JC_BRRY.ZYH as ZYH,"
				+ "JC_BRRY.ZYHM as ZYHM,"
				+ "JC_BRRY.BRXM as BRXM,"
				+ "JC_TBKK.JKXH as JKXH,"
				+ "JC_TBKK.SJHM as SJHM,"
				+ "-JC_TBKK.JKJE as JKJE,"
				+ "JC_TBKK.JKFS as FKFS,"
				+ "GY_FKFS.FKMC as JKFS,"
				+ "JC_TBKK.ZPHM as ZPHM,"
				+ "g.id as CZGH,"
				+ "1 AS ZFPB "
				+ "FROM JC_TBKK JC_TBKK,JC_BRRY JC_BRRY,JC_JKZF JC_JKZF,GY_FKFS GY_FKFS ,JC_JCJS JC_JCJS,ctd.account.user.User g "
				+ "WHERE ( JC_TBKK.ZYH  = JC_BRRY.ZYH ) AND "
				+ "(JC_BRRY.ZYH = JC_JCJS.ZYH) AND "
				+ "( JC_TBKK.JKXH = JC_JKZF.JKXH ) AND "
				+ " (JC_TBKK.JSCS = JC_JCJS.JSCS) AND "
				+ "( JC_TBKK.JKFS = GY_FKFS.FKFS ) "
				+ "AND   ( JC_TBKK.CZGH = g.id )	"
				+ "AND (JC_JCJS.CZGH =:as_czgh ) "
				+ "AND ( JC_TBKK.JGID =:al_jgid ) "
				+ "AND ( JC_TBKK.ZFPB  = '1' ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", JGID);
		parameters.put("as_czgh", userid);
		parameters.put("idt_jzrq", sdfdate.parse(JZRQ));
		boolean hasRecordMore = (Boolean) request.get("hasRecordMore");
		String ls_adt_clrq_b = JZRQ.substring(0, 10) + " 00:00:00";
		String ls_adt_clrq_e = JZRQ.substring(0, 10) + " 23:59:59";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		if (JZBS.equals("0")) {
			sql = sql
					+ "AND  JC_JCJS.JZRQ IS NULL AND JC_TBKK.JKRQ <=:idt_jzrq ";
			sql_zf = sql_zf
					+ " AND  JC_JCJS.JZRQ IS NULL AND JC_JKZF.ZFRQ <=:idt_jzrq ";
		} else {
			if (hasRecordMore) {
				parameters.remove("idt_jzrq");
				parameters.put("adt_jzrq_s", adt_clrq_b);
				parameters.put("adt_jzrq_e", adt_clrq_e);
				sql = sql
						+ "AND ( JC_JCJS.JZRQ >= :adt_jzrq_s ) AND  ( JC_JCJS.JZRQ < :adt_jzrq_e ) ";
				sql_zf = sql_zf
						+ "AND ( JC_JCJS.JZRQ >= :adt_jzrq_s ) AND  ( JC_JCJS.JZRQ < :adt_jzrq_e )";
			} else {
				sql = sql + "AND JC_JCJS.JZRQ =:idt_jzrq ";
				sql_zf = sql_zf + "AND JC_JCJS.JZRQ =:idt_jzrq ";
			}
		}
		sql = sql + " order by JC_BRRY.ZYH";
		sql_zf = sql_zf + " order by JC_BRRY.ZYH";
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		List<Map<String, Object>> list_zf = dao.doQuery(sql_zf, parameters);
		records.addAll(list);
		records.addAll(list_zf);
	}
}
