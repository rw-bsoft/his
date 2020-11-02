<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entry alias="农合报销明细对照" entityName="NH_BSOFT_MXDZ" sort="">
	<item alias="报销编码" id="BXID" length="30" not-null="true" pkey="true" type="string"/>
	<item alias="总计金额" id="ZJJE"  type="string"/>
	<item alias="农合卡号" id="ICKH" type="string" width="150"/>
	<item alias="交易类型" id="JYLX" type="string">
		<dic> 
	      <item key="1" text="正常"/>  
	      <item key="-1" text="作废"/>
    	</dic>
	</item>
	<item alias="实报金额" id="SBJE" type="string"/>
	<item alias="交易时间" id="JYSJ" type="string"/>
	<item alias="交易流水号" id="DJLSH" type="string"/>
	<item alias="发票号码" id="FPHM" type="string" width="100"/>
	<item alias="对账结果" id="DZJG" type="string" width="200">
		<dic render="LovCombo"> 
	      <item key="1" text="农合作废医院没作废"/>  
	      <item key="2" text="农合报销医院无记录"/>
	      <item key="3" text="农合作废医院多条作废" />
	      <item key="4" text="农合报销医院多条报销" />
	      <item key="5" text="报销金额不匹配" />
	      <item key="6" text="总金额不匹配" />
    	</dic>
	</item>
</entry>
