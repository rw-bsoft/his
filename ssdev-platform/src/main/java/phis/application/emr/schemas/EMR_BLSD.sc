<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLSD" alias="病历锁定表">
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1" generator="assigned" pkey="true" />
	<item id="SDYG" alias="锁定员工"  length="10" not-null="1">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="SDIP" alias="锁定IP" length="15" not-null="1"/>
	<item id="SDSJ" alias="锁定时间" type="timestamp" not-null="1" width="130"/>
	<item id="SDZT" alias="锁定状态" type="int" length="1">
		<dic>
			<item key="0" text="未锁定"/>
			<item key="1" text="锁定"/>
		</dic>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1">
		<dic id="phis.@manageUnit"/>
	</item>
</entry>