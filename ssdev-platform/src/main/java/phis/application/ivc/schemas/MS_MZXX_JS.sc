<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" display="0" length="18" type="long" generator="assigned" pkey="true" />
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" />
	<item id="ZJJE" alias="总计金额" length="12" type="double" readOnly="true" layout="FPQK"/>
	<item id="ZHZF" alias="账户支付" length="12" type="double" readOnly="true" layout="FPQK" />
	<item id="YBZF" alias="医保支付" length="12" type="double" readOnly="true" layout="FPQK" />
	<item id="JJZF" alias="其他应收" length="12" type="double" readOnly="true" layout="FPQK"/>
	<item id="ZFJE" alias="自负金额" length="12" type="double" readOnly="true" layout="FPQK"/>
	<item id="YSK" alias="应收款" length="12" type="double" readOnly="true" layout="SKJE"/>
	<item id="FPHM" alias="当前发票号" length="20" type="String" fixed="true" layout="SKJE"/>
	<item id="FFFS" alias="付款方式" type="int" length="1" layout="SKJE">
	<dic id="phis.dictionary.outpatientPayCategory" autoLoad="true"/>
	</item>
	<item id="JKJE" alias="缴款" length="12" type="double" layout="SKJE"/>
	<item id="TZJE" alias="退找" length="12" type="double" readOnly="true" layout="SKJE"/>
	<item id="ewm" alias="扫码支付" type="String" layout="SKJE"/>
</entry>
