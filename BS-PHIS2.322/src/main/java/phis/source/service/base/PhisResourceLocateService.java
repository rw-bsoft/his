package phis.source.service.base;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import ctd.util.S;
import ctd.util.annotation.RpcService;
import ctd.account.UserRoleToken;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.mvc.resource.service.ResourceLocateService;
import ctd.mvc.resource.service.URLContent;
import ctd.resource.ResourceCenter;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class PhisResourceLocateService extends ResourceLocateService {
	private static String HttpContextPath;

	@RpcService
	public URLContent findStaticResource(String path) throws IOException {
		String p = path.contains("?") ? S.substringBetween(path, "/", "?") : S
				.substringAfter(path, "/");
		String q = S.substringAfter(path, "?");
		String suffix = S.substringAfterLast(p, ".");
		if ("print".equals(suffix)) {
			URLContent uc = new URLContent();
			HttpServletRequest req = ContextUtils.get(Context.HTTP_REQUEST,
					HttpServletRequest.class);
			String url = S.substringBefore(req.getRequestURL().toString(),
					"rpc/");
			UserRoleToken urt = UserRoleToken.getCurrent();
			url = url + "*.print?pages=" + S.substringBeforeLast(p, ".")
					+ (S.isEmpty(q) ? "" : "&" + q) + "&"
					+ UserRoleTokenUtils.SESSION_UID_KEY + "="
					+ urt.getUserId() + "&"
					+ UserRoleTokenUtils.SESSION_TOKEN_KEY + "=" + urt.getId();
			uc.setUrl(url);
			return uc;
		}
		// 判断是否是EmrImageLoad
		if (path != null && path.indexOf("phis_emr_image") > 0) {
			URLContent uc = new URLContent();
			String url = buildHttpURLStr("/resources" + path);
			uc.setUrl(url);
			return uc;
		} else if (path != null && path.indexOf("phis_emr_xml") > 0) {
			URLContent uc = new URLContent();
			String url = buildHttpURLStr("/"
					+ path.substring(path.indexOf("phis_emr_xml") + 13,
							path.indexOf(".jpg")) + ".emrImageLoad");
			uc.setUrl(url);
			return uc;
		} else if (path != null && path.indexOf("phisUrlProxy") > 0) {// phisController
			URLContent uc = new URLContent();
			String url = buildHttpURLStr("/"
					+ path.substring((path.indexOf("phisUrlProxy") + 13)));
			uc.setUrl(url);
			return uc;
		}
		Resource r = ResourceCenter.load(ResourceUtils.CLASSPATH_URL_PREFIX,
				path);

		if (r.exists()) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			String url = buildHttpURLStr("/resources" + path);
			URLContent uc = new URLContent();
			uc.setUrl(url);
			uc.setLastModi(r.lastModified());
			return uc;
		} else {
			return null;
		}
	}

	private String buildHttpURLStr(String path) throws IOException {
		StringBuffer sb = new StringBuffer();
		if (HttpContextPath == null) {
			HttpServletRequest request = (HttpServletRequest) ContextUtils
					.get(Context.HTTP_REQUEST);
			sb.append(
					StringUtils.substringBefore(request.getProtocol(), "/")
							.toLowerCase()).append("://")
					.append(request.getServerName()).append(":")
					.append(request.getServerPort())
					.append(request.getContextPath());
			HttpContextPath = sb.toString();
		} else {
			sb.append(HttpContextPath);
		}
		sb.append(path);

		return sb.toString();
	}
}
