<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.phq.schemas.PHQ_HealthEducationCourse" alias="肿瘤健康教育课程表">
	<item id="courseId" alias="课程编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" endPos="9123372036854775807"/>
		</key>
	</item>
	<item id="content" alias="教育内容" type="string" length="2" not-null="true">
		<dic>
			<item key="01" text="多类别"/>
			<item key="02" text="大肠癌防治"/>
			<item key="03" text="胃癌防治"/>
			<item key="04" text="肝癌防治"/>
			<item key="05" text="肺癌防治"/>
			<item key="06" text="乳腺癌防治"/>
			<item key="07" text="宫颈癌防治"/>
			<item key="08" text="妇科肿瘤"/>
			<item key="09" text="其他疾病（不属于肿瘤）"/>
			<item key="10" text="早孕保健"/>
			<item key="11" text="学生健康教育"/>
			<item key="12" text="教师健康教育"/>
			<item key="13" text="学生疫苗接种"/>
			<item key="14" text="中小学生体检"/>
			<item key="15" text="网上健康教育问卷"/>
			<item key="16" text="公益卫生系统"/>
			<item key="17" text="汽轮厂健康教育"/>
		</dic>
	</item>
	<item id="source" alias="课程来源" type="string" length="1" not-null="true">
		<dic id="chis.dictionary.tumourHighRiskSource" />
	</item>
	<item id="type" alias="教育细节" type="string" length="20" colspan="2">
		<dic id="chis.dictionary.tumourHighRiskType" render="Checkbox" columnWidth="70" columns="6"/>
	</item>
	<item id="modality" alias="教育形式" type="string" length="50" not-null="true">
		<dic id="chis.dictionary.HealthEducationType" render="LovCombo" />
	</item>
	<item id="usedTime" alias="课程用时" type="string" length="2" not-null="true">
		<dic>
			<item key="1" text="＜10分钟"/>
			<item key="2" text="10—15分钟"/>
			<item key="3" text="16—20分钟"/>
			<item key="4" text="20—30分钟"/>
			<item key="5" text=">30分钟"/>
		</dic>
	</item>
	<item id="numberOfParticipants" alias="参加人数" type="int" length="4" display="1"/>
	<item id="aspect" alias="教育主题" type="string" length="20" colspan="2">
		<dic id="chis.dictionary.HealthEducationAspect"  render="Checkbox" columnWidth="200" columns="5"/>
	</item>
	<item id="otherAspect" alias="其他方面" type="string" length="50"/>
	<item id="startDate" alias="教育日期" type="datetime" not-null="true"/>
	<item id="speaker" alias="主讲医生" type="string" length="20" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="sponsor" alias="主办单位" type="string" length="20" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="remark" alias="备注" type="string" xtype="textarea" length="4000" display="2" colspan="2"/>
	
	<item id="createUser" alias="创建人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="创建机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="创建时间" type="datetime" xtype="datefield" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="1">
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
