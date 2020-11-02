<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_SFMX" alias="检查申请-收费明细" sort="a.JGID,a.LBID,a.BWID,a.XMID">
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="LBID" alias="检查类别ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="BWID" alias="检查部位ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="XMID" alias="检查项目ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item ref="b.LBMC" fixed="true" width="60" />
	<item ref="c.BWMC" fixed="true" width="100" />
	<item ref="d.XMMC" fixed="true" width="150"/>
	<item id="ZTBH" alias="组套编号" type="long" length="12" not-null="1" display="0"/>
	<item ref="e.ZTMC" fixed="true" width="150"/>
	<relations>
		<relation type="parent" entryName="phis.application.ccl.schemas.YJ_JCSQ_JCLB" >
			<join parent="LBID" child="LBID" />
		</relation>
		<relation type="parent" entryName="phis.application.ccl.schemas.YJ_JCSQ_JCBW" >
			<join parent="BWID" child="BWID" />
		</relation>
		<relation type="parent" entryName="phis.application.ccl.schemas.YJ_JCSQ_JCXM" >
			<join parent="XMID" child="XMID" />
		</relation>
		<relation type="parent" entryName="phis.application.ccl.schemas.YJ_JCSQ_ZT01" >
			<join parent="ZTBH" child="ZTBH" />
		</relation>
	</relations>
</entry>