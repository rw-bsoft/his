<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_BLMB_BL" tableName="GY_BLMB" alias="模版类型">
	<item id="JLXH" alias="记录序号" type="int" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="SSLB" alias="所属类别" type="int"	length="1" display="0"/>
	<item id="YGDM" alias="员工代码" type="string"	length="10" display="0"/>
	<item id="KSDM" alias="科室代码" type="long"	length="10" display="0"/>
	<item id="MBMC" alias="模板名称" type="string" length="100" display="0"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" display="0"/>
	<item id="ZSXX" alias="主诉信息" type="string" fixed="true" xtype="textarea" not-null="1" length="500" />
	<item id="XBS" alias="现病史" type="string" fixed="true" xtype="textarea" not-null="1" length="500"/>
	<item id="JWS" alias="既往史" type="string" fixed="true" xtype="textarea" length="500"/>
	<item id="TGJC" alias="体格检查" type="string" fixed="true" xtype="textarea" length="500"/>
	<item id="FZJC" alias="辅助检查" type="string" fixed="true" xtype="textarea" length="500"/>
	<item id="CLCS" alias="处理措施" type="string" display="0" xtype="textarea" length="500"/>
	<item id="QYBZ" alias="启用标志" type="int"	length="1" display="0"/>

</entry>