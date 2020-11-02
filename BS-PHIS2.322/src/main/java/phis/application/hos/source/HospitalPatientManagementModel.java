package phis.application.hos.source;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Hcnservices.Exception_Exception;
import Hcnservices.HCNWebservices;
import Hcnservices.HCNWebservicesService;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.application.xnh.source.XnhModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.validator.ValidateException;

public class HospitalPatientManagementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public HospitalPatientManagementModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 病人管理form回填
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doLoadBrxx(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long pkey = Long.parseLong(req.get("pkey") + "");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.ZY_BRRY
					+ "_RYDJ", pkey);
			if(BRXX.get("MZZD")!=null&&Long.parseLong(BRXX.get("MZZD")+"")!=0){
				BRXX.put("JBMC", dao.doLoad("YB_JBBM", BRXX.get("MZZD")).get("JBMC"));
			}
			if (BRXX.get("CSNY") != null) {
				BSPHISUtil.getPersonAge((Date) BRXX.get("CSNY"), null);
				BRXX.put("BRNL",
						BSPHISUtil.getPersonAge((Date) BRXX.get("CSNY"), null)
								.get("ages"));
			}
			res.put("body", BRXX);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 病人注销
	 * 
	 * @param req
	 * @param ctx
	 */
	public void doUpdateCanceled(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long pkey = Long.parseLong(req.get("pkey") + "");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
					pkey);
			if (1l == Long.parseLong(BRXX.get("CYPB") + "")) {
				res.put("body", "病人已通知出院，不能注销!");
				return;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", pkey);
			long count = dao.doCount("ZY_TBKK", "ZYH = :ZYH",
					parameters);
			if (count > 0) {
				Map<String, Object> JKJE = dao.doLoad(
						"SELECT sum(JKJE) as JKJE FROM "
								+ "ZY_TBKK"
								+ " WHERE ZYH = :ZYH AND ZFPB = 0", parameters);
				if (JKJE.get("JKJE") != null) {
					if (Double.parseDouble(JKJE.get("JKJE") + "") != 0) {
						res.put("body", "此病人已有缴款发生，不能进行注销操作!");
						return;
					}
				}
			}
			count = dao.doCount("ZY_BQYZ",
							"( ZYH = :ZYH ) AND ( LSBZ = 0 OR (LSBZ= 2 AND QRSJ IS NULL) OR SYBZ = 1 ) AND ( JFBZ < 3 OR JFBZ = 9 )",
							parameters);
			if (count > 0) {
				res.put("body", "病人有未停未发医嘱，不能进行注销操作!");
				return;
			}
			Map<String, Object> ZY_FYMX = dao.doLoad(
					"SELECT COUNT(*) as COUNT,sum(ZJJE) as ZJJE FROM "
							+ "ZY_FYMX" + " WHERE ZYH = :ZYH",
					parameters);
			count = Long.parseLong(ZY_FYMX.get("COUNT") + "");
			double zjje = 0;
			if (ZY_FYMX.get("ZJJE") != null) {
				zjje = Double.parseDouble(ZY_FYMX.get("ZJJE") + "");
			}
			if (count > 0) {
				if (zjje != 0) {
					res.put("body", "此病人已有费用发生，不能进行注销操作!");
					return;
				}
			}
			count = dao.doCount("ZY_RCJL",
					"ZYH = :ZYH and CZLX=1 AND BQPB=0", parameters);
			// 如果已有临床入院，则需要进行入院注销处理
			if (count > 0) {
				Map<String, Object> uf_ryrq_setparameters = new HashMap<String, Object>();
				uf_ryrq_setparameters.put("ZYH", pkey);
				uf_ryrq_setparameters.put("RYRQ", null);
				BSPHISUtil.uf_ryrq_set(uf_ryrq_setparameters, dao, ctx);
			}
			count = dao.doCount("ZY_CWSZ", "ZYH = :ZYH",parameters);
			if (count > 0) {
				dao.doUpdate("UPDATE " + "ZY_CWSZ"+ " SET ZYH = NULL WHERE ZYH = :ZYH", parameters);
			}
			parameters.put("CYRQ", new Date());
			//yx-农合的取消费用和登记-b
			if((BRXX.get("BRXZ")+"").equals("6000")){
				String NHDJID=BRXX.get("NHDJID")+"";
				if(BRXX.get("NHDJID")!=null && NHDJID.length() >0){
					JSONObject request=new JSONObject();
					String sjqzjstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId.substring(0,9));
					if(sjqzjstr!=null && sjqzjstr.length() >0){
						String qzj[]=sjqzjstr.split(":");
						URL url=XnhModel.getwebserviceurl(manaUnitId);
						HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
						HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
						try {
							//先取消费用登记
							request.put("djid", NHDJID);
							request.put("jzlx", "2");//住院
							request.put("operator",user);
							request.put("czlx","2");//操作类型 1 上传费用，2 取消费用
							request.put("yybh",qzj[0]);//村卫生室上传卫生院代码
							request.put("ip",qzj[1] );
							request.put("port",qzj[2] );
							request.put("serviceId", "UploadFyxx");
							request.put("msgType", "2");
							try {
								String reqxfyxx=hcn.doUploadFyxx(request.toString());
								JSONObject qxfyxx=new JSONObject(reqxfyxx);
								if(qxfyxx.optString("code").equals("1")){
									//更新费用明细上传标志为未上传
									Map<String, Object> p=new HashMap<String, Object>();
									p.put("ZYH",BRXX.get("ZYH"));
									dao.doSqlUpdate("update ZY_FYMX set NHSCBZ=0 where ZYH=:ZYH", p);
									//取消住院登记
									List<?> cnd = CNDHelper.createSimpleCnd("eq", "MZHM", "s", BRXX.get("MZHM"));
									Map<String, Object> xx=dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA) ;
									String nhkh="";
									String grbh="";
									if(xx!=null && xx.size() >0){
										nhkh=xx.get("NHKH")+"";
										grbh=xx.get("GRBH")+"";
									}
									if(nhkh!=null && nhkh.length() >0 && grbh!=null && grbh.length()>0 ){
										String qzjstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId.substring(0, 9));
										String yyqzj[]=qzjstr.split(":");
										request.put("cardid", nhkh);
										request.put("grbh", grbh);
										request.put("serviceId", "CancleZydj");
										request.put("yybh",yyqzj[0]);//村卫生室上传卫生院代码
										request.put("ip",yyqzj[1] );
										request.put("port",yyqzj[2] );
										String reqxdjxx=hcn.doCancleZydj(request.toString());
										JSONObject qxdjxx=new JSONObject(reqxdjxx);
										if(qxdjxx.optString("code").equals("1")){
											//重置住院农合登记为空
											dao.doSqlUpdate("update ZY_BRRY set NHDJID='' where ZYH=:ZYH", p);
										}else{
											throw new ModelDataOperationException(qxfyxx.optString("msg"));
										}
									}else{
										throw new ModelDataOperationException("获取基本信息出错，缺失基本农合信息！！！");
									}
								}else{
									throw new ModelDataOperationException(qxfyxx.optString("msg"));
								}
							} catch (Exception_Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
						throw new ModelDataOperationException("未找到该机构的农合信息，请确认该机构有报销权限！！！");
					}
				}
			}
			//yx-农合的取消费用和登记-e
			StringBuffer hql=new StringBuffer();
			hql.append("UPDATE ").append("ZY_BRRY").append(" SET CYPB = 99 , CYRQ = :CYRQ ");
			if(req.containsKey("isYb")){//如果是医保,则更新SRKH和ZYLSH
				hql.append(",SRKH=null ,ZYLSH=null");
			}
			hql.append("  WHERE ZYH = :ZYH");
			dao.doUpdate(hql.toString(),parameters);
			
