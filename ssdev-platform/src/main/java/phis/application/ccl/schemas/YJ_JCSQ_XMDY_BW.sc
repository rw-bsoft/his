<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_JCSQ_XMDY_BW" tableName="YJ_JCSQ_XMDY" alias="检查申请-项目对应-部位">
	<item id="LBID" alias="检查类别ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="BWID" alias="检查部位ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="XMID" alias="检查项目ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="BWMC" alias="检查部位名称"  length="20" type="string" not-null="1" width="150"/>
	<item id="PYDM" alias="拼音代码"  length="20" type="string" not-null="1" display="0"/>
	<!--<item ref="b.BWMC" fixed="true" />
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<item ref="b.PYDM" fixed="true" />
	
	<relations>
		<relation type="parent" entryName="phis.application.ccl.schemas.YJ_JCSQ_JCBW" >
			<join parent="BWID" child="BWID" />
		</relation>
	</relations>-->
</entry>