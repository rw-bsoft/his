<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SQ_MBYP" alias="慢病药品">
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="DL" alias="大类" type="long" length="2" not-null="1" queryable="true">
		<dic>
			<item key="1" text="高血压"/>
			<item key="2" text="糖尿病"/>
			<item key="22" text="结核病"/>
		</dic>
	</item>
	<item id="LBXH" alias="类别" type="long" length="2" not-null="1" width="150">
		<dic id="chis.dictionary.medicineType" render="Tree" onlySelectLeaf="true"/>
	</item>
	
	<item id="YPMC" alias="药品名称" type="string" length="20" not-null="1" queryable="true" width="200"/>
	<item id="PYM" alias="拼音码" type="string" length="20" queryable="true"/>
	<item id="QTMC1" alias="其他名称1" type="string" length="20"/>
	<item id="PYM1" alias="拼音码1" type="string" length="20"/>
	<item id="QTMC2" alias="其他名称2" type="string" length="20"/>
	<item id="PYM2" alias="拼音码2" type="string" length="20"/>
	<item id="QTMC3" alias="其他名称3" type="string" length="20"/>
	<item id="PYM3" alias="拼音码3" type="string" length="20"/>
	<item id="QTMC4" alias="其他名称4" type="string" length="20"/>
	<item id="PYM4" alias="拼音码4" type="string" length="20"/>
	<item id="QTMC5" alias="其他名称5" type="string" length="20"/>
	<item id="PYM5" alias="拼音码5" type="string" length="20"/>
	
	<item id="LBMC" alias="类别名称" type="string" length="100" hidden="true"/>
	<item id="YPBM" alias="药品编码" type="string" length="50" hidden="true"/>
	<item id="ZJFLAG" alias="注标标识" type="int" length="1" defaultValue="1" hidden="true"/>
	<!-- 试挤序号=大类key+类别key -->
	<item id="SJXH" alias="试挤序号" type="long" length="18" hidden="true"/>
	
</entry>