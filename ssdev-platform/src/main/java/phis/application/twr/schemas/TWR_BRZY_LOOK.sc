<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="TWR_BRZY_LOOK" alias="住院转诊申请">
	<item id="BRZLID" alias="转诊单号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="BRID" alias="病人ID号" type="string" display="0" length="18"/>
	<item id="MZHM" alias="门诊号码" type="string" display="0" length="32"/>
	<item id="BRXM" alias="姓名" fixed="true" type="string" length="40"/>
	<item id="BRXB" alias="性别" fixed="true" length="4" type="string"/>
	<item id="NL" alias="年龄" fixed="true" length="4" type="int"/>
	<item id="SFZH" alias="身份证号" fixed="true" type="string" length="20"/>
	<item id="LXDH" alias="联系电话" fixed="true" type="string" length="16"/>
	<item id="LXDZ" alias="地址" fixed="true" type="string" colspan="3" length="40"/>
	<item id="MSZD" alias="病人诊断" fixed="true" length="50" not-null="true" type="long"/>
	<item id="ZZYY" alias="转诊原因" length="50" fixed="true" not-null="true" colspan="3" type="string"/>
	<item id="BQMS" alias="病情描述" length="1000" fixed="true" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
	<item id="ZLJG" alias="治疗经过" length="1000" fixed="true" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
	<item id="SQJG" alias="申请机构" type="string" length="16" anchor="100%" width="180" fixed="true" not-null="true"/>
	<item id="JGDH" alias="机构电话" fixed="true" length="20" type="string" />
	<item id="SQYS" alias="申请医生" type="string" length="20" fixed="true" not-null="true">
		<dic id="phis.dictionary.user" autoLoad="true" render="Tree"/>
	</item>
	<item id="YSDH" alias="医生电话" length="20" fixed="true" not-null="true" type="string"/>
	<item id="ZYSX" alias="注意事项" length="1000" fixed="true" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
	<item id="ZRRQ" alias="转入日期" length="18" display="0" height="40" not-null="true" type="date"/>
	<item id="ZRJG" alias="转入机构" length="20" display="0" height="40" not-null="true" type="string"/>
	<item id="ZRKS" alias="转入科室" length="18" display="0" height="40" not-null="true" type="long"/>

</entry>