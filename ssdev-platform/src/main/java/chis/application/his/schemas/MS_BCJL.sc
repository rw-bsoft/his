<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="MS_BCJL" tableName="MS_BCJL" alias="门诊病历(病程记录)">
	<item id="JZXH" alias="就诊序号" length="18" type="long" not-null="1" generator="assigned" pkey="true"/>
	<item id="BRID" alias="病人ID" type="long" length="18"/>
	<item id="BLLX" alias="病历类型" length="1" type="int"/>
	<item id="BLLB" alias="病历类别" length="9" type="int"/>
	<item id="BLMC" alias="病历名称" type="string" length="100"/>
	<item id="JZKS" alias="就诊科室" length="18" type="long">
		<dic id="phis.dictionary.department_leaf" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="JZYS" alias="就诊医生" type="string" length="10">
		<dic id="phis.dictionary.doctor_cfqx" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZSXX" alias="主诉信息" type="string" length="1000"/>
	<item id="XBS" alias="现病史" type="string" length="1000"/>
	<item id="JWS" alias="既往史" type="string" length="1000"/>
	<item id="TGJC" alias="体格检查" type="string" length="1000"/>
	<item id="FZJC" alias="辅助检查" type="string" length="1000"/>
	<item id="CLCS" alias="处理措施" type="string" length="1000"/>
	<item id="DPY" alias="代配药" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="T" alias="体温" type="double" length="3" precision="1"/>
	<item id="P" alias="脉搏" type="int" length="4"/>
	<item id="R" alias="呼吸" type="int" length="4"/>
	<item id="SSY" alias="收缩压" type="int" length="3"/>
	<item id="SZY" alias="舒张压" type="int" length="3"/>
	<item id="W" alias="体重" type="int" length="4"/>
	<item id="H" alias="身高" type="int" length="4"/>
	<item id="BMI" alias="BMI" type="double" length="6" precision="2"/>
	<item id="CZYS" alias="操作医生" type="string" length="10"/>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="KS" alias="咳嗽" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="YT" alias="咽痛" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="HXKN" alias="呼吸困难" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="OT" alias="呕吐" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="FT" alias="腹痛" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="FX" alias="腹泻" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="PZ" alias="皮疹" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="QT" alias="其他" type="int" length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="BRQX" alias="病人去向" type="int" length="1">	
	    <dic id="phis.dictionary.patientDirection"/>  
	</item>
	<item id="JKJY" alias="健康教育" type="string">	    
	</item>
	<item id="BQGZ" alias="病情告知" type="string" length="1000"/>
	<item id="JKJYNR" alias="健康教育内容" type="string" />
	<item id="GMS" alias="过敏史" type="string" />
</entry>
