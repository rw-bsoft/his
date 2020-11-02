<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YSPB" alias="门诊医生排班" sort="ysdm">
	<item id="GZRQ" alias="工作日期" hidden="true" type="date" not-null="1" pkey="true"/>
	<item id="KSDM" alias="挂号科室" fixed="true" type="long" length="18" not-null="1" pkey="true">
		<dic id="phis.dictionary.ghdepartment" autoLoad="true"></dic>
	</item>
	<item id="YSDM" alias="医生姓名" fixed="true" type="string" length="10" not-null="1" pkey="true">
		<dic id="phis.dictionary.doctor" autoLoad="true"></dic>
	</item>
	<item id="ZBLB" alias="值班类别" type="int" fixed="true" length="4" not-null="1" pkey="true">
		<dic autoLoad="true">
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
	<item id="JGID" alias="机构ID" hidden="true" type="string" length="8" not-null="1"/>
	<item id="GHXE" alias="挂号限额" type="int" min="0" max="9999" length="4" not-null="1"/>
	<item id="YGRS" alias="已挂人数" type="int" hidden="true" length="4" not-null="1"/>
	<item id="YYRS" alias="预约人数" type="int" hidden="true" length="4" not-null="1"/>
	<item id="JZXH" alias="就诊序号" type="int" hidden="true" length="4"/>
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
	<item id="TGBZ" alias="停挂标志" type="int" hidden="true" length="1" not-null="1"/>
	<item id="YGXB" alias="员工性别" type="string" length="4" virtual="true" display="0"/>
</entry>