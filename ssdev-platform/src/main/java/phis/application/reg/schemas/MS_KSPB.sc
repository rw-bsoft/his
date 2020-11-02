<?xml version="1.0" encoding="UTF-8"?>

<entry tableName="MS_KSPB" alias="科室排班">
	<item id="GHRQ" alias="挂号日期" length="1" display="0" type="int" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZBLB" alias="值班类别" length="1" display="0" type="int" not-null="1" pkey="true"/>
	<item id="GHKS" alias="挂号科室" length="18" display="0" type="long" not-null="1" pkey="true"/>
	<item id="MZMC" alias="门诊名称" type="string" width="150"  fixed="true"/>
	<item id="KSMC" alias="科室名称" type="string" width="150" fixed="true"/>
	<item id="KSPB" type="string" alias="排班">
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
	</item>
	<item id="JGID" alias="机构" type="string" length="20" display="0"/>
	<item id="JZXH" alias="就诊序号" type="int" display="0" length="4"/>
	<item id="GHXE" alias="挂号限额" type="int" min="0" max="9999" length="4"/>
	<item id="YGRS" alias="已挂人数" type="int" display="0" length="4"/>
	<item id="YYRS" alias="预约人数" type="int" display="0" length="4"/>
	<item id="YYXE" alias="预约限额" type="int" min="0" max="9999" length="4"/>
	<item id="YYJG" alias="预约间隔(分)" type="string" length="5" width="90" >
		<dic id="phis.dictionary.YYJG" autoLoad="true"/>
	</item>
	<item id="YYKSSJ" alias="预约开始时间" type="string" length="5" width="90" >
		<dic id="phis.dictionary.YYKSSJ" autoLoad="true"/>
	</item>
	<item id="YYJSSJ" alias="预约结束时间" type="string" length="5" width="90" >
		<dic id="phis.dictionary.YYJSSJ" autoLoad="true"/>
	</item>
	<item id="TGBZ" alias="停挂标志" type="int" display="0" length="1"/>
</entry>
