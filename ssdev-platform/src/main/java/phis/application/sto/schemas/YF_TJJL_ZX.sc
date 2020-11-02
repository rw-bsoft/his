<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_TJJL"   alias="药房调价记录">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item ref="d.YFMC"  fixed="true" type="string" width="60" alias="库房名称"/>
	<item ref="b.YPMC"  width="110" type="string" fixed="true"/>
	<item id="YPGG" alias="药品规格" type="string" length="20" fixed="true" width="60"/>
	<item id="YFDW" alias="单位" type="string" length="4" fixed="true" width="40"/>
	<item ref="c.CDMC" alias="产地" type="string" fixed="true" width="100"/>
	<item id="YJHJ" alias="原进货价"  width="71" length="13" precision="4" not-null="1" fixed="true" type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="YLSJ" alias="原零售价"  width="71" length="13" precision="4" not-null="1" fixed="true" type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="XJHJ" alias="新进货价" fixed="true" width="71" length="11" precision="4" not-null="1"  type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="XLSJ" alias="新零售价" fixed="true" width="71" length="11" precision="4" not-null="1"  type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="TJSL" alias="调价数量"  width="71" length="11"  defaultValue="0" precision="2" not-null="1"  fixed="true" type="double" max="999999.9999" min="0" renderer="onRenderer_tow"/>
	<item id="TJJE" alias="调价金额" width="71" virtual="true" type="double" fixed="true" precision="2" renderer="onRenderer"/>
	<item id="YPPH" alias="药品批号" width="71" type="string" length="20"  fixed="true"/>
	<item id="YPXQ" alias="药品效期" width="71" type="datetime" fixed="true" renderer="rendererNull"/>
	
	<item id="XLSE" alias="新零售额" length="12" defaultValue="0" display="0" precision="4" not-null="1"  fixed="true" type="double" max="99999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="XPFJ" alias="新批发价" length="13" defaultValue="0" precision="6"  type="double" max="99999999.9999" min="0" display="0"/>
	<item id="YPFJ" alias="原批发价" length="13" defaultValue="0" precision="6"  type="double" max="99999999.9999" min="0" display="0"/>
	<item id="JGID" alias="机构ID" length="20" not-null="1" display="0" type="string" defaultValue="%user.manageUnit.id"/>
	<item id="YKSB" alias="药库识别" length="18" not-null="1" type="long" display="0" defaultValue="0"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" type="long" display="0"/>
	<item id="CKBH" alias="窗口编号" length="2" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="TJFS" alias="调价方式" length="2" not-null="1" type="int" display="0"/>	
	<item id="TJDH" alias="调价单号" length="6" not-null="1" type="int" display="0"/>
	<item id="TJRQ" alias="调价日期" type="datetime" not-null="1" display="0" defaultValue="%server.date.datetime"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int" display="0"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" display="0"/>
	<item id="TJWH" alias="调价文号" type="string" length="30" display="0"/>
	
	<item id="YLSE" alias="原零售额" length="12" defaultValue="0" precision="4" not-null="1" type="double" max="99999999.9999" min="0" display="0"/>
	<item id="YPFE" alias="原批发额" length="12" defaultValue="0" precision="4"  type="double" max="99999999.9999" min="0" display="0"/>
	<item id="XPFE" alias="新批发额" length="12" defaultValue="0" precision="4"  type="double" max="99999999.9999" min="0" display="0"/>
	<item id="YJHE" alias="原进货额" length="12" defaultValue="0" precision="4" not-null="1" type="double" max="99999999.9999" min="0" display="0"/>
	<item id="XJHE" alias="新进货额" length="12" defaultValue="0" precision="4" not-null="1" type="double" max="99999999.9999" min="0" display="0"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long" display="0" defaultValue="0"/>
	<item id="KCSL" alias="库存数量" length="10" precision="4" not-null="1" type="double" display="0" defaultValue="0"/>
	
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		     <join parent="YPXH" child="YPXH" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		     <join parent="YPCD" child="YPCD" />
		<relation type="parent" entryName="phis.application.pha.schemas.YF_YFLB" />
		     <join parent="YFSB" child="YFSB" />
	</relations>
</entry>
