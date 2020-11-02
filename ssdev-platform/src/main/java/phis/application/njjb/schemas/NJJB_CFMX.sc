<?xml version="1.0" encoding="UTF-8"?>
<entry alias="医保返回处方信息" entityName="NJJB_CFMX" sort="">
<item alias="主键" id="CFMXID" length="18" not-null="1" type="long" generator="assigned" pkey="true">
	<key>
		<rule name="increaseId" type="increase" length="18" startPos="1" />
	</key>
</item>
<item alias="处方号" id="LSH" length="20" type="string"/>
<item alias="处方流水号" id="CFLSH" length="30" type="string"/>
<item alias="处方日期" id="CFRQ" length="14" type="string"/>
<item alias="医院收费项目自编码" id="ZBM" length="20" type="string"/>
<item alias="金额" id="ZE" length="16" type="string"/>
<item alias="自付金额" id="ZFJE" length="16" type="string"/>
<item alias="自理金额" id="ZLJE" length="16" type="string"/>
<item alias="自付比例" id="ZFBL" length="5" type="string"/>
<item alias="支付上限" id="ZFSX" length="16" type="string"/>
<item alias="收费项目等级" id="SFXMDJ" length="3" type="string"/>
<item alias="说明信息" id="SMXX" length="1000" type="string"/>
<item alias="备用2" id="BY2" length="100" type="string"/>
<item alias="备用3" id="BY3" length="100" type="string"/>
<item alias="备用4" id="BY4" length="200" type="string"/>
<item alias="备用5" id="BY5" length="200" type="string"/>
<item alias="备用6" id="BY6" length="4000" type="string"/>
</entry>
