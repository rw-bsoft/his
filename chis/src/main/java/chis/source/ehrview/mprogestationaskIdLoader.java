package chis.source.ehrview;


public class mprogestationaskIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return false;
	}

	public String getEntryName() {
		return hc_m_progestationask;
	}
	
	@Override
	public String getEntityName() {
		return hc_m_progestationask;
	}

	public String getIdColumn() {
		return "phrId";
	}

	public String getIdName() {
		return "hc_m_progestationask.phrId";
	}
	
}
