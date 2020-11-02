<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_KBM_BLLB_FORM" tableName="EMR_KBM_BLLB" alias="病历类别表" sort="LBBH asc">
	<item id="LBBH" alias="类别编号" type="int" length="9" not-null="1" generator="assigned" pkey="true" display="0"/>
  
	<item id="YDLB" alias="约定类别" length="30" width="150" virtual="true"/>
	<item id="LBMC" alias="类别名称" length="30" not-null="1" width="150"/>
	<item id="MLBZ" alias="目录标志" type="int" length="1" not-null="1" width="60" defaultValue="1">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="0" text="否"/>
		</dic>
	</item>
	<item id="ZYPLXH" alias="在院排列序号" length="4" not-null="1"/>
	<item id="CYPLXH" alias="出院排列序号" length="4" not-null="1"/>
	<item id="BLLX" alias="病历类型" type="int" length="1" not-null="1">
		<dic>
			<item key="0" text="病历"/>
			<item key="1" text="病程"/>
		</dic>
	</item>
	<item id="HYBZ" alias="换页方式" type="int" length="1">
		<dic>
			<item key="1" text="不换页"/>
			<item key="2" text="段前换页"/>
			<item key="3" text="段后换页"/>
			<item key="4" text="前后换页"/>
		</dic>
	</item>
	<item id="DYWD" alias="单一文档" type="int" length="1" not-null="1">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
			<item key="2" text="组内单一"/>
		</dic>
	</item>
	<item id="WGXBZ" alias="打印网格线" type="int" length="1" width="100">
		<dic>
			<item key="0" text="不打印"/>
			<item key="1" text="按文字打印"/>
			<item key="2" text="全页打印"/>
		</dic>
	</item>
	<item id="XSMC" alias="显示名称" type="string" xtype="textarea" height="60" length="255" colspan="2"/>
	<item id="SM" alias="说明" type="string" xtype="label" height="60" length="255" colspan="2" virtual="true" />
  
	<item id="YDLBBM" alias="约定类别编码" length="8" not-null="1" display="0"/>
	<item id="SJLBBH" alias="上级类别编号" length="10" not-null="1" display="0"/>
	<item id="LBBM" alias="类别编码" length="8" not-null="1" display="0"/>
	<item id="BLFZ" alias="病历分组" type="int" length="1" not-null="1" display="0"/>
	<item id="ZYPLSX" alias="在院排列顺序" type="int" length="1" not-null="1" display="0"/>
	<item id="CYPLSX" alias="出院排列顺序" type="int" length="1" not-null="1" display="0"/>
	<!--
		  <item id="PRINTSETUP" alias="打印设置"/>
		  -->
	<item id="DYTS" alias="打印提示" length="255" display="0"/>
	<item id="MRDYJ" alias="默认打印机" length="20" display="0"/>
</entry>
