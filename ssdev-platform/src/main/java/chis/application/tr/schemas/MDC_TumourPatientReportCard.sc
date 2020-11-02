<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourPatientReportCard" alias="肿瘤患者报告卡" sort="a.empiId asc,a.createDate desc">
	<item id="TPRCID" alias="报告编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案号" type="string" length="32" fixed="true" width="150"/>
	<item id="empiId" alias="empiId" type="string" length="32" hidden="true"/>
	<item id="highRiskType" alias="高危类别" type="string" length="2" display="0">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="c.regionCode" 	display="1" 	queryable="true"/> 
	
	<item id="informStatus" alias="告知病人病情" type="string" length="1" not-null="1" width="100" colspan="2">
		<dic render="Radio" colWidth="50" columns="3">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
			<item key="3" text="不详"/>
		</dic>
	</item>
	<item id="agreeVisit" alias="是否同意随访" type="string" length="1" not-null="1" defaultValue="y" width="100">
		<dic id="chis.dictionary.yesOrNo" render="Radio" colWidth="50" columns="2"/>
	</item>
	<item id="patientNumber" alias="门诊号" type="string" length="50"/>
	<item id="admissionNumber" alias="住院号" type="string" length="50"/>
	<item id="tumourName" alias="肿瘤诊断名称" type="string" xtype="lookupfieldex" length="100" not-null="1" width="100" colspan="2"/>
	<item id="ICD10Code" alias="ICD-10编码" type="string" length="50" fixed="true"/>
	<item id="pathologyName" alias="病理诊断名称" type="string" length="100" width="100"/>
	<item id="ICDO3Code" alias="肿瘤学分类代码" type="string" length="50" width="100"/>
	<item id="pathologyNumber" alias="病理号" type="string" length="20"/>
	<item id="T" alias="T" type="string" length="10" not-null="1">
		<dic id="chis.dictionary.TumourT" />
	</item>
	<item id="N" alias="N" type="string" length="10" not-null="1">
		<dic id="chis.dictionary.TumourN" />
	</item>
	<item id="M" alias="M" type="string" length="10" not-null="1">
		<dic id="chis.dictionary.TumourM" />
	</item>
	<item id="clinicStageCode" alias="临床分期代码" type="string" length="1" not-null="1" width="100" colspan="3">
		<dic id="chis.dictionary.tumourClinicStage" render="Radio" colWidth="80" columns="6"/>
	</item>
	<item id="diagnosisDate" alias="诊断日期" type="date" not-null="1" defaultValue="%server.date.date"/>
	<item id="diagnosisGist" alias="诊断依据" type="string" not-null="1" length="50" colspan="2">
		<dic id="chis.dictionary.tumourDiagnosisGist"  render="LovCombo"/>
	</item>
	<item id="bodyParts" alias="瘤所在身体部位" type="string" length="30" display="0"/>
	<item id="tumourSite" alias="肿瘤位置" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="左侧"/>
			<item key="2" text="右侧"/>
			<item key="3" text="双侧"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="isDeath" alias="是否死亡" type="string" length="1" defaultValue="n" fixed="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="deathDate" alias="死亡日期" type="date"/>
	<item id="deathCause" alias="根本死因代码" type="string" length="20" display="0"/>
	<item id="reportDate" alias="报告日期" type="date" not-null="1"/>
	<item id="reportDoctor" alias="报告医师" type="string" length="20" not-null="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="reportUnit" alias="报告机构" type="string" length="20" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="manaDoctorId" alias="责任医生" not-null="1" update="false" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" fixed="true" width="180" queryable="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="status" alias="状态" type="string" length="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="dieFlag" alias="死补标志" type="string" length="1" defaultValue="n" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
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
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>