//			if("6089".equals(BRXX.get("BRXZ")+"")){
//				JXMedicareModel jxmm = new JXMedicareModel(dao);
//				jxmm.saveTransferProperties(BRXX, ctx);
//			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 病人性质转换
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateTransform(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		long pkey = Long.parseLong(req.get("pkey") + "");
		long brxz = Long.parseLong(req.get("brxz") + "");
		//Map<String, Object> YBXX = (Map<String, Object>)req.get("ybxx");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
					pkey);
//			if (1l == Long.parseLong(BRXX.get("CYPB") + "")) {
//				res.put("body", "病人已通知出院，不能转换!");
//				return;
//			}
			
			long lastbrxz=Long.parseLong(BRXX.get("BRXZ") + "");//数据库中的病人性质
			if(lastbrxz==brxz){
				throw new ModelDataOperationException("同性质类型不用转换！！！");
			}
			if(lastbrxz==6000){
			//农合转其他
				String NHDJID=BRXX.get("NHDJID")+"";
				if(BRXX.get("NHDJID")!=null && NHDJID.length() >0){
					JSONObject request=new JSONObject();
					String sjqzjstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId.substring(0,9));
					if(sjqzjstr!=null && sjqzjstr.length() >0){
						String qzj[]=sjqzjstr.split(":");
						URL url=XnhModel.getwebserviceurl(manaUnitId);
						HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
						HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
						try {
							//先取消费用登记
							request.put("djid", NHDJID);
							request.put("operator",user.getUserId());
							try {
								//取消住院登记
								List<?> cnd = CNDHelper.createSimpleCnd("eq", "MZHM", "s", BRXX.get("MZHM"));
								Map<String, Object> xx=dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA) ;
								String nhkh="";
								String grbh="";
								if(xx!=null && xx.size() >0){
									nhkh=xx.get("NHKH")+"";
									grbh=xx.get("GRBH")+"";
								}
								if(nhkh!=null && nhkh.length() >0 && grbh!=null && grbh.length()>0 ){
									String qzjstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId.substring(0, 9));
									String yyqzj[]=qzjstr.split(":");
									request.put("cardid", nhkh);
									request.put("grbh", grbh);
									request.put("serviceId", "CancleZydj");
									request.put("yybh",yyqzj[0]);//村卫生室上传卫生院代码
									request.put("ip",yyqzj[1] );
									request.put("port",yyqzj[2] );
									String reqxdjxx=hcn.doCancleZydj(request.toString());
									JSONObject qxdjxx=new JSONObject(reqxdjxx);
									if(qxdjxx.optString("code").equals("1")){
										//更新费用明细上传标志为未上传
										Map<String, Object> p=new HashMap<String, Object>();
										p.put("ZYH",BRXX.get("ZYH"));
										dao.doSqlUpdate("update ZY_FYMX set NHSCBZ=0 where ZYH=:ZYH", p);
										//重置住院农合登记为空
										dao.doSqlUpdate("update ZY_BRRY set NHDJID='' where ZYH=:ZYH", p);
									}else{
										throw new ModelDataOperationException(qxdjxx.optString("msg"));
									}
								}else{
									throw new ModelDataOperationException("获取基本信息出错，缺失基本农合信息！！！");
								}
							} catch (Exception_Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
						throw new ModelDataOperationException("未找到该机构的农合信息，请确认该机构有报销权限！！！");
					}
				}
			}
			
			if(brxz==6000){
			//转成农合
				String NHDJID=BRXX.get("NHDJID")+"";
				if(NHDJID!=null&& !NHDJID.equals("null")  && NHDJID.length() >0){
					throw new ModelDataOperationException("已有农合登记信息，请先转自费！！！");
				}
				List<?> cnd = CNDHelper.createSimpleCnd("eq", "JZXH", "s", BRXX.get("ZYH"));
				List<Map<String, Object>> zdlist=dao.doList(cnd, "a.ZDLB desc", BSPHISEntryNames.EMR_ZYZDJL);
				Map<String, Object> zd=new HashMap<String, Object>();
				if(zdlist!=null && zdlist.size() >0 ){
					zd=zdlist.get(0);
				}
				List<?> cnds = CNDHelper.createSimpleCnd("eq", "MZHM", "s", BRXX.get("MZHM"));
				Map<String, Object> xx=dao.doLoad(cnds, BSPHISEntryNames.MS_BRDA) ;
				String nhkh="";
				String grbh="";
				if(xx!=null && xx.size() >0){
					nhkh=xx.get("NHKH")+"";
					grbh=xx.get("GRBH")+"";
				}
				if(nhkh!=null && nhkh.length() >0 && grbh!=null && grbh.length()>0 ){
					String qzjstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId.substring(0, 9));
					if(qzjstr!=null && qzjstr.length() >0){
						String newargs[]=qzjstr.split(":");
						JSONObject request=new JSONObject();
						try {
							request.put("operator",user.getUserId() );
							if(zd!=null && zd.size() >0){
								request.put("ICD10",zd.get("JBBM")+"");
								request.put("jbmc",zd.get("MSZD")+"");
							}else{
								request.put("ICD10","J06.903");
								request.put("jbmc","上呼吸道感染");
							}
							request.put("cardid",nhkh);
							request.put("grbh",grbh);
							request.put("yybh", newargs[0]);
							Calendar d = Calendar.getInstance();
							d.setTime(new Date());
							request.put("year", d.get(Calendar.YEAR)+"");
							request.put("ryrq",(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime()));
							request.put("zyh", BRXX.get("ZYH"));
							request.put("ryzt", BRXX.get("RYQK")+"");
							request.put("drgs", "0");
							
							request.put("ip",newargs[1] );
							request.put("port",newargs[2]);
							request.put("serviceId", "Zydj");
							request.put("msgType", "2");
							URL url=XnhModel.getwebserviceurl(manaUnitId);
							HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
							HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
							try {
								String rezydj=hcn.doZydj(request.toString());
								JSONObject zydjxx=new JSONObject(rezydj);
								if(zydjxx.optString("code").equals("1")){
									Long djid=zydjxx.optLong("djid");
									Map<String, Object> p=new HashMap<String, Object>();
									p.put("NHDJID", djid);
									p.put("ZYH", Long.parseLong(BRXX.get("ZYH")+""));
									dao.doUpdate("update ZY_BRRY set NHDJID=:NHDJID where ZYH=:ZYH ", p);
								}else{
									throw new ModelDataOperationException(zydjxx.optString("msg"));
								}
							} catch (Exception_Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
						throw new ModelDataOperationException("未找到该机构的农合信息，请确认该机构有报销权限！！！");
					}
				}else{
					throw new ModelDataOperationException("获取基本信息出错，缺失基本农合信息！！！");
				}
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", pkey);
			parameters.put("JGID", manaUnitId);
			List<Map<String, Object>> list_ZY_FYMX = dao
					.doQuery(
							"SELECT JLXH as JLXH,ZYH as ZYH,FYRQ as FYRQ,FYXH as FYXH,FYMC as FYMC,YPCD as YPCD,FYSL as FYSL,FYDJ as FYDJ,ZJJE as ZJJE,ZFJE as ZFJE,ZLJE as ZLJE,YSGH as YSGH,SRGH as SRGH,QRGH as QRGH,FYBQ as FYBQ,FYKS as FYKS,ZXKS as ZXKS,JFRQ as JFRQ,XMLX as XMLX,YPLX as YPLX,FYXM as FYXM,JSCS as JSCS,ZFBL as ZFBL,YZXH as YZXH,HZRQ as HZRQ,YJRQ as YJRQ,ZLXZ as ZLXZ,JGID as JGID FROM "
									+ "ZY_FYMX"
									+ " WHERE ( ZYH = :ZYH ) AND ( JSCS = 0 ) AND  ( JGID = :JGID )",
							parameters);
			//修改费用明细的自费比例
			list_ZY_FYMX = BSPHISUtil.change(lastbrxz, brxz, list_ZY_FYMX,dao, ctx);
			parameters.remove("JGID");
			parameters.put("BRXZ", brxz);
			//parameters.put("MZZD", Long.parseLong(YBXX.get("MZZD")==null?"0":YBXX.get("MZZD")+""));//门诊诊断
			StringBuffer hql=new StringBuffer();
			hql.append("update ").append("ZY_BRRY").append(" set BRXZ = :BRXZ ");
//			dao.doUpdate("update " + BSPHISEntryNames.ZY_BRRY
//					+ " set BRXZ = :BRXZ where ZYH = :ZYH", parameters);
			//医保
			if(req.containsKey("YBGX")){//如果是医保
				Map<String,Object> YBGX=(Map<String,Object>)req.get("YBGX");
				if("1".equals(YBGX.get("TAG"))){//自费转市医保
					hql.append(",ZYLSH=:ZYLSH,SRKH=:SRKH,YYLSH=:YYLSH");
					parameters.put("ZYLSH", YBGX.get("ZYLSH")+"");
					parameters.put("SRKH", YBGX.get("SRKH")+"");
					parameters.put("YYLSH", YBGX.get("YYLSH")+"");
					parameters.put("GRBH", YBGX.get("GRBH")+"");
				}else if("2".equals(YBGX.get("TAG"))){//市医保转自费
					hql.append(",ZYLSH=null,SRKH=null,YYLSH=null,GRBH=null");
				}
			}
			//南京金保
			if(req.containsKey("njjbdjxx")){
				Map<String,Object> njjbdjxx=(Map<String,Object>)req.get("njjbdjxx");
				hql.append(",NJJBLSH=:NJJBLSH,NJJBYLLB=:NJJBYLLB");
				parameters.put("NJJBLSH", njjbdjxx.get("NJJBLSH")+"");
				parameters.put("NJJBYLLB", njjbdjxx.get("NJJBYLLB")+"");
			}
			hql.append(" where ZYH = :ZYH");
			dao.doUpdate(hql.toString(), parameters);
//			if(brxz==6089){
//				JXMedicareModel jxmm = new JXMedicareModel(dao);
//				//Map<String,Object> ZYLSHMap = new HashMap<String, Object>();
//				YBXX.put("ZYH", pkey);
//				Map<String,Object> ZYLSH = jxmm.saveTransferProperties(YBXX, ctx);
//				ZYLSH.put("BRXZ", brxz);
//				ZYLSH.put("ZYH", pkey);
//				ZYLSH.put("YBKH", YBXX.get("YBKH"));//医保卡号
//				ZYLSH.put("YBYE", YBXX.get("YBYE"));//医保卡余额
//				ZYLSH.put("MZZD", YBXX.get("MZZD"));//门诊诊断
//				dao.doSave("update", BSPHISEntryNames.ZY_BRRY + "_RYDJ", ZYLSH, false);
//			}else{
//				Map<String,Object> ZY_BRRY = dao.doLoad(BSPHISEntryNames.ZY_BRRY+"_RYDJ", pkey);
//				if(ZY_BRRY.get("YWLSH")!=null){
//					JXMedicareModel jxmm = new JXMedicareModel(dao);
//					jxmm.saveTransferProperties(ZY_BRRY, ctx);
////					ZY_BRRY.put("YBKH", null);
////					ZY_BRRY.put("YBYE", null);
////					ZY_BRRY.put("MZZD", null);
////					ZY_BRRY.put("YWLSH", null);
//					dao.doUpdate("UPDATE ZY_BRRY set YBKH=null,YWLSH=null where ZYH="+ZY_BRRY.get("ZYH"), null);
//				}
//			}
			for (int i = 0; i < list_ZY_FYMX.size(); i++) {
				Map<String, Object> record = list_ZY_FYMX.get(i);
				dao.doSave("update", BSPHISEntryNames.ZY_FYMX, record, false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人性质修改失败");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改费用明细失败");
		}
	}

	/**
	 * 病人管理列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPatientList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String al_jgid = user.getManageUnit().getId();
		int pageSize = Integer.parseInt(req.get("pageSize") + "");
		int pageNo = Integer.parseInt(req.get("pageNo") + "");
		List<Object> cnd = (List<Object>) req.get("cnd");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("al_jgid", al_jgid);
			StringBuffer totalSql = new StringBuffer(
					"SELECT a.ZYH as ZYH FROM ");
			totalSql.append("ZY_BRRY");
			totalSql.append(" a,");
			totalSql.append("ZY_CWSZ");
			totalSql.append(" b WHERE a.BRCH = b.BRCH AND a.ZYH  = b.ZYH AND a.CYPB < 8 and a.JGID = :al_jgid ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + ExpRunner.toString(cnd, ctx);
					where = where.replace("str", "to_char");
					totalSql.append(where);
				}
			}
			totalSql.append(" UNION ALL SELECT a.ZYH as ZYH FROM ");
			totalSql.append("ZY_BRRY");
			totalSql.append(" a WHERE a.CYPB < 8 AND a.JGID = :al_jgid and NOT EXISTS ( SELECT 1 FROM ");
			totalSql.append("ZY_CWSZ");
			totalSql.append(" b WHERE a.ZYH = b.ZYH )");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + ExpRunner.toString(cnd, ctx);
					where = where.replace("str", "to_char");
					totalSql.append(where);
				}
			}
			StringBuffer sql = new StringBuffer(
			"SELECT a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXM as BRXM,a.CSNY as CSNY,a.BRXB as BRXB,a.SFZH as SFZH,a.BRXZ as BRXZ,a.BRKS as BRKS,a.BRBQ as BRBQ,a.BRCH as BRCH,a.GZDW as GZDW2,a.RYRQ as RYRQ,a.CYPB as CYPB,1 as CWFP FROM ");
			sql.append("ZY_BRRY");
			sql.append(" a,");
			sql.append("ZY_CWSZ");
			sql.append(" b WHERE a.BRCH = b.BRCH AND a.ZYH  = b.ZYH AND a.CYPB < 8 and a.JGID = :al_jgid ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + ExpRunner.toString(cnd, ctx);
					where = where.replace("str", "to_char");
					sql.append(where);
				}
			}
			sql.append(" UNION ALL SELECT a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXM as BRXM,a.CSNY as CSNY,a.BRXB as BRXB,a.SFZH as SFZH,a.BRXZ as BRXZ,a.BRKS as BRKS,a.BRBQ as BRBQ,a.BRCH as BRCH,a.GZDW as GZDW,a.RYRQ as RYRQ,a.CYPB as CYPB,0 as CWFP FROM ");
			sql.append("ZY_BRRY");
			sql.append(" a WHERE a.CYPB < 8 AND a.JGID = :al_jgid and NOT EXISTS ( SELECT 1 FROM ");
			sql.append("ZY_CWSZ");
			sql.append(" b WHERE a.ZYH = b.ZYH ) ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + ExpRunner.toString(cnd, ctx);
					where = where.replace("str", "to_char");
					sql.append(where);
				}
			}
			sql.append(" order by ZYH desc");
			List<Map<String, Object>> listTotal = dao.doSqlQuery(
					totalSql.toString(), parameters);
			res.put("totalCount", 0);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			if (listTotal != null) {
				int total = listTotal.size();
				res.put("totalCount", total);
				if (total > 0) {
					parameters.put("first", (pageNo - 1) * pageSize);
					parameters.put("max", pageSize);
					List<Map<String, Object>> listBRLB = dao.doSqlQuery(
							sql.toString(), parameters);
					for (int i = 0; i < listBRLB.size(); i++) {
						int Ll_cypb = Integer.parseInt(listBRLB.get(i).get("CYPB")+"");
						long Ll_zyh = Long.parseLong(listBRLB.get(i).get("ZYH")+"");
						if (Ll_cypb == 1) {
							continue;
						}
						Map<String, Object> ZY_RCJLparameters = new HashMap<String, Object>();
						ZY_RCJLparameters.put("gl_jgid", al_jgid);
						ZY_RCJLparameters.put("ll_zyh", Ll_zyh);
						long ll_count = dao.doCount("ZY_RCJL",
								"JGID = :gl_jgid and ZYH=:ll_zyh and CZLX=-1",
								ZY_RCJLparameters);
						if (ll_count > 0) {
							listBRLB.get(i).put("CYPB", 102);
						}
					}
					SchemaUtil.setDictionaryMassageForList(listBRLB,
							"phis.application.hos.schemas.ZY_BRRY" + "_BRGL");
					res.put("body", listBRLB);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取病人列表失败");
		} catch (ExpException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取病人列表失败！");
		}

	}

	/**
	 * 病人信息修改
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateBRRY(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
					body.get("ZYH"));
			if (1l == Long.parseLong(BRXX.get("CYPB") + "")) {
				res.put("body", "病人已通知出院，不能修改!");
				return;
			}
			Map<String, Object> ZY_BRRY = new HashMap<String, Object>();
			ZY_BRRY.put("ZYH", body.get("ZYH"));
			ZY_BRRY.put("BRXM", body.get("BRXM"));
			ZY_BRRY.put("BRXB", body.get("BRXB"));
			ZY_BRRY.put("CSNY", BSHISUtil.toDate(body.get("CSNY") + ""));
			ZY_BRRY.put("RYNL", body.get("RYNL"));
			ZY_BRRY.put("SFZH", body.get("SFZH"));
			ZY_BRRY.put("GJDM", body.get("GJDM"));
			ZY_BRRY.put("MZDM", body.get("MZDM"));
			ZY_BRRY.put("HYZK", body.get("HYZK"));
			ZY_BRRY.put("ZYDM", body.get("ZYDM"));
			ZY_BRRY.put("GZDW", body.get("GZDW"));
			ZY_BRRY.put("JTDH", body.get("JTDH"));
			ZY_BRRY.put("LXRM", body.get("LXRM"));
			ZY_BRRY.put("LXDH", body.get("LXDH"));
			ZY_BRRY.put("HKYB", body.get("HKYB"));
			ZY_BRRY.put("LXDZ", body.get("LXDZ"));
			ZY_BRRY.put("BRID", body.get("BRID"));
			ZY_BRRY.put("BRKS", body.get("BRKS"));
			ZY_BRRY.put("BRKS", body.get("BRKS"));
			ZY_BRRY.put("RYQK", body.get("RYQK"));
			ZY_BRRY.put("SZYS", body.get("SZYS"));
			ZY_BRRY.put("ZSYS", body.get("ZSYS"));
			ZY_BRRY.put("RYRQ", BSHISUtil.toDate(body.get("RYRQ") + ""));
			/*********************/
			ZY_BRRY.put("CSD_SQS", body.get("CSD_SQS"));
			ZY_BRRY.put("CSD_S", body.get("CSD_S"));
			ZY_BRRY.put("CSD_X", body.get("CSD_X"));
			ZY_BRRY.put("JGDM_SQS", body.get("JGDM_SQS"));
			ZY_BRRY.put("JGDM_S", body.get("JGDM_S"));
			ZY_BRRY.put("XZZ_SQS", body.get("XZZ_SQS"));
			ZY_BRRY.put("XZZ_S", body.get("XZZ_S"));
			ZY_BRRY.put("XZZ_X", body.get("XZZ_X"));
			ZY_BRRY.put("XZZ_QTDZ", body.get("XZZ_QTDZ"));
			ZY_BRRY.put("XZZ_DH", body.get("XZZ_DH"));
			ZY_BRRY.put("XZZ_YB", body.get("XZZ_YB"));
			ZY_BRRY.put("HKDZ_SQS", body.get("HKDZ_SQS"));
			ZY_BRRY.put("HKDZ_S", body.get("HKDZ_S"));
			ZY_BRRY.put("HKDZ_X", body.get("HKDZ_X"));
			ZY_BRRY.put("HKDZ_QTDZ", body.get("HKDZ_QTDZ"));
			ZY_BRRY.put("DWDH", body.get("DWDH"));
			ZY_BRRY.put("DWYB", body.get("DWYB"));
			ZY_BRRY.put("LXGX", body.get("LXGX"));
			ZY_BRRY.put("YBLB", body.get("YBLB"));
			ZY_BRRY.put("MZZD", body.get("MZZD"));
			if(body.containsKey("NJJBYLLB")){
				ZY_BRRY.put("NJJBYLLB", body.get("NJJBYLLB"));
			}
			if(body.containsKey("YBMC")){
				ZY_BRRY.put("YBMC", body.get("YBMC"));
			}
			if(body.containsKey("ZSESHBZKH")){
				ZY_BRRY.put("ZSESHBZKH", body.get("ZSESHBZKH"));
			}
			if(body.containsKey("YBZY")){
				ZY_BRRY.put("YBZY", body.get("YBZY"));
			}
			/******************************* */
			Map<String, Object> MS_BRDA = new HashMap<String, Object>();
			MS_BRDA.put("BRXM", body.get("BRXM"));
			MS_BRDA.put("BRXB", body.get("BRXB"));
			MS_BRDA.put("CSNY", BSHISUtil.toDate(body.get("CSNY") + ""));
			MS_BRDA.put("SFZH", body.get("SFZH"));
			MS_BRDA.put("GJDM", body.get("GJDM"));
			MS_BRDA.put("MZDM", body.get("MZDM"));
			MS_BRDA.put("HYZK", body.get("HYZK"));
			MS_BRDA.put("ZYDM", body.get("ZYDM"));
			MS_BRDA.put("DWMC", body.get("GZDW"));
			MS_BRDA.put("JTDH", body.get("JTDH"));
			MS_BRDA.put("LXRM", body.get("LXRM"));
			MS_BRDA.put("LXDH", body.get("LXDH"));
			MS_BRDA.put("HKYB", body.get("HKYB"));
			MS_BRDA.put("LXDZ", body.get("LXDZ"));
			MS_BRDA.put("BRID", body.get("BRID"));
			MS_BRDA.put("XGSJ", new Date());
			/*********************/
			MS_BRDA.put("CSD_SQS", body.get("CSD_SQS"));
			MS_BRDA.put("CSD_S", body.get("CSD_S"));
			MS_BRDA.put("CSD_X", body.get("CSD_X"));
			MS_BRDA.put("JGDM_SQS", body.get("JGDM_SQS"));
			MS_BRDA.put("JGDM_S", body.get("JGDM_S"));
			MS_BRDA.put("XZZ_SQS", body.get("XZZ_SQS"));
			MS_BRDA.put("XZZ_S", body.get("XZZ_S"));
			MS_BRDA.put("XZZ_X", body.get("XZZ_X"));
			MS_BRDA.put("XZZ_QTDZ", body.get("XZZ_QTDZ"));
			MS_BRDA.put("XZZ_DH", body.get("XZZ_DH"));
			MS_BRDA.put("XZZ_YB", body.get("XZZ_YB"));
			MS_BRDA.put("HKDZ_SQS", body.get("HKDZ_SQS"));
			MS_BRDA.put("HKDZ_S", body.get("HKDZ_S"));
			MS_BRDA.put("HKDZ_X", body.get("HKDZ_X"));
			MS_BRDA.put("HKDZ_QTDZ", body.get("HKDZ_QTDZ"));
			MS_BRDA.put("DWDH", body.get("DWDH"));
			MS_BRDA.put("DWYB", body.get("DWYB"));
			MS_BRDA.put("LXGX", body.get("LXGX"));
			/******************************* */
			Map<String, Object> MPI_DemographicInfo = new HashMap<String, Object>();
			MPI_DemographicInfo.put("personName", body.get("BRXM"));
			MPI_DemographicInfo.put("sexCode", body.get("BRXB"));
			MPI_DemographicInfo.put("birthday",
					BSHISUtil.toDate(body.get("CSNY") + ""));
			MPI_DemographicInfo.put("idCard", body.get("SFZH"));
			MPI_DemographicInfo.put("nationalityCode", body.get("GJDM"));
			MPI_DemographicInfo.put("nationCode", body.get("MZDM"));
			MPI_DemographicInfo.put("maritalStatusCode", body.get("HYZK"));
			MPI_DemographicInfo.put("workCode", body.get("ZYDM"));
			MPI_DemographicInfo.put("workPlace", body.get("GZDW"));
			MPI_DemographicInfo.put("phoneNumber", body.get("JTDH"));
			MPI_DemographicInfo.put("contact", body.get("LXRM"));
			MPI_DemographicInfo.put("contactPhone", body.get("LXDH"));
			MPI_DemographicInfo.put("zipCode", body.get("HKYB"));
			MPI_DemographicInfo.put("address", body.get("LXDZ"));
			MPI_DemographicInfo.put(
					"empiId",
					dao.doLoad(BSPHISEntryNames.MS_BRDA, body.get("BRID")).get(
							"EMPIID"));
			dao.doSave("update", BSPHISEntryNames.ZY_BRRY + "_RYDJ", ZY_BRRY,
					false);
			dao.doSave("update", BSPHISEntryNames.MS_BRDA, MS_BRDA, false);
			dao.doSave("update", BSPHISEntryNames.MPI_DemographicInfo,
					MPI_DemographicInfo, false);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息修改失败");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息修改失败");
		}
	}

}
