<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_RK01" alias="未确认入库">
	<item id="JGID" alias="机构ID" length="20" type="string"   display="0" not-null="1" defaultValue="%user.manageUnit.id"  update="false"/>
	<item id="YFSB" alias="药房识别" length="18" display="0" defaultValue="%user.properties.pharmacyId" not-null="1" type="long" update="false" pkey="true"/>
	<item id="CKBH" alias="窗口编号" length="2" display="0" type="int" update="false" not-null="1" defaultValue="0"/>
	<item id="RKFS" alias="入库方式" type="int" length="4" display="2" not-null="1" fixed="true"  pkey="true">
		<dic id="phis.dictionary.drugStorage" autoLoad="true"></dic>
	</item>
	<item id="RKDH" alias="入库单号" length="8" display="1" not-null="1" generator="assigned" pkey="true"  type="int" >
	</item>
	<item id="RKRQ" alias="入库日期" type="datetime" display="2" defaultValue="%server.date.datetime" fixed="true" not-null="1"/>
	<item id="RKBZ" alias="入库备注" type="string" length="20"/>
	<item id="RKPB" alias="入库判别" length="1" type="int" display="0" not-null="1" defaultValue="0"/>
	<item id="CZGH" alias="操作员" type="string"  defaultValue="%user.userId"  length="10" fixed="true">
	<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
</entry>
