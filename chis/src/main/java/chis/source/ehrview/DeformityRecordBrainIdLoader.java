package chis.source.ehrview;


public class DeformityRecordBrainIdLoader extends AbstractIdLoader {

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
		return "DEF_BrainDeformityRecord";
	}
	
	@Override
	public String getEntityName() {
		return DEF_BrainDeformityRecord;
	}

	public String getIdColumn() {
		return "id";
	}

	public String getIdName() {
		return "DEF_BrainDeformityRecord.id";
	}

	public String getManaInfoEntryName() {
		return "DEF_BrainDeformityRecord";
	}

	
}
