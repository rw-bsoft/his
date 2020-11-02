<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pif.schemas.PUB_PublicInfo" alias="公告信息" sort="publishDate desc">

	<item id="infoId" alias="信息编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>

	<item id="infoTitle" alias="信息主题" type="string" length="100"
		width="200" colspan="3" anchor="100%"  not-null="1" queryable="true"/>

	<item id="infoDesc" alias="信息内容" type="string" length="524288000"
		display="2"  xtype="textarea" height="380" colspan="3" />

	<item id="publishUnit" alias="发布机构" type="string" length="20" update="false"
		defaultValue="%user.manageUnit.id" fixed="true" display="3" width="120">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>

	<item id="publishDate" alias="发布日期" type="datetime"  xtype="datefield" width="80" update="false"
		defaultValue="%server.date.today" fixed="true" display="3" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<item id="publishUser" alias="发布人" type="string" length="20" update="false"
		defaultValue="%user.userId" fixed="true" display="0" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>

	<item id="validDate" alias="有效日期" type="date" display="0" />
</entry>
