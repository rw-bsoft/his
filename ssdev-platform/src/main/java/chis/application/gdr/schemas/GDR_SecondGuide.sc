<?xml version="1.0" encoding="UTF-8"?>
<entry alias="群宴第二次指导">
	<item id="guideId" alias="记录号" hidden="true" type="string" length="16" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="gdrId" alias="群宴档案号" type="string" length="16" display="1"/>
	<item id="tableware" alias="餐用工具消毒" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="water" alias="饮用水消毒" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="tools" alias="工具生熟分开" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="material" alias="原料新鲜" type="string" length="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="foodRot" alias="食物腐烂变质" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="guests" alias="就餐人员名单" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="evnHealth" alias="环境卫生" type="string" length="1">
		<dic>
			<item key="1" text="好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="specialTools" alias="凉菜专用工具" type="string" length="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="sampleFood" alias="主要留样食物" length="500"/>
	<item id="guider" alias="指导人" length="20" defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="guideDate" alias="指导日期" type="date" defaultValue="%server.date.today" not-null="1" >
  	
	</item>
	<item id="guideResult" alias="指导意见" type="string" length="250" not-null="1"/>
	<item id="refurseReason" alias="不同意原因" length="100" colspan="3"/>
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
