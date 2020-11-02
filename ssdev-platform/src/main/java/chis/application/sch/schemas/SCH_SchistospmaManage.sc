<?xml version="1.0" encoding="UTF-8"?>
<entry alias="血吸虫综合管理" sort="schisManageId desc">
	<item id="schisManageId" alias="管理记录号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
  
	<item id="patient" alias="查病人次" length="9" minValue="1" type="int" queryable="true"/>
	<item id="regionCode" alias="网格地址(组)" type="string" length="25"
	update="false" not-null="1" width="250" colspan="2" anchor="100%"
	queryable="true">
	<dic id="chis.dictionary.areaGrid_group" includeParentMinLen="6"
		filterMin="10" minChars="4" filterMax="18" render="Tree"
		parentKey="%user.role.regionCode" />
</item>
	<item id="regionCode_text" alias="网格地址(组)" type="string" length="200" display="0"/>
  
  
	<item id="chemotherapy" alias="化疗人次" length="9" minValue="1" queryable="true" type="int"/>
		
	<item id="snailFind" alias="查螺数" type="int" minValue="1" length="5"/>
	<item id="snailWipeMedicine" alias="药物灭螺数" minValue="1" type="int" length="5"/>
	<item id="drinkingWaterType" alias="饮用水项目" minValue="1" type="int" length="5"/>
	<item id="ditchHarden" alias="沟渠硬化" type="int" minValue="1" length="5"/>
	<item id="syntheticdevelop" alias="综合开发" type="int" minValue="1" length="5"/>
	<item id="ditchRebuild" alias="渠道整治" type="int" minValue="1" length="5"/>
	<item id="farmCattleIllness" alias="耕牛查病" type="int" minValue="1" length="5"/>
	<item id="farmCattleHealth" alias="治疗病牛" type="int" minValue="1" length="5"/>
	<item id="methanePool" alias="林业建沼气池" type="int" minValue="1" length="5"/>
	<item id="machineReplaceCattle" alias="以机代牛" type="int" minValue="1" length="5"/>
	<item id="forestryKillSnail" alias="林业项目抑螺" type="int" minValue="1" length="5"/>
	<item id="toilet" alias="爱卫办改厕" type="int" minValue="1" length="5"/>
	<item id="registerDate" alias="登记日期" type="date" defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="registerPerson" alias="登记人" type="string" length="20"
		fixed="true" update="false" defaultValue="%user.userId"
		queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
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
			keyNotUniquely="true"  parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			keyNotUniquely="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
