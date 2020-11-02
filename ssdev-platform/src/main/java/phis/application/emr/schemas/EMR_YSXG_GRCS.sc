<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_YSXG_GRCS" alias="个人参数设置">
  <item id="DZXH" alias="定制序号" length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
  <item id="YHBH" alias="用户编号" type="string" length="30" display="0"/>
  <item id="TZYS" alias="书写病历时自动跳转到下一个元素" type="string" length="1" group="病历书写" defaultValue="0">
  	<dic>
  		<item key="0" text="否" />
		<item key="1" text="是" />
  	</dic>
  </item>
  <item id="BCJG" alias="自动保存病历文档的恢复文件的间隔时间(0表示不需要)(分钟)" length="3" defaultValue="10" type="int" maxValue="999" group="病历书写" xtype="spinner"/>
  <item id="XSBL" alias="病历文档的显示比例(%)" length="10" group="病历书写" type="string" defaultValue="100"  xtype="moduleQuery">
  </item>
</entry>