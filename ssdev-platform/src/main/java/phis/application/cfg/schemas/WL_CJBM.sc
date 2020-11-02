<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CJBM" alias="厂家别名(WL_CJBM)">
	<item id="BMXH" alias="别名序号" type="long" length="18" generator="assigned" hidden="true"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="CJXH" alias="厂家序号" type="long" length="18" hidden="true"/>
	<item id="CJBM" alias="厂家别名" type="string" length="60" not-null="1"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" target="CJBM" codeType="py"/>
	<item id="WBDM" alias="五笔代码" type="string" length="10" target="CJBM" codeType="wb"/>
	<item id="JXDM" alias="角形代码" type="string" length="10" target="CJBM" codeType="jx"/>
	<item id="QTDM" alias="其它代码" type="string" length="10"/>
</entry>
