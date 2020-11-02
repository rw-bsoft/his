<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Office" alias="科室代码">
	<item id="ID" alias="科室代码" type="long" length="15" generator="assigned"  display="0"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="15" startPos="1" />
		</key>
	</item>
	<item id="PARENTID" alias="上级科室"  type="long"  display="0" length="18" />
	
	<item id="OFFICENAME" alias="病区名称" not-null="true" type="string" length="50"/>
	<item id="PYCODE" alias="拼音码" type="string" length="100" target="KSMC" codeType="py"/>
</entry>