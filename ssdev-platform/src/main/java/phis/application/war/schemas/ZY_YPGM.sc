<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_YPGM" alias="病人过敏药物">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" pkey="true" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="BLFY" alias="不良反应" length="450" />
	<item id="GMZZ" alias="过敏症状" length="20" display="0"/>
	<item id="QTZZ" alias="其它症状" length="20" display="0"/>
</entry>
