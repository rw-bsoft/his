<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpi.schemas.MPI_DemographicInfo" alias="EMPI个人基本信息" sort=" createTime desc" version="1332124044000" filename="E:\MyProject\BZWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\mpi/MPI_DemographicInfo.xml">
	<item id="empiId" alias="EMPI" type="string" length="32" display="0" pkey="true" />
	<item id="cardNo" alias="卡号" xtype="iccardfield" type="string" virtual="true" display="2" update="false" length="20" queryable="true"/>
	<item id="personName" alias="姓名" type="string" length="20" queryable="true" not-null="1"/>
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" queryable="true" vtype="idCard" enableKeyEvents="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true" not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" queryable="true" not-null="1" maxValue="%server.date.today"/>
</entry>
