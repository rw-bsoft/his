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
	<item id="SYPC" alias="用法"  not-null="1"  type="string" length="6" width="60" fixed="true">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="FYCS" alias="次数" type="double" length="8"  precision="2" fixed="true"/>
	<item id="YPDJ" alias="项目单价" type="double" length="8" precision="2" fixed="true"  not-null="1"/>
	<item id="JE" alias="总金额" width="80" type="double"  precision="2" fixed="true" virtual="true" length="80"/>
	<item id="MRCS" alias="日次" type="int" display="0" length="2" not-null="1"/>
	<item id="KSSJ" alias="开嘱时间" width="150" fixed="true"/>
	<item id="TZSJ" alias="停嘱时间" width="150" fixed="true"/>
	
	<item id="ZXKS" alias="执行科室" display="0" length="18" />
	<item id="YSGH" alias="医生工号" display="0" length="18" />
	<item id="CZGH" alias="医生工号" display="0" length="18" />
	<item id="BRKS" alias="费用科室" display="0" length="18" />
	<item id="BRXZ" alias="病人性质" display="0" length="18" />
	<item id="YPLX" alias="药品类型" display="0" length="18" />
	<item id="XMLX" alias="项目类型" display="0" length="18" />
	<item id="FYXH" alias="费用序号" display="0" length="18" />
	<item id="QRSJ" alias="确认时间" display="0"/>
	<item id="LSBZ" alias="历史标志" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.war.schemas.ZY_BRRY_BQ" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
