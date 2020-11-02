<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" display="0" length="18" type="long" generator="assigned" pkey="true" />
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" />
	<item id="TFJE" alias="退现金" length="12" type="double" readOnly="true" layout="FPQK"/>
	<item id="ZHTF" alias="退账户" length="12" type="double" readOnly="true" layout="FPQK" />
	<item id="YSK" alias="应收款" length="12" type="double" readOnly="true" layout="SKJE"/>
	<item id="FKMC" alias="付款方式" type="string" readOnly="true" layout="SKJE"/>
	<item id="JKJE" alias="交款" length="12" type="double" readOnly="true" layout="SKJE"/>
	<item id="TZJE" alias="退找" length="12" type="double" readOnly="true" layout="SKJE"/>
</entry>
