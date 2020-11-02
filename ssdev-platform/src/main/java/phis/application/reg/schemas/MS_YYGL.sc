<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_BRDA" alias="预约管理">
	<item id="JZKH" alias="卡号" length="32" colspan="2" type="String"/>
	<item id="BRXM" alias="病人姓名" length="20" colspan="2" type="string" fixed="true"/>
	<item id="BRXB" alias="病人性别" length="4" display="0" type="int" width="50" fixed="true">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="BRXB_text" alias="病人性别" virtual="true" width="50" fixed="true"/>
	<item id="BRNL" alias="年龄" length="3" type="int" fixed="true"/>
	<item id="BRXZ" alias="病人性质" length="18" colspan="2" type="long"  fixed="true">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="LXDZ" alias="联系地址" length="40" type="string" colspan="4" fixed="true"/>
</entry>