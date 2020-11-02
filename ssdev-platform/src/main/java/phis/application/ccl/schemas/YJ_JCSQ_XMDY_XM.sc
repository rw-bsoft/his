<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_XMDY" alias="检查申请-项目对应-项目" sort="b.PYDM">
	<item id="LBID" alias="检查类别ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="BWID" alias="检查部位ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="XMID" alias="检查项目ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item ref="b.XMMC" fixed="true" width="200"/>
	<!--<item ref="b.PYDM" fixed="true" display="0"/>-->
	<item ref="b.BZ" fixed="true" />
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<relations>
		<relation type="parent" entryName="phis.application.ccl.schemas.YJ_JCSQ_JCXM" >
			<join parent="XMID" child="XMID" />
		</relation>
	</relations>
</entry>