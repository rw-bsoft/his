<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_CSRZ" alias="后台传输日志">
	<item id="RZXH" alias="日志序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="sting" length="20"/>
	<item id="RZSJ" alias="日志时间" type="date"/>
	<item id="RZLX" alias="日志类型" type="int" length="1"/>
	<item id="RZXX" alias="日志信息" type="sting"  width="127"/>
</entry>