<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.log.schemas.LOG_EHR_VindicateNumber" alias="个档维护数">
	<item id="recordId" alias="记录编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32"/>
	<item id="year" alias="年份" type="string" length="4"/>
	<item id="logId" alias="日志编号" type="string" length="16"/>
	<item id="logTable" alias="日志记录表" type="string" length="200"/>
	<item ref="b.manaDoctorId" display="1" queryable="true"/>
	<item ref="b.manaUnitId" display="1" queryable="true"/>
	<relations>
		<relation type="parent" entryName="gp.application.hr.schemas.EHR_HealthRecord"/>
	</relations>
</entry>
