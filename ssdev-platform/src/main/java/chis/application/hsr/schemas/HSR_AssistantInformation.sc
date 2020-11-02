<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hsr.schemas.HSR_AssistantInformation" alias="卫生监督协管信息报告登记表">
	<item id="RecordID" alias="卫生监督协管信息报告记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item> 
	<item id="discoveryTime" queryable="true" alias="发现时间" not-null="1" width="80" type="date" maxValue="%server.date.today"/>
	<item id="messageType"  queryable="true"  alias="信息类别" not-null="1" length="1" colspan="2" width="120" type="string" >
		<dic>
			<item key="1" text="食品安全" />
			<item key="2" text="饮用水卫生" />
			<item key="3" text="职业病危害" />
			<item key="4" text="学校卫生" />
			<item key="5" text="非法行医（采供血）" />
		</dic>
	</item>

	<item id="detail" alias="信息内容" length="500" colspan="3" xtype="textarea" width="250"/>
	<item id="reportTime" queryable="true" not-null="1" alias="报告时间" type="date" width="80" maxValue="%server.date.today"/>
	<item id="reportUser" queryable="true" alias="报告人" type="string" length="20"  width="100" not-null="1"  defaultValue="%user.userId"  >
		<dic id="chis.dictionary.user14" parentKey="%user.manageUnit.id" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="unitName" alias="报告机构" type="string" length="20"   width="120"   defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" display="2" xtype="datefield" update="false"  
		fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" display="2"  
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user14" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" display="2"
		width="180" fixed="true" update="false"   defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
