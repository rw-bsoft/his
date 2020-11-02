<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_JBZL" alias="基本资料" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="personName" alias="姓名" type="string" defaultValue="" not-null="1"/>
	<item id="fullPersonName" alias="全名" type="string" defaultValue=""/>
	<item id="sexCode" alias="性别" type="string" defaultValue="1" not-null="1">
		<dic id="chis.dictionary.gender" onlySelectLeaf="true"/>
	</item>
	<item id="birthday" alias="出生年月" type="datetime" xtype="datefield" not-null="1"/>
	<item id="idtype" alias="证件类型" type="string" defaultValue="1" not-null="1">
		<dic id="chis.dictionary.certificate" onlySelectLeaf="true"/>
	</item>
	<item id="id" alias="证件号码" type="string" defaultValue="110101198001010117" not-null="1"/>
	<item id="zrys" alias="责任医生" type="string" defaultValue="1" not-null="1">
		<dic id="chis.dictionary.doctor" onlySelectLeaf="true"/>
	</item>
	<item id="lxdh" alias="联系电话(手机)" type="string" defaultValue="12345678901" not-null="1"/>
	<item id="jtdh" alias="家庭电话" type="string" defaultValue="00000000"/>
	<item id="lxgb" alias="离休干部" type="string">
		<dic id="chis.dictionary.yesOrNo" onlySelectLeaf="true"/>
	</item>
	<item id="hklx" alias="户口类型" type="string" not-null="1">
		<dic id="chis.dictionary.registerType" onlySelectLeaf="true"/>
	</item>
	<item id="sfhz" alias="是否户主" type="string">
		<dic id="chis.dictionary.yesOrNo" onlySelectLeaf="true"/>
	</item>
	<item id="yhzgx" alias="与户主关系" type="string" defaultValue="1">
		<dic id="chis.dictionary.relaCode" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="hkdz" alias="户口地址" type="string" defaultValue="上海市闵行区古美西路420弄23-202"/>
	<item id="jtdz" alias="家庭地址" type="string" defaultValue="上海市闵行区古美西路420弄23-202" fixed="true" not-null="1"/>
	<item id="yzbm" alias="邮政编码" type="string" defaultValue="000000" not-null="1"/>
	<item id="sztd" alias="所在团队" type="string" defaultValue="1">
		<dic >
			<item key="1" text="古美中心团队"/>
		</dic>
	</item>
	<item id="szjw" alias="所在居委" type="string" defaultValue="1">
		<dic >
			<item key="1" text="中心居委"/>
		</dic>
	</item>
	<item id="jtbm" alias="家庭编码" type="string" defaultValue="" fixed="true"/>
	<item id="fqxm" alias="父亲姓名" type="string" defaultValue=""/>
	<item id="mqxm" alias="母亲姓名" type="string" defaultValue=""/>
	<item id="poxm" alias="配偶姓名" type="string" defaultValue=""/>
	<item id="jzzk" alias="居住状况" type="string" defaultValue="1">
		<dic >
			<item key="1" text="长住"/>
		</dic>
	</item>
	<item id="gzdw" alias="工作单位" type="string" defaultValue=""/>
	<item id="dwdz" alias="单位地址" type="string" defaultValue=""/>
	<item id="dwdh" alias="单位电话" type="string" defaultValue=""/>
	<item id="daly" alias="档案来源" type="string" defaultValue="1">
		<dic >
			<item key="1" text="签约发卡"/>
		</dic>
	</item>
	<item id="qybz" alias="签约标志" type="string">
		<dic id="chis.dictionary.yesOrNo" onlySelectLeaf="true"/>
	</item>
	<item id="jdr" alias="建档人" type="string" defaultValue="" fixed="true"/>
	<item id="jdrq" alias="建档日期" type="datetime" xtype="datefield" defaultValue="%server.date.today" fixed="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="jtdzxgr" alias="家庭地址修改人" type="string" defaultValue="" fixed="true"/>
	<item id="xgrq" alias="修改日期" type="datetime" xtype="datefield" fixed="true"/>
	
	<item id="jkk" alias="健康卡" type="string" colspan="2" defaultValue="241003086540000" fixed="true" group="卡信息"/>
	<item id="ybk" alias="医保卡" type="string" colspan="2" defaultValue="6003752167" fixed="true" group="卡信息"/>
	
	<item id="whcd" alias="文化程度" type="string" >
		<dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="mz" alias="民族" type="string" defaultValue="1" >
		<dic id="chis.dictionary.ethnic" onlySelectLeaf="true"/>
	</item>
	<item id="hyzk" alias="婚姻状况" type="string" colspan="2" defaultValue="1" not-null="1">
		<dic id="chis.dictionary.maritals" onlySelectLeaf="true"/>
	</item>
	<item id="mqzy" alias="目前职业" type="string" colspan="2" defaultValue="1" not-null="1">
		<dic >
			<item key="1" text="不详"/>
		</dic>
	</item>
	<item id="cssjzczy" alias="从事时间最长职业" colspan="2" type="string" defaultValue="1" >
		<dic >
			<item key="1" text="不详"/>
		</dic>
	</item>
	
	<item id="jg" alias="籍贯" type="string" colspan="2">
		<dic id="platform.reg.dictionary.adminDivision" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="zcjzd" alias="最长居住地" type="string" colspan="2">
		<dic id="platform.reg.dictionary.adminDivision" render="Tree" onlySelectLeaf="true"/>
	</item>
	
	<item id="ylfyzfxs" alias="医疗费用支付形式" type="string" defaultValue="1">
		<dic >
			<item key="1" text="自费"/>
			<item key="2" text="公费"/>
		</dic>
	</item>
	<item id="ydyy1" alias="约定医院1" type="string" defaultValue="">
		<dic id="hais.dictionary.yyjg" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="ydyy2" alias="约定医院2" type="string" defaultValue="">
		<dic id="hais.dictionary.yyjg" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="ydyy3" alias="约定医院3" type="string" defaultValue="">
		<dic id="hais.dictionary.yyjg" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="stbs" alias="身体不适时采取的措施" type="string" colspan="2" defaultValue="">
		<dic >
			<item key="1" text="医疗结构就诊"/>
		</dic>
	</item>
	<item id="wsfwxq" alias="卫生服务需求" type="string" colspan="2" defaultValue="">
		<dic >
			<item key="1" text="健康指导"/>
		</dic>
	</item>
	<item id="cjqk" alias="残疾情况" type="string" colspan="2" defaultValue="">
		<dic >
			<item key="1" text="无残"/>
		</dic>
	</item>
	<item id="bdcz" alias="被调查者" type="string" defaultValue="">
	</item>
	<item id="dcz" alias="调查者" type="string" defaultValue="">
	</item>
	<item id="jlz" alias="记录者" type="string" defaultValue="" fixed="true">
	</item>
	<item id="qmrq" alias="签名日期" type="datetime" xtype="datefield" />
	<item id="dcrq" alias="调查日期" type="datetime" xtype="datefield" />
	<item id="jlrq" alias="记录日期" type="datetime" xtype="datefield" fixed="true"/>
</entry>
