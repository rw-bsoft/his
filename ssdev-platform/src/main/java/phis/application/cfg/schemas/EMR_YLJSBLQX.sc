<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_YLJSBLQX" alias="医疗角色病历权限表" sortinfo="BLLB desc">
  <item id="QXXH" alias="序号" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
  <item id="JSXH" alias="医疗角色序号"  type="long" length="18" not-null="1" display="0"/>
  <item id="BLLB" alias="类别编码" type="long" width="80" length="18" not-null="1"/>
  <item id="LBMC" alias="病历类别" type="String" width="180" length="18" not-null="1"/>
  <item id="SXQX" alias="书写权限" xtype="checkBox" type="int" length="1" not-null="1"/>
  <item id="CKQX" alias="查看权限" xtype="checkBox" type="int" length="1" not-null="1"/>
  <item id="SYQX" alias="审阅权限" xtype="checkBox" type="int" length="1" not-null="1"/>
  <item id="DYQX" alias="打印权限" xtype="checkBox" type="int" length="1" not-null="1"/> 
</entry>