package chis.source.ehrview;


public class DeformityRecordIntellectIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return true;
	}
	
	public boolean hasCloseFlag() {
		return true;
	}
	
	public boolean hasMutiRecords(){
		return true ;
	}

	public String getEntryName() {
		return "DEF_IntellectDeformityRecord";
	}
	
	@Override
	public String getEntityName() {
		return DEF_IntellectDeformityRecord;
	}

	public String getIdColumn() {
		return "id";
	}

	public String getIdName() {
		return "DEF_IntellectDeformityRecord.id";
	}

	public String getManaInfoEntryName() {
		return DEF_IntellectDeformityRecord;
	}


	
}
