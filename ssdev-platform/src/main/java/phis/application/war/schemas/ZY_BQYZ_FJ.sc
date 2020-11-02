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
	<item id="KSSJ" alias="开嘱时间" type="datetimefield" width="120" renderer="dateFormat" display="0" />
	<item id="YZMC" alias="附加医嘱项目" type="string" mode="remote" length="100" width="200"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" min="0.01" max="9999.99" not-null="1" width="60"/>
	<item id="SYPC" alias="频次"  not-null="1"  type="string" length="6" width="60" fixed="true">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="MRCS" alias="每日次数" type="int" display="0"/>
	<item id="YZZXSJ" alias="执行时间" length="80" display="0"/>
	<item id="SRCS" alias="首日次数" type="int" max="999999" length="6" width="80" renderer="srcsRender" display="0"/>
	<item id="YPYF" alias="途径" type="long" length="18" listWidth="90" width="60" fixed="true" display="0">
		<dic id="phis.dictionary.drugWay" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" min="0.0001" max="99999999.9999" nullToValue="0" not-null="1" width="70" />
	<item id="YSGH" alias="开嘱医生" type="string" length="10" display="0"/>
	<item id="TZSJ" alias="停嘱时间" type="date" renderer="dateFormat" width="120" display="0" />
	<item id="TZYS" alias="停嘱医生" type="string" length="10" display="0" />
	<item id="CZGH" alias="操作工号" type="string" display="0" defaultValue="%user.userId" />
	<item id="YFBZ" alias="药房包装" type="int" display="0" />
	<item id="ZFPB" alias="自负判别" type="int" display="0" defaultValue="0"/>
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
	<item id="YSBZ" alias="医生医嘱标志" type="int" display="0" defaultValue="0"/>
	<item id="YSTJ" alias="医生提交标志" type="int" display="0" defaultValue="0"/>
	<item id="LSYZ" alias="临时医嘱" type="int" display="0" defaultValue="0"/>
	<item id="SIGN" alias="库存标志" virtual="true" display="0"/>
</entry>
