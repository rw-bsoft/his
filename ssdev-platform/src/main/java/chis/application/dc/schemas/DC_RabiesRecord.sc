<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dc.schemas.DC_RabiesRecord" alias="狂犬病档案" sort="a.createDate desc">
	<item id="rabiesId" pkey="true" alias="记录序号" type="string" width="160"
		length="16" not-null="1" hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" />
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.contactPhone" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	<item ref="c.regionCode_text" display="0"/>
	<item id="phrId" alias="健康档案号" type="string" length="30" width="160"
		display="0" />
	<item id="discoverDate" alias="暴露日期" type="date" queryable="true" not-null="1" maxValue="%server.date.today"/>
	<item id="treatmentDate" alias="就诊日期" type="date" queryable="true" maxValue="%server.date.today"/>
	<item id="animalInfo" alias="动物情况" type="string" length="1"
		not-null="1">
		<dic>
			<item key="1" text="流浪" />
			<item key="2" text="栓养" />
			<item key="3" text="敞养" />
		</dic>
	</item>
	<item id="immuneYN" alias="一年内进行免疫" type="string" length="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="injuryPosition" alias="伤口部位" type="string" length="20" not-null="1" colspan="2"
		width="60" >
		<dic render="LovCombo">
			<item key="1" text="头面部" />
			<item key="2" text="躯干部" />
			<item key="3" text="下肢" />
			<item key="4" text="右臂" />
			<item key="5" text="左臂" />
			<item key="6" text="手指" />
		</dic>
	</item>
	<item id="injuryDegree" alias="受伤程度" type="string" length="1"
		not-null="1">
		<dic>
			<item key="1" text="Ⅰ度 " />
			<item key="2" text="Ⅱ度 " />
			<item key="3" text="Ⅲ度 " />
		</dic>
	</item>
	<item id="injuryAnimal" alias="致伤动物" type="string" length="1"
		not-null="1">
		<dic>
			<item key="1" text="猫" />
			<item key="2" text="犬" />
			<item key="3" text="其他" />
		</dic>
	</item>
	<item id="others" alias="其他" type="string" length="30" />
	<item id="woundTreatment" alias="伤口处理" type="string" length="1"
		not-null="1">
		<dic>
			<item key="1" text="自家处理" />
			<item key="2" text="医疗机构" />
			<item key="3" text="未处理" />
		</dic>
	</item>
	<item id="vaccinationMill" alias="疫苗接种(厂家)" type="string" length="100"
		width="110" not-null="1"/>
	<item id="vaccinationBathNum" alias="疫苗接种(批号)" type="string"
		width="110" length="50" not-null="1"/>
	<item id="antiserumMill" alias="抗血清（厂家）" type="string" length="100"
		width="100" not-null="1"/>
	<item id="antiserumBathNum" alias="抗血清（批号）" type="string" length="50"
		width="100" not-null="1"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="createUser" alias="录入医生" type="string" length="20" update="false"
		queryable="true" defaultValue="%user.userId" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入日期" type="date" queryable="true" update="false"
		defaultValue="%server.date.today" fixed="true" >
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date" defaultValue="%server.date.today"
		fixed="true" display="0">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="status" alias="档案状态" type="string" defaultValue="0"
		length="1" display="0">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="注销" />
		</dic>
	</item>
	<item id="closedUnit" alias="结案单位" type="string" length="20"
		fixed="true" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="closedDoctor" alias="结案医师" type="string" length="20"
		fixed="true" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="closedDate" alias="结案日期" type="date" fixed="true" hidden="true"/>

	<item id="closeFlag" alias="结案标识" type="string" length="1"
		display="1" defaultValue="0">
		<dic>
			<item key="0" text="未结案" />
			<item key="1" text="已结案" />
		</dic>
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" hidden="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="未审核" />
		</dic>
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1"
		hidden="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="6" text="作废" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"
		length="100" display="0" /> 
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>