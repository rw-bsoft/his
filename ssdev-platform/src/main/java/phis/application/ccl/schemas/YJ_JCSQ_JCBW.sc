<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_JCBW" alias="检查申请-检查部位" sort="a.PYDM">
	<item id="BWID" alias="部位ID"  length="12" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" defaultFill="0" type="increase" length="12" startPos="1"/>
	    </key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<item id="BWMC" alias="检查部位" type="String" length="50" not-null="1" width="150"/>
	<item id="PYDM" alias="拼音代码" type="string" length="20" maxValue="10" display="2" queryable="true" selected="true" target="BWMC" codeType="py" not-null="1"/>
</entry>