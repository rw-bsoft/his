package phis.application.lis.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.lis.source.rpc.PHISServiceException;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.service.KeyCreator;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description lis医技单相关操作Model
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class PHISLisBillOperationModel extends PHISBaseModel {

	protected Logger logger = LoggerFactory
			.getLogger(PHISLisBillOperationModel.class);
	protected BaseDAO dao;

	public PHISLisBillOperationModel(Context ctx) {
		super(ctx);
		dao = new BaseDAO(ctx);
	}

	/**
	 * 门诊lis医技单提交
	 * 
	 * @param yjxxs
	 * @param sqdhId
	 *            申请单号ID
	 * @param djly
	 *            单据来源,1医生开单 2药房划价 3药房退药 4医技划价 5体检开单 6收费划价 7门诊退费 8检验开单
	 * @return
	 * @throws ModelOperationException
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> clinicLisRecordSubmit(List<Object> yjxxs,
			long sqdhId, String djly, Context ctx)
			throws ModelOperationException, ValidateException,
			PersistentDataOperationException, ModelDataOperationException {
//		System.out.println("接收LIS数据：" + yjxxs);
		List<Map<String, Object>> reslist = new ArrayList<Map<String, Object>>();

		if (yjxxs.size() == 0 || djly == null) {
			throw new ModelOperationException("Args Illegal!");
		}
		//System.out.println("==============接收LIS数据yjxxs："+yjxxs+"========================");
		//System.out.println("==============接收LIS数据医技检验项目条数："+yjxxs.size()+"条========================");
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		String njjbmsg="";
		try {
			ss.beginTransaction();
			/** 遍历LIS医技单List */
			for (Iterator it = yjxxs.iterator(); it.hasNext();) {
				Map<String, Object> yjxx = (Map<String, Object>) it.next();
				Map<String, Object> res01;
				/** MS_YJ01 */
				Map<String, Object> map_ = (Map<String, Object>) yjxx
						.get("YJ01");
				Map<String, Object> yj01Map = new HashMap<String, Object>();
				/*********modify by zhaojian 2017-05-11 解决主键冲突问题：改为从序列中获取*******************/
				/*long key = Long.parseLong(getTableID(BSPHISEntryNames.MS_YJ01_CIC,
						"YJXH",BSPHISEntryNames.MS_YJ01_TABLE));*/
				Schema sc = SchemaController.instance().get(BSPHISEntryNames.MS_YJ01_CIC);
				SchemaItem item_ms_yj01 = sc.getKeyItem();
				long key = MedicineUtils.parseLong(KeyCreator.create("MS_YJ01", item_ms_yj01.getKeyRules(), item_ms_yj01.getId()));

				String br_jgid = map_.get("JGDM") + "";
				//2017-08-09 实现沿江卫生服务中心下属的复兴社区可以开检验单，模拟为沿江医院的请求。
				//开申请单时机构编码修改为320111002，科室代码修改为1296（复兴卫生室）
				if(map_.get("JGDM").toString().equals("320111002") && map_.get("SECTION").toString().equals("1296")){
					yj01Map.put("KSDM", 1357);
					yj01Map.put("JGID", "320111002004");
					br_jgid="320111002004";
				}
				//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
				//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
				else if(map_.get("JGDM").toString().equals("320124005") && map_.get("SECTION").toString().equals("1107")){
					yj01Map.put("KSDM", 1069);
					yj01Map.put("JGID", "320124005016");
					br_jgid="320124005016";
				}
				//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
				//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
				else if(map_.get("JGDM").toString().equals("320124004") && map_.get("SECTION").toString().equals("1109")){
					yj01Map.put("KSDM", 1421);
					yj01Map.put("JGID", "320124004018");
					br_jgid="320124004018";
				}
				else{
					yj01Map.put("KSDM", map_.get("SECTION"));
					yj01Map.put("JGID", map_.get("JGDM"));
				}
				yj01Map.put("YJXH", key);
				yj01Map.put("YSDM", map_.get("REQUESTER"));
				yj01Map.put("KDRQ", new Date());
				yj01Map.put("ZFPB", 0);
				yj01Map.put("ZXPB", 0);
				yj01Map.put("CFBZ", 0);
				yj01Map.put("DJZT", 0);
				yj01Map.put("JZXH", map_.get("JZXH"));
				yj01Map.put("BRID", map_.get("BRID"));
				yj01Map.put("BRXM", map_.get("PATIENTNAME"));
				yj01Map.put("DJLY", djly);
				yj01Map.put("SQID", map_.get("DOCTREQUESTNO"));
				String sqdmc = map_.get("EXAMINAIM") + "";
				if (sqdmc.length() > 13) {
					sqdmc = sqdmc.substring(0, 13);
				}
				yj01Map.put("SQDMC", sqdmc);
				yj01Map.put("ZXKS", map_.get("ZXKS"));// 执行科室
				yj01Map.put("MZXH", "");// 门诊序号
				String hql01 = "insert into ms_yj01(YJXH,KSDM,YSDM,KDRQ,ZFPB,ZXPB,CFBZ,DJZT,JGID,JZXH,BRID,BRXM,DJLY,SQID,SQDMC,ZXKS,MZXH) values "
						+ "(:YJXH,:KSDM,:YSDM,:KDRQ,:ZFPB,:ZXPB,:CFBZ,:DJZT,:JGID,:JZXH,:BRID,:BRXM,:DJLY,:SQID,:SQDMC,:ZXKS,:MZXH)";
				dao.doSqlUpdate(hql01, yj01Map);

				/** 遍历LIS检验单明细项目 MS_YJ02 */
				List<Map<String, Object>> yj02s = (List<Map<String, Object>>) yjxx
						.get("YJ02S");

				//System.out.println("==============接收LIS数据医技检验明细项目："+yj02s+"========================");
				for (Iterator<Map<String, Object>> it2 = yj02s.iterator(); it2.hasNext();) {
					Map<String, Object> lisyj02 = (HashMap<String, Object>) it2.next();

					// 查询医疗收费信息
					String hqlylsf = "select a.XMLX as XMLX,b.FYDJ as FYDJ, a.FYGB as FYGB,"
							+ "a.FYDW as FYDW ,a.FYMC as FYMC from GY_YLSF a,GY_YLMX b where "
							+ "a.FYXH = b.FYXH and b.JGID = :JGID and a.FYXH =:FYXH";
					Map<String, Object> mapylsf = new HashMap<String, Object>();
					
					/*String mapylsf_jgid = map_.get("JGDM") + "";
					//2017-08-09 实现沿江卫生服务中心下属的复兴社区可以开检验单，模拟为沿江医院的请求。
					//开申请单时机构编码修改为320111002，科室代码修改为1296（复兴卫生室）
					if(mapylsf_jgid.equals("320111002")){
						mapylsf_jgid = "320111002004";
					}
					//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
					//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
					else if(mapylsf_jgid.equals("320124005")){
						mapylsf_jgid = "320124005016";
					}
					//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
					//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
					else if(mapylsf_jgid.equals("320124004")){
						mapylsf_jgid = "320124004018";
					}*/
					mapylsf.put("JGID", br_jgid);
					mapylsf.put("FYXH", lisyj02.get("YLXH"));
					List<Map<String, Object>> list = dao.doSqlQuery(hqlylsf,
							mapylsf);
					if (list.size() == 0) {
						throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR, "未找到对应的收费项目."+hqlylsf.replaceAll(":JGID", br_jgid).replaceAll(":FYXH", lisyj02.get("YLXH").toString()));
					}
					Map<String, Object> ylsf = list.get(0);

					// 查询自负比例信息
					Map<String, Object> zfbl = new HashMap<String, Object>();
					zfbl.put("TYPE", "0");
					zfbl.put("BRXZ", map_.get("BRXZ"));
					zfbl.put("FYXH", lisyj02.get("YLXH"));
					zfbl.put("FYGB", ylsf.get("FYGB"));
					Map<String, Object> zfblmap = BSPHISUtil.getzfbl(zfbl, ctx,dao);

					// 保存MS_YJ02
					Map<String, Object> yj02 = new HashMap<String, Object>();
					
					/*********modify by zhaojian 2017-05-11 解决主键冲突问题：改为从序列中获取*******************/
					/*long key02 = Long.parseLong(getTableID(
							BSPHISEntryNames.MS_YJ02_CIC, "SBXH", BSPHISEntryNames.MS_YJ02_TABLE));*/
					Schema sc02 = SchemaController.instance().get(BSPHISEntryNames.MS_YJ02_CIC);
					SchemaItem item_ms_yj02 = sc02.getKeyItem();
					long key02 = MedicineUtils.parseLong(KeyCreator.create("MS_YJ02", item_ms_yj02.getKeyRules(), item_ms_yj02.getId()));
					
					yj02.put("SBXH", key02);
					yj02.put("YJXH", key);// 医技序号
					/*//2017-08-09 实现沿江卫生服务中心下属的复兴社区可以开检验单，模拟为沿江医院的请求。
					//开申请单时机构编码修改为320111002，科室代码修改为1296（复兴卫生室）
					if(map_.get("JGDM").toString().equals("320111002") && map_.get("SECTION").toString().equals("1296")){
						yj02.put("JGID", "320111002004");
					}
					//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
					//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
					else if(map_.get("JGDM").toString().equals("320124005") && map_.get("SECTION").toString().equals("1107")){
						yj02.put("JGID", "320124005016");
					}
					//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
					//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
					else if(map_.get("JGDM").toString().equals("320124004") && map_.get("SECTION").toString().equals("1109")){
						yj02.put("JGID", "320124004018");
					}
					else{
						yj02.put("JGID", map_.get("JGDM") + "");
					}*/
					yj02.put("JGID", br_jgid);
					yj02.put("YLXH", lisyj02.get("YLXH"));
					
					if (ylsf.get("XMLX") == null) {
						throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR, "收费项目(费用序号:"+lisyj02.get("YLXH")
								+" )没有维护对应的项目类型，请联系管理员维护！");
					}
					yj02.put("XMLX", ylsf.get("XMLX"));// 项目类型
					yj02.put("ZFBL", zfblmap.get("ZFBL"));// 自负比例
					yj02.put("FYGB", ylsf.get("FYGB"));// 费用归并
					yj02.put("YLDJ", ylsf.get("FYDJ"));// 医疗单价

					if (Double.parseDouble(lisyj02.get("SL") + "") == 0.00) {
						throw new ModelOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "所开项目(费用序号:"+lisyj02.get("YLXH")
										+ " )的数量不应为0，请联系管理员！");
					}
					yj02.put("YLSL", lisyj02.get("SL"));// 医疗数量
					yj02.put("YJZH", "1");// 医技组号 ,默认传1
					double hjje = Double.parseDouble(ylsf.get("FYDJ")+"")*Double.parseDouble(lisyj02.get("SL")+"");
					yj02.put("HJJE", hjje);// 划价金额
					yj02.put("DZBL", 1);// 打折比例
					yj02.put("ZFPB", 0);// 自负判别(张伟说这里的ZFPB都传0,结算收费的地方重新计算)
					/**
					 * modified by chzhxiang 2013.10.01
					 * if((Double.parseDouble(zfblmap.get("ZFBL")+"")) == 1.00){
					 * yj02.put("ZFPB", 1);//自负判别 }else{ yj02.put("ZFPB", 0); }
					 */
					//yx-2018-04-26-金保提示项目没维护自编码-b
					String brxz=map_.get("BRXZ")+"";
					if(brxz.equals("2000")){
						String zbmjgid="";
						Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
						zbmjgid=njjb.getItem(br_jgid).getProperty("zbmjgid")+"";
						String sql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
						Map<String, Object> p=new HashMap<String, Object>();
						p.put("jgid",zbmjgid);
						p.put("fyxh",Long.parseLong(lisyj02.get("YLXH")+""));
						Map<String, Object> zbmmap=dao.doSqlLoad(sql,p);
						if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
							if(njjbmsg.length()==0){
								njjbmsg+="所开项目中"+lisyj02.get("YLMC");
							}else{
								njjbmsg+="、"+lisyj02.get("YLMC");
							}
						}
					}
					//yx-2018-04-26-金保提示项目没维护自编码-e
					String hql02 = "insert into ms_yj02(SBXH,JGID,YJXH,YLXH,XMLX,FYGB,ZFBL,YLSL,YLDJ,YJZH,HJJE,DZBL,ZFPB) values "
							+ "(:SBXH,:JGID,:YJXH,:YLXH,:XMLX,:FYGB,:ZFBL,:YLSL,:YLDJ,:YJZH,:HJJE,:DZBL,:ZFPB)";
					dao.doSqlUpdate(hql02, yj02);

					if (!it2.hasNext()) { // 业务要求同一条yj01对应的yj02明细中,必须有一条的yjzx值为1（Zhangsw提）
						dao.doSqlUpdate("update ms_yj02 set yjzx=1 where sbxh="
								+ key02, null);
					}
					
					// 返回LIS信息
					res01 = new HashMap<String, Object>();
					res01.put("SQDH", map_.get("DOCTREQUESTNO"));
					res01.put("YLXH", lisyj02.get("YLXH"));
					res01.put("YJXH", key);
					res01.put("SBXH", key02);
					res01.put("SAMPLETYPE", lisyj02.get("SAMPLETYPE"));//add by chzhxiang 2013.10.12
					res01.put("PREHYID", lisyj02.get("PREHYID"));
					reslist.add(res01);
				}
			}
		} catch (PersistentDataOperationException e) {
			ss.getTransaction().rollback();
			logger.error("save lis record failed.", e.getMessage());
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					e.getMessage());
		} catch (Exception e) {
			ss.getTransaction().rollback();
			logger.error("save lis record failed.", e.getMessage());
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					e.getMessage());
		}
		ss.getTransaction().commit();
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		res.put("body", reslist);
		if(njjbmsg.length() >0 ){
			res.put("njjbmsg",njjbmsg+"未维护医保自编码，将导致收费处不能收费！");
		}
		return res;
	}

	/**
	 * 门诊lis医技单执行
	 * 
	 * @param yjxhs医技序号集
	 * @param userId
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> clinicLisRecordExecute(List yjxhs, String userId)
			throws ModelOperationException {

		try {
			for (Iterator it2 = yjxhs.iterator(); it2.hasNext();) {
				Map<String, Object> parameters = new HashMap<String, Object>();

				parameters.put("YJXH", Long.parseLong((String) it2.next()));
				parameters.put("ZXPB", 1);
				parameters.put("ZXYS", userId);
				parameters.put("ZXKS", "");// ???
				parameters.put("ZXRQ", new Date());

				dao.doUpdate(
						"update MS_YJ01 "
								+ " set ZXKS=:ZXKS,ZXPB=:ZXPB,ZXYS=:ZXYS,ZXRQ=:ZXRQ where YJXH=:YJXH",
						parameters);
			}
		} catch (Exception e) {
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"更新失败.");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 门诊医技取消执行
	 * 
	 * @param yjxhs
	 * @return
	 * @throws PHISServiceException
	 */
	public Map<String, Object> clinicLisCancelExecuteRecord(List yjxhs)
			throws ModelOperationException {
		try {
			for (Iterator it2 = yjxhs.iterator(); it2.hasNext();) {
				Map<String, Object> parameters = new HashMap<String, Object>();

				parameters.put("YJXH", Long.parseLong((String) it2.next()));
				parameters.put("ZXPB", 0);
				// parameters.put("ZXYS",userId);
				parameters.put("ZXKS", "");// ???
				parameters.put("ZXRQ", new Date());

				dao.doUpdate(
						"update MS_YJ01 "
								+ " set ZXKS=:ZXKS,ZXPB=:ZXPB,ZXYS=:ZXYS,ZXRQ=:ZXRQ where YJXH=:YJXH",
						parameters);
			}
		} catch (Exception e) {
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"更新失败.");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 门诊医技作废
	 * 
	 * @param sqdhId
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> clinicLisInvalidRecord(Map<String, Object> lisid)
			throws ModelOperationException {

		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) lisid
					.get("list");
			ss.beginTransaction();
			for (Iterator<Map<String, Object>> it = list.iterator(); it
					.hasNext();) {
				Map<String, Object> lis = it.next();
//				Map<String, Object> parameters = new HashMap<String, Object>();
//				parameters.put("SQID", Long.parseLong(lis.get("SQID") + ""));
//				parameters.put("YJXH", Long.parseLong(lis.get("YJXH") + ""));
//				parameters.put("DJLY", "8");
//				parameters.put("ZFPB", 1);
//				parameters.put("ZXPB", 0);
//				parameters.put("ZXYS", "");
//				parameters.put("ZXKS", "");

				// 查询项目是否已经收费
				/***/
				Map<String, Object> yjsf = new HashMap<String, Object>();
				yjsf.put("SQID", Long.parseLong(lis.get("SQID") + ""));
				yjsf.put("YJXH", Long.parseLong(lis.get("YJXH") + ""));
				yjsf.put("DJLY", 8);
				Map<String, Object> yj01 = dao
						.doLoad("select a.FPHM as FPHM, a.ZFPB as ZFPB from MS_YJ01 a where a.SQID =:SQID and a.YJXH = :YJXH and a.DJLY = :DJLY",
								yjsf);
				 if(yj01 == null){
					ss.getTransaction().commit();
					Map<String, Object> res = new HashMap<String, Object>();
					res.put("x-response-code", ServiceCode.CODE_OK);
					res.put("x-response-msg", "success");
					return res;
					 //throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					 //		 "未找到对应项目！申请单号:"+lis.get("SQID")+",HIS医技序号:"+lis.get("YJXH")+"的检验开单！");
				 }
				 
				 String fphm = yj01.get("FPHM")+"";
				 int zfpb = Integer.parseInt(yj01.get("ZFPB")+"");
				 if(fphm.trim().length() != 0 && zfpb != 0){
					 throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,"该项目已收费,不能删除！发票号码为:"+fphm);
				 }
								 
				Map<String, Object> yj02 = new HashMap<String, Object>();
				yj02.put("YJXH", Long.parseLong(lis.get("YJXH") + ""));
				dao.doUpdate("delete from MS_YJ02  where YJXH = :YJXH ",yj02);

				dao.doUpdate("delete from MS_YJ01 where YJXH = :YJXH and SQID = :SQID and DJLY = :DJLY",yjsf);
				
//				dao.doSqlUpdate("update "
//								+ MS_YJ01
//								+ " set ZXKS=:ZXKS,ZXPB=:ZXPB,ZXYS=:ZXYS,ZFPB=:ZFPB where SQID=:SQID and YJXH = :YJXH and DJLY = :DJLY",
//						parameters);
			}

		} catch (Exception e) {
			ss.getTransaction().rollback();
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,"删除医技单失败！." + e.getMessage());
		}
		ss.getTransaction().commit();
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 门诊医技取消作废
	 * 
	 * @param sqdhId
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> clinicLisUnInvalidRecord(
			Map<String, Object> lisid) throws ModelOperationException {

		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			ss.beginTransaction();
			List<Map<String, Object>> list = (List<Map<String, Object>>) lisid
					.get("list");
			for (Iterator<Map<String, Object>> it = list.iterator(); it
					.hasNext();) {
				Map<String, Object> lis = it.next();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SQID", Long.parseLong(lis.get("SQID") + ""));
				parameters.put("YJXH", Long.parseLong(lis.get("YJXH") + ""));
				parameters.put("DJLY", "8");
				parameters.put("ZFPB", 0);

				dao.doSqlUpdate(
						"update MS_YJ01 "
								+ " set ZFPB=:ZFPB where SQID=:SQID and YJXH = :YJXH and DJLY = :DJLY",
						parameters);
			}

		} catch (Exception e) {
			ss.getTransaction().rollback();
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"更新失败." + e.getMessage());
		}
		ss.getTransaction().commit();
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 门诊医技状态查询 sqid 申请单号 jgid 机构ID
	 * 
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> clinicLisRecordStatus(long sqid, String jgid)
			throws ModelOperationException {

		try {
			List<Map<String, Object>> list;
			Map<String, Object> res = new HashMap<String, Object>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer hql = new StringBuffer();
			parameters.put("SQID", sqid);
			parameters.put("JGID", jgid);				
			parameters.put("ZFPB", 0);
			//zhaojian 2017-09-19 解决HIS中处置录入模块新增的信息sqid与检验开单后LIS中l_lis_sqd表中申请单号冲突问题，增加过滤条件以便LIS调用该接口时不会查询到处置录入模块录入的信息
			//hql.append("select SQID as sqid,JGID as jgid,ZFPB as zfpb,FPHM as fphm from MS_YJ01 where SQID=:SQID and JGID=:JGID and ZFPB=:ZFPB");
			hql.append("select SQID as sqid,JGID as jgid,ZFPB as zfpb,FPHM as fphm from MS_YJ01 where (SQDMC is not null or SQDMC<>'') and SQID=:SQID and JGID=:JGID and ZFPB=:ZFPB");
			list = dao.doSqlQuery(hql.toString(), parameters);
			if(list.size() == 0 &&  jgid.equals("320111002"))
			{
				parameters.put("JGID", "320111002004");
				list = dao.doSqlQuery(hql.toString(), parameters);
			}
			//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
			//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
			else if(list.size() == 0 &&  jgid.equals("320124005")){
				parameters.put("JGID", "320124005016");
				list = dao.doSqlQuery(hql.toString(), parameters);
			}
			//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
			//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
			else if(list.size() == 0 &&  jgid.equals("320124004")){
				parameters.put("JGID", "320124004018");
				list = dao.doSqlQuery(hql.toString(), parameters);
			}
			if (list.size() == 0) {
				throw new ModelOperationException(
						" query MS_YJ01 0 reocord with yjxh:" + sqid
								+ " and jgid:" + jgid + "zfpb:0");
			}
			// String zxpb = (String) map.get("ZXPB");
			// String zfpb = (String) map.get("ZFPB");
			// String fphm = (String) map.get("FPHM");
			// if (zfpb.equals("1")) {
			// res.put("body", "已作废");
			// } else if (zxpb.equals("0") && zfpb.equals("0") &&
			// fphm.equals("")) {
			// res.put("body", "未执行");
			// } else if (zxpb.equals("1") && zfpb.equals("0") &&
			// fphm.equals("")) {
			// res.put("body", "已执行");
			// } else if (zxpb.equals("1") && zfpb.equals("0") &&
			// fphm.equals("")) {
			// res.put("body", "已收费");
			// } else {
			// res.put("body", "未知状态");
			// }
			res.put("body", list);
			res.put("x-response-code", ServiceCode.CODE_OK);
			res.put("x-response-msg", "success");
			//System.out.println("======================="+res);
			return res;
		} catch (Exception e) {
			// e.printStackTrace();
			throw new ModelOperationException(
					"Get clinicLisRecordStatus failed.", e);
		}
	}

	/**
	 * 住院lis医技单提交
	 * 
	 * @param yjxxs
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> inhospLisRecordSubmit(List<Object> yjxxs,
			long sqdhId, String djly) throws ModelOperationException {
		//System.out.println("接收LIS住院医技单提交数据：" + yjxxs);
		List<Map<String, Object>> reslist = new ArrayList<Map<String, Object>>();
		Map<String, Object> res = new HashMap<String, Object>();
		String njjbmsg="";
		if (yjxxs.size() == 0 || djly == null) {
			throw new ModelOperationException("Args Illegal!");
		}
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			DictionaryItem di = DictionaryController.instance()
					.get("phis.dictionary.useRate").getItem("st");
			Schema sc;
			try {
				sc = SchemaController.instance().get(
						"phis.application.war.schemas.ZY_BQYZ_LS");
			} catch (ControllerException e) {
				throw new ModelDataOperationException("获取schema文件异常!", e);
			}
			List<SchemaItem> items = sc.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if ("YZZH".equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			Long YZZH = Long.parseLong(KeyManager.getKeyByName("ZY_BQYZ",
					item.getKeyRules(), item.getId(), ctx));

			ss.beginTransaction();
			/** 遍历LIS医技单List */
			for (Iterator it = yjxxs.iterator(); it.hasNext();) {
				Map<String, Object> yjxx = (Map<String, Object>) it.next();
				/** ZY_BQYZ */
				Map<String, Object> map_ = (Map<String, Object>) yjxx
						.get("BQYZ");
				Map<String, Object> yj01Map = new HashMap<String, Object>();
				
				String br_jgid = map_.get("JGDM") + "";
				// 查询病人信息
				String brxxhql = "select a.BRKS as BRKS ,a.BRBQ as BRBQ,a.BRCH as BRCH,a.BRXZ as BRXZ  from ZY_BRRY a where a.CYPB=0 and a.ZYH = :ZYH and a.JGID = :JGID";
				Map<String, Object> brry = new HashMap<String, Object>();		
				brry.put("ZYH", map_.get("ZYH"));
				brry.put("JGID", map_.get("JGDM") + "");
				List<Map<String, Object>> list = dao.doSqlQuery(brxxhql, brry);		
				if (list.size() == 0 && map_.get("JGDM").toString().equals("320124005")) {
				//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
				//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
					brry.put("JGID", "320124005016");
					list = dao.doSqlQuery(brxxhql, brry);	
					br_jgid="320124005016";
				}
				//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
				//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
				else if(list.size() == 0 && map_.get("JGDM").toString().equals("320124004")){
					brry.put("JGID", "320124004018");
					list = dao.doSqlQuery(brxxhql, brry);	
					br_jgid="320124004018";
				}
				if (list.size() == 0) {
					throw new ModelOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "未找到住院号为:"
									+ map_.get("ZYH") + "的病人!");
				}
				Map<String, Object> brxx = list.get(0);

				// 查询医疗收费信息
				String hqlylsf = "select a.XMLX as XMLX,b.FYDJ as FYDJ, a.FYGB as FYGB,"
						+ "a.FYDW as FYDW ,a.FYMC as FYMC from GY_YLSF a,GY_YLMX b where "
						+ "a.FYXH = b.FYXH and b.JGID = :JGID and a.FYXH =:FYXH";
				Map<String, Object> mapylsf = new HashMap<String, Object>();				

				/*String mapylsf_jgid = map_.get("JGDM") + "";
				String error_fy="未找到对应的收费项目.";
				//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
				//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
				if(mapylsf_jgid.equals("320124005")){
					mapylsf_jgid = "320124005016";
					error_fy="未找到明觉分院对应的收费项目，请保证中心已调入的收费项目分院也已调入.";
				}
				//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
				//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
				else if(mapylsf_jgid.equals("320124004")){
					mapylsf_jgid = "320124004018";
					error_fy="未找到群力分院对应的收费项目，请保证中心已调入的收费项目分院也已调入.";
				}*/
				mapylsf.put("JGID", br_jgid);
				mapylsf.put("FYXH", map_.get("YLXH"));
				List<Map<String, Object>> fylist = dao.doSqlQuery(hqlylsf,mapylsf);
				if (fylist.size() == 0) {
					throw new ModelOperationException(
							//ServiceCode.CODE_DATABASE_ERROR, "未找到对应的收费项目.");
							ServiceCode.CODE_DATABASE_ERROR, "未找到对应的收费项目."+hqlylsf.replaceAll(":JGID", br_jgid).replaceAll(":FYXH", map_.get("YLXH").toString()));
							//ServiceCode.CODE_DATABASE_ERROR, error_fy+hqlylsf.replaceAll(":JGID", mapylsf_jgid).replaceAll(":FYXH", map_.get("YLXH").toString()));
				}
				Map<String, Object> ylsf = fylist.get(0);
				
				/*********modify by zhaojian 2017-05-11 解决主键冲突问题：改为从序列中获取*******************/
				/*long key = Long.parseLong(getTableID(BSPHISEntryNames.ZY_BQYZ,
						"JLXH", "ZY_BQYZ"));*/
				Schema scyz = SchemaController.instance().get(BSPHISEntryNames.ZY_BQYZ);
				SchemaItem item_zy_bqyz = scyz.getKeyItem();
				long key = MedicineUtils.parseLong(KeyCreator.create("ZY_BQYZ", item_zy_bqyz.getKeyRules(), item_zy_bqyz.getId()));
				
				/*//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
				//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
				if(map_.get("JGDM").toString().equals("320124005")){
					yj01Map.put("JGID", "320124005016");// 机构ID
				}
				//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
				//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
				else if(mapylsf_jgid.equals("320124004")){
					yj01Map.put("JGID", "320124004018");// 机构ID
				}
				else{
					yj01Map.put("JGID", map_.get("JGDM"));// 机构ID
				}*/
				
				yj01Map.put("JGID", br_jgid);// 机构ID
				yj01Map.put("JLXH", key);//记录序号
				yj01Map.put("ZYH", map_.get("ZYH"));// 住院号
				yj01Map.put("YSGH", map_.get("YSGH"));// 医生工号
				yj01Map.put("YPXH", map_.get("YLXH"));// 医疗序号
				yj01Map.put("YPLX", 0);//药品类型
				yj01Map.put("YZZH", YZZH);//医嘱组号
				yj01Map.put("TZFHBZ", 0);//停嘱复核标志
				yj01Map.put("XMLX", "4");//项目类型
				yj01Map.put("YZMC", map_.get("YLMC"));// 医嘱名称
				yj01Map.put("SRKS", brxx.get("BRBQ"));// 输入科室
				yj01Map.put("ZXKS", map_.get("ZXKS"));// 执行科室
				yj01Map.put("KSSJ", new Date());// 开始时间
				yj01Map.put("TZSJ", new Date());// 停止时间
				yj01Map.put("BRKS", brxx.get("BRKS"));// 病人科室
				yj01Map.put("BRBQ", brxx.get("BRBQ"));// 病人病区
				yj01Map.put("BRCH", brxx.get("BRCH"));// 病人床号
				yj01Map.put("SQID", map_.get("SQID"));// 申请单号
				yj01Map.put("SQDMC", map_.get("YBLXMC"));// 申请单名称
				yj01Map.put("LSYZ", "1");// 临时医嘱
				//modify by zhaojian  2017-05-08 更改使用标志，增加计费标志
				//yj01Map.put("SYBZ", 1);// 使用标志
				yj01Map.put("SYBZ", 0);// 使用标志
				yj01Map.put("JFBZ", 2);// 计费标志 1药品 2临时医嘱 3文字医嘱 9医技使用的			
				//System.out.println("*******************************插入医技数据时，增加jfbz=2*******************************");	
				yj01Map.put("LSBZ", 0);// 历史标志
				yj01Map.put("YCSL", map_.get("SL"));// 一次数量
				yj01Map.put("YPDJ", Double.parseDouble(ylsf.get("FYDJ") + ""));// 费用单价
				yj01Map.put("TZYS", map_.get("YSGH"));// 停医嘱医生
				yj01Map.put("CZGH", map_.get("YSGH"));// 操作工号
				yj01Map.put("ZFPB", 1);// 自负判别
				yj01Map.put("SYPC", "st");// 使用频次
				yj01Map.put("BZXX", "检验电子申请");
				yj01Map.put("YSBZ", 1);//医生医嘱标志
				yj01Map.put("YSTJ", 1);//医生提交标志
				yj01Map.put("YZZXSJ", di.getProperty("ZXSJ"));//医嘱执行时间
				//modify by zhaojian  2017-05-08 增加计费标志
				/*String hql01 = "insert into zy_bqyz(JLXH,JGID,ZYH,YSGH,YPXH,YZMC,SRKS,KSSJ,BRKS,BRBQ,BRCH,SQID,SQDMC,XMLX,YPLX,YZZH,TZFHBZ,LSYZ,SYBZ,TZSJ,ZXKS,LSBZ,YCSL,YPDJ,TZYS,CZGH,ZFPB,SYPC,BZXX,YSBZ,YSTJ,YZZXSJ) values "
						+ "(:JLXH,:JGID,:ZYH,:YSGH,:YPXH,:YZMC,:SRKS,:KSSJ,:BRKS,:BRBQ,:BRCH,:SQID,:SQDMC,:XMLX,:YPLX,:YZZH,:TZFHBZ,:LSYZ,:SYBZ,:TZSJ,:ZXKS,:LSBZ,:YCSL,:YPDJ,:TZYS,:CZGH,:ZFPB,:SYPC,:BZXX,:YSBZ,:YSTJ,:YZZXSJ)";*/
				String hql01 = "insert into zy_bqyz(JLXH,JGID,ZYH,YSGH,YPXH,YZMC,SRKS,KSSJ,BRKS,BRBQ,BRCH,SQID,SQDMC,XMLX,YPLX,YZZH,TZFHBZ,LSYZ,SYBZ,TZSJ,ZXKS,LSBZ,YCSL,YPDJ,TZYS,CZGH,ZFPB,SYPC,BZXX,YSBZ,YSTJ,YZZXSJ,JFBZ) values "
						+ "(:JLXH,:JGID,:ZYH,:YSGH,:YPXH,:YZMC,:SRKS,:KSSJ,:BRKS,:BRBQ,:BRCH,:SQID,:SQDMC,:XMLX,:YPLX,:YZZH,:TZFHBZ,:LSYZ,:SYBZ,:TZSJ,:ZXKS,:LSBZ,:YCSL,:YPDJ,:TZYS,:CZGH,:ZFPB,:SYPC,:BZXX,:YSBZ,:YSTJ,:YZZXSJ,:JFBZ)";
				dao.doSqlUpdate(hql01, yj01Map);
				// 返回LIS信息
				//yx-2018-04-26-金保提示项目没维护自编码-b
				String brxz=brxx.get("BRXZ")+"";
				if(brxz.equals("2000")){
					String zbmjgid="";
					Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
					zbmjgid=njjb.getItem(br_jgid).getProperty("zbmjgid")+"";
					String sql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("jgid",zbmjgid);
					p.put("fyxh",Long.parseLong(map_.get("YLXH")+""));
					Map<String, Object> zbmmap=dao.doSqlLoad(sql,p);
					if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
						if(njjbmsg.length()==0){
							njjbmsg+="所开项目中"+map_.get("YLMC");
						}else{
							njjbmsg+="、"+map_.get("YLMC");
						}
					}
				}
				//yx-2018-04-26-金保提示项目没维护自编码-e
				map_.put("JLXH", key);
				reslist.add(map_);
			}
		} catch (PersistentDataOperationException e) {
			ss.getTransaction().rollback();
			logger.error("save lis record failed.", e.getMessage());
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					e.getMessage());
		} catch (Exception e) {
			ss.getTransaction().rollback();
			logger.error("save lis record failed.", e.getMessage());
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					e.getMessage());
		}
		ss.getTransaction().commit();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		if(njjbmsg.length() >0 ){
			res.put("njjbmsg",njjbmsg+"未维护医保自编码，将导致收费处不能收费！");
		}
		res.put("body", reslist);
		
		return res;
	}

	/**
	 * 住院lis医技单执行
	 * 
	 * @param brid
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> inhospLisRecordExecute(List yjxhs, String userId)
			throws ModelOperationException {
		try {
//			System.out.println(yjxhs + "  userId=" + userId);
			for (Iterator it2 = yjxhs.iterator(); it2.hasNext();) {
				// 判断是否是临时医嘱
				Map<String, Object> bqyz = new HashMap<String, Object>();
				long JLXH = Long.parseLong(it2.next() + "");
				bqyz.put("JLXH", JLXH);
				List<Map<String, Object>> yzxx = dao
						.doQuery(
								"select a.LSYZ as LSYZ from ZY_BQYZ a where a.JLXH = :JLXH",
								bqyz);
				if (yzxx.size() == 0) {
					throw new ModelOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "未找到对应项目！.");
				}
				Map<String, Object> map = yzxx.get(0);
				String lsyz = map.get("LSYZ") + "";

				if (lsyz.equals("1")) {// 临时医嘱
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JLXH", JLXH);
					parameters.put("SYBZ", 0);// 使用标志
					parameters.put("FHGH", userId);
					//zhaojian 2017-09-28 解决检验项目未入账时历史标志已经为1的问题，此时通知出院时未提醒存在未执行的检验项目
					//parameters.put("LSBZ", 1);
					parameters.put("LSBZ", 0);
					dao.doUpdate(
							"update ZY_BQYZ "
									+ " set SYBZ=:SYBZ,LSBZ=:LSBZ,FHGH=:FHGH where JLXH=:JLXH",
							parameters);
				} else {// 长期医嘱
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JLXH", Long.parseLong(it2.next() + ""));
					parameters.put("SYBZ", 0);
					parameters.put("FHGH", userId);

					dao.doUpdate("update ZY_BQYZ " 
							+ " set SYBZ=:SYBZ,FHGH=:FHGH where JLXH=:JLXH",
							parameters);
				}

			}
		} catch (Exception e) {
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"更新失败.");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 住院医技取消执行
	 * 
	 * @param yjxhs
	 * @return
	 * @throws PHISServiceException
	 */
	public Map<String, Object> inhospLisCancelExecuteRecord(List yjxhs)
			throws ModelOperationException {
		try {
			for (Iterator it2 = yjxhs.iterator(); it2.hasNext();) {
				Map<String, Object> parameters = new HashMap<String, Object>();

				parameters.put("JLXH", Long.parseLong(it2.next() + ""));
				parameters.put("FHGH", null);
				parameters.put("SYBZ", 1);
				parameters.put("LSBZ", 0);
				dao.doUpdate(
						"update ZY_BQYZ "
								+ " set FHGH=:FHGH,SYBZ=:SYBZ,LSBZ=:LSBZ where JLXH=:JLXH",
						parameters);
			}
		} catch (Exception e) {
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"更新失败.");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 住院医技作废
	 * 
	 * @param sqdhId
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> inhospLisInvalidRecord(Map<String, Object> lisid)
			throws ModelOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) lisid
					.get("list");
			ss.beginTransaction();
			for (Iterator<Map<String, Object>> it = list.iterator(); it
					.hasNext();) {
				Map<String, Object> lis = it.next();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SQID", lis.get("SQID") + "");
				parameters.put("JLXH", lis.get("JLXH"));
				// parameters.put("ZFBZ", 1);

				// 查询项目是否已执行
				Map<String, Object> yjsf = new HashMap<String, Object>();
				yjsf.put("SQID", Long.parseLong(lis.get("SQID") + ""));
				yjsf.put("JLXH", Long.parseLong(lis.get("JLXH") + ""));

				long count = dao
						.doCount(
								"ZY_BQYZ",
								" SQID =:SQID and JLXH = :JLXH and FHGH is null ",
								yjsf);
				if (count == 0) {
					throw new ModelOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "未找到对应项目或该项目已执行！.");
				}

				// dao.doSqlUpdate("update "+ ZY_BQYZ +
				// " set ZFBZ=:ZFBZ where SQID=:SQID and JLXH = :JLXH ",parameters);
				dao.doSqlUpdate("delete from ZY_BQYZ " 
						+ "  where SQID=:SQID and JLXH = :JLXH ", parameters);
			}

		} catch (Exception e) {
			ss.getTransaction().rollback();
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"删除失败." + e.getMessage());
		}
		ss.getTransaction().commit();
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 住院医技取消作废
	 * 
	 * @param sqdhId
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> inhospLisUnInvalidRecord(
			Map<String, Object> lisid) throws ModelOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) lisid
					.get("list");
			ss.beginTransaction();
			for (Iterator<Map<String, Object>> it = list.iterator(); it
					.hasNext();) {
				Map<String, Object> lis = it.next();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SQID", lis.get("SQID") + "");
				parameters.put("JLXH", lis.get("JLXH"));
				parameters.put("ZFBZ", 0);

//				System.out.println(parameters);
				dao.doSqlUpdate("update ZY_BQYZ "  
						+ " set ZFBZ=:ZFBZ where SQID=:SQID and JLXH = :JLXH ",
						parameters);
			}

		} catch (Exception e) {
			ss.getTransaction().rollback();
			logger.error("update failed.", e);
			throw new ModelOperationException(ServiceCode.CODE_DATABASE_ERROR,
					"更新失败." + e.getMessage());
		}
		ss.getTransaction().commit();
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("x-response-code", ServiceCode.CODE_OK);
		res.put("x-response-msg", "success");
		return res;
	}

	/**
	 * 住院医技状态查询
	 * 
	 * @param yjxh
	 * @return
	 * @throws ModelOperationException
	 */
	public Map<String, Object> inhospLisRecordStatus(long jlxh)
			throws ModelOperationException {
		try {
			List<?> cnd;
			List<Map<String, Object>> list;
			Map<String, Object> res = new HashMap<String, Object>();

			cnd = CNDHelper.createSimpleCnd("eq", "JLXH", "s", jlxh);
			list = dao.doQuery(cnd, "JLXH", BSPHISEntryNames.ZY_BQYZ);

			if (list.size() == 0) {
				throw new ModelOperationException(
						" query ZY_BQYZ 0 reocord with jlxh:" + jlxh);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map = list.get(0);
			String zxpb = (String) map.get("JFBZ");
			String zfpb = (String) map.get("ZFBZ");
			res.put("body", map);
			res.put("x-response-code", ServiceCode.CODE_OK);
			res.put("x-response-msg", "success");
			//System.out.println("================="+res);
			return res;
		} catch (Exception e) {
			logger.error("[query lis record failed!]"+e.getMessage());
			throw new ModelOperationException(
					"Get inhospLisRecordStatus failed.", e);
		}
	}

	public String getTableID(String entryNames, String pk, String entryTable)
			throws ModelDataOperationException {
		SchemaItem item = null;
		Schema sc;
		String key = "0";
		try {
			sc = SchemaController.instance().get(entryNames);
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem sit : items) {
				if (pk.equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			key = KeyManager.getKeyByName(entryTable, item.getKeyRules(),item.getId(), ctx);
		} catch (ControllerException e1) {
			e1.printStackTrace();
			logger.error("获取主键失败", e1);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取主键失败");
		}catch (KeyManagerException e) {
			logger.error("获取主键失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取主键失败");
		}
		return key;
	}

}
