<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TJ01" alias="调价01" sort="a.TJDH desc">
	<item id="JGID" alias="机构ID"  type="string" length="20" not-null="1" defaultValue="%user.manageUnit.id" display="0" />
	<item id="XTSB" alias="药库识别" type="long" pkey="true" length="18" not-null="1"  display="0"/>
	<item id="TJFS" alias="调价方式"  pkey="true" length="2" not-null="1" fixed="true" type="int"  display="2"  queryable="true" selected="true" defaultValue="1">
		<dic id="phis.dictionary.priceAdjust" autoLoad="true" />
	</item>
	<item id="TJDH" alias="调价单号" length="6"  type="int" not-null="1" display="1" generator="assigned" pkey="true">
	</item>
	<item id="SJRQ" alias="录入日期"  type="datetime" width="130" defaultValue="%server.date.datetime"/>
	<item id="TJWH" alias="调价文号" type="string" length="30"/>
	<item id="TJRQ" alias="调价日期"  display="2"  fixed="true" type="datetime" defaultValue="%server.date.datetime"/>
	<!--默认值从人员缓存中取-->
	<item id="ZXGH" alias="执行工号" type="string" length="10" display="0" />
	<item id="ZXRQ" alias="执行日期"  type="datetime" display="0" fixed="true"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" display="0" defaultValue="%user.userId">
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="ZYPB" alias="执行判别" type="int" length="1" not-null="1" display="0" defaultValue="0"/>
	<item id="DSSJ" alias="定时时间" type="date" display="0"/>
	<item id="DSPB" alias="定时判别" type="int" length="1" display="0"/>
	<item id="DSRQ" alias="定时日期" type="date" defaultValue="%server.date.date" display="0"/>
</entry>
