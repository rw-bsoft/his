<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYFL" alias="中医疾病分类">
	<item id="ZYFL" alias="疾病分类" type="long" length="9" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="FLBM" alias="分类编码" type="long" length="30"/>
    <item id="FLDM" alias="分类代码" type="string" length="20"/>
    <item id="FLMC" alias="疾病名称" type="string" length="60"/>
    <item id="PYDM" alias="拼音码" type="string" length="10" target="JBMC" codeType="py" queryable="true"/>
    <item id="WBDM" alias="五笔码" type="string" length="10" target="JBMC" display="0" codeType="wb"/>
    <item id="QTDM" alias="其它码" type="string" length="10" display="0"/>
    <item id="ZXBZ" alias="性别限制" type="int" length="1"/>
    <item id="BZXX" alias="允许治愈" type="string" length="255"/>
</entry>
