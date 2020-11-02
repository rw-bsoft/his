<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_QuestionnaireCriterion" alias="问卷标准" sort="year desc,createDate desc">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="hrcId" alias="高危标准ID" type="string" length="16" display="0"/>
	<item id="year" alias="管理年度" type="int" length="4" not-null="1" queryable="true" defaultValue="%server.date.year">
		<dic id="chis.dictionary.years" />
	</item>
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="highRiskSource" alias="高危来源" type="string" length="2" not-null="1" defaultValue="2">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="criterionType" alias="标准类别" type="string" length="2" defaultValue="1" not-null="1" fixed="true" queryable="true">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="QMId" alias="问卷模版编号" type="string" length="16" not-null="1" display="0"/>
	<item id="masterplateName" alias="模版名称" type="string" xtype="lookupfieldex" length="100" not-null="1" width="300"/>
	<item id="criterionExplain" alias="标准判别" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="所有"/>
			<item key="2" text="可疑"/>
		</dic>
	</item>
  
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true"  display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" width="130"  display="1">
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
		fixed="true" width="130" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
