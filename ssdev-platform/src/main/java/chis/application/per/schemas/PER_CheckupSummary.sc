<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.per.schemas.PER_CheckupSummary" alias="体检小结表">
	<item id="id" alias="ID" type="string" length="16" hidden="true"
		pkey="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="checkupNo" alias="体检号" type="string" length="16"
		display="0" />
	<item id="recordNo" alias="档案号" type="string" length="30"
		display="0" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" />
	<item id="checkupOffice" alias="体检科室" type="string" length="16"
		display="0" />
	<item id="projectOfficeCode" alias="项目科室代码" type="string"
		length="16" display="0" />
	<item id="checkupSummary" alias="体检小结" type="string" length="200" width="300"
		xtype="textarea" colspan="2"  anchor="100%"/>
	<item id="exceptionDesc" alias="结果描述" type="string" length="1000" width="600" anchor="100%"
		xtype="textarea" colspan="2" fixed="true" />
	<item id="summaryDate" alias="小结日期" type="date" display="0"
		defaultValue="%server.date.today" />
	<!--
		<item id="ifException" alias="是否检查" width="100">
			<dic render="Simple" autoLoad="true">
				<item key="1" text="已查" />
				<item key="3" text="未查" />
			</dic>
		</item>
		-->
	<item id="inputDoctor" alias="录入医生" type="string" length="20" colspan="2" defaultValue="%user.userId" fixed="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="checkupDoctor" alias="体检医生" colspan="2" type="string"
		length="50" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
