<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pub.schemas.PUB_VisitPlan" alias="慢病工作列表" sort="b.idCard,a.planDate" version="1331800523277" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_CommonTaskList.xml">
	<item id="planId" pkey="true" alias="计划序号" type="string" length="16" not-null="1" fixed="true" hidden="true"/>
	<item id="empiId" type="string" display="0"/>
	<item id="recordId" type="string" display="0"/>
	<item ref="b.personName" display="0" queryable="true"/>
	<item ref="b.sexCode" display="0" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="0" queryable="true"/>
	<item ref="c.manaDoctorId" display="0"/>
	<item id="planDate" alias="计划日期" type="date" width="160"/>
	<item id="earliestDate" alias="最早执行日期" type="date" virtual="true" width="160"/>
	<item id="lastDate" alias="最晚执行日期" type="date" virtual="true" width="160"/>
	<item id="businessType" alias="任务名称" type="string" length="1" queryable="true" width="160">
		<dic>
			<item key="1" text="高血压随访"/>
			<item key="2" text="糖尿病随访"/>
			<item key="4" text="老年人保健"/>
			<item key="10" text="精神病随访"/>
			<item key="14" text="离休干部随访"/>
		</dic>
	</item>
	<item id="remainDays" alias="剩余天数" type="string" virtual="true" width="160"/>
	<item ref="c.manaDoctorId" display="0"/>
	<item ref="c.regionCode" display="0"/>
	<item ref="c.regionCode_text" display="0"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="recordId" child="phrId"/>
		</relation>
	</relations>
</entry>
