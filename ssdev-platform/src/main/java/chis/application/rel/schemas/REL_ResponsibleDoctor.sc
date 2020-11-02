<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.rel.schemas.REL_ResponsibleDoctor" alias="关联责任医生">
	<item id="recordId" alias="recordId" type="string" length="16"  display="0"
		width="160" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="assistantId" alias="助理" type="string" width="100" not-null="1" queryable="true">
		<dic id="chis.dictionary.assistant" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="doctorId" alias="责任医生" width="100" colspan="1" length="200" not-null="1" type="string">
		<dic id="chis.dictionary.user01" render="TreeCheck" onlyLeafCheckable="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="createDate" alias="创建时间" width="100" display="1" type="date" />
	<item id="createUser" alias="创建人" width="100" display="1" length="20" type="string" >
		<dic id="chis.dictionary.Personnel" />
	</item>
	<item id="createUnit" alias="创建机构" width="100" display="1" length="20" type="string" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>	
	<item id="updateDate" alias="更新时间" width="100" display="1" type="date" />
	<item id="updateUser" alias="更新人" width="100" display="1" length="20" type="string">
		<dic id="chis.dictionary.Personnel" />
	</item>
	<item id="status" alias="状态" width="80" defaultValue="0" length="20" type="string" not-null="1">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
</entry>
