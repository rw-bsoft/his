<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_TYMX" alias="病区退药申请">
	<item ref="b.YPMC" alias="药品名称" width="120" fixed="true"/>
	<item id="YPGG" alias="规格" length="20" width="50" fixed="true" renderer="onRendererNull"/>
	<item id="YFDW" alias="单位" length="4" width="50" fixed="true" renderer="onRendererNull"/>
	<item id="YPSL" alias="退药数量" type="double" length="8" precision="2"  min="0.00" max="999999.99" />
	<item id="YPJG" alias="药品单价" type="double" length="13" precision="4" fixed="true"/>
	<item ref="c.CDMC" alias="药品产地" fixed="true"/>
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" display="0" type="long" length="18"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" display="0"/>
	<item id="SQRQ" alias="申请日期" type="timestamp" not-null="1" display="0"/>
	<item id="TYRQ" alias="退药日期" type="timestamp" display="0"/>
	<item id="TYBQ" alias="退药病区" type="long" length="18" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1" display="0"/>
	<item id="CZGH" alias="操作工号" length="10" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" display="0"/>
	<item id="ZFPB" alias="自费判别" type="int" length="2" display="0"/>
	<item id="TJBZ" alias="提交标志" type="int" length="1" display="0" defaultValue="0"/>
	<item id="YZID" alias="医嘱ID" type="long" length="18" display="0"/>
	<item id="THBZ" alias="退回标志" type="int" length="8" defaultValue="0" not-null="1" display="0"/>
	<item id="THSJ" alias="退回时间" type="timestamp" display="0"/>
	<item id="THR" alias="退回人" length="10" display="0"/>
	<item id="ZYH" alias="住院号" length="18" type="long" width="50" display="0"/>
	<item id="TYLX" alias="退药类型" length="1" type="int" width="50" display="0"/>
	<item id="KTSL" alias="可退数量"  type="double" width="50" display="0" virtual="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
