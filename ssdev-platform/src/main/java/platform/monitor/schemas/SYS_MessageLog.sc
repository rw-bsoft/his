<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_MessageLog" alias="系统消息日志" sort="SendDate desc">
  <item id="Id" alias="ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="Uuid" alias="UUID" Uuid="string" length="100" display="0"/>
  <item id="Sender" alias="发送者" type="string" length="100" width="200"/>
  <item id="Receiver" alias="接收者" type="string" length="100" width="200"/>
  <item id="Topic" alias="主题" type="string" width="150" length="100"/>
  <item id="IsSuccess" alias="是否成功" type="string" length="10">
  	<dic>
  		<item key="true" text="成功"/>
  		<item key="false" text="失败"/>
  	</dic>
  </item>
  <item id="SendDate" alias="发送日期" type="timestamp" width="200"/>
  <item id="ReceiveDate" alias="接收日期" type="timestamp" width="200"/>
</entry>
