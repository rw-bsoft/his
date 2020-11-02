<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_RK01" alias="未确认入库" sort="a.RKDH" >
	<item id="JGID" alias="机构ID" length="20" display="0" type="string" not-null="1" defaultValue="%user.manageUnit.id"  update="false"/>
	<item id="YFSB" alias="药房识别" length="18" display="0" defaultValue="%user.properties.pharmacyId" not-null="1" type="long" update="false" pkey="true" />
	<item id="CKBH" alias="窗口编号" length="2" display="0" type="int" update="false" not-null="1" defaultValue="0"/>
	<item id="RKFS" alias="入库方式" type="int" length="4" display="2" not-null="1"  pkey="true" >
		<dic id="phis.dictionary.drugStorage" autoLoad="true" ></dic>
	</item>
	<item id="RKRQ" alias="入库日期" type="datetime" display="0" defaultValue="%server.date.datetime" fixed="true" not-null="1"/>
	<item id="RKBZ" alias="入库备注" type="string" length="20" width="250" display="0"/>
	<item id="RKPB" alias="入库判别" length="1" type="int" display="0" not-null="1" defaultValue="0"/>
	<item id="CZGH" alias="操作工号" type="string" display="0" defaultValue="%user.userId"  length="10">
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="RKDH" alias="入库单号" length="8" display="1" not-null="1" generator="assigned" pkey="true"  type="int" width="100"/>
	<item ref="b.PERSONNAME" alias="操作员" queryable="false"/>
	<relations>
	<relation type="child" entryName="phis.application.cic.schemas.SYS_Personnel" >
		<join parent="CZGH" child="PERSONID"></join>
	</relation>
	</relations>
</entry>
