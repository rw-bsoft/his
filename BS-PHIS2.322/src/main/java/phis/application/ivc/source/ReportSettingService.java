package phis.application.ivc.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
/**
 * 
 * @description
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ReportSettingService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ReportSettingService.class);

	public void doBBMCQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ReportSettingModel advice = new ReportSettingModel(dao);
		try {
			advice.doBBMCQuery(req, res, ctx);
		} catch (Exception e) {
			throw new RuntimeException("报表载入失败", e);
		}

	}
	
	public void doXMGBQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ReportSettingModel advice = new ReportSettingModel(dao);
		try {
			advice.doXMGBQuery(req, res, ctx);
		} catch (Exception e) {
			throw new RuntimeException("报表详情载入失败", e);
		}

	}
	
	public void doXMGBSave(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ReportSettingModel advice = new ReportSettingModel(dao);
		try {
			advice.doXMGBSave(req, res, ctx);
		} catch (Exception e) {
			throw new RuntimeException("保存失败,请检查输入项", e);
		}

	}

}
