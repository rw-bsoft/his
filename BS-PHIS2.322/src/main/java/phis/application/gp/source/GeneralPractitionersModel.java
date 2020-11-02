package phis.application.gp.source;

import com.bsoft.mpi.util.CndHelper;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import ctd.validator.Validator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.application.ivc.source.ClinicChargesDiscount;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: Fengld
 * @Date: 2019/3/4 15:57
 * @Description: 家医签约实现层
 */
public class GeneralPractitionersModel {
    protected BaseDAO dao;
    protected Logger logger = LoggerFactory.getLogger(GeneralPractitionersModel.class);


    public GeneralPractitionersModel(BaseDAO dao) {
        this.dao = dao;
    }


    public Map<String, Object> GetSCIDWithIdcard(Map<String, Object> req) {

        return null;
    }

    public Map<String, Object> getSCIDWithEmpiId(Map<String, Object> req) {
        return null;
    }

    public Map<String, Object> QueryGpDetail(Map<String, Object> req) {
        return null;
    }

    public Map<String, Object> UpdateGpStatus(Map<String, Object> req) {
        return null;
    }

    public Map<String, Object> UpdateGpServerNums(Map<String, Object> req) {
        return null;
    }

	public Map<String, Object> getGpxtcs(Map<String, Object> req)
			throws ModelDataOperationException {
		String empiid = req.get("body").toString();
		String manaUnitId = UserRoleToken.getCurrent().getManageUnitId();
		Map<String, Object> body = getGpxtcs(manaUnitId, empiid);
		return body;
		// return null;
	}

