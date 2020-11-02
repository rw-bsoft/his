<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="高血压询问记录">
	<item id="inquireId" alias="记录号" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1" width="165" fixed="true" display="0" />
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" fixed="true" hidden="true" />
	
	<item id="inquireDate" alias="询问日期" type="date" not-null="1" fixed="true" defaultValue="%server.date.today" maxValue="%server.date.today">
		<!-- set type="exp">['$','%server.date.today']</set -->
	</item>
	<item id="inquireWay" alias="询问方式" type="string" length="1"	not-null="1">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"/>

	<item id="description" alias="措施描述" type="string" xtype="textarea" length="100" colspan="2" display="2" />

	<item id="isReferral" alias="是否已转诊" type="string" not-null="true" defaultValue="2">
		<dic>
			<item key="1" text="已转诊"/>
			<item key="2" text="未转诊"/>
		</dic>
	</item>
	<item id="agencyAndDept" alias="机构及科别" type="string" fixed="true"/>
	<item id="referralReason" alias="转诊原因" type="string" colspan="2" fixed="true"/>
	
	<item id="inquireUnit" alias="询问机构" type="string" length="20" update="false" fixed="true" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="inquireUser" alias="询问人员" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputUser" alias="录入员工" type="string" length="20" display="1" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" update="false" length="20" fixed="true" display="1" defaultValue="%user.manageUnit.id" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="inputDate" alias="录入日期" type="date" fixed="true" update="false"
		defaultValue="%server.date.today" display="1">
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
