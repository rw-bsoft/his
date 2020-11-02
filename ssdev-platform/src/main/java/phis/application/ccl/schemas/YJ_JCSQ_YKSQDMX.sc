<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_JCSQ_YKSQD_CIC" tableName="YJ_JCSQ_KD02" alias="检查申请-开单02-门诊" sort="a.SQDH"> 
	<item id="SBXH" alias="识别序号"  length="12" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" defaultFill="0" type="increase" length="12" startPos="1"/>
	    </key>
	</item>
	<item id="SQDH" alias="申请单号" type="long" length="12" not-null="1"  width="80" display="0"/>
	<item id="YLLB" alias="医疗类别" type="int" length="2" not-null="1"  width="80" display="0"/>
	<item id="LBID" alias="类别ID" type="long" length="12" not-null="1" width="80" display="0"/>
	<item id="BWID" alias="部位ID" type="long" length="12" not-null="1" width="80" display="0"/>
	<item id="XMID" alias="项目ID" type="long" length="12" not-null="1" width="80" display="0"/>
	<item ref="b.LBMC" fixed="true" width="100"/>
	<item ref="c.BWMC" fixed="true" width="100"/>
	<item ref="d.XMMC" fixed="true" width="250"/>
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
	</relations>
</entry>