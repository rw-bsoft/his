<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_JCLB" alias="检查申请-检查类别" sort="a.SSLX"> 
	<item id="LBID" alias="检查类别ID"  length="12" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" defaultFill="0" type="increase" length="12" startPos="1"/>
	    </key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<item id="LBMC" alias="检查类别" type="String" length="50" not-null="1"  width="80"/>
	<item id="PYDM" alias="拼音代码" type="string" length="20" maxValue="10" display="2" queryable="true" selected="true" target="LBMC" codeType="py" not-null="1"/>
	<item id="SSLX" alias="所属类别" type="int" length="2" not-null="1" width="80">
		<dic id="phis.dictionary.checkApplyType"></dic>
	</item>
</entry>