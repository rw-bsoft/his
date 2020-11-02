<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entry entityName="chis.application.hc.schemas.hc_m_progestationask" alias="孕前检查丈夫询问" sort="a.inputDate">
	<item id="phrId" alias="档案编号" length="16" type="string" pkey="true" fixed="true">
	</item>	
    <item id="empiId" alias="empiid"  length="32" type="string" display="0"/>
    <item ref="b.definePhrid" display="1" queryable="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="b.contactPhone" display="1" queryable="true"/>
	<item ref="b.registeredPermanent" display="0" queryable="true"/>
	<item ref="b.bloodTypeCode" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/>
    <item id="QZSFZH" alias="妻子身份证号"  length="18" type="string" not-null="true" />
	<item id="JHSJ" alias="结婚时间" type="date" />
    <item alias="历史疾病" id="LSJB" length="30" type="string">
        <dic render="LovCombo">
			<item key="01" text="否" />
			<item key="02" text="贫血" />
			<item key="03" text="高血压" />
			<item key="04" text="心脏病" />
			<item key="05" text="糖尿病" />
			<item key="06" text="癫痫" />
			<item key="07" text="甲状腺疾病" />
			<item key="08" text="慢性肾炎" />
			<item key="09" text="肿瘤" />
			<item key="10" text="结核" />
			<item key="11" text="乙型肝炎" />
			<item key="12" text="淋病、梅毒、衣原体感染等" />
			<item key="13" text="精神心理疾患等" />
		</dic>
	</item>
    <item alias="出生缺陷" id="CSQX" length="1" type="string">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="出生缺陷名称" id="CSQXTEXT" length="50" type="string"/>
    <item alias="男科疾病" id="NKJB" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="睾丸炎、附睾炎" />
			<item key="2" text="精神静脉曲张" />
			<item key="3" text="不育症" />
			<item key="4" text="腮腺炎" />
			<item key="9" text="其他" />
		
		</dic>
	</item>
    <item alias="男科疾病其它" id="NKJBQT" length="50" type="string"/>
    <item alias="服药" id="FY" length="1" type="string">
	    <dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
    <item alias="药物名称" id="YWMC" length="50" type="string"/>
    <item alias="注射过疫苗" id="ZSYM" length="1" type="string">
	    <dic>
			<item key="0" text="否" />
			<item key="1" text="乙肝疫苗" />
			<item key="9" text="其他" />
		</dic>
	</item>
    <item alias="疫苗其它" id="YMQT" length="50" type="string"/>
    <item alias="近亲结婚史" id="JQJHS" length="1" type="string">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="血缘关系" id="XYGX" length="30" type="string"/>
    <item alias="家族成员疾病史" id="JZCYJBS" length="1" type="string">
        <dic render="LovCombo">
			<item key="01" text="无" />
			<item key="02" text="地中海贫血" />
			<item key="03" text="白化病" />
			<item key="04" text="血友病" />
			<item key="05" text="G6PD缺乏症" />
			<item key="06" text="先天性心脏病" />
			<item key="07" text="唐氏综合症" />
			<item key="08" text="糖尿病" />
			<item key="09" text="先天性智力低下" />
			<item key="10" text="听力障碍（10岁以内发生）" />
			<item key="11" text="视力障碍（10岁以内发生）"/>
			<item key="12" text="新生儿和婴幼儿死亡" />
			<item key="99" text="其他出生缺陷" />
		</dic>
	</item>
    <item alias="家族成员疾病史其它" id="JZCYJBSQT" length="20" type="string"/>
    <item alias="患者与本人关系" id="HZYBRGX" length="20" type="string"/>
    <item alias="进食肉蛋" id="JSRD" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="厌食蔬菜" id="YSSC" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="食用生肉嗜好" id="SYSYSR" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="吸烟" id="SFXY" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="吸烟几支" id="XYSL" length="22" type="int"/>
    <item alias="被动吸烟" id="BDXY" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="偶尔" />
			<item key="2" text="经常" />
		</dic>		
	</item>
    <item alias="平均每天被动吸烟时间" id="MTBDXYSJ" length="10" type="string"/>
    <item alias="饮酒" id="YJ" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="偶尔" />
			<item key="2" text="经常" />
		</dic>		
	</item>
    <item alias="每天饮酒ml" id="MTYJML" length="22" type="int"/>
    <item alias="食用毒品" id="SYDP" length="1" type="string">
        <dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="毒品名称" id="DPMC" length="20" type="string"/>
    <item alias="接触因素" id="JCYS" length="1" type="string">
        <dic render="LovCombo">
			<item key="01" text="否" />
			<item key="02" text="放射线" />
			<item key="03" text="高温" />
			<item key="04" text="噪音" />
			<item key="05" text="有机溶液（如新装修、油漆）" />
			<item key="06" text="密切接触猫狗等家禽、宠物" />
			<item key="07" text="震动" />
			<item key="08" text="重金属（铅、汞等）" />
			<item key="09" text="农药" />
			<item key="99" text="其他" />
		</dic>
	</item>
    <item alias="其它因素" id="YSQT" length="30" type="string"/>
    <item alias="生活工作压力" id="SHGZYL" length="1" type="string">
	    <dic>
			<item key="0" text="无" />
			<item key="1" text="很少" />
			<item key="2" text="有一点" />
			<item key="3" text="比较大" />
			<item key="4" text="很大" />
		</dic>		
	</item>
    <item alias="亲友，同事关系是否紧张" id="GXSFJZ" length="1" type="string">
	    <dic>
			<item key="0" text="无" />
			<item key="1" text="很少" />
			<item key="2" text="有一点" />
			<item key="3" text="比较大" />
			<item key="4" text="很大" />
		</dic>		
	</item>
    <item alias="经济压力" id="JJYL" length="1" type="string">
	    <dic>
			<item key="0" text="无" />
			<item key="1" text="很少" />
			<item key="2" text="有一点" />
			<item key="3" text="比较大" />
			<item key="4" text="很大" />
		</dic>		
	</item>
    <item alias="做好怀孕准备" id="ZHHYZB" length="1" type="string">
	<dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>		
	</item>
    <item alias="其它社会心理因素 " id="QTSHSXYS" length="50" type="string"/>
    <item alias="询问时间" id="XWRQ" length="7" type="date"/>
    <item alias="医师签名" id="YSQM" length="20" type="string"/>
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人员" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
