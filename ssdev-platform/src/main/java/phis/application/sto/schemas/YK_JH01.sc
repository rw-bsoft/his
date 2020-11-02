<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_JH01" alias="计划01" sort="a.SBXH desc">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="XTSB" alias="药库识别" length="18"   type="long" display="0" />
	<item id="JHDH" alias="计划单号" length="6"   type="int" display="1"/>
	<item ref="b.DWMC" mode="remote"  alias="进货单位" not-null="1" showRed="true" nextFocus="JHBZ" width="100"/>
	<item id="BZRQ" alias="编制日期" type="date" defaultValue="%server.date.date"/>
	<item id="BZGH" alias="编制人" type="string" length="10"  display="1" defaultValue="%user.userId">
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="SP" alias="审批" type="int" precision="1" virtual="true" display="1" renderer="onRenderer_sure"/>
	<item id="SPGH" alias="审批人" type="string" length="10" display="1">
	<dic  id="phis.dictionary.user" sliceType="1"/>	
	</item>
	<item id="SPRQ" alias="审批日期" type="date" display="0"/>
	<item id="ZX" alias="执行" type="int" precision="1" virtual="true" display="1" renderer="onRenderer_sure"/>
	<item id="ZXRQ" alias="执行日期" type="date" display="1"/>
	<item id="ZXGH" alias="执行工号" type="string" length="10" display="0"/>
	<item id="DWXH" alias="进货单位" length="18" type="long" display="0" not-null="1"/>
	<item id="CKJE" alias="参考金额" type="double" precision="2" virtual="true" display="1"/>
	<item id="JHBZ" alias="备注" type="string" length="40" display="2" nextFocus="fire_createRow"/>
	<item id="SCBZ" alias="上传标志" length="1"   type="int" display="1" defaultValue="1">
		<dic>
			<item key="1" text="未上传"/>
			<item key="2" text="已上传"/>
		</dic>
	</item>
	<item id="SCRQ" alias="上传日期" type="date" display="1"/>
	<relations>
		<relation type="child" entryName="phis.application.mds.schemas.YK_JHDW" >
			<join parent="DWXH" child="DWXH"></join>
		</relation>
	</relations>
</entry>
