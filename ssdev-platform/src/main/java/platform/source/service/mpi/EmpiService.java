package platform.source.service.mpi;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.bsoft.mpi.model.ModelDataOperationException;

import ctd.controller.exception.ControllerException;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import ctd.validator.Validator;

/**
 * MPI操作类
 **/
public class EmpiService {
	public static final String MPI_Address = "phis.application.pix.schemas.MPI_Address";
	public static final String MPI_Card = "phis.application.pix.schemas.MPI_Card";
	public static final String MPI_Certificate = "phis.application.pix.schemas.MPI_Certificate";
	public static final String MPI_ChildBaseInfo = "phis.application.pix.schemas.MPI_ChildBaseInfo";
	public static final String MPI_ChildInfo = "phis.application.pix.schemas.MPI_ChildInfo";
	public static final String MPI_DemographicInfo = "phis.application.pix.schemas.MPI_DemographicInfo";
	public static final String MPI_DemographicInfo_CIC = "phis.application.cic.schemas.MPI_DemographicInfo_CIC";
	public static final String MPI_LocalInfo = "MPI_LocalInfo";
	public static final String MPI_Phone = "phis.application.pix.schemas.MPI_Phone";
	public static final String MPI_Extension = "phis.application.pix.schemas.MPI_Extension";

	@RpcService
	public void execute(Map<String, Object> record, String cmd)
			throws Exception {
		String op = (String) record.get("op");
		if (StringUtils.isEmpty(cmd)) {
			op = "create";
		}
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		try {
			ss.beginTransaction();
			if ("empi".equals(cmd)) {
				saveEmpiRecord(record, op, false);
			} else if ("address".equals(cmd)) {
				saveAddress(record);
			} else if ("phone".equals(cmd)) {
				savePhone(record);
			} else if ("certificate".equals(cmd)) {
				saveCertificate(record, op);
			} else if ("extension".equals(cmd)) {
				saveExtension(record);
			} else if ("card".equals(cmd)) {
				saveCard(record);
			}
			ss.getTransaction().commit();
		} catch (Exception e) {
			ss.getTransaction().rollback();
			throw e;
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}

	}

	/**
	 * 保存个人基本信息
	 * 
	 * @param validate
	 * 
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@RpcService
	public void saveEmpiRecord(Map<String, Object> record, String op,
			boolean validate) throws ValidateException, ServiceException {
		doSave(op, MPI_DemographicInfo, record, validate);
	}

	/**
	 * 新增一条卡信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@RpcService
	public void saveCard(Map<String, Object> record) throws ServiceException,
			ValidateException {
		record.put("status", '0');
		doSave("create", MPI_Card, record, false);
	}

	/**
	 * 保存地址信息
	 * 
	 * @param record
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@RpcService
	public void saveAddress(Map<String, Object> record)
			throws ValidateException, ServiceException {
		try {
			doSave("create", MPI_Address, record, false);
		} catch (ServiceException e) {
			throw new ServiceException("保存地址信息失败。", e);
		}
	}

	/**
	 * 保存电话信息
	 * 
	 * @param record
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void savePhone(Map<String, Object> record) throws ValidateException,
			ServiceException {
		try {
			doSave("create", MPI_Phone, record, false);
		} catch (ServiceException e) {
			throw new ServiceException("保存电话信息失败。", e);
		}
	}

	/**
	 * 保存证件信息
	 * 
	 * @param cerReq
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveCertificate(Map<String, Object> record, String op)
			throws ValidateException, ServiceException {
		try {
			doSave(op, MPI_Certificate, record, false);
		} catch (ServiceException e) {
			throw new ServiceException("保存证件信息失败。", e);
		}
	}

	/**
	 * 保存其他信息
	 * 
	 * @param extReq
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveExtension(Map<String, Object> record)
			throws ValidateException, ServiceException {
		try {
			doSave("create", MPI_Extension, record, false);
		} catch (ServiceException e) {
			throw new ServiceException("保存其他信息失败。", e);
		}
	}

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

}
