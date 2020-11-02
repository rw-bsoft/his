<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JG_WJZCZ" alias="危机值操作记录">
	<item id="JLXH" alias="记录序号" length="18" not-null="1" display="0" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="0" />
		</key>
	</item>
	<item id="WJZXH" alias="危急值序号" length="18"  type="long"  display="0"/>
	<item id="CZSJ" alias="操作时间"   type="datetime"  display="0"/>
	<item id="CZKS" alias="操作科室" length="10" type="string"  display="0"/>
	<item id="CZRY" alias="操作人员" length="10" type="string"  display="0"/>
	<item id="CZRYXM" alias="操作人员姓名" length="20" type="string"  display="0"/>
	<item id="CZNR" alias="请输入危机值处理的简短描述" length="250" type="string" xtype="textarea"/>
	<item id="TIMESTAMP" alias="时间戳"   type="datetime" display="0"/>
</entry>
