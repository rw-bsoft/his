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

public class DentistryService extends AbstractActionService implements DAOSupportable {
    private static final Logger logger = LoggerFactory.getLogger(DentistryService.class);
    /**
     * 新建保存拔牙知情同意书病历版
     * @param jsonReq
     * @param jsonRes
     * @param sc
     * @param ctx
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    protected void doSaveExtractionAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        DentistryModel Extraction = new DentistryModel(dao);
        Extraction.saveExtractionAgreement(jsonReq, jsonRes, dao, ctx);
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
    protected void doSaveFixedAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        DentistryModel Fixed = new DentistryModel(dao);
        Fixed.saveFixedAgreement(jsonReq, jsonRes, dao, ctx);
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
    protected void doSaveActivityAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        DentistryModel Activity = new DentistryModel(dao);
        Activity.saveActivityAgreement(jsonReq, jsonRes, dao, ctx);
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
    protected void doSavePeriodontalAgreement(Map<String, Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx) throws ServiceException {
        DentistryModel Periodontal = new DentistryModel(dao);
        Periodontal.savePeriodontalAgreement(jsonReq, jsonRes, dao, ctx);
    }

}
