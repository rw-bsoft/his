<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP01" alias="抽样01">
	<item id="CYXH" alias="抽样序号" length="18" type="long" not-null="1" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" defaultValue="%user.manageUnit.id"  />
	<item id="DPLX" alias="点评类型" length="1"   type="int" not-null="1"/>
	<item id="CYRQ" alias="抽样日期" type="datetime" not-null="1"/>
	<item id="CYGH" alias="抽样人员" type="string" length="10" not-null="1"/>
	<item id="KSRQ" alias="抽样开始日期" type="datetime" />
	<item id="JSRQ" alias="抽样结束日期" type="datetime" />
	<item id="CYFF" alias="抽样方法" length="8"  not-null="1" type="int"/>
	<item id="CYSL" alias="抽样数量" length="10"   type="long"/>
	<item id="WCZT" alias="完成状态" length="1"  not-null="1" type="int"/>
	<item id="ZFBZ" alias="作废标志" length="1"  not-null="1" type="int"/>
</entry>
