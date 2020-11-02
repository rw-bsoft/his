<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourHighRiskListView" tableName="chis.application.tr.schemas.MDC_TumourHighRisk" alias="高危人群表" sort="a.empiId asc,a.createDate desc">
	<item id="THRID" alias="高危编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="32" queryable="true" width="160"  fixed="true"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	
	<item id="age" alias="年龄" type="int" width="30" display="1"/>
	
	<item ref="b.mobileNumber" alias="联系电话" display="1" /> 
	
  	<item id="year" alias="管理年度" type="int" length="4" not-null="1" defaultValue="%server.date.year" queryable="true" display="1">
		<dic id="chis.dictionary.years" />
	</item>
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="turnHighRiskDate" alias="转高危日期" type="date" defaultValue="%server.date.date" not-null="1"/>
	<item id="turnHighRiskDoctor" alias="转高危医生" type="string" length="10" not-null="1" defaultValue="%user.userId" queryable="true" width="180">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="highRiskSource" alias="高危来源" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="nature" alias="性质" type="string" length="1" not-null="1" defaultValue="3" fixed="true">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="highRiskFactor" alias="高危因素" type="string" xtype="textarea" length="300" colspan="2"/>
	<item id="screeningPositive" alias="初筛阳性" type="string" lenght="1" defaultValue="0" fixed="true">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<item id="screeningSickness" alias="初筛疾病" type="string" lenght="1" defaultValue="0" fixed="true">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>

	<item id="yearVisitNumber" alias="本年随访次数" type="int" width="100" display="1"/>
	<item id="yearVisitPlanNumber" alias="本年计划次数" type="int" width="100" display="1"/>
	<item id="visitNumber" alias="随访次数" type="int" display="1"/>

	<item id="createStatus" alias="建卡标志" type="string" length="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="未建档"/>
			<item key="1" text="已建档"/>
		</dic>
	</item>
	<item id="createCardUser" alias="建卡医生" type="string" length="20" queryable="true" defaultValue="%user.userId" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="createCardUnit" alias="建卡人机构" type="string" length="20" width="320" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="createCardDate" alias="建卡时间" type="datetime" queryable="true" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date"/>
	<item id="timelyCreation" alias="建卡及时" type="string" defaultValue="0" display="1">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	
	<item id="managerGroup" alias="管理组别" type="string" length="1" queryable="true"  not-null="1">
		<dic id="chis.dictionary.tumourManagerGroup"/>
	</item>
	<item ref="c.regionCode" 	display="1" 	queryable="true"/> 
	<item id="manaDoctorId" alias="责任医生" not-null="1" update="false" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" fixed="true" width="180" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="highRiskMark" alias="高危标志" type="string" length="1" defaultValue="n" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="criterionMark" alias="规范标志" type="string" length="1" defaultValue="n" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item> 
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" display="1">
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
	
	<item id="createUser" alias="录入医生" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
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
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>