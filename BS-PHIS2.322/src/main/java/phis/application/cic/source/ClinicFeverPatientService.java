package phis.application.cic.source;

import com.sun.media.jfxmediaimpl.MediaUtils;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import java.util.HashMap;
import java.util.Map;

/**
 * @description 门诊发热病人
 *
 * @author wangyd</a>
 */
public class ClinicFeverPatientService extends AbstractActionService implements
        DAOSupportable {
    /**
     * 查询发热病人
     *
     * @param
     */
    public void doQueryClinicFeverInfo(Map<String, Object> req,
                                       Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        ClinicFeverPatientModule cmm = new ClinicFeverPatientModule(dao);
        try {
            res.putAll(cmm.doQueryClinicFeverInfo(req,res,ctx));
        } catch (ModelDataOperationException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 查询发热病人deleteFeverPatient
     *
     * @param
     */
    public void doSaveClinicFeverInfo(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
        ClinicFeverPatientModule cmm = new ClinicFeverPatientModule(dao);
        Map<String, Object> data = (Map<String, Object>) req.get("body");
        try {
            cmm.doSaveClinicFeverInfo(req, res,dao, ctx);
        } catch (ModelDataOperationException e) {
            res.put(RES_CODE, e.getCode());
            res.put(RES_MESSAGE, e.getMessage());
            throw new ServiceException(e);
        }
    }

    public void doDeleteFeverPatient(Map<String, Object> req,
                                             Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        ClinicFeverPatientModule cmm = new ClinicFeverPatientModule(dao);
        try {
            cmm.doDeleteFeverPatient(body,ctx);
        } catch (ModelDataOperationException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 查询发热病人
     *
     * @param
     */
    public Map<String, Object> doGetBySBXH(Map<String, Object> req,
                                           Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        ClinicFeverPatientModule cmm = new ClinicFeverPatientModule(dao);
        try {
            res.putAll(cmm.doGetBySBXH(req,res,ctx));
        } catch (ModelDataOperationException e) {
            throw new ServiceException(e);
        }
        return  res;
    }
}
