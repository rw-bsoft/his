<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_JH01" alias="计划01">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="XTSB" alias="药库识别" length="18"   type="long" display="0" />
	<item id="JHDH" alias="计划单号" length="6"   type="int" />
	<item id="BZRQ" alias="计划日期" type="date" defaultValue="%server.date.date"/>
	<item id="ZT" alias="状态" type="string" />
	<item id="BZGH" alias="计划人员" type="string" length="10"  defaultValue="%user.userId">
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="JHBZ" alias="计划备注" type="string" length="40" />
	<item id="DWXH" alias="进货单位" length="18" type="long" display="0" />
	<item id="DWMC" alias="单位名称"  type="string" display="0" />
</entry>
