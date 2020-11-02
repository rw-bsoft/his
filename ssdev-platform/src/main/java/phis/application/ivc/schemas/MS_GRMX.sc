<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GRMX" alias="挂号日报明细">
  <item id="CZGH" alias="操作工号" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
  <item id="JZRQ" alias="就诊日期" type="timestamp" not-null="1" pkey="true"/>
  <item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1" pkey="true"/>
   <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="SFJE" alias="收费金额" type="double" length="12" precision="2"/>
  <item id="FPZS" alias="发票张数" type="int" length="4"/>
  <item id="GHJE" alias="挂号金额" type="double" length="12" precision="2"/>
  <item id="ZLJE" alias="诊疗金额" type="double" length="12" precision="2"/>
  <item id="ZJFY" alias="专家金额" type="double" length="12" precision="2"/>
  <item id="BLJE" alias="病历金额" type="double" length="12" precision="2"/>
  <item id="YZJM" alias="义诊减免" type="double" length="12" precision="2"/>
</entry>
