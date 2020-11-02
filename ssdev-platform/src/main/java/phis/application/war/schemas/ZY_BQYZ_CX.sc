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
	<item id="YZZH_SHOW" alias=" " type="long" length="1" fixed="true" width="10" renderer="showColor" virtual="true"/>
	<item id="YZZH" alias="医嘱组号" type="long" length="1" isGenerate="false" display="0" width="10" >
		<key>
			<rule name="increaseYzzh"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="KSSJ" alias="开嘱时间" type="datetime" width="120" renderer="dateFormat" />
	<item id="ZFYP" defaultValue="0" alias="转" xtype="checkBox" width="40" type="int" length="1">
	</item>
	<item id="ZBY" alias="药品是否本来就是自备药" type="int" length="1" display="0"
		width="1" virtual="true" />
	<item id="YZMC" alias="医嘱项目" type="string" mode="remote" length="100" width="200"/>
	<item id="YPJL" alias="药品剂量" type="string" display="0" virtual="true"/>
	<item id="YCJL" alias="剂量" type="double" max="9999999.999" length="10" precision="3" not-null="1" width="60"/>
	<item id="JLDW" alias=" " display="1" width="30" fixed="true" virtual="true"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" min="0.001" max="9999.999" not-null="1" width="60" defaultValue="1"/>
	<item id="SYPC" alias="频次"  not-null="1"  type="string" length="6" width="60">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="MRCS" alias="每日次数" type="int" display="0"/>
	<item id="YZZXSJ" alias="执行时间" length="80" fixed="true"/>
	<item id="SRCS" alias="首日次数" type="int" max="999999" length="6" width="80" renderer="srcsRender"/>
	<item id="YPYF" alias="途径" type="long" length="18" listWidth="90" width="60" >
		<dic id="phis.dictionary.drugWay" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" nullToValue="0" not-null="1" width="70"/>
	<item id="YSGH" alias="开嘱医生" type="string" length="10" >
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSTJ" alias="状态" type="int" defaultValue="0" width="60" renderer="YstjRender" fixed="true"/>
	<item id="YSBZ" alias=" " type="int" length="1" fixed="true" width="30"  defaultValue="0" renderer="ysbzRender" />
	<item id="TZSJ" alias="停嘱时间" type="datetime" renderer="dateFormat" width="120" />
	<item id="TZYS" alias="停嘱医生" type="string" length="10">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="FYFS" alias="发药方式" type="long" length="18">
		<dic id="phis.dictionary.hairMedicineWay" autoLoad="true"/>
	</item>
	<item id="BZXX" alias="说明" type="string" length="150" width="80"/>
	<item id="YPCD" alias="产地" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.medicinePlace" autoLoad="true"/>
	</item>
	<item id="YFSB" alias="发药药房" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.wardPharmacy" filter="['and',['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']],['eq',['$','item.properties.BQDM'],['$','%user.properties.wardId']]]" autoLoad="true"/>
	</item>
	<item id="CZGH" alias="操作工号" type="string" display="0" defaultValue="%user.userId" />
	<item id="YFBZ" alias="药房包装" type="int" display="0" />
	<item id="YPLX" alias="药品类型" type="int" display="0" defaultValue="-1"/>
	<item id="SYBZ" alias="使用标志" type="int" display="0" defaultValue="0"/>
	<item id="MZCS" alias="每周次数" type="int" display="0" defaultValue="0"/>
	<item id="YJZX" alias="医技主项" type="int" display="0" defaultValue="0"/>
	<item id="ZXKS" alias="执行科室" type="long" display="0"/>
	<item id="YJXH" alias="医技序号" type="long" display="0"/>
	<item id="XMLX" alias="项目类型" type="int"  display="0"/>
	<item id="QRSJ" alias="确认时间" type="date" display="0"/>
	<item id="JFBZ" alias="计费标志" type="int" display="0" defaultValue="2"/>
	<item id="ZFBZ" alias="作废标志" type="int" display="0" defaultValue="0"/>
	<item id="SFJG" alias="审方结果" type="int" display="0" defaultValue="0"/>
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
	<item id="SIGN" alias="库存标志" virtual="true" display="0"/>
</entry>
