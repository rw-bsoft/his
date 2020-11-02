<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Office" alias="页眉页脚">
	<item ref="b.JLXH" alias="记录序号" type="long" length="9" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="ID" alias="科室名称" type="long" fixed="true" length="8" not-null="1" width="150">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item ref="b.MBBH" alias="页眉页脚模版" type="long" length="18" not-null="1" width="150">
		<dic id="phis.dictionary.chtemplate" autoLoad="true"/>
	</item>
	<item ref="b.KSFL" alias="科室分类" type="int" length="1" queryable="true" display="0" defaultValue="0">
		<dic autoLoad="true">
			<item key="0" text="全部科室"/>
			<item key="1" text="门诊科室"/>
			<item key="2" text="医技科室"/>
			<item key="3" text="住院科室"/>
		</dic>
	</item>
	<item ref="b.JGID" alias="机构ID" length="20" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.emr.schemas.EMR_KSMB_YMYJ">
			<join parent="ID" child="KSDM" />
		</relation>
	</relations>
</entry>
