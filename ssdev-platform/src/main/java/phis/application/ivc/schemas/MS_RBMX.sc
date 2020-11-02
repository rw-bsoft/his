<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_RBMX" alias="收费日报明细">
  <item id="CZGH" alias="操作工号" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
  <item id="JZRQ" alias="结帐日期" type="timestamp" not-null="1" pkey="true"/>
  <item id="SFXM" alias="收费项目" length="18" type="long" not-null="1" pkey="true"/>
  <item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
  <item id="SFJE" alias="收费金额" length="12" type="double" precision="2" not-null="1"/>
</entry>
