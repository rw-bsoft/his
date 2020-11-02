$package("chis.application.ohr.script")
$import("chis.script.BizTableFormView", "chis.script.util.widgets.MyMessageTip")
$styleSheet("chis.css.MedicineManageFormQ");

chis.application.ohr.script.MedicineManageFormQ = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.ohr.script.MedicineManageFormQ.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.chineseMedicineManageService";
	this.saveAction = "saveChineseMedicineManage";
	this.loadServiceId = "chis.chineseMedicineManageService";
	this.loadAction = "loadChineseMedicineManage";
}

Ext.extend(chis.application.ohr.script.MedicineManageFormQ,
		chis.script.BizTableFormView, {
			initPanel : function(sc) {
				this.id = this.generateMixed(7);
				if (this.form) {
					return this.form;
				}
				if (!this.data) {
					this.data = {};
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var form = new Ext.form.FormPanel({
							tbar : this.createButtons(),
							autoScroll : true,
							html : this.getHtml().apply({})
						})
				this.form = form;
				form.on("afterrender", this.onReady, this);
				return form;
			},
			getHtml : function() {
				var html = new Ext.Template('<div class="answer">'
						+ '<P>请根据近一年的体验和感觉，回答以下问题：</P><dl><dt>（1）您精力充沛么吗？（指精神头足，乐于做事）</dt><dd>'
						+ '<label><input type="radio" name="energyFull'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="energyFull'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="energyFull'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="energyFull'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="energyFull'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（2）您容易疲乏吗？（指体力如何，是否稍微活动一下或做一点家务劳动就感到累）</dt><dd>'
						+ '<label><input type="radio" name="easyWeary'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="easyWeary'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="easyWeary'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="easyWeary'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="easyWeary'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（3）您容易气短，呼吸短促，接不上气吗？</dt><dd>'
						+ '<label><input type="radio" name="easyPant'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="easyPant'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="easyPant'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="easyPant'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="easyPant'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（4）您说话声音低弱无力吗？（指说话没有力气）</dt><dd>'
						+ '<label><input type="radio" name="voiceWeak'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="voiceWeak'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="voiceWeak'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="voiceWeak'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="voiceWeak'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（5）您感到闷闷不乐，情绪低沉吗？（指心情不愉快，情绪低落）</dt><dd>'
						+ '<label><input type="radio" name="moodiness'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="moodiness'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="moodiness'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="moodiness'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="moodiness'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（6）您容易精神紧张，焦虑不安吗？（指遇事是否心情紧张）</dt><dd>'
						+ '<label><input type="radio" name="nervous'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="nervous'
						+ this.id
						+ '"/>[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="nervous'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="nervous'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="nervous'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（7）您因为生活状态改变而感到孤独、失落吗？</dt><dd>'
						+ '<label><input type="radio" name="loneliness'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="loneliness'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="loneliness'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="loneliness'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="loneliness'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（8）您容易感到害怕或受到惊吓吗？</dt><dd>'
						+ '<label><input type="radio" name="easyScare'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="easyScare'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="easyScare'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="easyScare'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="easyScare'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（9）您感到身体超重不轻松吗？（感觉身体沉重）[BMI指数＝体重（kg）/身高2（m）]</dt><dd>'
						+ '<label><input type="radio" name="overweight'
						+ this.id
						+ '" checked="true"/>[1]（BMI&lt;24）</label>'
						+ '<label><input type="radio" name="overweight'
						+ this.id
						+ '" />[2]（24&le;BMI&lt;25）</label>'
						+ '<label><input type="radio" name="overweight'
						+ this.id
						+ '" />[3]（25&le;BMI&lt;26）</label>'
						+ '<label><input type="radio" name="overweight'
						+ this.id
						+ '" />[4]（26&le;BMI&lt;28）</label>'
						+ '<label><input type="radio" name="overweight'
						+ this.id
						+ '" />[5]（BMI&ge;28）</label>'
						+ '</dd><dt>（10）您眼睛干涩吗？</dt><dd>'
						+ '<label><input type="radio" name="eyeDry'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="eyeDry'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="eyeDry'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="eyeDry'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="eyeDry'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（11）您手脚发凉吗？（不包含因周围温度低或穿的少导致的手脚发冷）</dt><dd>'
						+ '<label><input type="radio" name="footFearCold'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="footFearCold'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="footFearCold'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="footFearCold'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="footFearCold'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '<dt>（12）您胃脘部、背部或腰膝部怕冷吗？（指上腹部、背部、腰部或膝关节等，有一处或多处怕冷）</dt><dd>'
						+ '<label><input type="radio" name="backFearCold'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="backFearCold'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="backFearCold'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="backFearCold'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="backFearCold'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（13）您比一般人耐受不了寒冷吗？（指比别人容易害怕冬天或是夏天的冷空调，电扇等）</dt><dd>'
						+ '<label><input type="radio" name="fearCold'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="fearCold'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="fearCold'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="fearCold'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="fearCold'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（14）您容易患感冒吗？（指每年感冒的次数）</dt><dd>'
						+ '<label><input type="radio" name="cold'
						+ this.id
						+ '" checked="true"/>[1]一年&lt;2次</label>'
						+ '<label><input type="radio" name="cold'
						+ this.id
						+ '" />[2]一年感冒2－4次</label>'
						+ '<label><input type="radio" name="cold'
						+ this.id
						+ '" />[3]一年感冒5－6次</label>'
						+ '<label><input type="radio" name="cold'
						+ this.id
						+ '" />[4]一年感冒8次以上</label>'
						+ '<label><input type="radio" name="cold'
						+ this.id
						+ '" />[5]几乎每月都感冒</label>'
						+ '</dd><dt>（15）您没有感冒时也会鼻塞、流鼻涕吗？</dt><dd>'
						+ '<label><input type="radio" name="rhinobyon'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="rhinobyon'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="rhinobyon'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="rhinobyon'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="rhinobyon'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（16）您有口粘口腻，或睡眠打鼾吗？</dt><dd>'
						+ '<label><input type="radio" name="mouthGreasy'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="mouthGreasy'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="mouthGreasy'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="mouthGreasy'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="mouthGreasy'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（17）您容易过敏(对药物、食物、气味、花粉或在季节交替、气候变化时)吗?</dt><dd>'
						+ '<label><input type="radio" name="allergy'
						+ this.id
						+ '" checked="true"/>[1]从来没有</label>'
						+ '<label><input type="radio" name="allergy'
						+ this.id
						+ '" />[2]一年1、2次</label>'
						+ '<label><input type="radio" name="allergy'
						+ this.id
						+ '" />[3]一年3、4次</label>'
						+ '<label><input type="radio" name="allergy'
						+ this.id
						+ '" />[4]一年5、6次</label>'
						+ '<label><input type="radio" name="allergy'
						+ this.id
						+ '" />[5]每次遇到上述原因都过敏</label>'
						+ '</dd><dt>（18）您的皮肤容易起荨麻疹吗? (包括风团、风疹块、风疙瘩)</dt><dd>'
						+ '<label><input type="radio" name="skinUrticaria'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="skinUrticaria'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="skinUrticaria'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="skinUrticaria'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="skinUrticaria'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（19）您的皮肤在不知不觉中会出现青紫瘀斑、皮下出血吗?（指皮肤在没有外伤的情况下出现青一块紫一块的情况）</dt><dd>'
						+ '<label><input type="radio" name="skinBleeding'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="skinBleeding'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="skinBleeding'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="skinBleeding'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="skinBleeding'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（20）您的皮肤一抓就红，并出现抓痕吗?（指被指甲或钝物划过后皮肤的反应）</dt><dd>'
						+ '<label><input type="radio" name="skinRed'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="skinRed'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="skinRed'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="skinRed'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="skinRed'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（21）您皮肤或口唇干吗?</dt><dd>'
						+ '<label><input type="radio" name="skinDry'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="skinDry'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="skinDry'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="skinDry'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="skinDry'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（22）您有肢体麻木或固定部位疼痛的感觉吗？</dt><dd>'
						+ '<label><input type="radio" name="limbsNumb'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="limbsNumb'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="limbsNumb'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="limbsNumb'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="limbsNumb'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（23）您面部或鼻部有油腻感或者油亮发光吗?（指脸上或鼻子）</dt><dd>'
						+ '<label><input type="radio" name="faceGreasy'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="faceGreasy'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="faceGreasy'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="faceGreasy'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="faceGreasy'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（24）您面色或目眶晦黯，或出现褐色斑块/斑点吗?</dt><dd>'
						+ '<label><input type="radio" name="faceDim'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="faceDim'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="faceDim'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="faceDim'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="faceDim'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（25）您有皮肤湿疹、疮疖吗？</dt><dd>'
						+ '<label><input type="radio" name="skinEczema'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="skinEczema'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="skinEczema'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="skinEczema'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="skinEczema'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（26）您感到口干咽燥、总想喝水吗？</dt><dd>'
						+ '<label><input type="radio" name="mouthDry'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="mouthDry'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="mouthDry'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="mouthDry'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="mouthDry'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（27）您感到口苦或嘴里有异味吗?（指口苦或口臭）</dt><dd>'
						+ '<label><input type="radio" name="bitterTaste'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="bitterTaste'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="bitterTaste'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="bitterTaste'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="bitterTaste'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（28）您腹部肥大吗?（指腹部脂肪肥厚）</dt><dd>'
						+ '<label><input type="radio" name="bellyLarge'
						+ this.id
						+ '" checked="true"/>[1]（腹围&lt;80cm,相当于2.4尺）</label>'
						+ '<label><input type="radio" name="bellyLarge'
						+ this.id
						+ '" />[2]（腹围80-85cm,2.4-2.55尺）</label>'
						+ '<label><input type="radio" name="bellyLarge'
						+ this.id
						+ '" />[3]（腹围86-90cm,2.56-2.7尺）</label>'
						+ '<label><input type="radio" name="bellyLarge'
						+ this.id
						+ '" />[4]（腹围91-105cm,2.71-3.15尺）</label>'
						+ '<label><input type="radio" name="bellyLarge'
						+ this.id
						+ '" />[5]（腹围>105cm或3.15尺）</label>'
						+ '</dd><dt>（29）您吃(喝)凉的东西会感到不舒服或者怕吃(喝)凉的东西吗？（指不喜欢吃凉的食物，或吃了凉的食物后会不舒服）</dt><dd>'
						+ '<label><input type="radio" name="fearCool'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="fearCool'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="fearCool'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="fearCool'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="fearCool'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（30）您有大便黏滞不爽、解不尽的感觉吗?(大便容易粘在马桶或便坑壁上)</dt><dd>'
						+ '<label><input type="radio" name="stoolStiction'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="stoolStiction'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="stoolStiction'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="stoolStiction'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="stoolStiction'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（31）您容易大便干燥吗?</dt><dd>'
						+ '<label><input type="radio" name="stoolDry'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="stoolDry'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="stoolDry'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="stoolDry'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="stoolDry'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（32）您舌苔厚腻或有舌苔厚厚的感觉吗?（如果自我感觉不清楚可由调查员观察后填写）</dt><dd>'
						+ '<label><input type="radio" name="furStodgily'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="furStodgily'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="furStodgily'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="furStodgily'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="furStodgily'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd><dt>（33）您舌下静脉瘀紫或增粗吗？（可由调查员辅助观察后填写）</dt><dd>'
						+ '<label><input type="radio" name="stasisPurple'
						+ this.id
						+ '" checked="true"/>[1]没有（根本不/从来没有）</label>'
						+ '<label><input type="radio" name="stasisPurple'
						+ this.id
						+ '" />[2]很少（有一点/偶尔）</label>'
						+ '<label><input type="radio" name="stasisPurple'
						+ this.id
						+ '" />[3]有时（有些/少数时间）</label>'
						+ '<label><input type="radio" name="stasisPurple'
						+ this.id
						+ '" />[4]经常（相当/多数时间）</label>'
						+ '<label><input type="radio" name="stasisPurple'
						+ this.id
						+ '" />[5]总是（非常/每天）</label>'
						+ '</dd></dl></div>');
				this.html = html;
				return html;
			},
			onReady : function() {
				chis.application.ohr.script.MedicineManageFormQ.superclass.onReady
						.call(this);
			},

			doSave : function() {
				var empiData=this.exContext.empiData;
				var birthDay = empiData.birthday;
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth;
				if ((typeof birthDay == 'object')
						&& birthDay.constructor == Date) {
					birth = birthDay;
				} else {
					birth = Date.parseDate(birthDay, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear()
						- this.mainApp.exContext.oldPeopleAge);
				if (birth.getFullYear() <= currDate.getFullYear()) {
					
					var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.chineseMedicineManageService",
							serviceAction : "checkHasOldRecord",
							method : "execute",
							empiId : empiData.empiId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
					if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);
				this.fireEvent("doSave", values);
				}else{
				Ext.Msg.show({
								title : '提示信息',
								msg : '年龄小于'
										+ this.mainApp.exContext.oldPeopleAge
										+ '岁不允许建立中医药健康管理',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;
				}
			},
			getFormData : function() {
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext.empiData);
				Ext.apply(this.data, this.exContext.ids);
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					var v = this.data[it.id]
					if (v == undefined) {
						v = it.defaultValue
					}
					if (it.boxType != "radio") {
						continue;
					}
					var docs = document.getElementsByName(it.id + this.id);
					for (var j = 0; j < docs.length; j++) {
						if (docs[j].checked == true
								|| docs[j].checked == "true") {
							v = j + 1;
						}
					}
					if (v) {
						values[it.id] = v;
					}
				}
				return values;
			},
			initFormData : function(data) {
				this.beforeInitFormData(data);
				Ext.apply(this.data, data);
				this.op = "update";
				this.initDataId = this.data[this.schema.pkey];
				var items = this.schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var v = this.data[it.id];
					if (v == undefined || v == "") {
						continue;
					}
					if (it.boxType != "radio") {
						continue;
					}
					var docs = document.getElementsByName(it.id + this.id);
					if (docs&&docs[(parseInt(v) - 1) + ""]) {
						docs[(parseInt(v) - 1) + ""].checked = true;
					}
				}
			},

			doCreate : function() {
				this.initDataId = null;
				this.op = "create"
				if (!this.schema) {
					return
				}
				if (this.data) {
					this.data = {}
				}
				var items = this.schema.items;
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					var docs = document.getElementsByName(it.id + this.id);
					if (docs[0]) {
						docs[0].checked = true;
					}
				}
				this.fireEvent("doCreate");

			},
			loadData : function(){
				//不需要发送数据加载请求
			}
		});