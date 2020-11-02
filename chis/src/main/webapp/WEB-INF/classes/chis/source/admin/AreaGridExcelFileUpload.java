/**
 * @(#)ExcelUpLoad.java Created on 2012-6-5 上午09:50:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.SendDictionaryReloadSynMsg;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.dictionary.DictionaryController;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.PyConverter;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class AreaGridExcelFileUpload extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(AreaGridExcelFileUpload.class);

	private static int AllDataRow = 0;// 总文件行数
	private static int nowDataRow = 0;// 执行过行数

	private static String E1 = "e1";// 街道
	private static String F1 = "f1";// 社区
	private static String G1 = "g1";// 小区
	private static String H1 = "h1";// 幢
	private static String I1 = "i1";// 单元
	private static String CH = "1";// （城市）户

	private String qu = "";// 区
	private String jd = "";// 街道
	private String sq = "";// 社区
	private String xq = "";// 小区
	private String zh = "";// 幢
	private String dy = "";// 单元
	private String hu = "";// 户

	private int quNum = 0;// 区
	private int jdNum = 0;// 街道
	private int sqNum = 0;// 社区
	private int xqNum = 0;// 小区
	private int zhNum = 0;// 幢
	private int dyNum = 0;// 单元
	private int huNum = 0;// 户

	private Map<String, Object> xqMap = new HashMap<String, Object>();// 小区
	private Map<String, Object> zhMap = new HashMap<String, Object>();// 幢
	private Map<String, Object> dyMap = new HashMap<String, Object>();// 单元
	private Map<String, Object> huMap = new HashMap<String, Object>();// 户

	private Map<String, Object> xqButtomMap = new HashMap<String, Object>();// 小区
	private Map<String, Object> zhButtomMap = new HashMap<String, Object>();// 幢
	private Map<String, Object> dyButtomMap = new HashMap<String, Object>();// 单元

	private String beforeRegionCode = "";

	// 总列数
	private static int colNum;

	protected void doSaveAreaGrid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		resetValue();
		resetMap();
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		Session session = sf.openSession();
		ctx.put(Context.DB_SESSION, session);
		Transaction tx = session.beginTransaction();
		dao = getDAO();
		FileInputStream is = null;
		AreaGridModel areaGridModel = new AreaGridModel(dao);
		try {
			// 获取临时存储文件路径，并读取文件
			String path = (String) req.get("path");
			File file = new File(path);
			is = new FileInputStream(file);
			HSSFWorkbook wbs = new HSSFWorkbook(is);
			int pages = wbs.getNumberOfSheets();
			for (int i = 0; i < pages; i++) {
				if (wbs.getSheetAt(i).getLastRowNum() > 0) {
					AllDataRow += wbs.getSheetAt(i).getLastRowNum() - 1;
				}
			}
			boolean flag = true;
			for (int i = 0; i < pages; i++) {
				HSSFSheet childSheet = wbs.getSheetAt(i);
				if (childSheet.getLastRowNum() < 1) {
					continue;
				}
				// 总行数
				int rowNum = childSheet.getLastRowNum() + 1;
				// 循环每一行
				for (int j = 1; j < rowNum; j++) {
					nowDataRow++;
					HSSFRow row = childSheet.getRow(j);
					// name用于判断是否已经存过
					StringBuffer name = new StringBuffer();
					if (null != row) {
						Map<String, Object> record = new HashMap<String, Object>();
						beforeRegionCode = "";
						boolean isBottom = false;
						if (flag) {
							HSSFRow firstRow = childSheet.getRow(0);
							colNum = firstRow.getLastCellNum();
							flag = false;
						}
						// 循环一行的每个字段
						for (int k = 0; k < colNum - 3; k++) {
							HSSFCell cell = row.getCell(k);
							if (cell != null
									&& cell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
								name.append(cell.toString());
							} else {
								continue;
							}
							// 字段不为空，并且没有存过，执行
							if (cell != null
									&& cell.getCellType() != HSSFCell.CELL_TYPE_BLANK
									&& !judgeName(k, name.toString())) {
								if (k == 0 && !cell.toString().equals(qu)) {
									quNum = getQuNum(areaGridModel,
											cell.toString());
									resetValue();
									setBeforeRegionCode(k);
								}
								// 判断该数据存在与否的标志
								boolean isExist = false;
								// k = 0 时, 是区级字段，不执行
								if (k == 0) {
									continue;
								} else if (k == 1) {
									isExist = setJdNum(session,
											cell.toString(), beforeRegionCode);
								} else if (k == 2) {
									isExist = setSqNum(session,
											cell.toString(), beforeRegionCode);
									resetAfterMap(k);
								} else if (k == 3) {
									if (xqMap.size() == 0) {
										// 请求数据库，得到该同层次的所有map数据
										List<Map<String, Object>> list = getMap(
												areaGridModel, 3);
										xqMap = list.get(0);
										xqButtomMap = list.get(1);
										// 获得该层次的最大regionNo值
										int maxNum = getMaxNum(session,
												beforeRegionCode,
												beforeRegionCode.length() + 3);
										// 置最大值
										xqMap.put("maxNum", maxNum);
									}
									if (xqMap.get(cell.toString()) != null) {
										/* 该小区已经存在 */
										xqNum = (Integer) xqMap.get(cell
												.toString());
										// 如果不是前一条数据的小区名，重置map
										if (!cell.toString().equals(xq)) {
											resetAfterMap(k);
										}
										isExist = true;
									} else {
										/* 该小区不存在，为新数据 */
										xqNum = (Integer) xqMap.get("maxNum") + 1;
										xqMap.put(cell.toString(), xqNum);
										xqMap.put("maxNum", xqNum);
										resetAfterMap(k);
									}
								} else if (k == 4) {
									if (zhMap.size() == 0) {
										// 请求数据库，得到map
										List<Map<String, Object>> list = getMap(
												areaGridModel, 3);
										zhMap = list.get(0);
										zhButtomMap = list.get(1);
										int maxNum = getMaxNum(session,
												beforeRegionCode,
												beforeRegionCode.length() + 3);
										zhMap.put("maxNum", maxNum);
									}
									if (zhMap.get(cell.toString()) != null) {
										zhNum = (Integer) zhMap.get(cell
												.toString());
										if (!cell.toString().equals(zh)) {
											resetAfterMap(k);
										}
										isExist = true;
									} else {
										zhNum = (Integer) zhMap.get("maxNum") + 1;
										zhMap.put(cell.toString(), zhNum);
										zhMap.put("maxNum", zhNum);
										resetAfterMap(k);
									}
								} else if (k == 5) {
									if (dyMap.size() == 0) {
										// 请求数据库，得到map
										List<Map<String, Object>> list = getMap(
												areaGridModel, 3);
										dyMap = list.get(0);
										dyButtomMap = list.get(1);
										int maxNum = getMaxNum(session,
												beforeRegionCode,
												beforeRegionCode.length() + 3);
										dyMap.put("maxNum", maxNum);
									}
									if (dyMap.get(cell.toString()) != null) {
										dyNum = (Integer) dyMap.get(cell
												.toString());
										if (!cell.toString().equals(dy)) {
											resetAfterMap(k);
										}
										isExist = true;
									} else {
										dyNum = (Integer) dyMap.get("maxNum") + 1;
										dyMap.put(cell.toString(), dyNum);
										dyMap.put("maxNum", dyNum);
										resetAfterMap(k);
									}
								} else if (k == 6) {
									if (huMap.size() == 0) {
										// 请求数据库，得到map
										List<Map<String, Object>> list = getMap(
												areaGridModel, 4);
										huMap = list.get(0);
										int maxNum = getMaxNum(session,
												beforeRegionCode,
												beforeRegionCode.length() + 4);
										huMap.put("maxNum", maxNum);
									}
									if (huMap.get(cell.toString()) != null) {
										isExist = true;
									} else {
										huNum = (Integer) huMap.get("maxNum") + 1;
										huMap.put(cell.toString(), huNum);
										huMap.put("maxNum", huNum);
									}
								}
								// 更新缓存数据
								switchName(k, name.toString());
								if (isExist) {
									setBeforeRegionCode(k);
									continue;
								}
								// 不是最后一个字段，判断是否最底层
								// 如果是，直接置isBottom = true
								if (k < colNum - 4) {
									isBottom = isBottom(row, k);
								} else {
									isBottom = true;
								}
								if (isBottom) {
									record.put("isBottom", "y");
								} else {
									record.put("isBottom", "n");
								}
								// 获取isFamily字段值
								String isFamily = getIsFamilyKey(k);
								record.put("isFamily", isFamily);
								// 获取parentCode字段值
								String parentCode = beforeRegionCode;
								record.put("parentCode", parentCode);
								// 获取pyCode字段值
								String pyCode = PyConverter.getFirstLetter(cell
										.toString());
								record.put("pyCode", pyCode);
								// 获取regionName字段值
								String regionName = cell.toString();
								record.put("regionName", regionName);
								// 排序字段值
								int orderNo = getOrderNo(k);
								record.put("orderNo", orderNo);
								// 获取regionNo字段值
								String regionNo = getRegionNo(k);
								// 如果regionNo长度超出网格地址对应段规定的长度（3或4），抛异常
								if (!checkLength(regionNo, k)) {
									throw new ServiceException("第 " + (j + 1)
											+ " 行，第 " + (k + 1)
											+ " 个字段已经超出网格地址对应字段所分配的长度，请检查。");
								}
								record.put("regionNo", regionNo);
								// 获取regionCode字段值
								String regionCode = parentCode + regionNo;
								record.put("regionCode", regionCode);
								// 责任医生字段是否有数据，有，放入，没有，跳过
								if (row.getCell(colNum - 3) != null) {
									String manaDoctor = row.getCell(colNum - 3)
											.toString();
									record.put("manaDoctor", manaDoctor);
								}
								// 儿保字段是否有数据，有，放入，没有，跳过
								if (row.getCell(colNum - 2) != null) {
									String cdhDoctor = row.getCell(colNum - 2)
											.toString();
									record.put("cdhDoctor", cdhDoctor);
								}
								// 妇保医生字段是否有数据，有，放入，没有，跳过
								if (row.getCell(colNum - 1) != null) {
									String mhcDoctor = row.getCell(colNum - 1)
											.toString();
									record.put("mhcDoctor", mhcDoctor);
								}
								// System.out.println(name.toString() + ":"
								// + regionCode);
								int n = beforeRegionCode.length() / 3 - 2;
								String parentName = row.getCell(n).toString();
								// 判断是否需要更新数据
								boolean updateFlag = isUpdate(
										beforeRegionCode.length(), parentName);
								if (updateFlag) {
									updateRecord(session);
									setBottomMap(parentName, k);
								}
								setBottomMap(cell.toString(), k, isBottom);
								// 保存数据
								areaGridModel.saveAreaGrid(record, "create");
							}
							// 更新最近使用的网格地址编号
							if (cell != null
									&& cell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
								setBeforeRegionCode(k);
							}
						}
					}
					if (j % 1000 == 0) {
						session.flush();
						session.clear();
//						tx.commit();
//						tx = session.beginTransaction();
					}
				}
				session.flush();
				session.clear();
				tx.commit();
