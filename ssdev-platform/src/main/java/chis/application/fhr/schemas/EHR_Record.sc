<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.fhr.schemas.EHR_Record"    alias="家庭成员迁移记录" sort="applyDate desc">
	<item id="recordMoveId" pkey="true" alias="记录序号" type="string"
		width="160" length="16" not-null="1" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="personName" alias="姓名" type="string" length="20"
		not-null="1" fixed="true" queryable="true"/>	

	<item id="sexCode" alias="性别" type="string" length="1"
		 fixed="true" display="1">	
		<dic id="chis.dictionary.gender"/>
	</item>                                                   
	<item id="moveType" alias="类别" type="string" not-null="1" length="1" defaultValue="3" fixed="true">
		<dic>
			<item key="1" text="添加" />
			<item key="2" text="迁入" />
			<item key="3" text="迁出" />
			<item key="4" text="解除" />
		</dic>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30"
		not-null="1" width="160" fixed="true" display="2"/>
	<item id="familyId" alias="原家庭编码" type="string" length="20"
		not-null="1" width="160" fixed="true"/>
	<item id="newFamilyId" alias="现家庭编码" type="string" length="20"
		 width="160"   display="1"/>
	<item id="targetArea" alias="现网格地址" type="string"  length="25" 
		width="200" not-null="1"   display="2">
		<dic id="chis.dictionary.areaGrid"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="targetArea_text" alias="现网格地址" type="string"  length="200" display="0"/>
	
	<item id="manaDoctorId" alias="原责任医生" type="string" length="20"
		not-null="1" fixed="true" display="2">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="targetDoctor" alias="现责任医生" type="string" not-null="1" length="20"  display="2">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true"/>
	</item>
	
	<item id="regionCode" alias="原网格地址" type="string" length="25" 
		not-null="1" width="200"   fixed="true" display="2">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="regionCode_text" alias="原网格地址" type="string" length="200" display="0" fixed="true"/>
	<item id="targetUnit" alias="现管辖机构" type="string" length="20"
		width="320" not-null="true"   fixed="true" display="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	
	<item id="manaUnitId" alias="原管辖机构" type="string" length="20"
		width="320" not-null="true"   fixed="true" display="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="applyReason" alias="申请原因" type="string" length="500"
		display="2" />
	<item id="applyUnit" alias="申请机构" type="string" length="20" width="200" 
		fixed="true"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" display="2"/>
	</item>
	<item id="applyUser" alias="申请人" type="string" length="20"
		 fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="applyDate" alias="申请日期" type="datetime"  xtype="datefield"  
		fixed="true"  defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>