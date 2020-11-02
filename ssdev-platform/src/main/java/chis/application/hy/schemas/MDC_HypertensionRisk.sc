<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionRisk"
	alias="高血压高危人群登记">
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" 		display="1" 	queryable="true"/>
	<item ref="b.birthday" 		display="1" 	queryable="true"/>
	<item ref="b.idCard" 		display="1" 	queryable="true"/>
	<item ref="b.mobileNumber" 	display="1" 	queryable="true"/>
	<item id="riskId" alias="标识列" type="string" length="16" not-null="1"
		pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="个人健康档案号" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="dataSource" alias="数据来源" type="string" length="1"
		display="1">
		<dic>
			<item key="0" text="健康档案" />
			<item key="1" text="老年人随访" />
			<item key="2" text="糖尿病随访" />
			<item key="3" text="健康检查表" />
			<item key="4" text="体检登记" />
			<item key="5" text="门诊就诊" />
			<item key="6" text="35岁首诊测压" />
			<item key="7" text="高血压疑似核实" />
			<item key="8" text="社区团队服务协议" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="constriction" alias="收缩压(mmHg)"  type="int" 
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" display="1" width="150"/>
	<item id="diastolic" alias="舒张压(mmHg)" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" display="1" width="150"/>
	<item id="registerDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="registerUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="registerUnit" alias="登记单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item ref="c.regionCode" 		display="1" 	queryable="true"/>
	<item ref="c.manaUnitId" 		display="1" 	queryable="true"/>
	<item ref="c.manaDoctorId" 	display="1" 	queryable="true"/>
	<!--<item id="firstAssessmentDate" alias="首次评估日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1" width="150" />-->
	<item id="statusCase" alias="核实情况" type="string" length="1" display="1" defaultValue="0">
		<dic>
			<item key="0" text="待核实" />
			<item key="1" text="高危确诊" />
			<item key="2" text="疑似高血压" />
			<item key="9" text="排除" />
		</dic>
	</item>
	<item id="confirmUser" alias="核实人员" type="string" length="20"
		fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="confirmUnit" alias="核实单位" type="string" length="8"
		display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="confirmDate" alias="核实日期" type="date" fixed="true"
		display="0">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="createFlag" alias="建档标志" type="string" length="1" not-null="1">
		<dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
	<item id="inputDate" alias="建档日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUser" alias="建档人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="建档单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="effectCase" alias="转归情况" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="继续管理" />
			<item key="2" text="暂时失访" />
			<item key="3" text="终止管理" />
		</dic>
	</item>
	<item id="effect" alias="终止原因" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="转高血压" />
			<item key="2" text="搬迁" />
			<item key="3" text="死亡" />
			<item key="4" text="拒绝" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="hyCreate" alias="高血压档案" type="string" length="1" display="1" virtual="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" fixed="true" display="0">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1" fixed="true" display="1">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"
		length="100" hidden="true" colspan="3" anchor="100%" display="0"/>
	<item id="deadDate" alias="死亡日期" type="date" fixed="true" display="0">
	</item>
	<item id="closeUser" alias="注销人" type="string" length="20" fixed="true"
		display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="closeUnit" alias="注销单位" type="string" length="8"
		display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="closeDate" alias="注销日期" type="date" fixed="true"
		display="1">
	</item>

	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>
