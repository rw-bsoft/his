<?xml version="1.0" encoding="UTF-8"?>
<entry alias="健康卡数据采集基本信息">
	<item id="personName" alias="姓名" type="string" length="20" not-null="1" group="基本信息"/>
	<item id="birthday" alias="出生日期" type="date" width="75"  not-null="1" maxValue="%server.date.today" group="基本信息"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40"  not-null="1" defalutValue="9" group="基本信息">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="photo" alias="" xtype="imagefield" type="string" rowspan="5" group="基本信息"/>
	<item id="nationCode" alias="民族" type="string" length="2" not-null="1" defaultValue="01" group="基本信息">
		<dic id="chis.dictionary.ethnic"/>
	</item>
	<item id="cardtype" alias="身份证件类型" type="string" width="150" not-null="1" length="25" defaultValue="01" group="基本信息">
		<dic id="platform.reg.dictionary.cardtype"/>
	</item>
	<item id="cardnum" alias="身份证件号码" type="string" width="150" not-null="1"  length="25" group="基本信息"/>
	<item id="xnhkh" alias="新农合证(卡)号" type="string" length="20"  not-null="0" group="基本信息"/>
	<item id="jkdabh" alias="健康档案编号" type="string" length="20"  not-null="0" group="基本信息"/>
	<item id="mobileNumber" alias="本人电话" type="string" length="20" not-null="0" width="90" group="基本信息"/>
	<item id="educationCode" alias="文化程度" type="string" length="2" not-null="0" group="基本信息">
		<dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="workCode" alias="职业类别" type="string" length="3" defaultValue="8" not-null="1" group="基本信息">
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true"/>
	</item>
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="2" not-null="1" defaultValue="9" width="50" group="基本信息">
		<dic id="chis.dictionary.maritals" render="Tree" minChars="1" onlySelectLeaf="true"/>
	</item>
</entry>