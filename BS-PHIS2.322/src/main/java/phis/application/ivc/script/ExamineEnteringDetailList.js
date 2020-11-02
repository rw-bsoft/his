/**
 * 
 * 审批录入窗口
 * 
 * @author gaof
 */
$package("phis.application.ivc.script")

$import("phis.script.EditorList")

phis.application.ivc.script.ExamineEnteringDetailList = function(cfg) {
	// this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
//	Ext.apply(this, phis.script.yb.YbUtil);
	phis.application.ivc.script.ExamineEnteringDetailList.superclass.constructor.apply(
			this, [cfg])
	// this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.ivc.script.ExamineEnteringDetailList,
		phis.script.EditorList, {
			doCommit : function() {
				var SMKLSH = "";
				var SMKRZM = "";
				var ICXX = ""
				var sbkh = "";// 带有K或者Z的卡号
				var jbbh = "";
				var ypzllx = "";
				var ypzlxmbh = "";
				var xmjg = "";
				var sqjl = "";
				var jldw = "";
				var sqysxm = this.mainApp.uid;
				var ypzlxmyljgdmc = this.mainApp.dept;
				if (this.YBXX) {
					SMKLSH = this.YBXX["SMKLSH"];
					SMKRZM = this.YBXX["SMKRZM"];
					sbkh = this.YBXX["SRKH"];// 带有K或者Z的卡号
					ICXX = this.YBXX.ICXX;
				}
				var djs = [];
				djs["passed"] = [];
				djs["notPassed"] = [];
				var pass = "";
				var mc = "";
				var count = this.store.getCount();
				var sign = 0;
				// 数据校验
				for (var i = 0; i < count; i++) {
					if (this.store.getAt(i).data["SPBH"] == null
							|| this.store.getAt(i).data["SPBH"] == "") {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行审批编号为空");
						return false;
					}
					if (this.store.getAt(i).data["SPBH"].length > 15) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "审批编号超过指定长度");
						return false;
					}
					if (this.store.getAt(i).data["JBBM"]) {
						jbbh = this.store.getAt(i).data["JBBM"];
					}
					if (this.store.getAt(i).data["DJLX"]) {
						ypzllx = this.store.getAt(i).data["DJLX"];
					}
					if (this.store.getAt(i).data["YBBM"]) {
						ypzlxmbh = this.store.getAt(i).data["YBBM"];
					}
					if (this.store.getAt(i).data["YPDJ"]) {
						xmjg = this.store.getAt(i).data["YPDJ"];
					}
					if (this.store.getAt(i).data["YPJL"]) {
						sqjl = this.store.getAt(i).data["YPJL"];
					}
					if (this.store.getAt(i).data["JLDW"]) {
						jldw == this.store.getAt(i).data["JLDW"];
					}
				}
				if (this.sybsign == 1) {
					for (var i = 0; i < count; i++) {
						var lshobj = $PhisActiveXObjectUtils
								.szybUserBargaingApply("23", "0", "$$~~~~41$$",
										"");// 获取交易流水号
						if (lshobj.reCord < 0) {
							Ext.Msg.alert("省医保提示", lshobj.ErrMsg);
							return;
						}
						// 交易结果~错误信息 +空~空~空~ 申请的医院编号~ 用户交易流水号（<=14位）
						// $$0~~~~~1101~12345678901234$$
						// var lshobj = {};
						// lshobj.RetData = "$$0~~~~~1101~12345678901234$$";
						var lshformat = "JYJG|CWXX|K1|K2|K3|YYBH|YHJYLSH";
						var lshData = this.StrToObj(lshformat, lshobj.RetData
										.split("$$")[1], "~");
						var sybret = "$$0~" + ICXX + "~~~2~0~" + jbbh + "~~~~"
								+ sqysxm + "~" + ypzllx + "~" + ypzlxmbh + "~"
								+ ypzlxmyljgdmc + "~" + xmjg + "~" + sqjl + "~"
								+ jldw + "$$";

						var obj = $PhisActiveXObjectUtils
								.szybUserBargaingApply(41, lshData.YHJYLSH,
										sybret, "");
						if (obj.reCord < 0) {
							sign = 1
							var stroeoper = this.listModuleoper.grid.getStore();
							var n = this.stroeoper.getCount();
							for (var j = 0; j < n; j++) {// 收费明细的数据
								if (this.store.getAt(i).data["SBXH"] == stroeoper
										.getAt(j).data["SBXH"]) {
									stroeoper.getAt(j).set("ZFBL", 1)
								}
							}
						} else if (obj.RetData.split("$$")[1].split("~")[5] != "0") {
							djs["passed"].push(this.store.getAt(i).data);
							sign = 2
						}
					}
					if (sign == 1) {
						Ext.Msg.alert("省医保提示", obj.ErrMsg);
						return;
					} else {
						Ext.Msg.alert("省医保提示", obj.RetData.split("$$")[1]
										.split("~")[5]);
						return;
					}
				} else {
					for (var i = 0; i < count; i++) {
						// console.debug(this.store.getAt(i).data);
						// 调用医保接口校验
						// var reg =
						// "GRBH|DWBH|GRSFH|XM|XSGWYBZ|KH|YLZH|YLRYLB|GDBZDDYY1|GDBZDDYY2|BNZHZFLJJE|ZHJYJE|BNZHZFFD|BNTCZFLJ|JRTCYLFZE|"
						// +
						// "DWMC|DWLX|BNZH|LNZH|TXZDLB|MZQFXLJ|TXMZQDSJ|TXMZQDBZ|LJZYCS|JZXM|GWYBZ|BTGYBJSBZ|SFCJGSBX|BLLX|PTRYXSBJDYBZ|ZLFYLJ|ZFFYLJ|ZFUFYLJ|"
						// +
						// "CSNY|XB|QYZZMZTCQDBZ|QYZZMZTCQDSJ|LJMBZ|NMGDYXSBZ|JTBCJCTS|CQH|XNHBZ|DXSBZ|MZKNJZLJ|JRKNJZZYZFLJ|JRKNJZMZZFLJ|SFCZRY|"
						// + "HZJJXZBZ|EYBZ|YGYSBZ";
						var body = {
							"KH" : sbkh,
							"TYPE" : 1,
							"SPBH" : this.store.getAt(i).data["SPBH"],
							"XMBM" : this.store.getAt(i).data["XMBM"]
						};
						var reg = "SPBZ|SPBH|XMBM|XMMC|SPSL|SPYXKSSJ|SPYXJSSJ|GDBZXX|";
						var ret = this.simpleDyyb("spxxcx", body, reg, 1, sbkh
										+ "|000000|" + SMKLSH + "|" + SMKRZM
										+ "|");
						if (ret.code == 1) {
							if (ret.outputStr.SPBZ == 1) {
								djs["passed"].push(this.store.getAt(i).data);
								pass = pass + this.store.getAt(i).data["MC"]
										+ ",";
							} else if (ret.outputStr.SPBZ == 0) {
								if (obj.reCord < 0) {
									var stroeoper = this.listModuleoper.grid
											.getStore();
									var n = this.stroeoper.getCount();
									for (var j = 0; j < n; j++) {// 收费明细的数据
										if (this.store.getAt(i).data["SBXH"] == stroeoper
												.getAt(j).data["SBXH"]) {
											stroeoper.getAt(j).set("ZFBL", 1)
										}
									}
								}
								djs["notPassed"].push(this.store.getAt(i).data);
								mc = mc + this.store.getAt(i).data["MC"] + ",";
							}
						} else {
							MyMessageTip.msg("提示", ret.outputStr, true);
							return;
						}
						// 测试
						// djs["passed"].push(this.store.getAt(i).data);
						// djs["notPassed"].push(this.store.getAt(i).data);
						// pass = pass + this.store.getAt(i).data["MC"] + ",";
						// mc = mc + this.store.getAt(i).data["MC"] + ",";
					}
					// 测试
					// djs["passed"].push(this.store.getAt(0).data);
					// djs["passed"].push(this.store.getAt(1).data);
					// pass = pass + this.store.getAt(0).data["MC"] + ",";
					// pass = pass + this.store.getAt(1).data["MC"] + ",";
					// mc = mc + this.store.getAt(i).data["MC"] + ",";
					// console.debug(djs)

					// if (djs.length > 0) {
					// 如果通过校验
				}
				re = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "updateSPBH",
							body : djs
						});
				if (re.code > 300) {
					this.processReturnMsg(re.code, re.msg);
					return;
				}
				// }
				if (pass.length > 0) {
					MyMessageTip.msg("提示", pass + "校验成功", true, 5);
				}
				if (mc.length > 0) {
					MyMessageTip.msg("提示", mc + "未通过校验，按自费处理", true, 5);
				}
			}
		})