<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hq.schemas.MDC_HighriskVisitPlan" tableName="chis.application.pub.schemas.PUB_VisitPlan" alias="高危人群随访">
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
	<item id="visitDate" alias="实际随访日期" type="date" width="100" length="1" queryable="true" />
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
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.definePhrid" display="1" queryable="true"/>
	<item ref="d.manaDoctorId" alias="责任医生" type="string" length="20"
		display="0" queryable="true" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="b.contactPhone" display="1" queryable="true" />  
	<item ref="b.phoneNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	<item ref="e.visitWay" alias="随访方式" type="string" length="1" not-null="true">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item ref="e.visitEffect" alias="转归" type="string" defaultValue="1" length="1">
		<dic>
			<item key="1" text="特征项数增加" />
			<item key="2" text="特征项数不变" />
			<item key="3" text="特征项数降低" />
			<item key="4" text="转为慢性病患者" />
			<item key="5" text="迁出" />
			<item key="6" text="死亡" />
			<item key="7" text="其它" />
		</dic>
	</item>
	<item ref="e.noVisitReason" alias="原因" type="string" length="100" fixed="true" display="0"> 
		<dic> 
			<item key="1" text="死亡"/>  
			<item key="2" text="迁出"/>  
			<item key="3" text="失访"/> 
			<item key="4" text="拒绝"/> 
		</dic> 
	</item>   
	<item ref="e.interventions" alias="干预措施" type="string" length="64" colspan="1" defaultValue="1">
		<dic render="LovCombo">
			<item key="1" text="定期监测血压" />
			<item key="2" text="减少吸烟量或戒烟" />
			<item key="3" text="定期监测血糖" />
			<item key="4" text="定期监测血脂" />
			<item key="5" text="减轻或控制体重" />
			<item key="6" text="合理膳食，减少钠盐摄入" />
			<item key="7" text="适量运动" />
			<item key="8" text="缓解心理压力" />
		</dic>
	</item>
	<item ref="e.constriction" alias="收缩压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item ref="e.diastolic" alias="舒张压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item ref="e.isSmoke" alias="是否吸烟" type="string" length="1" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item ref="e.smokeCount" alias="日吸烟量" type="int" />
	<item ref="e.fbs" alias="空腹血糖(mmol/L)" type="double" length="4" minValue="1" maxValue="20" />
	<item ref="e.tc" alias="总胆固醇(mmol/L)" type="double" precision="2" length="4" minValue="1" maxValue="20" />
	<item ref="e.waistLine" alias="腰围(cm)" type="double" length="4" minValue="40" maxValue="200" />
	<item ref="e.nextDate" alias="下次预约时间" type="date" />
	<item ref="e.visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" not-null="1" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item ref="e.visitUnit" alias="随访机构" type="string" length="20" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>


	<item ref="e.inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item ref="e.inputUser" alias="录入员" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item ref="e.inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<item ref="d.manaUnitId" alias="管辖机构" type="string" length="20"
		display="0" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"  />
	</item>
	<item ref="e.lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item ref="e.lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item ref="e.lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
		<relation type="children" entryName="chis.application.hq.schemas.MDC_HighRiskRecord">
			<join parent="empiId" child="empiId" />
		</relation>
		<relation type="children" entryName="chis.application.hq.schemas.MDC_HighRiskVisit">
			<join parent = "empiId" child = "empiId" />
			<join parent="visitId" child="visitId" />
		</relation>	
	</relations>
</entry>
