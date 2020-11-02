<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.phq.schemas.PHQ_AttendPersonnel" alias="听课人员登记">
	<item id="APID" alias="登记人员编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" endPos="9123372036854775807"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="courseId" alias="课程编号" type="string" length="16" display="0"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
