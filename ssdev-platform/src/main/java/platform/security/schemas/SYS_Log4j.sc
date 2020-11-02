<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Log4j" alias="日志信息">
	<item id="ID" alias="ID" type="int" not-null="1" generator="auto"
		pkey="true" display="0" />
	<item id="logClass" alias="类名" type="string" length="100" queryable="true" width="400"/>
	<item id="logDate" alias="时间" type="datetime" queryable="true" width="200"/>
	<item id="logLevel" alias="级别" type="string" length="10" queryable="true" width="100">
		<dic>
			<item key="ERROR" text="错误" />
			<item key="WARN" text="警告" />
			<item key="INFO" text="信息" />
			<item key="DEBUG" text="调试" />
		</dic>
	</item>
	<item id="logRowNumber" alias="行号" type="string" length="5" width="100"/>
	<item id="logMessage" alias="信息" type="text" expand="true"/>
	<item id="logType" alias="日志类型" type="string" length="1" width="120">
		<dic>
			<item key="1" text="用户操作日志" />
			<item key="2" text="系统信息日志" />
		</dic>
	</item>
	<item id="logUser" alias="操作用户" type="string" length="20" queryable="true"/>
</entry>
