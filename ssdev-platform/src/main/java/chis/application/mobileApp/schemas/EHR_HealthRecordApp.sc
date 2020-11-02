<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mobileApp.schemas.EHR_HealthRecordApp" alias="个人健康档案" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="20" width="160" not-null="1" fixed="true" queryable="true" generator="assigned">
		<key>
			<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
			</Rule>
			<Rule name="increaseId" index="1" length="8" startPos="1" seedRel="areaCode" type="increase"/>
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="b.photo" display="1" queryable="true"/>
	<item ref="b.bloodTypeCode" display="1" queryable="true"/>
	<item ref="b.rhBloodCode" display="1" queryable="true"/>
	<item ref="b.contact" display="1" queryable="true"/>
	<item ref="b.contactPhone" display="1" queryable="true"/>
	<item ref="b.registeredPermanent" display="1" queryable="true"/>
	<item ref="b.nationCode" display="1" queryable="true"/>
	<item ref="b.educationCode" display="1" queryable="true"/>
	<item ref="b.maritalStatusCode" display="1" queryable="true"/>
    <item ref="b.insuranceCode" display="1" queryable="true"/>
    <item ref="b.workCode" display="1" queryable="true"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="true" queryable="true" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="masterFlag" alias="是否户主" type="string"  length="1">
		<dic id="chis.dictionary.yesOrNo" render="Tree"/>
	</item>
	<item id="isAgrRegister" alias="是否农业户籍" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="incomeSource" alias="经济来源" type="string" length="10" not-null="1" queryable="true">
		<dic render="LovCombo">
			<item key="1" text="社会救济"/>
			<item key="2" text="工作收入"/>
			<item key="3" text="其他"/>
		</dic>
	</item>
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="regionCode" alias="网格地址" type="string" length="25" not-null="1" width="200" colspan="2" anchor="100%" update="false" queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="regionCode_text" alias="网格地址" type="string" length="200" display="0"/>
	<item id="signFlag" alias="签约标志" type="string" length="1" defaultValue="n" canInput="true" validateOnBlur="false">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="familyDoctorId" alias="家庭医生" type="string" length="20" update="false" queryable="true" >
		<dic id="chis.dictionary.familydoctor" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
