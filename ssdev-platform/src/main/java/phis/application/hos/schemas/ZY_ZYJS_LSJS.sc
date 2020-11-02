<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_ZYJS" alias="住院结算表">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1" display="0" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="FPHM" alias="发票号码" length="20" renderer="zfpbRender"/>
	<item id="JSLX" alias="类型" type="int" length="2" width="100">
		<dic id="phis.dictionary.JSLXDic" autoLoad="true"/>
	</item>
	<item ref="b.BRXZ" alias="病人性质" type="long"/>
	<item id="JSXZ" alias="结算性质" type="int" length="4">
		<dic>
			<item key="1000" text="全自费"/>
			<item key="2000" text="医保"/>
		</dic>
	</item>
  <item id="FKFS" alias="付款方式" length="2" type="int">
		<dic id="phis.dictionary.hospitalSettlePayCategory" autoLoad="true"/>
	</item>
	<item id="JSRQ" alias="结算日期" type="timestamp" width="130"/>
	<item ref="b.ZYHM" alias="住院号码" type="string"/>
	<item ref="b.BRXM" alias="姓名" type="string"/>
	<item ref="b.BRKS" alias="科室" type="string"/>
	<item id="FYHJ" alias="总费用" type="double" length="10" precision="2" not-null="1"/>
	<item id="ZFHJ" alias="自负" type="double" length="10" precision="2" not-null="1"/>
	<item id="JKHJ" alias="缴款" type="double" length="10" precision="2" not-null="1"/>
	<item id="JSJK" alias="结算金额" type="double" length="10" precision="2" not-null="1"/>
	<item id="KSRQ" alias="开始日期" type="timestamp" display="0"/>
	<item id="CZGH" alias="工号" length="10">
		<dic id="phis.dictionary.user_YGBH" autoLoad="true"/>
	</item>
	<item id="ZFPB" alias="备注" type="int" length="1" not-null="1">
		<dic autoLoad="true">
			<item key="0" text=""/>
			<item key="1" text="作废"/>
		</dic>
	</item>
	<item id="ZFRQ" alias="作废时间" type="timestamp" width="130"/>
	<item id="ZFGH" alias="作废人" length="10">
		<dic id="phis.dictionary.user" autoLoad="true"/>
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"/>
		</relation>
	</relations>
</entry>
