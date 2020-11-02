<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="phis.application.ccl.schemas.YJ_JCSQ_KD01" alias="检查申请-开单01"> 
	<item id="SQDH" alias="申请单号"  length="12" type="long" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="YLLB" alias="医疗类别" type="int" length="2" not-null="1"  width="80"/>
	<item id="SSLX" alias="所属类型" type="int" length="2" not-null="1" width="80"/>
	<item id="ZSXX" alias="主诉信息" type="string" length="200"/>
	<item id="XBS" alias="现病史" type="string" length="200"/>
	<item id="JWS" alias="既往史" type="string" length="200"/>
	<item id="GMS" alias="过敏史" type="string" length="200"/>
	<item id="CTXX" alias="查体信息" type="string" length="500"/>
	<item id="FZJC" alias="辅助检查" type="string" length="200"/>
	<item id="TGJC" alias="体格检查" type="string" length="200"/>
	<item id="BZXX" alias="备注信息" type="string" length="200"/>
	<item id="XJ" alias="心界"  length="20" type="string" />
	<item id="XL" alias="心率"  length="20" type="string" />
	<item id="XY" alias="心音"  length="20" type="string" />
	<item id="XLV" alias="心律"  length="20" type="string" />
	<!--<item id="XBD" alias="心搏动"  length="20" type="string" />
	<item id="XZY" alias="心杂音"  length="20" type="string" />-->
	<item id="XLSJ" alias="心力衰竭"  length="20" type="string" />
	<item id="XGJC" alias="X光检查"  length="20" type="string" />
	<item id="DJZT" alias="登记状态"  length="2" type="int" />
</entry>