	public Map<String, Object> getGpxtcs(String manaUnitId, String empiid) throws ModelDataOperationException {
		String sql = "select CSMC as CSMC , CSZ as CSZ from GY_XTCS where CSMC in ('SFQYJYQY' , 'JYKS' , 'JYFY') and JGID =:jgid ";
		Map<String, Object> parm = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		parm.put("jgid", manaUnitId);
		try {
			List<Map<String, Object>> gpcs = dao.doSqlQuery(sql, parm);
			for (Map<String, Object> cs : gpcs) {
				if ("SFQYJYQY".equals(cs.get("CSMC"))) {
					result.put("SFQYJYQY", cs.get("CSZ"));
				} else if ("JYKS".equals(cs.get("CSMC"))) {
					result.put("JYKS", cs.get("CSZ"));
				} else if ("JYFY".equals(cs.get("CSMC"))) {
					result.put("JYFY", cs.get("CSZ"));
				}
			}
			if (!empiid.equals("")) {
				parm.remove("jgid");
				parm.put("EMPIID", empiid);
				sql = "select wm_concat(SFXM) as JYFY from SCM_SERVICEPACKAGE p where p.SPID in("
						+ "select a.SPID from SCM_SIGNCONTRACTPACKAGE a,SCM_SIGNCONTRACTRECORD b "
						+ "where a.SCID=b.SCID and EMPIID=:EMPIID and b.ENDDATE+1>sysdate and b.SIGNFLAG in(1,3))";
				Map<String, Object> jyfy = dao.doSqlLoad(sql, parm);
				result.put("JYFY",( jyfy.get("JYFY")==null?"":jyfy.get("JYFY").toString()));
			}
			if (result.isEmpty()) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "请先维护家医签约参数");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "读取家医签约参数失败");
		}
		return result;
	}

    public void SaveGpService(Map<String, Object> saveData) throws ModelDataOperationException {
        //String jgid = saveData.get("manaUnit").toString();
        String jgid = UserRoleToken.getCurrent().getManageUnitId();
        Map<String, Object> gpXtcs = getGpxtcs(jgid, "");
        Map<String, Object> formData = (Map<String, Object>) saveData.get("formData");
        List<Map<String, Object>> listData = (List<Map<String, Object>>) saveData.get("listData");
        List<Map<String, Object>> yjlistData = (List<Map<String, Object>>) saveData.get("yjlistData");
        List<Map<String, Object>> planList = (List<Map<String, Object>>) saveData.get("planList");

        String EmpiId = formData.get("favoreeEmpiId").toString();

        List<?> cnd_brda = CndHelper.createSimpleCnd("eq", "a.EMPIID", "s", EmpiId);
        try {
        	String yjxh = "";
        	Long brid = 0L,jyks = 0L;
            //取得beginDate和endDate
/*            String sql = "select BEGINDATE as BEGINDATE, ENDDATE as ENDDATE from SCM_SignContractRecord where SCID=:scid";
            Map<String, Object> parm = new HashMap<String, Object>();
            parm.put("scid", formData.get("scid"));
            Map<String, Object> dateFormTo = dao.doSqlLoad(sql, parm);*/
            Map<String, Object> brda = dao.doLoad(cnd_brda, BSPHISEntryNames.MS_BRDA);
            /* if (brda == null || brda.isEmpty()) {
                //todo 创建病人档案
                throw new ModelDataOperationException(
                        ServiceCode.NO_RECORD, "病人未挂号");
            }*/
            if (!(brda == null || brda.isEmpty()) && formData.get("loginSystem").toString().equals("phis")) {
                /*if (formData.get("ksdm") == null && gpXtcs.get("JYKS") == null) {
                    throw new ModelDataOperationException(
                            ServiceCode.CODE_ERROR, " 当前机构的系统参数JYKS未配置！");
                }*/
            	jyks = formData.get("ksdm") != null ? Long.parseLong(formData.get("ksdm").toString()) : 0;
                Map<String, Object> record_01 = new HashMap<String, Object>();
                record_01.put("JGID", jgid);
                record_01.put("BRID", brda.get("BRID"));
                record_01.put("BRXM", brda.get("BRXM"));
                record_01.put("KDRQ", new Date());
                record_01.put("KSDM", jyks);
                record_01.put("ZXKS", jyks);
                record_01.put("YSDM", formData.get("createUser"));
                record_01.put("ZXPB", "0");
                record_01.put("HJGH", formData.get("createUser"));
                record_01.put("ZFPB", "0");
                record_01.put("CFBZ", "0");
                record_01.put("JZXH", formData.get("jzxh") != null ? Long.parseLong(formData.get("jzxh").toString()) : 0);
                record_01.put("DJZT", "0");
                record_01.put("DJLY", "1");
                Long sqid;
                String sq="select SEQ_SQID.nextval as SQXH from dual ";
                List<Map<String, Object>> list_2 = new ArrayList<Map<String, Object>>();
                list_2=	dao.doSqlQuery(sq, null);
                for(Map<String,Object> o: list_2){
                    sqid=((BigDecimal)o.get("SQXH")).longValue();
                    record_01.put("SQID", sqid);
                }
                Map<String, Object> yj01 = dao.doSave("create", BSPHISEntryNames.MS_YJ01_CIC, record_01, false);
                yjxh = yj01.get("YJXH").toString();
            }
            for (Map<String, Object> itemMap : yjlistData) {
            	String sbxh = "";
            	if(!yjxh.equals("")){
                    Map<String, Object> record_02 = new HashMap<String, Object>();
                    record_02.put("JGID", jgid);
                    record_02.put("YJXH", yjxh);
                    record_02.put("YLXH", itemMap.get("SFXM"));
                    //给默认值，根据业务修改
                    record_02.put("XMLX", "0");
                    record_02.put("YJZX", "1");
                    //需要根据家医合计金额给出，合计多少钱写入多少钱
                    record_02.put("YLDJ", itemMap.get("totalCost"));
                    record_02.put("YLSL", "1");
                    record_02.put("HJJE", itemMap.get("totalCost"));
                    record_02.put("FYGB", "21");
                    record_02.put("ZFBL", "1");
                    record_02.put("DZBL", "1");
                    record_02.put("YJZH", "1");
                    record_02.put("ZFPB", "0");
                    record_02.put("CXBZ", "0");
                    Map<String, Object> jy02 = dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC, record_02, false);   
                    sbxh = jy02.get("SBXH").toString();
            	}
                Map<String, Object> gpServiceRecord = new HashMap<String, Object>();
                gpServiceRecord.put("EMPIID", EmpiId);
                gpServiceRecord.put("MANAUNIT", jgid);
                gpServiceRecord.put("MANADOC", formData.get("createUser"));
                gpServiceRecord.put("SCDATE", formData.get("scDate"));
                gpServiceRecord.put("LOGOFF", "2");
                gpServiceRecord.put("STARTDATE", formData.get("beginDate"));
                gpServiceRecord.put("ENDDATE", formData.get("endDate"));
                gpServiceRecord.put("MODIFYUSER", formData.get("createUser"));
                gpServiceRecord.put("MODIFYUNIT", jgid);
                gpServiceRecord.put("GPKS", jyks.toString());
                gpServiceRecord.put("YJXH", yjxh);
                gpServiceRecord.put("SBXH", sbxh);
                gpServiceRecord.put("SCID", formData.get("scid"));
                gpServiceRecord.put("SPID", itemMap.get("SPID"));
                Map<String, Object> gpSaveResult = dao.doSave("create", BSPHISEntryNames.SCM_INCREASESERVER, gpServiceRecord, false);
                //不算钱的实际价格算0
                for (Map<String, Object> item : listData) {
//                    if (item.containsKey("itemNature") && "2".equals(item.get("itemNature"))) {
                    if(item.containsKey("SPID") && itemMap.get("SPID").equals(item.get("SPID"))){
                        Map<String, Object> gpItemRecord = new HashMap<String, Object>();
                        gpItemRecord.put("SCINID", gpSaveResult.get("SCINID"));
                        gpItemRecord.put("FYXH", item.get("fyxh"));
                        gpItemRecord.put("ZFBL", item.get("hisZFBL"));
                        gpItemRecord.put("DISCOUNT", item.get("discount"));
                        gpItemRecord.put("SPIID", item.get("SPIID"));
                        gpItemRecord.put("SPID", item.get("SPID"));
                        for (Map<String, Object> planitem : planList) {
                            if(planitem.containsKey("SCID") && planitem.containsKey("taskCode")  && planitem.containsKey("SPID") 
                            		&& planitem.get("SCID").equals(formData.get("scid")) && planitem.get("taskCode").equals(item.get("itemCode")) 
                            		&& planitem.get("SPID").equals(item.get("SPID"))){
                    			gpItemRecord.put("TASKID", planitem.get("taskId"));
                    			break;
                            }
                        }
                        gpItemRecord.put("TASKCODE", item.get("itemCode"));
                        gpItemRecord.put("SERVICETIMES", 0);
                        gpItemRecord.put("TOTSERVICETIMES", item.get("SERVICETIMES"));
                        gpItemRecord.put("SCID", formData.get("scid"));
                        dao.doSave("create", BSPHISEntryNames.SCM_INCREASEITEMS, gpItemRecord, false);
                    }
//                    }
                }
            }
        } catch (PersistentDataOperationException e) {
            logger.error("GP-保存增值服务项失败"+e.getMessage());
            throw new ModelDataOperationException(
                    ServiceCode.CODE_DATABASE_ERROR, "病人档案查询失败"+e.getMessage());
        } catch (ValidateException e) {
            logger.error("GP-保存增值服务项失败"+e.getMessage());
            throw new ModelDataOperationException(
                    ServiceCode.CODE_DATABASE_ERROR, "签约记录写入失败"+e.getMessage());
        }
    }

    /**
     * @param op
     * @param entryName
     * @param record
     * @param validate
     * @return
     * @throws ServiceException
     * @Description:更新表数据
     * @Modify:
     */
    public Map<String, Object> doSave(String op, String entryName,
                                      Map<String, Object> record, boolean validate)
            throws ServiceException {
        Context ctx = ContextUtils.getContext();
        SimpleDAO dao = null;
        Map<String, Object> genValues = null;
        try {
            Schema sc = SchemaController.instance().get(entryName);
            dao = new SimpleDAO(sc, ctx);
            if (validate) {
                Validator.validate(sc, record, ctx);
            }
            if (StringUtils.isEmpty(op)) {
                op = "create";
            }
            if (op.equals("create")) {
                genValues = dao.create(record);
            } else {
                genValues = dao.update(record);
            }
        } catch (DataAccessException e) {
            throw new ServiceException(e);
        } catch (ServiceException e) {
            throw new ServiceException(e);
        } catch (ControllerException e) {
            throw new ServiceException(e);
        }

        return genValues;
    }

	public Map<String, Object> queryIsGP(Map<String, Object> req)
			throws NumberFormatException, ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> jsdata = (List<Map<String, Object>>) body
				.get("data");
		List<Map<String, Object>> gpdata = this.queryGpDetil(req);
		Map<String, Object> gpbrda = new HashMap<String, Object>();
		// try {
		// gpbrda = dao.doLoad(BSPHISEntryNames.MS_BRDA, req.get("body"));
		boolean isGp = new ClinicChargesDiscount(dao).validateByYwlx("1",
				Long.parseLong(body.get("brid").toString()), "");

		gpbrda.put("ISGP", (isGp && gpdata.size() > 0) ? 1 : 0);
		// } catch (PersistentDataOperationException e) {
		// e.printStackTrace();
		// }
		return gpbrda;

    }

    /**
     * 家医增值服务激活
     *
     * @param req
     */
    public void logOnGP(Map<String, Object> req) {
        Map<String, Object> body = (Map<String, Object>) req.get("body");

        Map<String, Object> parm1 = new HashMap<String, Object>();
        Map<String, Object> parm2 = new HashMap<String, Object>();

        String str_sbxh = "";
        List<String> sbxhs = (List<String>) body.get("SBXH");
        for (String sbxh : sbxhs) {
            str_sbxh += "'" + sbxh + "',";
        }
        String str = str_sbxh.substring(0, str_sbxh.length() - 1);
        parm1.put("brid", body.get("brid"));
        parm2.put("sbxh", str);
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        sb1.append("update MS_BRDA set ISGP = '1' where BRID =:brid");
        sb2.append("update SCM_INCREASESERVER set LOGOFF = '1' where SBXH in(").append(str).append(")");
        try {
            dao.doUpdate(sb1.toString(), parm1);
            dao.doSqlUpdate(sb2.toString(), null);
        } catch (PersistentDataOperationException e) {
            logger.error("GP-激活家医标志失败" + body);
            e.printStackTrace();
        }
    }

	public List<Map<String, Object>> queryGpDetil(Map<String, Object> req) throws PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("EMPIID", body.get("EMPIID"));
		
		// List cnd = CNDHelper.createSimpleCnd("eq", "b.EMPIID", "s",
		// body.get("EMPIID"));
		// List<Map<String, Object>> result = dao.doList(cnd, null,
		// BSPHISEntryNames.SCM_INCREASEITEMSDETIL);
		String sql = "select a.SCIID as SCIID,a.SERVICETIMES as SERVICETIMES,a.TOTSERVICETIMES as TOTSERVICETIMES,b.EMPIID as EMPIID,"
				+ "b.MANAUNIT as MANAUNIT,b.MaNADOC as MaNADOC,b.SCDATE as SCDATE,b.LOGOFF as LOGOFF,b.STARTDATE as STARTDATE,"
				+ "b.ENDDATE as ENDDATE,a.SCINID as SCINID,a.FYXH as FYXH,c.FYMC as FYMC,a.DISCOUNT as DISCOUNT,a.ZFBL as ZFBL,a.SPIID as SPIID,"
				+ "d.favoreeName as favoreeName,d.createUnit as createUnit,d.createUser as createUser,e.packageName as packageName,a.TASKID as TASKID,"
				+ "a.TASKCODE as TASKCODE,a.SPID as SPID, a.SCID as SCID "
				+ "from SCM_INCREASEITEMS a,SCM_INCREASESERVER b,GY_YLSF c,SCM_SignContractRecord d,SCM_ServicePackage e"
				+ " where a.SCINID=b.SCINID and a.FYXH=c.FYXH and a.SCID=d.SCID and a.SPID=e.SPID " 
				+ "and a.TOTSERVICETIMES>0 and b.EMPIID=:EMPIID "
				+ "and a.FYXH is not null and a.TOTSERVICETIMES>SERVICETIMES and signflag=1 and d.enddate>sysdate order by b.SCID,b.SCDATE";
		List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
		list = SchemaUtil.setDictionaryMassageForList(list,
				"phis.application.gp.schemas.SCM_INCREASEITEMSDETIL");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if(body.get("data")!=null){
			List<Map<String, Object>> datalist = (List<Map<String, Object>>) (List<Map<String, Object>>) body.get("data");
			for (Map<String, Object> item : list) {
				for (Map<String, Object> data : datalist) {
					if(item.get("FYXH").toString().equals(data.get("YPXH").toString()) && !result.contains(item)){
						result.add(item);
					}
				}
			}
		}else{
			result = list;
		}
		return result;
	}

    public void updateServiceTimes(Map<String, Object> req) throws PersistentDataOperationException, ValidateException, ModelDataOperationException {
        UserRoleToken user = UserRoleToken.getCurrent();
        String userid = user.getUserId() + "";
        String JGID = user.getManageUnit().getId();
        List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try{
				Map<String, Object> log01 = new HashMap<String, Object>();
				Map<String, Object> log02 = new HashMap<String, Object>();
				String empiid = body.get(0).get("EMPIID").toString();
				log01.put("empiId", body.get(0).get("EMPIID"));
				log01.put("logTime", new Date());
				log01.put("manaunitId", JGID);
				log01.put("LogUser", userid);
				log01.put("fphm", body.get(0).get("FPHM"));
				Map<String, Object> logInfo = dao.doSave("create",	BSPHISEntryNames.SCM_LOG01, log01, false);
		
		        for (Map<String, Object> info : body) {
		            log02.put("log1Id", logInfo.get("log1Id"));
		            log02.put("SPIID", info.get("SPIID"));
		            log02.put("serviceTimes", info.get("TOTSERVICETIMES"));
		            log02.put("costTimes", info.get("COSTTIMES").toString().split("\\.")[0]);
		            log02.put("fphm", info.get("FPHM"));
		            log02.put("sciId", info.get("SCIID"));
		            log02.put("scinId", info.get("SCINID"));
		//            log02.put("unitPrice", 0d);
		            log02.put("totPrice", info.get("HJJE"));
		            dao.doSave("create", BSPHISEntryNames.SCM_LOG02, log02, false);
		
					// 插入履约记录
		            Map<String, Object> saveData = new HashMap<String, Object>();
					saveData.put("taskId", info.get("TASKID"));
					saveData.put("empiId", empiid);
					saveData.put("serviceObj", info.get("FAVOREENAME"));//签约居民姓名
					saveData.put("serviceOrg", JGID);
					saveData.put("serviceTeam", info.get("CREATEUNIT"));
					saveData.put("servicer", info.get("MANADOC"));
					saveData.put("servicePackId", info.get("SPID"));
					saveData.put("servicePack", info.get("PACKAGENAME"));
					saveData.put("serviceItemsId", info.get("TASKCODE"));
					saveData.put("serviceItems", info.get("FYMC"));
					saveData.put("serviceMode", "0");
					saveData.put("serviceDate", new Date());
					saveData.put("gridAddress", JGID.substring(0,6));
					saveData.put("detailedAddress", info.get("MANAUNIT_text"));
					saveData.put("serviceDesc", "门诊收费履约"+info.get("COSTTIMES").toString().split("\\.")[0]+"次");
					saveData.put("SCID", info.get("SCID"));
					saveData.put("SPIID", info.get("SPIID"));
        			saveData.put("createTime", new Date());
					saveData.put("dataSource",  "1");
					saveData.put("createUser",  userid);
					saveData.put("fphm",  info.get("FPHM"));
					saveData.put("deleted",  0);
					saveData.put("SCIID", info.get("SCIID"));
					saveData.put("SCINID", info.get("SCINID"));
					Map<String, Object> newservice = dao.doSave("create",BSPHISEntryNames.SCM_NewService, saveData, false);
		            dao.doSave("update", BSPHISEntryNames.SCM_INCREASEITEMSDETIL, info, false);
				}
		}catch (PersistentDataOperationException e) {
		    logger.error(e.getMessage());
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, e.getMessage());
		}
	}
}
