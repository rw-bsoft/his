<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="R_SLAVE" alias="节点">
  <item id="ID_SLAVE" alias="编号" type="long" not-null="1" generator="assigned" pkey="true" display="0"/>
  <item id="NAME" alias="客户端名称" type="string" length="30" fixed="true"/>
  <item id="HOST_NAME" alias="IP地址" type="string" length="30" width="130" fixed="true"/>
  <item id="PORT" alias="端口号" type="string" length="10" fixed="true"/>
  <item id="PROXY_HOST_NAME" alias="代理地址" type="string" length="30" display="1"/>
  <item id="PROXY_PORT" alias="代理端口号" type="string" length="10" display="1"/>
  <item id="NON_PROXY_HOSTS" alias="代理服务的表达式" type="string" length="30" width="120" display="1"/>
  <item id="UNAME" alias="登录名" type="string" length="20" display="1"/>
  <item id="UPASSWORD" alias="登录密码" type="string" length="255" display="1"/>
  <item id="MASTER" alias="是否主机" display="1" type="string" length="1">
  	<dic id="platform.reg.dictionary.yesOrNo"/>
  </item>
  <item id="DICCODE" alias="分类" display="1" type="string" length="1"/>
  <item id="WEB_APP_NAME" alias="服务名" type="string" length="20" display="1"/>
  <item id="DESCRIPTION" alias="备注" type="string" length="255" display="1"/>
  <item id="ISACCREDIT" alias="是否认证" width="100" not-null="1" type="string" length="1">
 	 <dic id="platform.reg.dictionary.yesOrNo"/>
  </item>
  <item id="FLAG" alias="是否注销" width="100" not-null="1" display="0" type="string" length="1">
  	<dic id="platform.reg.dictionary.yesOrNo"/>
  </item>
</entry>
