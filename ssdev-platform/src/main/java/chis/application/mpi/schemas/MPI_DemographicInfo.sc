<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpi.schemas.MPI_DemographicInfo" alias="EMPI个人基本信息" sort=" createTime desc" version="1332124044000" filename="E:\MyProject\BZWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\mpi/MPI_DemographicInfo.xml">
	<item id="empiId" alias="EMPI" type="string" length="32" display="0" pkey="true" />
	<item id="cardNo" alias="卡号" xtype="iccardfield" type="string" virtual="true" display="2" update="false" length="20" queryable="true"/>
	<item id="personName" alias="姓名" type="string" length="20" queryable="true" not-null="1"/>
	<item id="photo" alias="" xtype="imagefield" type="string" display="2" rowspan="5"/>
	<item id="cardtype" alias="证件类型" type="string" width="150"  length="25" defaultValue="01" group="基本信息">
		<dic id="platform.reg.dictionary.cardtype"/>
	</item>
	<item id="idCard" alias="身份证号码" type="string" length="20" width="160"  queryable="true" vtype="idCard" enableKeyEvents="true"/>
	<item id="childCard" alias="其他证号码" type="string" length="20" width="160"  queryable="true"  enableKeyEvents="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true" not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" queryable="true" not-null="1" maxValue="%server.date.today"/>
	<item id="workPlace" alias="工作单位" type="string" length="50"/>
	<item id="mobileNumber" alias="本人电话" type="string" length="20" not-null="1" width="90"/>
	<item id="phoneNumber" alias="家庭电话" type="string" length="20"/>
	<item id="contact" alias="联系人姓名" type="string" length="20" not-null="1"/>
	<item id="contactPhone" alias="联系人电话" type="string" length="20" not-null="1"/>
	<item id="contactRelation" alias="与联系人关系" type="string" length="20" not-null="1"/>
	<item id="registeredPermanent" alias="常住类型" type="string" length="1" not-null="1" defaultValue="1">
		<dic id="chis.dictionary.registeredPermanent"/>
	</item>
	<item id="nationCode" alias="民族" type="string" length="2" not-null="1" defaultValue="01">
		<dic id="chis.dictionary.ethnic"/>
	</item>
	<item id="bloodTypeCode" alias="血型" type="string" length="1" not-null="1" defaultValue="5">
		<dic id="chis.dictionary.blood"/>
	</item>
	<item id="rhBloodCode" alias="RH血型" type="string" length="1" not-null="1" defaultValue="3">
		<dic id="chis.dictionary.rhBlood"/>
	</item>
	<item id="educationCode" alias="文化程度" type="string" length="2" not-null="1">
		<dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="workCode" alias="职业类别" type="string" length="3" >
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true"/>
	</item>
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="2" not-null="1" defaultValue="9" width="50">
		<dic id="chis.dictionary.maritals" render="Tree" minChars="1" onlySelectLeaf="true"/>
	</item>
	<item id="insuranceCode" alias="医疗支付方式" type="string" length="20" not-null="1">
		<dic id="chis.dictionary.payMode"/>
	</item>
	<item id="insuranceType" alias="其他支付方式" type="string" length="100" fixed="true"/>
	<item id="homePlace" alias="出生地" type="string" length="100" width="90" display="0"/>
	<item id="zipCode" alias="邮政编码" type="string" length="6"/>
	<item id="address" alias="现住址" type="string" length="100" not-null="1" width="200" colspan="2"/>
	<item id="email" alias="电子邮件" type="string" length="30"/>
	<item id="nationalityCode" alias="国籍" type="string" length="3" defaultValue="CN">
		<dic id="chis.dictionary.nationality"/>
	</item>
	<item id="startWorkDate" alias="参加工作日期" type="date" maxValue="%server.date.today"/>
	<item id="createUnit" alias="建档机构" type="string" update="false" length="16" canRead="false" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" update="false" length="20" display="0" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createTime" alias="建档时间" update="false" type="datetime"  xtype="datefield" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="16" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="lastModifyTime" alias="最后修改时间" type="datetime"  xtype="datefield" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="status" alias="状态" type="string" length="1" display="0"/>
	<item id="versionNumber" alias="版本号" type="string" length="32"  display="0"/>
	<item id="insuranceText" alias="其他支付" type="string" length="50"  display="0"/>
	<item id="definePhrid" alias="档案备注说明" type="string" length="60" />
	<item id="zlls" alias="诊疗历史标记" type="string" length="1" display="0" update="false"  >
		<dic>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="crowdType" alias="人群类型"  type="string" length="20" width="300"  >
	      <dic render="LovCombo">
				<item key="01" text="老年人"    selected="false"/>
			<item key="02" text="高血压"/>
			<item key="03" text="糖尿病"/>
			<item key="04" text="0-6岁儿童"/>
			<item key="05" text="孕产妇"/>
			<item key="06" text="精障"/>
			<item key="07" text="结核病"/>
			<item key="08" text="残疾人"/>
			<item key="09" text="建档立卡"/>
			<item key="10" text="城乡低保"/>
			<item key="11" text="城乡特困"/>
			<item key="12" text="计生特殊"/>
			<item key="13" text="慢阻肺患者"/>
			<item key="14" text="优抚对象"/>
			<item key="15" text="特别扶助"/>
			<item key="16" text="离休干部"/>
			<item key="17" text="创新创业人才"/>
			<item key="18" text="麻风病"/>
			<item key="19" text="肿瘤"/>
			<item key="20" text="白血病患者"/>
		</dic>
	</item>
	<item id="signFlag" alias="是否签约" type="string" length="1" display="1" update="false" fixed="true">
		<dic>
			<item key="0" text="未签约"/>
			<item key="1" text="已签约"/>
		</dic>
	</item>
	<item id="sceEndDate" alias="签约到期时间" type="date" display="1" update="false" fixed="true"/>	
</entry>
