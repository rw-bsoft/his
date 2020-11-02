<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_ZYJS" alias="费用清单">
	<item id="JSLX" alias="结算类型" fixed="true" layout="JBXX">
		<dic autoLoad="true">
			<item key="1" text="中途结算"/>
			<item key="4" text="出院终结"/>
			<item key="5" text="正常结算"/>
			<item key="10" text="取消结算"/>
		</dic>
	</item>
	<item id="ZYHM" alias="住院号" fixed="true" layout="JBXX"/>
	<item id="BRXM" alias="姓名" fixed="true" layout="JBXX"/>
	<item id="BRXZ" alias="性质" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="FPHM" alias="No." fixed="true"/>
	<item id="FYHJ" alias="总费用" length="10" readOnly="true" layout="JSJE"/>
	<item id="TCJE" alias="统筹金额" length="10" readOnly="true" layout="JSJE"/>
	<item id="ZFHJ" alias="自负" length="10" readOnly="true" layout="JSJE"/>
	<item id="YBZF" alias="医保支付" length="10" readOnly="true" layout="JSJE"/>	
	<item id="ZHZF" alias="账户支付" length="10" readOnly="true" layout="JSJE"/>
	<item id="JKHJ" alias="预缴款" length="10" readOnly="true" layout="JSJE"/>
	<item id="FKFS" alias="付款方式" type="int" length="18" layout="JSJEBT">
	<dic id="phis.dictionary.hospitalSettlePayCategory" autoLoad="true"/>
	</item>
	<item id="YSJE" alias="应收" length="10" readOnly="true" layout="JSJEBT"/>	
	<item id="JKJE" alias="缴款" type="double" length="10" precision="2" layout="JSJEBT"/>
	<item id="TZJE" alias="退找" length="10" readOnly="true" layout="JSJEBT"/>
	<item id="ewm" alias="扫码支付" type="String" layout="JSJEBT"/>
</entry>
