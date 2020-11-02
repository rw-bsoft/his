package phis.application.fsb.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.hph.source.HospitalPharmacyBackMedicineModel;
import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyInventoryManageModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class FamilySickBedPharmacyAccountingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedPharmacyAccountingModel.class);

	public FamilySickBedPharmacyAccountingModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-14
	 * @description 家床记账-病人信息查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadJcxx(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> ret=MedicineUtils.getRetMap();
		String hm=MedicineUtils.parseString(body.get("HM"));//住院号码或者床号
		String manaUnitId = UserRoleToken.getCurrent().getManageUnit().getId();
		StringBuffer hql=new StringBuffer();
		hql.append("select ZYH as ZYH,ZYHM as ZYHM,BRXM as BRXM,BRXB as BRXB,BRXZ as BRXZ from JC_BRRY where JGID=:jgid and CYPB=0 ");
			hm=BSPHISUtil.get_public_fillleft(hm, "0",
					BSPHISUtil.getRydjNo(manaUnitId, "ZYHM", "", dao).length());
			hql.append(" and ZYHM=:hm");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
		map_par.put("hm", hm);
		try {
			Map<String,Object> map_body=dao.doLoad(hql.toString(), map_par);
			if(map_body==null||map_body.size()==0){
				return MedicineUtils.getRetMap("未查到病人在院信息");
			}
			SchemaUtil.setDictionaryMassageForForm(map_body, "phis.application.fsb.schemas.JC_YPJZ_FORM");
			ret.put("body", map_body);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "病人信息查询失败", e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-14
	 * @description 家床记账保存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveFamilySickBedPharmacyAccounting(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user=UserRoleToken.getCurrent();
		String jgid=user.getManageUnit().getId();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));;
		String userid =user.getUserId();
		Date d=new Date();
		Map<String,Object> brxx=(Map<String,Object>)body.get("d01");
		List<Map<String,Object>> list_fymx=(List<Map<String,Object>>)body.get("d02");
		int yplx=MedicineUtils.parseInt(body.get("yplx"));
		try{
			Map<String, Object> map_jc_fyjl_data = new HashMap<String, Object>();// yf_fyjl保存记录
			map_jc_fyjl_data.put("JGID", jgid);
			map_jc_fyjl_data.put("FYSJ", d);
			map_jc_fyjl_data.put("FYGH", userid);
			map_jc_fyjl_data.put("FYLX", 1);
			map_jc_fyjl_data.put("JGID", jgid);
			map_jc_fyjl_data.put("YFSB", yfsb);
			map_jc_fyjl_data.put("FYFS", 6);
			map_jc_fyjl_data.put("DYPB", 0);
			map_jc_fyjl_data.put("FYLX", 1);
			map_jc_fyjl_data = dao.doSave("create", "phis.application.fsb.schemas.JC_FYJL",
					map_jc_fyjl_data, false);
			StringBuffer hql_yfkc=new StringBuffer();//查询库存包装
			hql_yfkc.append("select YFBZ as YFBZ from YF_KCMX where SBXH=:sbxh");
			StringBuffer hql_fyzl=new StringBuffer();//退药时查询发药总量
			hql_fyzl.append("select sum(a.YPSL) as FYSL from YF_JCFYMX a,JC_FYMX b  where a.JFID=b.JLXH and b.ZYH=:zyh and a.KCSB=:kcsb and b.XMLX=2 and b.YZXH is null");
			StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
			Schema sc = null;
			try {
				sc = SchemaController.instance().get("phis.application.fsb.schemas.YF_JCFYMX");
			} catch (ControllerException e) {
				throw new PersistentDataOperationException(e);
			}
			List<SchemaItem> items = sc.getItems();
			StringBuffer hql_yffymx=new StringBuffer();
			hql_yffymx.append("select ");
			int tag=0;
			for(SchemaItem it:items){
				if(tag>0){
					hql_yffymx.append(",");
				}
				hql_yffymx.append("a.").append(it.getId()).append(" as ").append(it.getId());
				tag++;
			}
			hql_yffymx.append(" from YF_JCFYMX a,JC_FYMX b where a.JFID=b.JLXH and b.ZYH=:zyh and a.KCSB=:kcsb and b.XMLX=2 and b.YZXH is null and a.YPSL>0");
			
//			List<Map<String,Object>> list_kc_temp=new ArrayList<Map<String,Object>>();
//			for(Map<String,Object> map_fymx:list_fymx){
//				if(MedicineUtils.parseDouble(map_fymx.get("YPSL"))<0){
//					continue;
//				}
//				double ypsl=MedicineUtils.simpleMultiply(2, map_fymx.get("YPSL"), brxx.get("CFTS"));
//				Map<String,Object> map_kc=new HashMap<String,Object>();
//				map_kc.putAll(map_fymx);
//				map_kc.put("YPSL", ypsl);
//				//map_kc.put("KCSB", map_fymx.get("KCSB"));
//				list_kc_temp.add(map_kc);
//			}
//			Map<String,Object> map_jkc=model.lessInventory(list_kc_temp, ctx);
//			if(map_jkc.containsKey("ypmc")&&!"".equals(MedicineUtils.parseString(map_jkc.get("ypmc")))){
//				throw new ModelDataOperationException("药品["+map_jkc.get("ypmc")+"]库存不够");
//			}
			for(Map<String,Object> map_fymx:list_fymx){
				Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
				map_par_yfbz.put("ypxh", MedicineUtils.parseLong(map_fymx.get("YPXH")));
				map_par_yfbz.put("yfsb", yfsb);
				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
						map_par_yfbz);
				if (map_yfbz == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "无对应的药房药品");
				}
				int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
				double ypsl=MedicineUtils.simpleMultiply(2, map_fymx.get("YPSL"), brxx.get("CFTS"));
				long fyxm=BSPHISUtil.getfygb(yplx, MedicineUtils.parseLong(map_fymx.get("YPXH")), dao, ctx);// 费用项目
				double zfbl = getZfbl(MedicineUtils.parseLong(brxx.get("ZYH")),
						MedicineUtils.parseLong(map_fymx.get("YPXH")), fyxm);
				if(ypsl>0){
					PharmacyInventoryManageModel model=new PharmacyInventoryManageModel(dao);
					map_fymx.put("YPDJ", MedicineUtils.parseDouble(map_fymx.get("LSJG")));
					map_fymx.put("YFSB",yfsb);
					List<Map<String, Object>> list_fymx_temp = new ArrayList<Map<String, Object>>();
					list_fymx_temp.add(map_fymx);
					List<Map<String, Object>> list_ret = model
							.queryAndLessInventory(list_fymx_temp, ctx);
					if (list_ret == null
							|| (list_ret.size() == 1 && list_ret.get(0)
									.containsKey("ypxh"))) {
						throw new ModelDataOperationException("药品["+map_fymx.get("YPMC")+",产地:"+map_fymx.get("CDMC")+"]库存不够");
					}
					for (Map<String, Object> map_kcmx : list_ret) {
						Map<String, Object> map_jc_fymx = new HashMap<String, Object>();
						map_jc_fymx.put("JGID", jgid);
						map_jc_fymx.put("ZYH", MedicineUtils.parseLong(brxx.get("ZYH")));
						map_jc_fymx.put("FYRQ", d);
						map_jc_fymx.put("FYXH", MedicineUtils.parseLong(map_fymx.get("YPXH")));
						map_jc_fymx.put("FYMC", MedicineUtils.parseString( map_fymx.get("YPMC")));
						map_jc_fymx.put("YPCD", MedicineUtils.parseLong(map_fymx.get("YPCD")));
						map_jc_fymx.put(
								"FYSL",MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
												* yfbz
												/ MedicineUtils.parseInt(map_fymx
														.get("YFBZ"))));
						map_jc_fymx.put(
								"FYDJ",MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_jc_fymx.put(
								"ZJJE",
								MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_jc_fymx.get("FYSL"))
												* MedicineUtils.parseDouble(map_jc_fymx
														.get("FYDJ"))));
						map_jc_fymx
								.put("ZFJE",
										MedicineUtils.formatDouble(
												2,
												zfbl
														* MedicineUtils.parseDouble(map_jc_fymx
																.get("ZJJE"))));
						map_jc_fymx.put("YSGH", brxx.get("YSDM"));
						map_jc_fymx.put("SRGH", userid);
						map_jc_fymx.put("QRGH", userid);
						map_jc_fymx.put("FYBQ", "");
						map_jc_fymx.put("FYKS", 0);
						map_jc_fymx.put("ZXKS", 0);
						map_jc_fymx.put("JFRQ", d);
						map_jc_fymx.put("XMLX", 2);
						map_jc_fymx.put("YPLX", yplx);
						map_jc_fymx.put("FYXM", fyxm);
						map_jc_fymx.put("ZFBL", zfbl);
						//map_jc_fymx.put("YZXH", map_fymx.get("YZXH"));
						map_jc_fymx.put("JSCS", 0);
						map_jc_fymx.put("ZLJE", 0);
						//map_jc_fymx.put("ZLXZ", map_fymx.get("ZLXZ"));
						//map_jc_fymx.put("YEPB", map_fymx.get("YEPB"));
						map_jc_fymx.put("DZBL", 0);
						map_jc_fymx = dao.doSave("create","phis.application.fsb.schemas.JC_FYMX", map_jc_fymx, false);
						// 更新YF_JCFYMX
						Map<String, Object> map_yf_jcfymx_data = new HashMap<String, Object>();
						map_yf_jcfymx_data.put("JGID", jgid);
						map_yf_jcfymx_data.put("YFSB", yfsb);
						map_yf_jcfymx_data.put("CKBH", 0);
						map_yf_jcfymx_data.put("FYLX", 1);
						map_yf_jcfymx_data.put("ZYH", brxx.get("ZYH"));
						map_yf_jcfymx_data.put("FYRQ", d);
						map_yf_jcfymx_data.put("YPXH", map_fymx.get("YPXH"));
						map_yf_jcfymx_data.put("YPCD", map_fymx.get("YPCD"));
						map_yf_jcfymx_data.put("YPGG", map_fymx.get("YFGG"));
						map_yf_jcfymx_data.put("YFDW", map_fymx.get("YFDW"));
						map_yf_jcfymx_data.put("YFBZ", map_fymx.get("YFBZ"));
						map_yf_jcfymx_data.put(
								"YPSL",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
												* yfbz
												/ MedicineUtils.parseInt(map_fymx
														.get("YFBZ"))));
						map_yf_jcfymx_data.put(
								"YPDJ",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_yf_jcfymx_data.put("ZFBL", zfbl);
						map_yf_jcfymx_data.put("QRGH", userid);
						map_yf_jcfymx_data.put("JFRQ", d);
						map_yf_jcfymx_data.put("YPLX", yplx);
						//map_yf_jcfymx_data.put("YZXH", map_fymx.get("YZXH"));
						//map_yf_jcfymx_data.put("YEPB", map_fymx.get("YEPB"));
						map_yf_jcfymx_data.put("ZFPB", zfbl == 1 ? 0 : 1);// zfbl =
																			// 1时是0
																			// 否则是1
						map_yf_jcfymx_data.put("FYFS", 6);
						map_yf_jcfymx_data.put(
								"LSJG",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_yf_jcfymx_data.put(
								"PFJG",
								0);
						map_yf_jcfymx_data.put(
								"JHJG",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("JHJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_yf_jcfymx_data.put(
								"FYJE",MedicineUtils.simpleMultiply(2, map_yf_jcfymx_data.get("YPSL"), map_yf_jcfymx_data.get("LSJG")));
						map_yf_jcfymx_data.put(
								"LSJE",MedicineUtils.simpleMultiply(2, map_yf_jcfymx_data.get("YPSL"), map_yf_jcfymx_data.get("LSJG")));
						map_yf_jcfymx_data.put(
								"PFJE",
								0);
						map_yf_jcfymx_data.put(
								"JHJE",MedicineUtils.simpleMultiply(2, map_yf_jcfymx_data.get("YPSL"), map_yf_jcfymx_data.get("JHJG")));
						map_yf_jcfymx_data.put("YPPH", map_kcmx.get("YPPH"));
						map_yf_jcfymx_data.put("YPXQ", map_kcmx.get("YPXQ"));
						map_yf_jcfymx_data.put("TYGL", 0);
						map_yf_jcfymx_data.put(
								"JBYWBZ",
								map_fymx.get("JYLX") == null ? 0 : map_fymx
										.get("JYLX"));
						map_yf_jcfymx_data.put("KCSB", map_kcmx.get("KCSB"));
						//map_yf_jcfymx_data.put("TJXH", map_fymx.get("JLXH"));
						map_yf_jcfymx_data.put("TYXH", 0);
						map_yf_jcfymx_data
								.put("JLID", map_jc_fyjl_data.get("JLID"));
						map_yf_jcfymx_data.put("JFID", map_jc_fymx.get("JLXH"));
						map_yf_jcfymx_data.put("FYFS", map_fymx.get("FYFS"));
						map_yf_jcfymx_data.put("FYKS", 0);
						map_yf_jcfymx_data.put("ZXKS", 0);
						dao.doSave("create","phis.application.fsb.schemas.YF_JCFYMX",
								map_yf_jcfymx_data, false);
					}
					
				}else{//退药
					
					Map<String,Object> map_par=new HashMap<String,Object>();
					map_par.put("kcsb", MedicineUtils.parseLong(map_fymx.get("KCSB")));
					map_par.put("zyh", MedicineUtils.parseLong(brxx.get("ZYH")));
					List<Map<String,Object>> list_fysl=dao.doSqlQuery(hql_fyzl.toString(), map_par);
					if(list_fysl==null||list_fysl.size()==0||list_fysl.get(0)==null||list_fysl.get(0).size()==0){
						throw new ModelDataOperationException("药品["+map_fymx.get("YPMC")+"]没有记账记录");
					}else{
						double fysl=MedicineUtils.parseDouble(list_fysl.get(0).get("FYSL"));
						if(fysl<-ypsl){
							throw new ModelDataOperationException("药品["+map_fymx.get("YPMC")+"]记账数量不够");	
						}
						List<Map<String,Object>> list_yffymx=dao.doQuery(hql_yffymx.toString(), map_par);
						double ytsl=-ypsl;//需要退的药品数量
						for(Map<String,Object> map_yffymx:list_yffymx){
							double tysl=0;//本次循环退药数量
							boolean isBreak=false;
							if(MedicineUtils.parseDouble(map_yffymx.get("YPSL"))>=ytsl){
								tysl=ytsl;
								isBreak=true;
							}else{
								tysl=MedicineUtils.parseDouble(map_yffymx.get("YPSL"));
							}
							//新增费用明细数据
							Map<String, Object> map_jc_fymx = new HashMap<String, Object>();
							map_jc_fymx.put("JGID", jgid);
							map_jc_fymx.put("ZYH", MedicineUtils.parseLong(brxx.get("ZYH")));
							map_jc_fymx.put("FYRQ", d);
							map_jc_fymx.put("FYXH", MedicineUtils.parseLong(map_fymx.get("YPXH")));
							map_jc_fymx.put("FYMC", MedicineUtils.parseString( map_fymx.get("YPMC")));
							map_jc_fymx.put("YPCD", MedicineUtils.parseLong(map_fymx.get("YPCD")));
							map_jc_fymx.put(
									"FYSL",-tysl);
							map_jc_fymx.put(
									"FYDJ",MedicineUtils.parseDouble(map_fymx.get("LSJG"))
									);
							map_jc_fymx.put(
									"ZJJE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_jc_fymx.get("FYSL"))
													* MedicineUtils.parseDouble(map_jc_fymx
															.get("FYDJ"))));
							map_jc_fymx
									.put("ZFJE",
											MedicineUtils.formatDouble(
													2,
													zfbl
															* MedicineUtils.parseDouble(map_jc_fymx
																	.get("ZJJE"))));
							map_jc_fymx.put("YSGH", brxx.get("YSDM"));
							map_jc_fymx.put("SRGH", userid);
							map_jc_fymx.put("QRGH", userid);
							map_jc_fymx.put("FYBQ", "");
							map_jc_fymx.put("FYKS", 0);
							map_jc_fymx.put("ZXKS", 0);
							map_jc_fymx.put("JFRQ", d);
							map_jc_fymx.put("XMLX", 2);
							map_jc_fymx.put("YPLX", yplx);
							map_jc_fymx.put("FYXM", fyxm);
							map_jc_fymx.put("ZFBL", zfbl);
							//map_jc_fymx.put("YZXH", map_fymx.get("YZXH"));
							map_jc_fymx.put("JSCS", 0);
							map_jc_fymx.put("ZLJE", 0);
							//map_jc_fymx.put("ZLXZ", map_fymx.get("ZLXZ"));
							//map_jc_fymx.put("YEPB", map_fymx.get("YEPB"));
							map_jc_fymx.put("DZBL", 0);
							map_jc_fymx = dao.doSave("create","phis.application.fsb.schemas.JC_FYMX"
									, map_jc_fymx, false);
							double lsjg = MedicineUtils.parseDouble(map_yffymx.get("LSJG"));
							double pfjg = MedicineUtils.parseDouble(map_yffymx.get("PFJG"));
							double jhjg = MedicineUtils.parseDouble(map_yffymx.get("JHJG"));
							map_yffymx.put("TYGL", map_yffymx.get("JLXH"));
							map_yffymx.remove("JLXH");
							map_yffymx.put("FYLX", 5);
							map_yffymx.put("YPSL", -tysl);
							map_yffymx.put("FYRQ", d);
							// map_yffymx.put("JFID",map_jc_fyjl_data.get("JLID"));
							//map_yffymx.put("TYXH", MedicineUtils.parseLong(map_tymx.get("JLXH")));
							map_yffymx.put("TJXH", 0);
							map_yffymx
									.put("FYJE",
											-MedicineUtils.formatDouble(
													4,
													tysl
															* MedicineUtils.parseDouble(map_yffymx
																	.get("YPDJ"))));
							map_yffymx.put("LSJE", -MedicineUtils.formatDouble(4, tysl * lsjg));
							map_yffymx.put("PFJE", -MedicineUtils.formatDouble(4, tysl * pfjg));
							map_yffymx.put("JHJE", -MedicineUtils.formatDouble(4, tysl * jhjg));
							HospitalPharmacyBackMedicineModel tymodel=new HospitalPharmacyBackMedicineModel(dao);
							// 库存处理
							tymodel.saveIncreaseInventory(
									MedicineUtils.formatDouble(4,
											tysl * MedicineUtils.parseInt(map_yffymx.get("YFBZ"))
													/ yfbz),
													MedicineUtils.parseLong(map_yffymx.get("KCSB")),
													MedicineUtils.parseLong(map_yffymx.get("YPXH")),
													MedicineUtils.parseLong(map_yffymx.get("YPCD")), jgid, yfsb,
													MedicineUtils.parseLong(map_yffymx.get("JLID")));
							// 处理调价
							savePriceAdjustment(
									yksb,
									jgid,
									yfsb,
									map_yffymx,
									(tysl * (MedicineUtils.parseInt(map_yffymx.get("YFBZ")) / yfbz)),
									userid, yfbz);
							map_yffymx.put("JFID", map_jc_fymx.get("JLXH"));
							map_yffymx.put("JLID", map_jc_fyjl_data.get("JLID"));
							map_yffymx.put("FYFS", map_fymx.get("FYFS"));
							map_yffymx.put("FYKS", 0);
							map_yffymx.put("ZXKS", 0);
							dao.doSave("create","phis.application.fsb.schemas.YF_JCFYMX" ,
									map_yffymx, false);
							if(isBreak){
								break;
							}
						}
						
					}
				}
				
			}
			
		}catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "记账保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "记账保存失败", e);
		}
		
		
	}
	
	public void savePriceAdjustment(long yksb, String jgid, long yfsb,
			Map<String, Object> body, double tjsl, String userid, int yfbz)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
			map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
			map_par.put("jgid", jgid);
			map_par.put("yfsb", yfsb);
			StringBuffer hql_fysj = new StringBuffer();
			hql_fysj.append("select FYSJ as FYSJ from JC_FYJL where JLID=:jlid ");
			map_par.clear();
			map_par.put("jlid", MedicineUtils.parseLong(body.get("JLID")));
			Map<String, Object> map_fysj = dao.doLoad(hql_fysj.toString(),
					map_par);
			if (map_fysj == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "发药时间查询失败");
			}
			StringBuffer hql_tjjl_count = new StringBuffer();//查询是否有调价
			StringBuffer hql_tjjl_sum = new StringBuffer();//查出最后调价记录主键
			map_par.clear();
			if(body.containsKey("KCSB")&&MedicineUtils.parseLong(body.get("KCSB"))!=0){
				hql_tjjl_count.append(" KCSB=:kcsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				hql_tjjl_sum.append("select max(SBXH) as SBXH from YF_TJJL  where KCSB=:kcsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.put("kcsb", MedicineUtils.parseLong(body.get("KCSB")));
			}else{
				hql_tjjl_count.append(" YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				hql_tjjl_sum
				.append("select max(SBXH) as SBXH from YF_TJJL  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
				map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
				map_par.put("yfsb", yfsb);
			}
			map_par.put("fyrq", map_fysj.get("FYSJ"));
			long l = dao.doCount("YF_TJJL",
					hql_tjjl_count.toString(), map_par);
			if (l != 0) {
				Map<String, Object> map_tjjl_sbxh = dao.doLoad(
						hql_tjjl_sum.toString(), map_par);
				StringBuffer hql_tjjl = new StringBuffer();
				hql_tjjl.append(
						"select XLSJ as XLSJ,XJHJ as XJHJ,XPFJ as XPFJ from YF_TJJL where SBXH=:sbxh");
				map_par.clear();
				map_par.put("sbxh", MedicineUtils.parseLong(map_tjjl_sbxh.get("SBXH")));
				Map<String, Object> map_tjjl_r = dao.doLoad(
						hql_tjjl.toString(), map_par);
				Map<String, Object> map_tjjl = new HashMap<String, Object>();
				map_tjjl.put("YPXH", MedicineUtils.parseLong(body.get("YPXH")));
				map_tjjl.put("YPGG", body.get("YPGG"));
				map_tjjl.put("YFDW", body.get("YFDW"));
				map_tjjl.put("JGID", jgid);
				map_tjjl.put("YFSB", yfsb);
				map_tjjl.put("YPCD", MedicineUtils.parseLong(body.get("YPCD")));
				map_tjjl.put("YFBZ", yfbz);
				map_tjjl.put("YKSB", yksb);// 药库识别
				map_tjjl.put("CKBH", 0);// 窗口编号先默认保存为0
				map_tjjl.put("TJFS", 1);// 调价方式先默认保存为1
				map_tjjl.put("TJDH", 0);// 调价单号先默认保存为0
				map_tjjl.put("TJRQ", new Date());// 调价日期
				map_tjjl.put("TJSL", tjsl);// 调价数量
				map_tjjl.put("XLSJ", map_tjjl_r.get("XLSJ"));// 新零售价
				map_tjjl.put("XPFJ", map_tjjl_r.get("XJHJ"));// 新批发价
				map_tjjl.put("XJHJ", map_tjjl_r.get("XPFJ"));// 原进货价
				map_tjjl.put(
						"YLSJ",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(body.get("LSJG")) * yfbz
								/ MedicineUtils.parseInt(body.get("YFBZ"))));// 原零售价
				map_tjjl.put(
						"YPFJ",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(body.get("PFJG")) * yfbz
								/ MedicineUtils.parseInt(body.get("YFBZ"))));// 原批发价
				map_tjjl.put(
						"YJHJ",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(body.get("JHJG")) * yfbz
								/ MedicineUtils.parseInt(body.get("YFBZ"))));// 原进货价
				map_tjjl.put("CZGH", userid);// 操作工号
				map_tjjl.put("TJWH", "住院退药");
				map_tjjl.put("YLSE",
						MedicineUtils.formatDouble(4, (Double) map_tjjl.get("YLSJ")) * tjsl);// 原零售价
				map_tjjl.put("YPFE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl.get("YPFJ")) * tjsl));// 原批发金额
				map_tjjl.put("YJHE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl.get("YJHJ")) * tjsl));// 原进货金额
				map_tjjl.put(
						"XLSE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl_r.get("XLSJ"))
								* tjsl));// 新零售金额
				map_tjjl.put(
						"XPFE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl_r.get("XPFJ"))
								* tjsl));// 新批发金额
				map_tjjl.put(
						"XJHE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl_r.get("XJHJ"))
								* tjsl));// 新进货金额
				map_tjjl.put("KCSB", MedicineUtils.parseLong(body.get("KCSB")));// 库存识别
				map_tjjl.put("KCSL", tjsl);// 库存数量
				dao.doSave("create", BSPHISEntryNames.YF_TJJL, map_tjjl, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调价记录新增失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "调价记录新增验证失败", e);
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-14
	 * @description 获取自负比例
	 * @updateInfo
	 * @param zyh
	 * @param ypxh
	 * @param fyxm
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double getZfbl(long zyh, long ypxh, long fyxm)
			throws ModelDataOperationException {
		double d = 1;
		StringBuffer hql_brzx = new StringBuffer();// 查询病人性质
		hql_brzx.append("select BRXZ as BRXZ from JC_BRRY where ZYH=:zyh");
		StringBuffer hql_ypjy = new StringBuffer();// 从gy_ypjy中查询自付比例
		hql_ypjy.append("select ZFBL as ZFBL from GY_YPJY  where BRXZ=:brxz and YPXH=:ypxh");
		StringBuffer hql_zfbl = new StringBuffer();// 从GY_ZFBL中查询自付比例
		hql_zfbl.append("select ZFBL as ZFBL from GY_ZFBL where BRXZ=:brxz and SFXM=:fyxm");
		Map<String, Object> map_par = new HashMap<String, Object>();
		double zfbl=1;
		try {
			map_par.put("zyh", zyh);
			Map<String, Object> map_brxz = dao.doLoad(hql_brzx.toString(),
					map_par);
			if (map_brxz == null) {
				return d;
			}
			map_par.clear();
			map_par.put("brxz", MedicineUtils.parseLong(map_brxz.get("BRXZ")));
			map_par.put("ypxh", ypxh);
			Map<String, Object> map_zfbl = dao.doLoad(hql_ypjy.toString(),
					map_par);
			if (map_zfbl != null) {
				return MedicineUtils.parseDouble(map_zfbl.get("ZFBL"));
			}
			map_par.remove("ypxh");
			map_par.put("fyxm", fyxm);
			map_zfbl = dao.doLoad(hql_zfbl.toString(), map_par);
			if (map_zfbl == null) {
				return d;
			}
			zfbl=MedicineUtils.parseDouble(map_zfbl.get("ZFBL"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自付比例查询失败", e);
		}
		return zfbl;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-14
	 * @description 家床记账数量输入负数时查询已经计费的费用明细的价格放到前台显示
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> queryJzmx(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		long zyh=MedicineUtils.parseLong(body.get("ZYH"));
		long kcsb=MedicineUtils.parseLong(body.get("KCSB"));
		if(kcsb!=0){//有库存识别
			StringBuffer hql_fyjl=new StringBuffer();
			hql_fyjl.append("select a.KCSB as KCSB,a.LSJG as LSJG,a.JHJG as JHJG from YF_JCFYMX a,JC_FYMX b  where a.JFID=b.JLXH and b.ZYH=:zyh and a.KCSB=:kcsb and b.XMLX=2 and b.YZXH is null");
			if(zyh==0||kcsb==0){
				return MedicineUtils.getRetMap("未找到对应的记账记录");
			}
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zyh", zyh);
			map_par.put("kcsb", kcsb);
			try {
				List<Map<String,Object>> list_ret=dao.doQuery(hql_fyjl.toString(), map_par);
				if(list_ret==null||list_ret.size()==0){
					return MedicineUtils.getRetMap("未找到对应的记账记录");
				}
				Map<String,Object> map_body=new HashMap<String,Object>();
				map_body.put("TYPE", 1);
				map_body.putAll(list_ret.get(0));
				map_ret.put("body", map_body);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "查询记账明细失败", e);
			}
		}else{//无库存识别
			long ypxh=MedicineUtils.parseLong(body.get("YPXH"));
			long ypcd=MedicineUtils.parseLong(body.get("YPCD"));
			StringBuffer hql_fyjl=new StringBuffer();
			hql_fyjl.append("select distinct KCSB as KCSB,LSJG as LSJG,JHJG as JHJG from YF_JCFYMX where YPXH=:ypxh and YPCD=:ypcd and ZYH=:zyh");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("ypxh", ypxh);
			map_par.put("ypcd", ypcd);
			map_par.put("zyh", zyh);
			try {
				List<Map<String,Object>> list_kcsb=dao.doSqlQuery(hql_fyjl.toString(), map_par);
				if(list_kcsb==null||list_kcsb.size()==0){
					return MedicineUtils.getRetMap("未找到对应的记账记录");
				}
				Map<String,Object> map_body=new HashMap<String,Object>();
				if(list_kcsb.size()==1){
					map_body.put("TYPE", 1);
					map_body.putAll(list_kcsb.get(0));
				}else{
					map_body.put("TYPE", 2);
					List<Long> list_kcsb_arr=new ArrayList<Long>();
					for(Map<String,Object> map_kcsb:list_kcsb){
						list_kcsb_arr.add(MedicineUtils.parseLong(map_kcsb.get("KCSB")));
					}
					map_body.put("KCSB", list_kcsb_arr);
				}
				map_ret.put("body", map_body);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "查询记账明细失败", e);
			}
		}
		return map_ret;
	}
}
