<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_YZDY" alias="医嘱本打印">
	<item id="DYXH" alias="打印序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="YZBXH" alias="医嘱本序号" type="long" length="18"/>
	<item id="ZYH" alias="住院号" type="long" length="18"  not-null="1" />
	<item id="YZQX" alias="医嘱期效" type="int" length="1"  not-null="1" />
	<item id="DYRQ" alias="打印日期" type="date"/>
	<item id="DYNR" alias="打印内容" type="sting"  width="255"/>
	<item id="DYYM" alias="打印页码" type="int" length="2"  not-null="1" />
	<item id="DYHH" alias="打印行号" type="int" length="4"  not-null="1" />
	<item id="CZBZ" alias="重整标志" type="int" length="1"  not-null="1" />
	<item id="JGID" alias="机构ID" type="sting" length="20"/>
</entry>										