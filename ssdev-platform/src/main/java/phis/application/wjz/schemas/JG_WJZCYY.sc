<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JG_WJZCYY" alias="危机值常用语">
	<item id="JLXH" alias="记录序号" length="10" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="0" />
		</key>
	</item>
	<item id="CYY" alias="常用语" length="100"  type="string" width="500"/>
	<item id="SXH" alias="顺序号"   type="int" length="6" display="0"/>
</entry>
