<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_CK01" alias="出库01" sort="a.CKDH">
	<item id="JGID" alias="机构ID" length="20"  type="string" display="0" not-null="1" defaultValue="%user.manageUnit.id" />
	<item id="YFSB" alias="药房识别" length="18" not-null="1" display="0" defaultValue="%user.properties.pharmacyId" type="long" pkey="true"/>
	<item id="CKBH" alias="窗口编号" length="2" not-null="1" display="0" defaultValue="1" type="int"/>
	<item id="CKFS" alias="出库方式" length="4" not-null="1" display="0" type="int" pkey="true">
		<dic id="phis.dictionary.drugDelivery" autoLoad="true" ></dic>
	</item>
	<item id="TP" alias="" type="string" display="1" renderer="onRenderer" width="23" length="20" virtual="true" />
	<item id="CKDH" alias="出库单号" length="8" not-null="1" pkey="true" type="int" width="100"/>
	<item id="CKRQ" alias="出库日期" type="datetime" defaultValue="%server.date.date"  fixed="true" width="130"/>
	<item id="CKBZ" alias="出库备注" type="string" length="10" width="250" />
	<item id="CKPB" alias="出库判别" length="1" not-null="1" display="0" defaultValue="0"/>
	<!--用户的工号,变量待查-->
	<item id="CZGH" alias="操作工号" type="string" length="10" display="0" defaultValue="%user.userId">
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="SQTJ" alias="申请提交" length="1" display="0" type="int" defaultValue="0"/>
	<item id="LYPB" alias="领用判别" length="1" display="0"  type="int" defaultValue="0"/>
	<item id="LYRQ" alias="领用日期" type="datetime" defaultValue="%server.date.date" display="0"/>
	<item id="LYGH" alias="领用工号" type="string" length="10" display="0"/>
	<item id="KSDM" alias="科室代码" length="18" type="long" display="0" defaultValue="0"/>
	<item id="LGZYH" alias="留观住院号" length="18" not-null="1" display="0" defaultValue="0" type="long"/>
</entry>
