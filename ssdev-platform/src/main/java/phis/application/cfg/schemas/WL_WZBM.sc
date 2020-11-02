<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZBM" alias="物资别名">
	<item id="BMXH" alias="别名序号" type="long" length="18" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase"  startPos="1" />
		</key>
	</item>
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="WZBM" alias="物资别名" length="60"  not-null="1"/>
	<item id="PYDM" alias="拼音代码" length="10" target="WZBM" codeType="py"/>
	<item id="WBDM" alias="五笔代码" length="10" target="WZBM" codeType="wb"/>
	<item id="JXDM" alias="角形代码" length="10" target="WZBM" codeType="jx"/>
	<item id="QTDM" alias="其它代码" length="10"/>
</entry>
