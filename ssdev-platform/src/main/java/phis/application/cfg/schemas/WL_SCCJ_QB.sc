<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_SCCJ_QB" tableName="WL_SCCJ" alias="生产厂家(WL_SCCJ)">
	<item id="CJXH" alias="厂家编号" display="0" type="long" length="12" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" /> 
	<item id="KFXH" alias="库房序号" display="0" type="int" length="8" layout="JBXX"/> 
	<item id="CJMC" alias="厂家名称" type="string" not-null="true" length="60" colspan="3"  layout="JBXX"/> 
  
	<item id="PYDM" alias="拼音代码" type="string" length="10" layout="JBXX" target="CJMC" codeType="py"/> 
	<item id="WBDM" alias="五笔代码" type="string" length="10" layout="JBXX" target="CJMC" codeType="wb"/> 
	<item id="JXDM" alias="角形代码" type="string" length="10" layout="JBXX" target="CJMC" codeType="jx"/> 
	<item id="QTDM" alias="其他代码" type="string" length="10" layout="JBXX" target="CJMC" /> 
  
	<item id="KHYH" alias="开户银行" type="string" length="45" layout="JBXX" /> 
	<item id="YHZH" alias="银行帐号" type="string" length="30" layout="JBXX" /> 
  
	<item id="QYXZ" alias="企业性质" not-null="true" type="int" length="6" layout="JBXX" >
		<dic id="phis.dictionary.companyProperty"/>
	</item> 
	<item id="FRDB" alias="法人代表" type="string" length="10" layout="JBXX"/> 
	<item id="CJZT" alias="厂家状态" type="int" length="1" fixed="true"  layout="JBXX" defaultValue="1">
		<dic id="phis.dictionary.status"/>
	</item>
  
	<item id="LXRY" alias="联系人员" type="string" length="10" layout="LXXX"/> 
	<item id="DHHM" alias="联系电话" type="string" length="20" layout="LXXX"/> 
	<item id="SJHM" alias="手机号码" type="string" length="20" layout="LXXX"/> 
	<item id="LXDZ" alias="联系地址" type="string" length="45" layout="LXXX" colspan="2"/> 
	<item id="YZBM" alias="邮政编码" type="string" length="6" layout="LXXX"/> 
	<item id="CZHM" alias="传真号码" type="string" length="20" layout="LXXX"/> 
	<item id="HLWZ" alias="互联网址" type="string" length="30" layout="LXXX"/> 
	<item id="DZYJ" alias="电子邮件" type="string" length="30" layout="LXXX" /> 
	<item id="QYJJ" alias="企业简介" type="string" length="500" xtype="textarea" colspan="3" layout="LXXX" width="300"/> 
</entry>
