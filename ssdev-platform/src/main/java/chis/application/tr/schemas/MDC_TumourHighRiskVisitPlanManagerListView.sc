<?xml version="1.0" encoding="UTF-8"?>

<entry entryName="chis.application.tr.schemas.MDC_TumourHighRiskVisitPlanManagerListView"  tableName="chis.application.pub.schemas.PUB_VisitPlan" alias="随访计划[MDC_TumourHighRiskVisitPlanListView复本显示 age字段]" sort="planDate" >
	<item id="planId" pkey="true" alias="计划识别" type="string" length="16" not-null="1" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="档案编号" type="string" length="30" hidden="true"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
	<item id="visitId" alias="随访记录ID" type="string" length="16" hidden="true"/>
	<item id="businessType" alias="计划类型" type="string" length="2" display="0">
		<dic id="chis.dictionary.planType"/>
	</item>
  
	<item ref="e.year" display="1"/>
	<item ref="d.turnHighRiskDate" display="1"/>
	<item ref="e.lastVisitDate" width="100" hidden="true"/>
	<item id="planDate" alias="计划随访日期" type="date" width="100" display="1"/>
	<item id="beginDate" alias="计划开始日期" type="date" width="100" display="1"/>
	<item id="endDate" alias="计划结束日期" type="date" width="100" display="1"/>
	<item id="beginVisitDate" alias="提醒日期" type="date" hidden="true"/>
	<item id="visitDate" alias="实际随访日期" type="date" width="100"/>
	<item ref="e.nextDate" alias="下次预约日期" width="100" display="1"/>
	<item id="planStatus" alias="计划状态" type="string" length="1" default="0">
		<dic>
			<item key="0" text="应访"/>
			<item key="1" text="已访"/>
			<item key="2" text="失访"/>
			<item key="3" text="未访"/>
			<item key="4" text="过访"/>
			<item key="8" text="结案"/>
			<item key="9" text="档案注销"/>
		</dic>
	</item>
	<item ref="b.personName" display="1"/>
	<item ref="b.sexCode" display="1"/>
	<item ref="b.birthday" display="1"/>
	<item ref="b.idCard" display="1"/>

	<item id="age" alias="年龄" type="int" width="30" display="1"/>
	
	<item ref="b.mobileNumber" alias="联系电话" display="1" />
	<item ref="d.highRiskType" display="1"/>
	<item ref="e.fixGroup" display="1"/>
	<item ref="e.visitWay" display="1"/>
	<item ref="e.visitEffect" display="1"/>
	<item ref="e.visitNorm" display="1"/>
	<item ref="e.checkResult" display="1"/>
	<item ref="e.visitDoctor" display="1"/>
	
	
	<item ref="c.regionCode" 	display="1" 	queryable="true"/> 
	<item ref="d.manaUnitId" display="1" queryable="true" />
	<item ref="d.manaDoctorId" display="1" queryable="true" />
	
	<item ref="e.lastModifyUnit" display="1"/>
	<item ref="e.lastModifyUser" display="1"/>
	<item ref="e.lastModifyDate" width="150" display="1"/>
  
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
		<relation type="children" entryName="chis.application.tr.schemas.MDC_TumourHighRisk">
			<join parent = "recordId" child = "THRID" />
		</relation>
		<relation type="children" entryName="chis.application.tr.schemas.MDC_TumourHighRiskVisit">
			<join parent = "empiId" child = "empiId" />
			<join parent="recordId" child="THRID" />
			<join parent="visitId" child="visitId" />
		</relation>
	</relations>
</entry>