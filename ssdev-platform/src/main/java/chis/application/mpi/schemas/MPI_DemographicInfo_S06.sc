<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mpi.schemas.MPI_DemographicInfo"  alias="EMPI个人基本信息" sort="a.createTime desc">
	<item ref="b.operType" display="0" queryable="false"/>
	<item id="empiId" alias="EMPI" type="string" length="32" display="0"
		pkey="true" />
	<item id="personName" alias="姓名" type="string" length="20"
		queryable="true" not-null="true" />
	<item id="idCard" alias="身份证号" not-null="true" type="string"
		length="20" width="160" queryable="true" vtype="idCard"
		enableKeyEvents="true" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40"
		queryable="true" defalutValue="9" fixed="true">
		<dic id="gender" />
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75"
		queryable="true" fixed="true" />
	<item id="status" alias="状态" type="string" length="1" display="0" />
	<relations>
		<relation type="children" entryName="PUB_Log">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>