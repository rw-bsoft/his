<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpi.schemas.MPI_DemographicInfo" alias="EMPI个人基本信息" sort=" createTime desc" version="1332124044000" filename="E:\MyProject\BZWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\mpi/MPI_DemographicInfo.xml">
	<item id="empiId" alias="EMPI" type="string" length="32" display="0" pkey="true" />
	<item id="personName" alias="姓名" type="string" length="20" queryable="true" not-null="1"/>
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" queryable="true" vtype="idCard" enableKeyEvents="true"/>
	<!--<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true" not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="registeredPermanent" alias="常住类型" type="string" length="1" not-null="1" defaultValue="1">
		<dic id="chis.dictionary.registeredPermanent"/>
	</item>
	<item id="nationCode" alias="民族" type="string" length="2" not-null="1" defaultValue="01">
		<dic id="chis.dictionary.ethnic"/>
	</item>
	<item id="bloodTypeCode" alias="血型" type="string" length="1" not-null="1" defaultValue="5">
		<dic id="chis.dictionary.blood"/>
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
	<item id="insuranceCode" alias="医疗支付方式" type="string" length="2" not-null="1">
		<dic id="chis.dictionary.payMode"/>
	</item>
 -->
	<item id="birthday" alias="出生日期" type="date" width="75" queryable="true" not-null="1" maxValue="%server.date.today"/>
	<item id="workPlace" alias="工作单位" type="string" length="50"/>
	<item id="mobileNumber" alias="本人电话" type="string" length="20" not-null="1" width="90"/>
	<item id="contact" alias="联系人姓名" type="string" length="20" not-null="1"/>
	<item id="contactPhone" alias="联系人电话" type="string" length="20" not-null="1"/>
	
	
	
	<item id="rhBloodCode" alias="RH血型" type="string" length="1" not-null="1" defaultValue="3">
		<dic id="chis.dictionary.rhBlood"/>
	</item>
	
	
	
	
	<item id="insuranceType" alias="其他支付方式" type="string" length="100" fixed="true"/>

</entry>
