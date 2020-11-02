<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YPXX"   alias="药房管理_库存对账" >
	<item id="YFSB" alias="药房识别" type="long" display="0"
		not-null="1" defaultValue="%user.properties.pharmacyId"  update="false" />
	<item id="YPXH" alias="药品序号" type="long"  display="0" not-null="1"   update="false"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%" length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="YFGG" alias="药房规格" type="string" length="20"/>
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="CDMC" alias="产地简称" type="string"  width="100" not-null="true" />
	<item id="QCSL" alias="数量" width="120" type="double" precision="2" mode="remote" summaryType="sum" summaryRenderer="totalHJSL" />
	<item id="QCJE" alias="金额" width="120" type="double" precision="4" mode="remote" summaryType="sum" summaryRenderer="totalHJJE" />
	<item id="RKSL" alias="数量" width="120" type="double" precision="2" mode="remote" summaryType="sum" summaryRenderer="totalHJSL" />
	<item id="RKJE" alias="金额" width="120" type="double" precision="4" mode="remote" summaryType="sum" summaryRenderer="totalHJJE" />
	<item id="CKSL" alias="数量" width="120" type="double" precision="2" mode="remote" summaryType="sum" summaryRenderer="totalHJSL" />
	<item id="CKJE" alias="金额" width="120" type="double" precision="4" mode="remote" summaryType="sum" summaryRenderer="totalHJJE" />
	<item id="QMSL" alias="数量" width="120" type="double" precision="2" mode="remote" summaryType="sum" summaryRenderer="totalHJSL" />
	<item id="QMJE" alias="金额" width="120" type="double" precision="4" mode="remote" summaryType="sum" summaryRenderer="totalHJJE" />
	<item id="YPSL" alias="当前库存数量" width="120" precision="2" min="0" max="999999.99" 
		type="double" mode="remote" summaryType="sum" summaryRenderer="totalHJSL" />
	<item id="LSJE" alias="当前库存金额" width="120" precision="4"  max="99999999.9999" 
		type="double" mode="remote" summaryType="sum" summaryRenderer="totalHJJE" />
	<item id="LSJG" alias="库存单价" width="100" precision="4" type="double" max="999999.999999" fixed="true" renderer="onRenderer_four"/>
	<item id="CDLSJG" alias="产地单价" width="100" type="double" max="999999.9999" min="0" precision="4" defaultValue="0"/>
	<item id="YFBZ" alias="药房包装" display="2" type="int" />
	<item id="ZXBZ" alias="最小包装" type="int" defaultValue="1" display="2" minValue="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0" />
</entry>