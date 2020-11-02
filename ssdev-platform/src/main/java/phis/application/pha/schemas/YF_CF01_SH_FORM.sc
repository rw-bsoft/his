<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MS_CF01"  alias="处方待审核详情">
	<item id="CFSB" alias="处方识别" length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true"/>	
	<item id="BRXM" alias="姓名" type="string" length="40" fixed="true" />
	<item id="BRID" alias="病人ID" type="long" display="0"/>
	<item id="XBMC" alias="性别" type="string" fixed="true" />
	<item id="XZMC" alias="性质" type="string" fixed="true"/>
	<item id="AGE" alias="年龄" virtual="true" width="50" fixed="true"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" fixed="true"/>
	<item id="MZHM" alias="门诊号码" length="32" fixed="true"/>
	<item id="KSMC" alias="就诊科室" width="250" type="string" length="50" fixed="true" />
	<item id="YSXM" alias="开方医生" type="string" fixed="true"/>
	<item id="KSDM" alias="科室代码" type="long" length="18" not-null="1" display="0"/>
	<item id="KFRQ" alias="开方日期" xtype="datetimefield" type="datetime"  fixed="true"/>
	<item id="BRZD" alias="病人诊断" type="string"  fixed="true" colspan="3"/>
</entry>