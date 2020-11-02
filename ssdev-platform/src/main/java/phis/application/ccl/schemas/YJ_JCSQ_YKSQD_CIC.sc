<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_JCSQ_YKSQD_CIC" tableName="YJ_JCSQ_KD01" alias="检查申请-开单01-门诊" sort="a.SQDH"> 
	<item id="SQDH" alias="申请单号"  length="12" type="long" not-null="1" />
	<item id="YLLB" alias="医疗类别" type="int" length="2" not-null="1"  width="80" display="0"/>
	<item id="SSLX" alias="所属类型" type="int" length="2" not-null="1" width="80" display="1">
		<dic id="phis.dictionary.checkApplyType"></dic>
	</item>
	<item ref="b.JGID" fixed="true" width="100"/>
	<item ref="b.KDRQ" fixed="true" width="150"/>
	<item ref="b.YSDM" fixed="true" width="100"/>
	<item ref="b.BRID" fixed="true" width="100"/>
	<item ref="b.ZXPB" fixed="true" width="100" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_YJ01_CIC" >
			<join parent="YJXH" child="SQDH" />
		</relation>
	</relations>
</entry>