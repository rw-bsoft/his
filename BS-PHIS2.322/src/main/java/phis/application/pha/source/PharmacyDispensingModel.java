package phis.application.pha.source;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
//import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

/**
 * 药房直接发药和取消发药model
 * 
 * @author caijy
 * 
 */
public class PharmacyDispensingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyDispensingModel.class);

	public PharmacyDispensingModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 发药
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDispensing(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Long cfsb = MedicineUtils.parseLong(body.get("cfsb"));
		String fygh = MedicineUtils.parseString(body.get("fygh"));
		// Long fyck = parseLong(body.get("fyck"));
		StringBuffer cfHql = new StringBuffer();
		cfHql.append(" CFSB=:cfsb and FYBZ=1");
		StringBuffer hql_cf_zf = new StringBuffer();
		hql_cf_zf.append(" CFSB=:cfsb and ZFPB=1");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cfsb", cfsb);
		try {
			long l = 0;
			l = dao.doCount("MS_CF01", cfHql.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("注意：该药已发!", 9000);
			}
			l = dao.doCount("MS_CF01", hql_cf_zf.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("注意：该处方单已被作废!", 9000);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询是发药失败", e);
		}
		StringBuffer hql_cfmx = new StringBuffer();
		hql_cfmx.append("select a.CFSB as CFSB,a.SJYF as SJYF,a.SJFYBZ as SJFYBZ,a.SJJG as SJJG,a.YFSB as YFSB,b.YPXH as YPXH,b.YPCD as YPCD,b.YPDJ as YPDJ,b.YPSL as YPSL,b.XMLX as YPLX,b.SBXH as SBXH,a.CFLX as CFLX,a.FPHM as FPHM,b.YFGG as YPGG,b.YFDW as YFDW,b.YFBZ as YFBZ,b.YPDJ as HJJG,b.CFTS as CFTS from MS_CF01 a,MS_CF02 b where a.CFSB=b.CFSB and a.CFSB=:cfsb and b.YPXH!=0 and b.ZFYP!=1");
		try {
			List<Map<String, Object>> list_cfmx = dao.doSqlQuery(hql_cfmx.toString(), parameters);
			//执行减库存
			List<Map<String, Object>> list_kc_update = queryAndLessInventory(list_cfmx, ctx);
			if (list_kc_update != null && list_kc_update.size() == 1
					&& list_kc_update.get(0).get("ypxh") != null) {
				StringBuffer hql_ypmc = new StringBuffer();
				hql_ypmc.append("select YPMC as YPMC from YK_TYPK  where YPXH=:ypxh");
				Map<String, Object> map_par_ypmc = new HashMap<String, Object>();
				map_par_ypmc.put("ypxh",MedicineUtils.parseLong(list_kc_update.get(0).get("ypxh")));
				Map<String, Object> map_ypmc = dao.doLoad(hql_ypmc.toString(),map_par_ypmc);
				return MedicineUtils.getRetMap("[" + map_ypmc.get("YPMC")+ "]库存不足", 9000);
			}
			if(list_kc_update==null){//当传入的ypxh或ypcd或yfsb或ypdj或ypsl为空时报该错
				throw new ModelDataOperationException("数据异常!");
			}
			for (int i = 0; i < list_kc_update.size(); i++) {
				Map<String, Object> map_fymx_insert = list_kc_update.get(i);
				double ypsl = 0;
				if (MedicineUtils.parseInt(map_fymx_insert.get("CFLX")) == 3) {
					ypsl = MedicineUtils.formatDouble(2,MedicineUtils.parseDouble(map_fymx_insert.get("YPSL"))
									* MedicineUtils.parseInt(map_fymx_insert.get("CFTS")));
				} else {
					ypsl = MedicineUtils.parseDouble(map_fymx_insert.get("YPSL"));
				}
				map_fymx_insert.put("FYCK", 0);
				map_fymx_insert.put("FYRQ", new Date());
				map_fymx_insert.put("CFSB", cfsb);
				map_fymx_insert.put("TYGL", 0);
				map_fymx_insert.put("YPSL", ypsl);
				// 基本药物标志 不知道要不要从药品表查,暂时没查
				map_fymx_insert.put("JBYWBZ", 1);
				map_fymx_insert.put("HJJE",MedicineUtils.formatDouble(4, (MedicineUtils
								.parseDouble(map_fymx_insert.get("HJJG")))* (ypsl)));
				map_fymx_insert.put("LSJE",MedicineUtils.formatDouble(4, (MedicineUtils
								.parseDouble(map_fymx_insert.get("LSJG")))* (ypsl)));
				map_fymx_insert.put("PFJE",MedicineUtils.formatDouble(4,
								(MedicineUtils.parseDouble(map_fymx_insert.get("PFJG"))) * (ypsl)));
				map_fymx_insert.put("JHJE",MedicineUtils.formatDouble(4,
								(MedicineUtils.parseDouble(map_fymx_insert.get("JHJG"))) * (ypsl)));
				// 增加以下代码 用于增加站点中药中心发药存中心的药房
				if (map_fymx_insert.containsKey("SJFYBZ")&& MedicineUtils.parseInt(map_fymx_insert.get("SJFYBZ")) == 1) {
					map_fymx_insert.put("YFSB", MedicineUtils.parseLong(map_fymx_insert.get("SJYF")));
					map_fymx_insert.put("JGID", MedicineUtils.parseString(map_fymx_insert.get("SJJG")));
				}
				dao.doSave("create", BSPHISEntryNames.YF_MZFYMX,map_fymx_insert, false);
			}
			StringBuffer hql_cf01_update = new StringBuffer();
			hql_cf01_update
					.append("update MS_CF01 set FYRQ=:fyrq ,FYGH=:fygh, FYBZ=1,PYGH=:pygh,PYBZ=1 where CFSB=:cfsb and FYBZ=0 and ZFPB=0 ");
			UserRoleToken user = UserRoleToken.getCurrent();
			Long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("fyrq", new Date());
			parameter.put("fygh", fygh);
			parameter.put("pygh", fygh);
			// parameter.put("fyck", fyck);
			parameter.put("cfsb", cfsb);
			dao.doUpdate(hql_cf01_update.toString(), parameter);
			// 由于考虑到以后多窗口 故先更新窗口表.如果没有实现多窗口 以下代码没什么用
			StringBuffer sql_pdsl = new StringBuffer();
			int ckbh = 1;// 窗口编号 暂时写死 窗口切换做好后修改
			if (body.get("ckbh") != null&& body.get("ckbh").toString().trim().length() > 0) {
				ckbh = Integer.parseInt(body.get("ckbh").toString());
			}
			Map<String, Object> map_par_pdcf = new HashMap<String, Object>();
			map_par_pdcf.put("ckbh", ckbh);
			map_par_pdcf.put("yfsb", yfsb);
			sql_pdsl.append("update YF_CKBH set PDCF=PDCF-1 where CKBH=:ckbh and YFSB=:yfsb and PDCF>0");
			dao.doSqlUpdate(sql_pdsl.toString(), map_par_pdcf);
			/**************add by LZ读取系统参数调用发药机Web Service服务****************/
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			int FYJBZ = Integer.parseInt(ParameterUtil.getParameter(jgid,"FYJBZ", ctx));
			if(FYJBZ>0){
				StringBuffer hql_cf = new StringBuffer();
				hql_cf.append("select FPHM as FPHM from MS_CF01 where JGID=:JGID and CFSB=:CFSB and ZFPB=0");
				parameters.clear();
				parameters.put("JGID", jgid);
				parameters.put("CFSB", cfsb);
				Map<String, Object> cfxx = dao.doLoad(hql_cf.toString(),
						parameters);
				if(cfxx!=null){
					// 定义服务  
			        Service service = new Service();
			        Call call;
			        String retVal2="";
					try {
						String wsstr=DictionaryController.instance().getDic("phis.dictionary.Fyjws").getText(jgid);
						if(wsstr!=null && !"".equals(wsstr)){
							call = (Call) service.createCall();
							call.setTargetEndpointAddress(new java.net.URL("http://"+wsstr+"/WebService.asmx"));
							call.setUseSOAPAction(true);
							call.setReturnType(new QName("http://www.w3.org/2001/XMLSchema","string")); 
							call.setOperationName(new QName("http://tempuri.org/", "HisSendPrescToDevice"));
							call.setSOAPActionURI("http://tempuri.org/HisSendPrescToDevice");
							call.addParameter(new QName("http://tempuri.org/", "_PatientIdentity"),// 这里的_PatientIdentity对应参数名称  
									XMLType.XSD_STRING, ParameterMode.IN);
							retVal2 = (String) call.invoke(new Object[] {cfxx.get("FPHM").toString()});
							System.out.println("发药机Web Service返回结果："+retVal2);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			/**************add by LZ读取系统参数调用发药机Web Service服务****************/
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "发药明细保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "发药明细保存失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 减库存(无kcsb)
	 * @updateInfo update by caijy  for 库存冻结 ,body里面要包含SBXH(cf02主键)
	 * @param body
	 *            集合里的Map必须包含"YPXH"(药品序号)"YFSB"(药房识别)"YPCD"(药品产地)"YPDJ"(药品单价||
	 *            药房单价)"YPSL"(要减掉的药品数量)
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryAndLessInventory(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		if (body == null || body.size() == 0) {
			return null;
		}
		List<Map<String, Object>> list_kc_update_ret = new ArrayList<Map<String, Object>>();// 需要更新的库存集合(返回用)
		Session session = (Session) ctx.get(Context.DB_SESSION);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		try {
			//库存冻结代码
			int SFQYYFYFY=0;
			try{
			SFQYYFYFY= MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, BSPHISSystemArgument.SFQYYFYFY, ctx));
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.SFQYYFYFY, e);
			}
			if(SFQYYFYFY==1){//如果启用库存冻结
				//先删除过期的冻结库存
				MedicineCommonModel model=new MedicineCommonModel(dao);
				model.deleteKCDJ(jgid, ctx);
			}
			// 出库顺序
			List<String> list_order = new ArrayList<String>();
			int cksx_ypxq_yf=0;
			try{
			cksx_ypxq_yf = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, BSPHISSystemArgument.CKSX_YPXQ_YF, ctx));// 是否按效期排序
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_YPXQ_YF, e);
			}
			if (cksx_ypxq_yf > 0) {
				String cksx_ypxq_order_yf="A";
				try{
				cksx_ypxq_order_yf = MedicineUtils.parseString(ParameterUtil.getParameter(jgid,BSPHISSystemArgument.CKSX_YPXQ_ORDER_YF, ctx));
				}catch(Exception e){
					MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_YPXQ_ORDER_YF, e);
				}
				//yx-2017-11-14-药品效期为空先出 nulls first
				if ("A".equals(cksx_ypxq_order_yf)) {
					list_order.add("YPXQ nulls first ");
				} else {
					list_order.add("YPXQ desc nulls first ");
				}
			}
			int cksx_kcsl_yf=0;
			try{
			 cksx_kcsl_yf = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, BSPHISSystemArgument.CKSX_KCSL_YF, ctx));// 是否按库存排序
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_KCSL_YF, e);
			}
			if (cksx_kcsl_yf > 0) {
				String cksx_kcsl_order_yf="A";
				try{
				cksx_kcsl_order_yf = MedicineUtils.parseString(ParameterUtil.getParameter(jgid,BSPHISSystemArgument.CKSX_KCSL_ORDER_YF, ctx));
				}catch(Exception e){
					MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_KCSL_ORDER_YF, e);
				}
				if ("A".equals(cksx_kcsl_order_yf)) {
					if (list_order.size() > 0) {
						list_order.add(",YPSL");
					} else {
						list_order.add("YPSL");
					}
				} else {
					if (list_order.size() > 0) {
						list_order.add(",YPSL desc");
					} else {
						list_order.add("YPSL desc");
					}
				}
			}
			long yfsb = 0;
			StringBuffer hql_kcdj=new StringBuffer();//查询冻结数量
			hql_kcdj.append("select YPSL as YPSL,YFBZ as YFBZ from YF_KCDJ where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and JLXH!=:jlxh");
			StringBuffer hql_yfbz=new StringBuffer();//查询药房包装,用于计算冻结的实际数量
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
			StringBuffer hql_kcsl_sum=new StringBuffer();//查询总的库存数量,用于减掉冻结的和当前要发的比较
			hql_kcsl_sum.append("select sum(YPSL) as KCSL from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd");
			StringBuffer hql_kcdj_delete_cfsb=new StringBuffer();//删除对应的库存冻结记录
			hql_kcdj_delete_cfsb.append("delete from YF_KCDJ where JLXH=:jlxh and YPXH=:ypxh and YPCD=:ypcd");
			for (int i = 0; i < body.size(); i++) {
				List<Map<String, Object>> list_kc_update = new ArrayList<Map<String, Object>>();// 需要更新的库存集合
				Map<String, Object> map_ypkc = body.get(i);
				if (map_ypkc.get("YPXH") == null || map_ypkc.get("YPCD") == null
					|| map_ypkc.get("YFSB") == null|| map_ypkc.get("YPDJ") == null
					|| map_ypkc.get("YPSL") == null) {
					return null;
				}
				long ypxh = MedicineUtils.parseLong(map_ypkc.get("YPXH"));
				long ypcd = MedicineUtils.parseLong(map_ypkc.get("YPCD"));
				yfsb = MedicineUtils.parseLong(map_ypkc.get("YFSB"));
				double ypsl = 0;
				if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
					ypsl = MedicineUtils.formatDouble(2,MedicineUtils.parseDouble(map_ypkc.get("YPSL"))
									* MedicineUtils.parseInt(map_ypkc.get("CFTS")));
				} else {
					ypsl = MedicineUtils.parseDouble(map_ypkc.get("YPSL"));
				}
				//库存冻结代码
				if(SFQYYFYFY==1){
					long jlxh=MedicineUtils.parseLong(map_ypkc.get("SBXH"));//有传就是发药,不传就是0
					double djsl=0;//冻结的总数量
					double kcsl=0;//总的库存数量
					Map<String,Object> map_par_kcdj=new HashMap<String,Object>();
					map_par_kcdj.put("ypxh", ypxh);
					map_par_kcdj.put("ypcd", ypcd);
					map_par_kcdj.put("yfsb", yfsb);
					map_par_kcdj.put("jlxh", jlxh);
					List<Map<String,Object>> list_kcdj=dao.doQuery(hql_kcdj.toString(), map_par_kcdj);
					map_par_kcdj.remove("jlxh");
					List<Map<String,Object>> list_kcsl=dao.doSqlQuery(hql_kcsl_sum.toString(), map_par_kcdj);
					if(list_kcsl!=null&&list_kcsl.size()>0&&list_kcsl.get(0)!=null){
						kcsl=MedicineUtils.parseDouble(list_kcsl.get(0).get("KCSL"));
					}
					if(list_kcdj!=null&&list_kcdj.size()>0){
						Map<String,Object> map_par_yfbz=new HashMap<String,Object>();
						map_par_yfbz.put("yfsb", yfsb);
						map_par_yfbz.put("ypxh", ypxh);
						Map<String,Object> map_yfbz=dao.doLoad(hql_yfbz.toString(), map_par_yfbz);
						int yfbz=MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
						for(Map<String,Object> map_kcdj:list_kcdj){
							djsl+=MedicineUtils.formatDouble(2, MedicineUtils.simpleMultiply(2, map_kcdj.get("YPSL"), map_kcdj.get("YFBZ"))/yfbz);
						}
					}
					if(kcsl-djsl<ypsl){//如果库存不够
						session.getTransaction().rollback();
						List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
						Map<String, Object> map_no = new HashMap<String, Object>();
						map_no.put("ypxh", ypxh);
						list_no.add(map_no);
						return list_no;
					}
					Map<String,Object> map_par_kcdj_delete=new HashMap<String,Object>();
					map_par_kcdj_delete.put("ypxh", ypxh);
					map_par_kcdj_delete.put("ypcd", ypcd);
					map_par_kcdj_delete.put("jlxh", jlxh);
					dao.doUpdate(hql_kcdj_delete_cfsb.toString(), map_par_kcdj_delete);
				}
				// 增加以下代码 用于增加站点发药减掉中心的库存
				if (map_ypkc.containsKey("SJFYBZ")
						&& MedicineUtils.parseInt(map_ypkc.get("SJFYBZ")) == 1) {
					yfsb = MedicineUtils.parseLong(map_ypkc.get("SJYF"));
				}
				double ypdj = MedicineUtils.parseDouble(map_ypkc.get("YPDJ"));

				StringBuffer hql_kc_xtjg = new StringBuffer();// 相同价格的药品库存查询
				hql_kc_xtjg.append("select YPSL as YPSL,SBXH as KCSB,JGID as JGID,YFSB as YFSB,YPXH as YPXH," +
						" YPCD as YPCD,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG ,PFJG as PFJG,JHJG as JHJG," +
						" YKJH as YKJH from YF_KCMX  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb " +
						" and LSJG=:ypdj and JYBZ!=1");
				if (list_order.size() > 0) {
					StringBuffer order = new StringBuffer();
					order.append(" order by ");
					for (String o : list_order) {
						order.append(o);
					}
					hql_kc_xtjg.append(order.toString());
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ypxh", ypxh);
				parameters.put("ypcd", ypcd);
				parameters.put("yfsb", yfsb);
				parameters.put("ypdj", ypdj);
				List<Map<String, Object>> list_kc_xtjg = dao.doSqlQuery(hql_kc_xtjg.toString(), parameters);
				// 有相同价格的库存
				if (list_kc_xtjg != null) {
					for (int j = 0; j < list_kc_xtjg.size(); j++) {
						Map<String, Object> map_kc_update = new HashMap<String, Object>();// 存要更新的库存
						Map<String, Object> map_kc_xtjg = list_kc_xtjg.get(j);
						double ypsl_kc_xtjg = MedicineUtils.parseDouble(map_kc_xtjg.get("YPSL"));// 相同价格的库存数量
						long kcsb_kc_xtjg = MedicineUtils.parseLong(map_kc_xtjg.get("KCSB"));// 相同价格的库存识别
						for (String key : map_ypkc.keySet()) {
							map_kc_xtjg.put(key, map_ypkc.get(key));
						}
						if (ypsl_kc_xtjg < ypsl) {
							map_kc_update.put("kcsb", kcsb_kc_xtjg);
							map_kc_update.put("ypsl", 0);
							list_kc_update.add(map_kc_update);
							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
								map_kc_xtjg.put("YPSL",MedicineUtils.formatDouble(2,ypsl_kc_xtjg
														/ MedicineUtils.parseInt(map_ypkc.get("CFTS"))));
							} else {
								map_kc_xtjg.put("YPSL", ypsl_kc_xtjg);
							}
							list_kc_update_ret.add(map_kc_xtjg);
							ypsl = ypsl - ypsl_kc_xtjg;
						} else {
							map_kc_update.put("kcsb", kcsb_kc_xtjg);
							map_kc_update.put("ypsl", ypsl_kc_xtjg - ypsl);
							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
								map_kc_xtjg.put("YPSL",MedicineUtils.formatDouble(2,ypsl
														/ MedicineUtils.parseInt(map_ypkc.get("CFTS"))));
							} else {
								map_kc_xtjg.put("YPSL", ypsl);
							}
							list_kc_update.add(map_kc_update);
							list_kc_update_ret.add(map_kc_xtjg);
							ypsl = 0;
							break;
						}

					}
				}
