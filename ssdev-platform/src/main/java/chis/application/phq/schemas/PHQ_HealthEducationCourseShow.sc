<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.phq.schemas.PHQ_HealthEducationCourseShow" tableName="chis.application.phq.schemas.PHQ_HealthEducationCourse" alias="肿瘤健康教育课程表">
	<item id="courseId" alias="课程编号" type="string" length="16" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" endPos="9123372036854775807"/>
		</key>
	</item>
	<item id="modality" alias="教育形式" type="string" length="50" fixed="true">
		<dic id="chis.dictionary.HealthEducationType" render="LovCombo" />
	</item>
	<item id="content" alias="教育内容" type="string" length="2000"  fixed="true"/>
	<item id="startDate" alias="教育日期" type="datetime" minValue="%server.date.date" fixed="true"/>
	<item id="source" alias="课程来源" type="string" length="1"  fixed="true">
		<dic id="chis.dictionary.tumourHighRiskSource" />
	</item>
	<item id="usedTime" alias="课程用时" type="string" length="50" fixed="true"/>
	<item id="type" alias="教育细节" type="string" length="20" display="0">
		<dic id="chis.dictionary.tumourHighRiskType" render="Checkbox" columnWidth="70" columns="6"/>
	</item>
	<item id="aspect" alias="教育主题" type="string" length="20" display="0">
		<dic id="chis.dictionary.HealthEducationAspect"  render="Checkbox" columnWidth="200" columns="5"/>
	</item>
	<item id="otherAspect" alias="其他方面" type="string" length="50" display="0"/>
	<item id="speaker" alias="主讲医生" type="string" length="20" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="sponsor" alias="主办单位" type="string" length="20" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="remark" alias="备注" type="string" xtype="textarea" length="4000" height="160" fixed="true" display="2" />
	
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
</entry>