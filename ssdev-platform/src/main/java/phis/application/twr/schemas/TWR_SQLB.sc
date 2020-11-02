<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="TWR_SQLB" alias="接收上转申请">
	<item id="BRZLID" alias="转诊单号" length="20" display="0" type="string" pkey="true"/>
	<item id="BRID" alias="病人ID号" type="string" display="0" length="18"/>
	<item id="MZHM" alias="门诊号码" type="string" queryable="true" display="0" length="32"/>
	<item id="BRXM" alias="姓名" type="string" length="40"/>
	<item id="BRXB" alias="性别" length="4" type="string"/>
	<item id="NL" alias="年龄" length="4" type="int"/>
	<item id="SFZH" alias="身份证号" type="string" length="20"/>
	<item id="ZZSJ" alias="转诊时间" type="date"/>
	<item id="ZCJG" alias="转出机构" type="string" length="20"/>
	<item id="ZRJG" alias="转入机构" type="string" length="20"/>
	<item id="JZSJ" alias="就诊时间" type="date"/>
	<item id="ZZZT" alias="转诊状态" type="int" >
		<dic id="phis.dictionary.treatmentstatus" autoLoad="true"/>
	</item>
</entry>