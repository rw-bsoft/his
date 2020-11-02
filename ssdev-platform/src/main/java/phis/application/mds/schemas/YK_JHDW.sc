<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_JHDW" alias="进货单位" >
	<item id="DWXH" alias="单位序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true" >
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="DWBM" alias="编码" type="string" display="0" length="20" fixed="true" />
	<item id="DWMC" alias="单位名称" type="string" width="200"  length="40" not-null="true" />
	<item id="ZFPB" alias="作废"   display="2" length="1"  xtype="checkbox" defaultValue="0"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" queryable="true" target="DWMC" codeType="py" />
	<item id="LXR" alias="联系人" type="string" display="2" length="10" not-null="true" />
	<item id="LXDH" alias="联系电话" type="string" display="2" length="20" not-null="true" />
	<item id="KHYH" alias="开户银行" type="string" display="2" length="20" not-null="true" />
	<item id="DWZH" alias="帐号" type="string" colspan="3" display="2" length="40" not-null="true" />
	<item id="DWDZ" alias="地址" type="string" colspan="3" width="200" length="40" not-null="true" />
	<item id="BZXX" alias="说明" type="string" colspan="3" display="2" xtype="textarea" length="200" />
</entry>
