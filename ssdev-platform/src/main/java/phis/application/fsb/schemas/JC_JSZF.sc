<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_JSZF" alias="结算作废">
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
  <item id="JSCS" alias="结算次数" type="int" length="3" not-null="1"/>
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="ZFGH" alias="作废工号" length="10" not-null="1"/>
  <item id="ZFRQ" alias="作废日期" type="timestamp" not-null="1"/>
  <item id="JZRQ" alias="结帐日期" type="timestamp"/>
  <item id="HZRQ" alias="汇总日期" type="timestamp"/>
</entry>
