<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_CK02" alias="抽样03">
	<item id="JLXH" alias="记录序号" length="18" type="long" not-null="1" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" defaultValue="%user.manageUnit.id"  />
	<item id="DPXH" alias="打印序号" length="18"   type="long" not-null="1"/>
	<item id="WTXH" alias="问题序号" length="18"   type="long" not-null="1"/>
	<item id="WTDM" alias="问题代码" type="string" length="20"  not-null="1"/>
</entry>
