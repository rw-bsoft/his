<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourInspectionItemQueryResultInput" tableName="MDC_TumourInspectionItem" alias="筛查检查项目">
	<item id="itemId" alias="项目ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="definiteItemName" alias="项目名称" type="string" length="100" width="200" queryable="true" fixed="true" acValue="1"/>
	<item id="checkResult" alias="检查结果" type="string" length="30" not-null="1" queryable="true" display="1" zbgcolor="#ffffff">
		<dic id="chis.dictionary.tumourCheckResult"/>
	</item>
	<item id="checkDate" alias="检查日期" type="date" xtype="datefield" defaultValue="%server.date.date" not-null="1" queryable="true" display="1" zbgcolor="#ffffff"/>
	
	<item id="itemCode" alias="项目代码" type="string" length="20" fixed="true" acValue="1"/>
	<item id="itemType" alias="项目类别" type="string" length="1" fixed="true" acValue="1">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="bigItemId" alias="大类" type="long" length="18" not-null="1" display="2" fixed="true" acValue="1">
		<dic id="chis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="bigItemName" alias="费用归并" type="string" length="20" not-null="1" width="100" display="1" fixed="true" acValue="1"/>
	<item id="definiteItemId" alias="项目ID" type="long" length="18" display="0" acValue="1"/>
	
	<item id="startUsing" alias="是否启用" type="string" length="1" not-null="1" defaultValue="y" fixed="true" queryable="true" acValue="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" display="0" acValue="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0" acValue="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0" acValue="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" acValue="1" 
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" acValue="1" 
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date" acValue="1"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>