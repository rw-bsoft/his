<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_RK02" alias="入库02">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="FPHM" alias="发票号码" type="string" length="20"/>
	<item ref="b.YPMC" mode="remote"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC" fixed="true"/>
	<item id="RKSL" alias="入库数量" length="9" precision="2" not-null="1" min="-999999.99" max="999999.99" type="double"/>
	<item id="JHJG" alias="进货价格" length="11" precision="4" not-null="1" min="0" max="999999.9999" type="double" />
	<item id="JHHJ" alias="进货合计" length="11" precision="2" not-null="1" min="-9999999.99" max="99999999.99" type="double" fixed="true"/>
	<item id="LSJG" alias="零售价格" length="11" precision="4" not-null="1" min="0" max="999999.9999" type="double"/>
	<item id="LSJE" alias="零售合计" length="12" precision="2" not-null="1" type="double" fixed="true"/>
	<item id="CJHJ" alias="差价"  length="12" precision="2" not-null="1" type="double" fixed="true"/>
	<item id="YPPH" alias="批号" type="string" length="20"/>
	<item id="YPXQ" alias="效期" type="date"/>
	<item id="SHHM" alias="随货号" type="string" length="12"/>
	
	<item id="XTSB" alias="药库识别" length="18" not-null="1" type="long" display="0"/>
	<item id="RKFS" alias="入库方式" length="4" not-null="1" type="int" display="0"/>
	<item id="RKDH" alias="入库单号" length="6" not-null="1" type="int" display="0"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCJ" alias="药品厂家" type="string" length="12" display="0"/>
	<item id="PFJG" alias="批发价格" length="11" precision="4"  min="0" max="999999.9999" type="double" display="0" />
	<item id="HGSL" alias="合格数量" length="10" precision="4" not-null="1" type="double" display="0"/>
	<item id="YSDH" alias="验收单号" length="6" type="int" display="0"/>
	<item id="YSGH" alias="验收工号" type="string" length="10" display="0"/>
	<item id="YSRQ" alias="验收日期" type="datetime" display="0"/>
	<item id="FKGH" alias="付款工号" type="string" length="10" display="0"/>
	<item id="PZHM" alias="批准文号" type="string" length="150" />
	<item id="FKRQ" alias="付款日期" type="datetime" display="0"/>
	<item id="TYPE" alias="库存性质" length="6" type="int" display="0" defaultValue="1"/>
	<item id="ZRJE" alias="折让金额" length="10" precision="4" type="double" display="0"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double" display="0"/>
	<item id="BZLJ" alias="标准零价" length="12" precision="6" not-null="1" type="double" display="0"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"  defaultValue="0" display="0"/>
	<item id="DJFS" alias="定价方式" length="1" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="DJGS" alias="定价公式" type="string" length="250" display="0"/>
	<item id="FKJE" alias="付款金额" length="12" precision="4" not-null="1" type="double" display="0" defaultValue="0"/>
	<item id="YFJE" alias="已付金额" length="12" precision="4" not-null="1" type="double" display="0" defaultValue="0"/>
	<item id="FKGS" alias="付款公式" type="string" length="100" display="0"/>
	<item id="YPKL" alias="药品扣率" length="3" precision="2" not-null="1" type="double" display="0" defaultValue="0"/>
	<item id="JBYWBZ" alias="基本药物标志" length="1" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="JHSBXH" alias="计划识别序号" length="18" type="long" display="0"/>
	<item ref="d.DJFS" display="0"/>
	<item ref="d.DJGS" display="0"/>
	<item ref="d.LSJG" id="BZLJ" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDXX" >
			<join parent="YPCD" child="YPCD"></join>
			<join parent="YPXH" child="YPXH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
	</relations>
</entry>
