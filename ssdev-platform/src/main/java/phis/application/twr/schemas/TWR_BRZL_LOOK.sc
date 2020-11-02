<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="TWR_BRJC" alias="接收检查单">
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
	<item id="SFZH" alias="身份证号" fixed="true" type="string" length="20"/>
	<item id="LXDH" alias="联系电话" fixed="true" type="string" length="16"/>
	<item id="LXDZ" alias="地址" fixed="true" type="string" colspan="3" length="40"/>
	<item id="ZZZD" alias="转诊诊断" length="50" fixed="true" not-null="true" type="string"/>
	<item id="ZZYY" alias="转诊原因" length="50" fixed="true" not-null="true" colspan="3" type="string"/>
	<item id="BQMS" alias="病情描述" length="1000" fixed="true" colspan="4" xtype="textarea" not-null="true" type="string" />
	<item id="ZLJG" alias="治疗经过" length="1000" fixed="true" colspan="4" not-null="true" xtype="textarea" type="string" />
	<item id="SQJG" alias="申请机构" type="string" length="16" anchor="100%" width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id" not-null="true"/>
	<item id="JGDH" alias="机构电话" fixed="true" length="20" type="string" />
	<item id="SQYS" alias="申请医生" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" not-null="true">
		<dic id="phis.dictionary.user" autoLoad="true" render="Tree"/>
	</item>
	<item id="YSDH" alias="医生电话" fixed="true" length="20" type="string" />
	<item id="JGID" alias="机构" length="20" not-null="true" display="0" type="string" />
	<item id="KSDM" alias="科室" length="20" not-null="true" display="0" type="long" />
	<item id="YYYS" alias="预约医生" length="20" not-null="true" display="0" type="string" />
	<item id="YSTC" alias="医生特长" length="20" not-null="true" display="0" type="string" />
	<item id="YYRQ" alias="预约日期" length="20" not-null="true" display="0" type="date" />
	<item id="YYHY" alias="预约号源" length="20" display="0" type="string" />
</entry>