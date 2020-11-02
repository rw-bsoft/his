<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YK02" alias="药房盈亏02" >
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string"  defaultValue="%user.manageUnit.id" display="0"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item ref="b.YPMC" fixed="true" colspan="2" />
	<item id="YPGG" alias="药品规格" type="string" length="20" fixed="true"/>
	<item id="YFDW" alias="药房单位" type="string" length="4" fixed="true"/>
	<item ref="c.CDMC" alias="产地名称" fixed="true" colspan="2" />
	<item id="LSJG" alias="零售价格" length="12" precision="4" not-null="1" type="double" max="999999.9999" min="0" fixed="true"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" not-null="1" type="double" max="999999.9999" min="0" fixed="true"/>
	<item id="PQSL" alias="盘前数量" length="10" precision="2" not-null="1" type="double" max="999999.99" min="0" fixed="true"/>
	<item id="SPSL" alias="实盘数量" length="10" precision="4" not-null="1" type="double" max="999999.99" min="0" fixed="true"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" type="long" display="0"/>
	<item id="CKBH" alias="窗口编号" length="2" not-null="1" type="int" display="0"/>
	<item id="PDDH" alias="盘点单号" length="8" not-null="1" type="int" display="0"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int" display="0"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4" type="double" max="999999.9999" min="0" display="0"/>
	<item id="YPPH" alias="药品批号" type="string" length="20" display="0"/>
	<item id="YPXQ" alias="药品效期" type="timestamp" display="0"/>
	<item id="YLSE" alias="原零售额" length="12" precision="4" not-null="1" type="double" max="99999999.99" min="0" display="0"/>
	<item id="YPFE" alias="原批发额" length="12" precision="4"  type="double" max="99999999.99" min="0" display="0"/>
	<item id="YJHE" alias="原进货额" length="12" precision="4" not-null="1" type="double" max="99999999.99" min="0" display="0"/>
	<item id="XLSE" alias="新零售额" length="12" precision="4" not-null="1" type="double" max="99999999.99" min="0" display="0"/>
	<item id="XPFE" alias="新批发额" length="12" precision="4"  type="double" max="99999999.99" min="0" display="0"/>
	<item id="XJHE" alias="新进货额" length="12" precision="4" not-null="1" type="double" max="99999999.99" min="0" display="0"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long" display="0"/>
	<item id="LRRY" alias="录入人员" type="string" length="10" display="0"/>
	<item id="BZ" alias="备注" type="string" length="80" display="0"/>
	<item id="LRWC" alias="录入完成" length="1" type="int" display="0"/>
	<item id="LRBZ" alias="盘点状态" length="1" type="int" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		     <join parent="YPXH" child="YPXH"></join>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		     <join parent="YPCD" child="YPCD"></join>
		<relation type="parent" entryName="phis.application.pha.schemas.YF_KCMX_CSH" >
			 <join parent="SBXH" child="KCSB"></join>
		</relation>
	</relations>
</entry>
