<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TJJL" alias="药库调价记录">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" defaultValue="1"  type="long"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" defaultValue="0"  type="long"/>
	<item id="TJFS" alias="调价方式" length="2" type="int" not-null="1" />
	<item id="TJDH" alias="调价单号" length="6" not-null="1"  type="int"/>
	<item id="YPXH" alias="药品序号" length="18" type="long" not-null="1"/>
	<item id="YPCD" alias="药品产地" length="18" type="long" not-null="1"/>
	<item id="TJSL" alias="调价数量" length="10" precision="2" not-null="1" defaultValue="0" type="double" />
	<item id="YJHJ" alias="原进货价" length="12" precision="2" not-null="1" fixed="true" type="double" max="999999.9999" min="0"/>
	<item id="YLSJ" alias="原零售价" width="71" length="12" precision="2" not-null="1" fixed="true" type="double" max="999999.9999" min="0"/>
	<item id="XJHJ" alias="新进货价" width="71" length="12" precision="2" not-null="1"  type="double" max="999999.9999" min="0" />
	<item id="XLSJ" alias="新零售价" width="71" length="12" precision="2" not-null="1"  type="double" max="999999.9999" min="0" />
	<item id="YPFJ" alias="原批发价" defaultValue="0" length="12" precision="2"  type="double" />
	<item id="XPFJ" alias="新批发价" defaultValue="0" length="12" precision="2"  type="double" />
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int" defaultValue="1"/>
	<item id="YPPH" alias="药品批号" type="string" length="50"/>
	<item id="YPXQ" alias="药品效期" type="date"/>
	<item id="YJHE" alias="原进货额" length="12" precision="4" not-null="1" fixed="true" type="double" max="999999.9999" min="0"/>
	<item id="YLSE" alias="原零售额" width="71" length="12" precision="4" not-null="1" fixed="true" type="double" max="999999.9999" min="0"/>
	<item id="XJHE" alias="新进货额" width="71" length="12" precision="4" not-null="1"  type="double" max="999999.9999" min="0" />
	<item id="XLSE" alias="新零售额" width="71" length="12" precision="4" not-null="1"  type="double" max="999999.9999" min="0" />
	<item id="YPFE" alias="原批发额" defaultValue="0" length="12" precision="4"  type="double" />
	<item id="XPFE" alias="新批发额" defaultValue="0" length="12" precision="4"  type="double" />
	<item id="KCSL" alias="库存数量" length="10" precision="4" type="double" not-null="1"/>
	<item id="KCSB" alias="库存识别" length="18" type="long"/>
	<item ref="b.YPMC" mode="remote"  width="120"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC"  fixed="true"/>
	<item ref="d.YKMC"  display="0" id="YFMC" alias="库房名称"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		<relation type="parent" entryName="phis.application.sto.schemas.YK_YKLB" >
			<join parent="YKSB" child="XTSB"/>
		</relation>
	</relations>
</entry>
