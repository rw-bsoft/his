<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GW_YGXX" tabelName="SYS_USERCOLLATE" alias="公卫员工信息">
	<item id="userId" alias="员工工号" type="string" not-null="1" generator="assigned" pkey="true" display="0" />
	<item id="userName" alias="员工姓名" type="string" length="10" not-null="1" width="80"/>
	<item id="password" alias="员工密码"  type="string" length="50" display="0"/>
	<item id="regionCode" display="0"  type="string" length="20"/>
	<item id="manaUnitId" display="0"  type="string" length="20"/>
	<item id="manaUnitId_text" alias="所属机构"  type="string" length="20" width="120"/>
	<item id="jobId" alias="职位ID" display="0"  type="string" length="20"/>
	<item id="jobId_text" alias="职位" type="string" length="20" width="100"/>
</entry>
