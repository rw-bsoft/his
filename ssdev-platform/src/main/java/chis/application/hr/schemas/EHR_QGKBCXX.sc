<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_QGKBCXX" alias="全国卡补充信息" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="hypyxm" alias="汉语拼音姓名" type="string" defaultValue="ZHANG SAN"/>
	<item id="zjyxq" alias="证件有效期" type="string" defaultValue="20300131" not-null="1"/>
	<item id="fzjg" alias="发证机关" type="string" defaultValue="" not-null="1"/>
	<item id="ysr" alias="月收入" type="string" defaultValue="" not-null="1"/>
	<item id="yzbm" alias="邮政编码" type="string" defaultValue="" not-null="1"/>
	
	<item id="hjp" alias="省" type="string" defaultValue="1" group="户籍地址" not-null="1">
		<dic >
			<item key="1" text="上海市"/>
		</dic>
	</item>
	<item id="hjc" alias="市" type="string" defaultValue="1" group="户籍地址" not-null="1">
		<dic >
			<item key="1" text="上海市"/>
		</dic>
	</item>
	<item id="hjq" alias="县(区)" type="string" defaultValue="1" group="户籍地址" not-null="1">
		<dic >
			<item key="1" text="闵行区"/>
		</dic>
	</item>
	<item id="hjx" alias="乡镇(街道)" type="string" defaultValue="1" group="户籍地址" not-null="1">
		<dic >
			<item key="1" text="古美"/>
		</dic>
	</item>
	<item id="hjcu" alias="村(居委会)" type="string" defaultValue="1" group="户籍地址" not-null="1">
		<dic >
			<item key="1" text="古美"/>
		</dic>
	</item>
	<item id="hjxxdz" alias="户籍地址详细" type="string" defaultValue="上海市闵行区古美西路420弄23-202" colspan="2" group="户籍地址" not-null="1"/>
	
	<item id="jzp" alias="省" type="string" defaultValue="1" group="居住地址" not-null="1">
		<dic >
			<item key="1" text="上海市"/>
		</dic>
	</item>
	<item id="jzc" alias="市" type="string" defaultValue="1" group="居住地址" not-null="1">
		<dic >
			<item key="1" text="上海市"/>
		</dic>
	</item>
	<item id="jzq" alias="县(区)" type="string" defaultValue="1" group="居住地址" not-null="1">
		<dic >
			<item key="1" text="闵行区"/>
		</dic>
	</item>
	<item id="jzx" alias="乡镇(街道)" type="string" defaultValue="1" group="居住地址" not-null="1">
		<dic >
			<item key="1" text="古美"/>
		</dic>
	</item>
	<item id="jztd" alias="团队" type="string" defaultValue="1" group="居住地址" not-null="1">
		<dic >
			<item key="1" text="古美中心团队"/>
		</dic>
	</item>
	<item id="jzcu" alias="村(居委会)" type="string" defaultValue="1" group="居住地址" not-null="1">
		<dic >
			<item key="1" text="古美"/>
		</dic>
	</item>
	<item id="jzxxdz" alias="家庭地址详细" type="string" defaultValue="上海市闵行区古美西路420弄23-202" colspan="2" group="居住地址" not-null="1"/>
	<item id="hyzk" alias="婚姻状况" type="string" defaultValue="" not-null="1">
		<dic>
			<item key="1" text="已婚" />
			<item key="2" text="未婚" />
		</dic>
	</item>
	<item id="zy" alias="职业" type="string" defaultValue="" not-null="1">
		<dic>
			<item key="1" text="国家机关" />
			<item key="2" text="其他" />
		</dic>
	</item>
	<item id="sjh" alias="手机号" type="string" defaultValue="13262652355" not-null="1"/>
	<item id="jtdh" alias="家庭电话" type="string" defaultValue="00000000" not-null="1"/>
	
	<item id="1stlxr" alias="第一联系人姓名" type="string" group="联系人" not-null="1"/>
	<item id="1styckrgx" alias="与持卡人关系" type="string" group="联系人" not-null="1">
		<dic render="Tree">
			<item key="1" text="父母"/>	
			<item key="2" text="其他"/>
		</dic>
	</item>
	<item id="1stdh" alias="电话" type="string" colspan="2" group="联系人" not-null="1"/>
	
	<item id="2ndlxr" alias="第二联系人姓名" type="string" group="联系人"/>
	<item id="2ndyckrgx" alias="与持卡人关系" type="string" group="联系人">
		<dic render="Tree">
			<item key="1" text="父母"/>	
			<item key="2" text="其他"/>
		</dic>
	</item>
	<item id="2nddh" alias="电话" type="string" colspan="2" group="联系人"/>
	
	<item id="3rdlxr" alias="第三联系人姓名" type="string" group="联系人"/>
	<item id="3rdyckrgx" alias="与持卡人关系" type="string" group="联系人">
		<dic render="Tree">
			<item key="1" text="父母"/>	
			<item key="2" text="其他"/>
		</dic>
	</item>
	<item id="3rddh" alias="电话" type="string" colspan="2" group="联系人"/>
	
	<item id="dlrxm" alias="姓名" type="string" group="代理人资料"/>
	<item id="dlryckrgx" alias="与持卡人关系" type="string" group="代理人资料">
		<dic render="Tree">
			<item key="1" text="父母"/>	
			<item key="2" text="其他"/>
		</dic>
	</item>
	<item id="dlrdh" alias="电话" type="string" group="代理人资料"/>
	<item id="dlryb" alias="邮编" type="string" group="代理人资料"/>
	<item id="dlrdbly" alias="代办理由" type="string" colspan="2" group="代理人资料"/>
	<item id="dlrdz" alias="代办人地址" type="string" colspan="2" group="代理人资料"/>
	
	<item id="khyh" alias="开户银行" type="string" not-null="1">
		<dic >
			<item key="1" text="中国银行"/>
			<item key="2" text="中国建设银行"/>
			<item key="3" text="上海浦东发展银行"/>
		</dic>
	</item>
	<item id="yhzh" alias="银行账号" colspan="2" type="string"/>
	<item id="xnhkh" alias="新农合卡号" colspan="2" type="string"/>
	<item id="jkdnh" alias="健康档案号" colspan="2" type="string"/>
	<item id="ylfyzffs" alias="医疗费用支付方式" colspan="4" type="string">
		<dic render="Radio" colWidth="160">
			<item key="1" text="城镇职工基本医疗保险"></item>
			<item key="2" text="新型农村合作医疗"></item>
			<item key="3" text="商业医疗保险"></item>
			<item key="4" text="全自费"></item>
			<item key="5" text="城镇居民基本医疗保险"></item>
			<item key="6" text="贫困救助"></item>
			<item key="7" text="全公费"></item>
			<item key="8" text="其他"></item>
		</dic>
	</item>
	<item id="aboxx" alias="ABO血型" type="string" group="生物标识">
		<dic >
			<item key="1" text="A型"/>
			<item key="2" text="B型"/>
			<item key="3" text="O型"/>
			<item key="4" text="AB型"/>
			<item key="5" text="不详"/>
		</dic>
	</item>
	<item id="rhyx" alias="RH阴性" type="string" group="生物标识">
		<dic >
			<item key="1" text="是"/>
			<item key="2" text="否"/>
			<item key="3" text="不详"/>
		</dic>
	</item>
	<item id="jbbs" alias="疾病标识" type="string" colspan="2" group="生物标识">
		<dic render="LovCombo">
			<item key="1" text="哮喘"/>
			<item key="2" text="心脏病"/>
			<item key="3" text="心脑血管病"/>
			<item key="4" text="癫痫病"/>
			<item key="5" text="精神病"/>
			<item key="6" text="凝血紊乱"/>
			<item key="7" text="糖尿病"/>
			<item key="8" text="青光眼"/>
			<item key="9" text="透析"/>
			<item key="10" text="器官移植"/>
			<item key="11" text="器官缺失"/>
			<item key="12" text="可装卸义肢"/>
			<item key="13" text="心脏起搏器"/>
		</dic>
	</item>
	<item id="qtyxjs" alias="其他医学警示" type="string" colspan="2" group="生物标识"/>
	
	<item id="gmwmc1" alias="过敏物1名称" type="string" colspan="2" group="过敏物"/>
	<item id="gmfy1" alias="过敏反应" type="string" colspan="2" group="过敏物"/>
	<item id="gmwmc2" alias="过敏物2名称" type="string" colspan="2" group="过敏物"/>
	<item id="gmfy2" alias="过敏反应" type="string" colspan="2" group="过敏物"/>
	<item id="gmwmc3" alias="过敏物3名称" type="string" colspan="2" group="过敏物"/>
	<item id="gmfy3" alias="过敏反应" type="string" colspan="2" group="过敏物"/>
	
	<item id="ymmc1" alias="疫苗1名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj1" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc2" alias="疫苗2名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj2" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc3" alias="疫苗3名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj3" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc4" alias="疫苗4名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj4" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc5" alias="疫苗5名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj5" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc6" alias="疫苗6名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj6" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc7" alias="疫苗7名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj7" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc8" alias="疫苗8名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj8" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc9" alias="疫苗9名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj9" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	<item id="ymmc10" alias="疫苗10名称" type="string"  group="疫苗接种"/>
	<item id="ymjzsj10" alias="接种时间" type="datetime" xtype="datefield" group="疫苗接种"/>
	
	<item id="cjr" alias="采集人" type="string" not-null="1"/>
	<item id="cjsj" alias="采集时间" type="string"/>
	<item id="lrr" alias="录入人" type="string"/>
	<item id="lrsj" alias="录入时间" type="string"/>
</entry>
