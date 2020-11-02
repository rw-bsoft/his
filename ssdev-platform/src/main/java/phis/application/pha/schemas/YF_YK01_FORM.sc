<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YK01" alias="药房盈亏01" >
	<item id="JGID" alias="机构ID" length="20" not-null="1"  type="string" defaultValue="%user.manageUnit.id" display="0"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0"/>
	<item id="CKBH" alias="窗口编号" length="2" not-null="1" type="int" pkey="true" display="0"/>
	<item id="PDDH" alias="盘点单号" length="8" not-null="1" type="int"  pkey="true" display="0"/>
	<item id="PDRQ" alias="盘点日期" type="datetime" not-null="1" defaultValue="%server.date.date" fixed="true"/>
	<item id="PDWC" alias="盘点状态" length="1" not-null="1" type="int" defaultValue="0" fixed="true">
		<dic>
	<item key="1" text="完成"/>
	<item key="0" text="未完成"/>
	</dic>
	</item>
	<item id="WCRQ" alias="完成日期" type="datetime" fixed="true"/>
	<item id="CZGH" alias="操作员" type="string" length="10" not-null="1" fixed="true">
		<dic id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="HZWC" alias="汇总完成" length="1" type="int" display="0" defaultValue="0"/>
	<item id="BZ" alias="备注" type="string" length="250" display="0"/>
</entry>
