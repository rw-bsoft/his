<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYZH" alias="中医证侯">
	<item id="ZHBS" alias="证侯标识" type="long" length="9" display="0"  not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="ZHFL" alias="证侯分类" type="long" length="9" not-null="1">
        <dic id="phis.dictionary.ZYZHFL" autoLoad="true">
        </dic>
    </item>
	<item id="ZHDM" alias="证侯代码" type="string" length="20" not-null="1"/>
	<item id="ZHMC" alias="证侯名称" type="string" length="60" not-null="1" width="130"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" display="0" target="ZHMC" codeType="py"/>
	<item id="WBDM" alias="五笔码" type="string" display="0" length="10" target="ZHMC" codeType="wb"/>
	<item id="QTDM" alias="其它码" type="string" length="10" display="0"/>
	<item id="ZXBZ" alias="注销标志" type="int" length="1" not-null="1" display="0" defaultValue="0"/>
	<item id="BZXX" alias="备注信息" type="string" display="0" length="255"/>
</entry>
