<?xml version="1.0" encoding="UTF-8"?>

<entry alias="医保住院结算" entityName="YB_ZYJS" >
	<!--其他的键根据各地医保返回值添加-->
	<!--<item id="SBXH" alias="识别序号" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="18"
				startPos="1" />
		</key>
	</item>-->
	<item id="JGID" alias="机构ID" type="string" length="20" />
	<item id="ZYH" alias="ZY_BRRY主键关联" type="long" length="18" />
	<!--<item id="ZFPB" alias="作废判别,1是作废" type="int" length="1" />-->
	<item id="JSCS" alias="结算次数" type="int" length="3" />
	<item id="TBR" alias="社会保障号" type="string" length="10" />
	<item id="XM" alias="姓名" type="string" length="20" />
	<item id="RYXZ" alias="人员性质" type="string" length="20" />
	<item id="XH" alias="住院序号" type="string" length="20" />
	<item id="CYSJ" alias="出院时间" xtype="datetimefield" type="datetime"/>
	<item id="ZFY" alias="医疗费用合计" type="double" length="10" precision="2" display="0"/>
	<item id="YF" alias="药费合计" type="double" length="10" precision="2" display="0"/>
	<item id="XMF" alias="治疗项目费合计" type="double" length="10" precision="2" display="0"/>
	<item id="GRZL" alias="个人自理" type="double" length="10" precision="2" display="0"/>
	<item id="GRZF" alias="个人自付" type="double" length="10" precision="2" display="0"/>
	<item id="YBZF" alias="医保支付" type="double" length="10" precision="2" display="0"/>
	<item id="ZHZF" alias="个人账户支付" type="double" length="10" precision="2" display="0"/>
	<item id="ZHYE" alias="个人账户余额" type="double" length="10" precision="2" display="0"/>
	<item id="XJZF" alias="现金支付" type="double" length="10" precision="2" display="0"/>
	<item id="XZMC" alias="险种" type="string" length="10" />
	<item id="DJH" alias="单据号" type="string" length="20" />
	<item id="YSM" alias="管床医生码" type="string" length="20" />
	<item id="YH1" alias="优惠1" type="double" length="10" precision="2" display="0"/>
	<item id="YH2" alias="优惠2" type="double" length="10" precision="2" display="0"/>
	<item id="YH3" alias="优惠3" type="double" length="10" precision="2" display="0"/>
	<item id="TCZF" alias="统筹支付" type="double" length="10" precision="2" display="0"/>
	<item id="DBZF" alias="大病支付" type="double" length="10" precision="2" display="0"/>
	<item id="BZZF" alias="补充支付" type="double" length="10" precision="2" display="0"/>
	<item id="YBJE" alias="医保金额" type="double" length="10" precision="2" display="0"/>
	<item id="QCZH" alias="期初账户" type="double" length="10" precision="2" display="0"/>
	<item id="XZFS" alias="选择方式" type="int" length="1"/>
	<item id="MZBZ" alias="民政补助" type="double" length="7" precision="2" display="0"/>
	<item id="QHMC" alias="区划名称" type="string" length="20" />
</entry>
