<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SMZ_PHICINFO" alias="SMZ个人基本信息" >
	<item id="personName" alias="姓名" type="string" length="40" queryable="true" not-null="1"/>
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" queryable="true"  pkey="true" not-null="1" vtype="idCard" enableKeyEvents="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true" not-null="1" defalutValue="9">
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" queryable="true" not-null="1" maxValue="%server.date.today"/>
	<item id="mobileNumber" alias="本人电话" type="string" length="20" width="90"/>
	<item id="phoneNumber" alias="家庭电话" type="string" length="20"/>
	<item id="contact" alias="联系人姓名" type="string" length="20"/>
	<item id="contactPhone" alias="联系人电话" type="string" length="20"/>
	<item id="nationCode" alias="民族" type="string" length="2" defaultValue="01">
	</item>
	<item id="educationCode" alias="文化程度" type="string" length="2">
	</item>
	<item id="workCode" alias="职业类别" type="string" length="3" defaultValue="8">
	</item>
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="2" defaultValue="9" width="50">
	</item>
	<item id="homePlace" alias="出生地" type="string" length="100" width="90" display="0"/>
	<item id="zipCode" alias="邮政编码" type="string" length="6"/>
	<item id="address" alias="联系地址" type="string" length="100" width="200" colspan="2"/>
	<item id="email" alias="电子邮件" type="string" length="30"/>
	<item id="nationalityCode" alias="国籍" type="string" length="3" defaultValue="CN">
	</item>
	<item id="startWorkDate" alias="参加工作日期" type="date" maxValue="%server.date.today"/>
	<item id="createUnit" alias="建档机构" type="string" update="false" length="16" canRead="false" display="0">
	</item>
	<item id="createUser" alias="建档人" type="string" update="false" length="20" display="0" queryable="true">
	</item>
	<item id="createTime" alias="建档时间" update="false" type="datetime"  xtype="datefield" display="0">
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="16" display="1">
	</item>
	<item id="lastModifyTime" alias="最后修改时间" type="datetime"  xtype="datefield" display="1">
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1">
	</item>
	<item id="ybkh" alias="医保卡号" type="string" length="50" display="0"/>
	<item id="smkh" alias="市民卡号" type="string" length="50"  display="0"/>
</entry>
