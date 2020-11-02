package chis.source.mobileApp;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;

public class MobileAppModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public MobileAppModel(BaseDAO dao) {
		this.dao = dao;
	}

}
