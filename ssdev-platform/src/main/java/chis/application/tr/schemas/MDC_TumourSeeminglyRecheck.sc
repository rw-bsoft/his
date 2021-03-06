<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourSeeminglyRecheck" tableName="chis.application.tr.schemas.MDC_TumourSeemingly" alias="疑似人群复合">
	<item id="recordId" alias="记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true" display="1">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="year" alias="管理年度" type="int" length="4" not-null="1" display="1" defaultValue="%server.date.year">
		<dic id="chis.dictionary.years" />
	</item>
	<item id="physicalId" alias="体检编号" type="string" length="16" display="0"/>
	<item id="seeminglyDte" alias="疑似日期" type="date" not-null="1" defaultValue="%server.date.date" queryable="true" display="1"/>
	<item id="highRiskFactor" alias="高危因素" type="string" xtype="textarea" length="300" display="1" colspan="3"/>
	<item id="nature" alias="性质" type="string" length="2" defaultValue="0" fixed="true" display="1">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="recheckStatus" alias="复核标识" type="string" length="2" defaultValue="n" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="recheckResult" alias="复核结果" not-null="1" type="string" length="2" queryable="true" display="2">
		<dic>
			<item key="1" text="转归正常"/>
			<item key="2" text="转归初筛"/>
			<item key="3" text="转诊"/>
		</dic>
	</item>
	<item id="recheckDoctor" alias="复核医生" not-null="1" type="string" length="20" defaultValue="%user.userId" queryable="true" display="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="recheckDate" alias="复核日期" not-null="1" type="date" defaultValue="%server.date.date" queryable="true" display="2"/>
	<item id="transferTreatment" alias="是否转诊" type="string" length="1" not-null="1" defaultValue="n" queryable="true" display="1" colspan="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false"  defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>