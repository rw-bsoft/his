package chis.source.ehrview;


public class DeformityRecordLimbIdLoader extends AbstractIdLoader {

	public boolean hasStatus() {
		return true;
	}
	
	public boolean hasMutiRecords(){
		return true ;
	}
	
	public boolean hasCloseFlag() {
		return true;
	}

	public String getEntryName() {
		return "DEF_LimbDeformityRecord";
	}
	
	@Override
	public String getEntityName() {
		return DEF_LimbDeformityRecord;
	}

	public String getIdColumn() {
		return "id";
	}

	public String getIdName() {
		return "DEF_LimbDeformityRecord.id";
	}

	public String getManaInfoEntryName() {
		return DEF_LimbDeformityRecord;
	}
	
}
