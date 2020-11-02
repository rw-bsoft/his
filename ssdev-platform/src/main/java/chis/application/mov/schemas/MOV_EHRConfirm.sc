<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_EHR"  alias="档案迁移记录" sort="applyDate desc">
	<item id="archiveMoveId" pkey="true" alias="记录序号" type="string"
		width="160" length="16" not-null="1" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="archiveType" alias="档案类别" type="string" length="1"
		not-null="1" queryable="true" fixed="true">
		<dic>
			<item key="1" text="个人档案" />
			<item key="2" text="家庭档案" />
		</dic>
	</item>
	<item id="moveType" alias="迁移类别" type="string" length="1" hidden="true"	>
		<dic>
			<item key="1" text="申请迁入" />
			<item key="2" text="申请迁出" />
		</dic>
	</item>
	<item id="archiveId" alias="档案编号" type="string" length="30"
		not-null="1" width="160" fixed="true"/>
	<item id="personName" alias="姓名" type="string" length="20"
		not-null="1" xtype="lookupfieldex" queryable="true" fixed="true"/>
	<item id="status" alias="迁移状态" type="string" display="1">
		<dic id="chis.dictionary.archiveMoveStatus" />
	</item>	
	<item id="affirmType" alias="确认处理" type="string" length="1"
		queryable="true" display="1">
		<dic>
			<item key="1" text="同意迁移" />
			<item key="2" text="退回" />
		</dic>
	</item>
	<item id="sourceArea" alias="原网格地址" type="string" length="25"
		not-null="1" width="200" colspan="3" queryable="true" fixed="true">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="sourceArea_text" type="string" length="200" display="0"/>
	<item id="sourceDoctor" alias="原责任医生" type="string" length="20"
		not-null="1" fixed="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="sourceUnit" alias="原管辖机构" type="string" length="20"
		width="320" not-null="true" queryable="true" colspan="2" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="targetArea" alias="现网格地址" type="string" length="25"
		width="200" not-null="1" colspan="3" queryable="true" fixed="true">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="targetArea_text" type="string" length="200" display="0"/>
	<item id="targetDoctor" alias="现责任医生" type="string" length="20" not-null="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true"/>
	</item>
	<item id="targetUnit" alias="现管辖机构" type="string" length="20"
		width="320" not-null="true" colspan="2" queryable="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="applyReason" alias="申请原因" type="string" length="500"
		colspan="3" display="2" fixed="true"/>
	<item id="applyUnit" alias="申请机构" type="string" length="20" width="320" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="applyUser" alias="申请人" type="string" length="20"
		queryable="true" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="applyDate" alias="申请日期" type="date" queryable="true" fixed="true"/>
	<item id="movesub" alias="同时迁移子档" type="string" length="1" not-null="1"  fixed="true" >
		<dic id="chis.dictionary.yesOrNo"/> 
	</item>
	<item id="affirmView" alias="确认人意见" type="string" length="500"
		colspan="2" display="2" />
	<item id="affirmUnit" alias="确认机构" type="string" length="20"
		width="180" display="2" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="affirmUser" alias="确认人" type="string" length="20"
		queryable="true" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="affirmDate" alias="确认日期" type="datetime"  xtype="datefield" queryable="true" fixed="true" 
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期"  type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>