<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_YPYF" alias="药品用法">
	<item id="YPYF" alias="药品用法" display="0" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="XMMC" alias="名称" type="string" length="20" width="250" not-null="1"/>
	<item id="PYDM" alias="拼音代码" type="string" length="6" width="150" queryable="true" target="XMMC" codeType="py"/>
	<item id="YZPX" alias="医嘱排序" display="0" length="2" type="int"/>
	<item id="XMLB" alias="归并类别" type="int" editable="true">
		<dic id="phis.dictionary.XMLB"/>
	</item>
	<item id="KPDY" alias="卡片打印" length="1" type="int">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="FYXH" alias="附加项目" length="18"  width="250" type="long" display="1" >
		<dic id="phis.dictionary.additionItem" ></dic>
	</item>
	<item id="FYMC" mode="remote" alias="附加项目" virtual="true" display="2" />
</entry>
