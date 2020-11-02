package phis.application.mds.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
/**
 * 生产厂家管理
 * @author caijy
 *
 */
public class MedicinesManufacturerModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MedicinesManufacturerModel.class);
	public MedicinesManufacturerModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-26
	 * @description 产地保存时验证产地名称有没重复
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> validationManufacturerModeName(Map<String, Object> body) throws ModelDataOperationException {
		String cdmc =MedicineUtils.parseString(body.get("cdmc")) ;// 产地名称
		String cdjc =MedicineUtils.parseString(body.get("cdjc"));// 产地简称
		long ypcd =MedicineUtils.parseLong(body.get("ypcd"));// 产地编码
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("cdmc", cdmc);
		map_par.put("cdjc", cdjc);
		StringBuffer hqlWhere = new StringBuffer();
		if (ypcd == 0) {
			hqlWhere.append("  CDQC=:cdmc or CDMC=:cdjc");
		} else {
			hqlWhere.append(" YPCD!=:ypcd and (CDQC=:cdmc or CDMC=:cdjc)");
			map_par.put("ypcd", ypcd);
		}
		try {
			Long l = dao.doCount("YK_CDDZ", hqlWhere.toString(),map_par);
			if(l>0){
				return MedicineUtils.getRetMap("产地名称或简称已存在");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "验证产地名称重复失败", e);
		}
		return MedicineUtils.getRetMap();
	}
}