//				tx = session.beginTransaction();
			}
			DictionaryController.instance().reload("chis.dictionary.areaGrid");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		} catch (FileNotFoundException e) {
			tx.rollback();
			logger.error("批量导入网格地址失败，FileNotFoundException!");
			throw new ServiceException(e);
		} catch (IOException e) {
			tx.rollback();
			logger.error("批量导入网格地址失败，IOException!");
			throw new ServiceException(e);
		} catch (ModelDataOperationException e) {
			tx.rollback();
			logger.error("批量导入网格地址失败，ModelDataOperationException!");
			throw new ServiceException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 当需要修改上层节点的isBottom值时，执行update
	 * 
	 * @param session
	 */
	private void updateRecord(Session session) {
		StringBuffer hql = new StringBuffer("update ").append(EHR_AreaGrid)
				.append(" set isBottom = 'n' ")
				.append("where regionCode = :regionCode");
		Query query = session.createQuery(hql.toString());
		query.setString("regionCode", beforeRegionCode);
		query.executeUpdate();
	}

	/**
	 * 保存数据时，将相对应的isBottom值，置入对应的map中
	 * 
	 * @param name
	 * @param k
	 * @param isBottom
	 */
	private void setBottomMap(String name, int k, boolean isBottom) {
		switch (k) {
		case 3:
			xqButtomMap.put(name, isBottom);
		case 4:
			zhButtomMap.put(name, isBottom);
		case 5:
			dyButtomMap.put(name, isBottom);
		}
	}

	/**
	 * 更新数据后，修改相对应的bottomMap的值
	 * 
	 * @param parentName
	 * @param k
	 */
	private void setBottomMap(String parentName, int k) {
		switch (k) {
		case 6:
			dyButtomMap.put(parentName, false);
		case 5:
			zhButtomMap.put(parentName, false);
		case 4:
			xqButtomMap.put(parentName, false);
		}
	}

	/**
	 * 判断是否需要更新数据
	 * 
	 * @param length
	 * @param name
	 * @return
	 */
	private boolean isUpdate(int length, String name) {
		switch (length) {
		case 21:
			if (dyButtomMap.get(name) != null) {
				return (Boolean) dyButtomMap.get(name);
			}
		case 18:
			if ((Boolean) zhButtomMap.get(name) != null) {
				return (Boolean) zhButtomMap.get(name);
			}
		case 15:
			if ((Boolean) xqButtomMap.get(name) != null) {
				return (Boolean) xqButtomMap.get(name);
			}
		default:
			return false;
		}
	}

	/**
	 * 重置后面层次的map
	 * 
	 * @param k
	 */
	private void resetAfterMap(int k) {
		switch (k) {
		case 2:
			xqMap = new HashMap<String, Object>();
			xqButtomMap = new HashMap<String, Object>();
		case 3:
			zhMap = new HashMap<String, Object>();
			zhButtomMap = new HashMap<String, Object>();
		case 4:
			dyMap = new HashMap<String, Object>();
			dyButtomMap = new HashMap<String, Object>();
		case 5:
			huMap = new HashMap<String, Object>();
		}

	}

	/**
	 * 获得同一层次的所有值
	 * 
	 * @param areaGridModel
	 * @param n
	 * @return
	 * @throws ModelDataOperationException
	 */
	private List<Map<String, Object>> getMap(AreaGridModel areaGridModel, int n)
			throws ModelDataOperationException {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> bottomMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = areaGridModel.getMap(beforeRegionCode,
				beforeRegionCode.length() + n + 1);
		for (Map<String, Object> m : list) {
			map.put((String) m.get("regionName"),
					Integer.parseInt((String) m.get("regionNo")));
		}
		for (Map<String, Object> m : list) {
			if (m.get("isBottom").equals("y")) {
				bottomMap.put((String) m.get("regionName"), true);
			} else {
				bottomMap.put((String) m.get("regionName"), false);
			}
		}
		listMap.add(0, map);
		listMap.add(1, bottomMap);
		return listMap;
	}

	/**
	 * 初始化map
	 */
	private void resetMap() {
		xqMap = new HashMap<String, Object>();// 小区
		zhMap = new HashMap<String, Object>();// 幢
		dyMap = new HashMap<String, Object>();// 单元
		huMap = new HashMap<String, Object>();// 户

		xqButtomMap = new HashMap<String, Object>();// 小区
		zhButtomMap = new HashMap<String, Object>();// 幢
		dyButtomMap = new HashMap<String, Object>();// 单元
	}

	/**
	 * 初始化数据
	 */
	private void resetValue() {
		jd = "";// 街道
		sq = "";// 社区
		xq = "";// 小区
		zh = "";// 幢
		dy = "";// 单元
		hu = "";// 户

		jdNum = 0;// 街道
		sqNum = 0;// 社区
		xqNum = 0;// 小区
		zhNum = 0;// 幢
		dyNum = 0;// 单元
		huNum = 0;// 户

		beforeRegionCode = "";
	}

	/**
	 * 得到该层次的最大值
	 * 
	 * @param session
	 * @param num
	 * @param length
	 * @return
	 * @throws ModelDataOperationException 
	 */
	@SuppressWarnings("rawtypes")
	private int getMaxNum(Session session, String num, int length) throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		List<String> fields = new ArrayList<String>();
		fields.add("max(regionNo)");
		StringBuffer cnd = new StringBuffer("['like',['$','regionCode'],['s','")
		.append(num).append("']]");
		cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
		.append(num).append("']]]");
		
		List<Map<String, Object>> recs;
		Map<String, Object> map;
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", fields, JSONUtils.parse(cnd.toString(), List.class), qc };
			@SuppressWarnings("unchecked")
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
			if(recs != null){
				map = (Map<String, Object>) recs.get(0);
				return Integer.valueOf((String) map.get("max(regionNo)")) ;
			}
			
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址信息失败", e);
		}
		return 0;
		