//				// 相同价格的药 数量不够
//				if (ypsl > 0) {
//					StringBuffer hql_kc_btjg = new StringBuffer();// 不同价格的药品库存查询
//					hql_kc_btjg
//							.append("select SBXH as KCSB,YPSL as YPSL,JGID as JGID,YFSB as YFSB,YPXH as YPXH,YPCD as YPCD,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG ,PFJG as PFJG,JHJG as JHJG,YKJH as YKJH from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and LSJG!=:ypdj and JYBZ!=1 ");
//					if (list_order.size() > 0) {
//						StringBuffer order = new StringBuffer();
//						order.append(" order by ");
//						for (String o : list_order) {
//							order.append(o);
//						}
//						hql_kc_btjg.append(order.toString());
//					}
//					List<Map<String, Object>> list_kc_btjg = dao.doQuery(
//							hql_kc_btjg.toString(), parameters);
//					if (list_kc_btjg == null) {
//						session.getTransaction().rollback();
//						List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
//						Map<String, Object> map_no = new HashMap<String, Object>();
//						map_no.put("ypxh", ypxh);
//						list_no.add(map_no);
//						return list_no;
//						//return null;
//					}
//					for (int k = 0; k < list_kc_btjg.size(); k++) {
//						Map<String, Object> map_kc_btjg = list_kc_btjg.get(k);
//						double ypsl_kc_btjg = MedicineUtils
//								.parseDouble(map_kc_btjg.get("YPSL"));// 不同价格的库存数量
//						long kcsb_kc_btjg = MedicineUtils.parseLong(map_kc_btjg
//								.get("KCSB"));// 不同价格的库存识别
//						Map<String, Object> map_kc_btjg_update = new HashMap<String, Object>();// 存要更新的库存
//						for (String key : map_ypkc.keySet()) {
//							map_kc_btjg.put(key, map_ypkc.get(key));
//						}
//						// 如果这个库存小于要减的ypsl,该库存数量变0,要减的数量减去这个库存数量
//						if (ypsl_kc_btjg < ypsl) {
//							map_kc_btjg_update.put("kcsb", kcsb_kc_btjg);
//							map_kc_btjg_update.put("ypsl", 0);
//							list_kc_update.add(map_kc_btjg_update);
//							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
//								map_kc_btjg
//										.put("YPSL",
//												MedicineUtils
//														.formatDouble(
//																2,
//																ypsl_kc_btjg
//																		/ MedicineUtils
//																				.parseInt(map_ypkc
//																						.get("CFTS"))));
//							} else {
//								map_kc_btjg.put("YPSL", ypsl_kc_btjg);
//							}
//							list_kc_update_ret.add(map_kc_btjg);
//							ypsl = ypsl - ypsl_kc_btjg;
//						} else {
//							map_kc_btjg_update.put("kcsb", kcsb_kc_btjg);
//							map_kc_btjg_update.put("ypsl", ypsl_kc_btjg - ypsl);
//							list_kc_update.add(map_kc_btjg_update);
//							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
//								map_kc_btjg
//										.put("YPSL",
//												MedicineUtils.formatDouble(
//														2,
//														ypsl
//																/ MedicineUtils
//																		.parseInt(map_ypkc
//																				.get("CFTS"))));
//							} else {
//								map_kc_btjg.put("YPSL", ypsl);
//							}
//							list_kc_update_ret.add(map_kc_btjg);
//							ypsl = 0;
//							break;
//						}
//					}
//				}
				// 相同价格库存不够
				if (ypsl > 0) {
					session.getTransaction().rollback();
					List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
					Map<String, Object> map_no = new HashMap<String, Object>();
					map_no.put("ypxh", ypxh);
					list_no.add(map_no);
					return list_no;
				}
				StringBuffer hql_kc_update = new StringBuffer();// 库存更新
				hql_kc_update.append("update YF_KCMX  set YPSL=:ypsl,JHJE=JHJG*:ypsl,PFJE=PFJG*:ypsl,LSJE=LSJG*:ypsl where SBXH=:sbxh");
				for (int x = 0; x < list_kc_update.size(); x++) {
					Map<String, Object> map_kc_update = list_kc_update.get(x);
					Map<String, Object> parameter = new HashMap<String, Object>();
					parameter.put("ypsl", MedicineUtils.parseDouble(map_kc_update.get("ypsl")));
					parameter.put("sbxh", map_kc_update.get("kcsb"));
					dao.doUpdate(hql_kc_update.toString(), parameter);
				}
			}
			Map<String, Object> map_kcsl_par = new HashMap<String, Object>();
			map_kcsl_par.put("yfsb", yfsb);
			StringBuffer sql_kc_ls_insert = new StringBuffer();
			sql_kc_ls_insert.append("insert into YF_KCMX_LS  select * from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doSqlUpdate(sql_kc_ls_insert.toString(), map_kcsl_par);
			StringBuffer hql_kc_delete = new StringBuffer();
			hql_kc_delete.append("delete from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doUpdate(hql_kc_delete.toString(), map_kcsl_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "减库存失败", e);
		}
		return list_kc_update_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 待发药记录查询
	 * @updateInfo
	 * @param cnds
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryDispensing(List<Object> cnds, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		long yfsb = MedicineUtils.parseLong(UserRoleToken.getCurrent()
				.getProperty("pharmacyId"));// 用户的药房识别
		List<Object> list_cnd_yfsb = CNDHelper.createArrayCnd("or",
				CNDHelper.createSimpleCnd("eq", "a.YFSB", "d", yfsb),
				CNDHelper.createSimpleCnd("eq", "a.SJYF", "d", yfsb));// update
																	// for 站点
		List<Object> list_cnds = CNDHelper.createArrayCnd("and", list_cnd_yfsb,
				cnds);
		StringBuffer hql_fylb = new StringBuffer();
		StringBuffer hql_where = new StringBuffer();
		List<Map<String, Object>> list_fylb = null;
		try {
			/*************modify by zhaojian 2017-05-18 修改直接发药模块病人列表按收费时间排序，增加MS_MZXX表的关联查询*****************/
			hql_where.append(" a.BRID=b.BRID and a.FPHM=c.FPHM and a.KFRQ >= sysdate - 120 and").append(
					ExpressionProcessor.instance().toString(list_cnds));
			hql_fylb.append(
					"select a.CFSB as CFSB,a.FPHM as FPHM,b.BRXM as BRXM,a.YFSB as YFSB,a.CFHM as CFHM,a.CFLX as CFLX,a.SJJG as SJJG,a.SJYF as SJYF,a.SJFYBZ as SJFYBZ from MS_CF01 a,MS_BRDA b,MS_MZXX c where ")
					.append(hql_where.toString()).append(" order by c.SFRQ");
			long l = dao.doCount(" MS_CF01 a,MS_BRDA b,MS_MZXX c", hql_where.toString(),
					null);
			Map<String, Object> parameters = new HashMap<String, Object>();
			list_fylb = dao.doQuery(hql_fylb.toString(), parameters);
			map_ret.put("totalCount", l);
			map_ret.put("body", list_fylb);
		} catch (ExpException e1) {
			MedicineUtils.throwsException(logger, "待发药列表查询失败", e1);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "待发药列表查询失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 处方信息查询(发药)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPrescribingInformation(
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> ret = null;
		try {
			Long cfsb = MedicineUtils.parseLong(body.get("cfsb"));
			StringBuffer hql = new StringBuffer();
			hql.append("select b.BRXM as BRXM,c.PERSONNAME as YSXM,a.YSDM as YSDM,a.BRID as BRID,a.KFRQ as KFRQ,a.CFHM as CFHM,a.PYGH as PYGH ,a.FPHM as FPHM,a.FYGH as FYGH,a.CFSB as CFSB,a.FYBZ as FYBZ,a.FYRQ as FYRQ,a.CFTS as CFTS from MS_CF01 a,MS_BRDA b,SYS_Personnel c  where a.BRID=b.BRID and a.YSDM=c.PERSONID and a.CFSB=:cfsb ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cfsb", cfsb);
			ret = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取处方信息失败", e);
		}
		return ret;
	}


	/**
	 * 
	 * @author zhaojian
	 * @createDate 2017-05-31
	 * @description 处方明细信息查询(发药)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPrescribingDetailInformation(
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			Long cfsb = MedicineUtils.parseLong(body.get("cfsb"));
			String jgid = user.getManageUnit().getId();
			long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
			StringBuffer hql = new StringBuffer();
//			hql.append("select b.YPMC as YPMC,a.SBXH as SBXH,a.CFSB as CFSB,a.YFGG as YFGG,a.YPXH as YPXH,a.YFDW as YFDW,a.YPSL as YPSL,a.YPDJ as YPDJ,a.HJJE as HJJE,a.CFTS as CFTS,a.YCJL as YCJL,a.YYTS as YYTS,a.GYTJ as GYTJ,c.CDMC as CDMC,a.YPCD as YPCD,a.ZFYP as ZFYP,d.YPSL as KFSL,e.XMMC as XMMC from MS_CF02 a,YK_TYPK b,YK_CDDZ c,YF_KCMX d, ZY_YPYF e where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.YPXH=d.YPXH and a.YPCD=d.YPCD and a.GYTJ = e.YPYF and a.CFSB=:cfsb and a.JGID=:jgid and d.YFSB=:yfsb ");
			hql.append("select a.YPMC as YPMC,a.SBXH as SBXH,a.CFSB as CFSB,a.YFGG as YFGG,a.YPXH as YPXH,a.YFDW as YFDW,a.YPSL as YPSL,");
			hql.append(" a.YPDJ as YPDJ,a.HJJE as HJJE,a.CFTS as CFTS,a.YCJL as YCJL,a.YYTS as YYTS,a.GYTJ as GYTJ,a.CDMC as CDMC,a.YPCD as YPCD,");
			hql.append(" a.ZFYP as ZFYP,nvl(d.YPSL,0) as KFSL,a.XMMC as XMMC from (select b.YPMC as YPMC,a.SBXH as SBXH,a.CFSB as CFSB,a.YFGG as YFGG,a.YPXH as YPXH,");
			hql.append(" a.YFDW as YFDW,a.YPSL as YPSL,a.YPDJ as YPDJ,a.HJJE as HJJE,a.CFTS as CFTS,a.YCJL as YCJL,a.YYTS as YYTS,a.GYTJ as GYTJ,c.CDMC as CDMC,");
			hql.append(" a.YPCD as YPCD,a.ZFYP as ZFYP,e.XMMC as XMMC from MS_CF02 a, YK_TYPK b, YK_CDDZ c, ZY_YPYF e");
			hql.append(" where a.YPXH = b.YPXH and a.YPCD = c.YPCD and a.GYTJ = e.YPYF and a.CFSB = :cfsb and a.JGID = :jgid) a");
			hql.append(" left join YF_KCMX d on a.YPXH = d.YPXH and a.YPCD = d.YPCD and d.YFSB = :yfsb");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cfsb", cfsb);
			parameters.put("jgid", user.getManageUnit()
					.getId());
			parameters.put("yfsb",
					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			list = dao.doSqlQuery(hql.toString(), parameters);
			//map_ret.put("body", list);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取处方明细信息失败", e);
		}
		return list;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 药房自动刷新参数查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPharmacyAutoRefresh(Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		int cksx_kcsl_yf=20;
		try{
		cksx_kcsl_yf = MedicineUtils.parseInt(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.MZFYZDSXMS, ctx));// 发药自动刷新时间
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.MZFYZDSXMS, e);
		}
		map_ret.put("MZFYZDSXMS", cksx_kcsl_yf);
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 获取当前窗口信息
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadPharmacyWindowInfo(Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JSJM", (String) ctx.get(Context.CLIENT_IP_ADDRESS));
		parameters.put("JGID", UserRoleToken.getCurrent().getManageUnit()
				.getId());
		parameters.put(
				"YFSB",
				MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty(
						"pharmacyId")));
		try {
			List<Map<String, Object>> list = dao
					.doQuery(
							"select CKBH as CKBH,CKMC as CKMC,QYPB as QYPB from YF_CKBH  where JSJM=:JSJM and JGID=:JGID and YFSB=:YFSB",
							parameters);
			if (list == null || list.size() == 0) {
				throw new ModelDataOperationException("未找到有效的发药窗口设置，请联系管理员维护!");
			}
			return list.get(0);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取发药窗口信息失败!", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 更新发药窗口状态
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSavePharmacyWindowStatus(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("CKBH", body.get("CKBH"));
			parameters.put("QYPB", body.get("QYPB"));
			parameters.put("JGID", manageUnit);
			parameters.put("YFSB",
					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			dao.doUpdate(
					"update YF_CKBH set QYPB=:QYPB where YFSB=:YFSB and JGID=:JGID and CKBH=:CKBH",
					parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新发药窗口状态失败!", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 取消发药保存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveBackMedicine(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Long cfsb = MedicineUtils.parseLong(body.get("CFSB"));
		List<Object> cnd_cfsb = CNDHelper.createSimpleCnd("eq", "CFSB", "s",
				cfsb);
		List<Object> cnd_tygl = CNDHelper.createSimpleCnd("le", "TYGL", "i", 0);// 不查出退药记录
		List<Object> cnds = CNDHelper.createArrayCnd("and", cnd_cfsb, cnd_tygl);
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = MedicineUtils.parseString(user.getUserId());
		StringBuffer hql_jg = new StringBuffer();// 查询药品的价格(主要用于发完药库存变为0再调价的情况,因为调价不更新yf_kcmx_ls表)
		hql_jg.append("select distinct round(a.LSJG*(b.YFBZ/c.ZXBZ),4) as LSJG,round(a.PFJG*(b.YFBZ/c.ZXBZ),4) as PFJG,round(a.JHJG*(b.YFBZ/c.ZXBZ),4) as JHJG from YK_CDXX  a,YF_YPXX b,YK_TYPK c  where a.YPXH=:ypxh and a.YPCD=:ypcd and a.JGID=:jgid and b.YFSB=:yfsb and a.JGID=b.JGID and a.YPXH=b.YPXH and a.YPXH=c.YPXH");
		StringBuffer hql_kcls_delete = new StringBuffer();// 用于删除库存历史
		hql_kcls_delete.append("delete from YF_KCMX_LS  where SBXH=:kcsb");
		StringBuffer hql_kcmx_insert = new StringBuffer();// 用于新增库存(库存为0)
		hql_kcmx_insert
				.append("insert into YF_KCMX  select * from YF_KCMX_LS where SBXH=:kcsb");
		StringBuffer hql_yksb = new StringBuffer();
		hql_yksb.append("select YKSB as YKSB from YK_YPXX where JGID=:jgid and YPXH=:ypxh");
		try {
			Map<String,Object> map_cf01=dao.doLoad(BSPHISEntryNames.MS_CF01, cfsb);
			if(MedicineUtils.parseInt(map_cf01.get("SJFYBZ"))==1){
				yfsb=MedicineUtils.parseLong(map_cf01.get("SJYF"));
				jgid=MedicineUtils.parseString(map_cf01.get("SJJG"));
			}
			List<Map<String, Object>> list_fymx = dao.doList(cnds, null,
					BSPHISEntryNames.YF_MZFYMX);// 发药明细
			for (int i = 0; i < list_fymx.size(); i++) {
				Map<String, Object> map_fymx = list_fymx.get(i);
				map_fymx.put("TYGL", 1l);
				dao.doSave("update", BSPHISEntryNames.YF_MZFYMX, map_fymx,
						false);
				long kcsb = MedicineUtils.parseLong(map_fymx.get("KCSB"));
				long ypxh = MedicineUtils.parseLong(map_fymx.get("YPXH"));
				long ypcd = MedicineUtils.parseLong(map_fymx.get("YPCD"));
				Map<String, Object> map_par_jg = new HashMap<String, Object>();
				map_par_jg.put("jgid", jgid);
				map_par_jg.put("ypxh", ypxh);
				map_par_jg.put("ypcd", ypcd);
				map_par_jg.put("yfsb", yfsb);
				Map<String, Object> map_jg = dao.doLoad(hql_jg.toString(),
						map_par_jg);
				// dao.doSave("update", BSPHISEntryNames.YF_MZFYMX, map_fymx,
				// false);
				Map<String, Object> map_kc = dao.doLoad(
						BSPHISEntryNames.YF_KCMX, kcsb);
				// 处理库存
				if (map_kc == null) {
					Map<String, Object> map_par_ls = new HashMap<String, Object>();
					map_par_ls.put("kcsb", kcsb);
					dao.doSqlUpdate(hql_kcmx_insert.toString(), map_par_ls);// 从历史表新增库存
					map_kc = dao.doLoad(BSPHISEntryNames.YF_KCMX, kcsb);
					map_kc.put("YPSL",
							MedicineUtils.parseDouble(map_fymx.get("YPSL")));
					map_kc.put("LSJG",
							MedicineUtils.parseDouble(map_jg.get("LSJG")));
					map_kc.put("JHJG",
							MedicineUtils.parseDouble(map_jg.get("JHJG")));
					map_kc.put("PFJG",
							MedicineUtils.parseDouble(map_jg.get("PFJG")));
					map_kc.put(
							"LSJE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_jg.get("LSJG")))
									* (MedicineUtils.parseDouble(map_fymx
											.get("YPSL"))));// 零售金额
					map_kc.put(
							"JHJE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_jg.get("JHJG")))
									* (MedicineUtils.parseDouble(map_fymx
											.get("YPSL"))));// 进货金额
					map_kc.put(
							"PFJE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_jg.get("PFJG")))
									* (MedicineUtils.parseDouble(map_fymx
											.get("YPSL"))));// 批发金额
					dao.doSave("update", BSPHISEntryNames.YF_KCMX, map_kc,
							false);// 更新库存的金额和价格
					dao.doUpdate(hql_kcls_delete.toString(), map_par_ls);
				} else {
					map_kc.put("YPSL", (Double) map_fymx.get("YPSL")
							+ (Double) map_kc.get("YPSL"));
					map_kc.put(
							"LSJE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_kc.get("LSJG")))
									* (MedicineUtils.parseDouble(map_kc
											.get("YPSL"))));// 零售金额
					map_kc.put(
							"JHJE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_kc.get("JHJG")))
									* (MedicineUtils.parseDouble(map_kc
											.get("YPSL"))));// 进货金额
					map_kc.put(
							"PFJE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_kc.get("PFJG")))
									* (MedicineUtils.parseDouble(map_kc
											.get("YPSL"))));// 批发金额
					dao.doSave("update", BSPHISEntryNames.YF_KCMX, map_kc,
							false);// 库存增加
				}
				// 处理调价
				if (!((Double) map_jg.get("LSJG")).equals(MedicineUtils
						.parseDouble(map_fymx.get("LSJG")))) {
					Map<String, Object> map_par_yksb = new HashMap<String, Object>();
					map_par_yksb.put("jgid", jgid);
					map_par_yksb.put("ypxh", ypxh);
					Map<String, Object> map_yksb = dao.doLoad(
							hql_yksb.toString(), map_par_yksb);
					if (map_yksb == null || map_yksb.size() == 0) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "查询药品药库信息失败");
					}
					map_fymx.put("YKSB",
							MedicineUtils.parseLong(map_yksb.get("YKSB")));// 药库识别
					map_fymx.put("CKBH", 0);// 窗口编号先默认保存为0
					map_fymx.put("TJFS", 1);// 调价方式先默认保存为1
					map_fymx.put("TJDH", 0);// 调价单号先默认保存为0
					map_fymx.put("TJRQ", new Date());// 调价日期
					map_fymx.put("TJSL", map_fymx.get("YPSL"));// 调价数量
					map_fymx.put("XLSJ", map_jg.get("LSJG"));// 新零售价
					map_fymx.put("XPFJ", map_jg.get("PFJG"));// 新批发价
					map_fymx.put("YLSJ", map_fymx.get("LSJG"));// 原零售价
					map_fymx.put("YPFJ", map_fymx.get("PFJG"));// 原批发价
					map_fymx.put("YJHJ", map_fymx.get("JHJG"));// 原进货价
					map_fymx.put("XJHJ", map_kc.get("JHJG"));// 原进货价
					map_fymx.put("CZGH", userId);// 操作工号
					map_fymx.put("TJWH", "门诊退药");
					map_fymx.put(
							"YLSE",
							MedicineUtils.formatDouble(4, MedicineUtils
									.parseDouble(map_fymx.get("LSJG")))
									* ((Double) MedicineUtils
											.parseDouble(map_fymx.get("YPSL"))));// 原零售价
					map_fymx.put("YPFE", MedicineUtils
							.formatDouble(
									4,
									(MedicineUtils.parseDouble(map_fymx
											.get("PFJG")))
											* ((Double) MedicineUtils
													.parseDouble(map_fymx
															.get("YPSL")))));// 原批发金额
					map_fymx.put("YJHE", MedicineUtils
							.formatDouble(
									4,
									(MedicineUtils.parseDouble(map_fymx
											.get("JHJG")))
											* ((Double) MedicineUtils
													.parseDouble(map_fymx
															.get("YPSL")))));// 原进货金额
					map_fymx.put("XLSE", MedicineUtils
							.formatDouble(
									4,
									(MedicineUtils.parseDouble(map_jg
											.get("LSJG")))
											* ((Double) MedicineUtils
													.parseDouble(map_fymx
															.get("YPSL")))));// 新零售金额
					map_fymx.put("XPFE", MedicineUtils.formatDouble(
							4,
							(MedicineUtils.parseDouble(map_jg.get("PFJG")))
									* (MedicineUtils.parseDouble(map_fymx
											.get("YPSL")))));// 新批发金额
					map_fymx.put("XJHE", MedicineUtils.formatDouble(
							4,
							(MedicineUtils.parseDouble(map_jg.get("JHJG")))
									* (MedicineUtils.parseDouble(map_fymx
											.get("YPSL")))));// 新进货金额
					map_fymx.put("KCSB", map_kc.get("SBXH"));// 库存识别
					map_fymx.put("KCSL", map_fymx.get("YPSL"));// 库存数量
					dao.doSave("create", BSPHISEntryNames.YF_TJJL, map_fymx,
							false);
				}
				// 新增发药明细负记录
				map_fymx.put("YPSL",
						-MedicineUtils.parseDouble(map_fymx.get("YPSL")));
				map_fymx.put("PFJE",
						-MedicineUtils.parseDouble(map_fymx.get("PFJE")));
				map_fymx.put("JHJE",
						-MedicineUtils.parseDouble(map_fymx.get("JHJE")));
				map_fymx.put("LSJE",
						-MedicineUtils.parseDouble(map_fymx.get("LSJE")));
				map_fymx.put("HJJE",
						-MedicineUtils.parseDouble(map_fymx.get("HJJE")));
				map_fymx.put("FYRQ", new Date());
				map_fymx.put("TYGL", MedicineUtils.parseLong(map_fymx.get("JLXH")));
				dao.doSave("create", BSPHISEntryNames.YF_MZFYMX, map_fymx,
						false);// 保存发药明细负记录
			}
			// 更新处方发药标志
			StringBuffer hql_cf_update = new StringBuffer();
			hql_cf_update
					.append("update MS_CF01  set FYBZ=0 where CFSB=:cfsb ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cfsb", cfsb);
			dao.doUpdate(hql_cf_update.toString(), parameters);
			// 由于考虑到以后多窗口 故先更新窗口表.如果没有实现多窗口 以下代码没什么用
			StringBuffer sql_pdsl = new StringBuffer();
			int ckbh = 1;// 窗口编号 暂时写死 窗口切换做好后修改
			Map<String, Object> map_par_pdcf = new HashMap<String, Object>();
			map_par_pdcf.put("ckbh", ckbh);
			map_par_pdcf.put("yfsb", yfsb);
			sql_pdsl.append("update YF_CKBH  set PDCF=PDCF+1 where CKBH=:ckbh and YFSB=:yfsb");
			dao.doSqlUpdate(sql_pdsl.toString(), map_par_pdcf);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "取消发药失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "明细保存验证失败", e);
		}
	}
	//部分退药保存
	@SuppressWarnings("unchecked")
	public void saveBacPartkMedicine(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		saveBackMedicine(body,ctx);//先全部取消发药
		try {
			long cfsb=MedicineUtils.parseLong(body.get("CFSB"));
		if(body.containsKey("QBTY")&&MedicineUtils.parseInt(body.get("QBTY"))==1){
			StringBuffer hql_cf_update = new StringBuffer();
			hql_cf_update.append("update MS_CF01  set FYBZ=3 where CFSB=:cfsb ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cfsb", cfsb);
			dao.doUpdate(hql_cf_update.toString(), parameters);
			return ;
		}
		Map<String,Object> map_tymx=(Map<String,Object>)body.get("TYMX");
		List<Object> cnd_cfsb = CNDHelper.createSimpleCnd("eq", "CFSB", "s",
				cfsb);
		Map<String,Object> map_cf01=dao.doLoad(BSPHISEntryNames.MS_CF01, cfsb);
		List<Map<String,Object>> list_cf02=dao.doList(cnd_cfsb, null, BSPHISEntryNames.MS_CF02);
		//增加新的CF01
		Map<String,Object> map_cf01_new=new HashMap<String,Object>();
		map_cf01_new.putAll(map_cf01);
		map_cf01_new.remove("CFSB");
		map_cf01_new.remove("MZXH");
		map_cf01_new.put("FPHM", "退药");
		map_cf01_new.put("FYBZ", 0);
		map_cf01_new.put("DJLY", 3);
		map_cf01_new.put("CFGL", cfsb);
		Map<String,Object> map_cfsb=dao.doSave("create", BSPHISEntryNames.MS_CF01, map_cf01_new, false);
		long cfsb_new=MedicineUtils.parseLong(map_cfsb.get("CFSB"));
		StringBuffer hql_cf01_new_update=new StringBuffer();//更新cf01的处方号码
		hql_cf01_new_update.append("update MS_CF01 set CFHM=:cfhm,CFGL=:oldcfsb where CFSB=:cfsb");
		Map<String,Object> map_par_cf01_new_update=new HashMap<String,Object>();
		map_par_cf01_new_update.put("cfsb", cfsb_new);
		map_par_cf01_new_update.put("cfhm", cfsb_new+"");
		map_par_cf01_new_update.put("oldcfsb", cfsb);
		dao.doUpdate(hql_cf01_new_update.toString(), map_par_cf01_new_update);
		//增加新的CF02
		int i=0;//用于判断有没新增的非自备药的处方
		for(Map<String,Object> map_cf02:list_cf02){
			Map<String,Object> map_cf02_new =new HashMap<String,Object>();
			map_cf02_new.putAll(map_cf02);
			map_cf02_new.remove("SBXH");
			map_cf02_new.put("CFSB", cfsb_new);
			double tysl=0;
			if(map_tymx.containsKey(MedicineUtils.parseString(map_cf02.get("SBXH")))){
				 tysl=MedicineUtils.parseDouble(map_tymx.get(MedicineUtils.parseString(map_cf02.get("SBXH"))));
				if(tysl==MedicineUtils.parseDouble(map_cf02.get("YPSL"))){//如果全退,则不新增新的处方明细
					continue;
				}
			}
				map_cf02_new.put("YPSL", MedicineUtils.parseDouble(map_cf02.get("YPSL"))-tysl);
				map_cf02_new.put("HJJE",MedicineUtils.simpleMultiply(2, MedicineUtils.simpleMultiply(4, map_cf02_new.get("YPSL"), map_cf02.get("CFTS")), map_cf02.get("YPDJ")));
				if(MedicineUtils.parseInt(map_cf02_new.get("ZFYP"))!=1){
					i++;
				}
				dao.doSave("create", BSPHISEntryNames.MS_CF02, map_cf02_new, false);
			
		}
//		//新增作废的CF01
//		Map<String,Object> map_cf01_new=new HashMap<String,Object>();
//		map_cf01_new.putAll(map_cf01);
//		map_cf01_new.remove("CFSB");
//		map_cf01_new.put("ZFPB", 1);
//		map_cf01_new.put("CFGL", cfsb);
//		Map<String,Object> map_cfsb=dao.doSave("create", BSPHISEntryNames.MS_CF01, map_cf01_new, false);
//		long cfsb_new=MedicineUtils.parseLong(map_cfsb.get("CFSB"));
//		StringBuffer hql_cf01_update=new StringBuffer();//更新CF01的发药标志和发票号码
//		hql_cf01_update.append("update MS_CF01 set FYBZ=0,FPHM='退药' where CFSB=:cfsb");
//		Map<String,Object> map_par_cf01_update=new HashMap<String,Object>();
//		map_par_cf01_update.put("cfsb", cfsb);
//		dao.doUpdate(hql_cf01_update.toString(), map_par_cf01_update);
//		//新增作废的CF02 并且修改原CF02药品数量
//		StringBuffer hql_cf02_update=new StringBuffer();//更新处方02的数量
//		hql_cf02_update.append("update MS_CF02 set YPSL=:ypsl where SBXH=:sbxh");
//		StringBuffer hql_cf02_delete=new StringBuffer();//删除数量是0的处方明细
//		hql_cf02_delete.append("delete from MS_CF02 where SBXH=:sbxh");
//		for(Map<String,Object> map_cf02:list_cf02){
//			Map<String,Object> map_cf02_new =new HashMap<String,Object>();
//			map_cf02_new.putAll(map_cf02);
//			map_cf02_new.remove("SBXH");
//			map_cf02_new.put("CFSB", cfsb_new);
//			dao.doSave("create", BSPHISEntryNames.MS_CF02, map_cf02_new, false);
//			if(map_tymx.containsKey(MedicineUtils.parseString(map_cf02.get("SBXH")))){
//				double tysl=MedicineUtils.parseDouble(map_tymx.get(MedicineUtils.parseString(map_cf02.get("SBXH"))));
//				Map<String,Object> map_par_cf02=new HashMap<String,Object>();
//				map_par_cf02.put("sbxh", MedicineUtils.parseLong(map_cf02.get("SBXH")));
//				if(tysl==MedicineUtils.parseDouble(map_cf02.get("YPSL"))){//如果全退,删除明细
//					dao.doUpdate(hql_cf02_delete.toString(), map_par_cf02);
//				}else{
//					map_par_cf02.put("ypsl", MedicineUtils.parseDouble(map_cf02.get("YPSL"))-tysl);
//					dao.doUpdate(hql_cf02_update.toString(), map_par_cf02);
//				}
//			}
//		}
		//update by caijy for zw2要求 yf_mzfymx只能要药房模块新增记录.
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String uid = user.getUserId() + "";
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("cfsb", cfsb_new);
//		map.put("fygh", uid);
//		Map<String, Object> m =saveDispensing(map, ctx);
		//新处方发药
		if(i!=0){
			Map<String,Object> map_fy=new HashMap<String,Object>();
			map_fy.put("cfsb", cfsb_new);
			map_fy.put("fygh",MedicineUtils.parseString(map_cf01.get("FYGH")));
			Map<String, Object> m =saveDispensing(map_fy,ctx);
			if (m.containsKey("code")) {
				if (Integer.parseInt(m.get("code") + "") > 300) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							m.get("msg") + "");
				}
			}
		}
		StringBuffer hql_cf01_fphm=new StringBuffer();//删掉新处方的发票号码,防止退费时不能收费
		hql_cf01_fphm.append("update MS_CF01 set FPHM='',FYBZ=1 where CFSB=:cfsb");
		Map<String,Object> map_par_fphm=new HashMap<String,Object>();
		map_par_fphm.put("cfsb", cfsb_new);
		dao.doUpdate(hql_cf01_fphm.toString(), map_par_fphm);
		StringBuffer hql_cf01_update=new StringBuffer();//更新处方01表的退药说明
		hql_cf01_update.append("update MS_CF01 set ZFPB=1,ZFSJ=:date,FYBZ=3 ");
		Map<String,Object> map_par_cf01_update=new HashMap<String,Object>();
		map_par_cf01_update.put("cfsb", cfsb);
		map_par_cf01_update.put("date", new Date());
		if(body.containsKey("TYSM")&&!"".equals(MedicineUtils.parseString(body.get("TYSM")))){
			hql_cf01_update.append(",TYSM=:tysm");
			map_par_cf01_update.put("tysm", MedicineUtils.parseString(body.get("TYSM")));
		}
		hql_cf01_update.append(" where CFSB=:cfsb");
		dao.doSqlUpdate(hql_cf01_update.toString(), map_par_cf01_update);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "部分退药失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "部分退药失败", e);
		} 
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 处方信息查询(取消发药)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryBackMedicinePrescribingInformation(
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> ret = null;
		try {
			Long cfsb = MedicineUtils.parseLong(body.get("cfsb"));
			StringBuffer hql = new StringBuffer();
			hql.append("select a.FYCK as FYCK,b.BRXM as BRXM,c.PERSONNAME as YSXM,a.YSDM as YSDM,a.BRID as BRID,a.KFRQ as KFRQ,a.CFHM as CFHM,a.PYGH as PYGH ,a.FPHM as FPHM,a.FYGH as FYGH,a.CFSB as CFSB,a.FYBZ as FYBZ,d.PERSONNAME as FYR,a.FYRQ as FYRQ,a.CFTS as CFTS from MS_CF01 a,MS_BRDA b,SYS_Personnel c,SYS_Personnel d where a.BRID=b.BRID and a.YSDM=c.PERSONID and a.CFSB=:cfsb and a.FYGH=d.PERSONID");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cfsb", cfsb);
			ret = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取处方信息失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 处方信息list查询(取消发药)
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPharmacyDispensingDetail(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		StringBuffer hql = new StringBuffer();
		hql.append("select b.YPMC as YPMC,a.SBXH as SBXH,a.CFSB as CFSB,a.YFGG as YFGG,a.YPXH as YPXH,a.YFDW as YFDW,a.YPSL as YPSL,a.YPDJ as YPDJ,a.HJJE as HJJE,a.CFTS as CFTS,a.YCJL as YCJL,a.GYTJ as GYTJ,a.YYTS as YYTS,c.CDMC as CDMC,a.YPCD as YPCD from MS_CF02 a,YK_TYPK b,YK_CDDZ c,MS_CF01 d  where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.CFSB=:cfsb and a.CFSB=d.CFSB");
		StringBuffer hql_kcsl = new StringBuffer();
		hql_kcsl.append("select sum(YPSL) as KCSL from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and JYBZ!=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("cfsb", MedicineUtils.parseLong(body.get("CFSB")));
		List<Map<String, Object>> list_cfmx = null;
		try {
			list_cfmx = dao.doSqlQuery(hql.toString(), map_par);
			for (Map<String, Object> map_cfmx : list_cfmx) {
				Map<String, Object> map_par_kcsl = new HashMap<String, Object>();
				map_par_kcsl.put("ypxh",
						MedicineUtils.parseLong(map_cfmx.get("YPXH")));
				map_par_kcsl.put("ypcd",
						MedicineUtils.parseLong(map_cfmx.get("YPCD")));
				map_par_kcsl.put("yfsb", yfsb);
				List<Map<String, Object>> list_kcsl = dao.doSqlQuery(
						hql_kcsl.toString(), map_par_kcsl);
				if (list_kcsl != null && list_kcsl.size() > 0
						&& list_kcsl.get(0) != null) {
					map_cfmx.put(
							"KCSL",
							MedicineUtils.parseDouble(list_kcsl.get(0).get(
									"KCSL")));
				} else {
					map_cfmx.put("KCSL", 0);
				}
				SchemaUtil.setDictionaryMassageForForm(map_cfmx,
						"phis.application.pha.schemas.MS_CF02_YFFY_LIST");
			}

		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "处方信息查询失败!", e);
		}
		return list_cfmx;

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 获取当前窗口信息
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadPharmacyWindowInfo(Context ctx)
			throws ModelDataOperationException {
		String clientIp = (String) ctx.get(Context.CLIENT_IP_ADDRESS);
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JSJM", clientIp);
		parameters.put("JGID", manageUnit);
		parameters.put("YFSB",
				MedicineUtils.parseLong(user.getProperty("pharmacyId")));
		try {
			List<Map<String, Object>> list = dao
					.doQuery(
							"select CKBH as CKBH,CKMC as CKMC,QYPB as QYPB from YF_CKBH  where JSJM=:JSJM and JGID=:JGID and YFSB=:YFSB",
							parameters);
			if (list == null || list.size() == 0) {
				throw new ModelDataOperationException("未找到有效的发药窗口设置，请联系管理员维护!");
			}
			return list.get(0);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取发药窗口信息失败!", e);
		}
	}
}
