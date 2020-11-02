/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author shiwy 2013.03.18
 */
public class ConfigTreasuryInformationModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigTreasuryInformationModule.class);

	public ConfigTreasuryInformationModule(BaseDAO dao) {
		this.dao = dao;
	}

	public void doUpdateConfigTreasuryInformation(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {

		try {

			// 生成默认数据
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String, Object> lzfs = new HashMap<String, Object>();
			lzfs.put("JGID", user.getManageUnit().getId());
			lzfs.put("KFXH", Long.parseLong(body.get("KFXH").toString()));
			lzfs.put("FSZT", 1);
			lzfs.put("SYKF", 1);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "采购入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 1);
			lzfs.put("FSPX", 10);
			lzfs.put("DJQZ", "RK");
			lzfs.put("DJNF", 1);
			lzfs.put("DJYF", 1);
			lzfs.put("XHCD", 4);
			lzfs.put("DYHS", 5);
			lzfs.put("BLSX", 1);
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("FSMC", "其他入库");
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 20);
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("FSMC", "盘盈入库");
			lzfs.put("TSBZ", 1);
			lzfs.put("FSPX", 30);
			lzfs.put("DJQZ", "PY");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("FSMC", "采购退回");
			lzfs.put("ZHZT", -3);
			lzfs.put("FKBZ", 1);
			lzfs.put("FSPX", 40);
			lzfs.put("TSBZ", 0);
			lzfs.put("DJQZ", "RK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("FSMC", "入库退回");
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 50);
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "领用出库");
			lzfs.put("TSBZ", 0);
			lzfs.put("JZBZ", 1);
			lzfs.put("ZHZT", 1);
			lzfs.put("FSPX", 50);
			lzfs.put("DJQZ", "CK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("FSMC", "其他出库");
			lzfs.put("TSBZ", 0);
			lzfs.put("FSPX", 60);
			lzfs.put("DJQZ", "CK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("SYKF", 1);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "盘亏出库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 1);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 70);
			lzfs.put("DJQZ", "PK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "出库退回");
			lzfs.put("JZBZ", 1);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 80);
			lzfs.put("DJQZ", "CK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "SL");
			lzfs.put("FSMC", "科室申领");
			lzfs.put("JZBZ", 1);
			lzfs.put("ZHZT", 1);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 90);
			lzfs.put("DJQZ", "SL");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "SL");
			lzfs.put("FSMC", "申领退回");
			lzfs.put("JZBZ", 1);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 100);
			lzfs.put("DJQZ", "SL");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "ZK");
			lzfs.put("FSMC", "转科");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 1);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 110);
			lzfs.put("DJQZ", "ZK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "BS");
			lzfs.put("FSMC", "报损");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", -2);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 120);
			lzfs.put("DJQZ", "BS");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "TK");
			lzfs.put("FSMC", "退库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 130);
			lzfs.put("DJQZ", "TK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "FC");
			lzfs.put("FSMC", "封存");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", -1);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 140);
			lzfs.put("DJQZ", "FC");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "PD");
			lzfs.put("FSMC", "盘点");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 150);
			lzfs.put("DJQZ", "PD");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "YH");
			lzfs.put("FSMC", "养护");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 160);
			lzfs.put("DJQZ", "YH");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "YS");
			lzfs.put("FSMC", "入库验收");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 170);
			lzfs.put("DJQZ", "YS");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "CZ");
			lzfs.put("FSMC", "资产重置");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 180);
			lzfs.put("DJQZ", "CZ");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "JH");
			lzfs.put("FSMC", "计划");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 190);
			lzfs.put("DJQZ", "JH");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "QT");
			lzfs.put("FSMC", "其他业务");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 200);
			lzfs.put("DJQZ", "QT");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("SYKF", 2);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "确认入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 10);
			lzfs.put("DJQZ", "RK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "其他入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 20);
			lzfs.put("DJQZ", "RK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "盘盈入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 1);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 30);
			lzfs.put("DJQZ", "PY");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "入库退回");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 40);
			lzfs.put("DJQZ", "RK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "消耗出库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 50);
			lzfs.put("DJQZ", "CK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "盘亏出库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 1);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 60);
			lzfs.put("DJQZ", "PK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "出库退回");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 70);
			lzfs.put("DJQZ", "CK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "SL");
			lzfs.put("FSMC", "申领入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 80);
			lzfs.put("DJQZ", "SL");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "SL");
			lzfs.put("FSMC", "申领退回");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 90);
			lzfs.put("DJQZ", "SL");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "DR");
			lzfs.put("FSMC", "调拨入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 100);
			lzfs.put("DJQZ", "DR");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "DR");
			lzfs.put("FSMC", "调拨入库退回");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 100);
			lzfs.put("DJQZ", "DR");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "DB");
			lzfs.put("FSMC", "调拨出库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 110);
			lzfs.put("DJQZ", "DB");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "DB");
			lzfs.put("FSMC", "调拨出库退回");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 110);
			lzfs.put("DJQZ", "DB");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "JG");
			lzfs.put("FSMC", "成品加工入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 110);
			lzfs.put("DJQZ", "JG");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "JG");
			lzfs.put("FSMC", "成品加工作废");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 110);
			lzfs.put("DJQZ", "JG");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "PD");
			lzfs.put("FSMC", "盘点");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 120);
			lzfs.put("DJQZ", "PD");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "DJ");
			lzfs.put("FSMC", "包裹登记");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 130);
			lzfs.put("DJQZ", "DJ");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "QT");
			lzfs.put("FSMC", "其他业务");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 140);
			lzfs.put("DJQZ", "QT");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("SYKF", 0);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "CS");
			lzfs.put("FSMC", "初始");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 10);
			lzfs.put("DJQZ", "CS");
			lzfs.put("DJNF", 0);
			lzfs.put("DJYF", 0);
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 0);
			lzfs.put("DJLX", "FK");
			lzfs.put("FSMC", "付款");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 20);
			lzfs.put("DJQZ", "FK");
			lzfs.put("DJNF", 1);
			lzfs.put("DJYF", 1);
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			/*lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "备货入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 1);
			lzfs.put("FSPX", 30);
			lzfs.put("DJQZ", "BK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "备货出库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 40);
			lzfs.put("DJQZ", "BK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", -1);
			lzfs.put("DJLX", "RK");
			lzfs.put("FSMC", "退回备货入库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 1);
			lzfs.put("FSPX", 50);
			lzfs.put("DJQZ", "BK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);
			lzfs.put("YWLB", 1);
			lzfs.put("DJLX", "CK");
			lzfs.put("FSMC", "退回备货出库");
			lzfs.put("JZBZ", 0);
			lzfs.put("ZHZT", 0);
			lzfs.put("TSBZ", 0);
			lzfs.put("FKBZ", 0);
			lzfs.put("FSPX", 60);
			lzfs.put("DJQZ", "BK");
			dao.doInsert(BSPHISEntryNames.WL_LZFS, lzfs, false);*/

			String ejkf = "0";
			if (body.get("EJKF") != null) {
				ejkf = body.get("EJKF") + "";
			}
			// System.out.println("----------------------" + ejkf);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", user.getManageUnit().getId());
			parameters.put("KFXH",
					Integer.parseInt(body.get("KFXH").toString()));
			if ("0".equals(ejkf) || ejkf == null) {
				// 是一级库房
				dao.doUpdate(
						"delete from WL_LZFS where JGID=:JGID and KFXH=:KFXH and SYKF=2",
						parameters);
			} else {
				// 是二级库房
				dao.doUpdate(
						"delete from WL_LZFS where JGID=:JGID and KFXH=:KFXH and SYKF=1",
						parameters);
				dao.doUpdate(
						"delete from WL_LZFS where JGID=:JGID and KFXH=:KFXH and SYKF=0",
						parameters);
			}

			parameters.clear();
			parameters.put("KFXH", Long.parseLong(body.get("KFXH").toString()));
			parameters.put("KFZT", 1);
			dao.doUpdate("update " + schemaList
					+ " set KFZT=:KFZT WHERE KFXH=:KFXH", parameters);

			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	// 判断名称不能重复
	public void doKfmcVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		if ("create".equals(op)) {
			if (body.get("KFMC") != null) {
				String sql = "KFMC=:KFMC and JGID=:JGID";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFMC", body.get("KFMC"));
				parameters.put("JGID", jgid);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "库房名称已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "库房名称校验失败");
				}
			}
		} else {
			if (body.get("KFMC") != null) {
				String sql = "KFMC=:KFMC and JGID=:JGID and KFXH<>:KFXH";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFMC", body.get("KFMC"));
				parameters.put("KFXH", Long.parseLong(body.get("KFXH") + ""));
				parameters.put("JGID", jgid);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "库房名称已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "库房名称校验失败");
				}
			}
		}
	}

	// 判断名称不能重复
	public void doQueryEJKF(Map<String, Object> body, Map<String, Object> res,
			String schemaDetailsList, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		long kfxh = Long.parseLong(body.get("KFXH") + "");
		parameters.put("JGID", jgid);
		parameters.put("EJKF", Long.parseLong(body.get("EJKF") + ""));
		parameters1.put("KFXH", kfxh);
		parameters1.put("JGID", jgid);
		parameters1.put("EJKF", Long.parseLong(body.get("EJKF") + ""));
		try {
			if (kfxh == 0) {
				Long l = dao.doCount(schemaDetailsList,
						"JGID=:JGID and EJKF=:EJKF", parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 615);
				}
			} else {
				Long l = dao.doCount(schemaDetailsList,
						"JGID=:JGID and EJKF=:EJKF and KFXH<>:KFXH",
						parameters1);
				if (l > 0) {
					res.put(Service.RES_CODE, 615);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	// 判断账簿类别是否启用
	public void doQueryZBLBXX(Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String TOPID = ParameterUtil.getTopUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("TOPID", TOPID);
		try {
			Long l = dao.doCount("WL_ZBLB",
					"(JGID=:JGID or JGID=:TOPID) and ZBZT=1", parameters);
			if (l <= 0) {
				res.put(Service.RES_CODE, 600);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	// 判断名称不能重复
	@SuppressWarnings("unchecked")
	public void doInitialize(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		int kfxh = Integer.parseInt(body.get("KFXH") + "");
		parameters.put("KFXH", kfxh);
		try {
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_CSKC);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_CSZC);
			// 入库
			dao.doUpdate(
					"Delete from WL_RK02 where DJXH in (select DJXH from WL_RK01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_RK01);
			// 出库
			dao.doUpdate(
					"Delete from WL_CK02 where DJXH in (select DJXH from WL_CK01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_CK01);
			// 报损
			dao.doUpdate(
					"Delete from WL_BS02 where DJXH in (select DJXH from WL_BS01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_BS01);
			// 重置
			dao.doUpdate(
					"Delete from WL_CZ02 where DJXH in (select DJXH from WL_CZ01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_CZ01);
			// 转科
			dao.doUpdate(
					"Delete from WL_ZK02 where DJXH in (select DJXH from WL_ZK01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_ZK01);
			// 计划
			dao.doUpdate(
					"Delete from WL_JH02 where DJXH in (select DJXH from WL_JH01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_JH01);
			// 盘点
			dao.doUpdate(
					"Delete from WL_PD02 where DJXH in (select DJXH from WL_PD01 where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_PD01);
			// 盘点录入
			dao.doUpdate(
					"Delete from WL_LRMX where LRXH in (select LRXH from WL_LRPD where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_LRPD);
			// 月结
			dao.doUpdate(
					"Delete from WL_YJJG where JZXH in (select JZXH from WL_YJJL where KFXH =:KFXH)",
					parameters);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_YJJL);
			// 库存、账册明细、科室帐、资产帐
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_WZKC);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_ZCMX);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_KSZC);
			dao.doRemove("KFXH", kfxh, BSPHISEntryNames.WL_ZCZB);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}
}
