<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人管理-农合上传展示">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
	<item id="BRXM" alias="病人姓名" length="20" not-null="1"/>
	<item id="BRXB" alias="病人性别" length="4">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="NHDJID" alias="农合登记ID" type="long" length="10" display="2"/>
</entry>
