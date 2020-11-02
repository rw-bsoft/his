<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourScreeningCheckResult" alias="初筛检查表" sort="a.recordId asc">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" endPos="9123372036854775807"/>
		</key>
	</item>
	<item id="screeningId" alias="初筛记录号" type="string" length="16" display="0"/>
	<item id="planId" alias="随访计划ID" type="string" length="16" display="0"/>
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	<item id="itemId" alias="项目ID" type="string" length="16" display="0"/>
	<item id="checkItem" alias="检查项目" type="string" xtype="lookupfieldex" length="100" not-null="1" queryable="true"/>
	<item id="checkResult" alias="检查结果" type="string" length="30" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourCheckResult"/>
	</item>
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="checkDate" alias="检查日期" type="date" defaultValue="%server.date.date" not-null="1" queryable="true"/>
	<item id="checkHospital" alias="检查医院" type="string" length="30"/>
	<item id="checkDoctor" alias="检查医生" type="string" length="20" width="180" defaultValue="%user.userId" not-null="1" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="criterionType" alias="项目性质" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="初筛"/>
			<item key="2" text="追踪"/>
		</dic>
	</item>
	<item id="remark" alias="备注信息" type="string" length="300" colspan="2"/>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="1">
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
