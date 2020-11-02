<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_MasterplateData" alias="字段数据表（主表）">
	<item id="recordId" alias="编号" length="16" not-null="1" generator="assigned"
		pkey="true" type="string" width="160" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="empiId" alias="empiId" length="32"  display="0"/>
	<item id="masterplateId" alias="模版编号" length="16" />
	<item id="masterplateName" alias="模版名称" length="100" />
	<item id="masterplateType" alias="模版类型" type="string">
		<dic>
			<item key="1" text="服务记录" />
			<item key="2" text="健康问卷" />
		</dic>
	</item>
	<item id="ServiceFlag" alias="服务状态" type="string" length="1"
		display="0">
		<dic>
			<item key="1" text="签约" />
			<item key="2" text="解约" />
		</dic>
	</item>
	<item id="masterplateTypezb" alias="指标模版类型" type="string" length="2">
		<dic id="chis.dictionary.masterplateType"/>
	</item>
	<item id="dateValue" alias="时间" type="date">
	</item>
	<item id="dateType" alias="时间类型" type="string">
		<dic>
			<item key="1" text="年"/>
			<item key="2" text="季"/>
			<item key="3" text="月"/>
			<item key="4" text="日"/>
		</dic>
	</item>
	<item id="years" alias="年度" type="string"  queryable="true" virtual="true" display="0">
		<dic id="chis.dictionary.years"/>
	</item>
	<item id="season" alias="季度" type="string" queryable="true" virtual="true" display="0">
		<dic id="chis.dictionary.season"/>
	</item>
	<item id="month" alias="月份" type="string" queryable="true" virtual="true" display="0">
		<dic id="chis.dictionary.month"/>
	</item>
	<item id="regionCode" alias="网格地址" type="string" length="25" width="200" anchor="100%" queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" queryable="true"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2"
		display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="manageDoctor" alias="责任医生" type="string" length="20"  queryable="true" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="remark" alias="备注" type="string" length="500" />
	<item id="inputUser" alias="录入医生" type="string" length="20"
		update="false" queryable="true" defaultValue="%user.userId" fixed="true"
		display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20"
		update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true"
		colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="date" queryable="true"
		update="false" defaultValue="%server.date.date" fixed="true" display="0">
		<set type="exp">['$','%server.date.date']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
</entry>
