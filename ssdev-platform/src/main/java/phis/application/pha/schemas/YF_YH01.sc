<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YH01" alias="养护01" sort="a.YHDH desc">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" pkey="true" type="long" display="0"/>
	<item id="YHDH" alias="养护单号" length="12" not-null="1" pkey="true" type="string"/>
	<item id="YPLB" alias="药品类别" length="16"   type="string" display="0"/>
	<item id="KWLB" alias="库位类别" length="16"  type="string" display="0"/>
	<item id="YHRQ" alias="养护日期" type="datetime" width="160"/>
	<item ref="b.PERSONNAME" alias="养护人" queryable="false"/>
	<item id="ZT" alias="状态" type="String" virtual="true" renderer="onRenderer"/>
	<item id="CZGH" alias="操作工号" length="10"  type="string" display="0"/>
	<item id="YSGH" alias="验收工号" length="10"  type="string" display="0"/>
	<item id="ZXRQ" alias="执行日期" type="datetime" display="0"/>
	<item id="BZXX" alias="备注" length="100"  type="string" />
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.SYS_Personnel" >
			<join parent="CZGH" child="PERSONID"></join>
		</relation>
	</relations>
</entry>
