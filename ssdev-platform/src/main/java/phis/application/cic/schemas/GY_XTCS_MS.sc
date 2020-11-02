<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_XTCS_MS" tableName="GY_XTCS" alias="系统参数">
	<item id="JGID" alias="机构ID" display="0" length="20" type="string" pkey="true"/>
	<item id="CSMC" alias="参数名称" display="0" type="string" length="20" pkey="true"/>
	<item id="CSZ" alias="参数值" display="0" type="string" length="400"/>
	<item id="MRZ" alias="默认值" display="0" type="string" length="400"/>
	<item id="BZ" alias="备注" display="0" type="string" length="80"/>
	<item id="YS_MZ_FYYF_XY" alias="西药"  virtual="true" type="int">
		<dic id="phis.dictionary.pharmacy" autoLoad="true" />
	</item>
	<item id="YS_MZ_FYYF_ZY" alias="中成药"  virtual="true" type="int">
		<dic id="phis.dictionary.pharmacy" autoLoad="true" />
	</item>
	<item id="YS_MZ_FYYF_CY" alias="中草药"  virtual="true" type="int">
		<dic id="phis.dictionary.pharmacy" autoLoad="true" />
	</item>
</entry>
