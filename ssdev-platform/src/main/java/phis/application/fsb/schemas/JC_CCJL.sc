<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_CCJL" alias="家床查床记录">
	<item id="SBXH" alias="查床序号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="ZYH" alias="家床号" type="long" length="18" not-null="1"
		display="0" />
	<item id="CCSJ" alias="查床时间" type="datetime" length="20" not-null="1" update="false" />

	<item id="ZSXX" alias="主诉" length="1000" xtype="textarea" />
	<item id="TGJC" alias="体格检查" length="1000" xtype="textarea" />
	<item id="FZJC" alias="辅助检查" length="1000" xtype="textarea" />
	<item id="GTQK" alias="联系人沟通情况" length="1000" xtype="textarea" />
</entry>
