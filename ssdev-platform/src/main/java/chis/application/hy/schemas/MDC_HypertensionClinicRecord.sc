<?xml version="1.0" encoding="UTF-8"?>
<entry alias="高血压就诊信息" sort="clinicDate desc">
	<item id="recordId" alias="就诊信息编号" type="string" display="0"
		length="16" not-null="1" pkey="true" fixed="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
	
	<item id="constriction" alias="收缩压(mmHg)" width="100" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"/>
	<item id="diastolic" alias="舒张压(mmHg)" width="100" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"/>
	
	<item id="height" alias="身高(cm)" not-null="1" type="double" minValue="0" maxValue="300" enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" not-null="1" type="double" minValue="0" maxValue="500" enableKeyEvents="true"/>
	
	<item id="clinicUnit" alias="就诊机构" type="string" length="20" update="false"
		fixed="true" defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="clinicUser" alias="就诊医生" type="string" length="20" update="false"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="clinicDate" alias="就诊日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>