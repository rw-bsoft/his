<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_FKXX" alias="付款信息">
	<item id="JLXH" alias="记录序号" display="0" length="18" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" length="20" type="string"/>
	<item id="MZXH" alias="门诊序号" length="18" type="long"/>
	<item id="FKFS" alias="付款方式" length="18" type="long"/>
	<item id="FKJE" alias="付款金额" length="12" type="double"/>
	<item id="FKHM" alias="付款号码" length="40" type="string"/>
</entry>
