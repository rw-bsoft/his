<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="DR_DemographicInfo" alias="MPI个人基本信息">
	<item id="mpiId" alias="MPI" type="string" length="32" display="0"
		pkey="true" />
	
	<item id="cardTypeCode" alias="卡类型" type="string" length="2" update="false"
		defaultValue="01">
		<dic id="chis.dictionary.cardTypeCode"/>
	</item>	
	<item id="cardNo" alias="卡号" type="string" width="150" length="20" update="false"/>
	<item id="personName" alias="姓名" type="string" length="20" update="false"
		queryable="true" not-null="1" />
	<item id="idCard" alias="身份证" type="string" length="20" width="150" update="false"
		 queryable="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="60" update="false"
		not-null="1" defalutValue = "9">
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="birthday" alias="出生日期" type="date" width="90" update="false"
		queryable="true" not-null="1" maxValue="%server.date.date"/>
	<item id="contactNo" alias="联系号码" type="string" length="20" not-null="1" width="105" update="false"/>
</entry>