/**
 * @(#)EmpiInterfaceImpi.java Created on 2012-7-2 下午02:05:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pix.source;

import java.util.Date;
import java.util.List;
import java.util.Map;

import phis.source.utils.BSPHISUtil;

import com.bsoft.mpi.server.MPIServiceException;
import com.bsoft.mpi.server.rpc.IMPIProvider;
import com.bsoft.mpi.server.rpc.IMPIWriter;

import ctd.service.core.ServiceException;
import ctd.spring.AppDomainContext;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class EmpiInterfaceImpi implements EmpiInterface {
	IMPIProvider impiProvider;
	IMPIWriter impiWriter;

	public EmpiInterfaceImpi(Context ctx) {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
//		impiProvider = (IMPIProvider) wac.getBean("mpi.mpiProvider");
//		impiWriter = (IMPIWriter) wac.getBean("mpi.mpiWriter");
		impiProvider = (IMPIProvider) AppDomainContext.getBean("mpi.mpiProvider");
		impiWriter = (IMPIWriter) AppDomainContext.getBean("mpi.mpiWriter");
	}

	public Map<String, Object> submitPerson(Map<String, Object> info)
			throws ServiceException {
		try {
			return impiWriter.submitMPI(info);
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public Map<String, Object> updatePerson(Map<String, Object> body)
			throws ServiceException, ServiceException {
		try {
			return impiWriter.updateMPI(body);
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public List<Map<String, Object>> advancedSearch(Map<String, Object> body)
			throws ServiceException, ServiceException {
		try {
			List<Map<String, Object>> list = impiProvider.getMPIDetail(body);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					Date birthday = (Date) map.get("birthday");
					map.put("age",
							BSPHISUtil.getPersonAge(birthday, null).get("ages"));
					if(map.containsKey("contactNo")){
						map.put("mobileNumber",map.get("contactNo"));
					}
					list.set(i, map);
				}
			}
			return list;
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public Map<String, Object> advancedSearch(String empiId)
			throws ServiceException, ServiceException {
		try {
			Map<String, Object> map = impiProvider.getMPIDetail(empiId);
			return map;
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public List<String> getEmpiId(Map<String, Object> body)
			throws ServiceException, ServiceException {
		// try {
		// return impiProvider.getMPIID(body);
		// } catch (MPIServiceException e) {
		// throw new ServiceException(e);
		// }
		return null;
	}

	public void registerCard(List<Map<String, Object>> cards)
			throws ServiceException {
		try {
			impiWriter.registerCards(cards);
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public void lockCard(String cardType, String cardId)
			throws ServiceException {
		try {
			impiWriter.lockCard(cardType, cardId);
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public void unlockCard(String cardType, String cardId)
			throws ServiceException {
		try {
			impiWriter.unlockCard(cardType, cardId);
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public void writeOffCard(String cardType, String cardId)
			throws ServiceException {
		try {
			impiWriter.writeOffCard(cardType, cardId);
		} catch (MPIServiceException e) {
			throw new ServiceException(e);
		}
	}

	public IMPIProvider getImpiProvider() {
		return impiProvider;
	}

	public void setImpiProvider(IMPIProvider impiProvider) {
		this.impiProvider = impiProvider;
	}

	public IMPIWriter getImpiWriter() {
		return impiWriter;
	}

	public void setImpiWriter(IMPIWriter impiWriter) {
		this.impiWriter = impiWriter;
	}
}
