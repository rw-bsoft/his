<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_HZRB" alias="门诊收费汇总">
	<item id="CZGH" alias="操作工号" length="10" not-null="1" type="string" pkey="true"/>
	<item id="JZRQ" alias="结帐日期" type="date" defaultValue="%server.date.datetime" pkey="true"/>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
	<item id="QZPJ" alias="起止票据" length="500" type="string"/>
	<item id="ZJJE" alias="总计金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZPJE" alias="支票金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZFZF" alias="帐户支付" length="12" type="double" precision="2" not-null="1"/>
	<item id="QTYS" alias="其他应收" length="12" type="double" precision="2" not-null="1"/>
	<item id="JJZFJE" alias="门诊统筹" length="12" type="double" precision="2" not-null="1"/>
	<item id="DBZC" alias="大病统筹" length="12" type="double" precision="2" not-null="1"/>
	<item id="TCZC" alias="统筹支付" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZXJZFY" alias="专项救助支付" length="12" type="double" precision="2" not-null="1"/>
	<item id="BCZHZF" alias="个人账户支付" length="12" type="double" precision="2" not-null="1"/>
	<item id="SZYB" alias="省医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="SYB" alias="市医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="YHYB" alias="余杭医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="SMK" alias="市民卡" length="12" type="double" precision="2" not-null="1"/>
	<item id="HBWC" alias="货币误差" length="12" type="double" precision="2" not-null="1"/>
	<item id="HZRQ" alias="汇总日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="FPZS" alias="发票张数" length="8" type="int" not-null="1"/>
	<item id="MZLB" alias="门诊类别" length="18" type="long" not-null="1"/>
	<item id="ZFFP" alias="作废发票" length="255" type="string"/>
	<item id="ZFJE" alias="作废金额" length="12" type="double" precision="2"/>
	<item id="XJJE" alias="现金金额" length="12" type="double" precision="2"/>
	<item id="ZFFSHJ" alias="自费现金金额" length="12" type="double" precision="2"/>
	<item id="NHZFHJ" alias="农合现金金额" length="12" type="double" precision="2"/>
	<item id="YBZFHJ" alias="医保现金金额" length="12" type="double" precision="2"/>
	<item id="NHJZ" alias="农合记账" length="12" type="double" precision="2"/>
	<item id="YBJZ" alias="医保记账" length="12" type="double" precision="2"/>
	<item id="JZJEST" alias="其他记账" length="12" type="double" precision="2"/>
	<item id="JZZE" alias="记账总额" length="12" type="double" precision="2"/>
	<item id="ZFZS" alias="作废张数" length="8" type="int" not-null="1"/>
	<item id="YSQTFB" alias="收费类型汇总" length="1000" type="string"  />
	<item id="WXHJ" alias="微信合计" type="double" length="12" precision="2"/>
	<item id="ZFBHJ" alias="支付宝合计" type="double" length="12" precision="2"/>
	<item id="APPWXHJ" alias="微信合计" type="double" length="12" precision="2"/>
	<item id="APPZFBHJ" alias="支付宝合计" type="double" length="12" precision="2"/>
</entry>
