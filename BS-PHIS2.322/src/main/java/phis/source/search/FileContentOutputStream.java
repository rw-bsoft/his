/**
 * @(#)FileContentSearch.java Created on 2013-5-2 下午4:41:19
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.source.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import phis.application.war.source.CaseHistoryReviewModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class FileContentOutputStream extends AbstractOutputStream {
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res,
			Context ctx) {
		String type = req.getParameter("type");
		String blbh = req.getParameter("BLBH");
		BaseDAO dao = new BaseDAO(ctx);
		CaseHistoryReviewModel chrm = new CaseHistoryReviewModel(dao);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = chrm.getFileContent(type, blbh);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		String result = (String) map.get("WDNR");
		if (result != null) {
			res.setCharacterEncoding("GBK");
			if (type.equals("HTML")) {
				res.setContentType("text/html");
			} else {
				res.setContentType("application/xml");
			}

			PrintWriter out = null;
			try {
				out = res.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					out.write(result);
					out.flush();
					out.close();
				}
			}
		}
	}

}
