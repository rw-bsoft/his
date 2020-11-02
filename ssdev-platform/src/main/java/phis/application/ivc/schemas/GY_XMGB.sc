<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_XMGB" tableName="GY_XMGB" alias="项目归并">
	<item id="GBXH" alias="归并序号"  type="long" length="18" not-null="1" pkey="true"  generator="assigned" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID"  type="string" length="20" defaultValue="%user.manageUnit.id" display="0"/>
	<item id="BBBH" alias="报表编号" type="long" length="18" display="0"/>
	<item id="SFXM" alias="收费项目" type="long" length="18" fixed="true"/>
	<item ref="b.SFMC" alias="收费名称" fixed="true" update="true"/>
	<item id="GBXM" alias="归并项目" type="long" length="4" not-null="1"/>
	<item id="XMMC" alias="显示名称"  type="string" length="20" not-null="1"/>
	<item id="SXH" alias="顺序号" type="long" length="4" not-null="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_SFXM" >
			<join parent="SFXM" child="SFXM" ></join>
		</relation>
	</relations>
</entry>
