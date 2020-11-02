<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱">
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" not-null="1"/>
	<item id="YZZH" alias=" " type="long" length="1" fixed="true" width="10" renderer="showColor"/>
	<item id="BRCH" alias="床号" length="12" width="60" fixed="true"/>
	<item id="BRXM" alias="姓名" length="12" width="60" fixed="true"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="YZMC" alias="项目名称" width="200" length="100" fixed="true"/>
	<item id="YCSL" alias="数量" type="double" length="8"  precision="2" fixed="true"/>
	<item id="FYCS" alias="次数" type="double" length="8" virtual="true" precision="2"   not-null="1" fixed="true"/>
	<item id="JSSL" alias="计算数量" width="80" type="double" precision="2" length="80" fixed="true" renderer="doRender"/>
	<item id="QRSL" alias="确认数量" width="80" type="double" precision="2" length="80" virtual="true"  />
	<item id="YPDJ" alias="项目单价" type="double" length="8" precision="2" fixed="true"  not-null="1"/>
	<item id="ZJE" alias="总金额" width="80" type="double"  precision="2" fixed="true" virtual="true" length="80"/>
	<item id="SYPC" alias="用法"  not-null="1"  type="string" length="6" width="60" fixed="true">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="MRCS" alias="日次" type="int" display="0" length="2" not-null="1"/>
	<item id="KSSJ" alias="开嘱时间" width="150" fixed="true"/>
	<item id="TZSJ" alias="停嘱时间" width="150" fixed="true"/>
	<item id="YPXH" alias="医技序号" display="0" />
	<item id="TPN" alias="TPN" type="int" display="0" defaultValue="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.war.schemas.ZY_BRRY_BQ" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
