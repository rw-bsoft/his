<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICRECORDLHISTORY"  alias="转诊历史记录">
	<!--(目前暂时先放在本地数据库)-->
	<item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>  
	</item>
	<item id="EMPIID" alias="EMPIID" type="string" length="32" display="0" />
	<item id="MZHM" alias="门诊号码" type="string" length="32" />
	<item id="BINGRENXM" alias="病人姓名" length="20"  type="string"/>
	<item id="BINGRENXB" alias="病人性别" length="20"  type="string"/>
	<item id="BINGRENNL" alias="病人年龄" length="10"  type="string" />
	<item id="STATUS" alias="转诊状态" length="2"  type="string" />
	<item id="JIUZHENKLX" alias="就诊卡类型" length="10"  type="string" />
	<item id="JIUZHENKH" alias="就诊卡号" length="16"  type="string" />
	<item id="YIBAOKLX" alias="医保卡类型" length="10"  type="string" />
	<item id="YIBAOKXX" alias="医保卡信息" length="32"  type="string" />
	<item id="YEWULX" alias="业务类型" length="10"  type="string" />
	<item id="BINGRENCSRQ" alias="病人出生日期" length="16"  type="string" />
	<item id="BINGRENSFZH" alias="病人身份证号" length="18"  type="string" />
	<item id="BINGRENLXDH" alias="病人联系电话" length="15"  type="string" />
	<item id="BINGRENLXDZ" alias="病人联系地址" length="50"  type="string" />
	<item id="BINGRENFYLB" alias="病人费用类别" length="10"  type="string" />
	<item id="SHENQINGJGDM" alias="申请机构代码" length="20"  type="string" />
	<item id="SHENQINGJGMC" alias="申请机构名称" length="32"  type="string" />
	<item id="SHENQINGJGLXDH" alias="申请机构联系电话" length="15"  type="string" />
	<item id="SHENQINGYSDH" alias="申请医生电话" length="15"  type="string" />
	<item id="ZHUANZHENYY" alias="转诊原因" length="100"  type="string" />
	<item id="ZHUANZHENZD" alias="转诊诊断" length="50" not-null="true" type="string"/>
	<item id="BINQINGMS" alias="病情描述" length="100"  type="string" />
	<item id="ZHUANZHENZYSX" alias="转诊注意事项" length="100"  type="string" />
	<item id="ZHUANRUKSDM" alias="转入科室代码" length="10"  type="string" />
	<item id="ZHUANRUKSMC" alias="转入科室名称" length="20"  type="string" />
	<item id="ZHUANZHENDH" alias="转诊单号" length="16"  type="string" />
	<item id="SHENQINGRQ" alias="申请日期" length="19"  type="string" />
	<item id="ZHUANZHENRQ" alias="转诊日期" length="19"  type="string" />
	<item id="SHENQINGYS" alias="申请医生" length="10"  type="string" />
	<item id="ZYH" alias="住院号" length="16"  type="string" />
	<item id="ZHUANRUYYDM" alias="转入医院代码" length="30"  type="string" />
	<item id="ZHUANRUYYMC" alias="转入医院名称" length="50"  type="string" />
	<item id="ZHUANRUKSDM" alias="转入科室代码" length="30"  type="string" />
	<item id="ZHUANRUKSMC" alias="转入科室名称" length="50"  type="string" />
</entry>
