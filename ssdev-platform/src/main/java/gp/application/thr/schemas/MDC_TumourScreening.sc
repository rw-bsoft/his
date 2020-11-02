<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.thr.schemas.MDC_TumourScreening" alias="初筛人群表">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="year" alias="年度" type="int" length="4" width="60" not-null="1" queryable="true"/>
	<item id="screeningDate" alias="初筛日期" type="date" defaultValue="%server.date.date" not-null="1">
	</item>
	<item id="screeningDoctor" alias="初筛医生" type="string" length="20" not-null="1" defaultValue="%user.userId" queryable="true" width="180">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="highRiskSource" alias="高危来源" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="nature" alias="性质" type="string" length="1" not-null="1" defaultValue="1" fixed="true">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="highRiskMark" alias="高危标志" type="string" length="1" defaultValue="n" not-null="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="criterionMark" alias="规范标志" type="string" length="1" defaultValue="n" not-null="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="highRiskFactor" alias="高危因素" type="string" xtype="textarea" length="300" colspan="2"/>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" hidden="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
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
		<set type="exp">['$','%server.date.date']</set>
	</item>
	<relations>
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
