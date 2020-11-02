<?xml version="1.0" encoding="UTF-8"?>

<entry tableName="MS_YSPB" alias="门诊医生排班">
  <item id="GZRQ" alias="工作日期" hidden="true" type="date" not-null="1" generator="assigned" pkey="true"/>
  <item id="KSDM" alias="科室代码" fixed="true" hidden="true" type="long" length="18" not-null="1" pkey="true">
  	<dic id="phis.dictionary.ghdepartment"></dic>
  </item>
  <item id="YSDM" alias="医生代码" fixed="true" hidden="true" type="string" length="10" not-null="1" pkey="true"/>
  <item ref="b.PERSONNAME" alias="医生姓名" fixed="true" queryable="" type="string" />
  <item ref="b.ISEXPERT" alias="专家" display="1" width="50" type="string">
  	<dic id="phis.dictionary.confirm"/>
  </item>
  <item ref="b.EXPERTCOST" alias="专家费" fixed="true" type="double" display="1" precision="2"/>
  <item ref="b.PYCODE" alias="拼音代码" display="2" type="string" queryable="true" selected="true"/>
  <item id="ZBLB" alias="值班类别" fixed="true" hidden="true" length="4" not-null="1">
  	<dic>
  	  <item key="1" text="上午"/>
      <item key="2" text="下午"/>
  	</dic>
  </item>
  <item id="JGID" alias="机构ID" hidden="true" length="8" not-null="1"/>
  <item id="GHXE" alias="挂号限额" type="int" length="4" not-null="1"/>
  <item id="YGRS" alias="已挂人数" type="int" length="4" not-null="1"/>
  <item id="YYRS" alias="预约人数" type="int" length="4" not-null="1"/>
  <item id="JZXH" alias="就诊序号" hidden="true" length="4"/>
  <item id="YYXE" alias="预约限额" type="int" display="2" length="4"/>
  <item id="TGBZ" alias="停挂标志" hidden="true" length="1" not-null="1"/>
  <relations>
		<relation type="child" entryName="phis.application.cic.schemas.SYS_Personnel" >
			<join parent="YSDM" child="PERSONID" />
		</relation>
  </relations>
</entry>
