<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLSJRZ" alias="病历日志表(EMR_BLRZ)">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="CZGH" alias="操作者" length="10" not-null="1">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="RZNR" alias="日志内容" length="255" not-null="1" width="300"/>
	<item id="SJXM" alias="审计项目" type="String" length="4" not-null="1" width="180">
		<dic id="phis.dictionary.review"/>
	</item>
	<item id="XTSJ" alias="系统时间" type="date" not-null="1" width="180"/>
	<item id="YWID1" alias="业务ID1" type="long" length="18" not-null="1" display="0"/>
	<item id="YWID2" alias="业务ID2" type="long" length="18" not-null="1" display="0"/>
	<item id="YEID3" alias="业务ID3" type="long" length="18" not-null="1" display="0"/>
	<item id="CZGH" alias="操作工号" length="10" not-null="1">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="IPDZ" alias="IP地址" length="20" not-null="1" width="180" display="0"/>
	<item id="JSJM" alias="计算机名" length="50"  not-null="1" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
</entry>
