<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_PSJL" tableName="YS_MZ_PSJL" alias="皮试记录表"
	sort="SQSJ desc">
	<item id="JLBH" alias="记录编号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="BRBH" alias="病人编号" type="long" length="18" not-null="1"
		display="0" />
	<item id="CFSB" alias="处方识别" type="long" length="18" display="0" />
	<item ref="c.MZHM" alias="门诊号码" width="80" queryable="true" selected="true"/>
	<item ref="d.CFHM" alias="处方号码" length="18" queryable="true"/>
	<item ref="c.BRXM" alias="姓名" width="60" queryable="true"/>
	<item id="PSSJ" alias="皮试时间" type="datetime" width="130" />
	<item id="PSYS" alias="皮试医生" type="string" length="10">
		<dic id="phis.dictionary.user" />
	</item>
	<item id="PSJG" alias="皮试结果" type="int" length="1" queryable="true">
		<dic id="phis.dictionary.skintestResult" />
	</item>
	<item id="GMPH" alias="过敏批号" type="string" />
	<item id="YPBH" alias="药品编号" type="long" length="18" not-null="1"
		display="0" />
	<item ref="b.YPMC" width="100" type="string" />
	<item ref="b.YPGG" width="100" type="string" />
	<item ref="b.YPDW" width="60" type="string" />
	<item ref="d.YSDM" alias="开方医生" type="string" length="10" />
	<item ref="d.KSDM" alias="医生科室" type="string" length="10" />
	<item id="WCBZ" alias="完成标志" type="int" length="1" not-null="1"
		display="0" />
	<item id="SQYS" alias="申请医生" type="string" length="10" not-null="1" display="0">
		<dic id="phis.dictionary.user" />
	</item>
	<item id="SQSJ" alias="申请时间" type="date" not-null="1" width="130" display="0" />
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.YK_TYPK_MS">
			<join parent="YPBH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.MS_BRDA">
			<join parent="BRBH" child="BRID"></join>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.MS_CF01">
			<join parent="CFSB" child="CFSB"></join>
		</relation>
	</relations>
</entry>