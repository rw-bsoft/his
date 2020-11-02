<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZGS" alias="物资归属(WL_WZGS)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" length="8"/>
	<item id="GYBZ" alias="公用标志" type="int" length="1"/>
	<item id="WZZT" alias="物资状态" type="int" length="1"/>

</entry>
