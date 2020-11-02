<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MPI_DemographicInfo_TJ" alias="EMPI个人基本信息" sort=" createTime desc">
	<item id="empiId" alias="EMPI" type="string" length="32" display="0"
		pkey="true" />
	<item id="cardNo" alias="卡号"  type="string" disabled="true" virtual="true" display="2" update="false"
		length="20" queryable="true" />
	<item id="MZHM" alias="门诊号码" type="string" disabled="true" length="32" virtual="true"  
		  queryable="true" editable="true" xtype="lookupfield_his"/>
	<item id="photo" alias="" xtype="imagefield" type="string"
		display="2" rowspan="5" />
	<item id="BRXZ" alias="性质" virtual="true" length="18"   not-null="1">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true" searchField="PYDM" />
	</item>
	<item id="personName" alias="姓名" type="string" length="20"
		queryable="true" not-null="1" />
	<item id="idCard" alias="身份证" type="string" length="20" width="160" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true" defaultValue = "1" not-null="1">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" queryable="true" maxValue="%server.date.today"/>
	<item id="age" alias="年龄" type="string" display="2" width="75"
		queryable="true" virtual="true" not-null="1" update="false"/>
	<item id="workPlace" alias="工作单位" type="string" length="50" />
	<item id="mobileNumber" alias="本人电话" type="string" length="20" 
		width="90" />
	<item id="phoneNumber" alias="家庭电话" type="string" length="20" />
	<item id="contact" alias="联系人" type="string" length="10" />
	<item id="contactPhone" alias="联系人电话" type="string" length="20" />
	<item id="registeredPermanent" alias="户籍标志" type="string" length="1" 
		defaultValue="1">
		<dic id="phis.dictionary.registeredPermanent" autoLoad="true"/>
	</item>
	<item id="nationCode" alias="民族" type="string" length="2" 
		defaultValue="01">
		<dic id="phis.dictionary.ethnic" autoLoad="true" />
		
	</item>
	<item id="bloodTypeCode" alias="血型" type="string" length="1" 
		defaultValue="5">
		<dic id="phis.dictionary.blood" autoLoad="true"/>
	</item>
	<item id="rhBloodCode" alias="RH血型" type="string" length="1" 
		defaultValue="3">
		<dic id="phis.dictionary.rhBlood" autoLoad="true"/>
	</item>
	<item id="educationCode" alias="文化程度" type="string" length="2">
		<dic id="phis.dictionary.education" autoLoad="true"/>
	</item>
	<item id="workCode" alias="职业类别" type="string" length="3" defaultValue="Y">
		
		<dic id="phis.dictionary.jobtitle" onlySelectLeaf="true" autoLoad="true"/>
	</item>
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="2" 
		defaultValue="90" width="50">
		<dic id="phis.dictionary.maritals" autoLoad="true"/>
	</item>
	<item id="homePlace" alias="出生地" type="string" length="100"
		width="90" display="0" />
	<item id="zipCode" alias="邮政编码" type="string" length="6" />
	<item id="address" alias="联系地址" type="string" length="100"
		width="200"  />
	<item id="email" alias="电子邮件" type="string" length="30"/>

	<item id="nationalityCode" alias="国籍" type="string" length="3"
		defaultValue="CN">
		<dic id="phis.dictionary.nationality" autoLoad="true"/>
	</item>
	<item id="startWorkDate" alias="参加工作日期" type="date" />
	<item id="createUnit" alias="建档机构" type="string" length="16"
		canRead="false" display="0" />
	<item id="createUser" alias="建档人" type="string" length="20"
		display="0" queryable="true"/>
	<item id="createTime" alias="建档时间" type="date" display="0" />
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="16"
		display="0" />
	<item id="lastModifyTime" alias="最后修改时间" type="date" display="0" />
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="0" />
	<item id="status" alias="状态" type="string" length="1" display="0"/>
	</entry>