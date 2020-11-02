<?xml version="1.0" encoding="UTF-8"?>
<entry  entityName="chis.application.pub.schemas.PUB_VisitPlan" alias="孕妇工作列表" sort="b.idCard,a.planDate">
	<item id="planId" pkey="true" alias="计划序号" type="string" length="16"
		not-null="1" fixed="true" hidden="true"/>	
	<item id="empiId" type="string" display="0"/>
	<item id="recordId" type="string" display="0"/>
	
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.personName" display="0" queryable="true" />
	<item ref="b.mobileNumber" display="0" queryable="true" />

	<item id="planDate" alias="计划日期" type="date" width="160"/>
	<item id="earliestDate" alias="最早执行日期" type="date" virtual="true" width="160"/>
	<item id="lastDate" alias="最晚执行日期" type="date" virtual="true" width="160"/>
	<item id="businessType" alias="任务名称" type="string" length="1" queryable="true" width="160">
		<dic>
			<item key="8" text="孕妇产检随访" />
			<item key="9" text="孕妇高危随访"/>
		</dic>
	</item>
	
	<item id="remainDays" alias="剩余天数" type="string" display="0" virtual="true" width="160"/>
	
	<item id="extend1" alias="孕周" type="int" width="160"/>
	
	<item ref="c.mhcDoctorId" display="0"/>
	<item ref="c.restRegionCode" display="0"/>
	<item ref="c.restRegionCode_text" display="0"/>
	
	
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.mhc.schemas.MHC_PregnantRecord">
			<join parent="recordId" child="pregnantId" />
		</relation>
	</relations>
</entry>