<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JDXX_DETAILFORM" tableName="WL_JDXX" alias="检定信息(WL_JDXX)">
	<item id="JDXH" alias="检定序号" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" length="8"  type="int" display="0"/>
	<item id="JLXH" alias="计量序号" length="18" type="long" display="0"/>
	<item id="JDRQ" alias="检定日期" type="date" defaultValue="%server.date.date"/>
	<item id="JDDWMC" alias="检定单位名称" length="30" display="0" />
	<item id="JDDWDM" alias="检定单位代码" length="10" >
		<dic id="phis.dictionary.supplyUnit" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="JDR" alias="检定人" length="20" defaultValue="%user.userId" >
		<dic id="phis.dictionary.wzdoctor_yjqx" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="JDJG" alias="检定结果" length="1" type="int" display="0"/>
	<item id="HGZH" alias="合格证号" length="20" display="0"/>
	<item id="DJRQ" alias="登记日期" type="date" defaultValue="%server.date.date"/>
	<item id="DJGH" alias="登记人" length="10" defaultValue="%user.userName"/>
	<item id="ZFBZ" alias="作废标志" length="1" type="int" display="0" defaultValue="0"/>
	<item id="BZXX" alias="备注信息" length="50" />
	<item id="JDJL" alias="检定结论" length="1" type="int" display="0"/>
</entry>