//		StringBuffer hql = new StringBuffer("select max(regionNo) from ")
//				.append(EHR_AreaGrid).append(" where regionCode like '")
//				.append(num).append("%' and regionCode <> :num")
//				.append(" and length(regionCode)=:length");
//		Query query = session.createQuery(hql.toString());
//		query.setString("num", num);
//		query.setInteger("length", length);
//		List list = query.list();
//		if (list.size() > 0) {
//			Object r = (Object) list.get(0);
//			if (r != null) {
//				return Integer.parseInt((String) r);
//			}
//		}
	}

	/**
	 * 设置社区层的regionNo
	 * 
	 * @param session
	 * @param regionName
	 * @param jdNum2
	 * @return
	 * @throws ModelDataOperationException 
	 */
	@SuppressWarnings("rawtypes")
	private boolean setSqNum(Session session, String regionName, String jdNum2) throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer("['like',['$','regionCode'],['s','")
		.append(jdNum2).append("']]");
		cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
		.append(jdNum2).append("']]]");
		cnd.insert(0, "['and',").append(",['lt',['len','regioncode'],['s','")
		.append(13).append("']]]");
		cnd.insert(0, "['and',").append(",['eq',['$','regionName'],['s','")
		.append(regionName).append("']]]");
		
		List<Map<String, Object>> recs;
		Map<String, Object> map;
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", JSONUtils.parse(cnd.toString(), List.class), qc };
			@SuppressWarnings("unchecked")
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
			if(recs != null){
				map = (Map<String, Object>) recs.get(0);
				if(map != null){
					sqNum = Integer.parseInt((String) map.get("regionNo"));
					return true;
				}
			}
			
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址信息失败", e);
		}
		
