package phis.application.reg.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class AnAppointmentModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public AnAppointmentModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doQueryPerson(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		try {
			if(req.containsKey("JZKH")){
				String JZKH = (String) req.get("JZKH");
				Map<String, Object> MPI_Cardparameters = new HashMap<String, Object>();
				MPI_Cardparameters.put("JZKH", JZKH);
				Map<String, Object> MPI_Card = dao
						.doLoad("select empiId as empiId from MPI_Card where cardNo=:JZKH and cardTypeCode="+KLX,
								MPI_Cardparameters);
				if(MPI_Card != null){
					String sql = "SELECT a.BRID as BRID,a.MZHM as MZHM,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.BRXZ as BRXZ,b.address as LXDZ FROM MS_BRDA a,MPI_DemographicInfo b WHERE a.EMPIID=b.empiId and a.EMPIID=:EMPIID";
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("EMPIID", MPI_Card.get("empiId"));
					Map<String, Object> personMap = dao.doLoad(sql, parameters);
					if (personMap != null) {
						if (personMap.get("CSNY") != null) {
							personMap
									.put("AGE", BSPHISUtil.getPersonAge((Date) personMap.get("CSNY"), null).get("ages"));
						}
						res.put("body", personMap);
					}
				}
			}else{
				String MZHM = (String) req.get("MZHM");
				String sql = "SELECT a.BRID as BRID,a.MZHM as MZHM,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY,a.BRXZ as BRXZ,b.address as LXDZ,a.EMPIID as EMPIID FROM MS_BRDA a,MPI_DemographicInfo b WHERE a.EMPIID=b.empiId and a.MZHM=:MZHM";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("MZHM", MZHM);
				Map<String, Object> personMap = dao.doLoad(sql, parameters);
				if (personMap != null) {
					if (personMap.get("CSNY") != null) {
						personMap
								.put("AGE", BSPHISUtil.getPersonAge((Date) personMap.get("CSNY"), null).get("ages"));
					}
					Map<String, Object> MPI_Cardparameters = new HashMap<String, Object>();
					MPI_Cardparameters.put("empiId", personMap.get("EMPIID"));
					Map<String,Object> MPI_Card = dao.doLoad("select a.cardNo as cardNo from MPI_Card a where a.cardTypeCode="+KLX+" and a.empiId=:empiId", MPI_Cardparameters);
					if(MPI_Card != null){
						personMap.put("JZKH", MPI_Card.get("cardNo"));
					}
					res.put("body", personMap);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 预约病人查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGetAnAppointment(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object> queryCnd = new ArrayList<Object>();
		int brid = 0;
		Date ghsj = null;
		int zblb = 0;
		if (req.containsKey("cnd")) {
			queryCnd = (List<Object>) req.get("cnd");
		}
		try {
		if(queryCnd.size()>0){
		brid = Integer.parseInt(queryCnd.get(0)+"");
		ghsj = sdf.parse(queryCnd.get(1)+"");
		zblb = Integer.parseInt(queryCnd.get(2)+"");
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
			String sql = "select a.YYXH as YYXH,a.JZXH as JZXH,d.MZHM as MZHM,a.KSDM as KSDM,b.KSMC as KSDM_text,a.YSDM as YSDM,c.PERSONNAME as YSDM_text from MS_YYGH a,MS_GHKS b,SYS_Personnel c,MS_BRDA d where a.KSDM=b.KSDM and a.YSDM=c.PERSONID and a.BRID=d.BRID and a.JGID=:JGID and a.BRID=:BRID and a.YYRQ=:YYRQ and a.ZBLB=:ZBLB and a.GHBZ=0 ";
			parameters.put("JGID", manaUnitId);
			parameters.put("BRID", brid);
			parameters.put("YYRQ", ghsj);
			parameters.put("ZBLB", zblb);
//			System.out.println(parameters
//					+ "parametersparametersparametersparameters");
			list = dao.doSqlQuery(sql, parameters);
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("KSDM_text", list.get(i).get("KSDM_TEXT"));
				list.get(i).put("YSDM_text", list.get(i).get("YSDM_TEXT"));
			}
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}