<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PD01" alias="药库盘点" sort="a.PDDH desc">
	<item id="JGID" alias="机构ID" length="20" type="string" display="0"/>
	<item id="XTSB" alias="药库识别" length="18" type="long" pkey="true" display="0"/>
	<item id="PDDH" alias="盘点单号" length="12" type="string" pkey="true"/>
	<item id="PDRQ" alias="盘点日期" type="datetime" width="140"/>
	<item id="CZGH" alias="盘点人" length="10" type="string">
		<dic id="phis.dictionary.user"/>
	</item>
	<item id="PDZT" alias="状态" virtual="true" type="string" renderer="onRenderer"/>
	<item id="PDPB" alias="状态" length="1" type="int" defaultValue="0" display="0">
		<!--<dic>
					<item key="0" text="盘点中"></item>
					<item key="1" text="已完成"></item>
				</dic>-->
		</item>
		<item id="BZXX" alias="备注信息" length="100" type="string" display="0"/>
		<item id="YPLB" alias="药品类别" length="16" type="string" display="0"/>
		<item id="KWLB" alias="库位类别" length="16" type="string" display="0"/>
		<item id="YSGH" alias="验收工号" length="10" type="string" display="0"/>
		<item id="ZXRQ" alias="执行日期" type="datetime" display="0"/>
		<item id="CKDH" alias="出库单号" length="6" type="int" display="0"/>
		<item id="RKDH" alias="入库单号" length="6" type="int" display="0"/>
</entry>
