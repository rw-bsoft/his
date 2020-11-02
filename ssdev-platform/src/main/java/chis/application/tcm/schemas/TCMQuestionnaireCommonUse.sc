<entry entityName="chis.application.tcm.schemas.TCMQuestionnaireCommonUse" alias="中医体质辨识问卷（大众版）-中华中医药学会标准" >
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
	
	<item id="QYangxz1" alias="(1)您手脚发凉吗?" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYangxz2" alias="(2)您胃脘部、背部或腰膝部怕冷吗？" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYangxz3" alias="(3)您感到怕冷、衣服比别人穿得多吗？" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYangxz4" alias="(4)您比一般人耐受不了寒冷（冬天的寒冷，夏天的冷空调、电扇等）吗？" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYangxz5" alias="(5)您比别人容易患感冒吗？" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYangxz6" alias="(6)您吃(喝)凉的东西会感到不舒服或者怕吃(喝)凉东西吗？" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYangxz7" alias="(7)您受凉或吃(喝)凉的东西后，容易腹泻(拉肚子)吗？" type="string" length="1" not-null="1" group="阳虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QYinxz1" alias="(8)您感到手脚心发热吗?" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz2" alias="(9)您感觉身体、脸上发热吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz3" alias="(10)您皮肢或口唇干吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz4" alias="(11)您口唇的颜色比一般人红吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz5" alias="(12)您容易便秘或大便干燥吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz6" alias="(13)您面部两颧潮红或偏红吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz7" alias="(14)您感到眼睛干涩吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QYinxz8" alias="(15)您感到口干咽燥、总想喝水吗？" type="string" length="1" not-null="1" group="阴虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QQixz1" alias="(17)您容易疲乏吗?" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz2" alias="(18)您容易气短（呼吸短促，接不上气）吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz3" alias="(19)您容易心慌吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz4" alias="(20)您容易头晕或站起时晕眩吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz5" alias="(21)您比别人容易患感冒吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz6" alias="(22)您喜欢安静、懒得说话吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz7" alias="(23)您说话声音低弱无力吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQixz8" alias="(24)您活动量稍在太容易出虚汗吗？" type="string" length="1" not-null="1" group="气虚质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QTansz1" alias="(25)您感到胸闷或腹部胀满吗?" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz2" alias="(26)您感到身体沉重不轻松或不爽快吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz3" alias="(27)您腹部肥满松软吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz4" alias="(28)您有额部油脂分泌多的现象吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz5" alias="(29)您上眼睑比别人肿(上眼睑有轻微隆起现象)吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz6" alias="(30)您嘴里有黏黏的感觉吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz7" alias="(31)您平时痰多，特别咽喉部总感到有痰堵着吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTansz8" alias="(32)您活动量稍在太容易出虚汗吗？" type="string" length="1" not-null="1" group="痰湿质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QShirz1" alias="(33)您面部或鼻部有油腻感或者油亮发光吗?" type="string" length="1" not-null="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QShirz2" alias="(34)您容易生痤疮或疮疖吗？" type="string" length="1" not-null="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QShirz3" alias="(35)您感到口苦或嘴里有异味吗？" type="string" length="1" not-null="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QShirz4" alias="(36)您大便黏滞不爽、有解不尽的感觉吗？" type="string" length="1" not-null="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QShirz5" alias="(37)您小便明尿道有发热感、尿色浓(深)吗？" type="string" length="1" not-null="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QShirz6" alias="(38)您带下色黄(白带颜色发黄)吗？(限女性回答)" type="string" length="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QShirz7" alias="(38)您的阴囊部位潮湿吗？(限男性回答)" type="string" length="1" group="湿热质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QXueyz1" alias="(39)您的皮肤在不知不觉中会出现青紫瘀斑(皮下出血)吗?" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QXueyz2" alias="(40)您两颧部有细微红丝吗？" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QXueyz3" alias="(41)您身体上哪里疼痛吗？" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QXueyz4" alias="(42)您面色晦黯或容易出现褐斑吗？" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QXueyz5" alias="(43)您容易有黑眼圈吗？" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QXueyz6" alias="(44)您容易忘事(健忘)吗？" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QXueyz7" alias="(45)您口唇颜色偏黯吗？" type="string" length="1" not-null="1" group="血瘀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QTebz1" alias="(46)您没有感冒时也会打喷嚏吗?" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTebz2" alias="(47)您没有感冒时也会鼻塞、流鼻涕吗？" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTebz3" alias="(48)您有因季节变化、温度变化或异味等原因而咳喘的现象吗？" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTebz4" alias="(49)您容易过敏(对药物、食物、气味、花粉或在季节交替、气候变化时)吗？" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTebz5" alias="(50)您的皮肤容易起荨麻疹(风团、风疹块、风疙瘩)吗？" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTebz6" alias="(51)您的皮肤因过敏出现过紫癜(紫红色瘀点、瘀斑)吗？" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QTebz7" alias="(52)您的皮肤一抓就红，并出现抓痕吗？" type="string" length="1" not-null="1" group="特禀质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QQiyz1" alias="(53)您感到闷闷不乐、情结低沉吗?" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQiyz2" alias="(54)您容易精神紧张、焦虑不安吗？" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQiyz3" alias="(55)您多愁善感、感情脆弱吗？" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQiyz4" alias="(56)您容易感到害怕或受到惊吓吗？" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQiyz5" alias="(57)您胁肋部或乳房胀痛吗？" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQiyz6" alias="(58)您无缘无故叹气吗？" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QQiyz7" alias="(59)您咽喉部有异物感,且吐之不出、咽之不下吗？" type="string" length="1" not-null="1" group="气郁质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	
	<item id="QHepz1" alias="(60)您精力充沛吗?" type="string" length="1" not-null="1" group="平和质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<!--
	<item id="QHepz2" alias="(2)您容易疲乏吗？＊" type="string" length="1" not-null="1" group="平和质">
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QHepz3" alias="(3)您说话声音低弱无力吗？＊" type="string" length="1" not-null="1" group="平和质">
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QHepz4" alias="(4)您感到闷闷不乐、情绪低沉吗？＊" type="string" length="1" not-null="1" group="平和质">
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QHepz5" alias="(5)您比一般人耐受不了寒冷(冬天的寒冷，夏天的冷空调、电扇等)吗？＊" type="string" length="1" not-null="1" group="平和质">
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	-->
	<item id="QHepz6" alias="(61)您能适应外界自然和社会环境的变化吗？" type="string" length="1" not-null="1" group="平和质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<item id="QHepz7" alias="(62)您容易失眠吗？＊" type="string" length="1" not-null="1" group="平和质" >
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	<!--
	<item id="QHepz8" alias="(8)您容易忘事(健忘)吗？＊" type="string" length="1" not-null="1" group="平和质">
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
	-->
</entry>