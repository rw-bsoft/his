package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSPHISUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 转科
 * @author zhangfs
 *
 */
public class WardTransferDeptService extends AbstractActionService implements DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(WardTransferDeptService.class);

	/**
	 * 初始化查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException 
	 */
	public void doQueryInfo(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		WardTransferDeptModel deptdao = new WardTransferDeptModel(dao);
		String zyh = body.get("zyh").toString();
		int type = Integer.parseInt(body.get("type").toString());

		long count = 0l;
		switch (type) {
		case 1:
			//查询是否有转科记录
			count = deptdao.queryisExistZkjl(ctx, zyh);
			break;
		case 2:
			//查询是否有未退包床
			count = deptdao.queryIsExistBc(ctx, zyh);
			break;
		case 3:
			//查询是否有出院证
			count = deptdao.queryIsExistCyz(ctx, body);
			break;

		case 4:
			//有会诊申请转科给予提示
			count = deptdao.queryIsExistHz(ctx, body);
			break;
		case 5:
			//查看是否有皮试药品未做
			count = deptdao.queryIsExistPs(ctx, body);
			break;
		case 6:
			//查看zy_brry中转科状态是否为1
			count = deptdao.queryisExistBrinfo(ctx, body);
			break;
		default:
			break;
		}

		res.put("count", count);
	}
	
	public void doQuerySFZK(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException {
		long ZYH = Long.parseLong(req.get("ZYH")+"");
		WardTransferDeptModel deptdao = new WardTransferDeptModel(dao);
		long count = deptdao.queryisExistZkjl(ctx, ZYH+"");
		if(count>0){
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("ZYH", ZYH);
			try {
				long ZY_ZKJLcount = dao.doCount("ZY_ZKJL", "ZYH = :ZYH and (HCLX = 3 OR HCLX = 1) and ZXBZ =2", parameters);
				if(ZY_ZKJLcount==1){
//					List<Object> cnd = CNDHelper.createSimpleCnd("eq", "ZYH", "i", (int)ZYH);
					Map<String,Object> body = dao.doLoad("select HHKS as HHKS,HHYS as HHYS,HHBQ as HHBQ from ZY_ZKJL where ZYH=:ZYH and (HCLX = 3 OR HCLX = 1) and ZXBZ =2",parameters);
					res.put("body", body);
				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		res.put("count", count);
	}
	/**
	 * 取出不符合该病人的转科条件的记录
	 */
	public void doQueryUnvalidRecord(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		body.put("ZYH", body.get("zyh"));
		WardTransferDeptModel deptdao = new WardTransferDeptModel(dao);
		int type = Integer.parseInt(body.get("type").toString());

		List<Map<String, Object>> list = null;
		switch (type) {
		case 1:
			List<Map<String, Object>> listGY_SYPC = BSPHISUtil
			.u_his_share_yzzxsj(null, dao, ctx);
			BSPHISUtil.uf_lsyz(listGY_SYPC, body, dao, ctx);
			list = deptdao.queryIsExistyzbd(ctx, body);
			break;
		case 2:
			//退药医嘱未提交或未确认表单记录数
			list = deptdao.queryIsExisttyzy(ctx, body);
			break;
		case 3:
			//退费单未确认表单记录数
			list = deptdao.queryIsExistf(ctx, body);
			break;
		case 7:
			//药品医嘱未提交未发药
			List<Map<String, Object>> listGY = BSPHISUtil
			.u_his_share_yzzxsj(null, dao, ctx);
			BSPHISUtil.uf_lsyz(listGY, body, dao, ctx);
			list = deptdao.queryIsExistyp(ctx, body);
			break;
		case 8:
			//项目医嘱未提交或未执行
			List<Map<String, Object>> listGY_S = BSPHISUtil
			.u_his_share_yzzxsj(null, dao, ctx);
			BSPHISUtil.uf_lsyz(listGY_S, body, dao, ctx);
			list = deptdao.queryIsExistxm(ctx, body);
			break;

		default:
			break;
		}

		res.put("record", list);
	}

	/**
	 * 更新brry转科标志，新增转科记录表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveorupdate(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardTransferDeptModel hbtbm = new WardTransferDeptModel(dao);
		try {
			//更新
			hbtbm.updateBrry(body, ctx);
			//保存
			hbtbm.saveZkjl(body, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	/**
	 * 根据选后科室，动态级联换后病区 
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBq(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardTransferDeptModel deptdao = new WardTransferDeptModel(dao);

		List<Map<String, Object>> list = deptdao.querySelectList(ctx, body);
		res.put("body", list);

	}

	/**
	 * 取消转科
	 */
	public void doUpdateUndo(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardTransferDeptModel hbtbm = new WardTransferDeptModel(dao);
		try {
			hbtbm.updateundoZk(res, body, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}
}
