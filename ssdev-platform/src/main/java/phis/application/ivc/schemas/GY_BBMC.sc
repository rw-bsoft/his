<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_BBMC" tableName="GY_BBMC" alias="报表名称" sort="BBBH">
	<item id="BBBH" alias="报表编号" type="long" length="18" not-null="1"  pkey="true" generator="assigned"  >
		<!--<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
		<dic autoLoad="true" >
			<item key="1" text="门诊医生开单科室报表"/>
			<item key="2" text="门诊医生开单医生报表"/>
			<item key="3" text="门诊医生执行科室报表"/>
			<item key="4" text="门诊医生执行医生报表"/>
			<item key="5" text="性质费用作废报表"/>
			<item key="6" text="性质费用明细作废报表"/>
			<item key="7" text="性质费用汇总作废报表"/>
		</dic>-->
	</item>
	<item id="JGID" alias="机构ID"  type="string" length="20"  display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BBMC" alias="报表名称" type="string" length="40"  not-null="1" width="200" display="3"/>
	<item id="BBLX" alias="报表类型" type="long" length="2" display="0" defaultValue="1"/>
	<item id="BBHS" alias="报表行数" type="long" length="3" display="0"/>
	<item id="BBLS" alias="报表列数" type="long" length="3" display="0"/>
	<item id="WIND" alias="执行窗口" type="string" length="40" display="0"/>
	<item id="BZXX" alias="备注" type="string" length="250" display="0"/>
	<item id="MAIN_TITLE" alias="主标题" type="string" length="250" display="0"/>
	<item id="SUB_TITLE1" alias="副标题1" type="string" length="250" display="0"/>
	<item id="SUB_TITLE2" alias="副标题2" type="string" length="250" display="0"/>
	<item id="SUB_TITLE3" alias="副标题3" type="string" length="250" display="0"/>
	<item id="TABLEMAKER" alias="显示制表人" type="long" length="1" display="0"/>
	<item id="PRINTDAY" alias="显示打印日期" type="long" length="1" display="0"/>
	<item id="PRINTPAGE" alias="显示页码" type="long" length="1" display="0"/>
	<item id="FONTFACE_TITLE" alias="主标题字体" type="string" length="40" display="0"/>
	<item id="FONTHEIGHT_TITLE" alias="主标题字号" type="string" length="10" display="0"/>
	<item id="FONTWEIGHT_TITLE" alias="主标题加粗" type="string" length="40" display="0"/>
	<item id="FONTITALIC_TITLE" alias="主标题斜体" type="string" length="1" display="0"/>
	<item id="FONTUNDERLINE_TITLE" alias="主标题下划线" type="string" length="1" display="0"/>
	<item id="FONTCOLOR_TITLE" alias="主标题字体颜色" type="long" length="8" display="0"/>
	<item id="FONTFACE_SUBTITLE" alias="副标题字体" type="string" length="40" display="0"/>
	<item id="FONTHEIGHT_SUBTITLE" alias="副标题字号" type="string" length="10" display="0"/>
	<item id="FONTWEIGHT_SUBTITLE" alias="副标题加粗" type="string" length="40" display="0"/>
	<item id="FONTITALIC_SUBTITLE" alias="副标题斜体" type="string" length="1" display="0"/>
	<item id="FONTUNDERLINE_SUBTITLE" alias="副标题下划线" type="string" length="1" display="0"/>
	<item id="FONTCOLOR_SUBTITLE" alias="副标题字体颜色" type="long" length="8" display="0"/>
	<item id="GROUPBYFYLB" alias="按费用类别分组" type="long" length="1" display="0"/>
	<item id="DISPLAYSUM" alias="显示分组小计" type="long" length="1" display="0"/>
	<item id="ZFHJ" alias="显示自负合计" type="long" length="1" display="0"/>
	<item id="YSHJ" alias="显示应收合计" type="long" length="1" display="0"/>
	<item id="XGPB" alias="修改判别" type="long" length="1" display="0" defaultValue="0"/>
	
</entry>
