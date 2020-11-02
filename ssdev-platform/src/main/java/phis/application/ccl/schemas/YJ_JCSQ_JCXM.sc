<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_JCXM" alias="检查申请-检查项目" sort="a.PYDM">
	<item id="XMID" alias="检查项目ID"  length="12" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" defaultFill="0" type="increase" length="12" startPos="1"/>
	    </key>
	</item>
	<item id="XMMC" alias="检查项目" type="String" length="50" not-null="1" width="200"/>
	<!--<item id="PYDM" alias="拼音代码" type="string" length="20"  not-null="1"/>-->
	<item id="PYDM" alias="拼音代码" type="string" length="20" maxValue="10" display="2" queryable="true" selected="true" target="XMMC" codeType="py" not-null="1"/>
	<item id="BZ" alias="备注" type="String" length="100" width="250"/>
</entry>