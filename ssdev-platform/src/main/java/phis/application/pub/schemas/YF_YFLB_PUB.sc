<?xml version="1.0" encoding="UTF-8"?>

<entry id="YF_YFLB_PUB" tableName="GY_QXKZ" alias="药房列表">
	<item id="JGID" alias="机构ID" display="0" type="string" length="20"
		not-null="1" />
	<item id="KSDM" alias="药房识别" type="int" display="0" defaultValue="2"
		length="18" not-null="1" generator="assigned" pkey="true" />
	<item id="YWLB" alias="业务类别" display="0" type="string" length="10"
		not-null="1" />
	<item ref="b.YFMC" alias="药房名称" width="250" length="30"></item>
	<item id="MRBZ" alias="选择" type="int" renderer="onRenderer"></item>
	<relations>
		<relation type="parent" entryName="phis.application.pha.schemas.YF_YFLB">
			<join parent="YFSB" child="KSDM" />
		</relation>
	</relations>
</entry>
