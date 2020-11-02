<?xml version="1.0" encoding="UTF-8"?>
<entry alias="查螺灭螺基本信息" sort="snailBaseInfoId desc">
	<item id="snailBaseInfoId" alias="基本信息号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="addressId" alias="地址序号" length="50"/>
	<item id="regionCode" alias="网格地址(组)" type="string" length="25" update="false"
		not-null="1" width="250" colspan="2" anchor="100%" queryable="true">
		<dic id="chis.dictionary.areaGrid_group" includeParentMinLen="6"
	filterMin="10" minChars="4" filterMax="18" render="Tree"
	parentKey="%user.role.regionCode" />
	</item>
	<item id="regionCode_text" alias="网格地址(组)" type="string" length="200" display="0"/>
	<item id="snailAddress" alias="有螺地段名称" length="50" queryable="true" not-null="1"/>
	<item id="environmentType" alias="环境类别" type="string" length="1" queryable="true" not-null="1">
		<dic>
			<item key="1" text="沟渠" />
			<item key="2" text="田" />
			<item key="3" text="园" />
			<item key="4" text="地" />
			<item key="5" text="林" />
			<item key="6" text="水塘（水库）" />
			<item key="7" text="生活区" />
			<item key="8" text="大型复杂环境" />
		</dic>
	</item>
	<item id="length" alias="长度(m)" queryable="true" type="double" length="8" precision="2"/>
	<item id="width" alias="宽度(m)" queryable="true" type="double" length="8" precision="2"/>
	<item id="area" alias="面积(㎡)" queryable="true" type="double" length="8" precision="2" not-null="1"/>
	<item id="examineType" alias="调查方法" type="string" length="1" queryable="true" defaultValue="3" not-null="1">
		<dic>
			<item key="1" text="系统抽样" />
			<item key="2" text="环境抽样" />
			<item key="3" text="全面普查" />
		</dic>
	</item>
	<item id="snailPseron" alias="查螺人"  length="20" queryable="true" defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user16" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="examineCount" alias="调查框数" queryable="true"  type="int" not-null="1"/>
	<item id="snailFind" alias="捕获螺数(只)" queryable="true"  type="int" not-null="1"/>
	<item id="densityAverage" alias="平均密度(只/㎡)" queryable="true" type="double" length="8" precision="2"/>
	<item id="densityMaximum" alias="最高密度(只/㎡)" queryable="true" type="double" length="8" precision="2"/>
	<item id="snailNegative" alias="阳性螺数(只)" queryable="true"  type="int" not-null="1"/>
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" hidden="true">
		<dic id="chis.dictionary.status" />
	</item>
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
