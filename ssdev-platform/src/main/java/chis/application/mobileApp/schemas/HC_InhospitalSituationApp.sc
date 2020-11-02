<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_InhospitalSituation" alias="住院治疗情况" sort="situationId">
	<item  id="situationId" alias="记录序号" length="16" width="150" type="string" pkey="true" generator="assigned" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item  id="healthCheck" alias="年检编号" length="16" type="string" display="0"/>
	
	<item id="inhospitalDate" alias="入院日期" type="date" width="150" not-null="true" maxValue="%server.date.today"/>
	<item id="outhospitalDate" alias="出院日期" type="date" width="150" maxValue="%server.date.today"/>
	<item id="inhospitalReason" alias="原因" length="50" type="string" width="150" not-null="true"/>
	<item id="medicalestablishmentName" alias="医疗机构名称" length="50" type="string" width="150" not-null="1"/>
	<item id="medicalrecordNumber" alias="医疗病案号" length="50" type="string" width="150"/>
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false" display="0"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<!--
	<item id="b.medicine" alias="药物名称" length="50" type="string" width="150" queryable="true" not-null="1" mode="remote"/>
	<item id="b.use" alias="用法" type="string" width="150" queryable="true" not-null="1"/>
	<item id="b.eachDose" alias="用量" length="50" type="string" width="150" queryable="true" not-null="1"/>
	<item id="b.useDate" alias="用药时间"  type="string" width="150" queryable="true" maxValue="%server.date.today" not-null="1"/>
	<item id="b.medicineYield" alias="服药依从性" type="string" length="1" colspan="2" not-null="1">
	<dic>
			<item key="1" text="规律" />
			<item key="2" text="间断" />
			<item key="3" text="不服药" />
		</dic>
	</item>-->
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"  display="0"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"  display="0"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
