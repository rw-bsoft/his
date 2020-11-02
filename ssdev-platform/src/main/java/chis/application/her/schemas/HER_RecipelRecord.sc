<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.her.schemas.HER_RecipelRecord" alias="健康教育处方" sort="a.recipelId desc">
	<item id="recipelId" alias="处方编号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key> 
	</item>
	
	<item id="recipelType" alias="处方类型" type="string" length="1" not-null="1" width="80" queryable="true">
		<dic id="chis.dictionary.recipelType"/>
	</item>
	<item id="recipelObjectType" alias="对象分类" type="string" length="2" not-null="1" width="80" queryable="true" colspan="2">
		<dic id="chis.dictionary.objectType"/>
	</item>
	<item id="recipelContent" alias="健康处方" type="binary" xtype="textarea" colspan="3" display="2"/>
	
	<item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" display="0"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="date"  update="false"  not-null="1" fixed="true" 
		  defaultValue="%server.date.today" enableKeyEvents="true" validationEvent="false" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.datetime" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>