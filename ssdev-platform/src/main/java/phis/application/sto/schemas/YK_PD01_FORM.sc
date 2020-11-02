<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PD01" alias="药库盘点"  >
	<item id="JGID" alias="机构ID" length="20" type="string" display="0"/>
	<item id="XTSB" alias="药库识别" length="18" type="long" pkey="true" display="0"/>
	<item id="PDDH" alias="盘点单号" length="12" type="string" pkey="true" display="0"/>
	<item id="CZGH" alias="盘点人" length="10" type="string" defaultValue="%user.userId" fixed="true" >
		<dic id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="PDRQ" alias="盘点日期" type="datetime" />
	<item id="BZXX" alias="备注信息" length="100" display="0"  type="string" />
	<item id="YPLB" alias="药品类别" length="16"  type="string" defaultValue="1"  fixed="true">
		<dic>
			<item key="1" text="全部"/>
		</dic>
	</item>
	<item id="KWLB" alias="库位类别" length="16"  type="string" display="0" defaultValue="1"  fixed="true">
		<dic>
			<item key="1" text="全部"/>
		</dic>
	</item>
	<item id="PDPB" alias="状态" length="1" type="int" display="0" defaultValue="0"/>
	<item id="YSGH" alias="验收工号" length="10" type="string" display="0"/>
	<item id="ZXRQ" alias="执行日期" type="datetime" display="0"/>
	<item id="CKDH" alias="出库单号" length="6" type="int" display="0"/>
	<item id="RKDH" alias="入库单号" length="6" type="int" display="0"/>
</entry>
