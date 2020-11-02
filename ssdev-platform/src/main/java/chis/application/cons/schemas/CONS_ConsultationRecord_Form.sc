<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cons.schemas.CONS_ConsultationRecord" alias="会诊记录">
	<item id="recordId" alias="recordId" length="16" not-null="1" generator="assigned"  pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="healthNo" alias="健康卡号" type="string" length="30" virtual="true"/>
	<item id="personName" alias="姓名" type="string" length="20" virtual="true" not-null="1"/>
	<item id="idCard" alias="身份证号" type="string" length="20" virtual="true" width="160"  vtype="idCard" enableKeyEvents="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" virtual="true" width="40"  not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" virtual="true"  not-null="1" maxValue="%server.date.today"/>

	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1"  hidden="true" />
	<item id="phrId" alias="档案编号" type="string" length="30" display="0"/>
	<item id="serialNumber" type="string" alias="编号" not-null="1" length="50"/>
	<item id="consultationReason" type="string" not-null="1" alias="会诊原因" xtype="textarea" colspan="3" length="1000"/>
	<item id="consultationOpinion" type="string" not-null="1" alias="会诊意见" xtype="textarea" colspan="3" length="1000"/>
	<item id="doctor" alias="会诊医生" type="string" not-null="1" colspan="3" length="1000">
		<dic id="chis.dictionary.userHER" render="TreeCheck" onlyLeafCheckable="true" maxHeight="400" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" fixed="true" colspan="2" length="25">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="consultationDate" alias="会诊日期" type="date" not-null="1" defaultValue="%server.date.date" maxValue="%server.date.date"/>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" display="1"
		width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入医生" type="string" length="20" update="false" display="1"
		fixed="true" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" display="1" xtype="datefield" fixed="true" defaultValue="%server.date.today"  update="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
