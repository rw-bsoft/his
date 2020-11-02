<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_SFRB_FKMX" alias="门诊收费日报付款明细">
  <item id="CZGH" alias="操作工号" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
  <item id="JZRQ" alias="结帐日期" type="timestamp" not-null="1"/>
  <item id="FKFS" alias="付款方式" type="long" length="18" not-null="1"/>
  <item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
  <item id="FKJE" alias="付款金额" type="double" length="12" precision="2" not-null="1"/>
</entry>
