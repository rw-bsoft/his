<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_JZXX" alias="收款结帐信息">
	<item id="JZRQ" alias="结帐日期" type="timestamp" not-null="1" generator="assigned" pkey="true"/>
	<item id="CZGH" alias="操作工号" length="10" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="CYSR" alias="出院收入" type="double" length="12" precision="2" not-null="1"/>
	<item id="YJJE" alias="预缴金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="YJXJ" alias="预缴现金" type="double" length="12" precision="2" not-null="1"/>
	<item id="YJZP" alias="预缴支票" type="double" length="12" precision="2" not-null="1"/>
	<item id="TPJE" alias="退票金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="FPZS" alias="发票张数" type="int" length="4" not-null="1"/>
	<item id="SJZS" alias="收据张数" type="int" length="4" not-null="1"/>
	<item id="YSJE" alias="应收金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="YSXJ" alias="应收现金" type="double" length="12" precision="2" not-null="1"/>
	<item id="YSZP" alias="应收支票" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZPZS" alias="支票张数" type="int" length="4" not-null="1"/>
	<item id="TYJJ" alias="退预交金" type="double" length="12" precision="2" not-null="1"/>
	<item id="TJKS" alias="退预缴款张数" type="int" length="4" not-null="1"/>
	<item id="KBJE" alias="空白支票金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="KBZP" alias="空白支票张数" type="int" length="4" not-null="1"/>
	<item id="HZRQ" alias="汇总日期" type="timestamp"/>
	<item id="YJQT" alias="预缴其它" type="double" length="12" precision="2" not-null="1"/>
	<item id="YSQT" alias="应收其它" type="double" length="12" precision="2" not-null="1"/>
	<item id="QTZS" alias="其它票据张数" type="int" length="4" not-null="1"/>
	<item id="SRJE" alias="舍入金额" type="double" length="6" precision="2" not-null="1"/>
	<item id="YJYHK" alias="预缴银行卡" type="double" length="12" precision="2" not-null="1"/>
	<item id="YSYHK" alias="应收银行卡" type="double" length="12" precision="2" not-null="1"/>
	<item id="YSYH" alias="应收优惠" type="double" length="12" precision="2" not-null="1"/>
	<item id="QZPJ" alias="起至票据" length="255"/>
	<item id="QZSJ" alias="起至收据" length="255"/>
	<item id="SZYB" alias="省医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="SYB" alias="市医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="YHYB" alias="余杭医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="SMK" alias="市民卡" length="12" type="double" precision="2" not-null="1"/>
</entry>
