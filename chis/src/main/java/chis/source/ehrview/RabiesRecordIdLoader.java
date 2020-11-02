package chis.source.ehrview;


public class RabiesRecordIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return true;
	}

	public boolean hasCloseFlag() {
		return true;
	}

	public String getEntryName() {
		return "DC_RabiesRecord";
	}
	
	@Override
	public String getEntityName() {
		return DC_RabiesRecord;
	}

	public String getIdColumn() {
		return "rabiesId";
	}

	public String getIdName() {
		return "DC_RabiesRecord.rabiesId";
	}

	public String getManaInfoEntryName() {
		return "EHR_HealthRecord";
	}


}
