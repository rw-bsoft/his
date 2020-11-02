<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLXG" alias="病历修改痕迹表" sort="XGSJ desc">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1"  display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="HJNR" alias="痕迹内容" height="410" hideLabel="1" xtype="textarea" />
</entry>
