<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YPXX" alias="药房药品信息" sort="a.DRSJ,a.YPXH desc">
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23" length="20" virtual="true" display="1"/>
	<item ref="b.YPMC" colspan="3" fixed="true"/>
	<item ref="b.PYDM" display="0"/>
	<item ref="b.WBDM" display="0"/>
	<item ref="b.JXDM" display="0"/>
	<item ref="b.QTDM" display="0"/>
	<item id="JGID" alias="机构ID" length="20"  update="false" display="0" not-null="1" defaultValue="%user.properties.manaUnitId"/>
	<item id="YFSB" alias="药房识别" length="18" type="long"  display="0" not-null="1" defaultValue="%user.properties.pharmacyId" pkey="true" update="false"/>
	<item id="YPXH" alias="药品序号" length="18" type="long"  display="0" not-null="1" generator="assigned" pkey="true" update="false"/>
	
	<item ref="b.YPGG" alias="药库规格" fixed="true" />
	<item ref="b.YPDW" alias="药库单位" fixed="true" />
	<item ref="b.ZXBZ" alias="药库包装" display="2" type="int" fixed="true"/>
	<item id="YFGG" alias="药房规格" type="string" length="20"/>
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="YFBZ" alias="药房包装" display="2" type="int" length="4" not-null="1"/>
	<item id="DRSJ" alias="导入时间" type="datetime" display="1" not-null="1" width="150"/>
	<item id="XGSJ" alias="修改时间" type="datetime" defaultValue="%server.date.datetime" width="150" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="YFGC" alias="药房高储" length="10" type="double" precision="2"  not-null="1" maxValue="999999.99" defaultValue="0"/>
	<item id="YFDC" alias="药房低储" length="10" precision="2" type="double"   not-null="1" maxValue="999999.99" defaultValue="0"/>
	<item id="QZCL" alias="取整策略" display="2"  type="int"
		length="1"  defaultValue="1" >
		<dic>
			<item key="0" text="每次发药数量取整"/>
			<item key="1" text="每天发药数量取整"/>
			<item key="2" text="不取整"/>
		</dic>
	</item>
	<item id="YFZF" alias="药房作废" length="1" display="0" not-null="1" type="int" update="false"/>
	<item id="KWBM" alias="库位编码" type="string"  length="20"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		   <join parent="TP" child="YPXH"></join>
	</relations>

</entry>
