<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_YXBDS_DY" alias="医学表达式" sortinfo="DYBDSBH desc">
  <item id="DYBDSBH" alias="定义表达式编号" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
  <item id="BDSMC" alias="表达式名称" length="25" width="180" not-null="1"/>
  <item id="SSZK" alias="所属专科" type="long" width="120" length="18" not-null="1">
  	<dic id="phis.dictionary.diploma"/>
  </item>
  <item id="ZXBZ" alias="注销标志" width="100" length="1" not-null="1" defaultValue="0">
  	<dic id="phis.dictionary.confirm"/>
  </item>
  <item id="BDSNR" alias="表达式内容" display="0"/>
</entry>