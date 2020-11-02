package chis.source.schedule.familyContract;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.schedule.AbstractJobSchedule;

public class FamilyContractRenewSchedule extends AbstractJobSchedule {

	@Override
	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		FamilyContractRenewScheduleModel fm = new FamilyContractRenewScheduleModel(
				dao);
		fm.renewFamilyContractRecord();
	}

}
