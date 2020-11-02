<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesComplication" alias="糖尿病并发症">
	<item id="complicationId" pkey="true" alias="并发症识别" type="string"
		length="16" hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" fixed="true"
		noList="true" width="165" display="2"/>
	<item id="visitId" alias="随访标识" type="string" length="16"
		hidden="true" />
	<item id="visitFlag" alias="随访标记" type="string" length="1"
		hidden="true" />
	<item id="years" alias="病程年数" type="string" length="25" virtual="true" display="1" fixed="true"/>
	<item id="complicationCode" alias="并发症" type="string" length="8" width="150" fixed = "true" >
		<dic id="chis.dictionary.diabetesComplication" />
	</item>
	<item id="diagnosisDate" alias="诊断日期" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true" maxValue="%server.date.today"/>
	<!-- 
		<item id="complicationChange" alias="并发症转归" type="string" length="1"
			colspan="2">
			<dic>
				<item key="1" text="缓解" />
				<item key="2" text="好转" />
				<item key="3" text="痊愈" />
				<item key="4" text="未愈" />
				<item key="5" text="药物维持" />
			</dic>
		</item>
		 -->
	<item id="inputUnit" alias="录入单位" type="string" update="false" length="20" fixed="true" defaultValue="%user.manageUnit.id" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="date" update="false" fixed="true" defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="inputUser" alias="录入员工" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
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