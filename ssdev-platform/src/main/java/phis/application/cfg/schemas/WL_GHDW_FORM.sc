<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_GHDW_FORM" tableName="WL_GHDW" alias="供货单位" sort="DWXH">
    <item id="DWXH" alias="单位序号" type="long" length="12" not-null="1" generator="assigned" pkey="true" display="0" layout="JBXX">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    <item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id" layout="JBXX"/>
    <item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
    <item id="DWMC" alias="单位名称" length="60" not-null="1" layout="JBXX" colspan="2" width="220"/>
    <item id="QYXZ" alias="企业性质" type="int" length="6" layout="JBXX">
        <dic>
            <item key="1" text="国有企业"/>
            <item key="2" text="集体企业"/>
            <item key="3" text="私有企业"/>
            <item key="4" text="中外合资"/>
        </dic>
    </item>
    <item id="DWLX" alias="单位类型" type="int" length="1" display="0">
        <dic>
            <item key="0" text="供应商"/>
            <item key="1" text="维修单位"/>
            <item key="2" text="计量鉴定单位"/>
        </dic>
    </item>
    <item id="FRDB" alias="法人代表" length="10" layout="JBXX"/>
    <item id="PYDM" alias="拼音代码" length="10" display="2" target="DWMC" codeType="py" layout="JBXX" fixed="true"/>
    <item id="WBDM" alias="五笔代码" length="10" display="2" target="DWMC" codeType="wb" layout="JBXX" fixed="true"/>
    <item id="JXDM" alias="角形代码" length="10" display="2" target="DWMC" codeType="jx" layout="JBXX" fixed="true"/>
    <item id="QTDM" alias="其他代码" length="10" display="2" layout="JBXX" />
    <item id="KHYH" alias="开户银行" length="45" display="2" layout="JBXX" />
    <item id="YHZH" alias="银行帐号" length="30" display="2" layout="JBXX" />
    <item id="LXDZ" alias="联系地址" length="45" display="2" layout="LXFS" colspan="2"/>
    <item id="YZBM" alias="邮政编码" length="6" display="2" layout="LXFS"/>
    <item id="LXRY" alias="联系人员" length="10" layout="LXFS"/>
    <item id="DHHM" alias="联系电话" length="20" layout="LXFS"/>
    <item id="SJHM" alias="手机号码" length="20" layout="LXFS"/>
    <item id="CZHM" alias="传真号码" length="20" layout="LXFS"/>
    <item id="HLWZ" alias="互联网址" length="30" display="2" layout="LXFS"/>
    <item id="DZYJ" alias="电子邮件" length="30" layout="LXFS"/>
    <item id="QYJJ" alias="企业简介" length="500" xtype="textarea" colspan="3" layout="LXFS" width="300"/>
    <item id="XKZH" alias="经营许可证" length="40" display="0" layout="JBXX" />
    <item id="XKXQ" alias="许可证效期" type="date" display="0" layout="JBXX"/>
    <item id="YYZZ" alias="营业执照" length="40" display="0" layout="JBXX"/>
    <item id="YYZZXQ" alias="营业执照效期" type="date" display="0" layout="JBXX" />
    <item id="CWBM" alias="财务编码" length="20" display="0"/>
    <item id="DWZT" alias="单位状态" type="int" length="1" defaultValue="1" fixed="true" layout="JBXX" > 
        <dic>
            <item key="-1" text="注销"/>
            <item key="1" text="正常"/>
        </dic>
    </item>
</entry>
