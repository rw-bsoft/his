<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_ZL_ZT02" alias="诊疗计划模版明细" joinMethod="left" sort="JLBH">
	<item id="JLBH" alias="记录编号" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true">
		<key>
			<rule type="sequence" seqName="seq_jczlzt02" />
		</key>
	</item>
	<item id="ZTBH" alias="组套编号" type="long" length="18" not-null="1"
		display="0" />
	<item id="YPZH" alias=" " type="int" length="3" not-null="1"
		renderer="showColor" width="20" fixed="true" />
	<item id="XMBH" alias="项目编号" type="long" length="18" not-null="1"
		display="0" />
	<item id="XMMC" alias="计划明细" length="100" not-null="1" mode="remote"
		width="195" />
	<item id="XMLX" alias="项目类型" type="int" length="1" not-null="1"
		display="0" />
	<item id="YCJL" alias="剂量" length="10" width="100" type="double"
		min="0" precision="3" max="9999999.999" defaultValue="1" />
	<item ref="b.JLDW" alias="" type="string" length="8" fixed="true"
		width="30" display="1" renderer="jldwRender"/>
	<item id="SYPC" alias="频次" length="12" width="80" type="string"
		not-null="1">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS"
			autoLoad="true" />
	</item>
	<item id="GYTJ" alias="途径" type="int" length="9" not-null="1">
		<dic id="phis.dictionary.drugMode" searchField="PYDM" autoLoad="true" />
	</item>
	<item id="KSSJ" alias="开始日期" type="date" not-null="1" />
	<item id="JSSJ" alias="结束日期" type="date" not-null="1" />
	<item id="BZXX" alias="备注" length="100" />
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.YK_TYPK_CIC" joinWay="left">
			<join parent="YPXH" child="XMBH"></join>
		</relation>
	</relations>
</entry>