//		StringBuffer hql = new StringBuffer("select regionNo from ")
//				.append(EHR_AreaGrid).append(" where regionName = :regionName")
//				.append(" and regionCode like '").append(jdNum2)
//				.append("%' and regionCode <> '").append(jdNum2)
//				.append("' and length(regionCode)<13");
//		Query query = session.createQuery(hql.toString());
//		query.setString("regionName", regionName);
//		List list = query.list();
//		if (list.size() > 0) {
//			Object r = (Object) list.get(0);
//			if (r != null) {
//				sqNum = Integer.parseInt((String) r);
//				return true;
//			}
//		}
		
//		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		List<String> fields = new ArrayList<String>();
		fields.add("max(regionNo)");
		cnd = new StringBuffer("['like',['$','regionCode'],['s','")
		.append(jdNum2).append("']]");
		cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
		.append(jdNum2).append("']]]");
		cnd.insert(0, "['and',").append(",['lt',['len','regioncode'],['s','")
		.append(13).append("']]]");
		
		List<Map<String, Object>> recs2;
		Map<String, Object> map2;
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", fields, JSONUtils.parse(cnd.toString(), List.class), qc };
			@SuppressWarnings("unchecked")
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs2 = qr.getItems();
			if(recs2 != null){
				map2 = (Map<String, Object>) recs2.get(0);
				if(map2 != null){
					sqNum =Integer.parseInt((String) map2.get("max(regionNo)")) + 1;
				}else{
					sqNum++;
				}
			}
			
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址信息失败", e);
		}
		return false;
