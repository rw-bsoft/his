<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entry alias="移动支付明细对照" entityName="MOBILEPAY_MXDZ" sort="">
	<item alias="业务类型" id="PAYSERVICE" type="string" defaultValue="2" width="60">
		<dic> 
	      <item key="1" text="门诊挂号"/>
	      <item key="2" text="门诊收费"/>
    	</dic>
	</item>
	<item alias="支付来源" id="PAYSOURCE" type="string" width="60">
		<dic> 
	      <item key="1" text="窗口"/>
	      <item key="2" text="自助机"/>
	      <item key="3" text="App"/>
    	</dic>
	</item>
	<item alias="支付类型" id="PAYTYPE" type="string" width="60">
		<dic> 
	      <item key="1" text="支付宝"/>
	      <item key="2" text="微信"/>
    	</dic>
	</item>
	<item alias="医院流水号" id="HOSPNO" type="string" width="200"/>
	<item alias="交易流水号" id="VERIFYNO" type="string" width="80" display="0"/>
	<item alias="交易时间" id="PAYTIME" type="string" width="130"/>
	<item alias="支付金额" id="PAYMONEY"  type="string"/>
	<item alias="退款标志" id="REFUNDFLAG" type="string" display="0"/>
	<item alias="病人性质" id="PATIENTTYPE" type="string" width="50">
		<dic render="LovCombo"> 
	      <item key="1000" text="自费"/>
	      <item key="2000" text="医保"/>
	      <item key="6000" text="农合"/>
    	</dic>
	</item>
	<item alias="病人id" id="PATIENTID" type="string" display="0"/>
	<item alias="病人姓名" id="NAME" type="string" width="60"/>
	<item alias="性别" id="SEX" type="string" display="0"/>
	<item alias="生日" id="BIRTHDAY" type="string" display="0"/>
	<item alias="身份证号" id="IDCARD" type="string"/>
	<item alias="操作员代码" id="COLLECTFEESCODE" type="string" width="60"/>
	<item alias="操作员姓名" id="COLLECTFEESNAME" type="string" width="60"/>
	<item alias="终端电脑名称" id="COMPUTERNAME" type="string" display="0"/>
	<item alias="终端电脑IP" id="IP" type="string" display="0"/>
	<item alias="发票号码" id="VOUCHERNO" type="string" width="100"/>
	<item alias="订单状态" id="STATUS" type="string" display="0"/>
	<item alias="对账结果" id="DZJG" type="string" width="200" renderer="DzjgRenderer">
		<dic render="LovCombo"> 
	      <item key="1" text="支付平台已支付医院已结算"/>
	      <item key="2" text="支付平台已支付医院已作废"/><!--需要调用退费接口将费用退还给病人-->
	      <item key="3" text="支付平台已支付医院有支付记录无结算记录"/><!--需要调用退费接口将费用退还给病人-->
	      <item key="4" text="支付平台已支付医院无记录"/><!--需要调用退费接口将费用退还给病人-->
	      <item key="5" text="支付平台已退款医院未作废"/><!--需要将HIS端发票进行作废-->
	      <item key="6" text="支付平台已退款医院已作废"/>
	      <item key="7" text="支付平台已退款医院无记录"/>
	      <item key="8" text="支付平台订单交易失败"/>
	      <!--<item key="3" text="农合作废医院多条作废" />
	      <item key="4" text="农合报销医院多条报销" />
	      <item key="5" text="报销金额不匹配" />
	      <item key="6" text="总金额不匹配" />-->
    	</dic>
	</item>
</entry>
