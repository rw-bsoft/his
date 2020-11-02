<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionRiskVisitPlan" tableName="chis.application.pub.schemas.PUB_VisitPlan" alias="高血压高危随访">
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
	<item id="planDate" alias="计划随访日期" type="date" width="100" display="1"/>
	<item id="endDate" alias="计划结束日期" type="date" width="100" display="1"/>
	<item id="visitDate" alias="实际随访日期" type="date" width="100" length="1"/>
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
	<item ref="d.age" alias="年龄" virtual="true" display="1" />
	<item ref="b.idCard" display="1"/>
	<item ref="b.mobileNumber" alias="联系电话" display="1" />
	<item ref="c.regionCode" 	display="1" 	queryable="true"/> 
	<item ref="c.manaUnitId" display="1" queryable="true" />
	<item ref="c.manaDoctorId" display="1" queryable="true" />
	<item ref="d.visitWay" alias="随访方式" type="string" length="1" display="1" />
	<item ref="d.constriction" alias="收缩压(mmHg)" display="1"  width="150"/>
	<item ref="d.diastolic" alias="舒张压(mmHg)" display="1"  width="150"/>
	<item ref="d.height" alias="身高(cm)" display="1" />
	<item ref="d.weight" alias="体重(kg)" display="1" />
	<item ref="d.bmi" alias="BMI" display="1"/>
	<item ref="d.visitDoctor" alias="随访医生" type="string" display="1" length="20"/>
	<item ref="d.visitUnit" alias="随访单位" type="string" display="1" length="20"/>
	<item ref="d.visitEffect" alias="转归情况" type="string" display="1" length="1"/>
	<item ref="d.stopDate" alias="终止日期" type="datetime" display="1" xtype="datefield"/>
	<item ref="d.stopCause" alias="终止原因" type="string" display="1" />
	<item ref="d.inputUnit" alias="登记单位" type="string" display="1" length="8"/>
	<item ref="d.inputDate" alias="登记日期" type="date" display="1"/>
	<item ref="d.inputUser" alias="登记人" type="string" display="1" length="20"/>
	<item ref="d.lastModifyUser" alias="最后修改人" type="string" display="1" length="20"/>
	<item ref="d.lastModifyUnit" alias="最后修改单位" type="string" display="1" length="20" width="180"/>
	<item ref="d.lastModifyDate" alias="最后修改日期" type="datetime" display="1" xtype="datefield"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
		<relation type="children" entryName="chis.application.hy.schemas.MDC_HypertensionRiskVisit">
			<join parent = "empiId" child = "empiId" />
			<join parent="visitId" child="visitId" />
		</relation>
	</relations>
</entry>
