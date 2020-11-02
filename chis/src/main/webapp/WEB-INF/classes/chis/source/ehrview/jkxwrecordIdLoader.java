package chis.source.ehrview;


public class jkxwrecordIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return false;
	}

	public String getEntryName() {
		return VIEW_ZJ_RECORD;
	}
	
	@Override
	public String getEntityName() {
		return VIEW_ZJ_RECORD;
	}

	public String getIdColumn() {
		return "Id";
	}

	public String getIdName() {
		return "VIEW_ZJ_RECORD.Id";
	}
	
}
