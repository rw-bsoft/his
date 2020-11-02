<?xml version="1.0" encoding="UTF-8"?>
<entry alias="回访信息">
	<item id="visitId" alias="记录号" type="string" hidden="true" length="16" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="gdrId" alias="群宴登记号" type="string" length="16" display="1"/>
	<item id="poisoning" alias="食物中毒" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="poisoningCount" alias="食物中毒人数" type="int" length="6"/>
	<item id="poisoningDesc" alias="中毒描述" length="100"/>
	<item id="visitUser" alias="回访人" length="20" defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitUnit" alias="回访机构" length="20" defaultValue="%user.manageUnit.id" not-null="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitDate" alias="回访时间" type="date" defaultValue="%server.date.today" not-null="1" >
	</item>
	<item id="createUser" alias="录入人" length="20" update="false" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree"  keyNotUniquely="true"  onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" length="20" update="false" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" defaultValue="%server.date.today" update="false">
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
		<dic id="chis.dictionary.user" render="Tree"  keyNotUniquely="true"  onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
</entry>
