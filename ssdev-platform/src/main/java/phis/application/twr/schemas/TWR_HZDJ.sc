<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="TWR_HZDJ" alias="接收检查单">
	<item id="ID" alias="转诊单号" length="20" display="0" type="string" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
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
	<item id="ZCJG" alias="转出机构" type="string" length="16" anchor="100%" width="180" not-null="true"/>
	<item id="JGDH" alias="机构电话" length="20" type="string" />
	<item id="ZCYS" alias="转出医生" type="string" length="20" not-null="true"/>
	<item id="YSDH" alias="医生电话" length="20" not-null="true" type="string"/>
	<item id="MSZD" alias="转出诊断" mode="remote" length="50" not-null="true" colspan="2" type="string"/>
	<item id="ZLJG" alias="治疗结果" length="1000" not-null="true" colspan="2" type="string"/>
	<item id="ZLGC" alias="治疗经过" length="1000" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
	<item id="XYBZLFAHYJ" alias="下一步治疗方案和意见" length="1000" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
	<item id="CLQKXX" alias="处理情况信息" length="1000" colspan="4" xtype="textarea" height="40" not-null="true" type="string"/>
</entry>