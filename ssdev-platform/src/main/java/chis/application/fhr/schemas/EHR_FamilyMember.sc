<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_HealthRecord" alias="家庭成员" sort="birthday desc">
	<item ref="b.personName" display="1" queryable="false"/>
	<item ref="b.sexCode" display="1" queryable="false"/>
	<item ref="b.birthday" display="1" queryable="false"/>
	<item id="familyId" alias="所属家庭" type="string" length="30" hidden="true"/>
	<item id="empiId" alias="empiid" type="string" hidden="true"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
