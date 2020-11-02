<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZMX" alias="门诊科室核算明细表">
	<item id="MXXH" alias="明细序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="15" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" not-null="1"/>
	<item id="HZXH" alias="汇总序号" length="18" not-null="1" type="long"/>
	<item id="SFXM" alias="收费项目" length="18" not-null="1" type="long" />
	<item id="SFJE" alias="收费金额" length="12" precision="2" not-null="1" type="double"/>
	<item id="ZFJE" alias="自负金额" length="12" precision="2" not-null="1" type="double"/>
</entry>
