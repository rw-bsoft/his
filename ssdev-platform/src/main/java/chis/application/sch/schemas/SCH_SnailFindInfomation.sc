<?xml version="1.0" encoding="UTF-8"?>
<entry alias="查螺信息" sort="snailFindInfoId desc">
	<item id="snailFindInfoId" alias="查螺记录号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="snailBaseInfoId" alias="基本信息号" type="string" length="16" not-null="1" fixed="true" width="150" display="2"/>
	<item id="snailFindUser" alias="查螺人" length="20" defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user16" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="snailFindDate" alias="查螺日期" type="date" defaultValue="%server.date.today" maxValue="%server.date.today" queryable="true" not-null="1"/>
	<item id="snailPersonTime" alias="查螺人次" type="int" queryable="true" not-null="1"/>
	<item id="snaiFindType" alias="调查方法" length="50" queryable="true" defaultValue="3" not-null="1">
		<dic>
			<item key="1" text="系统抽样" />
			<item key="2" text="环境抽样" />
			<item key="3" text="全面普查" />
		</dic>
	</item>
	<item id="snailArea" alias="现有钉螺面积" type="double" length="8" precision="2"  not-null="1"/>
	<item id="findCount" alias="调查框数" type="double" length="8" precision="2"  not-null="1"/>
	<item id="snailFindCount" alias="捕获螺数" type="int" queryable="true" not-null="1"/>
	<item id="densityAverage" alias="平局密度(只/㎡)"  type="double" length="8" precision="2" not-null="1"/>
	<item id="densityMaximum" alias="最高密度(只/㎡)"  type="double" length="8" precision="2"  not-null="1"/>
	<item id="snailNegative" alias="阳性螺数(只)"  type="int" queryable="true" not-null="1"/>
	<item id="snailWithoutKill" alias="未灭(㎡)"  type="double" length="8" precision="2" not-null="1"/>
	<item id="snailWithoutFind" alias="漏查(㎡)" type="double" length="8" precision="2" not-null="1"/>
	<item id="snailDiffuse" alias="扩散(㎡)" type="double" length="8" precision="2" not-null="1"/>
	<item id="snailOther" alias="其他(㎡)"  type="double" length="8" precision="2" not-null="1"/>
	<item id="inputUnit" alias="录入单位" length="20" type="string" 
		width="180" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUser" alias="录入人" type="string" length="20"
		fixed="true" update="false" defaultValue="%user.userId"
		queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
