<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_JZLS_CP" alias="就诊历史" tableName="YS_MZ_JZLS" sort="a.KSSJ desc">
	<item id="JZXH" alias="就诊序号" length="18" type="long" not-null="1" hidden="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="1000"/>
		</key>
	</item>
	
	<item id="GHXH" asName="SBXH" alias="挂号序号" type="long" length="18" not-null="1" display="0"/>
	<item id="BRBH" asName="BRID" alias="病人编号" type="long" length="18" not-null="1" display="0"/>
	
	
	<item id="KSDM" alias="就诊科室" type="long" length="4" not-null="1">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="YSDM" alias="就诊医生" type="string" length="10" not-null="1">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZYZD" alias="主要诊断" length="18" display="0" type="string"/>
	<item id="KSSJ" alias="开始时间" type="timestamp" not-null="1" width="140"/>
	<item id="JSSJ" alias="结束时间" type="timestamp" width="140"/>
	<item id="JZZT" alias="就诊状态" type="int" length="1" not-null="1" display="0"/>
	<item id="YYXH" alias="复诊预约序号" length="18" display="0" type="string"/>
	<item id="FZRQ" alias="复诊日期" type="timestamp" display="0"/>
	<item id="GHFZ" alias="挂号复诊" length="1" display="0" type="string"/>
	<item id="JGID" alias="就诊机构" length="25" width="180" type="string">
		<dic id="phis.@manageUnit"/>
	</item>
	
</entry>
