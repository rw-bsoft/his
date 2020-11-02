/**
 * author by renwei 2020-10-21
 */
package phis.application.dpc.source;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.application.cic.source.ClinicComboNameModel;
import phis.application.pix.source.EmpiModel;
import phis.application.pix.source.EmpiUtil;
import phis.application.pix.source.SmzModel;
import phis.application.pub.source.PublicModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;


public class DentistryModel {
    protected BaseDAO dao;
    protected Logger logger = LoggerFactory.getLogger(DentistryModel.class);
    public DentistryModel(BaseDAO dao) {
        this.dao = dao;
    }
    /**
     * 新建保存拔牙知情同意书病历版
     * @param jsonReq
     * @param jsonRes
     * @param sc
     * @param ctx
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    protected void saveExtractionAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        String op = (String) jsonReq.get("op");
        Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", body.get("id"));
        params.put("empiId", (String) body.get("empiId"));
        params.put("doctorId", (String) body.get("doctorId"));
        params.put("personName",(String) body.get("personName"));
        params.put("age", body.get("age"));
        params.put("sex",(String) body.get("sex"));
        params.put("diagnosis",(String) body.get("diagnosis"));
        params.put("operationDate",(String) body.get("operationDate"));
        params.put("proposed",(String) body.get("proposed"));
        params.put("createUser",(String) body.get("createUser"));
        params.put("createDate",body.get("createDate"));
        params.put("createUnit",(String) body.get("createUnit"));
        params.put("lastModifyUser",(String) body.get("lastModifyUser"));
        params.put("lastModifyUnit",(String) body.get("lastModifyUnit"));
        params.put("lastModifyDate",body.get("lastModifyDate"));

        try {
            if(op.equals("create")){
                dao.doSave("create", BSPHISEntryNames.DPC_ExtractionAgreement, params, false);
            }else if(op.equals("update")){
                if(params.containsKey("createUser")){
                    params.remove("createUser");
                }
                if(params.containsKey("createUnit")){
                    params.remove("createUnit");
                }
                if(params.containsKey("createDate")){
                    params.remove("createDate");
                }
                dao.doSave("update", BSPHISEntryNames.DPC_ExtractionAgreement, params, false);
            }
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 新建保存固定修复知情同意书
     * @param jsonReq
     * @param jsonRes
     * @param sc
     * @param ctx
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    protected void saveFixedAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        String op = (String) jsonReq.get("op");
        Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", body.get("id"));
        params.put("empiId", (String) body.get("empiId"));
        params.put("doctorId", (String) body.get("doctorId"));
        params.put("personName",(String) body.get("personName"));
        params.put("age", body.get("age"));
        params.put("sex",(String) body.get("sex"));
        params.put("diagnosis",(String) body.get("diagnosis"));
        params.put("operationDate",(String) body.get("operationDate"));
        params.put("proposed",(String) body.get("proposed"));
        params.put("createUser",(String) body.get("createUser"));
        params.put("createDate",body.get("createDate"));
        params.put("createUnit",(String) body.get("createUnit"));
        params.put("lastModifyUser",(String) body.get("lastModifyUser"));
        params.put("lastModifyUnit",(String) body.get("lastModifyUnit"));
        params.put("lastModifyDate",body.get("lastModifyDate"));

        try {
            if(op.equals("create")){
                dao.doSave("create", BSPHISEntryNames.DPC_FixedAgreement, params, false);
            }else if(op.equals("update")){
                if(params.containsKey("createUser")){
                    params.remove("createUser");
                }
                if(params.containsKey("createUnit")){
                    params.remove("createUnit");
                }
                if(params.containsKey("createDate")){
                    params.remove("createDate");
                }
                dao.doSave("update", BSPHISEntryNames.DPC_FixedAgreement, params, false);
            }
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 新建保存活动修复知情同意书
     * @param jsonReq
     * @param jsonRes
     * @param sc
     * @param ctx
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    protected void saveActivityAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        String op = (String) jsonReq.get("op");
        Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", body.get("id"));
        params.put("empiId", (String) body.get("empiId"));
        params.put("doctorId", (String) body.get("doctorId"));
        params.put("personName",(String) body.get("personName"));
        params.put("age", body.get("age"));
        params.put("sex",(String) body.get("sex"));
        params.put("diagnosis",(String) body.get("diagnosis"));
        params.put("operationDate",(String) body.get("operationDate"));
        params.put("proposed",(String) body.get("proposed"));
        params.put("createUser",(String) body.get("createUser"));
        params.put("createDate",body.get("createDate"));
        params.put("createUnit",(String) body.get("createUnit"));
        params.put("lastModifyUser",(String) body.get("lastModifyUser"));
        params.put("lastModifyUnit",(String) body.get("lastModifyUnit"));
        params.put("lastModifyDate",body.get("lastModifyDate"));

        try {
            if(op.equals("create")){
                dao.doSave("create", BSPHISEntryNames.DPC_ActivityAgreement, params, false);
            }else if(op.equals("update")){
                if(params.containsKey("createUser")){
                    params.remove("createUser");
                }
                if(params.containsKey("createUnit")){
                    params.remove("createUnit");
                }
                if(params.containsKey("createDate")){
                    params.remove("createDate");
                }
                dao.doSave("update", BSPHISEntryNames.DPC_ActivityAgreement, params, false);
            }
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 新建保存牙周治疗知情同意书病历版
     * @param jsonReq
     * @param jsonRes
     * @param sc
     * @param ctx
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    protected void savePeriodontalAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        String op = (String) jsonReq.get("op");
        Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", body.get("id"));
        params.put("empiId", (String) body.get("empiId"));
        params.put("doctorId", (String) body.get("doctorId"));
        params.put("personName",(String) body.get("personName"));
        params.put("age", body.get("age"));
        params.put("sex",(String) body.get("sex"));
        params.put("diagnosis",(String) body.get("diagnosis"));
        params.put("operationDate",(String) body.get("operationDate"));
        params.put("proposed",(String) body.get("proposed"));
        params.put("createUser",(String) body.get("createUser"));
        params.put("createDate",body.get("createDate"));
        params.put("createUnit",(String) body.get("createUnit"));
        params.put("lastModifyUser",(String) body.get("lastModifyUser"));
        params.put("lastModifyUnit",(String) body.get("lastModifyUnit"));
        params.put("lastModifyDate",body.get("lastModifyDate"));

        try {
            if(op.equals("create")){
                dao.doSave("create", BSPHISEntryNames.DPC_PeriodontalAgreement, params, false);
            }else if(op.equals("update")){
                if(params.containsKey("createUser")){
                    params.remove("createUser");
                }
                if(params.containsKey("createUnit")){
                    params.remove("createUnit");
                }
                if(params.containsKey("createDate")){
                    params.remove("createDate");
                }
                dao.doSave("update", BSPHISEntryNames.DPC_PeriodontalAgreement, params, false);
            }
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
