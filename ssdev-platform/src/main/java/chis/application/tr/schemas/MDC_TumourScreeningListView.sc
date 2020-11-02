<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourScreeningListView" tableName="chis.application.tr.schemas.MDC_TumourScreening" alias="初筛人群表" sort="b.personName asc ,b.idCard asc">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	<item id="phrId" alias="档案编号" type="string" length="32" display="0"/>
	
	<item ref="b.personName" display="1" queryable="true" locked="true"/>
	<item ref="b.sexCode" display="1" queryable="true" locked="true"/>
	<item ref="b.birthday" display="1" queryable="true" locked="true"/>
	<item ref="b.idCard" display="1" queryable="true" locked="true"/>
	
	<item id="age" alias="年龄" type="int" width="30" display="1"/>
	
	<item ref="b.mobileNumber" alias="联系电话" display="1" />
	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true" locked="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="year" alias="年度" type="int" length="4" width="60" not-null="1" queryable="true" defaultValue="%server.date.year">
		<dic id="chis.dictionary.years" />
	</item>
	<item id="TQDate" alias="问卷日期" type="date" defaultValue="%server.date.date" not-null="1" display="1"/>
	<item id="screeningDate" alias="初筛日期" type="date" defaultValue="%server.date.date" not-null="1">
	</item>
	<item id="screeningDoctor" alias="初筛医生" type="string" length="20" not-null="1" defaultValue="%user.userId" queryable="true" width="180">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="screeningUnit" alias="初筛机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="highRiskSource" alias="初筛来源" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="nature" alias="性质" type="string" length="1" not-null="1" defaultValue="1" queryable="true" fixed="true">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="highRiskMark" alias="高危标志" type="string" length="1" defaultValue="n" not-null="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="highRiskFactor" alias="高危因素" type="string" xtype="textarea" length="300" colspan="2"/>
	<item id="criterionMark" alias="规范标志" type="string" length="1" defaultValue="n" not-null="1" display="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="isTrace" alias="是否追踪" type="string" lenght="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<item id="traceNorm" alias="追踪规范" type="string" lenght="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<item id="questionnairePositive" alias="问卷阳性" type="string" lenght="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="阴性"/>
			<item key="1" text="阳性"/>
		</dic>
	</item>
	<item id="checkPositive" alias="检查阳性" type="string" lenght="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="阴性"/>
			<item key="1" text="阳性"/>
		</dic>
	</item>
	<item id="syntheticalPositive" alias="综合阳性" type="string" lenght="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="阴性"/>
			<item key="1" text="阳性"/>
		</dic>
	</item>
	<item id="checkItem1" alias="检查项目1" type="string" xtype="lookupfieldex" length="100" not-null="1" queryable="true" display="1"/>
	<item id="checkResult1" alias="检查结果1" type="string" length="30" not-null="1" queryable="true" display="1">
		<dic id="chis.dictionary.tumourCheckResult"/>
	</item>
	<item id="checkItem2" alias="检查项目2" type="string" xtype="lookupfieldex" length="100" not-null="1" queryable="true" display="1"/>
	<item id="checkResult2" alias="检查结果2" type="string" length="30" not-null="1" queryable="true" display="1">
		<dic id="chis.dictionary.tumourCheckResult"/>
	</item>
	<item id="checkItem3" alias="检查项目3" type="string" xtype="lookupfieldex" length="100" not-null="1" queryable="true" display="1"/>
	<item id="checkResult3" alias="检查结果3" type="string" length="30" not-null="1" queryable="true" display="1">
		<dic id="chis.dictionary.tumourCheckResult"/>
	</item>
	<item id="checkItem4" alias="检查项目4" type="string" xtype="lookupfieldex" length="100" not-null="1" queryable="true" display="1"/>
	<item id="checkResult4" alias="检查结果4" type="string" length="30" not-null="1" queryable="true" display="1">
		<dic id="chis.dictionary.tumourCheckResult"/>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" hidden="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1" display="1">
		<dic>
			<item key="1" text="死亡"/>
			<item key="2" text="迁出"/>
			<item key="3" text="失访"/>
			<item key="4" text="拒绝"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" display="1"/>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
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
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>