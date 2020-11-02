<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YK_FK01" alias="药库付款01">
	<item id="FKJL" alias="付款记录" length="18" not-null="1" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="YKSB" alias="药库识别" type="long" length="18" />
	<item id="PZHM" alias="凭证号码" type="string" length="10" />
	<item id="FKGH" alias="付款工号" type="string" length="10" />
	<item id="FKSJ" alias="付款时间" type="datetime"/>
	<item id="FKJE" alias="付款金额" type="double" length="12" precision="4"/>
</entry>