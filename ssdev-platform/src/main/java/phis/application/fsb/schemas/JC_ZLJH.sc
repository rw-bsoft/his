<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_ZLJH" alias="诊疗计划" joinMethod="left">
	<item id="JLBH" alias="记录编号" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true">
		<key>
			<rule type="sequence" seqName="seq_zljh" />
		</key>
	</item>
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
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS" autoLoad="true" />
	</item>
	<item id="GYTJ" alias="途径" type="int" length="9" not-null="1">
		<dic id="phis.dictionary.drugMode" searchField="PYDM" />
	</item>
	<item id="KSSJ" alias="开始日期" type="date" not-null="1" />
	<item id="JSSJ" alias="结束日期" type="date" not-null="1" />
	<item id="YSDM" alias="计划医生" type="string">
		<dic id="phis.dictionary.doctor"  searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="BZXX" alias="备注" length="100" />
	<item id="ZYH" type="long" display="0" />
	
	<item ref="b.TYPE" type="int" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.fsb.schemas.YK_TYPK_FSB" joinWay="left">
			<join parent="YPXH" child="XMBH"></join>
		</relation>
	</relations>
</entry>
