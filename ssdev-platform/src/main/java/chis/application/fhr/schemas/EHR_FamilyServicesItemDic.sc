<?xml version="1.0" encoding="UTF-8"?>
<entry entity-name="chis.application.fhr.schemas.EHR_FamilyServicesItemDic"
	alias="特色服务项目字典" sort="createDate asc">
	<item id="dicKey" alias="主键" pkey="true" type="string" length="32"
		fixed="true" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="32"
				startPos="1" />
		</key>
	</item>
	<item id="serveText" alias="服务名称" not-null="1" type="string" width="120" length="30" colspan="3"/>
	<item id="detail" alias="详细描述" xtype="textarea" width="500" length="1000" colspan="3" />
	<item id="createUnit" alias="录入单位" type="string" length="20" update="false"
		width="180"  fixed="true" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" update="false" width="160"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true"
		defaultValue="%server.date.today" update="false" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
