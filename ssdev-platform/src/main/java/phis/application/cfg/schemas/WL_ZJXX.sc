<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZJXX" alias="证件信息(WL_ZJXX)" >
	<item id="ZJXH" alias="证件序号" type="long" length="18" not-null="1" generator="assigned" pkey="true"  display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="DXXH" alias="对象序号" type="long" length="12" not-null="1"  display="0" />
	<item id="ZJLX" alias="证件类型" type="int" length="6" not-null="1" defaultValue="3" width="100"  display="1">
		<dic autoLoad="true">
			<item key="3" text="产品注册证"/>
			<item key="4" text="生产许可证"/>
		</dic>
	</item>
	<item id="ZJBH" alias="证件编号" type="string" length="50" not-null="1" display="1"/>
	<item id="FZRQ" alias="发证日期" type="date" display="1"/>
	<item id="SXRQ" alias="失效日期" type="date" display="1"/>
	<item id="ZJDX" alias="证件对象" type="int" length="1" display="0" defaultValue="2"/>
	<item id="ZJZT" alias="证件状态" type="int" length="1" defaultValue="1" display="1">
		<dic autoLoad="true">
			<item key="1" text="有效"/>
			<item key="-1" text="失效"/>
		</dic>
	</item>
	<item id="YJBZ" alias="预警标志" type="int" length="1" display="0" defaultValue="0">
		<dic autoLoad="true">
			<item key="0" text="预警"/>
			<item key="1" text="不再预警"/>
		</dic>
	</item>
	<item id="TPXX" alias="图片信息" type="string" length="100"  xtype="imagefield"  renderer="onRenderer" fixed="false"/>
</entry>
