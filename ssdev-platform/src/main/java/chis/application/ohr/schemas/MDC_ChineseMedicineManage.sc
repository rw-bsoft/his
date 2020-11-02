<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.ohr.schemas.MDC_ChineseMedicineManage" alias="老年人中医药健康管理"> 
	<item id="id" pkey="true" alias="标识" type="string" length="16" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key>
	</item>  
	<item id="phrId" alias="档案编码" type="string" length="30" fixed="true" colspan="2" display="0"/>  
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" display="0"/>  
	<item id="energyFull" alias="(1)您精力充沛吗？(指精神头足，乐于做事)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="easyWeary" alias="(2)您容易疲乏吗？(指体力如何，是否稍微活动一下或做一点家务劳动就感到累)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="easyPant" alias="(3)您容易气短，呼吸短促，接不上气吗？" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="voiceWeak" alias="(4)您说话声音低弱无力吗?(指说话没有力气)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="moodiness" alias="(5)您感到闷闷不乐、情绪低沉吗?(指心情不愉快，情绪低落)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="nervous" alias="(6)您容易精神紧张、焦虑不安吗?(指遇事是否心情紧张)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="loneliness" alias="(7)您因为生活状态改变而感到孤独、失落吗？" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="easyScare" alias="(8)您容易感到害怕或受到惊吓吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="overweight" alias="(9)您感到身体超重不轻松吗?(感觉身体沉重)[BMI指数=体重(kg)/身高2(m)]" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer5"/>
	</item> 
	<item id="eyeDry" alias="(10)您眼睛干涩吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="footFearCold" alias="(11)您手脚发凉吗?(不包含因周围温度低或穿的少导致的手脚发冷)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="backFearCold" alias="(12)您胃脘部、背部或腰膝部怕冷吗？(指上腹部、背部、腰部或膝关节等，有一处或多处怕冷)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="fearCold" alias="(13)您比一般人耐受不了寒冷吗？(指比别人容易害怕冬天或是夏天的冷空调、电扇等)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="cold" alias="(14)您容易患感冒吗?(指每年感冒的次数)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer4"/>
	</item> 
	<item id="rhinobyon" alias="(15)您没有感冒时也会鼻塞、流鼻涕吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="mouthGreasy" alias="(16)您有口粘口腻，或睡眠打鼾吗？" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="allergy" alias="(17)您容易过敏(对药物、食物、气味、花粉或在季节交替、气候变化时)吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer3"/>
	</item> 
	<item id="skinUrticaria" alias="(18)您的皮肤容易起荨麻疹吗? (包括风团、风疹块、风疙瘩)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinBleeding" alias="(19)您的皮肤在不知不觉中会出现青紫瘀斑、皮下出血吗?(指皮肤在没有外伤的情况下出现青一块紫一块的情况)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinRed" alias="(20)您的皮肤一抓就红，并出现抓痕吗?(指被指甲或钝物划过后皮肤的反应)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinDry" alias="(21)您皮肤或口唇干吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="limbsNumb" alias="(22)您有肢体麻木或固定部位疼痛的感觉吗？" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="faceGreasy" alias="(23)您面部或鼻部有油腻感或者油亮发光吗?(指脸上或鼻子)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="faceDim" alias="(24)您面色或目眶晦黯，或出现褐色斑块/斑点吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinEczema" alias="(25)您有皮肤湿疹、疮疖吗？" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="mouthDry" alias="(26)您感到口干咽燥、总想喝水吗？" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="bitterTaste" alias="(27)您感到口苦或嘴里有异味吗?(指口苦或口臭)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="bellyLarge" alias="(28)您腹部肥大吗?(指腹部脂肪肥厚)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer2"/>
	</item> 
	<item id="fearCool" alias="(29)您吃(喝)凉的东西会感到不舒服或者怕吃(喝)凉的东西吗？(指不喜欢吃凉的食物，或吃了凉的食物后会不舒服)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="stoolStiction" alias="(30)您有大便黏滞不爽、解不尽的感觉吗?(大便容易粘在马桶或便坑壁上)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="stoolDry" alias="(31)您容易大便干燥吗?" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="furStodgily" alias="(32)您舌苔厚腻或有舌苔厚厚的感觉吗?(如果自我感觉不清楚可由调查员观察后填写)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="stasisPurple" alias="(33)您舌下静脉瘀紫或增粗吗？(可由调查员辅助观察后填写)" type="string" length="1" boxType="radio">
		<dic id="chis.dictionary.answer1"/>
	</item>
	<item id="score1" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify1" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide1" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other1" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score2" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify2" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide2" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other2" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score3" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify3" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide3" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other3" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score4" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify4" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide4" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other4" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score5" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify5" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide5" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other5" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score6" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify6" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide6" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other6" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score7" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify7" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide7" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other7" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score8" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify8" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．倾向是" />
		</dic>
	</item> 
	<item id="healthGuide8" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other8" alias="其他" type="string" length="200" boxType="text">
	</item> 
	<item id="score9" alias="得分" type="int" length="5" boxType="number">
	</item> 
	<item id="physiqueIdentify9" alias="体质辨识" type="string" length="1" boxType="radio2">
		<dic>
			<item key="1" text="2．是" />
			<item key="2" text="3．基本是" />
		</dic>
	</item> 
	<item id="healthGuide9" alias="中医药保健指导" type="string" length="20" boxType="check">
		<dic>
			<item key="1" text="1．情志调摄" />
			<item key="2" text="2．饮食调养" />
			<item key="3" text="3．起居调摄" />
			<item key="4" text="4．运动保健" />
			<item key="5" text="5．穴位保健" />
			<item key="6" text="6．其他：" />
		</dic>
	</item> 
	<item id="other9" alias="其他" type="string" length="200" boxType="text">
	</item> 
	
	<item id="reportDate" alias="填表日期" type="date"  boxType="date"
		defaultValue="%server.date.date">
	</item>
	<item id="reportUser" alias="医生签名" type="string" length="20"  defaultValue="%user.userId" boxType="dic">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
		</dic>
	</item>
	<item id="bodyType" alias="体质类型" display="0" type="string" length="20">
		<dic render="LovCombo">
			<item key="1" text="气虚质" />
			<item key="2" text="阳虚质" />
			<item key="3" text="阴虚质" />
			<item key="4" text="痰湿质" />
			<item key="5" text="湿热质" />
			<item key="6" text="血瘀质" />
			<item key="7" text="气郁质" />
			<item key="8" text="特禀质" />
			<item key="9" text="平和质" />
		</dic>
	</item> 
	<item id="createUnit" alias="建档机构" type="string" length="20"  display="0"
		width="180" update="false" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20"  display="0"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" fixed="true" update="false" display="0"
		defaultValue="%server.date.date">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0"
		hidden="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true" display="0"
		defaultValue="%server.date.date">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
