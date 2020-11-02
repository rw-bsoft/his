<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_JCSQ_ZYJBXX" alias="检查申请诊断基本信息">
	<item id="BRXM" alias="病人姓名"  length="20" type="string" fixed="true" colspan="2"/>
	<item id="XJ" alias="心界"  length="200" type="string" display ="0">
		<dic autoLoad="true">
			<item key="扩大" text="扩大"/>
			<item key="正常" text="正常"/>
			<item key="缩小" text="缩小"/>
		</dic>
	</item>
	
	<item id="XY" alias="心音"  length="200" type="string" display ="0" >
		<dic autoLoad="true">
			<item key="有杂音" text="有杂音"/>
			<item key="无杂音" text="无杂音"/>
		</dic>
	</item>
	<item id="XL" alias="心率"  length="200" type="string" colspan="1" not-null="1">
		<dic autoLoad="true">
			<item key="过缓(低于60)" text="过缓(低于60)"/>
			<item key="正常(60-100)" text="正常(60-100)"/>
			<item key="过速(高于100)" text="过速(高于100)"/>
		</dic>
	</item>
	<item id="RYZD" alias="入院诊断"  length="20" type="string" fixed="true" colspan="3"/>
	<item id="ZRYS" alias="主任医生"  length="20" type="String" fixed="true" colspan="2" />
	
	<item id="XLV" alias="心律"  length="200" type="string" colspan="1" not-null="1">
		<dic autoLoad="true">
			<item key="齐 " text="齐"/>
			<item key="不齐" text="不齐"/>
		</dic>
	</item>
	<item id="XLSJ" alias="心力衰竭"  length="200" type="string" display ="0">
		<dic autoLoad="true">
			<item key="Ⅰ " text="Ⅰ"/>
			<item key="Ⅱ" text="Ⅱ"/>
			<item key="Ⅲ" text="Ⅲ"/>
		</dic>
	</item>
	<item id="XGJC" alias="X光检查"  length="200" type="string" colspan="2" display ="0">
		<dic autoLoad="true">
			<item key="心影增大" text="心影增大"/>
			<item key="心影正常" text="心影正常"/>
			<item key="心影缩小" text="心影缩小"/>
		</dic>
	</item>
	<item id="BZXX" alias="备注信息"  length="1000" type="string" colspan="3" />
	<item id="ZSXX" alias="主诉信息"  length="1000" type="string" colspan="3" />
	<item id="XBS" alias="现病史"  length="1000" type="string" colspan="3" />
	<item id="JWS" alias="既往史"  length="1000" type="string" colspan="3" />
	<item id="GMS" alias="过敏史"  length="1000" type="string" colspan="3"/>
	<item id="FZJC" alias="辅助检查"  length="1000" type="string" colspan="6" />
	<item id="TGJC" alias="体格检查"  length="1000" type="string" colspan="6" />
	
	
	<item id="CTXX" alias="查体信息"  length="1000" type="string" colspan="6" not-null="1" display ="0"/>
	<item id="SYXX" alias="实验器材检查"  length="1000" type="string" colspan="6" display="0"/>
</entry>