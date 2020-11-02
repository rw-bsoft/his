<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="phis.application.dpc.schemas.DPC_ExtractionAgreement" alias="拔牙知情同意书病历版">
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	<item id="personName" alias="姓名" type="string" length="30" queryable="true" not-null="0"/>
	<item id="sex" alias="性别" type="string" length="1" defaultValue="1" queryable="true" width="50" not-null="0">
		<dic>
			<item key="1" text="男"/>
			<item key="2" text="女"/>
		</dic>
	</item>
	<item id="age" alias="年龄" type="int" queryable="true" width="50" not-null="0"/>
	<item id="operationDate" alias="手术日期" type="datetime" xtype="datefield" queryable="true"  not-null="0" defaultValue="%server.date.date"/>
	<item id="diagnosis" alias="疾病诊断" type="string" xtype="textarea" length="200" width="250" not-null="0"/>
	<item id="proposed" alias="拟施手术" type="string" xtype="textarea" length="200" width="250" not-null="0"/>
	<item id="doctorId" alias="医生ID" type="string" length="18" not-null="1" defaultValue="%user.userId" display="0"/>
	<item id="createUser" alias="录入人" type="string" length="20" fixed="true" update="false"
		display="3" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false"
		fixed="true" display="3" defaultValue="%user.manageUnit.id" width="250">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield"  fixed="true" update="false"
		display="3" defaultValue="%server.date.date" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改时间"  type="datetime"  xtype="datefield"  fixed="true"
		display="1" defaultValue="%server.date.date">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>