<entry entityName="chis.application.tcm.schemas.TCMQuestionnaireOldPeople" alias="中医体质辨识问卷（老年版）-国标" >
	<item id="registerId" alias="登记主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="visit" alias="望" type="string" length="200" display="2"/>
	<item id="tongue" alias="舌" type="string" length="100" display="2"/>
	<item id="coatedTongue" alias="苔" type="string" length="100" display="2"/>
	<item id="smell" alias="闻" type="string" length="200" display="2"/>
	<item id="asking" alias="问" type="string" length="200" display="2"/>
	<item id="pulseTaking" alias="切" type="string" length="200" display="2"/>
	
	<item id="energyFull_1" alias="(1)您精力充沛吗？(指精神头足，乐于做事)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="easyWeary_2" alias="(2)您容易疲乏吗？(指体力如何，是否稍微活动一下或做一点家务劳动就感到累)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="easyPant_3" alias="(3)您容易气短，呼吸短促，接不上气吗？" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="voiceWeak_4" alias="(4)您说话声音低弱无力吗?(指说话没有力气)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="moodiness_5" alias="(5)您感到闷闷不乐、情绪低沉吗?(指心情不愉快，情绪低落)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="nervous_6" alias="(6)您容易精神紧张、焦虑不安吗?(指遇事是否心情紧张)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="loneliness_7" alias="(7)您因为生活状态改变而感到孤独、失落吗？" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="easyScare_8" alias="(8)您容易感到害怕或受到惊吓吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="overweight_9" alias="(9)您感到身体超重不轻松吗?(感觉身体沉重)[BMI指数=体重(kg)/身高2(m)]" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer5"/>
	</item> 
	<item id="eyeDry_10" alias="(10)您眼睛干涩吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="footFearCold_11" alias="(11)您手脚发凉吗?(不包含因周围温度低或穿的少导致的手脚发冷)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="backFearCold_12" alias="(12)您胃脘部、背部或腰膝部怕冷吗？(指上腹部、背部、腰部或膝关节等，有一处或多处怕冷)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="fearCold_13" alias="(13)您比一般人耐受不了寒冷吗？(指比别人容易害怕冬天或是夏天的冷空调、电扇等)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="cold_14" alias="(14)您容易患感冒吗?(指每年感冒的次数)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer4"/>
	</item> 
	<item id="rhinobyon_15" alias="(15)您没有感冒时也会鼻塞、流鼻涕吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="mouthGreasy_16" alias="(16)您有口粘口腻，或睡眠打鼾吗？" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="allergy_17" alias="(17)您容易过敏(对药物、食物、气味、花粉或在季节交替、气候变化时)吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer3"/>
	</item> 
	<item id="skinUrticaria_18" alias="(18)您的皮肤容易起荨麻疹吗? (包括风团、风疹块、风疙瘩)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinBleeding_19" alias="(19)您的皮肤在不知不觉中会出现青紫瘀斑、皮下出血吗?(指皮肤在没有外伤的情况下出现青一块紫一块的情况)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinRed_20" alias="(20)您的皮肤一抓就红，并出现抓痕吗?(指被指甲或钝物划过后皮肤的反应)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinDry_21" alias="(21)您皮肤或口唇干吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="limbsNumb_22" alias="(22)您有肢体麻木或固定部位疼痛的感觉吗？" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="faceGreasy_23" alias="(23)您面部或鼻部有油腻感或者油亮发光吗?(指脸上或鼻子)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="faceDim_24" alias="(24)您面色或目眶晦黯，或出现褐色斑块/斑点吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="skinEczema_25" alias="(25)您有皮肤湿疹、疮疖吗？" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="mouthDry_26" alias="(26)您感到口干咽燥、总想喝水吗？" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="bitterTaste_27" alias="(27)您感到口苦或嘴里有异味吗?(指口苦或口臭)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="bellyLarge_28" alias="(28)您腹部肥大吗?(指腹部脂肪肥厚)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer2"/>
	</item> 
	<item id="fearCool_29" alias="(29)您吃(喝)凉的东西会感到不舒服或者怕吃(喝)凉的东西吗？(指不喜欢吃凉的食物，或吃了凉的食物后会不舒服)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="stoolStiction_30" alias="(30)您有大便黏滞不爽、解不尽的感觉吗?(大便容易粘在马桶或便坑壁上)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="stoolDry_31" alias="(31)您容易大便干燥吗?" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="furStodgily_32" alias="(32)您舌苔厚腻或有舌苔厚厚的感觉吗?(如果自我感觉不清楚可由调查员观察后填写)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item> 
	<item id="stasisPurple_33" alias="(33)您舌下静脉瘀紫或增粗吗？(可由调查员辅助观察后填写)" type="string" length="1" boxType="radio" not-null="1" >
		<dic id="chis.dictionary.answer1"/>
	</item>
	
</entry>