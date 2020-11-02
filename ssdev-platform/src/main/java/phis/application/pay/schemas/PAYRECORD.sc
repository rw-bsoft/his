<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PAYRECORD" alias="移动支付交易记录表">
	<item id="ID" alias="流水号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
	<item id="PAYSERVICE" alias="业务类型" length="2" type="string"/>
	<item id="IP" alias="支付终端IP" length="20" type="string"/>
	<item id="ORGANIZATIONCODE" alias="机构编码" length="50" type="string"/>
	<item id="COMPUTERNAME" alias="支付终端计算机名" length="50" type="string"/>
	<item id="HOSPNO" alias="医院流水号" length="50" type="string"/>
	<item id="PAYMONEY" alias="支付金额" length="12" type="double" precision="2"/>
	<item id="VOUCHERNO" alias="就诊号码或发票号码（业务凭证号）" type="string" length="50"/>
	<item id="PATIENTYPE" alias="病人性质" length="4" type="string"/>
	<item id="PATIENTID" alias="病人ID" length="50" type="string"/>
	<item id="NAME" alias="姓名" length="50" type="string"/>
	<item id="SEX" alias="性别" length="1"  type="string"/>
	<item id="IDCARD" alias="身份证号码" length="18" type="string"/>
	<item id="BIRTHDAY" alias="出生年月" type="date"/>	
	<item id="PAYTIME" alias="交易时间" type="date" defaultValue="%server.date.datetime"/>
	<item id="VERIFYNO" alias="对账流水号（支付宝、微信等交易号）" type="string" length="50"/>
	<item id="BANKTYPE" alias="银行卡类型" length="2" type="string"/>
	<item id="BANKCODE" alias="银行代码" type="string" length="50"/>
	<item id="BANKNO" alias="银行卡号" type="string" length="50"/>
	<item id="PAYTYPE" alias="支付类型" length="2"  type="string"/>
	<item id="AUTH_CODE" alias="支付条码" type="string" length="50"/>	
	<item id="PAYSOURCE" alias="支付来源" length="1" type="string"/>
	<item id="TERMINALNO" alias="支付终端号" length="50" type="string"/>
	<item id="PAYNO" alias="支付账号" length="50" type="string"/>
	<item id="COLLECTFEESCODE" alias="操作员代码" length="50" type="string"/>
	<item id="COLLECTFEESNAME" alias="操作员姓名" length="50" type="string"/>
	<item id="CARDTYPE" alias="卡类型" length="1"  type="string"/>
	<item id="CARDNO" alias="卡号" length="50" type="string"/>	
	<item id="STATUS" alias="状态码" length="1"  type="string"/>
	<item id="SENDXML" alias="请求报文" type="object"/>
	<item id="RETNRNXML" alias="返回报文" type="object"/>
	<item id="RETURN_CODE" alias="返回状态码" length="16" type="string"/>
	<item id="RETURN_MSG" alias="返回信息" length="128" type="string"/>	
	<item id="TRADENO" alias="支付宝微信返回的交易号" length="100" type="string"/>
	<item id="TKBZ" alias="退款标志" length="1"  type="string"/>	
	<item id="REFUND_FEE" alias="已退金额" length="12" type="double" precision="2"/>
	<item id="HOSPNO_ORG" alias="退款交易时指向原HOSPNO" length="50" type="string"/>
</entry>
