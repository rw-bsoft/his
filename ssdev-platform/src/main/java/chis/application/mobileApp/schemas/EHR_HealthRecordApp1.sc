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
	<item ref="b.empiId" display="1" queryable="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<!--<item ref="b.photo" display="1" queryable="true"/>-->
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.workPlace" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
    <item ref="b.address" display="1" queryable="true"/>
	<!--<item ref="b.phoneNumber" display="1" queryable="true"/>
	<item ref="b.contact" display="1" queryable="true"/>
	<item ref="b.contactPhone" display="1" queryable="true"/>
	<item ref="b.registeredPermanent" display="1" queryable="true"/>
	<item ref="b.nationCode" display="1" queryable="true"/>
	<item ref="b.bloodTypeCode" display="1" queryable="true"/>
	<item ref="b.rhBloodCode" display="1" queryable="true"/>
	<item ref="b.educationCode" display="1" queryable="true"/>
    <item ref="b.workCode" display="1" queryable="true"/>
	<item ref="b.maritalStatusCode" display="1" queryable="true"/>
    <item ref="b.insuranceCode" display="1" queryable="true"/>
    <item ref="b.insuranceType" display="1" queryable="true"/>-->
    <item ref="b.homePlace" display="1" queryable="true"/>
    <!--<item ref="b.zipCode" display="1" queryable="true"/>
    <item ref="b.email" display="1" queryable="true"/>
    <item ref="b.nationalityCode" display="1" queryable="true"/>
    <item ref="b.startWorkDate" display="1" queryable="true"/>
    <item ref="b.insuranceText" display="1" queryable="true"/>
    <item ref="b.definePhrid" display="1" queryable="true"/>
    <item ref="b.zlls" display="1" queryable="true"/>-->
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
