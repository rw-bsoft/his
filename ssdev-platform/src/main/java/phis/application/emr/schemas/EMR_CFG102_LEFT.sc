<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_DWBM_WH" alias="CFG102_LEFT_LIST" sort="SJDWBM,DWBM">
	<item id="ID" alias="主键ID" type="long" length="9" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="DWBM" alias="定位编码" type="long" length="20" not-null="1" width="100"/>
	<item id="BMMC" alias="定位编码名称" length="40" not-null="1" width="200"/>
</entry>