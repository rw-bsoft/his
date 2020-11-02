<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YB_GHXX" alias="医保门诊挂号信息">
	<item id="SBXH" alias="识别序号" length="18" display="0" type="long" />
	<item id="JGID" alias="就诊机构" type="string" length="20" not-null="1" display="0"/>
	<item id="JSSJ" alias="就诊时间" xtype="datetimefield" type="datetime" width="130" defaultValue="%server.date.datetime" />
	<item id="TBR" alias="社会保障号(个人编号)" type="string" length="10" not-null="1" display="0"/>
	<item id="BRXZ" alias="病人性质" type="long" length="4" display="0"/>
	<item id="XM" alias="姓名" type="string" length="20" display="0"/>
	<item id="RYXZ" alias="人员性质" type="string" length="20" display="0"/>
	<item id="KSM" alias="医院科室码" type="string" length="10" display="0"/>
	<item id="KSMC" alias="医院科室名称" type="string" length="30" display="0"/>
	<item id="GHXH" alias="挂号序号" type="string" length="30" display="0"/>
	<item id="DJH" alias="单据号" type="string" length="20" display="0"/>
	<item id="FYLB" alias="费用类别" type="string" length="20" display="0"/>
	<item id="BZM" alias="病种码" type="string" length="10" display="0"/>
	<item id="BZMC" alias="病种名称" type="string" length="40" display="0"/>
	<item id="GHF" alias="挂号费" type="double" length="10" precision="2" display="0"/>
	<item id="ZLF" alias="诊疗费" type="double" length="10" precision="2" display="0"/>
	<item id="GRZL" alias="个人自理" type="double" length="10" precision="2" display="0"/>
	<item id="GRZF" alias="个人自付" type="double" length="10" precision="2" display="0"/>
	<item id="YBZF" alias="医保支付" type="double" length="10" precision="2" display="0"/>
	<item id="ZHZF" alias="个人账户支付" type="double" length="10" precision="2" display="0"/>
	<item id="ZHYE" alias="个人账户余额" type="double" length="10" precision="2" display="0"/>
	<item id="XJZF" alias="现金支付" type="double" length="10" precision="2" display="0"/>
	<item id="XZMC" alias="险种(发票上的人员类别)" type="string" length="20" display="0"/>
	<item id="YH1" alias="优惠1惠民补助" type="double" length="10" precision="2" display="0"/>
	<item id="YH2" alias="优惠2慈善减免" type="double" length="10" precision="2" display="0"/>
	<item id="YH3" alias="优惠3零差率优惠" type="double" length="10" precision="2" display="0"/>
	<item id="TCZF" alias="统筹支付" type="double" length="10" precision="2" display="0"/>
	<item id="DBZF" alias="大病支付" type="double" length="10" precision="2" display="0"/>
	<item id="BZZF" alias="补充支付" type="double" length="10" precision="2" display="0"/>
	<item id="CZGH" alias="操作挂号" type="string" length="10" display="0"/>
	<item id="JZRQ" alias="就诊时间" xtype="datetimefield" type="datetime" width="130" defaultValue="%server.date.datetime" />
	<item id="HZRQ" alias="会诊时间" xtype="datetimefield" type="datetime" width="130" />
	<item id="ZFPB" alias="支付" type="int" length="1" display="0"/>
	<item id="MZBZ" alias="民政补助" type="double" length="10" precision="2" display="0"/>
	<item id="QHMC" alias="区划名称" type="string" length="20" display="0"/>
</entry>
