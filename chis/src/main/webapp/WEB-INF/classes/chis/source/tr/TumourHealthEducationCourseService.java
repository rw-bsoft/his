/**
 * @(#)TumourHealthEducationCourseService.java Created on 2014-7-31 上午10:33:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHealthEducationCourseService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourHealthEducationCourseService.class);

	/**
	 * 
	 * @Description:记录课程数据服务
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-7-31 上午10:36:24
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTHECourse(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourHealthEducationCourseModel thecModel = new TumourHealthEducationCourseModel(
				dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = thecModel.saveTHECourse(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save record of PHQ_HealthEducationCourse failure.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:保存听课人员登记
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-7-31 下午4:19:03
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveListener(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String courseId = (String) reqBodyMap.get("courseId");
		TumourHealthEducationCourseModel thecModel = new TumourHealthEducationCourseModel(
				dao);
		boolean isExist = false;
		try {
			isExist = thecModel.isExistListener(empiId, courseId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Judge whether exist the listener in the course failure.",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> rsMap = null;
		if (!isExist) {
			try {
				rsMap = thecModel.saveListtener("create", reqBodyMap, true);
			} catch (ModelDataOperationException e) {
				logger.error("Save record of PHQ_AttendPersonnel failure.", e);
				throw new ServiceException(e);
			}
			// 更新参加人数
			try {
				thecModel.updateNumberOfParticipants(courseId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Update numberOfParticipants of PHQ_HealthEducationCourse failure.",
						e);
				throw new ServiceException(e);
			}
		}
		res.put("rsMap", rsMap);
	}

	/**
	 * 
	 * @Description:删除参加听课人员, 更新课程参加人数
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-1-14 下午3:07:03
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveAttendPersonnel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String pkey = (String) req.get("pkey");
		String courseId = (String) reqBodyMap.get("courseId");
		TumourHealthEducationCourseModel thecModel = new TumourHealthEducationCourseModel(
				dao);
		try {
			thecModel.removeAttendPersonnel(pkey);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Delete attend personnel record of Tumour Health Education Course failure.",
					e);
			throw new ServiceException(e);
		}
		try {
			thecModel.updateNumberOfParticipants(courseId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Update numberOfParticipants of PHQ_HealthEducationCourse failure.",
					e);
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @Description:刷卡，增加，定位听课人员
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-3-11 上午11:07:33
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSwipingCardHandle(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String cardNo = (String) reqBodyMap.get("cardNo");
		String courseId = (String) reqBodyMap.get("courseId");
		if(S.isNotEmpty(cardNo)){
			EmpiModel empiModel = new EmpiModel(dao);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				list = empiModel.getEmpiInfoByCardNo(cardNo);
			} catch (ModelDataOperationException e) {
				logger.error("Get Proson base info by cardNo failure.", e);
				throw new ServiceException(e);
			}
			Map<String, Object> resBodyMap = new HashMap<String, Object>();
			boolean hasMany = false;
			boolean havePerson = false;
			if(list.size() == 1){
				havePerson = true;
				Map<String, Object> eMap = list.get(0);
				String empiId = (String) eMap.get("empiId");
				TumourHealthEducationCourseModel thecModel = new TumourHealthEducationCourseModel(dao);
				boolean existListener = false; 
				try {
					existListener = thecModel.isExistListener(empiId, courseId);
				} catch (ModelDataOperationException e) {
					logger.error("Judge whether exist the listener in the course failure.",e);
					throw new ServiceException(e);
				}
				resBodyMap.put("existListener", existListener);
				resBodyMap.put("empiId", empiId);
				if(!existListener){
					//不存在则增加
					Map<String, Object> apMap = new HashMap<String, Object>();
					apMap.put("empiId", empiId);
					apMap.put("courseId", courseId);
					try {
						dao.beginTransaction();
						thecModel.saveListtener("create", apMap, true);
						// 更新参加人数
						try {
							thecModel.updateNumberOfParticipants(courseId);
						} catch (ModelDataOperationException e) {
							logger.error(
									"Update numberOfParticipants of PHQ_HealthEducationCourse failure.",
									e);
							throw new ServiceException(e);
						}
						dao.commitTransaction();
					} catch (ModelDataOperationException e) {
						logger.error("Add PHQ_AttendPersonnel failure.", e);
						dao.rollbackTransaction();
						throw new ServiceException(e);
					}finally{
						dao.closeSession();
					}
				}
			}
			if(list.size() > 1){
				hasMany = true;
				havePerson = true;
				resBodyMap.put("pList", list);
			}
			resBodyMap.put("havePerson", havePerson);
			resBodyMap.put("hasMany", hasMany);
			res.put("body", resBodyMap);
		}
	}
}
