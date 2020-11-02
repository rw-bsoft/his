<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_YXBDS_BL"  alias="医学表达式" sortinfo="BLBDSBH desc">
	<item id="BLBDSBH" alias="定义表达式编号" length="18" not-null="1" type="long" display="0"  isGenerate="false" generator="assigned" pkey="true"/>
	<item id="ZYMZ" alias="住院门诊使用" length="2" type="int" not-null="1"/>
	<item id="BLBH" alias="病历编号" length="18" type="long" not-null="1"/>
	<item id="DYBDSBH" alias="定义表达式编号" length="18" type="long" not-null="1"/>
	<item id="SYBZ" alias="使用标志" width="100"  type="int" length="1" not-null="1" defaultValue="1" />
	<item id="BDSNR" alias="表达式内容" type="object" display="0"/>
</entry>