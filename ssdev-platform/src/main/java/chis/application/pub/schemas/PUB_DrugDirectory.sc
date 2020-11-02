<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pub.schemas.PUB_DrugDirectory" alias="药品">
	<item id="drugCode" alias="记录序号" type="string" length="30"
		pkey="true" generator="assigned"  display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key>
	</item>
	<item id="drugName" alias="药品名称" type="string" length="80" width="200"  not-null="1" queryable="true" fixed="true"/>
	<item id="drugspec" alias="药品规格" type="string" length="50"  queryable="true" fixed="true"/>
	<item id="drugunits" alias="药品单位" type="string" length="6"  queryable="true" fixed="true"/>
	<item id="minunits" alias="最小单位" type="string" length="6" fixed="true"/>
	<item id="packnum" alias="包装数量" type="int"  fixed="true"/>
	<item id="drugtype" alias="药品类型" type="string" length="2"  fixed="true">
		<dic id="chis.dictionary.prescriptionType"/>
	</item>	
	<item id="classcode" alias="分类编号" type="string" length="30" display="0"/>
	<item id="formulation" alias="剂型" type="string" length="20"  width="100" fixed="true"/>
	<item id="pycode" alias="拼音代码" type="string" length="20" width="100"  queryable="true"  fixed="true"/>
	<item id="ctflag" alias="ctflag" type="string" length="1"  display="0"/>
	<item id="usemode" alias="使用途径" type="string" length="2"  fixed="true"/>
	<item id="invalid" alias="有效性" type="string" length="6"  fixed="true"/>
	<item id="drugtext" alias="药品说明" type="string"  width="200" colspan="2" fixed="true"/>
	<item id="enName" alias="英文名" type="string" length="100" width="200" fixed="true"/>
	<item id="mdcUse" alias="用药标识" type="string" length="1" width="100" colspan="2" queryable="true">
		<dic id="chis.dictionary.mdcUseMedicineType"/>
	</item>
	<item id="YPXH" alias="药品序号" type="long" length="18" display="0"/>
</entry>
