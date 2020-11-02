<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP01" alias="抽样01">
	<item id="CYXH" alias="抽样序号" length="18" type="long" display="0" not-null="1" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="CYRQ" alias="抽样日期" type="date" not-null="1"/>
	<item id="DPLX" alias="类型" length="1" defaultValue="0"  type="int" not-null="1" display="0" >
		<dic id="phis.dictionary.correspondingWay_dplx"  />
	</item>
	<item id="WCZT" alias="完成状态" length="1"  not-null="1" type="int" display="0"/>
</entry>
