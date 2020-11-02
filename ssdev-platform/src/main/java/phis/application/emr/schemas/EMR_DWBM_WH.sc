<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_DWBM_WH" alias="定位编码维护" sort="SJDWBM,DWBM">
	<item id="ID" alias="主键ID" type="long" length="9" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="BMJB" alias="编码级别" type="long" length="8" not-null="1" width="100" display="2" defaultValue="1" renderer="bmjbRenderer">
		<dic autoLoad = "true" >
			<item key="1" text="一级编码" />
			<item key="2" text="二级编码" />
		</dic>
	</item>
	<item id="SJDWBM" alias="上级定位编码" type="long" length="20" width="100" display="2" hidden="true"/>
	<item id="DWBM" alias="定位编码" type="long" length="20" not-null="1" width="100"/>
	<item id="BMMC" alias="定位编码名称" length="40" not-null="1" width="300"/>
	<item id="ZGKF" alias="最高扣分" type="int" length="3" not-null="1" width="100"/>
	<item id="ZF" alias="作废" type="long" length="20" not-null="1" width="100">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="0" text="是"/>
			<item key="1" text="否"/>
		</dic>
	</item>
</entry>