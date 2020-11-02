package phis.application.ivc.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class ChargesCheckout extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ClinicDiagnossisModel.class);
	public Date jzdate = new Date();
	public void doQueryFields(Map<String, Object> req,
							  List<Map<String, Object>> records,BaseDAO dao, Context ctx) throws PrintException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		parameters.put("jgid", jgid);
		parameters.put("czgh", uid);
		Calendar cdate = Calendar.getInstance();
		cdate.setTime((Date) req.get("jzrq"));
		cdate.set(Calendar.MILLISECOND, 0);
		parameters.put("jzrq", cdate.getTime());
		StringBuffer hql_rbmx = new StringBuffer();// 结帐后的明细信息
		hql_rbmx.append("select c.SFMC as SFMC ,b.SFJE as SFJE from MS_HZRB a,MS_RBMX b,GY_SFXM c ")
				.append(" where b.SFXM = c.SFXM and a.CZGH = b.CZGH and a.JZRQ = b.JZRQ AND a.JGID=:jgid and a.JZRQ = :jzrq and a.MZLB =:mzlb and a.CZGH=:czgh order by c.SFMC");
		try {
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			List<Map<String, Object>> list_rbmx = dao.doSqlQuery(hql_rbmx.toString(), parameters);
			for (int i = 0; i < list_rbmx.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map_rbmx = list_rbmx.get(i);
				map.put("SFXM",map_rbmx.get("SFMC"));
				map.put("ZJJE", String.format("%1$.2f", map_rbmx.get("SFJE")));
				if (i < list_rbmx.size() - 1) {
					i++;
					Map<String, Object> map_rbmx1 = list_rbmx.get(i);
					map.put("SFXM1",map_rbmx1.get("SFMC"));
					map.put("ZJJE1",String.format("%1$.2f",map_rbmx1.get("SFJE")));
				}
				if(list_rbmx.size()%2==1 && i==(list_rbmx.size() - 1)){
					map.put("SFXM1","");
					map.put("ZJJE1","");
				}
				records.add(map);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doGetFields(Map<String, Object> req,
							List<Map<String, Object>> records,BaseDAO dao, Context ctx) throws PrintException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		parameters.put("jgid", jgid);
		parameters.put("czgh", uid);
		Calendar cdate = Calendar.getInstance();
		cdate.setTime(jzdate);
		parameters.put("jzrq", cdate.getTime());								// MS_ZFFP
		StringBuffer hql_sfmx = new StringBuffer();// 统计收费明细MS_SFMX
		StringBuffer hql_sfmx_zf = new StringBuffer();// 统计收费作废明细MS_SFMX
		hql_sfmx.append("select b.SFXM as SFXM,b.ZJJE as ZJJE from MS_MZXX a,MS_SFMX b")
				.append(" where a.MZXH = b.MZXH and a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.SFRQ<=:jzrq");
		hql_sfmx_zf
				.append("select b.SFXM as SFXM,-b.ZJJE as ZJJE from MS_ZFFP a,MS_SFMX b")
				.append(" where a.MZXH = b.MZXH and a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.ZFRQ<=:jzrq");
		StringBuffer sql = new StringBuffer("select c.SFMC as SFMC,sum(ZJJE) as SFJE from (").append(hql_sfmx).append(" union all ").append(hql_sfmx_zf).append(")t,GY_SFXM c where t.SFXM = c.SFXM group by c.SFMC order by c.SFMC");
		try {
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			List<Map<String, Object>> list_rbmx = dao.doSqlQuery(sql.toString(), parameters);
			for (int i = 0; i < list_rbmx.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map_rbmx = list_rbmx.get(i);
				map.put("SFXM",map_rbmx.get("SFMC"));
				map.put("ZJJE", String.format("%1$.2f", map_rbmx.get("SFJE")));
				if (i < list_rbmx.size() - 1) {
					i++;
					Map<String, Object> map_rbmx1 = list_rbmx.get(i);
					map.put("SFXM1",map_rbmx1.get("SFMC"));
					map.put("ZJJE1",String.format("%1$.2f",map_rbmx1.get("SFJE")));
				}
				if(list_rbmx.size()%2==1 && i==(list_rbmx.size() - 1)){
					map.put("SFXM1","");
					map.put("ZJJE1","");
				}
				records.add(map);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doQuery(Map<String, Object> req, Map<String, Object> res,
						BaseDAO dao, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userName = user.getUserName();
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		res.put("toll", userName);//收费员
		res.put("SFRQ", sdftime.format(req.get("jzrq")));
		res.put("Lister", userName);
		res.put("ReviewedBy", "");
		res.put("dateTabling", BSHISUtil.getDate());
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		parameters.put("czgh", uid);
		Calendar cdate = Calendar.getInstance();
		cdate.setTime((Date) req.get("jzrq"));
		cdate.set(Calendar.MILLISECOND, 0);
		parameters.put("jzrq", cdate.getTime());
		try {
			List<Map<String, Object>> ksrq_list = dao
					.doSqlQuery("select to_char(max(JZRQ),'yyyy-mm-dd hh24:mi:ss') as JZRQ from (select max(JZRQ) as JZRQ from  MS_HZRB where JGID=:jgid and CZGH=:czgh and JZRQ<:jzrq union all select max(JZRQ) as JZRQ from  MS_GHRB where JGID=:jgid and CZGH=:czgh and JZRQ<:jzrq)", parameters);
			if(ksrq_list.size()>0 && ksrq_list.get(0).get("JZRQ") != null){
				Date ksrq;
				ksrq = sdftime.parse(ksrq_list.get(0).get("JZRQ") + "");
				res.put("KSRQ",
						BSHISUtil.toString(ksrq, "yyyy-MM-dd HH:mm:ss"));
			}else{
				ksrq_list = dao
						.doSqlQuery("select to_char(min(SFRQ),'yyyy-mm-dd hh24:mi:ss') as SFRQ from (select min(SFRQ) as SFRQ from  MS_MZXX where JGID=:jgid and CZGH=:czgh  and JZRQ<:jzrq union all select min(CZSJ) as SFRQ from  MS_GHMX where JGID=:jgid and CZGH=:czgh and JZRQ<:jzrq)",parameters);
				if(ksrq_list.size()>0 && ksrq_list.get(0).get("JZRQ") != null){
					Date ksrq;
					ksrq = sdftime.parse(ksrq_list.get(0).get("SFRQ") + "");
					res.put("KSRQ",
							BSHISUtil.toString(ksrq, "yyyy-MM-dd HH:mm:ss"));
				}else{
					res.put("KSRQ",
							BSHISUtil.toString(cdate.getTime(), "yyyy-MM-dd HH:mm:ss"));
				}
			}
			StringBuffer hql_hzrb = new StringBuffer();// 结帐后的信息 MS_HZRB
			hql_hzrb.append(
					"select QZPJ as QZPJ,ZFFP as ZFFP,ZJJE as ZJJE,FPZS as FPZS,ZFJE as ZFJE,ZFZS as ZFZS," +
							" YSQTFB as YSQTFB,ZFFSHJ as ZFFSHJ,NHZFHJ as NHZFHJ,YBZFHJ as YBZFHJ," +
							" NHJZ as NHJZ,YBJZ as YBJZ,JZJEST as JZJEST,JZZE as JZZE,XJJE as XJJE,ZFZF as ZFZF," +
							"WXHJ as WXHJ,ZFBHJ as ZFBHJ,APPWXHJ as APPWXHJ,APPZFBHJ as APPZFBHJ from ")
					.append(" MS_HZRB where JGID=:jgid and JZRQ = :jzrq and MZLB =:mzlb and CZGH=:czgh ");
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			Map<String, Object> map_hzrb = dao.doLoad(hql_hzrb.toString(),parameters);
			double sfhj = 0;
			double zffshj = 0;
			double nhzfhj = 0;
			double ybzfhj = 0;
			double jzze = 0;//记账总额
			double nhjz = 0;//农合记账
			double ybjz = 0;//医保记账
			double zfzf = 0;//账户记账
			double smze = 0;//扫码总额（结算）
			double appze = 0;//app总额（结算）
			double wxhj = 0;//微信合计
			double zfbhj = 0;//支付宝合计
			double appwxhj = 0;//app微信合计
			double appzfbhj = 0;//app支付宝合计
			if (map_hzrb != null && map_hzrb.size() > 0) {
				sfhj = BSPHISUtil.getDouble(map_hzrb.get("ZJJE"), 2);
				zffshj = BSPHISUtil.getDouble(map_hzrb.get("ZFFSHJ"), 2);
				nhzfhj = BSPHISUtil.getDouble(map_hzrb.get("NHZFHJ"), 2);
				ybzfhj = BSPHISUtil.getDouble(map_hzrb.get("YBZFHJ"), 2);
				nhjz = BSPHISUtil.getDouble(map_hzrb.get("NHJZ"), 2);
				ybjz = BSPHISUtil.getDouble(map_hzrb.get("YBJZ"), 2);
				zfzf = BSPHISUtil.getDouble(map_hzrb.get("ZFZF"), 2);
				wxhj = BSPHISUtil.getDouble(map_hzrb.get("WXHJ"), 2);//微信合计
				zfbhj = BSPHISUtil.getDouble(map_hzrb.get("ZFBHJ"), 2);//支付宝合计
				appwxhj = BSPHISUtil.getDouble(map_hzrb.get("APPWXHJ"), 2);//app微信合计
				appzfbhj = BSPHISUtil.getDouble(map_hzrb.get("APPZFBHJ"), 2);//app支付宝合计
				res.put("numPage", map_hzrb.get("FPZS") == null ? 0 : map_hzrb
						.get("FPZS").toString());
				res.put("qtysFb", map_hzrb.get("YSQTFB"));
				res.put("invalid", map_hzrb.get("ZFFP") == null ? " "
						: map_hzrb.get("ZFFP").toString());
				res.put("FPHM", map_hzrb.get("QZPJ") == null ? "" : map_hzrb
						.get("QZPJ").toString());
				res.put("invalidAmount",
						"张数：" + map_hzrb.get("ZFZS").toString() + " 作废金额："
								+ String.format("%1$.2f", map_hzrb.get("ZFJE")));
				res.put("jzjeSt", map_hzrb.get("JZJEST") == null ? "0.00" : String.format("%1$.2f", map_hzrb.get("JZJEST")));

			}else{
				res.put("FPHM","");
				res.put("numPage",0);
				res.put("qtysFb","");
				res.put("invalid","");
				res.put("invalidAmount","");
			}
			StringBuffer hql_ghrb = new StringBuffer();// 挂号日报 MS_GHRB
			hql_ghrb.append(
					"select QZPJ as QZPJ,ZJJE as ZJJE,FPZS as FPZS,THFP as THFP,THJE as THJE,THSL as THSL,WXHJ as WXHJ,ZFBHJ as ZFBHJ,WXHJ as WXHJ,ZFBHJ as ZFBHJ,ZFFSHJ as ZFFSHJ,YBZFHJ as YBZFHJ,YZJM as YZJM,QTYS as QTYS,ZHJE as ZHJE,YHJE as YHJE from ")
					.append("MS_GHRB where JGID=:jgid and JZRQ = :jzrq and MZLB =:mzlb and CZGH=:czgh ");
			Map<String, Object> map_ghrb = dao.doLoad(hql_ghrb.toString(),
					parameters);

			double ghhj = 0;
			double ghsmze = 0;//挂号扫码总额（结算）
			double ghwxhj = 0;//挂号微信合计
			double ghzfbhj = 0;//挂号支付宝合计
			double ghzffshj = 0;//自费现金合计
			double ghybzfhj = 0;//医保自费合计
			double yzjm = 0;//义诊减免
			double ghyhhj = 0;//优惠金额合计
			if (map_ghrb != null && map_ghrb.size() > 0) {
				ghhj = BSPHISUtil.getDouble(map_ghrb.get("ZJJE"), 2);
				ybjz = BSHISUtil.doublesum(ybjz,BSPHISUtil.getDouble(map_ghrb.get("QTYS"), 2));
				zfzf = BSHISUtil.doublesum(zfzf,BSPHISUtil.getDouble(map_ghrb.get("ZHJE"), 2));
				jzze = BSHISUtil.doublesum(jzze,BSHISUtil.doublesum(ybjz,zfzf));
				ghwxhj = BSPHISUtil.getDouble(map_ghrb.get("WXHJ"), 2);
				ghzfbhj = BSPHISUtil.getDouble(map_ghrb.get("ZFBHJ"), 2);
				ghzffshj = BSPHISUtil.getDouble(map_ghrb.get("ZFFSHJ"), 2);
				ghybzfhj = BSPHISUtil.getDouble(map_ghrb.get("YBZFHJ"), 2);
				yzjm = BSPHISUtil.getDouble(map_ghrb.get("YZJM"), 2);
				ghyhhj = BSPHISUtil.getDouble(map_ghrb.get("YHJE"), 2);
				res.put("JZHM", map_ghrb.get("QZPJ") == null ? "" : map_ghrb
						.get("QZPJ").toString());
				res.put("invalidgh", map_ghrb.get("THFP") == null ? " "
						: map_ghrb.get("THFP").toString());
				res.put("invalidghAmount",
						"张数：" + map_ghrb.get("THSL").toString() + " 退号金额："
								+ String.format("%1$.2f", map_ghrb.get("THJE")));
			}else{
				res.put("invalidghAmount","张数：0 退号金额：0.00");
			}
			zffshj = BSHISUtil.doublesum(zffshj,ghzffshj);
			ybzfhj = BSHISUtil.doublesum(ybzfhj,ghybzfhj);
			res.put("zffshj", String.format("%1$.2f", zffshj));
			res.put("xjje", String.format("%1$.2f", BSHISUtil.doublesum(BSHISUtil.doublesum(zffshj,nhzfhj),ybzfhj)));
			res.put("appwxhj",String.format("%1$.2f", appwxhj));//app微信合计
			res.put("appzfbhj",String.format("%1$.2f", appzfbhj));//app支付宝合计
			res.put("appze",String.format("%1$.2f", appze));//app总额
			res.put("nhzfhj", String.format("%1$.2f", nhzfhj));
			res.put("ybzfhj", String.format("%1$.2f", ybzfhj));
			res.put("nhjz", String.format("%1$.2f", nhjz));
			res.put("ybjz", String.format("%1$.2f", ybjz));
			res.put("jzze", String.format("%1$.2f",jzze));
			res.put("ZFZF",String.format("%1$.2f",zfzf));
			smze = wxhj+zfbhj;//扫码总额（结算）
			appze = appwxhj+appzfbhj;//app总额（结算）
			res.put("wxhj",String.format("%1$.2f", wxhj));
			res.put("zfbhj",String.format("%1$.2f", zfbhj));
			res.put("smze",String.format("%1$.2f", smze));
			res.put("total", String.format("%1$.2f", sfhj));
			ghsmze= ghwxhj + ghzfbhj;
			res.put("ghwxhj",String.format("%1$.2f",ghwxhj));
			res.put("ghzfbhj",String.format("%1$.2f",ghzfbhj));
			res.put("ghsmze",String.format("%1$.2f",ghsmze));
			//ghhj = BSHISUtil.doublesub(ghhj,yzjm);
			res.put("totalgh", String.format("%1$.2f", ghhj));
			res.put("totalgh",
					String.format("%1$.2f", ghhj));
			if(yzjm>0 || ghyhhj>0){
				res.put("totalgh", String.format("%1$.2f", ghhj)+"(其中减免"+String.format("%1$.2f",BSHISUtil.doublesum(yzjm,ghyhhj))+")");
			}

			StringBuffer hql_ghrbmx = new StringBuffer();// 挂号日报明细
			hql_ghrbmx
					.append("select rb.CZGH as CZGH,SUM(mx.GHJE) as GHJE,SUM(mx.ZLJE) as ZLJE,SUM(mx.ZJFY) as ZJFY,SUM(mx.BLJE) as BLJE from ")
					.append("MS_GHRB rb,")
					.append("MS_GRMX mx ")
					.append(" where rb.CZGH = mx.CZGH and rb.JZRQ = mx.JZRQ AND rb.JGID=:jgid and rb.JZRQ = :jzrq and rb.MZLB =:mzlb and rb.CZGH=:czgh GROUP BY rb.CZGH ");

			Map<String, Object> map_ghrbmx = dao.doLoad(hql_ghrbmx.toString(),
					parameters);
			if (map_ghrbmx != null && map_ghrbmx.size() > 0) {
				res.put("GHF", String.format("%1$.2f",map_ghrbmx.get("GHJE")));
				res.put("ZLF", String.format("%1$.2f",map_ghrbmx.get("ZLJE")));
				res.put("BLF", String.format("%1$.2f",map_ghrbmx.get("BLJE")));
				res.put("ZJF", String.format("%1$.2f",map_ghrbmx.get("ZJFY")));
			}
			else{
				res.put("GHF", "0.00");
				res.put("ZLF",  "0.00");
				res.put("BLF",  "0.00");
				res.put("ZJF",  "0.00");
			}
			if (sfhj + ghhj < 0) {
				res.put("amountIn","负" + BSPHISUtil.changeMoneyUpper(String.format("%1$.2f", -(sfhj + ghhj))));
			} else {
				res.put("amountIn", BSPHISUtil.changeMoneyUpper(String.format("%1$.2f", sfhj + ghhj)));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doGetParameters(Map<String, Object> req,
								Map<String, Object> res,BaseDAO dao, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userName = user.getUserName();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		res.put("toll", userName);//收费员
		res.put("SFRQ", req.get("jzrq")+"");
		res.put("Lister", userName);
		res.put("ReviewedBy", "");
		res.put("dateTabling", BSHISUtil.getDate());
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		parameters.put("czgh", uid);
		try {
			List<Map<String, Object>> ksrq_list = dao
					.doSqlQuery("select to_char(max(JZRQ),'yyyy-mm-dd hh24:mi:ss') as JZRQ from (select max(JZRQ) as JZRQ from  MS_HZRB where JGID=:jgid and CZGH=:czgh union all select max(JZRQ) as JZRQ from  MS_GHRB where JGID=:jgid and CZGH=:czgh)", parameters);
			if(ksrq_list.size()>0 && ksrq_list.get(0).get("JZRQ")!=null){
				Date ksrq;
				ksrq = sdf.parse(ksrq_list.get(0).get("JZRQ") + "");
				res.put("KSRQ",
						BSHISUtil.toString(ksrq, "yyyy-MM-dd HH:mm:ss"));
			}else{
				ksrq_list = dao
						.doSqlQuery("select to_char(min(SFRQ),'yyyy-mm-dd hh24:mi:ss') as SFRQ from (select min(SFRQ) as SFRQ from  MS_MZXX where JGID=:jgid and CZGH=:czgh union all select min(CZSJ) as SFRQ from  MS_GHMX where JGID=:jgid and CZGH=:czgh)",parameters);
				if(ksrq_list.size()>0 && ksrq_list.get(0).get("SFRQ")!=null){
					Date ksrq;
					ksrq = sdf.parse(ksrq_list.get(0).get("SFRQ") + "");
					res.put("KSRQ",
							BSHISUtil.toString(ksrq, "yyyy-MM-dd HH:mm:ss"));
				}else{
					res.put("KSRQ",
							BSHISUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
				}
			}
			StringBuffer hql_mzxx = new StringBuffer();// 统计收费信息MS_MZXX
			StringBuffer hql_mzxx_zf = new StringBuffer();// 统计收费作废信息MS_MZXX
			hql_mzxx.append("select a.CZGH as CZGH,a.FPHM as FPHM,nvl(a.FPZS,1) as FPZS," +
					" a.ZJJE as ZJJE,a.XJJE as XJJE,a.BRXZ as BRXZ,a.ZFJE as ZFJE,a.ZHJE as ZHJE," +
					"CASE WHEN FFFS=32 THEN ZPJE WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE," +
					"CASE WHEN FFFS=35 THEN ZPJE ELSE 0 END AS APPWXJE," +
					"CASE WHEN FFFS=36 THEN ZPJE ELSE 0 END AS APPZFBJE,a.QTYS as QTYS from MS_MZXX a")
					.append(" where a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB =:mzlb and a.CZGH=:czgh and a.SFRQ<=:jzrq order by a.FPHM");

			hql_mzxx_zf.append("select b.CZGH as CZGH,a.FPHM as FPHM,nvl(a.FPZS,1) as FPZS," +
					" a.ZJJE as ZJJE,a.XJJE as XJJE,a.BRXZ as BRXZ,a.ZFJE as ZFJE,a.ZHJE as ZHJE," +
					"CASE WHEN FFFS=32 THEN ZPJE WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE," +
					"CASE WHEN FFFS=35 THEN ZPJE ELSE 0 END AS APPWXJE," +
					"CASE WHEN FFFS=36 THEN ZPJE ELSE 0 END AS APPZFBJE from MS_MZXX a,")
					.append("MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB = :mzlb and b.CZGH =:czgh and b.ZFRQ<=:jzrq order by a.FPHM");
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			Date date = sdf.parse(req.get("jzrq")+"");
			jzdate = date;
			Calendar cdate = Calendar.getInstance();
			cdate.setTime(date);
			cdate.set(Calendar.MILLISECOND, 0);
			parameters.put("jzrq", cdate.getTime());
			List<Map<String, Object>> mzxx_list = dao.doSqlQuery(hql_mzxx.toString(), parameters);
			List<Map<String, Object>> fpxx_list = getFPList(mzxx_list);
			String fpxx = getFPXX(fpxx_list);
			res.put("FPHM", fpxx);//发票号码集;
			res.put("numPage", fpxx_list.size());//发票张数;
			List<Map<String, Object>> mzxx_zf_list = dao.doSqlQuery(hql_mzxx_zf.toString(), parameters);
			List<Map<String, Object>> zffpxx_list = getFPList(mzxx_zf_list);
			String zffpxx = getFPXX(zffpxx_list);
			res.put("invalid", zffpxx);//作废发票
			double sfhj = 0;
			double zfhj = 0;
			double zffshj=0;//自费方式合计
			double nhzfhj=0;//农合自付合计
			double ybzfhj=0;//医保自付合计
			double zfzf = 0;//账户支付
			double smze=0;//扫码总额
			double wxhj=0;//微信合计
			double zfbhj=0;//支付宝合计
			double appze=0;//app总额
			double appwxhj=0;//app微信合计
			double appzfbhj=0;//app支付宝合计
			String zddzmsg = "";//自动对账分析报告
			for(int i = 0 ; i < mzxx_list.size() ; i ++){
				Map<String,Object> mzxx = mzxx_list.get(i);
				sfhj = BSHISUtil.doublesum(sfhj,BSPHISUtil.getDouble(mzxx.get("ZJJE"),2));
				zfzf = BSHISUtil.doublesum(zfzf,BSPHISUtil.getDouble(mzxx.get("ZHJE"),2));
				double wxje =BSPHISUtil.getDouble(mzxx.get("WXJE"),2);//微信金额
				double zfbje = BSPHISUtil.getDouble(mzxx.get("ZFBJE"),2);//支付宝金额
				double appwxje =BSPHISUtil.getDouble(mzxx.get("APPWXJE"),2);//app微信金额
				double appzfbje = BSPHISUtil.getDouble(mzxx.get("APPZFBJE"),2);//app支付宝金额
				wxhj = BSHISUtil.doublesum(wxhj,wxje);//微信合计
				zfbhj = BSHISUtil.doublesum(zfbhj,zfbje);//支付宝合计
				appwxhj = BSHISUtil.doublesum(appwxhj,appwxje);//app微信合计
				appzfbhj = BSHISUtil.doublesum(appzfbhj,appzfbje);//app支付宝合计
				/****************add by hujian 2020-03-24 自动对账分析，错误输出报告*****************************************/
				double hjje = BSPHISUtil.getDouble(mzxx.get("ZJJE"),2);//单个发票合计金额
				double smhzje = BSHISUtil.doublesum(BSPHISUtil.getDouble(mzxx.get("WXJE"),2),BSPHISUtil.getDouble(mzxx.get("ZFBJE"),2));//窗口扫码汇总金额
				double apphzje = BSHISUtil.doublesum(BSPHISUtil.getDouble(mzxx.get("APPWXJE"),2),BSPHISUtil.getDouble(mzxx.get("APPZFBJE"),2));//窗口扫码汇总金额
				double dzzdje = BSHISUtil.doublesum(smhzje,apphzje);//电子账单
				double zhje = BSPHISUtil.getDouble(mzxx.get("ZHJE"),2);//账户金额
				double xjje = BSHISUtil.doublesub(BSPHISUtil.getDouble(mzxx.get("ZFJE"),2),BSHISUtil.doublesum(smhzje,apphzje));//总的现金金额
				double qtysje = BSPHISUtil.getDouble(mzxx.get("QTYS"),2);//医保金额
				double hzje = BSHISUtil.doublesum(BSHISUtil.doublesum(xjje,qtysje),BSHISUtil.doublesum(dzzdje,zhje));//现金金额+医保金额+账户金额+扫码+App金额=总金额
				double hbwc = BSPHISUtil.getDouble(mzxx.get("HBWC"),2);//货币误差金额
				if(hbwc>0.5){
					String FPHM = (String) mzxx.get("FPHM");//发票号码
					zddzmsg = zddzmsg+"货币误差过大，"+"发票号码:"+FPHM+"！";
				}
				if(hjje!=hzje){
					String FPHM = (String) mzxx.get("FPHM");//发票号码
					double wcje = BSHISUtil.doublesub(hzje,hjje);//误差金额
					zddzmsg = zddzmsg +"发票号码:"+FPHM+"，"+"发票应收总金额："+hjje+",实收金额："+hzje+"，误差："+wcje+"，明细：现金金额："+xjje+"记账金额："
							+BSPHISUtil.getDouble(mzxx.get("ZHJE"),2)+","+"医保金额："+qtysje+",";
				}
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				Long brxz = Long.parseLong(mzxx.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxz);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				if("1000".equals(mzxx.get("BRXZ")+"")){
					//zffshj=BSPHISUtil.getDouble(zffshj+ BSPHISUtil.getDouble(mzxx.get("ZJJE"),2),2);
					zffshj=BSHISUtil.doublesub(BSHISUtil.doublesum(zffshj,BSPHISUtil.getDouble(mzxx.get("ZJJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					zffshj=BSHISUtil.doublesub(zffshj,BSHISUtil.doublesum(appwxje,appzfbje));
				}
				if("3000".equals(mzxx.get("BRXZ")+"") || "2000".equals(mzxx.get("BRXZ")+"")){
					//ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(mzxx.get("ZFJE"),2),2);
					ybzfhj=BSHISUtil.doublesub(BSHISUtil.doublesum(ybzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					ybzfhj=BSHISUtil.doublesub(ybzfhj,BSHISUtil.doublesum(appwxje,appzfbje));
				}else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(mzxx.get("XJJE"),2),2);
				}
				if("6000".equals(mzxx.get("BRXZ")+"")){
					//nhzfhj=BSPHISUtil.getDouble(nhzfhj+ BSPHISUtil.getDouble(mzxx.get("ZFJE"),2),2);
					nhzfhj=BSHISUtil.doublesub(BSHISUtil.doublesum(nhzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					nhzfhj=BSHISUtil.doublesub(nhzfhj,BSHISUtil.doublesum(appwxje,appzfbje));
				}
			}
			for(int i = 0 ; i < mzxx_zf_list.size() ; i ++){
				Map<String,Object> mzxx_zf = mzxx_zf_list.get(i);
				sfhj = BSHISUtil.doublesub(sfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2));
				zfhj = BSHISUtil.doublesum(zfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2));
				zfzf = BSHISUtil.doublesub(zfzf,BSPHISUtil.getDouble(mzxx_zf.get("ZHJE"),2));
				double wxje =BSPHISUtil.getDouble(mzxx_zf.get("WXJE"),2);//微信金额
				double zfbje = BSPHISUtil.getDouble(mzxx_zf.get("ZFBJE"),2);//支付宝金额
				double appwxje =BSPHISUtil.getDouble(mzxx_zf.get("APPWXJE"),2);//app微信金额
				double appzfbje = BSPHISUtil.getDouble(mzxx_zf.get("APPZFBJE"),2);//app支付宝金额
				wxhj = BSHISUtil.doublesub(wxhj,wxje);//微信合计
				zfbhj = BSHISUtil.doublesub(zfbhj,zfbje);//支付宝合计
				appwxhj = BSHISUtil.doublesub(appwxhj,appwxje);//app微信合计
				appzfbhj = BSHISUtil.doublesub(appzfbhj,appzfbje);//app支付宝合计
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				Long brxz = Long.parseLong(mzxx_zf.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxz);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				if("1000".equals(mzxx_zf.get("BRXZ")+"")){
					//zffshj=BSPHISUtil.getDouble(zffshj-BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2),2);
					zffshj=BSHISUtil.doublesum(BSHISUtil.doublesub(zffshj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					zffshj=BSHISUtil.doublesum(zffshj,BSHISUtil.doublesum(appwxje,appzfbje));
				}
				if("3000".equals(mzxx_zf.get("BRXZ")+"") || "2000".equals(mzxx_zf.get("BRXZ")+"")){
					//ybzfhj=BSPHISUtil.getDouble(ybzfhj-BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2),2);
					ybzfhj=BSHISUtil.doublesum(BSHISUtil.doublesub(ybzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					ybzfhj=BSHISUtil.doublesum(ybzfhj,BSHISUtil.doublesum(appwxje,appzfbje));
				}else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(mzxx_zf.get("XJJE"),2),2);
				}
				if("6000".equals(mzxx_zf.get("BRXZ")+"")){
					//nhzfhj=BSPHISUtil.getDouble(nhzfhj-BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2),2);
					nhzfhj=BSHISUtil.doublesum(BSHISUtil.doublesub(nhzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					nhzfhj=BSHISUtil.doublesum(nhzfhj,BSHISUtil.doublesum(appwxje,appzfbje));
				}
			}
			sfhj=BSPHISUtil.getDouble(sfhj,2);
			zfhj=BSPHISUtil.getDouble(zfhj,2);
			String zfxx = "张数：" + zffpxx_list.size() + " 作废金额：" + String.format("%1$.2f", zfhj);// + " 作废自负：" + String.format("%1$.2f", zfzf);
			res.put("invalidAmount", zfxx);//作废合计
			res.put("total", String.format("%1$.2f", sfhj));//划价合计
			StringBuffer hql_grmx = new StringBuffer();// 统计收费信息MS_MZXX
			StringBuffer hql_grthmx = new StringBuffer();// 统计收费作废信息MS_MZXX
			hql_grmx.append(" SELECT (a.GHJE + a.ZLJE + a.ZJFY + a.BLJE ) as ZJJE,a.GHJE as GHJE,a.ZLJE as ZLJE,a.ZJFY as ZJFY,a.BLJE as BLJE,a.XJJE as XJJE,a.JZHM as FPHM,a.YZBZ as YZBZ,a.YZJM as YZJM,CASE WHEN FFFS=32 THEN ZPJE WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE,a.ZHJE as ZHJE,a.BRXZ as BRXZ,a.YHJE as YHJE,a.QTYS as QTYS,a.HBWC as HBWC from MS_GHMX a")
					.append(" where a.JGID =:jgid and a.JZRQ IS NULL and a.MZLB =:mzlb and a.CZGH=:czgh and a.CZSJ<=:jzrq order by a.JZHM");
			hql_grthmx.append(" SELECT (a.GHJE + a.ZLJE + a.ZJFY + a.BLJE ) as ZJJE,a.GHJE as GHJE,a.ZLJE as ZLJE,a.ZJFY as ZJFY,a.BLJE as BLJE,a.XJJE as XJJE,a.JZHM as FPHM,a.YZBZ as YZBZ,a.YZJM as YZJM,CASE WHEN FFFS=32 THEN ZPJE WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE,a.ZHJE as ZHJE,a.BRXZ as BRXZ,a.YHJE as YHJE from MS_GHMX a,MS_THMX b")
					.append(" where a.SBXH = b.SBXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB =:mzlb and b.CZGH=:czgh and b.THRQ<=:jzrq order by a.JZHM");
			List<Map<String, Object>> grmx_list = dao.doSqlQuery(hql_grmx.toString(), parameters);
			String jzhmxx = getFPXX(grmx_list);
			res.put("JZHM", jzhmxx);//就诊号码集;
			List<Map<String, Object>> grthmx_list = dao.doSqlQuery(hql_grthmx.toString(), parameters);
			List<Map<String, Object>> thxx_list = getFPList(grthmx_list);
			String thjzhmxx = getFPXX(thxx_list);
			res.put("invalidgh", thjzhmxx);//退号就诊号码
			double ghhj = 0;
			double thhj = 0;
//			double thzf = 0;
			double ghje = 0 ;
			double zlje = 0 ;
			double blje = 0 ;
			double zjfy = 0 ;
			double yzjm = 0;
			double ghsmze = 0;//挂号扫码总额
			double ghwxhj = 0;//挂号微信合计
			double ghzfbhj = 0;//挂号支付宝合计
			double ghyhhj = 0;//zhaojian 2019-05-12 增加优惠金额合计
			String ghmsg = "";//挂号自动对账信息（发票）
			String ghxzmsg = "";//挂号自动对账信息（病人性质）
			for(int i = 0 ; i < grmx_list.size() ; i ++){
				Map<String,Object> grmx = grmx_list.get(i);
				if("1".equals(grmx.get("YZBZ")+"")){
					yzjm = BSHISUtil.doublesum(yzjm,BSPHISUtil.getDouble(grmx.get("YZJM"),2));
				}
				ghhj = BSHISUtil.doublesum(ghhj,BSPHISUtil.getDouble(grmx.get("ZJJE"),2));
				ghje = BSHISUtil.doublesum(ghje,BSPHISUtil.getDouble(grmx.get("GHJE"),2));
				zlje = BSHISUtil.doublesum(zlje,BSPHISUtil.getDouble(grmx.get("ZLJE"),2));
				blje = BSHISUtil.doublesum(blje,BSPHISUtil.getDouble(grmx.get("BLJE"),2));
				zjfy = BSHISUtil.doublesum(zjfy,BSPHISUtil.getDouble(grmx.get("ZJFY"),2));
				double ghwxje =BSPHISUtil.getDouble(grmx.get("WXJE"),2);//微信金额
				double ghzfbje = BSPHISUtil.getDouble(grmx.get("ZFBJE"),2);//支付宝金额
				ghwxhj = BSHISUtil.doublesum(ghwxhj,ghwxje);//挂号微信合计
				ghzfbhj = BSHISUtil.doublesum(ghzfbhj,ghzfbje);//挂号支付宝合计
				zfzf = BSHISUtil.doublesum(zfzf,BSPHISUtil.getDouble(grmx.get("ZHJE"),2));
				//zhaojian 2019-05-12 增加优惠金额合计
				ghyhhj = BSHISUtil.doublesum(ghyhhj,BSPHISUtil.getDouble(grmx.get("YHJE"),2));
				Long brxz = Long.parseLong(grmx.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxz);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				if("1000".equals(grmx.get("BRXZ")+"")){
					//zffshj=BSHISUtil.doublesub(BSHISUtil.doublesum(zffshj,BSPHISUtil.getDouble(grmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					zffshj=BSHISUtil.doublesum(zffshj,BSPHISUtil.getDouble(grmx.get("XJJE"),2));
				}
				if("3000".equals(grmx.get("BRXZ")+"") || "2000".equals(grmx.get("BRXZ")+"")){
					//ybzfhj=BSHISUtil.doublesub(BSHISUtil.doublesum(ybzfhj,BSPHISUtil.getDouble(grmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					ybzfhj=BSHISUtil.doublesum(ybzfhj,BSPHISUtil.getDouble(grmx.get("XJJE"),2));
				}/*else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(grmx.get("XJJE"),2),2);
				}*/
				/****************add by hujian 2020-03-24 挂号自动对账分析，错误输出报告*****************************************/
				double hjje = BSPHISUtil.getDouble(grmx.get("ZJJE"),2);//单个发票合计金额
				double smjehz = BSHISUtil.doublesum(BSPHISUtil.getDouble(grmx.get("WXJE"),2),BSPHISUtil.getDouble(grmx.get("ZFBJE"),2));//扫码金额
				double yzje = BSPHISUtil.getDouble(grmx.get("YZJM"),2);
//				double apphzje = BSHISUtil.doublesum(BSPHISUtil.getDouble(mzxx.get("APPWXJE"),2),BSPHISUtil.getDouble(mzxx.get("APPZFBJE"),2));//窗口扫码汇总金额
				double xjje = BSPHISUtil.getDouble(grmx.get("XJJE"),2);//现金金额
				double zhje = BSPHISUtil.getDouble(grmx.get("ZHJE"),2);//账户金额
				double qtysje = BSPHISUtil.getDouble(grmx.get("QTYS"),2);//医保金额
				double yhje = BSPHISUtil.getDouble(grmx.get("YHJE"),2);//优惠金额
				double hz = BSHISUtil.doublesum(BSHISUtil.doublesum(xjje,qtysje),BSHISUtil.doublesum(zhje,yhje));//汇总下：现金金额+医保金额+账户金额+优惠
				double hzje = BSHISUtil.doublesum(hz,smjehz)+yzje;//（现金金额+医保金额+账户金额+优惠）+扫码金额=总金额
				double ghhbwc = BSPHISUtil.getDouble(grmx.get("HBWC"),2);//货币误差
				if(ghhbwc>0.5){
					String FPHM = (String) grmx.get("FPHM");//发票号码
					ghmsg = ghmsg+"货币误差过大，" +"发票号码:"+FPHM+"!";
				}
				if(hjje!=hzje){
					String FPHM = (String) grmx.get("FPHM");//发票号码
					double wcje = BSHISUtil.doublesub(hzje,hjje);//误差金额
					ghmsg = ghmsg +"异常发票号码:"+FPHM+"，"+"发票应收总金额："+hjje+",实收金额："+hzje+"，误差："+wcje+"，明细：现金金额："+xjje+"记账金额："
							+BSPHISUtil.getDouble(grmx.get("ZHJE"),2)+","+"医保金额："+qtysje+",";
				}
				/*************************************病人性质不在（1000,2000，3000）会造成报表不平，自动对账************************************************/
				if(!"1000".equals(grmx.get("BRXZ")+"")&&!"2000".equals(grmx.get("BRXZ")+"")){
					ghxzmsg = ghxzmsg +"挂号发票号码："+grmx.get("FPHM")+"，病人性质有误为："+grmx.get("BRXZ")+",";
				}
			}
			for(int i = 0 ; i < grthmx_list.size() ; i ++){
				Map<String,Object> grthmx = grthmx_list.get(i);
				//double yzjm = 0;
				if("1".equals(grthmx.get("YZBZ")+"")){
					yzjm = BSHISUtil.doublesub(yzjm,BSPHISUtil.getDouble(grthmx.get("YZJM"),2));
				}
				ghhj = BSHISUtil.doublesub(ghhj,BSPHISUtil.getDouble(grthmx.get("ZJJE"),2));
				thhj = BSHISUtil.doublesum(thhj,BSPHISUtil.getDouble(grthmx.get("ZJJE"),2));
//				thzf += BSPHISUtil.getDouble(grthmx.get("XJJE"),2);
				ghje = BSHISUtil.doublesub(ghje,BSPHISUtil.getDouble(grthmx.get("GHJE"),2));
				zlje = BSHISUtil.doublesub(zlje,BSPHISUtil.getDouble(grthmx.get("ZLJE"),2));
				blje = BSHISUtil.doublesub(blje,BSPHISUtil.getDouble(grthmx.get("BLJE"),2));
				zjfy = BSHISUtil.doublesub(zjfy,BSPHISUtil.getDouble(grthmx.get("ZJFY"),2));
				//zhaojian 2019-05-12 增加优惠金额合计
				ghyhhj = BSHISUtil.doublesub(ghyhhj,BSPHISUtil.getDouble(grthmx.get("YHJE"),2));
				double ghwxje =BSPHISUtil.getDouble(grthmx.get("WXJE"),2);//挂号微信金额
				double ghzfbje = BSPHISUtil.getDouble(grthmx.get("ZFBJE"),2);//挂号支付宝金额
				ghwxhj = BSHISUtil.doublesub(ghwxhj,ghwxje);//挂号微信合计
				ghzfbhj = BSHISUtil.doublesub(ghzfbhj,ghzfbje);//挂号支付宝合计
				zfzf = BSHISUtil.doublesub(zfzf,BSPHISUtil.getDouble(grthmx.get("ZHJE"),2));
				Long brxz = Long.parseLong(grthmx.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxz);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				if("1000".equals(grthmx.get("BRXZ")+"")){
					//zffshj=BSHISUtil.doublesum(BSHISUtil.doublesub(zffshj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					zffshj=BSHISUtil.doublesub(zffshj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2));
				}
				if("3000".equals(grthmx.get("BRXZ")+"") || "2000".equals(grthmx.get("BRXZ")+"")){
					//ybzfhj=BSHISUtil.doublesum(BSHISUtil.doublesub(ybzfhj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					ybzfhj=BSHISUtil.doublesub(ybzfhj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2));
				}/*else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(grthmx.get("XJJE"),2),2);
				}*/
			}
			zfzf=BSPHISUtil.getDouble(zfzf,2);
			res.put("ZFZF", String.format("%1$.2f", zfzf));
			//ghhj = BSHISUtil.doublesub(ghhj,yzjm);
			String thxx = "张数：" + grthmx_list.size() + " 退号金额：" + String.format("%1$.2f", thhj);// + " 退号自负：" + String.format("%1$.2f", thzf);
			res.put("invalidghAmount", thxx);//作废合计
			res.put("totalgh", String.format("%1$.2f", ghhj));//划价合计
			if(yzjm>0 || ghyhhj>0){
				res.put("totalgh", String.format("%1$.2f", ghhj)+"(其中减免"+String.format("%1$.2f",BSHISUtil.doublesum(yzjm,ghyhhj))+")");
			}
			res.put("GHF", String.format("%1$.2f",ghje));
			res.put("ZLF", String.format("%1$.2f",zlje));
			res.put("BLF", String.format("%1$.2f",blje));
			res.put("ZJF", String.format("%1$.2f",zjfy));
			res.put("YZJM", String.format("%1$.2f",yzjm));//义诊减免
			res.put("ghwxhj", String.format("%1$.2f",ghwxhj));//挂号微信合计
			res.put("ghzfbhj", String.format("%1$.2f",ghzfbhj));//挂号支付宝合计
			res.put("ghyhhj", String.format("%1$.2f",ghyhhj));//2019-05-12 zhaojian 挂号优惠合计
			if (sfhj + ghhj < 0) {
				res.put("amountIn","负" + BSPHISUtil.changeMoneyUpper(String.format("%1$.2f", -(sfhj + ghhj))));
			} else {
				res.put("amountIn", BSPHISUtil.changeMoneyUpper(String.format("%1$.2f", sfhj + ghhj)));
			}
			StringBuffer sql_fkfs = new StringBuffer("select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from (")
					.append("select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b where a.MZXH = b.MZXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.SFRQ<=:jzrq")
					.append(" union all ")
					.append("select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.ZFRQ<=:jzrq")
					.append(" union all ")
					.append("select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.CZSJ<=:jzrq")
					.append(" union all ")
					.append("select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.THRQ<=:jzrq")
					.append(") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS");
			StringBuffer sql_brxz = new StringBuffer("select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,nvl(d.DBPB,0) as DBPB from (")
					.append("select BRXZ as BRXZ,QTYS as QTYS from MS_MZXX" +
							" where JGID=:jgid and JZRQ IS NULL and MZLB = :mzlb and CZGH = :czgh and SFRQ<=:jzrq")
					.append(" union all ")
					.append("select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.ZFRQ<=:jzrq")
					.append(" union all ")
					.append("select BRXZ as BRXZ,QTYS as QTYS from MS_GHMX where JGID=:jgid and JZRQ IS NULL and MZLB = :mzlb and CZGH = :czgh and CZSJ<=:jzrq")
					.append(" union all ")
					.append("select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.THRQ<=:jzrq")
					.append(") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB");
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs.toString(),parameters);
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz.toString(),parameters);
			String  qtysFb="";
			String jzjeSt="0.00";
			String xjje="0.00";

			if (ids_fkfs  != null && ids_fkfs .size() != 0) {
				for(int j=0;j<ids_fkfs.size();j++){
					qtysFb = qtysFb +ids_fkfs.get(j).get("FKMC")+ ":"
							+ String.format("%1$.2f",ids_fkfs.get(j).get("FKJE"))
							+ " ";
					if("1".equals(ids_fkfs.get(j).get("FKFS")+"")){//现金支付
						xjje=String.format("%1$.2f",Double.parseDouble(xjje)
								+Double.parseDouble(ids_fkfs.get(j).get("FKJE")+""));
					}
					if("4".equals(ids_fkfs.get(j).get("FKFS")+"")){//货币误差
						xjje=String.format("%1$.2f",Double.parseDouble(xjje)
								+Double.parseDouble(ids_fkfs.get(j).get("FKJE")+""));
					}
				}
			}
/*			if(ghhj>0){
				xjje=String.format("%1$.2f",Double.parseDouble(xjje)-ghhj);
			}*/
			//扫码支付结算合计
			smze = BSHISUtil.doublesum(wxhj , zfbhj);
			//app结算支付合计
			appze = BSHISUtil.doublesum(appwxhj , appzfbhj);
			//挂号扫码支付合计
			ghsmze= BSHISUtil.doublesum(ghwxhj , ghzfbhj);
			//自费现金=自费合计-扫码支付结算合计-app支付结算合计-挂号扫码支付合计
			//zffshj = BSHISUtil.doublesum(BSHISUtil.doublesub(zffshj , smze),  BSHISUtil.doublesub(ghje , ghsmze));
			//zffshj = BSHISUtil.doublesum(zffshj,  BSHISUtil.doublesub(ghhj , ghsmze));
			res.put("smze", String.format("%1$.2f",smze));
			res.put("wxhj", String.format("%1$.2f",wxhj));
			res.put("zfbhj", String.format("%1$.2f",zfbhj));
			res.put("appze", String.format("%1$.2f",appze));
			res.put("appwxhj", String.format("%1$.2f",appwxhj));
			res.put("appzfbhj", String.format("%1$.2f",appzfbhj));
			res.put("ghsmze", String.format("%1$.2f",ghsmze));
			res.put("ghwxhj", String.format("%1$.2f",ghwxhj));
			res.put("ghzfbhj", String.format("%1$.2f",ghzfbhj));

			qtysFb = qtysFb+"农合自付："+String.format("%1$.2f",nhzfhj)+" "+"医保自付："+String.format("%1$.2f",ybzfhj)+" "+"自费自付："+String.format("%1$.2f",zffshj)+" ";

			res.put("nhzfhj", String.format("%1$.2f",nhzfhj));
			res.put("ybzfhj", String.format("%1$.2f",ybzfhj));
			res.put("zffshj", String.format("%1$.2f",zffshj));
			res.put("xjje", xjje);
			String ybjz="0.00";//医保记账
			String nhjz="0.00";//农合记账
			String jzze="0.00";//记账总额
			if (ids_brxz  != null && ids_brxz .size() != 0) {
				for(int j=0;j<ids_brxz.size();j++){
					if(Integer.parseInt(ids_brxz.get(j).get("DBPB")+"")==0){
						jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(j).get("QTYS")+ ""));
					}else{
						qtysFb = qtysFb +ids_brxz.get(j).get("XZMC");
/*						 if("2000".equals(ids_brxz.get(j).get("BRXZ")+"")){
							 qtysFb+="统筹:";
						 }else{*/
						qtysFb+="记账:";
						//}
						qtysFb+=String.format("%1$.2f",parseDouble(ids_brxz.get(j).get("QTYS")+ ""))+ " ";
						if("3000".equals(ids_brxz.get(j).get("BRXZ")+"") || "2000".equals(ids_brxz.get(j).get("BRXZ")+"")){
							ybjz=String.format("%1$.2f",parseDouble(ids_brxz.get(j).get("QTYS")+ ""));
						}
						if("6000".equals(ids_brxz.get(j).get("BRXZ")+"")){
							nhjz=String.format("%1$.2f",parseDouble(ids_brxz.get(j).get("QTYS")+ ""));
						}
					}
				}
				qtysFb = qtysFb+"南京金保账户:"+String.format("%1$.2f",zfzf)+" "+"其他记账 :"+jzjeSt;
				jzze=String.format("%1$.2f",parseDouble(jzjeSt)+parseDouble(ybjz)+parseDouble(nhjz)+zfzf);
				res.put("ybjz", ybjz);
				res.put("nhjz", nhjz);
				res.put("jzjeSt", jzjeSt);
				res.put("jzze", jzze);
			}
			res.put("qtysFb", qtysFb);
			/*********************************自动对账输出分析报告*********************************************************/
			Map<String, Object> mzdzrzParameters = new HashMap<String, Object>();
			if(zddzmsg.length()>0){
				String msg = "有门诊发票存在账目不平，请核对，"+zddzmsg;
				mzdzrzParameters.put("mzdz",msg);
				mzdzrzParameters.put("jgid",jgid);
				mzdzrzParameters.put("czgh", uid);
				String sql="insert into ZDDZ_RZ (rz,sj,jgid,czgh) values (:mzdz,sysdate,:jgid,:czgh)";
				dao.doSqlUpdate(sql, mzdzrzParameters);
				res.put("dzmsg", msg);
			}else{
				res.put("dzmsg", "***************校对完成：未发现异常门诊发票，如有必要请再次手工核对***************");
			}
			if(ghmsg.length()>0){
				String msg = "有挂号发票存在账目不平，请核对，"+ghmsg;
				mzdzrzParameters.put("ghdz",msg);
				mzdzrzParameters.put("jgid",jgid);
				mzdzrzParameters.put("czgh", uid);
				String sql="insert into ZDDZ_RZ (rz,sj,jgid,czgh) values (:ghdz,sysdate,:jgid,:czgh)";
				dao.doSqlUpdate(sql, mzdzrzParameters);
				res.put("ghmsg", msg);
			}else{
				res.put("ghmsg", "***************校对完成：未发现异常挂号发票，如有必要请再次手工核对***************");
			}
			if(ghxzmsg.length()>0){
				String msg = "病人建档数据存在错误，请联系管理员修改，"+ghxzmsg;
				mzdzrzParameters.put("jd",msg);
				mzdzrzParameters.put("jgid",jgid);
				mzdzrzParameters.put("czgh", uid);
				String sql="insert into ZDDZ_RZ (rz,sj,jgid,czgh) values (:jd,sysdate,:jgid,:czgh)";
				dao.doSqlUpdate(sql, mzdzrzParameters);
				res.put("ghxzmsg", msg);
			}else{
				res.put("ghxzmsg","***************校对完成：未发现异常建档数据，如有必要请再次手工核对***************");
			}


		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	public List<Map<String, Object>> getFPList(List<Map<String, Object>> mzgh_list){
		List<Map<String, Object>> fpxx_list = new ArrayList<Map<String,Object>>();
		for(int i = 0 ; i < mzgh_list.size(); i ++){
			Map<String, Object> map_fpxx = mzgh_list.get(i);
			if(map_fpxx.get("FPZS")!=null && Long.parseLong(map_fpxx.get("FPZS")+"")>1){
				for(int j = 1 ; j < Long.parseLong(map_fpxx.get("FPZS")+"") ; j ++){
					String fphm = map_fpxx.get("FPHM")+"";
					Map<String, Object> fphm_map1 = new HashMap<String, Object>();
					fphm_map1.put("FPHM", fphm);
					fpxx_list.add(fphm_map1);
					int k = -1 ;
					for(int q = fphm.length()-1 ; q >= 0 ; q --){
						if(fphm.charAt(q)<'0'||fphm.charAt(q)>'9'){
							k = q;
							break;
						}
					}
					String fphmzm = fphm.substring(0, k+1);
					String fphmsz = fphm.substring(k+1);
					fphm = fphmzm+String.format("%0" + fphmsz.length() + "d", Long.parseLong(fphmsz+"")+j);
					Map<String, Object> fphm_map2 = new HashMap<String, Object>();
					fphm_map2.put("FPHM", fphm);
					fpxx_list.add(fphm_map2);
				}
			}else{
				String fphm = map_fpxx.get("FPHM")+"";
				Map<String, Object> fphm_map = new HashMap<String, Object>();
				fphm_map.put("FPHM", fphm);
				fpxx_list.add(fphm_map);
			}
		}
		return fpxx_list;

	}
	public String getFPXX(List<Map<String, Object>> fpxx_list){
		if(fpxx_list.size()==0)
			return "";
		String beginfphm = "";
		String endfphm = "";
		String currentfphm = "0";
		String fpxx = "";
		for (int i = 0; i < fpxx_list.size(); i++) {
			Map<String, Object> map_fpxx = fpxx_list.get(i);
			if (i == 0) {
				beginfphm = map_fpxx.get("FPHM").toString();
				currentfphm = map_fpxx.get("FPHM")
						.toString();
			}
			if (!map_fpxx.get("FPHM").toString().equals(currentfphm)) {
				endfphm = fpxx_list.get(i - 1).get("FPHM")
						.toString();
				fpxx = InvoiceSequence(fpxx, beginfphm, endfphm);
				beginfphm = map_fpxx.get("FPHM").toString();
				currentfphm = map_fpxx.get("FPHM")
						.toString();
			}
			int k = -1 ;
			for(int j = currentfphm.length()-1 ; j >= 0 ; j --){
				if(currentfphm.charAt(j)<'0'||currentfphm.charAt(j)>'9'){
					k = j;
					break;
				}
			}
			String currentfphmzm = currentfphm.substring(0, k+1);
			String currentfphmsz = currentfphm.substring(k+1);
			currentfphm = currentfphmzm+String.format("%0" + currentfphmsz.length() + "d", Long.parseLong(currentfphmsz+"")+1);
		}
		endfphm = fpxx_list.get(fpxx_list.size() - 1)
				.get("FPHM").toString();
		fpxx = InvoiceSequence(fpxx, beginfphm, endfphm);
		return fpxx;
	}
	@SuppressWarnings("unchecked")
	public void doQuerySQL(Map<String, Object> req, Map<String, Object> res,
						   BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("czgh", uid);
		parameters.put("jgid", jgid);
		StringBuffer hql1 = new StringBuffer();
		try {
			if (req.containsKey("cnd")) {
				List<Object> listCND = (List<Object>) req.get("cnd");
				String cnd = ExpressionProcessor.instance().toString(listCND);
				cnd = cnd.replaceAll("str", "to_char");
				hql1.append("SELECT distinct JZRQ as JZRQ from (SELECT to_char(JZRQ,'yyyy-mm-dd hh24:mi:ss') as JZRQ FROM MS_HZRB ")
						.append(" WHERE CZGH =:czgh and JGID =:jgid and ")
						.append(cnd)
						.append("union all SELECT to_char(JZRQ,'yyyy-mm-dd hh24:mi:ss') as JZRQ FROM MS_GHRB ")
						.append(" WHERE CZGH =:czgh and JGID =:jgid and ")
						.append(cnd)
						.append(") order by JZRQ desc");
			}
			List<Map<String, Object>> listSQL1 = dao.doSqlQuery(hql1.toString(),
					parameters);
			res.put("body", listSQL1);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		}
	}
	public String InvoiceSequence(String fpxx, String beginfphm, String endfphm) {
		if (beginfphm.equals(endfphm)) {
			fpxx += " " + beginfphm;
		} else {
			fpxx += " " + beginfphm + "-" + endfphm;
		}
		return fpxx;
	}

	public List<Map<String, Object>> gethmNUm(List<Map<String, Object>> hmxx) {
		for (int i = 0; i < hmxx.size(); i++) {
			String hm = null;
			if (hmxx.get(i).containsKey("FPHM")) {
				hm = hmxx.get(i).get("FPHM") + "";
			}
			for (int j = 0; j < hm.length(); j++) {
				if (hm.charAt(j) < '0' || hm.charAt(j) > '9') {
					hmxx.remove(i);
					i--;
					break;
				}
			}
		}
		return hmxx;
	}

	public String gethmStr(List<Map<String, Object>> hmxx) {
		String strnum = "";
		for (int i = 0; i < hmxx.size(); i++) {
			String hm = null;
			if (hmxx.get(i).containsKey("FPHM")) {
				hm = hmxx.get(i).get("FPHM") + "";
			}
			for (int j = 0; j < hm.length(); j++) {
				if (hm.charAt(j) < '0' || hm.charAt(j) > '9') {
					strnum += " " + hmxx.get(i).get("FPHM");
					break;
				}
			}
		}
		return strnum;
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
