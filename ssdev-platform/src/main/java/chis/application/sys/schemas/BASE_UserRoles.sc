<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="UserRoleToken" alias="用户角色">
	<item id="id" alias="id" type="int" not-null="true" pkey="true" generator="auto" length="50" display="0"/>
	<item id="userId" alias="人员编号" type="string" length="20" display="0"/>
	<item id="domain" alias="所属域" type="string" not-null="1" width="150" editable="false">
	   <dic id="platform.reg.dictionary.domain" />
	</item>
	<item id="roleId" alias="职位" type="string" length="50"  width="200" not-null="1">
		<dic id="platform.reg.dictionary.positionDic" render="Tree" rootVisible="true" onlySelectLeaf="true"/>
	</item>
    <item id="manageUnitId" alias="所属部门" type="string" length="50" width="250" fixed="true">
       <dic id="platform.reg.dictionary.positionDic" render="Tree"/>
    </item>
    <item id="regionCode" alias="网格地址" type="string" length="50" width="150" expand="true" display="3">
       <dic render="Tree" id="chis.dictionary.areaGrid" />
    </item>
    <item id="organId" alias="部门编号" type="string" length="50" width="150" not-null="1" display="2"/>
    <item id="lastLoginTime" alias="最后登录时间" type="date"
		defaultValue="%server.date.date" display="0">
	</item>
	<item id="lastIPAddress" alias="最后登陆地址" type="string" length="18" display="1" expand="true"/>
	<item id="logoff" alias="状态" type="string" display="1" defaultValue="0" expand="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="注销"/>
		</dic>
	</item>
</entry>