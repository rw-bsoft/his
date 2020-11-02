<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX" alias="结算费用明细表-农合上传">
  <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
  <item id="JGID" alias="机构ID" type="string" length="20" display="2" />
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
  <item id="FYRQ" alias="费用日期" type="date" not-null="1"/>
  <item id="FYXH" alias="费用序号" type="long" length="18" display="2" />
  <item id="FYMC" alias="费用名称" length="80"/>
  <item id="YPCD" alias="药品产地" type="long" length="18" display="2" />
  <item id="FYSL" alias="费用数量" type="double" length="10" precision="2" not-null="1"/>
  <item id="FYDJ" alias="费用单价" type="double" length="10" precision="4" not-null="1"/>
  <item id="ZJJE" alias="总计金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="YSGH" alias="医生工号" length="10" display="2" />
  <item id="SRGH" alias="输入工号" length="10" display="2" />
  <item id="QRGH" alias="确认工号" length="10" display="2" />
  <item id="FYBQ" alias="费用病区" type="long" length="18" display="2"  not-null="1"/>
  <item id="FYKS" alias="费用科室" type="long" length="18" display="2"  not-null="1"/>
  <item id="ZXKS" alias="执行科室" type="long" length="18" display="2"  not-null="1"/>
  <item id="JFRQ" alias="记费日期" type="date" not-null="1" display="2"  />
  <item id="XMLX" alias="项目类型" type="int" length="2" display="2"  not-null="1"/>
  <item id="YPLX" alias="药品类型" type="int" length="1" display="2"  not-null="1"/>
  <item id="FYXM" alias="费用项目" type="long" length="18" display="2"  not-null="1"/>
  <item id="FYXM" alias="费用项目" type="long" length="18" display="2"  not-null="1"/>
  <item id="NHSCBZ" alias="上传标志(农合)" type="string" display="2"   length="1"/>
</entry>
