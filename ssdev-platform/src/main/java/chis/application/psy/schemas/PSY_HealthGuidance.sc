<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.psy.schemas.PSY_HealthGuidance" alias="精神病健康指导">
	<item id="ghId" alias="健康指导编号" pkey="true" type="string" length="16" display="0" generator="assigned" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="visitId" alias="随访id" type="string" length="16" hidden="true"/>
	<item id="guideDate" alias="指导时间" type="date" not-null="1"/>
	<item id="processor" alias="指导人" type="string" length="20" width="80" defaultValue="%user.userId" fixed="true" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="recipelContent" alias="健康指导" type="binary" xtype="htmleditor" colspan="3" display="2"/>
	
	<item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" display="0"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false"  not-null="1" fixed="true" 
		  defaultValue="%server.date.date" enableKeyEvents="true" validationEvent="false" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>