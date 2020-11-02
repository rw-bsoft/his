<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tcm.schemas.TCM_TCMRegister" alias="中医登记">
	<item id="registerId" alias="登记主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="个人主索引" type="string" length="32" hidden="true"/>
	<item id="IFCQRID" alias="问卷主表ID" type="string" length="16" hidden="true"/>
	
	<item ref="b.personName" display="1" queryable="true" locked="true"/>
	<item ref="b.sexCode" display="1" queryable="true" locked="true"/>
	<item ref="b.birthday" display="1" queryable="true" locked="true"/>
	<item ref="b.idCard" display="1" queryable="true" locked="true"/>
	
	<item id="yangxz" alias="阳虚质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="yinxz" alias="阴虚质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="qixz" alias="气虚质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="tansz" alias="痰湿质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="shirz" alias="湿热质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="xueyz" alias="血瘀质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="tebz" alias="特禀质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="qiyz" alias="气郁质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="hepz" alias="平和质" type="string" length="1" defaultValue="3">
		<dic id="chis.dictionary.TCMHabitusIdentificationResults" />
	</item>
	<item id="habitusSummarizeCode" alias="体质综述代码" type="string" length="50" display="0">
		<dic id="chis.dictionary.TCMHabitusSummarize" />
	</item>
	<item id="habitusSummarizeText" alias="体质综述" type="string" length="100" width="200" display="1"/>
	
	<item id="visit" alias="望" type="string" length="200" display="2"/>
	<item id="tongue" alias="舌" type="string" length="100" display="2"/>
	<item id="coatedTongue" alias="苔" type="string" length="100" display="2"/>
	<item id="smell" alias="闻" type="string" length="200" display="2"/>
	<item id="asking" alias="问" type="string" length="200" display="2"/>
	<item id="pulseTaking" alias="切" type="string" length="200" display="2"/>
	
	<item id="registerDate" alias="登记日期" type="date" update="false" defaultValue="%server.date.date" maxValue="%server.date.date"/>
	<item id="registerDoctor" alias="登记医生" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="registerUnitId" alias="登记单位" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="status" alias="状态" type="string" length="1" defaultValue="0" display="1">
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
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>
