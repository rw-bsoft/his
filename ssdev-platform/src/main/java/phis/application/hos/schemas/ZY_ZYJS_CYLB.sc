<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_ZYJS" alias="住院结算表">
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
  <item id="JSCS" alias="结算次数" type="int" length="3" not-null="1"/>
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="JSLX" alias="结算类型" type="int" length="2" not-null="1"/>
  <item id="KSRQ" alias="开始日期" type="timestamp"/>
  <item id="ZZRQ" alias="终止日期" type="timestamp"/>
  <item id="JSRQ" alias="结算日期" type="timestamp" not-null="1"/>
  <item id="FYHJ" alias="费用合计" type="double" length="10" precision="2" not-null="1"/>
  <item id="ZFHJ" alias="自负合计" type="double" length="10" precision="2" not-null="1"/>
  <item id="JKHJ" alias="缴款合计" type="double" length="10" precision="2" not-null="1"/>
  <item id="FPHM" alias="发票号码" length="20"/>
  <item id="CZGH" alias="操作工号" length="10"/>
  <item id="JZRQ" alias="结帐日期" type="timestamp"/>
  <item id="HZRQ" alias="汇总日期" type="timestamp"/>
  <item id="TPHM" alias="退票号码" length="8"/>
  <item id="ZFRQ" alias="作废日期" type="timestamp"/>
  <item id="ZFGH" alias="作废工号" length="10"/>
  <item id="ZFPB" alias="作废判别" type="int" length="1" not-null="1"/>
  <item id="JSXM" alias="结算项目" length="250"/>
  <item id="JSJK" alias="结算缴款" length="250"/>
  <item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1"/>
</entry>
