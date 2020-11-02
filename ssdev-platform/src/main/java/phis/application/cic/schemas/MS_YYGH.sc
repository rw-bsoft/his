<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YYGH" alias="复诊预约">
	<item id="YYXH" alias="记录编号" display="0" type="long" length="18" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构编码" display="0" type="string" length="20" />
	<item id="KSDM" alias="科室代码" display="0" type="long" length="18" />
	<item id="YSDM" alias="医生代码" display="0" type="string" length="10" />
	<item id="BRID" alias="病人ID" display="0" type="long" length="20" defaultValue="1"/>
	<item id="YYRQ" alias="预约日期"  type="date" minValue="%server.date.date" defaultValue="%server.date.date"/>
	<item id="ZBLB" alias="值班类别" type="int" defaultValue="1" length="1">
		<dic id="phis.dictionary.dutyCategory" autoLoad="true"/>
	</item>
	<item id="GHFZ" alias="挂号复诊" xtype="checkbox" length="1" virtual="true" checkValue="1,0" type="string" />
	<item id="GHRQ" alias="操作日期" xtype="datetimefield" display="0" type="date" defaultValue="%server.date.datetime"/>
	<item id="ZCID" alias="注册ID" display="0" type="long" length="18" />
</entry>
