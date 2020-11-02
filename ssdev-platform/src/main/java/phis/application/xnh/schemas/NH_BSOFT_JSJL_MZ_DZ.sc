<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entry alias="农合结算记录-对账" entityName="NH_BSOFT_JSJL" sort="">
<item alias="报销编码" id="BXID" length="30" hidden="true" not-null="true" pkey="true" type="string"/>
<item alias="门诊序号" id="MZXH"  type="long" hidden="true"/>
<item alias="机构编码" id="JGID"  type="string" hidden="true" />
<item alias="就诊类别" id="JZLB" type="string" hidden="true" />
<item alias="结算日期" id="JSRQ" type="date"/>
<item ref="b.BRXM" alias="病人姓名" fixed="true" type="string" length="40" />
<item alias="费用总额" id="SUM31" type="double"/>
<item alias="登记流水号" id="DJID" length="20" type="string" hidden="true" />
<item alias="备注" id="BZ" length="255" width="200" type="string"/>
<item alias="作废日期" id="ZFRQ" type="date"/>
<item alias="作废判别" id="ZFPB" not-null="true" type="double" hidden="true"/>
<item alias="农合卡号" id="ICKH" length="20" type="string" hidden="true"/>
<item alias="本次门诊补偿费用" id="SUM01" type="double"/>
<item alias="本次列入可补偿范围医疗费用" id="SUM04" type="double"/>
<item alias="本次二次补偿金额" id="SUM05" type="double"/>
<item alias="本年补偿累计金额" id="SUM06" type="double"/>
<item alias="本年住院累计列入可补偿范围医疗费用" id="SUM07" type="double"/>
<item alias="本年累计补偿次数" id="SUM08" type="double"/>
<item alias="妇幼保健补偿" id="SUM09" type="double"/>
<item alias="优抚补偿金额" id="SUM10" type="double"/>
<item alias="本次门诊医疗救助补偿金额" id="SUM11" type="double"/>
<item alias="本年累计门诊医疗救助补偿金额" id="SUM13" type="double"/>
<item alias="本年累计住院医疗救助补偿金额" id="SUM14" type="double"/>
<item alias="" id="SUM15" type="double"/>
<item alias="" id="SUM16" type="double"/>
<item alias="" id="SUM17" type="double"/>
<item alias="病种核定价格" id="SUM18" type="double"/>
<item alias="实际医疗费用" id="SUM19" type="double"/>
<item alias="合作医疗基金负担金额" id="SUM20" type="double"/>
<item alias="个人负担金额" id="SUM21" type="double"/>
<item alias="医院盈利" id="SUM22" type="double"/>
<item alias="单独结算按比例补偿部分" id="SUM23" type="double"/>
<item alias="单独结算按比例补偿部分" id="SUM24" type="double"/>
<item alias="单独结算按比例补偿部分" id="SUM25" type="double"/>
<item alias="单独结算自费部分" id="SUM26" type="double"/>
<item alias="单独结算自费部分" id="SUM27" type="double"/>
<item alias="个人自交" id="SUM32" type="double"/>
<item alias="累积费用总额 " id="SUM33" type="double"/>
<relations>
	<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA">
		<join parent="NHKH" child="ICKH" />
	</relation>
</relations>
</entry>
