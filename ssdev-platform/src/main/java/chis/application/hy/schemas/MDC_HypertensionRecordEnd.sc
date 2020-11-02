<?xml version="1.0" encoding="UTF-8"?>
<entry alias="高血压档案" sort="a.createDate desc" entityName="chis.application.hy.schemas.MDC_HypertensionRecord">
	<item id="phrId" alias="档案编号" type="string" length="30" width="160" pkey="true" queryable="true" display="0"/>
	<item ref="b.personName" 	display="1" 	queryable="true"/>
	<item ref="b.sexCode" 		display="1" 	queryable="true"/>
	<item ref="b.birthday" 		display="1" 	queryable="true"/>
	<item ref="b.mobileNumber" 	display="1" 	queryable="true"/>
	<item id="hypertensionGroup" alias="管理分组" 	display="1" type="string" length="1" fixed="true" queryable="true">
		<dic id="chis.dictionary.hyperGroupExt"/>
	</item>
	<item id="createUnit" alias="建档机构" type="string" display="1" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="endCheck" alias="注销核实情况" type="string" defaultValue="1"
		length="1"  display="1">
		<dic>
			<item key="1" text="待核实"/>
			<item key="2" text="已核实"/>
		</dic>
	</item>
	<item id="visitEffect" alias="转归" type="string" not-null="1" defaultValue="1"  colspan="3"
		length="1">
		<dic>
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item id="visitDate" alias="终止日期" not-null="true" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false" queryable="true"  display="1"/>
	<item id="noVisitReason" alias="终止原因" type="string" length="1" display="1"
		fixed="true"> 
		<dic> 
			<item key="1" text="死亡"/>  
			<item key="2" text="迁出"/>  
			<item key="3" text="失访"/> 
			<item key="4" text="拒绝"/> 
		</dic> 
	</item>  
	<item id="cancellationReason" alias="注销原因" type="string" length="1" fixed="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	
	<item ref="b.idCard" 		display="1" 	queryable="true"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" not-null="1" display="0"/>
	<item id="manaDoctorId" alias="责任医生" not-null="1" update="false" 	display="1" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" fixed="true" 	display="1" width="180" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"
		length="100" colspan="2" anchor="100%"  display="2"/>
	<item id="deadDate" alias="死亡日期" type="date" fixed="true" display="2" maxValue="%server.date.today"/>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		colspan="2" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" fixed="true" colspan="2"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" fixed="true" display="0">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
			<item key="2" text="注销核实中"/>
		</dic>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>