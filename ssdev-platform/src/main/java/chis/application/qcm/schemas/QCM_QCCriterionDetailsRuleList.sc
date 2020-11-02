<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.qcm.schemas.QCM_QCCriterionDetailsRuleList" tableName="chis.application.qcm.schemas.QCM_QCCriterionDetailsRule" alias="质控制标准明细规则记录表" sort="a.ruleRecordId">
	<item id="ruleRecordId" alias="规则记录编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="QCItemId" alias="标准项目编号" type="string" length="16" display="0"/>
	<item id="relationExp" alias="关系计算表示符" type="string" length="10" hidden="true"/>
	
	<item id="relation" alias="关系" type="string" length="10" width="120">
		<dic id="chis.dictionary.condition"/>
	</item>
	<item id="differenceValue" alias="差值" type="double" length="10" precision="2"/>
	<item id="gainScore" alias="得分" type="int" length="3"/>
	<item id="delOP" alias="" type="string"  virtual="true"  renderer="onRenderer" width="30"/>
	<item id="createUser" alias="录入人" type="string" length="20" update="false"  defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" width="150" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>