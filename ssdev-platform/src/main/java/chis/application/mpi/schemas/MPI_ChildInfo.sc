<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mpi.schemas.MPI_ChildInfo" alias="儿童直系亲属基本信息">

	<item id="childEmpiId" alias="childEmpiId" type="string" length="32"
		display="0" pkey="true" />
	<item id="relativeEmpiId" alias="relativeEmpiId" type="string"
		length="32" display="0" />
	<item id="relativeName" alias="姓名" type="string" length="20"
		queryable="true" not-null="1" />
	<item id="relativeIdCard" alias="身份证" type="string" length="20"
		width="160" queryable="true" not-null="1" />
	<item id="vaccinateCardNo" alias="计免接种卡号" type="string" length="20" />
	<item id="certificateNo" alias="出生证号" type="string" length="10" />
</entry>