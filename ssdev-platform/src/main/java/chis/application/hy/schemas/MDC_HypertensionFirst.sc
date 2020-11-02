<?xml version="1.0" encoding="UTF-8"?>
<entry alias="首诊测压">
	<item id="recordNumber" alias="编号" type="string" length="16"
		pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPI" type="string" length="32"
		hidden="true" /><!-- 
	<item id="manaUnitId" alias="管辖机构" type="string" length="20">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item> -->
	<item ref="b.manaDoctorId" display="1" queryable="true" />
	<item id="postHypertension" alias="高血压既往史" type="string" length="1"
		width="110">
		<dic id="chis.dictionary.ifHypertensionHis" />
	</item>
	<item id="diagnosisType" alias="核实结果" type="string" length="1">
		<dic id="chis.dictionary.hypertensionResult" />
	</item>
	<item id="diagnosisDate" alias="确诊日期" type="date" />
	<item id="diagnosisDoctor" alias="确诊医生" type="string" length="20">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="diagnosisUnit" alias="确诊机构" type="string" length="20">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="diagnosisUnit" alias="确诊机构" type="string" length="20">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="hypertensionFirstDate" alias="首诊测压时间" type="date"
		width="110">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="empiId" child="empiId" />
		</relation>
		<relation type="child" entryName="chis.application.hy.schemas.MDC_HypertensionFirstDetail">
			<join parent="recordNumber" child="recordNumber"/>
		</relation>
	</relations>

</entry>
