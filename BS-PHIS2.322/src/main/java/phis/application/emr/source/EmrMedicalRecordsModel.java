package phis.application.emr.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPEMRUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;


import ctd.account.UserRoleToken;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class EmrMedicalRecordsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(EmrMedicalRecordsModel.class);

	public EmrMedicalRecordsModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doLoadMedicalRecords(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = (String)user.getUserId();
		long ZYH = Long.parseLong(req.get("ZYH") + "");
		int BLLB = Integer.parseInt(req.get("BLLB") + "");
		Map<String, Object> Control = BSPEMRUtil.getCaseHistoryContory(BLLB+ "", dao, ctx);
		res.put("CKQX", Control.get("CKQX"));
		if ("0".equals(Control.get("CKQX") + "")) {
			return;
		}
		res.put("SXQX", Control.get("SXQX"));
		res.put("DYQX", Control.get("DYQX"));
		res.put("SYQX", Control.get("SYQX"));
		List<Map<String, Object>> update = (List<Map<String, Object>>) req.get("autoUpdate");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		List<Object> cnd = (List<Object>) req.get("cnd");

		try {
			Map<String, Object> JLXHparameters = new HashMap<String, Object>();
			JLXHparameters.put("ZYH", ZYH);
			JLXHparameters.put("BLLB", BLLB);
			Map<String, Object> JLXH = dao.doLoad(
					"select a.BLBH as JLXH,a.BLZT as BLZT,a.SXYS as SXYS from EMR_BL01 a where a.JZXH = :ZYH and a.BLLB = :BLLB",
					JLXHparameters);
			int count = 0;
			if (JLXH != null) {
				count = JLXH.size();
			}
			if (count == 0) {
				Map<String, Object> JBXX = dao.doLoad("SELECT a.ZYH as JZXH,a.BRCH as BRCH,a.BRID as BRID," +
						" a.ZYHM as ZYHN,a.BAHM as BAHM,a.SFZH as SFZJHM,a.BRXM as BRXM,a.BRXB as BRXB," +
						" a.CSNY as CSNY,a.RYNL as BRNL,a.ZYCS as ZYCS,a.HYZK as HYDM,a.ZYDM as ZYDM," +
						" a.MZDM as MZDM,a.GJDM as GJDM,a.GZDW as DWDZ,a.DWDH as DWDH,a.DWYB as DWYB," +
						" a.HKDZ as HKDZ,a.JTDH as JTDH,a.HKYB as HKDZ_YB,a.LXRM as LXRXM,a.XZZ_DH as XZZ_DH," +
						" a.XZZ_YB as XZZ_YB,a.LXGX as LXRGX,a.LXDZ as LXRDZ,a.LXDH as LXRDH,a.RYRQ as RYRQ," +
						" a.BRKS as RYKS,a.BRBQ as BRBQ,a.CYRQ as CYRQ,a.CSD_SQS as CSD_SQS,a.CSD_S as CSD_S," +
						" a.CSD_X as CSD_X,a.HKDZ_SQS as HKDZ_SQS,a.HKDZ_S as HKDZ_S ,a.HKDZ_X as HKDZ_X," +
						" a.HKDZ_QTDZ as HKDZ_DZ,a.XZZ_SQS as XZZ_SQS,a.XZZ_S as XZZ_S,a.XZZ_X as XZZ_X," +
						" a.XZZ_QTDZ as XZZ_DZ,a.JGDM_SQS as JGDM_SQS,a.JGDM_S as JGDM_S FROM " +
						"ZY_BRRY a WHERE a.ZYH = :ZYH", parameters);
				List<Map<String, Object>> ZKXX_list = dao.doQuery(
						"SELECT (SELECT OFFICENAME FROM SYS_Office b WHERE a.HHKS = b.ID) as ZKKSMC FROM "
								+ "ZY_HCMX a WHERE a.HCLX = 2 AND ZYH = :ZYH",parameters);
				Map<String,Object> ZKXX = new HashMap<String,Object>();
				String ZKKSMC = "";
				for(int i  = 0 ; i < ZKXX_list.size() ; i ++){
					if(i>0){
						ZKKSMC += "→"+ZKXX_list.get(i).get("ZKKSMC");
					}else{
						ZKKSMC = ZKXX_list.get(i).get("ZKKSMC")+"";
					}
				}
				if(ZKKSMC.length()>25){
					ZKKSMC = ZKKSMC.substring(0, 25);
				}
				ZKXX.put("ZKKSMC", ZKKSMC);
				List<Map<String, Object>> RYKS_list = dao.doQuery("SELECT a.HHKS as RYKS ,a.HHBQ as RYBF " +
						" FROM ZY_HCMX a WHERE a.HCLX = 0 AND a.ZYH = :ZYH order by a.HCRQ",parameters);
				if (RYKS_list.size() > 0) {
					Map<String, Object> RYKS = RYKS_list.get(0);
					JBXX.putAll(RYKS);
				}
				Map<String, Object> CYXX = dao.doLoad("SELECT a.BRKS as CYKS,a.BRBQ as CYBQ,CYRQ as CYRQ" +
						" FROM ZY_BRRY a WHERE a.ZYH = :ZYH",parameters);
				Map<String, Object> ZYTS = dao.doLoad("SELECT CYRQ-RYRQ as ZYTS FROM ZY_BRRY" +
						"  WHERE CYPB <> 0 AND CYPB <> 99  AND ZYH = :ZYH",parameters);
				if (ZKXX != null) {
					JBXX.putAll(ZKXX);
				}
				if (CYXX != null) {
					JBXX.putAll(CYXX);
				}
				if (ZYTS != null) {
					JBXX.putAll(ZYTS);
				}
				Map<String, Object> FYTJ = dao
						.doLoad("SELECT sum(a.ZJJE) as ZYZFY,sum(ZFJE) as ZFJE FROM ZY_FYMX a WHERE a.ZYH = :ZYH",
								parameters);
				JBXX.put("ZYZFY", 0);
				JBXX.put("ZFJE", 0);
				if (FYTJ != null) {
					if (FYTJ.get("ZYZFY") != null) {
						JBXX.putAll(FYTJ);
					}
				}
				Map<String, Object> KJYWFY = dao.doLoad("select sum(a.ZJJE) as KJYWFY from ZY_FYMX a,YK_TYPK b " +
						" where a.YPLX = 1 and a.FYXH = b.YPXH and b.KSBZ = 1 and a.ZYH = :ZYH",parameters);
				JBXX.put("KJYWFY", 0);
				if (KJYWFY != null) {
					if (KJYWFY.get("KJYWFY") != null) {
						JBXX.putAll(KJYWFY);
					}
				}
				List<Map<String, Object>> list_ZXYFY = dao.doQuery("select b.TYPE as TYPE,sum(a.ZJJE) as ZJJE " +
						" from ZY_FYMX a,YK_TYPK b where a.YPLX in (1,2,3) and a.FYXH = b.YPXH and a.ZYH = :ZYH group by b.TYPE",
						parameters);
				JBXX.put("XYF", 0);
				JBXX.put("ZCYF", 0);
				JBXX.put("ZCY", 0);
				JBXX.put("ZYZLF", 0);
				if (list_ZXYFY != null) {
					for (int i = 0; i < list_ZXYFY.size(); i++) {
						Map<String, Object> ZXYFY = list_ZXYFY.get(i);
						if ("1".equals(ZXYFY.get("TYPE") + "")) {
							JBXX.put("XYF", ZXYFY.get("ZJJE"));
						} else if ("2".equals(ZXYFY.get("TYPE") + "")) {
							JBXX.put("ZCYF", ZXYFY.get("ZJJE"));
						} else if ("3".equals(ZXYFY.get("TYPE") + "")) {
							JBXX.put("ZCY", ZXYFY.get("ZJJE"));
						}
					}
				}
				List<Map<String, Object>> list_SFXMFY = dao.doSqlQuery(
								"select BASYGB as BASYGB,sum(ZJJE) as ZJJE from (select b.BASYGB,a.ZJJE from ZY_FYMX a,GY_YLSF b where a.YPLX=0 and a.FYXH=b.FYXH and b.BASYGB is not null and a.ZYH = :ZYH union all select b.BASYGB,a.ZJJE from ZY_FYMX a,GY_SFXM b where a.YPLX=0 and a.FYXM = b.SFXM and b.BASYGB is null and a.ZYH = :ZYH) group by BASYGB",
								parameters);
				JBXX.put("YBYLFWF", 0);
				JBXX.put("YBZLCZF", 0);
				JBXX.put("QTFY", 0);
				JBXX.put("HLF", 0);
				JBXX.put("BLZDF", 0);
				JBXX.put("SYSZDF", 0);
				JBXX.put("LCZDXMF", 0);
				JBXX.put("YXXZDF", 0);
				JBXX.put("FSSZLXMF", 0);
				JBXX.put("LCWLZLF", 0);
				JBXX.put("MZF", 0);
				JBXX.put("SSF", 0);
				JBXX.put("SSZLF", 0);
				JBXX.put("KFF", 0);
				JBXX.put("XF", 0);
				JBXX.put("BDBLZPF", 0);
				JBXX.put("QDBLZPF", 0);
				JBXX.put("NXYZLZPF", 0);
				JBXX.put("XBYZLZPF", 0);
				JBXX.put("JCYCLF", 0);
				JBXX.put("ZLYCLF", 0);
				JBXX.put("SSYCLF", 0);
				JBXX.put("QTF", 0);
				if (list_SFXMFY != null) {
					for (int i = 0; i < list_SFXMFY.size(); i++) {
						Map<String, Object> SFXMFY = list_SFXMFY.get(i);
						if ("11".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("YBYLFWF", SFXMFY.get("ZJJE"));
						} else if ("12".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("YBZLCZF", SFXMFY.get("ZJJE"));
						} else if ("13".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("QTFY", SFXMFY.get("ZJJE"));
						} else if ("14".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("HLF", SFXMFY.get("ZJJE"));
						} else if ("21".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("BLZDF", SFXMFY.get("ZJJE"));
						} else if ("22".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("SYSZDF", SFXMFY.get("ZJJE"));
						} else if ("23".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("LCZDXMF", SFXMFY.get("ZJJE"));
						} else if ("24".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("YXXZDF", SFXMFY.get("ZJJE"));
						} else if ("31".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("FSSZLXMF", SFXMFY.get("ZJJE"));
						} else if ("32".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("LCWLZLF", SFXMFY.get("ZJJE"));
						} else if ("33".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("ZYZLF", SFXMFY.get("ZJJE"));
						} else if ("41".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("MZF", SFXMFY.get("ZJJE"));
							JBXX.put("SSZLF", BSPHISUtil.getDouble(
									Double.parseDouble(JBXX.get("SSZLF") + "")+ Double.parseDouble(SFXMFY.get("ZJJE") + ""), 2));
						} else if ("42".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("SSF", SFXMFY.get("ZJJE"));
							JBXX.put("SSZLF", BSPHISUtil.getDouble(
									Double.parseDouble(JBXX.get("SSZLF") + "")+ Double.parseDouble(SFXMFY.get("ZJJE") + ""), 2));
						} else if ("50".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("KFF", SFXMFY.get("ZJJE"));
						} else if ("61".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("XF", SFXMFY.get("ZJJE"));
						} else if ("62".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("BDBLZPF", SFXMFY.get("ZJJE"));
						} else if ("63".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("QDBLZPF", SFXMFY.get("ZJJE"));
						} else if ("64".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("NXYZLZPF", SFXMFY.get("ZJJE"));
						} else if ("65".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("XBYZLZPF", SFXMFY.get("ZJJE"));
						} else if ("71".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("JCYCLF", SFXMFY.get("ZJJE"));
						} else if ("72".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("ZLYCLF", SFXMFY.get("ZJJE"));
						} else if ("73".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("SSYCLF", SFXMFY.get("ZJJE"));
						} else if ("99".equals(SFXMFY.get("BASYGB") + "")) {
							JBXX.put("QTF", SFXMFY.get("ZJJE"));
						}
					}
				}
				if (JBXX.containsKey("HYDM")) {
					JBXX.put("HYDM", (JBXX.get("HYDM") + "").substring(0, 1));
				}
				SchemaUtil.setDictionaryMassageForList(JBXX,BSPHISEntryNames.EMR_BASY);
				res.put("JBXX", JBXX);
				res.put("count", 0);
				res.put("ZYSSJL", new ArrayList<Map<String, Object>>());
				List<Object> mzzdcnd = CNDHelper.createArrayCnd("and", cnd,CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 11));
				List<Object> blzdcnd = CNDHelper.createArrayCnd("and", cnd,CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 44));
				List<Object> sszdzdcnd = CNDHelper.createArrayCnd("and", cnd,CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 45));
				List<Object> cyzdcnd = CNDHelper.createArrayCnd("and", cnd,CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 51));
				Map<String, Object> MZ_ZYZDJLS = dao.doLoad(mzzdcnd,BSPHISEntryNames.EMR_ZYZDJL);
				if (MZ_ZYZDJLS != null) {
					SchemaUtil.setDictionaryMassageForList(MZ_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
					JBXX.put("MZZD", MZ_ZYZDJLS.get("MSZD"));
					JBXX.put("MZ_JBBM", MZ_ZYZDJLS.get("JBBM"));
				}
				Map<String, Object> BL_ZYZDJLS = dao.doLoad(blzdcnd,BSPHISEntryNames.EMR_ZYZDJL);
				if (BL_ZYZDJLS != null) {
					SchemaUtil.setDictionaryMassageForList(BL_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
					JBXX.put("BLZD", BL_ZYZDJLS.get("MSZD"));
					JBXX.put("BL_JBBM", BL_ZYZDJLS.get("JBBM"));
				}
				Map<String, Object> SSZD_ZYZDJLS = dao.doLoad(sszdzdcnd,BSPHISEntryNames.EMR_ZYZDJL);
				if (SSZD_ZYZDJLS != null) {
					SchemaUtil.setDictionaryMassageForList(SSZD_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
					JBXX.put("SSZD", SSZD_ZYZDJLS.get("MSZD"));
					JBXX.put("SS_JBBM", SSZD_ZYZDJLS.get("JBBM"));
				}
				List<Map<String, Object>> ZYZDJL = dao.doList(cyzdcnd, "JLXH",BSPHISEntryNames.EMR_ZYZDJL);
				SchemaUtil.setDictionaryMassageForList(ZYZDJL,BSPHISEntryNames.EMR_ZYZDJL);
				res.put("ZYZDJL", ZYZDJL);
				res.put("NEW", 1);
			} else {
				Map<String, Object> JBXX = dao.doLoad(BSPHISEntryNames.EMR_BASY, JLXH.get("JLXH"));
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("YWID1", ZYH);// 一般填写就诊序号
				record.put("YWID2", JBXX.get("JLXH"));// 一般填写病历编号
				record.put("YEID3", JBXX.get("JLXH"));// 一般填写业务操作的主键值
				record.put("RZNR", "病案首页");
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.beginTransaction();
				BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_VISIT, record, dao, ctx);
				ss.flush();
				ss.getTransaction().commit();
				String YL = JBXX.get("YL") + "";
				if (YL.indexOf(",") >= 0) {
					String[] YLS = YL.split(",");
					String YL_Y = YLS[0];
					String[] YL_FS = YLS[1].split("/");
					String YL_FZ = YL_FS[0];
					String YL_FM = YL_FS[1];
					JBXX.put("YL_Y", YL_Y);
					JBXX.put("YL_FZ", YL_FZ);
					JBXX.put("YL_FM", YL_FM);
				}
				int RYQHMSJ_T = Integer.parseInt(JBXX.get("RYQHMSJ")==null?"0": JBXX.get("RYQHMSJ")+ "")/(60 * 24);
				int RYQHMSJ_S = (Integer.parseInt(JBXX.get("RYQHMSJ")==null?"0": JBXX.get("RYQHMSJ") + "") % (60 * 24)) / 60;
				int RYQHMSJ_F = Integer.parseInt(JBXX.get("RYQHMSJ")==null?"0": JBXX.get("RYQHMSJ") + "") % 60;
				JBXX.put("RYQHMSJ_T", RYQHMSJ_T);
				JBXX.put("RYQHMSJ_S", RYQHMSJ_S);
				JBXX.put("RYQHMSJ_F", RYQHMSJ_F);
				int RYHHMSJ_T = Integer.parseInt(JBXX.get("RYHHMSJ")==null?"0":JBXX.get("RYHHMSJ")+"")/(60*24);
				int RYHHMSJ_S = (Integer.parseInt(JBXX.get("RYHHMSJ")==null?"0": JBXX.get("RYHHMSJ") + "") % (60 * 24)) / 60;
				int RYHHMSJ_F = Integer.parseInt(JBXX.get("RYHHMSJ")==null?"0": JBXX.get("RYHHMSJ") + "") % 60;
				JBXX.put("RYHHMSJ_T", RYHHMSJ_T);
				JBXX.put("RYHHMSJ_S", RYHHMSJ_S);
				JBXX.put("RYHHMSJ_F", RYHHMSJ_F);
				SchemaUtil.setDictionaryMassageForList(JBXX,BSPHISEntryNames.EMR_BASY);
				Map<String, Object> FYTJ = dao.doLoad(BSPHISEntryNames.EMR_BASY_FY, JLXH.get("JLXH"));
				SchemaUtil.setDictionaryMassageForList(FYTJ,BSPHISEntryNames.EMR_BASY_FY);
				JBXX.putAll(FYTJ);
				List<Map<String, Object>> ZYSSJL=dao.doList(cnd,"JLXH",BSPHISEntryNames.EMR_ZYSSJL);
				SchemaUtil.setDictionaryMassageForList(ZYSSJL,BSPHISEntryNames.EMR_ZYSSJL);
				List<Object> mzzdcnd = CNDHelper.createArrayCnd("and",cnd,CNDHelper.createSimpleCnd("eq","ZDLB","i", 11));
				List<Object> blzdcnd = CNDHelper.createArrayCnd("and",cnd,CNDHelper.createSimpleCnd("eq","ZDLB","i",44));
				List<Object> sszdzdcnd = CNDHelper.createArrayCnd("and",cnd,CNDHelper.createSimpleCnd("eq","ZDLB","i",45));
				List<Object> cyzdcnd = CNDHelper.createArrayCnd("and",cnd,CNDHelper.createSimpleCnd("eq","ZDLB","i",51));
				Map<String, Object> MZ_ZYZDJLS = dao.doLoad(mzzdcnd,BSPHISEntryNames.EMR_ZYZDJL);
				if (MZ_ZYZDJLS != null) {
					SchemaUtil.setDictionaryMassageForList(MZ_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
					JBXX.put("MZZD", MZ_ZYZDJLS.get("MSZD"));
					JBXX.put("MZ_JBBM", MZ_ZYZDJLS.get("JBBM"));
				}
				Map<String, Object> BL_ZYZDJLS = dao.doLoad(blzdcnd,BSPHISEntryNames.EMR_ZYZDJL);
				if (BL_ZYZDJLS != null) {
					SchemaUtil.setDictionaryMassageForList(BL_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
					JBXX.put("BLZD", BL_ZYZDJLS.get("MSZD"));
					JBXX.put("BL_JBBM", BL_ZYZDJLS.get("JBBM"));
				}
				Map<String, Object> SSZD_ZYZDJLS = dao.doLoad(sszdzdcnd,BSPHISEntryNames.EMR_ZYZDJL);
				if (SSZD_ZYZDJLS != null) {
					SchemaUtil.setDictionaryMassageForList(SSZD_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
					JBXX.put("SSZD", SSZD_ZYZDJLS.get("MSZD"));
					JBXX.put("SS_JBBM", SSZD_ZYZDJLS.get("JBBM"));
				}
				List<Map<String, Object>> ZYZDJL = dao.doList(cyzdcnd, "JLXH",BSPHISEntryNames.EMR_ZYZDJL);
				SchemaUtil.setDictionaryMassageForList(ZYZDJL,BSPHISEntryNames.EMR_ZYZDJL);
				res.put("ZYSSJL", ZYSSJL);
				res.put("count", doQuerySFUpdate(JBXX, update, ZYH));
				res.put("JBXX", JBXX);
				res.put("ZYZDJL", ZYZDJL);
				res.put("NEW", 0);
				if ("1".equals(res.get("SXQX") + "")) {
					Map<String, Object> BLSYparameters = new HashMap<String, Object>();
					BLSYparameters.put("BLBH", JLXH.get("JLXH"));
					Map<String, Object> EMR_BLSY = dao
							.doLoad("select max(a.QXJB) as QXJB from EMR_BLSY a where a.BLBH = :BLBH", BLSYparameters);
					//zhaojian 20170918 增加查询ZZYS字段，解决主治医生也可以编辑住院病案首页权限问题
					Map<String, Object> ZSYSMap = dao.doLoad(
							"select a.ZSYS as ZSYS,a.ZZYS as ZZYS from ZY_BRRY a where ZYH=:ZYH", parameters);
					String ZSYS = ZSYSMap.get("ZSYS") + "";
					String ZZYS = ZSYSMap.get("ZZYS") + "";
					if (EMR_BLSY.get("QXJB") == null) {
						//zhaojian 20170918 增加主治医生也可以编辑住院病案首页权限
						if (!(userId.equals(JLXH.get("SXYS") + "") || userId
								.equals(ZSYS) || userId.equals(ZZYS))) {
							res.put("SXQX", 0);
						}
					} else {
						if ("0".equals(res.get("SYQX") + "")) {
							res.put("SXQX", 0);
						} else {
							Map<String, Object> YLJS = BSPEMRUtil.getDocRoleByUid(userId, dao);
							int JSJB=Integer.parseInt(YLJS.get("JSJB")+"");
							int QXJB=Integer.parseInt(EMR_BLSY.get("QXJB")+"");
							if (JSJB <= QXJB) {
								//zhaojian 20170918 增加主治医生也可以编辑住院病案首页权限
								if (!(userId.equals(JLXH.get("SXYS")+"") || userId.equals(ZSYS) 
									|| userId.equals(ZZYS))) {
									res.put("SXQX", 0);
								}
							}
						}
					}
				}

			}
		} catch (PersistentDataOperationException e) {
			logger.error("新建或加载病历首页失败！", e);
			throw new ModelDataOperationException("新建或加载病历首页失败！", e);
		}
	}

	public int doQuerySFUpdate(Map<String, Object> BASY,
			List<Map<String, Object>> updateIds, long ZYH)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		try {
			Map<String, Object> JBXX = dao
					.doLoad("SELECT a.BRXZ as YLFYDM,a.SFZH as SFZJHM,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.RYNL as BRNL,a.ZYCS as ZYCS,a.HYZK as HYDM,a.ZYDM as ZYDM,a.MZDM as MZDM,a.GJDM as GJDM,a.GZDW as DWDZ,a.DWDH as DWDH,a.DWYB as DWYB,a.HKDZ as HKDZ,a.JTDH as JTDH,a.HKYB as HKDZ_YB,a.LXRM as LXRXM,a.XZZ_DH as XZZ_DH,a.XZZ_YB as XZZ_YB,a.LXGX as LXRGX,a.LXDZ as LXRDZ,a.LXDH as LXRDH,a.RYRQ as RYRQ,a.BRKS as RYKS,a.BRBQ as BRBQ,a.CYRQ as CYRQ,a.CSD_SQS as CSD_SQS,a.CSD_S as CSD_S,a.CSD_X as CSD_X,a.HKDZ_SQS as HKDZ_SQS,a.HKDZ_S as HKDZ_S ,a.HKDZ_X as HKDZ_X,a.HKDZ_QTDZ as HKDZ_DZ,a.XZZ_SQS as XZZ_SQS,a.XZZ_S as XZZ_S,a.XZZ_X as XZZ_X,a.XZZ_QTDZ as XZZ_DZ,a.JGDM_SQS as JGDM_SQS,a.JGDM_S as JGDM_S FROM "
							+ "ZY_BRRY a WHERE a.ZYH = :ZYH", parameters);
			JBXX.put("ZYZFY", 0);
			JBXX.put("ZFJE", 0);
			JBXX.put("KJYWFY", 0);
			JBXX.put("XYF", 0);
			JBXX.put("ZCYF", 0);
			JBXX.put("ZCY", 0);
			JBXX.put("ZYZLF", 0);
			JBXX.put("YBYLFWF", 0);
			JBXX.put("YBZLCZF", 0);
			JBXX.put("QTFY", 0);
			JBXX.put("HLF", 0);
			JBXX.put("BLZDF", 0);
			JBXX.put("SYSZDF", 0);
			JBXX.put("LCZDXMF", 0);
			JBXX.put("YXXZDF", 0);
			JBXX.put("FSSZLXMF", 0);
			JBXX.put("LCWLZLF", 0);
			JBXX.put("MZF", 0);
			JBXX.put("SSF", 0);
			JBXX.put("SSZLF", 0);
			JBXX.put("KFF", 0);
			JBXX.put("XF", 0);
			JBXX.put("BDBLZPF", 0);
			JBXX.put("QDBLZPF", 0);
			JBXX.put("NXYZLZPF", 0);
			JBXX.put("XBYZLZPF", 0);
			JBXX.put("JCYCLF", 0);
			JBXX.put("ZLYCLF", 0);
			JBXX.put("SSYCLF", 0);
			JBXX.put("QTF", 0);
			List<Map<String, Object>> ZKXX_list = dao.doQuery(
					"SELECT (SELECT OFFICENAME FROM SYS_Office b WHERE a.HHKS = b.ID) as ZKKSMC FROM "
							+ "ZY_HCMX a WHERE a.HCLX = 2 AND ZYH = :ZYH",parameters);
			Map<String,Object> ZKXX = new HashMap<String,Object>();
			String ZKKSMC = "";
			for(int i  = 0 ; i < ZKXX_list.size() ; i ++){
				if(i>0){
					ZKKSMC += "→"+ZKXX_list.get(i).get("ZKKSMC");
				}else{
					ZKKSMC = ZKXX_list.get(i).get("ZKKSMC")+"";
				}
			}
			if(ZKKSMC.length()>25){
				ZKKSMC = ZKKSMC.substring(0, 25);
			}
			ZKXX.put("ZKKSMC", ZKKSMC);
			if (ZKXX != null) {
				JBXX.putAll(ZKXX);
			}
			Map<String, Object> RYKS = dao.doLoad("SELECT a.HHKS as RYKS ,a.HHBQ as RYBF FROM ZY_HCMX a " +
					" WHERE a.HCLX = 0 AND ZYH = :ZYH group by a.HHKS,a.HHBQ",parameters);
			logger.info(">>>>>>>>>"+RYKS);
			if (RYKS != null) {
				JBXX.putAll(RYKS);
			}
			Map<String, Object> CYXX = dao.doLoad("SELECT a.BRKS as CYKS,a.BRBQ as CYBQ,CYRQ as CYRQ FROM ZY_BRRY a " +
					" WHERE a.ZYH = :ZYH ",parameters);
			if (CYXX != null) {
				JBXX.putAll(CYXX);
			}
			Map<String, Object> ZYTS = dao.doLoad("SELECT CYRQ-RYRQ as ZYTS FROM ZY_BRRY " +
					" WHERE CYPB <> 0 AND CYPB <> 99  AND ZYH = :ZYH",parameters);
			if (ZYTS != null) {
				JBXX.putAll(ZYTS);
			}
			Map<String, Object> FYTJ = dao.doLoad("SELECT sum(a.ZJJE) as ZYZFY,sum(ZFJE) as ZFJE FROM ZY_FYMX a " +
					" WHERE a.ZYH = :ZYH",parameters);
			if (FYTJ != null) {
				if (FYTJ.get("ZYZFY") != null) {
					JBXX.putAll(FYTJ);
				}
			}
			Map<String, Object> KJYWFY = dao.doLoad("select sum(a.ZJJE) as KJYWFY from ZY_FYMX a,YK_TYPK b " +
					" where a.YPLX = 1 and a.FYXH = b.YPXH and b.KSBZ = 1 and a.ZYH = :ZYH",parameters);
			JBXX.put("KJYWFY", 0);
			if (KJYWFY != null) {
				if (KJYWFY.get("KJYWFY") != null) {
					JBXX.putAll(KJYWFY);
				}
			}
			List<Map<String, Object>> list_ZXYFY = dao.doQuery("select b.TYPE as TYPE,sum(a.ZJJE) as ZJJE " +
					" from ZY_FYMX a,YK_TYPK b where a.YPLX in (1,2,3) and a.FYXH = b.YPXH and a.ZYH = :ZYH group by b.TYPE",
							parameters);
			if (list_ZXYFY != null) {
				for (int i = 0; i < list_ZXYFY.size(); i++) {
					Map<String, Object> ZXYFY = list_ZXYFY.get(i);
					if ("1".equals(ZXYFY.get("TYPE") + "")) {
						JBXX.put("XYF", ZXYFY.get("ZJJE"));
					} else if ("2".equals(ZXYFY.get("TYPE") + "")) {
						JBXX.put("ZCYF", ZXYFY.get("ZJJE"));
					} else if ("3".equals(ZXYFY.get("TYPE") + "")) {
						JBXX.put("ZCY", ZXYFY.get("ZJJE"));
					}
				}
			}
			List<Map<String, Object>> list_SFXMFY = dao.doSqlQuery("select nvl(BASYGB,99) as BASYGB,sum(ZJJE) as ZJJE from (select b.BASYGB,a.ZJJE from ZY_FYMX a,GY_YLSF b where a.YPLX=0 and a.FYXH=b.FYXH and b.BASYGB is not null and a.ZYH = :ZYH union all select b.BASYGB,a.ZJJE from ZY_FYMX a,GY_SFXM b where a.YPLX=0 and a.FYXM = b.SFXM and b.BASYGB is null and a.ZYH = :ZYH) group by BASYGB",
							parameters);
			if (list_SFXMFY != null) {
				for (int i = 0; i < list_SFXMFY.size(); i++) {
					Map<String, Object> SFXMFY = list_SFXMFY.get(i);
					if ("11".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("YBYLFWF", SFXMFY.get("ZJJE"));
					} else if ("12".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("YBZLCZF", SFXMFY.get("ZJJE"));
					} else if ("13".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("QTFY", SFXMFY.get("ZJJE"));
					} else if ("14".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("HLF", SFXMFY.get("ZJJE"));
					} else if ("21".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("BLZDF", SFXMFY.get("ZJJE"));
					} else if ("22".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("SYSZDF", SFXMFY.get("ZJJE"));
					} else if ("23".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("LCZDXMF", SFXMFY.get("ZJJE"));
					} else if ("24".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("YXXZDF", SFXMFY.get("ZJJE"));
					} else if ("31".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("FSSZLXMF", SFXMFY.get("ZJJE"));
					} else if ("32".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("LCWLZLF", SFXMFY.get("ZJJE"));
					} else if ("33".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("ZYZLF", SFXMFY.get("ZJJE"));
					} else if ("41".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("MZF", SFXMFY.get("ZJJE"));
						JBXX.put("SSZLF",BSPHISUtil.getDouble(Double.parseDouble(JBXX.get("SSZLF")+ "")
										+ Double.parseDouble(SFXMFY.get("ZJJE") + ""), 2));
					} else if ("42".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("SSF", SFXMFY.get("ZJJE"));
						JBXX.put("SSZLF",BSPHISUtil.getDouble(Double.parseDouble(JBXX.get("SSZLF")+ "")
										+ Double.parseDouble(SFXMFY.get("ZJJE") + ""), 2));
					} else if ("50".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("KFF", SFXMFY.get("ZJJE"));
					} else if ("61".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("XF", SFXMFY.get("ZJJE"));
					} else if ("62".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("BDBLZPF", SFXMFY.get("ZJJE"));
					} else if ("63".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("QDBLZPF", SFXMFY.get("ZJJE"));
					} else if ("64".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("NXYZLZPF", SFXMFY.get("ZJJE"));
					} else if ("65".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("XBYZLZPF", SFXMFY.get("ZJJE"));
					} else if ("71".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("JCYCLF", SFXMFY.get("ZJJE"));
					} else if ("72".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("ZLYCLF", SFXMFY.get("ZJJE"));
					} else if ("73".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("SSYCLF", SFXMFY.get("ZJJE"));
					} else if ("99".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("QTF", SFXMFY.get("ZJJE"));
					}
				}
			}
			for (int i = 0; i < updateIds.size(); i++) {
				String updateId = (String) updateIds.get(i).get("id");
				if (JBXX.containsKey(updateId) && BASY.containsKey(updateId)) {
					if (JBXX.get(updateId) == null||(JBXX.get(updateId)+"").length()==0) {
						continue;
					}
					if ("double".equals(updateIds.get(i).get("type") + "")) {
						if (Double.parseDouble(JBXX.get(updateId) + "") != Double
								.parseDouble(BASY.get(updateId) + "")) {
							return 1;
						}
					} else {
						if (!(JBXX.get(updateId) + "").equals(BASY.get(updateId) + "")) {
							return 1;
						}
					}

				}
			}
			return 0;
		} catch (PersistentDataOperationException e) {
			logger.error("判断是否有更新信息失败！", e);
			throw new ModelDataOperationException("判断是否有更新信息失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveMedicalRecords(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String userId = (String)user.getUserId();
		long ZYH = Long.parseLong(req.get("ZYH") + "");
		int BLLB = Integer.parseInt(req.get("BLLB") + "");

		Map<String, Object> EMR_BASY = (Map<String, Object>) req
				.get("EMR_BASY");
		Map<String, Object> EMR_BASY_FY = (Map<String, Object>) req
				.get("EMR_BASY_FY");
		List<Map<String, Object>> EMR_ZYSSJLs = (List<Map<String, Object>>) req
				.get("EMR_ZYSSJL");
		List<Map<String, Object>> EMR_ZYZDJLs = (List<Map<String, Object>>) req
				.get("EMR_ZYZDJL");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		parameters.put("BLLB", BLLB);
		String topUnitId = ParameterUtil.getTopUnitId();
		String KSMRZ = ParameterUtil.getParameter(topUnitId,
				BSPHISSystemArgument.BASYKSMRZ, ctx);
		if ("1".equals(KSMRZ)) {
			KSMRZ = "空白";
		} else if ("2".equals(KSMRZ)) {
			KSMRZ = "无";
		} else if ("3".equals(KSMRZ)) {
			KSMRZ = "-";
		} else {
			KSMRZ = "/";
		}
		for (Map.Entry<String, Object> entry : EMR_BASY.entrySet()) {
			if ("not-null".equals(entry.getValue())) {
				entry.setValue(KSMRZ);
			}
		}
		for (Map.Entry<String, Object> entry : EMR_BASY_FY.entrySet()) {
			if (entry.getValue() == null || "null".equals(entry.getValue())
					|| entry.getValue() == "") {
				entry.setValue(0d);
			}
		}
		try {
			Map<String, Object> map_LBBHparameters = new HashMap<String, Object>();
			map_LBBHparameters.put("YDLBBM", "01");
			Map<String, Object> map_LBBH = dao.doLoad(
					"select a.LBBH as LBBH From EMR_KBM_BLLB a Where YDLBBM = :YDLBBM", map_LBBHparameters);
			EMR_BASY.put("JGID", manageUnit);
			EMR_BASY.put("JZXH", ZYH);
			EMR_BASY_FY.put("JGID", manageUnit);
			EMR_BASY_FY.put("JZXH", ZYH);
			EMR_BASY_FY.put("BRID", EMR_BASY.get("BRID"));
			Map<String, Object> EMR_BL01 = getEMR_BL01(EMR_BASY);
			Map<String, Object> map_BLBH = dao.doLoad(
					"select a.BLBH as BLBH from EMR_BL01 a where JZXH = :ZYH and BLLB = :BLLB",
					parameters);
			EMR_BL01.put("MBLB", map_LBBH.get("LBBH"));
			EMR_BL01.put("BLLB", BLLB);
			String op = "create";
			String logOp = BSPEMRUtil.OP_CREATE;
			long BLBH = 0;
			if (map_BLBH != null) {
				BLBH = Long.parseLong(map_BLBH.get("BLBH") + "");
				op = "update";
				logOp = BSPEMRUtil.OP_UPDATE;
				EMR_BL01.put("BLBH", BLBH);
				EMR_BASY.put("JLXH", BLBH);
				EMR_BASY_FY.put("JLXH", BLBH);
				if (EMR_BASY.containsKey("QMYS")&& EMR_BASY.get("QMYS") != null) {
					Date date = new Date();
					EMR_BL01.put("BLZT", 1);
					EMR_BL01.put("WCSJ", date);
				}
			} else {
			
				EmrManageModel emm = new EmrManageModel(dao);
				BLBH = emm.getBl01Key(ctx);
				EMR_BL01.put("BLBH", BLBH);
				EMR_BASY.put("JLXH", BLBH);
				EMR_BASY_FY.put("JLXH", BLBH);
				Date date = new Date();
				EMR_BL01.put("JLSJ", date);
				EMR_BL01.put("XTSJ", date);
				EMR_BL01.put("BLLX", 0);
				EMR_BL01.put("MBBH", 0);
				EMR_BL01.put("DLLB", 4);
				EMR_BL01.put("DLJ", "-");
				EMR_BL01.put("BLMC", "住院病案首页");
				EMR_BL01.put("BLZT", 0);
				EMR_BL01.put("SYBZ", 0);
				EMR_BL01.put("BLYM", 0);
				EMR_BL01.put("YMJL", 0);
				EMR_BL01.put("SXYS", userId);
				EMR_BL01.put("SSYS", userId);
				if (EMR_BASY.containsKey("QMYS")
						&& EMR_BASY.get("QMYS") != null) {
					Map<String, Object> EMR_BL01parameters = new HashMap<String, Object>();
					EMR_BL01parameters.put("BLBH", BLBH);
					long count = dao.doCount("EMR_BL01","BLBH=:BLBH and BLZT=0", EMR_BL01parameters);
					if (count > 0) {
						EMR_BL01.put("BLZT", 1);
						EMR_BL01.put("WCSJ", date);
					}
				}
			}
			//yx-2017-12-04-增加查詢组织机构代码-b
			if(!(EMR_BASY.containsKey("YLJGDM") && EMR_BASY.get("YLJGDM")!=null && !(EMR_BASY.get("YLJGDM")+"").equals("/"))){
				Map<String, Object> pa= new HashMap<String, Object>();
				pa.put("ORGANIZCODE", user.getManageUnitId());
				Map<String, Object> remap=dao.doSqlLoad("select a.REGISTERNUMBER as REGISTERNUMBER from SYS_ORGANIZATION a " +
						" where a.ORGANIZCODE=:ORGANIZCODE", pa) ;
				EMR_BASY.put("YLJGDM", remap.get("REGISTERNUMBER")==null?"/":remap.get("REGISTERNUMBER")+"");
			}
			//yx-2017-12-04-增加查詢组织机构代码-e
			dao.doSave(op, BSPHISEntryNames.EMR_BL01, EMR_BL01, false);
			Map<String,Object> JLXH = dao.doSave(op, BSPHISEntryNames.EMR_BASY, EMR_BASY, false);
			res.put("body", JLXH);
			dao.doSave(op, BSPHISEntryNames.EMR_BASY_FY, EMR_BASY_FY, false);
			Map<String, Object> delparameters = new HashMap<String, Object>();
			delparameters.put("ZYH", ZYH);
			if (EMR_ZYSSJLs.size() > 0) {
				dao.doUpdate("delete from EMR_ZYSSJL a where a.JZXH = :ZYH", delparameters);
				for (int i = 0; i < EMR_ZYSSJLs.size(); i++) {
					Map<String, Object> EMR_ZYSSJL = EMR_ZYSSJLs.get(i);
					EMR_ZYSSJL.put("JZXH", ZYH);
					EMR_ZYSSJL.put("BRID", EMR_BASY.get("BRID"));
					EMR_ZYSSJL.put("JGID", manageUnit);
					EMR_ZYSSJL.put("DJGH", userId);
					EMR_ZYSSJL.put("DJSJ", new Date());
					dao.doSave("create", BSPHISEntryNames.EMR_ZYSSJL,EMR_ZYSSJL, false);
				}
			}
			dao.doUpdate("delete from EMR_ZYZDJL a where a.JZXH = :ZYH", delparameters);
			if (EMR_ZYZDJLs.size() > 0) {
				int cyzd = 0;
				int zzzd = 0;
				for (int i = 0; i < EMR_ZYZDJLs.size(); i++) {
					Map<String, Object> EMR_ZYZDJL = EMR_ZYZDJLs.get(i);
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						cyzd++;
					}
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")
							&& "1".equals(EMR_ZYZDJL.get("ZZBZ") + "")) {
						zzzd++;
					}
				}
				if (cyzd > 0 && zzzd == 0) {
					for (int i = 0; i < EMR_ZYZDJLs.size(); i++) {
						Map<String, Object> EMR_ZYZDJL = EMR_ZYZDJLs.get(i);
						if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
							EMR_ZYZDJLs.get(i).put("ZZBZ", 1);
							break;
						}
					}
				}
				int SFRYZD = 0;
				int SFMQZD = 0;
				String RYMSZD = "";
				String MQMSZD = "";
				for (int i = 0; i < EMR_ZYZDJLs.size(); i++) {
					Map<String, Object> EMR_ZYZDJL = EMR_ZYZDJLs.get(i);
					EMR_ZYZDJL.put("JZXH", ZYH);
					EMR_ZYZDJL.put("BRID", EMR_BASY.get("BRID"));
					EMR_ZYZDJL.put("JGID", manageUnit);
					EMR_ZYZDJL.put("XTSJ", new Date());
					EMR_ZYZDJL.put("ZNXH", 0);
					dao.doSave("create", BSPHISEntryNames.EMR_ZYZDJL,
							EMR_ZYZDJL, false);
					if ("22".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						SFRYZD = 1;
						RYMSZD = EMR_ZYZDJL.get("MSZD") + "";
						SFMQZD++;
						MQMSZD += SFMQZD + "." + EMR_ZYZDJL.get("MSZD") + "";
					}
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						SFMQZD++;
						MQMSZD += SFMQZD + "." + EMR_ZYZDJL.get("MSZD") + "";
					}
				}
				if (EMR_ZYZDJLs.size() > 0) {
					Map<String, Object> BRRY = new HashMap<String, Object>();
					if (SFRYZD == 1) {
						BRRY.put("RYZD", RYMSZD);
						if (MQMSZD.length() > 127) {
							BRRY.put("MQZD", MQMSZD.substring(0, 127));
						} else {
							BRRY.put("MQZD", MQMSZD);
						}
						BRRY.put("ZYH", ZYH);
						dao.doSave("update", BSPHISEntryNames.ZY_BRRY, BRRY,
								false);
					}
				}

			}
			// 医生签名
			if (EMR_BASY.containsKey("QMYS") && EMR_BASY.get("QMYS") != null) {
				Map<String, Object> YLJS = BSPEMRUtil.getDocRoleByUid(userId,
						dao);
				long SYQX = Long.parseLong(YLJS.get("JSXH") + "");
				int JSJB = Integer.parseInt(YLJS.get("JSJB") + "");
				String QMYS = EMR_BASY.get("QMYS") + "";
				Date date = new Date();
				Map<String, Object> EMR_BLSY = new HashMap<String, Object>();
				EMR_BLSY.put("BLBH", BLBH);
				EMR_BLSY.put("QMYS", QMYS);
				EMR_BLSY.put("JGID", manageUnit);
				EMR_BLSY.put("SYYS", EMR_BASY.get(QMYS));
				EMR_BLSY.put("SYQX", SYQX);
				EMR_BLSY.put("SYSJ", date);
				EMR_BLSY.put("JLSJ", date);
				EMR_BLSY.put("QXJB", JSJB);
				EMR_BLSY.put("BZXX", "/");
				try {
					dao.doSave("create", BSPHISEntryNames.EMR_BLSY, EMR_BLSY,
							false);
					logOp = BSPEMRUtil.OP_SIGNED;
				} catch (ValidateException e) {
					e.printStackTrace();
					throw new ModelDataOperationException("病历签名失败！", e);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
					throw new ModelDataOperationException("病历签名失败！", e);
				}
			}
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("YWID1", ZYH);// 一般填写就诊序号
			record.put("YWID2", BLBH);// 一般填写病历编号
			record.put("YEID3", BLBH);// 一般填写业务操作的主键值
			record.put("RZNR", "病案首页");
			BSPEMRUtil.doSaveEmrOpLog(logOp, record, dao, ctx);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病案首页保存失败！", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病案首页保存失败！", e);
		} catch (KeyManagerException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("获取病历01主键失败！", e);
		}
	}

	public Map<String, Object> getEMR_BL01(Map<String, Object> EMR_BASY) {
		Map<String, Object> EMR_BL01 = new HashMap<String, Object>();
		EMR_BL01.put("JZXH", EMR_BASY.get("JZXH"));
		EMR_BL01.put("BRBH", EMR_BASY.get("ZYHN"));
		EMR_BL01.put("CJKS", EMR_BASY.get("CYKS"));
		EMR_BL01.put("BRKS", EMR_BASY.get("CYKS"));
		EMR_BL01.put("BRBQ", EMR_BASY.get("CYBQ"));
		EMR_BL01.put("BRCH", EMR_BASY.get("BRCH"));
		EMR_BL01.put("BRNL", EMR_BASY.get("BRNL"));
		EMR_BL01.put("BRZD", /* EMR_BASY.get("BRZD") */"无");// 主要诊断
		EMR_BL01.put("JGID", EMR_BASY.get("JGID"));
		EMR_BL01.put("BRID", EMR_BASY.get("BRID"));
		EMR_BL01.put("BRXM", EMR_BASY.get("BRXM"));
		return EMR_BL01;
	}

	@SuppressWarnings("unchecked")
	public void doQueryUpdateCount(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		long ZYH = Long.parseLong(req.get("ZYH") + "");
		List<Map<String, Object>> updates = (List<Map<String, Object>>) req
				.get("udData");
		Map<String, Object> BASY = (Map<String, Object>) req.get("BASY");
		int count = doQuerySFUpdate(BASY, updates, ZYH);
		res.put("count", count);
	}

	@SuppressWarnings("unchecked")
	public void doQueryUpdate(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		long ZYH = Long.parseLong(req.get("ZYH") + "");
		List<Map<String, Object>> updates = (List<Map<String, Object>>) req
				.get("udData");
		Map<String, Object> BASY = (Map<String, Object>) req.get("BASY");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		try {
			Map<String, Object> JBXX = dao
					.doLoad("SELECT a.BRXZ as YLFYDM,a.SFZH as SFZJHM,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.RYNL as BRNL,a.ZYCS as ZYCS,a.HYZK as HYDM,a.ZYDM as ZYDM,a.MZDM as MZDM,a.GJDM as GJDM,a.GZDW as DWDZ,a.DWDH as DWDH,a.DWYB as DWYB,a.HKDZ as HKDZ,a.JTDH as JTDH,a.HKYB as HKDZ_YB,a.LXRM as LXRXM,a.XZZ_DH as XZZ_DH,a.XZZ_YB as XZZ_YB,a.LXGX as LXRGX,a.LXDZ as LXRDZ,a.LXDH as LXRDH,a.RYRQ as RYRQ,a.BRKS as RYKS,a.BRBQ as BRBQ,a.CYRQ as CYRQ,a.CSD_SQS as CSD_SQS,a.CSD_S as CSD_S,a.CSD_X as CSD_X,a.HKDZ_SQS as HKDZ_SQS,a.HKDZ_S as HKDZ_S ,a.HKDZ_X as HKDZ_X,a.HKDZ_QTDZ as HKDZ_DZ,a.XZZ_SQS as XZZ_SQS,a.XZZ_S as XZZ_S,a.XZZ_X as XZZ_X,a.XZZ_QTDZ as XZZ_DZ,a.JGDM_SQS as JGDM_SQS,a.JGDM_S as JGDM_S FROM "
							+ "ZY_BRRY a WHERE a.ZYH = :ZYH", parameters);
			JBXX.put("ZYZFY", 0);
			JBXX.put("ZFJE", 0);
			JBXX.put("KJYWFY", 0);
			JBXX.put("XYF", 0);
			JBXX.put("ZCYF", 0);
			JBXX.put("ZCY", 0);
			JBXX.put("ZYZLF", 0);
			JBXX.put("YBYLFWF", 0);
			JBXX.put("YBZLCZF", 0);
			JBXX.put("QTFY", 0);
			JBXX.put("HLF", 0);
			JBXX.put("BLZDF", 0);
			JBXX.put("SYSZDF", 0);
			JBXX.put("LCZDXMF", 0);
			JBXX.put("YXXZDF", 0);
			JBXX.put("FSSZLXMF", 0);
			JBXX.put("LCWLZLF", 0);
			JBXX.put("MZF", 0);
			JBXX.put("SSF", 0);
			JBXX.put("SSZLF", 0);
			JBXX.put("KFF", 0);
			JBXX.put("XF", 0);
			JBXX.put("BDBLZPF", 0);
			JBXX.put("QDBLZPF", 0);
			JBXX.put("NXYZLZPF", 0);
			JBXX.put("XBYZLZPF", 0);
			JBXX.put("JCYCLF", 0);
			JBXX.put("ZLYCLF", 0);
			JBXX.put("SSYCLF", 0);
			JBXX.put("QTF", 0);
			List<Map<String, Object>> ZKXX_list = dao.doQuery(
					"SELECT (SELECT OFFICENAME FROM SYS_Office b WHERE a.HHKS = b.ID) as ZKKSMC FROM "
							+ "ZY_HCMX a WHERE a.HCLX = 2 AND ZYH = :ZYH",
					parameters);
			Map<String,Object> ZKXX = new HashMap<String,Object>();
			String ZKKSMC = "";
			for(int i  = 0 ; i < ZKXX_list.size() ; i ++){
				if(i>0){
					ZKKSMC += "→"+ZKXX_list.get(i).get("ZKKSMC");
				}else{
					ZKKSMC = ZKXX_list.get(i).get("ZKKSMC")+"";
				}
			}
			if(ZKKSMC.length()>25){
				ZKKSMC = ZKKSMC.substring(0, 25);
			}
			ZKXX.put("ZKKSMC", ZKKSMC);
			if (ZKXX != null) {
				JBXX.putAll(ZKXX);
			}
			Map<String, Object> RYKS = dao
					.doLoad("SELECT a.HHKS as RYKS ,a.HHBQ as RYBF FROM ZY_HCMX a WHERE a.HCLX = 0 AND ZYH = :ZYH group by a.HHKS,a.HHBQ",
							parameters);
			if (RYKS != null) {
				JBXX.putAll(RYKS);
			}
			Map<String, Object> CYXX = dao
					.doLoad("SELECT a.BRKS as CYKS,a.BRBQ as CYBQ,CYRQ as CYRQ FROM ZY_BRRY a WHERE a.ZYH = :ZYH ",
							parameters);
			if (CYXX != null) {
				JBXX.putAll(CYXX);
			}
			Map<String, Object> ZYTS = dao
					.doLoad("SELECT CYRQ-RYRQ as ZYTS FROM ZY_BRRY WHERE CYPB <> 0 AND CYPB <> 99  AND ZYH = :ZYH",
							parameters);
			if (ZYTS != null) {
				JBXX.putAll(ZYTS);
			}
			Map<String, Object> FYTJ = dao
					.doLoad("SELECT sum(a.ZJJE) as ZYZFY,sum(ZFJE) as ZFJE FROM ZY_FYMX a WHERE a.ZYH = :ZYH",
							parameters);
			if (FYTJ != null) {
				if (FYTJ.get("ZYZFY") != null) {
					JBXX.putAll(FYTJ);
				}
			}
			Map<String, Object> KJYWFY = dao
					.doLoad("select sum(a.ZJJE) as KJYWFY from ZY_FYMX a,YK_TYPK b where a.YPLX = 1 and a.FYXH = b.YPXH and b.KSBZ = 1 and a.ZYH = :ZYH",
							parameters);
			JBXX.put("KJYWFY", 0);
			if (KJYWFY != null) {
				if (KJYWFY.get("KJYWFY") != null) {
					JBXX.putAll(KJYWFY);
				}
			}
			List<Map<String, Object>> list_ZXYFY = dao
					.doQuery(
							"select b.TYPE as TYPE,sum(a.ZJJE) as ZJJE from ZY_FYMX a,YK_TYPK b where a.YPLX in (1,2,3) and a.FYXH = b.YPXH and a.ZYH = :ZYH group by b.TYPE",
							parameters);
			if (list_ZXYFY != null) {
				for (int i = 0; i < list_ZXYFY.size(); i++) {
					Map<String, Object> ZXYFY = list_ZXYFY.get(i);
					if ("1".equals(ZXYFY.get("TYPE") + "")) {
						JBXX.put("XYF", ZXYFY.get("ZJJE"));
					} else if ("2".equals(ZXYFY.get("TYPE") + "")) {
						JBXX.put("ZCYF", ZXYFY.get("ZJJE"));
					} else if ("3".equals(ZXYFY.get("TYPE") + "")) {
						JBXX.put("ZCY", ZXYFY.get("ZJJE"));
					}
				}
			}
			List<Map<String, Object>> list_SFXMFY = dao
					.doSqlQuery(
							"select BASYGB as BASYGB,sum(ZJJE) as ZJJE from (select b.BASYGB,a.ZJJE from ZY_FYMX a,GY_YLSF b where a.YPLX=0 and a.FYXH=b.FYXH and b.BASYGB is not null and a.ZYH = :ZYH union all select b.BASYGB,a.ZJJE from ZY_FYMX a,GY_SFXM b where a.YPLX=0 and a.FYXM = b.SFXM and b.BASYGB is null and a.ZYH = :ZYH) group by BASYGB",
							parameters);
			if (list_SFXMFY != null) {
				for (int i = 0; i < list_SFXMFY.size(); i++) {
					Map<String, Object> SFXMFY = list_SFXMFY.get(i);
					if ("11".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("YBYLFWF", SFXMFY.get("ZJJE"));
					} else if ("12".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("YBZLCZF", SFXMFY.get("ZJJE"));
					} else if ("13".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("QTFY", SFXMFY.get("ZJJE"));
					} else if ("14".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("HLF", SFXMFY.get("ZJJE"));
					} else if ("21".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("BLZDF", SFXMFY.get("ZJJE"));
					} else if ("22".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("SYSZDF", SFXMFY.get("ZJJE"));
					} else if ("23".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("LCZDXMF", SFXMFY.get("ZJJE"));
					} else if ("24".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("YXXZDF", SFXMFY.get("ZJJE"));
					} else if ("31".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("FSSZLXMF", SFXMFY.get("ZJJE"));
					} else if ("32".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("LCWLZLF", SFXMFY.get("ZJJE"));
					} else if ("33".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("ZYZLF", SFXMFY.get("ZJJE"));
					} else if ("41".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("MZF", SFXMFY.get("ZJJE"));
						// JBXX.put("SSZLF", SFXMFY.get("ZJJE"));
						JBXX.put(
								"SSZLF",
								BSPHISUtil.getDouble(
										Double.parseDouble(JBXX.get("SSZLF")
												+ "")
												+ Double.parseDouble(SFXMFY
														.get("ZJJE") + ""), 2));
					} else if ("42".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("SSF", SFXMFY.get("ZJJE"));
						JBXX.put(
								"SSZLF",
								BSPHISUtil.getDouble(
										Double.parseDouble(JBXX.get("SSZLF")
												+ "")
												+ Double.parseDouble(SFXMFY
														.get("ZJJE") + ""), 2));
					} else if ("50".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("KFF", SFXMFY.get("ZJJE"));
					} else if ("61".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("XF", SFXMFY.get("ZJJE"));
					} else if ("62".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("BDBLZPF", SFXMFY.get("ZJJE"));
					} else if ("63".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("QDBLZPF", SFXMFY.get("ZJJE"));
					} else if ("64".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("NXYZLZPF", SFXMFY.get("ZJJE"));
					} else if ("65".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("XBYZLZPF", SFXMFY.get("ZJJE"));
					} else if ("71".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("JCYCLF", SFXMFY.get("ZJJE"));
					} else if ("72".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("ZLYCLF", SFXMFY.get("ZJJE"));
					} else if ("73".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("SSYCLF", SFXMFY.get("ZJJE"));
					} else if ("99".equals(SFXMFY.get("BASYGB") + "")) {
						JBXX.put("QTF", SFXMFY.get("ZJJE"));
					}
				}
			}
			SchemaUtil.setDictionaryMassageForList(JBXX,
					BSPHISEntryNames.EMR_BASY);
			SchemaUtil.setDictionaryMassageForList(BASY,
					BSPHISEntryNames.EMR_BASY);
			List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < updates.size(); i++) {
				String updateId = (String) updates.get(i).get("id");
				Map<String, Object> reMap = new HashMap<String, Object>();
				if (JBXX.containsKey(updateId) && BASY.containsKey(updateId)) {
					if (JBXX.get(updateId) == null||(JBXX.get(updateId)+"").length()==0) {
						continue;
					}
					if ("timestamp".equals(updates.get(i).get("type") + "")) {
						if (JBXX.get(updateId) != null
								&& (JBXX.get(updateId) + "").length() > 19) {
							JBXX.put(updateId,
									(JBXX.get(updateId) + "").substring(0, 19));
						}
					} else if ("date".equals(updates.get(i).get("type") + "")) {
						if (JBXX.get(updateId) != null
								&& (JBXX.get(updateId) + "").length() > 10) {
							JBXX.put(updateId,
									(JBXX.get(updateId) + "").substring(0, 10));
						}
					}
					if ("double".equals(updates.get(i).get("type") + "")) {
						if (Double.parseDouble(JBXX.get(updateId) + "") != Double
								.parseDouble(BASY.get(updateId) + "")) {
							reMap.put("XMID", updateId);
							reMap.put("XMMC", updates.get(i).get("name"));
							reMap.put("GXXX", "更新:" + BASY.get(updateId)
									+ " → " + JBXX.get(updateId));
							reMap.put("GXSJ", JBXX.get(updateId));
							reList.add(reMap);
						}
					} else {
						if (!(JBXX.get(updateId) + "").equals(BASY
								.get(updateId) + "")) {
							reMap.put("XMID", updateId);
							reMap.put("XMMC", updates.get(i).get("name"));
							if (BASY.containsKey(updateId + "_text")) {
								reMap.put(
										"GXXX",
										"更新:" + BASY.get(updateId + "_text")
												+ " → "
												+ JBXX.get(updateId + "_text"));
							} else {
								reMap.put("GXXX", "更新:" + BASY.get(updateId)
										+ " → " + JBXX.get(updateId));
							}
							reMap.put("GXSJ", JBXX.get(updateId));
							reList.add(reMap);
						}
					}
				}
			}
			res.put("body", reList);
		} catch (PersistentDataOperationException e) {
			logger.error("fail to load ms_cf02 information by database reason",
					e);
			throw new ModelDataOperationException("获取更新信息失败！", e);
		}
	}

	public void doSaveSignature(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String userId = (String)user.getUserId();
		long BLBH = Long.parseLong(req.get("BLBH") + "");
		Map<String, Object> YLJS = BSPEMRUtil.getDocRoleByUid(userId, dao);
		long SYQX = Long.parseLong(YLJS.get("JSXH") + "");
		int JSJB = Integer.parseInt(YLJS.get("JSJB") + "");
		String QMYS = req.get("QMYS") + "";
		Date date = new Date();
		Map<String, Object> EMR_BLSY = new HashMap<String, Object>();
		EMR_BLSY.put("BLBH", BLBH);
		EMR_BLSY.put("QMYS", QMYS);
		EMR_BLSY.put("JGID", manageUnit);
		EMR_BLSY.put("SYYS", userId);
		EMR_BLSY.put("SYQX", SYQX);
		EMR_BLSY.put("SYSJ", date);
		EMR_BLSY.put("JLSJ", date);
		EMR_BLSY.put("QXJB", JSJB);
		EMR_BLSY.put("BZXX", "/");
		try {
			dao.doSave("create", BSPHISEntryNames.EMR_BLSY, EMR_BLSY, false);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BLBH", BLBH);
			dao.doUpdate("update EMR_BASY set " + QMYS
					+ " = " + userId + " where JLXH = :BLBH", parameters);
			parameters.put("WCSJ", new Date());
			dao.doUpdate("update EMR_BL01 set BLZT = 1,WCSJ=:WCSJ where BLBH = :BLBH", parameters);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病历签名失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病历签名失败！", e);
		}
	}

	public void doUpdateSignature(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String uid = req.get("uid") + "";
		String psw = req.get("psw") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("password", MD5StringUtil.MD5Encode(psw));
		parameters.put("userId", uid);

		try {
			long userCount = dao.doCount("ctd.account.user.User",
					"password = :password and id = :userId", parameters);
			if (userCount == 0) {
				res.put("body", "密码错误请重新输入...");
				return;
			}
			String QMYS = req.get("QMYS") + "";
			long BLBH = Long.parseLong(req.get("BLBH") + "");
			long ZYH = Long.parseLong(req.get("ZYH") + "");

			Map<String, Object> BLBHparameters = new HashMap<String, Object>();
			BLBHparameters.put("BLBH", BLBH);
			long count = dao.doCount("EMR_BLSY", "BLBH=:BLBH",
					BLBHparameters);
			Map<String, Object> BLSYparameters = new HashMap<String, Object>();
			BLSYparameters.put("BLBH", BLBH);
			BLSYparameters.put("QMYS", QMYS);
			dao.doUpdate("delete from EMR_BLSY a where a.BLBH=:BLBH and a.QMYS = :QMYS",
					BLSYparameters);
			Map<String, Object> BASYparameters = new HashMap<String, Object>();
			BASYparameters.put("BLBH", BLBH);
			dao.doUpdate("update EMR_BASY set " + QMYS
					+ " = '' where JLXH = :BLBH", BASYparameters);
			if (count == 1) {
				dao.doUpdate("update EMR_BL01 set BLZT = 0 where BLBH = :BLBH", BLBHparameters);
			}
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("YWID1", ZYH);// 一般填写就诊序号
			record.put("YWID2", BLBH);// 一般填写病历编号
			record.put("YEID3", BLBH);// 一般填写业务操作的主键值
			record.put("RZNR", "病案首页");
			BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_UNSIGNED, record, dao, ctx);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("清除签名失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doZYSSJLLoad(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		List<Map<String, Object>> schema = (List<Map<String, Object>>) req
				.get("schema");
		try {
			List<Map<String, Object>> EMR_ZYSSJL = dao.doList(cnd, "JLXH",
					BSPHISEntryNames.EMR_ZYSSJL);
			for (int i = 8; i > EMR_ZYSSJL.size();) {
				Map<String, Object> ZYSSJL = new HashMap<String, Object>();
				ZYSSJL.put("_opStatus", "create");
				for (int j = 0; j < schema.size(); j++) {
					Map<String, Object> it = schema.get(j);
					if (it.containsKey("defaultValue")) {
						if (it.containsKey("dic")) {
							String defaultValue = it.get("defaultValue") + "";
							String dv = defaultValue.substring(
									defaultValue.indexOf("key") + 4,
									defaultValue.length() - 1);
							ZYSSJL.put(it.get("id") + "", dv);
						} else {
							String defaultValue = it.get("defaultValue") + "";
							ZYSSJL.put(it.get("id") + "", defaultValue);
						}
					} else {
						ZYSSJL.put(it.get("id") + "", "");
					}
				}
				EMR_ZYSSJL.add(ZYSSJL);
			}
			SchemaUtil.setDictionaryMassageForList(EMR_ZYSSJL,
					BSPHISEntryNames.EMR_ZYSSJL);
			res.put("body", EMR_ZYSSJL);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("加载手术记录失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doZYZDJLLoad(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		/**************modify by lizhi at 修改诊断*******************/
		List<Object> rycnd = (List<Object>) req.get("rycnd");
		/**************modify by lizhi at 修改诊断*******************/
		List<Object> mzcnd = (List<Object>) req.get("mzcnd");
		List<Object> blcnd = (List<Object>) req.get("blcnd");
		List<Object> sscnd = (List<Object>) req.get("sscnd");
		List<Object> cycnd = (List<Object>) req.get("cycnd");
		List<Map<String, Object>> schema = (List<Map<String, Object>>) req
				.get("schema");
		try {
			List<Map<String, Object>> EMR_ZYZDJL = new ArrayList<Map<String, Object>>();
			// dao.doList(cnd, "JLXH", BSPHISEntryNames.EMR_ZYZDJL);
			/**************modify by lizhi at 修改诊断*******************/
			List<Map<String, Object>> RY_ZYZDJL = dao.doList(rycnd, "JLXH",
					BSPHISEntryNames.EMR_ZYZDJL);
			if (RY_ZYZDJL.size() == 0) {
				Map<String, Object> ZYZDJL = new HashMap<String, Object>();
				for (int j = 0; j < schema.size(); j++) {
					Map<String, Object> it = schema.get(j);
					if (it.containsKey("defaultValue")) {
						if (it.containsKey("dic")) {
							String defaultValue = it.get("defaultValue") + "";
							String dv = defaultValue.substring(
									defaultValue.indexOf("key") + 4,
									defaultValue.length() - 1);
							ZYZDJL.put(it.get("id") + "", dv);
						} else {
							String defaultValue = it.get("defaultValue") + "";
							ZYZDJL.put(it.get("id") + "", defaultValue);
						}
					} else {
						ZYZDJL.put(it.get("id") + "", "");
					}
				}
				ZYZDJL.put("ZZBZ", 0);
				ZYZDJL.put("ZDLB", 22);
				EMR_ZYZDJL.add(ZYZDJL);
			} else {
				EMR_ZYZDJL.add(RY_ZYZDJL.get(0));
			}
			/**************modify by lizhi at 修改诊断*******************/
			List<Map<String, Object>> MZ_ZYZDJL = dao.doList(mzcnd, "JLXH",
					BSPHISEntryNames.EMR_ZYZDJL);
			if (MZ_ZYZDJL.size() == 0) {
				Map<String, Object> ZYZDJL = new HashMap<String, Object>();
				for (int j = 0; j < schema.size(); j++) {
					Map<String, Object> it = schema.get(j);
					if (it.containsKey("defaultValue")) {
						if (it.containsKey("dic")) {
							String defaultValue = it.get("defaultValue") + "";
							String dv = defaultValue.substring(
									defaultValue.indexOf("key") + 4,
									defaultValue.length() - 1);
							ZYZDJL.put(it.get("id") + "", dv);
						} else {
							String defaultValue = it.get("defaultValue") + "";
							ZYZDJL.put(it.get("id") + "", defaultValue);
						}
					} else {
						ZYZDJL.put(it.get("id") + "", "");
					}
				}
				ZYZDJL.put("ZZBZ", 0);
				ZYZDJL.put("ZDLB", 11);
				EMR_ZYZDJL.add(ZYZDJL);
			} else {
				EMR_ZYZDJL.add(MZ_ZYZDJL.get(0));
			}
			List<Map<String, Object>> BL_ZYZDJL = dao.doList(blcnd, "JLXH",
					BSPHISEntryNames.EMR_ZYZDJL);
			if (BL_ZYZDJL.size() == 0) {
				Map<String, Object> ZYZDJL = new HashMap<String, Object>();
				for (int j = 0; j < schema.size(); j++) {
					Map<String, Object> it = schema.get(j);
					if (it.containsKey("defaultValue")) {
						if (it.containsKey("dic")) {
							String defaultValue = it.get("defaultValue") + "";
							String dv = defaultValue.substring(
									defaultValue.indexOf("key") + 4,
									defaultValue.length() - 1);
							ZYZDJL.put(it.get("id") + "", dv);
						} else {
							String defaultValue = it.get("defaultValue") + "";
							ZYZDJL.put(it.get("id") + "", defaultValue);
						}
					} else {
						ZYZDJL.put(it.get("id") + "", "");
					}
				}
				ZYZDJL.put("ZZBZ", 0);
				ZYZDJL.put("ZDLB", 44);
				EMR_ZYZDJL.add(ZYZDJL);
			} else {
				EMR_ZYZDJL.add(BL_ZYZDJL.get(0));
			}
			List<Map<String, Object>> SS_ZYZDJL = dao.doList(sscnd, "JLXH",
					BSPHISEntryNames.EMR_ZYZDJL);
			if (SS_ZYZDJL.size() == 0) {
				Map<String, Object> ZYZDJL = new HashMap<String, Object>();
				for (int j = 0; j < schema.size(); j++) {
					Map<String, Object> it = schema.get(j);
					if (it.containsKey("defaultValue")) {
						if (it.containsKey("dic")) {
							String defaultValue = it.get("defaultValue") + "";
							String dv = defaultValue.substring(
									defaultValue.indexOf("key") + 4,
									defaultValue.length() - 1);
							ZYZDJL.put(it.get("id") + "", dv);
						} else {
							String defaultValue = it.get("defaultValue") + "";
							ZYZDJL.put(it.get("id") + "", defaultValue);
						}
					} else {
						ZYZDJL.put(it.get("id") + "", "");
					}
				}
				ZYZDJL.put("ZZBZ", 0);
				ZYZDJL.put("ZDLB", 45);
				EMR_ZYZDJL.add(ZYZDJL);
			} else {
				EMR_ZYZDJL.add(SS_ZYZDJL.get(0));
			}
			List<Map<String, Object>> CY_ZYZDJL = dao.doList(cycnd, "JLXH",
					BSPHISEntryNames.EMR_ZYZDJL);
			if (CY_ZYZDJL.size() == 0) {
				Map<String, Object> ZYZDJL = new HashMap<String, Object>();
				for (int j = 0; j < schema.size(); j++) {
					Map<String, Object> it = schema.get(j);
					if (it.containsKey("defaultValue")) {
						if (it.containsKey("dic")) {
							String defaultValue = it.get("defaultValue") + "";
							String dv = defaultValue.substring(
									defaultValue.indexOf("key") + 4,
									defaultValue.length() - 1);
							ZYZDJL.put(it.get("id") + "", dv);
						} else {
							String defaultValue = it.get("defaultValue") + "";
							ZYZDJL.put(it.get("id") + "", defaultValue);
						}
					} else {
						ZYZDJL.put(it.get("id") + "", "");
					}
				}
				ZYZDJL.put("ZZBZ", 1);
				ZYZDJL.put("ZDLB", 51);
				EMR_ZYZDJL.add(ZYZDJL);
			} else {
				EMR_ZYZDJL.addAll(CY_ZYZDJL);
			}
			SchemaUtil.setDictionaryMassageForList(EMR_ZYZDJL,
					BSPHISEntryNames.EMR_ZYZDJL);
			for (int i = 0; i < EMR_ZYZDJL.size(); i++) {
				Map<String, Object> ZYZDJL = EMR_ZYZDJL.get(i);
				if ("1".equals(ZYZDJL.get("ZXLB") + "")) {
					ZYZDJL.put("JBBW_FJBS", ZYZDJL.get("JBBW_text"));
				} else {
					ZYZDJL.put("JBBW_FJBS", ZYZDJL.get("FJBS_text"));
				}
			}
			res.put("body", EMR_ZYZDJL);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("加载诊断记录失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveCommon(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String userId = (String)user.getUserId();
		parameters.put("MSZD", data.get("MSZD"));
		parameters.put("ZDYS", userId);
		parameters.put("JGID", manageUnit);
		try {
			long count = dao.doCount("EMR_GRCY", "MSZD = :MSZD and ZDYS =:ZDYS and JGID = :JGID",
					parameters);
			if (count > 0) {
				throw new ModelDataOperationException("该诊断已在常用诊断列表！");
			}
			Map<String, Object> EMR_GRCY = new HashMap<String, Object>();
			EMR_GRCY.put("ZDYS", userId);
			EMR_GRCY.put("ZXLB", data.get("ZXLB"));
			EMR_GRCY.put("JBBS", data.get("JBXH"));
			if ("1".equals(data.get("ZXLB") + "")) {
				EMR_GRCY.put("FJBS", data.get("JBBW"));
			} else if ("2".equals(data.get("ZXLB") + "")) {
				EMR_GRCY.put("FJBS", data.get("FJBS"));
			}
			EMR_GRCY.put("FJMC", data.get("JBBM"));
			EMR_GRCY.put("MSZD", data.get("MSZD"));
			EMR_GRCY.put("JBMC", data.get("JBMC"));
			EMR_GRCY.put("JGID", manageUnit);
			dao.doSave("create", BSPHISEntryNames.EMR_GRCY, EMR_GRCY, false);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("常用诊断增加失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("常用诊断增加失败！", e);
		}
	}

	public void doLoadCommon(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String userId = (String)user.getUserId();
		int pageSize = 1;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 25;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZDYS", userId);
			parameters.put("JGID", manageUnit);
			long count = dao.doCount("EMR_GRCY",
					"ZDYS =:ZDYS and JGID = :JGID", parameters);
			parameters.put("first", (pageNo - 1) * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> rebody = dao
					.doQuery(
							"select a.CYBS as CYBS,a.ZDYS as ZDYS,a.ZXLB as ZXLB,a.JBBS as JBBS,a.FJBS as FJBS,a.MSZD as MSZD,a.FJMC as FJMC,a.JGID as JGID,a.JBMC as JBMC from "
									+ "EMR_GRCY a where a.ZDYS =:ZDYS and a.JGID = :JGID order by a.CYBS",
							parameters);
			for (int i = 0; i < rebody.size(); i++) {
				Map<String, Object> GRCY = rebody.get(i);
				if ("1".equals(GRCY.get("ZXLB") + "")) {
					SchemaUtil.setDictionaryMassageForList(rebody.get(i),
							"phis.application.emr.schemas.EMR_GRCY_XY");
				} else if ("2".equals(GRCY.get("ZXLB") + "")) {
					SchemaUtil.setDictionaryMassageForList(rebody.get(i),
							"phis.application.emr.schemas.EMR_GRCY_ZY");
				}
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("totalCount", count);
			res.put("body", rebody);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("加载常用诊断失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryBASY(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String userId = (String)user.getUserId();
		long ZYH = Long.parseLong(req.get("ZYH") + "");
		int BLLB = Integer.parseInt(req.get("BLLB") + "");
		res.put("CKQX", 1);
		res.put("SXQX", 1);
		res.put("DYQX", 1);
		res.put("SYQX", 1);
		res.put("SFSY", 0);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		List<Object> cnd = (List<Object>) req.get("cnd");

		try {
			Map<String, Object> JLXHparameters = new HashMap<String, Object>();
			JLXHparameters.put("ZYH", ZYH);
			JLXHparameters.put("BLLB", BLLB);
			Map<String, Object> JLXH = dao.doLoad(
					"select a.BLBH as JLXH,a.BLZT as BLZT,a.SXYS as SXYS from "
							+ "EMR_BL01 a where a.JZXH = :ZYH and a.BLLB = :BLLB",
					JLXHparameters);
			Map<String, Object> JBXX = dao.doLoad(BSPHISEntryNames.EMR_BASY,
					JLXH.get("JLXH"));
			int RYQHMSJ_T = Integer.parseInt(JBXX.get("RYQHMSJ") + "")
					/ (60 * 24);
			int RYQHMSJ_S = (Integer.parseInt(JBXX.get("RYQHMSJ") + "") % (60 * 24)) / 60;
			int RYQHMSJ_F = Integer.parseInt(JBXX.get("RYQHMSJ") + "") % 60;
			JBXX.put("RYQHMSJ_T", RYQHMSJ_T);
			JBXX.put("RYQHMSJ_S", RYQHMSJ_S);
			JBXX.put("RYQHMSJ_F", RYQHMSJ_F);
			int RYHHMSJ_T = Integer.parseInt(JBXX.get("RYHHMSJ") + "")
					/ (60 * 24);
			int RYHHMSJ_S = (Integer.parseInt(JBXX.get("RYHHMSJ") + "") % (60 * 24)) / 60;
			int RYHHMSJ_F = Integer.parseInt(JBXX.get("RYHHMSJ") + "") % 60;
			JBXX.put("RYHHMSJ_T", RYHHMSJ_T);
			JBXX.put("RYHHMSJ_S", RYHHMSJ_S);
			JBXX.put("RYHHMSJ_F", RYHHMSJ_F);
			SchemaUtil.setDictionaryMassageForList(JBXX,
					BSPHISEntryNames.EMR_BASY);
			Map<String, Object> FYTJ = dao.doLoad(BSPHISEntryNames.EMR_BASY_FY,
					JLXH.get("JLXH"));
			SchemaUtil.setDictionaryMassageForList(FYTJ,
					BSPHISEntryNames.EMR_BASY_FY);
			JBXX.putAll(FYTJ);

			// res.put("FYTJ", FYTJ);
			// Map<String, Object> BASY = new HashMap<String, Object>();
			// BASY.putAll(JBXX);
			// BASY.putAll(FYTJ);

			List<Map<String, Object>> ZYSSJL = dao.doList(cnd, "JLXH",
					BSPHISEntryNames.EMR_ZYSSJL);
			SchemaUtil.setDictionaryMassageForList(ZYSSJL,
					BSPHISEntryNames.EMR_ZYSSJL);

			List<Object> mzzdcnd = CNDHelper.createArrayCnd("and", cnd,
					CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 11));
			List<Object> blzdcnd = CNDHelper.createArrayCnd("and", cnd,
					CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 44));
			List<Object> sszdzdcnd = CNDHelper.createArrayCnd("and", cnd,
					CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 45));
			List<Object> cyzdcnd = CNDHelper.createArrayCnd("and", cnd,
					CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 51));
			Map<String, Object> MZ_ZYZDJLS = dao.doLoad(mzzdcnd,
					BSPHISEntryNames.EMR_ZYZDJL);
			if (MZ_ZYZDJLS != null) {
				SchemaUtil.setDictionaryMassageForList(MZ_ZYZDJLS,
						BSPHISEntryNames.EMR_ZYZDJL);
				JBXX.put("MZZD", MZ_ZYZDJLS.get("MSZD"));
				JBXX.put("MZ_JBBM", MZ_ZYZDJLS.get("JBBM"));
			}
			Map<String, Object> BL_ZYZDJLS = dao.doLoad(blzdcnd,
					BSPHISEntryNames.EMR_ZYZDJL);
			if (BL_ZYZDJLS != null) {
				SchemaUtil.setDictionaryMassageForList(BL_ZYZDJLS,
						BSPHISEntryNames.EMR_ZYZDJL);
				JBXX.put("BLZD", BL_ZYZDJLS.get("MSZD"));
				JBXX.put("BL_JBBM", BL_ZYZDJLS.get("JBBM"));
			}
			Map<String, Object> SSZD_ZYZDJLS = dao.doLoad(sszdzdcnd,
					BSPHISEntryNames.EMR_ZYZDJL);
			if (SSZD_ZYZDJLS != null) {
				SchemaUtil.setDictionaryMassageForList(SSZD_ZYZDJLS,
						BSPHISEntryNames.EMR_ZYZDJL);
				JBXX.put("SSZD", SSZD_ZYZDJLS.get("MSZD"));
				JBXX.put("SS_JBBM", SSZD_ZYZDJLS.get("JBBM"));
			}
			List<Map<String, Object>> ZYZDJL = dao.doList(cyzdcnd, "JLXH",
					BSPHISEntryNames.EMR_ZYZDJL);
			SchemaUtil.setDictionaryMassageForList(ZYZDJL,
					BSPHISEntryNames.EMR_ZYZDJL);
			res.put("ZYSSJL", ZYSSJL);
			res.put("count", 0);
			res.put("JBXX", JBXX);
			res.put("ZYZDJL", ZYZDJL);
			res.put("NEW", 0);
		} catch (PersistentDataOperationException e) {
			logger.error("加载病历首页失败！", e);
			throw new ModelDataOperationException("加载病历首页失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveZYZDJL(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		long ZYH = Long.parseLong(req.get("ZYH") + "");
		long BRID = Long.parseLong(req.get("BRID") + "");
		List<Map<String, Object>> EMR_ZYZDJLs = (List<Map<String, Object>>) req
				.get("data");
		Map<String, Object> delparameters = new HashMap<String, Object>();
		delparameters.put("ZYH", ZYH);
		try {
			dao.doUpdate("delete from EMR_ZYZDJL a where a.JZXH = :ZYH", delparameters);
			/*********************add by lizhi 病案首页诊断同步到病人信息诊断****************************/
			Map<String, Object> delparams = new HashMap<String, Object>();
			delparams.put("ZYH", ZYH);
			dao.doUpdate("delete from ZY_RYZD where ZYH=:ZYH and ZDLB in (1,2,3,4)", delparams);
			/*********************add by lizhi 病案首页诊断同步到病人信息诊断****************************/
			if (EMR_ZYZDJLs.size() > 0) {
				// dao.doRemove("JZXH", EMR_BL01.get("BLBH"),
				// BSPHISEntryNames.EMR_ZYZDJL);
				int cyzd = 0;
				int zzzd = 0;
				for (int i = 0; i < EMR_ZYZDJLs.size(); i++) {
					Map<String, Object> EMR_ZYZDJL = EMR_ZYZDJLs.get(i);
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						cyzd++;
					}
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")
							&& "1".equals(EMR_ZYZDJL.get("ZZBZ") + "")) {
						zzzd++;
					}
				}
				if (cyzd > 0 && zzzd == 0) {
					for (int i = 0; i < EMR_ZYZDJLs.size(); i++) {
						Map<String, Object> EMR_ZYZDJL = EMR_ZYZDJLs.get(i);
						if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
							EMR_ZYZDJLs.get(i).put("ZZBZ", 1);
							break;
						}
					}
				}
				int SFRYZD = 0;
				int SFMQZD = 0;
				String RYMSZD = "";
				String MQMSZD = "";
				for (int i = 0; i < EMR_ZYZDJLs.size(); i++) {
					Map<String, Object> EMR_ZYZDJL = EMR_ZYZDJLs.get(i);
					EMR_ZYZDJL.put("JZXH", ZYH);
					EMR_ZYZDJL.put("BRID", BRID);
					EMR_ZYZDJL.put("JGID", manageUnit);
					EMR_ZYZDJL.put("XTSJ", new Date());
					EMR_ZYZDJL.put("ZNXH", 0);
					dao.doSave("create", BSPHISEntryNames.EMR_ZYZDJL,
							EMR_ZYZDJL, false);
					if ("22".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						SFRYZD = 1;
						RYMSZD = EMR_ZYZDJL.get("MSZD") + "";
						SFMQZD++;
						MQMSZD += SFMQZD + "." + EMR_ZYZDJL.get("MSZD") + "";
					}
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						SFMQZD++;
						MQMSZD += SFMQZD + "." + EMR_ZYZDJL.get("MSZD") + "";
					}
					/*********************add by lizhi 病案首页诊断同步到病人信息诊断****************************/
					if(!"11".equals(EMR_ZYZDJL.get("ZDLB") + "") && !"22".equals(EMR_ZYZDJL.get("ZDLB") + "") && !"51".equals(EMR_ZYZDJL.get("ZDLB") + "")){
						continue;
					}
					Map<String, Object> ZY_RYZD = new HashMap<String, Object>();
					ZY_RYZD.put("ZYH", Long.parseLong(EMR_ZYZDJL.get("JZXH") + ""));
					ZY_RYZD.put("ZDXH", Long.parseLong(EMR_ZYZDJL.get("JBXH") + ""));
					ZY_RYZD.put("JGID", EMR_ZYZDJL.get("JGID") + "");
					ZY_RYZD.put("ZGQK", Long.parseLong(EMR_ZYZDJL.get("CYZGDM") + ""));
					ZY_RYZD.put("TXBZ", 0);
					ZY_RYZD.put("ICD10", EMR_ZYZDJL.get("JBBM") + "");
					if("1".equals(EMR_ZYZDJL.get("ZXLB") + "")){//西医
						ZY_RYZD.put("ZDBW", Long.parseLong(EMR_ZYZDJL.get("JBBW") + ""));
					}else{//中医症候
						ZY_RYZD.put("ZDBW", Long.parseLong(EMR_ZYZDJL.get("FJBS") + ""));
					}
					ZY_RYZD.put("ZDMC", EMR_ZYZDJL.get("MSZD") + "");
					ZY_RYZD.put("ZDSJ", EMR_ZYZDJL.get("ZDRQ"));
					ZY_RYZD.put("ZDYS", EMR_ZYZDJL.get("ZDYS") + "");
					ZY_RYZD.put("ZXLB", Long.parseLong(EMR_ZYZDJL.get("ZXLB") + ""));
					if("11".equals(EMR_ZYZDJL.get("ZDLB") + "")){//门诊诊断
						ZY_RYZD.put("ZDLB", 1);
					}
					if("22".equals(EMR_ZYZDJL.get("ZDLB") + "")){//入院诊断
						ZY_RYZD.put("ZDLB", 2);
					}
					if ("51".equals(EMR_ZYZDJL.get("ZDLB") + "")) {
						if("1".equals(EMR_ZYZDJL.get("ZZBZ") + "")){//出院主诊断
							ZY_RYZD.put("ZDLB", 3);
						}else{//出院次诊断
							ZY_RYZD.put("ZDLB", 4);
						}
					}
					dao.doSave("create", BSPHISEntryNames.ZY_RYZD_BQ,
							ZY_RYZD, false);
					/*********************add by lizhi 病案首页诊断同步到病人信息诊断****************************/
				}
				if (EMR_ZYZDJLs.size() > 0) {
					Map<String, Object> BRRY = new HashMap<String, Object>();
					if (SFRYZD == 1) {
						BRRY.put("RYZD", RYMSZD);
						if (MQMSZD.length() > 127) {
							BRRY.put("MQZD", MQMSZD.substring(0, 127));
						} else {
							BRRY.put("MQZD", MQMSZD);
						}
						BRRY.put("ZYH", ZYH);
						dao.doSave("update", BSPHISEntryNames.ZY_BRRY, BRRY,
								false);
					}
				}

			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病人诊断保存失败！", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病人诊断保存失败！", e);
		}
	}

	public void doQueryUser(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String uid = req.get("uid") + "";
		String psw = req.get("psw") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("password", MD5StringUtil.MD5Encode(psw));
		parameters.put("userId", uid);
		try {
			long count = dao.doCount("ctd.account.user.User",
					"password = :password and id = :userId", parameters);
			if (count == 0) {
				res.put("body", "密码错误请重新输入...");
				// throw new ModelDataOperationException("密码错误请重新输入...");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询用户信息失败！", e);
		}
	}

	
	/**
	 * @param req
	 * @throws ModelDataOperationException
	 * @description 住院病案首页删除手术记录
	 * @author tanc
	 */
	public void doDeleteOperationRecord(Map<String, Object> req) throws ModelDataOperationException {
		long jlxh = Long.parseLong(req.get("pkey").toString());
		String schema = req.get("schema").toString();
		try {
			dao.doRemove(jlxh, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除诊断出错", e);
		}
	}


}