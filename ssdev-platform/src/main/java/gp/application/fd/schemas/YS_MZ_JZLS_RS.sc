<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_JZLS_RS" tableName="YS_MZ_JZLS" alias="就诊历史">
	<item id="JZXH" alias="就诊序号" length="18" type="long" not-null="1" hidden="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="1000"/>
		</key>
	</item>
	
	<item id="GHXH" asName="SBXH" alias="挂号序号" type="long" length="18" not-null="1" display="0"/>
	<item id="BRBH" asName="BRID" alias="病人编号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.EMPIID" alias="empiId"  display="0"/>
	<item id="JGID" alias="就诊机构" length="25" width="180" type="string">
		<dic id="phis.@manageUnit"/>
	</item>
	<item ref="b.MZHM" alias="门诊号码" type="string" length="32" width="120" not-null="1" queryable="true" selected="true"/>
	<item ref="b.BRXZ" alias="性质" fixed="true" length="18" width="60" />
	<item ref="b.BRXM" alias="姓名" fixed="true" type="string" length="40" width="80" queryable="true"/>
	<item ref="b.BRXB" alias="性别" fixed="true" length="4" width="40" />
	<item ref="b.CSNY" alias="出生年月" fixed="true" length="4" display="0" width="40" />
	<item id="JZSJ" alias="就诊时间" fixed="true" width="100"/>
	<item id="KSSJ" alias="开始时间" type="date" not-null="1" width="140"  display="0" queryable="true"/>
	
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA">
			<join parent="BRID" child="BRBH" />
		</relation>
	</relations>
</entry>
