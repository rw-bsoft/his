<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_DiseaseOmission" alias="心脑血管疾病漏报">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="sfbk" alias="是否报卡" type="int" length="1" not-null="1" display="1">
		<dic>
			<item key="1" text="未报卡" />
			<item key="2" text="已报卡" />
		</dic>
	</item>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.phoneNumber" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/> 	
	<item ref="c.manaUnitId" display="1" queryable="true"/> 	
	<item ref="c.manaDoctorId" display="1" queryable="true"/> 	
	

	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	<item id="mzhm" alias="门诊号" type="string" length="32" not-null="1" display="2"/>
	<item id="jzjg" alias="就诊机构" type="string" length="32" not-null="1" display="3"/>
	<item id="jzsj" alias="就诊时间" type="date"  display="1"></item>
	
	<item id="jbzd" alias="疾病诊断" type="string" length="32" not-null="1" display="2"></item>
	<item id="mzys" alias="门诊医生" type="string" length="32" not-null="1" display="3"/>

	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="2" formGroup="bgxx">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="2" formGroup="bgxx"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="2" formGroup="bgxx">
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
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>
