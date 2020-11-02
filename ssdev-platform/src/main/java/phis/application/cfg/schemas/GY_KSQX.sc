<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_KSQX" alias="抗生素禁用">
	<item id="SBXH" alias="抗生素序号" type="int" length="10" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="10"
				startPos="1" />
		</key>
	</item>
	<item ref="b.YPMC" mode="remote" />
	<item ref="b.YFGG" display="1" fixed="true" />
	<item id="JGID" alias="机构ID" type="string" display="0" length="25"
		defaultValue="%user.manageUnit.id" />
	<item id="YSGH" alias="医生工号" type="string" display="0" length="10" />
	<item id="YPXH" alias="药品序号" type="int" display="0" length="18" />
	<item id="SYPB" alias="使用判别" type="int" length="1" display="0"
		defaultValue="0" />
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
	</relations>
</entry>
