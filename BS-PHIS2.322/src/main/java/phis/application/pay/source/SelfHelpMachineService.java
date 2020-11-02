/**
 * @(#)TcmService.java Created on 2018-07-11 上午10:26:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.pay.source;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import java.util.Map;

/**
 * @description 自助机支付接口调用
 * 
 * @author zhaojian</a>
 */
public class SelfHelpMachineService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 自助机挂号退款接口
	 * 
	 * @param ms_ghmx
	 * @param dao
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGhRefund(Map<String, Object> ms_ghmx,
			BaseDAO dao) throws ServiceException {
		SelfHelpMachineModel shmm = new SelfHelpMachineModel(dao);
		try {
			shmm.doGhRefund(ms_ghmx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自助机支付退款出错！",e);
		}
	}

	/**
	 * 自助机门诊结算退款接口
	 *
	 * @param mzxx
	 * @param dao
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRefund(Map<String, Object> mzxx,
						   BaseDAO dao) throws ServiceException {
		SelfHelpMachineModel shmm = new SelfHelpMachineModel(dao);
		try {
			shmm.doRefund(mzxx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自助机支付退款出错！",e);
		}
	}
}
