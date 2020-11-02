<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_CDH"   alias="儿童户籍地址迁移申请" sort="applyDate desc">
	<item id="archiveMoveId" pkey="true" alias="记录序号" type="string"
		width="160" length="16" not-null="1" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="moveType" alias="迁移类别" type="string" length="1" display="0">
		<dic>
			<item key="1" text="申请迁入" />
			<item key="2" text="申请迁出" />
		</dic>
	</item>
	<item id="archiveId" alias="儿童档案号" type="string" length="17" not-null="1"
		fixed="true" width="160" />
	<item id="personName" alias="儿童姓名" type="string" length="20" xtype="lookupfieldex" queryable="true" />
	<item id="status" alias="迁移状态" type="string" display="1">
		<dic id="chis.dictionary.archiveMoveStatus" />
	</item>
	<item id="affirmType" alias="确认处理" type="string" length="1"
		queryable="true" fixed="true" display="1">
		<dic>
			<item key="1" text="同意迁移" />
			<item key="2" text="退回" />
		</dic>
	</item>
	<item id="sourceOwnerArea" alias="原归属地" width="70" length="2"
		defaultValue="1" update="false" fixed="true"  not-null="1">
		<dic>
			<item key="11" text="本市"></item>
			<item key="22" text="本省外市"></item>
			<item key="23" text="外省"></item>
		</dic>
	</item>
	<item id="sourceHomeAddress" alias="原户籍地址" type="string" length="25"  not-null="1" 
		fixed="true" width="200" queryable="true" colspan="2" >
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="sourceHomeAddress_text" alias="原户籍地址" length="200" display="0"/> 
	<item id="sourceCdhDoctorId" alias="原儿保医生" type="string" length="20"  not-null="1" 
		fixed="true" >
		<dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="sourceManaUnitId" alias="原管辖机构" type="string" length="20"  not-null="1" 
		fixed="true" width="250" queryable="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="targetOwnerArea" alias="现归属地" width="70" length="2"
		not-null="1">
		<dic>
			<item key="11" text="本市"></item>
			<item key="22" text="本省外市"></item>
			<item key="23" text="外省"></item>
		</dic>
	</item>
	<item id="targetHomeAddress" alias="现户籍地址" type="string" length="25" 
		width="250" not-null="1" queryable="true" colspan="2" >
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="targetHomeAddress_text" alias="现户籍地址1" type="string" length="200" display="0"  not-null="1" />  
	<item id="targetCdhDoctorId" alias="现儿保医生" type="string" length="20" >
		<dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true"/>
	</item>
	<item id="targetManaUnitId" alias="现管辖机构" type="string" length="20"
		width="250" not-null="true" queryable="true" colspan="3" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" lengthLimit="9" querySliceType="0"  render="Tree" filter="['le',['len',['$','item.key']],['i',9]]"/>
	</item>
	<item id="applyReason" alias="申请原因" type="string" length="500"
		display="2" colspan="3" />
	<item id="applyUnit" alias="申请机构" type="string" length="20" width="250"
		fixed="true"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="applyUser" alias="申请人" type="string" length="20"
		queryable="true" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="applyDate" alias="申请日期" type="datetime"  xtype="datefield" queryable="true"   fixed="true"  defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="affirmView" alias="确认人意见" type="string" length="500"
		fixed="true" colspan="3" display="0" />
	<item id="affirmUnit" alias="确认机构" type="string" length="20"
		fixed="true" width="250" display="0"  virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="affirmUser" alias="确认人" type="string" length="20"
		queryable="true" fixed="true" display="0"   virtual="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="affirmDate" alias="确认日期" type="date" queryable="true"
		fixed="true"  display="0"/>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		width="180" display="0"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>