//		StringBuffer hql2 = new StringBuffer("select max(regionNo) from ")
//				.append(EHR_AreaGrid).append(" where regionCode like '")
//				.append(jdNum2).append("%' and regionCode <> '").append(jdNum2)
//				.append("' and length(regionCode)<13");
//		Query query2 = session.createQuery(hql2.toString());
//		List list2 = query2.list();
//		if (list2.size() > 0) {
//			Object r = (Object) list2.get(0);
//			if (r != null) {
//				sqNum = Integer.parseInt((String) r) + 1;
//			} else {
//				sqNum++;
//			}
//		}
//		return false;
	}

	/**
	 * 设置街道层的regionNo
	 * 
	 * @param session
	 * @param regionName
	 * @param quNum2
	 * @return
	 * @throws ModelDataOperationException 
	 */
	@SuppressWarnings("rawtypes")
	private boolean setJdNum(Session session, String regionName, String quNum2) throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer("['like',['$','regionCode'],['s','")
		.append(quNum2).append("']]");
		cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
		.append(quNum2).append("']]]");
		cnd.insert(0, "['and',").append(",['lt',['len','regioncode'],['s','")
		.append(10).append("']]]");
		cnd.insert(0, "['and',").append(",['eq',['$','regionName'],['s','")
		.append(regionName).append("']]]");
		
		List<Map<String, Object>> recs;
		Map<String, Object> map;
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", JSONUtils.parse(cnd.toString(), List.class), qc };
			@SuppressWarnings("unchecked")
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
			if(recs != null){
				map = (Map<String, Object>) recs.get(0);
				if(map != null){
					jdNum = Integer.parseInt((String) map.get("regionNo"));
					return true;
				}
			}
			
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址信息失败", e);
		}
		List<String> fields = new ArrayList<String>();
		fields.add("max(regionNo)");
		cnd = new StringBuffer("['like',['$','regionCode'],['s','")
		.append(quNum2).append("']]");
		cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
		.append(quNum2).append("']]]");
		cnd.insert(0, "['and',").append(",['lt',['len','regioncode'],['s','")
		.append(10).append("']]]");
		
		List<Map<String, Object>> recs2;
		Map<String, Object> map2;
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", fields, JSONUtils.parse(cnd.toString(), List.class), qc };
			@SuppressWarnings("unchecked")
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs2 = qr.getItems();
			if(recs2 != null){
				map2 = (Map<String, Object>) recs2.get(0);
				if(map2 != null){
					jdNum =Integer.parseInt((String) map2.get("max(regionNo)")) + 1;
				}else{
					jdNum++;
				}
			}
			
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址信息失败", e);
		}
		return false;
