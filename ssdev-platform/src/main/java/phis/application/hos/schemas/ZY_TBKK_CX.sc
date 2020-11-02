<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_TBKK" sort="a.JKXH desc" alias="退补缴款">
	<item id="JKXH" alias="缴款序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" not-null="1"/>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0" not-null="1"/>
	<item ref="b.ZYHM" />
	<item ref="b.BRXM" alias="姓名"/>
	<item ref="b.BRCH" alias="床号"/>
	<item ref="b.BRKS" alias="科室"/>
	<item id="JKRQ" alias="缴款日期" type="date" width="130" not-null="1"/>
	<item id="JKJE" alias="缴款金额" type="double" length="10" precision="2" not-null="1"/>
	<item id="JKFS" alias="缴款方式" type="int" length="6" not-null="1">
		<dic id="phis.dictionary.payment"/>
	</item>
	<item id="SJHM" alias="收据号码" length="20" not-null="1"/>
	<item id="ZPHM" alias="票(卡)号码" length="20"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" display="0" not-null="1"/>
	<item id="CZGH" alias="收款员" length="10">
		<dic id="phis.dictionary.user"/>
	</item>
	<item id="JZRQ" alias="结帐日期" type="date" display="0"/>
	<item id="HZRQ" alias="汇总日期" type="date" display="0"/>
	<item id="ZFRQ" alias="作废日期" type="date" display="0"/>
	<item id="ZFGH" alias="作废工号" length="10" display="0"/>
	<item id="ZFPB" alias="备注" type="int" length="1" not-null="1" renderer="zfpbRender">
		<dic>
			<item key="1" text="作废" style="color:red"/>
		</dic>
	</item>
	<item id="ZCPB" alias="转存判别" type="int" length="1" display="0" not-null="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY" />
	</relations>
</entry>
