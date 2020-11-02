<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SQ_YYZD" alias="医院字典" sort="a.YYBH">
	<item id="YYBH" alias="医院编号" type="string" length="18" generator="assigned" display="0"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="YYMC" alias="医院名称" type="string" length="40" width="300"/>
	<item id="PY" alias="拼音" type="string" length="20" width="100"/>
</entry>
