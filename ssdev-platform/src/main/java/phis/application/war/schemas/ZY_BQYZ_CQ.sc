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
	<item id="uniqueId" alias="附加项目关联字段" type="long" fixed="true" virtual="true" display="0"/>
	<item id="YZZH_SHOW" alias=" " type="long" length="1" fixed="true" width="8" renderer="showColor" virtual="true"/>
	<item id="YZZH" alias="医嘱组号" type="long" length="1" display="0" width="10" >
	</item>
	<item id="KSSJ" alias="开嘱时间" type="datetime" width="120" renderer="dateFormat" />
	<item id="ZFYP" defaultValue="0" alias="转" xtype="checkBox" width="40" type="int" length="1">
	</item>
	<item id="ZBY" alias="药品是否本来就是自备药" type="int" length="1" display="0"
		width="1" virtual="true" />
	<item id="YZMC" alias="长期医嘱项目" type="string" mode="remote" length="100" width="200"/>
	<item id="YPJL" alias="药品剂量" type="string" display="0" virtual="true"/>
	<item id="YCJL" alias="剂量" type="double" max="9999999.999" length="10" precision="3" not-null="true" width="60"/>
	<item id="JLDW" alias=" " type="string" display="1" width="30" fixed="true" virtual="true"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" min="0.01" max="9999.99" not-null="true" width="60" defaultValue="1"/>
	<item id="YFDW" alias=" "  width="30"  type="string" fixed="true" length="4" />
	<item id="SYPC" alias="频次"  not-null="true"  type="string" length="6" width="60">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="YYTS" alias="天数" type="int" length="4" width="60" display="0"/>
	<item id="MRCS" alias="每日次数" type="int" display="0"/>
	<item id="YZZXSJ" alias="执行时间" type="string" length="80" />
	<item id="SRCS" alias="首日次数" type="int" max="999999" not-null="true" length="6" width="80" renderer="srcsRender" defaultValue="0"/>
	<item id="YPYF" alias="途径" not-null="true" type="long" length="18" listWidth="90" width="60" >
		<dic id="phis.dictionary.drugWay" searchField="PYDM" fields="key,text,PYDM,FYXH" autoLoad="true"/>
	</item>
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" min="0.0001" max="99999999.9999" nullToValue="0" not-null="true" width="70"/>
	<item id="YSGH" alias="开嘱医生" type="string" not-null="true" length="10" width="60" >
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSTJ" alias="状态" type="int" defaultValue="0" width="45" renderer="YstjRender" fixed="true"/>
	<item id="YSBZ" alias=" " type="int" length="1" fixed="true" width="30"  defaultValue="0" renderer="ysbzRender" />
	<item id="TZSJ" alias="停嘱时间" type="datetime" renderer="dateFormat" width="120" />
	<item id="TZYS" alias="停嘱医生" type="string" length="10">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="CZGH" alias="转抄" type="string" fixed="true" length="10" defaultValue="%user.userId" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="FHGH" alias="复核" type="string" fixed="true" length="10" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="FYFS" alias="发药方式" type="long" length="18">
		<dic id="phis.dictionary.hairMedicineWay" autoLoad="true"/>
	</item>
	<item id="BZXX" alias="说明" type="string" length="125" width="80"/>
	<item id="YPCD" alias="产地" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.medicinePlace" autoLoad="true"/>
	</item>
	<item id="YFSB" alias="发药药房" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.wardPharmacy" autoLoad="true" />
	</item>
	<item id="YFBZ" alias="药房包装" type="int" display="0" />
	<item id="YPLX" alias="药品类型" type="int" display="0" defaultValue="-1"/>
	<item id="SYBZ" alias="使用标志" type="int" display="0" defaultValue="0"/>
	<item id="MZCS" alias="每周次数" type="int" display="0" defaultValue="0"/>
	<item id="YJZX" alias="医技主项" type="int" display="0" defaultValue="0"/>
	<item id="ZXKS" alias="执行科室" type="long" display="0"/>
	<item id="YJXH" alias="医技序号" type="long" display="0"/>
	<item id="XMLX" alias="项目类型" type="int"  display="0"/>
	<item id="QRSJ" alias="确认时间" type="datetime" display="0"/>
	<item id="JFBZ" alias="计费标志" type="int" display="0" defaultValue="2"/>
	<item id="ZFBZ" alias="作废标志" type="int" display="0" defaultValue="0"/>
	<item id="SFJG" alias="审方结果" type="string" display="0" defaultValue="0"/>
	<item id="FHBZ" alias="复核标志" type="int" display="0" defaultValue="0"/>
	<item id="TZFHBZ" alias="停嘱复核标志" type="int" display="0" defaultValue="0"/>
	<item id="PSPB" alias="皮试判别" type="int" display="0" defaultValue="0"/>
	<item id="ZFPB" alias="自负判别" type="int" display="0" defaultValue="0"/>
	<item id="YZPB" alias="医嘱判别" type="int" display="0" defaultValue="0"/>
	<item id="LSBZ" alias="历史标志" type="int" display="0" defaultValue="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" display="0" defaultValue="0"/>
	<item id="FYSX" alias="发药属性" type="int" display="0" defaultValue="0"/>
	<item id="TPN" alias="TPN" type="int" display="0" defaultValue="0"/>
	<item id="LSYZ" alias="临时医嘱" type="int" display="0" defaultValue="0"/>
	<item id="SIGN" alias="库存标志" type="string" virtual="true" display="0"/>
	
	<item id="CFTS" alias="贴数" type="int" display="0"/>
	<item id="YPZS" alias="煎法" type="int" display="0">
		<dic id="phis.dictionary.suggested"/>
	</item>
	<item id="JZ" alias="脚注" type="int" display="0">
		<dic id="phis.dictionary.suggested"/>
	</item>
	<item id="YQSY" alias="抗菌药物越权使用" type="int" display="0" defaultValue="0"/>
</entry>
