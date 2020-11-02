<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_PrivacyRules" alias="隐私规则">
  <item id="id" alias="id" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="domain" alias="域" type="string" length="50" width="100" queryable="true">
  	<dic id="platform.reg.dictionary.domain"/>
  </item>	
  <item id="rule" alias="规则类型" type="string" length="50" width="100" queryable="true" not-null="1">
  	<dic id="platform.security.dic.sysPrivacyRule"/>
  </item>
  <item id="game" alias="处理策略" type="string" length="50" width="100" queryable="true" not-null="1">
  	<dic>
	  	<item key="1" text="隐藏" />
		<item key="2" text="*号代替" />
  	</dic>
  </item>
  <item id="content" alias="规则内容" type="string" length="200" width="400" colspan="2" queryable="true" not-null="1"/>
</entry>
