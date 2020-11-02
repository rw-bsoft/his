<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_BGYZB" alias="保管员帐簿"> 
	<item id="YPXH" alias="药品序号" type="long"  width="18" anchor="100%" length="80" not-null="true" display="0" pkey="true"/>
	<item id="YPCD" alias="药品产地"  type="long" width="18" anchor="100%" length="80" not-null="true"  display="0" pkey="true"/>
	<item id="YPMC" alias="药品名称" type="string" width="300" />
	<item id="YPGG" alias="药品规格" type="string"  width="150" />
	<item id="YPDW" alias="单位" type="string" width="40" />
	<item id="CDMC" alias="生产厂家" type="string"  width="300"/>
	<item id="SWKC" alias="实物数量" type="double" precision="2" width="100" maxValue="99999999.99"/>
	<item id="KCSL" alias="帐册数量"	 type="double" precision="2" width="100" maxValue="99999999.99"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" selected="true" display="0"
		target="YPMC" codeType="py"  />
	<item id="WBDM" alias="五笔码" type="string" length="10" display="0" 
		  target="YPMC" codeType="wb" />
	<item id="JXDM" alias="角形码" type="string" length="10" display="0"
		  target="YPMC" codeType="jx" />
	<item id="QTDM" alias="其它码" type="string" length="10" display="0"/>
	<item id="YPDM" alias="药品类型" type="string"  length="10"  display="0" />
	<item id="KWBM" alias="库位编码" type="string" display="0" length="16"/>
	<item id="YKZF" alias="药库作废" type="int" length="1" display="0" not-null="1" defaultValue="0" update="false" />
	<item id="ZFPB" alias="作废判别" display="0" type="int"  defaultValue="0" />
	<item id="ZBLB" alias="账簿类别" type="int" length="2" display="0"/>
</entry>
