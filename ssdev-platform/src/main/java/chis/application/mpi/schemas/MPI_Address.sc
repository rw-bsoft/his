<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mpi.schemas.MPI_Address" alias="EMPI个人基本信息">
	<item id="addressId"  alias="编号"  type="string"  hidden="true"	length="16" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId"          alias="EPMI"    type="string" hidden="true" length="32" fixed="true"/>
	<item id="addressTypeCode" alias="地址类型" defaultValue="05"  type="string" length="2" not-null="1">
		<dic id="chis.dictionary.address"/>
	</item>
	<item id="address"         alias="地址"   type="string" length="100" not-null="1" width="200"/>
	<item id="postalCode"      alias="邮编" vtype="alphanum"  type="string" length="6"/>
	<item id="createUnit" alias="建档机构" type="string" length="16"
		canRead="false" display="0" />
	<item id="createUser" alias="建档人" type="string" length="20"
		display="0" queryable="true" />
	<item id="createTime" alias="建档时间" type="timestamp" display="0" />
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="16"
		display="0" />
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="0" />
	<item id="lastModifyTime" alias="最后修改时间" type="timestamp" display="0" />
</entry>