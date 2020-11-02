<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_DB01" alias="药房调拔01">
  <item id="JGID" alias="机构ID" length="20" not-null="1"/>
  <item id="SQYF" alias="申请药房" length="18" not-null="1" generator="assigned" pkey="true" type="long"/>
  <item id="SQDH" alias="申请单号" length="6" not-null="1" pkey="true" type="int"/>
  <item id="MBYF" alias="目标药房" length="18" type="long"/>
  <item id="SQRQ" alias="申请日期" type="datetime"/>
  <item id="CZGH" alias="操作工号" type="string" length="10"/>
  <item id="TJBZ" alias="提交标志" length="1" type="int" defaultValue="0"/>
  <item id="CKBZ" alias="出库标志" length="1" type="int" defaultValue="0"/>
  <item id="CKGH" alias="出库工号" type="string" length="10"/>
  <item id="CKRQ" alias="出库日期" type="datetime"/>
  <item id="RKBZ" alias="入库标志" length="1" type="int" defaultValue="0"/>
  <item id="RKGH" alias="入库工号" type="string" length="10"/>
  <item id="RKRQ" alias="入库日期" type="datetime"/>
  <item id="TYPB" alias="退药判别" length="1" not-null="1" type="int" defaultValue="0"/>
  <item id="BZXX" alias="备注信息" type="string" length="100"/>
</entry>
