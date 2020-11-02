<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_CKBH" alias="窗口编号">
	<item id="JGID" alias="机构ID" length="20" defaultValue="%user.manageUnit.id" display="0" not-null="1" update="false"/>
	<item id="YFSB" alias="药房识别" type="long" length="18" not-null="1"  display="0" pkey="true"/>
	<item id="CKBH" alias="窗口编号" type="int" length="2" not-null="1" generator="assigned" pkey="true">
	</item>
	<item id="CKMC" alias="窗口名称" type="string" length="12" not-null="1"/>
	<item id="XYQX" alias="西药权限" type="int" defaultValue="1"  length="1" not-null="1">
		<dic id="phis.dictionary.haveOrNo"/>
	</item>
	<item id="ZYQX" alias="中药权限" type="int" defaultValue="1" length="1" not-null="1">
		<dic id="phis.dictionary.haveOrNo"/>
	</item>
	<item id="CYQX" alias="草药权限" type="int" defaultValue="1" length="1" not-null="1">
		<dic id="phis.dictionary.haveOrNo"/>
  		
	</item>
	<item id="QYPB" alias="使用" type="int" defaultValue="1" width="60" length="1" not-null="1">
		<dic id="phis.dictionary.confirm" autoLoad="true" />
	</item>
	<item id="YXPB" alias="优先判别" type="int"  length="1" defaultValue="0" not-null="1" display="0"/>
	<item id="PDCF" alias="排队处方" length="18" type="long" defaultValue="0" not-null="1" display="0"/>
	<item id="JSJM" alias="绑定窗口IP" length="18" type="string" width="120"/>
</entry>
