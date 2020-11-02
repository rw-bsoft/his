<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_JSMX" alias="结算明细">
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
  <item id="JSCS" alias="结算次数" type="int" length="3" not-null="1" pkey="true"/>
  <item id="KSDM" alias="科室代码" type="long" length="18" not-null="1" pkey="true"/>
  <item id="FYXM" alias="费用项目" type="long" length="18" not-null="1" pkey="true"/>
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="ZJJE" alias="总计金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="ZLJE" alias="自理金额" type="double" length="12" precision="2" not-null="1"/>
</entry>
