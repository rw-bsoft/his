<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLSF" alias="按项目提交项目集合" sort="a.PYDM asc">
	
	<item id="FYXH" alias="费用序号"  length="18" type="long" not-null="1" generator="assigned" pkey="true" display="0">
		
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="10" target="FYMC" codeType="py" />
	<item id="FYMC" alias="费用名称"  type="string" length="80" width="220" not-null="1" renderer="filterLength"/>
	<item id="FYDW" alias="费用单位" type="string" length="4" display="0"/>
	
	<item ref="b.YPXH" alias="药品序号" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.war.schemas.ZY_BQYZ_CQ" >
			<join parent="FYXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>