<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_RK01" alias="入库01_财务验收_已验收"  sort="c.YSRQ desc">
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string" display="0"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" pkey="true" type="long" display="0"/>
	<item id="RKFS" alias="入库方式" length="4" not-null="1" pkey="true" type="int" display="0"/>
	<item id="RKDH" alias="入库单号" length="6" not-null="1" pkey="true" type="int" display="0"/>
	<item id="PWD" alias="票未到" length="1" not-null="1"  type="int" display="0"/>
	<item id="DWXH" alias="单位序号" length="18" type="long" display="0"/>
	<item id="CWPB" alias="财务判别" length="1" not-null="1" type="int" display="0"/>
	<item id="FDJS" alias="附单据数" length="3" type="int" display="0"/>
	<item id="RKBZ" alias="入库备注" type="string" length="30" display="0"/>
	<item id="RKPB" alias="入库判别" length="1" not-null="1" type="int" display="0"/>
	<item id="CGRQ" alias="采购日期" type="datetime" display="0"/>
	<item id="LRRQ" alias="录入日期" type="datetime" display="0"/>
	<item id="RKRQ" alias="入库日期" type="datetime" display="0"/>
	<item ref="c.YSDH" display="1"/>
	<item ref="c.YSRQ" display="1" type="datetime" width="140"/>
	<item ref="b.DWMC" display="1" alias="供货单位"/>
	<item id="CGGH" alias="采购工号" type="string" length="10" display="0"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" display="0"/>
	<item id="DJFS" alias="定价方式" length="1" not-null="1" type="int" display="0"/>
	<item id="DJGS" alias="定价公式" type="string" length="250" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_JHDW" />
		    <join parent="JGID" child="DWXH"/>
		<relation type="child" entryName="phis.application.sto.schemas.YK_RK02_CWYS_SH" >
			<join parent="XTSB" child="XTSB"/>
			<join parent="RKDH" child="RKDH"/>
			<join parent="RKFS" child="RKFS"/>
		</relation>
	</relations>
</entry>
