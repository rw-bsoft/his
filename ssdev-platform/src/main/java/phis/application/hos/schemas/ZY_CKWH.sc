<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_CKWH" alias="催款维护">
	<item id="KSDM" alias="科室(病区)名称" type="long" display="0" length="18" not-null="1" pkey="true"/>
	<item id="BRXZ" alias="病人性质(住院)" type="long" display="0" length="18" not-null="1" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1" pkey="true"/>
	<item id="CKBL" alias="催款比例" type="double" precision="2" nullToValue="0" max="1" min="0" length="6"/>
	<item id="CKJE" alias="催款金额" type="double" precision="2" nullToValue="0" length="6"/>
	<item id="DJJE" alias="冻结金额" type="double" precision="2" nullToValue="0" length="6"/>
	<item id="ZDXE" alias="最低限额" type="double" precision="2" nullToValue="0" length="6"/>
</entry>
