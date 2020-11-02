package ctd.account.meta;

import phis.source.utils.ParameterUtil;
import ctd.controller.support.AbstractController;

public class MetaController extends AbstractController {
	public void reload(String id) {
		ParameterUtil.reloadParamsWithNoMate(id);
	}
}
