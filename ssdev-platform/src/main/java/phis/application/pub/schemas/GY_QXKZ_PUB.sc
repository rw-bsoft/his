<?xml version="1.0" encoding="UTF-8"?>

<entry id="GY_QXKZ_PUB" tableName="GY_QXKZ" alias="医生科室权限">
	<item id="YGDM" alias="医生代码" display="0"  type="string" length="20" not-null="1"/>
	<item id="YWLB" alias="业务类别" display="0"  type="string" length="10" not-null="1"/>
	<item id="KSDM" alias="科室代码" type="long" width="250" length="18" not-null="1" display="0" />
	<item ref="b.KSMC" alias="科室名称" type="string" width="250" length="50" not-null="1" />
	<item id="MRBZ" alias="选择" type="int" renderer="onRenderer" ></item>
	<relations>
		<relation type="parent" entryName="phis.application.reg.schemas.MS_GHKS" >
			<join parent="KSDM" child="KSDM"></join>
		</relation>
	</relations>
</entry>