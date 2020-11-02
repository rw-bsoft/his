package chis.source.ehrview;


public class HighRiskRecordIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return false;
	}

	public String getEntryName() {
		return MDC_HighRiskRecord;
	}
	
	@Override
	public String getEntityName() {
		return MDC_HighRiskRecord;
	}

	public String getIdColumn() {
		return "phrId";
	}

	public String getIdName() {
		return "MDC_HighRiskRecord.phrId";
	}
	
}
