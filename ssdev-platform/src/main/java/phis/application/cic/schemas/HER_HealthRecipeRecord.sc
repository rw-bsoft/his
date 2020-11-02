<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="HER_HealthRecipeRecord" alias="健康处方维护">
	<item id="id" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="childId" alias="分表主键" type="string" length="16" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="recordId" alias="处方主键" type="string" length="16" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="diagnoseId" alias="疾病序号" type="string" length="16" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="wayId" alias="业务主键" type="string" length="32" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="phrId" alias="档案编号" type="string" length="30" fixed="true" width="160" notDefaultValue="true"/>
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1"/>
	<item ref="b.birthday" display="1"/>
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1"/>
	<item ref="c.regionCode" display="1" queryable="true" />
	<item ref="c.manaUnitId" display="1" queryable="true" />
	<item ref="c.manaDoctorId" display="1"/> 
	<item id="recipeName" alias="健康处方名称" type="string" length="100" width="200" colspan="2"/>
	<item id="examineUnit" alias="就诊机构" type="string" queryable="true" length="50" width="100"/>
	<item id="diagnoseName" alias="诊断名称" type="string" queryable="true" length="100" width="200"/>
	<item id="ICD10" alias="ICD10" type="string" length="50" queryable="true" width="100" update="false"/>
	<item id="guideDate" alias="指导日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today">
	</item>
	<item id="guideUser" alias="指导医生" type="string" queryable="true" length="20" width="150" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	
	<item id="guideWay" alias="指导途径" type="string" queryable="true" length="2">
		<dic>
			<item key="01" text="门诊"/>
			<item key="02" text="高血压随访"/>
			<item key="03" text="糖尿病随访"/>
			<item key="04" text="肿瘤随访"/>
			<item key="05" text="精神病随访"/>
			<item key="06" text="计划执行"/>
			<item key="07" text="高血压高危随访"/>
		</dic>
	</item>
	<item id="healthTeach" alias="指导建议" type="string" height="260" length="2000" width="200" colspan="3" xtype="textarea">
	</item>
	<item id="inputUnit" alias="录入机构" type="string" update="false" length="20" fixed="true" defaultValue="%user.manageUnit.id" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUser" alias="录入人" type="string" length="20" width="150" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="150"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" width="150"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
  
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
