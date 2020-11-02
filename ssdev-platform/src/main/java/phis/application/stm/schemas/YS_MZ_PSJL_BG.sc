<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_PSJL" tableName="YS_MZ_PSJL" alias="皮试记录表"
	sort="SQSJ">
	<item id="JLBH" alias="记录编号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="皮试医疗机构" type="string" length="20" width="100"
		display="0" />
	<item id="BRBH" alias="病人编号" type="long" length="18" not-null="1"
		display="0" />
	<item id="CFSB" alias="处方识别" type="long" length="18" display="0" />
	<item ref="c.BRXM" alias="姓名" width="60" />
	<item ref="c.EMPIID" alias="empiId" display="0"/>
	<item ref="b.YYBS"  display="0"/>
	<item ref="b.GMYWLB"  display="0"/>
	<item ref="d.CFHM" alias="处方号码" width="100" type="string" />
	<item id="YPBH" alias="药品编号" type="long" length="18" not-null="1"
		display="0" />
	<item ref="b.YPMC" width="100" type="string" />
	<item id="PSJG" alias="皮试结果" type="int" length="1" display="0">
		<dic id="phis.dictionary.skintestResult" />
	</item>
	<item id="WCBZ" alias="完成标志" type="int" length="1" not-null="1"
		display="0" />
	<item id="PSYS" alias="皮试医生" type="string" length="10" display="0">
		<dic id="phis.dictionary.user" />
	</item>
	<item id="PSSJ_Current" virtual="true" renderer="curentTime" alias="当前时间" type="string"
		width="80" />
	<item id="PSSJ_Total" virtual="true" alias="皮试时间(分钟)" type="int"
		defaultValue="20" width="110" />
	<item id="PSSJ" alias="皮试开始时间" type="datetime" width="130" display="0" />
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