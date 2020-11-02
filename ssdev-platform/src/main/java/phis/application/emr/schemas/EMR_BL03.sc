<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BL03" alias="病历03表">
	<item id="WDBH" alias="文档编号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="ZYMZ" alias="住院门诊" type="int" length="2" not-null="1"/>
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1"/>
	<item id="WDLX" alias="文档类型" type="int" length="4" not-null="1"/>
	<item id="WDNR" alias="文档内容" type="object"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
</entry>
