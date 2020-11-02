<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_QZJSJL" alias="病历强制解锁记录">
	<item id="JSXH" alias="解锁序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1"/>
	<item id="JSYG" alias="解锁员工"  length="10" not-null="1">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="JSIP" alias="解锁IP" length="15" not-null="1"/>
	<item id="JSSJ" alias="解锁时间" type="timestamp" not-null="1" width="130"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1">
		<dic id="phis.@manageUnit"/>
	</item>
</entry>