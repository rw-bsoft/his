<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_RK01" alias="入库01">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" pkey="true" type="long"/>
	<item id="RKFS" alias="入库方式" length="4" not-null="1" pkey="true" type="int"/>
	<item id="RKDH" alias="入库单号" length="6" not-null="1" pkey="true" type="int"/>
	<item id="PWD" alias="票未到" length="1" not-null="1"  type="int"/>
	<item id="DWXH" alias="单位序号" length="18" type="long"/>
	<item id="CWPB" alias="财务判别" length="1" not-null="1" type="int"/>
	<item id="FDJS" alias="附单据数" length="3" type="int"/>
	<item id="RKBZ" alias="入库备注" type="string" length="30"/>
	<item id="RKPB" alias="入库判别" length="1" not-null="1" type="int"/>
	<item id="CGRQ" alias="采购日期" type="datetime"/>
	<item id="LRRQ" alias="录入日期" type="datetime"/>
	<item id="RKRQ" alias="入库日期" type="datetime"/>
	<item id="CGGH" alias="采购工号" type="string" length="10"/>
	<item id="CZGH" alias="操作工号" type="string" length="10"/>
	<item id="DJFS" alias="定价方式" length="1" not-null="1" type="int"/>
	<item id="DJGS" alias="定价公式" type="string" length="250"/>
</entry>
