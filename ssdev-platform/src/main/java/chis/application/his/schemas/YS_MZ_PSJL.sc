<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_PSJL" tableName="YS_MZ_PSJL" alias="皮试记录表" sort="SQSJ desc">
	<item id="JLBH" alias="记录编号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="皮试医疗机构"  type="string" display="0" length="20" width="100" >
		<dic id="phis.@manageUnit" />
	</item>
	<item id="BRBH" alias="病人编号"  type="long" length="18" not-null="1" display="0"/>
	<item ref="c.BRXM" alias="姓名" width="60" display="0"/>
	<item id="YPBH" alias="药品编号"  type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YPMC" width="180" type="string"/>
	<item id="PSJG" alias="皮试结果"  type="int" length="1"  display="0">
		<dic id="phis.dictionary.skintestResult"/>
	</item>
	<item id="WCBZ" alias="完成标志"  type="int" length="1" not-null="1" display="0"/>
	<item id="PSYS" alias="皮试医生"  type="string" length="10"	 display="0">
		<dic id="phis.dictionary.user" />
	</item>
	<item id="PSSJ" alias="皮试时间"  type="date" width="130"  display="0"/>
	<item id="SQYS" alias="申请医生"  type="string" length="10" not-null="1" display="0">
		<dic id="phis.dictionary.user" />
	</item>
	<item id="SQSJ" alias="申请时间"  type="date"  not-null="1" width="130" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.YK_TYPK_MS" >
			<join parent="YPBH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.MS_BRDA" >
			<join parent="BRBH" child="BRID"></join>
		</relation>
	</relations>
</entry>