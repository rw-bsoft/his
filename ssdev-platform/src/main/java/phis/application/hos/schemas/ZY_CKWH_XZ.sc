<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_CKWH" alias="催款维护">
	<item id="KSDM" alias="科室(病区)名称" type="long" display="0" length="18" not-null="1" fixed="true"  pkey="true"/>
	<item id="BRXZ" alias="病人性质(住院)" type="long" length="18" not-null="1" width="120" fixed="true"  pkey="true">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true" />
	</item>
	<item id="CKBL" alias="催款比例" type="double" precision="2" nullToValue="0" max="1" min="0" length="6"/>
	<item id="CKJE" alias="催款金额" type="double" precision="2" nullToValue="0" length="6"/>
	<item id="DJJE" alias="冻结金额" type="double" precision="2" nullToValue="0" length="6"/>
	<item id="ZDXE" alias="最低限额" type="double" precision="2" nullToValue="0" length="6"/>
	<item id="OP_STATUS" alias="保存状态" type="String" display="0" />
</entry>
