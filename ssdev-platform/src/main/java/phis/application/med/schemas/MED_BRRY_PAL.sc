<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="按病人提交病人集合" sort="a.BRCH asc">
	
	
	<item ref="b.ZYH" alias="住院号" display="0" />
	 <item id="BRCH" alias="病人床号" length="12"/>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1" display="0"/>
	 <item id="BRXM" alias="病人姓名" length="20" not-null="1"/>
	<item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1" display="0"/>
	 <item id="DJID" alias="冻结ID号" type="long" length="18" not-null="1" display="0"/>
	
	
	<relations>
		<relation type="child" entryName="phis.application.war.schemas.ZY_BQYZ" >
			<join parent="ZYH" child="ZYH"></join>
			<join parent="JGID " child="JGID "></join>
		</relation>
	</relations>
</entry>