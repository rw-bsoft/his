<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_QXBM_WH" alias="缺陷编码维护">
	<item id="ID" alias="主键ID" type="long" length="9" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="DWBM" alias="定位编码" type="long" length="20" not-null="1" width="100" display="2"/>
	
	<item id="QXBM" alias="缺陷编码" type="string" length="20" not-null="1" width="100"/>
	<item id="QXMC" alias="缺陷名称" type="string" length="100" not-null="1" width="200"/>
	<item id="DXMC" alias="定性名称" type="long" length="2" not-null="1" width="150">
		<dic id="phis.dictionary.qxbmwh_dxmc"/>
	</item>
	<item id="QXQZ" alias="缺陷权重" type="long" length="2" not-null="1" width="65"/>
	<item id="JBKF" alias="基本扣分" type="long" length="2" not-null="1" width="65"/>
	<item id="DFFS" alias="打分方式" type="long" length="2" not-null="1" width="65">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="1" text="手动"/>
			<item key="2" text="自动"/>
		</dic>
	</item>
	<item id="ZDJC" alias="重点检查" type="long" length="2" not-null="1" width="65">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="0" text="是"/>
			<item key="1" text="否"/>
		</dic>
	</item>
	<item id="ZF" alias="作废" type="long" length="20" not-null="1" width="100">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="0" text="是"/>
			<item key="1" text="否"/>
		</dic>
	</item>
</entry>