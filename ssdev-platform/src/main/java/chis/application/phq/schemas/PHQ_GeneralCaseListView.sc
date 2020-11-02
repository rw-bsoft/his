<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.phq.schemas.PHQ_GeneralCaseListView" tableName="chis.application.phq.schemas.PHQ_GeneralCase" alias="健康问卷—— 一般情况" sort="a.idCard asc">
	<item id="gcId" alias="记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" endPos="9123372036854775807"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="phrId" alias="档案编号" type="string" length="32" display="0"/>
	<item id="courseId" alias="课程号" type="string" length="16" display="0"/>
	<item id="JZXH" alias="就诊序号" type="double" length="18" display="0"/>
	<item id="masterplateType" alias="问卷模版类型" type="string" length="2" display="0">
		<dic id="chis.dictionary.masterplate"/>
	</item>
	<item id="masterplateId" alias="问卷模板" type="string" length="16" display="0">
		<dic id="chis.dictionary.masterplateMaintain" />
	</item>
	<item id="informedConsent" alias="是否知情" type="string" length="1" defaultValue="1">
		<dic render="Checkbox" columnWidth="80" columns="1">
			<item key="1" text="知情同意"/>
		</dic>
	</item>
	<item id="personName" alias="姓名" type="string" length="50" queryable="true" not-null="1" fixed="true"/>
	<item id="sexCode" alias="性别" type="String" length="1" queryable="true" not-null="1" fixed="true">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="age" alias="年龄" type="int" display="1"/>
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" queryable="true" vtype="idCard" fixed="true" colspan="2"/>
	<item id="educationCode" alias="文化程度" type="string" length="2" colspan="4" display="0"> 
		<dic render="Radio" colWidth="120" columns="5">
			<item key="01" text="小学及以下"/>
			<item key="02" text="初中"/>
			<item key="03" text="高中中专技校"/>
			<item key="04" text="大专"/>
			<item key="05" text="本科及以上"/>
		</dic>
	</item>
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="2" colspan="4" display="0">
		<dic render="Radio" colWidth="120" columns="5">
			<item key="10" text="未婚"/>
			<item key="20" text="已婚"/>
			<item key="40" text="离异/分居"/>
			<item key="30" text="丧偶"/>
			<item key="22" text="再婚"/>
		</dic>
	</item>
	<item id="averageRevenue" alias="最近平均收入" type="string" length="2" colspan="4" display="0">
		<dic render="Radio" colWidth="70" columns="7">
			<item key="01" text="1000"/>
			<item key="02" text="1000~"/>
			<item key="03" text="2000~"/>
			<item key="04" text="3000~"/>
			<item key="05" text="4000~"/>
			<item key="06" text="6000~"/>
			<item key="09" text="≥10000"/>
		</dic>
	</item>
	<item id="unitNature" alias="单位性质" type="string" length="2" colspan="4" display="0">
		<dic render="Radio" colWidth="120" columns="5">
			<item key="01" text="政府机关"/>
			<item key="02" text="国营企业"/>
			<item key="03" text="其他事业单位"/>
			<item key="04" text="农民"/>
			<item key="05" text="教育系统"/>
			<item key="06" text="民营"/>
			<item key="07" text="医疗卫生"/>
			<item key="08" text="独资/合资/个人"/>
			<item key="09" text="无业"/>
			<item key="99" text="其它"/>
		</dic>
	</item>
	<item id="highRiskType" alias="类别" type="string" length="1" not-null="true" queryable="true" colspan="2" fixed="true">
		<dic id="chis.dictionary.tumourHighRiskType" render="Radio" colWidth="50" columns="6"/>
	</item>
	<item id="source" alias="来源" type="string" length="1" not-null="true" colspan="2" fixed="true">
		<dic id="chis.dictionary.tumourHighRiskSource" render="Radio" colWidth="50" columns="3"/>
	</item>
	<item id="content" alias="教育内容" type="string" length="2" not-null="true" display="1">
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
	<item id="modality" alias="教育形式" type="string" length="50" not-null="true" colspan="4" width="120">
		<dic id="chis.dictionary.HealthEducationType" render="Checkbox" columnWidth="80" columns="7"/>
	</item>
	<item id="surveyDate" alias="调查日期" type="date" queryable="true" update="false" defaultValue="%server.date.date" maxValue="%server.date.date" width="100">
	</item>
	<item id="surveyUser" alias="调查医生" type="string" length="20" update="false" width="180" queryable="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="surveyUnit" alias="调查机构" type="string" length="20" update="false" width="180" defaultValue="%user.manageUnit.id" fixed="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="nativePlace" alias="籍贯" type="string" length="20" not-null="1"/>
	<item id="domicilePlace" alias="户口所在地" type="string" length="100" not-null="1" colspan="2"/>
	<item id="residentialAddress" alias="居住地址" type="string" length="100" not-null="1" colspan="2"/>
	<item id="birthday" alias="出生年月" type="date" queryable="true" not-null="1" fixed="true"/>
	<item id="mobileNumber" alias="联系电话" type="string" length="20" not-null="1"/>
	<item id="status" alias="记录状态" type="string" length="1" defaultValue="0" display="1">
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
	<item id="createUser" alias="创建医生" type="string" length="20" update="false" width="180" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="创建机构" type="string" length="20" update="false" width="180" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" width="100" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="180"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date" width="130"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>