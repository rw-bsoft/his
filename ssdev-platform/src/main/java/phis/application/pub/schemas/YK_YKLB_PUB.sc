<?xml version="1.0" encoding="UTF-8"?>

<entry id="YK_YKLB_PUB" tableName="GY_QXKZ" alias="药库列表" sort="b.YKSB">
	<item id="JGID" alias="机构ID" display="0" type="string" length="20"
		not-null="1" />
	<item id="KSDM" alias="药房识别" type="int" display="0" pkey="true" />
	<item id="YWLB" alias="业务类别" display="0" type="string" length="10"
		not-null="1" />
	<item ref="b.YKMC" alias="药库名称" width="250" length="30"></item>
	<item id="MRBZ" alias="选择" type="int" renderer="onRenderer"></item>
	<relations>
		<relation type="parent" entryName="phis.application.sto.schemas.YK_YKLB">
			<join parent="YKSB" child="KSDM" />
		</relation>
	</relations>
</entry>
