<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_PastHistory" alias="个人既往史" sort="pastHisTypeCode">
	<item id="pastHistoryId" pkey="true" alias="档案编号" type="string"
		length="16" not-null="1" fixed="true" hidden="true"
		generator="assigned">
	</item>
	<item id="empiId" alias="empiid" type="string" length="32"
		fixed="true" notDefaultValue="true" hidden="true" />
	<item id="pastHisTypeCode" alias="既往史类别" type="string" length="2"
		queryable="true" not-null="1" update="false" width="180" fixed="true">
		<dic id="chis.dictionary.CV5101_01" />
	</item>
	<item id="protect" alias="防护措施" type="string" length="50" fixed="true"/>
	<item id="diseaseCode" alias="编码" type="string" length="30"
		queryable="true" fixed="true" hidden="true"/>
	<item id="methodsCode" alias="观测方法" type="string" length="1"
		queryable="true">
		<dic id="chis.dictionary.CV5101.02" />
	</item>
	<item id="diseaseCode" alias="编码" type="string" length="30"
		queryable="true" fixed="true" />
	<item id="diseaseText" alias="结果描述" type="string" length="500"
		width="180"  not-null="1" />
	<item id="vestingCode" alias="转归情况" type="string" length="8"
		queryable="true">
		<dic>
			<item key="1" text="痊愈"></item>
			<item key="2" text="好转"></item>
			<item key="3" text="未愈"></item>
			<item key="4" text="死亡"></item>
			<item key="5" text="其他"></item>
		</dic>
	</item>
	<item id="startDate" alias="起始日期" type="date" />
	<item id="endDate" alias="终止日期" type="date" />
	<item id="confirmDate" alias="确诊日期" type="date"/>
	<item id="recordUnit" alias="记录机构" type="string" length="20" update="false"
		width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.prop_manaUnitId"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="recordUser" alias="记录医生" type="string" length="20"
		update="false" hidden="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="recordDate" alias="记录日期" type="date" update="false"
		hidden="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.today']</set>
	</item>
</entry>