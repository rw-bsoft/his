<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_JZJL" alias="结账记录" >
  <item id="JGID" alias="机构ID" length="20" not-null="1" type="string" display="0"/>
  <item id="XTSB" alias="药库识别" length="18" not-null="1" type="long" pkey="true" display="0"/>
  <item id="CWYF" alias="财务月份" type="date" not-null="1" pkey="true" colspan="3"/>
  <item id="QSSJ" alias="起始时间" type="datetime" not-null="1" display="0"/>
  <item id="ZZSJ" alias="终止时间" type="datetime" not-null="1" display="0"/>
</entry>
