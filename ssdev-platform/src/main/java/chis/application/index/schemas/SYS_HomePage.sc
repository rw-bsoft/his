<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_HomePage"  alias="用户首页配置">
    <item id="id" alias="id" type="int" pkey="true" length="50" />
	<item id="userId" alias="人员编号" type="string" length="50">
	    <set type="exp" run="server">['$','%user.userId']</set>
	</item>
	<item id="moduleId" alias="模块编号" type="string"/>
	<item id="position" alias="模块位置" type="string" />
	<item id="appId" alias="模块位置" type="string" />   
</entry>