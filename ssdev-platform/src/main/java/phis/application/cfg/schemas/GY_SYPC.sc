<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SYPC" alias="给药频次">
  <item id="PCBM" alias="频次编码" display="0" width="100" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" startPos="24" />
    </key>
  </item>
  <item id="PCMC" alias="频次名称" type="string" width="100" length="20" not-null="1"/>
  <item id="MRCS" alias="每日次数" type="int" width="100" not-null="1" defaultValue="1" />
  <item id="ZXSJ" alias="执行时间"  length="80" width="250" type="string" not-null="1" fixed="true" />
  <item id="RLZ" alias="日历周"  length="1" type="long" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
  </item>
  <item id="ZXZQ" alias="最小周期" type="int" length="2" width="100" not-null="1" maxValue="30"  defaultValue="1" />
  <item id="RZXZQ" alias="周期天数"   length="7" display="0"  type="string" minValue="0" maxValue="1" not-null="1" fixed="true"/>
  <item id="BZXX" alias="备注" length="25" type="String"/>
</entry>
