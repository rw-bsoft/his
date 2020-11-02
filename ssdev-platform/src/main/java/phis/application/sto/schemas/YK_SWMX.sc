<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_SWMX" alias="实物明细">
	<item id="YPMC" alias="药品名称" length="80" type="string" width="280"/>
	<item id="YPGG" alias="规格" length="10" type="string"/>
	<item id="YPDW" alias="单位" length="2" type="string"/>
	<item id="CDMC" alias="生产厂家" length="7" type="string" width="180"/>
	<item id="KCSL" alias="库存数量" length="10" type="double" precision="2"/>
	<item id="LSJG" alias="零售价格" length="10" type="double" precision="4"/>
	<item id="JHJG" alias="进货价格" length="10" type="double" precision="4"/>
	<item id="YPPH" alias="药品批号" length="20" type="string"/>
	<item id="YPXQ" alias="药品效期" type="datetime"/>
	<item id="TYPE" alias="库存性质" length="6" type="int">
		<dic>
			<item key="1" text="合格"/>
			<item key="2" text="次品"/>
			<item key="3" text="伪劣"/>
			<item key="4" text="破损"/>
			<item key="5" text="霉变"/>
		</dic>
	</item>
	<item id="PYDM" alias="拼音码" length="10" type="string" display="0" queryable="true" selected="true"/>
	<item id="WBDM" alias="五笔码" length="10" type="string" display="0" queryable="true"/>
	<item id="JXDM" alias="角形码" length="10" type="string" display="0"/>
	<item id="QTDM" alias="其它码" length="10" type="string" display="0"/>
	<item id="YPXH" alias="药品内码" length="18" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="80" type="long" display="0"/>
	<item id="YPDM" alias="药品编码" length="10" type="string" display="0"/>
	<item id="KWBM" alias="库位编码" length="16" type="string"/>
	<item id="YKZF" alias="药库作废" length="1" type="int" display="0"/>
	<item id="ZBLB" alias="" display="0" type="string"/>
</entry>
