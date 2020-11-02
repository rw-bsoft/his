<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLZD" alias="特殊符号">
	<item id="XMBH" alias="编号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="YWLB" alias="业务类别" type="int" length="1" display="0" defaultValue="0"/>
	<item id="XMLX" alias="项目类型"  length="12" display="0" defaultValue="CHARACTER"/>
	<item id="PYDM" alias="拼音代码" length="6" display="0"/>
	
	<item id="XMMC" alias="符号类别"  length="30" not-null="1" queryable="true" selected="true">
		<dic>
			<item key="数字序号" text="数字序号"/>
			<item key="标点符号" text="标点符号"/>
			<item key="单位符号" text="单位符号"/>
			<item key="数学符号" text="数学符号"/>
			<item key="特殊符号" text="特殊符号"/>
			<item key="自定义" text="自定义"/>
		</dic>
	</item>
	<item id="XMQZ" alias="符号样式" length="255" not-null="1"/>
</entry>