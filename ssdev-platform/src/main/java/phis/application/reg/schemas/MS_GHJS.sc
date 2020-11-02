<?xml version="1.0" encoding="UTF-8"?>

<entry tableName="MS_GHJS" alias="挂号结算">
	<item id="JZHM" alias="就诊号码" fixed="true" type="string"/>
	<item id="ZJJE" alias="总计金额" readOnly="true" type="doubel"/>
	<item id="ZHZF" alias="账户支付" length="12" type="double" readOnly="true"/>
	<item id="YBZF" alias="医保支付" length="12" type="double" readOnly="true"/>
	<item id="JJZF" alias="其他应收" length="12" type="double" readOnly="true"/>
	<item id="ZFJE" alias="自负金额" readOnly="true" type="doubel"/>
	<item id="NJJBYLLB" alias="医疗类别" type="string" defaultValue="11" length="18" display="0">
		<dic id="phis.dictionary.ybyllb_mz" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="YBMC" alias="医保病种" type="string" defaultValue="J06.903" iem="false" display="0">
		<dic id="phis.dictionary.ybJbbm" searchField="PYDM" autoLoad="true" />
	</item>
	<item id="YZJM" alias="义诊减免" readOnly="true" type="doubel"/>
	<item id="YHJE" alias="优惠金额" type="double" precision="2" length="12"/>
	<item id="YSK" alias="应收款" readOnly="true" type="doubel"/>
	<item id="FKFS" alias="付款方式" type="int" length="1">
		<dic id="phis.dictionary.outpatientPayCategory" autoLoad="true"/>
	</item>
	<item id="JKJE" alias="交款金额" type="double" precision="2"/>
	<item id="TZJE" alias="退找金额" readOnly="true" type="doubel"/>
	<item id="ewm" alias="扫码支付" type="String"/>
</entry>
