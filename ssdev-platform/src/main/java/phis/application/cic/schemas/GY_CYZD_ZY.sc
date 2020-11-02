<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_CYZD_ZY" tableName="GY_CYZD" sort="a.JLBH desc" alias="个人常用诊断-中医">
	<item id="JLBH" alias="记录编号" type="int" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="SSLB" alias="所属类别" type="int" length="1" display="0"/>
	<item id="YGDM" alias="员工代码" type="sting" length="10" display="0" defaultValue="%user.userId"/>
	<item id="KSDM" alias="科室代码" type="long" length="10" display="0"/>
	<item id="ZDMC" alias="诊断名称"  width="150" display="1" type="string" />
	<item id="ZDXH" alias="诊断序号" display="0"  not-null="1" type="int" width="200" />
	<item id="ICD10" alias="诊断编码" type="string" fixed="true" length="20" />
	<item id="PYDM" alias="拼音码" type="string"	length="8" display="0" width="80"  queryable="true" selected="true"/>
	<item id="WBDM" alias="五笔码" type="string" display="0" length="8"/>
	<item id="JGID" alias="机构" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="CFLX" alias="处方类型"  type="int" length="1" display="0"/>
	<item ref="b.JBBS" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.emr.schemas.EMR_ZYJB" >
		<join parent="JBBS" child="ZDXH" />
		</relation>
	</relations>
</entry>