<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="TWR_BRJC_LOOK" alias="接收检查单">
	<item id="BRZLID" alias="转诊单号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="BRID" alias="病人ID号" type="string" display="0" length="18"/>
	<item id="MZHM" alias="门诊号码" type="string" display="0" length="32"/>
	<item id="BRXM" alias="姓名" type="string" fixed="true" length="40"/>
	<item id="BRXB" alias="性别" length="4" fixed="true" type="string"/>
	<item id="NL" alias="年龄" length="4" fixed="true" type="int"/>
	<item id="SFZH" alias="身份证号" type="string" fixed="true" length="20"/>
	<item id="LXDH" alias="联系电话" type="string" fixed="true" length="16"/>
	<item id="LXDZ" alias="地址" type="string" fixed="true" colspan="3" length="40"/>
	<item id="BRZD" alias="病人诊断" fixed="true" length="50" not-null="true" type="long"/>
	<item id="BRTZ" alias="病人体征" length="50" fixed="true" not-null="true" colspan="3" type="string"/>
	<item id="BQMS" alias="病情描述" length="1000" fixed="true" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
	<item id="SQJG" alias="申请机构" type="string" length="16" anchor="100%" width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id" not-null="true"/>
	<item id="JGDH" alias="机构电话" length="20" fixed="true" display="0" type="string" />
	<item id="SQYS" alias="申请科室" type="string" length="20" fixed="true" update="false" not-null="true" />
	<item id="JIANCHAXMMC" alias="送检项目" fixed="true" not-null="true" type="long"/>
	<item id="JCSM" alias="检查说明" length="1000" fixed="true" colspan="1" height="40" not-null="true" type="string"/>
	<item id="JCRQ" alias="检查日期" display="0" height="40" not-null="true" type="date"/>
	<item id="JCLX" alias="检查类型" length="18" display="0" height="40" not-null="true" type="long"/>
	<item id="JCBW" alias="检查部位" length="18" display="0" height="40" not-null="true" type="long"/>
	<item id="JCFX" alias="检查方向" length="18" display="0" height="40" not-null="true" type="long"/>
	<item id="YQ" alias="仪器" length="18" display="0" height="40" not-null="true" type="long"/>
	<item id="YYH" alias="预约号" length="20" display="0" height="40" not-null="true" type="string"/>
	<item id="JCSJ" alias="检查时间" length="20" display="0" height="40" not-null="true" type="string"/>
</entry>