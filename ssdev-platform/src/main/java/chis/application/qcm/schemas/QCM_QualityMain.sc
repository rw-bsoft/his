<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.qcm.schemas.QCM_QualityMain" alias="质控主表" sort="createDate desc">
	<item id="QMID" alias="记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="finishFlag" alias="是否完成" type="string" length="1">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="qualityKind" alias="项目类别" type="string" length="1" display="0">
		<dic id="chis.dictionary.QualityControl_XMLB"/>
	</item>
	<item id="period" alias="周期" type="string" length="20"/>
	<item id="zkLevel" alias="质控级别" type="string" length="1">
		<dic id="chis.dictionary.QualityControl_ZKJB"/>
	</item>
	<item id="periodKind" alias="周期类别" type="string" length="1">
		<dic id="chis.dictionary.QualityControl_ZKLB"/>
	</item>
	<item id="finishDate" alias="完成日期" type="date" display="0"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="150" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"  />
	</item>
	
	<item id="createUser" alias="录入医生" type="string" length="20" update="false"  defaultValue="%user.userId" fixed="true" display="0">
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
		width="180" display="0" defaultValue="%user.manageUnit.id">
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
		fixed="true" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
