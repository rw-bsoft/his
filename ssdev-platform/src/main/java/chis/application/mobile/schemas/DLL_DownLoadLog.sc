<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mobile.schemas.DLL_DownLoadLog"
	alias="移动离线数据下载日志表">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="userId" alias="用户Id" type="string" length="50" display="0" />
	<item id="roleId" alias="角色" type="string" length="10" display="0" />
	<item id="password" alias="登录密码" type="string" length="50"
		not-null="1" inputType="password" display="2" />
	<item id="manaUnitId" alias="管辖机构" type="string" length="32"
		display="0" />
	<item id="downLoadStatus" alias="下载状态" type="string" length="1"
		display="0" />
	<item id="downLoadDate" alias="下载时间" type="date" display="2"
		defaultValue="%server.date.today" enableKeyEvents="true" not-null="1"
		maxValue="%server.date.today" />
</entry>
  	