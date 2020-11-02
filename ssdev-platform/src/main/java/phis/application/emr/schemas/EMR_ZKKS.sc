<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZKKS" alias="西医专科科室">
	<item id="ZKKS" alias="专科科室" type="int" length="9" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="9" />
		</key>
	</item>
	<item id="ZKFL" alias="专科分类" type="int" length="9"  not-null="1" display="0"/>
	<!--<item ref="c.FLMC" alias="专科类别" type="int" length="9"  not-null="1"/>-->
	<item id="KSDM" alias="科室代码" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.OFFICENAME" alias="科室名称" width="250" type="string" length="50"/>
	<item ref="b.PYCODE" alias="拼音码" />
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<relations>
		<relation type="clild" entryName="phis.application.cic.schemas.SYS_Office" >
			<join parent="KSDM" child="ID"></join>
		</relation>
		<!--<relation type="clild" entryName="EMR_ZKFL" >
				<join parent="ZKFL" child="ZKFL"></join>
			</relation>-->
	</relations>
</entry>
