<?xml version="1.0" encoding="UTF-8"?>

<entry alias="处方对照" entityName="YK_TYPK" >
	<!--其他的键根据各地医保返回值添加-->
	<item id="YPXH" alias="药品序号" type="long" length="18" pkey="true"/>
	<item id="YPMC" alias="药品名称" type="string" length="20" />
	<item id="YPGG" alias="药品规格" type="string" length="20" />
	<item id="SBM" alias="药品编码" type="string"  />
	<item id="PYDM" alias="拼音编码" type="string" queryable="true" fixed="true" display="0" />
</entry>
