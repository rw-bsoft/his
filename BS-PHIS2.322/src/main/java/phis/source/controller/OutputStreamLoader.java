package phis.source.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import phis.source.search.AbstractOutputStream;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

@Controller("outputStreamLoader")
public class OutputStreamLoader {
	private static final Logger logger = LoggerFactory
			.getLogger(OutputStreamLoader.class);

	@RequestMapping(value = "{className}.outputStream", method = RequestMethod.GET)
	public void doLoad(@PathVariable("className") String className,
			HttpServletRequest req, HttpServletResponse res) {
		String packageName = "phis.source.search.";
		AbstractOutputStream asm = null;
		try {
			Context ctx = ContextUtils.getContext();
			asm = (AbstractOutputStream) Class.forName(
					packageName + className + "OutputStream").newInstance();
			asm.open(ctx);
			// res.setContentType("text/plain");
			asm.execute(req, res, ctx);
		} catch (InstantiationException e) {
			logger.error("OutputStream service failed.", e);
		} catch (IllegalAccessException e) {
			logger.error("OutputStream service failed.", e);
		} catch (ClassNotFoundException e) {
			logger.error("OutputStream service failed.", e);
		} finally {
			asm.close();
		}

	}
}
