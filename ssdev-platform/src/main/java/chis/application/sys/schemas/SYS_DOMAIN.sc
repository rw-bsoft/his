<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_DOMAIN" alias="域">
  <item id="actived" alias="活跃" type="string" virtual="true" display="1" renderer="formatShow" width="50"/>
  <item id="ID" alias="域名" type="string" length="50" not-null="1" width="100" generator="assigned" pkey="true" queryable="true"/>
  <item id="DomainName" alias="中文别名" type="string" length="50" not-null="1"  width="120" queryable="true"/>
  <item id="Status" alias="应用域状态" type="string" not-null="1" defaultValue="0">
  	<dic>
	    <item key="0" text="禁用"/>
  		<item key="1" text="独立应用"/>
  		<item key="2" text="从属平台应用"/>  		
  	</dic>
  </item>
  <item id="DomainUrl" alias="应用域地址" type="string" width="250" colspan="3" length="50"/>
  <item id="REMARK" alias="备注" type="string" length="200" xtype="textarea" colspan="3" width="220"/> 
</entry>
