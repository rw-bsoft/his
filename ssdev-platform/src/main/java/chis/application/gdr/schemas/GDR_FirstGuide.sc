<?xml version="1.0" encoding="UTF-8"?>
<entry  alias="群宴首次指导">
	<item id="recordId" alias="记录号" hidden="true" type="string" length="16" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="gdrId" alias="群宴登记号" type="string" length="20" display="0"/>
	<item id="numOfChef" alias="厨师人数" type="int" length="6"/>
	<item id="numOfCheckedChef" alias="厨师体检人数" type="int" length="6"/>
	<item id="numOfHelper" alias="帮工人数" type="int" length="6"/>
	<item id="numOfCheckedHelper" alias="帮工体检人数" type="int" length="6"/>
	<item id="disinfectants" alias="消毒药物" length="50">
          <dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="disinfectionMeasures" alias="环境消毒措施" length="100">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="teacher" alias="指导人" length="20" defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="teachDate" alias="指导日期" type="date" defaultValue="%server.date.today" not-null="1" />
	<item id="teachView" alias="指导意见" length="250" not-null="1"/>
	<item id="createUser" alias="录入人" length="20" update="false" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree"  keyNotUniquely="true" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" length="20" update="false" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" defaultValue="%server.date.today">
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
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
</entry>
