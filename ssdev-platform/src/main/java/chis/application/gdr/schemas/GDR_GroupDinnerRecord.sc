<?xml version="1.0" encoding="UTF-8"?>
<entry alias="群宴登记">
	<item id="gdrId" alias="记录号" hidden="true" type="string" length="16" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="personName" alias="申报人姓名" length="10" not-null="1"  queryable="true"/>
	<item id="idCard" alias="身份证号" length="18" not-null="1" width="150" vtype="childIdCard"  queryable="true"/>
	<item id="phone" alias="联系电话" length="50" not-null="1"/>
	<item id="numOfPeople" alias="宴请人数" type="int" length="6" />
	<item id="meetingDate" alias="宴请日期" type="date" not-null="1"/>
  
	<item id="reason" alias="宴请事由" length="2000"/>
	<item id="applyDate" alias="申报日期" type="date" not-null="1" maxValue="%server.date.today" queryable="true" defaultValue="%server.date.today">
	</item>
	<item id="villageView" alias="村委会意见" length="100"/>
	<item id="grovermentView" alias="镇政府意见" length="100"/>
	<item id="manaUnitId" alias="管辖机构" length="20" not-null="1" defaultValue="%user.manageUnit.id" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="false"
			parentKey="%user.manageUnit.id" rootVisible="true"/>
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
  
	<item id="regionCode" alias="网格地址" length="25" width="180" colspan="2" not-null="1" queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4"
			filterMax="18" render="Tree" onlySelectLeaf="true"  parentKey="%user.role.regionCode"/>
	</item>
	<item id="regionCode_text" type="string" length="200" display="0"/>
  
	<item id="createUser" alias="登记人员" length="20" update="false" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user" render="Tree" keyNotUniquely="true" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="登记机构" length="20" update="false" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="登记时间" type="datetime"  xtype="datefield" defaultValue="%server.date.today" update="fasle">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改时间" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.datetime">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" keyNotUniquely="true" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
</entry>
