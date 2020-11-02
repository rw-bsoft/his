<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRisk"
	alias="糖尿病高危人群登记">
	<item ref="b.personName" display="1" queryable="true" />
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

	<item id="fbs" alias="空腹血糖" type="double" length="6" precision="2"
		update="false" display="1"/>
	<item id="pbs" alias="餐后血糖" type="double" length="6" precision="2"
		update="false" display="1"/>

	<item id="firstAssessmentDate" alias="首次评估日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1" width="150" />
	<item id="dataSource" alias="数据来源" type="string" length="1"
		display="1">
		<dic>
			<item key="1" text="高血压评估" />
			<item key="2" text="健康检查表" />
			<item key="3" text="体检登记" />
		</dic>
	</item>
	<item id="effect" alias="转归" type="string" length="1">
		<dic>
			<item key="1" text="好转" />
			<item key="2" text="转糖尿病" />
		</dic>
	</item>
	<item id="status" alias="核实情况" type="string" length="1" display="0">
		<dic>
			<item key="0" text="待核实" />
			<item key="1" text="确诊" />
			<item key="9" text="结案" />
		</dic>
	</item>
	
	<item id="inputUnit" alias="登记单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
			<set type="exp">['$','%user.manageUnit.id']</set>
	</item>

	<item id="inputDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	
	<item id="closeUser" alias="结案人" type="string" length="20" fixed="true"
		display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="closeUnit" alias="结案单位" type="string" length="8"
		display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="closeDate" alias="结案日期" type="date" fixed="true"
		display="0">
		<set type="exp">["$","%server.date.today"]</set>
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
		<relation type="children"
			entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>
