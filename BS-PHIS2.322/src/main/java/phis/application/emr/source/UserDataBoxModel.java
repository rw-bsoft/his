/**
 * @(#)UserDataBoxModel.java Created on 2013-5-13 下午2:34:03
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.emr.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.context.Context;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.bean.Param;
import phis.source.service.ServiceCode;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class UserDataBoxModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(UserDataBoxModel.class);

	public UserDataBoxModel(BaseDAO dao) {
		this.dao = dao;
	}

	public List<Map<String, Object>> getTemporaryData(List<Object> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> body = null;
		try {
			body = dao.doList(cnd, "XMBH", BSPHISEntryNames.EMR_BLZD);
		} catch (PersistentDataOperationException e) {
			logger.error("fail to get Temporary Data.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询特殊字符失败.");
		}
		return body;
	}

	public Map<String, Object> listUserDataByCnd(List<Object> cnd,
			String queryCndsType, String sortInfo, int pageSize, int pageNo)
			throws ModelDataOperationException {
		Map<String, Object> body = null;
		List<Map<String, Object>>  result=new ArrayList<Map<String,Object>>();
		try {
			body=dao.doList(cnd, sortInfo, BSPHISEntryNames.EMR_BQYZ_SJH, pageNo, pageSize, queryCndsType);
			List<Map<String, Object>>  list= (List<Map<String, Object>>) body
					.get("body");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map=list.get(i);
				String XMLX=map.get("XMLX").toString();
				if(!XMLX.equals("4")){
					String YPXH=map.get("YPXH").toString();
					String jldw=getJLDWByYPXH(YPXH);
					map.put("JLDW", jldw);
				}
				result.add(map);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("fail to list UserData By Cnd.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医嘱失败.");
		}
		return body;
	}

	private String getJLDWByYPXH(String yPXH) throws ModelDataOperationException {
		String jldw="";
		try {
			Map<String, Object> body=dao.doLoad(BSPHISEntryNames.YK_TYPK, yPXH);
			if(body!=null){
				jldw=(String) body.get("JLDW");
			}else{
				return null;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医嘱失败.");
		}
		return jldw;
	}
	
	public List<Map<String, Object>> doGetFzjcList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ModelDataOperationException {
		String brid=String.valueOf(req.get("brid"));
		List<Map<String,Object>> cardList;
		String PATIENTID = "";
		try {
			cardList=dao.doSqlQuery("select cardno as cardno from mpi_card a,ms_brda b where a.empiid=b.empiid and b.brid=:brid", Param.instance().put("brid", brid));
			if(cardList.isEmpty()||cardList==null){
				throw new ModelDataOperationException("查询病人卡号失败!");
			}
			for (int i = 0; i < cardList.size(); i++) {
				if(i==cardList.size()-1){
					PATIENTID+="'"+String.valueOf(cardList.get(i).get("CARDNO"))+"'";
				}else{
					PATIENTID+="'"+String.valueOf(cardList.get(i).get("CARDNO"))+"',";
				}
			}
			System.out.println(PATIENTID);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		//查检验数据库，取检查项目
		//TODO
		return new ArrayList<Map<String,Object>>();
	}
}
