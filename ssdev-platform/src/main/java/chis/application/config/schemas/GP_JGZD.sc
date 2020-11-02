<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.config.schemas.GP_JGZD" alias="机构字典" sort="jgid asc">
	<item id="jgid" alias="机构id" type="string" length="20" queryable="true"
		width="160" not-null="1" generator="assigned" pkey="true"
		display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="20" startPos="1"/>
		</key>	
	</item>
	<item id="jgname" alias="机构名称" type="string" length="100" not-null="1" width="300" queryable="true"/>
</entry>
