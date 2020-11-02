<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_JZLS" tableName="YS_MZ_JZLS" alias="就诊历史" sort="a.JZXH desc">
	<item id="JZXH" alias="就诊序号" length="18" type="long" not-null="1" hidden="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="1000"/>
		</key>
	</item>
	<item id="GHXH" asName="SBXH" alias="挂号序号" type="long" length="18" not-null="1" display="0"/>
	<item id="BRBH" asName="BRID" alias="病人编号" type="long" length="18" not-null="1" display="0"/>
	<item id="KSSJ" alias="就诊时间" type="timestamp" not-null="1" width="140"/>
	<item id="JGID" alias="就诊机构" length="25" width="180" type="string">
		<dic id="phis.@manageUnit"/>
	</item>
	<item id="KSDM" alias="就诊科室" type="long" length="4" not-null="1" display="0">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="YSDM" alias="就诊医生" type="string" length="10" not-null="1">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item ref="b.SSY" alias="收缩压" type="int" length="3" width="70"/>
	<item ref="b.SZY" alias="舒张压" type="int" length="3" width="70"/>
	<item ref="b.P" alias="心率" type="int" length="3" width="50"/>
	<item ref="b.W" alias="体重" type="int" length="4" width="60"/>
	<item ref="b.H" alias="身高" type="int" length="6" precision="2" width="60"/>
	<item ref="b.BMI" alias="BMI" type="double" length="3" width="60"/>
	<item id="ZYZD" alias="主要诊断" length="18" display="0" type="string"/>
	<item id="JSSJ" alias="结束时间" type="timestamp" width="140" display="0"/>
	<item id="JZZT" alias="就诊状态" type="int" length="1" not-null="1" display="0"/>
	<item id="YYXH" alias="复诊预约序号" length="18" display="0" type="string"/>
	<item id="FZRQ" alias="复诊日期" type="timestamp" display="0"/>
	<item id="GHFZ" alias="挂号复诊" length="1" display="0" type="string"/>
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.MS_BCJL">
			<join parent="JZXH" child="JZXH" />
		</relation>
	</relations>
</entry>
