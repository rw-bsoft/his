<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourScreeningListView" tableName="chis.application.tr.schemas.MDC_TumourScreening" alias="初筛人群表(批量)" sort="b.personName asc ,b.idCard asc">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	<item id="phrId" alias="档案编号" type="string" length="32" display="0"/>
	
	<item ref="b.personName" display="1" locked="true"/>
	<item ref="b.sexCode" display="1" locked="true"/>
	<item ref="b.birthday" display="1" locked="true"/>
	<item ref="b.idCard" display="1" locked="true"/>
	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" locked="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="year" alias="年度" type="int" length="4" width="60" not-null="1" defaultValue="%server.date.year">
		<dic id="chis.dictionary.years" />
	</item>
	<item id="screeningDate" alias="初筛日期" type="date" defaultValue="%server.date.date" not-null="1">
	</item>
	<item id="screeningDoctor" alias="初筛医生" type="string" length="20" not-null="1" defaultValue="%user.userId" width="180">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="highRiskSource" alias="初筛来源" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="nature" alias="性质" type="string" length="1" not-null="1" defaultValue="1" fixed="true">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="highRiskMark" alias="高危标志" type="string" length="1" defaultValue="n" not-null="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="criterionMark" alias="规范标志" type="string" length="1" defaultValue="n" not-null="1" display="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>