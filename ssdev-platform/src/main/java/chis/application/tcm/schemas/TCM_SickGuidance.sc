<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tcm.schemas.TCM_SickGuidance" alias="中医指导">
	<item id="SGID" alias="指导记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="个人主索引" type="string" length="32" hidden="true"/>
	<item id="registerId" alias="登记主键" type="string" length="16" hidden="true"/>
	
	<item ref="b.personName" display="1" queryable="true" locked="true"/>
	<item ref="b.sexCode" display="1" queryable="true" locked="true"/>
	<item ref="b.birthday" display="1" queryable="true" locked="true"/>
	<item ref="b.idCard" display="1" queryable="true" locked="true"/>
	
	<item id="ageGroupType" alias="年龄段分类" type="string" length="1" width="120" colspan="4" fixed="true">
		<dic id="chis.dictionary.TCMAgeGroupType" render="Radio" colWidth="120" columns="4"/>
	</item>
	<item id="habitusType" alias="体质分类" type="string" length="200" colspan="4" fixed="true">
		<dic id="chis.dictionary.TCMHabitusSummarize"  render="TreeCheck" onlyLeafCheckable="true"  checkModel ="childCascade"/>
	</item>
	<item id="intendedPopulation" alias="适应人群" type="string" length="20" colspan="4">
		<dic id="chis.dictionary.TCMIntendedPopulation" render="Checkbox" columnWidth="65" columns="5"/>
	</item>
	<item id="tumourType" alias="肿瘤类别" type="string" length="20" colspan="4" fixed="true">
		<dic id="chis.dictionary.tumourHighRiskType" render="Checkbox" columnWidth="50" columns="6"/>
	</item>
	<item id="contentType" alias="内容分类" type="string" length="20" colspan="4" not-null="1">
		<dic id="chis.dictionary.TCMContentType" render="Checkbox" columnWidth="80" columns="6"/>
	</item>
	<item id="content" alias="内容" type="string" xtype="textarea" length="4000" height="300" colspan="4"/>
	
	<item id="guidanceDoctor" alias="指导医生" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="guidanceUnitId" alias="指导医生机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="guidanceDate" alias="指导日期" type="date" update="false" defaultValue="%server.date.date" maxValue="%server.date.date"/>
	
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
	</relations>
</entry>
