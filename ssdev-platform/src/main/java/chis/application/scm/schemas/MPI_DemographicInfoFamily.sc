<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.scm.schemas.MPI_DemographicInfoFamily" tableName="chis.application.mpi.schemas.MPI_DemographicInfo" alias="个人基本信息" sort=" createTime desc">
	<item id="empiId" alias="EMPI" type="string" length="32" display="0" pkey="true" />
	<item id="personName" alias="姓名" type="string" length="20" queryable="true" not-null="1"/>
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" queryable="true" vtype="idCard"
		  enableKeyEvents="true" encrypt="4-3"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true"
		  not-null="1" defaultValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" queryable="true" not-null="1" maxValue="%server.date.today"/>
	<item id="familyId" alias="所属家庭" type="string" length="30" hidden="true"/>
	<item id="mobileNumber" alias="本人电话" type="string" length="20" not-null="1" width="90" encrypt="3-3"  readGlass="mobilephone"/>
	<item ref="b.status" alias="状态" type="string" length="1" display="0"/>
	<item ref="b.familyId" alias="所属家庭" type="string" length="30" hidden="true" display="0"/>
	<relations>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>
