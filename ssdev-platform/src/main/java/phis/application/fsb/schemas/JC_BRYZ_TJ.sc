<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRYZ" alias="病区医嘱" sort="YZZH asc,YJZX desc,JLXH asc">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<!--<item ref="b.ZYHM" queryable="false"/>
	<item ref="b.BRXM" queryable="false"/>-->
	<item id="YZMC" alias="医嘱名称" type="string" length="100" width="200"/>
	<item id="YPYF" alias="途径" not-null="true" type="long" length="18" listWidth="90" width="60" >
		<dic id="phis.dictionary.drugWay" searchField="PYDM" fields="key,text,PYDM,FYXH" autoLoad="true"/>
	</item>
	<item id="YCJL" alias="剂量" type="double" max="9999999.999" length="10" precision="3" not-null="true" width="60"/>
	<item ref="d.JLDW" alias="单位"/>
	<item id="SYPC" alias="频次" not-null="true" type="string" length="6"
		width="60">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ"
			autoLoad="true" />
	</item>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" min="0.01" max="9999.99" not-null="true" width="60" defaultValue="1" />
	<item ref="c.YFDW"/>
	<item id="YPJE" alias="金额" type="double" length="4" width="60" precision="2" virtual="true" renderer="onRenderJe" />
	<item id="YSGH" alias="医生" type="string" not-null="true" length="10" width="60" >
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="HSGH" alias="护士" type="string" not-null="true" length="10" width="60" >
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="KSSJ" alias="开嘱时间" type="datetime" width="140" renderer="dateFormat" />
	<item id="TZSJ" alias="停嘱时间" type="datetime" renderer="dateFormat" width="140" />
	<item id="BZXX" alias="备注" type="string" length="250" width="120"/>
	<item id="YPDJ" alias="单价" type="double" length="4" width="60" display="0"/>
	<item id="MRCS" alias="每日次数" type="int" display="0" />
	<item id="YYTS" alias="天数" type="int" length="4" width="60" display="0"/>
	<item id="YPLX" alias="药品类型" type="int" display="0" defaultValue="-1" />
	<item id="YSTJ" type="int" length="1" display="0"/>
	<item id="YSBZ" type="int" length="1" display="0"/>
	<item id="SRKS" type="long" length="18" display="0"/>
	<item id="BRKS" type="long" length="18" display="0"/>
	<item id="ZXKS" type="long" length="18" display="0"/>
	<item id="YEPB" type="int" length="1" display="0"/>
	<item id="XMLX" type="int" length="2" display="0"/>
	<item id="FYFS" type="long" length="18" display="0"/>
	<item id="LSYZ" type="int" length="1" display="0"/>
	<item id="ZYH" type="long" length="18" display="0"/>
	<item id="YFBZ" type="int" length="4" display="0"/>
	<item id="YFGG" type="String" length="20" display="0"/>
	<item id="YPXH" type="long" length="18" display="0"/>
	<item id="YPCD" type="long" length="18" display="0"/>
	<item id="YFSB" type="long" length="18" display="0"/>
	<item ref="d.KSBZ" type="long" length="18" display="0"/>
	<item ref="b.BRXZ" queryable="false"  display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.fsb.schemas.JC_BRRY" >
			<join parent="ZYH" child="ZYH" />
		</relation>
		<relation type="parent" entryName="phis.application.pha.schemas.YF_YPXX" >
			<join parent="YPXH" child="YPXH" />
			<join parent="YFSB" child="YFSB" />
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH" />
		</relation>
	</relations>
</entry>
