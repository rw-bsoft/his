package chis.source.ehrview;


public class IDR_ReportIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return true;
	}

	public String getEntryName() {
		return "IDR_Report";
	}
	
	@Override
	public String getEntityName() {
		return IDR_Report;
	}

	public String getIdColumn() {
		return "RecordID";
	}

	public String getIdName() {
		return "IDR_Report.RecordID";
	}
	 
	
}
