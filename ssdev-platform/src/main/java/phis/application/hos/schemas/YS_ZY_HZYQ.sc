<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_ZY_HZYQ" alias="会诊_邀请对象">
	<item id="YQXH" alias="邀请序号" length="18" type="long" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="SQXH" alias="申请序号" length="18" not-null="1" type="long"/>
	<item id="YQFF" alias="邀请方法" length="1" not-null="1" type="int"/>
	<item id="YQDX" alias="邀请对象" type="string" length="10" not-null="1"/>
	<item id="DXLX" alias="对象类型" length="1" not-null="1" type="int"/>
	<item id="YQSJ" alias="邀请时间" type="timestamp" not-null="1"/>
	<item id="YQYS" alias="邀请医生" type="string" length="10" not-null="1"/>
	<item id="QRBZ" alias="确认标志" length="1" not-null="1" type="int"/>
	<item id="QRSJ" alias="确认时间" type="timestamp"/>
	<item id="MSGID" alias="会诊消息" length="18"/>
	<item id="BZXX" alias="备注信息" type="string" length="255"/>
	<item id="JGID" alias="机构ID" length="20" />
</entry>
