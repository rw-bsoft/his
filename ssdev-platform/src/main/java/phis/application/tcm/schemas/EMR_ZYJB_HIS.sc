<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYJB_HIS" alias="中医疾病">
	<item id="JBBS" alias="疾病标识" type="long" length="9" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
    <item id="JBDM" alias="疾病代码" type="string" length="20" not-null="1" />
    <item id="JBMC" alias="疾病名称" type="string" length="60" not-null="1" width="130"/>
    <item id="PYDM" alias="拼音码" type="string" length="10"/>
</entry>
