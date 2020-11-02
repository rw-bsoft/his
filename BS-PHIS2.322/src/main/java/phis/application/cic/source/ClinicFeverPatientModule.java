package phis.application.cic.source;

import com.bsoft.mpi.util.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpRunner;
import ctd.validator.ValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClinicFeverPatientModule {
    protected BaseDAO dao;
    protected Logger logger = LoggerFactory.getLogger(ClinicFeverPatientModule.class);

    public ClinicFeverPatientModule(BaseDAO dao) {
        this.dao = dao;
    }

    /**
     * 查询发热病人
     *
     * @param
     * @throws ModelDataOperationException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> doQueryClinicFeverInfo(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ModelDataOperationException {

        UserRoleToken user = UserRoleToken.getCurrent();
        String JGID = user.getManageUnit().getId();// 用户的机构ID
        int pageSize = 25;
        if (req.containsKey("pageSize")) {
            pageSize = (Integer) req.get("pageSize");
        }
        int first = 0;
        if (req.containsKey("pageNo")) {
            first = (Integer) req.get("pageNo") - 1;
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        try {
            parameters.put("JGID", JGID);

            // 返会列数的查询语句
            StringBuffer Sql_count = new StringBuffer(
                    "SELECT COUNT(*) as NUM FROM ");
            Sql_count.append("MS_MZFR t ");
            Sql_count.append("WHERE (t.JGID =:JGID) ");

            List<Map<String, Object>> coun = dao.doSqlQuery(
                    Sql_count.toString(), parameters);
            int total = Integer.parseInt(coun.get(0).get("NUM") + "");
            parameters.put("first", first * pageSize);
            parameters.put("max", pageSize);

            StringBuffer sql=new StringBuffer("select a.SBXH AS SBXH,a.BRID AS BRID,a.EMPIID AS EMPIID,a.SJLY AS SJLY,a.LYBS AS LYBS,a.FZBZ AS FZBZ,a.T AS T,a.YZNFR AS YZNFR,a.FRRQ AS FRRQ, ")
                    .append("a.SSY AS SSY,a.SZY AS SZY,a.R AS R,a.P AS P,a.PAO2 AS PAO2,a.FL AS FL,a.KS AS KS,a.BS AS BS,a.LT AS LT,a.YT AS YT,a.FX AS FX,a.QTZZ AS QTZZ,a.JCJB AS JCJB,a.QTJB AS QTJB, ")
                    .append("a.HZQX AS HZQX,a.QTQX AS QTQX,a.ZDXH AS ZDXH,a.XCGJG AS XCGJG,a.CPR AS CPR,a.YSZ AS YSZ,a.SFCT AS SFCT,a.RTPCR AS RTPCR,a.LRRY AS LRRY,a.JGID AS JGID,a.LRSJ AS LRSJ, ")
                    .append("a.XGR AS XGR,a.XGSJ AS XGSJ,b.BRXM as BRXM,b.BRXB as BRXB,b.CSNY as CSNY  from MS_MZFR a,MS_BRDA b where a.BRID=b.BRID and a.JGID=:JGID");
            if (req.containsKey("cnd") && !"null".equals(req.get("cnd")+"")) {
                List<Object> cnd = (List<Object>) req.get("cnd");
                String where = ExpRunner.toString(cnd, ctx);
                sql.append(" and ").append(where);
            }
            List<Map<String, Object>> data=dao.doSqlQuery(sql.toString(),parameters);
            data = SchemaUtil.setDictionaryMassageForList(data,
                    "phis.application.cic.schemas.frmz.MS_MZFR_L");
            // 返回list的查询语句
            res.put("pageSize", pageSize);
            res.put("pageNo", first);
            res.put("totalCount", total);
            res.put("body", data);
        } catch (Exception e) {
            throw new ModelDataOperationException("发热病人查询失败", e);
        }
        return res;

    }

    public void doSaveClinicFeverInfo(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx)throws ModelDataOperationException  {
        UserRoleToken user = UserRoleToken.getCurrent();
        String manageUnit = user.getManageUnit().getId();

        Map<String, Object> body = (Map<String, Object>) req.get("body");
        String op = MedicineUtils.parseString(req.get("op"));
        Long openerType = MedicineUtils.parseLong(body.get("openerType"));
        Long jzxh = MedicineUtils.parseLong(body.get("jzxh"));
        Long brid = MedicineUtils.parseLong(body.get("brid"));
        String empiId = MedicineUtils.parseString(body.get("empiId"));

        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> northData = (Map<String, Object>)body.get("northData");
        Map<String, Object> southData = (Map<String, Object>)body.get("southData");
        if(northData != null){
            northData.remove("BRXM");
            northData.remove("BRXB");
            northData.remove("CSNY");
            northData.remove("SFZH");
            northData.remove("BRDZ");
            northData.remove("BRDH");
            data.putAll(northData);
        }
        if(southData != null){
            data.putAll(southData);
        }
        data.put("LYBS", jzxh);   //直接新建该数值为0，门诊为jzxh，住院为zyh
        data.put("BRID", brid);
        data.put("EMPIID", empiId);
        data.put("XGR", user.getUserId());
        data.put("XGSJ", new Date());
        data.put("JGID",manageUnit);
        try {
            if ("create".equals(op)) {
                dao.doSave("create", BSPHISEntryNames.MS_MZFR, data , false);
            } else {
                dao.doSave("update", BSPHISEntryNames.MS_MZFR, data, false);
            }

            if(openerType == 2){//门诊调入
                Map<String,Object> parameters = new HashMap<String,Object>();
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("BRID",MedicineUtils.parseLong(data.get("BRID")));
                parameters.put("T",MedicineUtils.parseDouble(data.get("T")));
                parameters.put("P",MedicineUtils.parseInt(data.get("P")));
                parameters.put("R",MedicineUtils.parseInt(data.get("R")));
                parameters.put("SSY",MedicineUtils.parseInt(data.get("SSY")));
                parameters.put("SZY",MedicineUtils.parseInt(data.get("SZY")));
                parameters.put("JZXH",jzxh);
                StringBuffer hql = new StringBuffer();
                hql.append("select jzxh from MS_BCJL where JZXH = (select JZXH from (select JZXH from YS_MZ_JZLS where BRBH = :BRID order by JZXH desc) where ROWNUM = 1)");
                List<Map<String,Object>> count = dao.doSqlQuery(hql.toString(),map);
                if(count != null && count.size()>0){
                    dao.doSqlUpdate("update MS_BCJL set T=:T,P=:P,R=:R,SSY=:SSY,SZY=:SZY where JZXH=:JZXH",parameters);
                }else{
                    parameters.put("BRID",MedicineUtils.parseLong(data.get("BRID")));
                    parameters.put("JZYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
                    parameters.put("JZKS", user.getProperty("biz_departmentId", Long.class));
                    parameters.put("JGID", manageUnit);
                    parameters.put("BLLX", 1);
                    parameters.put("DYBZ", 0);
                    dao.doSave("create",BSPHISEntryNames.MS_BCJL, parameters, false);
                }
            }
        } catch (PersistentDataOperationException e) {
            MedicineUtils.throwsException(logger, "发热门诊保存失败", e);
        } catch (ValidateException e) {
            MedicineUtils.throwsException(logger, "发热门诊保存失败", e);
        }

    }

    public  void doDeleteFeverPatient(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("SBXH",MedicineUtils.parseLong(body.get("SBXH")));
        try{
            String sxtx_sql="delete from  MS_MZFR where SBXH=:SBXH ";
            dao.doUpdate(sxtx_sql, par);
        } catch (PersistentDataOperationException e) {
            throw new ModelDataOperationException("删除门诊发热病人数据失败!", e);
        }
    }

    public Map<? extends String,?> doGetBySBXH(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ModelDataOperationException{
        Map<String, Object> params = new HashMap<String, Object>();
        UserRoleToken user = UserRoleToken.getCurrent();
        String manageUnit = user.getManageUnit().getId();
        try {
            StringBuffer hql = new StringBuffer();
            hql.append("select a.SBXH AS SBXH,a.BRID AS BRID,a.EMPIID AS EMPIID,a.SJLY AS SJLY,a.LYBS AS LYBS,");
            hql.append("a.FZBZ AS FZBZ,a.T AS T,a.YZNFR AS YZNFR,a.FRRQ AS FRRQ,a.SSY AS SSY,a.SZY AS SZY,a.R AS R,");
            hql.append("a.P AS P,a.PAO2 AS PAO2,a.FL AS FL,a.KS AS KS,a.BS AS BS,a.LT AS LT,a.YT AS YT,a.FX AS FX,");
            hql.append("a.QTZZ AS QTZZ,a.JCJB AS JCJB,a.QTJB AS QTJB,a.HZQX AS HZQX,a.QTQX AS QTQX,a.ZDXH AS ZDXH,");
            hql.append("a.XCGJG AS XCGJG,a.CPR AS CPR,a.YSZ AS YSZ,a.SFCT AS SFCT,a.RTPCR AS RTPCR,a.LRRY AS LRRY,");
            hql.append("a.JGID AS JGID,a.LRSJ AS LRSJ,a.XGR AS XGR,a.XGSJ AS XGSJ,b.BRXM as BRXM,b.BRXB as BRXB,b.CSNY as CSNY from MS_MZFR a,MS_BRDA b where a.BRID=b.BRID and a.SBXH=:SBXH and a.JGID=:JGID");

            params.put("SBXH", MedicineUtils.parseLong(req.get("SBXH")));
            params.put("JGID", manageUnit);
            Map<String, Object> map = dao.doSqlLoad( hql.toString(), params);
            map = SchemaUtil.setDictionaryMassageForForm(map,
                    "phis.application.cic.schemas.frmz.MS_MZFR_L");
            res.put("body",map);
        } catch (PersistentDataOperationException e) {
            throw new ModelDataOperationException(" 查询门诊发热病人数据失败!", e);
        }
        return res;
    }
}
