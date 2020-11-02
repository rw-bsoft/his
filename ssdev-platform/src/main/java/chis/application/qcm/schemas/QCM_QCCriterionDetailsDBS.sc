<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.qcm.schemas.QCM_QCCriterionDetails" alias="质控制标准明细表" sort="a.QCItemId">
	<item id="QCItemId" alias="标准项目编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="QCCID" alias="标准编号" type="string" length="16" display="0"/>
	
	<item ref="b.criterionType"  fixed="true"/>
	<item ref="b.criterionCategoryType" colspan="2"  fixed="true"/>
	
	<item id="itemAlias" alias="项目名称" type="string" length="100"  not-null="1">
		<dic id="chis.dictionary.itemAliasDBS"/>
	</item>
	<item id="itemId" alias="项目标识" type="string" length="50" fixed="true"/>
	<item id="itemName" alias="项目名称" type="string" length="100" display="0"/>
	<item id="itemScore" alias="总分" type="int" length="3" maxValue="100" minValue="0" not-null="1"/>
	<item id="CriteriaDescription" alias="标准描述" type="string" xtype="textarea"  width="320" length="200" colspan="3"/>
	<item id="dicItemCode" alias="可选项代码" type="string" length="100" display="0"/>
	<item id="dicItemText" alias="可选项显示" type="string" xtype="textarea"  width="320" length="500" fixed="true" colspan="3"/>
	<item id="ruleExpression" alias="规则表达式" type="string" length="500" display="0"/>
  
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
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1" width="150">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<relations>
		<relation type="parent" entryName="chis.application.qcm.schemas.QCM_QualityControlCriterion">
			<join parent = "QCCID" child = "QCCID" />
		</relation>
	</relations>
</entry>
