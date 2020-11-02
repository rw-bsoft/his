<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_XZMX" alias="日报性质明细">
  <item id="CZGH" alias="操作工号" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
  <item id="JZRQ" alias="结帐日期" type="timestamp" not-null="1" pkey="true"/>
  <item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1" pkey="true"/>
  <item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
  <item id="SFJE" alias="收费金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="FPZS" alias="发票张数" type="int" length="4" not-null="1"/>
</entry>
