<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZZD_GDC" tableName="WL_WZZD" alias="物资字典">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" type="increase" startPos="1"  />
		</key>
	</item>
	<item id="GCSL" alias="高储数量" type="double" length="12" precision="2"/>
	<item id="DCSL" alias="低储数量" type="double" length="12" precision="2"/>
</entry>
