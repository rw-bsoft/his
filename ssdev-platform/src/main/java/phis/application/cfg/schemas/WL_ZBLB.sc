<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZBLB" alias="帐薄类别" sort="SXH asc">
	<item id="ZBLB" alias="帐薄类别" display="0" type="long" width="100" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" defaultValue="%user.manageUnit.id" display="0" length="20"/>
	<item id="ZBMC" alias="帐薄名称" not-null="1" length="30" width="150"/>
	<item id="KSZB" alias="科室帐薄" type="int" display="0" length="1"/>
	<item id="ZCZB" alias="资产帐薄" type="int" display="0" length="1"/>
	<item id="ZBZT" alias="帐薄状态" display="1" type="int" length="1" defaultValue="0" renderer="onRenderer">
		<dic id="confirm"/>
	</item>
	<item id="SXH" alias="顺序号" type="long" not-null="1" length="18"/>
</entry>
