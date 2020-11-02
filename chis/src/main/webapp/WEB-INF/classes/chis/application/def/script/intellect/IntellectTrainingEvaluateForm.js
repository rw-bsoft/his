$package("chis.application.def.script.intellect");

$import("chis.script.BizTableFormView");

chis.application.def.script.intellect.IntellectTrainingEvaluateForm = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_IntellectTrainingEvaluate"
	cfg.colCount = 6;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 57
	cfg.labelWidth = 80
	cfg.showButtonOnTop = true;
	chis.application.def.script.intellect.IntellectTrainingEvaluateForm.superclass.constructor.apply(this,
			[cfg]);
	this.saveServiceId = "chis.defIntellectService"
	this.saveAction = "saveIntellectTrainingEvaluate"
	this.loadServiceId = "chis.defIntellectService"
	this.loadAction = "loadIntellectTrainingEvaluateData"
};

Ext.extend(chis.application.def.script.intellect.IntellectTrainingEvaluateForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.def.script.intellect.IntellectTrainingEvaluateForm.superclass.onReady
						.call(this)

				
				var fanshen = this.form.getForm().findField("fanshen")
				fanshen.on("blur", this.onCalculateScore, this)
				fanshen.on("keyup", this.onCalculateScore, this)
				this.fanshen = fanshen
				
				var zuo = this.form.getForm().findField("zuo")
				zuo.on("blur", this.onCalculateScore, this)
				zuo.on("keyup", this.onCalculateScore, this)
				this.zuo = zuo
				
				var pa = this.form.getForm().findField("pa")
				pa.on("blur", this.onCalculateScore, this)
				pa.on("keyup", this.onCalculateScore, this)
				this.pa = pa
				
				var buxing = this.form.getForm().findField("buxing")
				buxing.on("blur", this.onCalculateScore, this)
				buxing.on("keyup", this.onCalculateScore, this)
				this.buxing = buxing
				
				var sxtj = this.form.getForm().findField("sxtj")
				sxtj.on("blur", this.onCalculateScore, this)
				sxtj.on("keyup", this.onCalculateScore, this)
				this.sxtj = sxtj
				
				var pao = this.form.getForm().findField("pao")
				pao.on("blur", this.onCalculateScore, this)
				pao.on("keyup", this.onCalculateScore, this)
				this.pao = pao
				
				var ssqw = this.form.getForm().findField("ssqw")
				ssqw.on("blur", this.onCalculateScore, this)
				ssqw.on("keyup", this.onCalculateScore, this)
				this.ssqw = ssqw
				
				var niequ = this.form.getForm().findField("niequ")
				niequ.on("blur", this.onCalculateScore, this)
				niequ.on("keyup", this.onCalculateScore, this)
				this.niequ = niequ
				
				var ninggai = this.form.getForm().findField("ninggai")
				ninggai.on("blur", this.onCalculateScore, this)
				ninggai.on("keyup", this.onCalculateScore, this)
				this.ninggai = ninggai
				
				var xkz = this.form.getForm().findField("xkz")
				xkz.on("blur", this.onCalculateScore, this)
				xkz.on("keyup", this.onCalculateScore, this)
				this.xkz = xkz
				
				var czz = this.form.getForm().findField("czz")
				czz.on("blur", this.onCalculateScore, this)
				czz.on("keyup", this.onCalculateScore, this)
				this.czz = czz
				
				var zhezhi = this.form.getForm().findField("zhezhi")
				zhezhi.on("blur", this.onCalculateScore, this)
				zhezhi.on("keyup", this.onCalculateScore, this)
				this.zhezhi = zhezhi
				
				var zswt = this.form.getForm().findField("zswt")
				zswt.on("blur", this.onCalculateScore, this)
				zswt.on("keyup", this.onCalculateScore, this)
				this.zswt = zswt
				
				var zsydwt = this.form.getForm().findField("zsydwt")
				zsydwt.on("blur", this.onCalculateScore, this)
				zsydwt.on("keyup", this.onCalculateScore, this)
				this.zsydwt = zsydwt
				
				var fbwd = this.form.getForm().findField("fbwd")
				fbwd.on("blur", this.onCalculateScore, this)
				fbwd.on("keyup", this.onCalculateScore, this)
				this.fbwd = fbwd
				
				var fbqw = this.form.getForm().findField("fbqw")
				fbqw.on("blur", this.onCalculateScore, this)
				fbqw.on("keyup", this.onCalculateScore, this)
				this.fbqw = fbqw
				
				var fbcjsy = this.form.getForm().findField("fbcjsy")
				fbcjsy.on("blur", this.onCalculateScore, this)
				fbcjsy.on("keyup", this.onCalculateScore, this)
				this.fbcjsy = fbcjsy
				
				var cjfb = this.form.getForm().findField("cjfb")
				cjfb.on("blur", this.onCalculateScore, this)
				cjfb.on("keyup", this.onCalculateScore, this)
				this.cjfb = cjfb
				
				var rswtdcz = this.form.getForm().findField("rswtdcz")
				rswtdcz.on("blur", this.onCalculateScore, this)
				rswtdcz.on("keyup", this.onCalculateScore, this)
				this.rswtdcz = rswtdcz
				
				var wpgl = this.form.getForm().findField("wpgl")
				wpgl.on("blur", this.onCalculateScore, this)
				wpgl.on("keyup", this.onCalculateScore, this)
				this.wpgl = wpgl
				
				var rscjlx = this.form.getForm().findField("rscjlx")
				rscjlx.on("blur", this.onCalculateScore, this)
				rscjlx.on("keyup", this.onCalculateScore, this)
				this.rscjlx = rscjlx
				
				var rsys = this.form.getForm().findField("rsys")
				rsys.on("blur", this.onCalculateScore, this)
				rsys.on("keyup", this.onCalculateScore, this)
				this.rsys = rsys
				
				var rsfw = this.form.getForm().findField("rsfw")
				rsfw.on("blur", this.onCalculateScore, this)
				rsfw.on("keyup", this.onCalculateScore, this)
				this.rsfw = rsfw
				
				var rsxz = this.form.getForm().findField("rsxz")
				rsxz.on("blur", this.onCalculateScore, this)
				rsxz.on("keyup", this.onCalculateScore, this)
				this.rsxz = rsxz
				
				var fbyw = this.form.getForm().findField("fbyw")
				fbyw.on("blur", this.onCalculateScore, this)
				fbyw.on("keyup", this.onCalculateScore, this)
				this.fbyw = fbyw
				
				var rsscsg = this.form.getForm().findField("rsscsg")
				rsscsg.on("blur", this.onCalculateScore, this)
				rsscsg.on("keyup", this.onCalculateScore, this)
				this.rsscsg = rsscsg
				
				var zdtqqk = this.form.getForm().findField("zdtqqk")
				zdtqqk.on("blur", this.onCalculateScore, this)
				zdtqqk.on("keyup", this.onCalculateScore, this)
				this.zdtqqk = zdtqqk
				
				var zdyggx = this.form.getForm().findField("zdyggx")
				zdyggx.on("blur", this.onCalculateScore, this)
				zdyggx.on("keyup", this.onCalculateScore, this)
				this.zdyggx = zdyggx
				
				var dianshu = this.form.getForm().findField("dianshu")
				dianshu.on("blur", this.onCalculateScore, this)
				dianshu.on("keyup", this.onCalculateScore, this)
				this.dianshu = dianshu
				
				var rssj = this.form.getForm().findField("rssj")
				rssj.on("blur", this.onCalculateScore, this)
				rssj.on("keyup", this.onCalculateScore, this)
				this.rssj = rssj
				
				var rsqb = this.form.getForm().findField("rsqb")
				rsqb.on("blur", this.onCalculateScore, this)
				rsqb.on("keyup", this.onCalculateScore, this)
				this.rsqb = rsqb
				
				var zdzjmz = this.form.getForm().findField("zdzjmz")
				zdzjmz.on("blur", this.onCalculateScore, this)
				zdzjmz.on("keyup", this.onCalculateScore, this)
				this.zdzjmz = zdzjmz
				
				var fcjddml = this.form.getForm().findField("fcjddml")
				fcjddml.on("blur", this.onCalculateScore, this)
				fcjddml.on("keyup", this.onCalculateScore, this)
				this.fcjddml = fcjddml
				
				var bdxq = this.form.getForm().findField("bdxq")
				bdxq.on("blur", this.onCalculateScore, this)
				bdxq.on("keyup", this.onCalculateScore, this)
				this.bdxq = bdxq
				
				var sjddyj = this.form.getForm().findField("sjddyj")
				sjddyj.on("blur", this.onCalculateScore, this)
				sjddyj.on("keyup", this.onCalculateScore, this)
				this.sjddyj = sjddyj
				
				var yyjl = this.form.getForm().findField("yyjl")
				yyjl.on("blur", this.onCalculateScore, this)
				yyjl.on("keyup", this.onCalculateScore, this)
				this.yyjl = yyjl
				
				var sxdjbnl = this.form.getForm().findField("sxdjbnl")
				sxdjbnl.on("blur", this.onCalculateScore, this)
				sxdjbnl.on("keyup", this.onCalculateScore, this)
				this.sxdjbnl = sxdjbnl
				
				var nzswc = this.form.getForm().findField("nzswc")
				nzswc.on("blur", this.onCalculateScore, this)
				nzswc.on("keyup", this.onCalculateScore, this)
				this.nzswc = nzswc
				
				var ycjc = this.form.getForm().findField("ycjc")
				ycjc.on("blur", this.onCalculateScore, this)
				ycjc.on("keyup", this.onCalculateScore, this)
				this.ycjc = ycjc
				
				var ycjh = this.form.getForm().findField("ycjh")
				ycjh.on("blur", this.onCalculateScore, this)
				ycjh.on("keyup", this.onCalculateScore, this)
				this.ycjh = ycjh
				
				var xbzl = this.form.getForm().findField("xbzl")
				xbzl.on("blur", this.onCalculateScore, this)
				xbzl.on("keyup", this.onCalculateScore, this)
				this.xbzl = xbzl
				
				var dbzl = this.form.getForm().findField("dbzl")
				dbzl.on("blur", this.onCalculateScore, this)
				dbzl.on("keyup", this.onCalculateScore, this)
				this.dbzl = dbzl
				
				var tyf = this.form.getForm().findField("tyf")
				tyf.on("blur", this.onCalculateScore, this)
				tyf.on("keyup", this.onCalculateScore, this)
				this.tyf = tyf
				
				var cyf = this.form.getForm().findField("cyf")
				cyf.on("blur", this.onCalculateScore, this)
				cyf.on("keyup", this.onCalculateScore, this)
				this.cyf = cyf
				
				var cxw = this.form.getForm().findField("cxw")
				cxw.on("blur", this.onCalculateScore, this)
				cxw.on("keyup", this.onCalculateScore, this)
				this.cxw = cxw
				
				var shuaya = this.form.getForm().findField("shuaya")
				shuaya.on("blur", this.onCalculateScore, this)
				shuaya.on("keyup", this.onCalculateScore, this)
				this.shuaya = shuaya
				
				var xilian = this.form.getForm().findField("xilian")
				xilian.on("blur", this.onCalculateScore, this)
				xilian.on("keyup", this.onCalculateScore, this)
				this.xilian = xilian
				
				var rsjthj = this.form.getForm().findField("rsjthj")
				rsjthj.on("blur", this.onCalculateScore, this)
				rsjthj.on("keyup", this.onCalculateScore, this)
				this.rsjthj = rsjthj
				
				var zdjjaq = this.form.getForm().findField("zdjjaq")
				zdjjaq.on("blur", this.onCalculateScore, this)
				zdjjaq.on("keyup", this.onCalculateScore, this)
				this.zdjjaq = zdjjaq
				
				var rsggss = this.form.getForm().findField("rsggss")
				rsggss.on("blur", this.onCalculateScore, this)
				rsggss.on("keyup", this.onCalculateScore, this)
				this.rsggss = rsggss
				
				var cjjthd = this.form.getForm().findField("cjjthd")
				cjjthd.on("blur", this.onCalculateScore, this)
				cjjthd.on("keyup", this.onCalculateScore, this)
				this.cjjthd = cjjthd
				
				var daqcs = this.form.getForm().findField("daqcs")
				daqcs.on("blur", this.onCalculateScore, this)
				daqcs.on("keyup", this.onCalculateScore, this)
				this.daqcs = daqcs
				
				var xishou = this.form.getForm().findField("xishou")
				xishou.on("blur", this.onCalculateScore, this)
				xishou.on("keyup", this.onCalculateScore, this)
				this.xishou = xishou
				
				var xijiao = this.form.getForm().findField("xijiao")
				xijiao.on("blur", this.onCalculateScore, this)
				xijiao.on("keyup", this.onCalculateScore, this)
				this.xijiao = xijiao
				
				var gbz = this.form.getForm().findField("gbz")
				gbz.on("blur", this.onCalculateScore, this)
				gbz.on("keyup", this.onCalculateScore, this)
				this.gbz = gbz
				
				var dblc = this.form.getForm().findField("dblc")
				dblc.on("blur", this.onCalculateScore, this)
				dblc.on("keyup", this.onCalculateScore, this)
				this.dblc = dblc
				
				var rsjjhj = this.form.getForm().findField("rsjjhj")
				rsjjhj.on("blur", this.onCalculateScore, this)
				rsjjhj.on("keyup", this.onCalculateScore, this)
				this.rsjjhj = rsjjhj
				
				var zdzj = this.form.getForm().findField("zdzj")
				zdzj.on("blur", this.onCalculateScore, this)
				zdzj.on("keyup", this.onCalculateScore, this)
				this.zdzj = zdzj
				
				var rsssdr = this.form.getForm().findField("rsssdr")
				rsssdr.on("blur", this.onCalculateScore, this)
				rsssdr.on("keyup", this.onCalculateScore, this)
				this.rsssdr = rsssdr
				
				var zhan = this.form.getForm().findField("zhan")
				zhan.on("blur", this.onCalculateScore, this)
				zhan.on("keyup", this.onCalculateScore, this)
				this.zhan = zhan
				
				var bt = new Ext.Button({
					name : 'show',
					text : '&nbsp;&nbsp;&nbsp;&nbsp;2:能 1:部分能 0:不能独立完成',
					prop : {}
				});
				this.form.getTopToolbar().addButton(bt)
			},
			onCalculateScore:function(){
				var score = Number(this.fanshen.getValue()) 
				+  Number(this.zuo.getValue()) 
				+  Number(this.pa.getValue()) 
				+  Number(this.buxing.getValue())
				+  Number(this.sxtj.getValue())
				+  Number(this.pao.getValue())
				+  Number(this.ssqw.getValue())
				+  Number(this.niequ.getValue())
				+  Number(this.ninggai.getValue())
				+  Number(this.xkz.getValue())
				+  Number(this.czz.getValue())
				+  Number(this.zhezhi.getValue()) 
				+  Number(this.zswt.getValue()) 
				+  Number(this.zsydwt.getValue())
				+  Number(this.fbwd.getValue())
				+  Number(this.fbqw.getValue())
				+  Number(this.fbcjsy.getValue())
				+  Number(this.cjfb.getValue())
				+  Number(this.rswtdcz.getValue())
				+  Number(this.wpgl.getValue())
				+  Number(this.rscjlx.getValue())
				+  Number(this.rsys.getValue()) 
				+  Number(this.rsfw.getValue()) 
				+  Number(this.rsxz.getValue())
				+  Number(this.fbyw.getValue())
				+  Number(this.rsscsg.getValue())
				+  Number(this.zdtqqk.getValue())
				+  Number(this.zdyggx.getValue())
				+  Number(this.dianshu.getValue())
				+  Number(this.rssj.getValue())
				+  Number(this.rsqb.getValue())
				+  Number(this.zdzjmz.getValue()) 
				+  Number(this.fcjddml.getValue()) 
				+  Number(this.bdxq.getValue())
				+  Number(this.sjddyj.getValue())
				+  Number(this.yyjl.getValue())
				+  Number(this.sxdjbnl.getValue())
				+  Number(this.nzswc.getValue())
				+  Number(this.ycjc.getValue())
				+  Number(this.ycjh.getValue())
				+  Number(this.xbzl.getValue())
				+  Number(this.dbzl.getValue()) 
				+  Number(this.tyf.getValue()) 
				+  Number(this.cyf.getValue())
				+  Number(this.cxw.getValue())
				+  Number(this.shuaya.getValue())
				+  Number(this.xilian.getValue())
				+  Number(this.rsjthj.getValue())
				+  Number(this.zdjjaq.getValue())
				+  Number(this.rsggss.getValue())
				+  Number(this.cjjthd.getValue())
				+  Number(this.daqcs.getValue()) 
				+  Number(this.xishou.getValue()) 
				+  Number(this.xijiao.getValue())
				+  Number(this.gbz.getValue())
				+  Number(this.dblc.getValue())
				+  Number(this.rsjjhj.getValue())
				+  Number(this.zdzj.getValue())
				+  Number(this.rsssdr.getValue())
				+  Number(this.zhan.getValue())
				
				this.form.getForm().findField("score").setValue(score)
			}
			,
			saveToServer : function(saveData) {
				this.onCalculateScore()
				saveData.defId = this.exContext.r.get("id")
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				chis.application.def.script.intellect.IntellectTrainingEvaluateForm.superclass.saveToServer.call(this,saveData)
			},
			doCreate : function() {
				this.fireEvent("create")
			},
			loadData : function() {
				if (this.exContext.r1.get("id")) {
					this.initDataId = this.exContext.r1.get("id")
					chis.application.def.script.intellect.IntellectTrainingEvaluateForm.superclass.loadData.call(this)
				} else {
					this.initDataId = null
					this.doInitialize()
				}
			},
			getLoadRequest : function() {
				return {
					ids : this.exContext.ids,
					r : this.exContext.r1.data
				}
			},
			doInitialize : function() {
				this.doNew()
				
			},
			doTest:function(){
				this.fanshen.setValue(1)
				this.zuo.setValue(1)
				this.pa.setValue(1)
				this.buxing.setValue(1)
				this.sxtj.setValue(1)
				this.pao.setValue(1)
				this.ssqw.setValue(1)
				this.niequ.setValue(1)
				this.ninggai.setValue(1)
				this.xkz.setValue(1)
				this.czz.setValue(1)
				this.zhezhi.setValue(1)
				this.zswt.setValue(1)
				this.zsydwt.setValue(1)
				this.fbwd.setValue(1)
				this.fbqw.setValue(1)
				this.fbcjsy.setValue(1)
				this.cjfb.setValue(1)
				this.rswtdcz.setValue(1)
				this.wpgl.setValue(1)
				this.rscjlx.setValue(1)
				this.rsys.setValue(1)
				this.rsfw.setValue(1)
				this.rsxz.setValue(1)
				this.fbyw.setValue(1)
				this.rsscsg.setValue(1)
				this.zdtqqk.setValue(1)
				this.zdyggx.setValue(1)
				this.dianshu.setValue(1)
				this.rssj.setValue(1)
				this.rsqb.setValue(1)
				this.zdzjmz.setValue(1)
				this.fcjddml.setValue(1)
				this.bdxq.setValue(1)
				this.sjddyj.setValue(1)
				this.yyjl.setValue(1)
				this.sxdjbnl.setValue(1)
				this.nzswc.setValue(1)
				this.ycjc.setValue(1)
				this.ycjh.setValue(1)
				this.xbzl.setValue(1)
				this.dbzl.setValue(1)
				this.tyf.setValue(1)
				this.cyf.setValue(1)
				this.cxw.setValue(1)
				this.shuaya.setValue(1)
				this.xilian.setValue(1)
				this.rsjthj.setValue(1)
				this.zdjjaq.setValue(1)
				this.rsggss.setValue(1)
				this.cjjthd.setValue(1)
				this.daqcs.setValue(1) 
				this.xishou.setValue(1)
				this.xijiao.setValue(1)
				this.gbz.setValue(1)
				this.dblc.setValue(1)
				this.rsjjhj.setValue(1)
				this.zdzj.setValue(1)
				this.rsssdr.setValue(1)
				this.zhan.setValue(1)
				this.onCalculateScore()
			}
		});