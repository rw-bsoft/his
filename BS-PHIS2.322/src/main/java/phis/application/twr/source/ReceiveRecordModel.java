package phis.application.twr.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import ctd.util.context.Context;

public class ReceiveRecordModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ReceiveRecordModel.class);

	public ReceiveRecordModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 根据病人empiid和转诊单号取转诊信息 
	 * liulichuang
	 * 2013-11-24
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getRecordHistoryByEmpiidAndDh(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> resData = new HashMap<String, Object>();
		Object temp = body.get("empiid");
		parameters.put("EMPIID", temp == null ? "":temp);
		
		temp = body.get("zhuanzhendh");
		parameters.put("ZHUANZHENDH", temp == null ? "":temp);
		
		temp = body.get("yewulx");
		parameters.put("YEWULX", temp == null ? "":temp);
		
		String sql = "select MZHM as MZHM,STATUS as STATUS,JIUZHENKLX as JIUZHENKLX,JIUZHENKH as JIUZHENKH,YIBAOKLX as YIBAOKLX,YIBAOKXX as YIBAOKXX,YEWULX as YEWULX,BINGRENXM as BINGRENXM,BINGRENXB as BINGRENXB,BINGRENCSRQ as BINGRENCSRQ,BINGRENNL as BINGRENNL,BINGRENSFZH as BINGRENSFZH,BINGRENLXDH as BINGRENLXDH,BINGRENLXDZ as BINGRENLXDZ,BINGRENFYLB as BINGRENFYLB,SHENQINGJGMC as SHENQINGJGMC,SHENQINGJGLXDH as SHENQINGJGLXDH,SHENQINGYSDH as SHENQINGYSDH,ZHUANZHENYY as ZHUANZHENYY,BINQINGMS as BINQINGMS,ZHUANZHENZYSX as ZHUANZHENZYSX,ZHUANRUKSMC as ZHUANRUKSMC,ZHUANZHENDH as ZHUANZHENDH,SHENQINGRQ as SHENQINGRQ,ZHUANZHENRQ as ZHUANZHENRQ,SHENQINGYS as SHENQINGYS from DR_CLINICRECORDLHISTORY where EMPIID =:EMPIID and ZHUANZHENDH =:ZHUANZHENDH and YEWULX =:YEWULX";
		try {
			List<Map<String, Object>> list = dao.doQuery(sql,parameters);
			if(list.size()>0){
				resData = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return resData;
	}
	
	/**
	 * 根据病人empiid和检查申请单号取检查申请信息 
	 * liulichuang
	 * 2013-11-24
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getCheckHistoryByEmpiidAndDh(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> resData = new HashMap<String, Object>();
		Object temp = body.get("empiid");
		parameters.put("EMPIID", temp == null ? "":temp);
		
		temp = body.get("jianchasqdh");
		parameters.put("JIANCHASQDH", temp == null ? "":temp);
		
		String sql = "select EMPIID as EMPIID,MZHM as MZHM,STATUS as STATUS,SONGJIANRQ as SONGJIANRQ,JIUZHENKLX as JIUZHENKLX,JIUZHENKH as JIUZHENKH,SONGJIANYS as SONGJIANYS,SONGJIANKSMC as SONGJIANKSMC,SHOUFEISB as SHOUFEISB,BINGQINGMS as BINGQINGMS,ZHENDUAN as ZHENDUAN,BINGRENTZ as BINGRENTZ,QITAJC as QITAJC,BINGRENZS as BINGRENZS,BINGRENXM as BINGRENXM,BINGRENSFZH as BINGRENSFZH,JIANCHASQDH as JIANCHASQDH,SHENQINGRQ as SHENQINGRQ from DR_CLINICCHECKHISTORY where EMPIID =:EMPIID and JIANCHASQDH =:JIANCHASQDH";
		try {
			List<Map<String, Object>> list = dao.doQuery(sql,parameters);
			if(list.size()>0){
				resData = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return resData;
	}
	
	/**
	 * 根据病人empiid和检查申请单号取设备预约信息 
	 * liulichuang
	 * 2013-11-24
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getEquipmentRecordByEmpiidAndDh(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> resData = new HashMap<String, Object>();
		Object temp = body.get("empiid");
		parameters.put("EMPIID", temp == null ? "":temp);
		
		temp = body.get("yuyuesqdbh");
		parameters.put("YUYUESQDBH", temp == null ? "":temp);
		
		String sql = "select EMPIID as EMPIID,BINGRENMZH as BINGRENMZH,BINGRENXM as BINGRENXM,YUYUERQ as YUYUERQ,JIANCHAYYMC as JIANCHAYYMC,JIANCHAXMLX as JIANCHAXMLX,YINGXIANGFX as YINGXIANGFX,JIANCHABWMC as JIANCHABWMC,JIANCHASBMC as JIANCHASBMC,JIANCHASBDD as JIANCHASBDD,JIANCHAXMMC as JIANCHAXMMC,YUYUESF as YUYUESF,YUYUESQDBH as YUYUESQDBH,YUYUEH as YUYUEH,YIQIYUYUEH as YIQIYUYUEH,YUYUESJ as YUYUESJ from DR_CLINICXXEQUIPMENTHISTORY where EMPIID =:EMPIID and YUYUESQDBH =:YUYUESQDBH";
		try {
			List<Map<String, Object>> list = dao.doQuery(sql,parameters);
			if(list.size()>0){
				resData = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return resData;
	}
	
	/**
	 * 根据病人empiid和检查申请单号取当天挂号信息 
	 * liulichuang
	 * 2013-11-24
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getRegisterHistoryByEmpiidAndDh(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> resData = new HashMap<String, Object>();
		Object temp = body.get("empiid");
		parameters.put("EMPIID", temp == null ? "":temp);
		
		temp = body.get("guahaoid");
		parameters.put("GUAHAOID", temp == null ? "":temp);
		String sql = "select EMPIID as EMPIID,MZHM as MZHM,YUYUERQ as YUYUERQ,SHENQINGYS as SHENQINGYS,ZHUANRUYYMC as ZHUANRUYYMC,ZHUANRUKSMC as ZHUANRUKSMC,YISHENGXM as YISHENGXM,GUAHAOBC as GUAHAOBC,JIUZHENSJ as JIUZHENSJ,YIZHOUPBID as YIZHOUPBID,DANGTIANPBID as DANGTIANPBID,DAISHOUFY as DAISHOUFY,ZHUANZHENDH as ZHUANZHENDH,GUAHAOID as GUAHAOID,GUAHAOXH as GUAHAOXH,BINGRENXM as BINGRENXM,JIUZHENDD as JIUZHENDD from DR_CLINICZZRECORDHISTORY where EMPIID =:EMPIID and GUAHAOID =:GUAHAOID";
		try {
			List<Map<String, Object>> list = dao.doQuery(sql,parameters);
			if(list.size()>0){
				resData = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return resData;
	}
	
	/**
	 * 根据病人empiid和取号密码取预约挂号信息 
	 * liulichuang
	 * 2013-11-24
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getRegisterReqHistoryByEmpiidAndDh(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> resData = new HashMap<String, Object>();
		Object temp = body.get("empiid");
		parameters.put("EMPIID", temp == null ? "":temp);
		
		temp = body.get("quhaomm");
		parameters.put("QUHAOMM", temp == null ? "":temp);
		String sql = "select EMPIID as EMPIID,MZHM as MZHM,YUYUERQ as YUYUERQ,SHENQINGYS as SHENQINGYS,ZHUANRUYYMC as ZHUANRUYYMC,ZHUANRUKSMC as ZHUANRUKSMC,YISHENGXM as YISHENGXM,GUAHAOBC as GUAHAOBC,JIUZHENSJ as JIUZHENSJ,YIZHOUPBID as YIZHOUPBID,DANGTIANPBID as DANGTIANPBID,DAISHOUFY as DAISHOUFY,ZHUANZHENDH as ZHUANZHENDH,GUAHAOID as GUAHAOID,GUAHAOXH as GUAHAOXH,BINGRENXM as BINGRENXM,JIUZHENDD as JIUZHENDD,QUHAOMM as QUHAOMM from DR_CLINICZZRECORDHISTORY where EMPIID =:EMPIID and QUHAOMM =:QUHAOMM";
		try {
			List<Map<String, Object>> list = dao.doQuery(sql,parameters);
			if(list.size()>0){
				resData = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return resData;
	}

}
