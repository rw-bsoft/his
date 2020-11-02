package chis.source.ehrview;


public class wprogestationaskIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return false;
	}

	public String getEntryName() {
		return hc_w_progestationask;
	}
	
	@Override
	public String getEntityName() {
		return hc_w_progestationask;
	}

	public String getIdColumn() {
		return "phrId";
	}

	public String getIdName() {
		return "hc_w_progestationask.phrId";
	}
	
}
