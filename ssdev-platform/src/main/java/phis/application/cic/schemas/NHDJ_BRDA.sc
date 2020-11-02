<?xml version="1.0" encoding="UTF-8"?>

<entry id="NHDJ_BRDA" tableName="MS_BRDA" alias="农合登记model">
	<item id="GRBH" alias="个人编号" type="string" display="2" length="18" generator="assigned" pkey="true" />
	<item id="NHKH" alias="农合卡号" type="string" width="180" length="20"/>
	<item id="BRXM" alias="病人姓名" type="string" length="40"/>
	<item id="BRXB" alias="病人性别" length="4" type="string">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="CSNY" alias="出生年月" type="timestamp"/>
	<item id="SFZH" alias="身份证号" type="string" length="20"/>
	<item id="HKDZ" alias="户口地址" type="string" length="40"/>
	<item id="LXDH" alias="联系电话" type="string" length="16"/>
</entry>