//		StringBuffer hql = new StringBuffer("select regionNo from ")
//				.append(EHR_AreaGrid).append(" where regionName = :regionName")
//				.append(" and regionCode like '").append(quNum2)
//				.append("%' and regionCode <> '").append(quNum2)
//				.append("' and length(regionCode)<10");
//		Query query = session.createQuery(hql.toString());
//		query.setString("regionName", regionName);
//		List list = query.list();
//		// System.out.println(list);
//		if (list.size() > 0) {
//			Object r = (Object) list.get(0);
//			if (r != null) {
//				jdNum = Integer.parseInt((String) r);
//				return true;
//			}
//		}
//		StringBuffer hql2 = new StringBuffer("select max(regionNo) from ")
//				.append(EHR_AreaGrid).append(" where regionCode like '")
//				.append(quNum2).append("%' and regionCode <> '").append(quNum2)
//				.append("' and length(regionCode)<10");
//		Query query2 = session.createQuery(hql2.toString());
//		List list2 = query2.list();
//		if (list2.size() > 0) {
//			Object r = (Object) list2.get(0);
//			if (r != null) {
//				jdNum = Integer.parseInt((String) r) + 1;
//			} else {
//				jdNum++;
//			}
//		}
//		return false;
	}

	/**
	 * 获得执行进度百分比
	 * 
	 * @return
	 */
	public static int getProgress() {
		if (nowDataRow != 0 && AllDataRow != 0) {
			int progress = 100 * nowDataRow / AllDataRow;
			if (progress > 100) {
				return 100;
			}
			return progress;
		}
		return 0;
	}

	/**
	 * 判断字段是否超出改字段最大长度（3位或4位）
	 * 
	 * @param regionNo
	 * @param k
	 * @return
	 */
	private boolean checkLength(String regionNo, int k) {
		if (k == 6) {
			return regionNo.length() < 5;
		} else {
			return regionNo.length() < 4;
		}
	}

	/**
	 * 修改beforeRegionCode的值（执行下一条数据时会用到）
	 * 
	 * @param k
	 */
	private void setBeforeRegionCode(int k) {
		switch (k) {
		case 6:
			break;
		case 5:
			beforeRegionCode = beforeRegionCode + getFullStr(dyNum, 3);
			break;
		case 4:
			beforeRegionCode = beforeRegionCode + getFullStr(zhNum, 3);
			break;
		case 3:
			beforeRegionCode = beforeRegionCode + getFullStr(xqNum, 3);
			break;
		case 2:
			beforeRegionCode = beforeRegionCode + getFullStr(sqNum, 3);
			break;
		case 1:
			beforeRegionCode = beforeRegionCode + getFullStr(jdNum, 3);
			break;
		case 0:
			beforeRegionCode = beforeRegionCode + getFullStr(quNum, 3);
		}
	}

	private int getOrderNo(int k) {
		resetNum(k);
		switch (k) {
		case 6:
			return huNum * 10;
		case 5:
			return dyNum * 10;
		case 4:
			return zhNum * 10;
		case 3:
			return xqNum * 10;
		case 2:
			return sqNum * 10;
		case 1:
			return jdNum * 10;
		}
		return 0;
	}

	/**
	 * 获得3位或4位的网格地址编号
	 * 
	 * @param k
	 * @return
	 */
	private String getRegionNo(int k) {
		resetNum(k);
		switch (k) {
		case 6:
			return getFullStr(huNum, 4);
		case 5:
			return getFullStr(dyNum, 3);
		case 4:
			return getFullStr(zhNum, 3);
		case 3:
			return getFullStr(xqNum, 3);
		case 2:
			return getFullStr(sqNum, 3);
		case 1:
			return getFullStr(jdNum, 3);
		}
		return null;
	}

	/**
	 * 得到完整的3位或4位的网格地址编号；不足的前面填充"0"
	 * 
	 * @param name
	 * @param length
	 * @return
	 */
	private String getFullStr(int nameNum, int length) {
		String str = "" + nameNum;
		while (str.length() < length) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * 获取isFamily属性值
	 * 
	 * @param key
	 * @return
	 */
	private String getIsFamilyKey(int key) {
		switch (key) {
		case 1:
			return E1;
		case 2:
			return F1;
		case 3:
			return G1;
		case 4:
			return H1;
		case 5:
			return I1;
		case 6:
			return CH;
		}
		return null;
	}

	/**
	 * 得到区级网格地址
	 * 
	 * @param areaGridModel
	 * @param regionName
	 * @return
	 * @throws ServiceException
	 */
	private int getQuNum(AreaGridModel areaGridModel, String regionName)
			throws ServiceException {
		try {
			List<Map<String, Object>> list = areaGridModel
					.getAreaGridByRegionName(regionName);
			if (list == null || list.size() == 0) {
				throw new ServiceException("数据有误，没有该区级（" + regionName + "）网格地址");
			}
			Map<String, Object> record = list.get(0);
			return Integer.parseInt((String) record.get("regionCode"));
		} catch (ModelDataOperationException e) {
			logger.error("get quNum is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断是否最底层
	 * 
	 * @param row
	 * @param k
	 * @return
	 */
	private boolean isBottom(HSSFRow row, int col) {
		HSSFCell cell = null;
		for (int i = col + 1; i < colNum - 3; i++) {
			cell = row.getCell(i);
			if (cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是刚存过的地址
	 * 
	 * @param key
	 * @param name
	 * @return
	 */
	private boolean judgeName(int key, String name) {
		switch (key) {
		case 0:
			return name.equals(qu);
		case 1:
			return name.equals(jd);
		case 2:
			return name.equals(sq);
		case 3:
			return name.equals(xq);
		case 4:
			return name.equals(zh);
		case 5:
			return name.equals(dy);
		case 6:
			return name.equals(hu);
		}
		return true;
	}

	/**
	 * 更新最近记录
	 * 
	 * @param key
	 * @param name
	 */
	private void switchName(int key, String name) {
		switch (key) {
		case 0:
			qu = name;
			break;
		case 1:
			jd = name;
			break;
		case 2:
			sq = name;
			break;
		case 3:
			xq = name;
			break;
		case 4:
			zh = name;
			break;
		case 5:
			dy = name;
			break;
		case 6:
			hu = name;
			break;
		}
	}

	/**
	 * 更新num
	 * 
	 * @param key
	 */
	private void resetNum(int key) {
		switch (key) {
		case 0:
			jdNum = 0;
		case 1:
			sqNum = 0;
		case 2:
			xqNum = 0;
		case 3:
			zhNum = 0;
		case 4:
			dyNum = 0;
		case 5:
			huNum = 0;
		}
	}

	/**
	 * 获得dao
	 * 
	 * @param session
	 * @return
	 */
	public BaseDAO getDAO( ) {
		BaseDAO dao = new BaseDAO();
		return dao;
	}

}
