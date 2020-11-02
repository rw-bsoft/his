package phis.application.hph.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;


import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyInventoryManageModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
/**
 * 住院记账Model
 * @author caijy
 *
 */
public class HospitalPharmacyAccountingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPharmacyAccountingModel.class);

	public HospitalPharmacyAccountingModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-22
	 * @description 住院记账-入院病人信息查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadZyxx(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> ret=MedicineUtils.getRetMap();
		String hm=MedicineUtils.parseString(body.get("HM"));//住院号码或者床号
		int type=MedicineUtils.parseInt(body.get("TYPE"));//判断是住院号码还是床号,1是床号,2是住院号码
		String manaUnitId = UserRoleToken.getCurrent().getManageUnit().getId();
		StringBuffer hql=new StringBuffer();
		hql.append("select ZYH as ZYH,BRCH as BRCH,ZYHM as ZYHM,BRXM as BRXM,BRKS as BRKS,BRXB as BRXB,BRXZ as BRXZ,BRBQ as BRBQ from ZY_BRRY where JGID=:jgid and CYPB=0 ");
		if(type==1){
			hql.append(" and BRCH=:hm");
		}else{
			hm=BSPHISUtil.get_public_fillleft(hm, "0",
					BSPHISUtil.getRydjNo(manaUnitId, "ZYHM", "", dao).length());
			hql.append(" and ZYHM=:hm");
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
		map_par.put("hm", hm);
		try {
			Map<String,Object> map_body=dao.doLoad(hql.toString(), map_par);
			if(map_body==null||map_body.size()==0){
				return MedicineUtils.getRetMap("未查到病人在院信息");
			}
			SchemaUtil.setDictionaryMassageForForm(map_body, "phis.application.hph.schemas.ZY_YPJZ_FORM");
			ret.put("body", map_body);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "病人信息查询失败", e);
		}
		
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-22
	 * @description 住院记账保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveHospitalPharmacyAccounting(Map<String, Object> body, Context ctx)
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
			Map<String, Object> map_yf_fyjl_data = new HashMap<String, Object>();// yf_fyjl保存记录
			map_yf_fyjl_data.put("JGID", jgid);
			map_yf_fyjl_data.put("FYSJ", d);
			map_yf_fyjl_data.put("FYGH", userid);
			map_yf_fyjl_data.put("FYLX", 1);
			map_yf_fyjl_data.put("JGID", jgid);
			map_yf_fyjl_data.put("YFSB", yfsb);
			map_yf_fyjl_data.put("FYFS", 6);
			map_yf_fyjl_data.put("DYPB", 0);
			map_yf_fyjl_data.put("FYLX", 1);
			map_yf_fyjl_data = dao.doSave("create", BSPHISEntryNames.YF_FYJL,
					map_yf_fyjl_data, false);
			StringBuffer hql_yfkc=new StringBuffer();//查询库存包装
			hql_yfkc.append("select YFBZ as YFBZ from YF_KCMX where SBXH=:sbxh");
			StringBuffer hql_fyzl=new StringBuffer();//退药时查询发药总量
			hql_fyzl.append("select sum(a.YPSL) as FYSL from YF_ZYFYMX a,ZY_FYMX b  where a.JFID=b.JLXH and b.ZYH=:zyh and a.KCSB=:kcsb and b.XMLX=2 and b.YZXH is null");
			StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(BSPHISEntryNames.YF_ZYFYMX);
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
			hql_yffymx.append(" from YF_ZYFYMX a,ZY_FYMX b where a.JFID=b.JLXH and b.ZYH=:zyh and a.KCSB=:kcsb and b.XMLX=2 and b.YZXH is null and a.YPSL>0");
//			PharmacyInventoryManageModel model=new PharmacyInventoryManageModel(dao);
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
						Map<String, Object> map_zy_fymx = new HashMap<String, Object>();
						map_zy_fymx.put("JGID", jgid);
						map_zy_fymx.put("ZYH", MedicineUtils.parseLong(brxx.get("ZYH")));
						map_zy_fymx.put("FYRQ", d);
						map_zy_fymx.put("FYXH", MedicineUtils.parseLong(map_fymx.get("YPXH")));
						map_zy_fymx.put("FYMC", MedicineUtils.parseString( map_fymx.get("YPMC")));
						map_zy_fymx.put("YPCD", MedicineUtils.parseLong(map_fymx.get("YPCD")));
						map_zy_fymx.put(
								"FYSL",MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
												* yfbz
												/ MedicineUtils.parseInt(map_fymx
														.get("YFBZ"))));
						map_zy_fymx.put(
								"FYDJ",MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz)
								);
						map_zy_fymx.put(
								"ZJJE",
								MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_zy_fymx.get("FYSL"))
												* MedicineUtils.parseDouble(map_zy_fymx
														.get("FYDJ"))));
						map_zy_fymx
								.put("ZFJE",
										MedicineUtils.formatDouble(
												2,
												zfbl
														* MedicineUtils.parseDouble(map_zy_fymx
																.get("ZJJE"))));
						map_zy_fymx.put("YSGH", brxx.get("YSDM"));
						map_zy_fymx.put("SRGH", userid);
						map_zy_fymx.put("QRGH", userid);
						map_zy_fymx.put("FYBQ", brxx.get("BRBQ"));
						map_zy_fymx.put("FYKS", brxx.get("BRKS") == null ? 0
								: brxx.get("BRKS"));
						map_zy_fymx.put("ZXKS", brxx.get("BRBQ") == null ? 0
								: brxx.get("BRBQ"));
						map_zy_fymx.put("JFRQ", d);
						map_zy_fymx.put("XMLX", 2);
						map_zy_fymx.put("YPLX", yplx);
						map_zy_fymx.put("FYXM", fyxm);
						map_zy_fymx.put("ZFBL", zfbl);
						//map_zy_fymx.put("YZXH", map_fymx.get("YZXH"));
						map_zy_fymx.put("JSCS", 0);
						map_zy_fymx.put("ZLJE", 0);
						//map_zy_fymx.put("ZLXZ", map_fymx.get("ZLXZ"));
						//map_zy_fymx.put("YEPB", map_fymx.get("YEPB"));
						map_zy_fymx.put("DZBL", 0);
						map_zy_fymx = dao.doSave("create",
								BSPHISEntryNames.ZY_FYMX, map_zy_fymx, false);
						// 更新YF_ZYFYMX
						Map<String, Object> map_yf_zyfymx_data = new HashMap<String, Object>();
						map_yf_zyfymx_data.put("JGID", jgid);
						map_yf_zyfymx_data.put("YFSB", yfsb);
						map_yf_zyfymx_data.put("CKBH", 0);
						map_yf_zyfymx_data.put("FYLX", 1);
						map_yf_zyfymx_data.put("ZYH", brxx.get("ZYH"));
						map_yf_zyfymx_data.put("FYRQ", d);
						map_yf_zyfymx_data.put("YPXH", map_fymx.get("YPXH"));
						map_yf_zyfymx_data.put("YPCD", map_fymx.get("YPCD"));
						map_yf_zyfymx_data.put("YPGG", map_fymx.get("YFGG"));
						map_yf_zyfymx_data.put("YFDW", map_fymx.get("YFDW"));
						map_yf_zyfymx_data.put("YFBZ", map_fymx.get("YFBZ"));
						map_yf_zyfymx_data.put(
								"YPSL",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
												* yfbz
												/ MedicineUtils.parseInt(map_fymx
														.get("YFBZ"))));
						map_yf_zyfymx_data.put(
								"YPDJ",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_yf_zyfymx_data.put("ZFBL", zfbl);
						map_yf_zyfymx_data.put("QRGH", userid);
						map_yf_zyfymx_data.put("JFRQ", d);
						map_yf_zyfymx_data.put("YPLX", yplx);
						map_yf_zyfymx_data.put("FYKS",brxx.get("BRKS") == null ? 0
								: brxx.get("BRKS"));
						map_yf_zyfymx_data.put("LYBQ", brxx.get("BRBQ") == null ? 0
								: brxx.get("BRBQ"));
						map_yf_zyfymx_data.put(
								"ZXKS",
								brxx.get("BRKS") == null ? 0
										: brxx.get("BRKS"));
						//map_yf_zyfymx_data.put("YZXH", map_fymx.get("YZXH"));
						//map_yf_zyfymx_data.put("YEPB", map_fymx.get("YEPB"));
						map_yf_zyfymx_data.put("ZFPB", zfbl == 1 ? 0 : 1);// zfbl =
																			// 1时是0
																			// 否则是1
						map_yf_zyfymx_data.put("FYFS", 6);
						map_yf_zyfymx_data.put(
								"LSJG",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_yf_zyfymx_data.put(
								"PFJG",
								0);
						map_yf_zyfymx_data.put(
								"JHJG",
								MedicineUtils.formatDouble(
										4,
										MedicineUtils.parseDouble(map_kcmx.get("JHJG"))
												* MedicineUtils.parseInt(map_fymx
														.get("YFBZ")) / yfbz));
						map_yf_zyfymx_data.put(
								"FYJE",MedicineUtils.simpleMultiply(2, map_yf_zyfymx_data.get("YPSL"), map_yf_zyfymx_data.get("LSJG")));
						map_yf_zyfymx_data.put(
								"LSJE",MedicineUtils.simpleMultiply(2, map_yf_zyfymx_data.get("YPSL"), map_yf_zyfymx_data.get("LSJG")));
						map_yf_zyfymx_data.put(
								"PFJE",
								0);
						map_yf_zyfymx_data.put(
								"JHJE",MedicineUtils.simpleMultiply(2, map_yf_zyfymx_data.get("YPSL"), map_yf_zyfymx_data.get("JHJG")));
						map_yf_zyfymx_data.put("YPPH", map_kcmx.get("YPPH"));
						map_yf_zyfymx_data.put("YPXQ", map_kcmx.get("YPXQ"));
						map_yf_zyfymx_data.put("TYGL", 0);
						map_yf_zyfymx_data.put(
								"JBYWBZ",
								map_fymx.get("JYLX") == null ? 1 : map_fymx
										.get("JYLX"));
						map_yf_zyfymx_data.put("KCSB", map_kcmx.get("KCSB"));
						//map_yf_zyfymx_data.put("TJXH", map_fymx.get("JLXH"));
						map_yf_zyfymx_data.put("TYXH", 0);
						map_yf_zyfymx_data
								.put("JLID", map_yf_fyjl_data.get("JLID"));
						map_yf_zyfymx_data.put("JFID", map_zy_fymx.get("JLXH"));
						map_yf_zyfymx_data.put("FYFS", map_fymx.get("FYFS"));
						dao.doSave("create", BSPHISEntryNames.YF_ZYFYMX,
								map_yf_zyfymx_data, false);
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
							Map<String, Object> map_zy_fymx = new HashMap<String, Object>();
							map_zy_fymx.put("JGID", jgid);
							map_zy_fymx.put("ZYH", MedicineUtils.parseLong(brxx.get("ZYH")));
							map_zy_fymx.put("FYRQ", d);
							map_zy_fymx.put("FYXH", MedicineUtils.parseLong(map_fymx.get("YPXH")));
							map_zy_fymx.put("FYMC", MedicineUtils.parseString( map_fymx.get("YPMC")));
							map_zy_fymx.put("YPCD", MedicineUtils.parseLong(map_fymx.get("YPCD")));
							map_zy_fymx.put(
									"FYSL",-tysl);
							map_zy_fymx.put(
									"FYDJ",MedicineUtils.parseDouble(map_fymx.get("LSJG"))
									);
							map_zy_fymx.put(
									"ZJJE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_zy_fymx.get("FYSL"))
													* MedicineUtils.parseDouble(map_zy_fymx
															.get("FYDJ"))));
							map_zy_fymx
									.put("ZFJE",
											MedicineUtils.formatDouble(
													2,
													zfbl
															* MedicineUtils.parseDouble(map_zy_fymx
																	.get("ZJJE"))));
							map_zy_fymx.put("YSGH", brxx.get("YSDM"));
							map_zy_fymx.put("SRGH", userid);
							map_zy_fymx.put("QRGH", userid);
							map_zy_fymx.put("FYBQ", brxx.get("BRBQ"));
							map_zy_fymx.put("FYKS", brxx.get("BRKS") == null ? 0
									: brxx.get("BRKS"));
							map_zy_fymx.put("ZXKS", brxx.get("BRBQ") == null ? 0
									: brxx.get("BRBQ"));
							map_zy_fymx.put("JFRQ", d);
							map_zy_fymx.put("XMLX", 2);
							map_zy_fymx.put("YPLX", yplx);
							map_zy_fymx.put("FYXM", fyxm);
							map_zy_fymx.put("ZFBL", zfbl);
							//map_zy_fymx.put("YZXH", map_fymx.get("YZXH"));
							map_zy_fymx.put("JSCS", 0);
							map_zy_fymx.put("ZLJE", 0);
							//map_zy_fymx.put("ZLXZ", map_fymx.get("ZLXZ"));
							//map_zy_fymx.put("YEPB", map_fymx.get("YEPB"));
							map_zy_fymx.put("DZBL", 0);
							map_zy_fymx = dao.doSave("create",
									BSPHISEntryNames.ZY_FYMX, map_zy_fymx, false);
							double lsjg = MedicineUtils.parseDouble(map_yffymx.get("LSJG"));
							double pfjg = MedicineUtils.parseDouble(map_yffymx.get("PFJG"));
							double jhjg = MedicineUtils.parseDouble(map_yffymx.get("JHJG"));
							map_yffymx.put("TYGL", map_yffymx.get("JLXH"));
							map_yffymx.remove("JLXH");
							map_yffymx.put("FYLX", 5);
							map_yffymx.put("YPSL", -tysl);
							map_yffymx.put("FYRQ", d);
							// map_yffymx.put("JFID",map_yf_fyjl_data.get("JLID"));
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
							tymodel.savePriceAdjustment(
									yksb,
									jgid,
									yfsb,
									map_yffymx,
									(tysl * (MedicineUtils.parseInt(map_yffymx.get("YFBZ")) / yfbz)),
									userid, yfbz);
							map_yffymx.put("JFID", map_zy_fymx.get("JLXH"));
							map_yffymx.put("JLID", map_yf_fyjl_data.get("JLID"));
							map_yffymx.put("FYFS", map_fymx.get("FYFS"));
							dao.doSave("create", BSPHISEntryNames.YF_ZYFYMX,
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
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-23
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
		hql_brzx.append("select BRXZ as BRXZ from ZY_BRRY where ZYH=:zyh");
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
	 * @createDate 2014-5-13
	 * @description 住院记账数量输入负数时查询已经计费的费用明细的价格放到前台显示
	 * @updateInfo 2014-11-25 by caijy for 页面药品模糊查询不关联库存表,在修改数量的时候查询库存信息或者记账信息 (zw2要求界面不能出现0库存记录)
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
		double ypsl=MedicineUtils.parseDouble(body.get("YPSL"));
		if(ypsl<0){//输入负数 查询记账记录
			if(kcsb!=0){//有库存识别
				StringBuffer hql_fyjl=new StringBuffer();
				hql_fyjl.append("select a.KCSB as KCSB,a.LSJG as LSJG,a.JHJG as JHJG from YF_ZYFYMX a,ZY_FYMX b  where a.JFID=b.JLXH and b.ZYH=:zyh and a.KCSB=:kcsb and b.XMLX=2 and b.YZXH is null");
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
				hql_fyjl.append("select distinct KCSB as KCSB,LSJG as LSJG,JHJG as JHJG from YF_ZYFYMX where YPXH=:ypxh and YPCD=:ypcd and ZYH=:zyh");
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
		}else{//输入正数 查询库存信息
			StringBuffer hql=new StringBuffer();
			hql.append("select SBXH as KCSB,LSJG as LSJG,JHJG as JHJG ,YPSL as YPSL from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
			map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
			map_par.put("yfsb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("pharmacyId")));
			try {
				List<Map<String,Object>> list_kcsb=dao.doSqlQuery(hql.toString(), map_par);
				if(list_kcsb==null||list_kcsb.size()==0){
					return MedicineUtils.getRetMap("未找到对应的库存记录");
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
				MedicineUtils.throwsException(logger, "查询库存记录失败", e);
			}
			
		}
		return map_ret;
	}
	
	@SuppressWarnings("unchecked")
	public void doLoadMedcineSet (Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ModelDataOperationException {
		Map<String,Object> body = (Map<String,Object>)req.get("body");
		long ztbh = Long.parseLong(body.get("YPXH")+"");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		try {
			List<Map<String, Object>> rebody = new ArrayList<Map<String, Object>>();
			// 发药药房 和 药品类别
			Object pharmacyId = body.get("pharmacyId");
			// pharmacyId = "1";//需去掉
			String orderBy = "ZTBH";
			String schema = "phis.application.cic.schemas.YS_MZ_ZT02_MS";
			String cnds = "['eq',['$','ZTBH'],['d'," + ztbh + "]]";
			List<Map<String, Object>> list = dao.doList(
					CNDHelper.toListCnd(cnds), orderBy, schema);
			SchemaUtil.setDictionaryMassageForList(list, schema);
			for (Map<String, Object> med : list) {
				Object ypxh = med.get("YPXH");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("YFSB", Long.parseLong(pharmacyId.toString()));
				params.put("YPXH", ypxh);
				params.put("JGID", jgid);
				StringBuffer hql = new StringBuffer(
						"select a.FYFS as FYFS,a.JYLX as JYLX,e.YPSL as KCSL,d.PFJG as PFJG,d.JHJG as JHJG,a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,c.CDMC as CDMC,a.TYPE as TYPE,b.YFDW as YFDW ");
				hql.append(" from YK_TYPK a,YF_YPXX b,YK_CDDZ c,YK_CDXX d,YF_KCMX e");
				hql.append(" where b.YPXH=e.YPXH and b.YFSB=e.YFSB and c.YPCD=e.YPCD and d.ZFPB!=1 and d.JGID=:JGID and d.YPCD=c.YPCD and a.YPXH=d.YPXH and b.YPXH = a.YPXH AND a.ZFPB = 0 AND b.YFZF = 0 AND b.YFSB=:YFSB AND a.YPXH = :YPXH ORDER BY a.YPXH,e.YPSL");
				List<Map<String, Object>> meds = dao.doQuery(hql.toString(),params);
				if (meds.size() > 0) {// 取第一条记录
					Map<String, Object> zt_med = meds.get(0);
					// zt_med.putAll(med);
					zt_med.put("YPSL", med.get("XMSL"));
					rebody.add(zt_med);
				} else {
					med.put("msg", "药品【" + med.get("YPMC") + "】库存不足!");
					rebody.add(med);
				}
			}
			res.put("body", rebody);
		} catch (ExpException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}
}
