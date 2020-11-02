<?xml version="1.0" encoding="UTF-8"?>

<entry alias="本地费用对照表" entityName="V_YLMX_YB" >
	<item id="JGID" alias="机构代码" type="string" length="20" pkey="true" display="0" />
	<item id="FYXH" alias="费用序号" type="long" length="18" pkey="true" display="0" />
	<item id="FYGL" alias="费用归类" type="string" length="20" width="60" fixed="true"/>
	<item id="FYMC" alias="费用名称"  type="string" length="80" queryable="true" width="220" not-null="1" fixed="true"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" width="100" selected="true"  queryable="true" fixed="true"/>
	<item id="FYDW" alias="单位" type="string" length="4" width="50" fixed="true"/>
	<item id="FYDJ" alias="费用单价" type="double"  length="12" width="75" fixed="true"/>
	<item id="XMBM" alias="项目编码" type="string" length="20" width="120" queryable="true" fixed="true"/>
	<!--<item id="WJBM" alias="物价编码" type="string" length="20" fixed="true"/>-->
	<item id="YYZBM" alias="医保编码" type="string" width="150" queryable="true" length="20"/>
</entry>
