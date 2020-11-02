<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYZH_TCM" alias="中医证侯（省中医馆）">
	<item id="ZHBS" alias="证侯标识" type="long" length="9"  display="0"  not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="ZHDM" alias="证侯代码" type="string" length="20" not-null="1"/>
	<item id="ZHMC" alias="证侯名称" type="string" length="60" not-null="1" width="130"/>
	<item id="PYDM" alias="拼音码" type="string" length="10"/>
</entry>
