package phis.application.stm.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

public class SkinTestManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(SkinTestManageModel.class);

	public SkinTestManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> loadPatient(Object key)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select a.BRID as BRID,b.MZHM as MZHM,b.BRXM,b.BRXB,b.BRXZ,b.CSNY,a.CFHM,"
				+ BSPHISUtil.toChar("a.KFRQ", "yyyy-mm-dd hh24:mi:ss")
				+ " as KFRQ,a.KSDM,a.YSDM,a.CFLX,b.HKDZ from MS_CF01 a,MS_BRDA b where a.BRID=b.BRID and a.CFSB=:CFSB";
		try {
			params.put("CFSB", Long.parseLong(key.toString()));
			List<Map<String, Object>> list = dao.doSqlQuery(hql, params);
			if (list.size() > 0) {
				Map<String, Object> m = list.get(0);
				SchemaUtil.setDictionaryMassageForForm(m,
						"phis.application.stm.schemas.MS_CF01_PS");
				int age = BSHISUtil.calculateAge((Date) m.get("CSNY"), null);
				m.put("NL", age);
				return m;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取皮试病人信息失败", e);
		}
		return null;
	}

	public void doSaveStartSkinTest(Map<String, Object> body)
			throws ModelDataOperationException {
		// 判断皮试信息是否有效
		// String sql =
		// "select JLBH as JLBH from YS_MZ_PSJL where YPBH=:YPXH and CFSB=:CSFB and JGID=:JGID and WCBZ=0";
		UserRoleToken user = UserRoleToken.getCurrent();
		body.put("YSDM", user.getUserId());
		body.put("PSSJ", new Date());
		try {
			String updateSql = "update YS_MZ_PSJL set WCBZ=9,PSYS=:YSDM,PSSJ=:PSSJ where YPBH=:YPXH and CFSB=:CFSB and JGID=:JGID and WCBZ=0";
			int i = dao.doSqlUpdate(updateSql, body);
			if (i == 0) {
				throw new ModelDataOperationException("无效的皮试信息,请检查数据是否正确!");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("无效的皮试信息,请检查数据是否正确", e);
		}

	}

	public void doSaveStopSkinTest(Map<String, Object> body)
			throws ModelDataOperationException {
		// 判断皮试信息是否有效
		try {
			String updateSql = "update YS_MZ_PSJL set WCBZ=0,PSYS=null,PSSJ=null where YPBH=:YPXH and CFSB=:CFSB and JGID=:JGID and WCBZ=9";
			int i = dao.doSqlUpdate(updateSql, body);
			if (i == 0) {
				throw new ModelDataOperationException("无效的皮试信息,请刷新后重试!");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("无效的皮试信息,请检查数据是否正确", e);
		}

	}

	public void doSaveSkinTestResult(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.putAll(body);
			params.remove("BRID");
			String updateSql = "update YS_MZ_PSJL set WCBZ=1,PSJG=:PSJG,GMPH=:GMPH where YPBH=:YPXH and CFSB=:CFSB and JGID=:JGID and WCBZ=9";
			int i = dao.doSqlUpdate(updateSql, params);
			if (i == 0) {
				throw new ModelDataOperationException("无效的皮试信息,请刷新后重试!");
			}
			params.remove("GMPH");
			String updateCf02 = "update MS_CF02 set PSJG=:PSJG where CFSB=:CFSB and YPXH=:YPXH and JGID=:JGID";
			dao.doSqlUpdate(updateCf02, params);
			if ("-1".equals(body.get("PSJG"))) {
				params.clear();
				params.put("CFSB", body.get("CFSB"));
				String updateCf01 = "update MS_CF01 set CFBZ=0 where CFSB=:CFSB and CFBZ=2";
				dao.doSqlUpdate(updateCf01, params);

				// String updateYJ01CFBZ =
				// "update MS_YJ01 set CFBZ=0 where CFBZ=2 and JZXH=(select JZXH from MS_CF01 where CFSB=:CFSB)";
				// dao.doSqlUpdate(updateYJ01CFBZ, params);
			}
			if ("1".equals(body.get("PSJG"))) { // 阳性时插入过敏药物信息
				Map<String, Object> gy_psjl = new HashMap<String, Object>();
				gy_psjl.put("BRID", body.get("BRID"));
				gy_psjl.put("YPXH", body.get("YPXH"));
				gy_psjl.put("JGID", body.get("JGID"));
				gy_psjl.put("PSJG", 1);
				gy_psjl.put("PSLY", 1);
				dao.doSave("create", "phis.application.cic.schemas.GY_PSJL_MZ",
						gy_psjl, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("无效的皮试信息,请检查数据是否正确", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("过敏药物保存失败,请检查数据是否正确", e);
		}
	}

	public void doRemoveSkinTest(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			// 判断是否收费
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("CFSB", Long.parseLong(body.get("CFSB").toString()));
			String hql = "select a.FPHM as FPHM,a.ZFPB as ZFPB,b.PSPB as PSPB from MS_CF01 a,MS_CF02 b where a.CFSB=b.CFSB and a.CFSB=:CFSB and a.ZFPB=0 and b.PSPB>0";
			List<Map<String, Object>> l = dao.doQuery(hql, params);
			if (l == null || l.size() == 0) {
				throw new ModelDataOperationException("无效的处方信息,请刷新后重试!");
			}
			Object fphm = l.get(0).get("FPHM");
			Object pspb = l.get(0).get("PSPB");
			if (fphm != null && fphm.toString().trim().length() > 0
					&& pspb != null && !pspb.toString().equals("2")) {
				throw new ModelDataOperationException("当前处方已收费,不能进行取消皮试操作!");
			}
			params.put("YPXH", body.get("YPXH"));
			params.put("JGID", body.get("JGID"));
			String updateSql = "update YS_MZ_PSJL a set a.WCBZ=0,a.PSJG=null,GMPH=null,PSYS=null,PSSJ=null where a.YPBH=:YPXH and a.CFSB=:CFSB and a.JGID=:JGID and a.WCBZ=1";
			int i = dao.doSqlUpdate(updateSql, params);
			if (i == 0) {
				throw new ModelDataOperationException("无效的皮试信息,请刷新后重试!");
			}
			// if ("-1".equals(body.get("PSJG"))) {
			String updateCf02 = "update MS_CF02 set PSJG=null where YPXH=:YPXH and CFSB=:CFSB and JGID=:JGID";
			dao.doSqlUpdate(updateCf02, params);
			params.clear();
			params.put("CFSB", Long.parseLong(body.get("CFSB").toString()));
			String updateCf01 = "update MS_CF01 set CFBZ=2 where CFSB=:CFSB";
			dao.doSqlUpdate(updateCf01, params);

			params.clear();
			if ("1".equals(body.get("PSJG") + "")) {
				params.put("YPXH", body.get("YPXH"));
				params.put("JGID", body.get("JGID"));
				params.put("BRID", Long.parseLong(body.get("BRID").toString()));
				dao.doSqlUpdate(
						"delete GY_PSJL where BRID=:BRID and JGID=:JGID and PSJG=1 and YPXH=:YPXH",
						params);
			}
			// String updateYJ01CFBZ =
			// "update MS_YJ01 set CFBZ=0 where CFBZ=2 and JZXH=(select JZXH from MS_CF01 where CFSB=:CFSB)";
			// dao.doSqlUpdate(updateYJ01CFBZ, params);

			// }
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("无效的皮试信息,请检查数据是否正确", e);
		}

	}
}
