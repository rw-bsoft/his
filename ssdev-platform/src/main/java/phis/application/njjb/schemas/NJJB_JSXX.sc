<?xml version="1.0" encoding="UTF-8"?>
<entry alias="南京金保-结算信息" entityName="NJJB_JSXX" sort="">
<item alias="结算主键" id="JSXXID" not-null="true" type="long"  generator="assigned" pkey="true">
	<key>
		<rule name="increaseId"  type="increase" length="18" startPos="1" />
	</key>
</item>
<item alias="流水号" id="LSH" type="long"/>
<item alias="发票号码" id="FPHM" length="18" type="string"/>
<item alias="机构名称" id="JGID" length="18" type="string"/>
<item alias="挂号序号" id="GHXH" type="long"/>
<item alias="门诊序号" id="MZXH" type="long"/>
<item alias="住院号" id="ZYH" type="long"/>
<item alias="结算时间" id="JSSJ" type="date"/>
<item alias="作废判别" id="ZFPB" type="double"/>

<item alias="本次医疗费总额" id="BCYLFZE" length="16" type="string"/>
<item alias="本次统筹支付金额" id="BCTCZFJE" length="16" type="string"/>
<item alias="本次大病救助支付" id="BCDBJZZF" length="16" type="string"/>
<item alias="本次大病保险支付" id="BCDBBXZF" length="16" type="string"/>
<item alias="本次民政补助支付" id="BCMZBZZF" length="16" type="string"/>
<item alias="本次帐户支付总额" id="BCZHZFZE" length="16" type="string"/>
<item alias="本次现金支付总额" id="BCXZZFZE" length="16" type="string"/>
<item alias="本次帐户支付自付" id="BCZHZFZF" length="16" type="string"/>
<item alias="本次帐户支付自理" id="BCZHZFZL" length="16" type="string"/>
<item alias="本次现金支付自付" id="BCXJZFZF" length="16" type="string"/>
<item alias="本次现金支付自理" id="BCXJZFZL" length="16" type="string"/>
<item alias="医保范围内费用" id="YBFWNFY" length="16" type="string"/>
<item alias="帐户消费后余额" id="ZHXFHYE" length="16" type="string"/>
<item alias="单病种病种编码" id="DBZBZBM" length="20" type="string"/>
<item alias="说明信息" id="SMXX" length="1000" type="string"/>
<item alias="药费合计" id="YFHJ" length="16" type="string"/>
<item alias="诊疗项目费合计" id="ZLXMFHJ" length="16" type="string"/>
<item alias="补保支付" id="BBZF" length="16" type="string"/>
<item alias="医疗类别" id="YLLB" length="3" type="string"/>
<item alias="备用6" id="BY6" length="4000" type="string"/>
<item alias="交易流水号" id="JYLSH" length="100" type="string"/>
</entry>
