<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_PSJL" alias="病人过敏药物">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="BRID" alias="病人ID" length="20" type="long" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1"  display="0"/>
	<item id="YPXH_NEW" alias="药品序号" type="long" length="18" virtual="true" display="0"/>
	<item ref="b.YPMC" alias="药品名称" mode="remote" type="string" width="130" anchor="100%"
		length="80" colspan="2" not-null="true"/>
	<item id="BLFY" alias="不良反应" length="250" />
	<item id="GMZZ" alias="过敏症状" length="20" display="0"/>
	<item id="QTZZ" alias="其它症状" length="20" display="0"/>
	<item id="PSJG" alias="皮试结果(默认为1)" type="int" length="20" display="0" defaultValue="1"/>
	<item id="PSLY" alias="皮试来源" length="20" type="int" display="0" defaultValue="1"/>
	<item id="JGID" alias="录入机构" type="string" length="20" width="140" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="phis.@manageUnit" autoLoad="true"/>
	</item>
	<item ref="b.YYBS" display="0"/>
	<item ref="b.GMYWLB" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.YK_TYPK_MS" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
