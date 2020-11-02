<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_BRXZ"   alias="用药限制">
	<item id="BRXZ" alias="性质代码" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="1"/>
		</key>
	</item>
	<item id="XZMC" alias="性质名称" width="160" type="string" not-null="1" length="30" fixed="true"/>
	 <item id="ZFBL" alias="自负比例%"   width="120" not-null="1" length="3" min="0" max="100" precision="1"  />
</entry>
