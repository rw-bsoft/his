<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ENR_JL02" alias="护理记录明细">
  <item id="MXBH" alias="明细编号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
  	<key>
	      <rule name="increaseId" type="increase" length="18" startPos="1"/>
	</key>
  </item>
  <item id="JLBH" alias="记录编号" type="long" length="18" not-null="1"/>
  <item id="XMBH" alias="项目编号" type="long" length="18" not-null="1"/>
  <item id="XMMC" alias="项目名称" length="20" not-null="1"/>
  <item id="JGID" alias="机构ID" type="string" length="20" />
  <item id="XSMC" alias="显示名称" length="20"/>
  <item id="XMQZ" alias="项目取值" length="4000"/>
  <item id="KSLH" alias="开始列号" type="long" length="2" not-null="1"/>
  <item id="JSLH" alias="结束列号" type="long" length="2" not-null="1"/>
  <item id="HDBZ" alias="活动标志" type="long" length="1"/>
  <item id="YMCLFS" alias="页面处理方式" type="long" length="1"/>
  <item id="HHJG" alias="换行间隔" type="long" length="1"/>
  <item id="XGBZ" alias="修改标志" type="long" length="1" not-null="1"/>
</entry>