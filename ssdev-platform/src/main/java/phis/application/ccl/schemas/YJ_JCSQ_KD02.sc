<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_KD02" alias="检查申请-开单02"> 
	<item id="SBXH" alias="识别序号"  length="12" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" defaultFill="0" type="increase" length="12" startPos="1"/>
	    </key>
	</item>
	<item id="SQDH" alias="申请单号" type="long" length="12" not-null="1"  width="80"/>
	<item id="YLLB" alias="医疗类别" type="int" length="2" not-null="1"  width="80"/>
	<item id="LBID" alias="类别ID" type="long" length="12" not-null="1" width="80"/>
	<item id="BWID" alias="部位ID" type="long" length="12" not-null="1" width="80"/>
	<item id="XMID" alias="项目ID" type="long" length="12" not-null="1" width="80"/>
</entry>