<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hy.schemas.MDC_HyBaseline" alias="高血压基线调查表">
	<!--基本信息-->
	<item id="recordId" alias="基线表主键" type="string" display="0" length="16"  pkey="true" fixed="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="32" width="165" queryable="true" display="2"  fixed="true" group="基本信息" colspan="2"/>
	<item id="empiId" alias="EMPIID" type="string" length="32"  width="160" fixed="true" display="2"  update="false" group="基本信息" colspan="2"/>
	<item id="fillingDate" alias="填表日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="基本信息" colspan="2" not-null="1"/>
	<item ref="b.personName" display="1" queryable="true" group="基本信息"/>
	<item ref="b.sexCode" display="1" queryable="true" group="基本信息"/>
	<item ref="b.birthday" display="1" queryable="true" group="基本信息"/>
	<item ref="b.educationCode" alias="文化程度" display="1" type="string" length="2" group="基本信息" not-null="0" fixed="true" colspan="2" />
	<item ref="b.nationCode" alias="民族" display="1" type="string" length="2"  defaultValue="01" group="基本信息"  not-null="0" fixed="true" colspan="2" />
	<item ref="b.address" display="1" group="基本信息"/>
	<item ref="b.mobileNumber" display="1" group="基本信息"/>
	<item ref="b.contact" display="1" group="基本信息"/>
	<item ref="b.contactPhone" display="1" group="基本信息"/>
	<!--心血管病危险因素-->
	<item id="GXY" alias="高血压" type="string" length="5" display="2" group="心血管病危险因素" editable="false" not-null="1" defaultValue="2" >
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="GXYZDSJ" alias="高血压诊断时间" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="心血管病危险因素" editable="false" />
	<item id="SFFYJYY" alias="是否服用降压药" type="string" length="5" display="2" group="心血管病危险因素" editable="false">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="KSYYSJ" alias="什么时间开始用药" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="心血管病危险因素" editable="false" />
	
	<item id="TNB" alias="糖尿病" type="string" length="5" display="2" group="心血管病危险因素"  editable="false" not-null="1"  defaultValue="2">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="TNBZDSJ" alias="糖尿病诊断时间" editable="false" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="心血管病危险因素"/>
	<item id="SFFYJTY" alias="是否服用(注射)降糖药" editable="false" type="string" length="5" display="2" group="心血管病危险因素" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="GZXZ" alias="高脂血症" type="string" length="5" display="2" group="心血管病危险因素"  editable="false" not-null="1"  defaultValue="2">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="GZXZZDSJ" alias="诊断时间" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="心血管病危险因素" editable="false"/>
	<item id="GZXZSFYY" alias="是否用药" type="string" length="5" display="2" group="心血管病危险因素" colspan="2" editable="false">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="JWYWGXB" alias="既往有无冠心病" type="string" length="5" display="2" group="心血管病危险因素" editable="false" not-null="1"  defaultValue="2">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="GXBZDSJ" alias="诊断时间" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="心血管病危险因素" editable="false" colspan="3"/>

	<item id="JWYWNZZ" alias="既往有无脑卒中" type="string" length="5" display="2" group="心血管病危险因素" editable="false" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="NZZZDSJ" alias="诊断时间" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="心血管病危险因素" editable="false" colspan="3"/>

	<item id="YWQTJB" alias="有无其他系统疾病" type="string" length="5" display="2" group="心血管病危险因素" editable="false" not-null="1"  defaultValue="2"> <!--(肝硬化、慢阻肺、肝肾功能不全等)-->
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="YWQTJBJT" alias="具体(用逗号分开)" type="string" display="2" length="200" group="心血管病危险因素" colspan="3"/>
	
	<item id="GXYJZS" alias="高血压家族史" type="string" length="5" display="2" group="心血管病危险因素" defaultValue="2" colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="GXBJZS" alias="冠心病家族史" type="string" length="5" display="2" group="心血管病危险因素" defaultValue="2" colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="TNBJZS" alias="糖尿病家族史" type="string" length="5" display="2" group="心血管病危险因素" defaultValue="2" colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="NZZJZS" alias="脑卒中家族史" type="string" length="5" display="2" group="心血管病危险因素" defaultValue="2" colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="DH" alias="打鼾" type="string" length="5" display="2" group="心血管病危险因素" defaultValue="2" colspan="2"  not-null="1">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="SFYSMHXZT" alias="是否有睡眠呼吸暂停" type="string" length="5" display="2" group="心血管病危险因素" colspan="2" defaultValue="2"  not-null="1">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>
	<item id="XYZK" alias="吸烟状况" type="string" length="5" display="2" group="心血管病危险因素" colspan="4" defaultValue="1" editable="false" not-null="1"> 
		<dic render="Radio">
			<item key="1" text="从不吸烟"/>
			<item key="2" text="已戒烟"/>
			<item key="3" text="吸烟"/>
		</dic>
	</item>
	<item id="XYZKJYKSNL" alias="开始吸烟年龄(岁)" type="string" display="2" length="2"  group="心血管病危险因素" colspan="2"/>
	<item id="XYZKJYMTJZ" alias="平均每天几支(支/天)" type="string" display="2" length="5"  group="心血管病危险因素"/>
	<item id="XYZKJYNL" alias="戒烟年龄(岁)" type="string" display="2" length="2"  group="心血管病危险因素"/>
	
	<!-- <item id="XYZKXYKSNL" alias="吸烟:开始吸烟年龄(岁)" type="string" display="2" length="2"  group="心血管病危险因素" colspan="2"/>
	<item id="XYZKXYMTJZ" alias="平均每天几支(支/天)" type="string" display="2" length="5"  group="心血管病危险因素" colspan="2"/> -->
	
	<item id="YJQK" alias="饮酒状况" type="string" length="5" display="2" group="心血管病危险因素" defaultValue="1" colspan="2" editable="false" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="从不"/>
			<item key="2" text="偶尔"/>
			<item key="3" text="经常"/>
			<item key="4" text="每天"/>
		</dic>
	</item>
	
	<item id="YJQKORMTJL" alias="平均(两/天)" type="string" display="2" length="2"  group="心血管病危险因素"  colspan="2"/>
	<!-- <item id="YJQKJCMTJL" alias="经常:平均(两/天)" type="string" display="2" length="2"  group="心血管病危险因素"/>
	<item id="YJQKMTMTJL" alias="每天:平均(两/天)" type="string" display="2" length="2"  group="心血管病危险因素"/> -->
	
	<item id="SFJJ" alias="是否戒酒" type="string" length="5" display="2" group="心血管病危险因素">
		<dic autoLoad="true">
			<item key="1" text="未戒酒"/>
			<item key="2" text="已戒酒"/>
		</dic>
	</item>
	<item id="SFJJYJJJJNL" alias="戒酒年龄(岁)" type="string" display="2" length="2"  group="心血管病危险因素"/>
	<item id="KSYJNL" alias="开始饮酒年龄(岁)" type="string" display="2" length="2"  group="心血管病危险因素"/>
	<item id="JYNSFJJ" alias="近一年内是否曾醉酒" type="string" display="2" length="2"  group="心血管病危险因素">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="YJZL" alias="饮酒种类" type="string" display="2" length="2"  group="心血管病危险因素" colspan="2">
		<dic render="Checkbox">
			<item key="1" text="白酒"/>
			<item key="2" text="啤酒"/>
			<item key="3" text="红酒"/>
			<item key="4" text="黄酒"/>
			<item key="5" text="其他"/>
		</dic>
	</item>
	<item id="YJZLQT" alias="其他饮酒种类" type="string" display="2" length="200"  group="心血管病危险因素" colspan="2"/>
	
	<!--体格检查-->
	<item id="SG" alias="身高(cm)" type="int" length="6" display="2" minValue="100" maxValue="300" enableKeyEvents="true" group="体格检查" not-null="1"/>
	<item id="TZ" alias="体重(kg)" type="double" length="6" display="2" minValue="30" maxValue="500" enableKeyEvents="true" group="体格检查" not-null="1"/>
	<item id="TZZS" alias="体重指数 BMI(kg/m2)" type="double" length="6" display="2" minValue="10" maxValue="500" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1" fixed="true"/>
	<item id="YYSSY" alias="右上肢血压:第一次收缩压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>
	<item id="YYSZY" alias="第一次舒张压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>
	<item id="YESSY" alias="右上肢血压:第二次收缩压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>
	<item id="YESZY" alias="第二次舒张压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>	
	<item id="ZYSSY" alias="左上肢血压:第一次收缩压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>
	<item id="ZYSZY" alias="第一次舒张压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>
	<item id="ZESSY" alias="左上肢血压:第二次收缩压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>
	<item id="ZESZY" alias="第二次舒张压(mmHg)" type="string" length="6" display="2" enableKeyEvents="true" group="体格检查" colspan="2" not-null="1"/>	
	<item id="XL" alias="心率(次/分)" type="string" length="3" display="2" enableKeyEvents="true" group="体格检查" colspan="4" not-null="1"/>
	
	<!--辅助检查-->
	<item id="FJJCSJ" alias="检测日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="辅助检查" colspan="4" not-null="1"/>
	<item id="XJ" alias="血钾(K)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="XN" alias="血钠(Na)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="BASAJZYM" alias="丙氨酸氨基转移酶(ALT)(U/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="TMDASAJZYM" alias="天门冬氨酸氨基转移酶(AST)(U/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="XNSD" alias="血尿素氮(BUN)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="XJG" alias="血肌酐(CREA)(μmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="XNS" alias="血尿酸(UA)(μmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="SXQLGL" alias="肾小球滤过率(eGFR)(ml/min/1.73m2)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" fixed="true" not-null="1"/>
	<item id="ZDGC" alias="总胆固醇(TC)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="GYSZ" alias="甘油三酯(TG)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="DMDZDBDGC" alias="低密度脂蛋白胆固醇(LDL-C)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="GMDZDBDGC" alias="高密度脂蛋白胆固醇(HLD-C)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="KFXT" alias="空腹血糖(FBG)(mmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" not-null="1"/>
	<item id="THXHDB" alias="糖化血红蛋白(HbA1c)(%)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="2"/>
	<item id="TXBGAS" alias="同型半胱氨酸(Hcy)(μmol/L)" type="string" length="20" display="2" enableKeyEvents="true" group="辅助检查" colspan="4"/>
	<item id="XDT" alias="心电图(ECG)" length="1" display="2" enableKeyEvents="true" group="辅助检查"  colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="正常"/>
			<item key="2" text="异常"/>
			<item key="3" text="未查"/>
		</dic>
	</item>
	<item id="XDTYC" alias="如有异常，请描述" type="string" length="200" display="2" enableKeyEvents="true" group="辅助检查" colspan="2" />
	
	<!--排除标准-->
	<item id="GSHZFH1" alias="血ALT大于正常上限3倍(正常0-40 U/L)" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="GSHZFH2" alias="透析治疗的患者" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="GSHZFH3" alias="eGFR(表皮生长因子受体)小于30ml/min" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="GSHZFH4" alias="血肌酐大于221mol/L" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1"> <!--[>2.5 mg/dl])(正常45-104 mol/L)-->
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="EXZL" alias="恶性肿瘤病史" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="GNZA" alias="有认知功能障碍者或生活无法自理者" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="WCN" alias="未满18岁的未成年人" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="JZ" alias="有至少6个月未在溧水区居住或永久迁出者" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="BTL" alias="不能或不愿提供知情同意的人" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="RSQ" alias="报告妊娠期或处于哺乳期的妇女" length="1" display="2" enableKeyEvents="true" group="排除标准" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	
	<!--患者目前治疗方案-->
	<item id="KZYS" alias="是否采取控制饮食" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" defaultValue="2" colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="YDCS" alias="是否采取运动措施" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" defaultValue="2" colspan="2" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="SFYY" alias="是否用药" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" defaultValue="2" colspan="4" not-null="1">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="SFYYYES" alias="如为是，请填写以下信息" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" defaultValue="1" colspan="4">
		<dic render="Radio">
			<item key="1" text="按医嘱坚持服药"/>
			<item key="2" text="间断服药"/>
			<item key="3" text="患者自行调整用药"/>
		</dic>
	</item>
	<item id="JDFY" alias="每周用药次数(次)" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>			
	
	<item id="YW1MC" alias="药物1:名称" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>	
	<item id="YW1YF" alias="用法" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW1JL" alias="用量" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW1SJ" alias="用药时间" type="string" length="50" display="2" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW1YCX" alias="服药依从性" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2">	
		<dic render="Radio">
			<item key="1" text="规律"/>
			<item key="2" text="间断"/>
			<item key="3" text="不服药"/>
		</dic>	
	</item>
	
	<item id="YW2MC" alias="药物2:名称" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>	
	<item id="YW2YF" alias="用法" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW2JL" alias="用量" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW2SJ" alias="用药时间" type="string" length="50" display="2" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW2YCX" alias="服药依从性" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2">	
		<dic render="Radio">
			<item key="1" text="规律"/>
			<item key="2" text="间断"/>
			<item key="3" text="不服药"/>
		</dic>	
	</item>
	
	<item id="YW3MC" alias="药物3:名称" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>	
	<item id="YW3YF" alias="用法" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW3JL" alias="用量" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW3SJ" alias="用药时间" type="string" length="50" display="2" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW3YCX" alias="服药依从性" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2">	
		<dic render="Radio">
			<item key="1" text="规律"/>
			<item key="2" text="间断"/>
			<item key="3" text="不服药"/>
		</dic>	
	</item>
	
	<item id="YW4MC" alias="药物4:名称" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>	
	<item id="YW4YF" alias="用法" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW4JL" alias="用量" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW4SJ" alias="用药时间" type="string" length="50" display="2" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW4YCX" alias="服药依从性" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2">	
		<dic render="Radio">
			<item key="1" text="规律"/>
			<item key="2" text="间断"/>
			<item key="3" text="不服药"/>
		</dic>	
	</item>
	
	<item id="YW5MC" alias="药物5:名称" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>	
	<item id="YW5YF" alias="用法" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW5JL" alias="用量" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW5SJ" alias="用药时间" type="string" length="50" display="2" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW5YCX" alias="服药依从性" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2">	
		<dic render="Radio">
			<item key="1" text="规律"/>
			<item key="2" text="间断"/>
			<item key="3" text="不服药"/>
		</dic>	
	</item>
	
	<item id="YW6MC" alias="药物6:名称" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="4"/>	
	<item id="YW6YF" alias="用法" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW6JL" alias="用量" type="string" length="50" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW6SJ" alias="用药时间" type="string" length="50" display="2" group="患者目前治疗方案" colspan="2"/>	
	<item id="YW6YCX" alias="服药依从性" type="string" length="1" display="2" enableKeyEvents="true" group="患者目前治疗方案" colspan="2">	
		<dic render="Radio">
			<item key="1" text="规律"/>
			<item key="2" text="间断"/>
			<item key="3" text="不服药"/>
		</dic>	
	</item>
	
	<!--各类事件发生情况-->
	<item id="JXXJGS" alias="急性心肌梗死" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>		
		</dic>
	</item>
	<item id="JXXJGSYES" alias="如有请勾选" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Checkbox">
			<item key="1" text="住院治疗"/>
			<item key="2" text="门诊"/>		
		</dic>
	</item>		
	<item id="JXXJGSFBRQ" alias="发病日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true" maxValue="%server.date.today" group="各类事件发生情况" colspan="4" />	
	
	<item id="BWDXJT" alias="不稳定心绞痛" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>		
		</dic>
	</item>
	<item id="BWDXJTYES" alias="如有请勾选" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2" >
		<dic render="Checkbox">
			<item key="1" text="住院治疗"/>
			<item key="2" text="门诊"/>		
		</dic>
	</item>		
	<item id="BWDXJTFBRQ" alias="发病日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="4" />	
	
	<item id="XSZYZL" alias="心衰住院治疗" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>		
		</dic>
	</item>	
	<item id="XSFJ" alias="心衰分级(NYHA)" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Radio">
			<item key="1" text="III级"/>
			<item key="2" text="IV级"/>		
		</dic>
	</item>	
	<item id="XSZYZLFBRQ" alias="发病日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="4" />
		
	<item id="XYCJ" alias="血运重建" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>		
		</dic>
	</item>	
	<item id="XYCJRGY" alias="如有请勾选" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Checkbox">
			<item key="1" text="介入治疗/支架"/>
			<item key="2" text="搭桥/外科手术"/>		
		</dic>
	</item>		
	<item id="XYCJRQ" alias="发病日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="4" />	
	
	<item id="NZZ" alias="脑卒中" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>	
	<item id="NZZSFZY" alias="是否住院" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="NZZFBRQ" alias="发病日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="4" />
	
	<item id="NZZSFZYZDYJ" alias="诊断依据:有无CT或核磁" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="9" text="不详"/>
		</dic>
	</item>		
	<item id="NZZFL" alias="卒中分类" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Radio">
			<item key="1" text="脑出血"/>
			<item key="2" text="大血管脑梗死"/>
			<item key="9" text="未分类"/>
		</dic>
	</item>
			
	<item id="NLJBS" alias="肿瘤疾病史" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="NLJBSZDRQ" alias="诊断日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>		
	<item id="NLJBSJBZD" alias="疾病诊断" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="4"/>
	
	<item id="BLSJJLJYN" alias="近1年不良事件记录" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="4" not-null="1"> <!--(如：低血压（SBP<110或DBP<50 mmHg）、晕厥、急性肾损伤（eGFR下降≥30%）、头晕，头痛，恶心，咳嗽，呕吐，皮疹，水肿等)-->
		<dic render="Radio">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>		
	
	<item id="BLSJMC1" alias="不良事件1:名称" type="string" length="50" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="4"/>
	<item id="BLSJFSSJ1" alias="发生日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJJSSJ1" alias="结束日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJYZCD1" alias="严重程度" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="轻度:有不适感，但不影响正常的日常活动"/>
			<item key="2" text="中度:不适感足以减少或影响正常的日常活动"/>
			<item key="3" text="重度:不能工作或进行正常的日常活动"/>
		</dic>	
	</item>
	<item id="BLSJSFJDFS1" alias="是/否间断发生" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic render="Radio">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="BLSJCL1" alias="处理" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJYWJL1" alias="是否调整研究药物剂量" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="BLSJJYYWGX1" alias="与降压药物关系" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="肯定有关"/>
			<item key="2" text="可能有关"/>
			<item key="3" text="可能无关"/>
			<item key="4" text="无关"/>
			<item key="5" text="无法判断"/>
		</dic>		
	</item>
	<item id="BLSJZG1" alias="转归" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="缓解无后遗症"/>
			<item key="2" text="缓解有后遗症"/>
			<item key="3" text="无变化"/>
			<item key="4" text="恶化"/>
		</dic>
	</item>			
			
	<item id="BLSJMC2" alias="不良事件2:名称" type="string" length="50" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="4"/>
	<item id="BLSJFSSJ2" alias="发生日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJJSSJ2" alias="结束日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJYZCD2" alias="严重程度" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="轻度:有不适感，但不影响正常的日常活动"/>
			<item key="2" text="中度:不适感足以减少或影响正常的日常活动"/>
			<item key="3" text="重度:不能工作或进行正常的日常活动"/>
		</dic>	
	</item>
	<item id="BLSJSFJDFS2" alias="是/否间断发生" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="BLSJCL2" alias="处理" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJYWJL2" alias="是否调整研究药物剂量" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="BLSJJYYWGX2" alias="与降压药物关系" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="肯定有关"/>
			<item key="2" text="可能有关"/>
			<item key="3" text="可能无关"/>
			<item key="4" text="无关"/>
			<item key="5" text="无法判断"/>
		</dic>		
	</item>
	<item id="BLSJZG2" alias="转归" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="缓解无后遗症"/>
			<item key="2" text="缓解有后遗症"/>
			<item key="3" text="无变化"/>
			<item key="4" text="恶化"/>
		</dic>
	</item>					
			
	<item id="BLSJMC3" alias="不良事件3:名称" type="string" length="50" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="4"/>
	<item id="BLSJFSSJ3" alias="发生日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJJSSJ3" alias="结束日期" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJYZCD3" alias="严重程度" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="轻度:有不适感，但不影响正常的日常活动"/>
			<item key="2" text="中度:不适感足以减少或影响正常的日常活动"/>
			<item key="3" text="重度:不能工作或进行正常的日常活动"/>
		</dic>	
	</item>
	<item id="BLSJSFJDFS3" alias="是/否间断发生" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>	
	<item id="BLSJCL3" alias="处理" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2"/>
	<item id="BLSJYWJL3" alias="是否调整研究药物剂量" type="string" length="200" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="BLSJJYYWGX3" alias="与降压药物关系" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="肯定有关"/>
			<item key="2" text="可能有关"/>
			<item key="3" text="可能无关"/>
			<item key="4" text="无关"/>
			<item key="5" text="无法判断"/>
		</dic>		
	</item>
	<item id="BLSJZG3" alias="转归" type="string" length="1" display="2" enableKeyEvents="true" group="各类事件发生情况" colspan="2">
		<dic autoLoad="true">
			<item key="1" text="缓解无后遗症"/>
			<item key="2" text="缓解有后遗症"/>
			<item key="3" text="无变化"/>
			<item key="4" text="恶化"/>
		</dic>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="165" queryable="true" defaultValue="%user.manageUnit.id"  colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="SFSC" alias="是否上传" display="1" defaultValue="0" type="string">
		<dic autoLoad="true">
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" 
		display="3" defaultValue="%user.userId" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" 
		display="3" defaultValue="%user.manageUnit.id" width="250" queryable="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间"  type="datetime" xtype="datefield"  
		display="3" defaultValue="%server.date.date" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>


