<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX_BQYT" alias="费用明细表">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="1" />
		</key>
	</item>
	<item id="ZYH" alias="住院号" type="long" display="0" length="18"/>
	<item id="FYRQ" alias="费用日期" type="date"  display="0" defaultValue="%server.data.data"/>
	<item id="TFRQ" alias="退费日期" type="date" fixed="true" />
	<item id="FYXH" alias="费用序号" type="long" length="18" display="0" />
	<item id="FYMC" alias="费用名称" length="80" width="200" fixed="true" />
	<item id="FYDJ" alias="单价" type="double" length="10" precision="4" fixed="true"
		not-null="1" />
	<item id="FYSL" alias="数量" type="double" length="10" precision="2"
		not-null="1" />
	<item id="YSXM" alias="开嘱医生" length="10" display="0"/>
	<item id="TFYS" alias="退费人" length="10" defaultValue="%user.userName" fixed="true" />
</entry>
