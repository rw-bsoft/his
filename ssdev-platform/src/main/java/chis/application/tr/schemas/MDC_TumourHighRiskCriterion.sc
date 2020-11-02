<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourHighRiskCriterion" alias="初筛转高危标准表">
	<item id="hrcId" alias="高危核准ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="year" alias="年度" type="int" length="4" not-null="1" width="50" defaultValue="%server.date.year" colspan="2">
		<dic id="chis.dictionary.years" />
	</item>
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="highRiskName" alias="高危名称" type="string" length="100" not-null="1" width="200" colspan="2"/>
	<item id="judgeType" alias="判断类型" type="string" length="2" defaultValue="3" not-null="1" colspan="2">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="highRiskSource" alias="高危来源" type="string" length="2" not-null="1" width="60" colspan="2">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="highRiskFactor" alias="高危因素" type="string" length="1" not-null="1" colspan="2">
		<dic>
			<item key="1" text="问卷症状--有"/>
			<item key="2" text="家族肿瘤史--有"/>
			<item key="3" text="肿瘤疾病史--有"/>
		</dic>
	</item>
	<item id="PSItemRelation" alias="初筛检查项目关系" type="string" length="1" defaultValue="1">
		<dic>
			<item key="1" text="或者"/>
			<item key="2" text="并且"/>
		</dic>
	</item>
	<item id="traceItemRelation" alias="追踪检查项目关系" type="string" length="1" defaultValue="1">
		<dic>
			<item key="1" text="或者"/>
			<item key="2" text="并且"/>
		</dic>
	</item>
    
	<item id="createUser" alias="录入人" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="100" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1" width="160">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1" width="150">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
