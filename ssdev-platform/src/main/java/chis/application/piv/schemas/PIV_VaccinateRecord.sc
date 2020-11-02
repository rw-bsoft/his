<?xml version="1.0" encoding="utf-8"?>

<entry  alias="计划免疫档案">
  <item id="phrId" alias="档案编号" type="string" length="30" not-null="1" generator="assigned" pkey="true"/>
  
  	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
  
	<item id="manaDoctorId" alias="责任医生" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlyLeafSelected="true" keyNotUniquely="true"/>
	</item>
	<item id="empiId" alias="empiid" type="string" length="32" not-null="1" display="0"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="100">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree"/>
	</item>
	<item id="cardNo" alias="接种卡号" type="string" length="20"  width="100"/>
	
	<item id="fatherEmpiId" alias="父亲empiid" type="string" length="32" display="0"/>
	<item id="motherEmpiId" alias="母亲empiid" type="string" length="32" display="0"/>
	
	<item id="fatherName" alias="父亲姓名" type="string" length="20"/>
	<item id="motherName" alias="母亲姓名" type="string" length="20"/>
	
	<item id="registeredType" alias="户口类型" type="string" length="1" display="0">
	</item>
	<item id="certificateNo" alias="出生证明编号" type="string" length="10"/>
	<item id="allerGY" alias="过敏症状" type="string" length="100"/>
	<item id="allerGYSource" alias="过敏原" type="string" length="20"/>
  
  <relations>
	<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
  </relations>
</entry>
