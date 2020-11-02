<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_FYBM" alias="费用别名">
	<item id="BMXH" alias="费用序号" type="int" length="18" generator="assigned" hidden="true"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="FYXH" alias="费用序号" type="long" length="18" display="0" not-null="1"/>
	<item id="FYMC" alias="费用名称" type="string" length="80" width="240" not-null="1"/>
	<item id="PYDM" alias="拼音码" type="string" length="80" target="FYMC" codeType="py"/>
	<item id="WBDM" alias="五笔码" type="string" length="80" target="FYMC" codeType="wb"/>
	<item id="JXDM" alias="角形码" type="string" length="80" target="FYMC" codeType="jx"/>
	<item id="BHDM" alias="笔画码" type="string" length="80" target="FYMC" codeType="bh"/>
	<item id="QTDM" alias="其他码" type="string" length="80"/>
	<item id="BMFL" alias="编码分类" length="2" display="0" defaultValue="1" not-null="1"/>
</entry>
