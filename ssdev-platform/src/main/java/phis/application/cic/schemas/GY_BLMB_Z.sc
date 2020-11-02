<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_BLMB_Z" tableName="GY_BLMB" sort="a.JLXH desc" alias="模版类型">
	<item id="JLXH" alias="记录序号" type="int" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
  <item id="SSLB" alias="所属类别" type="int"	length="1" display="0"/>
  <item id="YGDM" alias="员工代码" type="string"	length="10" display="0" defaultValue="%user.userId" />
  <item id="KSDM" alias="科室代码" type="long"	length="18" display="0" defaultValue="%user.properties.biz_departmentId"/>
  <item id="MBMC" alias="模板名称" type="string" length="20" maxValue="20" width="240" not-null="1"/>
  <item id="PYDM" alias="拼音代码" type="string" length="10" maxValue="10" display="2" queryable="true" selected="true" target="MBMC" codeType="py"/>
  <item id="QYBZ" alias="启用" type="int" width="60" length="1" display="1" defaultValue="0" renderer="onRenderer">
		<dic id="phis.dictionary.confirm"/>
	</item>
  <item id="ZSXX" alias="主诉信息" type="string"	length="1000" display="0"/>
  <item id="XBS" alias="现病史" type="string"	length="1000" display="0"/>
  <item id="JWS" alias="既往史" type="string"	length="1000" display="0"/>
  <item id="TGJC" alias="体格检查" type="string"	length="1000" display="0"/>
  <item id="FZJC" alias="辅助检查" type="string"	length="1000" display="0"/>
  <item id="CLCS" alias="处理措施" type="string"	length="1000" display="0"/>
  <item id="JGID" alias="机构" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
</entry>