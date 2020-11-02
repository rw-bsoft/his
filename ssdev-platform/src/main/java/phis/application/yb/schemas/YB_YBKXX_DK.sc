<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病人医保卡信息" entityName="YB_YBKXX" >
	<!--其他的键根据各地医保卡返回值添加-->
	<item id="SBXH" alias="识别序号" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="BRID" alias="MS_BRDA主键关联" type="long" length="18" />
</entry>
