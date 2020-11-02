<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_PastHistory" alias="个人既往史查询">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30"
		width="160" not-null="1" fixed="true" generator="assigned"
		virtual="true" />
	<item id="empiId" hidden="true" alias="EMPIID" virtual="true" />
	<item id="personName" display="1" alias="姓名" virtual="true" />
	<item id="sexCode" display="1" alias="性别" virtual="true" >
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="birthday" display="1" alias="出生日期" virtual="true" />
	<item id="idCard" width="160" display="1" alias="身份证号" virtual="true" />
	<item id="mobileNumber" display="1" alias="联系电话" virtual="true" />
	<item id="contactPhone" display="2" alias="家庭电话" virtual="true" />
	<item id="registeredPermanent" display="2" alias="国籍" virtual="true" />
	<item id="diseaseCode" alias="既往史类别" type="string" length="2"
		display="2" queryable="true" virtual="true">
		<dic id="chis.dictionary.pastHistory" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="regionCode" alias="网格地址" type="string" length="25"
		not-null="1" width="200" colspan="2" anchor="100%" update="false"
		virtual="true">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="regionCode_text" display="0" virtual="true"/>
	
	<item id="masterFlag" alias="是否户主" type="string" not-null="1"
		width="180" length="1" virtual="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="relaCode" alias="与户主关系" type="string" not-null="1"
		length="2" virtual="true">
		<dic id="chis.dictionary.relaCode" />
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		not-null="1" fixed="true" virtual="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="manaUnitId" alias="管理机构" type="string" length="20"
		colspan="2" anchor="100%" width="180" not-null="1" fixed="true"
		virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="signFlag" alias="签约标志" type="string" length="1"
		defaultValue="y" virtual="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="createUnit" alias="建档单位" type="string" length="20" update="false"
		width="180" fixed="true" virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20"
		fixed="true" update="false" virtual="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="date" update="false"
		fixed="true" virtual="true" >
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date" defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.today']</set>
	</item>
</entry>