<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.quality.schemas.QUALITY_ZK_GXSD"  >
	<item id="RECODID"  type="string" length="16"     not-null="1"   pkey="true"   hidden="true" display="0">
		  <key>
		      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		    </key>
	</item>
	<item id="XMBS" alias="项目标识" type="string" length="40"/>
	 <item id="GXSD" alias="关系" type="string" length="10"  not-null="1"  queryable="true">
	 	<dic id="chis.dictionary.QualityControl_GXSD"/>
	  </item>
	<item id="SJCZ" alias="数据差值" type="string" length="10"/>
	<item id="DF" alias="得分" type="string" length="10" width="200"/>
</entry>
