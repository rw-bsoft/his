<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="BASE_ManageUnitType" alias="组织架构类型">
	<item id="id" alias="编号" type="int" length="50" not-null="true" pkey="true" keyGenerator="auto" queryable="true"/>
	<item id="typeCode" alias="机构类型编码" type="string" length="10"/>
	<item id="typeName" alias="机构类型名称" type="string" length="50"/>
	<item id="roleId" alias="角色信息" type="string" length="500"/>
	<item id="manageType" alias="类型" type="string" length="20"/>	
	<item id="domain" alias="所属域" type="string" length="50"/>	
</entry>