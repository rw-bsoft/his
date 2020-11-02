<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_WTDM" alias="抽样01" sort="a.WTLX,a.WTDM">
	<item id="WTXH" alias="抽样序号" length="18" type="long" not-null="1" generator="assigned" pkey="true" display="0" >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23"
		length="20" virtual="true" display="1" />
	<item id="WTLX" alias="问题类型" type="long" length="18" not-null="1" >
		<dic id="phis.dictionary.correspondingWay_wtlx" defaultIndex="1"/>
	</item>
	<item id="WTDM" alias="问题代码" type="string" length="10" not-null="1"/>
	<item id="WTMC" alias="问题名称" type="string" length="255" not-null="1"/>
	<item id="PYDM" alias="拼音代码" type="string" length="20" target="WTMC" codeType="py" />
	<item id="WBDM" alias="五笔代码" type="string" length="20" target="WTMC" codeType="wb"/>
	<item id="QTDM" alias="其他代码" type="string" length="20"/>
	<item id="DPLX" alias="点评类型" length="1"   type="int" not-null="1" display="2">
		<dic id="phis.dictionary.correspondingWay_dplx" defaultIndex="1"/>
	</item>
	<item id="ZFPB" alias="作废标志" length="1"  not-null="1" type="int" display="0" defaultValue="0"/>
</entry>
