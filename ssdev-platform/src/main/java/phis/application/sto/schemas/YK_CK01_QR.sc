<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CK01" alias="出库01"  sort="a.CKDH desc">
  <item id="JGID" alias="机构ID" length="20" type="string"  not-null="1" display="0"/>
  <item id="XTSB" alias="药库识别" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0"/>
    <item id="CKFS" alias="出库方式" length="4" not-null="1" type="int" pkey="true">
  	<dic id="phis.dictionary.storehouseDelivery" autoLoad="true"/>
  </item>
  <item id="CKDH" alias="出库单号" length="6" not-null="1" type="int" pkey="true"/>
  <item id="YFSB" alias="药房识别" length="18" not-null="1" type="long" display="0"/>
  <item id="CKBZ" alias="出库备注" type="string" length="30" display="0"/>
  <item id="CKPB" alias="出库判别" length="1" not-null="1" type="int" display="0"/>
  <item id="SQRQ" alias="申请日期" type="datetime" display="0"/>
  <item id="CKRQ" alias="出库日期" type="datetime" width="160"/>
  <item id="CKKS" alias="出库科室" length="18" type="string"  display="0"/>
  <item id="CZGH" alias="操作工号" type="string" length="10" display="0"/>
  <item id="QRGH" alias="确认工号" type="string" length="10" display="0"/>
  <item id="SQTJ" alias="申请提交" length="1" type="string" not-null="1" display="0"/>
  <item id="LYRQ" alias="领用日期" type="datetime" display="0"/>
  <item id="LYPB" alias="领用判别" length="1" not-null="1" type="int" display="0"/>
  <item id="LYGH" alias="领用工号" type="string" length="10" display="0"/>
</entry>
