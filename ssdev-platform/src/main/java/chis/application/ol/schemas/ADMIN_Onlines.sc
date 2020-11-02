<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.ol.schemas.ADMIN_Onlines" alias="在线用户">
	<item id="onlinesId" alias="onlinesId" type="string" length="16"
		width="160" not-null="1" generator="assigned" pkey="true"
		display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="sessionId" alias="sessionId" type="string" length="100"
		width="160" not-null="1" generator="assigned" display="1"/>
	<item id="logonName" alias="登录名" type="string" not-null="1"
		hidden="true" fixed="true" length="20"/>
	<item id="jobTitle" alias="角色" type="string" length="10"
		not-null="true" display="3" width="120" >
		<dic id="jobName" />
	</item>
	<item id="ip" alias="ip地址" type="string" length="20"
		not-null="true" display="3" width="120">
	</item>
	<item id="mac" alias="网卡地址" type="string" length="20"
		not-null="true" display="3" width="120">
	</item>
	<item id="logonDate" alias="登录时间" type="date" update="false"
		fixed="true" defaultValue="%server.date.datetime" width="120">
	</item>
	
</entry>
