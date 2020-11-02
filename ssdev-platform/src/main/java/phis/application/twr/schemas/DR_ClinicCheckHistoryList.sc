<?xml version="1.0" encoding="UTF-8"?>

<entry id="DR_ClinicCheckHistoryList" tableName="DR_CLINICCHECKHISTORY" alias="检查申请记录">
	<!--(目前暂时先放在本地数据库)-->
	<item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>  
	</item>
	<item id="EMPIID" alias="EMPIID" type="string" length="32" display="0"/>
	<item id="MZHM" alias="门诊号码" type="string" length="32" display="0"/>
	<item id="BINGRENXM" alias="病人姓名" type="string" length="32"  queryable="true"/>
	<item id="BINGRENXB" alias="病人性别" length="20" virtual="true" type="string"  queryable="true">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="BINGRENNL" alias="病人年龄" length="10" virtual="true" type="string" />
	<item id="BINGRENSFZH" alias="病人身份证号" length="18" type="string"  queryable="true"/>
	<item id="SHENQINGRQ" alias="申请时间" length="12" type="string" />
	<item id="SONGJIANKSMC" alias="申请机构" length="10" type="string" />
	<item id="JIANCHAYYMC" alias="检查机构" virtual="true" length="32" type="string" />
	<item id="JIANCHAXMLX" alias="检查类型" virtual="true" length="10" type="string" />
	<item id="JIANCHASBMC" alias="设备名称" virtual="true" length="16" type="string" />
	<item id="YUYUEH" alias="预约号" virtual="true" length="10" type="string" />
	<item id="SONGJIANRQ" alias="预约时间" type="date" length="20"  update="false" defaultValue="%server.date.date" not-null="true" />
	<item id="YUYUESJ" alias="检查时间" virtual="true" type="date" length="20"  update="false" defaultValue="%server.date.date" not-null="true" />
	<item id="STATUS" alias="送检状态" length="2" type="string"/>
	<item id="JIANCHAXMMC" alias="检查项目" type="string" virtual="true" length="32"/>
	<item id="JIANCHASQDH" alias="检查申请单号" length="20" width="120" type="string" display="0"/>
	<item id="SONGJIANKS" alias="送检机构" type="string" length="16" display="0" anchor="100%" width="180" update="false" defaultValue="%user.manageUnit.id" not-null="true"/>
	<item id="SONGJIANYS" alias="送检医生" length="20" type="string" display="0"/>
	<item id="JIUZHENKLX" alias="就诊卡类型" length="10" type="string" display="0"/>
	<item id="JIUZHENKH" alias="就诊卡号" length="10" type="string" display="0"/>
	<item id="SHOUFEISB" alias="收费识别" length="5" type="string" display="0"/>
	<item id="BINGQINGMS" alias="病情描述" length="100" type="string" display="0"/>
	<item id="ZHENDUAN" alias="诊断" length="100" type="string" display="0"/>
	<item id="BINGRENTZ" alias="病人体征" length="100" type="string" display="0"/>
	<item id="QITAJC" alias="其它检查" length="100" type="string" display="0"/>
	<item id="BINGRENZS" alias="病人主诉" length="100" type="string" display="0"/>
	<item id="JIANCHALY" alias="检查来源" length="10" type="string" display="0"/>
	<item id="JIESHOUFS" alias="接收方式" length="10" type="string" display="0"/>
</entry>
