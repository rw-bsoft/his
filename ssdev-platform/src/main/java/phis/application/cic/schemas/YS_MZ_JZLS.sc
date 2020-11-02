<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_JZLS" tableName="YS_MZ_JZLS" alias="就诊历史" sort="a.JZXH desc">
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
	<item ref="c.JZHM" alias="就诊号码"  />
	<item ref="b.MZHM" alias="门诊号码" type="string" length="32" width="120" not-null="1" queryable="true" selected="true"/>
	<item ref="b.BRXZ" alias="性质" fixed="true" length="18" width="60" />
	<item ref="b.BRXM" alias="姓名" fixed="true" type="string" length="40" width="60" queryable="true" />
	<item ref="b.BRXB" alias="性别" fixed="true" length="4" width="40" />
	<item ref="b.CSNY" alias="出生年月" fixed="true" length="4" display="0" width="40" />
	<item ref="c.GHSJ" />
	<item ref="c.CZPB" />
	<item id="KSDM" alias="就诊科室" type="long" length="4" not-null="1">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="YSDM" alias="就诊医生" type="string" length="10" not-null="1" queryable="true">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZYZD" alias="主要诊断" length="18" display="0" type="string"/>
	<item id="KSSJ" alias="开始时间" type="timestamp" not-null="1" width="140" queryable="true"/>
	<item id="JSSJ" alias="结束时间" type="timestamp" width="140"/>
	<item id="JZZT" alias="就诊状态" type="int" length="1" not-null="1" display="0"/>
	<item id="YYXH" alias="复诊预约序号" length="18" display="0" type="string"/>
	<item id="FZRQ" alias="复诊日期" type="timestamp" display="0"/>
	<item id="GHFZ" alias="挂号复诊" length="1" display="0" type="string"/>
	<item id="SFDY" alias="是否打印" length="2" display="0" type="int"/>
	
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA">
			<join parent="BRID" child="BRBH" />
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_GHMX_MZ">
			<join parent="SBXH" child="GHXH" />
		</relation>
	</relations>
</entry>
