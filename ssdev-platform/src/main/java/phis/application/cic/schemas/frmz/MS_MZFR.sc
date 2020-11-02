<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZFR" tableName="MS_MZFR"  alias="病人档案表" sort="a.XGSJ desc">
	<item id="SBXH" alias="识别序号" type="long" length="18" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" defaultFill="0" type="sequence" length="10" startPos="1"/>
		</key>
	</item>
	<!--<item id="YYBZINFO" alias=""  type="string"  length="4" width="10" renderer="showColor" virtual="true" fixed="true"/>-->
	<item id="BRID" alias="病人" type="long" length="32" queryable="true" width="200"/>
	<item id="EMPIID" alias="EMPIID" type="string" length="32"   />
	<item id="SJLY" alias="数据来源" type="int" length="4" >
	    <dic>
            <item key="1" text="直接新增"/>
            <item key="2" text="门诊引入"/>
            <item key="3" text="住院引入"/>
        </dic>
	</item>
	<item id="LYBS" alias="来源标识" type="int" length="20" />
	<item id="FZBZ" alias="复诊标志" type="int" width="4">
	    <dic>
            <item key="1" text="初诊"/>
            <item key="2" text="复诊"/>
        </dic>
	</item>
  <item id="T" alias="体温" type="double" length="4" precision="1"/>
	<item id="YZNFR" alias="一周内是否发热" length="4"    type="int">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="FRRQ" alias="发热日起"  type="timestamp"/>
	<item id="SSY" alias="收缩压" length="25"    type="int" minValue="10" maxValue="500"/>
	<item id="SZY" alias="舒张压" length="25"    type="int" minValue="10" maxValue="500"/>
	<item id="R" alias="呼吸" length="25"    type="int"/>
	<item id="P" alias="脉搏" length="25"    type="int"/>
	<item id="PAO2" alias="动脉血样分压" type="long" length="25"  />
	<item id="FL" alias="乏力" type="int" length="4"  >
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="KS" alias="干咳" type="int" length="4"  >
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="BS" alias="鼻塞" type="int" length="4"  >
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="LT" alias="流涕" type="int" length="4"  >
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="YT" alias="咽痛" type="int" length="4"  >
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="FX" alias="腹泻" length="4"   type="int">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="QTZZ" alias="其他症状" type="String"/>
	<item id="JCJB" alias="基础疾病" type="String" length="256" >
        <dic render="LovCombo">
            <item key="1" text="心脑血管疾病"/>
            <item key="2" text="内分泌系统疾病"/>
            <item key="3" text="消化系统疾病"/>
            <item key="4" text="呼吸系统疾病"/>
            <item key="5" text="恶性肿瘤和神经系统疾病"/>
        </dic>
	</item>
	<item id="QTJB" alias="其他疾病" type="string" length="25"  />
	<item id="HZQX" alias="患者去向" length="4"   type="int">
        <dic>
            <item key="1" text="门诊留观（留观24小时以上）"/>
            <item key="2" text="居家观察（门诊治疗后回家）"/>
            <item key="3" text="转上级医院（门诊或住院）"/>
            <item key="4" text="转隔离点"/>
            <item key="99" text="其他"/>
        </dic>
	</item>
	<item id="QTQX" alias="其他去向" length="256"  type="String"/>
	<item id="ZDXH" alias="诊断序号" length="256"  type="String">
        <dic render="LovCombo">
            <item key="J12.800+B97.200" text="确诊新型冠状病毒肺炎"/>
            <item key="J12.800+B97.200+Z11.500" text="疑似新型冠状病毒肺炎"/>
            <item key="B34.2" text="核酸检测阳性，而无肺炎表现"/>
            <item key="Z20.800" text="接触和暴露于新型冠状病毒"/>
            <item key="Z11.500" text="新型冠状病感染的特殊筛查"/>
            <item key="098.800" text="妊娠合并新型冠状病毒肺炎"/>
        </dic>
	</item>
	<item id="XCGJG" alias="血常规结果" type="string" length="25"  />
	<item id="CPR" alias="高敏感CPR" length="4"    type="int">
	    <dic>
            <item key="1" text="阴性"/>
            <item key="2" text="阳性"/>
        </dic>
	</item>
	<item id="YSZ" alias="咽试因子" type="int" length="4"  >
		<dic>
             <item key="1" text="阴性"/>
             <item key="2" text="阳性"/>
        </dic>
    </item>
	<item id="SFCT" alias="双肺CT" type="int" length="4"  >
        <dic>
            <item key="1" text="正常"/>
            <item key="2" text="其他非新冠影像"/>
            <item key="3" text="多发小班片影及间质改变"/>
            <item key="4" text="双肺多发磨玻璃影、浸润影"/>
            <item key="5" text="肺实变"/>
            <item key="6" text="胸腔积液"/>
        </dic>
	</item>
	<item id="RTPCR" alias="核酸检测" type="int" length="4"  >
	    <dic>
            <item key="1" text="阴性"/>
            <item key="2" text="阳性"/>
        </dic>
	</item>
	<item id="LRRY" alias="录入人" length="25"    type="string"/>
	<item id="JGID" alias="录入机构" type="String" length="20" width="150"  />
	<item id="LRSJ" alias="录入时间" type="timestamp"  />
	<item id="XGR" alias="修改人" length="25"    type="string"/>
	<item id="XGSJ" alias="修改时间" type="timestamp" />
</entry>
