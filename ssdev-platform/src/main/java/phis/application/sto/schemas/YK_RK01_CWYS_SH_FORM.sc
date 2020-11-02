<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_RK01" alias="入库01审核" >
	<item id="JGID" alias="机构ID" length="20" not-null="1" display="0" type="string" defaultValue="%user.manageUnit.id"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" pkey="true" type="long" display="0"/>
	<item id="RKFS" alias="入库方式" length="4" not-null="1" pkey="true" type="int" display="0"/>
	<item ref="b.FSMC" fixed="true" />
	<item ref="c.DWMC" mode="remote"  alias="进货单位"/>
	<item id="CGRQ" alias="采购日期" type="datetime" defaultValue="%server.date.datetime" display="2" fixed="true"/>
	<item id="FDJS" alias="附单据数" length="3" type="int" fixed="true"/>
	<item id="PWD" alias="购入方式" length="1" not-null="1" defaultValue="0" type="int" display="2" >
		<dic>
			<item key="0" text="货到票到"/>
			<item key="1" text="货到票未到"/>
			<item key="2" text="票到货未到"/>
		</dic>
	</item>
	<item id="RKBZ" alias="备注" type="string" length="30" display="2" />
	<item id="RKDH" alias="入库单号" length="6" pkey="true" type="int" display="0"/>
	<item id="DWXH" alias="进货单位" length="18" type="long"  not-null="1" display="0"/>
	<item id="CWPB" alias="财务判别" length="1" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="RKPB" alias="入库判别" length="1" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="LRRQ" alias="录入日期" type="datetime" defaultValue="%server.date.datetime" display="0"/>
	<item id="RKRQ" alias="入库日期" type="datetime" defaultValue="%server.date.datetime" display="0"/>
	<item id="CGGH" alias="采购工号" type="string" length="10" display="0"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" display="0"/>
	<item id="DJFS" alias="定价方式" length="1" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="DJGS" alias="定价公式" type="string" length="250" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.sto.schemas.YK_RKFS" />
		    <join parent="RKFS" child="RKFS"></join>
		<relation type="child" entryName="phis.application.mds.schemas.YK_JHDW" >
			<join parent="DWXH" child="DWXH"></join>
		</relation>
	</relations>
</entry>
