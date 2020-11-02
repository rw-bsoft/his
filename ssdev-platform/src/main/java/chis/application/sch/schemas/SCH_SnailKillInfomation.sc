<?xml version="1.0" encoding="UTF-8"?>
<entry alias="灭螺信息" sort="snailKillInfoId desc">
	<item id="snailKillInfoId" alias="灭螺记录号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="snailBaseInfoId" alias="基本信息号" type="string" length="16" not-null="1" fixed="true" width="150" display="2"/>
	<item id="snailKillDate" alias="灭螺日期" type="date" defaultValue="%server.date.today" maxValue="%server.date.today" queryable="true" not-null="1"/>
  
	<item id="snailKillType" alias="灭螺方法" type="string" length="1" queryable="true" defaultValue="1" not-null="1" >
		<dic>
			<item key="1" text="薄膜覆盖" />
			<item key="2" text="喷洒" />
			<item key="3" text="泥封" />
			<item key="4" text="填埋" />
			<item key="5" text="浸杀" />
		</dic>
	</item>
	<item id="snailKillArea" alias="灭螺面积(㎡)" type="double" queryable="true" not-null="1"/>
	<item id="snailWithoutKillArea" alias="未灭面积(㎡)"  type="double" queryable="true" length="8" precision="2" not-null="1"/>
	<item id="snailKillTotalArea" alias="累计面积(㎡)" type="double" queryable="true" not-null="1"/>
	<item id="snailPersonTime" alias="灭螺人次" type="int" queryable="true" not-null="1"/>
	<item id="snailKillPerson" alias="灭螺人" colspan="2" length="20" defaultValue="%user.userId" queryable="true" not-null="1">
		<dic id="chis.dictionary.user16" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="memo" alias="备注" length="500" xtype="textarea" colspan="3" anchor="100%"/>
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
