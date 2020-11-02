<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_SCCJ" alias="生产厂家(WL_SCCJ)">
	<item id="CJXH" alias="厂家编号" display="0" type="long" length="12" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="CJMC" alias="厂家名称" type="string" length="60" width="160"/>
	<item id="QYXZ" alias="企业性质" type="int" length="6">
		<dic>
			<item key="1" text="国有企业"/>
			<item key="2" text="集体企业"/>
			<item key="3" text="私有企业"/>
			<item key="4" text="中外合资"/>
		</dic>
	</item>
	<item id="FRDB" alias="法人代表" type="string" length="20" width="100"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10"  display="0"  queryable="true"/>
	<item id="WBDM" alias="五笔代码" type="string" length="10" display="0"/>
	<item id="JXDM" alias="角形代码" type="string" length="10" display="0"/>
	<item id="QTDM" alias="其他代码" type="string" length="10" display="0"/>
	<item id="KHYH" alias="开户银行" type="string" length="90" display="0"/>
	<item id="YHZH" alias="银行帐号" type="string" length="30" display="0"/>
	<item id="LXDZ" alias="联系地址" type="string" length="90" display="0"/>
	<item id="YZBM" alias="邮政编码" type="string" length="6" display="0"/>
	<item id="LXRY" alias="联系人员" type="string" length="20" width="100"/>
	<item id="DHHM" alias="联系电话" type="string" length="20" width="100"/>
	<item id="SJHM" alias="手机号码" type="string" length="20" width="100"/>
	<item id="CZHM" alias="传真号码" type="string" length="20" width="100"/>
	<item id="HLWZ" alias="互联网址" type="string" length="30" width="100"/>
	<item id="DZYJ" alias="电子邮件" type="string" length="30" width="100"/>
	<item id="QYJJ" alias="企业简介" type="string" length="1000" display="0"/>
	<item id="CJZT" alias="厂家状态" type="int" length="1" defaultValue="1">
		<dic>
            <item key="-1" text="注销"/>
            <item key="1" text="正常"/>
        </dic>
	</item>
</entry>
