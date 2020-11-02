package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class MaintenanceMsgSearchFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameterwxpj = new HashMap<String, Object>();
		Long wxxh = 0l;
		if (request.get("wxxh") != null) {
			wxxh = Long.parseLong(request.get("wxxh") + "");
		}
		parameterwxpj.put("WXXH", wxxh);
		String hqlwxpj = "select b.WZMC as PJMC,b.WZDW as PJDW,b.WZGG as PJGG,a.PJJG as PJJG,a.PJSL as PJSL,a.PJJE as PJJE from WL_WXPJ a,WL_WZZD b where a.WZXH=b.WZXH and WXXH=:WXXH";
		try {
			List<Map<String, Object>> wxpjList = dao.doQuery(hqlwxpj,
					parameterwxpj);
			for (int i = 0; i < 5; i++) {
				Map<String, Object> nullmap = new HashMap<String, Object>();
				nullmap.put("PJMC", "");
				nullmap.put("PJDW", "");
				nullmap.put("PJGG", "");
				nullmap.put("PJJG", "");
				nullmap.put("PJSL", "");
				nullmap.put("PJJE", "");
				wxpjList.add(nullmap);
			}
			records.addAll(wxpjList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		response.put("TITLE", jgname);
		Long wxxh = 0l;
		if (request.get("wxxh") != null) {
			wxxh = Long.parseLong(request.get("wxxh") + "");
		}
		parameter.put("WXXH", wxxh);
		String hqlwxbg = "select a.WXBH as WXBH,a.SYKS as SYKS,a.SQGH as SQGH,a.SXRQ as SHRQ,a.WXLB as WXLB,a.FZGH as FZGH,a.KSRQ as KSRQ,a.FPHM as FPHM,a.WXFY as WXFY,a.CLFY as CLFY,a.YSGH as YSGH,a.LXDH as LXDH,a.WXDW as WXDW,a.WXSJ as WXSJ from WL_WXBG a where a.WXXH=:WXXH";
		String hqlwxsb = "select a.WXBH as WXBH,a.SYKS as SYKS,a.SQGH as SQGH,a.SXRQ as SHRQ,a.WXLB as WXLB,a.FZGH as FZGH,a.KSRQ as KSRQ,a.FPHM as FPHM,a.WXFY as WXFY,a.CLFY as CLFY,a.YSGH as YSGH,b.WZMC as WZMC,b.WZGG as WZGG,c.WZBH as WZBH from WL_WXBG a,WL_WZZD b,WL_ZCZB c where a.WZXH=b.WZXH and a.WZXH=b.WZXH and a.ZBXH=c.ZBXH and WXXH=:WXXH";
		try {
			Map<String, Object> wxbgmap = dao.doLoad(hqlwxbg, parameter);
			Map<String, Object> wxsbmap = dao.doLoad(hqlwxsb, parameter);
			if (wxbgmap != null) {
				if (wxbgmap.get("WXBH") != null) {
					response.put("WXBH", wxbgmap.get("WXBH") + "");
				}
				if (wxbgmap.get("SYKS") != null) {
					response.put(
							"SYKS",
							DictionaryController.instance()
									.getDic("phis.dictionary.department")
									.getText(wxbgmap.get("SYKS") + ""));
				}
				if (wxbgmap.get("WXDW") != null) {
					response.put(
							"WXDW",
							DictionaryController.instance()
									.getDic("phis.dictionary.supplyUnit")
									.getText(wxbgmap.get("WXDW") + ""));
				}
				if (wxbgmap.get("SQGH") != null) {
					response.put(
							"SQGH",
							DictionaryController.instance()
									.getDic("phis.dictionary.doctor")
									.getText(wxbgmap.get("SQGH") + ""));
				}
				if (wxbgmap.get("LXDH") != null) {
					response.put("LXDH", wxbgmap.get("LXDH") + "");
				} else {
					response.put("LXDH", "");
				}
				if (wxbgmap.get("YSGH") != null) {
					response.put(
							"YSGH",
							DictionaryController.instance()
									.getDic("phis.dictionary.doctor")
									.getText(wxbgmap.get("YSGH") + ""));
				}
				if (wxbgmap.get("SHRQ") != null) {
					response.put("SHRQ",
							BSHISUtil.getDate(wxbgmap.get("SHRQ") + ""));
				}
				if (wxbgmap.get("WXLB") != null) {
					if (Integer.parseInt(wxbgmap.get("WXLB") + "") == 1) {
						response.put("WXLB", "内维修");
					} else {
						response.put("WXLB", "外维修");
					}
				}
				if (wxbgmap.get("FZGH") != null) {
					response.put(
							"FZGH",
							DictionaryController.instance()
									.getDic("phis.dictionary.doctor")
									.getText(wxbgmap.get("FZGH") + ""));
				}
				if (wxbgmap.get("KSRQ") != null) {
					response.put("KSRQ",
							BSHISUtil.getDate(wxbgmap.get("KSRQ") + ""));
				}
				if (wxbgmap.get("FPHM") != null) {
					response.put("FPHM", wxbgmap.get("FPHM") + "");
				} else {
					response.put("FPHM", "");
				}
				if (wxbgmap.get("WXFY") != null) {
					response.put("WXFY",
							String.format("%1$.2f", wxbgmap.get("WXFY")));
				}
				if (wxbgmap.get("CLFY") != null) {
					response.put("CLFY",
							String.format("%1$.2f", wxbgmap.get("CLFY")));
				}
				if (wxbgmap.get("WXSJ") != null) {
					response.put("WXSJ",
							String.format("%1$.2f", wxbgmap.get("WXSJ")));
				}
			}
			if (wxsbmap != null) {
				String wzmc = "";
				if (wxsbmap.get("WZMC") != null) {
					wzmc = wxsbmap.get("WZMC") + ",";
				}
				String wzgg = "";
				if (wxsbmap.get("WZGG") != null) {
					wzgg = wxsbmap.get("WZGG") + ",";
				}
				String wxbh = "";
				if (wxsbmap.get("WZBH") != null) {
					wxbh = "编号为:" + wxsbmap.get("WZBH");
				}
				response.put("BXNR", wzmc + wzgg + wxbh);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
