<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.ol.schemas.SYS_OnlineUser" alias="在线用户信息">
	<item id="sessionId" alias="会话ID" display="0" />
	<item id="logonName" alias="登录帐号" width="150" queryable="true"/>
	<item id="logonTime" alias="本次登录时间" type="datetime" xtype="datetimefield" width="140" queryable="true"/>
	<item id="name" alias="姓名" display="0"/>
	<item id="role" alias="岗位" display="0"/>
	<item id="department" alias="部门" display="0"/>
	<item id="serverIP" alias="服务器IP" width="100" queryable="true"/>
	<item id="serverPORT" alias="服务器端口" width="80" queryable="true"/>
	<item id="clientIP" alias="客户端IP" width="100" queryable="true"/>
	<item id="userAgent" alias="客户端信息" width="550"/>
	<item id="lastAccessTime" alias="最后请求时间" display="0"/>
	<item id="leaveTime" alias="上次注销时间" display="0"/>
	<item id="isOnline" alias="是否在线" display="0"/>
</entry>
