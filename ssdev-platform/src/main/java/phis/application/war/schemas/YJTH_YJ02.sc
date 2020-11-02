<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJTH_YJ02" tableName="YJ_ZY02" alias="医技退回_医技02">
	<item id="SBXH" alias="识别序号"  length="18" type="long" not-null="1" display="0"  pkey="true"/>
	<item ref="b.FYMC" alias="项目名称" width="250"/>
	<item ref="b.FYDW" />
	<item id="YLDJ" alias="单价" type="double" length="10" not-null="1" />
	<item id="YLSL" alias="数量" type="double" length="8" not-null="1" />
	<item id="JE" alias="金额" type="double"  virtual="true" renderer="onRenderer_je"/>
	<item id="ZFBL" alias="自负比例" type="double" length="5" not-null="1"/>
	<item id="YJXH" alias="医技序号" type="long" length="18"  fixed="true" not-null="1" display="0"/>
	<item id="YZXH" alias="医嘱序号" type="long" length="18"  fixed="true" not-null="1" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_YLSF" >
			<join parent="FYXH" child="YLXH"></join>
		</relation>
	</relations>
</entry>