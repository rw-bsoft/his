<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_FKFS_MZ" tableName="GY_FKFS" alias="付款方式">
	<item id="FKFS" alias="付款方式" length="18" type="long" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="12"/>
		</key>
	</item>
	<item id="FKMC" alias="付款名称" type="string" not-null="1" length="40"/>
	<item id="SYLX" alias="使用类型" display="2" defaultValue="1" fixed="true" not-null="1" length="6">
		<dic id="phis.dictionary.typeOfUse"/>
	</item>
	<item id="FKLB" alias="付款类别" length="18" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.payCategory"/>
	</item>
	<item id="HMCD" alias="号码长度" length="6" type="int" maxValue="20"/>
	<item id="FKJD" alias="付款精度" length="2" not-null="1" defaultValue="3">
		<dic id="phis.dictionary.paymentAccuracy"/>
	</item>
	<item id="HBWC" alias="货币误差" length="2" not-null="1" defaultValue="0" fixed="true">
		<dic id="phis.dictionary.monetaryError"/>
	</item>
	<item id="KJFS" alias="快捷方式" display="0" type="string" length="6"/>
	<item id="SRFS" alias="舍入方式" length="8" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.roundingProcedure"/>
	</item>
	<item id="MRBZ" alias="默认标志" display="1" defaultValue="0" length="2" not-null="1" renderer="onRendererMRBZ"/>
	<item id="ZFBZ" alias="作废标志" display="1" defaultValue="0" length="2" not-null="1" renderer="onRendererZFBZ"/>
	<item id="BZXX" alias="备注信息" type="string" length="255"/>
</entry>
