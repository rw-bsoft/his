<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_QXKZ" alias="统一权限控制" sort="b.PERSONID">
	<item id="YGDM" alias="员工主键" display="0"  type="string" length="20" not-null="1" pkey="true"/>
	<item ref="b.PERSONID" alias="员工工号" display="1" />
	<item ref="b.PERSONNAME" alias="员工姓名" display="1"  />
	<item ref="b.PYCODE" alias="拼音码"  />
	
	<item id="MRBZ" alias="默认标志" display="0" />
	<item id="YWLB" alias="业务类别" display="0"  type="string" length="10" not-null="1"/>
	<item ref="b.OFFICECODE" alias="科室名称" type="long" width="100" length="18" not-null="1" display="1"/>
	<item id="JGID" alias="所属机构"  width="150">
		<dic id="phis.@manageUnit"/>
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.SYS_Personnel">
			<join parent="PERSONID" child="YGDM" />
		</relation>
	</relations>
</entry>