<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServiceProtocol" alias="签约协议">
	<item id="PRID" alias="签约协议ID" type="string" length="20" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="protocolNo" alias="签约协议编号" type="string" length="50" width="150" queryable="true"/>
	<item id="protocolName" alias="签约协议名称" type="string" length="50" width="150" queryable="true"/>
	<item id="intro" alias="简介" type="string" xtype="textarea" length="2000" colspan="2" width="400"/>
</entry>