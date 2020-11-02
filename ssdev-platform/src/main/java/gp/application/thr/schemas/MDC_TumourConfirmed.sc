<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.thr.schemas.MDC_TumourConfirmed" alias="肿瘤确诊人群表">
	<item id="phrId" alias="档案编号" type="string" length="32" not-null="1" generator="assigned" pkey="true" display="1" width="150"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="c.regionCode" 	display="1" 	queryable="true"/> 
	<item ref="c.signFlag" 	display="1" 	queryable="true"/> 
  	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="highRiskSource" alias="高危来源" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.tumourHighRiskSource"/>
	</item>
	<item id="confirmedSource" alias="确诊来源" type="string" length="1" not-null="1" fixed="true" defaultValue="2">
		<dic >
			<item key="1" text="初筛"/>
			<item key="2" text="高危人群"/>
		</dic>
	</item>
	<item id="confirmedGist" alias="确诊依据" type="string" length="50" not-null="1" colspan="3">
		<dic id="chis.dictionary.tumourDiagnosisGist"  render="LovCombo"/>
	</item>
	<item id="confirmedDate" alias="确诊日期" type="date" not-null="1" defaultValue="%server.date.date"/>
	<item id="confirmedHospital" alias="确诊医院" type="string" length="50" not-null="1"  colspan="2"/>
	<item id="cancerStage" alias="确诊期别" type="string" length="1" not-null="1" colspan="3">
		<dic id="chis.dictionary.tumourClinicStage" render="Radio" colWidth="80" columns="6"/>
	</item>
	<item id="T" alias="T" type="string" not-null="1" length="10"/>
	<item id="N" alias="N" type="string" not-null="1" length="10"/>
	<item id="M" alias="M" type="string" not-null="1" length="10"/>
	<item id="reviewDate" alias="评审日期" type="date" display="1"/>
	<item id="reviewSpecialist" alias="评审专家" type="string" length="100" display="1"/>
	<item id="year" alias="管理年度" type="int" length="4" display="1"/>
	<item id="notification" alias="是否传报" type="string" length="1" display="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="nature" alias="性质" type="string" length="1" not-null="1" defaultValue="4" fixed="true" display="1">
		<dic id="chis.dictionary.tumourNature"/>
	</item>
	<item id="status" alias="状态" type="string" length="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1" display="0">
		<dic>
			<item key="1" text="死亡"/>
			<item key="2" text="迁出"/>
			<item key="3" text="失访"/>
			<item key="4" text="拒绝"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" display="0"/>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
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
		<relation type="children" entryName="gp.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>
