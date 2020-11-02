<?xml version="1.0" encoding="UTF-8"?>

<entry alias="高血压中医辨体">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" hidden="true"/>
	<item id="visitId" alias="随访标识" type="string" length="16" hidden="true"/>
	<item id="tongueApprence" alias="舌象" type="string" length="50" >
		<dic id="chis.dictionary.mdcDescription01" render ="LovCombo"/>
	</item>
	
	<item id="tongueColor" alias="舌色" type="string" length="50" >
		<dic id="chis.dictionary.mdcDescription02" render ="LovCombo"/>
	</item>
	<item id="tongueMoss" alias="舌苔" type="string" length="50" >
		<dic id="chis.dictionary.mdcDescription03" render ="LovCombo"/>
	</item>
	<item id="pulse" alias="脉象" type="string" length="2">
		<dic id="chis.dictionary.mdcDescription04"/>
	</item>
	<item id="color" alias="兼色" type="string" length="50" colspan="2"/>
	<item id="othersCondition" alias="其他状况" type="string" length="50" colspan="2"/>
	<item id="differentiation" alias="中医辨证" type="string" length="50" colspan="2">
		<dic id="chis.dictionary.mdcDescription05" render ="LovCombo"/>
	</item>
	<item id="diagnosis" alias="中医诊断" type="string" length="200" colspan="2"/>
	<item id="jianJia" alias="兼/夹" type="string" length="200" colspan="2"/>
	<item id="guide" alias="中医指导" type="string" length="2000"  xtype="textarea" colspan="2"/>
	
	<item id="inputUser" alias="录入员工" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" update="false" length="20" fixed="true" defaultValue="%user.manageUnit.id" width="150" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
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
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
