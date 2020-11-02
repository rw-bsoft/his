<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZFR_N" tableName="MS_MZFR"  alias="病人档案表" sort="a.XGSJ desc">
	<item id="SBXH" alias="识别序号" type="long" length="18" generator="assigned" pkey="true" display="0" >
		<key>
			<rule name="increaseId" defaultFill="0" type="sequence" length="10" startPos="1"/>
		</key>
	</item>
	<item id="EMPIID" alias="EMPIID" type="string" length="32"  display="0" />
	<item id="BRID" alias="病人" type="long" length="25" display="0"/>
	<item id="BRXM" alias="病人姓名" type="string" length="25" translate="true" readOnly="true"/>
    <item id="BRXB" alias="病人性别" length="4" type="string" readOnly="true">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="CSNY" alias="出生年月" type="timestamp" readOnly="true"/>
	<item id="SFZH" alias="身份证号" type="string" length="25" readOnly="true"/>
	<item id="BRDZ" alias="本人地址" type="string" length="50" readOnly="true"/>
	<item id="BRDH" alias="本人电话" type="string" length="25" readOnly="true"/>
	<item id="SJLY" alias="数据来源" type="int" length="4" readOnly="true">
		<dic>
			<item key="1" text="直接新增"/>
			<item key="2" text="门诊引入"/>
			<item key="3" text="住院引入"/>
		</dic>
	</item>
	<item id="FZBZ" alias="复诊标志" type="int" width="4" not-null="1">
		<dic>
			<item key="1" text="初诊"/>
			<item key="2" text="复诊"/>
		</dic>
	</item>
	<item id="LYBS" alias="来源标识" type="string" length="25" display="0"/>
	<item id="T" alias="体温" type="double" length="4" precision="1" not-null="1"/>
	<item id="YZNFR" alias="一周内是否发热" length="4"    type="int" not-null="1">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="FRRQ" alias="发热日期" not-null="1" type="date" defaultValue="%server.date.date"/>
	<item id="SSY" alias="收缩压" length="25"    type="string"/>
	<item id="SZY" alias="舒张压" length="25"    type="string"/>
	<item id="R" alias="呼吸" length="25"    type="string"/>
	<item id="P" alias="脉搏" length="25"    type="string"/>
	<item id="PAO2" alias="动脉血样分压" type="string" length="25"/>
	<item id="FL" alias="乏力" type="int" length="4" not-null="1">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="KS" alias="干咳" type="int" length="4" not-null="1" >
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="BS" alias="鼻塞" type="int" length="4"  not-null="1">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="LT" alias="流涕" type="int" length="4"  not-null="1">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="YT" alias="咽痛" type="int" length="4"  not-null="1">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="FX" alias="腹泻" length="4"   type="int" not-null="1">
	    <dic>
            <item key="0" text="否"/>
            <item key="1" text="是"/>
        </dic>
	</item>
	<item id="QTZZ" alias="其他症状" type="String" length="25" />
	<item id="JCJB" alias="基础疾病" type="String"  length="4" not-null="1">
        <dic render="LovCombo">
            <item key="1" text="心脑血管疾病"/>
            <item key="2" text="内分泌系统疾病"/>
            <item key="3" text="消化系统疾病"/>
            <item key="4" text="呼吸系统疾病"/>
            <item key="5" text="恶性肿瘤和神经系统疾病"/>
        </dic>
	</item>
	<item id="QTJB" alias="其他疾病" type="string" length="25"  />
	<item id="HZQX" alias="患者去向" length="4"   type="int" not-null="1">
        <dic>
            <item key="1" text="门诊留观（留观24小时以上）"/>
            <item key="2" text="居家观察（门诊治疗后回家）"/>
            <item key="3" text="转上级医院（门诊或住院）"/>
            <item key="4" text="转隔离点"/>
            <item key="99" text="其他"/>
        </dic>
	</item>
	<item id="QTQX" alias="其他去向" length="25"  type="string"/>

</entry>
