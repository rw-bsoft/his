<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="User" alias="用户档案" sort="a.id">
	<item id="id" alias="人员编号" type="string" length="50" not-null="true" pkey="true" keyGenerator="auto" queryable="true"/>
	<item id="name" alias="人员姓名" type="string" length="50" display="0"/>
	<item ref="b.personName" fixed="true"/>
	<item ref="b.photo" alias="" update="false" fixed="true"/>
	<item ref="b.cardnum" alias="证件号码" type="string" xtype="lookupfieldex" editable="true"  enableKeyEvents="true"
	                 colspan="2" length="25" width="150" not-null="1" update="false">
	</item>
	<item id="password" alias="登录密码" type="string" length="50" not-null="1" inputType="password" display="2"/>	
	<item id="status" alias="用户状态" type="string" length="1" defaultValue="1" not-null="true">
		<dic>
			<item key="1" text="正常"/>
			<item key="0" text="禁用"/>
		</dic>
	</item>
	<item ref="b.gender" update="false"/>
	<item ref="b.mobile" display="1"/>	
	<item ref="b.pyCode" />
	<item id="createDt" alias="创建时间" type="data" display="1" width="150"/>
	<relations>
	 <relation type="children" entryName="platform.reg.schemas.SYS_Personnel">
	   <join parent="id" child="personId" />
	 </relation>
  </relations>
</entry>