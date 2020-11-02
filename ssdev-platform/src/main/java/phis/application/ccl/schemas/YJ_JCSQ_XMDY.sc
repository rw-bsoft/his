<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_XMDY" alias="检查申请-项目对应">
	<item id="LBID" alias="检查类别ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="BWID" alias="检查部位ID"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="XMID" alias="检查项目ID"  length="12" type="long" not-null="1" pkey="true" />
	<item id="LBMC" alias="检查类别" type="String" length="50" not-null="1"  width="80"/>
	<item id="BWMC" alias="部位类别" type="String" length="50" not-null="1"  width="80"/>
	<item id="XMMC" alias="项目类别" type="String" length="50" not-null="1"  width="200"/>
	<item id="JGID" alias="项目类别" type="String" length="20"   display="0"/>
</entry>