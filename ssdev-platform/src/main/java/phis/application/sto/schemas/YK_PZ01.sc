<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PZ01" alias="平账01">
  <item id="JGID" alias="机构ID" length="20" not-null="1" type="string"/>
  <item id="PZID" alias="平账ID号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="PZLX" alias="平账类型" length="1" not-null="1" type="int"/>
  <item id="XTSB" alias="药库识别" length="18" not-null="1" type="long"/>
  <item id="RCFS" alias="入出库方式" length="4" not-null="1" type="int"/>
  <item id="RCDH" alias="入出库单号" length="6" not-null="1" type="int"/>
  <item id="PZRQ" alias="平账日期" type="timestamp"/>
  <item id="PZGH" alias="平账工号" type="string" length="10"/>
  <item id="PZYY" alias="平账原因" type="string" length="250"/>
</entry>
