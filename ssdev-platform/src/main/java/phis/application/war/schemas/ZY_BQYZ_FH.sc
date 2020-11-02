<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱" sort="YZZH asc,YJZX desc,JLXH asc">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" display="0" />
	<item id="YZZH" alias="医嘱组号" type="long" length="1" isGenerate="false" display="0" width="10" >
		<key>
			<rule name="increaseYzzh"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item ref="c.ZYHM" queryable="true"/>
	<item ref="c.BRXM" />
	<item ref="c.BRCH"/>
	<item id="KSSJ" alias="开嘱时间" type="datetimefield" width="120" renderer="dateFormat" />
	<item id="YZMC" alias="医嘱项目" type="string"  length="100" width="200"/>
	
	<item id="YCJL" alias="剂量" type="double" max="9999999.999" length="10" precision="3" not-null="true" width="60"/>
	<item id="JLDW" alias=" " display="1" width="30" fixed="true" virtual="true"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" min="0.01" max="9999.99" not-null="true" width="60" defaultValue="1"/>
	<item id="YFDW" alias=" "  width="30"  type="string" fixed="true" length="4" />
	<item id="SYPC" alias="频次"  not-null="true"  type="string" length="6" width="60">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="MRCS" alias="每日次数" type="int" display="0"/>
	<item ref="b.XMMC" alias="途径"/>
	<!-- 
	<item id="YZZXSJ" alias="执行时间" length="80" fixed="true"/>
	<item id="SRCS" alias="首日次数" type="int" max="999999" not-null="true" length="6" width="80" renderer="srcsRender" defaultValue="0"/>
	<item id="YPYF" alias="途径" not-null="true" type="long" length="18" listWidth="90" width="60" >
		<dic id="phis.dictionary.drugWay" searchField="PYDM" fields="key,text,PYDM,FYXH" autoLoad="true"/>
	</item>
	-->
	<item id="YPYF" alias="途径" not-null="true" type="long" length="18" listWidth="90" width="60" display="0">
		
	</item>
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" min="0.0001" max="99999999.9999" nullToValue="0" not-null="true" width="70"/>
	<item id="YSGH" alias="开嘱医生" type="string" not-null="true" length="10" width="60" >
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="TZSJ" alias="停嘱时间" type="datetime" renderer="dateFormat" width="120" />
	<item id="TZYS" alias="停嘱医生" type="string" length="10">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="MZCS" alias="每周次数" type="int" display="0" defaultValue="0"/>
	<item id="CZGH" alias="转抄" type="string" fixed="true" length="10" defaultValue="%user.userId" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="QRSJ" alias="确认时间" type="datetime" display="0"/>
	<item id="YJZX" alias="医技主项" type="int" display="0" defaultValue="0"/>
	<item id="ZXKS" alias="执行科室" type="long" display="0"/>
	<item id="FHGH" alias="复核" type="string" fixed="true" length="10" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="YEPB" alias="婴儿判别" type="int" display="0" defaultValue="0"/>
	<item id="BZXX" alias="说明" type="string" length="125" width="80"/>
	<item id="FYSX" alias="发药属性" type="int" display="0" defaultValue="0"/>
	<item id="YFSB" alias="发药药房" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.wardPharmacy" autoLoad="true" filter="['and',['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']],['eq',['$','item.properties.BQDM'],['$','%user.properties.wardId']]]"/>
	</item>
	<item id="LSYZ" alias="临时医嘱" type="int" display="0" defaultValue="0"/>
	<item id="YZPB" alias="医嘱判别" type="int" display="0" defaultValue="0"/>
	<item id="LSBZ" alias="历史标志" type="int" display="0" defaultValue="0"/>
	<item id="JFBZ" alias="计费标志" type="int" display="0" defaultValue="2"/>
	<item id="YPLX" alias="药品类型" type="int" display="0" defaultValue="-1"/>
	
	
	<relations>
		<relation type="child" entryName="phis.application.cfg.schemas.ZY_YPYF" >
			<join parent="YPYF" child="YPYF"></join>
		</relation>
		<relation type="child" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		
	</relations>
</entry>
